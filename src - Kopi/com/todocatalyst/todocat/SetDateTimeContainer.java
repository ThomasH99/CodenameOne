/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BoxLayout;

/**
 *
 * @author Thomas
 */
public class SetDateTimeContainer extends Container {

    Container dateCont = new Container();
    Container timeCont = new Container();
    Button setDateButton = new Button("Set date");
    Button delDateButton = new Button("Del date");
    Button setTimeButton = new Button("Set time");
    Button delTimeButton = new Button("Del time");
    ActionListener actionListener;
    int state = 0; //0=empty, 1=date, 2=date+time

    SetDateTimeContainer() {
        setLayout(new BoxLayout(BoxLayout.X_AXIS));
        dateCont.addComponent(new DateField5());
        dateCont.addComponent(delDateButton);
        timeCont.addComponent(new DateField5());
        timeCont.addComponent(delTimeButton);

        actionListener = new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                if (state == 0) { //setDate
                    replace(setDateButton, dateCont, null);
                    state = 1;
                } else if (state == 1) { //date + delDate + setTime
                    if (evt.getSource() == delDateButton) {
                        replace(dateCont, delDateButton, null);
                    } else if (evt.getSource() == setTimeButton){
                        dateCont.replace(setTimeButton, timeCont, null);
                    }
                } else if (state == 2) { //date + delDate + time + delTime
                    if (evt.getSource() == delDateButton) {
                        replace(dateCont, delDateButton, null);
                    } else if (evt.getSource() == delTimeButton){
                        dateCont.replace(timeCont, setTimeButton, null);
                    }
                } else {
                    ASSERT.that("error");
                }
            }
        };
        setDateButton.addActionListener(actionListener);
        setTimeButton.addActionListener(actionListener);
    }
}
