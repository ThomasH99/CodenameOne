/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.TableLayout;
import static com.todocatalyst.todocatalyst.TimerStack.getInstance;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
public class ScreenTimerView {

    private TimerController controller;
    private TimerStackModel model;
    final static String TIMER_REPLAY = "StartTimer-";
    final static String SMALL_TIMER_TEXT_AREA_TO_START_EDITING = "SmallTimerTextArea";

    final static Object TIMER_LOCK = new Object(); //lock operations on Timer such as updating/saving instances or refreshing Timers from Server

    ScreenTimerView(TimerController controller) {
        this.controller = controller;
    }

    /**
    
    @param contentPane
    @param fullScreenTimer 
     */
//    public void startTimerOnNextOrExitIfNone(TimerInstance timerInstanceXXX, Container contentPane) {
//    public void startTimerOnNextOrExitIfNone(Container contentPane, boolean fullScreenTimer) {
    private void resetTimerSmallContainer() {
        setSmallContainer(null);
    }

    private void setSmallContainer(Container smallContainer) {
        this.smallContainer = smallContainer;
    }
//    private static Container getContentPaneSouth() {
//        return getContentPaneSouth(Display.getInstance().getCurrent());
//    }

    private static Container getContentPaneSouthXXX(Form form) {
//        Form form = Display.getInstance().getCurrent();
        if (form != null) {
            Container formContentPane = form.getContentPane();
            if (!(form instanceof ScreenTimer6)) {
                Layout contentPaneLayout = formContentPane.getLayout();
                if (contentPaneLayout instanceof MyBorderLayout) {
                    Component southComponent = ((MyBorderLayout) contentPaneLayout).getSouth();
                    if (southComponent instanceof Container) {
                        return (Container) southComponent;
                    }
                }
            }
        }
        return null;
    }

    /**
    return the container into which the smallTimer should be interted. Return null if no smallTimer should be shown. 
    Override to disable smallTimer or place it somewhere else. 
    @return 
     */
    static protected Container getContainerForSmallTimerXXX() {
        return getContainerForSmallTimerXXX(Display.getInstance().getCurrent());
    }

    static protected Container getContainerForSmallTimerXXX(Form form) {
        Container timerContainer = null;
//        Form form = this;
//        if (form instanceof ScreenListOfItemLists || form instanceof ScreenListOfItems
//                || form instanceof ScreenStatistics
//                || form instanceof ScreenListOfWorkSlots || form instanceof ScreenListOfCategories) {
        if (form instanceof MyForm && !(form instanceof ScreenCategoryPicker
                || form instanceof ScreenListOfAlarms
                || form instanceof ScreenLogin || form instanceof ScreenObjectPicker
                || form instanceof ScreenRepair || form instanceof ScreenTimer6)) {
//        }else {
            Container formContentPane = form.getContentPane();
            Layout contentPaneLayout = formContentPane.getLayout();
            if (contentPaneLayout instanceof MyBorderLayout) {
//                timerContainer = getContentPaneSouth(form);
                Component southComponent = ((MyBorderLayout) contentPaneLayout).getSouth();
                if (southComponent instanceof Container) {
                    timerContainer = (Container) southComponent;
                } else if (southComponent == null) {
                    Container newCont = new Container(BoxLayout.y());
                    formContentPane.add(MyBorderLayout.SOUTH, newCont);
                    timerContainer = newCont;
                }
            }
        }
        return timerContainer;
    }

