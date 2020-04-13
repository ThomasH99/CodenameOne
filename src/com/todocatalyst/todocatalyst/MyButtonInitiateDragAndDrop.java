/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.TextArea;

/**
 * implements the code to initiate drag and drop when longPressing the button
 *
 * @author Thomas
 */
//public class MyButtonInitiateDragAndDrop extends SpanButton {
public class MyButtonInitiateDragAndDrop extends MySpanButton {

    private Component dragAndDropComponent;
    private MyForm.GetBoolean isDragAndDropEnabledFct;
    int BUZZER_DRAG_ON_DURATION = 50;//100;

    MyButtonInitiateDragAndDrop(String text, Component dragAndDropComponent, MyForm.GetBoolean isDragAndDropEnabledFunction) {
        super(text);
        this.dragAndDropComponent = dragAndDropComponent;
        this.isDragAndDropEnabledFct = isDragAndDropEnabledFunction;
//        getTextComponent().setVerticalAlignment(TextArea.CENTER);
//        getTextComponent().setVerticalAlignment(TextArea.BOTTOM);
//        getTextComponent().setVerticalAlignment(TextArea.CENTER); //https://stackoverflow.com/questions/36674632/multiple-line-textarea-alignment-in-table-layout-codenameone: can't position text area center
//        getTextComponent().setActAsLabel(true);
//        setUIID("LabelField");
        setUIID("ListOfItemsTextCont");
        setTextUIID("ListOfItemsText");
        setAutoRelease(true); //"A bit of "black magic" to avoid that swipe triggers the button http://stackoverflow.com/questions/39558166/how-to-avoid-that-swiping-a-swipeablecontainer-also-creates-an-event-in-the-top
//        actualButton.setAutoRelease(true); //"A bit of "black magic" to avoid that swipe triggers the button http://stackoverflow.com/questions/39558166/how-to-avoid-that-swiping-a-swipeablecontainer-also-creates-an-event-in-the-top
//        setBlockLead(true);
        addLongPressListener((ev) -> {
            int x = ev.getX();
            int y = ev.getY();
            if (isDragAndDropEnabledFct.get()) {
                if (false) {
                    new Runnable() {
                        public void run() {
                            Display.getInstance().vibrate(BUZZER_DRAG_ON_DURATION);
                        }
                    }.run();
                }
                dragAndDropComponent.setDraggable(true);
                dragAndDropComponent.setFocusable(true);
                Form f = getComponentForm();
                f.pointerPressed(x, y);
                Display.getInstance().callSerially(() -> {
//                    pointerDragged(x - 1, y - 1); //ths dragged element moves by the '1' pixels
                    pointerDragged(x - 2, y - 2); //ths dragged element moves by the '1' pixels
                });
                if (false) {
                    ev.consume(); //not necessary since the event is only send once and there are no other longPress listeners(??)
                }
            } else {
//            Dialog.show("", "Turn Sort OFF to Drag and drop", "OK", null);
                Form f = getComponentForm();
                if (f instanceof MyForm && ((MyForm) f).isShowDragAndDropWarning()) {
                    Dialog.show("", "Drag and drop not possible (view sorted, or D&D not possible in this view)", "OK", null);
                }
            }
        });
    }

//    @Override
//    public void longPointerPressXXX(int x, int y) {
//        if (isDragAndDropEnabledFct.get()) {
//            if (false) {
//                new Runnable() {
//                    public void run() {
//                        Display.getInstance().vibrate(BUZZER_DRAG_ON_DURATION);
//                    }
//                }.run();
//            }
//            dragAndDropComponent.setDraggable(true);
//            dragAndDropComponent.setFocusable(true);
//            Form f = getComponentForm();
//            f.pointerPressed(x, y);
//            pointerDragged(x - 1, y - 1); //ths dragged element moves by the '1' pixels
//            pointerDragged(x - 2, y - 2); //ths dragged element moves by the '1' pixels
//        } else {
////            Dialog.show("", "Turn Sort OFF to Drag and drop", "OK", null);
//            Dialog.show("", "Drag and drop not possible (view sorted, or D&D not possible in this view)", "OK", null);
//        }
//    }
}
