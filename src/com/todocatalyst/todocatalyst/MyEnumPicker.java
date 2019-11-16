/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;

/**
 * show a picker to select a value of an enum
 *
 * @author Thomas
 */
public class MyEnumPicker<EnumType> extends Picker {

    private Object[] values;
    private String[] strings;
    MyComponentGroup myComponentGroup;

    enum EnumPickerOption {
        asComponentGroup, asPopupMenu
    }

    MyEnumPicker(Enum enumSelectedValues, String[] strings, Object[] values, EnumPickerOption enumPickerOption) {
        setType(Display.PICKER_TYPE_STRINGS);
        setStrings(strings);
        
        this.values = values;
        this.strings = strings;
        switch (enumPickerOption) {
            case asComponentGroup:
//                myComponentGroup = new MyComponentGroup(strings, selectedString, true);
                myComponentGroup = new MyComponentGroup(strings, enumPickerOption.toString(), true);
                break;
            case asPopupMenu:
                break;
        }
        

//        assert enumSelectedValue instanceof Enum;
//        MyComponentGroup group = new MyComponentGroup(strings, parseIdMap2,
//                () -> item.getImportance() == null ? "" : item.getImportance().getDescription(),
//                (s) -> item.setImportance(Item.HighMediumLow.getValue(s)));
    }

    public String getSelectedString() {
//        if (myComponentGroup!=null)
//        return myComponentGroup.getSelectedString();
        return null;
    }

    public Object getSelectedValue() {
//        return values[super.getSelectedStringIndex()];
        return null;
    }

}
