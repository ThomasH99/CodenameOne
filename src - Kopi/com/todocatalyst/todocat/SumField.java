/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

/**
 *
 * @author Thomas
 */
public interface SumField {
    /** returns the int value used by getSumAt() to calculate the sum of lists. Can be overwritten by eg. ItemLists to return some meaningful value to add up in
    lists of lists */
    long getSumField(int fieldId);    
    /** returns the int value used by getSumAt() to calculate the sum of lists. Can be overwritten by eg. ItemLists to return some meaningful value to add up in
    lists of lists */
    long getSumField();  
    /** should the value of this item be ignored? */
    boolean ignoreSumField();
}
