/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Container;
import com.codename1.ui.RadioButton;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.util.EventDispatcher;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author Thomas
 */
/**
 * cannot select multiple values! stores the index of the string array in Parse.
 * E.g. ["str1", "str2", "str3"] and str2 selected will store 1, str1 will store
 * 0.
 */
/**
 *
 * @author Thomas
 */
class MyComponentGroup extends ComponentGroup {

    Object[] values;
    String[] names;
//    ButtonGroup buttonGroup;
    private Button[] buttonsArray;

    private static int getIndexOfValue(Object[] values, Object value) {
        for (int i = 0, size = values.length; i < size; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    private int getIndexOfValue(Object value) {
        return getIndexOfValue(values, value);
    }

    private static int getIndexOfName(String[] fieldNames, String name) {
        for (int i = 0, size = fieldNames.length; i < size; i++) {
            if (fieldNames[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private int getIndexOfName(String name) {
        return getIndexOfName(names, name);
    }

    @Override
    public String toString() {
        String s = "";
        int selIdx = getSelectedIndex();
        for (int i = 0, size = names.length; i < size; i++) {
            s += names[i] + (i == selIdx ? "[x] | " : " | ");
        }
        return s;
    }

    /**
     *
     * @param values either an array of String or an array of Objects for which
     * toString() will be used
     * @param selectedString
     * @param unselectAllowed
     */
    MyComponentGroup(Object[] valueArray, String[] names, boolean unselectAllowed, boolean verticalLayout) {
        this(valueArray, names, unselectAllowed, verticalLayout, false);
    }

    MyComponentGroup(Object[] valueArray, String[] names, boolean unselectAllowed, boolean verticalLayout, boolean multipleSelectionAllowed) {
        this(valueArray, names, unselectAllowed, verticalLayout, multipleSelectionAllowed, false);
    }

//    boolean ignoreNextActionEvent = false;
    MyComponentGroup(Object[] valueArray, String[] names, boolean unselectAllowed, boolean verticalLayout, boolean multipleSelectionAllowed, boolean noSelectionAllowed) {
        super();
        this.values = valueArray;
        if (names != null) {
            this.names = names;
            ASSERT.that(this.names.length == values.length, "MyComponentGroup called with different number of values and names, values=" + valueArray + ", names=" + names);
        } else {
            this.names = new String[values.length];
            for (int i = 0, size = values.length; i < size; i++) {
                this.names[i] = values[i].toString();
            }
        }

        this.setHorizontal(!verticalLayout);

//        buttonGroup = new ButtonGroup();
        ActionListener buttonListener = (e) -> {
//            if (ignoreNextActionEvent) {
//                ignoreNextActionEvent = false;
//                return;
//            }

            Button source = (Button) e.getSource();

            //if noSelectionAllowed is not allowed, prevent unselecting the last selected checkbox (force it back to selected which triggers another actionEvent which must be ignored here)
            if (!noSelectionAllowed && !source.isSelected() && source instanceof CheckBox && getSelectedCount() == 0) {
//                ignoreNextActionEvent = true;
                ((CheckBox) source).setSelected(true);
            }

            if (dispatcher != null) {
                dispatcher.fireActionEvent(e);
            }

//<editor-fold defaultstate="collapsed" desc="comment">
//            for (int i = 0, size = this.getComponentCount(); i < size; i++) {
//                if (source.equals(this.getComponentAt(i))) {
//                    if (((Button) this.getComponentAt(i)).isSelected()) {
//                        if (selectionListener != null) {
//                            selectionListener.fireSelectionEvent(getSelectedIndex(), i);
//                        }
//                    } else if (selectionListener != null) {
//                        selectionListener.fireSelectionEvent(getSelectedIndex(), -1); //CORRECT?!
//                    }
//                }
//            }
//</editor-fold>
            for (int i = 0, size = buttonsArray.length; i < size; i++) {
                if (source.equals(buttonsArray[i])) {
                    if (buttonsArray[i].isSelected()) {
                        if (selectionListener != null) {
                            selectionListener.fireSelectionEvent(getSelectedIndex(), i);
                        }
                    } else if (selectionListener != null) {
                        selectionListener.fireSelectionEvent(getSelectedIndex(), -1); //CORRECT?!
                    }
                }
            }
        };

        buttonsArray = new Button[values.length];

//<editor-fold defaultstate="collapsed" desc="comment">
//        ButtonGroup buttonGroup = new ButtonGroup() {
//            @Override
//            public void setSelected(RadioButton rb) {
//                //if radionbutton is pressed when it is already selected then unselect (clearSelection)
//                if (isSelected() && rb.isSelected()) {
//                    clearSelection();
//                } else {
//                    super.setSelected(rb); //else handle it normally
//                }
//            }
//        };
//</editor-fold>
        RadioButton radioButton;
        ButtonGroup buttonGroup = null;
//            RadioButton[] radioButtonArray = new RadioButton[values.length];
        for (int i = 0; i < values.length; i++) {
//            radioButton = new RadioButton(values[i]);
//<editor-fold defaultstate="collapsed" desc="comment">
//            radioButton = new RadioButton(values[i] instanceof String?(String)values[i]:values[i].toString());
//            String s;
//            if (fieldNames != null && fieldNames.length > 0) {
//                s = fieldNames[i];
//            } else {
//                s = values[i].toString();
//            }
//            radioButton = new RadioButton(values[i] instanceof String ? (String) values[i] : values[i].toString());
//</editor-fold>

            if (multipleSelectionAllowed) {
                buttonsArray[i] = new CheckBox(this.names[i]);
                buttonsArray[i].setUIID("RadioButton");
            } else {
                if (buttonGroup == null) {
                    buttonGroup = new ButtonGroup();
                }
                radioButton = new RadioButton(this.names[i]);
//                radioButton.setToggle(true); //allow to de-select a selected button
                radioButton.setUnselectAllowed(unselectAllowed); //allow to de-select a selected button
//                radioButton.addActionListener(buttonListener); //allow to de-select a selected button
                buttonGroup.add(radioButton);
                buttonsArray[i] = radioButton;
            }
            this.add(buttonsArray[i]);
            buttonsArray[i].setToggle(true); //allow to de-select a selected button
            buttonsArray[i].addActionListener(buttonListener); //allow to de-select a selected button
//<editor-fold defaultstate="collapsed" desc="comment">
//                radioButtonArray[i] = radioButton;
//            if (selectedString != null && values[i].equals(selectedString)) {
//            if (selectedString != null && (values[i] instanceof String?(String)values[i]:values[i].toString()).equals(selectedString)) {
//            if (selectedString != null && values[i].toString().equals(selectedString)) {
//            if (selectedString != null && fieldNames[i].equals(selectedString)) {
//                radioButton.setSelected(true);
//            }
//            select(selectedString);
//            select(selectedIndex);
//</editor-fold>
        }
    }

    MyComponentGroup(Object[] valueArray, String[] names, int selectedIndex, boolean unselectAllowed, boolean verticalLayout) {
        this(valueArray, names, unselectAllowed, verticalLayout);
        selectIndex(selectedIndex);
    }

    MyComponentGroup(Object[] valueArray, String[] names, Vector initiallySelected, boolean unselectAllowed, boolean verticalLayout) {
        this(valueArray, names, unselectAllowed, verticalLayout);
        select(initiallySelected);
    }

    MyComponentGroup(Object[] valueArray, String[] names, Vector initiallySelected, boolean unselectAllowed, boolean verticalLayout, boolean multipleSelectionAllowed) {
        this(valueArray, names, unselectAllowed, verticalLayout, multipleSelectionAllowed, false);
        select(initiallySelected);
    }

    MyComponentGroup(Object[] valueArray, String[] names, Vector initiallySelected, boolean unselectAllowed, boolean verticalLayout,
            ComponentGroup[] compGroupRows, int[] nbButtonsInEachRow) {
        this(valueArray, names, unselectAllowed, verticalLayout, true, false);
        select(initiallySelected);
        setupLayout(values.length, compGroupRows, nbButtonsInEachRow);
    }

    MyComponentGroup(Object[] valueArray, String[] names, Vector initiallySelected, boolean unselectAllowed, boolean verticalLayout, boolean multipleSelectionAllowed,
            ComponentGroup[] compGroupRows, int[] nbButtonsInEachRow) {
        this(valueArray, names, unselectAllowed, verticalLayout, multipleSelectionAllowed, false);
        select(initiallySelected);
        setupLayout(values.length, compGroupRows, nbButtonsInEachRow);
    }

    MyComponentGroup(Object[] valueArray, String[] names, Vector initiallySelected, boolean unselectAllowed, boolean verticalLayout,
            ComponentGroup[] compGroupRows, int[] nbButtonsInEachRow, boolean sameWidth) {
        this(valueArray, names, unselectAllowed, verticalLayout, true, false);
        select(initiallySelected);
        setupLayout(values.length, compGroupRows, nbButtonsInEachRow, sameWidth);
    }
//    MyComponentGroup( Object[] valueArray, String[] names, Vector initiallySelected, boolean unselectAllowed, boolean verticalLayout,
//            ComponentGroup[] compGroupRows, int[] nbButtonsInEachRow) {
//        this(valueArray, names, initiallySelected, unselectAllowed, verticalLayout);
//        setupLayout(values.length, compGroupRows, nbButtonsInEachRow);
//    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    MyComponentGroup(Object[] valueArray, String[] names, String selectedString, boolean unselectAllowed, boolean verticalLayout, boolean xxx) {
//        super();
//        this.values = valueArray;
//        if (names != null) {
//            this.fieldNames = names;
//        } else {
//            this.fieldNames = new String[values.length];
//            for (int i = 0, size = values.length; i < size; i++) {
//                fieldNames[i] = values[i].toString();
//            }
//        }
//
//        this.setHorizontal(!verticalLayout);
//        buttonGroup = new ButtonGroup();
//        ActionListener buttonListener = (e) -> {
//            if (dispatcher != null) {
//                dispatcher.fireActionEvent(e);
//            }
//        };
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        ButtonGroup buttonGroup = new ButtonGroup() {
////            @Override
////            public void setSelected(RadioButton rb) {
////                //if radionbutton is pressed when it is already selected then unselect (clearSelection)
////                if (isSelected() && rb.isSelected()) {
////                    clearSelection();
////                } else {
////                    super.setSelected(rb); //else handle it normally
////                }
////            }
////        };
////</editor-fold>
//        RadioButton radioButton;
////            RadioButton[] radioButtonArray = new RadioButton[values.length];
//        for (int i = 0; i < values.length; i++) {
////            radioButton = new RadioButton(values[i]);
////            radioButton = new RadioButton(values[i] instanceof String?(String)values[i]:values[i].toString());
//            String s;
//            if (fieldNames != null && fieldNames.length > 0) {
//                s = fieldNames[i];
//            } else {
//                s = values[i].toString();
//            }
////            radioButton = new RadioButton(values[i] instanceof String ? (String) values[i] : values[i].toString());
//            radioButton = new RadioButton(fieldNames[i]);
//            radioButton.setToggle(true); //allow to de-select a selected button
//            radioButton.setUnselectAllowed(unselectAllowed); //allow to de-select a selected button
//            radioButton.addActionListener(buttonListener); //allow to de-select a selected button
//            buttonGroup.add(radioButton);
//            this.add(radioButton);
////<editor-fold defaultstate="collapsed" desc="comment">
////                radioButtonArray[i] = radioButton;
////            if (selectedString != null && values[i].equals(selectedString)) {
////            if (selectedString != null && (values[i] instanceof String?(String)values[i]:values[i].toString()).equals(selectedString)) {
////</editor-fold>
////            if (selectedString != null && values[i].toString().equals(selectedString)) {
////            if (selectedString != null && fieldNames[i].equals(selectedString)) {
////                radioButton.setSelected(true);
////            }
//            select(selectedString);
//        }
//    }
//</editor-fold>
//    MyComponentGroup(Object[] valueArray, String[] names, int selectedIdx, boolean unselectAllowed, boolean verticalLayout) {
////        this(valueArray, names, names[selectedIdx], unselectAllowed, verticalLayout);
//        this(valueArray, names, selectedIdx, unselectAllowed, verticalLayout);
//    }
    MyComponentGroup(Object[] valueArray, String[] names, Object selectedValue) {
//        this(valueArray, names, names[selectedIdx], true, false);
        this(valueArray, names, getIndexOfValue(valueArray, selectedValue), false, false);
    }

    MyComponentGroup(Object[] valueArray, String[] names) {
//        this(valueArray, names, names[selectedIdx], true, false);
        this(valueArray, names, -1, true, false);
    }

    MyComponentGroup(Object[] values, String[] names, Object selectedValue, boolean unselectAllowed) {
        this(values, names, getIndexOfValue(values, selectedValue), unselectAllowed, false);
    }

    MyComponentGroup(Object[] values, String[] names, boolean unselectAllowed) {
        this(values, names, -1, unselectAllowed, false);
    }

    MyComponentGroup(Object[] values, String[] names, Object selectedValue, boolean unselectAllowed, boolean multipleSelectionAllowed) {
        this(values, names, unselectAllowed, false, multipleSelectionAllowed);
        selectIndex(getIndexOfValue(values, selectedValue));
    }

    MyComponentGroup(Object[] values, String selectedString, boolean unselectAllowed, boolean verticalLayout) {
        this(values, null, getIndexOfValue(values, selectedString), unselectAllowed, verticalLayout);
    }

    MyComponentGroup(Object[] values, ParseIdMap2 parseIdMap, MyForm.GetString get, MyForm.PutString set, boolean unselectAllowed, boolean verticalLayout) {
//        this(values, get.get(), unselectAllowed);
        this(values, get.get(), unselectAllowed, verticalLayout);
        parseIdMap.put(this, () -> {
            int size = this.getComponentCount();
            for (int i = 0; i < size; i++) {
                if (((Button) this.getComponentAt(i)).isSelected()) {
                    set.accept(((Button) this.getComponentAt(i)).getText()); //store the index of the selected string
                    return;
                }
            }
            set.accept("");
        });
    }

    MyComponentGroup(Object[] values, String selectedString, boolean unselectAllowed) {
        this(values, selectedString, unselectAllowed, false);
    }

    MyComponentGroup(Object[] values, boolean unselectAllowed) {
        this(values, "", unselectAllowed);
    }

    MyComponentGroup(String[] values, int selectedStringIndex, boolean unselectAllowed) {
        this(values, values[selectedStringIndex], unselectAllowed);
    }

    MyComponentGroup(Object[] values, ParseIdMap2 parseIdMap, MyForm.GetString get, MyForm.PutString set, boolean unselectAllowed) {
        this(values, parseIdMap, get, set, unselectAllowed, false);
    }

    MyComponentGroup(Object[] values, ParseIdMap2 parseIdMap, MyForm.GetString get, MyForm.PutString set) {
        this(values, parseIdMap, get, set, true);
    }

    /**
     *
     * @param selectedIndex an illegal value, e.g. -1, will lead to all being
     * unselected
     */
    public void selectOLD(int selectedIndex) {
        int size = this.getComponentCount();
//        if (selectedIndex < 0 || selectedIndex >= size) {
//            return;
//        }
        int nbSelected = getSelectedValues().size();
        int oldIndex = getSelectedIndex();
        for (int i = 0; i < size; i++) {
//            ((RadioButton) this.getComponentAt(i)).setSelected(i == selectedIndex);
            if (i == selectedIndex) {
                if (this.getComponentAt(i) instanceof RadioButton) {
                    ((RadioButton) this.getComponentAt(i)).setSelected(true);
                } else if (this.getComponentAt(i) instanceof CheckBox) {
                    ((CheckBox) this.getComponentAt(i)).setSelected(true);
                }
                if (selectionListener != null) {
                    selectionListener.fireSelectionEvent(oldIndex, selectedIndex);
                }
            } else {
//                ((Button) this.getComponentAt(i)).setSelected(false);
                if (this.getComponentAt(i) instanceof RadioButton) {
                    ((RadioButton) this.getComponentAt(i)).setSelected(false);
                } else if (this.getComponentAt(i) instanceof CheckBox) {
                    ((CheckBox) this.getComponentAt(i)).setSelected(false);
                }
            }
        }
    }

    public void selectIndex(int selectedIndex) {
//        int size = this.getComponentCount();
        int size = buttonsArray.length;
//        if (selectedIndex < 0 || selectedIndex >= size) {
//            return;
//        }
//        int nbSelected = getSelectedValues().size();
        int oldIndex = getSelectedIndex();
        for (int i = 0; i < size; i++) {
//            ((RadioButton) this.getComponentAt(i)).setSelected(i == selectedIndex);
            if (i == selectedIndex) {
                if (buttonsArray[i] instanceof RadioButton) {
                    ((RadioButton) buttonsArray[i]).setSelected(true);
                } else if (buttonsArray[i] instanceof CheckBox) {
                    ((CheckBox) buttonsArray[i]).setSelected(true);
                }
                if (selectionListener != null) {
                    selectionListener.fireSelectionEvent(oldIndex, selectedIndex);
                }
            } else {
//                ((Button) this.getComponentAt(i)).setSelected(false);
                if (buttonsArray[i] instanceof RadioButton) {
                    ((RadioButton) buttonsArray[i]).setSelected(false);
                } else if (buttonsArray[i] instanceof CheckBox) {
                    ((CheckBox) buttonsArray[i]).setSelected(false);
                }
            }
        }
    }

    /**
     * any previous selected value will become unselected, an illegal string,
     * not in the list, will lead to all being deselected
     *
     * @param selectedString
     */
    public void select(String selectedString) {
        if (selectedString == null || selectedString.isEmpty()) {
            return;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            for (int i = 0; i < values.length; i++) {
////            radioButton = new RadioButton(values[i]);
////            if (selectedString != null && values[i].toString().equals(selectedString)) {
////                buttonGroup.getRadioButton(i).setSelected(true);
////            }
////            buttonGroup.getRadioButton(i).setSelected(selectedString != null && values[i].toString().equals(selectedString));
////            buttonGroup.getRadioButton(i).setSelected(selectedString != null && fieldNames[i].equals(selectedString));
//                if (names[i].equals(selectedString)) {
//                    select(i);
//                }
//            }
//        }
//</editor-fold>
        selectIndex(getIndexOfName(selectedString));
    }

    /**
     *
     * @param value the value to select, if not matching any defined values, no
     * effect unselected
     */
    public void select(Object value) {
        if (value == null) {
            return;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            for (int i = 0; i < values.length; i++) {
////            if (value.equals(values[i])) {
////                buttonGroup.getRadioButton(i).setSelected(true);
////            }
//                if (value.equals(values[i])) {
//                    select(i);
//                }
//            }
//        }
//</editor-fold>
        selectIndex(getIndexOfValue(value));
    }

    public void select(Vector initiallySelectedValues) {
        setSelected(initiallySelectedValues);
    }

    public void setSelected(Vector initiallySelectedValues) {
        for (Object o : initiallySelectedValues) {
            select(o);
        }
    }

    int getSelectedCount() {
        int count = 0;
        for (int i = 0, size = buttonsArray.length; i < size; i++) {
            if (isSelected(i)) {
                count++;
            }
        }
        return count;

    }

    /**
     * returns selected index, or -1 if none
     *
     * @return
     */
    public int getSelectedIndex() {
//            int size = this.getComponentCount();
        int size = buttonsArray.length;
        for (int i = 0; i < size; i++) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (this.getComponentAt(i) instanceof RadioButton) {
//                if (((RadioButton) this.getComponentAt(i)).isSelected()) {
//                    return i;
//                } else if (this.getComponentAt(i) instanceof CheckBox) {
//                    if (((CheckBox) this.getComponentAt(i)).isSelected()) {
//                        return i;
//                    }
//                }
//</editor-fold>
//            if (((Button) this.getComponentAt(i)).isSelected()) {
            if (buttonsArray[i].isSelected()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * returns selected String, or null if none
     *
     * @return
     */
    public String getSelectedString() {
        int selected = getSelectedIndex();
//<editor-fold defaultstate="collapsed" desc="comment">
//        int size = this.getComponentCount();
//        if (selected >= 0 && selected < size) {
//            return ((RadioButton) this.getComponentAt(selected)).getText();
//        }
//        return null;
//</editor-fold>
        if (selected >= 0 && selected < names.length) {
            return names[selected];
        } else {
            return null;
        }
    }

    /**
     * returns selected value as an Object, or null if none
     *
     * @return
     */
    public Object getSelected() {
        int selected = getSelectedIndex();
        if (selected >= 0 && selected < values.length) {
            return values[selected];
        } else {
            return null;
        }
    }

    public int getSelectedValue() {
        int selected = getSelectedIndex();
        if (selected >= 0 && selected < values.length) {
            return (int) values[selected];
        } else {
            return -1;
        }
    }

    /**
     * returns selected value as an int, or -1
     *
     * @return
     */
    public int getSelectedInt() {
        return (int) getSelectedValue();
    }

    private EventDispatcher dispatcher = null; //new EventDispatcher();

    /**
     * Adds a listener to the button which will cause an event to dispatch on
     * click
     *
     * @param l implementation of the action listener interface
     */
    public void addActionListener(ActionListener l) {
        if (dispatcher == null) {
            dispatcher = new EventDispatcher();
        }
        dispatcher.addListener(l);
    }

    /**
     * Removes the given action listener from the button
     *
     * @param l implementation of the action listener interface
     */
    public void removeActionListener(ActionListener l) {
        if (dispatcher == null) {
            dispatcher.removeListener(l);
        }
    }

    private EventDispatcher selectionListener = null; //new EventDispatcher();

    /**
     * @inheritDoc
     */
    public void addSelectionListener(SelectionListener l) {
        if (selectionListener == null) {
            selectionListener = new EventDispatcher();
        }
        selectionListener.addListener(l);
    }

    /**
     * @inheritDoc
     */
    public void removeSelectionListener(SelectionListener l) {
        if (selectionListener != null) {
            selectionListener.removeListener(l);
        }
    }

    private Container buttonsContainer;

    /**
     * sets the buttonsContainer that will receive the buttons and determine the
     * graphical layout
     */
    public void setContainer(Container buttonsContainer) {
        if (this.buttonsContainer != null) {
            this.buttonsContainer.removeAll(); //remove buttons from old container
        }
        this.buttonsContainer = buttonsContainer;
//        setupLayout(); //setup with new layout
    }

    /**
     * make and format an appropriate ComponentGroup
     *
     * @return
     */
    private ComponentGroup makeComponentGroup() {
        ComponentGroup buttonsContainerGroup = new ComponentGroup();
//        buttonsContainerGroup.setLayout(new FlowLayout(Component.CENTER));
//        buttonsContainerGroup.setElementUIID("ToggleButton");
        buttonsContainerGroup.setHorizontal(true);
//        buttonsContainerGroup.setSameWidth();
        return buttonsContainerGroup;
    }

    /**
     *
     * @param numberButtons total number of buttons
     * @param compGrouping indicates the number of rows of buttons and number of
     * radiobuttons in each row, like this: compGrouping[nbRows][nbButtonsInRow]
     */
    public void setupLayout(int numberButtons, ComponentGroup[] compGroupRows, int[] nbButtonsInEachRow) {
        setupLayout(numberButtons, compGroupRows, nbButtonsInEachRow, false);
    }

    public void setupLayout(int numberButtons, ComponentGroup[] compGroupRows, int[] nbButtonsInEachRow, boolean allButtonsSameWidth) {
//        if (compGroupRows != null) {
        if (nbButtonsInEachRow != null) {
            removeAll();
            setHorizontal(false);
            Container compGroupCont = new Container(BoxLayout.y());
//            ComponentGroup cGroup = makeComponentGroup();
            int compGroupIndex = 0;
//            int compIndex = 0;
            for (int i = 0, size = this.values.length; i < size; i++) {
                if (compGroupRows[compGroupIndex] == null) {
//                    compGroupRows[compGroupIndex] = makeComponentGroup();
                    compGroupRows[compGroupIndex] = new ComponentGroup();
                    addComponent(compGroupRows[compGroupIndex]);
                    compGroupRows[compGroupIndex].setHorizontal(true);
//                    compGroupRows[compGroupIndex].setUIID("Container");
                }
                if (false && compGroupRows[compGroupIndex].getComponentCount() == 0) {
                    compGroupCont.add(compGroupRows[compGroupIndex]); //add new CG
                }
                compGroupRows[compGroupIndex].addComponent(buttonsArray[i]);
                if (compGroupRows[compGroupIndex].getComponentCount() == nbButtonsInEachRow[compGroupIndex]) { //if this row is filled up
                    if (allButtonsSameWidth) { //activate same width before moving on to next group
                        compGroupCont.setSameWidth(compGroupRows[compGroupIndex]);
                    }
                    compGroupIndex++; //go to next row
//                    cGroup = makeComponentGroup();
                }
            }
            for (int i = 0, size = compGroupRows.length; i < size; i++) {
                compGroupRows[i].setUIID("Container"); //reset all UIIDs to Container (each adding to ComponentGroup updates them all)
            }
            if (false && allButtonsSameWidth) {
                compGroupCont.setSameWidth(compGroupRows[compGroupIndex]);
            }
            if (false) {
                addComponent(compGroupCont); //add buttons
            }//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setupLayout(int numberButtons, int[][] compGrouping) {
//        if (compGrouping != null) {
//            removeAll();
//            Container compGroupCont = new Container(BoxLayout.y());
//            ComponentGroup cGroup = makeComponentGroup();
//            int compGroupIndex = 0;
//            int compIndex = 0;
//            for (int i = 0, size = this.values.length; i < size; i++) {
//                cGroup.addComponent(buttonsArray[i]);
//                if (cGroup.getComponentCount() == compGrouping[compGroupIndex].length) { //if this row is filled up
//                    compGroupCont.add(cGroup);
//                    compGroupIndex++;
//                    cGroup = makeComponentGroup();
//                }
//            }
//            addComponent(compGroupCont); //add buttons
//</editor-fold>
        } else {
            if (buttonsContainer == null) {
                ComponentGroup buttonsContainerGroup = new ComponentGroup();
                buttonsContainerGroup.setLayout(new FlowLayout(Component.CENTER));
                if (false) {
                    buttonsContainerGroup.setElementUIID("ToggleButton");
                }
                buttonsContainerGroup.setHorizontal(true);
                buttonsContainer = buttonsContainerGroup;
            }
//        else {
//            buttonsContainer.removeAll(); //remove all buttons from previous container
//        }
            for (int i = 0, size = this.values.length; i < size; i++) {
                buttonsContainer.addComponent(buttonsArray[i]);
            }
            removeAll();
            addComponent(buttonsContainer); //add buttons
        }
    }

    /**
     * is RadioButton or CheckBox at index selected?
     */
    boolean isSelected(int index) {
//        if (buttonsArray[i] instanceof RadioButton)
//            return ((RadioButton)buttonsArray[i]).isSelected();
//        else 
//            return ((CheckBox)buttonsArray[i]).isSelected();
        return buttonsArray[index].isSelected();
    }

    /**
     * returns the selected logical value. NO: If no values are defined, then
     * simply returns the selected index.
     *
     * @return
     */
    public Vector getSelectedValues() {
        Vector selectedValues = new Vector();
        for (int i = 0, size = buttonsArray.length; i < size; i++) {
//        for (int i = 0, size = size(); i < size; i++) {
//            if (buttonsArray[i].isSelected()) {
            if (isSelected(i)) {
//                selectedValues.addElement(new Integer(values[i]));
//                selectedValues.addElement(new Integer(values[i]));
                selectedValues.addElement(values[i]);
            }
        }
        return selectedValues;
    }

////<editor-fold defaultstate="collapsed" desc="comment">
//    MyComponentGroup(boolean NOT_USED, String[] values, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set, boolean unselectAllowed) {
//        this(values, values[get.get()], unselectAllowed);
//        parseIdMap.put(this, () -> {
//            int size = this.getComponentCount();
//            for (int i = 0; i < size; i++) {
//                if (((RadioButton) this.getComponentAt(i)).isSelected()) {
//                    set.accept(i); //store the index of the selected string
//                    return;
//                }
//            }
//            //if nothing was selected (possible??), set String to empty
//            set.accept(-1); //TODO!!!! ????
////                    parseObject.remove(parseId); //if nothing's selected, remove the field
//        });
//    }
//
//    MyComponentGroup(int OLD, String[] values, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set, boolean unselectAllowed) {
//        super();
//        this.setHorizontal(true);
//        ButtonGroup buttonGroup = new ButtonGroup() {
//            public void setSelected(RadioButton rb) {
//                //if radionbutton is pressed when it is already selected then unselect (clearSelection)
//                if (rb.isSelected()) {
//                    clearSelection();
//                } else {
//                    super.setSelected(rb); //else handle it normally
//                }
//            }
//        };
////            String selected = parseObject.getString(parseId);
////                String selectedString = parseObject.getString(parseId);
//        String selectedString = values[get.get()];
//        RadioButton radioButton;
////            RadioButton[] radioButtonArray = new RadioButton[values.length];
//        for (int i = 0; i < values.length; i++) {
//            radioButton = new RadioButton(values[i]);
//            radioButton.setToggle(true); //allow to de-select a selected button
//            radioButton.setUnselectAllowed(unselectAllowed); //allow to de-select a selected button
//            buttonGroup.add(radioButton);
//            this.add(radioButton);
////                radioButtonArray[i] = radioButton;
//            if (selectedString != null && values[i].equals(selectedString)) {
//                radioButton.setSelected(true);
//            }
//        }
////            this.encloseHorizontal(radioButtonArray);
//        parseIdMap.put(this, () -> {
//            int size = this.getComponentCount();
//            for (int i = 0; i < size; i++) {
//                if (((RadioButton) this.getComponentAt(i)).isSelected()) {
////                        parseObject.put(parseId, (((RadioButton) this.getComponentAt(i)).getText())); //store the selected string
////                            parseObject.put(parseId, i); //store the index of the selected string
//                    set.accept(i); //store the index of the selected string
//                    return;
//                }
//            }
//            //if nothing was selected (possible??), set String to empty
////                    parseObject.remove(parseId); //if nothing's selected, remove the field
//        });
//    }
//
////    MyComponentGroup(String[] values, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set) {
////        this(values, parseIdMap, get, set, true);
////    }
//    MyComponentGroup(boolean OLD, String[] values, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString get, MyForm.PutString set, boolean unselectAllowed) {
//        super();
//        this.setHorizontal(true);
//        ButtonGroup buttonGroup = new ButtonGroup();
////<editor-fold defaultstate="collapsed" desc="comment">
////            {
////                public void clearSelection() {
////        if(selectedIndex!=-1) {
////            if(selectedIndex < buttons.size()) {
////                ((RadioButton)buttons.elementAt(selectedIndex)).setSelected(false);
////            }
////            selectedIndex=-1;
////        }
////
////    }
////};
////            {
////                public void setSelected(RadioButton rb) {
////                    //if radionbutton is pressed when it is already selected then unselect (clearSelection)
////                    if (rb.isSelected()) {
////                        clearSelection();
////                    } else {
////                        super.setSelected(rb); //else handle it normally
////                    }
////                }
////            };
////            String selected = parseObject.getString(parseId);
////                String selectedString = parseObject.getString(parseId);
////            String selectedString = values[get.get()];
////</editor-fold>
//        String selectedString = get.get();
//        RadioButton radioButton;
////            RadioButton[] radioButtonArray = new RadioButton[values.length];
//        for (int i = 0; i < values.length; i++) {
//            radioButton = new RadioButton(values[i]);
////<editor-fold defaultstate="collapsed" desc="comment">
////                {
////                    @Override
////                    public void released(int x, int y) {
////                        // prevent the radio button from being "turned off"
//////        if(!isSelected()) {
//////            setSelected(true);
//////        }
////                        setSelected(!isSelected());
////                        super.released(x, y);
//////                        Button.this.released(x, y);
//////                        super.repaint();
//////                                super.fireActionEvent(x, y);
////
////                    }
////
////                    @Override
////                    public void setSelected(boolean selected) {
////                        if (selected != isSelected()) { //
////                            if (!selected && isSelected()) { //unselect
////                                super.setSelected(false); //need to unselect before calling clearSelection
////                                buttonGroup.clearSelection(); //clearSelections will also unselect the button so no need to call super.setSelected(false);
////                            } //else {
////                            super.setSelected(selected);
//////                        }
////                        }
////                    }
////                };
////</editor-fold>
//            radioButton.setToggle(true); //allow to de-select a selected button
//            radioButton.setUnselectAllowed(unselectAllowed); //allow to de-select a selected button
//            buttonGroup.add(radioButton);
//            this.add(radioButton);
////                radioButtonArray[i] = radioButton;
//            if (selectedString != null && values[i].equals(selectedString)) {
//                radioButton.setSelected(true);
//            }
//        }
////            this.encloseHorizontal(radioButtonArray);
//        parseIdMap.put(this, () -> {
//            int size = this.getComponentCount();
//            for (int i = 0; i < size; i++) {
//                if (((RadioButton) this.getComponentAt(i)).isSelected()) {
////                        parseObject.put(parseId, (((RadioButton) this.getComponentAt(i)).getText())); //store the selected string
////                            parseObject.put(parseId, i); //store the index of the selected string
////                        set.accept(i); //store the index of the selected string
//                    set.accept(((RadioButton) this.getComponentAt(i)).getText()); //store the index of the selected string
//                    return;
//                }
//            }
//            //if nothing was selected (possible??), set String to empty
//            set.accept("");
//        });
//    }
//</editor-fold>
}
