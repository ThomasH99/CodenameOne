/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.RadioButton;

/**
 *
 * @author Thomas
 */
public class MyRadioButtonXXX extends RadioButton {

    /**
     * {@inheritDoc}
     */
    public void released(int x, int y) {
        // prevent the radio button from being "turned off"
//        if (isToggle()) { //THJ
//            setSelected(!isSelected()); //THJ
//        } else //THJ
//        {
//            if (!isSelected()) {
//                setSelected(true);
//            }
//        }
        setSelected(!isSelected());
//        Button.released(x, y);
        super.released(x, y);
        //code from Button.released():
//        state = STATE_ROLLOVER;
//        fireActionEvent(x, y);
//        repaint();

    }

}
