/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.ButtonGroup;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.RadioButton;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.util.EventDispatcher;
import java.util.Map;

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
    String[] fieldNames;
    ButtonGroup buttonGroup;

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

    private static Object getIndexOfName(String[] fieldNames, String name) {
        for (int i = 0, size = fieldNames.length; i < size; i++) {
            if (fieldNames[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private Object getIndexOfName(String name) {
        return getIndexOfName(fieldNames, name);
    }

    @Override
    public String toString() {
        String s = "";
        int selIdx = getSelectedIndex();
        for (int i = 0, size = fieldNames.length; i < size; i++) {
            s += fieldNames[i] + (i == selIdx ? "[x] | " : " | ");
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
    MyComponentGroup(Object[] valueArray, String[] names, int selectedIndex, boolean unselectAllowed, boolean verticalLayout) {
        super();
        this.values = valueArray;
        if (names != null) {
            this.fieldNames = names;
            ASSERT.that(this.fieldNames.length == values.length, "MyComponentGroup called with different number of values and names, values=" + valueArray + ", names=" + names);
        } else {
            this.fieldNames = new String[values.length];
            for (int i = 0, size = values.length; i < size; i++) {
                fieldNames[i] = values[i].toString();
            }
        }

        this.setHorizontal(!verticalLayout);
        buttonGroup = new ButtonGroup();
        ActionListener buttonListener = (e) -> {
            if (dispatcher != null) {
                dispatcher.fireActionEvent(e);
            }
            Object source = e.getSource();
            for (int i = 0, size = this.getComponentCount(); i < size; i++) {
                if (source.equals(this.getComponentAt(i))) {
                    if (((RadioButton) this.getComponentAt(i)).isSelected()) {
                        if (selectionListener != null) {
                            selectionListener.fireSelectionEvent(getSelectedIndex(), i);
                        }
                    } else if (selectionListener != null) {
                        selectionListener.fireSelectionEvent(getSelectedIndex(), -1); //CORRECT?!
                    }
                }
            }
        };

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
//            RadioButton[] radioButtonArray = new RadioButton[values.length];
        for (int i = 0; i < values.length; i++) {
//            radioButton = new RadioButton(values[i]);
//            radioButton = new RadioButton(values[i] instanceof String?(String)values[i]:values[i].toString());
//            String s;
//            if (fieldNames != null && fieldNames.length > 0) {
//                s = fieldNames[i];
//            } else {
//                s = values[i].toString();
//            }
//            radioButton = new RadioButton(values[i] instanceof String ? (String) values[i] : values[i].toString());
            radioButton = new RadioButton(fieldNames[i]);
            radioButton.setToggle(true); //allow to de-select a selected button
            radioButton.setUnselectAllowed(unselectAllowed); //allow to de-select a selected button
            radioButton.addActionListener(buttonListener); //allow to de-select a selected button
            buttonGroup.add(radioButton);
            this.add(radioButton);
//<editor-fold defaultstate="collapsed" desc="comment">
//                radioButtonArray[i] = radioButton;
//            if (selectedString != null && values[i].equals(selectedString)) {
//            if (selectedString != null && (values[i] instanceof String?(String)values[i]:values[i].toString()).equals(selectedString)) {
//</editor-fold>
//            if (selectedString != null && values[i].toString().equals(selectedString)) {
//            if (selectedString != null && fieldNames[i].equals(selectedString)) {
//                radioButton.setSelected(true);
//            }
//            select(selectedString);
            select(selectedIndex);
        }
    }

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
        this(valueArray, names, getIndexOfValue(valueArray, selectedValue), true, false);
    }

    MyComponentGroup(Object[] values, String[] names, Object selectedValue, boolean unselectAllowed) {
        this(values, names, getIndexOfValue(values, selectedValue), unselectAllowed, false);
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
                if (((RadioButton) this.getComponentAt(i)).isSelected()) {
                    set.accept(((RadioButton) this.getComponentAt(i)).getText()); //store the index of the selected string
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
    public void select(int selectedIndex) {
        int size = this.getComponentCount();
//        if (selectedIndex < 0 || selectedIndex >= size) {
//            return;
//        }
        int oldIndex = getSelectedIndex();
        for (int i = 0; i < size; i++) {
//            ((RadioButton) this.getComponentAt(i)).setSelected(i == selectedIndex);
            if (i == selectedIndex) {
                ((RadioButton) this.getComponentAt(i)).setSelected(true);
                if (selectionListener != null) {
                    selectionListener.fireSelectionEvent(oldIndex, selectedIndex);
                }
            } else {
                ((RadioButton) this.getComponentAt(i)).setSelected(false);
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
        if (false) {
            for (int i = 0; i < values.length; i++) {
//            radioButton = new RadioButton(values[i]);
//            if (selectedString != null && values[i].toString().equals(selectedString)) {
//                buttonGroup.getRadioButton(i).setSelected(true);
//            }
//            buttonGroup.getRadioButton(i).setSelected(selectedString != null && values[i].toString().equals(selectedString));
//            buttonGroup.getRadioButton(i).setSelected(selectedString != null && fieldNames[i].equals(selectedString));
                if (fieldNames[i].equals(selectedString)) {
                    select(i);
                }
            }
        }
        select(getIndexOfName(selectedString));
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
        if (false) {
            for (int i = 0; i < values.length; i++) {
//            if (value.equals(values[i])) {
//                buttonGroup.getRadioButton(i).setSelected(true);
//            }
                if (value.equals(values[i])) {
                    select(i);
                }
            }
        }
        select(getIndexOfValue(value));
    }

    /**
     * returns selected index, or -1 if none
     *
     * @return
     */
    public int getSelectedIndex() {
        int size = this.getComponentCount();
        for (int i = 0; i < size; i++) {
            if (((RadioButton) this.getComponentAt(i)).isSelected()) {
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
        if (selected > 0 && selected < fieldNames.length) {
            return fieldNames[selected];
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
        if (selected > 0 && selected < values.length) {
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