    private static void refreshScreenOnTimerUpdate() {
        Form form = Display.getInstance().getCurrent();
        if (form instanceof MyForm && !(form instanceof ScreenTimer6)) { //don't do when showing full screen timer
            ((MyForm) form).refreshAfterEdit();
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container getSmallContainerXXX() {
////        return smallContainer;
////        Container south = getContentPaneSouth();
//        Container south = MyForm.getContainerForSmallTimer();
//        if (south != null && south.getComponentCount() > 0) {
//            ASSERT.that(south.getComponentCount() == 1, "too many components in SmallTimerContainer");
//            Component smallTimerContainer = south.getComponentAt(0);
//            if (smallTimerContainer instanceof Container) {
//                return (Container) smallTimerContainer;
//            }
//        }
//        return null;
//    }
//    private boolean isSmallContainerCurrentlyShown() {
//        Container smallContainer = getSmallContainer();
//        if (smallContainer == null || smallContainer.getParent() == null || smallContainer.getComponentForm() == null || smallContainer.getComponentForm() != Display.getInstance().getCurrent()) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private  Container getOrMakeSmallContainer() {
    //        Container smallContainer = getSmallContainer();
    //        if (smallContainer == null) {
    //            TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
    //            if (timerInstance != null) {
    ////                timerInstance.setTimerContainer(new Container());
    //                timerInstance.setTimerFullScreen(false);
    //                smallContainer = new Container();
    ////                buildContentPane(smallContainer, timerInstance, timerInstance.isTimerFullScreen(), null); //false=>small timer container
    //                buildContentPane(smallContainer, timerInstance, false, null); //false=>small timer container
    //                setSmallContainer(smallContainer);
    //            }
    //        }
    //        return smallContainer;
    //    }
    //</editor-fold>
    /**
    called when using Back from full screen timer UI or when starting any screen that will show the small timer ui. after being added the small timer
    UI will update itself via the UITimer callbacks
    @param form add to the SOUTH container of this form, if form is null then add to Display.get
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static boolean addSmallTimerWindowIfTimerIsRunning(Form form) {
////        return addSmallTimerWindowIfTimerIsRunning(form, getInstance().getSmallContainer());
////        return addSmallTimerWindowIfTimerIsRunning(form, buildContentPaneSmall(form)); //make a new container each time
//        return addSmallTimerWindowIfTimerIsRunning(form, null); //make a new container each time
//
//    }
//
//    public static boolean addSmallTimerWindowIfTimerIsRunningXXX(Form form, Container timerContainer) {
////        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
////        if (timerInstance != null) {
//////            Container timerForContainer = MyForm.getContainerForSmallTimer();
////            Container timerForContainer = MyForm.getContainerForSmallTimer(form);
////            if (timerForContainer != null) {
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            Container timerForContainer = getContainerForSmallTimer(form);
//            if (timerForContainer != null) { //if no container, do nothing (avoid e.g. the case where ScreenLogin tries to load timers before login completed)
//                if (timerContainer == null) {
//                    timerContainer = buildContentPaneSmall(form);
//                }
//                if (timerContainer != null) { //buildContentPaneSmall returns null if no timer active
////            Container timerForContainer = MyForm.getContainerForSmallTimer();
////                if (timerContainer != null && timerContainer.getParent() != null) {
////                    if (timerContainer.getParent() != null) {
////                        timerContainer.getParent().removeComponent(timerContainer); //remove from previous parent before adding to new
////                    }
//                    timerContainer.remove();
////                    if (TimerStack.getInstance().smallContainer!=null )
////                        TimerStack.getInstance().smallContainer.remove();
//                    TimerStack.getInstance().removeTimerSmallContainer();
//                    timerForContainer.add(timerContainer);
//                    TimerStack.getInstance().smallContainer = timerContainer;
////                if (form.get)
//                    if (false) {
//                        form.animateLayout(300); //this call may be creating index out of bound in toolbar if called before the form is shown
//                    }
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static boolean addSmallTimerWindowIfTimerIsRunningXXX(Form form, Container timerContainer) {
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            Container formContentPane = form.getContentPane();
//            if (!(form instanceof ScreenTimer6)) {
//                Layout contentPaneLayout = formContentPane.getLayout();
//                if (contentPaneLayout instanceof BorderLayout) {
//                    Component southComponent = getContentPaneSouth(form);
//                    ASSERT.that(southComponent == null, "SOUTH should be empty in all screens where we add a small timer");
//                    if (southComponent == null) { //if south container is not already used for something else //TODO!!! shouldn't happen, but need to check
//                        if (timerContainer != null) {
//                            if (timerContainer.getParent() != null) {
//                                timerContainer.getParent().removeComponent(timerContainer); //remove from previous parent before adding to new
//                            }
//                            formContentPane.add(BorderLayout.SOUTH, timerContainer);
//                            TimerStack.getInstance().smallContainer = timerContainer;
//                            form.animateLayout(300);
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static boolean addSmallTimerWindowIfTimerIsRunningOLD(Form form, Container timerContainer) {
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            Container formContentPane = form.getContentPane();
//            if (!(form instanceof ScreenTimer6)) {
//                Layout contentPaneLayout = formContentPane.getLayout();
//                if (contentPaneLayout instanceof BorderLayout) {
//                    Component southComponent = ((BorderLayout) contentPaneLayout).getSouth();
//                    ASSERT.that(southComponent == null, "SOUTH should be empty in all screens where we add a small timer");
//                    if (southComponent == null) { //if south container is not already used for something else //TODO!!! shouldn't happen, but need to check
////                    Container timerContainer = getInstance().getSmallContainer(); //timerInstance.getTimerContainer();
//                        if (timerContainer != null) {
//                            if (timerContainer.getParent() != null) {
//                                timerContainer.getParent().removeComponent(timerContainer); //remove from previous parent before adding to new
//                            }
////                        formContentPane.add(CN.SOUTH, timerContainer);
//                            formContentPane.add(BorderLayout.SOUTH, timerContainer);
//                            form.animateLayout(300);
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//</editor-fold>
    private void removeTimerSmallContainer() {
//<editor-fold defaultstate="collapsed" desc="comment">
//         removeTimerSmallContainer(smallContainer);
//remove small timer UI
//        Container smallContainer = getSmallContainer();
//        if (smallContainer != null) {
//            Form form = smallContainer.getComponentForm();
//            Container parent = smallContainer.getParent();
//            if (parent != null) {
//                parent.removeComponent(smallContainer); //remove small timer window from its parent
//            }
//            if (form != null) {
//                form.animateLayout(300); //animate form to remove timerPane
//            } else if (parent != null) {
//                parent.animateLayout(300); //animate form to remove timerPane
//            }
//            if (false) {
//                setSmallContainer(null);
//            }
//        }
//        if (smallContainer != null && smallContainer.getParent() != null) {
//            smallContainer.getParent().removeComponent(smallContainer);
//            smallContainer = null;
//        }
//</editor-fold>
        if (smallContainer != null) smallContainer.remove();
        smallContainer = null;
    }

    /**
    update the UI based on current timer (if any). Either add (or remove!) a small timer to current screen or launch the full screen timer UI
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowTimerUI() {
//        refreshOrShowTimerUI(null);
//    }
//
//    void refreshOrShowTimerUI(MyForm previousForm) {
//        refreshOrShowTimerUI(previousForm, false);
//    }
//
//    private void addOrRefreshSmallTimerContainer() {
//        removeTimerSmallContainer();
//        if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//            Container small = buildContentPaneSmall(myCurrentForm); //refresh the small container
//            addSmallTimerWindowIfTimerIsRunning(myCurrentForm, small);
//            myCurrentForm.setStartEditingAsyncTextArea((TextArea) small.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
//            myCurrentForm.refreshAfterEdit(); //always removeFromCache, e.g. to update for Done tasks
//        } else { //show full screen timer
//            new ScreenTimer6(myCurrentForm, timerInstance).show();
//        }
//    }
//</editor-fold>
//    void refreshOrShowTimerUINEW(MyForm previousForm, boolean interruptOrInstantTask) {
    void refreshOrShowTimerUI() {
        refreshOrShowTimerUI(Display.getInstance().getCurrent());
    }

    void refreshOrShowTimerUI(Form currentForm) {
        /**
        different situations to account for:
        No (more) timers, remove any shown (remove smallTimer or Back from bigTimer).
        Replay (with show smallTimer or not) - do not show timer until last screen.
        Refresh (either small or big timer, which is already shown) when timer has changed. 
        Show timer for first timer = add smallTimer or show BigTimer. 
         */
        if (false && ReplayLog.getInstance().isReplayInProgress() && !ReplayLog.getInstance().isReplayAtLastCommand())
            return; //don't add small Timers to any replayed screens

        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if ()
//        Form currentForm = Display.getInstance().getCurrent();
        MyForm myCurrentForm = null;
        if (currentForm instanceof MyForm)
            myCurrentForm = (MyForm) currentForm;
        else {
            assert false;
            return;
        }
        if (timerInstance == null) { //no (or no more) timers, removeSmall or exit from big timer
            if (myCurrentForm instanceof ScreenTimer6) {
                if (false) myCurrentForm.showPreviousScreenOrDefault(true); //exit to previous screen //DON'T exit here, will be done by commands in Big Timer
            } else {
//                if (myCurrentForm.removeSmallTimerCont()) //remove old smallTimer (if there is one)
//                    myCurrentForm.revalidateWithAnimationSafety();
                myCurrentForm.removeSmallTimerCont(); //NO need to revalidate, done in refreshAfterEdit which calls refreshShowTimerUI //remove old smallTimer (if there is one)
            }
        } else { //there is an active timer
            if (myCurrentForm instanceof ScreenTimer6) { //if full screen Timer is active, refresh it
                if (false) myCurrentForm.refreshAfterEdit();
//                myCurrentForm.revalidateWithAnimationSafety(); //NOT sufficient, need to rebuild timer screen with commands related to new item
//            } else if (timerInstance.isFullScreen()) { //if running timer was running in FullScreen, start up in full screen again (on app relaunch)
//                new ScreenTimer6(myCurrentForm, timerInstance).show();
            } else { //other form than ScreenTimer6
//<editor-fold defaultstate="collapsed" desc="comment">
//                Container previousSmallTimer = myCurrentForm.getSmallTimerCont();
//                if (previousSmallTimer != null) { //smallTimer already visible
////                    myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
//                    myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
//                    smallTimer = buildContentPaneSmall(myCurrentForm); //refresh the small container
//                    myCurrentForm.addSmallTimerCont(smallTimer); //remove old smallTimer (if there is one)
////                    myCurrentForm.revalidateWithAnimationSafety();
//                    if (smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null)
//                        myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
//                } else { //show timer for first time
//                    if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//                        myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
//                        myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
//                        smallTimer = buildContentPaneSmall(myCurrentForm); //refresh the small container
//                        myCurrentForm.addSmallTimerCont(smallTimer); //remove old smallTimer (if there is one)
////                        myCurrentForm.revalidateWithAnimationSafety();
//                        if (smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null)
//                            myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
//                    } else {
////                        new ScreenTimer6(previousForm, timerInstance).show();
//                        new ScreenTimer6(myCurrentForm, timerInstance).show();
//                    }
//                }
//</editor-fold>
                myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
                if (//timerInstance.isFullScreen() ||
                        !(MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean()
                        && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean())) {
//                        new ScreenTimer6(previousForm, timerInstance).show();
                    timerInstance.setFullScreen(true); //set true in case we moved to full screen because of the settings
                    new ScreenTimer6(myCurrentForm, timerInstance).show();
                } else {//if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
                    myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
                    Container smallTimer = buildContentPaneSmall(myCurrentForm); //refresh the small container
                    myCurrentForm.addSmallTimerCont(smallTimer);
                    if (smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null)
                        myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
//                    myCurrentForm.revalidateWithAnimationSafety();
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowTimerUIXXX(MyForm previousForm, boolean interruptOrInstantTask) {
//        /**
//        different situations to account for:
//        No (more) timers, remove any shown (remove smallTimer or Back from bigTimer).
//        Replay (with show smallTimer or not) - do not show timer until last screen.
//        Refresh (either small or big timer, which is already shown) when timer has changed.
//        Show timer for first timer = add smallTimer or show BigTimer.
//         */
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        Form currentForm = Display.getInstance().getCurrent();
//        if (currentForm == null) {
//            ASSERT.that(ReplayLog.getInstance().isReplayInProgress(), "should only show new form if replay is ongoing");
//            ASSERT.that(previousForm != null, "should only have currentForm==null if previousForm is defined/a valid form");
//            new ScreenTimer6(previousForm, timerInstance).show();
//            return;
//        } else if (!(currentForm instanceof MyForm)) {
//            return;
//        }
//
//        MyForm myCurrentForm = (MyForm) currentForm;
//        if (myCurrentForm instanceof ScreenTimer6) {
//            //full screen timer currently shown, so remove it
//            myCurrentForm.refreshAfterEdit();
//        } else { //other form than ScreenTimer6
//            myCurrentForm.setKeepPos(new KeepInSameScreenPosition());
//            removeTimerSmallContainer();
//            if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//                Container small = buildContentPaneSmall(myCurrentForm); //refresh the small container
////                addSmallTimerWindowIfTimerIsRunning(myCurrentForm, small);
//                myCurrentForm.addSmallTimerCont(small);
//                myCurrentForm.setStartEditingAsyncTextArea((TextArea) small.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
//                myCurrentForm.refreshAfterEdit(); //always removeFromCache, e.g. to update for Done tasks
//            } else { //show full screen timer
//                new ScreenTimer6(myCurrentForm, timerInstance).show();
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowTimerUIXXX() {
//        Form form = Display.getInstance().getCurrent();
//        boolean isReplayInProgress = ReplayLog.getInstance().isReplayInProgress();
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if (isReplayInProgress) {
//            if (form != null && form instanceof MyForm && timerInstance != null) {
//                new ScreenTimer6((MyForm) form, timerInstance).show();
//            }
////        } else if ((form == null || !(form instanceof MyForm)) && !isReplayInProgress) {
//        } else if ((form == null || !(form instanceof MyForm))) {
//            return;
//        }
//        MyForm myForm = (MyForm) form;
//        if (myForm instanceof ScreenTimer6) {
//            //full screen timer currently shown, so removeFromCache it
////            ((ScreenTimer6) form).refreshAfterEdit();
//            myForm.refreshAfterEdit();
//        } else { //other form than ScreenTimer6
////            TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
////            if (timerInstance == null) {
////                //no timer to show, so remove any UI still shown (only relevant for smallTimer since timer screen will remove itself)
////                removeTimerSmallContainer();
////                return;
////            }
//            removeTimerSmallContainer();
////            if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && (isSmallContainerCurrentlyShown() || MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean())) {
//            if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
////                Container small = new Container();
////                buildContentPaneSmall(form, small); //refresh the small container
//                Container small = buildContentPaneSmall(myForm); //refresh the small container
//                if (addSmallTimerWindowIfTimerIsRunning(myForm, small)) {
//                    if (false) {
//                        setSmallContainer(small); //if successfully added, then save it
//                    }
//                }
//                myForm.refreshAfterEdit(); //always removeFromCache, e.g. to update for Done tasks
//            } else { //show full screen timer
////                Form form = Display.getInstance().getCurrent();
////                if (form instanceof MyForm || isReplayInProgress) {
////                if (form instanceof MyForm) {
//                if (false) {
//                    resetTimerSmallContainer();
//                }
////                    new ScreenTimer6((MyForm) form, timerInstance).show();
//                new ScreenTimer6(myForm, timerInstance).show();
////                }
//            }
//        }
//    }
//</editor-fold>
    //**************** BUILD THE UI ****************
//    protected static Container buildContentPaneFullScreen(Form form, Container contentPane, SaveEditedValuesLocally formPreviousValues) {
    //**************** BUILD THE UI ****************
//    protected static Container buildContentPaneFullScreen(Form form, Container contentPane, SaveEditedValuesLocally formPreviousValues) {
    protected static Container buildContentPaneFullScreen(MyForm myForm, SaveEditedValuesLocally formPreviousValues) {
//       return buildContentPane(form, contentPane, true, formPreviousValues);
        return buildContentPaneN(myForm, true, formPreviousValues);
    }

//    protected static Container buildContentPaneSmall(Form form, Container contentPane) {
    protected static Container buildContentPaneSmall(MyForm myForm) {
//        return buildContentPane(form, contentPane, false, null);
        return buildContentPaneN(myForm, false, null);
    }

    /**
     * build the user interface of the Timer
    
    @param myForm
    @param fullScreenTimer
    @param formPreviousValues
    @return null if no active timer
     */
//    private static Container buildContentPane(Form form, Container contentPane, boolean fullScreenTimer, SaveEditedValuesLocally formPreviousValues) {
    private static Container buildContentPaneN(MyForm myForm, boolean fullScreenTimer, SaveEditedValuesLocally formPreviousValues) {

        ASSERT.that(myForm != null, "form cannot be null since it's needed for the UITimer");
//        contentPane.removeAll();
//        Container contentPane = fullScreenTimer?new Container(BoxLayout.y()):new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
//        contentPane.setLayout(fullScreenTimer ? BoxLayout.y() : BorderLayout.center());
//        contentPane.setLayout(BoxLayout.y());

//        assert currEntry != null : "entry must always be defined";
        TimerInstance timerInstance = TimerStack.getInstance().getCurrentTimerInstanceN();
        if (timerInstance == null) {
            return null;
        }

//        Item timedItem = timerInstance.getTimedItemN(); //currEntry.timedItem;
        Item timedItem = TimerStack.getInstance().getTimedItemN(); //currEntry.timedItem;

        if (timedItem == null) {
            return null;
        }

        ItemList itemList = timerInstance.getItemList();

        Button elapsedTimeButton = new Button("", (fullScreenTimer ? "BigTimerTimer" : "SmallTimerTimer"));
        MyTextField description;

        MyCheckBox status = new MyCheckBox(timedItem.getStatus());
        status.setUIID("BigTimerItemStatus");

//    private Button status;
//        MyTextArea comment = new MyTextArea(Item.COMMENT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
        MyTextField comment = new MyTextField(Item.COMMENT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
        comment.setUIID("BigTimerComment");
//    private Container commentCont;
        MyDurationPicker effort = new MyDurationPicker();
        effort.setUIID("BigTimerEffort");
        MyDurationPicker estimate = new MyDurationPicker();
        estimate.setUIID("BigTimerEstimate");
        Label totalActualEffort = new Label();
        totalActualEffort.setUIID("BigTimerTotalActual");
        Button editItemButton;
        Picker hiddenElapsedTimePicker = new MyDurationPicker();
        hiddenElapsedTimePicker.setHidden(true); //hiddenElapsedTimePicker must be added to a Form, but we don't want to show it, only activate it via a longpress on the timer button
        MyButtonLongPress timerStartStopButton = new MyButtonLongPress() {
            int maxWidthSoFar = 0;

            @Override
            public Dimension getPreferredSize() {
                Dimension newDimension = super.getPreferredSize();
                maxWidthSoFar = Math.max(maxWidthSoFar, newDimension.getWidth());
                newDimension.setWidth(maxWidthSoFar);
                return newDimension;
            }
        };

//        };
        //TODO!!!!! longpress should activate the duration picker 
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            timerStartStopButton.setCommand(Command.create(null, null, (e) -> {
//                Picker elapsedTimePicker = new Picker();
//                elapsedTimePicker.setHidden(true);
////            elapsedTimeButton.getParent().add(elapsedTimePicker); //must belong to a form
//                timerStartStopButton.getParent().add(elapsedTimePicker); //must belong to a form
//                elapsedTimePicker.setType(Display.PICKER_TYPE_TIME);
////            elapsedTimePicker.setHidden(true);
//                elapsedTimePicker.setTime((int) timerInstance.getElapsedTimeToDisplay() / MyDate.MINUTE_IN_MILLISECONDS);
//                elapsedTimePicker.addActionListener((ev) -> {
//                    timerInstance.setElapsedTime(elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS);
//                    refreshTotalActualEffort.actionPerformed(null);
//                    refreshElapsedTime.actionPerformed(null);
//                    if (false) {
//                        elapsedTimePicker.stopEditing(null); //probably the actionListener above is enough
//                    }
//                    elapsedTimePicker.getParent().removeComponent(elapsedTimePicker); //remove from form again
//
//                });
//                elapsedTimePicker.startEditingAsync();
//            }));
//        }
//</editor-fold>
        SpanButton gotoNextTaskButtonWithItemText = null;
//        Picker elapsedTimePicker;
//        UITimer timer;
//        UITimer buzzerTimer;
        Map<Object, MyForm.UpdateField> parseIdMap2 = new HashMap<Object, MyForm.UpdateField>(); //create a new hashmap for this item

        ActionListener refreshTotalActualEffort = (e) -> {
//            long totalEffort = MyPrefs.timerShowTotalActualInTimer.getBoolean()
//                    ? timerInstance.getElapsedTime()
//                    : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself();
            long totalEffort = timerInstance.getElapsedTotalTime();
            totalActualEffort.setText(MyDate.formatDurationStd(totalEffort, MyPrefs.durationPickerShowSecondsIfLessThan1Minute.getBoolean())); //false=don't show seconds in Total
//            totalActualEffort.repaint();
            totalActualEffort.repaint();
//            totalActualEffort.getParent().revalidate();
        };

        ActionListener refreshElapsedTime = (e) -> {
//            elapsedTimeButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
//            elapsedTimeButton.repaint(); //this is enough to update the value on the screen
//            timerStartStopButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
            timerStartStopButton.setText(MyDate.formatDurationStd(timerInstance.getElapsedTimeToDisplay(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
//            timerStartStopButton.repaint(); //this is enough to update the value on the screen
//            timerStartStopButton.getParent().repaint(); //this is enough to update the value on the screen

            if (timerStartStopButton.getParent() != null) {
//                timerStartStopButton.getParent().revalidate();//this is enough to update the value on the screen
                timerStartStopButton.getParent().revalidateWithAnimationSafety();//this is enough to update the value on the screen
            }
        };

        timerStartStopButton.setLongPressCommand(Command.create("", null, ev -> {//));{
//            @Override
//            public void longPointerPress(int x, int y) {
//                if (false) {
//                    super.longPointerPress(x, y); //seems calling super.longPointerPress will start/stop the timer on longPress
//                }
            hiddenElapsedTimePicker.setDuration(timerInstance.getElapsedTimeToDisplay()); //must edit total elapsed time since that is what is shown
            hiddenElapsedTimePicker.startEditingAsync();
            hiddenElapsedTimePicker.addActionListener((e) -> {
                TimerInstance timerInstance2 = TimerStack.getInstance().getCurrentTimerInstanceN();
                //UI: OK to longPress a running timer (no need to stop it, will simply adjust timer on Done
                if (timerInstance2 != null) {

                    long oldElapsedTime = timerInstance2.getElapsedTime();
                    long oldElapsedTotalDisplayedTime = timerInstance2.getElapsedTimeToDisplay();

                    if (MyPrefs.timerShowTotalActualInTimer.getBoolean()) {
                        //adjust elapsed time
                        long editedElapsedTime = hiddenElapsedTimePicker.getDuration();
                        long diff = editedElapsedTime - oldElapsedTotalDisplayedTime; //NB can also be negative if timer was increased
                        long newElapsedTime = Math.max(0, oldElapsedTime + diff);
//                        timerInstance2.setElapsedTime(newElapsedTime);
                        timerInstance2.updateElapsedTime(newElapsedTime);

                        //since we have edited the total (elapsed+old actual) we may also need to reduce the old actual 
//                            long itemActualReduction = -diff > oldElapsedTime ? -diff - oldElapsedTime : 0;
                        long itemActualReduction = oldElapsedTime + diff < 0 ? -(oldElapsedTime + diff) : 0;
                        if (itemActualReduction > 0) {
                            long itemOldActual = timedItem.getActualForProjectTaskItself();
                            long itemNewActual = itemOldActual - itemActualReduction;
                            timedItem.setActual(itemNewActual, false);
                        }
                    } else { //enough to just adjust the elapsed time
                        long editedElapsedTime = hiddenElapsedTimePicker.getDuration();
//                        timerInstance2.setElapsedTime(editedElapsedTime);
                        timerInstance2.updateElapsedTime(editedElapsedTime);
                    }
                }
                hiddenElapsedTimePicker.stopEditing(null);
                refreshElapsedTime.actionPerformed(null);
                refreshTotalActualEffort.actionPerformed(null);
            });
//                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
        }));

        Runnable updateTimerDisplayForCurrentForm = () -> {
//                new Runnable() {   public void run() {
            // refreshDisplayedTimerInfo();  called on regular updates of the timer screen, as well as on app relaunch
            // setTaskStatusOngoingWhenMinimumThresholdPassed();
            if (Display.getInstance().getCurrent() != myForm) {
                return; //only update for the displayed form
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (status.getStatus() == ItemStatus.CREATED //DON'T revert eg cancelled/done/waiting task to ongoing just because time is spent on it
//                    //                    && timerInstance.getElapsedTime() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
//                    && timerInstance.getElapsedTimeToDisplay() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
//                //UI: It is OK to start timer on a completed task, it will simply add more time to actual
//                ASSERT.that(timedItem.getStatus() == ItemStatus.CREATED, "timedItem.status is CREATED, while status.getStatus()=" + status.getStatus());
//                status.setStatus(ItemStatus.ONGOING, false); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)
//                timedItem.setStatus(ItemStatus.ONGOING);
//                status.repaint(); //update UI
//                parseIdMap2.put("SET_ITEM_STARTED_ON_DATE", () -> timedItem.setStartedOnDate(System.currentTimeMillis()
//                        - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds
//                                .getInt() * MyDate.SECOND_IN_MILLISECONDS
//                ));
//            }
//</editor-fold>
            //UI: It is OK to start timer on a completed task, it will simply add more time to actual
            if (status.getStatus() == ItemStatus.CREATED //DON'T revert eg cancelled/done/waiting task to ongoing just because time is spent on it
                    //use getElapsedTotalTime since we don't want to change only because elpased itself gets bigger than threshold
                    && timerInstance.getElapsedTotalTime()
                    >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
                ASSERT.that(timedItem.getStatus() == ItemStatus.CREATED,
                        "timedItem.status is " + timedItem.getStatus() + " while status.getStatus()=" + status.getStatus());
                status.setStatus(ItemStatus.ONGOING, false); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)   
                timedItem.setStatus(ItemStatus.ONGOING);
                if (timedItem.getStartedOnDateD().getTime() == 0) //UI: in Timer, startedOnDate is set to when timer was started on the task, but only if the delay expired (if setting active)
                    timedItem.setStartedOnDate(MyDate.currentTimeMillis() - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS);
                DAO.getInstance().saveInBackground(timedItem); //ongoing value
                refreshScreenOnTimerUpdate(); //refresh screen to show Ongoing + it's possible impact on the mother project
                status.repaint(); //update UI
//                parseIdMap2.put("SET_ITEM_STARTED_ON_DATE",
//                        () -> timedItem.setStartedOnDate(MyDate.currentTimeMillis() - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS));
            }
//            elapsedTimeButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
//            elapsedTimeButton.repaint(); //this is enough to update the value on the screen
            refreshElapsedTime.actionPerformed(null);
            refreshTotalActualEffort.actionPerformed(null);
//            Log.p("RefreshTimer");
        };

        final int BUZZER_DURATION = 300;
        MyUITimer timerTimer = new MyUITimer(updateTimerDisplayForCurrentForm);
        MyUITimer buzzerTimer = new MyUITimer(() -> {
            if (Display.getInstance().getCurrent() != myForm) {
                return; //only update for the displayed form
            }
            Display.getInstance().vibrate(BUZZER_DURATION);
            Log.p("Buzz");
//<editor-fold defaultstate="collapsed" desc="comment">
//                    try {
//                        wait(BUZZER_PAUSE);
//                        Util.wait(this, BUZZER_PAUSE);
//                    } catch (InterruptedException ex) {
//                        Log.e(ex);
//                    }
//                        Display.getInstance().vibrate(BUZZER_DURATION);
//</editor-fold>
        });

        ActionListener stopUITimers
                = (e) -> {
                    if (timerTimer != null && timerTimer.isScheduled()) {
                        timerTimer.cancel(); //the timers may have been stopped manually when calling the commands
                    }
                    if (buzzerTimer != null && buzzerTimer.isScheduled()) {
                        buzzerTimer.cancel();
                    }
                };
        //<editor-fold defaultstate="collapsed" desc="comment">
        //            elapsedTimeButton = new Button("TimerTimer");
        //        ActionListener stopTimers = (e) -> {
        //            if (timerTimer!=null) timerTimer.cancel(); //stop timer as first thing
        ////                if (buzzerTimer != null) { //buzzer is optional
        ////                if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //buzzer is optional, only cancel if actually started (otherwise nullpointerexception
        //            if (buzzerTimer!=null) buzzerTimer.cancel();
        ////                }
        //        };
        //        timerContainer.setLeadComponent(elapsedTimePicker); //ensure that the button behaves like a Picker
        //        timerContainer.add(elapsedTimeButton, elapsedTimePicker);
        //        elapsedTimePicker.setUIID("TimerTimer");
        //        elapsedTimeButton.setCommand(Command.create(null, null, (e) -> {
        //        timerStartStopButton.setCommand(Command.create(null, null, (e) -> {
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="comment">
        //        parseIdMap2.put("ElapsedTime", () -> {
        ////            timedItem.setActualEffort(timerInstance.isTimerShowActualTotal()
        ////                    ? timerInstance.getElapsedTime()
        ////                    : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself());
        ////            timedItem.setActualEffort(timerInstance.getElapsedTimeToDisplay());
        //            timedItem.setActualEffort(timedItem.getActualEffortProjectTaskItself() + timerInstance.getElapsedTime());
        //        });
        //        SaveEditedValuesLocally previousValues = formPreviousValues != null ? formPreviousValues : new SaveEditedValuesLocally("Timer-" + timedItem.getObjectIdP());
        //        ActionListener saveEditedItem = (evt) -> {
        //            MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
        //            DAO.getInstance().saveInBackground(timedItem);
        //            previousValues.deleteFile();
        //        };
        //</editor-fold>
        if (Display.getInstance().isScreenSaverDisableSupported()) {
            Display.getInstance().setScreenSaverEnabled(!MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer));
        }

        // ********************************* COMMANDS **************************
        //Cmd used to show next task and go to it when clicked
        Command cmdStartNextTask = new CommandTracked("", null, "TimerCmdStartNextTask") { //"StartNextTask" - stop and save current task and move to next (autostart if set)
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);
//<editor-fold defaultstate="collapsed" desc="comment">
//                timerInstance.stopTimer(true);                //stop this timer, save item, //DON'T change status (is normally ONGOING)
//                timerInstance.stopTimerUpdateTimedTaskActualsAndSave(true); //stop this timer, save item, //DON'T change status (is normally ONGOING)
////<editor-fold defaultstate="collapsed" desc="comment">
////                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
////                DAO.getInstance().saveInBackground(timedItem);
////                previousValues.deleteFile();
////                if (timerInstance.isAutoGotoNextTask()) { //
////                TimerStack.getInstance().goToNextTimedItem();
////                getInstance().refreshOrShowTimerUI();
////</editor-fold>
//                TimerStack.getInstance().moveToNextTask();
//                TimerStack.getInstance().refreshOrShowTimerUI();
//                refreshScreenOnTimerUpdate();

//</editor-fold>
//                TimerStack.getInstance().gotoNext(timerInstance);
                controller.gotoNext(timerInstance);
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (TimerStack.getInstance().getCurrentTimerInstanceN() != null) {
//                    buildContentPane(contentPane, fullScreenTimer, previousValues); //rebuild
//                    contentPane.getParent().revalidate();
//                    contentPane.getComponentForm().animateLayout(300);
//                } else {
//                    removeTimerSmallContainer(contentPane);
//                }
//</editor-fold>
            }
        };

        /**
         * update/save current task (ask if update Remaining) and either move to
         * next task, and start the timer, or exit if no more tasks
         */
//        Command cmdStopTimerAndGotoNextTaskOrExit = new Command("Next", Icons.iconCheckboxOngoing) {
//        Command cmdStopTimerAndGotoNextTaskOrExit = new Command("Next", Icons.iconCheckboxOngoing) {
        Command cmdStopTimerAndGotoNextTaskOrExit = new CommandTracked("Next", Icons.iconTimerNextTask, "TimerCmdStopTimerAndGotoNextTaskOrExit") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);
//<editor-fold defaultstate="collapsed" desc="comment">
////                timerInstance.stopTimer(true);                //stop this timer, save item,
//                timerInstance.stopTimerUpdateTimedTaskActualsAndSave(true);
////<editor-fold defaultstate="collapsed" desc="comment">
////                status.setStatus(ItemStatus.DONE, false);
//
////                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
////                DAO.getInstance().saveInBackground(timedItem);
////                previousValues.deleteFile();
////                if (timerInstance.isAutoGotoNextTask()) {
//////                    cmdStartNextTask.actionPerformed(null);
////                    TimerStack.getInstance().goToNextTimedItem();
////                }
////                getInstance().refreshOrShowTimerUI();
////</editor-fold>
////                if (!TimerStack.getInstance().moveToNextTask()) {
////                    ((MyForm) Display.getInstance().getCurrent()).showPreviousScreenOrDefault(true); //exit if no more tasks
////                };
//                TimerStack.getInstance().moveToNextTask();
//                TimerStack.getInstance().refreshOrShowTimerUI();
//                refreshScreenOnTimerUpdate();
//</editor-fold>
//                TimerStackModel.getInstance().gotoNext(timerInstance);
                controller.gotoNext(timerInstance);
            }
        };

        /**
        exit large Timer
         */
        Command cmdSaveAndExitTimerScreen = new CommandTracked("Stop", Icons.iconTimerStopExitTimer, "TimerCmdSaveAndExitTimerScreen") { //"Stop/Exit" "Close/Exit" //TODO select icon for Exit from timer
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);

                //exit timer altogether: save each timed item, pop/delete all timers
//<editor-fold defaultstate="collapsed" desc="comment">
//                while (TimerStack.getInstance().activeTimers.size() > 0) {
//                    TimerInstance timerInstance = TimerStack.getInstance().activeTimers.remove(TimerStack.getInstance().activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//                    timerInstance.stopTimerUpdateTimedTaskActualsAndSave(false); //no save since deleted below
//                    timerInstance.deleteInstance();
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                showPreviousScreenOrDefault(((MyForm) Display.getInstance().getCurrent()).previousForm, true);
////                ((MyForm) Display.getInstance().getCurrent()).showPreviousScreenOrDefault(true);
////               TimerStack.getInstance().moveToNextTask();
////</editor-fold>
//                TimerStack.getInstance().refreshOrShowTimerUI();
//                refreshScreenOnTimerUpdate();
//</editor-fold>
                //necessary to refresh current form when exiting (small)Timer?
//                TimerStackModel.getInstance().exitTimer();
                controller.exitTimer();
                refreshScreenOnTimerUpdate(); //necessary to refresh current form when exiting (small)Timer?

            }
        };

        /**
        exit small Timer
         */
        Command cmdSaveAndExitSmallTimer = new CommandTracked("Exit", Icons.iconTimerStopExitTimer, "TimerCmdSaveAndExitSmallTimer") { //"Stop/Exit" "Close/Exit" //TODO select icon for Exit from timer
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);
                //exit timer altogether: save each timed item, pop/delete all timers
//<editor-fold defaultstate="collapsed" desc="comment">
//                while (TimerStack.getInstance().activeTimers.size() > 0) {
//                    TimerInstance timerInstance = TimerStack.getInstance().activeTimers.remove(TimerStack.getInstance().activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//                    timerInstance.stopTimerUpdateTimedTaskActualsAndSave(false); //no save since deleted below
//                    timerInstance.deleteInstance();
//                }
////                TimerStack.getInstance().removeTimerSmallContainer();
//                TimerStack.getInstance().refreshOrShowTimerUI();
//                refreshScreenOnTimerUpdate(); //necessary to refresh current form when exiting (small)Timer?
//</editor-fold>
//                TimerStackModel.getInstance().exitTimer();
                controller.exitTimer();
            }
        };

        Command cmdSetCompletedAndGotoNextTaskOrExit = new CommandTracked("Completed", Icons.iconItemStatusDone, "TimerCmdCompletedAndGotoNextTaskOrExit") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);

                status.setStatus(ItemStatus.DONE, false); //update UI in case timer doesn't move to next item
//<editor-fold defaultstate="collapsed" desc="comment">
//                timerInstance.stopTimer(true);                //stop this timer, save item,
//                timedItem.setStatus(status.getStatus());
////<editor-fold defaultstate="collapsed" desc="comment">
////                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
////                DAO.getInstance().saveInBackground(timedItem);
////                previousValues.deleteFile();
////                if (timerInstance.isAutoGotoNextTask()) {
//////                    cmdStartNextTask.actionPerformed(null);
////                    TimerStack.getInstance().goToNextTimedItem();
////                }
////                getInstance().refreshOrShowTimerUI();
////</editor-fold>
////                if (!TimerStack.getInstance().moveToNextTask()) {
////                    ((MyForm) Display.getInstance().getCurrent()).showPreviousScreenOrDefault(true); //exit if no more tasks
////                };
//                timerInstance.stopTimerUpdateTimedTaskActualsAndSave(true);
//                TimerStack.getInstance().moveToNextTask();
//                TimerStack.getInstance().refreshOrShowTimerUI();
//                refreshScreenOnTimerUpdate();
//</editor-fold>
//                TimerStackModel.getInstance().gotoNext(timerInstance, timedItem, ItemStatus.DONE);
                controller.gotoNext(timerInstance, timedItem, ItemStatus.DONE);
                controller.taskCompleted(timerInstance, timedItem, ItemStatus.DONE);
            }
        };

        Command cmdSetTaskWaitingAndGotoNextTaskOrExit = new CommandTracked("Wait", Icons.iconItemStatusWaiting, "TimerCmdSetTaskWaitingAndGotoNextTaskOrExit") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);

                status.setStatus(ItemStatus.WAITING, false); //move to controller
                timerInstance.stopTimer(true); //move to controller
                MyForm.showDialogUpdateRemainingTime(effort); //move to controller
//<editor-fold defaultstate="collapsed" desc="comment">
//                timedItem.setStatus(status.getStatus());
//                timerInstance.stopTimerUpdateTimedTaskActualsAndSave(true);
////<editor-fold defaultstate="collapsed" desc="comment">
////                MyForm.putEditedValues2(parseIdMap2); //update item with edited/changed values
////                DAO.getInstance().saveInBackground(timedItem);
////                previousValues.deleteFile();
////                if (timerInstance.isAutoGotoNextTask()) {
//////                    cmdStartNextTask.actionPerformed(null);
////                    TimerStack.getInstance().goToNextTimedItem();
////                }
////                getInstance().refreshOrShowTimerUI();
////</editor-fold>
////                if (!TimerStack.getInstance().moveToNextTask()) {
////                    ((MyForm) Display.getInstance().getCurrent()).showPreviousScreenOrDefault(true); //exit if no more tasks
////                };
//                TimerStack.getInstance().moveToNextTask();
//                TimerStack.getInstance().refreshOrShowTimerUI();
//                refreshScreenOnTimerUpdate();
//</editor-fold>
//                TimerStackModel.getInstance().gotoNext(timerInstance, timedItem, ItemStatus.WAITING);
                model.gotoNext(timerInstance, timedItem, ItemStatus.WAITING); //remove timerInstance and timedItem since controller should know them
            }
        };

        Command cmdSetTaskCancelledAndGotoNextTaskOrExit = new CommandTracked("Cancel", Icons.iconItemStatusCancelled, "TimerCmdTaskCancelledAndGotoNextTaskOrExit") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);
                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.CANCELLED, false);
//<editor-fold defaultstate="collapsed" desc="comment">
//                timedItem.setStatus(status.getStatus());
//                timerInstance.stopTimerUpdateTimedTaskActualsAndSave(true);
////<editor-fold defaultstate="collapsed" desc="comment">
////                MyForm.putEditedValues2(parseIdMap2); //update item with edited/changed values
////                DAO.getInstance().saveInBackground(timedItem);
////                previousValues.deleteFile();
////                if (timerInstance.isAutoGotoNextTask()) {
//////                    cmdStartNextTask.actionPerformed(null);
////                    TimerStack.getInstance().goToNextTimedItem();
////                }
////                getInstance().refreshOrShowTimerUI();
////</editor-fold>
////                if (!TimerStack.getInstance().moveToNextTask()) {
////                    ((MyForm) Display.getInstance().getCurrent()).showPreviousScreenOrDefault(true); //exit if no more tasks
////                };
//                TimerStack.getInstance().moveToNextTask();
//                TimerStack.getInstance().refreshOrShowTimerUI();
//                refreshScreenOnTimerUpdate();
//</editor-fold>
//                TimerStackModel.getInstance().gotoNext(timerInstance, timedItem, ItemStatus.WAITING);
                controller.taskCancelled(timerInstance, timedItem, ItemStatus.CANCELLED);
            }
        };

        Command cmdSetTaskOngoingAndGotoNextTaskOrExit = new CommandTracked("Ongoing**", Icons.iconItemStatusOngoing, "TimerCmdSetTaskOngoingAndGotoNextTaskOrExit") {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                timerTimer.cancel();
//                buzzerTimer.cancel();
                stopUITimers.actionPerformed(null);
                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.ONGOING, false);
