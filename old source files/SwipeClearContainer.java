/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.SwipeableContainer;

/**
 *
 * @author Thomas
 */
public class SwipeClearContainer extends Container { //TODO!!! unused, delete

//    interface SwipeClear {
//
//        void clearFieldValue();
//
//    }

    SwipeClearContainer(Component cont, SwipeClear swipeClear) {
//        Container newCont = new SwipeableContainer(null, new Button(Command.create("Delete", Icons.iconCloseCircleLabelStyle, (ev) -> {
//        add(new SwipeableContainer(null, new Button(Command.create("Delete", Icons.iconCloseCircleLabelStyle, (ev) -> {
        add(new SwipeableContainer(null, new Button(Command.create("Delete", null, (ev) -> {
            swipeClear.clearFieldValue();
//            cont.repaint();
//            this.getParent().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field? NO, shows Delete button on top of <set>
//            repaint();
//            getComponentForm().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field? 
        })), cont));
    }

}
