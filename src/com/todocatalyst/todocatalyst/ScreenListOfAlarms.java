/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.ui.*;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.animations.FlipTransition;
import com.codename1.ui.animations.Transition;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.tree.TreeModel;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.AlarmType.*;
import static com.todocatalyst.todocatalyst.MyTree2.KEY_ACTION_ORIGIN;
import com.todocatalyst.todocatalyst.MyTree2.StickyHeaderGenerator;
import static com.todocatalyst.todocatalyst.MyTree2.setIndent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
//import javafx.scene.effect.DisplacementMap;

/**
 * The list to be edited is passed to this screen, which edits it directly (add
 * new items, delete item, edit items in list). The caller is responsible for
 * saving the updated list. If the list is not saved, created objects (eg tasks,
 * categories, workslots) will not be deleted, leaving dangling tasks, which
 * could be cleaned up or caught in an 'Uncategorized' list.
 *
 * Edit an item with a list of Main screen should contain the following
 * elements: Views - user defined views Jot-list Add new item Categories - see
 * or edit categories People - list of people to assign tasks to Locations -
 * list of locations to assign tasks to Find(?) - or just a menu item in each
 * sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenListOfAlarms extends MyForm {

//    private LocalNotificationsShadowList notificationList;
//    private long now; //represent 'now' wrt latest update of the screen <8eg to ensure that an alarm that expires just after the screen is updated may be cancelled w/o being shown/seen
    /**
     * store and possibly reuse the latest manually adjusted snooze time
     */
    private long individuallySetSnoozeTimeMillis = MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS; //initialize to default value for first time use
//    private KeepInSameScreenPosition keepPos; // = new KeepInSameScreenPosition();
//    private List<ExpiredAlarm> expiredAlarms;
//     protected static String FORM_UNIQUE_ID = "ScreenListOfAlarms"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    private boolean exitOnEmptyAlarmList = false; //set to true on first show of alarm screen (used to enable showing an empty alarm screen, but subsequently exit if there are no more active alarms)

    private static ScreenListOfAlarms INSTANCE;

    private Command backCommand;

//    public static ScreenListOfAlarms getInstance() {
//        if (INSTANCE == null) {
////            INSTANCE = new ScreenListOfAlarms(AlarmHandler.getInstance().notificationList);
//            INSTANCE = new ScreenListOfAlarms();
//        }
//        return INSTANCE;
//    }
//    ScreenListOfAlarms(LocalNotificationsShadowList notificationList) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//    private ScreenListOfAlarms() { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        
//    }
    ScreenListOfAlarms(MyForm previousScreen) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
        super(ScreenType.ALARMS.getTitle(), previousScreen, null, ScreenType.ALARMS.getHelpText());
        previousScreen.setKeepPos(); //save scroll position of calling screen
//        setTextToShowIfEmptyList("No Reminders to deal with"); //"No Reminders to deal with",
        setTextToShowIfEmptyList(ScreenType.ALARMS.getEmptyScreenText()); //"No Reminders to deal with",
        configureWithScreenType(ScreenType.ALARMS);
        setUIID("AlarmsForm");
        setUniqueFormId("ScreenListOfAlarms");

//        this.notificationList=notificationList;
//        setScrollable(false); //don't set form scrollable when containing a (scrollable) list: https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        if (false && !(getLayout() instanceof BorderLayout)) {
            setLayout(new BorderLayout());
        }

//        setTransitionInAnimator(CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 200));
//        setTransitionInAnimator(new FlipTransition());
//        expandedObjects = new ExpandedObjects(getUniqueFormId());
        expandedObjectsInit("");

        addCommandsToToolbar(getToolbar());
        refreshAfterEdit();
    }

