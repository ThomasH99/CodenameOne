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
    public enum HighMediumLow {
        //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
        HIGH("High"), MEDIUM("Medium"), LOW("Low");

        HighMediumLow(String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }

        static String[] getDescriptionList() {
            return new String[]{LOW.getDescription(), MEDIUM.getDescription(), HIGH.getDescription()};
        }

        static int[] getDescriptionOrdinals() {
            return new int[]{LOW.ordinal(), MEDIUM.ordinal(), HIGH.ordinal()};
        }

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
    }

