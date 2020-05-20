/*
 * see: http://codenameone.blogspot.com/2011/07/toggle-buttons-grouping-them-together.html
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.FocusListener;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.list.DefaultListCellRenderer;
import com.codename1.ui.list.ListCellRenderer;
import com.codename1.ui.list.ListModel;
import java.util.Vector;

/**
 * a toggle button that will choose one of several values. Like
 * ComboBoxLogicalMultiSelection it can be initialized with a set of values and
 * corresponding strings (int[] values, String[] names)
 *
 * @author Thomas
 */
public class MyToggleButtonComponentGroupXXX extends List {

    int[] values;
    String[] names;
//    Object[] names;
//    RadioButton[] buttonsArray;
    int selectedValue;
    boolean onlySingleSelectionAllowed;
    Vector initiallySelectedValues;
    ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            Object source = evt.getSource();
//            for (int i = 0, size = buttonsArray.length; i < size; i++) {
            for (int i = 0, size = size(); i < size; i++) {
                if (getModel().getItemAt(i) == source) {
//                        selectedValue = values[i];
//                        selectedValue = valuesFinal[i];
//                        selectedValue = MyToggleButton.this.values[i];
                    flipSelectedIndex(i); //toggle
                }
            }
        }
    };

    /**
     *
     * @param values the array of values
     * @param names the array of strings that are displayed for each of the
     * values
     * @param valueIndex preselected value (one of the values in the array of
     * values) - makes it easy to select the right value
     */
//    MyToggleButton(String[] names, int[] values, String initiallySelectedString) {
//        this(names, values, names.);
//    }
    MyToggleButtonComponentGroupXXX(String[] names, int[] values, int initiallySelectedValue) {
        ASSERT.that((values == null && names != null && names.length > 0) || (values != null && names != null && values.length == names.length && values.length > 0), "Both values and names arrays must have same length, and be non-empty");

        this.names = names;
        this.values = values;
        if (values == null) {
            this.values = new int[names.length];
            for (int i = 0, size = names.length; i < size; i++) {
                this.values[i] = i;
            }
        }
        this.initiallySelectedValues = new Vector(1);
        this.initiallySelectedValues.addElement(new Integer(initiallySelectedValue));
        setup();
    }

    /**
     *
     * @param names array of 'logical' names for each value (the strings to
     * display for the enduser)
     * @param values array of the the int values associated with each logical
     * name (can be in any order)
     * @param initiallySelectedValues a vector of Integer values that are
     * selected by default. Values that are not in the values array are ignored
     * w/o warning.
     */
    MyToggleButtonComponentGroupXXX(String[] names, int[] values, Vector initiallySelectedValues) {
        super();

        ASSERT.that((values == null && names != null && names.length > 0) || (values != null && names != null && values.length == names.length && values.length > 0), "Both values and names arrays must have same length, and be non-empty");

        this.names = names;
        this.values = values;
        if (values == null) {
            this.values = new int[names.length];
            for (int i = 0, size = names.length; i < size; i++) {
                this.values[i] = i;
            }
        }
        this.initiallySelectedValues = initiallySelectedValues;
        setup();
    }

    /**
     *
     * @param values
     * @param valueIndex index of value in values
     * @param names
     */
    MyToggleButtonComponentGroupXXX(final int[] values, int valueIndex, String[] names) {
        this(names, values, values[valueIndex]);
//        int valueIndex=0; //0 is default choice if value is not found in values
//        for (int i = 0, size = Math.min(values.length, names.length); i < size; i++) {
//            if (value==values[i]) valueIndex=i;
//        }
//        this(values, names, valueIndex);
    }

    MyToggleButtonComponentGroupXXX(String[] names, int[] values) {
        this(names, values, -1);
    }

    private void updateUIID(String newUIID, Component c) {
        Object o = c.getClientProperty("$origUIID");
        if(o == null) {
            c.putClientProperty("$origUIID", c.getUIID());
        }
        c.setUIID(newUIID);
    }
    
    private void setup() {
//        buttonsArray = new RadioButton[this.values.length];
        for (int i = 0, size = this.values.length; i < size; i++) {
//            buttonsArray[i] = new RadioButton(names[i]);
//            buttonsArray[i] = new RadioButton(names[i]);
            addItem(new RadioButton(names[i]));
        }
        for (int i = 0, size = initiallySelectedValues.size(); i < size; i++) {
            setSelectedValue(((Integer) initiallySelectedValues.elementAt(i)).intValue());
        }

//        setupLayout();
    }

    void addLayoutToContainer(Container result) {
        //create the logical button group
        ButtonGroup bg = new ButtonGroup();
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
        for (int i = 0, size = size(); i < size; i++) {
//            bg.add(buttonsArray[i]);
            RadioButton radioButton = ((RadioButton) getModel().getItemAt(i));
            bg.add(radioButton);
            radioButton.setToggle(true);
//            buttons.addComponent(buttonsArray[i]);
//            buttonsArray[i].addActionListener(listener);
            radioButton.addActionListener(listener);
        }

        ComponentGroup buttons = new ComponentGroup();
        buttons.setElementUIID("ToggleButton");
        buttons.setHorizontal(true);

        //create grouping for display
//        Container centerFlow = new Container(new FlowLayout(Component.CENTER));
        result = new Container(new FlowLayout(Component.CENTER));
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
        for (int i = 0, size = size(); i < size; i++) {
            buttons.addComponent(((RadioButton) getModel().getItemAt(i)));
            if (false) { //rather just let the labels flow around if too wide for the screen
                if (buttons.getPreferredW() >= Display.getInstance().getDisplayWidth() / 2 && i < size - 1) { //break into multiple lines (but only if there's at least one more button to add)
                    result.addComponent(buttons);
                    buttons = new ComponentGroup();
                    buttons.setElementUIID("ToggleButton");
                    buttons.setHorizontal(true);
                }
            }
        }
        result.addComponent(buttons); //add last buttons
//        addComponent(centerFlow); //add the buttons to this container
    }

    /**
     * is RadioButton at index selected?
     */
    boolean isSelected(int index) {
        return ((RadioButton) getModel().getItemAt(index)).isSelected();
    }

