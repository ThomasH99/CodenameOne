package com.todocatalyst.todocatalyst;

/*
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2007 University of Maryland
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of a Bag. NB. No need to Externizable.externalize since ParseObject stores it as a list which CN1 can externalize already
 *
 * @author pugh
 * @param <E>
 */
public class Bag<E> {//implements Map  { Map.size() is costly with this implementation

    final Map<E, Integer> map  = new HashMap<E, Integer>();;

     Bag() {
//        map = new HashMap<E, Integer>();
    }

//    public Bag(Map<E, Integer> map) {
//    public Bag(Bag map) {
////        this.map = map;
//        addAll( map.toList());
//    }

    public Bag(List<E> itemList) {
        this();
//        map = new HashMap<E, Integer>();
//        for (E e : itemList) {
//            add(e);
//        }
        addAll(itemList);
    }

    public boolean add(E e) {
        Integer v = map.get(e);
        if (v == null) {
            map.put(e, 1);
        } else {
            map.put(e, v + 1);
        }
        return true;
    }

    /**
     * add e count times (increase counter by count)
     * @param e
     * @param count
     * @return 
     */
    public boolean add(E e, int count) {
        Integer v = map.get(e);
        if (v == null) {
            map.put(e, count);
        } else {
            map.put(e, v + count);
        }
        return true;
    }

    /**
     * add all in collection
     * @param c 
     */
    public void addAll(Collection<E> c) {
        for (E e : c) {
            add(e);
        }
    }

    public Set<E> keySet() {
        return map.keySet();
    }

    public Collection<Map.Entry<E, Integer>> entrySet() {
        return map.entrySet();
    }

    public int getCount(E e) {
        Integer v = map.get(e);
        if (v == null) {
            return 0;
        } else {
            return v;
        }
    }

    /**
     * removes e and returns true if e was exactly the last instance of e in the
     * bag, otherwise false (including when e was not in the bag)
     *
     * @param e
     * @return
     */
    public boolean remove(E e) {
        Integer v = map.get(e);
        if (v == null || v == 0) {
            return false;
        } else if (v == 1) {
            map.remove(e);
            return true;
        } else {
            map.put(e, v - 1);
            return false;
        }
    }

//    public void removeAll(Collection<E> c) {
//        for (E e : c) {
//            remove(e);
//        }
//    }
//
//    @Override
//    public int size() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    public List<E> toList() {
        ArrayList<E> arrayList = new ArrayList();
        for (E o : keySet()) {
            for (int i = 0, size = getCount(o); i < size; i++) {
                arrayList.add(o); //add obj once for each count
            }
        }
        return arrayList;
    }
//
//    @Override
//    public boolean contains(Object o) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Iterator iterator() {
//        return map.
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Object[] toArray() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Object[] toArray(Object[] a) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean containsAll(Collection c) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean retainAll(Collection c) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void clear() {
//        map.clear();
//    }
//}

//    @Override
//    public void clear() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean containsKey(Object key) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean containsValue(Object value) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Set entrySet() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Object get(Object key) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Object put(Object key, Object value) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public void putAll(Map map) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Object remove(Object key) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public int size() {
//        
//    }
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Collection values() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
