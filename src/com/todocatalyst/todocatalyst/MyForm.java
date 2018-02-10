/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.OnOffSwitch;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
//import com.codename1.ui.*;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
//import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.SideMenuBar;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
//import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.TableLayout;
import com.parse4cn1.ParseObject;
//import com.todocatalyst.todocatalyst.SwipeClearContainer.SwipeClear;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
public abstract class MyForm extends Form {

    //TODO copy graphical format from e.g. lignesd'azur on iPhone
    protected Map<Object, UpdateField> parseIdMap2; // = new HashMap<Object, UpdateField>();
    protected MyForm previousForm;
//    protected static Form form;
//    Resources theme;
//    GetItemList updateItemListOnDone;
    protected UpdateField updateActionOnDone;
    protected CheckDataIsComplete checkDataIsCompleteBeforeExit; //used to check if a Screen has defined all needed data and returns error message String if not
    HashSet<ItemAndListCommonInterface> expandedObjects; // = new HashSet(); //TODO!! save expandedObjects for this screen and the given list. NB visible to allow to expland items when subtasks are added
    protected KeepInSameScreenPosition keepPos; // = new KeepInSameScreenPosition();
//    List selectedObjects; //selected objects
    ListSelector<ItemAndListCommonInterface> selectedObjects; //selected objects
//    private List oldSelectedObjects; //store selection after deactivating
    private ListSelector<ItemAndListCommonInterface> oldSelectedObjects; //store selection after deactivating
//    private static boolean showDetailsForAllTasks = false;
//    private static HashSet tasksWithDetailsShown;
    protected HashSet showDetails = new HashSet(); //set of Items etc expanded to show task details
//    protected InlineInsertNewElementContainer lastInsertNewElementContainer;
    protected InsertNewElementFunc lastInsertNewElementContainer;
//    private TextArea editFieldOnShowOrRefresh;
    private InsertNewElementFunc inlineInsertContainer;
    private BooleanFunction testIfEdit;

//    public TextArea getEditFieldOnShowOrRefresh() {
//        return editFieldOnShowOrRefresh;
//    }

    interface BooleanFunction {

        boolean test();
    }

    /**
     * the textArea will be
     *
     * @param editFieldOnShowOrRefresh
     */
    public InsertNewElementFunc getInlineInsertContainer() {
        return inlineInsertContainer;
    }

    public void setInlineInsertContainer(InsertNewElementFunc inlineInsertContainer) {
        //if resetting to null, remove previous container with animation
//        if (inlineInsertContainer == null && this.inlineInsertContainer != null && this.inlineInsertContainer instanceof Component) {
//            Container parent = ((Component) this.inlineInsertContainer).getParent();
//            parent.removeComponent((Component) this.inlineInsertContainer);
//            parent.animateLayout(300);
//        }
        if (inlineInsertContainer == null) { //) && this.inlineInsertContainer != null ) {
            setEditOnShow(null); //remove old textArea
        }
        if (inlineInsertContainer != null && inlineInsertContainer.getTextArea() != null) {
            setEditOnShow(inlineInsertContainer.getTextArea());
        }
        this.inlineInsertContainer = inlineInsertContainer;
    }

//    public void setEditOnShowOrRefresh(TextArea editFieldOnShowOrRefresh) { //, BooleanFunction testIfEdit) {
//        this.editFieldOnShowOrRefresh = editFieldOnShowOrRefresh;
////        this.testIfEdit = testIfEdit;
//        setEditOnShow(editFieldOnShowOrRefresh);
//    }
//
//    public void setEditOnShowOrRefresh(TextArea editFieldOnShowOrRefresh) {
//        setEditOnShowOrRefresh(editFieldOnShowOrRefresh, null);
//    }
    String SCREEN_TITLE = "";
    //TODO: move titles into Screens
    static final String SCREEN_LISTS_TITLE = "Lists";
    static final String SCREEN_ALL_TASKS_TITLE = "All tasks";
    static final String SCREEN_INBOX_TITLE = "Inbox"; // "Inbox (no owner)" "Inbox"
    static final String SCREEN_PROJECTS_TITLE = "Projects";
    static final String SCREEN_TEMPLATES_TITLE = "Templates";
    static final String SCREEN_COMPLETION_LOG_TITLE = "Log"; // "Work og", "Completion log", "Completed tasks"
    static final String SCREEN_TEMPLATE_PICKER = "Select Template";
    static final String SCREEN_CREATION_LOG_TITLE = "Diary"; // "Log book", "Creation log", "Created tasks"
    static final String SCREEN_NEXT_TITLE = "Next"; // "What's next", "Calendar"
    static final String SCREEN_TODAY_TITLE = "Today"; // "Creation log", "Created tasks"
    static final String SCREEN_OVERDUE_TITLE = "Overdue"; // "Creation log", "Created tasks"
    static final String SCREEN_TUTORIAL = "Tutorial";
    static final String SCREEN_TOUCHED = "Touched";
    static final String SCREEN_STATISTICS = "Achievements"; //"Statistics", "History"

    protected static final String REPEAT_RULE_KEY = "$REPEAT_RULE$73"; //used to store repeatRules in ParseId2Map so they can be calculated last

    public void setAutoSizeMode(boolean on) {
        if (getToolbar() != null && getToolbar().getTitleComponent() instanceof Label) {
            ((Label) getToolbar().getTitleComponent()).setAutoSizeMode(true);
        } else {
            getTitleComponent().setAutoSizeMode(true);
        }
    }

//    static final String SOURCE_OBJECT = "SOURCE_OBJECT"; //label to store source object in containers/components in lists
//<editor-fold defaultstate="collapsed" desc="comment">
//    CreateAndEditListItem createAndEdit;
//    GetUpdatedList updateList;
//    HashMap properties;
//    Image iconDone;
//    Image iconEditProperties;
//    Image iconNew;
//    Image iconInterrupt;
//    Image iconEditSymbol;
//    Image iconTimerSymbol;
//    Image iconCheckedTask;
//    Image iconUncheckedTask;
//    Image iconCheckMark;
//    Image iconAlarmSet;
//    Image iconShowMore;
//    Image iconShowLess;
//    Image iconMoveUpDown;
//    ItemList itemList;
//    int itemType;
//    ContainerBuilder containerBuilder;
//    final static int ITEM_TYPE_ITEM = 1;
//    final static int ITEM_TYPE_CATEGORY = 2;
//    final static int ITEM_TYPE_ITEMLIST = 3;
//    final static int ITEM_TYPE_WORKSLOT = 4;
//</editor-fold>
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
//    MyForm(String title) { //throws ParseException, IOException {
//        super(title);
//    }
    MyForm(String title, MyForm previousForm, UpdateField updateActionOnDone) { //throws ParseException, IOException {
        super(title);
//        setLayout(layout);
//        getLayeredPane().setLayout(BorderLayout.center());
        ReplayLog.getInstance().resetForNewScreen();
        if (false) {
            getFormLayeredPane(null, true).setLayout(new BorderLayout());
        }
        SCREEN_TITLE = title;
//        setToolbar(new Toolbar());
        getToolbar().setTitleCentered(true); //ensure title is centered even when icons are added
        setTitle(title); //do again since super(title)
//        if (getToolbar().getTitleComponent() instanceof Label) {
//            ((Label) getToolbar().getTitleComponent()).setAutoSizeMode(true); //ensure title is centered even when icons are added
//        }//        getTitleComponent().setAutoSizeMode(true); //DOESN'T work with toolbar
        if (false) {
            setAutoSizeMode(true); //ensure title is centered even when icons are added
        }
        this.previousForm = previousForm;
        this.updateActionOnDone = updateActionOnDone;
        ASSERT.that(updateActionOnDone != null, "doneAction should always be defined, Form=" + this);
        parseIdMapReset();
//        form = new Form();
//        form = this;
//        setup();
//            void setup() {
//        setTactileTouch(true); //enables opening contextmenu on touching a list element??
        setScrollable(false); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        setLayout(new BorderLayout()); //use CENTER to fill the screen correctly with scrolling content (avoid blanc bar at the bottom of the iPhone screen?)
        if (false) {
            setLayout(BoxLayout.y());
            setScrollable(false); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        }
        //<editor-fold defaultstate="collapsed" desc="comment">
        //************** CORRECT WAY TO MAKE SCROLLABLE
//        form.setScrollable(false);
//        form.setLayout(new BorderLayout());
//        form.add(BorderLayout.CENTER, myList);
////https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
//****************************

//        setScrollableY(true); //always allow scrolling up/down
//        setScrollableY(true); //always allow scrolling up/down
//        if (true) {
//            try {
//                theme = Resources.openLayered("/themeNative");
//            } catch (IOException ex) {
//                Log.e(ex);
//            }
//            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
//        }
// the specification requires that only portrait would be supported
//        Display.getInstance().lockOrientation(true);
//        Toolbar toolbar = new Toolbar();
//        setToolbar(toolbar);
//        toolbar.setScrollOffUponContentPane(true);
//        setToolbar(new Toolbar());
//        setToolbar(new Toolbar());
//        getToolbar().setScrollOffUponContentPane(true); //not working well
//        iconDone = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, getToolbar().getStyle());
//        iconEditProperties = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, getToolbar().getStyle());
//        iconNew = FontImage.createMaterial(FontImage.MATERIAL_ADD, getToolbar().getStyle());
//        iconInterrupt = FontImage.createMaterial(FontImage.MATERIAL_FLASH_ON, getToolbar().getStyle());
//        iconEditSymbol = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, getToolbar().getStyle());
//        iconTimerSymbol = FontImage.createMaterial(FontImage.MATERIAL_TIMER, getToolbar().getStyle());
//        iconCheckedTask = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, getToolbar().getStyle());
//        iconUncheckedTask = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, getToolbar().getStyle());
//        iconCheckMark = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, getToolbar().getStyle());
//        iconAlarmSet = FontImage.createMaterial(FontImage.MATERIAL_ALARM_ON, getToolbar().getStyle());
//        iconShowMore = FontImage.createMaterial(FontImage.MATERIAL_EXPAND_MORE, getToolbar().getStyle());
//        iconShowLess = FontImage.createMaterial(FontImage.MATERIAL_EXPAND_LESS, getToolbar().getStyle());
//        iconMoveUpDown = FontImage.createMaterial(FontImage.MATERIAL_SWAP_VERT, getToolbar().getStyle());
// We load the images that are stored in the resource file here so we can use them later
//        placeholder = (EncodedImage) theme.getImage("placeholder");
//        getProperties(); //get existing (previously saved) properties from Parse
//</editor-fold>
    }

    protected void setKeepPos(KeepInSameScreenPosition keepPos) {
        this.keepPos = keepPos;
    }

    protected void restoreKeepPos() {
        if (this.keepPos != null) {
            this.keepPos.setNewScrollYPosition();
        }
    }

    interface GetParseValue {

        void saveEditedValueInParseObject();
    }

    /**
     * clears/resets/reinitializes the parseIdMap2
     */
    public void parseIdMapReset() {
        parseIdMap2 = new HashMap<Object, UpdateField>();
    }

