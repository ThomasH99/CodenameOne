/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.components.OnOffSwitch;
import com.codename1.io.Log;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.util.Resources;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * When instantiating MyForm the following must be done: NORMAL USE: - create a
 * new screen using MyForm form = new MyForm(params, ..., new ScreenArg...
 * (Netbeans creates a template for ScreenArg) - the done(Object returnValue,
 * boolean done) method must implement the correct behavior for dealing with the
 * edited value (if done==true) or with cleaning up if done==false. - add other
 * commands using the methods below when useful. - implement the body of
 * mySetup() (should typically call setup() that calls (setEditLayout();
 * setCommands(); setEditedFields();) to ensure that - add commands Done and
 * Cancel CALLING: to create and call the form: new MyForm("Select tasks
 * where...", expr).display(); SPECIAL CASES: - if new constructors are defined
 * (shouldn't be necessary in most cases) they must call one of MyForm's
 * constructors to ensure . - if the screen is exited in other ways than calling
 * commands Done or Cancel, then the code from theh commands must be created
 * manually
 *
 * @author Thomas
 */
//abstract public class MyForm extends Form {
//public class MyScreen extends ParseObject {
public class MyForm extends Form {

//    protected static Form form;
    Resources theme;
//    HashMap properties;

    /**
     * create a new MyForm to edit the Object value. If the user exits with
     * Done, then the edited value is passed back. Instantiate the ScreenArg
     * with an appropriate implementaion of done() that will deal with return
     * value.
     *
     * @param value the object to edit - if null, then the form should create a
     * new empty value (to handle the 'first time' case when no previous value
     * exists), however, it nothing is created (e.g. nothing is added to the
     * empty value, or cancel is chosen, then return null!)
     * @param myScreen
     * @param screenArg
     */
//    MyForm(Object value, /*Screen myScreen,*/ ScreenArg screenArg) {
    MyForm(String title) { //throws ParseException, IOException {
        super(title);
//        form = new Form();
//        form = this;
//        setup();
//            void setup() {
        setTactileTouch(true); //enables opening contextmenu on touching a list element??
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true); //always allow scrolling up/down 
        try {
            theme = Resources.openLayered("/theme");
        } catch (IOException ex) {
            Log.e(ex);
        }
        UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));

        // the specification requires that only portrait would be supported
        Display.getInstance().lockOrientation(true);

        // We load the images that are stored in the resource file here so we can use them later
//        placeholder = (EncodedImage) theme.getImage("placeholder");
//        getProperties(); //get existing (previously saved) properties from Parse
    }

    interface GetParseValue {

        void saveEditedValueInParseObject();
    }

    /**
     * This Custom Toolbar changes the opacity of the Toolbar background upon
     * scroll
     *
     * @author Chen
     */
    public class CustomToolbar extends Toolbar implements ScrollListener {

        private int alpha;

        public CustomToolbar() {
        }

        public CustomToolbar(boolean layered) {
            super(layered);
        }

        public void paintComponentBackground(Graphics g) {
            int a = g.getAlpha();
            g.setAlpha(alpha);
            super.paintComponentBackground(g);
            g.setAlpha(a);
        }

        public void scrollChanged(int scrollX, int scrollY, int oldscrollX, int oldscrollY) {
            alpha = scrollY;
            alpha = Math.max(alpha, 0);
            alpha = Math.min(alpha, 255);
        }
    }

    interface UpdateField {

        void update();
    }

    interface GetString {

        String get();
    }

    interface PutString {

        void accept(String s);
    }

    interface GetInt {

        int get();
    }

    interface PutInt {

        void accept(int i);
    }

    interface GetDate {

        Date get();
    }

    interface PutDate {

        void accept(Date d);
    }

    interface GetBoolean {

        boolean get();
    }

    interface PutBoolean {

        void accept(boolean b);
    }

    class MyTimePicker extends Picker {

//        String title;
//        String parseId;
        MyTimePicker(Map<Object, UpdateField> parseIdMap, GetInt get, PutInt set) {
            super();
//            this.title = title;
//            this.parseId = parseId;
            this.setType(Display.PICKER_TYPE_TIME);
            Integer i = get.get();
            if (i != null) {
                this.setTime(i);
            } else {
                //
            }
//            parseIdMap.put(parseId, () -> parseObject.put(parseId, this.getTime()));
            parseIdMap.put(this, () -> set.accept(this.getTime()));
        }

//        MyTimePicker(String title, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//            super();
//            this.title = title;
//            this.parseId = parseId;
//            this.setType(Display.PICKER_TYPE_TIME);
//            Integer i = parseObject.getInt(parseId);
//            if (i != null) {
//                this.setTime(i);
//            }
//            parseIdMap.put(parseId, () -> parseObject.put(parseId, this.getTime()));
//        }
//        String getTitle() {
//            return title;
//        }
    };

    class MySimpleDateFormat extends SimpleDateFormat {

        String showWhenUndefined;

        /**
         * Construct a SimpleDateFormat with a given pattern.
         *
         * @param pattern
         */
        public MySimpleDateFormat(String pattern, String showWhenUndefined) {
            super(pattern);
            this.showWhenUndefined = showWhenUndefined;
        }

        public String format(Date source) {
            if (source.getTime() == 0) {
                return showWhenUndefined;
            } else {
                return super.format(source);
            }
        }
    }

    class MyDateAndTimePicker extends Picker {

        @Override
        public void pressed() {
            //set date to Now if empty when button is clicked
            if (getDate().getTime() == 0) {
//                setDate(new Date());
                getDate().setTime(new Date().getTime()); //use this instead of setDate to set date to avoid updating label before showing ticker
            }
            super.pressed();
        }

        @Override
        protected void updateValue() {
            Date date = getDate();
            if (date != null && date.getTime() == 0) {
                setText(zeroValuePattern); // return zeroValuePattern when value of date is 0 (not defined)
            } else {
                super.updateValue(); //To change body of generated methods, choose Tools | Templates.
            }
        }
        String zeroValuePattern;

//        String title;
//        String parseId;
//        MyDateAndTimePicker(String title, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
        MyDateAndTimePicker(String zeroValuePattern, Map<Object, UpdateField> parseIdMap, GetDate get, PutDate set) {
            super();
//            this.title = title;
//            this.parseId = parseId;
            this.zeroValuePattern = zeroValuePattern;
            this.setType(Display.PICKER_TYPE_DATE_AND_TIME);
//            this.addActionListener(
//                    (e) -> {
//                        if (getDate().getTime() == 0) {
//                            setDate(new Date());
//                        }
//
//                    }); //set date to Now if empty when button is clicked
//            this.setDate(parseObject.getDate(parseId)); //!doesn't work for null value
            Date d = get.get();
            if (d
                    != null) {
                this.setDate(d);
            } else {
                setDate(new Date(0)); //UI: default date is undefined
            }
//            this.setFormatter(new MySimpleDateFormat(this.getFormatter().toPattern(), zeroValuePattern)); //reuse default formatter pattern, only override for 0 value

//            parseIdMap.put(parseId,() -> parseObject.put(parseId, this.getDate()));
            parseIdMap.put(this, () -> set.accept(this.getDate()));
        }
    }

        class MyDatePicker extends Picker {

            @Override
            protected void updateValue() {
                Date date = getDate();
                if (date != null && date.getTime() == 0) {
                    setText(zeroValuePattern); // return zeroValuePattern when value of date is 0 (not defined)
                } else {
                    super.updateValue(); //To change body of generated methods, choose Tools | Templates.
                }
            }

            @Override
            public void pressed() {
                //set date to Now if empty when button is clicked
                if (getDate().getTime() == 0) {
//                setDate(new Date());
                    getDate().setTime(new Date().getTime()); //use this instead of setDate to set date to avoid updating label before showing ticker
                }
                super.pressed();
            }

            String zeroValuePattern;

//        String title;
//        String parseId;
            MyDatePicker(String zeroValuePattern, Map<Object, UpdateField> parseIdMap, GetDate get, PutDate set) {
                super();
//            this.title = title;
//            this.parseId = parseId;
                this.zeroValuePattern = zeroValuePattern;
                this.setType(Display.PICKER_TYPE_DATE);
//            this.setDate(parseObject.getDate(parseId));
                Date d = get.get();
                if (d != null) {
                    this.setDate(d);
                } else {
                    setDate(new Date(0)); //UI: default date is undefined
                }
                parseIdMap.put(this, () -> set.accept(d));
            }

//        MyDatePicker(String title, String zeroValuePattern, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//            super();
//            this.title = title;
//            this.parseId = parseId;
//            this.zeroValuePattern = zeroValuePattern;
//            this.setType(Display.PICKER_TYPE_DATE);
////            this.setDate(parseObject.getDate(parseId));
//            Date d = parseObject.getDate(parseId);
//            if (d != null) {
//                this.setDate(d);
//            } else {
//                setDate(new Date(0)); //UI: default date is undefined
//            }
//            parseIdMap.put(parseId, () -> parseObject.put(parseId, this.getDate()));
//        }
//
//        String getTitle() {
//            return title;
//        }
        };

        class MyStringPicker extends Picker {

            MyStringPicker(String[] stringArray, Map<Object, UpdateField> parseIdMap, GetString get, PutString set) {
                super();
                this.setType(Display.PICKER_TYPE_STRINGS);
                this.setStrings(stringArray);
                String s = get.get();
                if (s != null) {
                    this.setSelectedString(s);
                }
                parseIdMap.put(this, () -> set.accept(this.getSelectedString()));
            }

            MyStringPicker(String[] stringArray, Map<Object, UpdateField> parseIdMap, GetInt get, PutInt set) {
                super();
                this.setType(Display.PICKER_TYPE_STRINGS);
                this.setStrings(stringArray);
                this.setSelectedString(stringArray[get.get()]);
                parseIdMap.put(this, () -> {
                        String str = this.getSelectedString();
                        for (int i = 0; i < stringArray.length; i++) {
                            if (stringArray[i].equals(str)) {
//                                parseObject.put(parseId, i);
                                set.accept(i);
//                                break;
                                return;
                            }
                        }
                });
            }

        };

        class MyOnOffSwitch extends OnOffSwitch {

//            String title;
//            String parseId;

            MyOnOffSwitch(Map<Object, UpdateField> parseIdMap, GetBoolean get, PutBoolean set) {
                super();
//                this.title = title;
//                this.parseId = parseId;
//            this.setValue(parseObject.getBoolean(parseId));
//                Boolean b = parseObject.getBoolean(parseId);
                Boolean b = get.get();
                if (b != null) {
                    this.setValue(b);
                }
//                parseIdMap.put(parseId, () -> parseObject.put(parseId, this.isValue()));
                parseIdMap.put(this, () -> set.accept(this.isValue()));
            }

//            MyOnOffSwitch(String title, String onString, String offString, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
            MyOnOffSwitch(String offString, String onString, Map<Object, UpdateField> parseIdMap, GetBoolean get, PutBoolean set) {
                this(parseIdMap, get, set);
                this.setOn(onString);
                this.setOff(offString);
            }

        };

        /**
         * stores the index of the string array in Parse. E.g. ["str1", "str2",
         * "str3"] and str2 selected will store 1, str1 will store 0.
         */
        class MyComponentGroup extends ComponentGroup {

            MyComponentGroup(String[] values, Map<Object, UpdateField> parseIdMap, GetInt get, PutInt set) {
                super();
                this.setHorizontal(true);
                ButtonGroup buttonGroup = new ButtonGroup();
//            String selected = parseObject.getString(parseId);
//                String selectedString = parseObject.getString(parseId);
                String selectedString = values[get.get()];
                RadioButton radioButton;
//            RadioButton[] radioButtonArray = new RadioButton[values.length];
                for (int i = 0; i < values.length; i++) {
                    radioButton = new RadioButton(values[i]);
                    radioButton.setToggle(true); //allow to de-select a selected button
                    buttonGroup.add(radioButton);
                    this.add(radioButton);
//                radioButtonArray[i] = radioButton;
                    if (selectedString != null && values[i].equals(selectedString)) {
                        radioButton.setSelected(true);
                    }
                }
//            this.encloseHorizontal(radioButtonArray);
                parseIdMap.put(this, () -> {
                    int size = this.getComponentCount();
                    for (int i = 0; i < size; i++) {
                        if (((RadioButton) this.getComponentAt(i)).isSelected()) {
//                        parseObject.put(parseId, (((RadioButton) this.getComponentAt(i)).getText())); //store the selected string
//                            parseObject.put(parseId, i); //store the index of the selected string
                            set.accept(i); //store the index of the selected string
                            return;
                        }
                    }
                    //if nothing was selected (possible??), set String to empty
//                    parseObject.remove(parseId); //if nothing's selected, remove the field
                });
            }


        };

        class MyTextField extends TextField {

//            String title;
//            String parseId;

            MyTextField(String hint, Map<Object, UpdateField> parseIdMap, GetString getValue, PutString setValue) {
                this(hint, 20, TextArea.ANY, parseIdMap, getValue, setValue);
            }
//        MyTextField(String title, String hint, int columns, int constraint, Map<String, ScreenItemP.GetParseValue> parseIdMap, Consumer<String> setValue, Supplier<String> getValue, ParseObject parseObject, String parseId) {
            MyTextField(String hint, int columns, int constraint, Map<Object, UpdateField> parseIdMap, GetString getValue, PutString setValue) {
                super("", hint, columns, constraint);
//                this.title = title;
//                this.parseId = parseId;
//            setText(parseObject.getString(parseId));
                setText(getValue.get());
//                parseIdMap.put(parseId, this);
//            parseIdMap.put(parseId, () -> getText());
//            parseIdMap.put(parseId, () -> parseObject.put(parseId, getText()));
                parseIdMap.put(this, () -> setValue.accept(getText()));
            }

//            MyTextField(String title, String hint, int columns, int constraint, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//                super("", hint, columns, constraint);
////                this.title = title;
////                this.parseId = parseId;
//                setText(parseObject.getString(parseId));
////                parseIdMap.put(parseId, this);
////            parseIdMap.put(parseId, () -> getText());
//                parseIdMap.put(parseId, () -> parseObject.put(parseId, getText()));
//            }

        };

        class MyCheckBox extends CheckBox {

            MyCheckBox(Map<Object, UpdateField> parseIdMap, GetBoolean get, PutBoolean set) {
                this(null, parseIdMap, get, set);
            }

            MyCheckBox(String title, Map<Object, UpdateField> parseIdMap, GetBoolean get, PutBoolean set) {
                super();
                if (title!=null) this.setSelectCommandText(title);
                 Boolean b = get.get();
                if (b != null) {
                    this.setSelected(b);
                }
                parseIdMap.put(this, () -> set.accept(this.isSelected()));
            }
        };

        /**
         * returns list of all currently active menu commands (can then be
         * restored with setAllCommands)
         */
        Vector getAllCommands() {
            Vector commands = new Vector(getCommandCount());
            for (int i = 0, size = getCommandCount(); i < size; i++) {
                commands.addElement(getCommand(i));
            }
            return commands;
        }

        void setAllCommands(Vector commands) {
//        Vector commands = new Vector(getCommandCount());
            for (int i = 0, size = commands.size(); i < size; i++) {
                addCommand((Command) commands.elementAt(i));
            }
        }

        /**
         * adds commands in 'natural' order: first one is first in menu, second
         * is next, ... , last one goes on single softkey)
         *
         * @param command
         */
        public void addCommands(Command[] commands) {
//        for (int i=0, size=commands.length; i<size; i++) {
            for (int i = commands.length - 1; i >= 0; i--) { //add commands in reverse order
                addCommand(commands[i]);
            }
        }

//    Form getForm() {
//        return form;
//    }
//    void show(boolean back) {
//        if (back) {
//            form.showBack();
//        } else {
//            form.show();
//        }
//    }
//    void show() {
//        show(false);
//    }
//        protected static void putEditedValues(Map<String, GetParseValue> parseIdMap) {
//            for (String parseId : parseIdMap.keySet()) {
////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
//                parseIdMap.get(parseId).saveEditedValueInParseObject();
//            }
//        }

//        protected static void putEditedValues2(Map<String, GetParseValue> parseIdMap, Map<Object, UpdateField> parseIdMap2) {
        /**
         * will iterate over all the fields in parseIdMap and call the stored
         * lambda functions to update the corresponding fields in the edited
         * ParseObject
         */
        protected static void putEditedValues2(Map<Object, UpdateField> parseIdMap2) {
//            for (String parseId : parseIdMap2.keySet()) {
////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
//                parseIdMap.get(parseId).saveEditedValueInParseObject();
//            }
            for (Object parseId : parseIdMap2.keySet()) {
//            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
                parseIdMap2.get(parseId).update();
            }
        }

        void refreshAfterEdit(){
            revalidate();
        };
        
    }
