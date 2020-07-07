/*
 * see: http://codenameone.blogspot.com/2011/07/toggle-buttons-grouping-them-together.html
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.EventDispatcher;
import java.util.Vector;

/**
 * a toggle button that will choose one of several values. Like
 * ComboBoxLogicalMultiSelection it can be initialized with a set of values and
 * corresponding strings (int[] values, String[] names)
 *
 * @author Thomas
 */
public class MyToggleButton extends Container {

    private int[] values;
    private String[] names;
//    Object[] names;
//    RadioButton[] buttonsArray;
    private Button[] buttonsArray;
//    int selectedValue;
    private boolean onlySingleSelectionAllowed = true; //selecting one automatically unselects previously selected
    private boolean unselectAllowed = false; //OK to unselect the selected, meaning no items are selected
    private Vector initiallySelectedValues;
    private int oldIndex = -1;
    private ComponentGroup buttonsContainer;
    private ActionListener listener = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
//            int oldIndex = getSelectedIndex();
            Object source = evt.getSource();
            for (int i = 0, size = buttonsArray.length; i < size; i++) {
//            for (int i = 0, size = size(); i < size; i++) {
//                if (getModel().getItemAt(i) == source) {
                if (buttonsArray[i] == source) {
//                        selectedValue = values[i];
//                        selectedValue = valuesFinal[i];
//                        selectedValue = MyToggleButton.this.values[i];
//                    flipSelectedIndex(i); //toggle
                    if (selectionListener != null) {
                        selectionListener.fireSelectionEvent(oldIndex, i);
                    }
                    oldIndex = i; //keep track of previous selected index to fire selectionLIstener correctly
                }
            }
        }
    };

    /**
     *
     * @param names array of 'logical' names for each value (the strings to
     * display for the enduser)
     * @param values array of the the int values associated with each logical
     * name (can be in any order)
     * @param initiallySelectedIntegerValues a vector of Integer values that are
     * selected by default. Values that are not in the values array are ignored
     * w/o warning.
     */
    MyToggleButton(String[] names, int[] values, Vector initiallySelectedIntegerValues, int initiallySelectedIntValue, boolean multipleSelectionAllowed) {
        this(names, values, initiallySelectedIntegerValues, initiallySelectedIntValue, multipleSelectionAllowed, null, null);
    }

    private void setupInitiallySelectedValues(Vector initiallySelectedIntegerValues, int initiallySelectedIntValue) {

        if (initiallySelectedIntegerValues == null) {
            this.initiallySelectedValues = new Vector(1);
            if (initiallySelectedIntValue != -1) {
                this.initiallySelectedValues.addElement(new Integer(initiallySelectedIntValue));
            }
        } else {
            this.initiallySelectedValues = initiallySelectedIntegerValues;
        }
    }

    private void setSelected(Vector initiallySelectedValues) {
        for (int i = 0, size = initiallySelectedValues.size(); i < size; i++) {
            setSelectedValue(((Integer) initiallySelectedValues.elementAt(i)).intValue());
        }
    }

    MyToggleButton(String[] names, int[] values, Vector initiallySelectedIntegerValues, int initiallySelectedIntValue,
            boolean multipleSelectionAllowed, ComponentGroup[] compGroupRows, int[] nbButtonsInEachRow) {
        super();
        ASSERT.that((values == null && names != null && names.length > 0) || (values != null && names != null && values.length == names.length && values.length > 0), "Both values and names arrays must have same length, and be non-empty");

        this.names = names;
        this.values = values;
        onlySingleSelectionAllowed = !multipleSelectionAllowed;
        //below code is never used?! (MyToggleButton never called values==null)
        //automatically fill out values with {0, 1, ...}
        if (this.values == null) {
            this.values = new int[names.length];
            for (int i = 0, size = names.length; i < size; i++) {
                this.values[i] = i;
            }
        }

//        if (initiallySelectedIntegerValues == null) {
//            this.initiallySelectedValues = new Vector(1);
//            if (initiallySelectedIntValue != -1) {
//                this.initiallySelectedValues.addElement(new Integer(initiallySelectedIntValue));
//            }
//        } else {
//            this.initiallySelectedValues = initiallySelectedIntegerValues;
//        }
//        setupInitiallySelectedValues( initiallySelectedIntegerValues,  initiallySelectedIntValue);
//        setupXX();
        buttonsArray = new Button[this.values.length];

        //create the logical button group
        ButtonGroup bg = new ButtonGroup();

        for (int i = 0, size = this.values.length; i < size; i++) {
//            buttonsArray[i] = new RadioButton(names[i]);
            if (onlySingleSelectionAllowed) {
                buttonsArray[i] = new RadioButton(names[i]);
                bg.add((RadioButton) buttonsArray[i]); //ButtonGroup's only purpose is to avoid multiple simultanous selections
            } else {
                buttonsArray[i] = new CheckBox(names[i]);
                buttonsArray[i].setUIID("RadioButton");
            }
            buttonsArray[i].setToggle(true);
            buttonsArray[i].addActionListener(listener);
//            buttons.addComponent(buttonsArray[i]);
        }
        //set selected
        if (initiallySelectedIntegerValues == null) {
            initiallySelectedIntegerValues = new Vector(1);
            if (initiallySelectedIntValue != -1) {
                initiallySelectedIntegerValues.addElement(new Integer(initiallySelectedIntValue));
            }
        }
//        for (int i = 0, size = initiallySelectedValues.size(); i < size; i++) {
//            setSelectedValue(((Integer) initiallySelectedValues.elementAt(i)).intValue());
//        }
        setSelected(initiallySelectedIntegerValues);

        setupLayout(this.values.length, compGroupRows, nbButtonsInEachRow);
    }

    MyToggleButton(String[] names, int[] values, Vector initiallySelectedIntegerValues,
            boolean multipleSelectionAllowed, ComponentGroup[] compGroupRows, int[] nbButtonsInEachRow) {
        this(names, values, initiallySelectedIntegerValues, -1, multipleSelectionAllowed, compGroupRows, nbButtonsInEachRow);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    MyToggleButton(String[] names, int[] values, String initiallySelectedString) {
//        this(names, values, names.);
//    }

    /**
     *
     * @param values the array of values
     * @param names the array of strings that are displayed for each of the
     * values
     * @param valueIndex preselected value (one of the values in the array of
     * values) - makes it easy to select the right value
     */
//    MyToggleButton(String[] names, int[] values, int initiallySelectedIntValue, boolean multipleSelectionAllowed) {
//        this(names, values, null, initiallySelectedIntValue, multipleSelectionAllowed);
//    }
//</editor-fold>

    /* used externally */
    MyToggleButton(String[] names, int[] values, int initiallySelectedIntValue) {
        this(names, values, null, initiallySelectedIntValue, false);
//<editor-fold defaultstate="collapsed" desc="comment">
//        super();
//        ASSERT.that((values == null && names != null && names.length > 0) || (values != null && names != null && values.length == names.length && values.length > 0), "Both values and names arrays must have same length, and be non-empty");
//
//        this.names = names;
//        this.values = values;
//        if (values == null) {
//            this.values = new int[names.length];
//            for (int i = 0, size = names.length; i < size; i++) {
//                this.values[i] = i;
//            }
//        }
//        this.initiallySelectedValues = new Vector(1);
//        if (initiallySelectedIntValue != -1) {
//            this.initiallySelectedValues.addElement(new Integer(initiallySelectedIntValue));
//        }
//        setup();
//</editor-fold>
    }

    /*used externally*/
    MyToggleButton(String[] names, int[] values) {
        this(names, values, -1);
    }

//    MyToggleButton(String[] names) {
//        this(names, null, -1);
//    }

    /*used externally*/
    MyToggleButton(String[] names, int[] values, Vector initiallySelectedIntegerValues, boolean multipleSelectionAllowed) {
        this(names, values, initiallySelectedIntegerValues, -1, multipleSelectionAllowed);
    }

////<editor-fold defaultstate="collapsed" desc="comment">
//    MyToggleButton(String[] names, int[] values, Vector initiallySelectedIntegerValues) {
//        this(names, values, initiallySelectedIntegerValues, false);
//    }
    /**
     *
     * @param values
     * @param valueIndex index of value in values
     * @param names
     */
//    MyToggleButton(final int[] values, int valueIndex, String[] names) {
//        this(names, values, null, values[valueIndex], false);
////        int valueIndex=0; //0 is default choice if value is not found in values
////        for (int i = 0, size = Math.min(values.length, names.length); i < size; i++) {
////            if (value==values[i]) valueIndex=i;
////        }
////        this(values, names, valueIndex);
//    }
    /**
     * sets the buttonsContainer that will receive the buttons and determine the
     * graphical layout
     */
//    public void setContainerXXX(Container buttonsContainer) {
//        if (this.buttonsContainer != null) {
//            this.buttonsContainer.removeAll(); //remove buttons from old container
//        }
//        this.buttonsContainer = buttonsContainer;
////        setupLayout(); //setup with new layout
//    }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void setup(int nbRows) {
////        buttonsArray = new RadioButton[this.values.length];
//        buttonsArray = new Button[this.values.length];
//
//        //create the logical button group
//        ButtonGroup[] bgArray = new ButtonGroup[nbRows];
//
//        int bgRowIndex = 0;
//        int shiftRowThreshold = this.values.length / nbRows; //will make first row shorter, e.g. nbRows=7 => threshold=3
//if (bgArray[bgRowIndex]==null) bgArray[bgRowIndex] = new ComponentGroup();
//
//        for (int i = 0, size = this.values.length; i < size; i++) {
////            buttonsArray[i] = new RadioButton(names[i]);
//            if (onlySingleSelectionAllowed) {
//                buttonsArray[i] = new RadioButton(names[i]);
//                bgArray[bgRowIndex].add((RadioButton) buttonsArray[i]); //ButtonGroup's only purpose is to avoid multiple simultanous selections
//            } else {
//                buttonsArray[i] = new CheckBox(names[i]);
//                buttonsArray[i].setUIID("RadioButton");
//            }
//            buttonsArray[i].setToggle(true);
//            buttonsArray[i].addActionListener(listener);
////            buttons.addComponent(buttonsArray[i]);
//            if (i >= shiftRowThreshold) {
//                bgRowIndex++;
//            }
//        }
//        //set selected
//        for (int i = 0, size = initiallySelectedValues.size(); i < size; i++) {
//            setSelectedValue(((Integer) initiallySelectedValues.elementAt(i)).intValue());
//        }
//        setupLayout(this.values.length);
//    }
//</editor-fold>
    private void setupXX() {
//        buttonsArray = new RadioButton[this.values.length];
        buttonsArray = new Button[this.values.length];

        //create the logical button group
        ButtonGroup bg = new ButtonGroup();

        for (int i = 0, size = this.values.length; i < size; i++) {
//            buttonsArray[i] = new RadioButton(names[i]);
            if (onlySingleSelectionAllowed) {
                buttonsArray[i] = new RadioButton(names[i]);
                bg.add((RadioButton) buttonsArray[i]); //ButtonGroup's only purpose is to avoid multiple simultanous selections
            } else {
                buttonsArray[i] = new CheckBox(names[i]);
                buttonsArray[i].setUIID("RadioButton");
            }
            buttonsArray[i].setToggle(true);
            buttonsArray[i].addActionListener(listener);
//            buttons.addComponent(buttonsArray[i]);
        }
        //set selected
        for (int i = 0, size = initiallySelectedValues.size(); i < size; i++) {
            setSelectedValue(((Integer) initiallySelectedValues.elementAt(i)).intValue());
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setupLayout() {
//        setupLayout(this.values.length);
//    }
//
//    /**
//     * creates the layout of the buttons. Can be overvritten
//     *
//     * @param numberButtons
//     */
//    public void setupLayout(int numberButtons) {
//        setupLayout(numberButtons, 1);
//    }
//
//    public void setupLayout(int numberButtons, int bgRows) {
//
//    }
//</editor-fold>
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
            Container compGroupCont = new Container(BoxLayout.y());
//            ComponentGroup cGroup = makeComponentGroup();
            int compGroupIndex = 0;
//            int compIndex = 0;
            for (int i = 0, size = this.values.length; i < size; i++) {
                if (compGroupRows[compGroupIndex] == null) {
                    compGroupRows[compGroupIndex] = makeComponentGroup();
                }
                if (compGroupRows[compGroupIndex].getComponentCount() == 0) {
                    compGroupCont.add(compGroupRows[compGroupIndex]); //add new CG
                }
                compGroupRows[compGroupIndex].addComponent(buttonsArray[i]);
                if (compGroupRows[compGroupIndex].getComponentCount() == nbButtonsInEachRow[compGroupIndex]) { //if this row is filled up
                    compGroupIndex++; //go to next row
//                    cGroup = makeComponentGroup();
                }
            }
            if (allButtonsSameWidth) {
                compGroupCont.setSameWidth(compGroupRows[compGroupIndex]);
            }
            addComponent(compGroupCont); //add buttons
//<editor-fold defaultstate="collapsed" desc="comment">
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
//                ComponentGroup buttonsContainer = new ComponentGroup();
                buttonsContainer = new ComponentGroup();
                buttonsContainer.setLayout(new FlowLayout(Component.CENTER));
                buttonsContainer.setElementUIID("ToggleButton");
                buttonsContainer.setHorizontal(true);
//                buttonsContainer = buttonsContainer;
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

    private void setupLayoutAsTable(int rows, int columns, int numberButtons) {
        Container buttons = new Container(new TableLayout(rows, columns));
        for (int i = 0; i < numberButtons; i++) {
            buttons.addComponent(buttonsArray[i]);
        }
        addComponent(buttons); //add buttons
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void setup() {
//        buttonsArray = new RadioButton[this.values.length];
//        for (int i = 0, size = this.values.length; i < size; i++) {
////            buttonsArray[i] = new RadioButton(names[i]);
//            buttonsArray[i] = new RadioButton(names[i]);
////            addItem(new RadioButton(names[i]));
//        }
//        for (int i = 0, size = initiallySelectedValues.size(); i < size; i++) {
//            setSelectedValue(((Integer) initiallySelectedValues.elementAt(i)).intValue());
//        }
//
////        setupLayout();
////        addLayoutToContainer(this);
//        addLayoutToContainer();
//    }
//
//    void xxxaddLayoutToContainer() {
//        //create the logical button group
//        ButtonGroup bg = new ButtonGroup();
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
////        for (int i = 0, size = size(); i < size; i++) {
//            if (onlySingleSelectionAllowed) {
////                bg.add(buttonsArray[i]); //ButtonGroup's only purpose is to avoid multiple simultanous selections
//            }
////            RadioButton radioButton = ((RadioButton) getModel().getItemAt(i));
////            bg.add(radioButton);
////            radioButton.setToggle(true);
//            buttonsArray[i].setToggle(true);
////            buttons.addComponent(buttonsArray[i]);
////            buttonsArray[i].addActionListener(listener); //-not needed anymore
////            radioButton.addActionListener(listener);
//        }
//
//        ComponentGroup buttons = new ComponentGroup();
//        buttons.setLayout(new FlowLayout(Component.CENTER));
//        buttons.setElementUIID("ToggleButton");
//        buttons.setHorizontal(true);
//
//        //create grouping for display
////        Container centerFlow = new Container(new FlowLayout(Component.CENTER));
////        Container result = new Container(new FlowLayout(Component.CENTER));
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
////        for (int i = 0, size = size(); i < size; i++) {
////            buttons.addComponent(((RadioButton) getModel().getItemAt(i)));
//            buttons.addComponent(buttonsArray[i]);
//            if (false) { //rather just let the labels flow around if too wide for the screen
//                if (buttons.getPreferredW() >= Display.getInstance().getDisplayWidth() / 2 && i < size - 1) { //break into multiple lines (but only if there's at least one more button to add)
////                    result.addComponent(buttons);
//                    buttons = new ComponentGroup();
//                    buttons.setElementUIID("ToggleButton");
//                    buttons.setHorizontal(true);
//                }
//            }
//        }
////        result.addComponent(buttons); //add last buttons
////        addComponent(result); //add last buttons
//        addComponent(buttons); //add last buttons
////        addComponent(centerFlow); //add the buttons to this buttonsContainer
//    }
//</editor-fold>
    /**
     * is RadioButton at index selected?
     */
    boolean isSelected(int index) {
//        return ((RadioButton) getModel().getItemAt(index)).isSelected();
        return buttonsArray[index].isSelected();
    }

//    public int getSelectedIndex() {
//        return ((RadioButton) getModel().getItemAt(index)).isSelected();
//    }
    private void setSelectedIndexState(int index, boolean set) {
//        buttonsArray[index].setSelected(set);
//        ((RadioButton) getModel().getItemAt(index)).setSelected(set);
        int oldIndex = getSelectedIndex();
        if (onlySingleSelectionAllowed) { //shouldn't test on onlySingleSelectionAllowed here????
            ((RadioButton) buttonsArray[index]).setSelected(set);
            if (selectionListener != null) {
                selectionListener.fireSelectionEvent(oldIndex, index);
            }
        } else {
            ((CheckBox) buttonsArray[index]).setSelected(set);
        }
//        selectionListener.fireSelectionEvent(oldIndex, index);
//        super.setSelectedIndex(index); //trigger selection listener
    }

//    public void setSelectedIndexXXX(int index) {
//        setSelectedIndexState(index, true);
//    }
//
//    public void flipSelectedIndexXXX(int index) {
////        buttonsArray[index].setSelected(!buttonsArray[index].isSelected());
//        setSelectedIndexState(index, !isSelected(index)); //flip selected state
//    }

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
                selectedValues.addElement(new Integer(values[i]));
            }
        }
        return selectedValues;
    }

    /**
     * returns vector with all selected names
     */
//    public Vector getSelectedNamesXXX() {
//        Vector selectedNames = new Vector();
//        for (int i = 0, size = buttonsArray.length; i < size; i++) {
////        for (int i = 0, size = size(); i < size; i++) {
////            if (buttonsArray[i].isSelected()) {
//            if (isSelected(i)) {
////                selectedValues.addElement(new Integer(values[i]));
//                selectedNames.addElement(names[i]);
//            }
//        }
//        return selectedNames;
//    }

    /**
     * returns the index of the *first* selected element (in case there are
     * several selected)
     *
     * @return -1 if no element were selected. NO: returns 0 so first element is
     * selected by default??
     */
    public int getSelectedIndex() {
        for (int i = 0, size = buttonsArray.length; i < size; i++) {
//        for (int i = 0, size = size(); i < size; i++) {
            if (isSelected(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * returns the selected logical value.
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
//    public void setSelectedNameXXX(String name) {
////        int index = 0;
////        for (int i = 0, size = names.length; i < size; i++) {
//        for (int i = 0, size = names.length; i < size; i++) {
//            if (names[i].equals(name)) {
////                index = i;
//                setSelectedIndexState(i, true);
//            }
//        }
////        buttonsArray[index].setSelected(true);
////        buttonsArray[index].setSelected(true);
//    }

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
}
