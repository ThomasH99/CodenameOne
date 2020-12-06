/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Preferences;
import com.codename1.ui.Form;
import com.codename1.ui.Toolbar.BackCommandPolicy;
import java.util.Date;

/**
 *
 * @author THJ
 */
public class MyPrefs {

    final static boolean prod = !Config.TEST;
    //TODAY view
    static PrefEntry todayViewIncludeOverdueFromThisManyPastDays
            = new PrefEntry("In Today, include tasks overdue by this many days", "todayViewIncludeOverdueFromThisManyPastDays", prod ? 0 : 1, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewIncludeWaitingExpiringToday
            = new PrefEntry("In Today, include tasks until today", "todayViewIncludeWaitingExpiringToday", prod ? true : true, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewIncludeAlarmsExpiringToday
            = new PrefEntry("In Today, include tasks with reminders today", "todayViewIncludeAlarmsExpiringToday", prod ? true : true, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewIncludeStartingToday
            = new PrefEntry("In Today, include tasks starting today", "todayViewIncludeStartingToday", prod ? true : true, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewIncludeWorkSlotsCoveringToday
            = new PrefEntry("In Today, include WorkSlots starting today", "todayViewIncludeWorkSlotsCoveringToday", prod ? true : true, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewShowProjectsInsteadOfLeafTasks
            = new PrefEntry("In Today, show top-level projects instead of leaf tasks", "todayViewShowProjectsInsteadOfLeafTasks", prod ? false : false, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)

//ESTIMATES
    static PrefEntry automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining
            = new PrefEntry("**", "automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining", prod ? true : true, "**");

    static PrefEntry automaticallyUpdateRemainingToEffortMinusActualWhenActualEffortIsUpdated
            = new PrefEntry("**", "automaticallyUpdateRemainingToEffortMinusActualWhenActualEffortIsUpdated", prod ? false : false, "**");

    static PrefEntry automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero
            = new PrefEntry("**", "automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero", prod ? true : true, "**");

    static PrefEntry automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual
            = new PrefEntry("**", "automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual", prod ? false : true, "**");

    static PrefEntry itemRemoveTrailingPrecedingSpacesAndNewlines
            = new PrefEntry("Always remove trailing spaces/newlines from task or comment text", "itemRemoveTrailingPrecedingSpacesAndNewlines", prod ? true : false, "Leaving such invisible characters can make the editing confusing");

    static PrefEntry useEstimateDefaultValueForZeroEstimatesInMinutes
            = new PrefEntry("Use default time estimate", "useEstimateDefaultValueForZeroEstimatesInMinutes", prod ? false : true,
                    "Default time estimate to use for non-estimated tasks. Set to typical average value of actual effort for small tasks to avoid having to estimate these. Set to 0 to de-activate.");
    static PrefEntry estimateDefaultValueForZeroEstimatesInMinutes
            = new PrefEntry("Default time estimate", "estimateDefaultValueForZeroEstimatesInMinutes", prod ? 10 : 4,
                    "Default time estimate to use for non-estimated tasks. Set to typical average value of actual effort for small tasks to avoid having to estimate these. Set to 0 to de-activate.");

    static PrefEntry estimateRemainingOnlyUseSubtasksRemaining
            = new PrefEntry("Only use subtasks' Remaining estimates", "estimateRemainingOnlyUseSubtasksRemaining", prod ? true : false,
                    "Otherwise a project's remaining is the sum of the project task's own remaining and the sum of the remaining of all subtasks");

    static PrefEntry estimateEffortEstimateOnlyUseSubtasksEstimates
            = new PrefEntry("Only use subtasks' Effort estimates", "estimateEffortEstimateOnlyUseSubtasksEstimates", prod ? true : true,
                    "Otherwise a project's effort estimate is the sum of the project task's own effort estimates and the sum of the remaining of all subtasks");

    //INSERTION OF NEW ITEMS
//    static PrefEntry insertNewItemsInStartOfCategory = new PrefEntry("insertNewItemsInStartOfCategory", true, "Always insert new tasks at the beginning of lists (instead of at the end)");
    static PrefEntry insertNewItemsInStartOfLists //used not just for tasks but also new lists and categories
            //            = new PrefEntry("Insert tasks at top of lists", "insertNewItemsInStartOfLists", prod ? true : true, "Always insert new or moved tasks at the beginning of lists (instead of at the end)");
            = new PrefEntry("Insert new at top of list", "insertNewItemsInStartOfLists", prod ? true : true, "Always insert new elements at the beginning of lists (instead of at the end)");

    static PrefEntry insertNewItemListsInStartOfItemListList
            = new PrefEntry("Insert new lists at the top of lists", "insertNewItemListsInStartOfItemListList", prod ? true : false, "Always insert new lists at the beginning of lists (instead of at the end)");

    static PrefEntry insertNewSubtasksInScreenItemInStartOfLists
            = new PrefEntry("**Insert new subtasks created at top of list", "insertNewSubtasksInScreenItemInStartOfLists", prod ? false : false, "**Always insert new subtasks at the beginning of lists (instead of at the end)");

    static PrefEntry itemContinueAddingInlineItems //see text for workSlotContinueAddingInlineWorkslots
            = new PrefEntry("**", "itemContinueAddingInlineItems", prod ? true : true, "**");
    //"Insert created at top of list", "Insert new tasks at beginning of list"

//    static PrefEntry insertNewCategoriesForItemsInStartOfIList //NO, confusing, always show in same order as in CategoryList
//            = new PrefEntry("Add new categories to beginning of list**(make this setting public??)", "insertNewCategoriesForItemsInStartOfIList", false, 
//                    "Always insert added categories at the beginning (makes most recently added categories appear first)");
    static PrefEntry dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList
            = new PrefEntry("When dragging a task to a Category, insert at the top of its list of tasks", "dropItemAtBeginningOfUnexpandedCategorySubtaskList",
                    prod ? true : true, "**");

    static PrefEntry insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList
            //            = new PrefEntry("When tasks are dropped as subtask under a task with unexpanded subtasks, insert it as the last subtask (end of subtask list)", 
            = new PrefEntry("Add subtasks at end of subtask list",
                    "insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList", prod ? true : true, "**");

    //DRAG AND DROP
    static PrefEntry dragDropAsSubtaskEnabled
            = new PrefEntry("Drag and drop to right edge of screen inserts as subtask", "dragDropAsSubtaskEnabled", prod ? false : true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

    static PrefEntry dragDropAsSupertaskEnabled
            = new PrefEntry("Drag and drop to left edge of screen inserts tasks at level above", "dragDropAsSupertaskEnabled", prod ? false : true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

//    static PrefEntry dropZoneWidthInPercentForDroppingAsSubtaskOrSuperTask
    static PrefEntry dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask
            = new PrefEntry("Approximate width in millimeters of the drop zone that will drop dragged items as either subtasks (right side of drop target) or supertasks (left side)",
                    "dropZoneWidthInPercentForDroppingAsSubtaskOrSuperTask", prod ? 5 : 10, "**");

//    static PrefEntry dragDropLeftDropZoneWidth
//            = new PrefEntry("Width of the left-hand drop zone (%)", "dragDropLeftDropZoneWidth", 10, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)
//    
//    static PrefEntry dragDropRightDropZoneWidth
//            = new PrefEntry("Width of the right-hand drop zone (%)", "dragDropRightDropZoneWidth", 10, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)
    //Edit ITEM screen - Screenitem2
    static PrefEntry itemEditEnableSwipeBetweenTabs
            = new PrefEntry("Enable swiping between task details", "itemEditEnableSwipeBetweenTabs", prod ? true : true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

    //TIMER - ScreenTimer6
    static PrefEntry timerAutomaticallyStartTimer
            = new PrefEntry("Automatically start timer for a task", "automaticallyStartTimer", prod ? true : true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

    static PrefEntry timerAutomaticallyGotoNextTask
            = new PrefEntry("Automatically continue on next task", "timerAutomaticallyGotoNextTask", prod ? true : true, "Automatically go to the next task in the list or project after the current task is done");

    static PrefEntry timerShowNextTaskWithRemainingTimeXXX
            = new PrefEntry("Show " + Item.EFFORT_REMAINING + " for next-coming task", "timerShowNextTaskWithRemainingTime", prod ? true : true,
                    "Will show the " + Item.EFFORT_REMAINING + " in square brackets like [1:15] after next task at the bottom of the Timer screen."); //TODO make timerShowNextTask a numerical value to show 0/1/2/3 next tasks and start one by clicking on it
    static PrefEntry timerShowNextTask
            = new PrefEntry("Show next-coming task in Timer", "timerShowNextTask", prod ? false : true, "Will show the next task in a list at the bottom of the Timer screen. This can help mentally prepare but can also disturb the focus on the current task"); //TODO make timerShowNextTask a numerical value to show 0/1/2/3 next tasks and start one by clicking on it

    static PrefEntry timerShowRemainingForNextTask
            = new PrefEntry("Show remaining time for next-coming task in Timer", "timerShowRemainingForNextTask", prod ? false : true, "**Will show the next task in a list at the bottom of the Timer screen. This can help mentally prepare but can also disturb the focus on the current task"); //TODO make timerShowNextTask a numerical value to show 0/1/2/3 next tasks and start one by clicking on it

//    static PrefEntry automaticallyStartTimerForNewItem = new PrefEntry("automaticallyStartTimerForNewItem", true);
    //TODO! setting update interval to minutes (60sec) should also prevent showing seconds in timer display (otherwise you may end up with eg 36s frozen, but changing sometimes due to relative imprecision of timer update!! and only minutes advancing)
    static PrefEntry timerShowSecondsInTimer
            = new PrefEntry("Show seconds in Timer", "showSecondsInTimer", prod ? true : true, "Hiding seconds may feel less stressful but doesn't visually show that the Timer is running");

    static PrefEntry timerBuzzerActive
            = new PrefEntry("Buzz at regular intervals to remind that Timer is running", "timerBuzzerActive", prod ? false : true, ""); //, "Reminder vibration interval when Timer is running");
    static PrefEntry timerBuzzerInterval
            = new PrefEntry("Buzz interval when Timer is running (minutes)", "timerBuzzerInterval", (int) (prod ? 10 : 1), ""); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerUpdateInterval
            = new PrefEntry("Update Timer elapsed time every N seconds", "timerUpdateInterval", (int) (prod ? 1 : 1), ""); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds
            = new PrefEntry("Minimum timer threshold (seconds)", "timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds", (int) (prod ? 15 : 5), "Tasks will not be marked ongoing and the time will not be saved until after this number seconds. Useful to avoid that skipping a task in Timer marks it ongoing"); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerShowEffortEstimateDetails
            = new PrefEntry("Show Estimate, total time and Remaining time for task", "timerShowEffortEstimateDetails", prod ? false : false,
                    "Timer will show details on estimate etc**"); //, "Reminder vibration interval when Timer is running");

    //TODO!!!! in Timer: check waiting date when skipping, or not, waiting tasks
    static PrefEntry timerIncludeWaitingTasks
            = new PrefEntry("Time Waiting tasks even before the waiting date is met", "timerDoNotSkipWaitingTasks", prod ? false : false,
                    "Normally, the Timer will skip Waiting tasks until the waiting date. This setting will include them**");

    static PrefEntry timerIncludeDoneTasks
            = new PrefEntry("Time Done tasks", "timerIncludeDoneTasks", prod ? false : false,
                    "Normally, the Timer will skip Done tasks. This setting will include them**");

//    static PrefEntry timerBuzzerActive = 
//            new PrefEntry("Buzz when Timer is running","timerBuzzerActive", false, "Buzzer active when timer is running");
//    static PrefEntry timerShowDetailedEstimateInfo = 
//            new PrefEntry("timerDetailedEstimateInfo", false, "Show detailed information about task Estimated time, Total time and Remaining time");
//    static PrefEntry timerShowTaskContext = 
//            new PrefEntry("timerShowTaskContext", false, "Show the List and/or Project the task belongs to");
    static PrefEntry timerShowTotalActualInTimer
            = new PrefEntry("Show total time in timer", "timerShowTotalActualInTimer", prod ? false : false,
                    "Timer always shows the total amount of elapsed time, not just from this timer session");

    static PrefEntry keepScreenAlwaysOnInApp
            = new PrefEntry("Keep screen on when app is active", "keepScreenAlwaysOnInApp", prod ? false : false,
                    "Disables device screen saver while app is in foreground");
    static PrefEntry timerKeepScreenAlwaysOnInTimer
            = new PrefEntry("Keep screen on when Timer is active", "timerKeepScreenAlwaysOnInTimer", prod ? false : false,
                    "Prevents device screen saver from closing while Timer is active");

    static PrefEntry timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem
            = new PrefEntry("Show popup to ask to update Remaining time after timing a task", "timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem",
                    prod ? false : true, "Helps you not forget to update remaining when you stop working on a task**");

    static PrefEntry timerAlwaysExpandListHierarchy
            = new PrefEntry("Show task's parent list & project", "timerAlwaysExpandListHierarchy", prod ? true : true,
                    "**");

//    static PrefEntry timerMaxTimerDurationInHoursXXX
//            = new PrefEntry("Maximum time the Timer is allowed to run (hours)", "timerMaxTimerDurationInHours", 12,
//                    "**DOESN'T MAKE SENSE since you'd still hae to fxi whatever time the timer stopped at*** Timer stops automatically when this maximum in reached. Can be used to avoid the Timer running wild since a popup is shown when reached. NB. Whatever this value, The Timer can never exceed 23h59m.");
    static PrefEntry timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList
            = new PrefEntry("Show Timer popup when no more tasks", "timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList", prod ? true : true,
                    "**");

    static PrefEntry timerAlwaysStartWithNewTimerInSmallWindow
            = new PrefEntry("Always show new timers in current screenshow", "timerAlwaysShowNewTimerInSmallWindow", prod ? false : true,
                    "Instead of showing a started timer in full screen...**");

    static PrefEntry timerEnableShowingSmallTimerWindow
            = new PrefEntry("Show small timer in other views", "timerEnableSmallTimerWindow", prod ? true : true,
                    "**");

    static PrefEntry timerItemOrItemListCanInterruptExistingItemOrItemList
            = new PrefEntry("Possible to start **Always show new timers in current screenshow", "timerItemOrItemListCanInterruptExistingItemOrItemList",
                    prod ? true : true, "Instead of showing a started timer in full screen...**");

    static PrefEntry timerInterruptTaskCanInterruptAlreadyRunningInterruptTask
            = new PrefEntry("Possible to start **Always show new timers in current screenshow (PRO)", "timerInterruptTaskCanInterruptAlreadyRunningInterruptTask",
                    prod ? false : true, "Instead of showing a started timer in full screen...**");

    static PrefEntry timerCanBeSwipeStartedEvenOnInvalidItem
            = new PrefEntry("Allow to swipe-start Timer on any task/project **", "timerCanBeSwipeStartedEvenOnInvalidItem", prod ? true : true,
                    "Start Timer directly on any swiped task or project"); //be force-started on tasks which are normally skipped, e.g. Done, Cancelled or Waiting tasks (depend on settings)**");
    static PrefEntry timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject
            = new PrefEntry("If the currently timed task is no longer in the timed list or project, then start Timer on first task", "timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject",
                    prod ? false : false, "**");
    static PrefEntry enableTimerToRestartOnLists
            = new PrefEntry("Enable Timer to restart over on a timed list, for example if the previously timed task is no longer in the list",
                    "enableTimerToRestartOnLists", prod ? false : false,
                    "**");
    static PrefEntry timerAskBeforeStartingOnNewElement
            = new PrefEntry("Ask before starting Timer starts on new task or list always If Possible to start **Always show new timers in current screenshow",
                    "timerAskBeforeStartingOnNewElement", prod ? true : true, "**");
    static PrefEntry timerMayPauseAlreadyRunningTimer
            = new PrefEntry("Starting Timer on a new task or list will pause an already running timer", "timerMayPauseAlreadyRunningTimer", prod ? true : true,
                    "**");
    static PrefEntry hideIconsInEditTaskScreen
            = new PrefEntry("Hiden the icons when editing a task",
                    "hideIconsInEditTaskScreen", prod ? false : false, "Get a cleaner or more space-efficient look");

    //COMMENTS
    static PrefEntry commentsAddToBeginningOfComment
            = new PrefEntry("Insert time-stamped comments at beginning of comment", "commentsAddToBeginningOfComment", prod ? false : false, "Add automatic comments to beginning of comment instead of at the end");

//    static PrefEntry commentsAddTimedEntriesWithDateButNoTime
//            = new PrefEntry("commentsAddTimedEntriesWithDateButNoTime", false);
    static PrefEntry commentsAddTimedEntriesWithDateANDTime
            = new PrefEntry("Add time stamp to Comments time stamps", "commentsAddTimedEntriesWithDateANDTime", prod ? false : false, "When adding time stamp to Comments, also add current time");

    static PrefEntry taskMaxSizeInChars
            = new PrefEntry("**", "taskMaxSizeInChars", prod ? false : 255, "**"); //TODO make max task size a PRO subscription dependendant setting

    static PrefEntry commentMaxSizeInChars
            = new PrefEntry("**", "commentMaxSizeInChars", prod ? 1024 : 512, "**"); //TODO make max comment size a PRO subscription dependendant setting

    //CATEGORY
    static PrefEntry showCategoryDescriptionInCategoryList
            = new PrefEntry("**", "showCategoryDescriptionInCategoryList", prod ? false : false, "Show Category description in category lists");
//    static PrefEntry categoryAllowDuplicateNamesDescriptionInCategoryList
//            = new PrefEntry("**", "showCategoryDescriptionInCategoryList", false, "Show Category description in category lists");
    static PrefEntry addNewCategoriesToBeginningOfCategoryList
            = new PrefEntry("Add new categories to the beginning of the list", "addNewCategoriesToBeginningOfCategoryList", prod ? false : false, "**");

    //ALARMS
//    static PrefEntry alarmLastDateUptoWhichAlarmsHaveBeenSet = new PrefEntry("alarmLastDateUptoWhichAlarmsHaveBeenSet", new Date(0));
    static PrefEntry alarmsActivatedOnThisDevice
            = new PrefEntry("Enable Reminders on this device", "alarmsActivatedOnThisDevice", prod ? true : true, "**"); //alarms activated by default

    static PrefEntry alarmSoundFile
            = new PrefEntry("**", "alarmSoundFile", "notification_sound_Cuckoo_bird_sound.mp3", "**");
    static PrefEntry alarmPlayBuiltinAlarmSound
            = new PrefEntry("Use built in alarm sound", "alarmPlayBuiltinAlarmSound", prod ? true : true, "When adjusting the snooze time of a task manually, the value is kept as default for other tasks, making it easy to snooze several tasks with the same duration. When not set, the Picker is initialized with the default value"); //alarms activated by default

    static PrefEntry alarmIntervalBetweenAlarmsRepeatsMillisInMinutes
            = new PrefEntry("Minutes between reminder notifications", "alarmIntervalBetweenAlarmsRepeatsMillisInMinutes", prod ? 5 : 1, "Defines the minutes between the repeats of a reminder. Chose 0 to not repeat."); //alarms activated by default

    static PrefEntry alarmDefaultSnoozeTimeInMinutes
            = new PrefEntry("Snooze time in minutes", "alarmDefaultSnoozeTimeInMinutes", prod ? 10 : 1, "Set the time an alarm is snoozed"); //alarms activated by default
    static PrefEntry alarmReuseIndividuallySetSnoozeDurationForLongPress
            = new PrefEntry("Remember Snooze time set on long press", "alarmReuseIndividuallySetSnoozeDurationForLongPress", prod ? false : true, "When adjusting the snooze time of a task manually, the value is kept as default for other tasks, making it easy to snooze several tasks with the same duration. When not set, the Picker is initialized with the default value"); //alarms activated by default
    static PrefEntry alarmReuseIndividuallySetSnoozeDurationForNormalSnooze
            = new PrefEntry("If snooze time has been set on long press, use that value ", "alarmReuseIndividuallySetSnoozeDurationForNormalSnooze",
                    prod ? false : true, "When adjusting the snooze time of a task manually, the value is kept as default for other tasks, making it easy to snooze several tasks with the same duration. When not set, the Picker is initialized with the default value. NB. The value is only kept while in the same screen"); //alarms activated by default

    static PrefEntry alarmFutureIntervalInWhichToSetAlarmsInHours
            = new PrefEntry("**", "alarmFutureIntervalInWhichToSetAlarmsInHours", 24, "How many days ahead are local notifications activated**"); //alarms activated by default

    static PrefEntry alarmMaxNumberItemsForWhichToSetupAlarms
            = new PrefEntry("**", "alarmMaxNumberItemsForWhichToSetupAlarms", 32, "Maximum number of tasks for which to set up alarms for the period alarmFutureIntervalInWhichToSetAlarmsInDays. Used to limit how many Items are fetched in background from"); //alarms activated by default

    static PrefEntry alarmTimeOfDayWhenToUpdateAlarmsInMinutes
            = new PrefEntry("**", "alarmTimeOfDayWhenToUpdateAlarmsInMinutes", 10, "How many days ahead are alarmsminutes should an alarm snooze"); //10 = 10 minutes after midnight

    static PrefEntry alarmShowDueTimeAtEndOfNotificationText
            = new PrefEntry(Format.f("Include {0} in notifications", Item.DUE_DATE), "alarmShowDueTimeAtEndOfNotificationText", prod ? true : true,
                    "**");
    static PrefEntry alarmShowAlarmTimeAtEndOfNotificationText
            = new PrefEntry(Format.f("Include {0} in notifications", Item.ALARM_DATE), "alarmShowAlarmTimeAtEndOfNotificationText", prod ? false : true,
                    "**");
    static PrefEntry alarmShowWaitingTimeAtEndOfNotificationText
            = new PrefEntry(Format.f("Include {0} in notifications", Item.WAIT_UNTIL_DATE), "alarmShowWaitingTimeAtEndOfNotificationText", prod ? true : true,
                    "**");
    static PrefEntry alarmShowLastCommentLineInWaitingNotificationsZZZ //TODO: //UI
            = new PrefEntry(Format.f("Show last {0} line in {1} notifications", Item.COMMENT, ItemStatus.WAITING.getName()), "alarmShowLastCommentLineInWaitingNotifications",
                    prod ? true : true, "**");
    static PrefEntry alarmShowWaitingAlarmTimeAtEndOfNotificationText
            = new PrefEntry(Format.f("Include {0} in notifications", Item.WAITING_ALARM_DATE), "alarmShowWaitingAlarmTimeAtEndOfNotificationText",
                    prod ? false : true, "**");
    static PrefEntry alarmShowSnoozeUntilTimeAtEndOfNotificationText
            = new PrefEntry(Format.f("Include {0} in notifications", Item.WAITING_ALARM_DATE), "alarmShowSnoozeUntilTimeAtEndOfNotificationText",
                    prod ? false : true, "**");

    static PrefEntry alarmDaysAheadToFetchFutureAlarms
            = new PrefEntry("**", "alarmDaysAheadToFetchFutureAlarms", 30, "**"); //10 = 10 minutes after midnight
    static PrefEntry alarmRoundSnoozeTimeDownToMinutes
            = new PrefEntry("", "alarmDaysAheadToFetchFutureAlarms", prod ? false : true, "**"); //mainly for testing?! to have snooze expire on the top of the minute

    //ITEM
    static PrefEntry checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress
            = new PrefEntry("Show " + Item.STATUS + " menu on click", "checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress", prod ? false : false, "**");

//    static PrefEntry changeSubtasksStatusWithoutConfirmationXXX
//            = new PrefEntry("**", "changeSubtasksStatusWithoutConfirmation", false, "**");
//
//    static PrefEntry alwaysShowSubtasksExpandedInScreenItemXXX
//            = new PrefEntry("**", "alwaysShowSubtasksExpandedInScreenItem", false, "**");
//    static PrefEntry neverChangeProjectsSubtasksWhenChangingProjectStatusXXX
//            = new PrefEntry("**", "neverChangeProjectsSubtasksWhenChangingProjectStatus", false, "**");
    static PrefEntry waitingAskToSetWaitingDateWhenMarkingTaskWaiting
            = new PrefEntry("Popup to set " + Item.WAIT_UNTIL_DATE + " and " + Item.WAITING_ALARM_DATE + " when setting a task to " + ItemStatus.WAITING,
                    "waitingAskToSetWaitingDateWhenMarkingTaskWaiting", prod ? false : true, "**does nothing if both of the waiting dates are already set");

    static PrefEntry waitingSetStatusWaitingWhenSettingDateWhenWaiting
            = new PrefEntry("Automatically set task to " + ItemStatus.WAITING+" when setting " + Item.DATE_WHEN_SET_WAITING ,
                    "waitingSetStatusWaitingWhenSettingDateWhenWaiting", prod ? false : true, "**");
    static PrefEntry waitingSetStatusAwayFromWaitingWhenRemovingSettingDateWhen
            = new PrefEntry("Automatically set task status when deleting " + Item.DATE_WHEN_SET_WAITING ,
                    "waitingSetStatusAwayFromWaitingWhenRemovingSettingDateWhen", prod ? false : true, "**");
    static PrefEntry waitingSetStatusEtcWhenSettingWaitingUntilDate
            = new PrefEntry("Automatically update task when setting " + Item.WAIT_UNTIL_DATE ,
                    "waitingSetStatusEtcWhenSettingWaitingUntilDate", prod ? false : true, "**");
    static PrefEntry waitingSetStatusEtcWhenDeletingWaitingUntilDate
            = new PrefEntry("Automatically update task when deleting " + Item.WAIT_UNTIL_DATE ,
                    "waitingSetStatusEtcWhenDeletingWaitingUntilDate", prod ? false : true, "**");
    static PrefEntry waitingSetWaitingAlarmWhenSettingWaitingUntilDate
            = new PrefEntry("Automatically set a Waiting reminder " + Item.WAIT_UNTIL_DATE ,
                    "waitingSetWaitingAlarmWhenSettingWaitingUntilDate", prod ? false : true, "**");
    static PrefEntry waitingSetWaitingAlarmMinutesBeforeWaitingUntilDate
            = new PrefEntry("Automatically set a Waiting reminder " + Item.WAIT_UNTIL_DATE ,
                    "waitingSetWaitingAlarmWhenSettingWaitingUntilDate", prod ? 60 : 1, "**");

    static PrefEntry updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem
            = new PrefEntry("Copy initial values between " + Item.EFFORT_ESTIMATE + " and " + Item.EFFORT_REMAINING + " the first time these values are being edited for a task",
                    "updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItem", prod ? false : true, "**");

    static PrefEntry addCommentWhenRemaningIsSetToZeroWhenTaskBecomesProject
            = new PrefEntry(Format.f("When a task has {0 remaining} set and is made into a project, add a comment with the old value",Item.EFFORT_REMAINING),
                    "addCommentWhenRemaningIsSetToZeroWhenTaskBecomesProject", prod ? false : true, "**");

    //NB probably not really useful since the easy way to update the other field is to delete it before setting the other... More intuitive (difficult to explain why different values cannot be set)
//    static PrefEntry alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItemXXX
//            = new PrefEntry("Always copy values between " + Item.EFFORT_ESTIMATE + " and " + Item.EFFORT_REMAINING + " the first time these values are being edited for a task",
//                    "alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItem", false, "NB. Makes it impossible to set different initial values for two fields");
    static PrefEntry showDetailsForAllTasks
            = new PrefEntry("Always show details for tasks", "showDetailsForAllTasks", prod ? false : false, "**");

    static PrefEntry itemMaxNbSubTasksToChangeStatusForWithoutConfirmation
            = new PrefEntry("Confirm changing status for this many subtasks", "itemMaxNbSubTasksToChangeStatusForWithoutConfirmation",
                    prod ? 2 : 2, "For a project, ask for confirmation when changing the status of this many subtasks");

    static PrefEntry askToEnterActualIfMarkingTaskDoneOutsideTimer
            = new PrefEntry(Format.f("Enter {0 Actual effort} on {1 task status}", Item.EFFORT_ACTUAL, ItemStatus.DONE.getName()), "askToEnterActualIfMarkingTaskDoneOutsideTimer",
                    prod ? false : true, "When marking a task Done outside the timer, show popup to enter Actual effort");
    static PrefEntry askToEnterActualIfMarkingTaskWaitingOutsideTimer
            = new PrefEntry(Format.f("Enter {0 Actual effort} on {1 task status}", Item.EFFORT_ACTUAL, ItemStatus.WAITING.getName()), "askToEnterActualIfMarkingTaskWaitingOutsideTimer",
                    prod ? false : true, "When marking a task Done outside the timer, show popup to enter Actual effort");
//    static PrefEntry askToEnterActualIfMarkingTaskDoneOutsideTimerOnlyWhenActualIsZeroXXX
//            = new PrefEntry("Enter Actual effort when none was set", "askToEnterActualIfMarkingTaskDoneOutsideTimerOnlyWhenActualIsZero",
//                    true, "When marking a task Done outside the timer, show popup to enter Actual effort");

    //TEMPLATES
    static PrefEntry askBeforeInsertingTemplateIntoAndUnderAnAlreadyCreatedItem //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Confirm before inserting a template into an existing task", "askBeforeInsertingTemplateIntoAndUnderAnAlreadyCreatedItem",
                    prod ? false : false, "**");
    static PrefEntry showTemplateListAfterCreatingNewTemplateFromExistingProject //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Show the list of templates after creating new template from existing project", "showTemplateListAfterCreatingNewTemplateFromExistingProject",
                    prod ? true : true, "Allows you to immediately make edits to the just created template"); //"helpful to easily make small adjustments
    static PrefEntry maxNbTemplatesAllowedToChoseForInsertion //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Maximum number of templates that can chosen for insertion", "maxNbTemplatesAllowedToChoseForInsertion",
                    prod ? 1 : 3, "Allows you to immediately make edits to the just created template"); //"helpful to easily make small adjustments
    static PrefEntry addTemplateTaskTextToEndOfExistingTaskText = new PrefEntry("Add template task text when inserting a template", "addTemplateTaskTextToEndOfExistingTaskText",
            prod ? true : true, "**"); //"helpful to easily make small adjustments
    static PrefEntry useActualAsEstimateForTemplatesOrCopies = 
            new PrefEntry(Format.f("Use a non-zero {0 actual} as {1 estimate} for task/project copies or Templates",Item.EFFORT_ACTUAL, Item.EFFORT_ESTIMATE)                     , 
            "useActualAsEstimateForTemplatesOrCopies",
            prod ? true : true, "**use"); //"helpful to easily make small adjustments
//    static PrefEntry keepDoneTasksVisibleTheDayTheyreCompletedXXX //TODO!!! No, only ask if overwriting an alreadyd defined value!
//            //            = new PrefEntry("If Completed tasks are hidden, keep them visible until midnight of the day they were completed", //"Don't hide Completed tasks the day they were completed
//            = new PrefEntry("Show just Completed tasks until midnight", //"Don't hide Completed tasks the day they were completed
//                    "keepDoneTasksVisibleTheDayTheyreCompleted",
//                    true, "**");
    static PrefEntry itemDueDateDefaultDaysAheadInTime //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Default number of days ahead in time for Due dates (0 disables)", //"Don't hide Completed tasks the day they were completed
                    "itemDueDateDefaultDaysAheadInTime",
                    prod ? 0 : 7, "**");

    static PrefEntry itemDefaultAlarmTimeBeforeDueDateInMinutes //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Default for how long before the Due date the alarm is set (", //"Don't hide Completed tasks the day they were completed
                    "itemDefaultAlarmTimeBeforeDueDateInMinutes",
                    prod ? 0 : 60, "**");
    static PrefEntry itemWaitingDateDefaultDaysAheadInTime //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Default number of days ahead in time for Waiting dates (0 disables)", //"Don't hide Completed tasks the day they were completed
                    "itemWaitingDateDefaultDaysAheadInTime",
                    prod ? 0 : 7, "**");
    static PrefEntry itemWaitingAlarmDefaultDaysBeforeWaitingDate //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Default number of days ahead in time for Waiting dates (0 disables)", //"Don't hide Completed tasks the day they were completed
                    "itemWaitingAlarmDefaultDaysBeforeWaitingDate",
                    prod ? 0 : 1, "**");

    static PrefEntry itemAlwaysCancelSubtaskEditsWhenCancellingEditOfProject
            = new PrefEntry("When canceling edits of a project, always cancel added/deleted subtasks",
                    "itemAlwaysCancelSubtaskEditsWhenCancellingEditOfProject",
                    prod ? true : false, "If you cancel editing of a project, you will normally be asked if you want to cancel added/deleted subtasks (including from templates). Setting this option automatically cancels those edits without asking for confirmation");

    // ************** inherit values from owning Project *************
    final static String INHERITS = "Subtasks inherit ";
    static PrefEntry itemInheritOwnerProjectProperties
            = new PrefEntry("Subtasks inherit properties from their project", "itemInheritOwnerProjectProperties",
                    prod ? false : true, "Subtasks inherit properties due date**, priorities etc** from the project they belong to");
    //TODO!! set false for easy startup

    static PrefEntry itemInheritOwnerProjectChallenge
            //            = new PrefEntry("Subtasks inherit "+Item.CHALLENGE+" from their project", "itemInheritOwnerProjectChallenge", true, "**");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.CHALLENGE), "itemInheritOwnerProjectChallenge",
                    prod ? false : false, INHERITS + Item.CHALLENGE);

    static PrefEntry itemInheritOwnerProjectStarred
            //            = new PrefEntry(Format.f("Subtasks inherit {0} from their project","propert", "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.STARRED), "itemInheritOwnerProjectStarred",
                    prod ? false : true, INHERITS + Item.STARRED);

