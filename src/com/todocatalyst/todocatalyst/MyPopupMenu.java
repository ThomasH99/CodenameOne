/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Dialog;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;

/**
 *
 * @author Thomas
 */
    class MyPopupMenu extends Dialog {
//Button pop = new Button("Pop");
//pop.addActionListener(e -> {
//    Dialog dlg = new Dialog();

        // makes the dialog transparent
//    dlg.setDialogUIID("Container");
//    dlg.setLayout(BoxLayout.y());
        private Command cancelCmd = null;

        MyPopupMenu(boolean includeCancel, Command... commands) {
            this(includeCancel?new Command("Cancel"):null, commands);
        }

        MyPopupMenu(Command cancelOptional, Command... commands) {
            super();
            setDialogUIID("Container");
            setLayout(BoxLayout.y());
            setDisposeWhenPointerOutOfBounds(true); //close if clicking outside menu

            ComponentGroup group = new ComponentGroup();
            for (Command cmd : commands) {
                group.add(new Button(cmd));
            }
            add(group);

//    Command optionACmd = new Command("Option A");
//    Command optionBCmd = new Command("Option B");
//    Command optionCCmd = new Command("Option C");
//    Command cancelCmd = new Command("Cancel");
//    dlg.add(
//            ComponentGroup.enclose(
//                    new Button(optionACmd), 
//                    new Button(optionBCmd), 
//                    new Button(optionCCmd)
//                    )).
//            add(ComponentGroup.enclose(new Button(cancelCmd)));
//    add(ComponentGroup.enclose(commands));
            this.cancelCmd = cancelOptional;
            if (cancelCmd != null) {
                add(ComponentGroup.enclose(new Button(cancelCmd)));
            }

//    Command result = dlg.showStretched(BorderLayout.SOUTH, true);
//    ToastBar.showMessage("Command " + result.getCommandName(), FontImage.MATERIAL_INFO);
        }

        /**
         * show the menu and execute the selected Command, or do nothing if Cancel is selected
         */
        public void popup() {
//        Command result = dlg.showStretched(BorderLayout.SOUTH, true);
            Command choice = showStretched(BorderLayout.SOUTH, true);
            if (choice != null && choice != cancelCmd) {
                choice.actionPerformed(null);
            }
//                        pop.addActionListener(e -> {

        }
    }
