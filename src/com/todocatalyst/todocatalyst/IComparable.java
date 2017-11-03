// http://www.koders.com/java/fid140EEE5501D133C8F633BCC0955E0B3304E91A67.aspx?s=util#L9
//-----------------------------------------------------------------------------
// $RCSfile: IComparable.java,v $
// $Revision: 1.2 $
// $Author: snoopdave $
// $Date: 2000/04/06 11:41:10 $
//-----------------------------------------------------------------------------

//package org.relayirc.util;
package com.todocatalyst.todocatalyst;

/**
 * Objects that implement this interface are sortable by QuickSort.
 * @see org.relayirc.util.QuickSort
 *  The result is a negative integer if this String object lexicographically
 * precedes the argument string. The result is a positive integer if this
 * String object lexicographically follows the argument string. The result
 * is zero if the strings are equal; compareTox returns 0 exactly when the
 * equals(Object) method would return true.
 * a.compareTox(b)
 * a < b  : -1
 * a == b :  0
 * a > b  :  1
 */
public interface IComparable {
//    final static int ITEM_DESCRIPTION = 1; //- declared in each comparable item

    //	Compare to other object. Works like String.compareTox()
//    public int compareTox(IComparable c);
    public int compareTo(IComparable c, int compareOn);
    //setup compare, e.g. to compare on a specific fields, or multiple fields
//    public int compareOn(int compareOn);
//    public int compareOn(int firstCompareField, int secondCompareField);

}