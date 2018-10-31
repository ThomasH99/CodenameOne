/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores
 * CA 94065 USA or visit www.oracle.com if you need additional information or
 * have any questions.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Display;
import com.codename1.io.Storage;
import com.codename1.util.EasyThread;
import com.parse4cn1.ParseObject;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A cache map is essentially a hashtable that indexes entries based on age and
 * is limited to a fixed size. Hence when an entry is placed into the cache map
 * and the cache size needs to increase, the least referenced entry is removed.
 * A cache hit is made both on fetching and putting, hence frequently fetched
 * elements will never be removed from a sufficiently large cache. Cache can
 * work purely in memory or swap data into storage based on user definitions.
 * Notice that this class isn't threadsafe.
 *
 * @author Shai Almog
 */
public class MyCacheMap {

    private int cacheSize = 10;
    private Hashtable memoryCache = new Hashtable(); //THJ: structure key -> Object[], [0]: (Long) currentAge, [1]: referenced object
    private Hashtable weakCache = new Hashtable();

    private int storageCacheSize = 0;
    private Vector storageCacheContentVec;
    private String cachePrefix = "";
    private boolean alwaysStore;
    private int storageKey = -1;

    private Vector getStorageCacheContent() {
        if (storageCacheContentVec == null) {
            storageCacheContentVec = (Vector) Storage.getInstance().readObject("$CACHE$Idx" + cachePrefix);
            if (storageCacheContentVec == null) {
                storageCacheContentVec = new Vector();
            }
        }
        return storageCacheContentVec;
    }

    /**
     * Default constructor
     */
    public MyCacheMap() {
    }

    /**
     * Creates a cache map with a prefix string
     *
     * @param prefix string to prepend to the cache entries in storage
     */
    public MyCacheMap(String prefix) {
        this.cachePrefix = prefix;
    }

    /**
     * Indicates the size of the memory cache after which the cache won't grow
     * further Size is indicated by number of elements stored and not by KB or
     * similar benchmark!
     *
     * @return the cacheSize
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * Indicates the size of the memory cache after which the cache won't grow
     * further Size is indicated by number of elements stored and not by KB or
     * similar benchmark!
     *
     * @param cacheSize the cacheSize to set
     */
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    /*private*/ static final Object LOCK = new Object(); 

    /**
     * Puts the given key/value pair in the cache
     *
     * @param key the key
     * @param value the value
     */
    public void put(Object key, Object value) {
        synchronized (LOCK) {
            if (cacheSize <= memoryCache.size()) {
                // we need to find the oldest entry
                Enumeration e = memoryCache.keys();
                long oldest = System.currentTimeMillis();
                Object oldestKey = null;
                Object[] oldestValue = null;
                while (e.hasMoreElements()) { //optimization: if stored in age order, iterate from end of list?
                    Object currentKey = e.nextElement();
                    Object[] currentValue = (Object[]) memoryCache.get(currentKey);
                    long currentAge = ((Long) currentValue[0]).longValue();
                    if (currentAge <= oldest || oldestValue == null) {
                        oldest = currentAge;
                        oldestKey = currentKey;
                        oldestValue = currentValue;
                    }
                }
                placeInStorageCache(oldestKey, oldest, oldestValue[1]);
                weakCache.put(oldestKey, Display.getInstance().createSoftWeakRef(oldestValue[1]));
                memoryCache.remove(oldestKey);
            }
            long lastAccess = System.currentTimeMillis();
            Object previousValue = memoryCache.put(key, new Object[]{new Long(lastAccess), value});
            if (false && Config.TEST) {
                ASSERT.that(previousValue == null || (!(previousValue instanceof ParseObject && value instanceof ParseObject)
                        || ((ParseObject) value).keySet().size() > ((ParseObject) previousValue).keySet().size()),
                        () -> "Error, replacing ParseObject with more data with ParseObject with less data, prev="
                        + previousValue
                        + ((((ParseObject) previousValue).keySet() != null) ? (" ("
                        + ((ParseObject) previousValue).keySet().size() + ")") : "")
                        + ", new=" + value
                        + ((((ParseObject) value).keySet() != null) ? (" (" + ((ParseObject) value).keySet().size() + ")") : "")
                //                                +" ("+((ParseObject) value).keySet().size()+")"
                );
            }
            if (alwaysStore) {
                placeInStorageCache(key, lastAccess, value);
            }
        }
    }

