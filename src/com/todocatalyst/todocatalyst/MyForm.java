/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.analytics.AnalyticsService;
//import com.codename1.components.OnOffSwitch;
import com.codename1.compat.java.util.Objects;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.Switch;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import static com.codename1.ui.CN.EAST;
import static com.codename1.ui.CN.WEST;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
//import com.codename1.ui.*;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
//import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.MenuBar;
import com.codename1.ui.SideMenuBar;
import com.codename1.ui.StickyHeader;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.animations.ComponentAnimation;
//import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.spinner.PickerDialog;
import static com.codename1.ui.spinner.PickerDialog.CANCEL_BUTTON_TEXT;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.UITimer;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.MyTree2.KEY_OBJECT;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
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

    //TODO copy graphical format from e.g. lignesd'azur on iPhone
//    protected Map<Object, Runnable> parseIdMap2; // = new HashMap<Object, UpdateField>();
    protected ParseIdMap2 parseIdMap2 = new ParseIdMap2(); // = new HashMap<Object, UpdateField>();
    protected MyForm parentForm;

    public MyForm getParentForm() {
        return parentForm;
    }

    public void setParentForm(MyForm parentForm) {
        this.parentForm = parentForm;
    }
//    protected static Form form;
//    Resources theme;
//    UpdateItemListAfterEditing updateItemListOnDone;
    private Runnable updateActionOnDone;
    private Runnable updateActionOnCancel;
    protected CheckIfDataIsComplete checkDataIsCompleteBeforeExit; // = () -> true; //used to check if a Screen has defined all needed data and returns error message String if not
//    HashSet<ItemAndListCommonInterface> expandedObjects; // = new HashSet(); //TODO!! save expandedObjects for this screen and the given list. NB visible to allow to expland items when subtasks are added
    ExpandedObjects expandedObjects; // = new HashSet(); //TODO!! save expandedObjects for this screen and the given list. NB visible to allow to expland items when subtasks are added
    protected KeepInSameScreenPosition keepPos; // = new KeepInSameScreenPosition();
//    List selectedObjects; //selected objects
    protected ListSelector<Item> selectedObjects; //selected objects
//    private List oldSelectedObjects; //store selection after deactivating
    private ListSelector<Item> oldSelectedObjects; //store selection after deactivating
//    private static boolean showDetailsForAllTasks = false;
//    private static HashSet tasksWithDetailsShown;
    protected HashSet showDetails = new HashSet(); //set of Items etc expanded to show task details
//    protected InlineInsertNewElementContainer lastInsertNewElementContainer;
//    protected InsertNewElementFunc lastInsertNewElementContainer;
//    private TextArea editFieldOnShowOrRefresh;
//    private InsertNewElementFunc pinchInsertContainer;
    private PinchInsertContainer pinchInsertContainer;
//    private BooleanFunction testIfEdit;
    private String uniqueFormId; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    private TextArea startEditingAsyncTextArea;
    protected ScreenType screenType;

    private String showIfEmptyList; //holds Container with actual content, typically MyTree2

    SwipeableContainer openSwipeContainer = null; //stores the currently open SwipeContainer for this screen
    public final static int ANIMATION_TIME_DEFAULT = 300; //in milliseconds
    final static int ANIMATION_TIME_FAST = 150; //in milliseconds
    private Container smallTimer;

    private boolean triggerSaveOnExit;

    /**
     * return true if this screen or a parent form will trigger a save to Parse
     * of the edited element (true if a parent form is already
     * triggerSaveOnExit).
     *
     * @return
     */
    public boolean isTriggerSaveOnExit() {
//        return triggerSaveOnExit;
        if (triggerSaveOnExit) {
            return true;
        } else if (getParentForm() != null) {
            return getParentForm().isTriggerSaveOnExit();
        } else {
            return false; //==triggerSaveOnExit;
        }
    }

    public void setTriggerSaveOnExit(boolean triggerSaveOnExit) {
        this.triggerSaveOnExit = triggerSaveOnExit;
    }

//    private Date editSessionStartTime;
    private MySearchCommand searchCmd;

    static int GAP_LABEL_ICON = 0; //in pixels!

    protected Container newContentContainer; //used to access a just created new contentPane container (before it has been fully added)

    protected MySearchCommand getSearchCmd() {
        return searchCmd;
    }

    protected void setSearchCmd(MySearchCommand searchCommand) {
        searchCmd = searchCommand;
    }

    protected void expandedObjectsInit(String objIdOrOtherUniqueName) {
        expandedObjects = new ExpandedObjects(getUniqueFormId(), objIdOrOtherUniqueName);
    }

    public CheckIfDataIsComplete getCheckIfSaveOnExit() {
//        if (checkDataIsCompleteBeforeExit != null) {
        return checkDataIsCompleteBeforeExit;
//        } else {
//            return () -> true; //always true if no function is defined
//        }
    }

    /**
     * check if data is complete (or otherwise not fit for saving and return the
     * appropriate error message, used to prevent quitting a screen
     *
     * @param errorMsgIfDataIncomplete must return null if OK to exit the
     * screen, otherwise an error message
     */
    public void setCheckIfSaveOnExit(CheckIfDataIsComplete errorMsgIfDataIncomplete) {
        this.checkDataIsCompleteBeforeExit = errorMsgIfDataIncomplete;
    }

    public String getTextToShowIfEmptyList() {
        return showIfEmptyList;
    }

    /**
     * text to show in label shown in empty list. E.g. "Looks like you have no
     * tasks today. Enjoy!" in an empty Today screen.
     *
     * @param showIfEmptyList
     */
    public void setTextToShowIfEmptyList(String showIfEmptyList) {
        this.showIfEmptyList = showIfEmptyList;
    }

    /**
     * set an area will be in active edit mode when the screen is refreshed (eg
     * an inlineInsert container)
     *
     * @param startEditTextArea
     */
    protected void setStartEditingAsyncTextArea(TextArea startEditTextArea) {
        this.startEditingAsyncTextArea = startEditTextArea;
    }

    public TextArea getStartEditingAsyncTextArea() {
        return startEditingAsyncTextArea;
    }

    public String getUniqueFormId() {
        return uniqueFormId != null ? uniqueFormId : (getTitle() != null && getTitle().length() > 0 ? getTitle() : "NoScreenId");
    }

//    public String getUniqueFormIdXXX(String extensionStr) {
//        return getUniqueFormId() + extensionStr;
//    }
    public void setUniqueFormId(String formUniqueId) {
        this.uniqueFormId = formUniqueId;
    }

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
//    public InsertNewElementFunc getPinchInsertContainer() {
    public PinchInsertContainer getPinchInsertContainer() {
        return pinchInsertContainer;
    }