//<editor-fold defaultstate="collapsed" desc="comment">
//                timedItem.setStatus(status.getStatus());
//                timerInstance.stopTimerUpdateTimedTaskActualsAndSave(false);
////<editor-fold defaultstate="collapsed" desc="comment">
////                MyForm.putEditedValues2(parseIdMap2); //update item with edited/changed values
////                DAO.getInstance().saveInBackground(timedItem);
////                previousValues.deleteFile();
////                if (timerInstance.isAutoGotoNextTask()) {
//////                    cmdStartNextTask.actionPerformed(null);
////                    TimerStack.getInstance().goToNextTimedItem();
////                }
////                getInstance().refreshOrShowTimerUI();
////</editor-fold>
////                if (!TimerStack.getInstance().moveToNextTask()) {
////                    ((MyForm) Display.getInstance().getCurrent()).showPreviousScreenOrDefault(true); //exit if no more tasks
////                };
//                TimerStack.getInstance().moveToNextTask();
//                TimerStack.getInstance().refreshOrShowTimerUI();
//                refreshScreenOnTimerUpdate();
//</editor-fold>
//                TimerStackModel.getInstance().gotoNext(timerInstance, timedItem, ItemStatus.WAITING);
                controller.setOngoing(timerInstance, timedItem, ItemStatus.WAITING);

            }
        };

