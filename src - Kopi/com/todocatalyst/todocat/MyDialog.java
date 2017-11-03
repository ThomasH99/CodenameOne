/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Dialog;
import com.codename1.ui.Image;

/**
 *
 * @author Thomas
 */
public class MyDialog extends Dialog {

    public static Dialog buildDialog(String title, Component body, Command[] cmds, int type) {
        Dialog dialog = new Dialog(title);
        dialog.addComponent(body);
        dialog.setDialogType(type);
        for (int iter = 0; iter < cmds.length; iter++) {
            dialog.addCommand(cmds[iter]);
        }
        return dialog;
    }
}
