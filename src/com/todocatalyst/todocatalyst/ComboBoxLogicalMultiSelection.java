/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.CheckBox;
import com.codename1.ui.Component;
import com.codename1.ui.List;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.list.ListCellRenderer;
import java.util.Vector;

/**
 *
 * @author Thomas
 */
public class ComboBoxLogicalMultiSelection extends ComboBoxLogicalNames {

    private class ComboBoxMultiSelectionRenderer extends CheckBox implements ListCellRenderer {

        public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
            setFocus(isSelected);
            setText(value.toString());
            setSelected(selected[index]);
            return this;
        }

        public Component getListFocusComponent(List list) {
            return null;
        }
    }
    boolean[] selected;
    private ListCellRenderer renderer = new ComboBoxMultiSelectionRenderer();

    ComboBoxLogicalMultiSelection(int[] values, String[] names) {
        super(values, names);
//        selected = new boolean[values.length]; //by default all values are false <=> unselected
        selected = new boolean[getIterationSize()]; //by default all values are false <=> unselected
        setListCellRenderer(renderer);
        addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                selected[getSelectedIndex()] = !selected[getSelectedIndex()]; //flip selected
            }
        });
    }

    ComboBoxLogicalMultiSelection(int[] values, String[] names, Vector selectedList) {
//        super(values, names);
        this(values, names);
//        setListCellRenderer(renderer);
        setSelected(selectedList);
//        addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent evt) {
//                selected[getSelectedIndex()] = !selected[getSelectedIndex()]; //flip selected
//            }
//        });
    }

    ComboBoxLogicalMultiSelection(int[] values, String[] names, int setBitValues) {
        this(values, names);
        setSelectedOredTogether(setBitValues);
    }

//    ComboBoxLogicalMultiSelection(int[] values, String[] names) {
//        this(values, names, new Vector(0));
//    }
    /**
     * selects all the elements in selectedList (and unselects all others)
     * @param selectedList
     */
    void setSelected(Vector selectedList) {
//        selected = new boolean[values.length];
        if (selectedList == null) {
            return;
        }
//        for (int i = 0, size = selected.length; i < size; i++) {
        for (int i = 0, size = getIterationSize(); i < size; i++) {
            for (int j = 0, size2 = selectedList.size(); j < size2; j++) {
                if (values[i] == ((Integer) selectedList.elementAt(j)).intValue()) {
                    selected[i] = true;
                }
            }
        }
    }

    /**
     * returns Vector with list of selected items
     * @return
     */
    Vector getSelected() {
        Vector selectedList = new Vector();
//        for (int i = 0, size = selected.length; i < size; i++) {
        for (int i = 0, size = getIterationSize(); i < size; i++) {
            if (selected[i]) {
                selectedList.addElement(new Integer(values[i]));
            }
        }
        return selectedList;
    }

    /**
     * set the selected values corresponding to each bit value.
     * NB! Only works for constants defined as a single bit value, eg.
     *     0x1;0x2; 0x4; 0x8; 0x10; 0x20; 0x40; ...
     * @return
     */
    void setSelectedOredTogether(int setBitValues) {
//            if ((setBitValues & values[i]) != 0) {
        for (int i = 0, size = getIterationSize(); i < size; i++) {
            if ((setBitValues & values[i]) != 0) {
                selected[i] = true;
            }
        }
    }

//    void xxsetSelectedOredTogether(int setBitValues) {
//        int bitValue = 1;
////        for (int i = 0, size = Math.min(selected.length, 32); i < size; i++) {
//        for (int i = 0, size = Math.min(selected.length, 32); i < size; i++) {
//            if ((bitValue & setBitValues) != 0) {
////                selected[i] = true;
//                for (int idx = 0, size2 = values.length; idx < size2; idx++) { //optimization: create a reverse array indexed by bit value
//                    if (values[idx] == (bitValue & setBitValues)) { //run through defined values to find the bit value
//                        selected[idx] = true;
////                        continue;
//                    }
//                    //check that a set bit value is always found somewhere in the values array
////                    ASSERT.a(idx, null); //TODO!!!
//                }
//            }
//            bitValue = bitValue << 1;
//        }
//    }

    /**
     * returns the selected values 'OR'ed together (combined with | ).
     * @return
     */
    int getSelectedOredTogether() {
        int result = 0;
//        for (int i = 0, size = selected.length; i < size; i++) {
        for (int i = 0, size = getIterationSize(); i < size; i++) {
            if (selected[i]) {
                result |= values[i];
            }
        }
        return result;
    }
}
