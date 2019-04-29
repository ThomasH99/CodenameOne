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

    AutoSaveTimer(Form form, TextField textField, ParseObject parseObject, int timeout, Runnable update) {
        super(() -> {
            if (textField.isEditing()) {
                update.run(); //timedItem.setText(textField.getText());
                DAO.getInstance().saveInBackground(parseObject);
            }
        });
        textField.addDataChangedListener((chgType, index) -> {
            schedule(timeout, false, form); ////reschedule a save on each change, will save after 5 seconds // final  int TEXT_AUTOSAVE_TIMEOUT = 5000; //TODO: make this a setting?
        });

    }
}
