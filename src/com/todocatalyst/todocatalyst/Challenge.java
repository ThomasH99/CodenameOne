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
//    private final static String[] challengeNames = new String[]{"Piece of cake", "Easy", "Average", "Tough", "Hard"};
//    private final static int[] challengeValues = new int[]{0, 1, 2, 3, 4};
public enum Challenge {
    //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
//        VERY_EASY("Very easy", "V.easy"), EASY("Easy"), AVERAGE("Average", "Avrg"), HARD("Tough"), VERY_HARD("Hard");
    VERY_EASY("Simple"), EASY("Easy"), AVERAGE("Normal"), HARD("Hard"), VERY_HARD("Tough");

    private final String description;
    private final String shortDescription;

    Challenge(String description, String shortDescription) {
        this.description = description;
        this.shortDescription = shortDescription;
    }

    Challenge(String description) {
        this(description, description);
    }

    String getDescription() {
        return description;
    }

    String getDescription(boolean shortLabels) {
        if (shortLabels) {
            return shortDescription;
        } else {
            return getDescription();
        }
    }

    static String[] getDescriptionList() {
//            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
//        return new String[]{VERY_EASY.description, EASY.description, AVERAGE.description, HARD.description, VERY_HARD.description};
        return new String[]{EASY.description, HARD.description};
    }

    static String[] getDescriptionList(boolean shortLabels) {
        if (shortLabels) {
//            return new String[]{VERY_EASY.shortDescription, EASY.shortDescription, AVERAGE.shortDescription, HARD.shortDescription, VERY_HARD.shortDescription};
            return new String[]{EASY.shortDescription, HARD.shortDescription};
        } else //            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
        {
            return getDescriptionList();
        }
    }

    static int[] getDescriptionValues() {
//        return new int[]{VERY_EASY.ordinal(), EASY.ordinal(), AVERAGE.ordinal(), HARD.ordinal(), VERY_HARD.ordinal()};
        return new int[]{EASY.ordinal(), HARD.ordinal()};
    }

    static String[] getNameList() {
//            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
//        return new String[]{VERY_EASY.name(), EASY.name(), AVERAGE.name(), HARD.name(), VERY_HARD.name()};
        return new String[]{EASY.name(), HARD.name()};
    }

    static char[] getIconList() {
//            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
//        return new String[]{VERY_EASY.name(), EASY.name(), AVERAGE.name(), HARD.name(), VERY_HARD.name()};
        return new char[]{Icons.iconChallengeEasyCust, Icons.iconChallengeHardCust};
    }

    static Font getIconFont() {
        return Icons.myIconFont;
    }

    char getIcon() {
        switch (this) {
            case VERY_EASY:
                return Icons.iconChallengeVeryEasy;
            case EASY:
//                return Icons.iconChallengeEasy;
                return Icons.iconChallengeEasyCust;
            case AVERAGE:
                return Icons.iconChallengeAverage;
            case HARD:
//                return Icons.iconChallengeHard;
                return Icons.iconChallengeHardCust;
            case VERY_HARD:
                return Icons.iconChallengeVeryHard;
        }
        return '?';
    }

    String getChalTxtDebug() { //only for debug
        switch (this) {
            case VERY_EASY:
                return "VEasy";
            case EASY:
                return "Easy";
            case AVERAGE:
                return "Avg";
            case HARD:
                return "Hard";
            case VERY_HARD:
                return "VHard";
        }
        return "Ch??";
    }

    /**
     * returns the enum corresponding to the description string
     *
     * @param description
     * @return enum or null if no enum value corresponds to the description
     * string
     */
    static Challenge getValue(String description) {
        return getValue(description, false);
    }

    static Challenge getValue(String description, boolean shortLabels) {
        String[] descList = getDescriptionList(shortLabels);
        for (int i = 0, size = getDescriptionList().length; i < size; i++) {
            if (descList[i].equals(description)) {
                return Challenge.values()[getDescriptionValues()[i]]; //values() return an array of the ordinals
            }
        }
        return null;
    }
}