//    @Override
//    void showPreviousScreenXXX(boolean callRefreshAfterEdit) {
//        individuallySetSnoozeTimeMillis = 0; //since the alarm screen is a singleton, values not kept between invocations must be explicitly reset
//        super.showPreviousScreen(callRefreshAfterEdit);
//    }
//    @Override
//    public void showXXX() {
//        ASSERT.that(false, "shouldn't be called - since it won't add previousForm");
//        if (false) {
//            showXXX(parentForm);
//        } else {
//            exitOnEmptyAlarmList = false;
//            refreshAfterEdit();
//            super.show();
//        }
//
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public void showBackXXX(boolean popCommand) {
//        if (false) {
//            if (AlarmHandler.getInstance().getExpiredAlarms().size() == 0 && exitOnEmptyAlarmList) {
//                exitOnEmptyAlarmList = false;
//                ReplayLog.getInstance().popCmd(); //pop the replay command for show alarm screen
//                showPreviousScreen(true); //exit if the there are no more alarms
////        Form form = getCurrentFormAfterClosingDialogOrMenu();
////        exitOnEmptyAlarmList = true;
//            } else {
//                exitOnEmptyAlarmList = false;
////                super.showBack(popCommand);
//            }
//        }
//        exitOnEmptyAlarmList = false;
////        backCommand.actionPerformed(null);
////        super.showBack(popCommand);
//    }
//    @Override
//    public void showBackXXX() {
//        if (AlarmHandler.getInstance().getExpiredAlarms().size() == 0 && exitOnEmptyAlarmList) {
//            exitOnEmptyAlarmList = false;
//            showPreviousScreenOrDefault(true); //exit if the there are no more alarms
////        Form form = getCurrentFormAfterClosingDialogOrMenu();
////        exitOnEmptyAlarmList = true;
//        } else {
//            exitOnEmptyAlarmList = false;
//            refreshAfterEdit();
//            super.showBack();
//        }
//    }
//</editor-fold>
//    public void showXXX(MyForm previousForm) {
//        showXXX(previousForm, false);
//    }
//    public void showXXX(MyForm previousForm, boolean flipTransition) {
//        if (false) {
//            ASSERT.that(previousForm != null, "shouldn't be called s previousForm==null");
//
////<editor-fold defaultstate="collapsed" desc="comment">
//////        List<ExpiredAlarm> expiredAlarms = AlarmHandler.getInstance().getExpiredAlarms(); //need a copy of the list to avoid java.util.ConcurrentModificationException in CancellAll/SnoozeAll loops below
//////        if (expiredAlarms.size() > 1) {
//////        if (expiredAlarms.size() == 0 && exitOnEmptyAlarmList) {
////        if (AlarmHandler.getInstance().getExpiredAlarms().size() == 0 && exitOnEmptyAlarmList) {
////            exitOnEmptyAlarmList = false;
////            showPreviousScreenOrDefault(true); //exit if the there are no more alarms
//////        Form form = getCurrentFormAfterClosingDialogOrMenu();
//////        exitOnEmptyAlarmList = true;
////        } else {
////</editor-fold>
//            exitOnEmptyAlarmList = false;
//            if (previousForm != null) {
//                this.parentForm = previousForm;
//            } else {
//                MyForm current = getCurrentFormAfterClosingDialogOrMenu();
////<editor-fold defaultstate="collapsed" desc="comment">
////        if ((current instanceof ScreenListOfAlarms)) {
////        if ((current == this)) {
////            //if a new alarm has expired removeFromCache the current screen
//////            refreshAfterEdit();
////        } else {
////            //if listOfAlarms already shown when new alarm expires then keep the previous
////            this.previousForm = getCurrentFormAfterClosingDialogOrMenu();
////        }
////</editor-fold>
//                if ((current != this)) { //don't store ScreenListOfAlarms as previous screen if it is already shown!
//                    this.parentForm = current; //getCurrentFormAfterClosingDialogOrMenu();
//                }
//            }
//            refreshAfterEdit();
////        super.showPreviousScreenOrDefault(form instanceof MyForm ? (MyForm) form : null, true);
//            if (false) {
//                this.setTransitionInAnimator(CommonTransitions.createCover(CommonTransitions.SLIDE_VERTICAL, false, ANIMATION_TIME_DEFAULT));
//                this.setTransitionOutAnimator(CommonTransitions.createUncover(CommonTransitions.SLIDE_VERTICAL, false, ANIMATION_TIME_DEFAULT));
//            }
//        }
//        //make a flipTransition when AlarmScreen is triggered by expiration of timer (not if launched manually)
//        Transition prevTransition = null;
//        if (flipTransition) {
//            prevTransition = getTransitionInAnimator();
//            setTransitionInAnimator(new FlipTransition());
//        }
//        if (true || previousForm == this) { //true always refresh & show, even if current Form is ScreenListOfAlarms
////        show();
//            this.parentForm = previousForm; //always store previous form since AlarmScreen is a singleton
//            refreshAfterEdit();
//            super.show();
//            if (prevTransition != null) {
//                setTransitionInAnimator(prevTransition);
//            }
//        }
////        }
//    }
    @Override
    public void refreshAfterEdit() {
        if (Config.TEST) {
            Log.p("Calling ScreenListOfalarms.RefreshAfterEdit");
        }

        Date now = new MyDate();
//        List<ExpiredAlarm> expiredAlarmsCopy = new ArrayList(AlarmHandler.getInstance().getExpiredAlarms()); //need a copy of the list to avoid java.util.ConcurrentModificationException in CancellAll/SnoozeAll loops below
        List<AlarmRecord> expiredAlarmsCopy = new ArrayList(AlarmHandler.getInstance().getUnprocessedAlarms(now)); //need a copy of the list to avoid java.util.ConcurrentModificationException in CancellAll/SnoozeAll loops below

//        if (expiredAlarms.size() > 1) {
        if (false && expiredAlarmsCopy.size() == 0 && exitOnEmptyAlarmList) {
            exitOnEmptyAlarmList = false;
            showPreviousScreen(true); //exit if the there are no more alarms
        } else {
//            exitOnEmptyAlarmList = false; //reset for the case where expiredAlarms.size() > 0
            ReplayLog.getInstance().clearSetOfScreenCommandsNO_EFFECT(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again

//            now = MyDate.currentTimeMillis();
            getContentPane().removeAll();
            Container alarmCont = buildContentPaneForAlarmList(expiredAlarmsCopy, parentForm);
            getContentPane().add(BorderLayout.CENTER, alarmCont);
            //        if (this.keepPos != null) {
            //            this.keepPos.setNewScrollYPosition();
            //        }
//keep snooze all even if only a single item, more consistent UX
            //add Cancel All and Snooze All buttons
            Button cancelAll = new Button(CommandTracked.create("Cancel All", Icons.iconAlarmOff, (evt) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                while (!notificationList.isEmpty()) { //exit screen if all alarms are dealt with
//                for (int i = 0, size = expiredAlarms.size(); i < size; i++) { //exit screen if all alarms are dealt with
//
//                    NotificationShadow notif = expiredAlarms.removeAlarmAndCancelLocalNotificationImpl(i);
//
//                    Item item = DAO.getInstance().fetchItem(AlarmType.getObjectIdStrWithoutTypeStr(notif.notificationId, notif.type));
//                    if (notif.alarmTime.getTime() <= now) {
//                        expiredAlarms.removeAlarmAndRepeatAlarm(expiredAlarms.getNextFutureAlarm().notificationId); //update
//                    } else {
//                        break;
//                    }
//</editor-fold>
//                for (ExpiredAlarm expired : expiredAlarms) {
//                    AlarmHandler.getInstance().removeExpiredAlarm(expired);
//                }
                AlarmHandler.getInstance().cancelAllExpiredAlarms();
                showPreviousScreen(true); //false);

            }, "CancelAllAlarms"));
            cancelAll.setUIID("ScreenAlarmsCancelAll");

//            MyDurationPicker snoozeTimePicker = new MyDurationPicker(MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt()*MyDate.MINUTE_IN_MILLISECONDS);
            MyDurationPicker snoozeAllPicker = new MyDurationPicker(MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS);
            snoozeAllPicker.setHidden(true);
            snoozeAllPicker.addActionListener(e -> {
                //popup picker and snooze and exit screen immediately
//                long customSnoozeDuration = snoozeTimePicker.getDuration();
//                Date snooze = MyDate.getStartOfMinute(new Date(MyDate.currentTimeMillis() + snoozeTimePicker.getDuration()));
                Date snooze = new MyDate(MyDate.currentTimeMillis() + snoozeAllPicker.getDuration());
                AlarmHandler.getInstance().snoozeAllExpiredAlarms(snooze);
                showPreviousScreen(true); //false);
            });

            Button snoozeAll = new MyButtonLongPress(
                    CommandTracked.create("Snooze All", Icons.iconSnooze, (evt) -> {
//                        Date snooze = MyDate.getStartOfMinute(new Date(MyDate.currentTimeMillis() + ((long) MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt()) * MyDate.MINUTE_IN_MILLISECONDS));
                        Date snooze = new MyDate(MyDate.currentTimeMillis() + ((long) MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt()) * MyDate.MINUTE_IN_MILLISECONDS);
                        AlarmHandler.getInstance().snoozeAllExpiredAlarms(snooze);
                        showPreviousScreen(true); //false);
                    }, "SnoozeAllAlarms"),
                    CommandTracked.create("", null, (evt) -> {
//                        snoozeAllPicker.pressed();//simulate pressing the picker button(?)
                        snoozeAllPicker.released(); //simulate pressing the picker button(?)
                    }, "SnoozeAllAlarmsCustomDuration"));
            snoozeAll.setUIID("ScreenAlarmsSnoozeAll");
//            cancelAllButtonsCont.add(MyBorderLayout.EAST, Container.encloseIn(BoxLayout.x(), snoozeTimePicker, snoozeAll));

            snoozeAll.setName("SnoozeAll");
            snoozeAll.setGrabsPointerEvents(true);
            snoozeAll.setFocusable(true);
            Container cancelAllButtonsCont = BorderLayout.north(GridLayout.encloseIn(2, cancelAll, snoozeAll))
                    .add(BorderLayout.SOUTH, snoozeAllPicker); //a picker needs to be added to form to work correctly
            cancelAllButtonsCont.setName("cancelAllButtonsCont");
//            cancelAllButtonsCont.setFocusable(true);
//            getContentPane().add(BorderLayout.SOUTH, cancelAllButtonsCont);
            addToSouthOfContentPane(cancelAllButtonsCont, 0);
//            alarmCont.animateHierarchy(300); //works??
//            alarmCont.animateLayout(ANIMATION_TIME_DEFAULT); //works??
//            exitOnEmptyAlarmList = false;
//        revalidate();
//        restoreKeepPos();
            super.refreshAfterEdit();
//            animateHierarchy(ANIMATION_TIME_DEFAULT);
//            animateLayout(ANIMATION_TIME_DEFAULT);
        }

    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        super.addCommandsToToolbar(toolbar);

        //NEW TASK to Inbox
        toolbar.addCommandToOverflowMenu(makeCommandNewItemSaveToInbox());

        //BACK
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(true));
        backCommand = addStandardBackCommand();

//        toolbar.addCommandToRightBar(MyReplayCommand.createKeep("AlarmSettings", "", Icons.iconSettings, (e) -> {
        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("AlarmSettings", ScreenSettingsAlarms.SETTINGS_MENU_TEXT, Icons.iconSettings, (e) -> {
            boolean oldShowDueTime = MyPrefs.alarmShowDueTimeAtEndOfNotificationText.getBoolean();
            int oldAlarmInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt();

            setKeepPos(new KeepInSameScreenPosition());
            new ScreenSettingsAlarms(ScreenListOfAlarms.this, () -> {
                if (!MyPrefs.alarmsActivatedOnThisDevice.getBoolean()) {
                    AlarmHandler.getInstance().cancelAllExpiredAlarms();
                }
                if (MyPrefs.alarmShowDueTimeAtEndOfNotificationText.getBoolean() != oldShowDueTime
                        || MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt() != oldAlarmInterval) {
//                    AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //refresh all local notifications
                    AlarmHandler.getInstance().updateLocalNotificationsOnChange();//refresh all local notifications
                }
//                refreshAfterEdit(); //refresh since default snooze time may have changed
            }).show();
        }
        ));

        if (Config.TEST) {
            toolbar.addCommandToOverflowMenu(new MyReplayCommand("ShowLocalNotfTEST", "Show local notifications", 'T', (evt) -> {
                Form form = new MyForm("Local notifications", null, null);
                form.setLayout(BoxLayout.y());
                form.getToolbar().setBackCommand(Command.createMaterial("", Icons.iconBackToPreviousScreen, (e) -> ScreenListOfAlarms.this.showBack()));
//                    LocalNotificationsShadowList list = AlarmHandler.getInstance().getLocalNotificationsTEST();
                List<NotificationShadow> list = AlarmHandler.getInstance().getLocalNotificationsTEST();
                for (int i = 0, size = list.size(); i < size; i++) {
                    form.addComponent(new SpanLabel(list.get(i).toString()));
                }
                form.show();
            }
            ));

            toolbar.addCommandToOverflowMenu(new Command("Test task alarm +5s") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    long now = MyDate.currentTimeMillis();
                    Item testItem = new Item("Alarm test task, created " + new MyDate(now) + ", alarm=" + new MyDate(now));
                    testItem.setAlarmDate(new MyDate(now + MyDate.SECOND_IN_MILLISECONDS * 5));
//                    DAO.getInstance().saveAndWait(testItem);
//                    DAO.getInstance().saveNew(testItem, true);
//                    DAO.getInstance().saveNew(testItem);
//                    DAO.getInstance().saveNewTriggerUpdate();
                    DAO.getInstance().saveToParseNow(testItem);
//                    DAO.getInstance().saveInBackground(testItem);
                    Log.p("testItem created=" + testItem + ", alarm=" + new MyDate(now));
                }
            });
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        toolbar.addCommandToRightBar(new MyReplayCommand("AlarmSettings", null, Icons.iconSettingsLabelStyle) {
//            boolean oldShowDueTime = MyPrefs.alarmShowDueTimeAtEndOfNotificationText.getBoolean();
//            int oldAlarmInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();
//
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                setKeepPos(new KeepInSameScreenPosition());
//                new ScreenSettingsAlarms(ScreenListOfAlarms.this, () -> {
//                    if (!MyPrefs.alarmsActivatedOnThisDevice.getBoolean()) {
//                        AlarmHandler.getInstance().cancelAllExpiredAlarms();
//                    }
//                    if (MyPrefs.alarmShowDueTimeAtEndOfNotificationText.getBoolean() != oldShowDueTime
//                            || MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt() != oldAlarmInterval) {
//                        AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //refresh all local notifications
//                    }
//                    refreshAfterEdit(); //refresh since default snooze time may have changed
//                }).show();
//            }
//        });
//</editor-fold>
    }

    /**
     *
     * @param content
     * @return
     */
//    protected static Container buildItemAlarmContainer(ScreenListOfAlarms myForm, Item item, ExpiredAlarm expiredAlarm, List<ExpiredAlarm> expiredAlarms,
    protected static Container buildItemAlarmContainer(ScreenListOfAlarms myForm, Item item, AlarmRecord expiredAlarm, List<AlarmRecord> expiredAlarms,
            MyForm.Action refreshAfterItemEdit, KeepInSameScreenPosition keepPos, MyForm previousForm, ExpandedObjects expandedObjects, Date now) {
//<editor-fold defaultstate="collapsed" desc="comment">
//buildItemContainer(Item item, ItemList orgList, //DONE!!! remove orgList
//            MyForm.GetBoolean isDragAndDropEnabled, MyForm.Action refreshOnItemEdits,
//            boolean selectionModeAllowed, ArrayList<Item> selectedObjects, Category category,
//            KeepInSameScreenPosition keepPos, HashSet expandedObjects, MyForm.Action animator, boolean projectEditMode, boolean singleSelectionMode) {
//        Container itemCont = ScreenListOfItems.buildItemContainer(item);
//        Container itemCont = ScreenListOfItems.buildItemContainer(item, null, ()->false, ()->refreshOnItemEdits, false,null, null, keepPos, null, null, false, false);
//        Container itemCont = ScreenListOfItems.buildItemContainer(item, null, () -> false, refreshOnItemEdits, false, null, null, keepPos, null, null, false, false);
//</editor-fold>
//        Date now = new MyDate();
//        Container itemCont = ScreenListOfItems.buildItemContainer(myForm, item, null, null, expandedObjects, e -> {
        Container itemCont = ScreenListOfItems.buildItemContainer(myForm, item, null, e -> {
//            List<AlarmRecord> allAlarmDatesForEditedItem = item.getAllAlarmRecords(new MyDate(MyDate.MIN_DATE), true);
            List<AlarmRecord> allAlarmDatesForEditedItem = item.getUnprocessedAlarms();
            myForm.exitOnEmptyAlarmList = true; //MUST call before since showBack gets called on Back from ScreenEdit2 (before the lambda function below)
            //UI: updating an alarm (Reminder/Waiiting) to the future will cancel the alarm AND snoozed alarms
            for (AlarmRecord alarm : allAlarmDatesForEditedItem) {
                if ( //if the expired alarm was a normal notification or a snoozed one AND the time was edited to a date in the future
                        (alarm.type == notification
                        && (expiredAlarm.type == notification || expiredAlarm.type == notificationRepeat || expiredAlarm.type == snoozedNotif)
                        && alarm.alarmTime.getTime() > expiredAlarm.alarmTime.getTime())
                        //or if the expired alarm was a Waiting notification or a snoozed one AND the time was edited to a date in the future
                        || (alarm.type == waiting
                        && (expiredAlarm.type == waiting || expiredAlarm.type == waitingRepeat || expiredAlarm.type == snoozedWaiting)
                        && alarm.alarmTime.getTime() > expiredAlarm.alarmTime.getTime())) {
                    //then cancel the expired Alarms
                    AlarmHandler.getInstance().cancelExpiredAlarm(expiredAlarm);
//                    myForm.exitOnEmptyAlarmList = true; //TOO late to call here
//                    if (false) {
//                        if (AlarmHandler.getInstance().getExpiredAlarms().isEmpty()) { //exit screen if all alarms are dealt with
//                            myForm.showPreviousScreen(true);
//                        } else {
//                            refreshAfterItemEdit.launchAction();
//                        }
//                    }
                }
            }
            refreshAfterItemEdit.launchAction();
        });

//        Container alarmCont = BorderLayout.north(itemCont);
//        Container alarmCont = MyBorderLayout.north(BorderLayout.center(itemCont)); //center to ensure Item takes up full width
        Container alarmCont = BorderLayout.south(itemCont); //center to ensure Item takes up full width
        alarmCont.setUIID("ScreenAlarmContainer");

        SpanLabel alarmHeader;
        if ((expiredAlarm.type == AlarmType.waiting || expiredAlarm.type == AlarmType.waitingRepeat)) {
            alarmHeader = new SpanLabel("Waiting " + MyDate.formatDateSmart(expiredAlarm.alarmTime, true, true, true), "ScreenAlarmsWaitingTitle");
//            alarmHeader.setMaterialIcon(Icons.iconWaitingAlarm);
            alarmHeader.setFontIcon(Icons.myIconFont, Icons.iconWaitingAlarmExpiredCust);
        } else if ((expiredAlarm.type == AlarmType.snoozedWaiting)) {
            alarmHeader = new SpanLabel("Snooze Waiting " + MyDate.formatDateSmart(expiredAlarm.alarmTime, true, true, true), "ScreenAlarmsWaitingTitle"); //"Waiting snoozed "
//            alarmHeader.setMaterialIcon(Icons.iconWaitingAlarm);
            alarmHeader.setFontIcon(Icons.myIconFont, Icons.iconWaitingAlarmExpiredCust);
        } else if ((expiredAlarm.type == AlarmType.snoozedNotif)) {
            alarmHeader = new SpanLabel("Snooze Reminder " + MyDate.formatDateSmart(expiredAlarm.alarmTime, true, true, true), "ScreenAlarmsWaitingTitle");
//            alarmHeader.setMaterialIcon(Icons.iconWaitingAlarm);
            alarmHeader.setMaterialIcon(Icons.iconSnoozed);
        } else {//     if ((expiredAlarm.type == AlarmType.waiting || expiredAlarm.type == AlarmType.waitingRepeat)) {
            alarmHeader = new SpanLabel("Reminder " + MyDate.formatDateSmart(expiredAlarm.alarmTime, true, true, true), "ScreenAlarmsWaitingTitle");
            alarmHeader.setMaterialIcon(Icons.iconAlarmTriggered);
        }
        alarmHeader.setIconUIID("ScreenAlarmsWaitingTitle");
//        else         if ((expiredAlarm.type == AlarmType.snooze )) {
//            alarmHeader = new Label("Reminder snoozed " + MyDate.formatDateSmart(expiredAlarm.alarmTime), "ScreenAlarmsWaitingTitle");
//            alarmHeader.setMaterialIcon(Icons.iconAlarmDate);
//        }
//        SpanLabel alarmHeader = new SpanLabel(header);
        Container alarmHeaderCont = BorderLayout.center(alarmHeader);
        alarmHeaderCont.setUIID("ScreenAlarmsHeaderCont");
        alarmCont.add(BorderLayout.NORTH, alarmHeaderCont);

        //SNOOZE PICKER
        long snoozeTimeMillis = MyPrefs.alarmReuseIndividuallySetSnoozeDurationForLongPress.getBoolean() && ((ScreenListOfAlarms) myForm).individuallySetSnoozeTimeMillis != 0
                ? ((ScreenListOfAlarms) myForm).individuallySetSnoozeTimeMillis
                : MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS;
        MyDurationPicker snoozePicker = new MyDurationPicker(snoozeTimeMillis);
        snoozePicker.setHidden(true);
        snoozePicker.addActionListener(e -> {
            //popup picker and snooze and exit screen this was the last/only alarm in the screen
//            Date snoozeExpireTimeInMillis = MyDate.getStartOfMinute(new Date(MyDate.currentTimeMillis() + snoozePicker.getDuration())); //UI: snooze interval always from the moment you activate snooze
            Date snoozeExpireTimeInMillis = new MyDate(
                    (MyPrefs.alarmRoundSnoozeTimeDownToMinutes.getBoolean() ? MyDate.currentTimeMillisRoundedDownToMinutesAsLong() : MyDate.currentTimeMillis())
                    + snoozePicker.getDuration()); //UI: snooze interval always from the moment you activate snooze
            AlarmHandler.getInstance().snoozeAlarm(expiredAlarm, snoozeExpireTimeInMillis);
            if (MyPrefs.alarmReuseIndividuallySetSnoozeDurationForLongPress.getBoolean()) {
                ((ScreenListOfAlarms) myForm).individuallySetSnoozeTimeMillis = snoozePicker.getDuration();
            }
            if (AlarmHandler.getInstance().getUnprocessedAlarms().isEmpty()) { //exit screen if all alarms are dealt with
//                        showPreviousScreenOrDefault(previousForm, true);
                myForm.showPreviousScreen(true);
            } else {
                refreshAfterItemEdit.launchAction();
            };
        });

        //CANCEL
        Button cancelAlarm = new Button(CommandTracked.create("", Icons.iconAlarmOff, (evt) -> {
//            expiredAlarms.removeAlarmAndRepeatAlarm(notif.notificationId); //update
            AlarmHandler.getInstance().cancelExpiredAlarm(expiredAlarm);
            if (AlarmHandler.getInstance().getUnprocessedAlarms().isEmpty()) { //exit screen if all alarms are dealt with
//                        showPreviousScreenOrDefault(previousForm, true);
                myForm.showPreviousScreen(true);
            } else {
                refreshAfterItemEdit.launchAction();
            }
        }, "CancelAlarm"));
        cancelAlarm.setUIID("ScreenAlarmsCancelAlarm");

        //SNOOZE BUTTON
        Button snoozeAlarm = new MyButtonLongPress(
                CommandTracked.create("", Icons.iconSnooze, (evt) -> {
                    Date snoozeExpireTimeInMillis;
                    if (MyPrefs.alarmReuseIndividuallySetSnoozeDurationForNormalSnooze.getBoolean() && ((ScreenListOfAlarms) myForm).individuallySetSnoozeTimeMillis != 0) { //                        snoozeExpireTimeInMillis = MyDate.getStartOfMinute(new Date(MyDate.currentTimeMillis() + ((ScreenListOfAlarms) myForm).individuallySetSnoozeTimeMillis)); //UI: snooze interval always from the moment you activate snooze
                        snoozeExpireTimeInMillis = new MyDate(MyDate.currentTimeMillis() + ((ScreenListOfAlarms) myForm).individuallySetSnoozeTimeMillis); //UI: snooze interval always from the moment you activate snooze
                    } else { //                        snoozeExpireTimeInMillis = MyDate.getStartOfMinute(new Date(MyDate.currentTimeMillis() + snoozePicker.getDuration())); //UI: snooze interval always from the moment you activate snooze
                        snoozeExpireTimeInMillis = new MyDate(MyDate.currentTimeMillis() + snoozePicker.getDuration()); //UI: snooze interval always from the moment you activate snooze
                    }
                    AlarmHandler.getInstance().snoozeAlarm(expiredAlarm, snoozeExpireTimeInMillis);
                    if (AlarmHandler.getInstance().getUnprocessedAlarms(now).isEmpty()) { //exit screen if all alarms are dealt with
//                        showPreviousScreenOrDefault(previousForm, true);
                        myForm.showPreviousScreen(true);
                    } else {
                        refreshAfterItemEdit.launchAction();
                    };
                }, "SnoozeAlarm"),
                CommandTracked.create("", null, (evt) -> {
                    snoozePicker.released(); //simulate pressing the picker button(?)
                }, "SnoozeAlarmsCustomDuration"));
        snoozeAlarm.setUIID("ScreenAlarmsSnoozeAlarm");

        alarmHeaderCont.add(BorderLayout.EAST, BoxLayout.encloseX(cancelAlarm, snoozeAlarm, snoozePicker));
//        alarmHeaderCont.add(BorderLayout.CENTER,  snoozePicker);

        //hack to promote the expandCollapse subtasks button from the ItemContainer to the AlarmContainer
        if (false) { //this disables the start of code to enable expansion of task subtasks
            Object expandButton = itemCont.getClientProperty(KEY_ACTION_ORIGIN);
            alarmCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, expandButton);
        }

        return alarmCont;
    }

