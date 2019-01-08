/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.ui.*;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Toolbar;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.MyTree2.setIndent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.scene.effect.DisplacementMap;

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

    public static String screenTitle = "Past reminders"; //"Past reminders", "Expired reminders"
    private static String screenHelp = "Shows past reminders. Starting Timer on a task or editing it will cancel the reminder";
//    private LocalNotificationsShadowList notificationList;
    private long now; //represent 'now' wrt latest update of the screen <8eg to ensure that an alarm that expires just after the screen is updated may be cancelled w/o being shown/seen
//    private KeepInSameScreenPosition keepPos; // = new KeepInSameScreenPosition();
    private List<ExpiredAlarm> expiredAlarms;

    private static ScreenListOfAlarms INSTANCE;

    public static ScreenListOfAlarms getInstance() {
        if (INSTANCE == null) {
//            INSTANCE = new ScreenListOfAlarms(AlarmHandler.getInstance().notificationList);
            INSTANCE = new ScreenListOfAlarms();
        }
        return INSTANCE;
    }

//    ScreenListOfAlarms(LocalNotificationsShadowList notificationList) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
    private ScreenListOfAlarms() { //, GetUpdatedList updateList) { //throws ParseException, IOException {
        super(screenTitle, null, () -> {
        });

//        this.notificationList=notificationList;
        setScrollable(false); //don't set form scrollable when containing a (scrollable) list: https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        if (!(getLayout() instanceof BorderLayout)) {
            setLayout(new BorderLayout());
        }

        addCommandsToToolbar(getToolbar());
        refreshAfterEdit();
    }

    @Override
    public void show() {
//        Form form = getCurrentFormAfterClosingDialogOrMenu();
        Form current = getCurrentFormAfterClosingDialogOrMenu();
//        if ((current instanceof ScreenListOfAlarms)) { 
//        if ((current == this)) {
//            //if a new alarm has expired refresh the current screen
////            refreshAfterEdit();
//        } else {
//            //if listOfAlarms already shown when new alarm expires then keep the previous
//            this.previousForm = getCurrentFormAfterClosingDialogOrMenu();
//        }
        if ((current != this)) {
            this.previousForm = getCurrentFormAfterClosingDialogOrMenu();
        }
        refreshAfterEdit();
//        super.showPreviousScreenOrDefault(form instanceof MyForm ? (MyForm) form : null, true);
        this.setTransitionInAnimator(CommonTransitions.createCover(CommonTransitions.SLIDE_VERTICAL, false, 300));
        this.setTransitionOutAnimator(CommonTransitions.createUncover(CommonTransitions.SLIDE_VERTICAL, false, 300));
        super.show();
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again

        now = System.currentTimeMillis();

        getContentPane().removeAll();
        expiredAlarms = new ArrayList(AlarmHandler.getInstance().getExpiredAlarms()); //need a copy of the list to avoid java.util.ConcurrentModificationException in CancellAll/SnoozeAll loops below
        Container alarmCont = buildContentPaneForAlarmList(expiredAlarms, previousForm);
        getContentPane().add(BorderLayout.CENTER, alarmCont);
//        if (this.keepPos != null) {
//            this.keepPos.setNewScrollYPosition();
//        }

//        if (expiredAlarms.size() > 1) {
        if (expiredAlarms.size() > 0) { //keep snooze all even if only a single item
            Container cancelAllButtonsCont = new Container(new BorderLayout());
            //add Cancel All and Snooze All buttons
            cancelAllButtonsCont.add(BorderLayout.WEST, new Button(Command.create("Cancel All", Icons.iconAlarmOffLabelStyle, (evt) -> {
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
                showPreviousScreenOrDefault(true); //false);

            })));

            MyDurationPicker snoozeTimePicker = new MyDurationPicker(MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt());

            cancelAllButtonsCont.add(BorderLayout.EAST, Container.encloseIn(BoxLayout.x(), snoozeTimePicker, new Button(Command.create("Snooze All", Icons.iconAlarmOffLabelStyle, (evt) -> {
//                Date snoozeExpireTimeInMillis = new Date(System.currentTimeMillis() + MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS); //UI: snooze interval always from the moment you activate snooze
//<editor-fold defaultstate="collapsed" desc="comment">
//                while (!notificationList.isEmpty()) { //exit screen if all alarms are dealt with
//                for (int i = 0, size = expiredAlarms.size(); i < size; i++) { //exit screen if all alarms are dealt with
//
//                    NotificationShadow notif = expiredAlarms.removeAlarmAndCancelLocalNotificationImpl(i);
//
//                    Item item = DAO.getInstance().fetchItem(AlarmType.getObjectIdStrWithoutTypeStr(notif.notificationId, notif.type));
//
//                    if (notif.alarmTime.getTime() <= now) {
//                        if (notif.type == AlarmType.notification) {
//                            expiredAlarms.snoozeAlarm(notif.notificationId, snoozeExpireTimeInMillis, item.makeNotificationTitleText(AlarmType.notification), item.makeNotificationBodyText(AlarmType.notification));
//                        } else if (notif.type == AlarmType.waiting) {
//                            expiredAlarms.snoozeAlarm(notif.notificationId, snoozeExpireTimeInMillis, item.makeNotificationTitleText(AlarmType.waiting), item.makeNotificationBodyText(AlarmType.waiting));
//                        }
//                    } else {
//                        break;
//                    }
//                }
//</editor-fold>
//                for (ExpiredAlarm expired : expiredAlarms) {
//                    AlarmHandler.getInstance().snoozeAlarm(expired, snoozeExpireTimeInMillis);
//                }
                AlarmHandler.getInstance().snoozeAllExpiredAlarms(
                        MyDate.getStartOfMinute(new Date(System.currentTimeMillis() + ((long) snoozeTimePicker.getTime()) * MyDate.MINUTE_IN_MILLISECONDS)));
                showPreviousScreenOrDefault(true); //false);
            }))));

            getContentPane().add(BorderLayout.SOUTH, cancelAllButtonsCont);
//            alarmCont.animateHierarchy(300); //works??
            alarmCont.animateLayout(300); //works??
        }

        revalidate();
        restoreKeepPos();
        super.refreshAfterEdit();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        toolbar.addCommandToRightBar(MyReplayCommand.create("AlarmSettings", null, Icons.iconSettingsLabelStyle, (e) -> {
            boolean oldShowDueTime = MyPrefs.alarmShowDueTimeAtEndOfNotificationText.getBoolean();
            int oldAlarmInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();

            setKeepPos(new KeepInSameScreenPosition());
            new ScreenSettingsAlarms(ScreenListOfAlarms.this, () -> {
                if (!MyPrefs.alarmsActivatedOnThisDevice.getBoolean()) {
                    AlarmHandler.getInstance().cancelAllExpiredAlarms();
                }
                if (MyPrefs.alarmShowDueTimeAtEndOfNotificationText.getBoolean() != oldShowDueTime
                        || MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt() != oldAlarmInterval) {
                    AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //refresh all local notifications
                }
                refreshAfterEdit(); //refresh since default snooze time may have changed
            }).show();
        }
        ));

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
        //BACK
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(true));
    }

    /**
     *
     * @param content
     * @return
     */
    protected static Container buildItemAlarmContainer(MyForm myForm, Item item, ExpiredAlarm expiredAlarm, List<ExpiredAlarm> expiredAlarms, MyForm.Action refreshOnItemEdits, KeepInSameScreenPosition keepPos, MyForm previousForm) {
//<editor-fold defaultstate="collapsed" desc="comment">
//buildItemContainer(Item item, ItemList orgList, //DONE!!! remove orgList
//            MyForm.GetBoolean isDragAndDropEnabled, MyForm.Action refreshOnItemEdits,
//            boolean selectionModeAllowed, ArrayList<Item> selectedObjects, Category category,
//            KeepInSameScreenPosition keepPos, HashSet expandedObjects, MyForm.Action animator, boolean projectEditMode, boolean singleSelectionMode) {
//        Container itemCont = ScreenListOfItems.buildItemContainer(item);
//        Container itemCont = ScreenListOfItems.buildItemContainer(item, null, ()->false, ()->refreshOnItemEdits, false,null, null, keepPos, null, null, false, false);
//        Container itemCont = ScreenListOfItems.buildItemContainer(item, null, () -> false, refreshOnItemEdits, false, null, null, keepPos, null, null, false, false);
//</editor-fold>
        Container itemCont = ScreenListOfItems.buildItemContainer(myForm, item, null, null);

//        Container alarmCont = BorderLayout.north(itemCont);
        Container alarmCont = BorderLayout.north(itemCont);

        String header = ((expiredAlarm.type == AlarmType.waiting || expiredAlarm.type == AlarmType.waitingRepeat)
                ? "Waiting reminder at " : "Reminder at ") + MyDate.formatDateTimeNew(expiredAlarm.alarmTime);
        alarmCont.add(BorderLayout.CENTER, new SpanLabel(header));

        MyDurationPicker snoozeTimePicker = new MyDurationPicker(MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt());

        alarmCont.add(BorderLayout.SOUTH, TableLayout.encloseIn(2,
                //        alarmCont.add(BorderLayout.west(new Button(Command.create("Cancel", Icons.iconAlarmOffLabelStyle, (evt) -> {
                new Button(Command.create("Cancel", Icons.iconAlarmOffLabelStyle, (evt) -> {
//            expiredAlarms.removeAlarmAndRepeatAlarm(notif.notificationId); //update
                    AlarmHandler.getInstance().removeExpiredAlarm(expiredAlarm);

                    if (AlarmHandler.getInstance().getExpiredAlarms().isEmpty()) { //exit screen if all alarms are dealt with
                        showPreviousScreenOrDefault(previousForm, true);
                    } else {
                        refreshOnItemEdits.launchAction();
                    }
//        }))).add(BorderLayout.EAST, Container.encloseIn(BoxLayout.x(), new Button(Command.create("Snooze", Icons.iconSnoozeLabelStyle, (evt) -> {
                })), Container.encloseIn(BoxLayout.x(), snoozeTimePicker, new Button(Command.create("Snooze", Icons.iconSnoozeLabelStyle, (evt) -> {
//            Date snoozeExpireTimeInMillis = new Date(System.currentTimeMillis() + MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS); //UI: snooze interval always from the moment you activate snooze
                    Date snoozeExpireTimeInMillis = new Date(System.currentTimeMillis() + snoozeTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS); //UI: snooze interval always from the moment you activate snooze
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (expiredAlarm.type == AlarmType.notification) {
//                expiredAlarms.snoozeAlarm(expiredAlarm.notificationId, snoozeExpireTimeInMillis, item.makeNotificationTitleText(AlarmType.notification), item.makeNotificationBodyText(AlarmType.notification));
//            } else if (expiredAlarm.type == AlarmType.waiting) {
//                expiredAlarms.snoozeAlarm(expiredAlarm.notificationId, snoozeExpireTimeInMillis, item.makeNotificationTitleText(AlarmType.waiting), item.makeNotificationBodyText(AlarmType.waiting));
//            }
//            for (ExpiredAlarm expired : expiredAlarms) {
//                AlarmHandler.getInstance().snoozeAlarm(expired, snoozeExpireTimeInMillis);
//            }
//</editor-fold>
                    AlarmHandler.getInstance().snoozeAlarm(expiredAlarm, snoozeExpireTimeInMillis);

                    if (AlarmHandler.getInstance().getExpiredAlarms().isEmpty()) { //exit screen if all alarms are dealt with
                        showPreviousScreenOrDefault(previousForm, true);
                    } else {
                        refreshOnItemEdits.launchAction();
                    };
                })))));

        return alarmCont;
    }

    protected Container buildContentPaneForAlarmList(List<ExpiredAlarm> expiredAlarms, MyForm previousForm) {
        parseIdMapReset();
//        Container cont = new Container();
        Container cont = new ContainerScrollY();
        cont.setScrollableY(true);
//        for (int i = 0, size = expiredAlarms.size(); i < size; i++) {
//            ExpiredAlarm notif = expiredAlarms.get(i);
        for (ExpiredAlarm notif : expiredAlarms) {
//            ExpiredAlarm notif = expiredAlarms.get(i);
            if (notif.alarmTime.getTime() <= now) {
                Item item = DAO.getInstance().fetchItem(notif.objectId);
                Component cmp = buildItemAlarmContainer(ScreenListOfAlarms.this, item, notif, expiredAlarms, () -> refreshAfterEdit(),
                        keepPos, previousForm);
                cont.add(cmp);
            } else {
                break;
            }
        }
        return cont;
    }

}
