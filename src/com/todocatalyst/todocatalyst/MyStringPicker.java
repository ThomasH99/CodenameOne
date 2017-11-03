/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;
import java.util.Map;

/**
 * 
 * @author Thomas
 */
class MyStringPicker extends Picker {

    MyStringPicker(String[] stringArray, String selectedString) {
//        this(stringArray, null, get, set);
        super();
        this.setType(Display.PICKER_TYPE_STRINGS);
        this.setStrings(stringArray);
        if (selectedString != null) {
            this.setSelectedString(selectedString);
        }
    }

    MyStringPicker(String[] stringArray, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString get, MyForm.PutString set) {
        super();
        this.setType(Display.PICKER_TYPE_STRINGS);
        this.setStrings(stringArray);
        String s = get.get();
        if (s != null) {
            this.setSelectedString(s);
        }
        if (parseIdMap != null) {
            parseIdMap.put(this, () -> set.accept(this.getSelectedString()));
        }
    }

    MyStringPicker(String[] stringArray, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set) {
        super();
        this.setType(Display.PICKER_TYPE_STRINGS);
        this.setStrings(stringArray);
        this.setSelectedString(stringArray[get.get()]);
        if (parseIdMap != null) {
            parseIdMap.put(this, () -> {
                String str = this.getSelectedString();
                for (int i = 0; i < stringArray.length; i++) {
                    if (stringArray[i].equals(str)) {
//                                parseObject.put(parseId, i);
                        set.accept(i);
//                                break;
                        return;
                    }
                }
            });
        }
    }

};