//Show next tasks
        //TODO optimization: only construct nextTask containers etc if there is one and it is shown
        //TODO show button to select auto-start Timer on task or not
//        Item nextItem = TimerStack.getInstance().findNextTimedItem();
//        Item nextItem = TimerStack.getInstance().getTimedItemN();
        Item nextComingItem = TimerStack.getInstance().getTheNextComingTimedItem();
        if (nextComingItem != null) {
//            return null;
            gotoNextTaskButtonWithItemText = MyPrefs.timerShowNextTask.getBoolean()
                    ? new SpanButton("Next: \"" + nextComingItem.getText() + "\"" //                            + (MyPrefs.timerShowRemainingForNextTask.getBoolean() ? (" [" + MyDate.formatTimeDuration(nextComingItem.getRemainingEffort()) + "]") : ""))
                            + (MyPrefs.timerShowRemainingForNextTask.getBoolean() ? (" [" + MyDate.formatDurationShort(nextComingItem.getRemainingForProjectTaskItself(true)) + "]") : ""))
                    : new SpanButton(""); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
            gotoNextTaskButtonWithItemText.setCommand(cmdStartNextTask);
            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
//                gotoNextTaskButtonWithItemText.setUIID("Label");
                gotoNextTaskButtonWithItemText.setTextUIID("BigTimerNextItemWithText");
            }
            gotoNextTaskButtonWithItemText.setHidden(!MyPrefs.timerShowNextTask.getBoolean());
        }

        Command cmdGotoFullScreenTimer = null;

        if (!fullScreenTimer) {
//                cmdGotoFullScreenTimer = new Command("FullScreenTimer", Icons.iconEditSymbolLabelStyle) {
//            cmdGotoFullScreenTimer = new MyReplayCommand(TIMER_REPLAY, "", Icons.iconEditSymbolLabelStyle) {
//            cmdGotoFullScreenTimer = MyReplayCommand.create(TIMER_REPLAY, "", Icons.iconEditSymbolLabelStyle, (e) -> {
            cmdGotoFullScreenTimer = CommandTracked.create("", Icons.iconEditSymbolLabelStyle, (e) -> {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
                //save edited values //TODO!!!!
//                        new ScreenTimer6((MyForm) contentPane.getComponentForm(), timerInstance).show();
                timerInstance.setFullScreen(true);
                timerInstance.saveMe();
                new ScreenTimer6(myForm, timerInstance).show();
            }, "InterruptInScreen" + myForm.getUniqueFormId());
        }

        status.setStatusChangeHandler((oldStatus, newStatus) -> {
            if (newStatus != oldStatus) {
                timedItem.setStatus(status.getStatus()); //must do *before* calling commands, otherwise removeFromCache will show old value!
                switch (newStatus) {
                    case DONE:
                        cmdSetCompletedAndGotoNextTaskOrExit.actionPerformed(null);
                        break;
                    case WAITING:
                        cmdSetTaskWaitingAndGotoNextTaskOrExit.actionPerformed(null);
                        break;
                    case CANCELLED:
//                    processCurrentItemAndLaunchNextOrExit(null, false, true, false, false); //TODO!!! when cancelling a task, should we still store the time stored?
                        cmdSetTaskCancelledAndGotoNextTaskOrExit.actionPerformed(null);
                        break;
                    case ONGOING:
                        //do nothing - should already be set to Ongoing when starting timer //TODO!!! not if long time is set before making 
                        cmdSetTaskOngoingAndGotoNextTaskOrExit.actionPerformed(null);
                    case CREATED:
                    //do nothing - user forces status back to empty checkbox
                }
                //moved from status.actionListener: 
//                timedItem.setStatus(status.getStatus());
                if (false) DAO.getInstance().saveInBackground(timedItem); //done in the commands
            }
        });

