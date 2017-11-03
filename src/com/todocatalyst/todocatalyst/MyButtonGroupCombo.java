/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.todocatalyst.todocatalyst;

import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Container;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.Layout;

/**
 *  encapsulates a ButtonGroup so it appears like a component like ComboBox 
 * @author Thomas
 */
 class MyButtonGroupCombo extends Container {

        ButtonGroup buttonGroup;

        MyButtonGroupCombo(ButtonGroup buttonGrp, Layout buttonGroupLayout) {
            super(buttonGroupLayout);
            buttonGroup = buttonGrp;
        }

        int getSelectedIndex() {
            return buttonGroup.getSelectedIndex();
        }

        void setSelectedIndex(int index) {
            buttonGroup.setSelected(index);
        }

        void addActionListener(ActionListener actionListener) {
            for (int i = 0, size = buttonGroup.getButtonCount(); i < size; i++) {
                buttonGroup.getRadioButton(i).addActionListener(actionListener);
            }
        }
    }
