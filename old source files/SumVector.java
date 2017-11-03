/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * SumVector adds up values for a source list in a lazy manner. E.g. adds up
 * remainingEffort for a list of tasks. It just needs a value getter to provide
 * it the right value to add up. It will listen to the source list to invalidate
 * previously calculated values when the
 *
 * @author THJ
 */
class SumVector {//implements ChangeEventListener {

//    public void receiveChangeEvent(ChangeEvent changeEvent) {
//        invalidate(changeEvent.getListIndex());
//    }

    interface SumFieldGetter {

        long getFieldValue(int index);
    }

    interface SumFieldSize {

        int getSize();
    }

//    SumVector(ItemList itemList, SumFieldGetter sumFieldGetter) {
//    SumVector(List itemList, SumFieldGetter sumFieldGetter) {
    SumVector(SumFieldSize sumFieldSizer, SumFieldGetter sumFieldGetter) {
        this.sumFieldGetter = this.sumFieldGetter;
        this.sumFieldSizer = this.sumFieldSizer;
//        this.itemList = itemList;
//        itemList.addChangeEventListener(itemList);
//        sumVector = new Vector<Long>(itemList.size());
        sumVector = new Vector<Long>(sumFieldSizer.getSize());
//        sumVector = new ArrayList(itemList.size());
//        sumVector = new ArrayList<Long>();
    }

    /**
     * keeps the sum of sumField in this list. Invariant: sums are always
     * correct, OR no sum is in the vector
     */
    private Vector<Long> sumVector;
//    private ArrayList<Long> sumVector;
    private SumFieldGetter sumFieldGetter;
    private SumFieldSize sumFieldSizer;
//    private List itemList;

    /**
     * returns the int sum of the fields of all elements in the list up to and
     * including index. Returns 0 if list is empty, if there are no objects of
     * type Item in list, or if fieldId does not correspond to an int field.
     * Useful eg to sum of the total effort of elements in the list. Returns 0
     * for objects in list that are either not WorkSlots, Items, or if fieldId
     * does not return an Integer. Preconditions: index illegal value (<0, >=
     * getSize): returns 0; ItemList empty NB! Currently no buffering of results
     * so recalculated each time, maing it a very expensive operation.
     *
     * @param index
     * @param fieldId
     * @return
     */
    long getSumAt(int index) {
        int getSize = sumFieldSizer.getSize();
        if (index < 0 || getSize == 0) { //if illegal index or empty list
            return 0;
        }
        if (index >= getSize) { //return sum of entire list (assuming that getSize()>=1)
            index = getSize - 1;
        }
        if (index < sumVector.size()) {
            //sum alredy calculated and stored in derivedSumVector
//            return ((Long) sumVector.elementAt(index)).longValue();
//            return sumVector.elementAt(index);
            return sumVector.get(index);
        } else { //need to calculate sum value first
            long returnValue = 0;
//            Object item = itemList.get(index);
            long fieldValue = sumFieldGetter.getFieldValue(index);
            if (index == 0) { //stop the recursion here
                returnValue = fieldValue;
            } else {
                //calculate sum recursively as getSumAt of previous item + sum of item at index
                //this ensures that the minimum is calculated and will spread the calculation out evenly when e.g. scrolling down a list
                returnValue = getSumAt(index - 1) + fieldValue; 
            }
//            sumVector.addElement(new Long(returnValue)); //buffer (store) the new sum
            sumVector.add(returnValue); //buffer (store) the new sum
            return returnValue;
        }
    }

    /**
     * returns the index of the Object that has a sum value greater than or
     * equal to sum, AND which has a value larger than zero (to avoid returning
     * possibly empty/zero value items). If no such element is found, e.g. sum
     * is bigger than total sum of all items in list, then -1 is returned.
     *
     * @param sum
     * @return
     */
    int getIndexAtSum(long sum) {
        //optimization!!! instead of running through all values, use algorithm where interval is halfed each time
        //optimization!!! or store actual values in a hashtable

//        for (int i = 0, size = itemList.size(); i < size; i++) {
        for (int i = 0, size = sumFieldSizer.getSize(); i < size; i++) {
            if (getSumAt(i) >= sum && sumFieldGetter.getFieldValue(i) > 0) {
                return i;
            }
        }
        return - 1;
    }
    /**
     * invalidates the sum at index (and thus ensures that sums are
     * recalculated). Can be called without initializing derivedSumVector.
     *
     * @param index
     */
    void invalidate(int index) {
        if (sumVector != null && index < sumVector.size() && index >= 0) {
//            sumVector.setSize(index); //removes all elements at index and after - this ensures lazy recalculation of sums
            sumVector.setSize(index); //removes all elements at index and after - this ensures lazy recalculation of sums
        }
    }

}