//            Container contentPane = fullScreenTimer ? new Container(BoxLayout.y()) : new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
        Container contentPane = fullScreenTimer ? new Container(BoxLayout.y()) : new Container(new MyBorderLayout(MyBorderLayout.CENTER_BEHAVIOR_CENTER));
//<editor-fold defaultstate="collapsed" desc="comment">
//            contentPane.removeAll(); //clear before rebuilding
//            Container cont = new Container(BoxLayout.y());
//            cont.setScrollableY(true);
//            Container contentPane = contentPane;
//            contentPane.add(BorderLayout.CENTER, cont);
//</editor-fold>
        if (fullScreenTimer) {

//                comment = new MyTextArea(Item.COMMENT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
            comment.setSingleLineTextArea(false);
            comment.addActionListener((e) -> {
                timedItem.setComment(comment.getText());
                DAO.getInstance().saveInBackground(timedItem);
            });
//<editor-fold defaultstate="collapsed" desc="comment">
//            UITimer commentSaveTimer = new UITimer(() -> {
//                if (comment.isEditing()) {
//                    timedItem.setComment(comment.getText());
//                    DAO.getInstance().saveInBackground(timedItem);
//                }
//            });
//
//            comment.addDataChangedListener((chgType, index) -> {
//                commentSaveTimer.schedule(5000, false, myForm); ////reschedule a save on each change, will save after 5 seconds // final  int TEXT_AUTOSAVE_TIMEOUT = 5000; //TODO: make this a setting?
//            });
//</editor-fold>
            AutoSaveTimer commentSaveTimer = new AutoSaveTimer(myForm, comment, timedItem, 5000, () -> timedItem.setComment(comment.getText()));

            //find and show hierarchy context for the item (List to which it belongs, and if a subtask, the (hierarchical) project context
            String listName = null; //"TEMP - Name of the list";

            if (itemList != null) {
                listName = itemList instanceof Category ? "Category: " : "List: " + itemList.getText(); //source is an ItemList 
            } else if (timedItem.isInteruptOrInstantTask()) {
                listName = timedItem.getTaskInterrupted() == null ? "INSTANT TASK" : "INTERRUPT TASK";
            } //else: listName remains null, e.g. if only a single task is timed

            String hierarchyStr = timedItem.getOwnerHierarchyAsString();

            if (listName == null) { //if no list, nor interrupt task, use project hierarchy as top-level context
                listName = hierarchyStr;
                hierarchyStr = null;
            }

            if (hierarchyStr != null) {
                SpanLabel itemHierarchyContainer = new SpanLabel("Project: " + hierarchyStr, "BigTimerListTitle");
                itemHierarchyContainer.setHidden(!MyPrefs.getBoolean((MyPrefs.timerAlwaysExpandListHierarchy))); //initial state of visibility
                Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
                buttonShowItemHierarchy.addActionListener((e) -> {
                    itemHierarchyContainer.setHidden(!itemHierarchyContainer.isHidden()); //inverse visibility
                    buttonShowItemHierarchy.setIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
                    buttonShowItemHierarchy.getParent().getParent().animateLayout(300);
                });

                contentPane.add(MyBorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName)))
                        .add(MyBorderLayout.EAST, buttonShowItemHierarchy).add(MyBorderLayout.SOUTH, itemHierarchyContainer));

            } else if (listName != null) {
                contentPane.add(MyBorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName))));
            } //else: no context to show, show nothing
