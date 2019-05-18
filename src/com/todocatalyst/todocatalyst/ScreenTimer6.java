package com.todocatalyst.todocatalyst;

import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.MyBorderLayout;
//import java.util.function.*;
/**
 * Shows a timer. UI:
 *
 * @author Thomas
 */
public class ScreenTimer6 extends MyForm {

    //TODO!!! get owner project / list from owner field of timed task (to ensure that even if started from e.g. Overdue, it will show!
    //TODO Imp: rearrange Timer to show most useful/needed first: show task and timer first, then Notes, then estimates.
    //TODO Imp: when storing time, round of to seconds to avoid that a few miliseconds suddenly makes a difference between actual and total actual
    //TODO implement pop up to validate Remaining time on Done/Next/Waiting + setting to activate/deactivate (+option to deactivate in pop-up)
    //TODO Imp: add option to always continue Timer with previous ActualEffort value (instead of showing previous Actual separately and use Timer for the ongoing work now). PRO option
    //TODO Feature: use app.start/stop() to save timer state (instead of doing it regularly when updating timer screen), http://stackoverflow.com/questions/28225954/how-can-i-load-a-specific-codenameone-form-every-time-the-app-is-open?rq=1
    //TODO Imp: change the timer symbol shown everywhere when timer is running (e.g. to inverse/black frame)
    //TODO Imp: move buttons from Timer titlebar to bottom toolbar to get a cleaner look
    //TODO Imp: add visual cues to indicate when totalActual is above EffortEstimate, or when timer elapsed time is above Remaining
    //DONE Imp: add setting button (or in ScreenTimerSettings) to show seconds in timer (or just keep as default?!)
    //TODO Imp: make button COmpleted green and two rows high
    //TODO Imp: make buttons Exit/Wait look like buttons on iPhone, give them a color
    //TODO Imp: pop-up Toastbar every x minutes when Timer is running + setting "Reminder message when timer is running" - as an alternative to buzzer
    //TODO Feature: log start/stop of timer to capture exactly when work was done on it, amount of interrupts etc
    //TODO Imp: activate timer in list screens (works, remain to do in all appropriate screens)
    //TODO Imp: add small Timer container to other screens (and/or change Timer symbol to show it is running - no, best to be cclearly reminded about what task is running to not loose focus)
    //TODO Imp: timer lable is not updated while overflow menu is shown
    //DONE Imp: create and implement Setting to adjust buzzer interval
    //TODO!!! Imp: buzz 'warning' pattern or sound when Remaining (Estimate) is passed (or make this an adjustable interval, e.g. warn 5min/0min/10% before reaching estimate). 
    //TODO Feature(?): add followup task when setting a task Waiting (instead of setting a wait until date and/or alarm)
    //TODO Feature: Pomodoro timer (to take breaks regularly): set interval, turn on/off, break interval, text/buzz pattern on start/stop. 
    //TODO Feature: Integrate Timer as a 'music' player on phone: Music title=Task, Album=List/Project, Play/Pause=Start/Pause timer, Played time=Actual+timer, Remaining time=Remaining-actual-timer, Next >>=Done (or Next?? - make this an option). Volume==50%=>buzzer/>80%=>buzzer+sound.
    //TODO Feature: Discrete mode = 2 modes: incognito mode w. black screen unless pushes button/touches screen, discrete mode: small font task title + time on black screen. Full mode (working alone): all text, big timer, lots of info, e.g. going over time, etc etc. Also see Word doc
    //TODO should it be possible to start the timer on a Done task (yes, if you realize there was a bit more work to do, so also update the CompletedBy date when updating Actual - make it an option of setActual(boolean updateCompletedDateWhenChangingActualForADoneItem) so you can also update the Actual while editing the Item if it was wrong)
    //TODO Imp: find a way to avoid that StartedOn date is set if timer starts automatically when entering the Timer but you didn't intend to work on it (but: why would you enter the timer if you don't intend to work on the task??)
    //DONE set task checkbox symbol to Ongoing as soon as the timer starts!!
    //DONE!!!! error: when rentering Timer for running task after an Interrupt and the timer is not starting automatically, the elapsed time is not updated (until you start the timer) AND the Pause icon is shown instead of the Play icon
    //DONE when setting Actual manually, it is not saved till next time
    //DONE handle situation when user starts Timer on the same item that is already being timed (already working OK?) -> nothing is done if Timer is already running on (any) item
    //DONE handle situation when user exits Timer with Back and reenters with timer symbol (especially when it is the same task in which case it shouldn't do anything)
    //DONE error: when doing Back from Timer to previous screen and then reenter Timer+Continue, the Timer doesn't continue but exits to previous screen
    //DONE when end of itemList is reached and pressing, the same popup as when starting Timer on an empty list should be shown (to ensure simple and consistent behavior)
    //DONE error: when reaching the end of a list, the "Next: yyy" shows the already timed task yyy. Should show "No more tasks"
    //DONE menu when Timer on list is not same as menu on single item
    //DONE error: continues with same subtask in a project
    //DONE error: when interrupting a task, actual is updated&saved, but timer continuous from where it was before, meaning time before interrupt is counted DOUBLE!
    //DONE popup dialog to ask for waiting date when setting a task Waiting
    //DONE error: when marking an interrupt task Done, remaining is not set to 0 (really??)
    //DONE ensure that edited fields are updated on interrupt - before showing the popup dialog showing the previously running task's name (only a problem when popup dialog asking if Keep/Save etc)
    //DONE update pop-up when starting an interrupt task from within the Timer (you pushed the interrupt button so you know a task is already running)
    //DONE ensure consistency btw the Picker for elapsed time and the display (e.g. show seconds separate? or change Picker to support seconds?)
    //DONE show project context (mothertasks) when starting Timer on a subtask within a project
    //DONE on app.stop() (or app.destroy()-same thing), save item (to reduce the need for 
    //DONE test that Timer starts up correctly on previous task after shutdown
    //DONE add dialog to ask if restart timer after app shutdown (or simply start up in the timer view with Timer paused to let the user decide what to do?) -> simply continues with Timer
    //DONE Done (or set status) = long press on checkbox
    //DONE the Total should show the sum of the previous Actual and the Timer (and be updated as the timer is)
    //
    final static String SCREEN_TITLE = "Timer";
//    final static String TIMER_REPLAY = "StartTimer-";