    /**
     * This Custom Toolbar changes the opacity of the Toolbar background upon
     * scroll
     *
     * @author Chen
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public class CustomToolbar extends Toolbar implements ScrollListener {
//        //TODO use this toolbar
//
//        private int alpha;
//
//        public CustomToolbar() {
//        }
//
//        public CustomToolbar(boolean layered) {
//            super(layered);
//        }
//
//        public void paintComponentBackground(Graphics g) {
//            int a = g.getAlpha();
//            g.setAlpha(alpha);
//            super.paintComponentBackground(g);
//            g.setAlpha(a);
//        }
//
//        public void scrollChanged(int scrollX, int scrollY, int oldscrollX, int oldscrollY) {
//            alpha = scrollY;
//            alpha = Math.max(alpha, 0);
//            alpha = Math.min(alpha, 255);
//        }
//    }
//</editor-fold>
    interface CreateAndEditListItem {

        void editNewItemListItem(ItemList itemList);
    }

    interface GetItemList {

        void update(ItemList itemList);
    }

    interface GetWorkSlotList {

        void update(List<WorkSlot> workSlotList);
//        void update(List workSlotList);
    }

    interface FetchWorkSlotList {

        List<WorkSlot> getUpdatedWorkSlotList(Object objworkSlotList);
//        void update(List workSlotList);
    }

    interface UpdateField {

        void update();
    }

    interface CheckDataIsComplete {

        /**
         * return error message if data in this screen is not complete,
         * otherwise null
         *
         * @return
         */
        String check();
    }

    interface GetUpdatedList {

        List getList();
    }

    interface ContainerBuilder {

        Component makeContainer(Object listItem);
    }

    interface GetString {

        String get();
    }

    interface PutString {

        void accept(String s);
    }

    interface GetStringFrom {

        String get(Object o);
    }

    interface GetInt {

        int get();
    }

    interface PutInt {

        void accept(int i);
    }

    interface GetDouble {

        double get();
    }

    interface PutDouble {

        void accept(double i);
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

    interface Action {

        void launchAction();
    }

    /**
     * creates a container where left is left adjusted and right is
     * right-adjusted
     *
     * @param left
     * @param right
     * @return
     */
    static Container createLeftRightAdjustedContainer(Component left, Component right) {
        return BorderLayout.west(left).add(BorderLayout.EAST, right);
    }

    /**
     * opens dialog to select WaitingTill date when setting a task to Waiting
     *
     * @param item
     * @return
     */
//    static void dialogSetWaitingDateAndAlarm(Item item, Map<Object, UpdateField> parseIdMap2) {
    static void dialogSetWaitingDateAndAlarm(Item item) {
        if (!MyPrefs.waitingAskToSetWaitingDateWhenMarkingTaskWaiting.getBoolean()
                || (item.getWaitingTillDateD().getTime() != 0 && item.getWaitingAlarmDateD().getTime() != 0)) {
            return; //do nothing if both waiting dates are already set
        }
        Map<Object, UpdateField> parseIdMap2 = new HashMap<Object, UpdateField>();
        Dialog dia = new Dialog();
        dia.setTitle("Set Waiting");
        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        dia.setCommandsAsButtons(true);
        dia.setAutoDispose(true); //should be default according to javadoc, but doesn't autodispose on [OK]

        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        dia.add(cont);

//        Picker p = new Picker();
//        MyDateAndTimePicker waitingDatePicker = new MyDateAndTimePicker("<set date>", parseIdMap2, () -> {
        MyDatePicker waitingDatePicker = new MyDatePicker("<set date>", parseIdMap2, () -> {
//            return new Date(item.getWaitingTillDate());
            return item.getWaitingTillDateD();
        }, (d) -> {
//            item.setWaitingTillDate(d.getTime());
            item.setWaitingTillDate(d);
        });
//        cont.add(new Label("Wait until")).add(waitingDatePicker).add("When you set a date, waiting tasks can automatically be hidden until that date.");
        cont.add(new Label(Item.WAIT_UNTIL_DATE)).add(waitingDatePicker).add(new SpanLabel("Waiting tasks are automatically hidden until the set date."));

        MyDateAndTimePicker waitingAlarmPicker = new MyDateAndTimePicker("<set date>", parseIdMap2, () -> {
//            return new Date(item.getWaitingAlarmDate());
            return item.getWaitingAlarmDateD();
        }, (d) -> {
//            item.setWaitingAlarmDate(d.getTime());
            item.setWaitingAlarmDate(d); //NB. only called if date is edited to sth different than 0
        });
//        cont.add(new Label("Waiting alarm")).add(waitingAlarmPicker).add("Set a special alarm for waiting tasks.");
        cont.add(new Label(Item.WAITING_ALARM_DATE)).add(waitingAlarmPicker).add(new SpanLabel("Set a reminder to follow up on a waiting task."));

        cont.addComponent(new Button(Command.create("OK", null, (e) -> {
            putEditedValues2(parseIdMap2);
            dia.dispose(); //close dialog
        })));
        dia.show();
//        return dia;
    }

    static Dialog dialogUpdateRemainingTime(MyDurationPicker remainingTimePicker) {
        Dialog dia = new Dialog();
        dia.setTitle("Update " + Item.EFFORT_REMAINING + "?");
        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        dia.setCommandsAsButtons(true);
        dia.setAutoDispose(true); //should be default according to javadoc, but doesn't autodispose on [OK]

        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        dia.add(cont);

        if (remainingTimePicker.getParent() != null) {
//            repaint(); //see Java doc for removeComponent 
            remainingTimePicker.getParent().removeComponent(remainingTimePicker);
        }
        cont.add(makeHelpButton(Item.EFFORT_REMAINING, "**"));
        cont.add(remainingTimePicker); //.add(new SpanLabel("Waiting tasks are automatically hidden until the set date."));

        cont.addComponent(new Button(Command.create("OK", null, (e) -> {
            dia.dispose(); //close dialog
        })));
        return dia;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static Dialog dialogUpdateRemainingTimeXXX(Item item, Map<Object, UpdateField> parseIdMap2) {
//        Dialog dia = new Dialog();
//        dia.setTitle("Update " + Item.EFFORT_REMAINING + "?");
//        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.setCommandsAsButtons(true);
//        dia.setAutoDispose(true); //should be default according to javadoc, but doesn't autodispose on [OK]
//
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.add(cont);
//
////        Picker p = new Picker();
////        MyDateAndTimePicker waitingDatePicker = new MyDateAndTimePicker("<set date>", parseIdMap2, () -> {
//        MyDurationPicker remainingTimePicker = new MyDurationPicker("<set date>", parseIdMap2, () -> {
////            return new Date(item.getWaitingTillDate());
////            return item.getRemainingEffortInMinutes();
//            return (int) item.getRemainingEffort() / MyDate.MINUTE_IN_MILLISECONDS;
//        }, (d) -> {
////            item.setWaitingTillDate(d.getTime());
////            item.setRemainingEffortInMinutes(d);
//            item.setRemainingEffort(d * MyDate.MINUTE_IN_MILLISECONDS);
//        });
//
////        cont.add(new Label("Wait until")).add(waitingDatePicker).add("When you set a date, waiting tasks can automatically be hidden until that date.");
//        cont.add(helpBut(Item.EFFORT_REMAINING, "**"));
//        cont.add(remainingTimePicker); //.add(new SpanLabel("Waiting tasks are automatically hidden until the set date."));
//
//        cont.addComponent(new Button(Command.create("OK", null, (e) -> {
//            dia.dispose(); //close dialog
//        })));
//        return dia;
//    }
//</editor-fold>
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

////<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * // * stores the index of the string array in Parse. E.g. ["str1",
     * "str2", // * "str3"] and str2 selected will store 1, str1 will store 0.
     * //
     */
//    class MyComponentGroup extends ComponentGroup {
//
//        MyComponentGroup(String[] values, Map<Object, UpdateField> parseIdMap, GetInt get, PutInt set) {
//            this(values, parseIdMap, get, set, true);
//        }
//
//        MyComponentGroup(String[] values, Map<Object, UpdateField> parseIdMap, GetInt get, PutInt set, boolean unselectAllowed) {
//            super();
//            this.setHorizontal(true);
//            ButtonGroup buttonGroup = new ButtonGroup() {
//                public void setSelected(RadioButton rb) {
//                    //if radionbutton is pressed when it is already selected then unselect (clearSelection)
//                    if (rb.isSelected()) {
//                        clearSelection();
//                    } else {
//                        super.setSelected(rb); //else handle it normally
//                    }
//                }
//            };
////            String selected = parseObject.getString(parseId);
////                String selectedString = parseObject.getString(parseId);
//            String selectedString = values[get.get()];
//            RadioButton radioButton;
////            RadioButton[] radioButtonArray = new RadioButton[values.length];
//            for (int i = 0; i < values.length; i++) {
//                radioButton = new RadioButton(values[i]);
//                radioButton.setToggle(true); //allow to de-select a selected button
//                radioButton.setUnselectAllowed(unselectAllowed); //allow to de-select a selected button
//                buttonGroup.add(radioButton);
//                this.add(radioButton);
////                radioButtonArray[i] = radioButton;
//                if (selectedString != null && values[i].equals(selectedString)) {
//                    radioButton.setSelected(true);
//                }
//            }
////            this.encloseHorizontal(radioButtonArray);
//            parseIdMap.put(this, () -> {
//                int size = this.getComponentCount();
//                for (int i = 0; i < size; i++) {
//                    if (((RadioButton) this.getComponentAt(i)).isSelected()) {
////                        parseObject.put(parseId, (((RadioButton) this.getComponentAt(i)).getText())); //store the selected string
////                            parseObject.put(parseId, i); //store the index of the selected string
//                        set.accept(i); //store the index of the selected string
//                        return;
//                    }
//                }
//                //if nothing was selected (possible??), set String to empty
////                    parseObject.remove(parseId); //if nothing's selected, remove the field
//            });
//        }
//
//        MyComponentGroup(String[] values, Map<Object, UpdateField> parseIdMap, GetString get, PutString set) {
//            this(values, parseIdMap, get, set, true);
//        }
//
//        MyComponentGroup(String[] values, Map<Object, UpdateField> parseIdMap, GetString get, PutString set, boolean unselectAllowed) {
//            super();
//            this.setHorizontal(true);
//            ButtonGroup buttonGroup = new ButtonGroup();
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
//            String selectedString = get.get();
//            RadioButton radioButton;
////            RadioButton[] radioButtonArray = new RadioButton[values.length];
//            for (int i = 0; i < values.length; i++) {
//                radioButton = new RadioButton(values[i]);
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
//                radioButton.setToggle(true); //allow to de-select a selected button
//                radioButton.setUnselectAllowed(unselectAllowed); //allow to de-select a selected button
//                buttonGroup.add(radioButton);
//                this.add(radioButton);
////                radioButtonArray[i] = radioButton;
//                if (selectedString != null && values[i].equals(selectedString)) {
//                    radioButton.setSelected(true);
//                }
//            }
////            this.encloseHorizontal(radioButtonArray);
//            parseIdMap.put(this, () -> {
//                int size = this.getComponentCount();
//                for (int i = 0; i < size; i++) {
//                    if (((RadioButton) this.getComponentAt(i)).isSelected()) {
////                        parseObject.put(parseId, (((RadioButton) this.getComponentAt(i)).getText())); //store the selected string
////                            parseObject.put(parseId, i); //store the index of the selected string
////                        set.accept(i); //store the index of the selected string
//                        set.accept(((RadioButton) this.getComponentAt(i)).getText()); //store the index of the selected string
//                        return;
//                    }
//                }
//                //if nothing was selected (possible??), set String to empty
//                set.accept("");
//            });
//        }
//
//    };
//</editor-fold>
    static int COLUMNS_FOR_INT = 5;

    class MyNumericTextField extends TextField {

        MyNumericTextField(String hint, Map<Object, UpdateField> parseIdMap, GetDouble getValue, PutDouble setValue) {
            super("", hint, COLUMNS_FOR_INT, TextArea.DECIMAL);
//                        super("", 1, columns, constraint);
//            setHint(hint);
            setGrowByContent(false);
            setAlignment(Component.RIGHT);
//            setSameWidth(new Label("9999"));
            setAutoDegradeMaxSize(true);
//            setGrowLimit(maxRows);
//            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
//            setMaxSize(maxTextSize);
            if (getValue.get() != 0) {
                this.setText(getValue.get() + "");
            }
//            this.set //TODO how to ensure cursor is positioned at end of entered text and not beginning?
            if (parseIdMap != null) {
                parseIdMap.put(this, () -> setValue.accept(getText().equals("") ? 0 : Double.valueOf(getText())));
            }
        }
    }

    class MyIntTextField extends TextField {

        int intMin = Integer.MIN_VALUE;
        int intMax = Integer.MAX_VALUE;
        int intDefault = 0;

        MyIntTextField(Integer initialValue, String hint, Integer intMin, Integer intMax, Integer defaultValue) {
            super("", hint, COLUMNS_FOR_INT, TextArea.NUMERIC);
            if (intMin != null) {
                this.intMin = intMin;
            }
            if (intMax != null) {
                this.intMax = intMax;
            }
            if (defaultValue != null) {
                this.intDefault = defaultValue;
            }
            setGrowByContent(false);
            setAutoDegradeMaxSize(true);
            if (initialValue != null) {
                this.setText(initialValue + "");
            }
//            this.set //TODO how to ensure cursor is positioned at end of entered text and not beginning?
        }

        public int getValue() {
            String str = getText();
            int interval = intDefault;
            if (str != null && str.length() > 0) {
                interval = Integer.parseInt(str);
                if (interval < intMin || interval > intMax) {
                    interval = intDefault;
                }
            }
            return interval;
        }

        MyIntTextField(String hint, Map<Object, UpdateField> parseIdMap, GetInt getValue, PutInt setValue, Integer intMin, Integer intMax, Integer defaultValue) {
//            super("", hint, COLUMNS_FOR_INT, TextArea.DECIMAL);
            this(getValue.get(), hint, intMin, intMax, defaultValue);
//            this.set //TODO how to ensure cursor is positioned at end of entered text and not beginning?
            parseIdMap.put(this, () -> setValue.accept(getValue()));
        }
    }

    static int COLUMNS_FOR_STRING = 20;

    class MyTextField extends TextField {

        MyTextField(String hint, Map<Object, UpdateField> parseIdMap, GetString getValue, PutString setValue) {
            this(hint, COLUMNS_FOR_STRING, TextArea.ANY, parseIdMap, getValue, setValue);
        }

        MyTextField(String hint, int columns, int constraint, Map<Object, UpdateField> parseIdMap, GetString getValue, PutString setValue) {
            this(hint, columns, 128, constraint, parseIdMap, getValue, setValue); //UI: 128 = default max size of a text field //TODO: make a preference or PRO feature
        }

        MyTextField(String hint, int columns, int maxTextSize, int constraint, Map<Object, UpdateField> parseIdMap, GetString getValue, PutString setValue) {
            this(hint, columns, maxTextSize, constraint, parseIdMap, getValue, setValue, TextField.LEFT);
        }

        MyTextField(String hint, int columns, int maxTextSize, int constraint, Map<Object, UpdateField> parseIdMap, GetString getValue, PutString setValue, int alignment) {
            super("", hint, columns, constraint);
            setAlignment(alignment);
            setGrowByContent(true);
            setAutoDegradeMaxSize(true);
//            setGrowLimit(maxRows);
            setHint(hint);
//            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
            setMaxSize(maxTextSize);
            setText(getValue.get());
            parseIdMap.put(this, () -> setValue.accept(getText()));
        }

    };

    class MyIntPicker extends Picker {

        int intMin = Integer.MIN_VALUE;
        int intMax = Integer.MAX_VALUE;
//        int intDefault = 0;

//                MyStringPicker(String[] stringArray, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set) {
        MyIntPicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set, int minValue, int maxValue) {
            this(parseIdMap, get, set, minValue, maxValue, 1);
        }

        MyIntPicker(Integer value, Integer minValue, Integer maxValue) {
            this(value, minValue, maxValue, 1);
        }

        MyIntPicker(Integer value, Integer minValue, Integer maxValue, Integer step) {
            super();
            if (minValue != null) {
                this.intMin = minValue;
            }
            if (maxValue != null) {
                this.intMax = maxValue;
            }
//            int defaultSelectedValue = get.get();
            assert minValue < maxValue && step != 0 && value >= minValue && value <= maxValue && (value - minValue) % step == 0 : "wrong init values";
            this.setType(Display.PICKER_TYPE_STRINGS);
            int i = minValue;
            int count = 0;
            String[] strings = new String[((maxValue - minValue) / step) + 1];
            while (i <= maxValue) {
                strings[count] = Integer.toString(i);
                count++;
                i += step;
            }

            this.setStrings(strings);
            this.setSelectedString(Integer.toString(value));
        }

        MyIntPicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set, int minValue, int maxValue, int step) {
            this(get.get(), minValue, maxValue, step);
            if (parseIdMap != null) {
                parseIdMap.put(this, () -> {
//                    String str = this.getSelectedString();
//                    set.accept(Integer.parseInt(str));
                    set.accept(getValueInt());
                });
            }
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyIntPicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set, int minValue, int maxValue, int step) {
//            super();
//            int defaultSelectedValue = get.get();
//            assert minValue < maxValue && step != 0 && defaultSelectedValue >= minValue && defaultSelectedValue <= maxValue && (defaultSelectedValue - minValue) % step == 0 : "wrong init values";
//            this.setType(Display.PICKER_TYPE_STRINGS);
//            int i = minValue;
//            int count = 0;
//            String[] strings = new String[((maxValue - minValue) / step) + 1];
//            while (i <= maxValue) {
//                strings[count] = Integer.toString(i);
//                count++;
//                i += step;
//            }
//
//            this.setStrings(strings);
////        this.setSelectedString(stringArray[get.get()]);
//            this.setSelectedString(Integer.toString(defaultSelectedValue));
//            if (parseIdMap != null) {
//                parseIdMap.put(this, () -> {
//                    String str = this.getSelectedString();
//                    set.accept(Integer.parseInt(str));
//                });
//            }
//        }
//</editor-fold>
        public int getValueInt() {
//            String str = getSelectedString();
//            int interval;
//            if (str != null && str.length() > 0) {
//                interval = Integer.parseInt(str);
//                if (interval < intMin || interval > intMax) {
//                    interval = intDefault;
//                }
//            }
            return Integer.parseInt(getSelectedString());
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static Component clearcreate(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set, boolean addClearButton) {
//        Button clearButton = new Button();
//
//        MyDurationPicker p = new MyDurationPicker(parseIdMap, get, set) {
//            @Override
//            public void setTime(int time) {
//                super.setTime(time); //To change body of generated methods, choose Tools | Templates.
//                if (clearButton != null) {
//                    clearButton.setHidden(getTime() == 0);
//                }
////                    for (Object al : MyDurationPicker.this.getListeners()) {
//                for (Object al : getListeners()) {
//                    if (al instanceof MyActionListener) {
//                        ((MyActionListener) al).actionPerformed(null);
//                    }
//                }
//            }
//        };
//
//        if (addClearButton) {
//            Command clear = Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//                p.setTime(0);
//            });
//            clearButton.setCommand(clear);
//            clearButton.setHidden(p.getTime() == 0);
////<editor-fold defaultstate="collapsed" desc="comment">
////listener to hide/show clear button when field is empty/not empty
////                addActionListener(new MyActionListener() {
////                    @Override
////                    public void actionPerformed(ActionEvent evt) {
////                        clearButton.setHidden(getTime() == 0);
////                    }
////                });
////</editor-fold>
//            return LayeredLayout.encloseIn(p, FlowLayout.encloseRightMiddle(clearButton));
//        }
//        return p;
//    }
//
//    static Component addClearButton(MyDurationPicker timePicker) {
//        Button clearButton = new Button();
//        timePicker.setClearButton(clearButton);
//        Command clear = Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            timePicker.setTime(0); //will hide the button
//            for (Object al : timePicker.getListeners()) {
//                if (al instanceof MyActionListener) {
//                    ((MyActionListener) al).actionPerformed(null);
//                }
//            }
//        });
//        clearButton.setCommand(clear);
//        clearButton.setHidden(timePicker.getTime() == 0); //always set since we don't know if Picker.setTime() is already called or will be called later
//        //listener to hide/show clear button when field is empty/not empty
//        timePicker.addActionListener(new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                clearButton.setHidden(timePicker.getTime() == 0);
//            }
//        });
//        return LayeredLayout.encloseIn(timePicker, FlowLayout.encloseRightMiddle(clearButton));
//    }
//
//    static Component addClearButton(MyDatePicker timePicker) {
//        Button clearButton = new Button();
//        timePicker.setClearButton(clearButton);
//        Command clear = Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            timePicker.setTime(0); //will hide the button
//            for (Object al : timePicker.getListeners()) {
//                if (al instanceof MyActionListener) {
//                    ((MyActionListener) al).actionPerformed(null);
//                }
//            }
//        });
//        clearButton.setCommand(clear);
//        clearButton.setHidden(timePicker.getTime() == 0); //always set since we don't know if Picker.setTime() is already called or will be called later
//        //listener to hide/show clear button when field is empty/not empty
//        timePicker.addActionListener(new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                clearButton.setHidden(timePicker.getTime() == 0);
//            }
//        });
//        return LayeredLayout.encloseIn(timePicker, FlowLayout.encloseRightMiddle(clearButton));
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    class MyCheckBox extends CheckBox {
//
//        MyCheckBox(Map<Object, UpdateField> parseIdMap, GetBoolean get, PutBoolean set) {
//            this(null, parseIdMap, get, set);
//        }
//
//        MyCheckBox(String title, Map<Object, UpdateField> parseIdMap, GetBoolean get, PutBoolean set) {
//            super();
//            if (title != null) {
//                this.setSelectCommandText(title);
//            }
//            Boolean b = get.get();
//            if (b != null) {
//                this.setSelected(b);
//            }
//            parseIdMap.put(this, () -> set.accept(this.isSelected()));
//        }
//    };
//</editor-fold>
    /**
     * returns list of all currently active menu commands (can then be restored
     * with setAllCommands)
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
     * adds commands in 'natural' order: first one is first in menu, second is
     * next, ... , last one goes on single softkey)
     *
     * @param command
     */
    public void addCommands(Command[] commands) {
//        for (int i=0, size=commands.length; i<size; i++) {
        for (int i = commands.length - 1; i >= 0; i--) { //add commands in reverse order
            addCommand(commands[i]);
        }
    }

    /**
     * will iterate over all the fields in parseIdMap and call the stored lambda
     * functions to update the corresponding fields in the edited ParseObject
     *
     * @param parseObject will save parseObject first if new object
     */
    protected static void putEditedValues2(Map<Object, UpdateField> parseIdMap2, ParseObject parseObject) {
        if (false && parseObject.getObjectIdP() == null) {
            DAO.getInstance().save(parseObject); //TODO!!! why is it necessary to save here??
        }
        putEditedValues2(parseIdMap2);
    }

    protected static void putEditedValues2(Map<Object, UpdateField> parseIdMap2) {
//            for (String parseId : parseIdMap2.keySet()) {
////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
//                parseIdMap.get(parseId).saveEditedValueInParseObject();
//            }
        ASSERT.that(parseIdMap2 != null);
        if (parseIdMap2 != null) {
            UpdateField repeatRule = parseIdMap2.remove(REPEAT_RULE_KEY); //set a repeatRule aside for execution last (after restoring all fields)
            for (Object parseId : parseIdMap2.keySet()) {
//            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
                parseIdMap2.get(parseId).update();
            }
            if (repeatRule != null) {
                repeatRule.update();
            }
        }
    }

    void setDoneUpdater(UpdateField updateActionOnDone) {
        this.updateActionOnDone = updateActionOnDone;
    }

    /**
     * used to refresh the screen after (major) edits. E.g. either revalidate if
     * only format has been changed or rebuild content pane if the data has been
     * changed beyond what updating a single container can help (e.g. if a
     * subtask was edited impating both the project and possibly the work time
     * of the entire list)
     */
    public void refreshAfterEdit() {
//        if (editFieldOnShowOrRefresh != null) { // && (testIfEdit == null || testIfEdit.test())) {
        if (inlineInsertContainer != null && inlineInsertContainer.getTextArea() != null) { // && (testIfEdit == null || testIfEdit.test())) {
//            editFieldOnShowOrRefresh.startEditingAsync();
            inlineInsertContainer.getTextArea().startEditingAsync();
        }
    }

//    abstract void refreshAfterEdit(KeepInSameScreenPosition keepPos);
//    {
////        revalidate();
//    }
    protected boolean isDragAndDropEnabled() {
        return false;
    }

//    public String getListAsCommaSeparatedString(Set<Category> setOrList) {
//    public static String getListAsCommaSeparatedString(List<Category> setOrList) {
    public static String getListAsCommaSeparatedString(List<ItemAndListCommonInterface> setOrList) {
        String str = "";
        String separator = "";
        if (setOrList != null) {
//            for (Category cat : setOrList) {
            for (ItemAndListCommonInterface itemCategoryOrList : setOrList) {
//                str = itemCategoryOrList.toString() + separator + str;
//                str = itemCategoryOrList.getText() + separator + str;
                str = str + separator + itemCategoryOrList.getText();
                separator = ", ";
            }
        }
        return str;
    }

    public String getDefaultIfStrEmpty(String str, String defaultStr) {
        if (str == null || str.equals("")) {
            return defaultStr;
        } else {
            return str;
        }
    }

    void showPreviousScreenOrDefault(boolean callRefreshAfterEdit) {
        showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit);
    }

    /**
     * will show the previous form (or the default Main form if previous form is
     * undefined)
     *
     * @param previousForm
     */
//    void showPreviousScreenOrDefault(MyForm previousForm) {
////        showPreviousScreenOrDefault(previousForm, true);
//        showPreviousScreenOrDefault(previousForm, false);
//    }
    static void showPreviousScreenOrDefault(MyForm previousForm, boolean callRefreshAfterEdit) {
        if (previousForm != null) {
            if (callRefreshAfterEdit) {
                previousForm.refreshAfterEdit();
            }
            previousForm.showBack();
        } else {
            new ScreenMain().show();
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public Command makeTimerCommand(String title, Image icon, ItemList orgItemList, FilterSortDef filterSortDef) {
////    public Command makeTimerCommand(String title, Image icon, ItemList orgItemList) {
//        return new Command(title, icon) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                new ScreenTimer(itemList, MyForm.this,
////                ScreenTimer.getInstance().setup(itemList, MyForm.this, () -> {});
////                ScreenTimer.getInstance().startTimerOnItemList(orgItemList, filterSortDef, MyForm.this);
//                ScreenTimer.getInstance().startTimerOnItemList(orgItemList, MyForm.this);
//            }
//        };
//    }
//</editor-fold>
    /**
     * default timerCommand, only shows the timer symbol
     */
//    public Command makeTimerCommand(ItemList itemList, FilterSortDef filterSortDef) {
////        return makeTimerCommand(null, Icons.iconTimerSymbolToolbarStyle, itemList, filterSortDef);
//        return makeTimerCommand(null, Icons.iconTimerSymbolToolbarStyle, itemList);
//    }
    public Command makeDoneUpdateWithParseIdMapCommand(boolean callRefreshAfterEdit) {
        return makeDoneUpdateWithParseIdMapCommand("", Icons.iconBackToPrevFormToolbarStyle(), callRefreshAfterEdit);
    }

    public Command makeDoneUpdateWithParseIdMapCommand() {
        return makeDoneUpdateWithParseIdMapCommand("", Icons.iconBackToPrevFormToolbarStyle(), false); //default false since otherwise edited values will be lost
    }

//    public Command makeDoneUpdateWithParseIdMapCommand(String title, Image icon) {
//        return makeDoneUpdateWithParseIdMapCommand(title, icon, true);
//    }
    public Command makeDoneUpdateWithParseIdMapCommand(String title, Image icon, boolean callRefreshAfterEdit) {
        Command cmd = new Command(title, icon) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String errorMsg;
                if (checkDataIsCompleteBeforeExit == null || (errorMsg = checkDataIsCompleteBeforeExit.check()) == null) {
                    putEditedValues2(parseIdMap2);
                    updateActionOnDone.update();
//                previousForm.refreshAfterEdit();
//                previousForm.showBack();
                    showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit);
                } else {
                    Dialog.show("INFO", errorMsg, "OK", null);
                }
            }
        };
        cmd.putClientProperty("android:showAsAction", "withText");
        return cmd;
    }

    public Command makeDoneUpdateWithParseIdMapCommand(boolean callRefreshAfterEdit, GetBoolean canGoBack, String errorMsg) {
        return makeDoneUpdateWithParseIdMapCommand("", Icons.iconBackToPrevFormToolbarStyle(), callRefreshAfterEdit, canGoBack, errorMsg);
    }

    public Command makeDoneUpdateWithParseIdMapCommand(String title, Image icon, boolean callRefreshAfterEdit, GetBoolean canGoBack, String errorMsg) {
        Command cmd = new Command(title, icon) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (canGoBack.get()) {
                    String errorMsg;
                    if (checkDataIsCompleteBeforeExit == null || (errorMsg = checkDataIsCompleteBeforeExit.check()) == null) {
                        putEditedValues2(parseIdMap2);
                        updateActionOnDone.update();
//                previousForm.refreshAfterEdit();
//                previousForm.showBack();
                        showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit);
                    } else {
                        Dialog.show("INFO", errorMsg, "OK", null);
                    }
                } else {
                    Dialog.show("Error", errorMsg, "OK", null);
                }
            }
        };
        cmd.putClientProperty("android:showAsAction", "withText");
        return cmd;
    }

    public Command makeDoneCommandWithNoUpdate() {
        Command cmd = new Command("", Icons.iconBackToPrevFormToolbarStyle()) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                previousForm.refreshAfterEdit();
//                previousForm.showBack();
                showPreviousScreenOrDefault(previousForm, false);
            }
        };
        cmd.putClientProperty("android:showAsAction", "withText");
        return cmd;
    }

    public Command makeCancelCommand() {
        return makeCancelCommand("Cancel", null);
    }

    private Command makeCancelCommand(String title, Image icon) {
        Command cmd = new Command(title, icon) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//            Log.p("Clicked");
//            item.refresh(); or item.clear()?? //see here: https://www.parse.com/questions/make-a-copy-of-a-pfobject, revert(); //forgetChanges***/refresh, notably categories
//            previousForm.showBack(); //drop any changes
//                previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//                previousForm.showBack(); //drop any changes
                showPreviousScreenOrDefault(previousForm, false);

            }
        };
        cmd.putClientProperty("android:showAsAction", "withText");
        return cmd;
    }

    public Command makeInterruptCommand() {
        return makeInterruptCommand("", Icons.iconInterruptToolbarStyle()); //"Interrupt", "New Interrupt"
    }

    public Command makeInterruptCommand(String title, Image icon) {
        //TODO only make interrupt task creation available in Timer (where it really interrupts something)?? There is [+] for 'normal' task creation elsewhere... Actually, 'Interrupt' should be sth like 'InstantTimedTask'
        //TODO implement longPress to start Interrupt *without* starting the timer (does it make sense? isn't it the same as [+] to add new task?)
        return MyReplayCommand.create("StartTimerFromMyForm", title, icon, (e) -> {
            Item item = new Item();
//                if (ScreenTimerNew.getInstance().isTimerRunning()) {
//                    item.setInteruptTask(true); //UI: automatically mark as Interrupt task if timer is already running. TODO is this right behavior?? Should all Interrupt tasks be marked as such or only when using timer?? Only when using Timer, otherwise just an 'instant task'
//                    item.setTaskInterrupted(ScreenTimer.getInstance().getTimedItem());
//                }
//                ScreenTimer.getInstance().startTimer(item, MyForm.this);
            ScreenTimer.getInstance().startInterrupt(item, MyForm.this); //TODO!!! verify that item is always saved (within Timer, upon Done/Exit/ExitApp
            //TODO Allow to pick a common (predefined/template) interrupt task (long-press??)
            //Open it up in editing mode with timer running
//            setupTimerForItem(item, 0);
            //Upon Done/Stop, save and return to previous task
        }
        );
    }

    public Command newItemSaveToInboxCmd() {

        Command cmd = MyReplayCommand.create("CreateNewItem", "", Icons.iconNewTaskToolbarStyle(), (e) -> {
            Item item = new Item();
//                addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg); //necessary to add to owner when creating repeatInstances (item will be added to itemListOrg upon acceptance/exit from screen)
            setKeepPos(new KeepInSameScreenPosition());
            new ScreenItem(item, (MyForm) getComponentForm(), () -> {
                if (item.hasSaveableData() || Dialog.show("INFO", "No key data in this task, save anyway?", "Save", "Don't save")) {
                    //TODO!!!! this test is not in the right place - it should be tested inside ScreenItem before exiting
                    //only save if data (don't save if no relevant data)
                    if (true) {
                        //TODO!!! save directly to Inbox
//                            addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg);
                    }
                    DAO.getInstance().save(item); //must save item since adding it to itemListOrg changes its owner
                    refreshAfterEdit(); //TODO!!! scroll to where the new item was added (either beginning or end of list)
//                    }
                } else {
                    //if no saveable data, do nothing
//                        itemListOrg.removeFromList(item); //if no saveable data, undo the 
                    //TODO!!!! how to remove from eg Categories if finally the task is not saved??
                }
            }, false).show(); //false=optionTemplateEditMode
        });
        return cmd;
    }

    public static Button makeAddTimeStampToCommentAndStartEditing(TextArea comment) {
        //TODO only make interrupt task creation available in Timer (where it really interrupts something)?? There is [+] for 'normal' task creation elsewhere... Actually, 'Interrupt' should be sth like 'InstantTimedTask'
        //TODO implement longPress to start Interrupt *without* starting the timer (does it make sense? isn't it the same as [+] to add new task?)
        return new Button(Command.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {
            comment.setText(Item.addTimeToComment(comment.getText()));
//                    comment.setstartEditing(); //TODO how to position cursor at end of text (if not done automatically)?
//comment.setCursor //only on TextField, not TextArea
//            comment.startEditing(); //TODO in CN bug db #1827: start using startEditAsync() is a better approach
            comment.startEditingAsync();//TODO in CN bug db #1827: start using startEditAsync() is a better approach
        }));
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void restartScreenXXX() {
//        //TODO
//        //TODO store info on current screen when calling stop()??!!
//        //TODO restart screen with same lists expanded
//        //TODO restart a screen with the same placement in the list
//        Item item = null;
//        ItemList itemList = null;
//        FilterSortDef filter = null;
//        String screenName = null; //read from local storage
//        switch (screenName) {
//            case ScreenTimer.SCREEN_TITLE:
//                break;
//            case ScreenListOfItems.SCREEN_ID:
//                new ScreenListOfItems(itemList, null, null).show();
//                break;
//            default:
//                new ScreenMain().show();
//                return;
//        }
//    }
//</editor-fold>
    final static int TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS = 80; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s
    final static int ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR = 500; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s

//    protected static SpanButton addHelp(SpanButton comp, String helpText) {
    protected static Component addHelp(Component comp, String helpText) {
        if (helpText == null || helpText.length() == 0) {
            return comp;
        }
        ActionListener al = (e) -> {
//            ToastBar.Status status = ToastBar.getInstance().createStatus().setMessage(text);
            ToastBar.Status status = ToastBar.getInstance().createStatus();
            status.setMessage(helpText);
            //status.setExpires(3000);
//                final int TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS = 65; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s
//            status.setExpires(status.getMessage().length() * TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS + ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR);
            status.setExpires(helpText.length() * TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS + ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR);
            status.show();
        };
//        comp.setUIID("Label"); //CN1 Support: this not working for SpanButton??
        if (comp instanceof SpanButton) {
            ((SpanButton) comp).addActionListener(al);
        } else if (comp instanceof Button) {
            ((Button) comp).addActionListener(al);
        } else {
            assert false : "Unknown type of help button, comp=" + comp;
        }
        return comp;
    }

    protected static Component makeHelpButton(String label, String helpText) {
        return makeHelpButton(label, helpText, true);
    }

    protected static Component makeHelpButton(String label, String helpText, boolean makeSpanButton) {
        if (makeSpanButton) {
//            SpanButton spanB = new SpanButton(label, "LabelField");
            SpanButton spanB = new SpanButton(label, "LabelField");
//            spanB.setTextUIID("LabelField"); //already done in constructor new SpanButton(label, "LabelField")
//            spanB.setUIID("LabelField");
            spanB.setUIID("Container"); //avoid adding additional white space by setting the Container UIID to LabelField

//            spanB.setTextUIID("SpanButtonTextAreaFixedLeft"); //already done in constructor new SpanButton(label, "LabelField")
//            spanB.setUIID("LabelField"); //already done in constructor new SpanButton(label, "LabelField")
            return addHelp(spanB, helpText);
        } else {
            return addHelp(new Button(label, "LabelField"), helpText);
        }
    }

//    protected static Component layout(String fieldLabelTxt, Component field) {
//        return layout(fieldLabelTxt, field, null);
//    }
    protected static Component layout(String fieldLabelTxt, Component field, boolean checkForTooLargeWidth) {
        return layout(fieldLabelTxt, field, null, null, checkForTooLargeWidth, false, true);
    }

    protected static Component layout(String fieldLabelTxt, Component field, String help) {
        return layout(fieldLabelTxt, field, help, field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue() : null, true, false, true);
    }

    protected static Component layout(String fieldLabelTxt, Component field, String help, boolean wrapText) {
        return layout(fieldLabelTxt, field, help, field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue() : null, wrapText, true, true);
    }

//    protected static Component layout(String fieldLabelTxt, Component field, String help, boolean wrapText, boolean makeFieldUneditable) {
//        return layout(fieldLabelTxt, field, help, null, wrapText, makeFieldUneditable, true);
//    }
    protected static Component layout(String fieldLabelTxt, Component field, String help, boolean wrapText, boolean makeFieldUneditable, boolean hideEditButton) {
//        return layout(fieldLabelTxt, field, help, (field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue(): null), wrapText, makeFieldUneditable, hideEditButton);
        return layout(fieldLabelTxt, field, help, null, wrapText, makeFieldUneditable, hideEditButton);
    }

//    protected static Component layout(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear) {
//        return layout(fieldLabelTxt, field, help, swipeClear, true, false, false);
//    }
    protected static Component layout(String fieldLabelTxt, MyDateAndTimePicker field, String help) {
        return layout(fieldLabelTxt, field, help, () -> field.swipeClear(), true, false, false);
    }

    protected static Component layout(String fieldLabelTxt, MyDatePicker field, String help) {
        return layout(fieldLabelTxt, field, help, () -> field.swipeClear(), true, false, false);
    }

    protected static Component layout(String fieldLabelTxt, MyDurationPicker field, String help) {
        return layout(fieldLabelTxt, field, help, () -> field.swipeClear(), true, false, false);
    }

//    protected static Component layout(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear, boolean wrapText) {
//        return layout(fieldLabelTxt, field, help, swipeClear, wrapText, false, false);
//    }
    /**
     *
     * @param fieldLabelTxt
     * @param group
     * @param help
     * @param swipeClear if defined (non-null) will be added as a swipe function
     * @param wrapText check if field text and field together become larger than
     * form width and make as a spanButton if so
     * @param makeFieldUneditable show formatted as uneditable, no swipe delete,
     * no edit button (but space left button to align with other fields)
     * @param hideEditButton no edit button [>] and no space left button to
     * align with other fields)
     * @return
     */
    protected static Component layoutN(String fieldLabelTxt, MyComponentGroup group, String help) {
        return layoutN(fieldLabelTxt, group, help, null, true, false, false, true);
    }

    protected static Component layoutN(String fieldLabelTxt, Picker field, String help) {
        return layoutN(fieldLabelTxt, field, help, field instanceof MyDurationPicker ? (() -> ((MyDurationPicker) field).swipeClear())
                : (field instanceof MyDatePicker ? () -> ((MyDatePicker) field).swipeClear() : () -> ((MyDateAndTimePicker) field).swipeClear()),
                true, false, false, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Picker field, String help,
            boolean wrapText, boolean makeFieldUneditable, boolean hideEditButton) {
        return layout(fieldLabelTxt, field, help,
                field instanceof MyDurationPicker ? (() -> ((MyDurationPicker) field).swipeClear())
                        : field instanceof MyDatePicker ? () -> ((MyDatePicker) field).swipeClear()
                                : () -> ((MyDateAndTimePicker) field).swipeClear(),
                wrapText, makeFieldUneditable, hideEditButton, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear) {
        return layoutN(fieldLabelTxt, field, help, swipeClear, true, false, true, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help) { //normal edit field with [>]
        return layoutN(fieldLabelTxt, field, help, null, true, false, true, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, boolean showAsFieldUneditable) {
//        return layoutN(fieldLabelTxt, field, help, null, true, showAsFieldUneditable, false, false);
        return layoutN(fieldLabelTxt, field, help, null, true, showAsFieldUneditable, !showAsFieldUneditable, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton) {
        return layoutN(fieldLabelTxt, field, help, null, wrapText, showAsFieldUneditable, visibleEditButton, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton) {
        return layoutN(fieldLabelTxt, field, help, swipeClear, wrapText, showAsFieldUneditable, visibleEditButton, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton) {
        if (field instanceof OnOffSwitch | field instanceof MyOnOffSwitch) {
//            field.getAllStyles().setPaddingRight(6);
        } else {
            if (field instanceof WrapButton) {
//                ((SpanButton) field).setTextUIID(showAsUneditableField ? "LabelFixed" : "SpanButtonTextAreaValueRight");
                ((WrapButton) field).setTextUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
                ((WrapButton) field).setUIID("Container");
            } else {//if (!(field instanceof MyComponentGroup)) {
                field.setUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
            }
        }

        //EDIT FIELD
        Component visibleField = null; //contains the edit field and possibly the edit button
//        if (hideEditButton) {
        if (!visibleEditButton && hiddenEditButton) {
            visibleField = field;
        } else { //place a visible or invisible button
            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
//            boolean editButtonHidden = makeFieldUneditable || hideEditButton; //invisible if uneditable or if explicitly make invisible
//            editFieldButton.setVisible(!editButtonInvisible);
//            editFieldButton.setVisible(!showAsFieldUneditable || visibleEditButton); //Visible, but still using space
            editFieldButton.setVisible(!showAsFieldUneditable || visibleEditButton); //Visible, but still using space
//            editFieldButton.setVisible(!hideEditButton);
            editFieldButton.setHidden(hiddenEditButton); //hidden, not taking any space
//<editor-fold defaultstate="collapsed" desc="comment">
//            visibleField = FlowLayout.encloseRightMiddle(field, editFieldButton);
//            visibleField = BoxLayout.encloseXNoGrow(field, editFieldButton);
//            visibleField = BoxLayout.encloseX(field, editFieldButton);
//            visibleField = BorderLayout.centerEastWest(null, field, editFieldButton);
//</editor-fold>
            visibleField = BorderLayout.centerEastWest(field, editFieldButton, null);
            if (field instanceof WrapButton) {
                ((Container) visibleField).setLeadComponent(((WrapButton) field).getActualButton());
            } else {
                ((Container) visibleField).setLeadComponent(field);
            }
        }

        Container fieldContainer = new Container(new BorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);

        //SWIPE CLEAR
        if (swipeClear != null) { //ADD SWIPE to delete
            SwipeableContainer swipeCont;
            assert !showAsFieldUneditable : "showAsUneditableField should never be true if we also define a swipeClear function";
            Button swipeDeleteFieldButton = new Button();
            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
            ActionListener l = (ev) -> {
                swipeClear.clearFieldValue();
                fieldContainer.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
                swipeCont.close();
            };
            swipeCont.addSwipeOpenListener(l);
            swipeDeleteFieldButton.setCommand(Command.create("", Icons.iconCloseCircleLabelStyle, l));
            visibleField = swipeCont;
        }

        //FIELD LABEL
        Component fieldLabel = makeHelpButton(fieldLabelTxt, help, wrapText);
        if (wrapText) {
            int availDisplWidth = (Display.getInstance().getDisplayWidth() * 90) / 100; //asumme roughly 90% of width is available after margins
//            int availDisplWidthParent = getPaDisplay.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
            int labelPreferredW = fieldLabel.getPreferredW();
            int fieldPreferredW = visibleField.getPreferredW();
            if (labelPreferredW + fieldPreferredW > availDisplWidth) { //if too wide
                if (field instanceof MyComponentGroup) { //MyComponentGroups cannot wrap and must be shown fully so split on *two* lines
                    fieldContainer.add(BorderLayout.NORTH, fieldLabel);
                    fieldContainer.add(BorderLayout.EAST, visibleField);
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
                fieldContainer.add(BorderLayout.WEST, fieldLabel);
                fieldContainer.add(BorderLayout.EAST, visibleField);
            }
        } else {
            fieldContainer.add(BorderLayout.WEST, fieldLabel);
            fieldContainer.add(BorderLayout.EAST, visibleField);
        }
        fieldContainer.revalidate(); //right way to get the full text to size up?
        return fieldContainer;
    }

    protected static Component layout(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean makeFieldUneditable, boolean hideEditButton) {
        return layout(fieldLabelTxt, field, help, swipeClear, wrapText, makeFieldUneditable, hideEditButton, false);
    }

    protected static Component layout(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean makeFieldUneditable, boolean hideEditButton, boolean forceVisibleEditButton) {

        if (field instanceof OnOffSwitch | field instanceof MyOnOffSwitch) {
//            field.getAllStyles().setPaddingRight(6);
        } else {
            if (field instanceof WrapButton) {
//                ((SpanButton) field).setTextUIID(showAsUneditableField ? "LabelFixed" : "SpanButtonTextAreaValueRight");
                ((WrapButton) field).setTextUIID(makeFieldUneditable ? "LabelFixed" : "LabelValue");
                ((WrapButton) field).setUIID("Container");
            } else {
                field.setUIID(makeFieldUneditable ? "LabelFixed" : "LabelValue");
            }
        }

        //EDIT FIELD
        Component visibleField = null; //contains the edit field and possibly the edit button
        if (hideEditButton) {
            visibleField = field;
        } else { //place a visible or invisible button
            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
            boolean editButtonHidden = makeFieldUneditable || hideEditButton; //invisible if uneditable or if explicitly make invisible
//            editFieldButton.setVisible(!editButtonInvisible);
            editFieldButton.setVisible(!(makeFieldUneditable || hideEditButton) || forceVisibleEditButton); //Visible, but still using space
//            editFieldButton.setVisible(!hideEditButton);
            editFieldButton.setHidden(editButtonHidden || !forceVisibleEditButton); //hidden, not taking any space
//<editor-fold defaultstate="collapsed" desc="comment">
//            visibleField = FlowLayout.encloseRightMiddle(field, editFieldButton);
//            visibleField = BoxLayout.encloseXNoGrow(field, editFieldButton);
//            visibleField = BoxLayout.encloseX(field, editFieldButton);
//            visibleField = BorderLayout.centerEastWest(null, field, editFieldButton);
//</editor-fold>
            visibleField = BorderLayout.centerEastWest(field, editFieldButton, null);
            if (field instanceof WrapButton) {
                ((Container) visibleField).setLeadComponent(((WrapButton) field).getActualButton());
            } else {
                ((Container) visibleField).setLeadComponent(field);
            }
        }

        Container fieldContainer = new Container(new BorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);

        //SWIPE CLEAR
        if (swipeClear != null) { //ADD SWIPE to delete
            SwipeableContainer swipeCont;
            assert !makeFieldUneditable : "showAsUneditableField should never be true if we also define a swipeClear fucntion";
            Button swipeDeleteFieldButton = new Button();
            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
            ActionListener l = (ev) -> {
                swipeClear.clearFieldValue();
                fieldContainer.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
                swipeCont.close();
            };
            swipeCont.addSwipeOpenListener(l);
            swipeDeleteFieldButton.setCommand(Command.create("", Icons.iconCloseCircleLabelStyle, l));
            visibleField = swipeCont;
        }

        //FIELD LABEL
        Component fieldLabel = makeHelpButton(fieldLabelTxt, help, wrapText);
        if (wrapText) {
            int availDisplWidth = Display.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
//            int availDisplWidthParent = getPaDisplay.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
            int labelPreferredW = fieldLabel.getPreferredW();
            int fieldPreferredW = visibleField.getPreferredW();
            if (labelPreferredW + fieldPreferredW > availDisplWidth) { //if too wide
                if (field instanceof MyComponentGroup) { //MyComponentGroups cannot wrap and must be shown fully so split on *two* lines
                    fieldContainer.add(BorderLayout.NORTH, fieldLabel);
                    fieldContainer.add(BorderLayout.EAST, visibleField);
                } else {
                    int widthFirstColumn = 0;
                    int relativeWidthFieldPercent = fieldPreferredW * 100 / availDisplWidth;
                    int relativeWidthLabelPercent = labelPreferredW * 100 / availDisplWidth;
                    if (relativeWidthFieldPercent < 30) { //visibleField takes up less than 30% of avail space 
//                        int widthFirstColumn = Math.min(Math.max(fieldLabelPreferredW / visibleFieldPreferredW * 100, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
                        widthFirstColumn = 100 - relativeWidthFieldPercent; //first column gets the rest
                    }
                    if (relativeWidthLabelPercent < 45 && fieldLabelTxt.indexOf(" ") == -1) { //label takes up less than 45% of avail space and no spaces (no wrap)
                        widthFirstColumn = relativeWidthLabelPercent; //give it full space (no wrap)
                    } else {
//                        int widthVisibleFieldPercent = 100 - widthFieldLabelPercent;
                        int widthLabelPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
                        widthFirstColumn = Math.min(Math.max(widthLabelPercent, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
                    }
                    TableLayout tl = new TableLayout(1, 2);
                    tl.setGrowHorizontally(true); //grow the remaining right-most column
//                fieldContainer = new Container(tl);
                    fieldContainer.setLayout(tl);
                    fieldContainer.
                            add(tl.createConstraint().widthPercentage(widthFirstColumn), fieldLabel).
                            //                            add(visibleField);
                            add(tl.createConstraint().verticalAlign(Component.CENTER).horizontalAlign(Component.RIGHT), visibleField); //align center right
//<editor-fold defaultstate="collapsed" desc="comment">
//                fieldLabel.getAllStyles().setMarginBottom(0);
//                fieldLabel.getAllStyles().setPaddingBottom(0); //reduce space between label and field
//                visibleField.getAllStyles().setMarginBottom(0);
//                visibleField.getAllStyles().setPaddingTop(0);
//                fieldContainer.add(BorderLayout.NORTH, fieldLabel);
//                fieldContainer.add(BorderLayout.EAST, visibleField); //label NORTH
//</editor-fold>
                }
            } else {
                fieldContainer.add(BorderLayout.WEST, fieldLabel);
                fieldContainer.add(BorderLayout.EAST, visibleField);  //label WEST
            }
        } else {
            fieldContainer.add(BorderLayout.WEST, fieldLabel);
            fieldContainer.add(BorderLayout.EAST, visibleField);
        }

        return fieldContainer;
    }

    protected static Component layoutOLD4(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear, boolean checkForTooLargeWidth, boolean showAsUneditableField, boolean noEditButton) {

        if (field instanceof OnOffSwitch) {
            field.getAllStyles().setPaddingRight(6);
        } else {
            field.setUIID(showAsUneditableField ? "LabelFixed" : "LabelValue");
            if (field instanceof SpanButton) {
                ((SpanButton) field).setTextUIID(showAsUneditableField ? "LabelFixed" : "SpanButtonTextAreaValueRight");
            }
        }

        //EDIT FIELD
        Component visibleField = null; //contains the edit field and possibly the edit button
        if (noEditButton) {
            visibleField = field;
        } else { //place a visible or invisible button
            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
            editFieldButton.setVisible(!showAsUneditableField && !noEditButton);
//            visibleField = FlowLayout.encloseRightMiddle(field, editFieldButton);
//            visibleField = BoxLayout.encloseXNoGrow(field, editFieldButton);
            visibleField = BoxLayout.encloseX(field, editFieldButton);
//BoxLayout box = new BoxLayout(BoxLayout.X_AXIS);
//box.
//            visibleField = BorderLayout.centerEastWest(null, field, editFieldButton);
            ((Container) visibleField).setLeadComponent(field);
        }

        Container fieldContainer = new Container(new BorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);

        //SWIPE CLEAR
        if (swipeClear != null) { //ADD SWIPE to delete
            SwipeableContainer swipeCont;
            assert !showAsUneditableField : "showAsUneditableField should never be true if we also define a swipeClear fucntion";
            Button swipeDeleteFieldButton = new Button();
            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
            swipeDeleteFieldButton.setCommand(Command.create("", Icons.iconCloseCircleLabelStyle, (ev) -> {
                swipeClear.clearFieldValue();
                fieldContainer.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
                swipeCont.close();
            }));
            visibleField = swipeCont;
        }

        //FIELD LABEL
        Component fieldLabel = makeHelpButton(fieldLabelTxt, help, checkForTooLargeWidth);
        if (checkForTooLargeWidth) {
            int availDisplWidth = Display.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
            int fieldLabelPreferredW = fieldLabel.getPreferredW();
            int visibleFieldPreferredW = visibleField.getPreferredW();
            if (fieldLabelPreferredW + visibleFieldPreferredW > availDisplWidth) { //if too wide
                fieldLabel.getAllStyles().setMarginBottom(0);
                fieldLabel.getAllStyles().setPaddingBottom(0); //reduce space between label and field
                visibleField.getAllStyles().setMarginBottom(0);
                visibleField.getAllStyles().setPaddingTop(0);
                fieldContainer.add(BorderLayout.NORTH, fieldLabel);
                fieldContainer.add(BorderLayout.EAST, visibleField); //label NORTH
            } else {
                fieldContainer.add(BorderLayout.WEST, fieldLabel);
                fieldContainer.add(BorderLayout.EAST, visibleField);  //label WEST
            }
        } else {
            fieldContainer.add(BorderLayout.WEST, fieldLabel);
            fieldContainer.add(BorderLayout.EAST, visibleField);
        }

        return fieldContainer;
    }

    protected static Component layoutOLD3(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear, boolean checkForTooLargeWidth, boolean showAsUneditableField, boolean noButton) {
//        return BorderLayout.center(addHelp(new Button(label), help)).add(BorderLayout.EAST, field);
//        new SwipeClearContainer(BoxLayout.encloseXNoGrow(field), swipeClear);
        boolean labelAndFieldOnSeparateLines = false;

        if (field instanceof OnOffSwitch) {
            field.getAllStyles().setPaddingRight(6);
        } else {
            field.setUIID(showAsUneditableField ? "LabelFixed" : "LabelValue");
            if (field instanceof SpanButton) {
//                ((SpanButton) field).setUIID(showAsUneditableField ? "LabelFixed" : "LabelValue");
                ((SpanButton) field).setTextUIID(showAsUneditableField ? "LabelFixed" : "SpanButtonTextAreaValueRight");
            }
        }

//        Container fieldContainer = null; //contains the edit field and possibly the edit button
        Component visibleField = null; //contains the edit field and possibly the edit button
        if (noButton) {
            visibleField = field;
        } else { //place a visible or invisible button
//<editor-fold defaultstate="collapsed" desc="comment">
//            Label editFieldButton = null;
//Define [>] button
//            editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "LabelValue"); // [>]
//</editor-fold>
            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
            editFieldButton.setVisible(!showAsUneditableField && !noButton);
//<editor-fold defaultstate="collapsed" desc="comment">
//            editFieldIcon.setHidden(noVisibleEditButton);
//            editFieldButton.getAllStyles().setMarginLeft(0);
//            editFieldButton.getAllStyles().setPaddingLeft(0); //TODO move this formatting to theme
//            fieldValueEdit = FlowLayout.encloseIn(swipeCont, editFieldIcon);
//            fieldValueEdit = FlowLayout.encloseRightMiddle(swipeCont, editFieldIcon);
//            visibleField = BoxLayout.encloseXNoGrow(field, editFieldButton); //keeps left position as size of content varies
//</editor-fold>
            visibleField = FlowLayout.encloseRightMiddle(field, editFieldButton);
//            ((Container) fieldContainer).setLeadComponent(field);
            ((Container) visibleField).setLeadComponent(field);
        }

        Container l = new Container(new BorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);

        //SWIPE CLEAR
//        SwipeableContainer fieldCont = new SwipeableContainer(null, BoxLayout.encloseXNoGrow(deleteFieldButton), field);
        if (swipeClear != null) { //ADD SWIPE to delete
            SwipeableContainer swipeCont;
            assert !showAsUneditableField : "showAsUneditableField should never be true if we also define a swipeClear fucntion";
            Button swipeDeleteFieldButton = new Button();
//        SwipeableContainer fieldCont = new SwipeableContainer(null, deleteFieldButton, field);
            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
//            swipeDeleteFieldButton.setCommand(Command.create("Delete", Icons.iconCloseCircleLabelStyle, (ev) -> {
            swipeDeleteFieldButton.setCommand(Command.create("", Icons.iconCloseCircleLabelStyle, (ev) -> {
                swipeClear.clearFieldValue();
//<editor-fold defaultstate="collapsed" desc="comment">
//                field.repaint(); //??
//                visibleField.repaint(); //??
//                swipeCont.getComponentAt(1).repaint();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
//                ((Container)swipeCont.getComponentAt(1)).revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
//</editor-fold>
                l.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
                swipeCont.close();
//                field.repaint(); //??
//<editor-fold defaultstate="collapsed" desc="comment">
//            cont.repaint();
//            this.getParent().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field? NO, shows Delete button on top of <set>
//            repaint();
//            getComponentForm().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field?
//</editor-fold>
            }));
            visibleField = swipeCont;
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        Container l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, FlowLayout.encloseCenterMiddle(field));
//        Container fieldValueEdit = FlowLayout.encloseCenter(fieldCont, new Label(Icons.iconEditSymbolLabelStyle, "LabelValue"));
//        Container fieldValueEdit = FlowLayout.encloseCenter(fieldCont, editFieldIcon);
//</editor-fold>
        Component fieldLabel;
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (checkForTooLargeWidth) {
//        } else {
//            fieldLabel = makeHelpButton(fieldLabelTxt, help, !checkForTooLargeWidth);
//        }
//</editor-fold>
        fieldLabel = makeHelpButton(fieldLabelTxt, help, checkForTooLargeWidth);

        if (checkForTooLargeWidth) {
//            l.revalidate();
//            if (fieldLabel.getWidth() < fieldLabel.getPreferredW() || fieldValueEdit.getWidth() < fieldValueEdit.getPreferredW()) { //if either of the fields got less width than needed
            int availDisplWidth = Display.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
            int fieldLabelPreferredW = fieldLabel.getPreferredW();
            int visibleFieldPreferredW = visibleField.getPreferredW();
            if (fieldLabelPreferredW + visibleFieldPreferredW > availDisplWidth) { //if too wide
                fieldLabel.getAllStyles().setMarginBottom(0);
                fieldLabel.getAllStyles().setPaddingBottom(0); //reduce space between label and field
                visibleField.getAllStyles().setMarginBottom(0);
                visibleField.getAllStyles().setPaddingTop(0);
//<editor-fold defaultstate="collapsed" desc="comment">
//                l.removeComponent(fieldLabel); //
////                l = BorderLayout.north(fieldLabel).add(BorderLayout.EAST, fieldValueEdit);
//                l.add(BorderLayout.NORTH, fieldLabel);
//                l = BorderLayout.north(fieldLabel).add(BorderLayout.EAST, visibleField); //label NORTH
//</editor-fold>
                l.add(BorderLayout.NORTH, fieldLabel);
                l.add(BorderLayout.EAST, visibleField); //label NORTH
            } else //            l = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);  //label WEST
            {
//                l = BorderLayout.west(fieldLabel).add(BorderLayout.EAST, visibleField);  //label WEST
                l.add(BorderLayout.WEST, fieldLabel);
                l.add(BorderLayout.EAST, visibleField);  //label WEST
            }//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
////    int availDisplWidth = Display.getInstance().getDisplayWidth() * 9 / 10; //asumme roughly 90% of width is available after margins
////    int fieldLabelPreferredW = fieldLabel.getPreferredW();
////    int fieldValueEditPreferredW = fieldValueEdit.getPreferredW();
////    if (fieldLabelPreferredW + fieldValueEditPreferredW > availDisplWidth) { //if too wide
////{
////            } else {
////            }
//                if (false && checkForTooLargeWidth) {
//                    if (l.getPreferredW() > Display.getInstance().getDisplayWidth()) { //if too wide, put label in North (so it becomes two separate lines)
//                        if (field instanceof TextArea || field instanceof TextField) {
////                    field.setSameWidth(d);
//                            Dimension d = field.getPreferredSize();
//                            d.setWidth(Display.getInstance().getDisplayWidth() / 2); //allow a textField to take up mox half the screen size.
//                            field.setPreferredSize(d); //TODO!!! CN1 Support: setPreferredSize deprecated, but what is right way then??
//                        } else {
////                    l.removeAll(); //remove already added components to avoid getting an error about adding twice
//                            field.getParent().removeComponent(field); //remove already added components to avoid getting an error about adding twice
//                            l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, field);
//                        }
//                    }
//                }
//
//                if (false) {
//                    if (labelAndFieldOnSeparateLines) {
////                        l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, fieldContainer);
//                    } else {
////                        l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.SOUTH, fieldContainer);
//                    }
//                }
//            }
//</editor-fold>
        } else {
//            l = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
//            l = BorderLayout.west(fieldLabel).add(BorderLayout.EAST, visibleField);
            l.add(BorderLayout.WEST, fieldLabel);
            l.add(BorderLayout.EAST, visibleField);
        }

//        return BorderLayout.center(helpBut(label, help)).add(BorderLayout.EAST, field);
        return l;
    }

    protected static Component layoutOLD2(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear, boolean checkForTooLargeWidth, boolean showAsUneditableField, boolean noVisibleEditButton) {
//        return BorderLayout.center(addHelp(new Button(label), help)).add(BorderLayout.EAST, field);
//        new SwipeClearContainer(BoxLayout.encloseXNoGrow(field), swipeClear);
        boolean labelAndFieldOnSeparateLines = false;

        field.setUIID(showAsUneditableField ? "LabelFixed" : "LabelValue");

        Label editFieldIcon = null;
        if (!noVisibleEditButton) {
            //Define [>] button
            editFieldIcon = new Label(Icons.iconEditSymbolLabelStyle, "LabelValue"); // [>]
            editFieldIcon.setVisible(!showAsUneditableField && !noVisibleEditButton);
//            editFieldIcon.setHidden(noVisibleEditButton);
            editFieldIcon.getAllStyles().setMarginLeft(0);
            editFieldIcon.getAllStyles().setPaddingLeft(0); //TODO move this formatting to theme
        }

//        SwipeableContainer fieldCont = new SwipeableContainer(null, BoxLayout.encloseXNoGrow(deleteFieldButton), field);
        Component fieldContainer = null;
        SwipeableContainer swipeCont;
        if (swipeClear != null) { //ADD SWIPE to delete
            assert !showAsUneditableField : "showAsUneditableField should never be true if we also define a swipeClear fucntion";
            Button deleteFieldButton = new Button();
//        SwipeableContainer fieldCont = new SwipeableContainer(null, deleteFieldButton, field);
            swipeCont = new SwipeableContainer(null, deleteFieldButton, field);
            deleteFieldButton.setCommand(Command.create("Delete", Icons.iconCloseCircleLabelStyle, (ev) -> {
                swipeClear.clearFieldValue();
                swipeCont.close();
                field.repaint(); //??
//<editor-fold defaultstate="collapsed" desc="comment">
//            cont.repaint();
//            this.getParent().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field? NO, shows Delete button on top of <set>
//            repaint();
//            getComponentForm().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field?
//</editor-fold>
            }));
//            fieldValueEdit = FlowLayout.encloseIn(swipeCont, editFieldIcon);
//            fieldValueEdit = FlowLayout.encloseRightMiddle(swipeCont, editFieldIcon);
            if (swipeClear != null) {
                fieldContainer = swipeCont;
            } else {
                fieldContainer = BoxLayout.encloseXNoGrow(swipeCont, editFieldIcon);
                ((Container) fieldContainer).setLeadComponent(field);
            }
        } else {
//            fieldValueEdit = FlowLayout.encloseIn(field, editFieldIcon);
//            fieldValueEdit = FlowLayout.encloseRightMiddle(field, editFieldIcon);
            if (noVisibleEditButton) {
//                fieldContainer = swipeCont;
            } else {
                fieldContainer = BoxLayout.encloseXNoGrow(field, editFieldIcon);
                if (!showAsUneditableField) {
                    ((Container) fieldContainer).setLeadComponent(field);
                }
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, FlowLayout.encloseCenterMiddle(field));
//        Container fieldValueEdit = FlowLayout.encloseCenter(fieldCont, new Label(Icons.iconEditSymbolLabelStyle, "LabelValue"));
//        Container fieldValueEdit = FlowLayout.encloseCenter(fieldCont, editFieldIcon);
//</editor-fold>
        Component fieldLabel = makeHelpButton(fieldLabelTxt, help);

        Container l = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, fieldContainer);

        if (checkForTooLargeWidth) {
//            l.revalidate();
//            if (fieldLabel.getWidth() < fieldLabel.getPreferredW() || fieldValueEdit.getWidth() < fieldValueEdit.getPreferredW()) { //if either of the fields got less width than needed
            int availDisplWidth = Display.getInstance().getDisplayWidth() * 9 / 10; //asumme roughly 90% of width is available after margins
            int fieldLabelPreferredW = fieldLabel.getPreferredW();
            int fieldValueEditPreferredW = fieldContainer.getPreferredW();
            if (fieldLabelPreferredW + fieldValueEditPreferredW > availDisplWidth) { //if too wide
                fieldLabel.getAllStyles().setMarginBottom(0);
                fieldLabel.getAllStyles().setPaddingBottom(0); //reduce space between label and field
                l.removeComponent(fieldLabel); //
//                l = BorderLayout.north(fieldLabel).add(BorderLayout.EAST, fieldValueEdit);
                l.add(BorderLayout.NORTH, fieldLabel);
            }
//<editor-fold defaultstate="collapsed" desc="comment">
            if (false) {
//    int availDisplWidth = Display.getInstance().getDisplayWidth() * 9 / 10; //asumme roughly 90% of width is available after margins
//    int fieldLabelPreferredW = fieldLabel.getPreferredW();
//    int fieldValueEditPreferredW = fieldValueEdit.getPreferredW();
//    if (fieldLabelPreferredW + fieldValueEditPreferredW > availDisplWidth) { //if too wide
//{
//            } else {
//            }
                if (false && checkForTooLargeWidth) {
                    if (l.getPreferredW() > Display.getInstance().getDisplayWidth()) { //if too wide, put label in North (so it becomes two separate lines)
                        if (field instanceof TextArea || field instanceof TextField) {
//                    field.setSameWidth(d);
                            Dimension d = field.getPreferredSize();
                            d.setWidth(Display.getInstance().getDisplayWidth() / 2); //allow a textField to take up mox half the screen size.
                            field.setPreferredSize(d); //TODO!!! CN1 Support: setPreferredSize deprecated, but what is right way then??
                        } else {
//                    l.removeAll(); //remove already added components to avoid getting an error about adding twice
                            field.getParent().removeComponent(field); //remove already added components to avoid getting an error about adding twice
                            l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, field);
                        }
                    }
                }

                if (false) {
                    if (labelAndFieldOnSeparateLines) {
                        l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, fieldContainer);
                    } else {
                        l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.SOUTH, fieldContainer);
                    }
                }
            }
//</editor-fold>
        }

//        return BorderLayout.center(helpBut(label, help)).add(BorderLayout.EAST, field);
        return l;
    }

    protected static Component layoutOLD(String fieldLabelTxt, Component field, String help, boolean checkForTooLargeWidth) {
//        return BorderLayout.center(addHelp(new Button(label), help)).add(BorderLayout.EAST, field);
//        Container l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, FlowLayout.encloseCenterMiddle(field)); //CenterMiddle seems to shift down a bit
        Container l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, FlowLayout.encloseCenter(field));
//        field.setUIID("LabelValue");
        if (checkForTooLargeWidth) {
            if (l.getPreferredW() > Display.getInstance().getDisplayWidth()) { //if too wide, put label in North (so it becomes two separate lines)
                if (field instanceof TextArea || field instanceof TextField) {
//                    field.setSameWidth(d);
                    Dimension d = field.getPreferredSize();
                    d.setWidth(Display.getInstance().getDisplayWidth() / 2); //allow a textField to take up mox half the screen size. 
                    field.setPreferredSize(d); //TODO!!! CN1 Support: setPreferredSize deprecated, but what is right way then??
                } else {
//                    l.removeAll(); //remove already added components to avoid getting an error about adding twice
                    field.getParent().removeComponent(field); //remove already added components to avoid getting an error about adding twice
                    l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, field);
                }
            }
        }

//        return BorderLayout.center(helpBut(label, help)).add(BorderLayout.EAST, field);
        return l;
    }

    protected void animateMyForm() {
//        ASSERT.that(false, "not implemented!!!");
        getContentPane().animateLayoutAndWait(300); //need AndWait to ensure that form is animited into place before setting InlineAddTask text field in focus??! 
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Component layout(String label, Component setting, String help) {
//        return BorderLayout.center(new SpanLabel(label)).add(BorderLayout.EAST, setting).add(BorderLayout.SOUTH, new SpanLabel(help));
//    }
//</editor-fold>

    static Component makeContainerWithClearButton(Component comp, Action clearFct) {
        Button clearButton = new Button();
        clearButton.setCommand(Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
            clearFct.launchAction();
            clearButton.setHidden(true); //hide on clear action
        }));
        return FlowLayout.encloseRightMiddle(comp, clearButton);
    }

    static MyForm getCurrentFormAfterClosingDialogOrMenu() {
        Form current = Display.getInstance().getCurrent();
        if (current instanceof Dialog) {
            ((Dialog) current).dispose(); //UI: any open dialog is automatically closed on a reminder notification
            current = Display.getInstance().getCurrent();
        } else if (current != null && current.getClientProperty("cn1$sideMenuParent") != null
                && current.getClientProperty("cn1$sideMenuParent") instanceof SideMenuBar) {
            //TODO!!!!: HACK to check if menu is open (if this property disappears, the alarm dialog should simply return to main form)
            ((SideMenuBar) current.getClientProperty("cn1$sideMenuParent")).closeMenu();
            current = Display.getInstance().getCurrent();
        }
//        return current;
        return current instanceof MyForm ? (MyForm) current : null;
    }

    boolean isSelectionMode() {
        return selectedObjects != null;
    }

    void setSelectionMode(boolean selectionModeActivated) {
        if (selectionModeActivated) {
//            selectedObjects = new HashSet();
            selectedObjects = oldSelectedObjects;
            if (selectedObjects == null) {
//                selectedObjects = new ArrayList();
//                selectedObjects = new ListSelector(1, true);
                selectedObjects = new ListSelector();
            }
        } else {
            oldSelectedObjects = selectedObjects; //UI:store selection so it isn't lost between selection sessions
            selectedObjects = null;
        }
    }

    /**
     * scrolls the scrollable part of this form to either the top or the bottom
     *
     * @param scrollToBottom
     */
    public void scrollListToTopOrBottom(boolean toBottom) {
        getContentPane().scrollComponentToVisible(getContentPane().getComponentAt(toBottom ? getContentPane().getComponentCount() : 0));
    }

    @Override
    public void show() {
//        Command replayCommand = ReplayLog.getInstance().getNextReplayCmd();
//        if (replayCommand != null) { //if there is a command to replay do that instead of showing this screen
//            replayCommand.actionPerformed(new ActionEvent(this));
//        } else { //only show if no replay command
        if (!ReplayLog.getInstance().replayCmd(new ActionEvent(this))) { //only show screen is there was no command to replay
            super.show();
        }
    }

    @Override
    public void showBack() {
        ReplayLog.getInstance().popCmd(); //pop any previous command
        super.showBack();
    }

    /**
     * save any ongoing edits locally when app is paused (in case it is
     * destroyed later on). Does nothing in screens with no new edits. Saved
     * items must be read back in constructor of the screen.
     */
    public void saveEditedValuesLocallyOnAppExit() {

    }

    public boolean restoreEditedValuesSavedLocallyOnAppExit() {
        return false;
    }

    public void deleteEditedValuesSavedLocallyOnAppExit() {

    }

    private double distance(int[] x, int[] y) {
        int disx = x[0] - x[1];
        int disy = y[0] - y[1];
        return Math.sqrt(disx * disx + disy * disy);
    }

    /**
     * If this Component is focused, the pointer dragged event will call this
     * method
     *
     * @param x the pointer x coordinate
     * @param y the pointer y coordinate
     */
    public void pointerDraggedXX(int[] x, int[] y) {
//        if (x.length > 1) {
//            double currentDis = distance(x, y);
//
//            // prevent division by 0
//            if (pinchDistance <= 0) {
//                pinchDistance = currentDis;
//            }
//            double scale = currentDis / pinchDistance;
//            if (pinch((float) scale)) {
//                return;
//            }
//            Log.p("PointerDragged dist=" + pinchDistance + ", x=" + x + ", y=" + y);
//        }
//        pointerDragged(x[0], y[0]);
    }

    private int pinchDistance;
//    private boolean pinchOut;

    private void insertNewContainer() {
        //find position where to insert in list
        //insert a (scaled) temporary container (create from empty Item)
        //cancel insert if less than 100%
        //insert: create new Item in appropriate underlying ItemList, 
    }

//    @Override
    public void pointerDraggedXXX(int[] x, int[] y) {
////        super.pointerDragged(x, y);
//        if (x.length > 1) {
//            double currentDis = distance(x, y);
//
//            // prevent division by 0
//            if (pinchDistance <= 0) {
//                pinchDistance = currentDis;
//            }
//            double scale = currentDis / pinchDistance;
//            if (pinch((float) scale)) {
//                return;
//            }
//        }
//        pointerDragged(x[0], y[0]);
    }

    //DEBUG:
    private Form fPinchOut;// = new Form(new BorderLayout());
    private Container disp1;// = new Container(BoxLayout.y());
    private Label title;//= new Label();
    private Label xLabel;// = new Label();
    private Label yLabel;// = new Label();
    private Label distLabel;// = new Label();
    private Label comp1Label;// = new Label();
    private Label comp2Label;//= new Label();
    private Label cont1Label;//= new Label();
    private Label cont2Label;//= new Label();
    private Label dropTarget1Label;// = new Label();
    private Label dropTarget2Label;//= new Label();
    private Component pinchContainer; //Container holding the pinchComponent (and implementing the resize)
//    private Component pinchComponent;
//    private Component prevComponentAbove;
//    private Component prevComponentBelow;
//    private Item pinchItem;

    private boolean pinchInsertEnabled;
    private int pinchInitialYDistance = Integer.MIN_VALUE;

    private void initPinch() {
//fPinchOut.setScrollableY(true);
        disp1 = new Container(BoxLayout.y());
        title = new Label("XX");
        xLabel = new Label("XX");
        yLabel = new Label("XX");
        distLabel = new Label("XX");
        comp1Label = new Label("XX");
        comp2Label = new Label("XX");
        cont1Label = new Label("XX");
        cont2Label = new Label("XX");
        dropTarget1Label = new Label("XX");
        dropTarget2Label = new Label("XX");
//        disp1.removeAll();
        disp1.addAll(title, xLabel, yLabel, distLabel, comp1Label, comp2Label, cont1Label, cont2Label, dropTarget1Label, dropTarget2Label);
        fPinchOut.addComponent(BorderLayout.SOUTH, disp1);

        Container labelCont = new Container(BoxLayout.y());
        labelCont.setScrollableY(true);
        for (int i = 0, size = 40; i < size; i++) {
            Label l = new Label("Label " + i);
            l.setName("Label" + i);
            labelCont.add(l);
        }
        fPinchOut.addComponent(BorderLayout.CENTER, labelCont);
    }

    private void display(int[] x, int[] y, boolean inPinch) {
        title.setText(inPinch ? "***PINCH***" : "MOVE");
        xLabel.setText("(x[0],y[0])=(" + x[0] + "," + y[0] + ")");
        Component comp1 = getComponentAt(x[0], y[0]);
        comp1Label.setText("Comp1=" + comp1.getName());
        if (comp1 instanceof Container) {
            Container cont1 = (Container) comp1;
            cont1Label.setText("Cont1=" + cont1);
            Component dropTarget1 = cont1.findDropTargetAt(x[0], y[0]);
            dropTarget1Label.setText("dropTarget1=" + dropTarget1);
        }
        if (x.length > 1) {
            yLabel.setText("(x[1],y[1])=(" + x[1] + "," + y[1] + ")");
            distLabel.setText("dist=" + distance(x, y));
            Component comp2 = getComponentAt(x[1], y[1]);
            comp1Label.setText("Comp2=" + comp2.getName());
            if (comp2 instanceof Container) {
                Container cont2 = (Container) comp2;
                cont2Label.setText("Cont2=" + cont2);
                Component dropTarget2 = cont2.findDropTargetAt(x[1], y[1]);
                dropTarget2Label.setText("dropTarget2=" + dropTarget2);
            }
        } else {
            comp1Label.setText("Comp2=");
            cont2Label.setText("Cont2=");
            dropTarget2Label.setText("dropTarget2=");
        }

        fPinchOut.revalidate();
    }

    private static boolean minimumPinchSizeReached(int pinchYDistance, Component pinchContainer) {
        return pinchYDistance > pinchContainer.getPreferredH() / 2; //true if over half the required size has been pinched out
    }

    public void setInsertItemValues(Object obj, Object sortField, Object objBefore, Object objAfter) {//, getValueFunction, makeNewValueFunction) {
        if (obj instanceof Item) {
            Item item = (Item) obj;

        } else if (obj instanceof WorkSlot) {
        } else if (obj instanceof Category) {
        } else if (obj instanceof ItemList) {

        }
    }

    public Component createInsertComponent() {
        return null;
    }

    /**
     * inserts insertObj at the appropriate position
     *
     * @param componentAbove
     * @param componentBelow
     * @param insertObj
     */
//    private void insertPinchContainer(MyDragAndDropSwipeableContainer componentAbove, MyDragAndDropSwipeableContainer componentBelow, Object insertObj) {
//        insertPinchContainer(componentAbove, componentBelow, insertObj, false);
//    }
    /**
     * return true if an insert is possible with the given components
     *
     * @param componentAbove
     * @param componentBelow
     * @return
     */
//    private boolean canInsertPinchContainer(MyDragAndDropSwipeableContainer componentAbove, MyDragAndDropSwipeableContainer componentBelow) {
//        return insertPinchContainer(componentAbove, componentBelow, null, true);
//    }
    interface IntFunction {

        int get(int orgH);
    }

    private Container wrap(final Component pinchComponent) {
        Container pinchContainer = null;
        if (pinchComponent != null) { //pinchOut makes sense here, a new pinchInsert container with the right type of element is created and inserted
            pinchContainer = new Container(BorderLayout.center()) {
                public Dimension calcPreferredSize() {
                    Dimension orgPrefSize = pinchComponent.getPreferredSize();
                    //if I'm old pinchContainer, and reduced to zero size
                    //TODO!! do like Clear app: if pinching further out than the size show some 'elastic' empty space around the container 
                    int h = Math.max(0, Math.min(pinchDistance, orgPrefSize.getHeight())); //cannot become bigger than preferredHeight of the component
                    return new Dimension(orgPrefSize.getWidth(), h); //Math.max(0, since pinch distance may become negative when fingers cross vertically
                }
            };
            pinchContainer.add(BorderLayout.CENTER, pinchComponent);
        }
        return pinchContainer;
    }

    private Component createInsertContainer(ItemAndListCommonInterface elementType) {
        return createInsertContainer(elementType, elementType.getOwner());
    }

    private Component createInsertContainer(ItemAndListCommonInterface elementType, ItemAndListCommonInterface list) {
        if (elementType instanceof Item && list instanceof Category) {
            Item newItem = new Item();
//            newItem.addCategoryToItem((Category)list, false); //add category in InlineInsertNewItemContainer2
            return wrap(new InlineInsertNewItemContainer2(MyForm.this, (Item) elementType));
        } else if (elementType instanceof Item && list instanceof ItemList) {
            return wrap(new InlineInsertNewItemContainer2(MyForm.this, (Item) elementType));
        } else if (elementType instanceof Category) {
            return wrap(new InlineInsertNewCategoryContainer(MyForm.this, (Item) elementType));
//        } else if (elementType instanceof ItemList) {
//            return wrap(new InlineInsertNewItemListContainer(MyForm.this, (Item) elementType));
//        } else if (elementType instanceof WorkSlot) {
//            return wrap(new InlineInsertNewWorkSlotContainer(MyForm.this, (Item) elementType));
        }
        return null;
    }

    /**
     * either check or insert
     *
     * @param componentAbove the top-most (visually) pinched object (with the
     * lowest/smallest index if in the same list as componentBelow)
     * @param componentBelow the bottom-most (visually) pinched object (with the
     * highest index if in the same list as componentAbove)
     * @param insertObj can be null if checkValidaty is true (since no insert
     * will happen)
     * @param checkValidity if true, no insert will happen, but will return true
     * if an insert can happen. Used to check if it makes sense to pinch out and
     * show an new insert container
     * @return
     */
//    private boolean insertPinchContainer(MyDragAndDropSwipeableContainer componentAbove, MyDragAndDropSwipeableContainer componentBelow, Object insertObj, boolean checkValidity) {
    private Component createAndInsertPinchContainer(int[] x, int[] y) {
        Component containerAbove = y[0] < y[1] ? getComponentAt(y[0], x[0]) : getComponentAt(y[1], x[1]);
        Component containerBelow = y[0] < y[1] ? getComponentAt(y[1], x[1]) : getComponentAt(y[0], x[0]);
        Container parentContainerAbove = containerAbove.getParent();
        Container parentContainerBelow = containerBelow.getParent();
        MyDragAndDropSwipeableContainer dropComponentAbove = findDropContainerIn(containerAbove);
        MyDragAndDropSwipeableContainer dropComponentBelow = findDropContainerIn(containerBelow);
        ItemAndListCommonInterface objAbove = (ItemAndListCommonInterface) dropComponentAbove.getDragAndDropObject();
//        List objAboveOwnerList = null;
        ItemAndListCommonInterface objBelow = (ItemAndListCommonInterface) dropComponentBelow.getDragAndDropObject();
//        List objBelowOwnerList = null;
        Component insertContainer = null;
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (dropComponentAbove != null) {
//            objAbove = (ItemAndListCommonInterface) dropComponentAbove.getDragAndDropObject();
//            objAboveOwnerList = objAbove.getOwner().getList();
//            objAboveOwnerList.add()
//        }
//        if (dropComponentBelow != null) {
//            objBelow = (ItemAndListCommonInterface) dropComponentBelow.getDragAndDropObject();
//            objBelowOwnerList = objBelow.getOwner().getList();
//        }
//</editor-fold>
        if (dropComponentAbove == null) { //pull down on top-most item, insert before the first item
//            if (checkValidity) {                return true;            }
//            if (false) {                objBelowOwnerList.add(0, insertObj);            }
            insertContainer = createInsertContainer(objBelow); //create insertContainer
            parentContainerBelow.addComponent(0, insertContainer); //insert insertContainer at beginning of list that the other pinch finger touches
        } else if (dropComponentBelow == null) { //pull down on bottom-most item, insert at the end of the list
//            if (checkValidity) {                return true;            }
//            if (false) {                objAboveOwnerList.add(insertObj);            }
            insertContainer = createInsertContainer(objAbove, null); //create insertContainer
            parentContainerBelow.addComponent(parentContainerBelow.getComponentCount(), insertContainer); //insert insertContainer at end of list that the other pinch finger touches
//        } else if (objAboveOwnerList == objBelowOwnerList) { //we're inserting in the same list
        } else if (parentContainerAbove == parentContainerBelow) { //we're inserting in the same list, insert just below the containerAbove
//            assert objAbove.getClass()==objBelow.getClass():"";
            ASSERT.that(objAbove.getClass() == objBelow.getClass()); //should always be of same class if in same list (TODO!!!! what about Today view?!
            //Covers both Items, ItemList, Category, createInsertContainer will create the right container
//            if (checkValidity) {                return true;            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            int insertIndex = objBelowOwnerList.indexOf(objBelow);
//            int insertIndex = objAboveOwnerList.indexOf(objAbove); //insert below the top-most object
//            objAboveOwnerList.add(insertIndex, insertObj);
//</editor-fold>
            insertContainer = createInsertContainer(objAbove, null); //create insertContainer
            int insertIndex = parentContainerAbove.getComponentIndex(containerAbove) + 1;
            parentContainerAbove.addComponent(insertIndex, insertContainer); //insert insertContainer at end of list that the other pinch finger touches
        } else if (objBelow instanceof Item && (objAbove instanceof Category || objAbove instanceof ItemList)) { //insert above the Item (instanceof ItemList also matches Category)
            if (((Category) objAbove).getList().contains(objBelow)) { //only insert if the item below is actually *in* the category
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (checkValidity) {                     return true;                }
//                int insertIndex = objBelowOwnerList.indexOf(objBelow);
//                objBelowOwnerList.add(insertIndex, insertObj);
//</editor-fold>
                insertContainer = createInsertContainer(objBelow, objBelow.getOwner()); //create insertContainer
                parentContainerBelow.addComponent(0, insertContainer); //insert new Item at the beginning of the item list (just below the 'header' category)
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//        } else if (objAbove instanceof ItemList && objBelow instanceof Item) { //insert above the Item (instanceof ItemList also matches Category)
//            if (((ItemList) objAbove).getList().contains(objBelow)) { //only insert if the item below is actually *in* the category
////                if (checkValidity) {                    return true;                }
////                int insertIndex = objBelowOwnerList.indexOf(objBelow);
////                objBelowOwnerList.add(insertIndex, insertObj);
//                return true;
//            }
//</editor-fold>
        } else if (objAbove instanceof Item && (objBelow instanceof ItemList || objBelow instanceof ItemList)) { //insert *below* (+1) the objAbove Item 
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (checkValidity) {                return true;            }
//            int insertIndex = objAboveOwnerList.indexOf(objAbove) + 1;
//            objBelowOwnerList.add(insertIndex, insertObj);
//</editor-fold>
            insertContainer = createInsertContainer(objAbove); //, objAbove.getOwner()); //create insertContainer
            parentContainerAbove.addComponent(parentContainerAbove.getComponentCount(), insertContainer); //insert new Item at the beginning of the item list (just below the 'header' category)
        } else if (objAbove instanceof Item && objBelow instanceof Item) { //both objects are Items but not in same list, insert below (+1) objAbove
//            if (checkValidity) {                return true;            }
//            ItemAndListCommonInterface owner = ((ItemAndListCommonInterface) objAbove).getOwner();
//            int insertIndex = owner.getList().indexOf(objAbove) + 1;
//            owner.getList().add(insertIndex, insert);
            insertContainer = createInsertContainer(objAbove); //, objAbove.getOwner()); //create insertContainer
            int insertIndex = parentContainerAbove.getComponentIndex(containerAbove) + 1;
            parentContainerAbove.addComponent(insertIndex, insertContainer); //insert new Item at the beginning of the item list (just below the 'header' category)
        } else if (objAbove != null) { //insert *after* objAbove
            ASSERT.that(objAbove != null);
//            if (checkValidity) {                return true;            }
            insertContainer = createInsertContainer(objAbove); //, objAbove.getOwner()); //create insertContainer
            int insertIndex = parentContainerAbove.getComponentIndex(containerAbove) + 1;
            parentContainerAbove.addComponent(insertIndex, insertContainer); //insert new Item at the beginning of the item list (just below the 'header' category)
        }
        return insertContainer;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private int getInsertContainerHeight(int preferredHeight) {
//        if (pinchOut) {
//            //TODO!! do like Clear app: if pinching further out than the size show some 'elastic' empty space around the container
//            return Math.min(pinchDistance, preferredHeight); //cannot become bigger than preferredHeight of the component
//        } else {
//            return Math.max(0, preferredHeight - pinchDistance); //must not get below 0
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    interface CreateElementComponent {
//
//        Component createFinalComponent(Object insertElement);
//    }
//    CreateElementComponent createElementComponent;
//
//    interface PinchContainerSize {
//
//        Dimension calcPreferredSize();
//    }
//
//    interface CreateInsertElementContainer {
//
//        Component createAndInsert(int[] x, int[] y, boolean onlyTestButNoInsert);
//
////        CreateInsertElementContainer create(MyForm myForm, boolean createNewOnEnter, List elementList, PinchContainerSize size);
////
////        ItemAndListCommonInterface getElement();
//    }
//    CreateInsertElementContainer createAndInsertPinchComponent;
//
//    public void initPinchInsert(CreateElementComponent createElementComponent) {
//        this.createElementComponent = createElementComponent;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * returns true if comp is visually between
//     *
//     * @param x
//     * @param y
//     * @param comp
//     * @return
//     */
//    public boolean isComponentBetween(int[] x, int[] y, Component cmp) {
//        Container cont;
//        cont.contains(cmp);
////    public boolean contains(Component cmp) {
//        if (cmp == null) {
//            return false;
//        }
//        cmp = cmp.getParent();
//        while (cmp != null) {
//            if (cmp == this) {
//                return true;
//            }
//            cmp = cmp.getParent();
//        }
//        return false;
////<editor-fold defaultstate="collapsed" desc="comment">
////            comp.getX();
////        int count = getComponentCount();
////        for (int i = count - 1; i >= 0; i--) {
////            Component cmp = getComponentAt(i);
////            if (cmp.contains(x, y)) {
////                if (cmp.isDropTarget()) {
////                    return cmp;
////                }
////                if (cmp instanceof Container) {
////                    Component component = ((Container) cmp).findDropTargetAt(x, y);
////                    if(component != null) {
////                        return component;
////                    }
////                }
////            }
////        }
////        return null;
////</editor-fold>
//    }
//</editor-fold>
    private static MyDragAndDropSwipeableContainer findDropContainerIn(Component comp) {
//        int count = cont.getComponentCount();
//        for (int i = count - 1; i >= 0; i--) {
        if (comp instanceof MyDragAndDropSwipeableContainer) {
            return (MyDragAndDropSwipeableContainer) comp;
        } else if (comp instanceof Container) {
            Container cont = (Container) comp;
            for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
                Component cmp = cont.getComponentAt(i);
                if (cmp instanceof MyDragAndDropSwipeableContainer) {
                    return (MyDragAndDropSwipeableContainer) cmp;
                }
            }
            //for performance reasons, avoid diving into hierarchy of each sub Container, test top-level first
            for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
                Component cmp = cont.getComponentAt(i);
                if (cmp instanceof Container) {
                    Component component = findDropContainerIn((Container) cmp);
                    if (component != null) {
                        return (MyDragAndDropSwipeableContainer) component;
                    }
                }
            }
        }
        return null;
    }

    public Component getComponentOn(Container comp, int x, int y, boolean onlyDropTarget) {
        if (onlyDropTarget) {
            return comp.findDropTargetAt(x, y);
        } else {
            return comp.getComponentAt(x, y);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Component create(int[] x, int[] y, boolean checkValidity) {
//        int yMin = y[1] <= y[0] ? y[1] : y[0];
//        int yMax = y[1] > y[0] ? y[1] : y[0];
//        int xMin = y[1] <= y[0] ? x[1] : x[0]; //xMin is the x[n] corresponding to the minimal y[n]
//        int xMax = y[1] > y[0] ? x[1] : x[0];
//
//        MyDragAndDropSwipeableContainer componentAbove = getComponentOn(disp1, xMin, yMin, focusScrolling);
//        MyDragAndDropSwipeableContainer componentBelow = getComponentOn(disp1, xMax, yMax, focusScrolling);
////        Container parentAbove = componentAbove.getParent().getParent();
////        Container parentBelow = componentBelow.getParent().getParent();
//        ItemAndListCommonInterface objAbove = null;
//        List objAboveOwnerList = null;
//        ItemAndListCommonInterface objBelow = null;
//        List objBelowOwnerList = null;
//        if (componentAbove != null) {
//            objAbove = (ItemAndListCommonInterface) componentAbove.getDragAndDropObject();
//            objAboveOwnerList = objAbove.getOwner().getList();
//        }
//        if (componentBelow != null) {
//            objBelow = (ItemAndListCommonInterface) componentBelow.getDragAndDropObject();
//            objBelowOwnerList = objBelow.getOwner().getList();
//            if (objAbove instanceof Item) {
//
//            }
//        } else if (componentAbove == null) { //pull down on top-most item, insert before the first item
//            if (checkValidity) {
//                return true;
//            }
//            objBelowOwnerList.add(0, insertObj);
//            return true;
//        } else if (componentBelow == null) { //pull down on bottom-most item, insert at the end of the list
//            if (checkValidity) {
//                return true;
//            }
//            objAboveOwnerList.add(insertObj);
//            return true;
//        } else if (objAboveOwnerList == objBelowOwnerList) { //we're inserting in the same list
//            if (checkValidity) {
//                return true;
//            }
////            int insertIndex = objBelowOwnerList.indexOf(objBelow);
//            int insertIndex = objAboveOwnerList.indexOf(objAbove); //insert below the top-most object
//            objAboveOwnerList.add(insertIndex, insertObj);
//            return true;
//        } else if (objAbove instanceof Category && objBelow instanceof Item) { //insert above the Item (instanceof ItemList also matches Category)
//            if (((Category) objAbove).getList().contains(objBelow)) { //only insert if the item below is actually *in* the category
//                if (checkValidity) {
//                    return true;
//                }
//                int insertIndex = objBelowOwnerList.indexOf(objBelow);
//                objBelowOwnerList.add(insertIndex, insertObj);
//                return true;
//            }
//        } else if (objAbove instanceof ItemList && objBelow instanceof Item) { //insert above the Item (instanceof ItemList also matches Category)
//            if (((ItemList) objAbove).getList().contains(objBelow)) { //only insert if the item below is actually *in* the category
//                if (checkValidity) {
//                    return true;
//                }
//                int insertIndex = objBelowOwnerList.indexOf(objBelow);
//                objBelowOwnerList.add(insertIndex, insertObj);
//                return true;
//            }
//        } else if (objAbove instanceof Item && objBelow instanceof ItemList) { //insert *below* (+1) the objAbove Item
//            if (checkValidity) {
//                return true;
//            }
//            int insertIndex = objAboveOwnerList.indexOf(objAbove) + 1;
//            objBelowOwnerList.add(insertIndex, insertObj);
//            return true;
//        } else if (objAbove instanceof Item && objBelow instanceof Item) { //both objects are Items but not in same list, insert below (+1) objAbove
//            if (checkValidity) {
//                return true;
//            }
//            int insertIndex = objAboveOwnerList.indexOf(objAbove) + 1;
//            objBelowOwnerList.add(insertIndex, insertObj);
//            return true;
//        } else { //insert *after* objAbove
//            ASSERT.that(objAbove != null);
//            if (checkValidity) {
//                return true;
//            }
//            int insertIndex = objAboveOwnerList.indexOf(objAbove);
//            objAboveOwnerList.add(insertIndex, insertObj);
//            return true;
//        }
//        return false;
//        return null;
//    }
//</editor-fold>
    @Override
    public void pointerDragged(int[] x, int[] y) {
        if (!pinchInsertEnabled) {
            super.pointerDragged(x, y);
        } else { //pinchInsertEnabled
//            } else { //pinchContainer != null) => we already have a pinchContainer (either being inserted or inserted previously)
            if (x.length <= 1) { //PinchOut is either finished or not ongoing (newPinchContainer!=null means a pinch was ongoing before)
                if (pinchContainer == null) { //no previous pinchContainer, do nothing
                    super.pointerDragged(x, y);
                } else { //a pinch container already exists, do nothing, insertContainer already in place
                    if (minimumPinchSizeReached(pinchDistance, pinchContainer)) {
//<editor-fold defaultstate="collapsed" desc="comment">
//add new item into underlying list - NO, done in the pinchConatiner itself when hitting Enter or [>]
//                    itemList.addItemAtIndex(pinchItem, pos);
//insert pinchContainer into the displayed list at the right position
//                        insertPinchContainer(prevComponentAbove, prevComponentBelow, pinchContainer); //ALREADY inserted when growing, just leave it in place
//replace pinchContainer by (temporary) new Element container to quickly update (before regenerating the list) - NO, done in pinchContainer itself as well
//                        pinchContainer.getParent().replace(pinchContainer, createElementComponent.createFinalComponent(itemList), null); //TODO!!! add meaningful animation
//                        pinchContainer = null; //keep the container if there's a later pinchIn
//</editor-fold>
                    } else {
                        //delete inserted container (whether a new container not sufficiently pinched OUT or an existing SubtaskContainer pinched IN)
                        Label emptyLabel = new Label();
                        Container pinchContainerParent = pinchContainer.getParent();
                        pinchContainerParent.replace(pinchContainer, emptyLabel, null); //TODO!!! add meaningful animation
                        pinchContainerParent.removeComponent(emptyLabel);
                        pinchContainer = null; //indicates done with this container
//                        MyForm.this.refreshAfterEdit();
                        MyForm.this.revalidate(); //necessary after using replace()??
                    }
                    pinchInitialYDistance = Integer.MIN_VALUE; //reset pinchdistance
                }
                display(x, y, false);
            } else { // (x.length > 1) => PINCH ONGOING
                //TODO!!! What happens if a pinch in is changed to PinchOut while moving fingers? Should *not* insert a new container but just leave the old one)
                //TODO!!! What happens if a pinch out is changed to PinchIn while moving fingers? Simply remove the inserted container!
                int yMin = y[1] <= y[0] ? y[1] : y[0];
                int yMax = y[1] > y[0] ? y[1] : y[0];
                int xMin = y[1] <= y[0] ? x[1] : x[0]; //xMin is the x[n] corresponding to the minimal y[n] 
                int xMax = y[1] > y[0] ? x[1] : x[0];
                int newYDist = yMax - yMin;
//                if (newYDist<0)newYDist=0; //should not be allowed to become negative
                if (pinchInitialYDistance == Integer.MIN_VALUE) {
                    pinchInitialYDistance = newYDist; //Math.abs(y[1]-y[0]);
                }
                pinchDistance = newYDist - pinchInitialYDistance;
                if (pinchDistance < 0) {
                    pinchDistance = 0; //should not be allowed to become negative
                }
//                pinchOut = pinchDistance > 0;

                if (pinchContainer == null) {
                    //for now: simply decrease the size of the existing container
//                    if (pinchOut) {
                    if (pinchDistance > 0) { //as soon as we have a positive pinchOut, create and insert the insertContainer
                        //TODO!! if existing pinch container is elsewhere, insert a new one between the two fingers and decrease the size of the old one inversely wrt new size
//                        pinchComponent = createAndInsertPinchComponent.createAndInsert(x, y, true, () -> pinchDistance);
//                        pinchComponent = createAndInsert(x, y, true, () -> pinchDistance);
                        pinchContainer = createAndInsertPinchContainer(x, y);
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (pinchComponent != null) { //pinchOut makes sense here, a new pinchInsert container with the right type of element is created and inserted
//                            pinchContainer = new Container(BorderLayout.center()) {
//                                public Dimension calcPreferredSize() {
////                                    Dimension orgPrefSize = super.calcPreferredSize();
//                                    Dimension orgPrefSize = pinchComponent.getPreferredSize();
////<editor-fold defaultstate="collapsed" desc="comment">
////                                    if (oldPinchContainer != null && oldPinchContainer == Container.this) { //I am now the old pinchContainer
////                                        return new Dimension(orgPrefSize.getWidth(), getInsertContainerHeight(orgPrefSize.getHeight())); //Math.max(0, since pinch distance may become negative when fingers cross vertically
////if I'm old pinchContainer, and reduced to zero size
////                                    } else {
////</editor-fold>
//                                    return new Dimension(orgPrefSize.getWidth(), getInsertContainerHeight(orgPrefSize.getHeight())); //Math.max(0, since pinch distance may become negative when fingers cross vertically
//                                }
//                            };
//                            pinchContainer.add(BorderLayout.CENTER, pinchComponent);
//                        }
//</editor-fold>
//                        MyForm.this.refreshAfterEdit(); //really necessary? //no, should only be done once the new element is effectively inserted (so, should be done by the InsertContainer itself)
                        MyForm.this.revalidate(); //refresh
                    } else {
                        //reduce size of existing container
                        //DO NOTHING (don't create a new pinchContainer if pinching in)
                    }
                } else { //pinchContainer != null
//                    if (pinchDistance > 0) { //as soon as we have a positive pinchOut, create and insert the insertContainer
                        //TODO!! if existing pinch container is elsewhere, insert a new one between the two fingers and decrease the size of the old one inversely wrt new size
                    //TODO!! check if the pinch container is between the two fingers and only decrease it then??
                    //we already have a pinchContainer (either being inserted or inserted previously), so do nothing other than resize
                    Log.p("PointerDragged dist=" + pinchDistance + ", x=" + x + ", y=" + y);
                    MyForm.this.revalidate(); //refresh with new size of pinchContainer
                    display(x, y, true);
                }
            }
        }
        //            super.pointerDragged(x[0], y[0]);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void pointerDraggedADVANCED(int[] x, int[] y) {
//        if (!pinchInsertEnabled) {
//            super.pointerDragged(x, y);
//        } else { //pinchInsertEnabled
////            } else { //pinchContainer != null) => we already have a pinchContainer (either being inserted or inserted previously)
//            if (x.length <= 1) { //PinchOut is (maybe) finished (newPinchContainer!=null means a pinch was ongoing before)
//                if (pinchContainer == null) { //no previous pinchContainer, do nothing
//                    super.pointerDragged(x, y);
//                } else { //a pinch container already exists
//                    ItemList itemList = null;
//                    int pos = 0; //TODO find position of *lowest* container (==highest index, == thumb position == most 'stable' position)
//                    if (minimumPinchSizeReached(pinchDistance, pinchContainer)) {
//                        //add new item into underlying list - NO, done in the pinchConatiner itself when hitting Enter or [>]
////                    itemList.addItemAtIndex(pinchItem, pos);
//                        //insert pinchContainer into the displayed list at the right position
//                        insertPinchContainer(componentAbove, componentBelow, pinchContainer);
//                        //replace pinchContainer by (temporary) new Element container to quickly update (before regenerating the list) - NO, done in pinchContainer itself as well
////                        pinchContainer.getParent().replace(pinchContainer, createElementComponent.createFinalComponent(itemList), null); //TODO!!! add meaningful animation
////                        pinchContainer = null; //keep the container if there's a later pinchIn
//                    } else {
//                        //delete inserted container (whether a new container not sufficiently pinched OUT or an existing SubtaskContainer pinched IN)
//                        Container list = null;
//                        Label emptyLabel = new Label();
//                        Container pinchContainerParent = pinchContainer.getParent();
//                        pinchContainerParent.replace(pinchContainer, emptyLabel, null); //TODO!!! add meaningful animation
//                        pinchContainerParent.removeComponent(emptyLabel);
//                        pinchContainer = null; //indicates done with this container
//                        MyForm.this.refreshAfterEdit();
//                    }
//                    pinchInitialYDistance = Integer.MIN_VALUE; //reset pinchdistance
//                }
//                display(x, y, false);
//            } else { // (x.length > 1) => PINCH == TWO FINGERS
//                //TODO!!! What happens if a pinch in is changed to PinchOut while moving fingers? Should *not* insert a new container but just leave the old one)
//                //TODO!!! What happens if a pinch out is changed to PinchIn while moving fingers? Simply remove the inserted container!
//                int yMin = y[1] <= y[0] ? y[1] : y[0];
//                int yMax = y[1] > y[0] ? y[1] : y[0];
//                int xMin = y[1] <= y[0] ? x[1] : x[0]; //xMin is the x[n] corresponding to the minimal y[n]
//                int xMax = y[1] > y[0] ? x[1] : x[0];
//
//                int newDist = yMax - yMin;
//                if (pinchInitialYDistance == Integer.MIN_VALUE) {
//                    pinchInitialYDistance = newDist; //Math.abs(y[1]-y[0]);
//                }
//                pinchDistance = newDist - pinchInitialYDistance;
//                pinchOut = pinchDistance > 0;
//
//                if (pinchContainer != null) { //we already have a pinchContainer (either being inserted or inserted previously
//                    //for now: simply decrease the size of the existing container
//                    if (pinchOut) {
//                        //TODO!! if existing pinch container is elsewhere, insert a new one between the two fingers and decrease the size of the old one inversely wrt new size
//                        //do nothing (insert container is already there)
//                    } else {
//                        //TODO!! is the pinch container between the fingers??
//                        //reduce size of existing container
//                    }
//                } else { //pinchContainer == null
//                    if (pinchOut) {  //pinch increasing (normal case) (if no previous insert container and pinchIn, do nothing
////            Component fingerAboveComp = findDropTargetAt(x[0], y[0]); //TODO!!!! find right ocmponent (should work in any list with any type of objects actually! WorkSlots, ...
////            Component fingerBelowComp = findDropTargetAt(x[1], y[1]);
//                        //TODO!!!!! check if dropTargets are MyDragAndDropSwipeableContainer
//                        MyDragAndDropSwipeableContainer fingerAboveComp = (MyDragAndDropSwipeableContainer) findDropTargetAt(xMin, yMin); //TODO!!!! find right ocmponent (should work in any list with any type of objects actually! WorkSlots, ...
//                        MyDragAndDropSwipeableContainer fingerBelowComp = (MyDragAndDropSwipeableContainer) findDropTargetAt(xMax, yMax);
//                        if (canInsertPinchContainer(fingerAboveComp, fingerBelowComp)) { //makes sense to insert here
//                            //create the appropriate type of insert container (Item, Category, ItemList, WorkSlow(?)
//                            pinchContainer = createAndInsertPinchComponent.create(MyForm.this, true, elementList,
//                                    () -> {
//                                        Dimension orgPrefSize = super.calcPreferredSize();
//                                        return new Dimension(orgPrefSize.getWidth(), getInsertContainerHeight(orgPrefSize.getHeight()));
//                                    });
////<editor-fold defaultstate="collapsed" desc="comment">
////                            pinchContainer = new InlineInsertNewTaskContainer(MyForm.this, itemList) {
////                                public Dimension calcPreferredSize() {
////                                    Dimension orgPrefSize = super.calcPreferredSize();
//////                                return new Dimension(getPreferredW(), Math.min(getPreferredH(), Math.max(0, y[0] - y[1]))); //Math.max(0, since pinch distance may become negative when fingers cross vertically
////                                    return new Dimension(orgPrefSize.getWidth(), getInsertContainerHeight(orgPrefSize.getHeight())); //Math.max(0, since pinch distance may become negative when fingers cross vertically
////                                }
////                            };
////</editor-fold>
//                        }
//
//                        Container containerList = null; //TODO find container (==srollable list?)
//                        pinchItem = new Item();
//                        //insert
//                        containerList.addComponent(pos, pinchContainer);
//                        MyForm.this.refreshAfterEdit(); //really necessary?
//                    } else { //Pinch IN - to delete a just inserted container (or any other item? NO, don't make Delete easy)
//                        Component pinchedInComp = null; //TODO find a possible pinchContainer between the
//                        int firstIndex = fingerAboveComp.getParent().getComponentIndex(fingerAboveComp);
//                        int lastIndex = 0;
//                        if (fingerAboveComp.getParent() == fingerBelowComp.getParent()) {
//                            lastIndex = fingerBelowComp.getParent().getComponentIndex(fingerBelowComp);
//                        } else {
//                            lastIndex = fingerBelowComp.getParent().getComponentCount();
//                        }
//                        Container parentList = null;
//                        for (int i = firstIndex, size = lastIndex; i >= size; i++) {
//                            Component comp = parentList.getComponentAt(i);
//                            if (comp.getClientProperty("element")) {
//                                pinchContainer = comp;
//                            }
//                        }
//                        if (pinchedInComp instanceof InlineInsertNewElementContainer) {
//                            pinchContainer = (InlineInsertNewElementContainer) pinchedInComp;
//                        }
//                    }
//
//                    Log.p("PointerDragged dist=" + pinchDistance + ", x=" + x + ", y=" + y);
//                    display(x, y, true);
//                }else {
//            if (pinchContainer == null) {
//
//        }
//    }
//            }
//        }
//        //            super.pointerDragged(x[0], y[0]);
//        super.pointerDragged(x, y);
//
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//     public Component findScrollableChild(Container c) { //NOT needed, use Component.getScrollable()
////         return super.findScrollableChild( c) ;
//        if(c.isScrollableY()) {
//            return c;
//        }
//        int count = c.getComponentCount();
//        for(int iter = 0 ; iter < count ; iter++) {
//            Component comp = c.getComponentAt(iter);
//            if(comp.isScrollableY()) {
//                return comp;
//            }
//            if(comp instanceof Container) {
//                Component chld = findScrollableChild((Container)comp);
//                if(chld != null) {
//                    return chld;
//                }
//            }
//        }
//        return null;
//     }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Command makeCreateNewItemCommand(String title, Image icon, ItemList itemList) {
////        Command c = new Command(title, icon) {
//        return new Command(title, icon) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                createAndEdit.editNewItemListItem(itemList);
////                switch (itemType) {
////                    case 0:
////                        break;
////                    case ITEM_TYPE_ITEM:
////                        Item item = new Item();
////                        new ScreenItem(item, MyForm.this, () -> {
////                            itemList.addItemAtIndex(item, 0);
////                            item.setOwner(itemList);
////                            DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
////                        }).show();
////                        break;
////                    case ITEM_TYPE_CATEGORY:
////                        Category category = new Category();
////                        new ScreenCategory(category, MyForm.this, () -> {
////                            itemList.addItemAtIndex(category, 0);
////                        }).show();
////                        break;
////                    case ITEM_TYPE_ITEMLIST:
////                        ItemList newItemList = new ItemList();
////                        new ScreenItemListProperties(newItemList, MyForm.this, () -> {
////                            itemList.addItem(newItemList);
////                        }).show();
////                        break;
////                    case ITEM_TYPE_WORKSLOT:
////                        WorkSlot newWorkSlot = new WorkSlot();
////                        new ScreenWorkSlot(newWorkSlot, MyForm.this, () -> {
////                            itemList.addItem(newWorkSlot);
////                        }).show();
////                        break;
////                    default:
////                        throw new RuntimeException("wrong ITEM_TYPE");
////                }
//            }
//        };
////        return c;
//    }
    /**
     * format: 1a) Category text | {(#subtasks)] | [FinishDate] 1b) sub-tree (in
     * SOUTH container)
     *
     * @param content
     * @return
     */
//    protected Container buildCategoryContainerx(Category category) {
//        Container cont = new Container();
//        cont.setLayout(new BorderLayout());
////        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
//        Button editItemButton = new Button();
//        editItemButton.setCommand(new Command(category.getText()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenListBase(category.getText(), category, MyForm.this,
//                        (itemList) -> {
//                            category.setList(itemList.getList());
//                            DAO.getInstance().save(category);
//                        },
//                        (node) -> {
//                            return buildCategoryContainerx((Category) node);
//                        },
//                        (itemList) -> {
//                            Item item = new Item();
//                            new ScreenItem(item, MyForm.this, () -> {
//                                itemList.addItemAtIndex(item, 0);
//                                item.setOwner(itemList);
//                                DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            }).show();
//                        }
//                ).show();
//            }
//        });
//        editItemButton.setUIID("Label");
//        cont.addComponent(BorderLayout.CENTER, editItemButton);
//
//        Button editItemPropertiesButton = new Button();
//        final FontImage iconEdit = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, editItemPropertiesButton.getUnselectedStyle());
////        editItemPropertiesButton.setIcon(iconEdit);
//        editItemPropertiesButton.setCommand(new Command("", iconEdit) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenCategory(category, MyForm.this).show();
//            }
//        });
//
//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
////        Button subTasksButton = new Button();
//        if (!category.getComment().equals("")) {
//            Label description = new Label(" (" + category.getComment() + ")");
//            east.addComponent(description);
//        }
//
//        if (category.getSize() != 0) {
//            Label nbTasks = new Label("[" + category.getSize() + "]");
//            east.addComponent(nbTasks);
//        }
//        east.addComponent(editItemPropertiesButton);
//
////        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
//        cont.addComponent(BorderLayout.EAST, east);
//
//        return cont;
//    }
//    protected Container buildItemListContainerx(ItemList itemList) {
//        Container cont = new Container();
//        cont.setLayout(new BorderLayout());
////        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
//        //EDIT LIST
//        Button editItemButton = new Button();
//        editItemButton.setCommand(new Command(itemList.getText()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                ItemList itemList = (ItemList) editItemButton.getClientProperty("itemList");
////                new ScreenItemListProperties(itemList, ScreenItemList.this).show();
//                new ScreenListBase(itemList.getText(), itemList, MyForm.this,
//                        (itemList) -> {
//                            itemList.setList(itemList.getList());
//                            DAO.getInstance().save(itemList);
//                        },
//                        (node) -> {
//                            return buildItemListContainer((ItemList) node);
//                        },
//                        (itemList) -> {
//                            ItemList newItemList = new ItemList();
//                            new ScreenItemListProperties(newItemList, MyForm.this, () -> {
//                                DAO.getInstance().save(newItemList); //save before adding to itemList
//                                itemList.addItem(newItemList);
//                            }).show();
//                        }
//                ).show();
////                super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
//            }
//        }
//        );
////        editItemButton.putClientProperty("itemList", itemList);
//        editItemButton.setUIID("Label");
//        cont.addComponent(BorderLayout.CENTER, editItemButton);
//
//        Button editItemPropertiesButton = new Button();
//        final FontImage iconEdit = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, editItemPropertiesButton.getUnselectedStyle());
////        editItemPropertiesButton.setIcon(iconEdit);
//        editItemPropertiesButton.setCommand(new Command("", iconEdit) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenItemListProperties(itemList, MyForm.this).show();
//            }
//        });
//
//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
////        Button subTasksButton = new Button();
//        if (!itemList.getComment().equals("")) {
//            Label description = new Label(" (" + itemList.getComment() + ")");
//            east.addComponent(description);
//        }
//
//        if (itemList.getSize() != 0) {
//            Label nbTasks = new Label("[" + itemList.getSize() + "]");
//            east.addComponent(nbTasks);
//        }
//        east.addComponent(editItemPropertiesButton);
//
////        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
//        cont.addComponent(BorderLayout.EAST, east);
//
//        return cont;
//    }
    /**
     * format: 1a) Prio/Star | Task text | {(#subtasks)] | [DueDate] |
     * [FinishDate] 1b) sub-tree (in SOUTH container)
     *
     * @param content
     * @return
     */
//    protected Container buildItemContainerx(Item item) {
////        Container cont = new Container();
////        cont.setLayout(new BorderLayout());
//        MyDropContainer cont = new MyDropContainer(new BorderLayout(), itemList, item);
////        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
//
//        //EDIT Item in list
//        Button editItemButton = new Button();
//        Command editCmd = new Command(item.getText()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                Item item = (Item) editItemButton.getClientProperty("item");
//                new ScreenItem(item, MyForm.this).show();
////                super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
//            }
//        };
//        editItemButton.setCommand(editCmd);
//        editItemButton.putClientProperty("item", item);
//        editItemButton.setUIID("Label");
//        cont.addComponent(BorderLayout.CENTER, editItemButton);
//
//        Container west = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        if (item.getPriority() != 0) {
//            west.add(new Label(item.getPriority() + ""));
//        } else {
//            west.add(new Label(" "));
//        }
//        cont.addComponent(BorderLayout.WEST, west);
//
//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//
//        //EDIT subtasks in Item
//        Button subTasksButton = new Button();
//        if (item.getItemListSize() != 0) {
//            Command expandSubTasks = new Command("[" + item.getItemListSize() + "]"); // {
////                @Override
////                public void actionPerformed(ActionEvent evt) {
//////                    super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
////
////                }
////            };
//            subTasksButton.setCommand(expandSubTasks);
//            cont.putClientProperty("subTasksButton", subTasksButton);
//            east.addComponent(subTasksButton);
//        }
//        subTasksButton.setUIID("Label");
//
////        east.addComponent(new Label(SimpleDateFormat.new Date(item.getDueDate())));
////        east.addComponent(new Label(new SimpleDateFormat().format(new Date(item.getDueDate()))));
////                        setText(L10NManager.getInstance().formatDateShortStyle((Date)value));
//        if (item.getDueDateD().getTime() != 0) {
//            east.addComponent(new Label(L10NManager.getInstance().formatDateShortStyle(new Date(item.getDueDate()))));
//        }
//
////        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
//        cont.addComponent(BorderLayout.EAST, east);
////        cont.setLeadComponent(east);
//
//        Container bottom = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        bottom.add(new Button("X")); //Edit(?)
//        bottom.add(new Button("Y")); //Create new task below
//        bottom.add(new Button("Z")); //Postpone due date
//        bottom.add(new Button("Z")); //See details of task?
//
////        if (true) {
////            cont.setDraggable(true);
////            return cont;//ignore Swipeable for the moment
////        } else {
////            SwipeableContainer swip = new SwipeableContainer(bottom, cont);
////            swip.addSwipeOpenListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent evt) {
////                    if (swip.isOpenedToRight()) {
////                        item.setDone(true);
////                    }
////                }
////            });
////            swip.setDraggable(true);
////            return swip;
////        }
//        return cont;//ignore Swipeable for the moment
//    }
    /**
     * builds a Container that displayes a list of Items (for use in
     * ScreenItemList as well as to edit subtasks in ScreenItem etc)
     *
     * @param cont
     * @param itemList
     * @return
     */
//    protected Container buildContentPaneForItemList(Container cont, ItemList itemList) {
//    protected Container buildContentPaneForItemList(ItemList itemList) {
//
////        MyTree dt = new MyTree(itemList, (node) -> {
//////            return buildItemContainer((ParseObject)node)
////            if (node instanceof Item) {
////                return buildItemContainer((Item) node);
////            } else if (node instanceof Category) {
////                return buildItemContainer((Category) node);
////            } else { //if (node instanceof ItemList) {
////                return buildItemContainer((ItemList) node);
////            }
////        }
////        );
//        MyTree dt = new MyTree(itemList) {
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Component cmp = containerBuilder.makeContainer(node);
////                Component cmp = null;
////                if (node instanceof Item) {
////                    cmp = buildItemContainer((Item) node);
////                } else if (node instanceof Category) {
////                    cmp = buildItemContainer((Category) node);
//////                } else if (node instanceof WorkSlot) {
//////                    cmp = buildItemContainer((WorkSlot) node);
////                } else { //if (node instanceof ItemList) {
////                    cmp = buildItemContainer((ItemList) node);
////                }
//                cmp.getSelectedStyle().setMargin(LEFT, depth * myDepthIndent);
//                return cmp;
//            }
//        };
//
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cont.setScrollableY(true);
//        cont.add(dt);
////        cont.setDraggable(true);
//        dt.setDropTarget(true);
//        return cont;
//    }
//</editor-fold>
}