//    public void setPinchInsertContainer(InsertNewElementFunc inlineInsertContainer) {
    public void setPinchInsertContainer(PinchInsertContainer inlineInsertContainer) {
//        setPinchInsertContainer(inlineInsertContainer, false); //false since refreshAfterEdit should always activate the right one
//    }
//
////    public void setPinchInsertContainer(InsertNewElementFunc inlineInsertContainer, boolean startEditingAsyncOnThisCall) {
//    public void setPinchInsertContainer(InlineInsertNewContainer inlineInsertContainer, boolean startEditingAsyncOnThisCall) {
//<editor-fold defaultstate="collapsed" desc="comment">
//if resetting to null, remove previous container with animation
//        if (inlineInsertContainer == null && this.inlineInsertContainer != null && this.inlineInsertContainer instanceof Component) {
//            Container parent = ((Component) this.inlineInsertContainer).getParent();
//            parent.removeComponent((Component) this.inlineInsertContainer);
//            parent.animateLayout(300);
//        }
//        if (inlineInsertContainer == null) { //) && this.inlineInsertContainer != null ) {
//            setEditOnShow(null); //remove old textArea
//        }
//        if (inlineInsertContainer != null && inlineInsertContainer.getTextArea() != null) {
//            setEditOnShow(inlineInsertContainer.getTextArea());
//        }
//</editor-fold>
        this.pinchInsertContainer = inlineInsertContainer;
//        if (this.pinchInsertContainer != null && startEditingAsyncOnThisCall) {
//        if (false && this.pinchInsertContainer != null) {//&& startEditingAsyncOnThisCall) {
//            setStartEditingAsyncTextArea(this.pinchInsertContainer.getTextArea()); //set to ensure it starts up in edit-model
//        }
//        if (this.inlineInsertContainer != null)
////            setStartEditingAsync(this.inlineInsertContainer.getTextArea());
//            setStartEditingAsyncTextArea(inlineInsertContainer.getTextArea()); //set to ensure it starts up in edit-model
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setInlineInsertContainerIfMyTree2(Container contentContainer) {
//        if (contentContainer instanceof MyTree2) {
//            InsertNewElementFunc insertNewElementFunc = ((MyTree2) contentContainer).getInlineInsertField();
//            if (insertNewElementFunc != null) {
//                setInlineInsertContainer(insertNewElementFunc);
//            }
//        }
//    }
//    public void setStartEditingAsyncIfDefined(Container contentContainer) {
//        if (contentContainer instanceof MyTree2) {
//            InsertNewElementFunc insertNewElementFunc = ((MyTree2) contentContainer).getInlineInsertField();
//            if (insertNewElementFunc != null && insertNewElementFunc.getTextArea() != null) {
////                setStartEditingAsync(insertNewElementFunc.getTextArea());
//                insertNewElementFunc.getTextArea().startEditingAsync();
//            }
//        }
//    }

//    public void setEditOnShowOrRefresh(TextArea editFieldOnShowOrRefresh) { //, BooleanFunction testIfEdit) {
//        this.editFieldOnShowOrRefresh = editFieldOnShowOrRefresh;
////        this.testIfEdit = testIfEdit;
//        setEditOnShow(editFieldOnShowOrRefresh);
//    }
//
//    public void setEditOnShowOrRefresh(TextArea editFieldOnShowOrRefresh) {
//        setEditOnShowOrRefresh(editFieldOnShowOrRefresh, null);
//    }
//</editor-fold>
//    String SCREEN_TITLE = "";
    //TODO: move titles into Screens
    static final String SCREEN_LISTS_TITLE = "Lists";
    static final String SCREEN_LISTS_HELP = "Lists";
    static final String SCREEN_ALL_TASKS_TITLE = "All tasks";
    static final String SCREEN_ALL_TASKS_HELP = "Show all tasks and projects. Project subtasks are only shown under their project";
    static final String SCREEN_INBOX_TITLE = "Inbox"; // "Inbox (no owner)" "Inbox"
    static final String SCREEN_INBOX_HELP = "Inbox"; // "Inbox (no owner)" "Inbox"
    static final String SCREEN_PROJECTS_TITLE = "All projects";
    static final String SCREEN_PROJECTS_HELP = "Projects";
    static final String SCREEN_TEMPLATES_TITLE = "Templates";
    static final String SCREEN_TEMPLATES_HELP = "Templates";
    static final String SCREEN_COMPLETION_LOG_TITLE = "Log"; // "Work og", "Completion log", "Completed tasks"
    static final String SCREEN_COMPLETION_LOG_HELP = "Log"; // "Work og", "Completion log", "Completed tasks"
    static final String SCREEN_TEMPLATE_PICKER = "Select Template";
    static final String SCREEN_CREATION_LOG_TITLE = "Diary"; // "Log book", "Creation log", "Created tasks"
    static final String SCREEN_CREATION_LOG_HELP = "Diary"; // "Log book", "Creation log", "Created tasks"
    static final String SCREEN_NEXT_TITLE = "Next"; // "What's next", "Calendar"
    static final String SCREEN_NEXT_HELP = "Have a look at what's up the next month";
    static final String SCREEN_TODAY_TITLE = "Today"; // "Creation log", "Created tasks"
    static final String SCREEN_TODAY_HELP = "What is scheduled for today, due tasks, waiting tasks scheduled for today and tasks that fit today's workslots";
    static final String SCREEN_OVERDUE_TITLE = "Overdue"; // "Creation log", "Created tasks"
    static final String SCREEN_OVERDUE_HELP = "Overdue tasks, you probably want to deal with these before moving on to other tasks"; // "Creation log", "Created tasks"
    static final String SCREEN_TUTORIAL = "Tutorial";
    static final String SCREEN_TUTORIAL_HELP = "Tutorial";
    static final String SCREEN_TOUCHED = "Touched";
    static final String SCREEN_TOUCHED_HELP = "Touched";
    static final String SCREEN_TOUCHED_24H = "Touched last 24h";
    static final String SCREEN_STATISTICS = "Achievements"; //"Statistics", "History"
    static final String SCREEN_IMPROVE = "Improve"; //how to improve, more precise estimates, analysis per type of tasks (respect due date, estimates, split up, allow to become interrupted, ...)
    static final String SCREEN_IMPROVE_HELP = "Shows you insights on how well you do and gives feedback on how you may improve. COMING..."; //how to improve, more precise estimates, analysis per type of tasks (respect due date, estimates, split up, allow to become interrupted, ...)
    static final String SETTINGS_SCREEN_TITLE = "Settings for "; //"Statistics", "History"

    public enum ScreenType {
        NOT_INIT("Not initialized"), ALARMS(ScreenListOfAlarms.screenTitle), LISTS(SCREEN_LISTS_TITLE), ALL_TASKS(SCREEN_ALL_TASKS_TITLE), TODAY(SCREEN_TODAY_TITLE), INBOX(SCREEN_INBOX_TITLE),
        PROJECTS(SCREEN_PROJECTS_TITLE), TEMPLATES(SCREEN_TEMPLATES_TITLE),
        COMPLETION_LOG(SCREEN_COMPLETION_LOG_TITLE), CREATION_LOG(SCREEN_CREATION_LOG_TITLE),
        NEXT(SCREEN_NEXT_TITLE), OVERDUE(SCREEN_OVERDUE_TITLE), TOUCHED(SCREEN_TOUCHED), STATISTICS(SCREEN_STATISTICS);
        private String screenTitle;

        ScreenType(String title) {
            screenTitle = title;
        }

        String getTitle() {
            return screenTitle;
        }

        static ScreenType getScreenType(String screenTitle) {
            for (ScreenType st : ScreenType.values()) {
                if (st.getTitle().equals(screenTitle)) {
                    return st;
                }
            }
            return null;
        }
    }

    protected static final String REPEAT_RULE_KEY = "$REPEAT_RULE$73"; //used to store repeatRules in ParseId2Map so they can be calculated last
    protected static final String SUBTASK_KEY = "$SUBTASK$73"; //used to store repeatRules in ParseId2Map so they can be calculated last

//    public void setAutoSizeModeXXX(boolean on) { //now done when creating dedicated title component
//        if (getToolbar() != null && getToolbar().getTitleComponent() instanceof Label) {
//            ((Label) getToolbar().getTitleComponent()).setAutoSizeMode(true);
//        } else {
//            getTitleComponent().setAutoSizeMode(true);
//        }
//    }
    @Override
    public String toString() {
        return "MyForm " + getTitle() + super.toString();
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
//    MyForm(String title, MyForm previousForm) { //throws ParseException, IOException {
//        this(title, previousForm, () -> {
//        });
//    }
    private UITimer doubleTapTitleTimer;
    private static int TIME_FOR_DOUBLE_TAP = 200; //50 works on simulator, but not on iPhone (probably too short)

    MyForm(ScreenType screenType, MyForm previousForm, Runnable updateActionOnDone) { //throws ParseException, IOException {
        this(screenType.getTitle(), previousForm, updateActionOnDone);
        this.screenType = screenType;

    }

    MyForm(String title, MyForm previousForm, Runnable updateActionOnDone) { //throws ParseException, IOException {
        this(title, previousForm, updateActionOnDone, null);
    }

    MyForm(String title, MyForm previousForm, Runnable updateActionOnDone, Runnable updateActionOnCancel) { //throws ParseException, IOException {
//    MyForm(String title, UpdateField updateActionOnDone) { //throws ParseException, IOException {
//        super(title);
        super();
//        editSessionStartTime = new MyDate(MyDate.currentTimeMillis()); //always track when an editing session was started
        ReplayLog.getInstance().clearCommandsFromPreviousScreen(); //always clear the ReplayCommands from the previous screen!

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
////            String title = getToolbar().getTitleComponent().
//            setToolbar(new Toolbar() {
//                @Override
//                protected void initTitleBarStatus() {
//                    Form f = getComponentForm();
////        if (f != null && !f.shouldPaintStatusBar()) {
////            return;
////        }
//                    if (!shouldPaintStatusBar()) {
//                        return;
//                    }
//                    if (getUIManager().isThemeConstant("paintsTitleBarBool", false)) {
//                        // check if its already added:
//                        Component oldStatusBar = ((BorderLayout) getLayout()).getNorth();
////                        if (((BorderLayout) getLayout()).getNorth() != null) {
//                        if (oldStatusBar != null) {
//                            oldStatusBar.remove();
//                        }
//                        if (true || ((BorderLayout) getLayout()).getNorth() == null) {
////                            Container bar = new Container();
//                            Button bar = new Button(title, "FormTitle") {
//
//                                public void pointerReleased(int x, int y) {
//                                    super.pointerReleased(x, y);
//                                    if (doubleTapTitleTimer == null) {
//                                        doubleTapTitleTimer = UITimer.timer(TIME_FOR_DOUBLE_TAP, false, getComponentForm(), () -> {
//                                            //SINGLE TAP - scroll list to top
//                                            ContainerScrollY cont = findScrollableContYChild(getComponentForm());
//                                            if (cont != null) {
//                                                prevScrollPos = cont.getScrollY();
//                                                Component firstComp = cont.getComponentAt(0); //scroll list to bottom
//                                                if (firstComp != null) {
//                                                    cont.scrollComponentToVisible(firstComp);
//                                                }
//                                            }
//                                            doubleTapTitleTimer = null;
//                                        });
//                                    } else {
//                                        doubleTapTitleTimer.cancel();
//                                        doubleTapTitleTimer = null;
//                                        //DOUBLE TAP - switch to previous position
//                                        //scroll list to bottom //TODO!!! improve so that doubletap scrolls back and forth between top of list and the scroll point
//                                        ContainerScrollY cont = findScrollableContYChild(getComponentForm());
//                                        if (cont != null) {
//                                            int currentScrollPos = cont.getScrollY();
//                                            if (prevDoubleTapPos != -1) {
//                                                cont.setScrollYPublic(prevDoubleTapPos);
////                            prevDoubleTapPos=currentScrollPos;
//                                            } else if (prevScrollPos != -1) {
//                                                cont.setScrollYPublic(prevScrollPos);
//                                            } else {
//                                                prevScrollPos = currentScrollPos;
//                                                if (MyPrefs.firstDoubleTapScrollsToBottomOfScreen.getBoolean()) {
//                                                    int idx = cont.getComponentCount() - 1;
//                                                    if (idx >= 0) {
//                                                        Component lastComp = cont.getComponentAt(idx); //scroll list to bottom
//                                                        if (lastComp != null) {
//                                                            cont.scrollComponentToVisible(lastComp);
//                                                        }
//                                                    }
//                                                } else {
//                                                    if (cont.getComponentCount() > 0) {
//                                                        Component firstComp = cont.getComponentAt(0); //scroll list to bottom
//                                                        if (true || firstComp != null) { //firstComp should never be null?!
//                                                            cont.scrollComponentToVisible(firstComp);
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            prevDoubleTapPos = currentScrollPos;
//                                        }
//                                    }
//                                }
//
//                            };
//                            bar.addLongPressListener((e) -> {
//                                ContainerScrollY cont = findScrollableContYChild(getComponentForm());
//                                if (cont != null) {
//                                    prevScrollPos = cont.getScrollY();
//                                    int idx = cont.getComponentCount() - 1;
//                                    if (idx >= 0) {
//                                        Component lastComp = cont.getComponentAt(idx); //scroll list to bottom
//                                        if (lastComp != null) {
//                                            cont.scrollComponentToVisible(lastComp);
//                                        }
//                                    }
//                                }
//                            });
//                            if (getUIManager().isThemeConstant("landscapeTitleUiidBool", false)) {
//                                bar.setUIID("StatusBar", "StatusBarLandscape");
//                            } else {
//                                bar.setUIID("StatusBar");
//                            }
//                            addComponent(BorderLayout.NORTH, bar);
//                        }
//                    }
//                }
//            });
//        }
//</editor-fold>
        setTitle(title); //must do here to use overridden version of setTitle()
        this.parentForm = previousForm;
        if (false) {
            setCyclicFocus(false); //to avoid Next on keyboard on iPhone?!
        }
//        ReplayLog.getInstance().deleteAllReplayCommandsFromPreviousScreen(title);
//        SCREEN_TITLE = title;
//        if (false) {
//            getToolbar().setTitleCentered(true); //ensure title is centered even when icons are added
//            setTitle(title); //do again since super(title)
//        }
        if (false) {
            setScrollVisible(true); //show scroll bar(?)
        }
//        getToolbar().setTitleCentered(true); //ensure title is centered even when icons are added
        if (false) { //NOT good UI since we have commands in the toolbar
            getToolbar().setScrollOffUponContentPane(true);
            //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One:
            ComponentAnimation title2 = getToolbar().getTitleComponent().createStyleAnimation("Title", 200);
            getAnimationManager().onTitleScrollAnimation(title2);
        }

        initMyStatusBar(); //initialize statusbar to jump to top/bottom/switch position on doubletap
//        if (false) {
//            setAutoSizeMode(true); //ensure title is centered even when icons are added
//        }
//        this.previousForm = previousForm;
//        this.previousForm = getComponentForm();
        addUpdateActionOnDone(updateActionOnDone);
        setUpdateActionOnCancel(updateActionOnCancel);
        setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 200));
//        setTransitionOutAnimator( CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 200));
//        ASSERT.that(updateActionOnDone != null, () -> "doneAction should always be defined, Form=" + this); //NOT necessary, we may set it with setUpdateActionOnDone()
        parseIdMap2.parseIdMapReset();

//        form = new Form();
//        form = this;
//        setup();
//            void setup() {
//        setTactileTouch(true); //enables opening contextmenu on touching a list element??
        setScrollable(false); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        setLayout(new BorderLayout()); //use CENTER to fill the screen correctly with scrolling content (avoid blanc bar at the bottom of the iPhone screen?)
//        setLayout(BoxLayout.y()); //use CENTER to fill the screen correctly with scrolling content (avoid blanc bar at the bottom of the iPhone screen?)
        if (false) {
            setLayout(BoxLayout.y());
            setScrollableY(true); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
            setScrollable(false); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        }

        //<editor-fold defaultstate="collapsed" desc="comment">
//        Component timerContainer = TimerStack.getInstance().getSmallContainer();
//        if (timerContainer != null) {
//            addComponent(CN.SOUTH, timerContainer);
//        }
//        if (true || !(this instanceof ScreenTimer6)) {
//            TimerStack.addSmallTimerWindowIfTimerIsRunning(this);
//        }
        //getToolbar().setTitleComponent(new SpanLabel(title, "FormTitle"));
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
        setMyShowAlarmsReplayCmd(makeAlarmsReplayCmd());
        setBigTimerReplayCmd(makeBigTimerReplayCmd());
        getContentPane().setSafeArea(MyPrefs.enableSafeArea.getBoolean()); //protect scrollbar at bottom of screen from swipe commands

//        getContentPane().setSafeAreaRoot(MyPrefs.enableSafeArea.getBoolean()); //protect scrollbar at bottom of screen from swipe commands
        if (false && Config.TEST) { //TODO!!!! Not sure this is a good idea since it makes the menu and Back button disappear --> maybe 
            //this only works if contentPane is scrollableY (and not BorderLayout as now)
            getToolbar().setScrollOffUponContentPane(MyPrefs.addNewCategoriesToBeginningOfCategoryList.getBoolean()); //see https://github.com/codenameone/CodenameOne/issues/2295
        }
//        if (previousValues!=null&&previousValues.get(MySearchCommand.SEARCH_KEY) != null && getSearchCmd() != null) {
//            getSearchCmd().actionPerformed(null); //re-activate Search, null=>reuse locally stored text
//        }
    }

    int prevScrollPos = -1; //-1 undefined
    int prevDoubleTapPos = -1;
    boolean longStatusBarPress;

    void initMyStatusBar() {
        //NB!! the code below relies on internal implementation sdetails in CN1 Toolbar.initTitleBarStatus() so it may break if that code changes!!
        Layout statusBarLayout = (BorderLayout) getToolbar().getLayout();
        Container statusBar = (Container) ((BorderLayout) getToolbar().getLayout()).getNorth();
        statusBar.setUIID("StatusBarZeroSize");
//        Button bar = new Button("", "FormTitle") {
        Button statusBarButton = new Button("", "StatusBarButton") {

            @Override
            public void pointerReleased(int x, int y) {
                if (longStatusBarPress) { //ignore after long press
                    longStatusBarPress = false;
                    return;
                }
                super.pointerReleased(x, y);
                if (doubleTapTitleTimer == null) {
                    doubleTapTitleTimer = UITimer.timer(TIME_FOR_DOUBLE_TAP, false, getComponentForm(), () -> {
                        //SINGLE TAP - scroll list to top
                        ContainerScrollY cont = findScrollableContYChild(getComponentForm());
                        if (cont != null) {
                            prevScrollPos = cont.getScrollY();
                            Component firstComp = cont.getComponentAt(0); //scroll list to bottom
                            if (firstComp != null) {
                                cont.scrollComponentToVisible(firstComp);
                            }
                        }
                        doubleTapTitleTimer = null;
                    });
                } else {
                    doubleTapTitleTimer.cancel();
                    doubleTapTitleTimer = null;
                    //DOUBLE TAP - switch to previous position
                    //scroll list to bottom //TODO!!! improve so that doubletap scrolls back and forth between top of list and the scroll point
                    ContainerScrollY cont = findScrollableContYChild(getComponentForm());
                    if (cont != null) {
                        int currentScrollPos = cont.getScrollY();
                        if (prevDoubleTapPos != -1) {//if previous doubletap position, use that
                            cont.setScrollYPublic(prevDoubleTapPos);
//                            prevDoubleTapPos=currentScrollPos;
                        } else if (prevScrollPos != -1) { //if no previous doubletap, use the prevScrollPos set by 
                            cont.setScrollYPublic(prevScrollPos);
                        } else {
                            if (currentScrollPos == 0 || MyPrefs.firstDoubleTapScrollsToBottomOfScreen.getBoolean()) { //currentScrollPos==0 => if already at top, scroll to bottom
                                //scroll to bottom
                                int idx = cont.getComponentCount() - 1;
                                if (idx >= 0) {
                                    Component lastComp = cont.getComponentAt(idx); //scroll list to bottom
                                    if (lastComp != null) {
                                        cont.scrollComponentToVisible(lastComp);
                                    }
                                }
                            } else {
                                if (cont.getComponentCount() > 0) {
                                    Component firstComp = cont.getComponentAt(0); //scroll list to bottom
                                    if (true || firstComp != null) { //firstComp should never be null?!
                                        cont.scrollComponentToVisible(firstComp);
                                    }
                                }
                            }
                        }
                        prevDoubleTapPos = currentScrollPos;
                    }
                }
            }

            /**
             * If this Component is focused this method is invoked when the user
             * presses and holds the pointer on the Component
             *
             */
            @Override
            public void longPointerPress(int x, int y) {
                //orginal code from Component.longPointerPress(int x, int y)
//                if (longPressListeners != null && longPressListeners.hasListeners()) {
//                    ActionEvent ev = new ActionEvent(this, ActionEvent.Type.LongPointerPress, x, y);
//                    longPressListeners.fireActionEvent(ev);
//                    if (ev.isConsumed()) {
//                        return;
//                    }
//                }
                ContainerScrollY cont = findScrollableContYChild(getComponentForm());
                if (cont != null) {
                    longStatusBarPress = true;
                    prevScrollPos = cont.getScrollY();
                    int idx = cont.getComponentCount() - 1;
                    if (idx >= 0) {
                        Component lastComp = cont.getComponentAt(idx); //scroll list to bottom
                        if (lastComp != null) {
                            cont.scrollComponentToVisible(lastComp);
                        }
                    }
                }
            }
        };
        statusBarButton.setShowEvenIfBlank(true);
        statusBar.setLayout(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
        statusBar.add(BorderLayout.CENTER, statusBarButton);
        if (false) {
            statusBar.setLeadComponent(statusBarButton);
        }

        if (false) {
            statusBarButton.addLongPressListener((e) -> {
                ContainerScrollY cont = findScrollableContYChild(getComponentForm());
                if (cont != null) {
                    prevScrollPos = cont.getScrollY();
                    int idx = cont.getComponentCount() - 1;
                    if (idx >= 0) {
                        Component lastComp = cont.getComponentAt(idx); //scroll list to bottom
                        if (lastComp != null) {
                            cont.scrollComponentToVisible(lastComp);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void setTitle(String title) {
        if (false) {
            super.setTitle(title);
        } else {
//        Label titleComponent = new Label(getTitle(),"FormTitle") {
            Button titleComponent = new Button(title, "FormTitle");
            if (false) {
                titleComponent = new Button(title, "FormTitle") {

                    public void pointerReleased(int x, int y) {
                        super.pointerReleased(x, y);
                        if (doubleTapTitleTimer == null) {
                            doubleTapTitleTimer = UITimer.timer(TIME_FOR_DOUBLE_TAP, false, getComponentForm(), () -> {
                                //SINGLE TAP - scroll list to top
                                ContainerScrollY cont = findScrollableContYChild(getComponentForm());
                                if (cont != null) {
                                    prevScrollPos = cont.getScrollY();
                                    Component firstComp = cont.getComponentAt(0); //scroll list to bottom
                                    if (firstComp != null) {
                                        cont.scrollComponentToVisible(firstComp);
                                    }
                                }
                                doubleTapTitleTimer = null;
                            });
                        } else {
                            doubleTapTitleTimer.cancel();
                            doubleTapTitleTimer = null;
                            //DOUBLE TAP - switch to previous position
                            //scroll list to bottom //TODO!!! improve so that doubletap scrolls back and forth between top of list and the scroll point
                            ContainerScrollY cont = findScrollableContYChild(getComponentForm());
                            if (cont != null) {
                                int currentScrollPos = cont.getScrollY();
                                if (prevDoubleTapPos != -1) {
                                    cont.setScrollYPublic(prevDoubleTapPos);
//                            prevDoubleTapPos=currentScrollPos;
                                } else if (prevScrollPos != -1) {
                                    cont.setScrollYPublic(prevScrollPos);
                                } else {
                                    prevScrollPos = currentScrollPos;
                                    if (MyPrefs.firstDoubleTapScrollsToBottomOfScreen.getBoolean()) {
                                        int idx = cont.getComponentCount() - 1;
                                        if (idx >= 0) {
                                            Component lastComp = cont.getComponentAt(idx); //scroll list to bottom
                                            if (lastComp != null) {
                                                cont.scrollComponentToVisible(lastComp);
                                            }
                                        }
                                    } else {
                                        if (cont.getComponentCount() > 0) {
                                            Component firstComp = cont.getComponentAt(0); //scroll list to bottom
                                            if (true || firstComp != null) { //firstComp should never be null?!
                                                cont.scrollComponentToVisible(firstComp);
                                            }
                                        }
                                    }
                                }
                                prevDoubleTapPos = currentScrollPos;
                            }
                        }
                    }

//                @Override
//                public void longPointerPress(int x, int y) {
//                    super.longPointerPress(x, y);
//                    ContainerScrollY cont = findScrollableContYChild(getComponentForm());
//                    if (cont != null) {
//                        prevScrollPos = cont.getScrollY();
//                        int idx = cont.getComponentCount() - 1;
//                        if (idx >= 0) {
//                            Component lastComp = cont.getComponentAt(idx); //scroll list to bottom
//                            if (lastComp != null) {
//                                cont.scrollComponentToVisible(lastComp);
//                            }
//                        }
//                    }
//                }
                };
            }
            if (false) {
                titleComponent.addLongPressListener((e) -> {
                    ContainerScrollY cont = findScrollableContYChild(getComponentForm());
                    if (cont != null) {
                        prevScrollPos = cont.getScrollY();
                        int idx = cont.getComponentCount() - 1;
                        if (idx >= 0) {
                            Component lastComp = cont.getComponentAt(idx); //scroll list to bottom
                            if (lastComp != null) {
                                cont.scrollComponentToVisible(lastComp);
                            }
                        }
                    }
                });
            }
            titleComponent.setAutoSizeMode(MyPrefs.titleAutoSize.getBoolean());
//        titleComponent.setMinAutoSize(2); //TODO!!!!! pull request to add this to CN1
            titleComponent.setVerticalAlignment(Component.CENTER);
            getToolbar().setTitleComponent(titleComponent);
            getToolbar().setTitleCentered(true);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public String getTitle() {
//        if (getToolbar() != null && getToolbar().getTitleComponent() instanceof Label)
//            return ((Label) getToolbar().getTitleComponent()).getText();
////        else return "";
//        else return super.getTitle();
//    }
//    @Override
//    public Label getTitleComponent() {
//        if (getToolbar() != null && getToolbar().getTitleComponent() instanceof Label)
//            return ((Label) getToolbar().getTitleComponent());
//        else return super.getTitleComponent();
//    }
//</editor-fold>
    protected void setTitleAnimation(Container scrollableComponent) {
        //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One:
        if (false) {
            ComponentAnimation title2 = getToolbar().getTitleComponent().createStyleAnimation("TitleSmall", 200);
            getAnimationManager().onTitleScrollAnimation(scrollableComponent, title2);
        }
    }

    /**
     * Allows subclasses to disable the global toolbar for a specific form by
     * overriding this method
     */
    @Override
    protected void initGlobalToolbar() {
        if (Toolbar.isGlobalToolbar()) {
            setToolbar(new Toolbar() {
                protected void initTitleBarStatus() {
//        Form f = getComponentForm();
//        if (f != null && !f.shouldPaintStatusBar()) {
//            return;
//        }
//        if (getUIManager().isThemeConstant("paintsTitleBarBool", false)) {
                    if (!MyPrefs.hideStatusBar.getBoolean()) {
                        // check if its already added:
                        if (((BorderLayout) getLayout()).getNorth() == null) {
                            Container bar = new Container();
                            if (getUIManager().isThemeConstant("landscapeTitleUiidBool", false)) {
                                bar.setUIID("StatusBar", "StatusBarLandscape");
                            } else {
                                bar.setUIID("StatusBar");
                            }
                            addComponent(BorderLayout.NORTH, bar);
                        }
                    }
                }

            });
        }
    }

    protected void setKeepPos(KeepInSameScreenPosition keepPos) {
        this.keepPos = keepPos;
//        previousValues.put(KEEP_POS_KEY,keepPos);
    }

    protected void setKeepPos() {
        setKeepPos(new KeepInSameScreenPosition());
    }

    protected KeepInSameScreenPosition getKeepPos() {
        return keepPos;
    }

//    private static String KEEP_POS_KEY = "KeepPos";
    protected boolean restoreKeepPos() {
        if (Config.TEST) {
            Log.p("calling MyForm.restoreKeepPos(), with keepPos=" + keepPos);
        }
        if (keepPos != null) {
            keepPos.setNewScrollYPosition();
            keepPos = null;
            return true;
        }
        return false;
//        else if (previousValues.get(KEEP_POS_KEY)!=null)
//            this.keepPos.setNewScrollYPosition();
    }

    @Override
    public void revalidate() {
        super.revalidate();
//        Log.p("REVALIDATE for form=" + getUniqueFormId());
        ASSERT.that(true, "REVALIDATE for form=" + getUniqueFormId());
    }

    protected ScreenType getScreenType() {
        return screenType == null ? ScreenType.NOT_INIT : screenType; //avoid returning null
    }

    protected void setScreenType(ScreenType screenType) {
        this.screenType = screenType;
    }

    /**
     * returns the container in which to add the smallTimer, can be overridden
     * to place the smallTimer in other places than the default South container.
     *
     * @return
     */
    public Container getContainerForSmallTimer() {
        Container containerForSmallTimer = null;
        Form form = this;
        if ((form instanceof ScreenCategoryPicker //|| form instanceof ScreenListOfAlarms
                || form instanceof ScreenLogin || form instanceof ScreenObjectPicker
                || form instanceof ScreenRepair || form instanceof ScreenTimer6)) {
            return null;
        } else {
            Container formContentPane = form.getContentPane();
            Layout contentPaneLayout = formContentPane.getLayout();
            if (contentPaneLayout instanceof BorderLayout) {
//                timerContainer = getContentPaneSouth(form);
                Component southComponent = ((BorderLayout) contentPaneLayout).getSouth();
                if (southComponent instanceof Container) {
                    containerForSmallTimer = (Container) southComponent;
                } else if (southComponent == null) {
                    Container newCont = new Container(BoxLayout.y());
                    formContentPane.add(BorderLayout.SOUTH, newCont);
                    containerForSmallTimer = newCont;
                }
            } //else: nothing, only BorderLayout can currently show a smallTimer in the south container
        }
        return containerForSmallTimer;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container getSmallTimerCont(Container cont) {
//        if (cont == null) {
//            return null;
//        } else {
//            if (Boolean.TRUE.equals(cont.getClientProperty(TimerStack.SMALL_TIMER_CONTAINER_ID))) {
//                return cont;
//            } else {
//                for (Component c : cont.getChildrenAsList(true)) {
//                    if (Boolean.TRUE.equals(c.getClientProperty(TimerStack.SMALL_TIMER_CONTAINER_ID))) {
//                        return (Container) c;
//                    } else if (c instanceof Container) {
//                        Container tc = getSmallTimerCont((Container) c);
//                        if (tc != null) {
//                            return (Container) tc;
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//    public Container getSmallTimerCont() {
//        return getSmallTimerCont(getContainerForSmallTimer());
//    }
//    public Container getSmallTimerContOLD() {
//        Container cont = getContainerForSmallTimer();
//        if (cont == null) {
//            return null;
//        } else {
//            if (cont.getComponentCount() == 0) {
//                return null;
//            } else if (cont.getComponentCount() == 1) {
//                return (Container) cont.getComponentAt(0);
//            } else {
//                if (Config.PROD_LOG) {
//                    ASSERT.that(false, "more than one component in containerForSmallTimer");
//                }
//            }
//        }
//        return null;
//    }
//</editor-fold>
    public boolean removeSmallTimerCont() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container smallTimerCont = getSmallTimerCont();
//        if (smallTimerCont != null) {
//            smallTimerCont.remove();
//            return true;
//        }
//        return false;
//</editor-fold>
        if (smallTimer != null) {
            smallTimer.remove();
            return true;
        } else {
            return false;
        }
    }

    public boolean addSmallTimerCont(Container smallTimer) {
        removeSmallTimerCont(); //remove previous in case
        this.smallTimer = smallTimer;
        Container smallTimerCont = getContainerForSmallTimer();
        if (smallTimerCont != null) {
            if (smallTimerCont.getLayout() instanceof BorderLayout) {
                smallTimerCont.add(BorderLayout.SOUTH, smallTimer);
            } else {
                smallTimerCont.add(smallTimer);
            }
            return true;
        }
        return false;
    }

    interface GetParseValue {

        void saveEditedValueInParseObject();
    }

    /**
     * clears/resets/reinitializes the parseIdMap2
     */
//    public void parseIdMapResetXXX() {
//        parseIdMap2 = new HashMap<Object, Runnable>();
//    }
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

    interface UpdateItemListAfterEditing {

        /**
         *
         * @param itemList
         */
//        void update(ItemList itemList);
        void update(ItemAndListCommonInterface itemList);
    }

//    interface GetWorkSlotList {
//        void update(List<WorkSlot> workSlotList);
////        void update(List workSlotList);
//    }
    interface FetchWorkSlotList {

//        List<WorkSlot> getUpdatedWorkSlotList(Object objworkSlotList);
//        List<WorkSlot> getUpdatedWorkSlotList();
        WorkSlotList getUpdatedWorkSlotList();
//        void update(List workSlotList);
    }

//    interface Runnable {
//
//        void run();
//    }
    public interface CheckIfDataIsComplete {

        /**
         * return error message if data in this screen is not complete,
         * otherwise null
         *
         * @return
         */
        boolean check();
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

    interface GetStringFrom {

        String get(Object input);
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

    interface GetLong {

        long get();
    }

    interface PutLong {

        void accept(long l);
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

    interface GetItemListFct {

//        ItemList getUpdatedItemList();
        ItemAndListCommonInterface getUpdatedItemList();
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
    static void showDialogSetWaitingDateAndAlarmIfAppropriate(Item item) {
        if (MyPrefs.waitingAskToSetWaitingDateWhenMarkingTaskWaiting.getBoolean()) {
            //                || (item.getWaitingTillDate().getTime() != 0 && item.getWaitingAlarmDate().getTime() != 0)) {
//                || (item.getWaitingTillDate().getTime() < MyDate.currentTimeMillis() || item.getWaitingAlarmDate().getTime() < MyDate.currentTimeMillis())) {

            new PickerDialog("Set Waiting", "Waiting tasks are automatically hidden until the set date.",
                    Format.f("Set a {0}", Item.WAIT_UNTIL_DATE),
                    item.getWaitingTillDate(),
                    new MyDate(MyDate.currentTimeMillis() + MyPrefs.itemWaitingDateDefaultDaysAheadInTime.getInt() * MyDate.DAY_IN_MILLISECONDS),
                    Format.f("Set a {0}", Item.WAITING_ALARM_DATE), item.getWaitingAlarmDate(),
                    d -> item.setWaitingTillDate(d), d -> item.setWaitingAlarmDate(d),
                    (d) -> d != null && d.getTime() != 0
                    //if a waiting date is defined, set alarm default days before, 
                    //UI: it is OK to set WaitingDate in the past (item will not be hidden), or alarmDate in the past (alarm will just never be activated)
                    //UI: if no waitingTillDate is set, the alarm Date will be set to defaultWaitDaysAhead-defaultAlarmDaysBeforeWaitingDate
                    //                    ? new MyDate(Math.max(MyDate.currentTimeMillis(), d.getTime() - MyPrefs.itemWaitingAlarmDefaultDaysBeforeWaitingDate.getInt() * MyDate.DAY_IN_MILLISECONDS))
                    ? new MyDate(d.getTime() - MyPrefs.itemWaitingAlarmDefaultDaysBeforeWaitingDate.getInt() * MyDate.DAY_IN_MILLISECONDS)
                    : new MyDate(MyDate.currentTimeMillis() + ((MyPrefs.itemWaitingDateDefaultDaysAheadInTime.getInt() - MyPrefs.itemWaitingAlarmDefaultDaysBeforeWaitingDate.getInt()) * MyDate.DAY_IN_MILLISECONDS))
            ).show();
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static void showDialogSetWaitingDateAndAlarmIfAppropriateOLD(Item item) {
//        if (!MyPrefs.waitingAskToSetWaitingDateWhenMarkingTaskWaiting.getBoolean()
//                || (item.getWaitingTillDate().getTime() != 0 && item.getWaitingAlarmDate().getTime() != 0)) {
//            return; //do nothing if both waiting dates are already set
//        }
////        Map<Object, Runnable> parseIdMap2 = new HashMap<Object, Runnable>();
//        Dialog dia = new Dialog();
//        dia.setTitle("Set Waiting");
//        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.setCommandsAsButtons(true);
//        dia.setAutoDispose(true); //should be default according to javadoc, but doesn't autodispose on [OK]
//
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.add(cont);
////<editor-fold defaultstate="collapsed" desc="comment">
////        Picker p = new Picker();
////        MyDateAndTimePicker waitingDatePicker = new MyDateAndTimePicker("<set date>", parseIdMap2, () -> {
////        MyDatePicker waitingDatePicker = new MyDatePicker("<set date>", parseIdMap2, () -> {
//////            return new Date(item.getWaitingTillDate());
////            return item.getWaitingTillDateD();
////        }, (d) -> {
//////            item.setWaitingTillDate(d.getTime());
////            item.setWaitingTillDate(d);
////        });
////</editor-fold>
//        MyDatePicker waitingDatePicker = new MyDatePicker(item.getWaitingTillDate(), "<set date>");
//        waitingDatePicker.addActionListener((e) -> item.setWaitingTillDate(waitingDatePicker.getDate()));
////        cont.add(new Label("Wait until")).add(waitingDatePicker).add("When you set a date, waiting tasks can automatically be hidden until that date.");
//        cont.add(new Label(Item.WAIT_UNTIL_DATE)).add(waitingDatePicker).add(new SpanLabel("Waiting tasks are automatically hidden until the set date."));
////<editor-fold defaultstate="collapsed" desc="comment">
////        MyDateAndTimePicker waitingAlarmPicker = new MyDateAndTimePicker("<set date>", parseIdMap2, () -> {
//////            return new Date(item.getWaitingAlarmDate());
////            return item.getWaitingAlarmDateD();
////        }, (d) -> {
//////            item.setWaitingAlarmDate(d.getTime());
////            item.setWaitingAlarmDate(d); //NB. only called if date is edited to sth different than 0
////        });
////</editor-fold>
//        MyDateAndTimePicker waitingAlarmPicker = new MyDateAndTimePicker(item.getWaitingAlarmDate(), "<set date>");
//        waitingAlarmPicker.addActionListener((e) -> item.setWaitingAlarmDate(waitingAlarmPicker.getDate()));
////        cont.add(new Label("Waiting alarm")).add(waitingAlarmPicker).add("Set a special alarm for waiting tasks.");
//        cont.add(new Label(Item.WAITING_ALARM_DATE)).add(waitingAlarmPicker).add(new SpanLabel("Set a reminder to follow up on a waiting task."));
//
//        cont.addComponent(new Button(Command.create("OK", null, (e) -> {
////            putEditedValues2(parseIdMap2);
//            dia.dispose(); //close dialog
//        })));
//        dia.show();
////        return dia;
//    }
//</editor-fold>
    static Long showDialogUpdateActualTimeIfAppropriate(long actualCurrent) {
        if (MyPrefs.askToEnterActualIfMarkingTaskDoneOutsideTimer.getBoolean()) {
            PickerDialog dia = new PickerDialog(Format.f("Set {0 Actual effort}", Item.EFFORT_ACTUAL),
                    Format.f("Enter how much {0 actual effort} for this task?", Item.EFFORT_ACTUAL),
                    actualCurrent);
            return (Long) dia.show();
        } else {
            return null;
        }

    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static void showDialogUpdateActualTimeIfAppropriateOLD(Item item) {
//        if (false && (item.isDone()
//                || !(MyPrefs.askToEnterActualIfMarkingTaskDoneOutsideTimer.getBoolean() //                || (MyPrefs.askToEnterActualIfMarkingTaskDoneOutsideTimerOnlyWhenActualIsZeroXXX.getBoolean() && item.getActual() == 0)))
//                ))) {
//            return; //do nothing if item is done, or settings/conditions not fulfilled
//        }
////        Map<Object, Runnable> parseIdMap2 = new HashMap<Object, Runnable>();
//        Dialog dia = new Dialog();
//        dia.setTitle(Format.f("Set {0 Actual effort}", Item.EFFORT_ACTUAL));
//        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.setCommandsAsButtons(true);
//        dia.setAutoDispose(true); //should be default according to javadoc, but doesn't autodispose on [OK]
//
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.add(cont);
//
//        cont.add(new SpanLabel(Format.f("Enter how much {0 actual effort} for this task?", Item.EFFORT_ACTUAL)));
//
//        //TODO!!!! if marking a project, with undone subtasks, Done, then also show sum of subtask actuals to know how much time was spend on them
////        MyDurationPicker actualPicker = new MyDurationPicker(item.getActualForProjectTaskItself(), "0:00");
//        Picker actualPicker = new Picker();
//        actualPicker.setType(Display.PICKER_TYPE_DURATION);
//        actualPicker.setDuration(item.getActualForProjectTaskItself());
////        actualPicker.set
////        Picker actualPicker = new Picker(item.getActualForProjectTaskItself());
//        actualPicker.setUseLightweightPopup(true);
//        actualPicker.addActionListener((e) -> {
//            item.setActual(actualPicker.getDuration(), false); //false, since this dialog is ONLY called when setting a task status, so no reason
//            actualPicker.stopEditing(null);
//            dia.dispose(); //dispose of dialog on Done on picker
//        });
////<editor-fold defaultstate="collapsed" desc="comment">
////        }, (l) -> {
//////            item.setActualEffort(d*MyDate.MINUTE_IN_MILLISECONDS);
////            item.setActualEffort(l);
////        });
////                new Label(Item.EFFORT_ACTUAL)).add(actualPicker).
////                .add(new SpanLabel("How much time was spend on this task?"));
////                .add(new SpanLabel("Click to set how much time was spend on this task"));
//
////        cont.addComponent(new Button(Command.create("OK", null, (e) -> {
////</editor-fold>
////        cont.addComponent(new Button(Command.create("Cancel", null, (e) -> {
//        cont.add(actualPicker);
//        cont.addComponent(new Button(Command.create("Skip", null, (e) -> {
////            putEditedValues2(parseIdMap2);
//            actualPicker.stopEditing(null); //close picker
//            dia.dispose(); //close dialog
//        })));
//        dia.show();
//        actualPicker.startEditingAsync();
//    }
//</editor-fold>
    /**
     * returns the dueDate in dueDate, or sets it to 0 is skipped
     *
     * @param dueDate
     */
    static Date showDialogSetDueDate(Date dueDate) {
        assert dueDate != null;

        PickerDialog dia = new PickerDialog(Format.f("Set {0 due date}", Item.DUE_DATE),
                Format.f("This template has dates defined relative to {0 due date}, enter a {0} to use them or {1 Cancel}", Item.DUE_DATE, PickerDialog.CANCEL_BUTTON_TEXT),
                dueDate);
        return (Date) dia.show();
    }

    static boolean showDialogCannotSaveNow(Item item) {
        return Dialog.show("Cannot save now",
                "Previous unsaved changes to this task must be saved before updating the task. "
                + "Probably due to no network or slow connection. Please try again later.", "OK", null);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static void showDialogSetDueDateOLD(Date dueDate) {
//        assert dueDate != null;
//        Dialog dia = new Dialog();
//        //"Set "+Item.DUE_DATE, "This template has dates relative to {0 due date}, please set {0}   SUBTASK_KEY, this, cmds);
//        dia.setTitle(Format.f("Set {0 due date}", Item.DUE_DATE));
//        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.setCommandsAsButtons(true);
//        dia.setAutoDispose(true); //should be default according to javadoc, but doesn't autodispose on [OK]
//        dia.growOrShrink();
//
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.add(cont);
//
//        String popupText = "The template has fields that are set relative to due date but no due date is set for. If you set a due date now, the dependent fields will be updated, otherwise they will be ignored. Next time you use this template you can set a due date before inserting the template";
//        cont.add(new SpanLabel(Format.f("This template has dates relative to {0 due date}, enter a {0} ", Item.DUE_DATE)));
//
////        MyDateAndTimePicker dueDatePicker = new MyDateAndTimePicker(dueDate, "<set date>");
//        Picker dueDatePicker = new Picker();
//        dueDatePicker.setType(Display.PICKER_TYPE_DATE_AND_TIME);
//        dueDatePicker.setDate(dueDate);
////        actualPicker.set
////        Picker actualPicker = new Picker(item.getActualForProjectTaskItself());
//        dueDatePicker.setUseLightweightPopup(true);
//
//        dueDatePicker.addActionListener((e) -> {
//            dueDatePicker.stopEditing(() -> dia.dispose());
////            dia.dispose(); //dispose of dialog on Done on picker
//        });
//        cont.add(dueDatePicker);
//        cont.addComponent(new Button(Command.create("Skip", null, (e) -> {
////            dueDatePicker.stopEditing(null); //close picker
//            dueDatePicker.stopEditing(() -> dia.dispose()); //close picker
////            dia.dispose(); //close dialog
//            dueDate.setTime(0);
//        })));
////        dueDatePicker.startEditingAsync();
//        dia.show();
//    }
//</editor-fold>
    static Dialog showDialogUpdateRemainingTime(MyDurationPicker remainingTimePicker) {
        Dialog dia = new Dialog();
        dia.setTitle(Format.f("Update {0 remaining effort}", Item.EFFORT_REMAINING));
        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        dia.setCommandsAsButtons(true);
        dia.setAutoDispose(true); //should be default according to javadoc, but doesn't autodispose on [OK]

        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        dia.add(cont);

//        if (remainingTimePicker.getParent() != null) {
////            repaint(); //see Java doc for removeComponent 
//            remainingTimePicker.getParent().removeComponent(remainingTimePicker);
//        }
        remainingTimePicker.remove();
        cont.add(makeHelpButton(Item.EFFORT_REMAINING, "**"));
        cont.add(remainingTimePicker); //.add(new SpanLabel("Waiting tasks are automatically hidden until the set date."));

        cont.addComponent(new Button(Command.create("OK", null, (e) -> {
            dia.dispose(); //close dialog
        })));
        return dia;
    }

    static void showDialogUpdateRemainingTimeXXXhow(MyDurationPicker remainingTimePicker) {
        if (MyPrefs.timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem.getBoolean()) {
            showDialogUpdateRemainingTime(remainingTimePicker).show();
        }
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
//            item.setRemainingEffortXXX(d * MyDate.MINUTE_IN_MILLISECONDS);
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

    static int COLUMNS_FOR_STRING = 20;

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

//<editor-fold defaultstate="collapsed" desc="comment">
//            for (String parseId : parseIdMap2.keySet()) {
//    protected static void putEditedValues2OLD(Map<Object, Runnable> parseIdMap2) {
//////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
////                parseIdMap.get(parseId).saveEditedValueInParseObject();
////            }
//        //Log.p("putEditedValues2 - saving edited element, parseIdMap2=" + parseIdMap2);
//        ASSERT.that(parseIdMap2 != null);
//        if (parseIdMap2 != null) {
//            Runnable repeatRule = parseIdMap2.remove(REPEAT_RULE_KEY); //set a repeatRule aside for execution last (after restoring all fields)
////            UpdateField repeatRule = parseIdMap2.remove(Item.PARSE_REPEAT_RULE); //set a repeatRule aside for execution last (after restoring all fields)
//            if (false && repeatRule != null) {
//                DAO.getInstance().saveInBackground((ParseObject) repeatRule); //MUST save before saving Item, since item will reference a new repeatRule
//            }
//
//            for (Object parseId : parseIdMap2.keySet()) {
////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
//                parseIdMap2.get(parseId).run();
//            }
//            if (repeatRule != null) {
//                repeatRule.run();
//            }
//        }
//    }
//    private static void putEditedValues2(Map<Object, Runnable> parseIdMap2, ParseObject parseObject) {
//    public static void putEditedValues2XXX(Map<Object, Runnable> parseIdMap2) {
//////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
////                parseIdMap.get(parseId).saveEditedValueInParseObject();
////            }
//        //Log.p("putEditedValues2 - saving edited element, parseIdMap2=" + parseIdMap2);
////        ASSERT.that(parseIdMap2 != null);
////        if (parseIdMap2 != null) {
//        Runnable repeatRule = null;
//        if (false) {
//            repeatRule = parseIdMap2.remove(REPEAT_RULE_KEY);
//        } //set a repeatRule aside for execution last (after restoring all fields)
////            UpdateField repeatRule = parseIdMap2.remove(Item.PARSE_REPEAT_RULE); //set a repeatRule aside for execution last (after restoring all fields)
////            if (false && repeatRule != null) {
////                DAO.getInstance().saveInBackground((ParseObject) repeatRule); //MUST save before saving Item, since item will reference a new repeatRule
////            }
//
//        for (Object parseId : parseIdMap2.keySet()) {
////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
//            parseIdMap2.get(parseId).run();
//        }
//        if (repeatRule != null) {
////            if (parseObject != null && parseObject.getObjectIdP() == null)
////                DAO.getInstance().saveInBackground(parseObject); //if not saved
//            repeatRule.run();
//        }
////        }
//    }
//    protected static void putEditedValues2(Map<Object, Runnable> parseIdMap2) {
//        putEditedValues2(parseIdMap2, null);
//    }
    /**
     * will iterate over all the fields in parseIdMap and call the stored lambda
     * functions to update the corresponding fields in the edited ParseObject
     *
     * @param parseObject will save parseObject first if new object
     */
//    protected static void putEditedValues2XXX(Map<Object, Runnable> parseIdMap2, ParseObject parseObject) {
//        if (false && parseObject.getObjectIdP() == null) {
//            DAO.getInstance().saveInBackground(parseObject); //TODO!!! why is it necessary to save here??
//        }
//        putEditedValues2(parseIdMap2);
//    }
//    protected void putEditedValues2XXX() {
//        putEditedValues2(parseIdMap2);
//    }
//</editor-fold>
    /**
     *
     * @param updateActionOnDone Runnable to add to actions when exiting Screen
     * with Done (Back), if null, no effect
     */
    void addUpdateActionOnDone(Runnable updateActionOnDone) {
        if (Config.TEST) {
            ASSERT.that(updateActionOnDone == null || this.updateActionOnDone == null, "Setting updateActionOnDone twice, old="
                    + this.updateActionOnDone + "; new=" + updateActionOnDone);
        }
        if (updateActionOnDone != null) {
            if (this.updateActionOnDone == null) {
                this.updateActionOnDone = updateActionOnDone;
            } else {
                this.updateActionOnDone = () -> {
                    this.updateActionOnDone.run();
                    updateActionOnDone.run();
                };
            }
        }
    }

    Runnable getUpdateActionOnDone() {
        return updateActionOnDone;
    }

    void setUpdateActionOnCancel(Runnable updateActionOnCancel) {
        if (Config.TEST) {
            ASSERT.that(updateActionOnCancel == null || this.updateActionOnCancel == null, "Setting updateActionOnCancel twice, old="
                    + this.updateActionOnCancel + "; new=" + updateActionOnCancel);
        }
        this.updateActionOnCancel = updateActionOnCancel;
    }

    Runnable getUpdateActionOnCancel() {
        return updateActionOnCancel;
    }

//    void addToContentContainerXXX(Component content) {
//        getContentPane().add(BorderLayout.CENTER, content);
//    }
    /**
     * returns the main content container (as seen by the app, whatever is used
     * to received the list components)
     *
     * @return
     */
    Container getContentContainer() {
        Container container = getContentPane();
//        if (container.getLayout() instanceof BorderLayout) {
//            return (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
        while (container.getLayout() instanceof BorderLayout) { //we may have multiple 
            container = (Container) ((BorderLayout) container.getLayout()).getCenter();
        }
//        } else {
//            if (Config.TEST) {
//                ASSERT.that("unexpected content container type/oayout, for contentPane=" + container);
//            }
//            return (Container) contentPane.getComponentAt(0);
        return container;
    }

    /**
     *
     * @param toolbar
     */
    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {
        // makeMyShowAlarmsReplayCmd(); //add the  //NOW done in MyForm init
    }

    /**
     * used to refresh the screen after (major) edits. E.g. either revalidate if
     * only format has been changed or rebuild content pane if the data has been
     * changed beyond what updating a single container can help (e.g. if a
     * subtask was edited impating both the project and possibly the work time
     * of the entire list)
     */
    public void refreshAfterEdit() {
        if (false && Config.TEST) {
            ASSERT.that(false, "Calling RefreshAfterEdit");
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (editFieldOnShowOrRefresh != null) { // && (testIfEdit == null || testIfEdit.test())) {
//        if (false && getPinchInsertContainer() != null && getPinchInsertContainer().getTextArea() != null) { // && (testIfEdit == null || testIfEdit.test())) {
////            editFieldOnShowOrRefresh.startEditingAsync();
//            getPinchInsertContainer().getTextArea().startEditingAsync();
//        }
//        TimerStack.addSmallTimerWindowIfTimerIsRunning(this);
//        if (true || !(this instanceof ScreenTimer6)) //don't refresh if Timer6 is being shown
//        if (!ReplayLog.getInstance().isReplayInProgress()) //don't refresh (which may show big timer) if replay is still ongoing
//        if (!ReplayLog.getInstance().isReplayInProgress() || ReplayLog.getInstance().isReplayAtLastCommand()) //don't refresh (which may show big timer) if replay is still ongoing
//        TimerStack.getInstance().refreshOrShowTimerUI(this); //add smallTimer if relevant. Pass 'this' since currentForm may be the previous (currently shown) form
//        TimerStack.getInstance().refreshOrShowSmallTimerUI(this); //add smallTimer if relevant. Pass 'this' since currentForm may be the previous (currently shown) form
//</editor-fold>
        TimerStack.getInstance().refreshOrShowSmallTimerUI(this); //add smallTimer if relevant. Pass 'this' since currentForm may be the previous (currently shown) form

        if (true) {
            revalidateWithAnimationSafety();
//            restoreKeepPos();
            if (false) {
                if (getStartEditingAsyncTextArea() != null) {
                    getStartEditingAsyncTextArea().startEditingAsync();
                    Log.p("---->>> startEditingAsync() for TextArea named=" + getStartEditingAsyncTextArea().getName());
                } else if (getPinchInsertContainer() != null) {
                    getPinchInsertContainer().getTextArea().startEditingAsync();
                }
            }
//            if (getStartEditingAsyncTextArea() != null) {
//                getStartEditingAsyncTextArea().startEditingAsync();
//            }
        }
        if (Config.TEST) {
            Log.p("******* finished refreshAfterEdit for Screen: " + getTitle());
        }
        if (false) {
            if (true || !restoreKeepPos()) { //an explicitly set restorePos takes precedence over normal scroll
                if (previousValues != null) {// && previousValues.getScrollY() != null) {
                    previousValues.scrollToSavedYOnFirstShow(findScrollableContYChild());
                }
            }

            if (previousValues != null && previousValues.get(MySearchCommand.SEARCH_KEY) != null && getSearchCmd() != null) {
                getSearchCmd().actionPerformed(null); //re-activate Search, null=>reuse locally stored text
            }
        }

//        if (newContentContainer != null) {
//            ContainerScrollY scrollable = findScrollableContYChild(newContentContainer);
        ContainerScrollY scrollableContainer = findScrollableContYChild(newContentContainer != null ? newContentContainer : getContentPane());
        if (false && scrollableContainer != null) {
            scrollableContainer.setScrollVisible(true);
        }
        if (previousValues != null) {
            previousValues.scrollToSavedYOnFirstShow(scrollableContainer);
//            }
//            if (true && previousValues != null) {//            previousValues.setScrollComponent(findScrollableContYChild(contentContainer));
            previousValues.setListenToYScrollComponent(scrollableContainer);
        }
//        }
    }

//    abstract void refreshAfterEdit(KeepInSameScreenPosition keepPos);
//    {
////        revalidate();
//    }
    protected boolean isDragAndDropEnabled() {
        return false;
    }

    /**
     * if returning true a warning will be shown to the user that drag and drop
     * is not possible, otherwise nothing happens
     *
     * @return
     */
    protected boolean isShowDragAndDropWarning() {
        return false;
    }

//    public String getListAsCommaSeparatedString(Set<Category> setOrList) {
//    public static String getListAsCommaSeparatedString(List<Category> setOrList) {
    public static String getListAsCommaSeparatedString(List<ItemAndListCommonInterface> setOrList) {
        return getListAsCommaSeparatedString(setOrList, false);
    }

    public static String getListAsCommaSeparatedString(List<ItemAndListCommonInterface> setOrList, boolean showObjIds) {
        String str = "";
        String separator = "";
        if (setOrList != null) {
//            for (Category cat : setOrList) {
            for (ItemAndListCommonInterface itemCategoryOrList : setOrList) {
//                str = itemCategoryOrList.toString() + separator + str;
//                str = itemCategoryOrList.getText() + separator + str;
                str = str + separator + itemCategoryOrList.getText() + (showObjIds ? "/" + itemCategoryOrList.getObjectIdP() : "");
                separator = ", ";
            }
        }
        return str;
    }

    public static String getCategoriesAsCommaSeparatedString(List<Category> setOrList) {
        return getCategoriesAsCommaSeparatedString(setOrList, false);
    }

    public static String getCategoriesAsCommaSeparatedString(List<Category> setOrList, boolean showObjIds) {
        String str = "";
        String separator = "";
        if (setOrList != null) {
//            for (Category cat : setOrList) {
            for (Category itemCategoryOrList : setOrList) {
//                str = itemCategoryOrList.toString() + separator + str;
//                str = itemCategoryOrList.getText() + separator + str;
                str = str + separator + itemCategoryOrList.getText() + (showObjIds ? "/" + itemCategoryOrList.getObjectIdP() : "");
                separator = ", ";
            }
        }
        return str;
    }

    public static String getStringListAsCommaSeparatedString(List<String> setOrList) {
        String str = "";
        String separator = "";
        if (setOrList != null) {
            for (String s : setOrList) {
                str = str + separator + s;
                separator = ", ";
            }
        }
        return str;
    }

    public static String getListAsSeparatedString(List setOrList, GetStringFrom listName, String separator, int maxLength) {
        String str = "";
        String sep = "";
        if (setOrList != null) {
//            for (Object s : setOrList) {
            int size = maxLength > 0 ? (maxLength > setOrList.size() ? setOrList.size() : maxLength) : setOrList.size(); //limit maxLength if > than size()
            for (int i = 0; i < size; i++) {
                Object s = setOrList.get(i);
                str = str + sep + listName.get(s);
                sep = separator;
            }
        }
        return str;
    }

    public static String getListAsSeparatedString(List setOrList, GetStringFrom listName, String separator) {
        return getListAsSeparatedString(setOrList, listName, separator, -1);
    }

    public static String getListAsCommaSeparatedString(List setOrList, GetStringFrom listName) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        String str = "";
//        String separator = "";
//        if (setOrList != null) {
//            for (Object s : setOrList) {
//                str = str + separator + listName.get(s);
//                separator = ", ";
//            }
//        }
//        return str;
//</editor-fold>
        return getListAsSeparatedString(setOrList, listName, ", ");
    }
//    public static String getListAsCommaSeparatedString(List setOrList) {
//        return getListAsCommaSeparatedString(setOrList, (o)->o.toString());
//    }

    public static String getDefaultIfStrEmpty(String str, String defaultStr) {
        if (str == null || str.equals("")) {
            return defaultStr;
        } else {
            return str;
        }
    }

    public static String getEmptyStrIfStrEmpty(String str) {
        if (str == null || str.equals("")) {
            return "";
        } else {
            return str;
        }
    }

    public String makeUniqueIdForExpandedObjects(ItemAndListCommonInterface elt, String defaultString) {
//        String s = "";
        if (elt.getGuid() != null) {
            return elt.getGuid();
        } else if (elt.getObjectIdP() != null) {
            ASSERT.that(!elt.isNoSave());
            return elt.getObjectIdP();
        } else if (elt instanceof ItemList) {
            if (!((ItemList) elt).getSystemName().equals("")) {
                return ((ItemList) elt).getSystemName();
            } else if (elt instanceof CategoryList) {
                return ((ItemList) elt).getSystemName();
            }
        }
//            else if (elt.isNoSave())
        return defaultString;
    }

////<editor-fold defaultstate="collapsed" desc="comment">
//    abstract void deleteLocallyEditedValues();
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
//    static void showPreviousScreenOrDefaultXXX(MyForm previousForm, boolean callRefreshAfterEdit) {
////        if (previousForm != null) {
////            Form f = Display.getInstance().getCurrent();
////            if (f instanceof MyForm) {
////                ((MyForm) f).deleteLocallyEditedValues();
////            }
////            if (callRefreshAfterEdit) {
////                previousForm.refreshAfterEdit();
////            }
////
////            previousForm.showBack();
////        } else {
////            Form f = Display.getInstance().getCurrent();
////            ASSERT.that(false, "should not happen anymore, screen \"" + f != null ? f.getTitle() : "<null form>");
////            new ScreenMain().show();
////        }
////        if (previousForm.previousValues != null) {
////            previousForm.previousValues.deleteFile();
////        }
//        if (false) {
//            Form f = Display.getInstance().getCurrent();
//            if (f instanceof MyForm && ((MyForm) f).previousValues != null) { //if this (current) form has locally saved value, delete them before the previous form is shown
//                ((MyForm) f).previousValues.deleteFile();
//                ((MyForm) f).previousValues.clear(); //if still accessed
//            }
//        }
//        if (callRefreshAfterEdit) {
//            previousForm.refreshAfterEdit();
//        }
//        previousForm.showBack();
//    }
////</editor-fold>
    void showPreviousScreen(boolean callRefreshAfterEdit) {
        if (previousValues != null) { //if this (current) form has locally saved value, delete them before the previous form is shown
            previousValues.deleteFile();
//            previousValues.clear(); //if still accessed
        }
        if (Config.TEST) {
            ASSERT.that(parentForm != null, "In showPreviousScreenOrDefault() in form=" + getUniqueFormId() + ", previousForm==null!");
        }
        if (false && parentForm.getPinchInsertContainer() != null) { //now done in PinchInsertContainer.closePinchContainer
//            MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParent(getPinchInsertContainer());
            parentForm.getPinchInsertContainer().closePinchContainer(true);
        }
        if (callRefreshAfterEdit) {
            parentForm.refreshAfterEdit();

            //if saves are still pending, force a(nother) refresh once they are all done
//            if (!NetworkManager.getInstance().isQueueIdle()) {
//                DAO.getInstance().saveInBackground(() -> previousForm.refreshAfterEdit());
//            }
        }
        if (false) {
//            previousForm.showBack(!(this instanceof ScreenTimer6));  //prevent exiting from ScreenTimer6 to pop the last replayCommand (since ScreenTimer6 is never launched with a replayCommand)
        } else {
            onExit();
            parentForm.showBack();  //prevent exiting from ScreenTimer6 to pop the last replayCommand (since ScreenTimer6 is never launched with a replayCommand)
        }
    }

    /**
     * checks if any conditions to save the
     *
     * @return
     */
//    boolean checkIfSaveOnExit() {
//        return true;
//    }
//    void updateEditedValues() {
//        parseIdMap2.update();
//    }
//    void updateEditedValuesOnExitXXX() {
////        updateEditedValues();
//        parseIdMap2.update(true);
//        if (getUpdateActionOnDone() != null) {
//            getUpdateActionOnDone().run();
//
//        }
//    }
//    void saveOnExit() {
////        DAO.getInstance().saveInBackground((Item)this);
//        assert false;
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    void showPreviousScreenOrDefault(boolean callRefreshAfterEdit) {
//        showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit);
//    }
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
    interface ComponentListForSearch {

        Container get();
    }

//    protected ActionListener makeSearchFunctionUpperLowerStickyHeaders(ItemList itemListOrg, ComponentListForSearch getCompList) {
    protected ActionListener makeSearchFunctionUpperLowerStickyHeaders(ItemAndListCommonInterface itemListOrg, ComponentListForSearch getCompList) {
        return (e) -> { //NB. if e==null=> reuse previous locally stored search string
            String text;
            Component firstVisibleComp = null;
            MyForm myForm = (MyForm) getComponentForm();

            if (e != null) {
                text = (String) e.getSource();
            } else {
                //get TextField with search string
//                Component titleComp = getToolbar().getTitleComponent();
//                if (titleComp instanceof TextField) {
//                    text = ((TextField) titleComp).getText();
//                } else {
//                    text = ""; //if ever the implementation with TextField is changed, simply reset the search when coming back to a Form
//                }
                text = (String) myForm.previousValues.get(MySearchCommand.SEARCH_TEXT_KEY); //reuse 
            }
//            Container compList = null;
//            compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
            Container compList = getCompList.get();

            //UI: search will automatically remove an insert container (since it breaks the below algorithm)
            if (myForm != null && myForm.getPinchInsertContainer() != null) {
                Component topLevelComp = (Component) myForm.getPinchInsertContainer();
//                Component topLevelParent = topLevelComp.getParent();
                while (topLevelComp != null && topLevelComp.getParent() != compList) {
                    topLevelComp = topLevelComp.getParent();
                }
//                MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnCont(topLevelComp);
                if (true || topLevelComp != null) { //should never be null?!
                    topLevelComp.remove();
                }
                myForm.setPinchInsertContainer(null);
            }

            if (text != null && text.length() > 0) {
                myForm.previousValues.put(MySearchCommand.SEARCH_TEXT_KEY, text);
            } else {
                myForm.previousValues.remove(MySearchCommand.SEARCH_TEXT_KEY);
            }
            if (compList != null) {
                int labelCount = 0;
                int nonLabelCount = 0;
                boolean searchOnLowerCaseOnly;
                Component lastLabel = null;
                boolean hide;
                searchOnLowerCaseOnly = text.equals(text.toLowerCase()); //if search string is all lower case, then search on lower case only, otherwise search on 
                for (int i = 0, size = compList.getComponentCount(); i < size; i++) {
                    //https://www.codenameone.com/blog/toolbar-search-mode.html:

                    Component comp = compList.getComponentAt(i);
                    if (firstVisibleComp == null) {
                        firstVisibleComp = comp;
                    }
                    if (comp instanceof Label || comp instanceof StickyHeader) {
                        if (lastLabel != null) {
                            lastLabel.setHidden(nonLabelCount == 0); //hide previous label if nothing is shown after it
                            if (nonLabelCount != 0 && firstVisibleComp == null) {
                                firstVisibleComp = comp;
                            }
                        }
                        nonLabelCount = 0; //reset count on every Label
                        labelCount++; //hack: StickyHeaders are Labels, so count them and add to count
                        lastLabel = comp;
//                    } else if (comp instanceof InlineInsertNewContainer) {
//                        comp.remove();
                    } else {
                        if (searchOnLowerCaseOnly) {
                            hide = ((ItemAndListCommonInterface) itemListOrg.get(i - labelCount)).getText().toLowerCase().indexOf(text) < 0;
                        } else {
                            hide = ((ItemAndListCommonInterface) itemListOrg.get(i - labelCount)).getText().indexOf(text) < 0;
                        }
                        comp.setHidden(hide);
                        if (!hide) {
                            if (firstVisibleComp == null) {
                                firstVisibleComp = comp;
                            }
                            nonLabelCount++;
                        }
                    }
                }
                if (nonLabelCount == 0 && lastLabel != null) {
                    lastLabel.setHidden(true); //hide previous label if nothing is shown after it
                }
            } else {
                for (int i = 0, size = compList.getComponentCount(); i < size; i++) {
                    Component comp = compList.getComponentAt(i);
                    Object sourceObj = comp.getClientProperty(KEY_OBJECT);
//                    comp.setHidden(sourceObj != null && sourceObj instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) sourceObj).getText().toLowerCase().indexOf(text) < 0);
                    boolean hide = sourceObj != null && sourceObj instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) sourceObj).getText().toLowerCase().indexOf(text) < 0;
                    if (firstVisibleComp == null && !hide) {
                        firstVisibleComp = comp;
                    }
                    comp.setHidden(hide);
                }
            }
            if (compList != null) {
                if (firstVisibleComp != null) {
                    compList.scrollComponentToVisible(firstVisibleComp);
                }
                compList.animateLayout(ANIMATION_TIME_FAST);
            }
        };
    }

//    protected ActionListener makeSearchFunctionUpperLowerStickyHeaders(ItemList itemListOrg) {
    protected ActionListener makeSearchFunctionUpperLowerStickyHeaders(ItemAndListCommonInterface itemListOrg) {
        return makeSearchFunctionUpperLowerStickyHeaders(itemListOrg, () -> (Container) ((BorderLayout) getContentPane().getLayout()).getCenter());
    }

    protected ActionListener makeSearchFunctionSimple(ItemList itemListList, ComponentListForSearch getCompList) {
        return (e) -> {
            String text = (String) e.getSource();
//            Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
            Container compList = getCompList.get();
            MyForm myForm = (MyForm) getComponentForm();
            //UI: search will automatically remove an insert container (since it breaks the below algorithm)
            if (myForm != null && myForm.getPinchInsertContainer() != null) {
                Component topLevelComp = (Component) myForm.getPinchInsertContainer();
//                Component topLevelParent = topLevelComp.getParent();
                while (topLevelComp != null && topLevelComp.getParent() != compList) {
                    topLevelComp = topLevelComp.getParent();
                }
//                MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnCont(topLevelComp);
                if (true || topLevelComp != null) { //should never be null?!
                    topLevelComp.remove();
                }
                myForm.setPinchInsertContainer(null);
            }
            boolean showAll = text == null || text.length() == 0;
            for (int i = 0, size = itemListList.getSize(); i < size; i++) {
                //TODO!!! compare same case (upper/lower)
                //https://www.codenameone.com/blog/toolbar-search-mode.html:
//                compList.getComponentAt(i).setHidden(((ItemList) itemListList.get(i)).getText().toLowerCase().indexOf(text) < 0);
                compList.getComponentAt(i).setHidden(((ItemAndListCommonInterface) itemListList.get(i)).getText().toLowerCase().indexOf(text) < 0);
            }
            compList.animateLayout(ANIMATION_TIME_FAST);
        };
    }

    protected ActionListener makeSearchFunctionSimple(ItemList itemListList) {
        return makeSearchFunctionSimple(itemListList, () -> (Container) ((BorderLayout) getContentPane().getLayout()).getCenter());
    }

    /**
     * return true if pending savings prevent exiting the current form and
     * saving the edited element
     *
     * @return
     */
//    boolean areSavingsPendingXXX() {
//        assert false; //must be overridden
//        DAO.getInstance().isSavesPending();
//        return false; 
//    }
    /**
     * override in forms to do actual save of edited value
     */
    protected void updateOnExit() {
        parseIdMap2.update(true);
        if (getUpdateActionOnDone() != null) {
            getUpdateActionOnDone().run();
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * default timerCommand, only shows the timer symbol
     */
//    public Command makeTimerCommand(ItemList itemList, FilterSortDef filterSortDef) {
////        return makeTimerCommand(null, Icons.iconTimerSymbolToolbarStyle, itemList, filterSortDef);
//        return makeTimerCommand(null, Icons.iconTimerSymbolToolbarStyle, itemList);
//    }
    //    public Command makeDoneUpdateWithParseIdMapCommand(String title, Image icon) {
//        return makeDoneUpdateWithParseIdMapCommand(title, icon, true);
//    }
//    public Command makeDoneUpdateWithParseIdMapCommand(String title, Image icon, boolean callRefreshAfterEdit, CheckDataIsComplete getCheckOnExit) {
//    public Command makeDoneUpdateWithParseIdMapCommandOLD(String title, char icon, boolean callRefreshAfterEdit, CheckDataIsComplete getCheckOnExit) {
//        Command cmd = new Command(title, null) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                //use checkOnExit from parameters if defined, otherwise use the one set for the form if defined
//                if ((getCheckOnExit == null || getCheckOnExit.check()) && (getCheckIfSaveOnExit() == null || getCheckIfSaveOnExit().check())) {
////                    putEditedValues2(parseIdMap2);
//                    parseIdMap2.update();
//                    if (getUpdateActionOnDone() != null)
//                        getUpdateActionOnDone().run();
//                }
//                showPreviousScreen(callRefreshAfterEdit);
//            }
//        };
//        cmd.setMaterialIcon(icon);
//        cmd.putClientProperty("android:showAsAction", "withText");
//        return cmd;
//    }
//</editor-fold>
//    public Command makeDoneUpdateWithParseIdMapCommand(String title, char icon, boolean callRefreshAfterEdit, CheckIfDataIsComplete getCheckIfDataIsCompleteOnExit) {
//        return makeDoneUpdateWithParseIdMapCommand(title, callRefreshAfterEdit, getCheckIfDataIsCompleteOnExit, true);
//    }
//    public Command makeDoneUpdateWithParseIdMapCommand(String title, char icon, boolean callRefreshAfterEdit, CheckIfDataIsComplete getCheckOnExit, boolean allowExitOnIncompleteData) {
    public Command makeDoneUpdateWithParseIdMapCommand(String title, boolean callRefreshAfterEdit, // CheckIfDataIsComplete getCheckIfSaveOnExit, 
            boolean allowExitOnIncompleteData) {
        Command cmd = new Command(title, null) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                boolean savesArePending = areSavingsPending();
//                if (savesArePending) {
//                    showDialogCannotSaveNow();
//                } else //use checkOnExit from parameters if defined, otherwise use the one set for the form if defined
                if ((getCheckIfSaveOnExit() == null || getCheckIfSaveOnExit().check())// || allowExitOnIncompleteData))
                        //                        || (getCheckIfSaveOnExit() != null && (getCheckIfSaveOnExit().check()))// || allowExitOnIncompleteData))
                        || allowExitOnIncompleteData) {
//                    if (false&&getPinchInsertContainer() != null) {//false since too smart! //if an insertContainer is left when pushing Back, then save the new element if some text was entered
//                        getPinchInsertContainer().done();
//                    }
//                    updateEditedValuesOnExit();
//                    if (triggerSaveOnExit) {
//                        DAO.getInstance().saveNewTriggerUpdate3();
                    updateOnExit();
//                    }
                    showPreviousScreen(callRefreshAfterEdit);
                }
            }
        };
        cmd.setMaterialIcon(Icons.iconBackToPreviousScreen);
        cmd.setMaterialIconSize(MyPrefs.defaultIconSizeInMM.getFloat());
        cmd.setIconGapMM(MyPrefs.defaultIconGapInMM.getFloat());
        cmd.putClientProperty("android:showAsAction", "withText");
        return cmd;
    }

//    public Command makeDoneUpdateWithParseIdMapCommand(String title, boolean callRefreshAfterEdit, boolean allowExitOnIncompleteData) {
//        return makeDoneUpdateWithParseIdMapCommand(title, callRefreshAfterEdit, getCheckIfSaveOnExit(), allowExitOnIncompleteData);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    //makeDoneUpdateWithParseIdMapCommand above is better: each check will show one or more dialogs etc and can fix things before returning true or false
//    public Command makeDoneUpdateWithParseIdMapCommandXXX(boolean callRefreshAfterEdit, GetString canGoBack) {
////        Command cmd = new Command(title, icon) {
//        Command cmd = new Command("", Icons.iconBackToPrevFormToolbarStyle()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                String errorMsg;
//                if ((errorMsg = canGoBack.get()) == null || Dialog.show("INFO", errorMsg, "Yes", "No")) {
////                    if (checkDataIsCompleteBeforeExit == null || (errorMsg = checkDataIsCompleteBeforeExit.check()) == null) {
////                    if (getCheckOnExit() != null || (errorMsg = getCheckOnExit().check()) != null) {
//                    putEditedValues2(parseIdMap2);
//                    if (getUpdateActionOnDone() != null)
//                        getUpdateActionOnDone().update();
//                    showPreviousScreenOrDefault(callRefreshAfterEdit);
//                } else {
//                    //nothing, stay in screen
////                    Dialog.show("Error", errorMsg, "OK", null);
////                    Dialog.show("INFO", "No key data in this task, save anyway?", "Save", "Don't save");
//                }
//            }
//        };
//        cmd.putClientProperty("android:showAsAction", "withText");
//        return cmd;
//    }
//</editor-fold>
//    public Command makeDoneUpdateWithParseIdMapCommand(boolean callRefreshAfterEdit) {
////        return makeDoneUpdateWithParseIdMapCommand("", Icons.iconBackToPreviousScreen, callRefreshAfterEdit, getCheckIfSaveOnExit());
//        return makeDoneUpdateWithParseIdMapCommand("", callRefreshAfterEdit, null, true);
//    }
    public Command makeDoneUpdateWithParseIdMapCommand() {
//        return makeDoneUpdateWithParseIdMapCommand("", true, getCheckIfSaveOnExit(), false); //false); //default false since otherwise edited values will be lost
        return makeDoneUpdateWithParseIdMapCommand("", true, false); //false); //default false since otherwise edited values will be lost
    }

    public Command addStandardBackCommand() {
        Command backCmd = makeDoneUpdateWithParseIdMapCommand();
        backCmd.putClientProperty("android:showAsAction", "withText");
//            getToolbar().setBackCommand(makeDoneUpdateWithParseIdMapCommand(),Toolbar.BackCommandPolicy.AS_ARROW,MyPrefs.defaultIconSizeInMM.getFloat());
        getToolbar().setBackCommand(backCmd, Toolbar.BackCommandPolicy.AS_ARROW, MyPrefs.defaultIconSizeInMM.getFloat());
        return backCmd;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public Command makeDoneUpdateWithParseIdMapCommand(CheckIfDataIsComplete getCheckOnExit) {
//        return makeDoneUpdateWithParseIdMapCommand("", Icons.iconBackToPreviousScreen, true, getCheckOnExit);
//    }
//    public Command makeDoneUpdateWithParseIdMapCommand(String title, char icon, boolean callRefreshAfterEdit) {
//        return makeDoneUpdateWithParseIdMapCommand(title, icon, callRefreshAfterEdit, getCheckIfSaveOnExit());
//    }
//    public Command makeDoneUpdateWithParseIdMapCommand(boolean callRefreshAfterEdit, GetString canGoBack) {
//        return makeDoneUpdateWithParseIdMapCommand("", Icons.iconBackToPrevFormToolbarStyle(), callRefreshAfterEdit, canGoBack);
//    }
//    public Command makeDoneCommandWithNoUpdateXXX() {
//        Command cmd = new Command("", Icons.iconBackToPrevFormToolbarStyle()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                previousForm.refreshAfterEdit();
////                previousForm.showBack();
////                showPreviousScreenOrDefault(previousForm, false);
//                showPreviousScreen(false);
//            }
//        };
//        cmd.putClientProperty("android:showAsAction", "withText");
//        return cmd;
//    }
//</editor-fold>
    public Command makeCancelCommand() {
        return makeCancelCommand("Cancel", null);
    }

    private Command makeCancelCommand(String title, Image icon) {
//        Command cmd = new CommandTracked(title, icon) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////<editor-fold defaultstate="collapsed" desc="comment">
////            Log.p("Clicked");
////            item.refreshTimersFromParseServer(); or item.clear()?? //see here: https://www.parse.com/questions/make-a-copy-of-a-pfobject, revert(); //forgetChanges***/refresh, notably categories
////            previousForm.showBack(); //drop any changes
////                previousForm.refreshAfterEdit();
//////            previousForm.revalidate();
////                previousForm.showBack(); //drop any changes
////                showPreviousScreenOrDefault(previousForm, false);
////</editor-fold>
//                showPreviousScreenOrDefault(false);
//super.actionPerformed(evt);
//            }
//        };
//        Command cmd = CommandTracked.create(title, icon, (e) -> showPreviousScreenOrDefault(false), "Cancel");
//        Command cmd = MyReplayCommand.createKeep("Cancel", title, Icons.iconCancel, (e) -> showPreviousScreenOrDefault(false));
        Command cmd = CommandTracked.create(title, Icons.iconCancel, (e) -> {
            if (getUpdateActionOnCancel() != null) {
                getUpdateActionOnCancel().run();
            }
            showPreviousScreen(true); //could probably be false, but just in case always refresh!
        }, "Cancel");
        cmd.putClientProperty("android:showAsAction", "withText");
        return cmd;
    }

    public Command makeInterruptCommand(boolean includeText) {
//        return makeInterruptCommand("", Icons.iconInterruptToolbarStyle()); //"Interrupt", "New Interrupt"
//    }
//
//    private Command makeInterruptCommand(String title, Image icon) {
//        String title = "";
//        Image icon = Icons.iconInterruptToolbarStyle();
        //TODO only make interrupt task creation available in Timer (where it really interrupts something)?? There is [+] for 'normal' task creation elsewhere... Actually, 'Interrupt' should be sth like 'InstantTimedTask'
        //TODO implement longPress to start Interrupt *without* starting the timer (does it make sense? isn't it the same as [+] to add new task?)
//        return MyReplayCommand.create(TimerStack.TIMER_REPLAY, title, icon, (e) -> {
//        return CommandTracked.create("", icon, (e) -> {
        return CommandTracked.create(includeText ? "Time interrupt" : "", Icons.iconInterrupt, (e) -> {
            Item newInterruptItem = new Item(false);
//            newInterruptItem.setRemaining(0);//remove default estimate for interrupt tasks
            newInterruptItem.setInteruptOrInstantTask(true);
//            DAO.getInstance().saveNew(newInterruptItem, true);
//            DAO.getInstance().saveNew(newInterruptItem); //TODO!!!!: don't save until exeting the Timer to allow for Cancel in Timer?!!
//            DAO.getInstance().saveNewTriggerUpdate();
            DAO.getInstance().saveToParseNow(newInterruptItem); //TODO!!!!: don't save until exeting the Timer to allow for Cancel in Timer?!!
//                if (ScreenTimerNew.getInstance().isTimerRunning()) {
//                    item.setInteruptTask(true); //UI: automatically mark as Interrupt task if timer is already running. TODO is this right behavior?? Should all Interrupt tasks be marked as such or only when using timer?? Only when using Timer, otherwise just an 'instant task'
//                    item.setTaskInterrupted(ScreenTimer.getInstance().getTimedItemN());
//                }
//                ScreenTimer.getInstance().startTimer(item, MyForm.this);
//            ScreenTimer2.getInstance().startInterrupt(interruptItem, MyForm.this); //TODO!!! verify that item is always saved (within Timer, upon Done/Exit/ExitApp
//            TimerStack.getInstance().startInterruptOrInstantTask(interruptItem, MyForm.this); //TODO!!! verify that item is always saved (within Timer, upon Done/Exit/ExitApp
            TimerStack.getInstance().startInterruptOrInstantTask(newInterruptItem, MyForm.this); //TODO!!! verify that item is always saved (within Timer, upon Done/Exit/ExitApp
            //TODO Allow to pick a common (predefined/template) interrupt task (long-press??)
            //Open it up in editing mode with timer running
//            setupTimerForItem(item, 0);
            //Upon Done/Stop, save and return to previous task
//        }, () -> !MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()); //only push this command if we start with BigTimer (do NOT always start with smallTimer)
        }, "InterruptInScreen" + getUniqueFormId()); //only push this command if we start with BigTimer (do NOT always start with smallTimer)
    }

    public Command makeStartTimerCommand(boolean includeText, ItemAndListCommonInterface itemListOrg) {
//        return CommandTracked.create(TimerStack.getInstance().isTimerActive() ? "Open Timer" : "Start Timer on list",
        return CommandTracked.create("Start Timer",
                //                    TimerStack.getInstance().isTimerActive() ? Icons.iconLaunchTimerAlreadyRunning : Icons.iconLaunchTimer,
                Icons.iconLaunchTimer,
                (e) -> {
                    if (itemListOrg instanceof ItemList) {
                        TimerStack.getInstance().startTimerOnItemList((ItemList) itemListOrg, MyForm.this); //itemListOrg because Timer stores the original Parse objects and does its own filter/sort
                    } else if (itemListOrg instanceof Item) {
                        TimerStack.getInstance().startTimerOnItem((Item) itemListOrg, MyForm.this); //itemListOrg because Timer stores the original Parse objects and does its own filter/sort
                    }
                }, "InterruptInScreen" + getUniqueFormId() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
        );
    }

    /**
     * neither swipCont must NOT be null and must be added to a form
     *
     * @param swipCont
     * @param itemOrItemList
     * @param commandTrackId
     * @return
     */
    public static Button makeTimerSwipeButton(SwipeableContainer swipCont, ItemAndListCommonInterface itemOrItemList, String commandTrackId) {
        Button startTimer = new Button(CommandTracked.create(null, Icons.iconLaunchTimer, (ev) -> {
//            if (swipCont != null) {
            MyForm myForm = (MyForm) swipCont.getComponentForm();
            if (myForm != null) {
                myForm.setKeepPos(new KeepInSameScreenPosition(itemOrItemList, swipCont));
            }
            TimerStack.getInstance().startTimerOnItemOrItemList(itemOrItemList, myForm);//true == start timer even on invalid timer items, forceTimerStartOnLeafTasksWithAnyStatus
            swipCont.close(); //close before save 
        }, commandTrackId
        ));
//            startTimer.setMaterialIcon(Icons.iconLaunchTimer);
        startTimer.setUIID("SwipeButtonTimer");
        return startTimer;
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * adds new item to itemListOrg at the given position and saves both list
     * and item. Nothing is done if itemListOrg is null or not already saved
     * (temporary list) or not a Category.
     *
     * @param item
     * @param pos
     * @param itemListOrg
     */
    //    private static void addNewTaskSetTemplateAddToListAndSave(Item item, int pos, ItemList itemListOrg) {
    //    static void addNewTaskToListAndSave(Item item, Item refItem, ItemAndListCommonInterface itemListOrg, boolean insertAfterRefItemOrEndOfList) {
    ////        item.setTemplate(itemListOrg.isTemplate()); //template or not
    //        boolean addToList = (itemListOrg != null && ((ParseObject) itemListOrg).getObjectIdP() != null && !(itemListOrg instanceof Category)); //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
    //        if (addToList) { //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
    //            itemListOrg.addToList(pos, item); //UI: add to top of list
    //        }
    //        DAO.getInstance().saveInBackground(item); //must save item since adding it to itemListOrg changes its owner
    //        if (addToList) {
    //            DAO.getInstance().saveInBackground((ParseObject) itemListOrg); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
    //        }
    //    }
    //    
    //    static void addNewTaskToListAndSaveOLD(Item item, int pos, ItemAndListCommonInterface itemListOrg) {
    ////        item.setTemplate(itemListOrg.isTemplate()); //template or not
    //        boolean addToList = (itemListOrg != null && ((ParseObject) itemListOrg).getObjectIdP() != null && !(itemListOrg instanceof Category)); //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
    //        if (addToList) { //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
    //            itemListOrg.addToList(pos, item); //UI: add to top of list
    //        }
    //        DAO.getInstance().saveInBackground(item); //must save item since adding it to itemListOrg changes its owner
    //        if (addToList) {
    //            DAO.getInstance().saveInBackground((ParseObject) itemListOrg); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
    //        }
    //    }
    //    static void addNewTaskSetTemplateAddToListAndSave(Item item, ItemList itemListOrg) {
    /**
     * adds new item to itemListOrg at the default position (as given by the
     * settings) and saves both list and item
     *
     * @param item
     * @param pos
     */
    //    private static void addNewTaskToListAndSaveXXX(Item item, ItemAndListCommonInterface itemListOrg, boolean insertInStartOfLists) {
    //        addNewTaskToListAndSave(item, insertInStartOfLists ? 0 : itemListOrg.getSize(), itemListOrg);
    //    }
    //    static void addNewTaskToListAndSaveXXX(Item item, ItemAndListCommonInterface itemListOrg) {
    //        addNewTaskToListAndSaveXXX(item, itemListOrg, MyPrefs.insertNewItemsInStartOfLists.getBoolean());
    //    }
    //    private void addNewTaskToListAndSave(Item item, int pos, ItemAndListCommonInterface itemListOrg) {
    ////        item.setTemplate(optionTemplateEditMode); //template or not
    //        boolean addToList = (itemListOrg != null && itemListOrg.getObjectIdP() != null
    //                && !(itemListOrg instanceof Category)); //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
    //        if (addToList) { //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
    //            itemListOrg.addToList(pos, item); //UI: add to top of list
    //        }
    //        DAO.getInstance().save(item); //must save item since adding it to itemListOrg changes its owner
    //        if (addToList) {
    //            DAO.getInstance().save((ParseObject)itemListOrg); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
    //        }
    //    }
    //</editor-fold>
    public Command makeCommandNewItemSaveToItemList(ItemList itemListOrg, String cmdText) {
//        return makeCommandNewItemSaveToItemList(itemListOrg, cmdText, Icons.iconNewTaskToolbarStyle());
//        return makeCommandNewItemSaveToItemList(itemListOrg, cmdText, Icons.iconNewTaskToolbarStyle());
        return makeCommandNewItemSaveToItemList(itemListOrg, cmdText, Icons.iconNewTaskToInbox);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Command makeCommandNewItemSaveToItemListXXX(ItemList itemListOrg, String cmdText, Image icon) {
//
//        Command cmd = MyReplayCommand.createKeep("CreateNewItem", cmdText, icon, (e) -> {
//            Item item = new Item(true);
//            item.setOwner(itemListOrg); //need to set owner here (even if cancelled) for repeatRule
//            setKeepPos(new KeepInSameScreenPosition());
//            new ScreenItem2(item, (MyForm) getComponentForm(), () -> {
//                if (true || item.hasSaveableData() || Dialog.show("INFO", "No key data in this task, save anyway?", "Save", "Don't save")) {
//                    //TODO!!!! this test is not in the right place - it should be tested inside ScreenItem before exiting
//                    //only save if data (don't save if no relevant data)
//                    if (true) {
//                        //TODO!!! save directly to Inbox
////                            addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg);
//                    }
////                    addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg);
//                    itemListOrg.addToList(item, null, MyPrefs.insertNewItemsInStartOfLists.getBoolean()); //UI: add to top of list
////                    DAO.getInstance().saveNew(true, (ParseObject) item, (ParseObject) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                    DAO.getInstance().saveNew((ParseObject) item, (ParseObject) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                    DAO.getInstance().saveNewExecuteUpdate();
//
////                    DAO.getInstance().saveInBackground(item, itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                    if (false) {
//                        refreshAfterEdit(); //TODO!!! scroll to where the new item was added (either beginning or end of list)
//                    }//                    }
//                } else {
//                    //if no saveable data, do nothing
////                        itemListOrg.removeFromList(item); //if no saveable data, undo the
//                    //TODO!!!! how to remove from eg Categories if finally the task is not saved??
//                }
//            }, false, null).show(); //false=optionTemplateEditMode
//        });
//        return cmd;
//    }
//</editor-fold>
//    public Command makeCommandNewItemSaveToItemList(ItemList itemListOrg, String cmdText, char icon) {
    public Command makeCommandNewItemSaveToItemList(ItemAndListCommonInterface itemListOrg, String cmdUniqueID, String cmdText, char icon) {

        Command cmd = MyReplayCommand.createKeep(cmdUniqueID, cmdText, icon, (e) -> {
            Item newItem = new Item(true);
            //first insert owner / category into Item before editing
            if (itemListOrg instanceof Category) {
                newItem.setOwner(Inbox.getInstance()); //only set owner here
                newItem.addCategoryToItem((Category) itemListOrg, false); //don't add to category until saving, MyPrefs.insertNewCategoriesForItemsInStartOfIList.getBoolean());
            } else {
                newItem.setOwner(itemListOrg); //also updates inherited values!! (but ow are they set when owner is a new project not yet saved?!
            }
            setKeepPos(new KeepInSameScreenPosition());
            new ScreenItem2(newItem, (MyForm) getComponentForm(), () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (true || item.hasSaveableData() || Dialog.show("INFO", "No key data in this task, save anyway?", "Save", "Don't save")) {
//TODO!!!! this test is not in the right place - it should be tested inside ScreenItem before exiting
//only save if data (don't save if no relevant data)
//                if (true) {
//                    //TODO!!! save directly to Inbox
////                            addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg);
//                }
//                    addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg);
//</editor-fold>
                if (newItem.hasSaveableData()) {
                    if (itemListOrg instanceof Category) {
                        ((Category) itemListOrg).addItemToCategory(newItem, null, false, MyPrefs.insertNewItemsInStartOfLists.getBoolean()); //false because added above (before editing)
                        Inbox.getInstance().addToList(newItem, !MyPrefs.insertNewItemsInStartOfLists.getBoolean());
//                    DAO.getInstance().saveNew(true, newItem, Inbox.getInstance(), (Category) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                        DAO.getInstance().saveNew(newItem, Inbox.getInstance(), (Category) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                        DAO.getInstance().saveNewTriggerUpdate();
//                        DAO.getInstance().saveToParseNow(newItem, Inbox.getInstance(), (Category) itemListOrg); //must save item since adding it to itemListOrg changes its owner
                        DAO.getInstance().saveToParseNow(newItem); //must save item since adding it to itemListOrg changes its owner
                    } else if (itemListOrg instanceof ItemList) {
                        itemListOrg.addToList(newItem, null, MyPrefs.insertNewItemsInStartOfLists.getBoolean()); //UI: add to top of list
//                    DAO.getInstance().saveNew(true, newItem, (ItemList) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                        DAO.getInstance().saveNew(newItem, (ItemList) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                        DAO.getInstance().saveNewTriggerUpdate();
//                        DAO.getInstance().saveToParseNow(newItem, (ItemList) itemListOrg); //must save item since adding it to itemListOrg changes its owner
                        DAO.getInstance().saveToParseNow(newItem); //must save item since adding it to itemListOrg changes its owner
                    } else if (itemListOrg instanceof Item) {
                        itemListOrg.addToList(newItem, null, MyPrefs.insertNewItemsInStartOfLists.getBoolean()); //UI: add to top of list
//                    DAO.getInstance().saveNew(true, newItem, (Item) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                        DAO.getInstance().saveNew(newItem, (Item) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                        DAO.getInstance().saveNewTriggerUpdate();
//                        DAO.getInstance().saveToParseNow(newItem, (Item) itemListOrg); //must save item since adding it to itemListOrg changes its owner
                        DAO.getInstance().saveToParseNow(newItem); //must save item since adding it to itemListOrg changes its owner
                    } else {
                        ASSERT.that(false);
                    }
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                    DAO.getInstance().saveInBackground(item, itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                if (false) {
//                    refreshAfterEdit(); //TODO!!! scroll to where the new item was added (either beginning or end of list)
//                }
//                    }
//                } else {
//                    //if no saveable data, do nothing
////                        itemListOrg.removeFromList(item); //if no saveable data, undo the
//                    //TODO!!!! how to remove from eg Categories if finally the task is not saved??
//                }
//</editor-fold>
            }, false, null).show(); //false=optionTemplateEditMode
        });
        return cmd;
    }

    public Command makeCommandNewItemSaveToItemList(ItemAndListCommonInterface itemListOrg, String cmdText, char icon) {
        return makeCommandNewItemSaveToItemList(itemListOrg, "CreateNewItem", cmdText, icon);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Command makeCommandNewItemSaveToItemListORG(ItemList itemListOrg, String cmdText, char icon) {
//
//        Command cmd = MyReplayCommand.createKeep("CreateNewItem", cmdText, icon, (e) -> {
//            Item item = new Item(true);
//            item.setOwner(itemListOrg);
//            setKeepPos(new KeepInSameScreenPosition());
//            new ScreenItem2(item, (MyForm) getComponentForm(), () -> {
//                if (true || item.hasSaveableData() || Dialog.show("INFO", "No key data in this task, save anyway?", "Save", "Don't save")) {
//                    //TODO!!!! this test is not in the right place - it should be tested inside ScreenItem before exiting
//                    //only save if data (don't save if no relevant data)
//                    if (true) {
//                        //TODO!!! save directly to Inbox
////                            addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg);
//                    }
////                    addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg);
//                    itemListOrg.addToList(item, null, MyPrefs.insertNewItemsInStartOfLists.getBoolean()); //UI: add to top of list
////                    DAO.getInstance().saveNew(true, (ParseObject) item, (ParseObject) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                    DAO.getInstance().saveNew((ParseObject) item, (ParseObject) itemListOrg); //must save item since adding it to itemListOrg changes its owner
//                    DAO.getInstance().saveNewExecuteUpdate();
//
////                    DAO.getInstance().saveInBackground(item, itemListOrg); //must save item since adding it to itemListOrg changes its owner
////                    if (false) {
////                        refreshAfterEdit(); //TODO!!! scroll to where the new item was added (either beginning or end of list)
////                    }
////                    }
//                } else {
//                    //if no saveable data, do nothing
////                        itemListOrg.removeFromList(item); //if no saveable data, undo the
//                    //TODO!!!! how to remove from eg Categories if finally the task is not saved??
//                }
//            }, false, null).show(); //false=optionTemplateEditMode
//        });
//        return cmd;
//    }
//    public Command makeCommandNewItemSaveToInboxXXX(String cmdText) {
//        return makeCommandNewItemSaveToItemList(Inbox.getInstance(), cmdText);
//    }
//</editor-fold>
    public Command makeCommandNewItemSaveToInboxXXX(boolean useStdCmdText) {
        return makeCommandNewItemSaveToItemList(Inbox.getInstance(), useStdCmdText ? "Add task to Inbox" : "");
    }

    public Command makeCommandNewItemSaveToInbox() {
//        return makeCommandNewItemSaveToInbox(true);
        return makeCommandNewItemSaveToItemList(Inbox.getInstance(), "CreateNewItemInInbox", "Add task to Inbox", Icons.iconNewTaskToInbox);
    }

    public MyReplayCommand makeEditFilterSortCommand(ItemAndListCommonInterface filterOwnerItemListOrItem) {
        return MyReplayCommand.createKeep("FilterSortSettings", "Edit filter/sort", Icons.iconEditFilterSort, (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//            FilterSortDef filterSortDef = itemListOrItem.getFilterSortDefN() == null
//                    ? new FilterSortDef()
//                    : (itemListOrItem.getFilterSortDefN().equals(FilterSortDef.getDefaultFilter())
//                    ? new FilterSortDef(itemListOrItem.getFilterSortDefN()) : itemListOrItem.getFilterSortDefN()); //need this construct due to use in lambda below
//            FilterSortDef filterSortDef = itemListOrItem.getFilterSortDefN() == null
//                    ? new FilterSortDef() //if no filter defined, create a new one to edit
//                    : new FilterSortDef(itemListOrItem.getFilterSortDefN()); //always edit a *copy* of the filter
//            FilterSortDef filterSortDef = itemListOrItem.getFilterSortDefN() ;
//            boolean newFilterEdited=false;
//            if (filterSortDef==null){
//                filterSortDef=itemListOrItem.getDefaultFilterSortDef();
//                newFilterEdited=true;
//            }
//</editor-fold>
            //need this construct due to use in lambda below
//            FilterSortDef filterSortDef = itemListOrItem.getFilterSortDefN() != null
//                    ? itemListOrItem.getFilterSortDefN() : itemListOrItem.getFilterSortDef(true);
            FilterSortDef filterSortDef = filterOwnerItemListOrItem.getFilterSortDef(true);
            setKeepPos(new KeepInSameScreenPosition());
            new ScreenFilter(filterSortDef, MyForm.this, filterOwnerItemListOrItem, () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (itemListOrItem.getFilterSortDefN() == null) {
//                    if (!filterSortDef.equals(itemListOrItem.getDefaultFilterSortDef())) { //if filter edited
//                        itemListOrItem.setFilterSortDef(filterSortDef); //save a copy of the new edited filter
//                    }
//                } else if (filterSortDef.isDirty()) { //if filter edited
//                    itemListOrItem.setFilterSortDef(filterSortDef); //save a copy of the new edited filter
//                }
//</editor-fold>
//                if ((itemListOrItem.getFilterSortDefN() == null && !filterSortDef.equals(itemListOrItem.getFilterSortDef(true))) //if new default filter was edited
//                        || (itemListOrItem.getFilterSortDefN() != null && filterSortDef.isDirty())) { //or if existing filter was edited
                if (true || !filterSortDef.equals(filterOwnerItemListOrItem.getFilterSortDef(true))) {//if original or default filter was edited
//                        ) { //or if existing filter was edited
                    filterOwnerItemListOrItem.setFilterSortDef(filterSortDef); //update with edited filter
                }
//                DAO.getInstance().saveNew(filterSortDef); //save updates
//                DAO.getInstance().saveNew((ParseObject) filterOwnerItemListOrItem);
//                DAO.getInstance().saveNewTriggerUpdate();
                if (false) {
                    DAO.getInstance().saveToParseNow((ParseObject) filterOwnerItemListOrItem); //saving owner of filter will also save the new filter
                } else {
                    DAO.getInstance().saveToParseNow(filterSortDef); //save possibly edited filter explicitly
                }                //TODO any way to scroll to a meaningful place after applying a filter/sort? Probably not! Or: scroll to any previously visible task? Or scroll down 'as much' as before (if possible)?
            }).show();
        }
        );
    }

    public static Button makeAddTimeStampToCommentAndStartEditing(TextArea comment) {
        //TODO only make interrupt task creation available in Timer (where it really interrupts something)?? There is [+] for 'normal' task creation elsewhere... Actually, 'Interrupt' should be sth like 'InstantTimedTask'
        //TODO implement longPress to start Interrupt *without* starting the timer (does it make sense? isn't it the same as [+] to add new task?)
//        Button button = new Button(CommandTracked.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {
        Button button = new Button(CommandTracked.create(null, Icons.iconAddTimeStampToComment, (e) -> {
            comment.setText(Item.addTimeToComment(comment.getText()));
//                    comment.setstartEditing(); //TODO how to position cursor at end of text (if not done automatically)?
//comment.setCursor //only on TextField, not TextArea
//            comment.startEditing(); //TODO in CN bug db #1827: start using startEditAsync() is a better approach
            comment.startEditingAsync();//TODO in CN bug db #1827: start using startEditAsync() is a better approach
        }, "AddTimeStampToComment"));
        button.setUIID("ScreenItemCommentDateStamp");
//         button..
//        button.setIcon(FontImage.createMaterial(ItemStatus.iconCheckboxCreatedChar, UIManager.getInstance().getComponentStyle("ItemCommentIcon")));
        button.setMaterialIcon(Icons.iconCommentTimeStamp);
        return button;
    }

    /**
     * replace the button created in the toolbar for cmd by the newCmdButton
     * (which can for example have a longpress command)
     *
     * @param cmd
     * @param longPressCmd
     * @return true if a button was found for
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    boolean addLongPressCmdToToolbarCmdButton(Command cmd, Command longPressCmd) {
//        Button oldCmdButton = getToolbar().findCommandComponent(cmd);
//        if (oldCmdButton != null) {
//
////            MyButtonLongPressXXX newLongPressButton = new MyButtonLongPressXXX(cmd, longPressCmd);
//
//            newLongPressButton.setUIID(oldCmdButton.getUIID()); //keep the same UIID
//            newLongPressButton.putClientProperty("TitleCommand", oldCmdButton.getClientProperty("TitleCommand")); //keep the same values as set in addCommandToLeft/Right/Bar
//            newLongPressButton.putClientProperty("Left", oldCmdButton.getClientProperty("Left"));
//
//            oldCmdButton.getParent().replace(oldCmdButton, newLongPressButton, null);
//            return true;
//        }
//        return false;
//    }
//</editor-fold>
    boolean replaceCommandButton(Command cmd, Button newCmdButton) {
        ASSERT.that(newCmdButton.getCommand() == cmd || newCmdButton.getCommand().equals(cmd));

        Button oldCmdButton = getToolbar().findCommandComponent(cmd);
        if (oldCmdButton != null) {

            newCmdButton.setUIID(oldCmdButton.getUIID()); //keep the same UIID
            newCmdButton.putClientProperty("TitleCommand", oldCmdButton.getClientProperty("TitleCommand")); //keep the same values as set in addCommandToLeft/Right/Bar
            newCmdButton.putClientProperty("Left", oldCmdButton.getClientProperty("Left"));

            MenuBar sideMenu = getToolbar().getMenuBar();
//        sideMenu.replace(oldCmdButton, newCmdButton, null);
            oldCmdButton.getParent().replace(oldCmdButton, newCmdButton, null);
            return true;
        }
        return false;
    }

    public Component makeSpacer() {
        Label l = new Label("", "Spacer");
        l.setShowEvenIfBlank(true);
        return l;
    }

    public Component makeSpacerThin() {
        Label l = new Label("", "SpacerThin");
        l.setShowEvenIfBlank(true);
        return l;
    }

    public Component makeSpacerThick() {
        Label l = new Label("", "SpacerThick");
        l.setShowEvenIfBlank(true);
        return l;
    }

////<editor-fold defaultstate="collapsed" desc="comment">
////    void restartScreenXXX() {
////        //TODO
////        //TODO store info on current screen when calling stop()??!!
////        //TODO restart screen with same lists expanded
////        //TODO restart a screen with the same placement in the list
////        Item item = null;
////        ItemList itemList = null;
////        FilterSortDef filter = null;
////        String screenName = null; //read from local storage
////        switch (screenName) {
////            case ScreenTimer.SCREEN_TITLE:
////                break;
////            case ScreenListOfItems.SCREEN_ID:
////                new ScreenListOfItems(itemList, null, null).show();
////                break;
////            default:
////                new ScreenMain().show();
////                return;
////        }
////    }
////</editor-fold>
    final static int TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS = 80; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s
    final static int ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR = 500; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s

    /**
     * show toastbar for the time necessary to read the text
     *
     * @param message
     */
    static void showToastBar(String message) {
        showToastBar(message, 0);
    }

    static void showToastBar(String message, int timeMillis) {
        ToastBar.Status status = ToastBar.getInstance().createStatus();
        status.setMessage(message);
        int timeOut = timeMillis != 0 ? timeMillis : message.length() * TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS + ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR;
        status.setExpires(timeOut);
        status.show();
    }

//    protected static SpanButton addHelp(SpanButton comp, String helpText) {
    protected static Component addHelp(Component comp, String helpText) {
        if (helpText == null || helpText.length() == 0) {
            return comp;
        }
        ActionListener al = (e) -> {
////            ToastBar.Status status = ToastBar.getInstance().createStatus().setMessage(text);
//            ToastBar.Status status = ToastBar.getInstance().createStatus();
//            status.setMessage(helpText);
//            //status.setExpires(3000);
////                final int TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS = 65; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s
////            status.setExpires(status.getMessage().length() * TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS + ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR);
//            status.setExpires(helpText.length() * TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS + ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR);
//            status.show();
            showToastBar(helpText, 0);
        };
//        comp.setUIID("Label"); //CN1 Support: this not working for SpanButton??
        if (comp instanceof SpanButton) {
            ((SpanButton) comp).addActionListener(al);
        } else if (comp instanceof Button) {
            ((Button) comp).addActionListener(al);
//        } else if (comp instanceof MySpanButton) {
//            ((MySpanButton) comp).addActionListener(al);
        } else {
            assert false : "Unknown type of help button, comp=" + comp;
        }
        return comp;
    }

    protected static Component makeHelpButton(String label, String helpText) {
        return makeHelpButton(label, helpText, true);
    }

    protected static Component makeHelpButton(String label, String helpText, boolean makeSpanButton) {
        return makeHelpButton(label, helpText, makeSpanButton, null);
    }

    protected static Component makeHelpButton(String label, String helpText, boolean makeSpanButton, Character materialIcon) {
        if (label == null) {
            return null;
        }
        Component spanB;
        if (makeSpanButton) {
            SpanButton spanButton = new SpanButton(label, "ScreenItemFieldLabel");
            spanButton.setTextPosition(Component.RIGHT); //put icon on the left
            if (materialIcon != null) {
                spanButton.setMaterialIcon(materialIcon);
            }
            spanButton.setUIID("Container"); //avoid adding additional white space by setting the Container UIID to LabelField
            spanButton.setName("FieldContHlpSpanBut-" + label); //avoid adding additional white space by setting the Container UIID to LabelField
            spanButton.setIconUIID("ScreenItemFieldIcon"); //avoid adding additional white space by setting the Container UIID to LabelField
            spanB = spanButton;
        } else {
            Button button = new Button(label, "ScreenItemFieldLabel");
            button.setName("FieldContHlpBut-" + label); //avoid adding additional white space by setting the Container UIID to LabelField
            button.setTextPosition(Component.RIGHT); //put icon on the left
            if (materialIcon != null) {
                button.setMaterialIcon(materialIcon);
            }
            spanB = button;
        }

        if (helpText != null && !helpText.isEmpty()) {
            return addHelp(spanB, helpText);
        } else {
            return spanB;
        }
    }

    public static String SHOW_ALARM_SCREEN_REPLAY_CMD_ID = "ShowAlarmsId";
    public static String SHOW_BIG_TIMER_SCREEN_REPLAY_CMD_ID = "ShowBigTimerCmdId";
    private MyReplayCommand showAlarmsReplayCmd = null;
    private MyReplayCommand showBigTimerReplayCmd = null;

    /**
     * make the replay command to be used when showing, or replaying, the show
     * alarms
     *
     * @return a replay command (which is not used by the returning form, this
     * method is just to add it to the list of replay commands)
     */
    private MyReplayCommand makeAlarmsReplayCmd() {
//        Form form = Display.getInstance().getCurrent();
//        if (form instanceof ScreenListOfAlarms)
//            return null; //if already in alarm screen, don't create a replay command
        if (this instanceof ScreenListOfAlarms) {
            return null;
        }
        MyReplayCommand showOrRefreshScreenListOfAlarms = MyReplayCommand.createKeep(SHOW_ALARM_SCREEN_REPLAY_CMD_ID, 'x', (e) -> {
//            MyForm currentForm = MyForm.getCurrentFormAfterClosingDialogOrMenu();
            MyForm currentForm = this;
            if (currentForm instanceof ScreenListOfAlarms) {
//                if (false) {
//                    currentForm.refreshAfterEdit();
//                }
                if (Config.TEST) {
                    ASSERT.that("shouldn't happen since no LarmsReplayCmd should be generated/added for ScreenListOfAlarms");
                }
            } else {
//                ScreenListOfAlarms.getInstance().refreshAfterEdit(); //ALREADY done in ScreenListOfAlarms.getInstance().show() // refresh to ensure new list of alarms is shown
                ScreenListOfAlarms.getInstance().show(currentForm, true); //true=flip transition
            }
        });

        return showOrRefreshScreenListOfAlarms;
    }

    protected void setMyShowAlarmsReplayCmd(MyReplayCommand showAlarmsReplayCmd) {
        this.showAlarmsReplayCmd = showAlarmsReplayCmd;
    }

    protected MyReplayCommand getMyShowAlarmsReplayCmd() {
        return showAlarmsReplayCmd;

    }

    private MyReplayCommand makeBigTimerReplayCmd() {
        MyReplayCommand showBigTimer = MyReplayCommand.createKeep(SHOW_BIG_TIMER_SCREEN_REPLAY_CMD_ID, "", Icons.iconEdit, (e) -> {
            ASSERT.that(TimerStack.getInstance().getCurrentTimerInstanceN() != null, "makeBigTimerReplayCmd called from screen=" + getUniqueFormId() + " but TimerInstance==null");
            ASSERT.that(!(this instanceof ScreenListOfAlarms), "makeBigTimerReplayCmd called from from ScreenListOfAlarms, shoulnd't happen");
            new ScreenTimer6(this).show();
        });
        return showBigTimer;
    }

    protected void setBigTimerReplayCmd(MyReplayCommand showBigTimerReplayCmd) {
        this.showBigTimerReplayCmd = showBigTimerReplayCmd;
    }

    protected MyReplayCommand getBigTimerReplayCmd() {
        return showBigTimerReplayCmd;

    }

    interface CreateItem {

        Item createNewTask(boolean xxx);
    }

    interface SetItem {

        Item setLastCreatedItem(Item lastCreatedItem);
    }

////<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Command makeCreateInlineCmd(Item item, Item refItem, CreateItem createNewTask, SetItem lastCreatedItem, SaveEditedValuesLocally previousValues) {
//        return MyReplayCommand.create("CreateNewItemInline-" + item.getObjectIdP(), "", Icons.iconEdit, (ev) -> {
//
//            Item newItem = (Item) previousValues.get("CreateInlineNewItem", InlineInsertNewItemContainer2.createNewTask(true));
////                    Item newItem = newTaskTemp != null ? newTaskTemp : new Item();
////                    lastCreatedItem = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts)
//            lastCreatedItem.setLastCreatedItem(null); //reset value (in case ScreenItem does a Cancel meaning no more inserts)
//            //TODO!!!! create even if no text was entered into field
////                    MyForm myForm = (MyForm) getComponentForm();
//            MyForm myForm = (MyForm) Display.getInstance().getCurrent();
//            myForm.setKeepPos(new KeepInSameScreenPosition(refItem, this)); //if Cancel, keep the current item in place 
////                        new ScreenItem(lastCreatedItem, (MyForm) getComponentForm(), () -> {
//            new ScreenItem2(newItem, myForm, () -> {
//                //TODO!!! replace isDirty() with more fine-grained check on what has been changed and what needs to be refreshed
////                            DAO.getInstance().save(newTask);
//                insertNewTaskAndSaveChanges(newItem);
////                        if (false && myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
////                            myForm.setEditOnShowOrRefresh(null); //reset the previous editField
////                        }
////                        myForm.setKeepPos(new KeepInSameScreenPosition(newItem));
////                        lastCreatedItem = continueAddingNewItems ? newItem : null; //ensures that MyTree2 will create a new insertContainer after newTask
//                lastCreatedItem.setLastCreatedItem(continueAddingNewItems ? newItem : null); //ensures that MyTree2 will create a new insertContainer after newTask
////                        Container parent = getParent();
////                        parent.removeComponent(InlineInsertNewItemContainer2.this);
////replace the insert container with the created item, NOT GOOD approach since refrehsAfterEdit will rebuild, and not needed??!!
////                        if (false) {
//////                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                            Container parent = getParent();
////                            parent.replace(InlineInsertNewItemContainer2.this,
////                                    //                                ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewTasks2, null), MorphTransition.create(300));
////                                    ScreenListOfItems.buildItemContainer(myFormXXX, newItem, itemOrItemListForNewTasks, null), null, null, 300); //
////                        }
//                myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this, -1)); //if editing the new task in separate screen, 
//                myForm.refreshAfterEdit();  //OK? NOT good, refreshAfterEdit will remove the new 
//            }).show();
//        });
//    }
//    protected static Component layout(String fieldLabelTxt, Component field) {
//        return layout(fieldLabelTxt, field, null);
//    }
//    protected static Component layoutXXX(String fieldLabelTxt, Component field, boolean checkForTooLargeWidth) {
//        return layoutOLD(fieldLabelTxt, field, null, null, checkForTooLargeWidth, false, true);
//    }
////</editor-fold>
    protected static Component layout(String fieldLabelTxt, Component field, String help) {
//        return layoutOLD(fieldLabelTxt, field, help, field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue() : null, true, false, true);
        return new EditFieldContainer(fieldLabelTxt, field, help,
                (field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue() : null),
                true, false, true, false);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Component layoutXXX(String fieldLabelTxt, Component field, String help, boolean wrapText) {
//        return layoutOLD(fieldLabelTxt, field, help, field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue() : null, wrapText, true, true);
//    }
//    protected static Component layout(String fieldLabelTxt, Component field, String help, boolean wrapText, boolean makeFieldUneditable) {
//        return layout(fieldLabelTxt, field, help, null, wrapText, makeFieldUneditable, true);
//    }
//    protected static Component layoutXXX(String fieldLabelTxt, Component field, String help, boolean wrapText, boolean makeFieldUneditable, boolean hideEditButton) {
////        return layout(fieldLabelTxt, field, help, (field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue(): null), wrapText, makeFieldUneditable, hideEditButton);
//        return layoutOLD(fieldLabelTxt, field, help, null, wrapText, makeFieldUneditable, hideEditButton);
//    }
//    protected static Component layout(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear) {
//        return layout(fieldLabelTxt, field, help, swipeClear, true, false, false);
//    }
//    protected static Component layoutXXX(String fieldLabelTxt, MyDateAndTimePicker field, String help) {
//        return layoutOLD(fieldLabelTxt, field, help, () -> field.swipeClear(), true, false, false);
//    }
//    protected static Component layoutXXX(String fieldLabelTxt, MyDatePicker field, String help) {
//        return layoutOLD(fieldLabelTxt, field, help, () -> field.swipeClear(), true, false, false);
//    }
//    protected static Component layoutXXX(String fieldLabelTxt, MyDurationPicker field, String help) {
//        return layoutOLD(fieldLabelTxt, field, help, () -> field.swipeClear(), true, false, false);
//    }
//    protected static Component layout(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear, boolean wrapText) {
//        return layout(fieldLabelTxt, field, help, swipeClear, wrapText, false, false);
//    }
//</editor-fold>
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
//    protected static Component layoutNXXX(String fieldLabelTxt, MyComponentGroup group, String help) {
//        return layoutN(fieldLabelTxt, group, help, null, true, false, false, true);
//    }
    protected static Component layoutN(String fieldLabelTxt, MyToggleButton toggleButton, String help) {
        return layoutN(fieldLabelTxt, toggleButton, help, null, true, false, false, true);
    }

    protected static Component layoutN(String fieldLabelTxt, Picker field, String help) {
        return layoutN(fieldLabelTxt, field, help,
                //                field instanceof MyDurationPicker 
                //                        ? (() -> ((MyDurationPicker) field).swipeClear())
                //                : (field instanceof MyDatePicker ? () -> ((MyDatePicker) field).swipeClear() : () -> ((MyDateAndTimePicker) field).swipeClear()),
                (field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue() : null),
                true, false, false, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Picker field, String help, Character materialIcon) {
        return new EditFieldContainer(fieldLabelTxt, field, help,
                //                field instanceof MyDurationPicker
                //                        ? (() -> ((MyDurationPicker) field).swipeClear())
                //                        : (field instanceof MyDatePicker
                //                                ? () -> ((MyDatePicker) field).swipeClear()
                //                                : () -> ((MyDateAndTimePicker) field).swipeClear()),
                (field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue() : null),
                true, false, false, false, false, materialIcon);
//        return EditFieldContainer(fieldLabelTxt, field, help, field instanceof MyDurationPicker ? (() -> ((MyDurationPicker) field).swipeClear())
//                : (field instanceof MyDatePicker ? () -> ((MyDatePicker) field).swipeClear() : () -> ((MyDateAndTimePicker) field).swipeClear()),
//                true, false, false, false, false,materialIcon);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Component layoutN(String fieldLabelTxt, Picker field, String help,
//            boolean wrapText, boolean makeFieldUneditable, boolean hideEditButton) {
//        return layoutOLD(fieldLabelTxt, field, help,
////                field instanceof MyDurationPicker ? (() -> ((MyDurationPicker) field).swipeClear())
////                        : field instanceof MyDatePicker ? () -> ((MyDatePicker) field).swipeClear()
////                                : () -> ((MyDateAndTimePicker) field).swipeClear(),
//                (field instanceof SwipeClear ? () -> ((SwipeClear) field).clearFieldValue() : null),
//                wrapText, makeFieldUneditable, hideEditButton, false);
//    }
//</editor-fold>
    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear) {
        return layoutN(fieldLabelTxt, field, help, swipeClear, true, false, true, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help) { //normal edit field with [>]
//        return layoutN(fieldLabelTxt, field, help, null, true, false, true, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, null, true, false, false, true);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, Character materialIcon) { //normal edit field with [>]
        return layoutN(fieldLabelTxt, field, help, null, true, false, true, true, false, materialIcon);
    }

    protected static Component layoutN(boolean sizeWestBeforeEast, String fieldLabelTxt, Component field, String help) { //normal edit field with [>]
//        return layoutN(fieldLabelTxt, field, help, null, true, false, true, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, null, true, false, true, false, sizeWestBeforeEast, null);
    }

    protected static Component layoutN(boolean sizeWestBeforeEast, String fieldLabelTxt, Component field, String help, Character materialIcon) { //normal edit field with [>]
//        return layoutN(fieldLabelTxt, field, help, null, true, false, true, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, null, true, false, true, false, sizeWestBeforeEast, materialIcon);
    }

//    protected static Component layoutN(boolean visibleEditButton, String fieldLabelTxt, Component field, String help) { //normal edit field with [>]
//        return layoutN(fieldLabelTxt, field, help, null, true, false, visibleEditButton, false);
//    }
    protected static Component layoutN(String fieldLabelTxt, MyOnOffSwitch onOffSwitch, String help) { //normal edit field with [>]
        return layoutN(fieldLabelTxt, onOffSwitch, help, null, true, false, false, true);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, boolean showAsFieldUneditable) {
//        return layoutN(fieldLabelTxt, field, help, null, true, showAsFieldUneditable, false, false);
//        return layoutN(fieldLabelTxt, field, help, null, true, showAsFieldUneditable, !showAsFieldUneditable, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, null, true, showAsFieldUneditable, !showAsFieldUneditable, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, boolean showAsFieldUneditable, Character materialIcon) {
//        return layoutN(fieldLabelTxt, field, help, null, true, showAsFieldUneditable, false, false);
//        return layoutN(fieldLabelTxt, field, help, null, true, showAsFieldUneditable, !showAsFieldUneditable, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, null, true, showAsFieldUneditable, !showAsFieldUneditable, false, materialIcon);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton) {
//        return layoutN(fieldLabelTxt, field, help, null, wrapText, showAsFieldUneditable, visibleEditButton, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, null, wrapText, showAsFieldUneditable, visibleEditButton, false, false, null);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, Character materialIcon) {
//        return layoutN(fieldLabelTxt, field, help, null, wrapText, showAsFieldUneditable, visibleEditButton, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, null, wrapText, showAsFieldUneditable, visibleEditButton, false, false, materialIcon);
    }

    protected static Component layoutN(boolean sizeWestBeforeEast, String fieldLabelTxt, Component field, String help,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton) {
//        return layoutN(fieldLabelTxt, field, help, null, wrapText, showAsFieldUneditable, visibleEditButton, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, null, wrapText, showAsFieldUneditable, visibleEditButton, false, sizeWestBeforeEast, null);

    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help,
            boolean showAsFieldUneditable, boolean visibleEditButton) {
        return layoutN(fieldLabelTxt, field, help, null, false, showAsFieldUneditable, visibleEditButton, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton) {
        return layoutN(fieldLabelTxt, field, help, swipeClear, wrapText, showAsFieldUneditable, visibleEditButton, false);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, Character materialIcon) {
//        return layoutN(fieldLabelTxt, field, help, swipeClear, wrapText, showAsFieldUneditable, visibleEditButton, false);
        return new EditFieldContainer(fieldLabelTxt, field, help, swipeClear, wrapText, showAsFieldUneditable, visibleEditButton, false, false, materialIcon);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton) {
        return new EditFieldContainer(fieldLabelTxt, field, help, swipeClear, wrapText, showAsFieldUneditable, visibleEditButton, hiddenEditButton);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton, boolean sizeWestBeforeEast) {
        return new EditFieldContainer(fieldLabelTxt, field, help, swipeClear, wrapText, showAsFieldUneditable, visibleEditButton, hiddenEditButton, sizeWestBeforeEast, null);
    }

    protected static Component layoutN(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton,
            boolean sizeWestBeforeEast, Character materialIcon) {
        return new EditFieldContainer(fieldLabelTxt, field, help, swipeClear, wrapText, showAsFieldUneditable,
                visibleEditButton, hiddenEditButton, sizeWestBeforeEast, materialIcon);
    }
////<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Component layoutNXXX(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
//            boolean wrapText, boolean showAsFieldUneditable, boolean visibleEditButton, boolean hiddenEditButton) {
//        if (field instanceof OnOffSwitch | field instanceof MyOnOffSwitch) {
////            field.getAllStyles().setPaddingRight(6);
//        } else {
//            if (field instanceof WrapButton) {
////                ((SpanButton) field).setTextUIID(showAsUneditableField ? "LabelFixed" : "SpanButtonTextAreaValueRight");
//                ((WrapButton) field).setTextUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
//                ((WrapButton) field).setUIID("Container");
//            } else {//if (!(field instanceof MyComponentGroup)) {
//                field.setUIID(showAsFieldUneditable ? "LabelFixed" : "LabelValue");
//            }
//        }
//
//        //EDIT FIELD
//        Component visibleField = null; //contains the edit field and possibly the edit button
////        if (hideEditButton) {
//        if (!visibleEditButton && hiddenEditButton) {
//            visibleField = field;
//        } else { //place a visible or invisible button
//            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
////            boolean editButtonHidden = makeFieldUneditable || hideEditButton; //invisible if uneditable or if explicitly make invisible
////            editFieldButton.setVisible(!editButtonInvisible);
////            editFieldButton.setVisible(!showAsFieldUneditable || visibleEditButton); //Visible, but still using space
//            editFieldButton.setVisible(!showAsFieldUneditable || visibleEditButton); //Visible, but still using space
////            editFieldButton.setVisible(!hideEditButton);
//            editFieldButton.setHidden(hiddenEditButton); //hidden, not taking any space
////<editor-fold defaultstate="collapsed" desc="comment">
////            visibleField = FlowLayout.encloseRightMiddle(field, editFieldButton);
////            visibleField = BoxLayout.encloseXNoGrow(field, editFieldButton);
////            visibleField = BoxLayout.encloseX(field, editFieldButton);
////            visibleField = BorderLayout.centerEastWest(null, field, editFieldButton);
////</editor-fold>
//            visibleField = BorderLayout.centerEastWest(field, editFieldButton, null);
//            if (field instanceof WrapButton) {
//                ((Container) visibleField).setLeadComponent(((WrapButton) field).getActualButton());
//            } else {
//                ((Container) visibleField).setLeadComponent(field);
//            }
//        }
//
//        Container fieldContainer = new Container(new BorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
//
//        //SWIPE CLEAR
//        if (swipeClear != null) { //ADD SWIPE to delete
//            SwipeableContainer swipeCont;
//            assert !showAsFieldUneditable : "showAsUneditableField should never be true if we also define a swipeClear function";
//            Button swipeDeleteFieldButton = new Button();
//            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
//            ActionListener l = (ev) -> {
//                swipeClear.clearFieldValue();
//                fieldContainer.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
//                swipeCont.close();
//            };
//            swipeCont.addSwipeOpenListener(l);
//            swipeDeleteFieldButton.setCommand(Command.create("", Icons.iconCloseCircleLabelStyle, l));
//            visibleField = swipeCont;
//        }
//
//        //FIELD LABEL
//        Component fieldLabel = makeHelpButton(fieldLabelTxt, help, wrapText);
//        if (wrapText) {
//            int availDisplWidth = (Display.getInstance().getDisplayWidth() * 90) / 100; //asumme roughly 90% of width is available after margins
////            int availDisplWidthParent = getPaDisplay.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
//            int labelPreferredW = fieldLabel.getPreferredW();
//            int fieldPreferredW = visibleField.getPreferredW();
//            if (labelPreferredW + fieldPreferredW > availDisplWidth) { //if too wide
//                if (field instanceof MyComponentGroup) { //MyComponentGroups cannot wrap and must be shown fully so split on *two* lines
//                    fieldContainer.add(BorderLayout.NORTH, fieldLabel);
//                    fieldContainer.add(BorderLayout.EAST, visibleField);
//                } else {
//                    int widthFirstColumn = 0;
//                    int labelRelativeWidthPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
//                    int labelScreenWidthPercent = labelPreferredW * 100 / (availDisplWidth);
//                    if (true) {
//                        if (labelScreenWidthPercent < 45 && fieldLabelTxt.indexOf(" ") == -1) { //label takes up less than 45% of avail space and no spaces (no wrap)
//                            widthFirstColumn = labelScreenWidthPercent;
//                        }
//                    } else {
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
//                        if (labelRelativeWidthPercent > 70) { //visibleField takes up less than 30% of avail space
//                            widthFirstColumn = 70; //first column gets the rest
//                        } else if (labelRelativeWidthPercent < 45 && fieldLabelTxt.indexOf(" ") == -1) { //label takes up less than 45% of avail space and no spaces (no wrap)
//                            widthFirstColumn = labelRelativeWidthPercent; //give it full space (no wrap)
//                        } else if (labelRelativeWidthPercent < 30) { //visibleField takes up less than 30% of avail space
////                        int widthVisibleFieldPercent = 100 - widthFieldLabelPercent;
////                        int widthLabelPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
//                            widthFirstColumn = 30; //Math.min(Math.max(widthLabelPercent, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
//                        }
//                    }
//                    TableLayout tl = new TableLayout(1, 2);
//                    tl.setGrowHorizontally(true); //grow the remaining right-most column
////                fieldContainer = new Container(tl);
//                    fieldContainer.setLayout(tl);
//                    fieldContainer.
//                            add(tl.createConstraint().verticalAlign(Component.CENTER).horizontalAlign(Component.LEFT).widthPercentage(widthFirstColumn), fieldLabel).
//                            add(tl.createConstraint().verticalAlign(Component.CENTER).horizontalAlign(Component.RIGHT), visibleField); //align center right
//                }
//            } else {
//                fieldContainer.add(BorderLayout.WEST, fieldLabel);
//                fieldContainer.add(BorderLayout.EAST, visibleField);
//            }
//        } else {
//            fieldContainer.add(BorderLayout.WEST, fieldLabel);
//            fieldContainer.add(BorderLayout.EAST, visibleField);
//        }
//        fieldContainer.revalidate(); //right way to get the full text to size up?
//        return fieldContainer;
//    }
//    protected static Component layoutOLD(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
//            boolean wrapText, boolean makeFieldUneditable, boolean hideEditButton) {
//        return layoutOLD(fieldLabelTxt, field, help, swipeClear, wrapText, makeFieldUneditable, hideEditButton, false);
//    }

//    protected static Component layoutOLD(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear,
//            boolean wrapText, boolean makeFieldUneditable, boolean hideEditButton, boolean forceVisibleEditButton) {
//
////        if (field instanceof OnOffSwitch | field instanceof MyOnOffSwitch) {
//        if (field instanceof MyOnOffSwitch) {
////            field.getAllStyles().setPaddingRight(6);
//        } else {
//            if (field instanceof MySpanButton) {
////                ((SpanButton) field).setTextUIID(showAsUneditableField ? "LabelFixed" : "SpanButtonTextAreaValueRight");
//                ((MySpanButton) field).setTextUIID(makeFieldUneditable ? "ScreenItemValueUneditable" : "ScreenItemEditableValue");
//                ((MySpanButton) field).setUIID("Container");
//            } else {
//                field.setUIID(makeFieldUneditable ? "ScreenItemValueUneditable" : "ScreenItemEditableValue");
//            }
//        }
//
//        //EDIT FIELD
//        Component visibleField = null; //contains the edit field and possibly the edit button
//        if (hideEditButton) {
//            visibleField = field;
//        } else { //place a visible or invisible button
////            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
//            Label editFieldButton = new Label("", "IconEdit"); // [>]
//            editFieldButton.setMaterialIcon(Icons.iconEdit);
//            boolean editButtonHidden = makeFieldUneditable || hideEditButton; //invisible if uneditable or if explicitly make invisible
////            editFieldButton.setVisible(!editButtonInvisible);
//            editFieldButton.setVisible(!(makeFieldUneditable || hideEditButton) || forceVisibleEditButton); //Visible, but still using space
////            editFieldButton.setVisible(!hideEditButton);
//            editFieldButton.setHidden(editButtonHidden || !forceVisibleEditButton); //hidden, not taking any space
////<editor-fold defaultstate="collapsed" desc="comment">
////            visibleField = FlowLayout.encloseRightMiddle(field, editFieldButton);
////            visibleField = BoxLayout.encloseXNoGrow(field, editFieldButton);
////            visibleField = BoxLayout.encloseX(field, editFieldButton);
////            visibleField = BorderLayout.centerEastWest(null, field, editFieldButton);
////</editor-fold>
//            visibleField = MyBorderLayout.centerEastWest(field, editFieldButton, null);
//            if (field instanceof MySpanButton) {
////                ((Container) visibleField).setLeadComponent(((WrapButton) field).getActualButton());
//                ((Container) visibleField).setLeadComponent(field);
//            } else {
//                ((Container) visibleField).setLeadComponent(field);
//            }
//        }
//
//        Container fieldContainer = new Container(new MyBorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
//
//        //SWIPE CLEAR
//        if (swipeClear != null) { //ADD SWIPE to delete
//            SwipeableContainer swipeCont;
//            assert !makeFieldUneditable : "showAsUneditableField should never be true if we also define a swipeClear fucntion";
//            Button swipeDeleteFieldButton = new Button();
//            swipeDeleteFieldButton.setUIID("ClearFieldButton");
//            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
//            ActionListener l = (ev) -> {
//                swipeClear.clearFieldValue();
//                fieldContainer.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
//                swipeCont.close();
//            };
//            swipeCont.addSwipeOpenListener(l);
////            swipeDeleteFieldButton.setCommand(CommandTracked.create("", Icons.iconCloseCircleLabelStyle, l, "SwipeDeleteField"));
//            swipeDeleteFieldButton.setCommand(CommandTracked.create("", Icons.iconCloseCircle, l, "SwipeDeleteField"));
//            visibleField = swipeCont;
//        }
//
//        //FIELD LABEL
//        Component fieldLabel = makeHelpButton(fieldLabelTxt, help, wrapText);
//        if (wrapText) {
//            int availDisplWidth = Display.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
////            int availDisplWidthParent = getPaDisplay.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
//            int labelPreferredW = fieldLabel.getPreferredW();
//            int fieldPreferredW = visibleField.getPreferredW();
//            if (labelPreferredW + fieldPreferredW > availDisplWidth) { //if too wide
//                if (field instanceof MyComponentGroup) { //MyComponentGroups cannot wrap and must be shown fully so split on *two* lines
//                    fieldContainer.add(MyBorderLayout.NORTH, fieldLabel);
//                    fieldContainer.add(MyBorderLayout.EAST, visibleField);
//                } else {
//                    int widthFirstColumn = 0;
//                    int relativeWidthFieldPercent = fieldPreferredW * 100 / availDisplWidth;
//                    int relativeWidthLabelPercent = labelPreferredW * 100 / availDisplWidth;
//                    if (relativeWidthFieldPercent < 30) { //visibleField takes up less than 30% of avail space 
////                        int widthFirstColumn = Math.min(Math.max(fieldLabelPreferredW / visibleFieldPreferredW * 100, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
//                        widthFirstColumn = 100 - relativeWidthFieldPercent; //first column gets the rest
//                    }
//                    if (relativeWidthLabelPercent < 45 && fieldLabelTxt.indexOf(" ") == -1) { //label takes up less than 45% of avail space and no spaces (no wrap)
//                        widthFirstColumn = relativeWidthLabelPercent; //give it full space (no wrap)
//                    } else {
////                        int widthVisibleFieldPercent = 100 - widthFieldLabelPercent;
//                        int widthLabelPercent = labelPreferredW * 100 / (labelPreferredW + fieldPreferredW);
//                        widthFirstColumn = Math.min(Math.max(widthLabelPercent, 30), 70); //30 to avoid first field gets smaller than 30%, 70 to avoid it gets wider than 70%
//                    }
//                    TableLayout tl = new TableLayout(1, 2);
//                    tl.setGrowHorizontally(true); //grow the remaining right-most column
////                fieldContainer = new Container(tl);
//                    fieldContainer.setLayout(tl);
//                    fieldContainer.
//                            add(tl.createConstraint().widthPercentage(widthFirstColumn), fieldLabel).
//                            //                            add(visibleField);
//                            add(tl.createConstraint().verticalAlign(Component.CENTER).horizontalAlign(Component.RIGHT), visibleField); //align center right
////<editor-fold defaultstate="collapsed" desc="comment">
////                fieldLabel.getAllStyles().setMarginBottom(0);
////                fieldLabel.getAllStyles().setPaddingBottom(0); //reduce space between label and field
////                visibleField.getAllStyles().setMarginBottom(0);
////                visibleField.getAllStyles().setPaddingTop(0);
////                fieldContainer.add(BorderLayout.NORTH, fieldLabel);
////                fieldContainer.add(BorderLayout.EAST, visibleField); //label NORTH
////</editor-fold>
//                }
//            } else {
//                fieldContainer.add(MyBorderLayout.WEST, fieldLabel);
//                fieldContainer.add(MyBorderLayout.EAST, visibleField);  //label WEST
//            }
//        } else {
//            fieldContainer.add(MyBorderLayout.WEST, fieldLabel);
//            fieldContainer.add(MyBorderLayout.EAST, visibleField);
//        }
//
//        return fieldContainer;
//    }
//</editor-fold>
    protected static Component layoutSetting(String fieldLabelTxt, Component field, String help) {

        if (!(field instanceof MyOnOffSwitch) && field != null) {
            field.setUIID("ScreenItemEditableValue");
        }

        MyBorderLayout layout = MyBorderLayout.center();
//        layout.setScaleEdges(false);
        layout.setSizeEastWestMode(MyBorderLayout.SIZE_EAST_BEFORE_WEST);
        Container fieldContainer = new Container(layout, "SettingContainer");

//        Component fieldLabel = makeHelpButton(fieldLabelTxt, help, true);
//        SpanButton fieldLabel = (SpanButton) makeHelpButton(fieldLabelTxt, help, true);
        Component fieldLabel = makeHelpButton(fieldLabelTxt, help, true);
//        fieldContainer.add(MyBorderLayout.WEST, fieldLabel);
        if (false) {
            fieldContainer.add(WEST, fieldLabel);
        }
//        fieldContainer.add(WEST, fieldLabel);
//        fieldContainer.add(MyBorderLayout.EAST, field);  //label WEST
        if (field != null) {
            fieldContainer.add(EAST, field);  //label WEST
        }
        fieldContainer.add(WEST, fieldLabel);
        return fieldContainer;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Component layoutOLD4(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear, boolean checkForTooLargeWidth, boolean showAsUneditableField, boolean noEditButton) {
//
//        if (field instanceof OnOffSwitch) {
//            field.getAllStyles().setPaddingRight(6);
//        } else {
//            field.setUIID(showAsUneditableField ? "LabelFixed" : "LabelValue");
//            if (field instanceof SpanButton) {
//                ((SpanButton) field).setTextUIID(showAsUneditableField ? "LabelFixed" : "SpanButtonTextAreaValueRight");
//            }
//        }
//
//        //EDIT FIELD
//        Component visibleField = null; //contains the edit field and possibly the edit button
//        if (noEditButton) {
//            visibleField = field;
//        } else { //place a visible or invisible button
//            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
//            editFieldButton.setVisible(!showAsUneditableField && !noEditButton);
////            visibleField = FlowLayout.encloseRightMiddle(field, editFieldButton);
////            visibleField = BoxLayout.encloseXNoGrow(field, editFieldButton);
//            visibleField = BoxLayout.encloseX(field, editFieldButton);
////BoxLayout box = new BoxLayout(BoxLayout.X_AXIS);
////box.
////            visibleField = BorderLayout.centerEastWest(null, field, editFieldButton);
//            ((Container) visibleField).setLeadComponent(field);
//        }
//
//        Container fieldContainer = new Container(new BorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
//
//        //SWIPE CLEAR
//        if (swipeClear != null) { //ADD SWIPE to delete
//            SwipeableContainer swipeCont;
//            assert !showAsUneditableField : "showAsUneditableField should never be true if we also define a swipeClear fucntion";
//            Button swipeDeleteFieldButton = new Button();
//            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
//            swipeDeleteFieldButton.setCommand(Command.create("", Icons.iconCloseCircleLabelStyle, (ev) -> {
//                swipeClear.clearFieldValue();
//                fieldContainer.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
//                swipeCont.close();
//            }));
//            visibleField = swipeCont;
//        }
//
//        //FIELD LABEL
//        Component fieldLabel = makeHelpButton(fieldLabelTxt, help, checkForTooLargeWidth);
//        if (checkForTooLargeWidth) {
//            int availDisplWidth = Display.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
//            int fieldLabelPreferredW = fieldLabel.getPreferredW();
//            int visibleFieldPreferredW = visibleField.getPreferredW();
//            if (fieldLabelPreferredW + visibleFieldPreferredW > availDisplWidth) { //if too wide
//                fieldLabel.getAllStyles().setMarginBottom(0);
//                fieldLabel.getAllStyles().setPaddingBottom(0); //reduce space between label and field
//                visibleField.getAllStyles().setMarginBottom(0);
//                visibleField.getAllStyles().setPaddingTop(0);
//                fieldContainer.add(BorderLayout.NORTH, fieldLabel);
//                fieldContainer.add(BorderLayout.EAST, visibleField); //label NORTH
//            } else {
//                fieldContainer.add(BorderLayout.WEST, fieldLabel);
//                fieldContainer.add(BorderLayout.EAST, visibleField);  //label WEST
//            }
//        } else {
//            fieldContainer.add(BorderLayout.WEST, fieldLabel);
//            fieldContainer.add(BorderLayout.EAST, visibleField);
//        }
//
//        return fieldContainer;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Component layoutOLD3(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear, boolean checkForTooLargeWidth, boolean showAsUneditableField, boolean noButton) {
////        return BorderLayout.center(addHelp(new Button(label), help)).add(BorderLayout.EAST, field);
////        new SwipeClearContainer(BoxLayout.encloseXNoGrow(field), swipeClear);
//        boolean labelAndFieldOnSeparateLines = false;
//
//        if (field instanceof OnOffSwitch) {
//            field.getAllStyles().setPaddingRight(6);
//        } else {
//            field.setUIID(showAsUneditableField ? "LabelFixed" : "LabelValue");
//            if (field instanceof SpanButton) {
////                ((SpanButton) field).setUIID(showAsUneditableField ? "LabelFixed" : "LabelValue");
//                ((SpanButton) field).setTextUIID(showAsUneditableField ? "LabelFixed" : "SpanButtonTextAreaValueRight");
//            }
//        }
//
////        Container fieldContainer = null; //contains the edit field and possibly the edit button
//        Component visibleField = null; //contains the edit field and possibly the edit button
//        if (noButton) {
//            visibleField = field;
//        } else { //place a visible or invisible button
////<editor-fold defaultstate="collapsed" desc="comment">
////            Label editFieldButton = null;
////Define [>] button
////            editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "LabelValue"); // [>]
////</editor-fold>
//            Label editFieldButton = new Label(Icons.iconEditSymbolLabelStyle, "IconEdit"); // [>]
//            editFieldButton.setVisible(!showAsUneditableField && !noButton);
////<editor-fold defaultstate="collapsed" desc="comment">
////            editFieldIcon.setHidden(noVisibleEditButton);
////            editFieldButton.getAllStyles().setMarginLeft(0);
////            editFieldButton.getAllStyles().setPaddingLeft(0); //TODO move this formatting to theme
////            fieldValueEdit = FlowLayout.encloseIn(swipeCont, editFieldIcon);
////            fieldValueEdit = FlowLayout.encloseRightMiddle(swipeCont, editFieldIcon);
////            visibleField = BoxLayout.encloseXNoGrow(field, editFieldButton); //keeps left position as size of content varies
////</editor-fold>
//            visibleField = FlowLayout.encloseRightMiddle(field, editFieldButton);
////            ((Container) fieldContainer).setLeadComponent(field);
//            ((Container) visibleField).setLeadComponent(field);
//        }
//
//        Container l = new Container(new BorderLayout()); // = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
//
//        //SWIPE CLEAR
////        SwipeableContainer fieldCont = new SwipeableContainer(null, BoxLayout.encloseXNoGrow(deleteFieldButton), field);
//        if (swipeClear != null) { //ADD SWIPE to delete
//            SwipeableContainer swipeCont;
//            assert !showAsUneditableField : "showAsUneditableField should never be true if we also define a swipeClear fucntion";
//            Button swipeDeleteFieldButton = new Button();
////        SwipeableContainer fieldCont = new SwipeableContainer(null, deleteFieldButton, field);
//            swipeCont = new SwipeableContainer(null, swipeDeleteFieldButton, visibleField);
////            swipeDeleteFieldButton.setCommand(Command.create("Delete", Icons.iconCloseCircleLabelStyle, (ev) -> {
//            swipeDeleteFieldButton.setCommand(Command.create("", Icons.iconCloseCircleLabelStyle, (ev) -> {
//                swipeClear.clearFieldValue();
////<editor-fold defaultstate="collapsed" desc="comment">
////                field.repaint(); //??
////                visibleField.repaint(); //??
////                swipeCont.getComponentAt(1).repaint();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
////                ((Container)swipeCont.getComponentAt(1)).revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
////</editor-fold>
//                l.revalidate();//in Swipeable constructor, top component is added after non-null swipe components so should be index 1 //repaint before closing
//                swipeCont.close();
////                field.repaint(); //??
////<editor-fold defaultstate="collapsed" desc="comment">
////            cont.repaint();
////            this.getParent().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field? NO, shows Delete button on top of <set>
////            repaint();
////            getComponentForm().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field?
////</editor-fold>
//            }));
//            visibleField = swipeCont;
//        }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        Container l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, FlowLayout.encloseCenterMiddle(field));
////        Container fieldValueEdit = FlowLayout.encloseCenter(fieldCont, new Label(Icons.iconEditSymbolLabelStyle, "LabelValue"));
////        Container fieldValueEdit = FlowLayout.encloseCenter(fieldCont, editFieldIcon);
////</editor-fold>
//        Component fieldLabel;
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (checkForTooLargeWidth) {
////        } else {
////            fieldLabel = makeHelpButton(fieldLabelTxt, help, !checkForTooLargeWidth);
////        }
////</editor-fold>
//        fieldLabel = makeHelpButton(fieldLabelTxt, help, checkForTooLargeWidth);
//
//        if (checkForTooLargeWidth) {
////            l.revalidate();
////            if (fieldLabel.getWidth() < fieldLabel.getPreferredW() || fieldValueEdit.getWidth() < fieldValueEdit.getPreferredW()) { //if either of the fields got less width than needed
//            int availDisplWidth = Display.getInstance().getDisplayWidth() * 10 / 10; //asumme roughly 90% of width is available after margins
//            int fieldLabelPreferredW = fieldLabel.getPreferredW();
//            int visibleFieldPreferredW = visibleField.getPreferredW();
//            if (fieldLabelPreferredW + visibleFieldPreferredW > availDisplWidth) { //if too wide
//                fieldLabel.getAllStyles().setMarginBottom(0);
//                fieldLabel.getAllStyles().setPaddingBottom(0); //reduce space between label and field
//                visibleField.getAllStyles().setMarginBottom(0);
//                visibleField.getAllStyles().setPaddingTop(0);
////<editor-fold defaultstate="collapsed" desc="comment">
////                l.removeComponent(fieldLabel); //
//////                l = BorderLayout.north(fieldLabel).add(BorderLayout.EAST, fieldValueEdit);
////                l.add(BorderLayout.NORTH, fieldLabel);
////                l = BorderLayout.north(fieldLabel).add(BorderLayout.EAST, visibleField); //label NORTH
////</editor-fold>
//                l.add(BorderLayout.NORTH, fieldLabel);
//                l.add(BorderLayout.EAST, visibleField); //label NORTH
//            } else //            l = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);  //label WEST
//            {
////                l = BorderLayout.west(fieldLabel).add(BorderLayout.EAST, visibleField);  //label WEST
//                l.add(BorderLayout.WEST, fieldLabel);
//                l.add(BorderLayout.EAST, visibleField);  //label WEST
//            }//<editor-fold defaultstate="collapsed" desc="comment">
////            if (false) {
//////    int availDisplWidth = Display.getInstance().getDisplayWidth() * 9 / 10; //asumme roughly 90% of width is available after margins
//////    int fieldLabelPreferredW = fieldLabel.getPreferredW();
//////    int fieldValueEditPreferredW = fieldValueEdit.getPreferredW();
//////    if (fieldLabelPreferredW + fieldValueEditPreferredW > availDisplWidth) { //if too wide
//////{
//////            } else {
//////            }
////                if (false && checkForTooLargeWidth) {
////                    if (l.getPreferredW() > Display.getInstance().getDisplayWidth()) { //if too wide, put label in North (so it becomes two separate lines)
////                        if (field instanceof TextArea || field instanceof TextField) {
//////                    field.setSameWidth(d);
////                            Dimension d = field.getPreferredSize();
////                            d.setWidth(Display.getInstance().getDisplayWidth() / 2); //allow a textField to take up mox half the screen size.
////                            field.setPreferredSize(d); //TODO!!! CN1 Support: setPreferredSize deprecated, but what is right way then??
////                        } else {
//////                    l.removeAll(); //remove already added components to avoid getting an error about adding twice
////                            field.getParent().removeComponent(field); //remove already added components to avoid getting an error about adding twice
////                            l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, field);
////                        }
////                    }
////                }
////
////                if (false) {
////                    if (labelAndFieldOnSeparateLines) {
//////                        l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, fieldContainer);
////                    } else {
//////                        l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.SOUTH, fieldContainer);
////                    }
////                }
////            }
////</editor-fold>
//        } else {
////            l = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, visibleField);
////            l = BorderLayout.west(fieldLabel).add(BorderLayout.EAST, visibleField);
//            l.add(BorderLayout.WEST, fieldLabel);
//            l.add(BorderLayout.EAST, visibleField);
//        }
//
////        return BorderLayout.center(helpBut(label, help)).add(BorderLayout.EAST, field);
//        return l;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Component layoutOLD2(String fieldLabelTxt, Component field, String help, SwipeClear swipeClear, boolean checkForTooLargeWidth, boolean showAsUneditableField, boolean noVisibleEditButton) {
////        return BorderLayout.center(addHelp(new Button(label), help)).add(BorderLayout.EAST, field);
////        new SwipeClearContainer(BoxLayout.encloseXNoGrow(field), swipeClear);
//        boolean labelAndFieldOnSeparateLines = false;
//
//        field.setUIID(showAsUneditableField ? "LabelFixed" : "LabelValue");
//
//        Label editFieldIcon = null;
//        if (!noVisibleEditButton) {
//            //Define [>] button
//            editFieldIcon = new Label(Icons.iconEditSymbolLabelStyle, "LabelValue"); // [>]
//            editFieldIcon.setVisible(!showAsUneditableField && !noVisibleEditButton);
////            editFieldIcon.setHidden(noVisibleEditButton);
//            editFieldIcon.getAllStyles().setMarginLeft(0);
//            editFieldIcon.getAllStyles().setPaddingLeft(0); //TODO move this formatting to theme
//        }
//
////        SwipeableContainer fieldCont = new SwipeableContainer(null, BoxLayout.encloseXNoGrow(deleteFieldButton), field);
//        Component fieldContainer = null;
//        SwipeableContainer swipeCont;
//        if (swipeClear != null) { //ADD SWIPE to delete
//            assert !showAsUneditableField : "showAsUneditableField should never be true if we also define a swipeClear fucntion";
//            Button deleteFieldButton = new Button();
////        SwipeableContainer fieldCont = new SwipeableContainer(null, deleteFieldButton, field);
//            swipeCont = new SwipeableContainer(null, deleteFieldButton, field);
//            deleteFieldButton.setCommand(Command.create("Delete", Icons.iconCloseCircleLabelStyle, (ev) -> {
//                swipeClear.clearFieldValue();
//                swipeCont.close();
//                field.repaint(); //??
////<editor-fold defaultstate="collapsed" desc="comment">
////            cont.repaint();
////            this.getParent().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field? NO, shows Delete button on top of <set>
////            repaint();
////            getComponentForm().revalidate(); //enough to relayout/resize the field eg when adding a date to a previously empty field?
////</editor-fold>
//            }));
////            fieldValueEdit = FlowLayout.encloseIn(swipeCont, editFieldIcon);
////            fieldValueEdit = FlowLayout.encloseRightMiddle(swipeCont, editFieldIcon);
//            if (swipeClear != null) {
//                fieldContainer = swipeCont;
//            } else {
//                fieldContainer = BoxLayout.encloseXNoGrow(swipeCont, editFieldIcon);
//                ((Container) fieldContainer).setLeadComponent(field);
//            }
//        } else {
////            fieldValueEdit = FlowLayout.encloseIn(field, editFieldIcon);
////            fieldValueEdit = FlowLayout.encloseRightMiddle(field, editFieldIcon);
//            if (noVisibleEditButton) {
////                fieldContainer = swipeCont;
//            } else {
//                fieldContainer = BoxLayout.encloseXNoGrow(field, editFieldIcon);
//                if (!showAsUneditableField) {
//                    ((Container) fieldContainer).setLeadComponent(field);
//                }
//            }
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        Container l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, FlowLayout.encloseCenterMiddle(field));
////        Container fieldValueEdit = FlowLayout.encloseCenter(fieldCont, new Label(Icons.iconEditSymbolLabelStyle, "LabelValue"));
////        Container fieldValueEdit = FlowLayout.encloseCenter(fieldCont, editFieldIcon);
////</editor-fold>
//        Component fieldLabel = makeHelpButton(fieldLabelTxt, help);
//
//        Container l = BorderLayout.center(fieldLabel).add(BorderLayout.EAST, fieldContainer);
//
//        if (checkForTooLargeWidth) {
////            l.revalidate();
////            if (fieldLabel.getWidth() < fieldLabel.getPreferredW() || fieldValueEdit.getWidth() < fieldValueEdit.getPreferredW()) { //if either of the fields got less width than needed
//            int availDisplWidth = Display.getInstance().getDisplayWidth() * 9 / 10; //asumme roughly 90% of width is available after margins
//            int fieldLabelPreferredW = fieldLabel.getPreferredW();
//            int fieldValueEditPreferredW = fieldContainer.getPreferredW();
//            if (fieldLabelPreferredW + fieldValueEditPreferredW > availDisplWidth) { //if too wide
//                fieldLabel.getAllStyles().setMarginBottom(0);
//                fieldLabel.getAllStyles().setPaddingBottom(0); //reduce space between label and field
//                l.removeComponent(fieldLabel); //
////                l = BorderLayout.north(fieldLabel).add(BorderLayout.EAST, fieldValueEdit);
//                l.add(BorderLayout.NORTH, fieldLabel);
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
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
//                        l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, fieldContainer);
//                    } else {
//                        l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.SOUTH, fieldContainer);
//                    }
//                }
//            }
////</editor-fold>
//        }
//
////        return BorderLayout.center(helpBut(label, help)).add(BorderLayout.EAST, field);
//        return l;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Component layoutOLD(String fieldLabelTxt, Component field, String help, boolean checkForTooLargeWidth) {
////        return BorderLayout.center(addHelp(new Button(label), help)).add(BorderLayout.EAST, field);
////        Container l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, FlowLayout.encloseCenterMiddle(field)); //CenterMiddle seems to shift down a bit
//        Container l = BorderLayout.center(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, FlowLayout.encloseCenter(field));
////        field.setUIID("LabelValue");
//        if (checkForTooLargeWidth) {
//            if (l.getPreferredW() > Display.getInstance().getDisplayWidth()) { //if too wide, put label in North (so it becomes two separate lines)
//                if (field instanceof TextArea || field instanceof TextField) {
////                    field.setSameWidth(d);
//                    Dimension d = field.getPreferredSize();
//                    d.setWidth(Display.getInstance().getDisplayWidth() / 2); //allow a textField to take up mox half the screen size.
//                    field.setPreferredSize(d); //TODO!!! CN1 Support: setPreferredSize deprecated, but what is right way then??
//                } else {
////                    l.removeAll(); //remove already added components to avoid getting an error about adding twice
//                    field.getParent().removeComponent(field); //remove already added components to avoid getting an error about adding twice
//                    l = BorderLayout.north(makeHelpButton(fieldLabelTxt, help)).add(BorderLayout.EAST, field);
//                }
//            }
//        }
//
////        return BorderLayout.center(helpBut(label, help)).add(BorderLayout.EAST, field);
//        return l;
//    }
//</editor-fold>
//    private String previousValuesFilename;
//    protected void setPreviousValuesFilename(String filename) {
//        previousValuesFilename = filename;
//    }
    protected SaveEditedValuesLocally previousValues;
//    Map<Object, Object> previousValues;

//    protected void initLocalSaveOfEditedValues(String filename) {
////        previousValuesFilename = filename;
//        if (Config.TEST) ASSERT.that(filename != null && filename.length() > 0, "empty filename");
//        previousValues = new SaveEditedValuesLocally(this, filename,true);
////<editor-fold defaultstate="collapsed" desc="comment">
////        previousValues = new HashMap<Object, Object>() {
////            void saveFile() {
//////            Storage.getInstance().writeObject("ScreenItem-" + item.getObjectIdP(), this); //save
////                Storage.getInstance().writeObject(previousValuesFilename, this); //save
////            }
////
////            public Object put(Object key, Object value) {
////                Object previousValue = super.put(key, value);
////                saveFile();
////                return previousValue;
////            }
////
////            public Object remove(Object key) {
////                Object previousValue = super.remove(key);
////                saveFile();
////                return previousValue;
////            }
////
////        };
////        if (Storage.getInstance().exists(previousValuesFilename)) {
////            previousValues.putAll((Map) Storage.getInstance().readObject(previousValuesFilename));
////        }
////</editor-fold>
//    }
//    public void deleteLocallyEditedValues() {
////            Storage.getInstance().deleteStorageFile("ScreenItem-" + item.getObjectIdP());
//        if (previousValuesFilename != null && !previousValuesFilename.isEmpty()) {
//            Storage.getInstance().deleteStorageFile(previousValuesFilename);
//        }
//    }
    interface GetVal {

        Object getVal();
    }

    interface PutVal {

        void setVal(Object val);
    }

    interface GetBool {

        boolean getVal();
    }

    interface SetBool {

        void setVal(boolean value);
    }

    /* the one used by most field in ScreenItem: */
    void initField(String identifier, Object field, GetVal getVal, PutVal putVal, GetVal getField, PutVal putField) {
//        initField(identifier, field, getVal, putVal, getField, putField, null, null, null, null);
//        initField(identifier, field, getVal, putVal, getField, putField, null, null, previousValues, parseIdMap2);
        initField(identifier, field, getVal, putVal, getField, putField, null, null, null, previousValues, parseIdMap2);
    }

    void initField(String identifier, Object field, GetVal getVal, PutVal putVal, GetVal getField, PutVal putField, GetBool isInherited) {
//        initField(identifier, field, getVal, putVal, getField, putField, null, null, null, null);
//        initField(identifier, field, getVal, putVal, getField, putField, null, null, previousValues, parseIdMap2);
        initField(identifier, field, getVal, putVal, getField, putField, null, null, isInherited, previousValues, parseIdMap2);
    }

    void initField(String identifier, Object field, GetVal getVal, PutVal putVal, GetVal getField, PutVal putField, Object undefinedValue, GetVal defaultValue) {
//        initField(identifier, field, getVal, putVal, getField, putField, null, null, null, null);
//        initField(identifier, field, getVal, putVal, getField, putField, null, null, previousValues, parseIdMap2);
        initField(identifier, field, getVal, putVal, getField, putField, undefinedValue, defaultValue, null, previousValues, parseIdMap2);
    }

    void initField(String identifier, Object field, GetVal getVal, PutVal putVal, GetVal getField, PutVal putField, GetBool isInherited, Object undefinedValue, GetVal defaultValue) {
//        initField(identifier, field, getVal, putVal, getField, putField, null, null, null, null);
//        initField(identifier, field, getVal, putVal, getField, putField, null, null, previousValues, parseIdMap2);
        initField(identifier, field, getVal, putVal, getField, putField, undefinedValue, defaultValue, isInherited, previousValues, parseIdMap2);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void initField(String identifier, Object field, GetVal getVal, PutVal putVal, GetVal getField, PutVal putField) {
////        initField(identifier, field, getVal, putVal, getField, putField, null, null, null, null);
////        initField(identifier, field, getVal, putVal, getField, putField, null, null, previousValues, parseIdMap2);
//        initField(identifier, field, getVal, putVal, getField, putField, null, previousValues, parseIdMap2);
//    }
//    private void initField(String fieldLabel, String fieldHelp, Object field, String fieldIdentifier, GetVal getVal, PutVal putVal, GetVal getField, PutVal putField, GetBool isInherited) {
//         initField(fieldLabel, fieldHelp, field, fieldIdentifier, getVal, putVal, getField, putField, isInherited, null);
//    }
//    private void initField(String fieldLabel, String fieldHelp, Object field, String fieldIdentifier, GetVal getVal, PutVal putVal, GetVal getField, PutVal putField,
//            GetBool isInherited, ActionListener actionListener) {
//    void initField(String fieldIdentifier, Object field, GetVal getOrg, PutVal putOrg, GetVal getField, PutVal putField,
//            GetBool isInherited, ActionListener actionListener) {
//        initField(fieldIdentifier, field, getOrg, putOrg, getField, putField, isInherited, actionListener, previousValues, parseIdMap2);
//    }
//</editor-fold>
    static void initField(String fieldIdentifier, Object field, GetVal getOrg, PutVal putOrg, GetVal getField, PutVal putField,
            SaveEditedValuesLocally previousValues, ParseIdMap2 parseIdMap2) {
//        initField(fieldIdentifier, field, getOrg, putOrg, getField, putField, null, null, previousValues, parseIdMap2);
        initField(fieldIdentifier, field, getOrg, putOrg, getField, putField, null, null, null, previousValues, parseIdMap2);
    }

    private static String INHERITED = "Inherited";
    private static int INHERITED_LEN = INHERITED.length();

    public static void updateUIIDForInherited(Component field, boolean isInherited) {
//        if (isInherited.getVal()) {
        String fieldUIID = field.getUIID();
        if (isInherited) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (field.getUIID().equals("LabelValue"))
//                field.setUIID("LabelValueInherited");
//        } else if (field.getUIID().equals("LabelValueInherited"))
//            field.setUIID("LabelValue");
//</editor-fold>
            if (!fieldUIID.contains(INHERITED)) {
                field.setUIID(fieldUIID + INHERITED);
            }
        } else if (fieldUIID.contains(INHERITED)) {
            StringBuilder oldUIID = new StringBuilder(fieldUIID);
            int start = fieldUIID.lastIndexOf(INHERITED);
            int end = start + INHERITED_LEN; //INHERITED.length();
            oldUIID.delete(start, end);
            field.setUIID(oldUIID.toString());
        }
    }

    /**
     *
     * @param fieldIdentifier
     * @param field
     * @param getOrg
     * @param putOrg
     * @param getField
     * @param putField
     * @param isInherited
     * @param actionListener
     * @param previousValues
     * @param parseIdMap2
     */
//    static void initField(String fieldIdentifier, Object field, GetVal getOrg, PutVal putOrg, GetVal getField, PutVal putField,
//            GetBool isInherited, ActionListener actionListener, SaveEditedValuesLocally previousValues, Map<Object, UpdateField> parseIdMap2) {
//    static void initField(String fieldIdentifier, Object field, GetVal getOrg, PutVal putOrg, GetVal getField, PutVal putField,
//            GetBool isInherited, SaveEditedValuesLocally previousValues, Map<Object, UpdateField> parseIdMap2) {
//        initField(fieldIdentifier, field, getOrg, putOrg, getField, putField, null, null, isInherited, previousValues, parseIdMap2);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    static void initField(String fieldIdentifier, Object field, GetVal getOrg, PutVal putOrg, GetVal getField, PutVal putField,
//            GetBool isInherited, SaveEditedValuesLocally previousValues, Map<Object, UpdateField> parseIdMap2) {
//        initField(fieldIdentifier, field, getOrg, putOrg, getField, putField, null, null, isInherited, previousValues, parseIdMap2);
//    }
//</editor-fold>
    /**
     * getOrg.getVal and putOrg.putVal will fetch and update actual data from
     * the edited element. putField.setVal and getField.getVal will set the UI
     * input field (Picker) with those values. So before editing:
     * putField.setVal(getOrg.getVal()), and after editing:
     * putOrg.setVal(getField.getVal()).
     *
     * initField also adds automatic saving of edited values to each field via
     * an actionListener:
     *
     * On each edit action, the listener will compare using equal() the
     * inputFields value (getField.getVal()) with the original, unedited, value
     * from getOrg.getVal() and either store a new edited value locally, or if
     * the edited value is now equal to the original value, remove the locally
     * stored value. Since getField.getVal() is locally stored ('serialized' by
     * CN1's writeObject) it must either return a native type like (String, int,
     * Date, List, ...) or a class implementing CN1's Externalizable interface.
     *
     * Finally, initField also adds the logic to initialize the input fields
     * with a locally stored value, so instead of initializing with the value
     * from getOrg.getVal() it uses previousValues.get(fieldIdentifier) as in
     * putField.setVal(previousValues.get(fieldIdentifier)).
     *
     * It is also possible to supply a default value to use for undefined data.
     * This will check if getOrg.getVal() returns 'undefinedValue' and if so,
     * initialize the input field with the value returned by
     * getDefaultValue.getVal().
     *
     *
     * Most input fields directly accept the original value's type (Date, int,
     * ...) but when that is not the case (Enums, lists, ParseObject), a 'pivot'
     * format should be used in the form of a String which. Keep in mind of e.g.
     * the value for Enums can be null (not defined value).
     *
     * So, the simplest pattern for unsupported types is: getOrg and putOrg
     * returns/takes a String and converts it to the right format, while
     * getField and putField takes the same string and uses it to update the
     * field (e.g.
     *
     * @param fieldIdentifier
     * @param field
     * @param getOrg
     * @param putOrg
     * @param getField
     * @param putField
     * @param undefinedValue
     * @param getDefaultValue
     * @param isInherited
     * @param previousValues
     * @param parseIdMap2
     */
    static void initFieldOLD(String fieldIdentifier, Object field, GetVal getOrg, PutVal putOrg, GetVal getField, PutVal putField, Object undefinedValue,
            GetVal getDefaultValue, GetBool isInherited, SaveEditedValuesLocally previousValues, ParseIdMap2 parseIdMap2) {
//<editor-fold defaultstate="collapsed" desc="comment">
//         initField(fieldLabel, fieldHelp, field, fieldIdentifier, getVal, putVal, getField, putField, isInherited, null, null);
//    }
//
//    private void initField(String fieldLabel, String fieldHelp, Object field, String fieldIdentifier, GetVal getVal, PutVal putVal, GetVal getField, PutVal putField,
//            GetBool isInherited, ActionListener actionListener, Container componentCont) {
//                if (previousValues.get(Item.PARSE_EFFORT_ESTIMATE) != null) {
//</editor-fold>
        //check if a previous or default value exists, and if yes, use that to initialize the editable field with (//UI: don't autosave default value, it may also (need to) be updated if replayed later)
//            if (putField != null && getOrg != null) { //if putField==null => not an editable field
        if (putField != null) { //if putField==null => not an editable field
            if (previousValues != null) {
                if (previousValues.get(fieldIdentifier) != null) {
                    if (!Objects.equals(previousValues.get(fieldIdentifier), getOrg.getVal())) { //            effortEstimate.setDurationInMillis((long) previousValues.get(Item.PARSE_EFFORT_ESTIMATE)); //use a previously edited value
                        putField.setVal(previousValues.get(fieldIdentifier)); //use a previously edited value
                    } else {
                        putField.setVal(getOrg.getVal()); //use a previously edited value
                        previousValues.remove(fieldIdentifier); //if previous value is (now) the same as current, remove the previous (may for example happen after editing subtasks)
                    }
//            } else if (isInherited != null && isInherited.getVal()) {
                    //handle inheritance when appropriate
                } else { //no previous value
                    if (undefinedValue != null && MyUtil.eql(getOrg.getVal(), undefinedValue)) { //if org value==undefinedValue, then set field with default value
                        putField.setVal(getDefaultValue.getVal()); //set editable field (will be stored in previousValues *if* it is modified later on
                    } else {
//                effortEstimate.setDurationInMillis(item.getEffortEstimate());
                        putField.setVal(getOrg.getVal()); //set editable field (will be stored in previousValues *if* it is modified later on
                    }
                }
            }
        }

        //set actionListener on edited field, to store edited values (only if different from the original one)
        if (getField != null && getOrg != null) {
//            MyActionListener storeEditedValueLocallyAL = (e) -> { //must be a MyActionListener to get triggered if programmatically setting the value
            ActionListener storeEditedValueLocallyAL = (e) -> { //must be a MyActionListener to get triggered if programmatically setting the value
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (actionListener != null) {
//                    actionListener.actionPerformed(e);
//                }
//            if (effortEstimate.getDuration() != item.getEffortEstimate()) {
//                if (false) { //OK now that values can be null
//                    ASSERT.that(getField.getVal() != null, "getField.getVal()==null, for field=" + fieldIdentifier);
//                    ASSERT.that(getOrg.getVal() != null, "getOrg.getVal()==null, for field=" + fieldIdentifier);
//                }
//</editor-fold>
                if (previousValues != null) {
//                    if (!getField.getVal().equals(getOrg.getVal())) {
                    //(a == null) ? (a == b) : a.equals(b) <=> (a == null) ? b == null : a.equals(b)  -->> https://stackoverflow.com/questions/1402030/compare-two-objects-with-a-check-for-null
//                    if (getField.getVal() == null ? getOrg.getVal() == null : getField.getVal().equals(getOrg.getVal())) { //values are the same
                    if (MyUtil.eql(getField.getVal(), getOrg.getVal())) { //values are the same
                        previousValues.remove(fieldIdentifier); //remove any old value if edited back to same value as Item has already
                    } else { //value's been edited
                        previousValues.put(fieldIdentifier, getField.getVal());
                    }
//                    if (isInherited.getVal()) {
//                        if (((Component) field).getUIID().equals("LabelValue"))
//                            ((Component) field).setUIID("LabelValueInherited");
//                    } else if (((Component) field).getUIID().equals("LabelValueInherited"))
//                        ((Component) field).setUIID("LabelValue");
                }
                if (isInherited != null) {
                    updateUIIDForInherited((Component) field, isInherited.getVal());
                }
            };

            //add change listenerlisten to changes an update+save if edited to different value than item.orgValue
            if (field instanceof Picker) {
                ((Picker) field).addActionListener(storeEditedValueLocallyAL);
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (field instanceof MyDurationPicker) {
//                ((MyDurationPicker) field).addActionListener(al);
//            ((MyDurationPicker) field).addActionListener((e) -> {
////            if (effortEstimate.getDuration() != item.getEffortEstimate()) {
//                if (!getField.getVal().equals(getVal.getVal())) {
////                previousValues.put(Item.PARSE_EFFORT_ESTIMATE, effortEstimate.getDuration());
//                    previousValues.put(fieldIdentifier, getField.getVal());
//                } else {
//                    previousValues.remove(fieldIdentifier); //remove any old value if edited back to same value as Item has already
//                }
//            });
//            } else if (field instanceof MyTextArea) {
//</editor-fold>
            } else if (field instanceof TextArea) {
                if (false) {
                    ((TextArea) field).addActionListener(storeEditedValueLocallyAL); //doesn't seem to trigger if entering text and then clicking directly in another field
                }
                ((TextArea) field).addDataChangedListener((type, index) -> storeEditedValueLocallyAL.actionPerformed(null));
                if (false) {
                    ((TextArea) field).setDoneListener(storeEditedValueLocallyAL);
                }
            } else if (field instanceof Button) {
                ((Button) field).addActionListener(storeEditedValueLocallyAL);
            } else if (field instanceof SpanButton) {
                ((SpanButton) field).addActionListener(storeEditedValueLocallyAL);
            } else if (field instanceof Switch) {
                ((Switch) field).addActionListener(storeEditedValueLocallyAL);
            } else if (field instanceof MyComponentGroup) {
                ((MyComponentGroup) field).addActionListener(storeEditedValueLocallyAL);
//<editor-fold defaultstate="collapsed" desc="comment">
//            ((MyTextArea) field).addActionListener((e) -> {
////            if (effortEstimate.getDuration() != item.getEffortEstimate()) {
//                if (!getField.getVal().equals(getVal.getVal())) {
////                previousValues.put(Item.PARSE_EFFORT_ESTIMATE, effortEstimate.getDuration());
//                    previousValues.put(fieldIdentifier, getField.getVal());
//                } else {
//                    previousValues.remove(fieldIdentifier); //remove any old value if edited back to same value as Item has already
//                }
//            });
//</editor-fold>
            } else {
                assert false;
            }

        }

        //set edited value on exit 
        if (putOrg != null && getField != null && parseIdMap2 != null) {
            //        parseIdMap2.put(Item.PARSE_EFFORT_ESTIMATE,()->{
            parseIdMap2.put(fieldIdentifier, () -> {
//        if (effortEstimate.getDuration() != item.getEffortEstimate()) {
                ASSERT.that(true || getField.getVal() != null, "saving: getField.getVal()==null, for field=" + fieldIdentifier);
//<editor-fold defaultstate="collapsed" desc="comment">
//                ASSERT.that(getOrg.getVal() != null, "saving: getOrg.getVal()==null, for field=" + fieldIdentifier);
//                if (getField.getVal() != null && !getField.getVal().equals(getOrg.getVal())) {
//https://stackoverflow.com/questions/11271554/compare-two-objects-in-java-with-possible-null-values/11271611: (str1 == null ? str2 == null : str1.equals(str2)) <=>
//!(str1 == null ? str2 == null : str1.equals(str2)) <=> (
//                if (!(getField.getVal() == null
//                        ? getOrg.getVal() == null
//                        : getField.getVal().equals(getOrg.getVal()))) {
////            item.setEstimate((long) effortEstimate.getDuration()); //if value has been changed, update item
//                    putOrg.setVal(getField.getVal()); //if value has been changed, update item
//                }
//                if (!(getField.getVal() == null && (getOrg.getVal() == null || getField.getVal().equals(getOrg.getVal())))) {
//</editor-fold>
                if (MyUtil.neql(getField.getVal(), getOrg.getVal())) {
//            item.setEstimate((long) effortEstimate.getDuration()); //if value has been changed, update item
                    putOrg.setVal(getField.getVal()); //if value has been changed, update item
                }
            });
        }
//else assert false:"should never happen";
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (componentCont == null) { //if a container is already defined use that, e.g. for comment field
//            return componentCont;
//        }
//        return layoutN(fieldLabel, (Component) field, fieldHelp);
//</editor-fold>
    }

    static void initField(String fieldIdentifier, Object field, GetVal getOrg, PutVal putOrg, GetVal getField, PutVal putField, Object undefinedValue,
            GetVal getDefaultValue, GetBool isInherited, SaveEditedValuesLocally previousValues, ParseIdMap2 parseIdMap2) {
        //initialize 
        if (getOrg.getVal() != null) {
            putField.setVal(getOrg.getVal());
            if (isInherited != null) {
                updateUIIDForInherited((Component) field, isInherited.getVal());
            }
        } else if (undefinedValue != null && MyUtil.eql(getOrg.getVal(), undefinedValue)) { //if org value==undefinedValue, then set field with default value
            putField.setVal(getDefaultValue.getVal()); //set editable field (will be stored in previousValues *if* it is modified later on
            if (isInherited != null) {
                updateUIIDForInherited((Component) field, isInherited.getVal());
            }
        }
        //set actionListener on edited field, to store edited values (only if different from the original one)
        if (getField != null) {
            ActionListener updateElement = (e) -> { //must be a MyActionListener to get triggered if programmatically setting the value
                putOrg.setVal(getField.getVal()); //update original value
                //save updated element locally
                previousValues.saveElementToSaveLocally();
                if (isInherited != null) {
                    updateUIIDForInherited((Component) field, isInherited.getVal());
                }
            };

            //add change listenerlisten to changes an update+save if edited to different value than item.orgValue
            if (field instanceof TextArea) {
                ((TextArea) field).addDataChangedListener((type, index) -> updateElement.actionPerformed(null));
            } else if (field instanceof Button) {
                ((Button) field).addActionListener(updateElement);
            } else if (field instanceof SpanButton) {
                ((SpanButton) field).addActionListener(updateElement);
            } else if (field instanceof Switch) {
                ((Switch) field).addActionListener(updateElement);
            } else if (field instanceof MyComponentGroup) {
                ((MyComponentGroup) field).addActionListener(updateElement);
            } else {
                assert false;
            }
        }
    }

    protected void animateMyForm() {
//        ASSERT.that(false, "not implemented!!!");
//        getContentPane().animateLayoutAndWait(300); //need AndWait to ensure that form is animited into place before setting InlineAddTask text field in focus??! 
//        getContentPane().animateLayout(150); //need AndWait to ensure that form is animited into place before setting InlineAddTask text field in focus??! 
        getContentContainer().animateLayout(ANIMATION_TIME_DEFAULT); //need AndWait to ensure that form is animited into place before setting InlineAddTask text field in focus??! 
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Component layout(String label, Component setting, String help) {
//        return BorderLayout.center(new SpanLabel(label)).add(BorderLayout.EAST, setting).add(BorderLayout.SOUTH, new SpanLabel(help));
//    }
//</editor-fold>

    static Component makeContainerWithClearButton(Component comp, Action clearFct) {
        Button clearButton = new Button();
        clearButton.setCommand(Command.createMaterial(null, Icons.iconCloseCircle, (e) -> {
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

    void setSelectionMode(boolean activateSelectionMode, Collection referenceSet) {
        if (activateSelectionMode) {
            if (oldSelectedObjects != null) {
                oldSelectedObjects.setReferenceSetAndRefreshSelection(referenceSet);
                selectedObjects = oldSelectedObjects;
            } else if (previousValues != null) {
                if (previousValues.get(ListSelector.CLASS_NAME) != null) {
                    selectedObjects = new ListSelector(
                            DAO.getInstance().fetchListOfItemsFromListOfObjectIds((List<String>) previousValues.get(ListSelector.CLASS_NAME)),
                            true, Integer.MAX_VALUE, true,
                            (o, b) -> previousValues.put(ListSelector.CLASS_NAME, selectedObjects.getSelectedObjIds()), true, referenceSet); //put: save selected values locally
//                    selectedObjects = (ListSelector) previousValues.get(ListSelector.CLASS_NAME); //reuse locally saved selected values if any
//                    List<String> objIds = (List<String>) previousValues.get(ListSelector.CLASS_NAME); //reuse locally saved selected values if any
//                    for (String objId : objIds) {
//                        selectedObjects.select(DAO.getInstance().fetchItem(objId));
//                    }
                } else {
                    assert (selectedObjects == null);
                    selectedObjects = new ListSelector(null, true, Integer.MAX_VALUE, true,
                            (o, b) -> previousValues.put(ListSelector.CLASS_NAME, selectedObjects.getSelectedObjIds()), true, referenceSet); //put: save selected values locally
                }
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
        if (Config.DEBUG_LOGGING) {
            Log.p("Show MyForm: " + getTitle());
        }
        if (!ReplayLog.getInstance().replayCmd(new ActionEvent(this))) { //only show screen is there was no command to replay
//            Form prevForm = Display.getInstance().getCurrent(); //!!doesn't get previous form because it was not actually shown
//            MyAnalyticsService.visit(getTitle(), prevForm != null ? prevForm.getTitle() : "noPrevForm");
//            MyAnalyticsService.visit(getUniqueFormId(), prevForm != null ? ((MyForm) prevForm).getUniqueFormId() : "noPrevForm");
            if (MyAnalyticsService.isEnabled()) {
//                MyAnalyticsService.visit(getUniqueFormId(), prevForm instanceof MyForm ? ((MyForm) prevForm).getUniqueFormId() : "noPrevForm");
                MyAnalyticsService.visit(getUniqueFormId(), parentForm instanceof MyForm ? ((MyForm) parentForm).getUniqueFormId() : "noPrevForm");
            }
            //restore scroll position on replay
            if (previousValues != null) {
                previousValues.scrollToSavedYOnFirstShow(findScrollableContYChild());
            }
            super.show();
            if (false && ReplayLog.getInstance().justFinishedReplaying()) { //show bigTimer if was active
                //if bigTimer was active, and we're not replaying, then show big timer again (from whatever screen was the last in replay)
                TimerInstance timerInstance = TimerStack.getInstance().getCurrentTimerInstanceN();
                if (!(this instanceof ScreenTimer6) && timerInstance != null && timerInstance.isFullScreen()) { //                    TimerStack.getInstance().refreshOrShowTimerUI(MyForm.this);
                    //                        && !ReplayLog.getInstance().isReplayInProgress() //THE test above on justFinished should be enough
                    new ScreenTimer6(MyForm.this, timerInstance).show();
                }
//            else
//                super.show();
            }
        }
    }

//    public void showBack(boolean popCommand) {
//        if (popCommand) {
//            ReplayLog.getInstance().popCmd(); //pop any previous command
//        }
//        super.showBack();
//    }
//
    @Override
    public void showBack() {
        ReplayLog.getInstance().popCmd(); //pop any previous command
        super.showBack();
//        showBack(true);
    }
    /**
     * save any ongoing edits locally when app is paused (in case it is
     * destroyed later on). Does nothing in screens with no new edits. Saved
     * items must be read back in constructor of the screen.
     */
//    public void saveEditedValuesLocallyOnAppExitXXX() {
//
//    }
//
//    public boolean restoreEditedValuesSavedLocallyOnAppExitXXX() {
//        return false;
//    }
//
//    public void deleteEditedValuesSavedLocallyOnAppExitXXX() {
//        if (previousValues != null) {
//            previousValues.deleteFile();
//        }
//    }
    private Container pinchContainer; //Container holding the pinchComponent (and implementing the resize)
    private Container oldPinchContainer; //keeping the old pinchContainer to decrease while pinching out the new (and to swap back to old if new is too small to keep)
    private Container previousPinchContainer; //Container holding the pinchComponent (and implementing the resize)
//    private Component pinchComponent;
//    private Component prevComponentAbove;
//    private Component prevComponentBelow;
//    private Item pinchItem;
    private boolean pinchInsertEnabled; // = false; //is pinch insert enabled for this screen? TODO!! only true for testing
    private boolean pinchInsertInitiated; // = false; //tracks whenever a pinch was initiated (to ensure we only finish when it makes sense)
    private int pinchInitialYDistance = Integer.MIN_VALUE;
    private int pinchDistance = Integer.MAX_VALUE;
//    private boolean pinchOut;

    public boolean isPinchInsertEnabled() {
//        return pinchInsertEnabled;
        return pinchInsertEnabled && MyPrefs.pinchInsertEnabled.getBoolean();
    }

    public void setPinchInsertEnabled(boolean pinchInsertEnabled) {
        this.pinchInsertEnabled = pinchInsertEnabled;
    }

    public boolean isPinchOngoing() {
//        return pinchInsertEnabled;
        return pinchInsertInitiated;
    }

    private static boolean minimumPinchSizeReached(int pinchYDistance, Component pinchContainer) {
        int minH = pinchContainer.getPreferredH() / 3 * 2;
        Log.p("minimumPinch: pinchYDistance=" + pinchYDistance + ", pinchContainer.getPreferredH()=" + pinchContainer.getPreferredH() + ", minH=" + minH + ", minPinch=" + (pinchYDistance > minH));
        return pinchYDistance > minH; //true if over 2/3 of the required size has been pinched out
    }

    /**
     * either list or category
     *
     * @param refElement
     * @param ownerList
     * @param typeElement just used to indicate the type of element to create
     * since we don't have an actual object to work from
     * @param category
     * @param insertBeforeRefElement
     * @return
     */
//    private InsertNewElementFunc createInsertContainer(ItemAndListCommonInterface typeElement, ItemAndListCommonInterface refElement,
    private PinchInsertContainer createInsertContainer(ItemAndListCommonInterface typeElement, ItemAndListCommonInterface refElement,
            ItemAndListCommonInterface ownerList, Category category, boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        return createInsertContainer(refElement, list, insertBeforeRefElement, null);
//    }
//
//    private Container createInsertContainer(ItemAndListCommonInterface refElement, ItemAndListCommonInterface list, boolean insertBeforeRefElement, Action closeAction) {
//        ASSERT.that(!insertBeforeRefElement, "not implemented yet");
//        InsertNewElementFunc insertContainer = null;
        PinchInsertContainer insertContainer = null;
//</editor-fold>
        if (refElement instanceof Item || typeElement instanceof Item) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (ownerList instanceof Category) {
////            Item newItem = new Item();
////            newItem.addCategoryToItem((Category)list, false); //add category in InlineInsertNewItemContainer2
//                return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, null, (Category) ownerList, insertBeforeRefElement)); //don't insert into any list, just add to Category
//            } else if (ownerList instanceof ItemList) {
//                if (((ItemList) ownerList).isNoSave()) {
//                    return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, insertBeforeRefElement));
//                } else {
//                    return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, insertBeforeRefElement)); //null=> don't insert into any list, only 'inbox'
//                }
//            } else if (ownerList instanceof Item) { //NB! inserting refElement into a Project (as a subtask)!
//                return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, insertBeforeRefElement));
//            } else {
//                ASSERT.that(false, () -> "error1 in createInsertContainer: refElt=" + (refElement == null ? "<null>" : refElement) + "; list=" + (ownerList == null ? "<null>" : ownerList) + "; insertBefore=" + (insertBeforeRefElement));
//            }
//            return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, ownerList, category, insertBeforeRefElement));
//</editor-fold>
//            insertContainer = new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, ownerList, category, insertBeforeRefElement);
            insertContainer = new PinchInsertItemContainer(MyForm.this, (Item) refElement, ownerList, category, insertBeforeRefElement);

        } else if (refElement instanceof Category || typeElement instanceof Category) {
//            return wrapInPinchableContainer(new InlineInsertNewCategoryContainer(MyForm.this, (Category) refElement, insertBeforeRefElement));
            insertContainer = new PinchInsertCategoryContainer(MyForm.this, (Category) refElement, (CategoryList) ownerList, insertBeforeRefElement);
        } else if (refElement instanceof ItemList || typeElement instanceof ItemList) {
//            return wrapInPinchableContainer(new InlineInsertNewItemListContainer(MyForm.this, (ItemList) refElement, insertBeforeRefElement));
            insertContainer = new PinchInsertItemListContainer(MyForm.this, (ItemList) refElement, (ItemListList) ownerList, insertBeforeRefElement);
        } else if (refElement instanceof WorkSlot || typeElement instanceof WorkSlot) {
//            WorkSlotList workSlotList = ((WorkSlot) refElement).getWorkSlotListN();
//            insertContainer = new InlineInsertNewWorkSlotContainer(MyForm.this, (WorkSlot) refElement, workSlotList, insertBeforeRefElement); //TODO!!!!! implement pinch insert of new WorkSlots, require adapting InlineContainer!
            insertContainer = new PinchInsertWorkSlotContainer(MyForm.this, (WorkSlot) refElement, ownerList, insertBeforeRefElement); //TODO!!!!! implement pinch insert of new WorkSlots, require adapting InlineContainer!
        } else {
            ASSERT.that(false, () -> "error2 in createInsertContainer: refElt=" + refElement + "; list=" + ownerList + "; insertBefore=" + insertBeforeRefElement);
        }
//        return null;
        return insertContainer;
    }

//    private InsertNewElementFunc createInsertContainer(ItemAndListCommonInterface refElement, ItemAndListCommonInterface ownerList,
    private PinchInsertContainer createInsertContainer(ItemAndListCommonInterface refElement, ItemAndListCommonInterface ownerList,
            Category category, boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
        return createInsertContainer(null, refElement, ownerList, category, insertBeforeRefElement);
    }

    /**
     * returns true if x is an insertNewContainer or is inside one
     *
     * @param x the component to start the search for insertNewContainer from
     * @return
     */
//    private boolean isInsertNewContainer(Component comp) {
//        return (comp instanceof PinchInsertItemContainer
//                || comp instanceof PinchInsertCategoryContainer
//                || comp instanceof PinchInsertItemListContainer
//                || comp instanceof PinchInsertWorkSlotContainer);
//    }
//    private boolean isOrPartOfInsertNewContainer(Component x) {
//        if (isInsertNewContainer(x)) {
//            return true;
//        }
//        Container parent = x.getParent();
//        while (parent != null) {
//            if (isInsertNewContainer(x)) {
//                return true;
//            }
//            parent = parent.getParent();
//        }
//        return false;
//    }
    protected void createAndAddPinchInsertContainer(MyDragAndDropSwipeableContainer refComponentN, ItemAndListCommonInterface itemElt,
            boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
        if (false && (itemElt == null || refComponentN == null)) { //both can be null when inserting first element into a list
//            return null;
            pinchContainer = null;
            return;
        }
//        MyDragAndDropSwipeableContainer refComponent = null;xx;
//        Category category = refComponentN.getDragAndDropCategory();
//        InsertNewElementFunc insertContainer = createInsertContainer(itemElt, itemElt.getOwner(), category, insertBeforeRefElement, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//        if (pinchContainer == null) { //NO, done in InlineInsertCont when closing the previous container (ensure latest scroll pos is used)// store scrollPos on first insert
//            setKeepPos();
//        }
//        InsertNewElementFunc insertContainer = createInsertContainer(itemElt, itemElt.getOwner(), refComponentN.getDragAndDropCategory(), insertBeforeRefElement);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
        PinchInsertContainer pinchInsertContainer = createInsertContainer(itemElt, itemElt.getOwner(), refComponentN.getDragAndDropCategory(), insertBeforeRefElement);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
        Container wrappedInsertContainer = wrapInPinchableContainer(pinchInsertContainer);
        MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(refComponentN, wrappedInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
//        setPinchInsertContainer(insertContainer, false); //NOW done in pinchInsertFinished // call this *after* inserting the new container to ensure that text field starts in editing mode
        setPinchInsertContainer(pinchInsertContainer); //NOW done in pinchInsertFinished // call this *after* inserting the new container to ensure that text field starts in editing mode
//        return wrappedInsertContainer;
        pinchContainer = wrappedInsertContainer;
    }

    private void createAndAddPinchInsertIntoEmptyContainer(Container insertIntoContainer, ItemAndListCommonInterface typeElement,
            ItemAndListCommonInterface owner, Category category) {
//        InsertNewElementFunc insertContainer = createInsertContainer(typeElement, null, owner, category, false);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
        PinchInsertContainer insertContainer = createInsertContainer(typeElement, null, owner, category, false);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
        Container wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
//        MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(refComponentN, wrappedInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
        insertIntoContainer.removeAll(); //remove any previous content (eg label with "Insert new task with +...")
        insertIntoContainer.addComponent(wrappedInsertContainer);
//        setPinchInsertContainer(insertContainer, true); //call this *after* inserting the new container to ensure that text field starts in editing mode
        setPinchInsertContainer(insertContainer); //call this *after* inserting the new container to ensure that text field starts in editing mode
        pinchContainer = wrappedInsertContainer;
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected void createAndAddInsertContainer(MyDragAndDropSwipeableContainer above, MyDragAndDropSwipeableContainer below) {
//        if (above == null)
//            createAndAddInsertContainer(below, below.getDragAndDropObject(), true);
//        else
//            createAndAddInsertContainer(above, above.getDragAndDropObject(), false);
//    }
//</editor-fold>

//    MyDragAndDropSwipeableContainer findMyDDCont(ItemAndListCommonInterface refElement){
    private MyDragAndDropSwipeableContainer findMyDDContWithObjIdN(Component comp, String refObjId) {
//        Container cont = getContentPane();
//        if (comp instanceof MyDragAndDropSwipeableContainer && ((MyDragAndDropSwipeableContainer) comp).getDragAndDropObject().getObjectIdP().equals(refObjId))
        if (comp instanceof MyDragAndDropSwipeableContainer && refObjId.equals(((MyDragAndDropSwipeableContainer) comp).getDragAndDropObject().getObjectIdP())) {
            return (MyDragAndDropSwipeableContainer) comp;
        } else if (comp instanceof Container) {
            Component c = null;
            Container cont = (Container) comp;
            for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
                c = findMyDDContWithObjIdN(cont.getComponentAt(i), refObjId);
                if (c instanceof MyDragAndDropSwipeableContainer) {
                    return (MyDragAndDropSwipeableContainer) c;
                }
            }
        }
        return null;
    }

    private MyDragAndDropSwipeableContainer findMyDDContWithObjIdN(List<Component> compList, String refObjId) {
//        Container cont = getContentPane();
//        if (comp instanceof MyDragAndDropSwipeableContainer && ((MyDragAndDropSwipeableContainer) comp).getDragAndDropObject().getObjectIdP().equals(refObjId))
        for (Component comp : compList) {
            if (comp instanceof MyDragAndDropSwipeableContainer && refObjId.equals(((MyDragAndDropSwipeableContainer) comp).getDragAndDropObject().getObjectIdP())) {
                return (MyDragAndDropSwipeableContainer) comp;
            } else if (comp instanceof Container) {
                Component c = null;
                Container cont = (Container) comp;
                for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
                    c = findMyDDContWithObjIdN(cont.getComponentAt(i), refObjId);
                    if (c instanceof MyDragAndDropSwipeableContainer) {
                        return (MyDragAndDropSwipeableContainer) c;
                    }
                }
            }
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected void createAndAddInsertContainerXXX(String refEltObjId, String eltParseClass, boolean insertBeforeRefElement) {
//        ItemAndListCommonInterface refElement = null;
//        switch (eltParseClass) {
//            case Item.CLASS_NAME:
////                Item aboveItem = DAO.getInstance().fetchItem(refEltObjId);
//                refElement = DAO.getInstance().fetchItem(refEltObjId);
//                break;
//            case ItemList.CLASS_NAME:
////                ItemList aboveItemList = DAO.getInstance().fetchItemList(refEltObjId);
//                refElement = DAO.getInstance().fetchItemList(refEltObjId);
//                break;
//            case Category.CLASS_NAME:
////                ItemList aboveCategory = DAO.getInstance().fetchCategory(refEltObjId);
//                refElement = DAO.getInstance().fetchCategory(refEltObjId);
//                break;
//            case WorkSlot.CLASS_NAME:
////                ItemList aboveWorkSlot = DAO.getInstance().fetchCategory(refEltObjId);
//                refElement = DAO.getInstance().fetchCategory(refEltObjId);
//                break;
//            default:
//                if (Config.TEST) ASSERT.that(false, "Error in createAndAddInsertContainer: wrong element ParseClass=" + eltParseClass);
//        }
////        MyDragAndDropSwipeableContainer myDDContN = findMyDDContWithObjIdN(getContentPane(), refEltObjId);
//        MyDragAndDropSwipeableContainer myDDContN = findMyDDContWithObjIdN(getContentPane().getChildrenAsList(true), refEltObjId);
//        if (Config.TEST) ASSERT.that(myDDContN != null, "no MyDragAndDropSwipeableContainer found for refEltObjId=" + refEltObjId + ", eltParseClass=" + eltParseClass + ", insertAfter=" + insertBeforeRefElement);
////        createAndAddInsertContainer(myDDContN, refElement, myDDContN.getDragAndDropCategory(), insertBeforeRefElement); //NB: createAndAddInsertContainer checks for null values
//        createAndAddInsertContainer(myDDContN, refElement, insertBeforeRefElement); //NB: createAndAddInsertContainer checks for null values
//    }
//</editor-fold>
    protected static final String SAVE_LOCALLY_REF_ELT_OBJID_KEY = "InlineInsertElementOBJID";
    protected static final String SAVE_LOCALLY_REF_ELT_PARSE_CLASS = "InlineInsertEltParseCLASS";
    protected static final String SAVE_LOCALLY_INSERT_BEFORE_REF_ELT = "InlineInsertBEFORERefElt";
    protected static final String SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK = "InlineInsertSavedSUBTASK"; //used to save inline text from within the InlineInsert container
    protected static final String SAVE_LOCALLY_INLINE_INSERT_LEVEL = "InlineInsertSavedLEVEL"; //used to save inline text from within the InlineInsert container

    protected static final String SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE = "InlineInsertEditTaskACTIVE";
    protected static final String SAVE_LOCALLY_INLINE_INSERT_TEXT = "InlineInsertSavedTEXT"; //used to save inline text from within the InlineInsert container

    private void createAndInsertInlineInsertContainerEmptyScreen() {
        if (this instanceof ScreenListOfItems) {
            ItemAndListCommonInterface ownerList = ((ScreenListOfItems) this).itemListOrItemOrg;
            if (ownerList instanceof Category) {
                createAndAddPinchInsertIntoEmptyContainer(getContentContainer(), new Item(false), null, (Category) ownerList); //NB: createAndAddInsertContainer checks for null values
            } else if (ownerList instanceof ItemList || ownerList instanceof Item) {
                createAndAddPinchInsertIntoEmptyContainer(getContentContainer(), new Item(false), ownerList, null); //NB: createAndAddInsertContainer checks for null values
            }
        } else if (this instanceof ScreenListOfItemLists) {
            createAndAddPinchInsertIntoEmptyContainer(getContentContainer(), new ItemList(), ItemListList.getInstance(), null); //NB: createAndAddInsertContainer checks for null values
        } else if (this instanceof ScreenListOfCategories) {
            createAndAddPinchInsertIntoEmptyContainer(getContentContainer(), new Category(), CategoryList.getInstance(), null); //NB: createAndAddInsertContainer checks for null values
        } else if (this instanceof ScreenListOfWorkSlots) {
            createAndAddPinchInsertIntoEmptyContainer(getContentContainer(), new WorkSlot(), ((ScreenListOfWorkSlots) this).workSlotListOwner, null); //NB: createAndAddInsertContainer checks for null values
        } else if (Config.TEST) {
            ASSERT.that("pinchinsert into empty screen not handled for this screen=" + this);
        }
    }

    /**
     * if inline insert was active in previous session
     * (SAVE_LOCALLY_REF_ELT_OBJID_KEY points to, then
     *
     * @return
     */
    protected void recreateInlineInsertContainerAndReplayCmdIfNeeded() {
//        if (previousValues != null && (previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY) != null)) {
//            createAndAddInsertContainer((String) previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY), 
//                    (String) previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS), 
//                    (Boolean) previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT));
//            makeInlineInsertReplayCmd().actionPerformed(null);
//            makeInlineInsertReplayCmd();
        if (previousValues != null) {
            //if inlineInsert was left active when app was last active, then re-insert the container again
            if (previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY) != null) { //check if there were an earlier inline container
                String refEltObjId = (String) previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY);
                String refClass = (String) previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS);
//                boolean insertBefore = (Boolean) previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //!=null;
                boolean insertBefore = previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT) != null; //!=null;
//                createAndAddInsertContainer(refEltObjId, refClass, insertBefore);

                ItemAndListCommonInterface refElement;// = null;
                if (refClass != null) {
                    switch (refClass) {
                        case Item.CLASS_NAME:
//                Item aboveItem = DAO.getInstance().fetchItem(refEltObjId);
                            refElement = DAO.getInstance().fetchItem(refEltObjId);
                            break;
                        case ItemList.CLASS_NAME:
                            refElement = DAO.getInstance().fetchItemList(refEltObjId);
                            break;
                        case Category.CLASS_NAME:
                            refElement = DAO.getInstance().fetchCategory(refEltObjId);
                            break;
                        case WorkSlot.CLASS_NAME:
                            refElement = DAO.getInstance().fetchCategory(refEltObjId);
                            break;
                        default:
                            if (Config.TEST) {
                                ASSERT.that(false, "Error in createAndAddInsertContainer: wrong element ParseClass=" + refClass);
                            }
                            refElement = null;  //needed to only initialize once to use in lambda expression below
                    }
                } else {
                    refElement = null; //needed to only initialize once to use in lambda expression below
                }

                MyDragAndDropSwipeableContainer myDDContN = findMyDDContWithObjIdN(getContentPane().getChildrenAsList(true), refEltObjId);
                if (Config.TEST) {
                    ASSERT.that(myDDContN != null, "no MyDragAndDropSwipeableContainer found for refEltObjId=" + refEltObjId + ", eltParseClass=" + refClass + ", insertAfter=" + insertBefore);
                }
                if (myDDContN != null && refElement != null) {
                    createAndAddPinchInsertContainer(myDDContN, refElement, insertBefore); //NB: createAndAddInsertContainer checks for null values

                    //if full screen edit was launched from inline container, then do so here:
                    if (false && previousValues.get(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null) {
//                        if (false) {
//                            InsertNewElementFunc inlineCont = getPinchInsertContainer();
//                            inlineCont.getEditTaskCmd().actionPerformed(null);
//                        }
                        if (false && myDDContN != null && refElement != null) { //don't need this, the ReplayCmd is created in InlineInsert 
//                        makeAndAddCreatePinchContCommand(myDDContN, refElement, insertBefore); //inserts the command into Screen's cmds for Replay
                            MyReplayCommand.create(PinchInsertItemContainer.REPLAY_CMD_ID, null, (e) -> {
//                                createAndAddInsertContainer(myDDContN, refElement, insertBefore); //NB: createAndAddInsertContainer checks for null values
                                if (getPinchInsertContainer() != null) {
                                    getPinchInsertContainer().getEditTaskCmd().actionPerformed(null); //run the cmd to edit task in screen
                                }
                            });
                        }
                    }
                } else if (false) {
                    createAndInsertInlineInsertContainerEmptyScreen();
                }

            }
        }
//        }
    }

////<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * recreate the ReplayCommand for Replay if
     */
//    protected void recreateInlineInsertCommandOLD() {
//        if (previousValues != null && previousValues.get(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null) {
//            //if inlineInsert was left active when app was last active, then re-insert the container again
//            if (previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY) != null) { //check if there were an earlier inline container
//                String refEltObjId = (String) previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY);
//                String refClass = (String) previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS);
//                boolean insertBefore = previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT) != null; //!=null;
//
//                ItemAndListCommonInterface refElement = null;
//                if (refClass != null) {
//                    switch (refClass) {
//                        case Item.CLASS_NAME:
//                            refElement = DAO.getInstance().fetchItem(refEltObjId);
//                            break;
//                        case ItemList.CLASS_NAME:
//                            refElement = DAO.getInstance().fetchItemList(refEltObjId);
//                            break;
//                        case Category.CLASS_NAME:
//                            refElement = DAO.getInstance().fetchCategory(refEltObjId);
//                            break;
//                        case WorkSlot.CLASS_NAME:
//                            refElement = DAO.getInstance().fetchCategory(refEltObjId);
//                            break;
//                        default:
//                            if (Config.TEST) {
//                                ASSERT.that(false, "Error in createAndAddInsertContainer: wrong element ParseClass=" + refClass);
//                            }
//                    }
//                }
//
//                MyDragAndDropSwipeableContainer myDDContN = findMyDDContWithObjIdN(getContentPane().getChildrenAsList(true), refEltObjId);
//
//                if (refElement != null && myDDContN != null) {
//                    makeAndAddCreatePinchContCommand(myDDContN, refElement, insertBefore); //inserts the command into Screen's cmds for Replay
//                } else if (Config.TEST) {
//                    ASSERT.that(myDDContN != null, "no MyDragAndDropSwipeableContainer found for refEltObjId=" + refEltObjId + ", eltParseClass=" + refClass + ", insertAfter=" + insertBefore);
//                }
//////        createAndAddInsertContainer(myDDContN, refElement, myDDContN.getDragAndDropCategory(), insertBeforeRefElement); //NB: createAndAddInsertContainer checks for null values
////                if (myDDContN != null && refElement != null) {
////                    createAndAddInsertContainer(myDDContN, refElement, insertBefore); //NB: createAndAddInsertContainer checks for null values
////                } else {
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                    if (this instanceof ScreenListOfItems) {
//////                        ItemAndListCommonInterface ownerList = ((ScreenListOfItems) this).itemListOrg;
//////                        if (ownerList instanceof Category) {
//////                            createAndAddInsertContainer(getContentPane(), new Item(), null, (Category) ownerList); //NB: createAndAddInsertContainer checks for null values
//////                        } else if (ownerList instanceof ItemList || ownerList instanceof Item) {
//////                            createAndAddInsertContainer(getContentPane(), new Item(), ownerList, null); //NB: createAndAddInsertContainer checks for null values
//////                        }
//////                    } else if (this instanceof ScreenListOfItemLists) {
//////                        createAndAddInsertContainer(getContentPane(), new ItemList(), ItemListList.getInstance(), null); //NB: createAndAddInsertContainer checks for null values
//////                    } else if (this instanceof ScreenListOfCategories) {
//////                        createAndAddInsertContainer(getContentPane(), new Category(), CategoryList.getInstance(), null); //NB: createAndAddInsertContainer checks for null values
//////                    } else if (this instanceof ScreenListOfWorkSlots) {
//////                        createAndAddInsertContainer(getContentPane(), new WorkSlot(), ((ScreenListOfWorkSlots) this).workSlotListOwner, null); //NB: createAndAddInsertContainer checks for null values
//////                    } else if (Config.TEST) {
//////                        ASSERT.that("pinchinsert into empty screen not handled for this screen=" + this);
//////                    }
//////</editor-fold>
////                    if (false) {
////                        createAndInsertInlineInsertContainerEmptyScreen();
////                    }
////                }
////
////                //if full screen edit was launched from inline container, then do so here:
////                if (previousValues.get(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null) {
////                    InsertNewElementFunc inlineCont = getInlineInsertContainer();
////                    inlineCont.getEditTaskCmd().actionPerformed(null);
////                }
//            }
//        }
////        }
//    }
//    protected void recreateInlineInsertCommandXXX() {
//        if (previousValues != null && previousValues.get(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null) {
//            MyReplayCommand cmd = MyReplayCommand.create("InlineInsert", "Add template", null, (e) -> {
//
//                //if inlineInsert was left active when app was last active, then re-insert the container again
//                if (previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY) != null) { //check if there were an earlier inline container
//                    String refEltObjId = (String) previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY);
//                    String refClass = (String) previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS);
//                    boolean insertBefore = previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT) != null; //!=null;
//
//                    ItemAndListCommonInterface refElement = null;
//                    if (refClass != null) {
//                        switch (refClass) {
//                            case Item.CLASS_NAME:
//                                refElement = DAO.getInstance().fetchItem(refEltObjId);
//                                break;
//                            case ItemList.CLASS_NAME:
//                                refElement = DAO.getInstance().fetchItemList(refEltObjId);
//                                break;
//                            case Category.CLASS_NAME:
//                                refElement = DAO.getInstance().fetchCategory(refEltObjId);
//                                break;
//                            case WorkSlot.CLASS_NAME:
//                                refElement = DAO.getInstance().fetchCategory(refEltObjId);
//                                break;
//                            default:
//                                if (Config.TEST) {
//                                    ASSERT.that(false, "Error in createAndAddInsertContainer: wrong element ParseClass=" + refClass);
//                                }
//                        }
//                    }
//
//                    MyDragAndDropSwipeableContainer myDDContN = findMyDDContWithObjIdN(getContentPane().getChildrenAsList(true), refEltObjId);
//
//                    if (refElement != null && myDDContN != null) {
//                        makeAndAddCreatePinchContCommand(myDDContN, refElement, insertBefore); //inserts the command into Screen's cmds for Replay
//                    } else if (Config.TEST) {
//                        ASSERT.that(myDDContN != null, "no MyDragAndDropSwipeableContainer found for refEltObjId=" + refEltObjId + ", eltParseClass=" + refClass + ", insertAfter=" + insertBefore);
//                    }
//                    createAndAddPinchInsertContainer(myDDContN, refElement, insertBefore); //NB: createAndAddInsertContainer checks for null values
//                }
//            });
//        }
//    }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    create (and insert into list) the replay command to re-insert the inlineinsert container. It will then automatically be launched by the Replay
//    @return
//     */
//    private MyReplayCommand makeInlineInsertReplayCmdXXX() {
//        return MyReplayCommand.create("InlineInsertCmd", 'x', (e) -> {
//            if (previousValues != null) {
//                //if inlineInsert was left active when app was last active, then re-insert the container again
//                if (previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY) != null) {
//                    createAndAddInsertContainer((String) previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY),
//                            (String) previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS),
//                            (Boolean) previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT));
//                    //if full screen edit was launched from inline container, then do so here:
//                    if (previousValues.get(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null && previousValues.get(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE).equals(true)) {
//
//                        if (previousValues.get(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null) {
//                            InsertNewElementFunc inlineCont = getInlineInsertContainer();
//                            inlineCont.getEditTaskCmd().actionPerformed(null);
//                        }
//                    }
//                }
//            }
//        });
//    }
//</editor-fold>

    /* adjust x and y to adjust for imprecise finger placement. Notably, the top (index) finger more easily moves up faster than the lower thumb. */
    private void adjustXAndY(int[] x, int[] y) {

    }

    /**
     * create the command that will create the inline insert pinch container and
     * insert it correctly relative to refComp and refElt. the ReplayCommand can
     * either be called when pinching out to create the container, OR on replay
     * to recreate the container (it will automatically be inserted into the
     * MyForm's list of replayable commands).
     *
     * @param refComp
     * @param refElt
     * @param insertBeforeRefElement
     */
    protected MyReplayCommand makeAndAddCreatePinchContCommand(MyDragAndDropSwipeableContainer refComp, ItemAndListCommonInterface refElt, boolean insertBeforeRefElement) {
        MyReplayCommand cmd = MyReplayCommand.create("InlineInsert", "Add template", null, (e) -> {

            createAndAddPinchInsertContainer(refComp, refElt, insertBeforeRefElement); //NB: createAndAddInsertContainer checks for null values
        });
        return cmd;
    }

    /**
     * override to indicate whether pinchinsert is possible for the given refElt
     * and insertBefore value
     *
     * @param refElt
     * @param insertBeforeRefElt
     * @return
     */
    public boolean isPinchInsertEnabled(ItemAndListCommonInterface refElt, boolean insertBeforeRefElt) {
        return false;
    }

    /**
     * insert container and animate?? three cases: 1) simple: pinching out two
     * siblings => insert between. 2) Pinching out between a parent (Item
     * project/ItemList/Category) and its expanded subtask => insert new subtask
     * before the pinched one 3) Pinching out between a subtask and a following
     * task at a higher level => insert new subtask after the subtask. 4)
     * Pinching out between a task/subtask and a non-task
     * (list/category/nothing/null) => insert new subtask after pinched one. 5)
     * Pinching out between a TOTO: update doc wrt notebook page 151
     *
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
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container createAndInsertPinchContainer(int[] x, int[] y) {
//        return createAndInsertPinchContainer(x, y, null);
//    }
//    private Container createAndInsertPinchContainer(int[] x, int[] y, Action closeAction) {
//    private Container createAndInsertPinchContainer(int[] x, int[] y) {
//</editor-fold>
    private void createAndInsertPinchContainer(int[] x, int[] y) {
        ASSERT.that(y[0] <= y[1]); //should 
        pinchContainer = null; //reset previous container
        adjustXAndY(x, y);
//        Component compAbove = y[0] < y[1] ? getComponentAt(x[0], y[0]) : getComponentAt(x[1], y[1]);
        Component compAbove = getComponentAt(x[0], y[0]);
//        Component compAbove = y[0] < y[1] ? getClosestComponentTo(x[0], y[0]) : getClosestComponentTo(x[1], y[1]);
//        Component compBelow = y[0] < y[1] ? getComponentAt(x[1], y[1]) : getComponentAt(x[0], y[0]); //UI: if both fingers on same container, we still create a new one below (ie lower on the sc reen than the lowest placed finger)
        Component compBelow = getComponentAt(x[1], y[1]); //UI: if both fingers on same container, we still create a new one below (ie lower on the sc reen than the lowest placed finger)
//<editor-fold defaultstate="collapsed" desc="comment">
//        Component compBelow = y[0] < y[1] ? getClosestComponentTo(x[1], y[1]) : getClosestComponentTo(x[0], y[0]); //UI: if both fingers on same container, we still create a new one below (ie lower on the sc reen than the lowest placed finger)
//        if (isOrPartOfInsertNewContainer(compAbove) || isOrPartOfInsertNewContainer(compBelow)) return null; //UI: cannot pinchinsert next to an existing insertContainer
//        Container parentContainerAbove = null;
//        if (compAbove != null) {
//            parentContainerAbove = compAbove.getParent();
//        }
//        Container parentContainerBelow = null;
//        if (compAbove != null) {
//            parentContainerAbove = compBelow.getParent();
//        }
//        Container parentContainerBelow = compBelow.getParent();
//</editor-fold>
        //find the drop containers
        MyDragAndDropSwipeableContainer dropComponentAbove = MyDragAndDropSwipeableContainer.findMyDDContStartingFrom(compAbove);

        MyDragAndDropSwipeableContainer dropComponentBelow = null;
        if (dropComponentAbove != null) { //UI: be default we ignore the lowest placed finger and always create insertCont below the highest placed finger
//            if (isOrPartOfInsertNewContainer(compAbove) || isOrPartOfInsertNewContainer(compBelow)) return null; //UI: cannot pinchinsert next to an existing insertContainer
            dropComponentBelow = MyDragAndDropSwipeableContainer.findNextDDCont(dropComponentAbove);
        } else if (compBelow != null) { //if dropComponentAbove==null, then use the container , can happen eg if inserting above top-most item in list (above finger is on toolbar)
            dropComponentBelow = MyDragAndDropSwipeableContainer.findMyDDContStartingFrom(compBelow);
            if (false && dropComponentBelow != null) { //false: why set dropComponentAbove if it was originally null (indicating that top finger was on toolbar)?!
                dropComponentAbove = MyDragAndDropSwipeableContainer.findPrecedingMyDDCont(dropComponentBelow, null);
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//            MyDragAndDropSwipeableContainer test = MyDragAndDropSwipeableContainer.findNextDDCont(dropComponentAbove);
//        MyDragAndDropSwipeableContainer dropComponentBelow = findDropContainerStartingFrom(compBelow);
//        if (false && dropComponentAbove == dropComponentBelow) { //if both fingers on same element, do nothing //NOW: always use Next container, even if both fingers on the same
//            return null;
//        }
//        //find the drop containers parents - to insert pinch container?!
//        Container parentContainerAbove = null;
//        if (dropComponentAbove != null) {
//            parentContainerAbove = dropComponentAbove.getParent();
//        }
//        Container parentContainerBelow = null;
//        if (dropComponentBelow != null) {
//            parentContainerAbove = dropComponentBelow.getParent();
//        }
//</editor-fold>
        ItemAndListCommonInterface itemEltAbove = dropComponentAbove != null ? (ItemAndListCommonInterface) dropComponentAbove.getDragAndDropObject() : null; //(ItemAndListCommonInterface) dropComponentAbove.getDragAndDropObject();
        ItemAndListCommonInterface itemEltBelow = dropComponentBelow != null ? (ItemAndListCommonInterface) dropComponentBelow.getDragAndDropObject() : null;// = (ItemAndListCommonInterface) dropComponentBelow.getDragAndDropObject();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (dropComponentAbove != null) {
//            itemEltAbove = (ItemAndListCommonInterface) dropComponentAbove.getDragAndDropObject();
//        }
//        List objAboveOwnerList = null;
//        if (dropComponentBelow != null) {
//            itemEltBelow = (ItemAndListCommonInterface) dropComponentBelow.getDragAndDropObject();
//        }
//</editor-fold>
        //check if we're trying to insert at the position of the existing pinchContainer, if so, return null/do nothing //TOTO!!!!! not working
        if (oldPinchContainer != null) {
            int idxOldDrop = MyDragAndDropSwipeableContainer.getPositionInParentContainerScrollY(oldPinchContainer);
            ASSERT.that(idxOldDrop != -1);
            if (dropComponentAbove != null) {
                int idxAbove = MyDragAndDropSwipeableContainer.getPositionInParentContainerScrollY(dropComponentAbove);
                ASSERT.that(idxAbove != -1);
                if (idxOldDrop == idxAbove + 1) { //                    return null;
                    return;
                }
            } else if (dropComponentBelow != null) {
                int idxBelow = MyDragAndDropSwipeableContainer.getPositionInParentContainerScrollY(dropComponentBelow);
                ASSERT.that(idxBelow != -1);
                if (idxOldDrop == idxBelow - 1) { //                    return null;
                    return;
                }
            }
        }

        if (Config.TEST_PINCH && itemEltAbove != null) {
            Log.p("PinchAbove=" + itemEltAbove);//.getText());
        }
        if (Config.TEST_PINCH && itemEltBelow != null) {
            Log.p("PinchBelow=" + itemEltBelow);//.getText());
        }//<editor-fold defaultstate="collapsed" desc="comment">
//        List objBelowOwnerList = null;
//        InsertNewElementFunc insertContainer = null;

//        //get any category
//        Category category = null;
//        if (dropComponentAbove != null)
//            category = dropComponentAbove.getDragAndDropCategory();
//        if (category == null && dropComponentBelow != null)
//            category = dropComponentBelow.getDragAndDropCategory();
//        ItemAndListCommonInterface ownerList = null;
//        if (category == null)
//            if (itemEltAbove != null)
//                ownerList = itemEltAbove.getOwner();
//            else if (itemEltBelow != null)
//                ownerList = itemEltBelow.getOwner();
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
//        Container wrappedInsertContainer = null;
//<editor-fold defaultstate="collapsed" desc="1st try for Replay support">
//        if (false) {
//            String inlineElementKey = "InlineInsertElement";
////        String inlineElementOwnerKey = "InlineInsertElementOwner";
//            String inlineElementBelowKey = "InlineInsertElementBelow";
//            String inlineCategoryKey = "InlineInsertCategory";
//            String inlineBeforeKey = "InlineInsertBefore";
//            previousValues.put(inlineElementKey, EAST);
//            previousValues.put(inlineElementBelowKey, EAST);
//            previousValues.put(inlineBeforeKey, EAST);
//            previousValues.put(inlineCategoryKey, EAST);
//
////        ItemAndListCommonInterface refElement = (ItemAndListCommonInterface)previousValues.get(inlineElementKey);
////        ItemAndListCommonInterface inlineElementOwner = (ItemAndListCommonInterface)previousValues.get(inlineElementKey);
////        boolean inlineElementBelow = (Boolean)previousValues.get(inlineElementBelowKey);
////        Category inlineCategory = (Category)previousValues.get(inlineCategoryKey);
//            previousValues.put("", EAST);
//            previousValues.put(inlineElementBelowKey, EAST);
//            previousValues.put(inlineCategoryKey, EAST);
//
//            if (previousValues.get("InlineInsertCmd") != null) {
//
//                MyReplayCommand inlineInsert = MyReplayCommand.create("", "", null, (e) -> {
//
//                    if (previousValues.get("InlineInsertCmd").equals("InsertContainer")) {
//
//                    } else {
//                        if (Config.TEST) ASSERT.that(previousValues.get("InlineInsertCmd").equals("EditItem"));
//
//                    }
//
//                    previousValues.remove(inlineElementKey); //removing the refElement is a marker to indicate
////            Container wrappedInsertContainer2;
//                    ItemAndListCommonInterface refElement = (ItemAndListCommonInterface) previousValues.get(inlineElementKey);
//                    ItemAndListCommonInterface inlineElementOwner = (ItemAndListCommonInterface) previousValues.get(inlineElementKey);
//                    boolean inlineElementBelow = (Boolean) previousValues.get(inlineElementBelowKey);
//                    Category inlineCategory = (Category) previousValues.get(inlineCategoryKey);
//                    InsertNewElementFunc insertContainer2 = createInsertContainer(refElement, inlineElementOwner, inlineCategory, inlineElementBelow); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//////            insertContainer = createInsertContainer(itemEltBelow, ownerList, category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                    Container wrappedInsertContainer2 = wrapInPinchableContainer(insertContainer2);
//                    Component dropComponentBelow2 = null; //TODO!!!!! smartest to let MyTree find the component for an element?!
//                    MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentBelow2, wrappedInsertContainer2, 0); //insert insertContainer at position of dropComponentBelow
////            setInlineInsertContainer(insertContainer); //call this *after* inserting the new container to ensure that text field starts in editing mode
//
//                });
//            }
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="old code for pinch insert">
//        if (false) {
//            if (dropComponentAbove == null && dropComponentBelow != null) { //pull down on top-most item, insert before the first element (can be Item/Category/ItemList)
//                insertContainer = createInsertContainer(itemEltBelow, itemEltBelow.getOwner(), category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
////            insertContainer = createInsertContainer(itemEltBelow, ownerList, category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
//                MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentBelow, wrappedInsertContainer, 0); //insert insertContainer at position of dropComponentBelow
//                setInlineInsertContainer(insertContainer); //call this *after* inserting the new container to ensure that text field starts in editing mode
//            } else if (dropComponentBelow == null && dropComponentAbove != null) { //pull down on bottom-most item, insert at the end of the list (can be Item/Category/ItemList)
//                insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner(), category, false); //create insertContainer
//                wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////            insertContainer = createInsertContainer(itemEltAbove, ownerList, category, false); //create insertContainer
//                MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentAbove, wrappedInsertContainer, 1); //insert insertContainer *after* dropComponentAbove
//                setInlineInsertContainer(insertContainer);
//
//            } else if (itemEltAbove instanceof Item) { //inserting *after* an Item
//                if (itemEltBelow instanceof Category || itemEltBelow instanceof ItemList) {
//                    //insert after itemEltAbove
//                    insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner(), category, false); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                    wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////                insertContainer = createInsertContainer(itemEltAbove, ownerList, category, false); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                    MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentAbove, wrappedInsertContainer, 1); //insert insertContainer at start of subtask lise (before itemEltBelow)
//                    setInlineInsertContainer(insertContainer);
//
//                } else if (itemEltBelow instanceof Item) {
//                    //belong to same owner, insert after
//                    if (itemEltAbove.getOwner() == itemEltBelow.getOwner()) {
//                        insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner(), category, false); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                        wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////                    insertContainer = createInsertContainer(itemEltAbove, ownerList, category, false); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                        MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentAbove, wrappedInsertContainer, 1); //insert insertContainer at beginning of list that the other pinch finger touches
//                        setInlineInsertContainer(insertContainer);
//                    } else if (((Item) itemEltAbove).hasAsSubtask((Item) itemEltBelow)) { //
//                        insertContainer = createInsertContainer(itemEltBelow, itemEltBelow.getOwner(), category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                        wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////                    insertContainer = createInsertContainer(itemEltBelow, ownerList, category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                        MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentBelow, wrappedInsertContainer, 0); //insert insertContainer at start of subtask lise (before itemEltBelow)
//                        setInlineInsertContainer(insertContainer);
//                    } else { //simply insert before elementAbove (e.g. eltAbove=subask of previous item A, eltBelow a sibling to A
//                        insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner(), category, false); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                        wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////                    insertContainer = createInsertContainer(itemEltAbove, ownerList, category, false); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                        MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentAbove, wrappedInsertContainer, 1); //insert insertContainer at start of subtask lise (before itemEltBelow)
//                        setInlineInsertContainer(insertContainer);
//                    }
//                }
//
//            } else if (itemEltAbove instanceof Category || itemEltAbove instanceof ItemList) { //inserting *after* a Category or ItemList
//                if (itemEltBelow instanceof Item) { //insert before itemEltBelow
//                    insertContainer = createInsertContainer(itemEltBelow, itemEltBelow.getOwner(), category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                    wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////                insertContainer = createInsertContainer(itemEltBelow, ownerList, category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                    MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentBelow, wrappedInsertContainer, 0); //insert insertContainer at start of subtask lise (before itemEltBelow)
//                    setInlineInsertContainer(insertContainer);
//                } else {
//                    ASSERT.that(itemEltBelow instanceof Category || itemEltBelow instanceof ItemList, "if itemEltBelow is not an Item, it can only a Cateogyr or ItemList");
//                    insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner(), category, false); //create insertContainer
//                    wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////                insertContainer = createInsertContainer(itemEltAbove, ownerList, category, false); //create insertContainer
//                    MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentAbove, wrappedInsertContainer, 1); //insert insertContainer *after* dropComponentAbove
//                    setInlineInsertContainer(insertContainer);
//                }
//            } else if (itemEltAbove instanceof WorkSlot || itemEltBelow instanceof WorkSlot) {
//                if (itemEltAbove instanceof WorkSlot) { //insert before itemEltBelow
//                    insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner(), category, false); //create insertContainer
//                    wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////                insertContainer = createInsertContainer(itemEltAbove, ownerList, category, false); //create insertContainer
//                    MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentAbove, wrappedInsertContainer, 1); //insert insertContainer *after* dropComponentAbove
//                    setInlineInsertContainer(insertContainer);
//                } else {
////                ASSERT.that(itemEltBelow instanceof Category || itemEltBelow instanceof ItemList, "if itemEltBelow is not an Item, it can only a Cateogyr or ItemList");
//                    insertContainer = createInsertContainer(itemEltBelow, itemEltBelow.getOwner(), category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                    wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////                insertContainer = createInsertContainer(itemEltBelow, ownerList, category, true); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//                    MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(dropComponentBelow, wrappedInsertContainer, 0); //insert insertContainer at start of subtask lise (before itemEltBelow)
//                    setInlineInsertContainer(insertContainer);
//                }
//            }
//        }
//</editor-fold>
//        createAndAddInsertContainer(itemEltBelow, pinchInsertEnabled);
        MyDragAndDropSwipeableContainer refComp;
        ItemAndListCommonInterface refElt;
//        Category category = null;
        boolean insertBeforeRefElement = false;
        if (((itemEltAbove instanceof Category || itemEltAbove instanceof ItemList))
                && (itemEltBelow instanceof Category || itemEltBelow instanceof ItemList || itemEltBelow == null)) {
            //new Category/ItemList between two Categories or ItemLists or at the end of the list of Categories/ItemLists
            refElt = itemEltAbove;
            refComp = dropComponentAbove; //Category can be obtained via this
        } else if (((itemEltBelow instanceof Category || itemEltBelow instanceof ItemList))
                && (itemEltAbove == null)) {
            //insert at the *beginning* of the list of Categories or ItemLists (above the topmost element)
            refElt = itemEltBelow;
            insertBeforeRefElement = true;
            refComp = dropComponentBelow;
        } else if ((itemEltAbove instanceof Category || itemEltAbove instanceof ItemList) && itemEltBelow instanceof Item) {
            //inserting btw a Category and an expanded subtask => insert the subtask before the expanded item
            refElt = itemEltBelow;
            insertBeforeRefElement = true;
            refComp = dropComponentBelow; //Category can be obtained via this
//            if (itemEltAbove instanceof Category) category = (Category) itemEltAbove;
        } else if (itemEltAbove instanceof Item && itemEltBelow == null) {
            //inserting after the last element in the list
            refElt = itemEltAbove;
            refComp = dropComponentAbove;
        } else if (itemEltAbove == null && itemEltBelow instanceof Item) {
            //inserting before the first item in the list
            refElt = itemEltBelow;
            insertBeforeRefElement = true;
            refComp = dropComponentBelow;
        } else if (itemEltAbove instanceof Item && itemEltBelow instanceof Item) {
            //insert between two Items (can be in sequence, a project + first expanded subtask or a subtask and a higher-level item (a sibling to the subtask's parent task)
            Item itemAbove = (Item) itemEltAbove;
            Item itemBelow = (Item) itemEltBelow;
            if (itemAbove.isSubtaskTo(itemBelow)) {
                //pinch insert between a project and its first subtask
                refElt = itemBelow;
                insertBeforeRefElement = true;
                refComp = dropComponentBelow;
            } else if (itemAbove.getOwner() == itemBelow.getOwner()) {
                //pinch insert between two sibling tasks (insert after the first)
                refElt = itemAbove;
                refComp = dropComponentAbove;
            } else {
                //item below is a higher-level item (a sibling to the subtask's parent task) so we insert below the itemAbove
//                ASSERT.that();
                refElt = itemAbove;
                refComp = dropComponentAbove;
            }
//        } else if (itemEltAbove == getToolbar() && itemEltBelow == null) { //inserting first element
        } else if (itemEltAbove == null && itemEltBelow == null) { //inserting first element
            refElt = null;
            refComp = null;
        } else {
            ASSERT.that(false, "unknown error when inserting InsertContainer, above=" + itemEltAbove + ", below=" + itemEltBelow);
            refElt = null;
            refComp = null;
        }
//        refComp = dropComponentAbove != null ? dropComponentAbove : (dropComponentBelow != null ? dropComponentBelow : null);
//        if (true || refComp != null) { //may become null in some edge cases like an empty list with only the initial inline insert container
//            refElt = refComp.getDragAndDropObject();
        if (previousValues != null) {
            if (refElt != null) { //can be null if inserting into empty list
                previousValues.put(SAVE_LOCALLY_REF_ELT_OBJID_KEY, refElt.getObjectIdP());
                previousValues.put(SAVE_LOCALLY_REF_ELT_PARSE_CLASS, ((ParseObject) refElt).getClassName());
//            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            boolean insertBeforeRefElement = (refComp == dropComponentBelow);
//            insertBeforeRefElement = dropComponentAbove == null && dropComponentBelow != null;
//            if (dropComponentAbove != null && dropComponentBelow != null
//                    && itemEltBelow instanceof Item && itemEltAbove.isSubtaskTo((Item) itemEltBelow)) //pinch insert between a project and its first subtask
//                previousValues.put(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK, true);
//</editor-fold>
                if (insertBeforeRefElement) {
                    previousValues.put(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, true);
                }
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//                previousValues.remove(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE);
//        }
//        wrappedInsertContainer = createAndAddInsertContainer(refComp, refElt, insertBeforeRefElement);
//        makeInlineInsertReplayCmd().actionPerformed(null);
//        createAndAddInsertContainer(refComp, refElt, insertBeforeRefElement); //will setInlineInsertContainer() as a side effect
//</editor-fold>
        if (false) {
            if (refComp != null && refElt != null) {
//            createAndAddInsertContainer(refComp, refElt, insertBeforeRefElement); //NB: createAndAddInsertContainer checks for null values
                MyReplayCommand insertCmd = makeAndAddCreatePinchContCommand(refComp, refElt, insertBeforeRefElement);
                insertCmd.actionPerformed(null);
            } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (this instanceof ScreenListOfItems) {
//                ItemAndListCommonInterface ownerList = ((ScreenListOfItems) this).itemListOrg;
//                if (ownerList instanceof Category) {
//                    createAndAddInsertContainer(getContentContainer(), null, (Category) ownerList); //NB: createAndAddInsertContainer checks for null values
//                } else if (ownerList instanceof ItemList || ownerList instanceof Item) {
//                    createAndAddInsertContainer(getContentContainer(), ownerList, null); //NB: createAndAddInsertContainer checks for null values
//                }
//            } else if (Config.TEST) {
//                ASSERT.that("pinchinsert into empty screen not handled for this screen=" + this);
//            }
//</editor-fold>
                if (false) { //don't create for now since may happen if pinch didn't capture any of the actual 
                    createAndInsertInlineInsertContainerEmptyScreen();
                }
            }
        }
        if (isPinchInsertEnabled(refElt, insertBeforeRefElement)) {
            createAndAddPinchInsertContainer(refComp, refElt, insertBeforeRefElement); //NB: createAndAddInsertContainer checks for null values
        }
//        if (getInlineInsertContainer().getTextArea() != null) { //Moved to pinchFinished to avoid opening keyboard while pinching
//            getInlineInsertContainer().getTextArea().startEditingAsync();
//        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        }
//        } else if (parentContainerAbove == parentContainerBelow) { //we're inserting in the same list, insert just below the containerAbove
//            ASSERT.that(itemEltAbove.getClass() == itemEltBelow.getClass()); //should always be of same class if in same list (TODO!!!! what about Today view?!
//            //Covers both Items, ItemList, Category, createInsertContainer will create the right container
//            insertContainer = createInsertContainer(itemEltAbove, null); //create insertContainer
//            int insertIndex = parentContainerAbove.getComponentIndex(compAbove) + 1;
//            parentContainerAbove.addComponent(insertIndex, insertContainer); //insert insertContainer at end of list that the other pinch finger touches
//        } else if (itemEltBelow instanceof Item && (itemEltAbove instanceof Category || itemEltAbove instanceof ItemList)) { //insert above the Item (instanceof ItemList also matches Category)
//            if (((Category) itemEltAbove).getList().contains(itemEltBelow)) { //only insert if the item below is actually *in* the category
//                insertContainer = createInsertContainer(itemEltBelow, itemEltBelow.getOwner()); //create insertContainer
//                parentContainerBelow.addComponent(0, insertContainer); //insert new Item at the beginning of the item list (just below the 'header' category)
//            }
//        } else if (itemEltAbove instanceof Item && (itemEltBelow instanceof ItemList || itemEltBelow instanceof ItemList)) { //insert *below* (+1) the objAbove Item
//            insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner()); //, objAbove.getOwner()); //create insertContainer
//            parentContainerAbove.addComponent(parentContainerAbove.getComponentCount(), insertContainer); //insert new Item at the beginning of the item list (just below the 'header' category)
//        } else if (itemEltAbove instanceof Item && itemEltBelow instanceof Item) { //both objects are Items but not in same list, insert below (+1) objAbove
//            insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner()); //, objAbove.getOwner()); //create insertContainer
//            int insertIndex = parentContainerAbove.getComponentIndex(compAbove) + 1;
//            parentContainerAbove.addComponent(insertIndex, insertContainer); //insert new Item at the beginning of the item list (just below the 'header' category)
//        } else if (itemEltAbove != null) { //insert *after* objAbove
//            ASSERT.that(itemEltAbove != null);
//            insertContainer = createInsertContainer(itemEltAbove, itemEltAbove.getOwner()); //, objAbove.getOwner()); //create insertContainer
//            int insertIndex = parentContainerAbove.getComponentIndex(compAbove) + 1;
//            parentContainerAbove.addComponent(insertIndex, insertContainer); //insert new Item at the beginning of the item list (just below the 'header' category)
//        }
//        if (false && insertContainer != null && ((Container) insertContainer).getParent() != null) { //false: doesn't make sense to animate when insertContainer size is varied by pinch
//            ((Container) insertContainer).getParent().animateLayout(300);
//        }
//        insertContainer.setName("pinchWrapContainer");
//        return insertContainer;
//        return wrappedInsertContainer;
//</editor-fold>
    }

//    private Container wrapInPinchableContainer(final InsertNewElementFunc pinchComponent) {
    private Container wrapInPinchableContainer(final PinchInsertContainer pinchComponent) {
        //TODO!! make more fancy animation for container (eg fold like Clear)
//        Container pinchCont;
        if (pinchComponent != null) { //pinchOut makes sense here, a new pinchInsert container with the right type of element is created and inserted
            Container pinchCont = new Container(MyBorderLayout.center()) {
//                public Dimension calcPreferredSize() {
                public Dimension getPreferredSize() {
                    Dimension orgPrefSize = ((Component) pinchComponent).getPreferredSize();
                    //TODO!! do like Clear app: if pinching further out than the size show some 'elastic' empty space around the container 
//<editor-fold defaultstate="collapsed" desc="comment">
//                    MyForm myForm = (MyForm) pinchContainer.getComponentForm();
//                    if (myForm.pinchContainer == this) { //I'm the ongoing
//                    Log.p("+++++++++++++ this inside wrapInPinchableContainer = "+this);
//</editor-fold>
                    if (this == pinchContainer) { //I'm the ongoing pinchContainer (NB. this refers to the sourrounding Container!)
//                    int h = Math.max(0, orgPrefSize.getHeight()+pinchDistance); //distance negative if pinching in, max(0 to avoid negative) //TODO!! what happens if pinching out beyond the preferred height (does it just grow or leave white space??)
                        int h = Math.max(0, Math.min(pinchDistance, orgPrefSize.getHeight())); //min:cannot become bigger than preferredHeight of the component, max: can't get negative
                        return new Dimension(orgPrefSize.getWidth(), h); //Math.max(0, since pinch distance may become negative when fingers cross vertically
                    } else if (this == previousPinchContainer) { //I'm the previously inserted container
                        if (pinchContainer != null) { //the other pinchContainer may be pinching me down in size
                            int h = Math.max(0, Math.min(orgPrefSize.getHeight() - pinchDistance, orgPrefSize.getHeight())); //cannot become bigger than preferredHeight of the component
                            return new Dimension(orgPrefSize.getWidth(), h); //Math.max(0, since pinch distance may become negative when fingers cross vertically
                        }
                    } //else { //not sure if/why this happens (was it due to overriding calcPreferredSize() instead of getPreferredSize()??
                    return orgPrefSize;
//                    }
//                    return null;
                }
            };
            pinchCont.add(MyBorderLayout.CENTER, (Component) pinchComponent);

            if (Config.TEST_PINCH) {
                pinchCont.setName("wrapPinchContainer");
            }
            return pinchCont;
        }
        return null;
    }

    private Component getWrappedPinchableContainer(Container wrapped) {
        BorderLayout wrappedLayout = (BorderLayout) wrapped.getLayout(); //unwrap the actual pinchInsert container (wrapped above in a pinchable container)
        return wrappedLayout.getCenter();
    }

    /**
     * called either when two fingers (pointer) are released (pointerReleasea9,
     * or if one finger is released (changing from pinch to drag)
     */
    private void pinchInsertFinished() {
        if (Config.TEST_PINCH) {
            Log.p("pinchInsertFinished called");
        }
//        if (true || isPinchInsertEnabled()) { //checked before calling pinchInsertFinished()
        if (pinchInsertInitiated) { //checked before calling pinchInsertFinished()
            Container parentToAnimate = null;
            if (pinchContainer == null) { //no new pinch created
                if (previousPinchContainer != null) { //if pinched-in previous container
                    if (Config.TEST_PINCH) {
                        Log.p("[A] removing  pinchContainerPrevious");
                    }
//                    if (!minimumPinchSizeReached(pinchDistance, previousPinchContainer)) {
                    if (!minimumPinchSizeReached(pinchDistance, getWrappedPinchableContainer(previousPinchContainer))) {
                        parentToAnimate = MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParent(previousPinchContainer); //just remove it
                        ASSERT.that(parentToAnimate != null, "error in removing pinchContainer from its scrollY parent, direct parent=" + previousPinchContainer.getParent());
                        previousPinchContainer = null;
                    }
                }
                //TODO!!! what if we do a new pinch out at the same place as before (where a pinch cont is already inserted)?? ideally do nothing, but complicated to detect
            } else { //a new pinch created, pinchContainer != null
                //delete inserted container (whether a new container not sufficiently pinched OUT or an existing SubtaskContainer pinched IN)
                if (minimumPinchSizeReached(pinchDistance, getWrappedPinchableContainer(pinchContainer))) {
                    if (Config.TEST_PINCH) {
                        Log.p("pinchContainerPrevious left visible");
                    }
//                    Container pinchContainerParent = MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnCont(pinchContainer);
                    if (previousPinchContainer != null && !MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(previousPinchContainer)) {
                        if (Config.TEST_PINCH) {
                            Log.p("!! pinchContainerPrevious not removed correctly");
                        }
                    } //remove previous in one exists
                    previousPinchContainer = pinchContainer; //save just inserted container

                    pinchDistance = Integer.MAX_VALUE; //ensure that insertContainer is shown in full height even if pinch was released before pinchDistance reached that value

                    parentToAnimate = MyDragAndDropSwipeableContainer.getParentScrollYContainer(pinchContainer);
                    //once container is inserted, activate editing 
                    if (MyPrefs.pinchInsertActivateEditing.getBoolean() && getPinchInsertContainer() != null && getPinchInsertContainer().getTextArea() != null) {
                        getPinchInsertContainer().getTextArea().startEditingAsync();
                    }
                    ASSERT.that(parentToAnimate != null, "error in removing pinchContainer from its scrollY parent, direct parent=" + previousPinchContainer.getParent()); //possible when inserting the very first element
                } else {
                    if (Config.TEST_PINCH) {
                        Log.p("[B] removing  pinchContainer");
                    }
                    parentToAnimate = MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParent(pinchContainer); //remove new pinch container, leave old one (if exists) in pinchContainerPrevious
                    ASSERT.that(pinchContainer.getParent() == null, "removeFromParentScrollYContainer) did not remove pinchContainer from its parent");
                    if (true || pinchContainer.getParent() != null) {
                        pinchContainer.remove(); //TODO just use .remove() - should be enough to remove the pinchContainer and faster/safer than removeFromParentScrollYContainer
                    }
                }
            }
            //reset all values
            pinchContainer = null; //indicates done with this container //DOESN'T work since a pinch may be followed by another pinch w/o any drag or swipe!
            pinchDistance = Integer.MAX_VALUE; //ensure that insertContainer is shown in full height even if pinch was released before pinchDistance reached that value
            pinchInitialYDistance = Integer.MIN_VALUE; //reset pinchdistance
//                MyForm.this.revalidate(); //necessary after using replace()??
            initY = -1;
//                animateHierarchy(300);
            if (parentToAnimate != null) {
//                parentToAnimate.animateHierarchy(300);
                parentToAnimate.revalidateWithAnimationSafety();
            }
            pinchInsertInitiated = false;
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (pointerReleasedListener != null) {
//                removePointerReleasedListener(pointerReleasedListener);
//                pointerReleasedListener = null;
//            }
//</editor-fold>
        }
    }

//    ActionListener pointerReleasedListener = null;
//<editor-fold defaultstate="collapsed" desc="comment">
//    = (e) -> {
//        Log.p("pointerReleased called!!!!");
//        if (pinchInsertEnabled && pinchInsertInitiated) {
////            if (pinchContainer != null) {
//            pinchInsertFinished();
////            }
//        } //else { //don't call super.pointerR if finishing pinch since it may launch other events
//    };
//    @Override
//    public void pointerReleased(int x, int y) {
//        Log.p("pointerReleased called!!!!");
//        if (pinchInsertEnabled && pinchInsertInitiated) {
////            if (pinchContainer != null) {
//            pinchInsertFinished();
////            }
//        } //else { //don't call super.pointerR if finishing pinch since it may launch other events
//        super.pointerReleased(x, y);
////        }
//        //       addPointerReleasedListener((e) -> Log.p("PointerReleasedListener: pointer release (listener), evt= " + e));
//    }
//</editor-fold>
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
//    @Override
    public void pointerReleased(int[] x, int[] y) {
        if (pinchInsertInitiated) {
            if (Config.TEST_PINCH) {
                Log.p("pointerReleased(int[] x, int[] y) called!!!!");
            }
            pinchInsertFinished();
//            return;
        }
        super.pointerReleased(x, y);
    }

//    @Override
    public void pointerReleased(int x, int y) {
        if (pinchInsertInitiated) {
            if (Config.TEST_PINCH) {
                Log.p("pointerReleased(int x, int y) called!!!!");
            }
            pinchInsertFinished();
//            return;
        }
        super.pointerReleased(x, y);
    }

    private int initY = -1;

    @Override
    public void pointerDragged(int[] x, int[] y) {
//<editor-fold defaultstate="collapsed" desc="code to simulate two fingers on CN1 Simulator">
        boolean testingPinchOnSimulator = Config.TEST_PINCH && Display.getInstance().isSimulator();
        if (testingPinchOnSimulator) {
            int displayHeight = Display.getInstance().getDisplayHeight();
            //when testing in simulatoer, the simulated pinch zone is half the drop target zone
            int simulatePinchZoneWidthInPixels = Display.getInstance().convertToPixels(MyPrefs.dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask.getInt(), true) / 2;
            if (Display.getInstance().isSimulator() && y.length == 1 && x.length == 1
                    //                    && x[0] >= Display.getInstance().getDisplayWidth() * (100 - Config.TEST_PINCH_SCR_WIDTH_PERCENT) / 100
                    && x[0] >= Display.getInstance().getDisplayWidth() - simulatePinchZoneWidthInPixels
                    && y[0] < displayHeight / 2) {
                //simulate a pinch by mirroring the y values when dragging on the very right (10%) of the screen
                int[] y2 = new int[2];
                int[] x2 = new int[2];
                y2[0] = y[0];
                x2[0] = x[0];
                int x0 = x[0];
//                int x1=x[1];
                int y0 = y[0];
//                int y1=y[1];

                x2[1] = x[0]; //set simulated x for other finger to same value as first finger
//                y2[1] = Math.min(displayHeight, (displayHeight / 2 - y[0]) + displayHeight / 2); //set simulated y to y mirrored around the middle of the screen
//                y2[1] = Math.min(displayHeight, y[0] + displayHeight / 12); //set simulated y to y mirrored around the middle of the screen
                if (initY == -1) {
                    initY = y[0]; //1 pixel below
                    y2[1] = initY;
                } else {
                    int dist = initY - y[0]; //dist positive when moving UP
                    int simulY;
//                    if (y[0]<initY) {
//                        simulY = initY + dist;
//                    } else {
//                        simulY = initY - dist;
//                    }
                    simulY = initY + dist;
                    y2[1] = Math.min(displayHeight, Math.max(0, simulY)); //set simulated y to y mirrored around the middle of the screen
                }
//                y2[1] = Math.min(displayHeight, y[0] + 140); //80==roughly one workslot container
//                Log.p("simulating pinch x[0]=" + x[0] + " y[0]=" + y[0] + " simulated x[1]=" + x[1] + " y[1]=" + y[1]);
                if (false && Config.TEST_PINCH) {
                    Log.p("simulating pinch x[0]=" + x2[0] + " y[0]=" + y2[0] + " simulated x[1]=" + x2[1] + " y[1]=" + y2[1]);
                }
                x = x2; //replace org values with simulatd pair
                y = y2;
            }
        }
//</editor-fold>
        if (!isPinchInsertEnabled() || x.length <= 1) { //if pinch not enabled, do nothing (other than call super.pointerDragged())
//<editor-fold defaultstate="collapsed" desc="comment">
//            super.pointerDragged(x, y);
//        } else { //while pinching, pinch will consume the pointer dragged (to avoid that the list moves at the same time as if it was dragged)
//            if (x.length <= 1) { //PinchOut is either finished or not ongoing , simply call super.pointerDragged(x, y);
//                Log.p("pointerDragged called with just one finger active!!!!");
//                if (pinchContainer != null) { //a pinch container already exists meaning a pinch was ongoing before
//                    if (!minimumPinchSizeReached(pinchDistance, pinchContainer)) {
//                        //delete inserted container (whether a new container not sufficiently pinched OUT or an existing SubtaskContainer pinched IN)
////                        Label emptyLabel = new Label();
//                        Container pinchContainerParent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(pinchContainer.getParent());
////                        if ()
////                        pinchContainerParent.replace(pinchContainer, emptyLabel, null); //TODO!!! add meaningful animation
////                        pinchContainerParent.removeComponent(pinchContainer); //TODO!!! add meaningful animation
//                        MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(pinchContainer);
//                        pinchContainerParent.animateHierarchy(300);
////                        pinchContainerParent.removeComponent(emptyLabel);
////                        pinchDistance = Integer.MAX_VALUE; //ensure that insertContainer is shown in full height even if pinch was released before pinchDistance reached that value
////                        MyForm.this.revalidate(); //necessary after using replace()??
//                    }
//                }
//                if (false) {
//                    pinchInsertFinished();
//                }
//reset all values
//                pinchContainer = null; //indicates done with this container //DOESN'T work since a pinch may be followed by another pinch w/o any drag or swipe!
//                pinchDistance = Integer.MAX_VALUE; //ensure that insertContainer is shown in full height even if pinch was released before pinchDistance reached that value
//                MyForm.this.revalidate(); //necessary after using replace()??
//                pinchInitialYDistance = Integer.MIN_VALUE; //reset pinchdistance
//</editor-fold>
            super.pointerDragged(x, y);
        } else { // (x.length > 1) => PINCH ONGOING
//            ASSERT.that(pinchInsertInitiated || pointerReleasedListener == null, "pointerReleasedListener NOT null as it should be");
            pinchInsertInitiated = true;
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false && pointerReleasedListener == null) {
//                pointerReleasedListener = (e) -> {
//                    if (Config.TEST_PINCH) Log.p("pointerReleased called!!!!");
////                    if (pinchInsertEnabled && pinchInsertInitiated) {
//                    if (pinchInsertInitiated) {
////            if (pinchContainer != null) {
//                        pinchInsertFinished();
////                        e.consume(); //to avoid that finishing the pinch triggers other actions
////            }
//                    } //else { //don't call super.pointerR if finishing pinch since it may launch other events
//                };
//                addPointerReleasedListener(pointerReleasedListener);
//            }
//</editor-fold>
            //TODO!!! What happens if a pinch in is changed to PinchOut while moving fingers? Should *not* insert a new container but just leave the old one)
            //TODO!!! What happens if a pinch out is changed to PinchIn while moving fingers? Simply remove the inserted container!
            int yMin = Math.min(y[1], y[0]); //y[1] <= y[0] ? y[1] : y[0];
            int yMax = Math.max(y[1], y[0]); // y[1] > y[0] ? y[1] : y[0];
            int xTop;
            if (yMin == y[0]) {
                xTop = x[0];
            } else {
                xTop = x[1];
            }
            int newYDist = yMax - yMin;
            int pinchOutThreshold = Display.getInstance().convertToPixels((float) 0.7); //threshold for starting to pinch out about 1.5mm
//                if (newYDist<0)newYDist=0; //should not be allowed to become negative
            boolean pinchingOut = pinchInitialYDistance != Integer.MIN_VALUE && newYDist > pinchInitialYDistance + pinchOutThreshold;
            if (pinchInitialYDistance == Integer.MIN_VALUE) {
                pinchInitialYDistance = newYDist; //Math.abs(y[1]-y[0]);
            }
//                pinchDistance = Math.max(0, newYDist - pinchInitialYDistance); //not allowed to become negative
            pinchDistance = newYDist - pinchInitialYDistance; //not allowed to become negative
            if (false && Config.TEST) {
                Log.p("PointerDragged pinchInitialYDistance=" + pinchInitialYDistance + "; newYDist=" + newYDist + "; pinchDistance=" + pinchDistance + ", y[0]=" + y[0] + ", y[1]=" + y[1]);
            }

            if (pinchContainer == null) { // && pinchDistance > 0) { if we wait till pinchDistance is >0, then the finger may already have moved in to another item than the one we started in(?!)
//DONE!! if existing pinch container is elsewhere, insert a new one between the two fingers and decrease the size of the old one inversely wrt new size
//<editor-fold defaultstate="collapsed" desc="comment">
//                    pinchContainerPrevious=pin

//                pinchContainer = createAndInsertPinchContainer(x, y, () -> {
//                    //NO longer needed, wrapping is handled by pinchInsertFinished()
////                        if (this.pinchContainer ==
//                    pinchContainer = null; //when the container closes itself, we need to know to determine if to insert a new one, or pinch the existing
//                    pinchContainerPrevious = null; //when the container closes itself, we need to know to determine if to insert a new one, or pinch the existing
//                    //no need to call animate, is done when closing
//                });
//                pinchContainer = createAndInsertPinchContainer(x, y);
//</editor-fold>
                Container parent;
                if (pinchingOut) {
                    int[] yAdj = new int[2];
                    if (Config.TEST) { //TODO set values in hard, or improve efficiency
                        yAdj[0] = yMin + newYDist / 10 * MyPrefs.pinchAdjustUpper.getInt();
                        yAdj[1] = yMax - newYDist / 10 * MyPrefs.pinchAdjustLower.getInt();
                    } else {
                        yAdj[0] = yMin + newYDist / 10 * MyPrefs.pinchAdjustUpper.getInt();
                        yAdj[1] = yMax - newYDist / 10 * MyPrefs.pinchAdjustLower.getInt();
                    }
                    createAndInsertPinchContainer(x, yAdj);

                    if (Config.TEST_PINCH) {
                        Log.p("inserted pinchContainer");
                    }
                    parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(pinchContainer);
                } else { //we're pinching in, if there's already previous pinch container, animate to squeeze it in (and remove)
                    parent = previousPinchContainer != null ? previousPinchContainer.getParent() : null;
                }
//                    MyForm.this.animateLayout(300);//.revalidate(); //refresh
                if (parent != null) {
//                        parent.animateLayout(300);
                    parent.revalidateWithAnimationSafety();
                }
            } else { //pinchContainer != null || pinchDistance <= 0
                //we already have a pinchContainer (either being inserted or inserted previously), so do nothing other than resize
//<editor-fold defaultstate="collapsed" desc="comment">
//                    MyForm.this.revalidate(); //refresh with new size of pinchContainer
//                if (pinchContainer != null) {
////                        MyForm.this.repaint();//is repaint enough to refreshTimersFromParseServer the view?? refreshTimersFromParseServer with new size of pinchContainer
//                    if (pinchContainer.getParent() != null) {
////                            pinchContainer.getParent().animateLayout(300);
//                        pinchContainer.getParent().revalidateWithAnimationSafety(); //refresh to reflect to new pinched size of pinchContainer
//                    }
//                }
//                if (pinchContainer != null &&pinchContainer.getParent() != null)
//</editor-fold>
                if (pinchContainer.getParent() != null) {
                    pinchContainer.getParent().revalidateWithAnimationSafety(); //refresh to reflect to new pinched size of pinchContainer
                }
            }
            if (false) { //if calling super.point... here, pinching will also scroll!
                super.pointerDragged(x, y); //OK to have this call, not causing problems for above pinch logic??
            }
            super.pointerDragged(xTop, yMin); //emulate a single finger to ensure scriolling up as inserting pichcontainer
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//            super.pointerDragged(x, y); //leaving this call will make the screen scroll at the same time if the two fingers move
//        }
//        if (false) {
//            super.pointerDragged(x, y);
//        }
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void pointerDraggedOLD(int[] x, int[] y) {
//        if (!pinchInsertEnabled) {
////            super.pointerDragged(x, y);
//        } else { //pinchInsertEnabled
////            } else { //pinchContainer != null) => we already have a pinchContainer (either being inserted or inserted previously)
//            if (x.length <= 1) { //PinchOut is either finished or not ongoing (newPinchContainer!=null means a pinch was ongoing before)
////                if (pinchContainer == null) { //no previous pinchContainer, do nothing
//////                    super.pointerDragged(x, y);
////                } else { //a pinch container already exists, do nothing, insertContainer already in place
//                if (pinchContainer != null) { //a pinch container already exists, do nothing, insertContainer already in place
////                    if (minimumPinchSizeReached(pinchDistance, pinchContainer)) {
////<editor-fold defaultstate="collapsed" desc="comment">
////add new item into underlying list - NO, done in the pinchConatiner itself when hitting Enter or [>]
////                    itemList.addItemAtIndex(pinchItem, pos);
////insert pinchContainer into the displayed list at the right position
////                        insertPinchContainer(prevComponentAbove, prevComponentBelow, pinchContainer); //ALREADY inserted when growing, just leave it in place
////replace pinchContainer by (temporary) new Element container to quickly update (before regenerating the list) - NO, done in pinchContainer itself as well
////                        pinchContainer.getParent().replace(pinchContainer, createElementComponent.createFinalComponent(itemList), null); //TODO!!! add meaningful animation
////                        pinchContainer = null; //keep the container if there's a later pinchIn
////</editor-fold>
////                    } else {
//                    if (!minimumPinchSizeReached(pinchDistance, pinchContainer)) {
//                        //delete inserted container (whether a new container not sufficiently pinched OUT or an existing SubtaskContainer pinched IN)
////                        Label emptyLabel = new Label();
//                        Container pinchContainerParent = pinchContainer.getParent();
////                        pinchContainerParent.replace(pinchContainer, emptyLabel, null); //TODO!!! add meaningful animation
////                        pinchContainerParent.removeComponent(emptyLabel);
////                        pinchContainerParent.replace(pinchContainer, emptyLabel, null); //TODO!!! add meaningful animation
//                        pinchContainerParent.removeComponent(pinchContainer);
//                        pinchContainer = null; //indicates done with this container
////                        MyForm.this.refreshAfterEdit();
//                        MyForm.this.revalidate(); //necessary after using replace()??
//                    }
//                    pinchInitialYDistance = Integer.MIN_VALUE; //reset pinchdistance
//                }
////                display(x, y, false);
//            } else { // (x.length > 1) => PINCH ONGOING
//                //TODO!!! What happens if a pinch in is changed to PinchOut while moving fingers? Should *not* insert a new container but just leave the old one)
//                //TODO!!! What happens if a pinch out is changed to PinchIn while moving fingers? Simply remove the inserted container!
//                int yMin = Math.min(y[1], y[0]); //y[1] <= y[0] ? y[1] : y[0];
//                int yMax = Math.max(y[1], y[0]); // y[1] > y[0] ? y[1] : y[0];
////                int xMin = y[1] <= y[0] ? x[1] : x[0]; //xMin is the x[n] corresponding to the minimal y[n]
////                int xMax = y[1] > y[0] ? x[1] : x[0];
//                int newYDist = yMax - yMin;
////                if (newYDist<0)newYDist=0; //should not be allowed to become negative
//                if (pinchInitialYDistance == Integer.MIN_VALUE) {
//                    pinchInitialYDistance = newYDist; //Math.abs(y[1]-y[0]);
//                }
//                pinchDistance = Math.max(0, newYDist - pinchInitialYDistance); //not allowed to become negative
//
//                if (pinchContainer == null && pinchDistance > 0) {
//                    //for now: simply decrease the size of the existing container
////                    if (pinchOut) {
////                    if (pinchDistance > 0) { //as soon as we have a positive pinchOut, create and insert the insertContainer
//                    //TODO!! if existing pinch container is elsewhere, insert a new one between the two fingers and decrease the size of the old one inversely wrt new size
////                        pinchComponent = createAndInsertPinchComponent.createAndInsert(x, y, true, () -> pinchDistance);
////                        pinchComponent = createAndInsert(x, y, true, () -> pinchDistance);
//                    pinchContainer = createAndInsertPinchContainer(x, y);
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (pinchComponent != null) { //pinchOut makes sense here, a new pinchInsert container with the right type of element is created and inserted
////                            pinchContainer = new Container(BorderLayout.center()) {
////                                public Dimension calcPreferredSize() {
//////                                    Dimension orgPrefSize = super.calcPreferredSize();
////                                    Dimension orgPrefSize = pinchComponent.getPreferredSize();
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                                    if (oldPinchContainer != null && oldPinchContainer == Container.this) { //I am now the old pinchContainer
//////                                        return new Dimension(orgPrefSize.getWidth(), getInsertContainerHeight(orgPrefSize.getHeight())); //Math.max(0, since pinch distance may become negative when fingers cross vertically
//////if I'm old pinchContainer, and reduced to zero size
//////                                    } else {
//////</editor-fold>
////                                    return new Dimension(orgPrefSize.getWidth(), getInsertContainerHeight(orgPrefSize.getHeight())); //Math.max(0, since pinch distance may become negative when fingers cross vertically
////                                }
////                            };
////                            pinchContainer.add(BorderLayout.CENTER, pinchComponent);
////                        }
////</editor-fold>
////                        MyForm.this.refreshAfterEdit(); //really necessary? //no, should only be done once the new element is effectively inserted (so, should be done by the InsertContainer itself)
//                    MyForm.this.revalidate(); //refresh
////                    } else {
//                    //reduce size of existing container
//                    //DO NOTHING (don't create a new pinchContainer if pinching in)
////                    }
//                } else { //pinchContainer != null
////                    if (pinchDistance > 0) { //as soon as we have a positive pinchOut, create and insert the insertContainer
//                    //TODO!! if existing pinch container is elsewhere, insert a new one between the two fingers and decrease the size of the old one inversely wrt new size
//                    //TODO!! check if the pinch container is between the two fingers and only decrease it then??
//                    //we already have a pinchContainer (either being inserted or inserted previously), so do nothing other than resize
//                    Log.p("PointerDragged dist=" + pinchDistance + ", x=" + x + ", y=" + y);
//                    MyForm.this.revalidate(); //refresh with new size of pinchContainer
////                    display(x, y, true);
//                }
//            }
//        }
//        super.pointerDragged(x, y);
////        displayTest(x, y, true);
//        //            super.pointerDragged(x[0], y[0]);
//    }
//</editor-fold>
    @Override
    public void animateHierarchy(final int duration) {
        if (Config.TEST_PINCH) {
            Log.p("*******animateHierarchy(" + duration + ") - expensive call");
        }
        super.animateHierarchy(duration);
    }

    /**
     * copied from Form (where it is **private)
     *
     * @param c
     * @return
     */
    public static Component findScrollableChild(Container c) {
        if (c.isScrollableY()) {
            return c;
        }
        int count = c.getComponentCount();
        for (int iter = 0; iter < count; iter++) {
            Component comp = c.getComponentAt(iter);
            if (comp.isScrollableY()) {
                return comp;
            }
            if (comp instanceof Container) {
                Component chld = findScrollableChild((Container) comp);
                if (chld != null) {
                    return chld;
                }
            }
        }
        return null;
    }

    public static ContainerScrollY findScrollableContYChild(Container c) {
        if (c instanceof ContainerScrollY) {
            return (ContainerScrollY) c;
        }
        int count = c.getComponentCount();
        for (int iter = 0; iter < count; iter++) {
            Component comp = c.getComponentAt(iter);
            if (comp instanceof ContainerScrollY) {
                return (ContainerScrollY) comp;
            }
            if (comp instanceof Container) {
                Component chld = findScrollableChild((Container) comp);
                if (chld instanceof ContainerScrollY) {
                    return (ContainerScrollY) chld;
                }
            }
        }
        return null;
    }

    public ContainerScrollY findScrollableContYChild() {
        return findScrollableContYChild(getContentPane());
    }

    /**
     * return the top-level ItemAndListCommonInterface edited or displayed by
     * this Form. Eg if showing a Project or List, return that
     *
     * @return
     */
    public ItemAndListCommonInterface getDisplayedElement() {
        assert false;
        return null;
    }

    /**
     * if y is within the upper/lower bounds of an adjacent
     * MyDragAndDropSwipeableContainer, then return it
     *
     * @param cmp
     * @param x
     * @param y
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private MyDragAndDropSwipeableContainer findMyDDContAtY(Component cmp, int y) {
//        if (cmp instanceof MyDragAndDropSwipeableContainer && y >= cmp.getAbsoluteY() && y <= cmp.getAbsoluteY() + cmp.getHeight())
//            return (MyDragAndDropSwipeableContainer) cmp;
//        if (cmp instanceof Container) {
//            Container cont = (Container) cmp;
//            for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
//                Component c = cont.getComponentAt(i);
//                MyDragAndDropSwipeableContainer found = findMyDDContAtY(c, y);
//                if (found != null)
//                    return found;
//            }
//        }
//        return null;
//    }
//</editor-fold>
    private static Component findDropTargetAtY(Component cmp, int y) {
        if (y >= cmp.getAbsoluteY() && y < cmp.getAbsoluteY() + cmp.getHeight()) {
            if (cmp.isDropTarget()) {
                return cmp;
            }
            if (cmp instanceof Container) {
                Container cont = (Container) cmp;
                for (int i = 0, size = cont.getComponentCount(); i < size; i++) {
                    Component c = cont.getComponentAt(i);
                    Component found = findDropTargetAtY(c, y);
                    if (found != null) {
                        return found;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Recursively searches the container hierarchy for a drop target, extended
     * compared to Container.findDropTargetAt(int x, int y) to also search for
     * and return any MyDragAndDropSwipeableContainer which is next to/adjacent
     * to the (x,y) (y is within the upper&lower bounds of the
     * MyDragAndDropSwipeableContainer). This to cover the use case where the
     * dropTarget is indented to the right (expanded subtasks) and the drop is
     * done on the left side of the screen (drop as supertask).
     *
     * @param x position in which we are searching for a drop target
     * @param y position in which we are searching for a drop target
     * @return a drop target or null if no drop target could be found at the x/y
     * position
     */
    @Override
    public Component findDropTargetAt(int x, int y) {
        return findDropTargetAt(this, x, y);
    }

    static Component findDropTargetAt(Container cont, int x, int y) {
        int count = cont.getComponentCount();
//        for (int i = count - 1; i >= 0; i--) {
        for (int i = 0, size = cont.getComponentCount(); i < size; i++) {
            Component cmp = cont.getComponentAt(i);
            if (cmp.contains(x, y)) {
//                System.out.print("+");
                if (cmp.isDropTarget()) {
                    return cmp;
                }
                if (cmp instanceof Container) {
                    Component component = findDropTargetAt((Container) cmp, x, y);
                    if (component != null) {
                        return component;
                    }
                }
            } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//                int absY = cmp.getAbsoluteY();
//                int absX = cmp.getAbsoluteX();
//                int absXW = absX + cmp.getWidth();
//                int h = cmp.getHeight();
//                int absYH = absY + h;
//                System.out.print("-");
//                if (cmp instanceof MyDragAndDropSwipeableContainer) {
//                if (y >= cmp.getAbsoluteY() && y <= cmp.getAbsoluteY() + cmp.getHeight()) {
//                    if (cmp.isDropTarget()) {
//                        return cmp;
//                    }
//                    if (cmp instanceof Container) {
////                    Component component = ((Container) cmp).findDropTargetAt(x, y);
//                        Component component = findDropTargetAt((Container) cmp, x, y);
//                        if (component != null) {
//                            return component;
//                        }
//                    }
//                    System.out.print("=[" + y + ";" + absY + ";" + absYH + "]");
////                System.out.print("m");
//                    if (y >= cmp.getAbsoluteY() && y <= cmp.getAbsoluteY() + cmp.getHeight()) {
////                        Log.p("XXX");
//                        System.out.print("XXXX");
//                        return cmp;
//                    }
//                }
//</editor-fold>
                Component c = findDropTargetAtY(cmp, y);
                if (c != null) {
                    return c;
                }
            }
        }
        return null;
    }

    public void showStatusBar(boolean show) {
//        if (getUIManager().isThemeConstant("paintsTitleBarBool", false)) {
        Hashtable<String, Object> theme = new Hashtable<>();
        theme.put("paintsTitleBarBool", show);
//        getUIManager().setThemeProps(theme);
        UIManager.getInstance().addThemeProps(theme);
    }

    @Override
    protected void onShow() {
        if (getStartEditingAsyncTextArea() != null) {
            getStartEditingAsyncTextArea().startEditingAsync();
            Log.p("---->>> startEditingAsync() for TextArea named=" + getStartEditingAsyncTextArea().getName());
        } else if (getPinchInsertContainer() != null) {
            getPinchInsertContainer().getTextArea().startEditingAsync();
        }

        if (true || !restoreKeepPos()) { //an explicitly set restorePos takes precedence over normal scroll
            if (previousValues != null) {// && previousValues.getScrollY() != null) {
                previousValues.scrollToSavedYOnFirstShow(findScrollableContYChild());
            }
        }

        if (previousValues != null && previousValues.get(MySearchCommand.SEARCH_KEY) != null && getSearchCmd() != null) {
            getSearchCmd().actionPerformed(null); //re-activate Search, null=>reuse locally stored text
        }
    }

    /**
     * called when leaving a screen
     */
    void onExit() {

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
