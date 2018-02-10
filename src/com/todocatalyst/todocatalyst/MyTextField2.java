/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.TextField;
import com.codename1.ui.TextArea;
import com.codename1.ui.util.UITimer;

/**
 *
 * @author Thomas
 */
public class MyTextField2 extends TextField {
    
    public MyTextField2() {
        super();
    }
    
       private UITimer timer;
       public void pointerReleased(int x, int y) {
           super.pointerReleased(x, y);
           if(timer == null) {
//              timer = UITimer.timer(300, false, getComponentForm(), () -> {
              timer = UITimer.timer(300, false, () -> {
//                  singleTapEvent();
                  Log.p("singletap");
                  timer = null;
              });
           } else {
              timer.cancel();
              timer = null;
//              doubleTapEvent();
                  Log.p("doubletap");
           }
       }
    
}
