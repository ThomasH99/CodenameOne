/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.list.DefaultListModel;

/**
 * Easy instantiation of different ListModels for e.g. ComboBoxes.
 *
 * @author Thomas
 */
//class ListModelInfinite implements ListModel {
class ListModelInfinite extends DefaultListModel {

    int index = 0;
    /**
     * the (optional) prefix added to strings
     */
    String prefixStr;
    /**
     * the (optional) postfix added to strings
     */
    String postfixStr;
    /**
     * how much to offset the displayed index from the actual index. E.g. lists
     * always start with 0, but often we want the list to start with 1
     */
    int min;
    /**
     * the maximum value displayed. E.g. 31 if selecting day in month
     */
    int max;
    /**
     * when true, add eg "1st", "2nd", "11th" to displayed numbers
     */
    boolean nthPostFix;
    /**
     * when true, generate nothing for '1', e.g. instead of "every 1 week" you
     * get "every week"
     */
    boolean suppressOnes;
//    int offsetFromZero;
    /**
     * how many chars are removed from the end of the postfix when the number
     * displayed is >1. This allows e.g. to display "1 day" and "2 days". Works
     * for English
     */
    int removeLastNCharsWhenNumberAboveZero;
    /**
     * how many spaces are added between prefix and number
     */
    int numberSpacesAfterPrefix = 1;
    /**
     * how many spaces are added between number and postfix
     */
    int numberSpacesBeforePostfix = 1;
    /**
     * when showing month dates (1..31), then show value 32 as "Last"
     */
//    boolean show32AsLast;
    /**
     * when showing month dates (1..31), then show value 32 (or higher) as this
     * string, e.g. "Last"
     */
    String show32OrHigherAs;

    /**
     *
     * @param min defines the minimum value - NB. All values are offset with
     * this one, so seen from the outside, the list starts with index min
     * @param max
     * @param prefixStr
     * @param postfixStr
     * @param removeLastNCharsWhenNumberAboveZero
     * @param nthPostFix
     */
    ListModelInfinite(int min, int max, String prefixStr, String postfixStr, /*int offsetFromZero,*/ int removeLastNCharsWhenNumberAboveZero, boolean nthPostFix, boolean suppressOnes) {
//        this.min = min;
//        this.max = max;
//        this.prefixStr = prefixStr;
//        this.postfixStr = postfixStr;
////        this.offsetFromZero=offsetFromZero;
//        this.removeLastNCharsWhenNumberAboveZero = removeLastNCharsWhenNumberAboveZero;
//        this.nthPostFix = nthPostFix;
//        this.suppressOnes = suppressOnes;
        setListModelValues(min, max, prefixStr, postfixStr, removeLastNCharsWhenNumberAboveZero, nthPostFix, suppressOnes);
    }

    ListModelInfinite(int max, String prefixStr, String postfixStr) {
        this(1, max, prefixStr, postfixStr, 1, false, false);
    }

    ListModelInfinite(int max, String prefixStr, String postfixStr, boolean suppressOnes) {
        this(1, max, prefixStr, postfixStr, 1, false, suppressOnes);
    }

    ListModelInfinite(int min, int max, boolean nthPostFix) {
        this(min, max, "", "", 0, nthPostFix, false);
    }

//    ListModelInfinite(int min, int max, boolean nthPostFix, boolean show32AsLast) {
//        this(min, max, "", "", 0, nthPostFix, false);
//        this.show32AsLast = show32AsLast;
//    }
    ListModelInfinite(int min, int max, boolean nthPostFix, String show32OrHigherAs) {
        this(min, max, "", "", 0, nthPostFix, false);
        this.show32OrHigherAs = show32OrHigherAs;
    }

    void setListModelValues(int min, int max, String prefixStr, String postfixStr, /*int offsetFromZero,*/ int removeLastNCharsWhenNumberAboveZero, boolean nthPostFix, boolean suppressOnes) {
        this.min = min;
        this.max = max;
        this.prefixStr = prefixStr;
        this.postfixStr = postfixStr;
//        this.offsetFromZero=offsetFromZero;
        this.removeLastNCharsWhenNumberAboveZero = removeLastNCharsWhenNumberAboveZero;
        this.nthPostFix = nthPostFix;
        this.suppressOnes = suppressOnes;
    }

    public void setPrefix(String prefixStr) {
        this.prefixStr = prefixStr;
    }

    public void setPostfix(String postfixStr) {
        this.postfixStr = postfixStr;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }

    private String getSpaces(int numberSpaces) {
        char[] spaceArray = new char[numberSpaces];
        for (int i = 0; i < numberSpaces; i++) {
            spaceArray[i] = ' ';
        }
        String spaces = new String(spaceArray);
//        ASSERT.a(numberSpaces<=1, "Currently only one space supported");
//        return numberSpaces==1?" ":"";
        return spaces;
    }

    public Object getItemAt(int index) {
        String str = "";
        if (show32OrHigherAs != null && index >= 31) { //31 since offset with -1 (index actually goes from 0..31)
            str = show32OrHigherAs;
        } else {
            str = ((suppressOnes && (min + index) == 1) ? "" : "" + (min + index)); //if suppressOnes, don't show anything for value 1
            if (nthPostFix) {
                str = MyDate.addNthPostFix(str);
            }
        }
        if (prefixStr != null) {
//            str = prefixStr + " " + str;
            str = prefixStr + getSpaces(str.length() == 0 ? 0 : numberSpacesAfterPrefix) + str; //"str.length()==0?0:numberSpacesAfterPrefix" - avoid generating double spaces when suppressOnes is active, meaning no number is generated between prefix and postfix
        }
        if (postfixStr != null) {
            str += getSpaces(numberSpacesBeforePostfix)
                    + (((index + min) == 1 && (removeLastNCharsWhenNumberAboveZero > 0) && (postfixStr.length() >= removeLastNCharsWhenNumberAboveZero))
                    ? postfixStr.substring(0, postfixStr.length() - removeLastNCharsWhenNumberAboveZero)
                    : postfixStr);
        }
        return str;
    }

// <editor-fold defaultstate="collapsed" desc="comment">
//    public Object xxxgetItemAt(int index) {
//        String numberStr;
//        if (nthPostFix) {
//            String str = "" + (min + index);
//            char lastChiffer = str.charAt(str.length() - 1);
//            return str + (lastChiffer == 1 ? "st" : (lastChiffer == 2 ? "nd" : (lastChiffer == 3 ? "rd" : "th")));
//        } else {
//            if (index == 0) {
//                return prefixStr + " " + postfixStr.substring(0, postfixStr.length() - 1);
//            } else {
//                return prefixStr + " " + (index + 1) + " " + postfixStr;
//            }
//        }
//    }// </editor-fold>
    public int getSize() {
        return max - min + 1;
    }

    public int getSelectedIndex() {
//        return index+min;
        return index;
    }

    public void setSelectedIndex(int index) {
//        this.index = index-min;
        this.index = index;
    }
//    public void addDataChangedListener(DataChangedListener l) {
//        ASSERT.that(false, "Not implemented");
//    }
//
//    public void removeDataChangedListener(DataChangedListener l) {
//        ASSERT.that(false, "Not implemented");
//    }
//
//    public void addSelectionListener(SelectionListener l) {
//        ASSERT.that(false, "Not implemented");
//    }
//
//    public void removeSelectionListener(SelectionListener l) {
//        ASSERT.that(false, "Not implemented");
//    }
//
//    public void addItem(Object item) {
//        ASSERT.that(false, "Not implemented");
//    }
//
//    public void removeItem(int index) {
//        ASSERT.that(false, "Not implemented");
//    }
}
