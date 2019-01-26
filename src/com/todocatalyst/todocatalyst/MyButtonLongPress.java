/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;

/**
 *
 * @author Thomas
 */
public class MyButtonLongPress extends Button {

//    Command shortPressCmd;
    Command longPressCmd;
    boolean ignoreActionEvent = false;
    boolean longPointerPress = false;
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    final Command shortPressWrapperCmd = new Command("") {
    //        public void actionPerformed(ActionEvent evt) {
    //            if (ignoreActionEvent) {
    //                ignoreActionEvent = false;
    //                return;
    //            } else {
    //                shortPressCmd.actionPerformed(evt);
    //            }
    //        }
    //    };
    //</editor-fold>
//    final Command shortPressWrapperCmd = new Command("") {
//        public void actionPerformed(ActionEvent evt) {
//            if (ignoreActionEvent) {
//                ignoreActionEvent = false;
//                return;
//            } else {
//                shortPressCmd.actionPerformed(evt);
//            }
//        }
//    };

    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public MyButtonLongPress(final Command shortPressCmd, final Command longPressCmd) {
    //        super();
    //        this.shortPressCmd = shortPressCmd;
    //        this.longPressCmd = longPressCmd;
    //        setCommand(shortPressWrapperCmd);
    //    }
    //</editor-fold>
    public MyButtonLongPress(Command shortPressCmd, Command longPressCmd, Image icon) {
        super(shortPressCmd);
//        this.shortPressCmd = shortPressCmd;
        this.longPressCmd = longPressCmd;
        setCommand(shortPressCmd);
        setUIID("Label");
    }

    public MyButtonLongPress(Command shortPressCmd, Command longPressCmd) {
        this(shortPressCmd, longPressCmd, null);
    }
    
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void longPointerPress(int x, int y) {
    //        super.longPointerPress(x, y);
    //        longPressCmd.actionPerformed(new ActionEvent(null, x, y));
    //        ignoreActionEvent = true;
    //    }
    //    public void longPointerPress(int x, int y) {
    //        super.longPointerPress(x, y);
    //        longPressCmd.actionPerformed(new ActionEvent(null, x, y));
    //        longPointerPress = true;
    ////        longPressCmd.actionPerformed(new ActionEvent(null, x, y));
    ////        ignoreActionEvent = true;
    //    }
    //</editor-fold>
    @Override
    public void longPointerPress(int x, int y) {
        Log.p("MyButtonLongPress.longPointerPress(" + x + ", " + y + ")");
        super.longPointerPress(x, y);
        if (longPressCmd != null) {
            longPressCmd.actionPerformed(new ActionEvent(null, x, y));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void pointerReleased(int x, int y) {
    //        super.pointerReleased(x, y);
    //        ignoreActionEvent = false;
    //    }
    //    public void pointerReleased(int x, int y) {
    //        if (!longPointerPress) {
    //            super.pointerReleased(x, y);
    //        } else {
    //            setCommand(longPressCmd);
    //            super.pointerReleased(x, y);
    ////            longPressCmd.actionPerformed(new ActionEvent(null, x, y));
    //            setCommand(shortPressCmd);
    //            longPointerPress = false;
    //        }
    //    }
    //    public void pointerReleased(int x, int y) {
    //        if (longPointerPress) {
    //            Command temp = getCommand();
    //            setCommand(new Command("")); //temporarily remove command
    //            super.pointerReleased(x, y);
    ////            longPressCmd.actionPerformed(new ActionEvent(null, x, y));
    //            setCommand(temp);
    //            longPointerPress = false;
    //        } else {
    //            super.pointerReleased(x, y);
    //        }
    //    }
    //</editor-fold>
//    public void released(int x, int y) {
//        if (longPointerPress) {
//            Command temp = getCommand();
//            setCommand(new Command("")); //temporarily remove command
//            super.released(x, y);
//            setCommand(temp);
//            longPointerPress = false;
//        } else {
//            super.released(x, y);
//        }
//
//    }
}