//        }
//
//        if (fullScreenTimer) {
            //TODO!!! do NOT use item.isInteruptTask() since we may later continue working on a task that was originally created as an interrupt but after that is just treated as a normal task
            description = new MyTextField(Item.DESCRIPTION_HINT, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY) {
                @Override
                public void longPointerPress(int x, int y) {
                    Log.p("longPointerPress on Timer text area");
                    //TODO!!! call "Regular tasks"
                }
            };
            description.setUIID("BigTimerItemText");
            description.setColumns(100);
            description.setGrowByContent(true);
            description.setActAsLabel(true);
//                MyForm.initField(Item.PARSE_TEXT, description, () -> timedItem.getText(),
//                        (t) -> timedItem.setText((String) t), () -> description.getText(), (t) -> description.setText((String) t), previousValues, parseIdMap2);
//            description.addActionListener((e) -> {
//                timedItem.setText(description.getText());
//                DAO.getInstance().saveInBackground(timedItem);
//            });

            if (timedItem.isInteruptOrInstantTask() && description.getText().equals("")) {
//                    contentPane.getComponentForm().setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
                myForm.setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
            }
            description.setText(timedItem.getText());
        } else { //smallTimer
            description = new MyTextField(Item.DESCRIPTION_HINT, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY);
            description.setSingleLineTextArea(false);
            description.setColumns(100);
            description.setActAsLabel(true);
//            description.setUIID("Label");
            description.setUIID("SmallTimerItemText");
            description.setCommitTimeout(300);
            description.setEditable(true); //true=editable (but will look like a label until clicked), false=not editable in small container
            description.setText(timedItem.getText());
            if (timedItem.isInteruptOrInstantTask() && description.getText().equals("")) {
//                    contentPane.getComponentForm().setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
                myForm.setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
            }
        }

        description.addActionListener((e) -> {
            timedItem.setText(description.getText());
            DAO.getInstance().saveInBackground(timedItem);
        });
        description.setDoneListener((e) -> {
            timedItem.setText(description.getText());
            DAO.getInstance().saveInBackground(timedItem);
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//        UITimer descriptionSaveTimer = new UITimer(() -> {
//            if (description.isEditing()) {
//                timedItem.setText(description.getText());
//                DAO.getInstance().saveInBackground(timedItem);
//            }
//        });
//        description.addDataChangedListener((chgType, index) -> {
//            descriptionSaveTimer.schedule(5000, false, myForm); ////reschedule a save on each change, will save after 5 seconds // final  int TEXT_AUTOSAVE_TIMEOUT = 5000; //TODO: make this a setting?
//        });
//</editor-fold>
        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, description, timedItem, 5000, () -> timedItem.setText(description.getText()));

//            MyForm.initField(Item.PARSE_STATUS, status, () -> timedItem.getStatus(), (t) -> timedItem.setStatus((ItemStatus) t),
//                    () -> status.getStatus(), (t) -> status.setStatus((ItemStatus) t), previousValues, parseIdMap2);
        if (false) {
            status.addActionListener((e) -> { //is NOT called when status Button's text is changed! BUT when clicked, so must keep!
                timedItem.setStatus(status.getStatus());
                DAO.getInstance().saveInBackground(timedItem);
            });
        }
        editItemButton = new Button(MyReplayCommand.create("EditItemFromTimer-", timedItem.getObjectIdP(), "", Icons.iconEditSymbolLabelStyle,
                (e) -> {
//                MyForm.putEditedValues2(parseIdMap2, timedItem); //first update Item with any values changed in Timer
//                ScreenItem2 screenItem = new ScreenItem2(timedItem, (MyForm) contentPane.getComponentForm(), () -> {
//                    long oldActual = timedItem.getActualForProjectTaskItself();
                    ScreenItem2 screenItem = new ScreenItem2(timedItem, (MyForm) myForm, () -> {
                        //TODO!!!!!! if item values like description or comment were edited in Timer, they must be shown when editing the item. Simply pass previousValues?!
//                        DAO.getInstance().saveInBackground(timedItem); //done below
                        description.setText(timedItem.getText());
                        status.setStatus(timedItem.getStatus());
                        comment.setText(timedItem.getComment());
//                        effortEstimate.setTime((int) timedItem.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS);
                        estimate.setDuration(timedItem.getEstimate());
//                remainingEffort.setTime((int) timedItem.getRemainingEffort(false, false) / MyDate.MINUTE_IN_MILLISECONDS); //don't use 0 for done tasks (if we time a Done task, want to see actual value stored in Remaining)
//                        remainingEffort.setTime((int) timedItem.getRemainingEffortProjectTaskItself() / MyDate.MINUTE_IN_MILLISECONDS); //don't use 0 for done tasks (if we time a Done task, want to see actual value stored in Remaining)
                        effort.setDuration((int) timedItem.getRemainingForProjectTaskItselfFromParse()); //don't use 0 for done tasks (if we time a Done task, want to see actual value stored in Remaining)

                        //update actual and elapsed to match new value of actual, editedActual==newElapsed+item.newActual (principle: if possible, keep elapsed the same and reduce item.actual (unless edited actual is smaller then elapsed, then reduce that as well)
                        long newActual = timedItem.getActualForProjectTaskItself();
//                        if (newActual != oldActual) { //test to avoid unnecessary saving (again) of timedItem if actual does not need to be updated
                        TimerInstance timerInst = getInstance().getCurrentTimerInstanceN();
                        if (newActual < timerInst.getElapsedTime()) { //new edited value is smaller than already elapsed time, so reduce elapsed to new value, and set item.Actual to 0 
//                            timerInst.setElapsedTime(newActual);
                            timerInst.updateElapsedTime(newActual);
                            timedItem.setActual(0, false);
                            timerInst.saveMe();
                        } else {
                            //no change to elapsed time
                            timedItem.setActual(newActual - timerInst.getElapsedTime(), false); //update item.actual to edited value minus the elapsed time
                        }

                        DAO.getInstance().saveInBackground(timedItem); //save updated values
//                        }

                        refreshTotalActualEffort.actionPerformed(null); //refresh screen
//                ScreenTimer6.this.revalidate();
//                    ((MyForm) contentPane.getComponentForm()).revalidate();
//                    ((MyForm) form).revalidate();
                        ((MyForm) myForm).revalidateWithAnimationSafety();
                    }, false, null); //previousValues: pass locally edited value to ScreenItem
                    screenItem.show();
                }
        ));
        if (fullScreenTimer) {
            contentPane.add(MyBorderLayout.west(status).add(MyBorderLayout.CENTER, description).add(MyBorderLayout.EAST, editItemButton));
        }

        ActionListener startFormUpdateTimers = (e) -> {
            ASSERT.that(myForm != null);
            timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, myForm); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
//                if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //Start Buzzer
////                    buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, contentPane.getComponentForm());
//                    buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, form);
//                }
            //TODO!!! find better solution than activating the buzzerTimer with Integer.MAX
            buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt() != 0 ? MyPrefs.timerBuzzerInterval.getInt() * MyDate.MINUTE_IN_MILLISECONDS : Integer.MAX_VALUE, true, myForm); //Integer.MAX_VALUE=25days so little risk of unexpceted buzz
        };

//            elapsedTimeButton = new Button("TimerTimer");
        //If go to nexttask
