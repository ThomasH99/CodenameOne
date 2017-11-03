/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.CheckBox;
import com.codename1.ui.ComboBox;
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
public class ComboBoxMultiSelection extends ComboBox {

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

    ComboBoxMultiSelection(Vector list) {
        setListAndSelected(list, null);
    }

    /**
     *
     * @param elementList
     * @param selectedList
     */
    ComboBoxMultiSelection(Vector elementList, Vector selectedList) {
        setListAndSelected(elementList, selectedList);
    }

    ComboBoxMultiSelection(ItemList list, Vector selectedList) {
//        setListAndSelected(list.getVector(), selectedList);
        setListAndSelected(new Vector(list.getList()), selectedList);
    }

    /** selects */
//    ComboBoxMultiSelection(ItemList list) {
//        setListAndSelected(list.getVector(), list.getSelectedAsVector());
//    }

    void setListAndSelected(Vector elementList, Vector selectedList) {
        selected = new boolean[elementList.size()]; //all unselected by default
        if (selectedList != null) {
            setSelected(selectedList);
        }
        addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                selected[getSelectedIndex()] = !selected[getSelectedIndex()]; //flip selected
            }
        });
    }

    /**
     * selects all the elements in selectedList (and unselects all others)
     * @param selectedList
     */
    void setSelected(Vector selectedList) {
        for (int i = 0, size = selected.length; i < size; i++) {
            for (int j = 0, size2 = selectedList.size(); j < size2; j++) {
                selected[i] = (getModel().getItemAt(i) == selectedList.elementAt(j));
            }
        }
    }

    /**
     * returns Vector with list of selected items
     * @return
     */
    Vector getSelected() {
        Vector selectedList = new Vector();
        for (int i = 0, size = selected.length; i < size; i++) {
            if (selected[i]) {
                selectedList.addElement(getModel().getItemAt(i));
            }
        }
        return selectedList;
    }
}
