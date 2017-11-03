/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.ComboBox;
import com.codename1.ui.list.DefaultListModel;

/**
 * initialize with a list of logical values (e.g. 1, 5, 99) and a list of
 * name labels (e.g. "Up", "Down", "Ingore").
 * getSelectedValue returns the logical value associated with the name label.
 * If no values are defined, then getSelectedValue and setSelectedValue act simply as
 * getSelectedIndex and setSelectedIndex.
 * TODO: add help text to each name used, e.g. if a menu option is "repeat from today", help text could be "Will repeat from the firstcoming day that matches your repeat definition below"
 * @author Thomas
 */
public class ComboBoxLogicalNames extends ComboBox {

    int[] values;
//    String[] names;
    Object[] names;

//    Object[] vectorToArray(Vector vector) {
//        Object[] array = new Object[vector.size()];
//        for (int i=0, size=vector.size(); i<size; i++) {
//            array[i] = vector.elementAt(i);
//        }
//        return array;
//    }

    ComboBoxLogicalNames(int[] values, Object[] names, int selectedValue) {
//        super(names);
        this(values, names);
//        setValues(values, names);
        setSelectedValue(selectedValue);
    }

    ComboBoxLogicalNames(int[] values, Object[] names) {
//        this(values, names, 0);
        super();
        setValues(values, names);
//        super(names);
//        setValues(values, names);
    }


//    ComboBoxLogicalNames(String[] names) {
//        super(names);
//    }

//    public void setValues(int[] values, Object[] names, int selectedIndex) {
    public void setValues(int[] values, Object[] names) {
//        checkValues(values, names);
        ASSERT.that(values.length==names.length && values.length>0, "ComboBoxLogicalNames: values and names not equal length, or zero length");
        super.setModel(new DefaultListModel(names));
        this.values = values;
        this.names = names;
//        setSelectedIndex(selectedIndex);
    }

//    public void setValues(int[] values, Object[] names) {
//        setValues(values, names, 0);
//    }

//    private void checkValues(int[] values, String[] names) {
//        if (values.length != names.length || names.length <1) {
//            try {
////                throw new Exception("ComboBoxLogicalNames: fewer values than names defined");
//                throw new Exception("ComboBoxLogicalNames: not same amount of values and names defined or no values defined");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

//    public void setValues(int[] values) {
//        this.values = values;
//        checkValues(values, names);
//    }
//
//    public void setNames(String[] names) {
//        super.setModel(new DefaultListModel(names)); //does this work??
//        this.names = names;
//        checkValues(values, names);
//    }

    /**
     * returns the selected logical value. NO: If no values are defined, then simply returns the selected index.
     * @return
     */
    public int getSelectedValue() {
//        if (values != null && getSelectedIndex()<values.length) {
            return values[getSelectedIndex()];
//        }
//        else {
//            return getSelectedIndex();
//        }
    }

    /** returns the 'safe' value to iterate over, e.g. for the case where the size of values[] is different
     * from the size of names[]. In case one is longer than the other, the additional values will be ignored.
     * @return
     */
    protected int getIterationSize() {
        return Math.min(values.length, names.length);
    }

    /**
     * if a set of values is defined, then select the corresponding ComboBox line.
     * NO: If not values are set, select the logicalValue index.
     * @param logicalValue
     */
    public void setSelectedValue(int logicalValue) {
//        if (values != null) {
            for (int i = 0, size = getIterationSize(); i < size; i++) {
                if (values[i] == logicalValue) {
                    setSelectedIndex(i);
                    return;
                }
            }
//#mdebug
//        Log.l("ERROR in ComboBoxLogicalNames: illegal logicalValue=" + logicalValue + " not in list of values=" + values);
        ASSERT.that("ERROR in ComboBoxLogicalNames: illegal logicalValue=" + logicalValue + " not in list of values=" + values);
//#enddebug
//        } else {
//            setSelectedIndex(logicalValue);
////        setSelectedIndex(0); //if the logicalValue not found simply select the first //-no, just don't change default value
//        }
    }
}
