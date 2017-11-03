/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 *
 * @author Thomas
 */
public interface MyEnumInterface {

    /**
     * returns the user-visible value of the enum
     * @return 
     */
    String getDescription();

    /**
     * returns the list of user-visible value of the enum
     * @return 
     */
    String[] getDescriptionList();
//    String[] getInternalNames(){
//        values(Enum e)
//    }

    /**
     * returns the list of user-visible value of the enum
     * @return 
     */
    int[] getDescriptionOrdinals();

    /**
     * returns the enum corresponding to the description string
     *
     * @param description
     * @return enum or null if no enum value corresponds to the description
     * string
     */
    Object getValue(String description);
}
