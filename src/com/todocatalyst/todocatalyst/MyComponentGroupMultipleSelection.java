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
import com.codename1.ui.util.EventDispatcher;
import java.util.ArrayList;
import java.util.List;
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
class MyComponentGroupMultipleSelection extends ComponentGroup {

    Object[] values;
    ButtonGroup buttonGroup;
    private final List selection;
    private EventDispatcher dispatcher = new EventDispatcher();

    /**
     *
     * @param values either an array of String or an array of Objects for which
     * toString() will be used
     * @param selectedString
     * @param unselectAllowed
     * @param noSelectionAllowed OK to not have anything selected
     */
//    MyComponentGroupMultipleSelection(Object[] values, List selection, boolean unselectAllowed, boolean noSelectionAllowed, boolean vertical) {
    MyComponentGroupMultipleSelection(Object[] values, List selection, boolean noSelectionAllowed, boolean vertical) {
        super();
        this.values = values;
        this.setHorizontal(!vertical);
        buttonGroup = new ButtonGroup();
//        final List sel = new ArrayList(selection);
        this.selection = new ArrayList(selection);
        ActionListener buttonListener = (e) -> {
            dispatcher.fireActionEvent(e);
        };

        for (int i = 0; i < values.length; i++) {
            RadioButton radioButton = new RadioButton(values[i] instanceof String ? (String) values[i] : values[i].toString());
            radioButton.setToggle(true); //allow to de-select a selected button
//            radioButton.setUnselectAllowed(unselectAllowed); //allow to de-select a selected button
            radioButton.setUnselectAllowed(true); //allow to de-select a selected button
            radioButton.addActionListener(buttonListener); //allow to de-select a selected button
            Object v=values[i];
            radioButton.addActionListener((e) ->{
            if (radioButton.isSelected()) {
//                sel.add(values[0]);
//                this.selection.add(values[0]);
                this.selection.add(v);
            } else {
//                sel.remove(values[i]);
                this.selection.remove(v);
            }}); //allow to de-select a selected button
            buttonGroup.add(radioButton);
            this.add(radioButton);
//            if (selectedString != null && values[i].toString().equals(selectedString)) {
            if (this.selection.contains(values[i])) {
                radioButton.setSelected(true);
            }
        }
    }

//    MyComponentGroupMultipleSelection(Object[] values, boolean unselectAllowed) {
//        this(values, "", unselectAllowed);
//    }
//
//    MyComponentGroupMultipleSelection(String[] values, int selectedStringIndex, boolean unselectAllowed) {
//        this(values, values[selectedStringIndex], unselectAllowed);
//    }
//
//    MyComponentGroupMultipleSelection(Object[] values, ParseIdMap2 parseIdMap, MyForm.GetString get, MyForm.PutString set) {
//        this(values, parseIdMap, get, set, true);
//    }

//    MyComponentGroupMultipleSelection(Object[] values, ParseIdMap2 parseIdMap, MyForm.GetString get, MyForm.PutString set, boolean unselectAllowed) {
//        this(values, get.get(), unselectAllowed);
//        parseIdMap.put(this, () -> {
//            int size = this.getComponentCount();
//            for (int i = 0; i < size; i++) {
//                if (((RadioButton) this.getComponentAt(i)).isSelected()) {
//                    set.accept(((RadioButton) this.getComponentAt(i)).getText()); //store the index of the selected string
//                    return;
//                }
//            }
//            set.accept("");
//        });
//    }

    /**
     * Adds a listener to the button which will cause an event to dispatch on
     * click
     *
     * @param l implementation of the action listener interface
     */
    public void addActionListener(ActionListener l) {
        dispatcher.addListener(l);
    }

    /**
     * Removes the given action listener from the button
     *
     * @param l implementation of the action listener interface
     */
    public void removeActionListener(ActionListener l) {
        dispatcher.removeListener(l);
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
        int size = this.getComponentCount();
        for (int i = 0; i < values.length; i++) {
//            }
            buttonGroup.getRadioButton(i).setSelected(selectedString != null && values[i].toString().equals(selectedString));
        }
    }

    /**
     *
     * @param selectedIndex an illegal value, e.g. -1, will lead to all being
     * unselected
     */
    public void select(int selectedIndex) {
        int size = this.getComponentCount();
        if (selectedIndex < 0 || selectedIndex >= size) {
            return;
        }
        for (int i = 0; i < size; i++) {
            ((RadioButton) this.getComponentAt(i)).setSelected(i == selectedIndex);
        }
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
        int size = this.getComponentCount();
        if (selected >= 0 && selected < size) {
            return ((RadioButton) this.getComponentAt(selected)).getText();
        }
        return null;
    }

    public List getSelection() {
        return selection;
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
