/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.components.OnOffSwitch;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.table.TableLayout;
import static com.todocatalyst.todocatalyst.MyForm.makeHelpButton;

/**
 *
 * @author thomashjelm
 */
public class EditFieldContainer extends Container {

    EditFieldContainer(String fieldLabelTxt, Component field, String helpText, SwipeClear swipeClearFct,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton) {
        this(fieldLabelTxt, field, helpText, swipeClearFct, wrapText, showAsFieldUneditable, visibleEditButton, hiddenEditButton, null);
    }

    EditFieldContainer(String fieldLabelTxt, Component field, String helpText, SwipeClear swipeClearFct,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton, Image fieldIcon) {
        super(new MyBorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
        setUIID("EditFieldContainer");
        Container fieldContainer = this;
        MyBorderLayout layout = MyBorderLayout.center();
        layout.setSizeEastWestMode(MyBorderLayout.SIZE_WEST_BEFORE_EAST);
        fieldContainer.setLayout(layout);

//        if (field instanceof OnOffSwitch | field instanceof MyOnOffSwitch) {
        if (field instanceof TextField) {
        } else if (field instanceof MyOnOffSwitch) {
        } else if (field instanceof WrapButton) {
//                ((WrapButton) field).setTextUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
            ((WrapButton) field).setTextUIID(showAsFieldUneditable ? "ScreenItemField" : "ScreenItemValue");
            ((WrapButton) field).setUIID("Container");
            ((WrapButton) field).getTextComponent().setRTL(true);
        } else if (field instanceof ComponentGroup) {
        } else {
//                field.setUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
            field.setUIID(showAsFieldUneditable ? "ScreenItemValueUneditable" : "ScreenItemValue");
        }

        //EDIT FIELD
        Component visibleField = null; //contains the edit field and possibly the edit button
        if (!visibleEditButton && hiddenEditButton) {
            visibleField = field;
        } else { //place a visible or invisible button
//            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
            Label editFieldButton = new Label("", "IconEdit"); // [>]
            editFieldButton.setMaterialIcon(Icons.iconEdit);
            editFieldButton.setVisible(!showAsFieldUneditable || visibleEditButton); //Visible, but still using space
            editFieldButton.setHidden(hiddenEditButton); //hidden, not taking any space
//            visibleField = BorderLayout.centerEastWest(field, editFieldButton, null);
//            visibleField = FlowLayout.encloseRight(field, editFieldButton);
            visibleField = BorderLayout.centerCenterEastWest(field, editFieldButton, null);
            if (field instanceof WrapButton) {
//                ((Container) visibleField).setLeadComponent(((WrapButton) field).getActualButton());
                ((Container) visibleField).setLeadComponent(((WrapButton) field).getLeadComponent());
//                ((Container) visibleField).setLeadComponent(((WrapButton) field).setLeadComponent(field));
//                ((Container) visibleField).setLeadComponent(field.getALeadComponent());
            } else {
                ((Container) visibleField).setLeadComponent(field);
            }
        }

        //SWIPE CLEAR
        if (swipeClearFct != null) { //ADD SWIPE to delete
            SwipeableContainer swipeCont;
            assert !showAsFieldUneditable : "showAsUneditableField should never be true if we also define a swipeClear function";
            Button swipeDeleteFieldButton = new Button();
            swipeDeleteFieldButton.setUIID("ClearFieldButton");
            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
                ActionListener l = (ev) -> {
                    swipeClearFct.clearFieldValue();
                    fieldContainer.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
                    swipeCont.close();
                };
            if (false) { //deactivate since bad UI, better to show the clear command and leave it to the user to clear
                swipeCont.addSwipeOpenListener(l);
            }
//            swipeDeleteFieldButton.setCommand(Command.create("", Icons.iconCloseCircleLabelStyle, l));
            swipeDeleteFieldButton.setCommand(Command.createMaterial("", Icons.iconCloseCircle, l));
            visibleField = swipeCont;
        }

        //FIELD LABEL
        Component fieldLabel = makeHelpButton(fieldLabelTxt, helpText, wrapText);
//        if (true) {
//            fieldContainer.add(MyBorderLayout.WEST, fieldLabel);
//            fieldContainer.add(MyBorderLayout.EAST, visibleField);
//        } else 
        if (fieldLabel != null) {
            if (wrapText) {
                int availDisplWidth = (Display.getInstance().getDisplayWidth() * 90) / 100; //asumme roughly 90% of width is available after margins
//            int availDisplWidthParent = getPaDisplay.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
                int labelPreferredW = fieldLabel.getPreferredW();
                int fieldPreferredW = visibleField.getPreferredW();
                if (labelPreferredW + fieldPreferredW > availDisplWidth) { //if too wide
                    if (field instanceof MyComponentGroup) { //MyComponentGroups cannot wrap and must be shown fully so split on *two* lines
                        fieldContainer.add(MyBorderLayout.NORTH, fieldLabel);
                        fieldContainer.add(MyBorderLayout.EAST, visibleField);
                    } else {
                        int widthFirstColumn = 0;
                        int labelRelativeWidthPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
                        int labelScreenWidthPercent = labelPreferredW * 100 / (availDisplWidth);
                        if (true) {
                            if (labelScreenWidthPercent < 45 && fieldLabelTxt.indexOf(" ") == -1) { //label takes up less than 45% of avail space and no spaces (no wrap)
                                widthFirstColumn = labelScreenWidthPercent;
                            }
                        } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//field should not be less than 30% of width
//                    int labelRelativeWidthPercent = labelPreferredW * 100/ availDisplWidth ;
//                    int fieldRelativeWidthPercent = fieldPreferredW * 100/ availDisplWidth ;
//                        int labelRelativeWidthPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
//                    int fieldRelativeWidthPercent = 100 - labelPreferredW;
//                    if (fieldRelativeWidthPercent < 30) { //visibleField takes up less than 30% of avail space
//                        int widthFirstColumn = Math.min(Math.max(fieldLabelPreferredW / visibleFieldPreferredW * 100, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
//                        widthFirstColumn = 100 - fieldRelativeWidthPercent; //first column gets the rest
//</editor-fold>
                            if (labelRelativeWidthPercent > 70) { //visibleField takes up less than 30% of avail space 
                                widthFirstColumn = 70; //first column gets the rest
                            } else if (labelRelativeWidthPercent < 45 && fieldLabelTxt.indexOf(" ") == -1) { //label takes up less than 45% of avail space and no spaces (no wrap)
                                widthFirstColumn = labelRelativeWidthPercent; //give it full space (no wrap)
                            } else if (labelRelativeWidthPercent < 30) { //visibleField takes up less than 30% of avail space 
//                        int widthVisibleFieldPercent = 100 - widthFieldLabelPercent;
//                        int widthLabelPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
                                widthFirstColumn = 30; //Math.min(Math.max(widthLabelPercent, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
                            }
                        }
                        TableLayout tl = new TableLayout(1, 2);
                        tl.setGrowHorizontally(true); //grow the remaining right-most column
//                fieldContainer = new Container(tl);
                        fieldContainer.setLayout(tl);
                        fieldContainer.
                                add(tl.createConstraint().verticalAlign(Component.CENTER).horizontalAlign(Component.LEFT).widthPercentage(widthFirstColumn), fieldLabel).
                                add(tl.createConstraint().verticalAlign(Component.CENTER).horizontalAlign(Component.RIGHT), visibleField); //align center right
                    }
                } else {
                    fieldContainer.add(MyBorderLayout.WEST, fieldLabel);
                    fieldContainer.add(MyBorderLayout.EAST, visibleField);
                }
            } else {
                fieldContainer.add(MyBorderLayout.WEST, fieldLabel);
                fieldContainer.add(MyBorderLayout.EAST, visibleField);
            }
        } else {
//            fieldContainer.add(MyBorderLayout.WEST, fieldLabel);
            fieldContainer.add(MyBorderLayout.EAST, visibleField);

        }
//        fieldContainer.revalidate(); //right way to get the full text to size up?
//        return fieldContainer;
    }

}