    private TimerInstance timerInstance;//= new TimerStack();
    private Command backCommand = null;
//    protected static String FORM_UNIQUE_ID = "ScreenTimer"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
//    private Container timerContentainer = new Container(BoxLayout.y());
//    private TimerStackEntry entry;

//    private CheckBox startTimerAutomaticallyForNextTask; //should be unique for the Timer
//    private Command continueCommand; //make global to be able to test return value; 
    ScreenTimer6(MyForm previousScreen, TimerInstance timerInstance) {
        this(previousScreen, timerInstance, null);
    }

    ScreenTimer6(MyForm previousScreen, TimerInstance timerInstance, SaveEditedValuesLocally previousValues) {//,  Map<Object, UpdateField> parseIdMap2) {
        super(SCREEN_TITLE, previousScreen, () -> {
        });
        setUniqueFormId("ScreenTimer");
        setUIID("BigTimerForm");
        this.timerInstance = timerInstance;
        if (!(getLayout() instanceof MyBorderLayout)) {
            setLayout(new MyBorderLayout());
        }
//        addComponent(CN.CENTER, timerInstance.getTimerContainer());
//        addComponent(CN.CENTER, timerContentainer);

//        if (previousValues != null) {
//            this.previousValues = previousValues;
//        } else {
////            this.previousValues = new SaveEditedValuesLocally("Timer-" + timerInstance.getTimedItemN().getObjectIdP());
////            this.previousValues = new SaveEditedValuesLocally("Timer-" + TimerStack.getInstance().getTimedItemN().getObjectIdP());
////            this.previousValues = new SaveEditedValuesLocally("Timer");
////            initLocalSaveOfEditedValues("Timer");
//            this.previousValues=new SaveEditedValuesLocally(getUniqueFormId() + "-" + timedItem.getObjectIdP());
//        }

//        setScrollable(false);
        //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        setScrollableY(true); //since the size of the timer may overflow
        setAlwaysTensile(false); //only make scrollable when bigger than screen //TODO!!! not working!

//        initLocalSaveOfEditedValues();
        addCommandsToToolbar(getToolbar()); //no commands depend on the task or itemList
        //updateActionOnDone cannot be used in Timer due to interrupt tasks and since they cannot be pushed onto the stack
//        this.updateActionOnDone = () -> {        };
//        setUpdateActionOnDone(() -> {        }); //not necessary, already done in call to super() above!
        refreshAfterEdit();
    }

