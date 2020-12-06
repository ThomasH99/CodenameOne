/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Form;
import com.codename1.ui.TextField;
import com.codename1.ui.util.UITimer;
import com.parse4cn1.ParseObject;

/**
 *
 * @author thomashjelm
 */
public class AutoSaveTimer extends UITimer {

    final static int TEXT_AUTOSAVE_TIMEOUT = 2000; //TODO: make this a setting?

    /**
     *
     * @param form the form to which the UITimer is attached
     * @param textField the text field to autosave
     * @param parseObject an optional (can be null) object that will be saved on
     * autotimer, used eg by Timer to save any edited value
     * @param timeout the timeout after which the saving takes place (
     * @param update the update function called on timeout
     */
    private AutoSaveTimer(MyForm form, TextField textField, ParseObject parseObject, int timeout, Runnable update) {
        super(() -> {
            if (textField.isEditing() && !textField.getText().isEmpty() && update != null) { //only need to save if actively editing (when leaving text field, it is saved locally usimg the normal mechanism)
                if (update != null) {
                    update.run(); //timedItem.setText(textField.getText());
                }
                if (parseObject != null) { //                    DAO.getInstance().saveNew(parseObject,true);
//                    DAO.getInstance().saveNew(parseObject);
//                    DAO.getInstance().saveNewTriggerUpdate();
                    DAO.getInstance().saveToParseNow(parseObject);
                }
            }
        });
        textField.addDataChangedListener((chgType, index) -> {
            schedule(timeout, false, form); ////reschedule a save on each change, will save after 5 seconds // 
        });

        //store any initially set value (since editing the field may never be initiated
        if (!textField.getText().isEmpty() && update != null) {
            update.run(); //timedItem.setText(textField.getText());
        }
    }

    AutoSaveTimer(MyForm form, TextField textField, ParseObject parseObject, Runnable update) {
        this(form, textField, parseObject, TEXT_AUTOSAVE_TIMEOUT, update);
    }

    private AutoSaveTimer(MyForm form, TextField textField, String localSaveKey, int timeout) {
        super(() -> {
            if (textField.isEditing()) {
                form.previousValues.put(localSaveKey, textField.getText());
            }
        });
        textField.addDataChangedListener((chgType, index) -> {
            schedule(timeout, false, form); ////reschedule a save on each change, will save after 5 seconds // final  int TEXT_AUTOSAVE_TIMEOUT = 5000; //TODO: make this a setting?
        });

        //store any initially set value (since editing the field may never be initiated
        if (!textField.getText().isEmpty()) {
            form.previousValues.put(localSaveKey, textField.getText());
        }
    }

    AutoSaveTimer(MyForm form, TextField textField, String localSaveKey) {
        this(form, textField, localSaveKey, TEXT_AUTOSAVE_TIMEOUT);
    }

}
