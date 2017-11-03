/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.todocatalyst.todocat;

import com.codename1.ui.ComboBox;

/**
 *
 * @author Thomas
 */
public class ComboBoxOffset extends ComboBox {

    ComboBoxOffset(ListModelInfinite listModelWithOffset) {
        super(listModelWithOffset);
    }

    public int getSelectedValue() {
        return getSelectedIndex() + ((ListModelInfinite)getModel()).min ;
    }
    public void setSelectedValue(int value) {
        super.setSelectedIndex(value - ((ListModelInfinite)getModel()).min);
    }
}
