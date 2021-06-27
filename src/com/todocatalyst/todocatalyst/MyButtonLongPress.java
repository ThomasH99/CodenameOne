/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.Button;
import static com.codename1.ui.Button.STATE_DEFAULT;
import static com.codename1.ui.Button.STATE_ROLLOVER;
import com.codename1.ui.Command;
import com.codename1.ui.Display;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import static com.todocatalyst.todocatalyst.MyForm.showToastBar;

/**
 * listens to longPress (and ONLY triggers longPress whereas a longPressListener
 * also triggers a shortPress)
 *
 * @author Thomas
 */
public class MyButtonLongPress extends Button {

//    Command shortPressCmd;
    Command longPressCmd;
//    boolean ignoreActionEvent = false;
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

    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public MyButtonLongPress(final Command shortPressCmd, final Command longPressCmd) {
    //        super();
    //        this.shortPressCmd = shortPressCmd;
    //        this.longPressCmd = longPressCmd;
    //        setCommand(shortPressWrapperCmd);
    //    }
    //</editor-fold>
    public MyButtonLongPress() {
        super();
    }

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

    public MyButtonLongPress(Command shortPressCmd, String longPressHelpText) {
        this(shortPressCmd, CommandTracked.create(null, null, (e) -> {
            showToastBar(longPressHelpText);
        }, "LongPressHelp-" + shortPressCmd.getCommandName()), null);
    }

    public void setLongPressCommand(Command longPressCmd) {
        this.longPressCmd = longPressCmd;
    }

    /*
    https://stackoverflow.com/questions/53584712/codename-one-long-press-event-to-ignore-normal-press
     */
    @Override
    protected void fireActionEvent(int x, int y) {
        if (longPointerPress) { //ignore fireActionEvent after longPress
            longPointerPress = false;
        } else {
            super.fireActionEvent(x, y);
        }
//        state=null;
//        getComponentForm()      
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected void dragInitiated() { //from Button
//        if(Display.getInstance().shouldRenderSelection(this)) {
//            state=STATE_ROLLOVER;
//        } else {
//            state=STATE_DEFAULT;
//        }
//        repaint();
//    }
//</editor-fold>
    protected void dragInitiated() {
        /*
        dragInitiated is called in Form.autoRelease(int x, int y) eg Picker is shown. 
        In that case, Button.pointerRelased(xy) is NOT called, 
        which leaves the longPointerPress hanging as true
        Seems to work as expected with this change!!
         */
        if (longPointerPress) { //ignore fireActionEvent after longPress
            longPointerPress = false;
        }
        super.dragInitiated();
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
////    @Override
//    public void pointerReleasedXX(int x, int y) {
//        if (longPointerPress) {
//            longPointerPress = false;
//        } else {
//            super.pointerReleased(x, y);
//        }
////        ignoreActionEvent = false;
//    }
//
//    public void pointerDraggedXXX(int[] x, int[] y) {
//
//    }
//
//    public void pointerDraggedXXX(final int x, final int y) {
//
//    }
//
//    public void pointerPressedXXX(int[] x, int[] y) {
//
//    }
//
//    public void pointerPressed(int x, int y) {
//        super.pointerPressed(x, y);
//    }
//
//    public void pointerReleasedXXX(int[] x, int[] y) {
//
//    }
//
//    public void pointerReleased(int x, int y) {
//        super.pointerReleased(x, y);
//    }
////public void longPointerPress(int x, int y){
////    
////}
//
//    public void keyPressedXXX(int keyCode) {
//
//    }
//
//    public void keyReleasedXXX(int keyCode) {
//
//    }
//
//    public void keyRepeatedXXX(int keyCode) {
//
//    }
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
//    @Override
//    public void pointerReleased(int x, int y) {
//        if (!longPointerPress) {
//            super.pointerReleased(x, y);
//        } else {
//            setCommand(longPressCmd);
//            super.pointerReleased(x, y);
//            //            longPressCmd.actionPerformed(new ActionEvent(null, x, y));
//            setCommand(shortPressCmd);
//            longPointerPress = false;
//        }
//    }
//
//    public void pointerReleased(int x, int y) {
//        if (longPointerPress) {
//            Command temp = getCommand();
//            setCommand(new Command("")); //temporarily remove command
//            super.pointerReleased(x, y);
//            //            longPressCmd.actionPerformed(new ActionEvent(null, x, y));
//            setCommand(temp);
//            longPointerPress = false;
//        } else {
//            super.pointerReleased(x, y);
//        }
//    }
//    @Override
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
//    }
    //</editor-fold>
}
