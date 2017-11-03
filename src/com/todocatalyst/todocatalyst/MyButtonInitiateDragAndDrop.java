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

/**
 * implements the code to initiate drag and drop when longPressing the button
 *
 * @author Thomas
 */
public class MyButtonInitiateDragAndDrop extends SpanButton {

    private Component dragAndDropComponent;
    private MyForm.GetBoolean isDragAndDropEnabledFct;
    int BUZZER_DRAG_ON_DURATION = 50;//100;

    MyButtonInitiateDragAndDrop(String text, Component dragAndDropComponent, MyForm.GetBoolean isDragAndDropEnabledFunction) {
        super(text);
        this.dragAndDropComponent = dragAndDropComponent;
        this.isDragAndDropEnabledFct = isDragAndDropEnabledFunction;
//        setUIID("LabelField");
        setUIID("ListOfItemsText");
        setTextUIID("ListOfItemsText");
        setAutoRelease(true); //"A bit of "black magic" to avoid that swipe triggers the button http://stackoverflow.com/questions/39558166/how-to-avoid-that-swiping-a-swipeablecontainer-also-creates-an-event-in-the-top
    }

    @Override
    public void longPointerPress(int x, int y) {
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
            pointerDragged(x - 1, y - 1); //ths dragged element moves by the '1' pixels
            pointerDragged(x - 2, y - 2); //ths dragged element moves by the '1' pixels
        } else {
//            Dialog.show("", "Turn Sort OFF to Drag and drop", "OK", null);
            Dialog.show("", "Drag and drop not possible (view sorted, or D&D not possible in this view)", "OK", null);
        }
    }

}