    static PrefEntry itemInheritOwnerProjectPriority
            //            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectPriority", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.PRIORITY), "itemInheritOwnerProjectPriority",
                    prod ? false : true, INHERITS + Item.PRIORITY);

    static PrefEntry itemInheritOwnerProjectDreadFun
            //            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectDreadFun", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.FUN_DREAD), "itemInheritOwnerProjectDreadFun",
                    prod ? false : true, INHERITS + Item.FUN_DREAD);

    static PrefEntry itemInheritOwnerProjectImportance
            //            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectImportance", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.IMPORTANCE), "itemInheritOwnerProjectImportance",
                    prod ? false : true, INHERITS + Item.IMPORTANCE);

    static PrefEntry itemInheritOwnerProjectUrgency
            //            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectUrgency", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.URGENCY), "itemInheritOwnerProjectUrgency",
                    prod ? false : true, INHERITS + Item.URGENCY);

    static PrefEntry itemInheritOwnerProjectDueDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.DUE_DATE), "itemInheritOwnerProjectDueDate",
                    prod ? false : true, INHERITS + Item.DUE_DATE);

    static PrefEntry itemInheritOwnerProjectExpiresOnDateXXX
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.EXPIRES_ON_DATE), "itemInheritOwnerProjectExpiresOnDate",
                    prod ? false : false,
                    INHERITS + Item.EXPIRES_ON_DATE);

    static PrefEntry itemInheritOwnerProjectStartByDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.START_BY_TIME), "itemInheritOwnerProjectStartByDate",
                    prod ? false : true, INHERITS + Item.START_BY_TIME);

    static PrefEntry itemInheritOwnerProjectWaitingTillDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.WAIT_UNTIL_DATE), "itemInheritOwnerProjectWaitingTillDate",
                    prod ? false : true, INHERITS + Item.WAIT_UNTIL_DATE);

    static PrefEntry itemInheritOwnerProjectDateWhenSetWaitingZZZ
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.DATE_WHEN_SET_WAITING), "itemInheritOwnerProjectDateWhenSetWaiting",
                    prod ? false : false,
                    INHERITS + Item.DATE_WHEN_SET_WAITING);

    static PrefEntry itemInheritOwnerProjectHideUntilDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.HIDE_UNTIL), "itemInheritOwnerProjectHideUntilDate",
                    prod ? false : true, INHERITS + Item.HIDE_UNTIL);

    static PrefEntry itemInheritOwnerProjectTemplateXXX //doesn't make sense to inherit this, each template subtask should have this set directly?! 
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.TEMPLATE), "itemInheritOwnerProjectTemplate",
                    prod ? false : false, INHERITS + Item.TEMPLATE);
    static PrefEntry itemInheritEvenDoneSubtasksInheritOwnerValues
            = new PrefEntry(Format.f("Even completed subtasks inherit values from their project", Item.TEMPLATE), "itemInheritEvenDoneSubtasksInheritOwnerValues",
                    prod ? false : false, INHERITS + Item.TEMPLATE);

