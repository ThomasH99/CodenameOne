/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.components.OnOffSwitch;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.Switch;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextField;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
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
        this(fieldLabelTxt, field, helpText, swipeClearFct, wrapText, showAsFieldUneditable, visibleEditButton, hiddenEditButton, false, null);
    }

    EditFieldContainer(String fieldLabelTxt, Component field, String helpText, SwipeClear swipeClearFct,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton, Character materialIcon) {
        this(fieldLabelTxt, field, helpText, swipeClearFct, wrapText, showAsFieldUneditable, visibleEditButton, hiddenEditButton, false, materialIcon);
    }

    EditFieldContainer(String fieldLabelTxt, Component field, String helpText, SwipeClear swipeClearFct,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton, Character materialIcon, Font iconFont) {
        this(fieldLabelTxt, field, helpText, swipeClearFct, wrapText, showAsFieldUneditable, visibleEditButton, hiddenEditButton, false, materialIcon, iconFont);
    }

    /**
     *
     * @param fieldLabelTxt
     * @param fieldN
     * @param helpText
     * @param swipeClearFct
     * @param wrapText use a spanbutton to wrap text that doesn't fit onto one
     * line (should be default)
     * @param showAsFieldUneditable use UIID to show that field is editable
     * @param visibleEditButton make button visible, or invisible but still
     * reserving the space
     * @param hiddenEditButton make button completely hidden (doesn't take any
     * space)
     * @param sizeWestBeforeEast give desired size to west/left container and
     * then what is left to editable field
     * @param materialIcon
     */
    public EditFieldContainer(String fieldLabelTxt, Component fieldN, String helpText, SwipeClear swipeClearFct,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton, boolean sizeWestBeforeEast, Character materialIcon) {
        this(fieldLabelTxt, fieldN, helpText, swipeClearFct, wrapText, showAsFieldUneditable, visibleEditButton, hiddenEditButton, sizeWestBeforeEast, materialIcon, null);
    }

    public EditFieldContainer(String fieldLabelTxt, Component fieldN, String helpText, SwipeClear swipeClearFct,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton, boolean sizeWestBeforeEast,
            Character materialIcon, Font iconFont) {
        this(null, fieldLabelTxt, fieldN, helpText, swipeClearFct, wrapText, showAsFieldUneditable, visibleEditButton, hiddenEditButton, sizeWestBeforeEast, materialIcon, iconFont);
    }

    public EditFieldContainer(boolean OLD, String settingId, String fieldLabelTxt, Component fieldN, String helpText, SwipeClear swipeClearFct,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton, boolean sizeWestBeforeEast,
            Character materialIcon, Font iconFont) {
//        super(new BorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
        super(); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
        if (Config.TEST) {
            setName("FieldCont-" + fieldLabelTxt);
        }
        setUIID(hiddenEditButton ? "EditFieldContainer" : "EditFieldContainerEditable");
//        Container fieldContainer = this;
        MyBorderLayout layout = MyBorderLayout.center();
//        layout.setSizeEastWestMode(MyBorderLayout.SIZE_WEST_BEFORE_EAST);
        layout.setSizeEastWestMode(sizeWestBeforeEast ? MyBorderLayout.SIZE_WEST_BEFORE_EAST : MyBorderLayout.SIZE_EAST_BEFORE_WEST);
        setLayout(layout);

//        if (field instanceof OnOffSwitch | field instanceof MyOnOffSwitch) {
        Component fieldLabel = makeHelpButton(settingId, fieldLabelTxt, helpText, wrapText, materialIcon, iconFont);
        if (fieldN == null) {
//FIELD LABEL
//            Component fieldLabel = makeHelpButton(fieldLabelTxt, helpText, wrapText, materialIcon);
            add(MyBorderLayout.WEST, fieldLabel);
        } else {
            if (fieldN instanceof TextField) {
            } else if (fieldN instanceof MyOnOffSwitch) {
            } else if (fieldN instanceof SpanButton) {
//                ((WrapButton) field).setTextUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
//            ((MySpanButton) field).setTextUIID(showAsFieldUneditable ? "ScreenItemField" : "ScreenItemEditableValue");
                ((SpanButton) fieldN).setTextUIID(showAsFieldUneditable ? "ScreenItemValueUneditable" : "ScreenItemEditableValue");
                ((SpanButton) fieldN).setUIID("Container");
                ((SpanButton) fieldN).getTextComponent().setRTL(true);
            } else if (fieldN instanceof SpanLabel) {
//                ((WrapButton) field).setTextUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
//            ((SpanLabel) field).setTextUIID(showAsFieldUneditable ? "ScreenItemField" : "ScreenItemEditableValue");
                ((SpanLabel) fieldN).setTextUIID("ScreenItemValueUneditable");
                ((SpanLabel) fieldN).setUIID("Container");
                ((SpanLabel) fieldN).getTextComponent().setRTL(true);
            } else if (fieldN instanceof ComponentGroup || fieldN instanceof MyToggleButton) {
            } else {
//                field.setUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
                fieldN.setUIID(showAsFieldUneditable ? "ScreenItemValueUneditable" : "ScreenItemEditableValue");
            }

            //EDIT FIELD
            Component visibleField = null; //contains the edit field and possibly the edit button
            if (!visibleEditButton && hiddenEditButton) {
                visibleField = fieldN;
            } else { //place a visible or invisible button
//            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
                Label editFieldButton = new Label("", "IconEdit"); // [>]
                editFieldButton.setName("FieldContEditButton-" + fieldLabelTxt);
                editFieldButton.setMaterialIcon(Icons.iconEdit);
                editFieldButton.setVisible(!showAsFieldUneditable || visibleEditButton); //Visible, but still using space
                editFieldButton.setHidden(hiddenEditButton); //hidden, not taking any space
//            visibleField = BorderLayout.centerEastWest(field, editFieldButton, null);
//            visibleField = FlowLayout.encloseRight(field, editFieldButton);
                visibleField = BorderLayout.centerCenterEastWest(fieldN, editFieldButton, null);
                if (fieldN instanceof SpanButton) {
//                ((Container) visibleField).setLeadComponent(((WrapButton) field).getActualButton());
                    ((Container) visibleField).setLeadComponent(((SpanButton) fieldN).getLeadComponent());
//                ((Container) visibleField).setLeadComponent(((WrapButton) field).setLeadComponent(field));
//                ((Container) visibleField).setLeadComponent(field.getALeadComponent());
                } else if (!(fieldN instanceof ComponentGroup)) {
                    ((Container) visibleField).setLeadComponent(fieldN);
                }
            }

            //SWIPE CLEAR
            if (swipeClearFct != null) { //ADD SWIPE to delete
                SwipeableContainer swipeCont;
                assert !showAsFieldUneditable : "showAsUneditableField should never be true if we also define a swipeClear function";
                Button swipeDeleteFieldButton = new Button();
//            {
//                protected boolean shouldBlockSideSwipe(){
//                    return true;
//                }
//            };
                swipeDeleteFieldButton.setUIID("ClearFieldButton");
                swipeDeleteFieldButton.setName("FieldContClearBut-" + fieldLabelTxt);
//            swipeCont.blocksSideSwipe();
                swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
                swipeCont.setName("FieldContSwipeable-" + fieldLabelTxt);
                ActionListener l = (ev) -> {
                    swipeClearFct.clearFieldValue();
                    revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
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
//            Component fieldLabel = makeHelpButton(fieldLabelTxt, helpText, wrapText, materialIcon);
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
                        if (fieldN instanceof ComponentGroup) {//|| field instanceof MyToggleButton) { //MyComponentGroups cannot wrap and must be shown fully so split on *two* lines
                            add(MyBorderLayout.NORTH, fieldLabel);
                            add(MyBorderLayout.EAST, visibleField);
                            if (fieldN instanceof ComponentGroup) {
                                fieldN.setUIID("ComponentGroupTwoLines");
                            }
                        } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (false) {
//                            int widthFirstColumn = 0;
//                            int labelRelativeWidthPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
//                            int labelScreenWidthPercent = labelPreferredW * 100 / (availDisplWidth);
//                            if (true) {
//                                if (labelScreenWidthPercent < 45 && fieldLabelTxt.indexOf(" ") == -1) { //label takes up less than 45% of avail space and no spaces (no wrap)
//                                    widthFirstColumn = labelScreenWidthPercent;
//                                }
//                            } else {
////<editor-fold defaultstate="collapsed" desc="comment">
////field should not be less than 30% of width
////                    int labelRelativeWidthPercent = labelPreferredW * 100/ availDisplWidth ;
////                    int fieldRelativeWidthPercent = fieldPreferredW * 100/ availDisplWidth ;
////                        int labelRelativeWidthPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
////                    int fieldRelativeWidthPercent = 100 - labelPreferredW;
////                    if (fieldRelativeWidthPercent < 30) { //visibleField takes up less than 30% of avail space
////                        int widthFirstColumn = Math.min(Math.max(fieldLabelPreferredW / visibleFieldPreferredW * 100, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
////                        widthFirstColumn = 100 - fieldRelativeWidthPercent; //first column gets the rest
////</editor-fold>
//                                if (labelRelativeWidthPercent > 70) { //visibleField takes up less than 30% of avail space
//                                    widthFirstColumn = 70; //first column gets the rest
//                                } else if (labelRelativeWidthPercent < 45 && fieldLabelTxt.indexOf(" ") == -1) { //label takes up less than 45% of avail space and no spaces (no wrap)
//                                    widthFirstColumn = labelRelativeWidthPercent; //give it full space (no wrap)
//                                } else if (labelRelativeWidthPercent < 30) { //visibleField takes up less than 30% of avail space
////                        int widthVisibleFieldPercent = 100 - widthFieldLabelPercent;
////                        int widthLabelPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
//                                    widthFirstColumn = 30; //Math.min(Math.max(widthLabelPercent, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
//                                }
//                            }
//
//                            TableLayout tl = new TableLayout(1, 2);
//                            tl.setGrowHorizontally(true); //grow the remaining right-most column
////                fieldContainer = new Container(tl);
//                            setLayout(tl);
//
//                                    add(tl.createConstraint().verticalAlign(Component.CENTER).horizontalAlign(Component.LEFT).widthPercentage(widthFirstColumn), fieldLabel).
//                                    add(tl.createConstraint().verticalAlign(Component.CENTER).horizontalAlign(Component.RIGHT), visibleField); //align center right
//                        } else
//</editor-fold>
                            {
                                add(MyBorderLayout.WEST, fieldLabel);
                                add(MyBorderLayout.EAST, visibleField);
                            }
                        }
                    } else {
                        add(MyBorderLayout.WEST, fieldLabel);
                        add(MyBorderLayout.EAST, visibleField);
                    }
                } else {
                    add(MyBorderLayout.WEST, fieldLabel);
                    add(MyBorderLayout.EAST, visibleField);
                }
            } else {
//            add(MyBorderLayout.WEST, fieldLabel);
                add(MyBorderLayout.EAST, visibleField);

            }
        }
