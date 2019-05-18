/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.ui.Button;

/**
 *
 * @author thomashjelm
 */
public class WrapButton extends SpanButton {

    public WrapButton() {
        super();
    }

    public WrapButton(String text) {
        super(text);
    }

    public Button getActualButton() {
//        return actualButton;
        return (Button) getLeadComponent();
    }

    public void setMaterialIcon(char c) {
        getActualButton().setMaterialIcon(c);
    }

}