//    static PrefEntry useDefaultFilterInItemsWhenNoneDefinedXXX
//            = new PrefEntry("By default, hide Done and Cancelled tasks in lists", "useDefaultFilterInItemsWhenNoneDefined", true, "**");
    // ************** END inherit values from owning Project *************
    static PrefEntry itemExtractRemainingEstimateFromStringInTaskText
            = new PrefEntry("Extract remaining time from task text", "itemExtractRemainingEstimateFromStringInTaskText",
                    prod ? false : true, "**"); //TODO: REALLY default false?!

    static PrefEntry itemKeepRemainingEstimateStringInTaskText
            = new PrefEntry("Keep remaining time in task text", "itemKeepRemainingEstimateStringInTaskText",
                    prod ? false : false, "**");

//    static PrefEntry itemProjectPropertiesDerivedFromSubtasks
//            = new PrefEntry("Project properties like [STARTED_ON] shows values from subtasks", "itemProjectPropertiesDerivedFromSubtasks", true, "**");
    static PrefEntry workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime
            = new PrefEntry(Format.f("Prioritize {0} from Categories", Item.WORTIME), "workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime",
                    prod ? true : true, "If one of a tasks categories has work time, use that to calculate the finish time instead of the work time of the Porject or List the task belongs to");

    //INTERNAL / TECHNICAL / CACHE
    static PrefEntry backgroundFetchIntervalInSeconds = new PrefEntry("**", "backgroundFetchIntervalInSeconds",
            1 * MyDate.HOUR_IN_MILISECONDS / 1000, "interval for updating eg local notifications and badge count when app is in background**");

    static PrefEntry cacheDynamicSize = new PrefEntry("**", "cacheDynamicSize", 10000, "number of tasks, lists, categories etc cached**");
    static PrefEntry cacheDynamicSizeWorkSlots = new PrefEntry("**", "cacheDynamicSizeWorkSlots", 10000, "number of WorkSlots cached**");

    static PrefEntry cacheLocalStorageSize
            = new PrefEntry("**", "cacheLocalStorageSize", 10000, "deactivated if 0** need to implement Externalizable before this will work!!");

    static PrefEntry cacheLocalStorageSizeWorkSlots
            = new PrefEntry("**", "cacheLocalStorageSizeWorkSlots", 10000, "deactivated if 0** need to implement Externalizable before this will work!!");

    static PrefEntry cacheMaxNumberParseObjectsToFetchInQueries
            = new PrefEntry("**", "cacheMaxNumberParseObjectsToFetchInQueries", 100000, "deactivated if 0** need to implement Externalizable before this will work!!");

    static PrefEntry cacheLoadChangedElementsOnAppStart
            = new PrefEntry("cacheLoadChangedElementsOnAppStart (INTERNAL)", "cacheLoadChangedElementsOnAppStart", true, "used to speed up app start during testing - NOT END USER");

    static PrefEntry showObjectIdsInEditScreens
            = new PrefEntry("Show ObjectIds when editing", "showObjectIdsInEditScreens",
                    prod ? false : true, "show internal unique ID when editing - NOT END USER");

    static PrefEntry showSourceItemInEditScreens
            = new PrefEntry("For a copy, show the original", "showSourceItemInEditScreens",
                    prod ? false : true, "**");

    static PrefEntry showSourceWorkSlotInEditScreens
            = new PrefEntry("For a copy, show the original", "showSourceWorkSlotInEditScreens",
                    prod ? false : true, "**");

    static PrefEntry showDebugInfoInLabelsEtc
            = new PrefEntry("Show debug info, e.g. add ^ to subtask", "showDebugInfoInLabelsEtc",
                    prod ? false : true, "** - NOT END USER");

    static PrefEntry reloadChangedDataInBackground
            = new PrefEntry("Refresh changed data in background", "reloadChangedDataInBackground",
                    prod ? false : false, "** - NOT END USER");

