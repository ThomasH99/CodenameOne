/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;

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
//    private Command cancelCmd = null;
//    MyPopupMenu(boolean includeCancel, Command... commands) {
//        this(includeCancel ? new Command("Cancel") : null, commands);
//    }
//
//    MyPopupMenu(Command cancelOptional, Command... commands) {
//        this(cancelOptional, "MyPopupMenu", commands);
//    }
//
//    MyPopupMenu(Command cancelOptional, String style, Command... commands) {
//
//    }
    Component refButton;

    MyPopupMenu(String groupStyle, Button cancel, Button... commands) {
        super();
//        setDialogUIID("Container");
        setDialogUIID("PickerDialog");
        getContentPane().setUIID("MyPopupContentPane");
        setAutoAdjustDialogSize(true);
//        setDialogType(Dialog.true);
        setLayout(BoxLayout.y());
        setDisposeWhenPointerOutOfBounds(true); //close if clicking outside menu

//        ComponentGroup group = new ComponentGroup();
//        group.setUIID(style);
////        for (Command cmd : commands) {
////            group.add(new Button(cmd));
////        }
//        for (Button cmd : commands) {
//            group.add(cmd);
//        }
//        add(group);
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
//        this.cancelCmd = cancelOptional;
//        if (cancelCmd != null) {
//            add(ComponentGroup.enclose(new Button(cancelCmd)));
//        }
//        setCommands(groupStyle, cancel, commands);
        setSameWidth(commands);
        setCommands(groupStyle, null, commands);

//    Command result = dlg.showStretched(BorderLayout.SOUTH, true);
//    ToastBar.showMessage("Command " + result.getCommandName(), FontImage.MATERIAL_INFO);
    }

    MyPopupMenu(Component refButton, Button... buttons) {
        super();
        this.refButton = refButton;
//        setDialogUIID("PickerDialog");
        setDialogUIID("PopupDialog");
        getTitleComponent().setUIID("Container"); //force title component to zero size (avoid margign/padding)
        getContentPane().setUIID("MyPopupContentPane");
//        setAutoAdjustDialogSize(true);
        setLayout(BoxLayout.y());
//        setLayout(new BoxLayout(LEFT));
        setDisposeWhenPointerOutOfBounds(true); //close if clicking outside menu

        setSameWidth(buttons);
        Container cmdList = BoxLayout.encloseYCenter(buttons);
        add(cmdList);
    }

    private void setCommandsOLD(String groupStyle, Button cancel, Button... buttons) {
        ComponentGroup group = ComponentGroup.enclose(buttons);
//        ComponentGroup group = new ComponentGroup();
        group.setUIID(groupStyle);
//        for (Button b : buttons) {
//            group.add(b);
//        }
        add(group);
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
//        this.cancelCmd = cancelOptional;
        if (cancel != null) {
//            add(ComponentGroup.enclose(new Button(cancelCmd)));
            group = ComponentGroup.enclose(cancel);
            group.setUIID(groupStyle);
            add(group);
        }
    }

    private void setCommands(String groupStyle, Button cancel, Button... buttons) {
        setSameWidth(buttons);
//        Container cmdList = BoxLayout.encloseYCenter(buttons);
        Container cmdList = BoxLayout.encloseYCenter(buttons);
//        Container cmdList = FlowLayout.encloseLeftMiddle(buttons);
        add(cmdList);
    }

    /**
     * show the menu and execute the selected Command, or do nothing if Cancel
     * is selected
     */
    public void popup() {
//        Command result = dlg.showStretched(BorderLayout.SOUTH, true);
//        Command choice = showStretched(BorderLayout.SOUTH, true);
        Command choice = showPopupDialog(refButton);
//        Command choice = showPacked(BorderLayout.SOUTH, true);
//        if (choice != null && choice != cancelCmd) {
        if (false && choice != null) { //NO need to run actionPerformed here, it's already done by added buttons
            choice.actionPerformed(null);
        }
//                        pop.addActionListener(e -> {
    }
}
