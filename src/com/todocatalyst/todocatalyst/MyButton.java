/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;

/**
 *
 * @author thomashjelm
 */
public class MyButton extends Button {
    
    MyButton(Command cmd) {
        super(cmd);
    }

    public void setLongPressHelp(String helpText) {
        if (helpText != null && !helpText.isEmpty()) {
            addLongPressListener((e) -> MyForm.showToastBar(helpText));
        }
    }

}