//    static PrefEntry defaultIconSizeInMM = new PrefEntry("Default icons size in millimeters", "defaultIconSizeInMM", Float.parseFloat(Form.getUIManager().getThemeConstant("menuImageSize", "4.5")), "** - NOT END USER");
    static PrefEntry defaultIconSizeInMM = new PrefEntry("Default icons size in millimeters",
            "defaultIconSizeInMM", 6.0f, "** - NOT END USER"); //NB! Also set in theme.css/#Constants: overflowImageSize and menuImageSize

    static PrefEntry defaultIconGapInMM
            = new PrefEntry("Default icons size in 0.1 millimeters", "defaultIconGapInMM", 0.5f, "** - NOT END USER");

    static BackCommandPolicy defaultBackPolicyXXX = BackCommandPolicy.AS_ARROW;

    //ITEMLIST
    static PrefEntry useDefaultFilterInItemListsWhenNoneDefined
            = new PrefEntry("By default, hide Done and Cancelled tasks in lists", "useDefaultFilterInItemListsWhenNoneDefined",
                    prod ? true : true, "**");
    static PrefEntry listDefaultHeaderForUndefinedValue //default StickyHeader to show when no value is defined
            = new PrefEntry("By default, hide Done and Cancelled tasks in lists", "listDefaultHeaderForUndefinedValue",
                    prod ? "No value" : "No value", "**");

    //LOOK AND FEEL
    static PrefEntry themeNameWithoutBackslash
            = new PrefEntry("**", "themeNameWithoutBackslash", "theme", "name of the graphical theme");
    static PrefEntry titleAutoSize
            = new PrefEntry("Reduce Screen title font size for long texts", "titleAutoSize", prod ? true : true, "**");
    static PrefEntry scrollToolbarOffScreenOnScrollingDown
            = new PrefEntry("Hide the toolbar at top of the screen when scrolling a screen down", "scrollToolbarOffScreenOnScrollingDown",
                    prod ? true : true, "**"); //TODO: but not working?!
    static PrefEntry firstDoubleTapScrollsToBottomOfScreen
            = new PrefEntry("First statusbar doubletap scrolls to bottom of list instead of top",
                    "firstDoubleTapScrollsToBottomOfScreen", prod ? true : true, "First time the status bar of a scrolled task list is doubleHide the toolbar at top of the screen when scrolling a screen down");

    //OTHER / SYSTEM-LEVEL
    static PrefEntry enableCancelInAllScreens
            = new PrefEntry("**", "enableCancelInAllScreens", false, "temporarily used to disable Cancel everywhere due to problems eg with too complex to Cancel when inserting Templates with subtasks");
    static PrefEntry dateShowDatesInUSFormat
            = new PrefEntry("**", "dateShowDatesInUSFormat", prod ? false : false, "**");
    static PrefEntry disableGoogleAnalytics
            = new PrefEntry("**", "disableGoogleAnalytics", prod ? false : false, "diable sending anonymous usage pattern information to Google Analytics (used to help improve TDC");
    static PrefEntry enableRepairCommandsInMenus
            = new PrefEntry("enableRepairCommandsInMenus", "enableRepairCommandsInMenus", prod ? false : true, "**");
    static PrefEntry pinchInsertEnabled
            = new PrefEntry("Enable pinch insert in lists", "pinchInsertEnabled", prod ? true : true, "**");
    static PrefEntry pinchInsertActivateEditing
            = new PrefEntry("Automatically start editing new task after pinch insert ", "pinchInsertActivateEditing", prod ? true : true, "**");
    static PrefEntry screenEnableDisplayRotationToLandscape
            = new PrefEntry("Enable screen rotation to landscape", "screenEnableDisplayRotation", prod ? false : false,
                    "Rotation to landscape mode is disabled by default, setting this option enables it");
    static PrefEntry enableShowingSystemInfo
            = new PrefEntry("Show additional system information like unique identified", "enableShowingSystemInfo", prod ? false : true, "**");
    static PrefEntry enableSafeArea
            = new PrefEntry("Do not allow content on top/bottom part of iPhone X type devices (safe area)", "enableSafeArea", prod ? true : true, "**");
    static PrefEntry hideStatusBar
            = new PrefEntry("Hide the status bar at the top of the screen", "hideStatusBar", prod ? false : false, "**");