//    public int getSelectedIndex() {
//        return ((RadioButton) getModel().getItemAt(index)).isSelected();
//    }

    public void setSelectedIndexState(int index, boolean set) {
//        buttonsArray[index].setSelected(set);
        ((RadioButton) getModel().getItemAt(index)).setSelected(set);
        super.setSelectedIndex(index); //trigger selection listener
    }

    public void setSelectedIndex(int index) {
        setSelectedIndexState(index, true);
    }

    public void flipSelectedIndex(int index) {
//        buttonsArray[index].setSelected(!buttonsArray[index].isSelected());
        setSelectedIndexState(index, !isSelected(index)); //flip selected state
    }

    /**
     * returns the selected logical value. NO: If no values are defined, then
     * simply returns the selected index.
     *
     * @return
     */
    public Vector getSelectedValues() {
        Vector selectedValues = new Vector();
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
        for (int i = 0, size = size(); i < size; i++) {
//            if (buttonsArray[i].isSelected()) {
            if (isSelected(i)) {
//                selectedValues.addElement(new Integer(values[i]));
                selectedValues.addElement(new Integer(values[i]));
            }
        }
        return selectedValues;
    }

    /**
     * returns vector with all selected names
     */
    public Vector getSelectedNames() {
        Vector selectedNames = new Vector();
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
        for (int i = 0, size = size(); i < size; i++) {
//            if (buttonsArray[i].isSelected()) {
            if (isSelected(i)) {
//                selectedValues.addElement(new Integer(values[i]));
                selectedNames.addElement(names[i]);
            }
        }
        return selectedNames;
    }

    /**
     * returns the index of the *first* selected element (in case there are
     * several selected)
     *
     * @return -1 if no element were selected. NO: returns 0 so first element is
     * selected by default??
     */
    public int getSelectedIndex() {
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
        for (int i = 0, size = size(); i < size; i++) {
            if (isSelected(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * returns the selected logical value. NO: If no values are defined, then
     * simply returns the selected index.
     *
     * @return
     */
    public int getSelectedValue() {
        return values[getSelectedIndex()];
    }

    public String getSelectedName() {
        return names[getSelectedIndex()];
    }

    /**
     * selects the given name. If name does not exist in the names array, then
     * nothing is done
     *
     */
    public void setSelectedName(String name) {
//        int index = 0;
//        for (int i = 0, size = names.length; i < size; i++) {
        for (int i = 0, size = names.length; i < size; i++) {
            if (names[i].equals(name)) {
//                index = i;
                setSelectedIndexState(i, true);
            }
        }
//        buttonsArray[index].setSelected(true);
//        buttonsArray[index].setSelected(true);
    }

    /**
     * if a set of values is defined, then select the corresponding ComboBox
     * line. NO: If not values are set, select the logicalValue index.
     *
     * @param logicalValue
     */
    public void setSelectedValue(int logicalValue) {
//        if (values != null) {
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
//            if (values[i] == logicalValue) {
//                buttonsArray[i].setSelected(true);
//                return;
//            }
        for (int i = 0, size = values.length; i < size; i++) {
            if (values[i] == logicalValue) {
                setSelectedIndexState(i, true);
//                buttonsArray[i].setSelected(true);
                return;
            }
//            else {
//                buttonsArray[i].setSelected(false); //make sure all other buttons are unselected //-NOT necdessary since RadioGroup
//            }
        }
//#mdebug
        ASSERT.that("ERROR in ComboBoxLogicalNames: illegal logicalValue=" + logicalValue + " not in list of values=" + values);
//#enddebug
    }
}
