/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.todocatalyst.todocatalyst.MyForm.GetBool;
import com.todocatalyst.todocatalyst.MyForm.SetBool;

/**
 * A container with an title bar with an expand/collapse icon, when touches will
 * expand to show content, or hide it
 *
 * @author thomashjelm
 */
public class ExpandableContainer extends Container {

    ExpandableContainer(String title, Container hideableContent, GetBool hideInitiallyFct, SetBool persistExpandedState) {

        setLayout(new BorderLayout());

//        MySpanButton itemHierarchyTitle = new MySpanButton(title);
//        Container itemHierarchyTitle = new Container(BorderLayout.center());
        Button itemHierarchyTitle = new Button(title,Icons.iconShowLess, "ExpandableContainer");
        itemHierarchyTitle.setTextPosition(Button.LEFT);
//        itemHierarchyTitle.setUIID("ExpandableContainerTitle");
//        itemHierarchyTitle.add(BorderLayout.CENTER,new Label(title, "ExpandableContainerTitle"));
//        Button hideShowButton = new Button("",Icons.iconShowLess, "ExpandableContainerButton");
//        itemHierarchyTitle.add(BorderLayout.EAST,hideShowButton);
        if (hideInitiallyFct != null) {
//            itemHierarchyTitle.setHidden(!hideInitiallyFct.getVal()); //initial state of visibility
            hideableContent.setHidden(!hideInitiallyFct.getVal()); //initial state of visibility
        }

        addComponent(BorderLayout.NORTH, itemHierarchyTitle);
        addComponent(BorderLayout.SOUTH, hideableContent);

        ActionListener listener = (e) -> {
            boolean nowHidden = !hideableContent.isHidden();

            if (persistExpandedState != null) {
                persistExpandedState.setVal(nowHidden);
            }

            hideableContent.setHidden(nowHidden); //inverse hidden status
//            itemHierarchyTitle.setMaterialIcon(nowHidden ? Icons.iconShowMore : Icons.iconShowLess);
//            hideShowButton.setMaterialIcon(nowHidden ? Icons.iconShowMore : Icons.iconShowLess);
            itemHierarchyTitle.setMaterialIcon(nowHidden ? Icons.iconShowMore : Icons.iconShowLess);
            hideableContent.getParent().animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
        };

//        itemHierarchyTitle.addActionListener(listener);
//        hideShowButton.addActionListener(listener);
        itemHierarchyTitle.addActionListener(listener);

        listener.actionPerformed(null); //set initial state of button icons
    }

}