//PINCH
    static PrefEntry pinchAdjustUpper //used to play with/tune the adjustment factor used if pinch fingers are far apart
            = new PrefEntry("Pinch adjust value upper (sum upper+lower<=10!!)", "pinchAdjustUpper", 4, "**");
    static PrefEntry pinchAdjustLower
            = new PrefEntry("Pinch adjust value upper", "pinchAdjustLower", 3, "**");

    static PrefEntry deleteLocalStorageIfRestartedQuickly
            = new PrefEntry("Delete local copy of data if app is restarted quickly", "deleteLocalStorageIfRestartedQuickly",
                    prod ? true : false, "**");
    static PrefEntry deleteLocalStorageIfRestartedBeforeSeconds
            = new PrefEntry("Delete local copy of data if app is within this many seconds", "deleteLocalStorageIfRestartedBeforeSeconds",
                    prod ? 8 : 15, "**");

    //SECURITY / ENCRYPTION - disable in code for now
    static PrefEntry encryptTaskTextAndComments
            = new PrefEntry("Scramble task text and comments stored in the cloud", "encryptTaskTextAndComments", prod ? false : true,
                    "Makes the text unreadable to a human (NB. The text is not encrypted and there is no risk of losing the text by forgetting a password");
    static PrefEntry encryptTaskTextAndCommentsSecret
            = new PrefEntry("Delete local copy of data if app is within this many seconds", "encryptTaskTextAndComments", "0923jsdo932", "**");

    //REPEATRULE
    static PrefEntry repeatMaxInterval = new PrefEntry("repeatMaxInterval**", "repeatMaxInterval", prod ? 365 : 365, "maximun value for repeat interval**");
    static PrefEntry repeatMaxNumberFutureInstancesToGenerateAhead
            = new PrefEntry("Maximum number of future repeats allowed", "repeatMaxNumberFutureInstancesToGenerateAhead", prod ? 5 : 10, "**");