//        fieldContainer.revalidate(); //right way to get the full text to size up?
//        return fieldContainer;
    }

    private void setFieldUIIDs(Component fieldN, boolean showAsFieldUneditable) {
//        if (fieldN instanceof TextField) {
//        } else if (fieldN instanceof MyOnOffSwitch) {
//        } else 
        if (fieldN instanceof SpanButton) {
            ((SpanButton) fieldN).setTextUIID(showAsFieldUneditable ? "ScreenItemValueUneditable" : "ScreenItemEditableValue");
            ((SpanButton) fieldN).setUIID("Container");
            ((SpanButton) fieldN).getTextComponent().setRTL(true);
        } else if (fieldN instanceof SpanLabel) {
            ((SpanLabel) fieldN).setTextUIID("ScreenItemValueUneditable");
            ((SpanLabel) fieldN).setUIID("Container");
            ((SpanLabel) fieldN).getTextComponent().setRTL(true);
//        } else if (fieldN instanceof ComponentGroup || fieldN instanceof MyToggleButton) {
        } else if(!(fieldN instanceof Switch)){
//                field.setUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
            fieldN.setUIID(showAsFieldUneditable ? "ScreenItemValueUneditable" : "ScreenItemEditableValue");
        }
    }

//    private void setIcon(SpanButton spanButton, Character materialIcon, Font iconFont) {
//        if (materialIcon != null) {
//            if (iconFont != null) {
//                spanButton.setFontIcon(iconFont, materialIcon);
//            } else {
//                spanButton.setMaterialIcon(materialIcon);
//            }
//        }
//    }
    private SpanButton makeFieldLabel(String settingId, String label, String helpText, boolean makeSpanButton, Character materialIcon, Font iconFont) {
        if (label == null) {
            return null;
        }
        SpanButton spanButton = new SpanButton(label, "ScreenItemFieldLabel"); //sets text UIID
        spanButton.setTextPosition(Component.RIGHT); //put icon on the left
//        setIcon(spanButton, materialIcon, iconFont);
        if (materialIcon != null) {
            if (iconFont != null) {
                spanButton.setFontIcon(iconFont, materialIcon);
            } else {
                spanButton.setMaterialIcon(materialIcon);
            }
        }
        spanButton.setUIID("Container"); //avoid adding additional white space by setting the Container UIID to LabelField
        spanButton.setName("FieldContHlpSpanBut-" + label); //avoid adding additional white space by setting the Container UIID to LabelField
        spanButton.setIconUIID("ScreenItemFieldIcon"); //avoid adding additional white space by setting the Container UIID to LabelField
        return spanButton;
    }

    public EditFieldContainer(String settingId, String fieldLabelTxt, Component editFieldN, String helpText, SwipeClear swipeClearFct,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton, boolean sizeWestBeforeEast,
            Character materialIcon, Font iconFont) {
        super(); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
        if (Config.TEST) {
            setName("FieldCont-" + fieldLabelTxt);
        }
        setUIID(hiddenEditButton ? "EditFieldContainer" : "EditFieldContainerEditable");
        MyBorderLayout layout = MyBorderLayout.center();
        layout.setSizeEastWestMode(sizeWestBeforeEast ? MyBorderLayout.SIZE_WEST_BEFORE_EAST : MyBorderLayout.SIZE_EAST_BEFORE_WEST);
        setLayout(layout);

        SpanButton fieldLabel = null;
        if (fieldLabelTxt != null && !fieldLabelTxt.isEmpty()) {
            fieldLabel = makeFieldLabel(settingId, fieldLabelTxt, helpText, wrapText, materialIcon, iconFont);
            if (helpText != null && !helpText.isEmpty()) {
                Component helpTxt = new SpanLabel(helpText, "FieldHelpText");
                ActionListener al;
                if (settingId == null) {
                    helpTxt.setHidden(true); //if no setting available, hide by default
                    al = (e) -> {
                        helpTxt.setHidden(!helpTxt.isHidden());
                        helpTxt.getParent().getParent().animateLayout(MyForm.ANIMATION_TIME_FAST);
                    };
                } else {
                    String helpSettingId = settingId + "ShowHelp";
                    helpTxt.setHidden(!MyPrefs.getBoolean(helpSettingId));
                    al = (e) -> {
//                        MyPrefs.setBoolean(helpSettingId, !MyPrefs.getBoolean(helpSettingId)); //flip
                        MyPrefs.flipBoolean(helpSettingId); //flip
                        helpTxt.setHidden(!MyPrefs.getBoolean(helpSettingId));
                        helpTxt.getParent().getParent().animateLayout(MyForm.ANIMATION_TIME_FAST);
                    };
                }
                if (MyPrefs.helpShowHelpOnLongPress.getBoolean()) {
                    fieldLabel.addLongPressListener(al);
                } else {
                    fieldLabel.addActionListener(al);
                }
                add(MyBorderLayout.SOUTH, helpTxt);
            }
        }

        if (editFieldN == null) {
//            if(fieldLabel!=null) //SHOULND'T happen, not both label and field should be empty/undefined
            add(MyBorderLayout.WEST, fieldLabel);
        } else { //editFieldN != null
            //EDIT FIELD
            setFieldUIIDs(editFieldN, showAsFieldUneditable);
            Component visibleField = null; //contains the edit field and possibly the edit button
            if (!visibleEditButton && hiddenEditButton) {
                visibleField = editFieldN;
            } else { //place a visible or invisible button
                Label editFieldButton = new Label("", "IconEdit"); // [>]
                editFieldButton.setName("FieldContEditButton-" + fieldLabelTxt);
                editFieldButton.setMaterialIcon(Icons.iconEdit);
                editFieldButton.setVisible(!showAsFieldUneditable || visibleEditButton); //Visible, but still using space
                editFieldButton.setHidden(hiddenEditButton); //hidden, not taking any space
                visibleField = BorderLayout.centerCenterEastWest(editFieldN, editFieldButton, null);
                if (editFieldN instanceof SpanButton) {
                    ((Container) visibleField).setLeadComponent(((SpanButton) editFieldN).getLeadComponent());
                } else if (!(editFieldN instanceof ComponentGroup)) {
                    ((Container) visibleField).setLeadComponent(editFieldN);
                }

                //SWIPE CLEAR
                if (swipeClearFct != null) { //ADD SWIPE to delete
                    SwipeableContainer swipeCont;
                    assert !showAsFieldUneditable : "showAsUneditableField should never be true if we also define a swipeClear function";
                    Button swipeDeleteFieldButton = new Button();
                    swipeDeleteFieldButton.setUIID("ClearFieldButton");
                    swipeDeleteFieldButton.setName("FieldContClearBut-" + fieldLabelTxt);
                    swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
                    swipeCont.setName("FieldContSwipeable-" + fieldLabelTxt);
                    ActionListener l = (ev) -> {
                        swipeClearFct.clearFieldValue();
                        revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
                        swipeCont.close();
                    };
                    if (false) { //deactivate since bad UI, better to show the clear command and leave it to the user to clear
                        swipeCont.addSwipeOpenListener(l);
                    }
                    swipeDeleteFieldButton.setCommand(Command.createMaterial("", Icons.iconCloseCircle, l));
                    visibleField = swipeCont;
                }
            }

            wrapText = true;
            //Place label and field
            if (fieldLabel == null) {
                add(MyBorderLayout.EAST, visibleField);
            } else { //fieldLabel != null
                if (wrapText) {
                    int availDisplWidth = (Display.getInstance().getDisplayWidth() * 90) / 100; //asumme roughly 90% of width is available after margins
                    int marginPlusPaddingWidth = getStyle().getHorizontalMargins() + getStyle().getHorizontalPadding();
                    int labelPreferredW = fieldLabel.getPreferredW();
                    int fieldPreferredW = visibleField.getPreferredW();
                    if (labelPreferredW + fieldPreferredW > availDisplWidth && editFieldN instanceof ComponentGroup) { //if too wide
                        add(MyBorderLayout.NORTH, fieldLabel);
//                        add(MyBorderLayout.EAST, visibleField);
                        editFieldN.setUIID("ComponentGroupTwoLines");
                    } else {
                        add(MyBorderLayout.WEST, fieldLabel);
//                        add(MyBorderLayout.EAST, visibleField);
                    }
                    add(MyBorderLayout.EAST, visibleField);
                }
            }
        }
    }

}