    /**
     * Deletes a cached entry
     *
     * @param key entry to remove from the cache
     */
    public void delete(Object key) {
        synchronized (LOCK) {
            memoryCache.remove(key);
            weakCache.remove(key);
            Vector storageCacheContent = getStorageCacheContent();
            int s = storageCacheContent.size();
//        for (int iter = 0; iter < s; iter++) {
            int iter = 0;
            while (iter < s) {
                Object[] obj = (Object[]) storageCacheContent.elementAt(iter);
                if (obj[1].equals(key)) {
                    Storage.getInstance().deleteStorageFile("$CACHE$" + cachePrefix + key.toString());
//                obj[0] = new Long(Long.MIN_VALUE);
//                obj[1] = obj[0]; //THJ: obj[1] = obj[0] seems like a dirty way of deleting the index entry, is Vector.remove(index) so expensive?? ACTUALLY, ERROR: this corrupts the idx since the obj[1] value gets included in getKeysInCache!!!
                    storageCacheContent.remove(iter); //THJ, not allowed with for loop
                    Storage.getInstance().writeObject("$CACHE$Idx" + cachePrefix, storageCacheContent);
                    return;
                }
                iter++;
            }
        }
    }

    /**
     * Returns the object matching the given key
     *
     * @param key key object
     * @return value from a previous put or null
     */
    public Object get(Object key) {
        synchronized (LOCK) {
            Object[] o = (Object[]) memoryCache.get(key);
            if (o != null) {
                return o[1];
            }
            Object ref = weakCache.get(key);
            if (ref != null) {
                ref = Display.getInstance().extractHardRef(ref);
                if (ref != null) {
                    // cache hit! Promote it to the hard cache again
                    put(key, ref);
                    return ref;
                }
            }
            if (storageCacheSize > 0) {
                Vector storageCacheContent = getStorageCacheContent();
                for (int iter = 0, size = storageCacheContent.size(); iter < size; iter++) { //THJ: optimization
                    Object[] obj = (Object[]) storageCacheContent.elementAt(iter);
                    if (obj[1].equals(key)) { //THJ: stored format: { value, lastAccessed, key } so must be '2'. NOPE: index is {lastAccessed, key}
                        // place the object back into the memory cache and return the value
                        Vector v = (Vector) Storage.getInstance().readObject("$CACHE$" + cachePrefix + key.toString());
                        if (v != null) {
                            Object val = v.elementAt(0);
                            put(key, val);
                            return val;
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * when storageCacheContent is used as persistent storage for cached items,
     * load as many as possible to memory to speed up access.
     */
    synchronized public void loadCacheToMemory() {
        synchronized (LOCK) {
            Vector storageCacheContent = getStorageCacheContent();
            boolean oldAlwaysStore = alwaysStore;
            alwaysStore = false; //avoid to persist to StorageCache (very slow)
            int size = Math.min(cacheSize, Math.min(storageCacheContent.size(), storageCacheSize)); //iterate over as many items as possible, starting with most recently accessed
            for (int i = 0; i < size; i++) { //THJ: optimization
                Object key = ((Object[]) storageCacheContent.elementAt(i))[1];
                Vector v = (Vector) Storage.getInstance().readObject("$CACHE$" + cachePrefix + key.toString());
                if (v != null) {
                    put(key, v.elementAt(0));
                }
            }
            alwaysStore = oldAlwaysStore;
        }
    }

    /**
     * Clears the caches for this cache object
     */
    public void clearAllCache() {
        clearMemoryCache();
        clearStorageCache();
    }

    /**
     * Clears the memory cache
     */
    public void clearMemoryCache() {
        memoryCache.clear();
        weakCache.clear();
    }

    private void placeInStorageCache(Object key, long lastAccessed, Object value) {
        if (storageCacheSize < 1) {
            return;
        }

        Vector storageCacheContent = getStorageCacheContent();
        int vecSize = storageCacheContent.size();

        // find the best place
        for (int iter = 0; iter < vecSize; iter++) {
            Object[] index = (Object[]) storageCacheContent.elementAt(iter);
            Object indexKey = index[1];
            if (indexKey.equals(key)) {
                // already in storage just update the data
                placeInStorageCache(iter, key, lastAccessed, value);
                return;
            }
        }

        if (storageCacheContent.size() < storageCacheSize) {
            placeInStorageCache(storageCacheContent.size(), key, lastAccessed, value);
        } else {
            long smallest = Long.MAX_VALUE;
            int offset = 0;

            // find the best place
            for (int iter = 0; iter < vecSize; iter++) {
                Object[] index = (Object[]) storageCacheContent.elementAt(iter);
                long current = ((Long) index[0]).longValue();
                if (smallest > current) {
                    smallest = current;
                    offset = iter;
                }
            }
            placeInStorageCache(offset, key, lastAccessed, value);
        }
    }

    private void placeInStorageCache(int offset, Object key, long lastAccessed, Object value) {
        Vector v = new Vector();
        // stored format: { value, lastAccessed, key } 
        v.addElement(value);
        Long l = new Long(lastAccessed);
        v.addElement(l);
        v.addElement(key);
        Storage.getInstance().writeObject("$CACHE$" + cachePrefix + key.toString(), v);
        Vector storageCacheContent = getStorageCacheContent();
        if (storageCacheContent.size() > offset) {
            storageCacheContent.setElementAt(new Object[]{l, key}, offset); //format of storage index: { lastAccessed, key } 
        } else {
            storageCacheContent.insertElementAt(new Object[]{l, key}, offset);
        }
        Storage.getInstance().writeObject("$CACHE$Idx" + cachePrefix, storageCacheContent);
    }

    /**
     * Returns the keys for all the objects currently in cache, this is useful
     * to traverse all the objects and refresh them without actually deleting
     * the cache and fetching them from scratch.<br>
     * <b>Important</b> this vector is a copy of a current state, keys might not
     * exist anymore or might change, others might be added in the interim.
     *
     * @return a vector containing a snapshot of the current elements within the
     * cache.
     */
    public Vector getKeysInCache() {
        Vector r = new Vector();
        Enumeration en = memoryCache.keys();
        while (en.hasMoreElements()) {
            r.addElement(en.nextElement());
        }
        Vector storageCacheContent = getStorageCacheContent();
        for (int iter = 0; iter < storageCacheContent.size(); iter++) {
            Object[] o = (Object[]) storageCacheContent.elementAt(iter);
            if (!r.contains(o[1])) {
                r.addElement(o[1]);
            }
        }
        return r;
    }

    private Vector fetchFromStorageCache(int offset) {
        Vector v = getStorageCacheContent();
        Object[] arr = (Object[]) v.elementAt(offset);
        return (Vector) Storage.getInstance().readObject("$CACHE$" + cachePrefix + arr[1].toString());
    }

    /**
     * Clears the storage cache
     */
    public void clearStorageCache() {
        if (storageCacheSize > 0) {
            Vector v = getStorageCacheContent();
            int s = v.size();
            for (int iter = 0; iter < s; iter++) {
                Object[] arr = (Object[]) v.elementAt(iter);
                Storage.getInstance().deleteStorageFile("$CACHE$" + cachePrefix + arr[1].toString()); //THJ: 'arr[iter]'-> 'arr[1]'
            }
            Storage.getInstance().deleteStorageFile("$CACHE$Idx" + cachePrefix);
            storageCacheContentVec = new Vector();
        }
    }

    /**
     * Indicates the size of the storage cache after which the cache won't grow
     * further Size is indicated by number of elements stored and not by KB or
     * similar benchmark!
     *
     * @return the storageCacheSize
     */
    public int getStorageCacheSize() {
        return storageCacheSize;
    }

    /**
     * Indicates the size of the storage cache after which the cache won't grow
     * further Size is indicated by number of elements stored and not by KB or
     * similar benchmark!
     *
     * @param storageCacheSize the storageCacheSize to set
     */
    public void setStorageCacheSize(int storageCacheSize) {
        this.storageCacheSize = storageCacheSize;
        /*for(int iter = 0 ; iter < storageCacheSize ; iter++) {
            Vector v = fetchFromStorageCache(iter);
            if(v != null) {
                Object o = new Object[] {v.elementAt(1), v.elementAt(2)};
                Vector storageCacheContent = getStorageCacheContent();
                if(iter >= storageCacheContent.size()) {
                    storageCacheContent.addElement(o);
                } else {
                    storageCacheContent.insertElementAt(o, iter);
                }
            }
        }*/
        if (storageCacheSize == 0) {
            alwaysStore = false;
        }
    }

    /**
     * A prefix prepended to storage entries to differentiate them
     *
     * @return the cachePrefix
     */
    public String getCachePrefix() {
        return cachePrefix;
    }

    /**
     * A prefix prepended to storage entries to differentiate them
     *
     * @param cachePrefix the cachePrefix to set
     */
    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    /**
     * When set to true indicates that all entries should be persisted to
     * storage for a constantly persisting cache
     *
     * @return the alwaysStore
     */
    public boolean isAlwaysStore() {
        return alwaysStore;
    }

    /**
     * When set to true indicates that all entries should be persisted to
     * storage for a constantly persisting cache
     *
     * @param alwaysStore the alwaysStore to set
     */
    public void setAlwaysStore(boolean alwaysStore) {
        this.alwaysStore = alwaysStore;
    }
}