//    static PrefEntry repeatMaxNumberOfRepeatsToGenerate
//            = new PrefEntry("To avoid that too many repeats get generated and overflod the server or the app, 0 disables", "repeatMaxNumberOfRepeatsToGenerate", 20,
//                    "more of an internal limitation for now**"); //TODO: what happens if the max is reached? Will the algorithms still work?
    static PrefEntry repeatMaxNumberOfRepeatsAllowed //TODO!!!: update so that -1 means infinite (or add setting to disable this maximu)
            = new PrefEntry("Max number of repeats enabled in picker", "repeatMaxNumberOfRepeatsAllowed", prod ? 100 : 100, "**");
    static PrefEntry repeatMaxNumberFutureDaysToGenerateAheadZZZ = new PrefEntry("repeatMaxNumberFutureDaysToGenerateAhead**", "repeatMaxNumberFutureDaysToGenerateAhead",
            prod ? 31 : 31, "**");
    static PrefEntry maxNumberRepeatInstancesToDeleteWithoutConfirmation
            = new PrefEntry("Ask for confirmation if deleting more repeat instances that this", "maxNumberRepeatInstancesToDeleteWithoutConfirmation",
                    prod ? false : 3, "**");
    static PrefEntry repeatSetRelativeFieldsWhenCreatingRepeatInstances
            = new PrefEntry("Set Alarm, Hide until, Start by and Expires on fields relative to Due date", "repeatSetRelativeFieldsWhenCreatingRepeatInstances",
                    prod ? true : true, "set the relative date fields like Alarm/HideUntil/StartBy/Autoexpire** etc to same time before/after due date**");
    static PrefEntry repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule
            = new PrefEntry("Reuse existing instances when changing " + RepeatRuleParseObject.REPEAT_RULE, "repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule",
                    prod ? true : true, "When changing a repeat rule, reuse any task instances that already exist, this will keep any edits to such instances");

    static PrefEntry repeatHidePreviousTasksDetails
            = new PrefEntry("When show previously generated repeat instances, expand to show their details", "repeatHidePreviousTasksDetails",
                    prod ? false : false, "When editing an existing repeat rule, expand the list of existing tasks");
    static PrefEntry repeatCancelNotDeleteSuperflousInstancesWithActualRecorded
            = new PrefEntry("When changing a repeat rule, if any repeat instances have already had work time recorded, "
                    + "then don't delete them, but just Cancel to keep the work time for statistics", "repeatCancelNotDeleteSuperflousInstancesWithActualRecorded",
                    prod ? true : true, //TODO!!!: should ask each time
                    "This ensures that any recorded work time is not lost, e.g. for statistics**"); //TODO: what happens if the max is reached? Will the algorithms still work?
    static PrefEntry repeatShowInternalDataInRepeatScreen
            = new PrefEntry("Show internal RepeatRule data", "repeatShowInternalDataInRepeatScreen", prod ? false : true, "**");
    static PrefEntry repeatInsertAfterLastDueDateInstanceInsteadOfJustCompleted
            = new PrefEntry("Show internal RepeatRule data", "repeatInsertAfterLastDueDateInstanceInsteadOfJustCompleted", prod ? false : true, "**");
    static PrefEntry insertNewRepeatInstancesInStartOfLists
            = new PrefEntry("not used in code yet**", "insertNewRepeatInstancesInStartOfLists", prod ? false : false, "Always insert new repeat tasks at the beginning of lists (instead of after the repeating tasks)"); //"at the end"
    static PrefEntry insertNewRepeatInstancesJustAfterRepeatOriginator
            = new PrefEntry("Insert new repeat instances after the original ",
                    "insertNewRepeatInstancesJustAfterRepeatOriginator", prod ? false : true, "Always insert new repeat tasks after the repeating task instead of beginning/end of list"); //"at the end"
    static PrefEntry repeatOnCompletionFromDueDateIfLaterThanCompletedDate
            = new PrefEntry("Repeat from Completed date will repeat from Due date for tasks completed before due date",
                    "repeatOnCompletionFromDueDateIfLaterThanCompletedDate", prod ? true : true, "**"); //"at the end"

    //ITEMS IN LIST
    static PrefEntry itemListAlwaysShowHideUntilDate = new PrefEntry("Show Hide Until date", "itemListAlwaysShowHideUntilDate", prod ? false : true, "**");
    static PrefEntry itemListAlwaysShowStartByDate = new PrefEntry("Show Start By dates", "itemListAlwaysShowStartByDate", true, "**");
    static PrefEntry itemListExpiresByDate = new PrefEntry("Show Expires By date", "itemListExpiresByDate", prod ? false : true, "**");
    static PrefEntry itemListWaitingTillDate = new PrefEntry("Show Waiting Till date", "itemListWaitingTillDate", true, "**");
    static PrefEntry itemListShowRemainingEvenIfZero = new PrefEntry(Format.f("Show {0} even when 0", Item.EFFORT_REMAINING), "itemListShowRemainingEvenIfZero",
            prod ? false : false, "**");
    static PrefEntry itemListHideRemainingWhenDefaultValue = new PrefEntry(Format.f("Hide {0} with default value", Item.EFFORT_REMAINING), "itemListHideRemainingWhenDefaultValue",
            prod ? false : true, "**");
    static PrefEntry itemListShowActualIfNonZeroEvenIfNotDone = new PrefEntry(Format.f("Show {0} for tasks not {1}", Item.EFFORT_ACTUAL, ItemStatus.DONE.getName()),
            "itemListShowActualIfNonZeroEvenIfNotDone", prod ? false : true, "**");
    static PrefEntry itemListEffortEstimate = new PrefEntry(Format.f("Show {0} even when 0", Item.EFFORT_ESTIMATE), "itemListEffortEstimate",
            prod ? false : true, "**");
    static PrefEntry itemListAllowDuplicateListNames = new PrefEntry("Allow use of same name for different lists", "itemListAllowDuplicateListNames",
            prod ? false : false, "**"); //TODO!!!: ask for confirmation instead!
    static PrefEntry earnedValueDecimals = new PrefEntry(Format.f("Number of decimals shown for {0 earled value}", Item.EARNED_VALUE), "earnedValueDecimals",
            prod ? 2 : 2, "**");
    static PrefEntry itemListDontShowValueIfEarnedValuePerHourIsNonZero = new PrefEntry(Format.f("Show {0} even when 0", Item.EARNED_VALUE),
            "itemListDontShowValueIfEarnedValuePerHourIsNonZero", prod ? false : true, "**"); //TODO!!!: check this setting is correctly named/used

    //
    static PrefEntry creationLogInterval = new PrefEntry("Number of past days included in " + ScreenMain.SCREEN_CREATION_LOG_TITLE, "creationLogInterval",
            prod ? 30 : 30, "How many days back in time are included in " + ScreenMain.SCREEN_CREATION_LOG_TITLE);

    static PrefEntry completionLogInterval = new PrefEntry("Number of past days included in " + ScreenMain.SCREEN_COMPLETION_LOG_TITLE, "completionLogInterval",
            prod ? 30 : 30, "How many days back in time are included in " + ScreenMain.SCREEN_COMPLETION_LOG_TITLE);

    static PrefEntry overdueLogInterval = new PrefEntry("Number of past days included in " + ScreenMain.SCREEN_OVERDUE_TITLE, "overdueLogInterval",
            prod ? 30 : 30, "How many days back in time are included in " + ScreenMain.SCREEN_OVERDUE_TITLE);

    static PrefEntry touchedLogInterval = new PrefEntry("Number of past days included in " + ScreenMain.SCREEN_TOUCHED, "touchedLogInterval",
            prod ? 30 : 30, "How many days back in time are included in " + ScreenMain.SCREEN_TOUCHED);
    static PrefEntry nextInterval = new PrefEntry("Number of past days included in " + ScreenMain.SCREEN_NEXT_TITLE, "nextInterval",
            prod ? 30 : 30, "How many days ahead in time are included in " + ScreenMain.SCREEN_NEXT_TITLE);

    //SMART DATES
    static PrefEntry useSmartdatesForThisManyDaysOverdueDueOrFinishDates = new PrefEntry("Use Smart format for past " + Item.DUE_DATE + " or " + Item.FINISH_WORK_TIME + " when overdue by less than this many days",
            "useSmartdatesForThisManyDaysOverdueDueOrFinishDates",
            prod ? 7 : 30, "**Show overdue dates in Smart format, e.g. Wed17h30. This is useful for overdue dates in the near past, but can get confusing for");
    static PrefEntry smartDatesShowOnlyTimeOfDayToday = new PrefEntry(
            "Smart dates show only time for dates on today",
            "smartDatesShowOnlyTimeOfDayToday", prod ? false : true, "**");
    static PrefEntry smartDatesShowYesterdayAsYesterday = new PrefEntry(
            Format.f("Show dates yesterday as \"Yesterday\"", Item.DUE_DATE, Item.FINISH_WORK_TIME),
            "smartDatesShowYesterdayAsYesterday", prod ? true : true, "**");
    static PrefEntry smartDatesShowTimeOfDayForPastDates = new PrefEntry(
            "Show time of day for past dates",
            "smartDatesShowTimeOfDayForPastDates", prod ? false : false, "**");
    static PrefEntry smartDatesShowOnlyWeekdayAndTimeForNextcomingWeek = new PrefEntry(
            "Show only day of week and time of day for dates in the next 7 days",
            "smartDatesShowOnlyWeekdayAndTimeForNextcomingWeek", prod ? true : true, "**");
    static PrefEntry smartDatesShowOnlyMonDayForNext365Days = new PrefEntry(
            "Show only month and day of month for dates in the next 365 days",
            "smartDatesShowOnlyMonDayForNext365Days", prod ? false : true, "**");
    static PrefEntry smartDatesHideYearForThisManyDaysOverdueDueOrFinishDates = new PrefEntry(
            //            "Use Smart format for past " + Item.DUE_DATE + " or " + Item.FINISH_WORK_TIME + " when overdue by less than this many days",
            Format.f("Use Smart format for past {0} or {1} when overdue by less than this many days", Item.DUE_DATE, Item.FINISH_WORK_TIME),
            "useSmartdatesForThisManyDaysOverdueDueOrFinishDates",
            prod ? 30 : 30, "**Show overdue dates in Smart format, e.g. Wed17h30. This is useful for overdue dates in the near past, but can get confusing for");

    //STATISTICS SCREEN
