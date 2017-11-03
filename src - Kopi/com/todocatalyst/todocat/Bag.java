package com.todocatalyst.todocat;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of a Bag
 *
 * @author pugh
 */
public class Bag<E> {

    final Map<E, Integer> map;

    public Bag() {
        map = new HashMap<E, Integer>();
    }

    public Bag(Map<E, Integer> map) {
        this.map = map;
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

    public void removeAll(Collection<E> c) {
        for (E e : c) {
            remove(e);
        }
    }
}
