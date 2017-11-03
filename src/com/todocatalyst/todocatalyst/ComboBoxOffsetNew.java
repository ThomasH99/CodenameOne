/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.ComboBox;
import com.codename1.ui.spinner.Picker;

/**
 *
 * @author Thomas
 */
public class ComboBoxOffsetNew extends Picker {

    private String[] list = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "Last"};

    ComboBoxOffsetNew(ListModelInfinite listModelWithOffset) {
        super();
    }

    ComboBoxOffsetNew() {
        super();
        setStrings(list);
    }

    public void setSelectedValue(int value) {
//        super.setSelectedIndex(value - ((ListModelInfinite)getModel()).min);
        setSelectedString(list[value-1]); //convert from "1" to index 0
    }

    public int getSelectedValue() {
        String selStr = getSelectedString();
        for (int i = 0, size = list.length; i < size; i++) {
            if (list[i].equals(selStr)) {
                return i + 1; //convert from index 0 to "1"
            }
        }
        ASSERT.that(false, "should never happen");
        return -1;
    }
}