//    static PrefEntry numberPastWeeksToShowInStatisticsScreen = new PrefEntry("eee", "numberPastDaysToShowInStatisticsScreen", 30, "How many past days to show Show completed tasks grouped by");
//    static PrefEntry numberPastMonthsToShowInStatisticsScreen = new PrefEntry("eee", "numberPastDaysToShowInStatisticsScreen", 30, "How many past days to show Show completed tasks grouped by");
//    static PrefEntry statisticsGroupBy = new PrefEntry("Order tasks by", "statisticsGroupBy", ScreenStatistics2.ShowGroupedBy.week.toString(), "Show completed tasks grouped by");
//    static PrefEntry statisticsGroupBy = new PrefEntry("Show tasks ordered tasks by", "statisticsGroupBy", ScreenStatistics2.ShowGroupedByXXX.week.toString(), "Show completed tasks grouped by");
//    static PrefEntry statisticsGroupByDateInterval = new PrefEntry("eee", "statisticsGroupByDateInterval", ScreenStatistics.ShowGroupedBy.day, "Show completed tasks grouped by");
//    static PrefEntry statisticsSortBy = new PrefEntry("Sort by", "statisticsSortBy", ScreenStatistics.SortStatsOn.dateAndTime.name(), "Sort task by");
    //Group tasks by; For tasks in same group, create for each; group tasks For dates tasks, Create separate group per; "Group tasks by"
//    static PrefEntry statisticsSortBy = new PrefEntry("Group tasks by", "statisticsSortBy", ScreenStatistics2.SortStatsOn.dateThenLists.name(), "**");
//    static PrefEntry statisticsSortBy = new PrefEntry("Show tasks per", "statisticsSortBy", ScreenStatistics2.SortStatsOn.dateThenLists.name(), "**");
//    static PrefEntry statisticsSortBy = new PrefEntry("Group tasks for each", "statisticsSortBy", ScreenStatistics2.SortStatsOnXXX.dateThenLists.name(), "**");
    static PrefEntry statisticsFirstGroupBy = new PrefEntry("Group tasks by", "statisticsFirstGroupBy",
            prod ? ScreenStatistics2.GroupOn2.day.name() : ScreenStatistics2.GroupOn2.day.name(), "**");
    static PrefEntry statisticsSecondGroupBy = new PrefEntry("Then group by", "statisticsSecondGroupBy",
            prod ? ScreenStatistics2.GroupOn2.lists.name() : ScreenStatistics2.GroupOn2.lists.name(), "**");
//    static PrefEntry statisticsSortByXX = new PrefEntry("Sort by", "statisticsSortBy", ScreenStatistics.SortStatsOn.dateAndTime.name(), "Sort task by");