    //****************** UI *********************
    //
    public void addCommandsToToolbar(Toolbar toolbar) {

//        backCommand = makeDoneUpdateWithParseIdMapCommand(true); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
        backCommand = makeDoneUpdateWithParseIdMapCommand(() -> {
            TimerInstance timerInstance = TimerStack.getInstance().getCurrentTimerInstanceN();
            if (timerInstance != null) {//can be null if exiting after finishing with last timer?!
                timerInstance.setFullScreen(false);
                timerInstance.saveMe();
            }
            return true;
        }); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
        toolbar.setBackCommand(backCommand); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html

        //Create an interrupt task and start the timer on it
//        toolbar.addCommandToRightBar(makeInterruptCommand());
        toolbar.addCommandToLeftBar(makeInterruptCommand()); //left like all other screens

        toolbar.addCommandToRightBar(MyReplayCommand.createKeep("TimerSettings", null, Icons.iconSettingsLabelStyle, (e) -> {
            new ScreenSettingsTimer(ScreenTimer6.this, () -> {
                refreshAfterEdit();
            }).show();
        }
        ));
    }

    @Override
    public void refreshAfterEdit() {
//        getContentPane().removeAll(); //clear existing contentPane
//        buildContentPane(getTimedItemN(), itemList, getContentPane()); //rebuild for new values of item etc
//        timerInstance = TimerStack.getInstance().getCurrentTimerInstanceN();
        Item timedItem = TimerStack.getInstance().getTimedItemN();
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
//        TimerStack.buildContentPane(timerInstance.getTimerContainer(), timerInstance, true, previousValues); //also removes previous content of contentPane
        //clear previous edited values
        if (previousValues != null) {
            previousValues.deleteFile();
        }
//        if (timerInstance != null) {
        if (timedItem != null) {
//            previousValues = new SaveEditedValuesLocally("Timer-" + timerInstance.getTimedItemN().getObjectIdP());
//            previousValues = new SaveEditedValuesLocally("Timer-" + timerInstance.getTimedItemN().getObjectIdP());
//            previousValues = new SaveEditedValuesLocally("Timer-" + timedItem.getObjectIdP());
//            initLocalSaveOfEditedValues("Timer-" + timedItem.getObjectIdP());
            this.previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + timedItem.getObjectIdP());
//        TimerStack.buildContentPaneFullScreen(ScreenTimer6.this, timerContentainer,  previousValues); //also removes previous content of contentPane
            Container contentPane = getContentPane();
            contentPane.removeAll();
            contentPane.add(MyBorderLayout.CENTER, TimerStack.buildContentPaneFullScreen(ScreenTimer6.this, previousValues)); //also removes previous content of contentPane
            if (false) super.refreshAfterEdit(); //WILL cause infinite loop when updating ScreenTimer6 via refreshOrShowTimerUI
            revalidateWithAnimationSafety();
//            revalidate();
            revalidateWithAnimationSafety();
        }
    }

    // ************************** START TIMER ************************
    //
//    static void showPreviousScreenOrDefaultXXX(MyForm previousForm, boolean callRefreshAfterEdit) {
//        if (Display.getInstance().isScreenSaverDisableSupported() && MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer)) {
//            Display.getInstance().setScreenSaverEnabled(true); //true enable normal screensaver, false keeps screen on all the time
//        }
////        super.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refreshTimersFromParseServer whenever returning from Timer since tasks may have been closed
//        MyForm.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refreshTimersFromParseServer whenever returning from Timer since tasks may have been closed
//    }
    void showPreviousScreenOrDefault(boolean callRefreshAfterEdit) {
        if (Display.getInstance().isScreenSaverDisableSupported() && MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer)) {
            Display.getInstance().setScreenSaverEnabled(true); //true enable normal screensaver, false keeps screen on all the time
        }
//        super.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refreshTimersFromParseServer whenever returning from Timer since tasks may have been closed
        super.showPreviousScreenOrDefault(callRefreshAfterEdit); //need to refreshTimersFromParseServer whenever returning from Timer since tasks may have been closed
    }

//    static void showPreviousScreenOrDefault(boolean callRefreshAfterEdit) {
//        Form f = Display.getInstance().getCurrent();
//        if (f instanceof MyForm) {
//            showPreviousScreenOrDefault((MyForm) f, callRefreshAfterEdit);
//        }
//    }
    @Override
    public void show() {
        //only show if an item is (still) timed, otherwisw showback to return to previous screen, check since show can be called during Replay
        if (TimerStack.getInstance().getTimedItemN() == null) {
            //TODO could do sth more fancy here, like show a message that timer was stopped on othr device...
            backCommand.actionPerformed(null);
        } else {
            super.show();
        }
    }

//    @Override
//    public void showBack() {
////        ReplayLog.getInstance().popCmd(); //pop any previous command
////        super.showBack();
//        showBack(false);
//    }

}
