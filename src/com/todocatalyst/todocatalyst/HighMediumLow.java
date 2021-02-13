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
public enum HighMediumLow {
    //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
//    HIGH("High"), MEDIUM("Medium"), LOW("Low");
    LOW("Low"),
    MEDIUM("Medium"), 
    HIGH("High");
//    HIGH("High"), LOW("Low");

    HighMediumLow(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }

    static String[] getDescriptionList() {
//        return new String[]{LOW.getDescription(), MEDIUM.getDescription(), HIGH.getDescription()};
        return new String[]{LOW.getDescription(), HIGH.getDescription()};
    }

    static int[] getDescriptionOrdinals() {
//        return new int[]{LOW.ordinal(), MEDIUM.ordinal(), HIGH.ordinal()};
        return new int[]{LOW.ordinal(), HIGH.ordinal()};
    }

    static String[] getNameList() {
//        return new String[]{LOW.name(), MEDIUM.name(), HIGH.name()};
        return new String[]{LOW.name(), HIGH.name()};
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
    static HighMediumLow getValue(String description) {
        String[] descList = getDescriptionList();
        for (int i = 0, size = getDescriptionList().length; i < size; i++) {
            if (descList[i].equals(description)) {
                return HighMediumLow.values()[getDescriptionOrdinals()[i]]; //values() return an array of the ordinals
            }
        }
        return null;
    }

    private final String description;

    static char[] getImportanceIconList() {
//            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
//        return new String[]{VERY_EASY.name(), EASY.name(), AVERAGE.name(), HARD.name(), VERY_HARD.name()};
        return new char[]{Icons.iconImportanceLowCust, Icons.iconImportanceHighCust};
    }

    static char[] getUrgencyIconList() {
//            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
//        return new String[]{VERY_EASY.name(), EASY.name(), AVERAGE.name(), HARD.name(), VERY_HARD.name()};
        return new char[]{Icons.iconUrgencyLowCust, Icons.iconUrgencyHighCust};
    }

    static Font getIconFont() {
        return Icons.myIconFont;
    }

    char getImportanceIcon() {
        switch (this) {
            case LOW:
                return Icons.iconImportanceLowCust;
            case HIGH:
                return Icons.iconImportanceHighCust;
        }
        return '?';
    }

    char getUrgencyIcon() {
        switch (this) {
            case LOW:
                return Icons.iconUrgencyLowCust;
            case HIGH:
                return Icons.iconUrgencyHighCust;
        }
        return '?';
    }

    static char getImportanceUrgencyIcon(HighMediumLow importance, HighMediumLow urgency) {
        if (importance == HIGH) {
            if (urgency == HIGH) {
                return Icons.iconImpHighUrgHigh;
            } else {
                return Icons.iconImpHighUrgLow;
            }
        } else { //importance==LOW
            if (urgency == HIGH) {
                return Icons.iconImpLowUrgHigh;
            } else {
                return Icons.iconImpLowUrgLow;
            }
            
        }
    }
    
        public static int compare(HighMediumLow d1, HighMediumLow d2) {
        if (d1 == null) {
            if (d2 == null) {
                return 0;
            } else if (d2 == HighMediumLow.HIGH) {
                return -1;
            } else { //(d2==DreadFunValue.DREAD)
                if (Config.TEST) {
                    ASSERT.that(d2 == HighMediumLow.LOW);
                }
                return 1;
            }
        } else {
            if (d2 == null) {
                if (d1 == HighMediumLow.HIGH) {
                    return 1;
                } else { //(d2==DreadFunValue.DREAD)
                    if (Config.TEST) {
                        ASSERT.that(d1 == HighMediumLow.LOW);
                    }
                    return -1;
                }
            } else {
                return (d1.compareTo(d2));
            }
        }
    }


}