//    static PrefEntry statisticsGroupByCategoryInsteadOfList = new PrefEntry("eee", "statisticsGroupByCategoryInsteadOfList", false, "Show completed tasks grouped by Category instead of Lists (NB. If a task has multiple categories, only the first is used)");
//    static PrefEntry statisticsGroupTasksUnderTheirProject = new PrefEntry("Show subtasks grouped under their project", "statisticsGroupTasksUnderTheirProject", true, "Show completed subtasks grouped under their top-level project");
//    static PrefEntry statisticsGroupTasksUnderTheirProject = new PrefEntry("Show subtasks under their project", "statisticsGroupTasksUnderTheirProject", false, "Show completed subtasks grouped under their top-level project");
    static PrefEntry statisticsGroupTasksUnderTheirProject = new PrefEntry("Group subtasks in same group under their top-level project",
            "statisticsGroupTasksUnderTheirProject", prod ? false : false, "Show completed subtasks grouped under their top-level project");
    static PrefEntry statisticsShowDetailsForAllLists = new PrefEntry("Show details for groups", "statisticsShowDetailsForAllLists",
            prod ? false : false, "**");
    static PrefEntry statisticsShowDetailsForAllTasks = new PrefEntry("Show details for tasks", "statisticsShowDetailsForAllTasks",
            prod ? false : false, "**");
    static PrefEntry statisticsShowMostRecentFirst = new PrefEntry("Show most recent first", "statisticsShowMostRecentFirst",
            prod ? true : true, "**");
    static PrefEntry statisticsScreenNumberPastDaysToShow = new PrefEntry("Past days to include in view", "statisticsScreenNumberPastDaysToShow",
            prod ? 30 : 30, "How many past days to include");

    //LIST OF CATEGORIES
    static PrefEntry listOfCategoriesShowNumberUndoneTasks = new PrefEntry("Show number of tasks", "listOfCategoriesShowNumberUndoneTasks",
            prod ? true : true, "**");
    static PrefEntry listOfCategoriesShowNumberDoneTasks = new PrefEntry("Show number of completed tasks, e.g. 7/23", "listOfCategoriesShowNumberDoneTasks",
            prod ? false : true, "**");
    static PrefEntry listOfCategoriesShowRemainingEstimate = new PrefEntry("Show sum of Remaining estimates for tasks",
            "listOfCategoriesShowRemainingEstimate", prod ? true : true, "**"); //, "e.g. 3h20", Only shown if not-zero. NB. May make displaying the list slower for very large lists or slow devices
    static PrefEntry listOfCategoriesShowTotalTime = new PrefEntry("Show sum of Total time for tasks",
            "listOfCategoriesShowTotalTime", prod ? false : true, "**");
    static PrefEntry listOfCategoriesShowWorkTime = new PrefEntry("Show sum of defined work time for the list",
            "listOfCategoriesShowWorkTime", prod ? false : true, "**"); //"e.g. 1h10/23h12/[4h00]
    static PrefEntry listOfCategoriesShowTotalNumberOfLeafTasks = new PrefEntry("Show number of leaf tasks instead of number of projects",
            "listOfCategoriesShowTotalNumberOfLeafTasks", prod ? false : true, "**");

    //LIST OF ITEMLISTS
    static PrefEntry listOfItemListsShowNumberUndoneTasks = new PrefEntry("Show number of tasks", "listOfItemListsShowNumberUndoneTasks",
            prod ? true : true, "**");
    static PrefEntry listOfItemListsShowNumberDoneTasks = new PrefEntry("Show number of completed tasks, e.g. 7/23", "listOfItemListsShowNumberDoneTasks",
            prod ? false : true, "**");
    static PrefEntry listOfItemListsShowRemainingEstimate = new PrefEntry("Show sum of Remaining estimates for tasks",
            "listOfItemListsShowRemainingEstimate", prod ? true : true, "**"); //, "e.g. 3h20", Only shown if not-zero. NB. May make displaying the list slower for very large lists or slow devices
    static PrefEntry listOfItemListsShowTotalTime = new PrefEntry("Show sum of Total time for tasks",
            "listOfItemListsShowTotalTime", prod ? false : true, "**");
    static PrefEntry listOfItemListsShowWorkTime = new PrefEntry("Show sum of defined work time for the list",
            "listOfItemListsShowWorkTime", prod ? false : true, "**"); //"e.g. 1h10/23h12/[4h00]
    static PrefEntry listOfItemListsShowTotalNumberOfLeafTasks = new PrefEntry("Show number of leaf tasks instead of number of projects",
            "listOfItemListsShowTotalNumberOfLeafTasks", prod ? false : true, "**");

    static PrefEntry hideStickyHeadersForSortedLists = new PrefEntry("Do not show group headers for sorted lists",
            "showStickyHeadersForSortedLists", prod ? false : false, "**"); //show as 'flat' lists

    //GLOBAL
    //localization
    static PrefEntry localeUserSelected = new PrefEntry("Language for text", "localeUserSelected",
            "en", "Determines the language used. Use this setting to override the default language used on your device");

    static PrefEntry dateTimePickerMinuteStep = new PrefEntry("Picker minute steps", "dateTimePickerMinuteStep",
            prod ? 5 : 1, "Minutes in pickers 1/5/10/15");
    static PrefEntry durationPickerMinuteStep = new PrefEntry("Duration picker minute steps", "durationPickerMinuteStep",
            prod ? 1 : 1, "Minutes in pickers, chose 1, 5, 10, 15");
    static PrefEntry durationPickerShowSecondsIfLessThanXMinutesXXX = new PrefEntry("Show seconds in time if under N minutes (0 disables)", "durationPickerShowSecondsIfLessThanXMinutes",
            prod ? 0 : 1, "**");
    static PrefEntry durationPickerShowSecondsIfLessThan1Minute = new PrefEntry("Show seconds in time if less than 1 minute", "durationPickerShowSecondsIfLessThan1Minute",
            prod ? true : true, "**");
    static PrefEntry weeksStartOnMondays = new PrefEntry("Weeks start on Mondays (instead of Sundays)", "weeksStartOnMondays",
            prod ? true : true, "**"); //TODO: make automatic based on locale!!

//LOGIN
    static PrefEntry loginStoreEmail = new PrefEntry("Show email on login", "loginStoreEmail", prod ? false : true, "**");
//    static PrefEntry loginStayLoggedIn = new PrefEntry("Stay logged in", "loginStayLoggedIn", true, "**");//DOESN'T make sense to log user out automatically (would happen each time app is switched to background since destroy is unreliable)
    static PrefEntry loginEmail = new PrefEntry("Your email:", "loginEmail", "", "**Stores your email in settings to reuse it on later logins");
    static PrefEntry loginIncognitoModeXXX = new PrefEntry("Is current user Incognito", "loginIncognitoMode", false, "**");
    static PrefEntry loginFirstTimeLoginXXX = new PrefEntry("Is this the first time someone opens TodoCtatalyst on this device (NOT an end-user setting)", "loginFirstTimeLogin", true, "**");

    //WORKSLOT
    static PrefEntry workSlotDefaultDurationInMinutes = new PrefEntry("Default work slot duration (min)",
            "workSlotDefaultDurationInMinutes", prod ? 60 : 60, "**");
    static PrefEntry workSlotDefaultStartDateIsNow = new PrefEntry("Use current time as Default work slot start time",
            "workSlotDefaultStartDateIsNow", prod ? true : true, "**");
    static PrefEntry workSlotDurationStepIntervalInMinutes = new PrefEntry("Step in WorkSlot duration (min)",
            "workSlotDurationStepIntervalInMinutes", prod ? 15 : 15, "**");
    static PrefEntry workSlotContinueAddingInlineWorkslots = new PrefEntry("Continue adding a new workslot below one inserted with pinch",
            "workSlotContinueAddingInlineWorkslots", prod ? true : true, "**");
    static PrefEntry workSlotUseSmartDates = new PrefEntry("Use smart dates",
            "workSlotUseSmartDates", prod ? true : true, "**");
    static PrefEntry workSlotsMayBeCreatedInThePast = new PrefEntry("Use smart dates",
            "workSlotsMayBeCreatedInThePast", prod ? true : true, "**");

    MyPrefs() {
//         int x=7:
//        super();
    }

    static class PrefEntry {

//        PrefEntry(String fieldDescription, String settingId, MyEnumInterface defaultValue, String helpText) {
//            this.fieldDescription = fieldDescription;
//            this.settingId = settingId;
//            this.defaultValue = defaultValue;
//            this.helpText = helpText;
//        }
        PrefEntry(String fieldDescription, String settingId, Object defaultValue, String helpText) {
            this.fieldDescription = fieldDescription;
            this.settingId = settingId;
            this.defaultValue = defaultValue;
            this.helpText = helpText;
        }

//        PrefEntry(String settingId, Object defaultValue, String helpText) {
//            this(null, settingId, defaultValue, helpText);
//        }
//        PrefEntry(String settingId, Object defaultValue) {
//            this(settingId, defaultValue, null);
//        }
        String fieldDescription;
        String settingId;
        Object defaultValue;
        String helpText;

        @Override
        public String toString() {
            return settingId + "=" + ((String) Preferences.get(settingId, (String) defaultValue));
        }

        public String getFieldScription() {
            return fieldDescription == null ? "" : fieldDescription;
        }

        public String getHelpText() {
            return helpText;
        }

        public String getString() {
            return (String) Preferences.get(settingId, (String) defaultValue);
        }

        public int getInt() {
            return (Integer) Preferences.get(settingId, (Integer) defaultValue);
        }

        public long getLong() {
            return (Long) Preferences.get(settingId, (Long) defaultValue);
        }

        public float getFloat() {
            return (Float) Preferences.get(settingId, (Float) defaultValue);
        }

        public Date getDate() {
            return new MyDate(Preferences.get(settingId, ((Date) defaultValue).getTime()));
        }

        public boolean getBoolean() {
            return (Boolean) Preferences.get(settingId, (Boolean) defaultValue);
        }

        public void flipBoolean() {
            Preferences.set(settingId, !getBoolean());
        }

    }

    // **** GET ****
    //TODO!!! replace below accessors by simpler versions above directly on PrefEntry
    public static String getString(PrefEntry setting) {
        return Preferences.get(setting.settingId, (String) setting.defaultValue);
    }

    public static int getInt(PrefEntry setting) {
        return Preferences.get(setting.settingId, (Integer) setting.defaultValue);
    }

    public static long getLong(PrefEntry setting) {
        return Preferences.get(setting.settingId, (Long) setting.defaultValue);
    }

    public static Date getDate(PrefEntry setting) {
        return new MyDate(Preferences.get(setting.settingId, ((Date) setting.defaultValue).getTime()));
    }

    public static boolean getBoolean(PrefEntry setting) {
        return Preferences.get(setting.settingId, (Boolean) setting.defaultValue);
    }

    // **** SET ****
    public static void setString(PrefEntry setting, String s) {
        Preferences.set(setting.settingId, s);
    }

//    public static void setEnum(PrefEntry setting, Object e) {
//        Preferences.set(setting.settingId, e.toString());
//    }
//
//    public static Object getEnum(PrefEntry setting, Object e) {
//        Preferences.set(setting.settingId, e.toString());
//    }
    public static void setInt(PrefEntry setting, int i) {
        Preferences.set(setting.settingId, i);
    }

    public static void setLong(PrefEntry setting, long l) {
        Preferences.set(setting.settingId, l);
    }

    public static void setBoolean(PrefEntry setting, boolean b) {
        Preferences.set(setting.settingId, b);
    }

    public static void flipBoolean(PrefEntry setting) {
        Preferences.set(setting.settingId, !getBoolean(setting));
    }

    public static String pick(PrefEntry setting, String stringIfTrue, String stringIfFalse) {
        return getBoolean(setting) ? stringIfTrue : stringIfFalse;
    }

    public static void setDate(PrefEntry setting, Date d) {
        Preferences.set(setting.settingId, d.getTime());
    }
}
