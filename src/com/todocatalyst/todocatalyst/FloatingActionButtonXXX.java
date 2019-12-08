/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.layouts.BoxLayout;

/**
 * https://www.codenameone.com/blog/sticky-headers.html
 *
 * Creates a floating action button on the lower right corner of the screen, expands
 * @author Thomas
 */
public class FloatingActionButtonXXX {

    FloatingActionButtonXXX() {

//        Container con = BoxLayout.encloseY(fabNewsletter, fabAdd, fabMark, fabClear);
        Container con = BoxLayout.encloseY();

        Dialog f = new Dialog();
        f.setDialogUIID("Container");

        f.getLayeredPane().removeAll();
        f.showPacked(MyBorderLayout.CENTER, false);
        f.getLayeredPane().setLayout(new MyBorderLayout());
        f.getLayeredPane().add(MyBorderLayout.SOUTH, new Container(new MyBorderLayout()).add(MyBorderLayout.EAST, con));

        con.animateLayoutAndWait(10);

    }
}