//            timerStartStopButton = new Button();
        Command timerStartStopCmd = Command.create(null, null, (e) -> {
//            if (timer == null) {
            if (!timerInstance.isRunning()) {                    //start Timer
                //UI: It is OK to start timer on a completed task, it will simply add more time to actual
//                    setTaskStatusOngoingWhenMinimumThresholdPassed(); //done when updating the display
//                    timerInstance.startTimer();
                timerInstance.startTimer(true);
                if (false) {
                    elapsedTimeButton.setEnabled(false); //disable while running
                }
                if (false) {
                    timerStartStopButton.setIcon(Icons.iconTimerPauseLabelStyle);
                }
//                    timerStartStopButton.repaint(); //this is enough to update the value on the screen
//                    timerStartStopButton.getParent().revalidate();//this is enough to update the value on the screen
                timerStartStopButton.getParent().revalidateWithAnimationSafety();//this is enough to update the value on the screen

//                    timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, contentPane.getComponentForm()); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
//                    timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, form); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
//                    if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //Start Buzzer
//                        buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, contentPane.getComponentForm());
//                    }
//                    timerStartStopButton.setUIID("TimerTimer" + (fullScreenTimer ? "" : "Small")); //iconTimerStartTimer);
                startFormUpdateTimers.actionPerformed(null);
//    }
            } else {
                timerInstance.stopTimer();
//                    timerTimer.cancel(); //stop timer as first thing
//                    buzzerTimer.cancel();
                stopUITimers.actionPerformed(null);

                if (false) {
                    elapsedTimeButton.setEnabled(true); //enable the picker when timer is paused
                }
                //refresh timer whens topped (since update of display may be a bit behind - otherwise a new timer on a different device may show a different value
                refreshElapsedTime.actionPerformed(null);
                refreshTotalActualEffort.actionPerformed(null);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    timerStartStopButton.setIcon(Icons.iconTimerStartLabelStyle); //iconTimerStartTimer);
//                    timerStartStopButton.setUIID("TimerTimer" + (fullScreenTimer ? "" : "Small") + "Paused"); //iconTimerStartTimer);
//                    timerStartStopButton.repaint(); //this is enough to update the value on the screen? NOPE: doesn't increase the size as hours are added!
//                    timerStartStopButton.getParent().revalidate();
//</editor-fold>
                timerStartStopButton.getParent().revalidateWithAnimationSafety();
            }
//            timerStartStopButton.setUIID((fullScreenTimer ? "BigTimerTimer" : "SmallTimerTimer"));
            timerStartStopButton.setUIID(fullScreenTimer ? (timerInstance.isRunning() ? "BigTimerTimer" : "BigTimerTimerPaused") : (timerInstance.isRunning() ? "SmallTimerTimer" : "SmallTimerTimerPaused")); //update uiid to display running/paused timer appropriately
        });
        timerStartStopButton.setCommand(timerStartStopCmd);
//        timerStartStopButton.setUIID("TimerTimer" + (fullScreenTimer ? "" : "Small") + (timerInstance.isRunning() ? "" : "Paused")); //iconTimerStartTimer);
        timerStartStopButton.setUIID(fullScreenTimer ? (timerInstance.isRunning() ? "BigTimerTimer" : "BigTimerTimerPaused") : (timerInstance.isRunning() ? "SmallTimerTimer" : "SmallTimerTimerPaused")); //update uiid to display running/paused timer appropriately
//            timerStartStopButton.set(); //iconTimerStartTimer);
        refreshElapsedTime.actionPerformed(null); //update timerStartStopButton with initial time

        if (timerInstance.isRunning()) {
//                timerStartStopCmd.actionPerformed(null); //start the UI timer
            startFormUpdateTimers.actionPerformed(null); //start the UI timer
        }

        if (false) {
            timerStartStopButton.setIcon(timerInstance.isRunning() ? Icons.iconTimerPauseLabelStyle : Icons.iconTimerStartLabelStyle);
        }

        if (fullScreenTimer) { //smallContainer
//            remainingEffort = new MyDurationPicker();
            effort.setShowZeroValueAsZeroDuration(true); //show "0:00"
//                MyForm.initField(Item.PARSE_REMAINING_EFFORT, remainingEffort,
//                        () -> timedItem.getRemainingEffort(false), (l) -> timedItem.setRemaining((long) l),
//                        () -> remainingEffort.getDuration(), (l) -> remainingEffort.setDuration((long) l), previousValues, parseIdMap2);
//                effortEstimate = new MyDurationPicker();
            estimate.setShowZeroValueAsZeroDuration(true); //show "0:00"
//                MyForm.initField(Item.PARSE_EFFORT_ESTIMATE, effortEstimate, () -> timedItem.getEffortEstimate(), (l) -> timedItem.setEstimate((long) l),
//                        () -> effortEstimate.getDuration(), (l) -> effortEstimate.setDuration((long) l), previousValues, parseIdMap2);
//            boolean effortEstimateBeingAutoupdated = false;
//            boolean remainingEstimateBeingAutoupdated = false;

            estimate.addActionListener((e) -> {
//                effortEstimateBeingAutoupdated=true;
                timedItem.setEstimate(estimate.getDuration(), false); //saved immediately on edit
                if (timedItem.getRemainingForProjectTaskItselfFromParse() == 0
                        && effort.getDuration() == 0
                        && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean()) {
//                    timedItem.setRemaining(effortEstimate.getDuration(), false); //NB. not necessary because updating the duration picker will trigger the other actionListener
                    timedItem.setRemaining(estimate.getDuration(), false); //saved immediately on edit
                    effort.setDuration(estimate.getDuration());
                    effort.repaint();
                }
                DAO.getInstance().saveInBackground(timedItem);
//                effortEstimateBeingAutoupdated=false;
            });

            effort.addActionListener((e) -> {
//                remainingEstimateBeingAutoupdated=true;
                timedItem.setRemaining(effort.getDuration(), false); //saved immediately on edit
                if (timedItem.getEstimate() == 0 && estimate.getDuration() == 0
                        && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean()) {
//                    timedItem.setEstimate(remainingEffort.getDuration(), false);
                    timedItem.setEstimate(effort.getDuration(), false); //saved immediately on edit
                    estimate.setDuration(effort.getDuration());
                    estimate.repaint();
                }
                DAO.getInstance().saveInBackground(timedItem);
//                remainingEstimateBeingAutoupdated=false;
            });

            estimate.setDuration(timedItem.getEstimate());
//            remainingEffort.setDuration(timedItem.getRemainingEffort());
            effort.setDuration(timedItem.getRemainingForProjectTaskItselfFromParse());

//            totalActualEffort = new Label(); //MyDate.formatTime(calcTotalEffortInMinutes(item, elapsedTimePicker.getTime(), MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), showSeconds), "Button");
            refreshTotalActualEffort.actionPerformed(null);

//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) { //Now done in action listeners above on each field
//                //Automatically update Estimate and Remaining when one of them is set (and no value is defined manually). NB. This will only work for the first one being set.
//                ActionListener estimateAndRemainingUpdater = new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        //update estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
//                        if (remainingEffort.getTime() != 0 && timedItem.getEffortEstimate() == 0 && effortEstimate.getTime() == 0) { //if remaining is changed AND effortEstimate is not...
//                            effortEstimate.setTime(remainingEffort.getTime());
//                            effortEstimate.repaint();
//                        }
//                        //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
//                        if (effortEstimate.getTime() != 0 && timedItem.getRemainingEffort(false) == 0 && remainingEffort.getTime() == 0) {
//                            remainingEffort.setTime(effortEstimate.getTime());
//                            remainingEffort.repaint();
//                        }
//                    }
//                };
//                remainingEffort.addActionListener(estimateAndRemainingUpdater);
//                effortEstimate.addActionListener(estimateAndRemainingUpdater);
//            }
//</editor-fold>
            TableLayout tl = new TableLayout(2, 3);
            final Container estimateTable = new Container(tl);

            Button showEffortDetailsButton = new Button(Icons.iconShowMoreLabelStyle);

            showEffortDetailsButton.addActionListener((e) -> {
                showEffortDetailsButton.setIcon(showEffortDetailsButton
                        .getIcon() == Icons.iconShowMoreLabelStyle
                                ? Icons.iconShowLessLabelStyle
                                : Icons.iconShowMoreLabelStyle);
                estimateTable.setHidden(!estimateTable.isHidden());
                MyPrefs.flipBoolean(MyPrefs.timerShowEffortEstimateDetails);
//                            contentPane.getComponentForm().animateLayout(300);
//                form.animateLayout(300);
                estimateTable.getParent().getParent().animateLayout(300);
            }
            );
//            Container effortDetailsCont =  LayeredLayout.encloseIn(
//                    FlowLayout.encloseRightMiddle(showEffortDetailsButton), //!!: reuse same strings as from ScreenItem!
//                    //                        GridLayout.encloseIn(3, new Label(""), elapsedTimeButton, FlowLayout.encloseIn(timerStartStopButton))
//                    GridLayout.encloseIn(3, new Label(""), timerStartStopButton)
//            );
            Container effortDetailsCont
                    = MyBorderLayout.centerAbsoluteEastWest(timerStartStopButton, showEffortDetailsButton, new Label());
            contentPane.add(effortDetailsCont);

            estimateTable.add(tl.createConstraint().widthPercentage(33).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_ESTIMATE_SHORT)); //"Estimate")); //leftalign labels (like the Tickers)
            estimateTable.add(tl.createConstraint().widthPercentage(34).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_TOTAL_SHORT)); //"Total"));
            estimateTable.add(tl.createConstraint().widthPercentage(33).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_REMAINING_SHORT)); //"Remaining"));
            //TODO make the effort Pickers small (size as the time, not as the cell) and centered (and center the labels above again)
            estimateTable.add(estimate).add(totalActualEffort).add(effort);  //!!: reuse same strings as from ScreenItem!
            estimateTable
                    .setHidden(!MyPrefs.getBoolean(MyPrefs.timerShowEffortEstimateDetails)); //hide initially
            contentPane.add(MyBorderLayout.center(estimateTable));

            MyForm.initField(Item.PARSE_COMMENT, comment,
                    () -> timedItem.getComment(),
                    (t) -> timedItem.setComment((String) t),
                    //                    () -> comment.getText(), (t) -> comment.setText((String) t), null, parseIdMap2
                    () -> comment.getText(),
                    (t) -> comment.setText((String) t), null, null, null, null, null //parseIdMap2=null, since everything is saved to ParseServer on edit, so no point in saving on exit as well
            );
            Container commentContainer = ScreenItem2.makeCommentContainer(comment);
            contentPane.add(MyBorderLayout.center(commentContainer)); //TODO add full screen edit for Notes

            //Action buttons
            //Show interrupted tasks
