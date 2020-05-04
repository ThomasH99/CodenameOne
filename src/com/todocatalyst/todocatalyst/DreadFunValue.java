/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 *
 * @author thomashjelm
 */
public enum DreadFunValue {
    //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
    FUN("Fun"), NEUTRAL("Neutral"), DREAD("Dread");

    private final String description;

    DreadFunValue(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }

    char getIcon() {
        switch (this) {
            case FUN:
                return Icons.iconFun;
            case NEUTRAL:
                return Icons.iconDreadFunNeutral;
            case DREAD:
                return Icons.iconDread;
        }
        return '?';
    }

    String getTestTxt() {
        switch (this) {
            case FUN:
                return "Fun";
            case NEUTRAL:
                return "Neutr";
            case DREAD:
                return "Dread";
        }
        return "FuDr??";
    }

    //returns description strings for the enum in display order (which may be different from the sort order which follows the declaration order
    static String[] getDescriptionList() {
        return new String[]{FUN.getDescription(), NEUTRAL.getDescription(), DREAD.getDescription()};
    }

    static int[] getDescriptionValues() {
        return new int[]{FUN.ordinal(), NEUTRAL.ordinal(), DREAD.ordinal()};
    }

    /**
     * returns the enum corresponding to the description string
     *
     * @param description
     * @return enum or null if no enum value corresponds to the description
     * string
     */
    static DreadFunValue getValue(String description) {
        String[] descList = getDescriptionList();
        for (int i = 0, size = getDescriptionList().length; i < size; i++) {
            if (descList[i].equals(description)) {
                return DreadFunValue.values()[getDescriptionValues()[i]]; //values() return an array of the ordinals
            }
        }
        return null;
    }
}
