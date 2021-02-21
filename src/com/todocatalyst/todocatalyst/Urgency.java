/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Font;

/**
 *
 * @author thomashjelm
 */
public enum Urgency {
    //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
//    HIGH("High"), MEDIUM("Medium"), LOW("Low");
    HIGH("High"),  LOW("Low");

    Urgency(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }

    static String[] getDescriptionList() {
//        return new String[]{LOW.getDescription(), MEDIUM.getDescription(), HIGH.getDescription()};
        return new String[]{LOW.getDescription(),  HIGH.getDescription()};
    }

    static int[] getDescriptionOrdinals() {
//        return new int[]{LOW.ordinal(), MEDIUM.ordinal(), HIGH.ordinal()};
        return new int[]{LOW.ordinal(),  HIGH.ordinal()};
    }

    static String[] getNameList() {
//        return new String[]{LOW.name(), MEDIUM.name(), HIGH.name()};
        return new String[]{LOW.name(),  HIGH.name()};
    }

//        public String toString() { //toString() by default returns .name() which is the fixed ('internal') name of the enum
//            return 
//        }
    /**
     * returns the enum corresponding to the description string
     *
     * @param description
     * @return enum or null if no enum value corresponds to the description
     * string
     */
    static Urgency getValue(String description) {
        String[] descList = getDescriptionList();
        for (int i = 0, size = getDescriptionList().length; i < size; i++) {
            if (descList[i].equals(description)) {
                return Urgency.values()[getDescriptionOrdinals()[i]]; //values() return an array of the ordinals
            }
        }
        return null;
    }

    private final String description;
    
    static char[] getIconList() {
//            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
//        return new String[]{VERY_EASY.name(), EASY.name(), AVERAGE.name(), HARD.name(), VERY_HARD.name()};
        return new char[]{Icons.iconUrgencyLowCust, Icons.iconUrgencyHighCust};
    }

    static Font getIconFont() {
        return Icons.myIconFont;
    }

//    char getIcon() {
//        switch (this) {
//            case VERY_EASY:
//                return Icons.iconChallengeVeryEasy;
//            case EASY:
////                return Icons.iconChallengeEasy;
//                return Icons.iconChallengeEasyCust;
//            case AVERAGE:
//                return Icons.iconChallengeAverage;
//            case HARD:
////                return Icons.iconChallengeHard;
//                return Icons.iconChallengeHardCust;
//            case VERY_HARD:
//                return Icons.iconChallengeVeryHard;
//        }
//        return '?';
//    }
}