//        if (currEntry.interruptOrInstantTask) {
            int textPos = Button.RIGHT; //BOTTOM;

            contentPane.add(hiddenElapsedTimePicker);
            contentPane.setScrollableY(true); //since the size of the timer may overflow

            Button c10 = new Button(cmdSaveAndExitTimerScreen); //"Exit"),
            c10.setUIID("BigTimerExit");
            c10.setTextPosition(textPos);
            Button c11 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"), 
            c11.setUIID("BigTimerWaiting");
            c11.setTextPosition(textPos);
            Button c12 = null;
            if (cmdStopTimerAndGotoNextTaskOrExit != null) {
                c12 = new Button(cmdStopTimerAndGotoNextTaskOrExit); //"Stop", "Next", 
                c12.setUIID("BigTimerNext");
                c12.setTextPosition(textPos);
            }
            Button c13 = null;
            c13 = new Button(cmdSetCompletedAndGotoNextTaskOrExit);
            c13.setUIID("BigTimerCompleted");
            c13.setTextPosition(textPos);
            contentPane.add(GridLayout.encloseIn(1, c13));
            if (c12 != null)
                contentPane.add(GridLayout.encloseIn(3, c10, c11, c12));
            else contentPane.add(GridLayout.encloseIn(2, c10, c11));
//                        nextTaskCont.add(gotoNextTaskButtonWithItemText);
            if (gotoNextTaskButtonWithItemText != null)
                contentPane.add(gotoNextTaskButtonWithItemText);
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                if (timedItem.isInteruptOrInstantTask()) {
//                    Item interruptedItem = timedItem.getTaskInterrupted();
//
//                    if (interruptedItem != null) { //a task was interrupted
//                        //UI: not possible to Exit Timer when an interrupted task is pending, must first deal w ith interrupt and then chose an action on the interrupted task
//                        Button c1 = new Button(cmdStopTimerAndGotoNextTaskOrExit);//"Next"),
//                        c1.setTextPosition(textPos);
//                        Button c2 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
//                        c2.setTextPosition(textPos);
//                        Button c3 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
//                        c3.setTextPosition(textPos);
//                        contentPane.add(GridLayout.encloseIn(2, c1, c2)); //autofit
//                        contentPane.add(GridLayout.encloseIn(1, c3)); //autofit
//                        //UI: as long as there is interrupted task(s!) show only those (not next tasks)
//                        contentPane.add(new SpanLabel("Interrupted: " + interruptedItem.getText()));
//                    } else {
////                    assert size() == 1 : "timerStack should always be size==1 when no tasks was interrupted";
////                    Button c4 = new Button(cmdExitTimer); //"Stop"),
//                        Button c4 = new Button(cmdSaveAndExitTimerScreen); //"Stop"),
//                        c4.setTextPosition(textPos);
//                        Button c5 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
//                        c5.setTextPosition(textPos);
//                        Button c6 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
//                        c6.setTextPosition(textPos);
//                        contentPane.add(GridLayout.encloseIn(2, c4, c5));
//                        contentPane.add(GridLayout.encloseIn(1, c6));
//                    }
////            } else if (currEntry.nextItem == null) {
//                } else if (gotoNextTaskButtonWithItemText == null) {
//                    //DONE!!! use task checkbox to mark task Done/Waiting? Only keep Exit/Next to leave it ongoing
//                    Button c7 = new Button(cmdSaveAndExitTimerScreen); //"Exit"),
//                    c7.setTextPosition(textPos);
//                    Button c8 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit);
//                    c8.setTextPosition(textPos);
//                    Button c9 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
//                    c9.setTextPosition(textPos);
//                    contentPane.add(GridLayout.encloseIn(2, c7, c8));
//                    contentPane.add(GridLayout.encloseIn(1, c9));
//                } else {
//                    Button c10 = new Button(cmdSaveAndExitTimerScreen); //"Exit"),
//                    c10.setTextPosition(textPos);
//                    Button c11 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
//                    c11.setTextPosition(textPos);
//                    Button c12 = new Button(cmdStopTimerAndGotoNextTaskOrExit); //"Stop", "Next",
//                    c12.setTextPosition(textPos);
//                    Button c13 = new Button(cmdSetCompletedAndGotoNextTaskOrExit);
//                    c13.setTextPosition(textPos);
//                    contentPane.add(GridLayout.encloseIn(3, c10, c11, c12));
//                    contentPane.add(GridLayout.encloseIn(1, c13));
////                        nextTaskCont.add(gotoNextTaskButtonWithItemText);
//                    contentPane.add(gotoNextTaskButtonWithItemText);
//                }
//
//                if (false) { //new button setup code
//                    Button c10 = new Button(cmdSaveAndExitTimerScreen); //"Exit"),
//                    c10.setTextPosition(textPos);
//                    Button c11 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
//                    c11.setTextPosition(textPos);
//                    Button c12 = new Button(cmdStopTimerAndGotoNextTaskOrExit); //"Stop", "Next",
//                    c12.setTextPosition(textPos);
//                    Button c13 = new Button(cmdSetCompletedAndGotoNextTaskOrExit);
//                    c13.setTextPosition(textPos);
//                    contentPane.add(GridLayout.encloseIn(3, c10, c11, c12));
//                    contentPane.add(GridLayout.encloseIn(1, c13));
////                        nextTaskCont.add(gotoNextTaskButtonWithItemText);
//                    contentPane.add(gotoNextTaskButtonWithItemText);
//                }
//            }
//</editor-fold>
            return contentPane;
        } else {
            //SMALL TIMER container
//                Container swipeSmallContainer = new SwipeableContainer(nextTask, null, contentPane);
            status.setUIID("SmallTimerItemStatus");
            boolean interruptTask = timedItem.isInteruptOrInstantTask();
            Button nextTask = new Button(cmdStopTimerAndGotoNextTaskOrExit); //, Icons.iconTimerNextTask);
            nextTask.setUIID("SmallTimerSwipeNext");
            Button exitTimer = new Button(cmdSaveAndExitSmallTimer); //, Icons.iconTimerNextTask);
            exitTimer.setUIID("SmallTimerSwipeExit");
            exitTimer.setText(""); //remove text in small timer
            nextTask.setText("");
//                Container swipeable = new SwipeableContainer(BoxLayout.encloseX(nextTask,exitTimer), null, contentPane);
            SwipeableContainer swipeable = new SwipeableContainer(nextTask, exitTimer, contentPane) {
//                public void fireActionEvent(ActionEvent ev) {
                public void actionPerformed(ActionEvent evt) {
                    evt.consume();
                }
            };
            if (Config.TEST) swipeable.setName("SmallTimerSwipeable");
//            swipeable.setGrabsPointerEvents(true);
            swipeable.setGrabsPointerEvents(true);
//                contentPane.setUIID("SmallTimerContainer");
            swipeable.setUIID("SmallTimerContainer");
//                nextTask.setTextPosition(CN.BOTTOM);
            Button fullScreenTimerButton = new Button(cmdGotoFullScreenTimer);
            fullScreenTimerButton.setUIID("SmallTimerEditItem");
//<editor-fold defaultstate="collapsed" desc="comment">
//                Container timerContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//                timerContainer.addAll(elapsedTimeButton, timerStartStopButton);
//                timerContainer.addAll(timerStartStopButton);

//                contentPane.add(BorderLayout.centerCenterEastWest(
//                contentPane.add(BorderLayout.center(
//                contentPane.add(BorderLayout.CENTER, interruptTask ? BoxLayout.encloseX(new Label(Icons.iconInterruptToolbarStyle), description) : description);
//                        /*Center*/interruptTask ? BoxLayout.encloseXNoGrow(new Label(Icons.iconInterruptToolbarStyle), description) : description,
/*Center*/
//                        BorderLayout.west(status).add(BorderLayout.CENTER, interruptTask ? BoxLayout.encloseXNoGrow(new Label(Icons.iconInterruptToolbarStyle), description) : description).add(BorderLayout.EAST, editItemButton),
//</editor-fold>
            /*East*/
            contentPane.add(MyBorderLayout.EAST,
                    BoxLayout.encloseXNoGrow(timerStartStopButton, fullScreenTimerButton));
//<editor-fold defaultstate="collapsed" desc="comment">
//                                : description).add(BorderLayout.EAST, editItemButton),
//                        BoxLayout.encloseXNoGrow(timerContainer, nextTask, fullScreenTimerButton),
//                        BoxLayout.encloseXNoGrow(timerContainer,  fullScreenTimerButton),
//</editor-fold>
            /*West*/
            Container west = BoxLayout.encloseXNoGrow(status);

            if (interruptTask) {
                Label interruptIcon = new Label();
                interruptIcon.setMaterialIcon(Icons.iconInterrupt);
                interruptIcon.setUIID("SmallTimerInterruptIcon");
                west.add(interruptIcon);
            }
//            if (timedItem.isInteruptOrInstantTask() && (timedItem.getText() == null || timedItem.getText().isEmpty())) {
            if (interruptTask || (timedItem.getText() == null || timedItem.getText().isEmpty())) {
//                west.add(description); //only make task text editable if empty interrupt task
//                contentPane.add(BorderLayout.CENTER, description);
                west.add(description);
                swipeable.putClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING, description); //pass the description field up to the form to enable start editing smallTimer interrupt
            } else {
                if (Config.TEST) {
                    west.add(new SpanLabel(timedItem.getText() + "->" + (nextComingItem != null ? nextComingItem.getText() : "<>"))); //otherwise just show as label
                } else {
                    west.add(new Label(timedItem.getText())); //otherwise just show as label
                }
            }
            west.add(hiddenElapsedTimePicker);
//                contentPane.add(BorderLayout.WEST, status);
            contentPane.add(MyBorderLayout.WEST, west);
            return swipeable;
        }
//        return contentPane;
//        }
//        return null;
    }
}