//    protected Container buildContentPaneForAlarmList(List<ExpiredAlarm> expiredAlarms, MyForm previousForm) {
    protected Container buildContentPaneForAlarmList(List<AlarmRecord> unprocessedAlarms, MyForm previousForm) {
        parseIdMap2.parseIdMapReset();

        MyTreeModel xx = new MyTreeModel() {
            /**
             * Returns the child objects representing the given parent, null
             * should return the root objects
             *
             * @param parent the parent object whose children should be
             * returned, null would return the tree roots
             * @return the children of the given node within the tree
             */
            @Override
            public List getChildrenList(Object parent) {
//                List<ExpiredAlarm> expiredFiltered = new ArrayList();
//                for (ExpiredAlarm notif : expiredAlarms) {
                List<AlarmRecord> expiredFiltered = new ArrayList();
                for (AlarmRecord notif : unprocessedAlarms) {
//            ExpiredAlarm notif = expiredAlarms.get(i);
//                    if (true||notif.alarmTime.getTime() <= now) {
//                        Item item = DAO.getInstance().fetchItemN(notif.guid);
                    Item item = notif.item;
                    expiredFiltered.add(notif);
//                    }
                }
                return expiredFiltered;
            }

            /**
             * Is the node a leaf or a folder
             *
             * @param node a node within the tree
             * @return true if the node is a leaf that can't be expanded
             */
            @Override
            public boolean isLeaf(Object node) {
                return true;
            }
        };

        Date now = new MyDate();
        if (unprocessedAlarms.size() > 0) {
            MyTree2 myTree = new MyTree2(this, xx, expandedObjects, null, (StickyHeaderGenerator) null) {//                    lastInsertNewElementContainer != null ? 
                @Override
//                protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList, Category category) {
//                protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList) {
                protected Component createNode(Object node, int depth, Object itemOrItemList) {
//                    ExpiredAlarm notif = (ExpiredAlarm) node;
                    AlarmRecord notif = (AlarmRecord) node;
//                    Item item = DAO.getInstance().fetchItemN(notif.guid);
//                    Item item = DAO.getInstance().fetchItemN(notif.item.getGuid());
                    Item item = notif.item;
                    showDetails.add(item); //hack to always show alarmTime even if normally hidden in details
                    Component cmp = buildItemAlarmContainer(ScreenListOfAlarms.this, item, notif, unprocessedAlarms, () -> refreshAfterEdit(),
                            keepPos, previousForm, expandedObjects, now);
                    setIndent(cmp, depth);
                    return cmp;
                }
            };
            return myTree;
        } else {
            return BorderLayout.centerCenter(new SpanLabel(getTextToShowIfEmptyList()));
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected Container buildContentPaneForAlarmListOLD(List<ExpiredAlarm> expiredAlarms, MyForm previousForm) {
//        parseIdMap2.parseIdMapReset();
//        if (expiredAlarms != null && expiredAlarms.size() > 0) {
////        Container cont = new Container();
//            Container cont = new ContainerScrollY(BoxLayout.y());
//            cont.setScrollableY(true);
////        for (int i = 0, size = expiredAlarms.size(); i < size; i++) {
////            ExpiredAlarm notif = expiredAlarms.get(i);
//            for (ExpiredAlarm notif : expiredAlarms) {
////            ExpiredAlarm notif = expiredAlarms.get(i);
//                if (notif.alarmTime.getTime() <= now) {
//                    Item item = DAO.getInstance().fetchItem(notif.objectId);
//                    showDetails.add(item);
////                Component cmp = buildItemAlarmContainer(ScreenListOfAlarms.this, item, notif, expiredAlarms, () -> refreshAfterEdit(),
//                    Component cmp = buildItemAlarmContainer(ScreenListOfAlarms.this, item, notif, expiredAlarms, () -> refreshAfterEdit(),
//                            keepPos, previousForm, expandedObjects);
//                    cont.add(cmp);
//                } else {
//                    break;
//                }
//            }
//            return cont;
//        } else {
//            return BorderLayout.centerCenter(new SpanLabel(getTextToShowIfEmptyList()));
//        }
//    }
//</editor-fold>
}
