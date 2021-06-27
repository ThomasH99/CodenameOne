/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.todocatalyst.todocatalyst.MyForm.GetBool;
import com.todocatalyst.todocatalyst.MyForm.SetBool;

/**
 * A container with an title bar with an expand/collapse icon, when touches will
 * expand to show content, or hide it
 *
 * @author thomashjelm
 */
public class ExpandableContainer extends Container {

    private boolean hidden;
    private boolean longPressShowsThisAndHidesOthers; //if true, longPress shows the current and hide the others
    ActionListener hideListener;

    ExpandableContainer(String title, Component hideableContent, GetBool hideInitiallyFct, SetBool persistExpandedState) {
        this(title, hideableContent, hideInitiallyFct, persistExpandedState, false);
    }

    ExpandableContainer(String title, Component hideableContent, GetBool hideInitiallyFct, SetBool persistExpandedState, boolean longPressShowsAndHidesOthers) {

        setLayout(new BorderLayout());
        setLongPressShowsAndHidesOthers(longPressShowsAndHidesOthers);

//        MySpanButton itemHierarchyTitle = new MySpanButton(title);
//        Container itemHierarchyTitle = new Container(BorderLayout.center());
        Button itemHierarchyTitle = new Button(title, Icons.iconShowLess, "ExpandableContainer");
        itemHierarchyTitle.setTextPosition(Button.LEFT);
//        itemHierarchyTitle.setUIID("ExpandableContainerTitle");
//        itemHierarchyTitle.add(BorderLayout.CENTER,new Label(title, "ExpandableContainerTitle"));
//        Button hideShowButton = new Button("",Icons.iconShowLess, "ExpandableContainerButton");
//        itemHierarchyTitle.add(BorderLayout.EAST,hideShowButton);

        addComponent(BorderLayout.NORTH, itemHierarchyTitle);
        addComponent(BorderLayout.SOUTH, hideableContent);

        hideListener = (e) -> {
//            boolean nowHidden = !hideableContent.isHidden();
            Log.p(" listerner "+title);
            if (e != null && e.getSource() instanceof Boolean) {
                hidden = (Boolean) e.getSource();
            } else {
                hidden = !hidden;
            }
            itemHierarchyTitle.setMaterialIcon(hidden ? Icons.iconShowMore : Icons.iconShowLess);
            if (hideableContent instanceof Container) {
                Container cont = ((Container) hideableContent);
                for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
                    cont.getComponentAt(i).setHidden(hidden);
                }
//                cont.getParent().animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
//                cont.animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
                getParent().animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
            } else {
                hideableContent.setHidden(hidden);
                hideableContent.getParent().animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
            }
            if (persistExpandedState != null) {
                persistExpandedState.setVal(hidden);
            }
        };

        //on longPress show the current and hide the others
        ActionListener hideAllListener = (e) -> {
            Log.p("longpress listerner "+title);
            boolean hideOthers;
            if (longPressShowsThisAndHidesOthers) { //show this and hide the others
                hidden = false;
                hideOthers = true;
            } else { //set hidden of all to the inverse of this container's state
                hidden = !hidden;
                hideOthers = hidden;
            }
            Container commonParent = getParent();
            for (int i = commonParent.getComponentCount() - 1; i >= 0; i--) {
                Component comp = commonParent.getComponentAt(i);
                if (comp instanceof ExpandableContainer) {
                    ExpandableContainer expCont = ((ExpandableContainer) comp);
                    expCont.hideListener.actionPerformed(new ActionEvent(expCont == this ? hidden : hideOthers));
                }
            }
//            commonParent.animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
            getParent().animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
        };

        if (hideInitiallyFct
                != null) {
//            itemHierarchyTitle.setHidden(!hideInitiallyFct.getVal()); //initial state of visibility
//            hideableContent.setHidden(!hideInitiallyFct.getVal()); //initial state of visibility
            hidden = !hideInitiallyFct.getVal(); //initial state of visibility
//            hideListener.actionPerformed(null);
        }

//        itemHierarchyTitle.addActionListener(listener);
//        hideShowButton.addActionListener(listener);
        itemHierarchyTitle.addActionListener(hideListener);

        itemHierarchyTitle.addLongPressListener(hideAllListener);

        hideListener.actionPerformed(
                null); //set initial state of button icons
    }

    ExpandableContainer(String title, Container hideableContent) {
        this(title, hideableContent, () -> false, null);
    }

    ExpandableContainer(String title, Component... components) {
        this(title, BoxLayout.encloseY(components), () -> false, null);
    }

    public void setLongPressShowsAndHidesOthers(boolean on) {
        longPressShowsThisAndHidesOthers = on;
    }
}
