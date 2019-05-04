/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.FontImage;
import com.codename1.ui.Image;

/**
 *
 * @author Thomas
 */
public enum ItemStatus {
    //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
    CREATED("Created", "New task"),
    /**
     * work has started / is currently being worked on
     */
    ONGOING("In progress", "Work has started"),
    /**
     * work has started, but has been put on hold or is waiting for something
     * from the outside (remaining can't do work on it until some external event
     * or input happens)
     */
    WAITING("Waiting", "Task is on hold and waiting for something"),
    DONE("Done", "Task is completed, no more work"),
    CANCELLED("Cancelled", "Task has been cancelled (similar to deleted, but is kept to maintain history)");
    
//String[] descriptionList = new String[]{CREATED.description, ONGOING.getDescription(), WAITING.getDescription(), DONE.getDescription(), CANCELLED.getDescription()};
    final static String[] descriptionList = new String[]{values()[0].fullDescription, values()[1].fullDescription, values()[2].fullDescription, values()[3].fullDescription, values()[4].fullDescription};
    final static String[] nameList = new String[]{values()[0].description, values()[1].description, values()[2].description, values()[3].description, values()[4].description};
    final static int[] descriptionValues = new int[]{values()[0].ordinal(), values()[1].ordinal(), values()[2].ordinal(), values()[3].ordinal(), values()[4].ordinal()};
//    static String iconStyle;
//     static Image[] icons = new Image[]{Icons.iconCheckboxCreated, Icons.iconCheckboxOngoing, Icons.iconCheckboxWaiting, Icons.iconCheckboxDone, Icons.iconCheckboxCancelled};

    private final String description;
    final String fullDescription;

    ItemStatus(String name, String description) {
        this.description = name;
        this.fullDescription = description;
    }

//    public String toString() { //DON'T USE - STORES WRONG VALUE in Parse
//        return shortName;
//    }

    public String getName() {
        return description;
    }

    public String getDescription() {
        return fullDescription;
    }

    static String[] getDescriptionList() {
//            return new String[]{STATUS_CREATED.getDescription(), STATUS_ONGOING.getDescription(), STATUS_WAITING.getDescription(), STATUS_DONE.getDescription(), STATUS_CANCELLED.getDescription()};
        return descriptionList;
    }

    static int[] getDescriptionValues() {
//           return new int[]{STATUS_CREATED.ordinal(), STATUS_ONGOING.ordinal(), STATUS_WAITING.ordinal(), STATUS_DONE.ordinal(), STATUS_CANCELLED.ordinal()};
        return descriptionValues;
    }
    
//    static Image getStatusIconXXX(ItemStatus itemStatus) { //now icons are created in ChechBox
//        return ItemStatus.icons[itemStatus.ordinal()];
//    }

   
            /**
         * returns the enum corresponding to the description string
         *
         * @param description
         * @return enum or null if no enum value corresponds to the description
         * string
         */
        static ItemStatus getValue(String description) {
//            return getValue(description, false);
//        }
//
//        static ItemStatus getValue(String description, boolean shortLabels) {
            String[] descList = getDescriptionList();
            for (int i = 0, size = getDescriptionList().length; i < size; i++) {
                if (descList[i].equals(description)) {
                    return ItemStatus.values()[getDescriptionValues()[i]]; //values() return an array of the ordinals
                }
            }
            return null;
        }


}
