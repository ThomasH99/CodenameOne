/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Preferences;
import java.util.Date;

/**
 *
 * @author THJ
 */
public class MyPrefs {

    //TODAY view
    static PrefEntry todayViewIncludeOverdueFromThisManyPastDays
            = new PrefEntry("In Today, include overdue tasks from last X days", "todayViewIncludeOverdueFromThisManyPastDays", 1, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewIncludeWaitingExpiringToday
            = new PrefEntry("In Today, include tasks until today", "todayViewIncludeWaitingExpiringToday", true, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewIncludeAlarmsExpiringToday
            = new PrefEntry("In Today, include tasks with reminders today", "todayViewIncludeAlarmsExpiringToday", true, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewIncludeStartingToday
            = new PrefEntry("In Today, include tasks starting today", "todayViewIncludeStartingToday", true, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)
    static PrefEntry todayViewIncludeWorkSlotsCoveringToday
            = new PrefEntry("In Today, include WorkSLots starting today", "todayViewIncludeWorkSlotsCoveringToday", true, "**"); //UI: 1 day allows you to deal with overdune/undone the next day (or leave them)

//ESTIMATES
    static PrefEntry automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining
            = new PrefEntry("**", "automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining", true, "**");

    static PrefEntry automaticallyUpdateRemainingToEffortMinusActualWhenActualEffortIsUpdated
            = new PrefEntry("**", "automaticallyUpdateRemainingToEffortMinusActualWhenActualEffortIsUpdated", false, "**");

    static PrefEntry automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero
            = new PrefEntry("**", "automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero", true, "**");

    static PrefEntry automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual
            = new PrefEntry("**", "automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual", true, "**");

    static PrefEntry itemRemoveTrailingPrecedingSpacesAndNewlines
            = new PrefEntry("**", "itemRemoveTrailingPrecedingSpacesAndNewlines", true, "**");

    static PrefEntry estimateDefaultValueForZeroEstimatesInMinutes
            = new PrefEntry("Default time estimate", "estimateDefaultValueForZeroEstimatesInMinutes", 4,
                    "Default time estimate to use for non-estimated tasks. Set to typical average value of actual effort for small tasks to avoid having to estimate these. Set to 0 to de-activate.");

    static PrefEntry estimateRemainingOnlyUseSubtasksRemaining
            = new PrefEntry("Only use subtasks' Remaining estimates", "estimateRemainingOnlyUseSubtasksRemaining", false,
                    "Otherwise a project's remaining is the sum of the project task's own remaining and the sum of the remaining of all subtasks");

    static PrefEntry estimateEffortEstimateOnlyUseSubtasksEstimates
            = new PrefEntry("Only use subtasks' Effort estimates", "estimateEffortEstimateOnlyUseSubtasksEstimates", true,
                    "Otherwise a project's effort estimate is the sum of the project task's own effort estimates and the sum of the remaining of all subtasks");

    //INSERTION OF NEW ITEMS
//    static PrefEntry insertNewItemsInStartOfCategory = new PrefEntry("insertNewItemsInStartOfCategory", true, "Always insert new tasks at the beginning of lists (instead of at the end)");
    static PrefEntry insertNewItemsInStartOfLists
            = new PrefEntry("Insert tasks at top of lists", "insertNewItemsInStartOfLists", true, "Always insert new or moved tasks at the beginning of lists (instead of at the end)");

    static PrefEntry insertNewItemListsInStartOfItemListList
            = new PrefEntry("Insert new lists at the top of lists", "insertNewItemListsInStartOfItemListList", false, "Always insert new lists at the beginning of lists (instead of at the end)");

    static PrefEntry insertNewSubtasksInScreenItemInStartOfLists
            = new PrefEntry("**Insert new subtasks created at top of list", "insertNewSubtasksInScreenItemInStartOfLists", false, "**Always insert new subtasks at the beginning of lists (instead of at the end)");

    static PrefEntry itemContinueAddingInlineItems //see text for workSlotContinueAddingInlineWorkslots
            = new PrefEntry("**", "itemContinueAddingInlineItems", true, "**");
    //"Insert created at top of list", "Insert new tasks at beginning of list"

//    static PrefEntry insertNewCategoriesForItemsInStartOfIList //NO, confusing, always show in same order as in CategoryList
//            = new PrefEntry("Add new categories to beginning of list**(make this setting public??)", "insertNewCategoriesForItemsInStartOfIList", false, 
//                    "Always insert added categories at the beginning (makes most recently added categories appear first)");
    static PrefEntry insertNewRepeatInstancesInStartOfLists
            = new PrefEntry("not used in code yet**", "insertNewRepeatInstancesInStartOfLists", false, "Always insert new repeat tasks at the beginning of lists (instead of after the repeating tasks)"); //"at the end"

    static PrefEntry insertNewRepeatInstancesJustAfterRepeatOriginator
            = new PrefEntry("Insert new repeat instances after the original ",
                    "insertNewRepeatInstancesJustAfterRepeatOriginator", true, "Always insert new repeat tasks after the repeating task instead of beginning/end of list"); //"at the end"

    static PrefEntry dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList
            = new PrefEntry("When dragging a task to a Category, insert at the top of its list of tasks", "dropItemAtBeginningOfUnexpandedCategorySubtaskList", true, "**");

    static PrefEntry insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList
            //            = new PrefEntry("When tasks are dropped as subtask under a task with unexpanded subtasks, insert it as the last subtask (end of subtask list)", 
            = new PrefEntry("Add subtasks at end of subtask list",
                    "insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList", true, "**");

    //DRAG AND DROP
    static PrefEntry dragDropAsSubtaskEnabled
            = new PrefEntry("Drag and drop to right edge of screen inserts as subtask", "dragDropAsSubtaskEnabled", true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

    static PrefEntry dragDropAsSupertaskEnabled
            = new PrefEntry("Drag and drop to left edge of screen inserts tasks at level above", "dragDropAsSupertaskEnabled", true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

//    static PrefEntry dropZoneWidthInPercentForDroppingAsSubtaskOrSuperTask
    static PrefEntry dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask
            = new PrefEntry("Approximate width in millimeters of the drop zone that will drop dragged items as either subtasks (right side of drop target) or supertasks (left side)",
                    "dropZoneWidthInPercentForDroppingAsSubtaskOrSuperTask", 10, "**");

//    static PrefEntry dragDropLeftDropZoneWidth
//            = new PrefEntry("Width of the left-hand drop zone (%)", "dragDropLeftDropZoneWidth", 10, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)
//    
//    static PrefEntry dragDropRightDropZoneWidth
//            = new PrefEntry("Width of the right-hand drop zone (%)", "dragDropRightDropZoneWidth", 10, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)
    //Edit ITEM screen - Screenitem2
    static PrefEntry itemEditEnableSwipeBetweenTabs
            = new PrefEntry("Enable swiping between task details", "itemEditEnableSwipeBetweenTabs", true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

    //TIMER - ScreenTimer6
    static PrefEntry timerAutomaticallyStartTimer
            = new PrefEntry("Automatically start timer for a task", "automaticallyStartTimer", true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

    static PrefEntry timerAutomaticallyGotoNextTask
            = new PrefEntry("Automatically continue on next task", "timerAutomaticallyGotoNextTask", true, "Automatically go to the next task in the list or project after the current task is done");

    static PrefEntry timerShowNextTaskWithRemainingTime
            = new PrefEntry("Show " + Item.EFFORT_REMAINING + " for next-coming task", "timerShowNextTaskWithRemainingTime", true,
                    "Will show the " + Item.EFFORT_REMAINING + " in square brackets like [1:15] after next task at the bottom of the Timer screen."); //TODO make timerShowNextTask a numerical value to show 0/1/2/3 next tasks and start one by clicking on it
    static PrefEntry timerShowNextTask
            = new PrefEntry("Show next-coming task in Timer", "timerShowNextTask", true, "Will show the next task in a list at the bottom of the Timer screen. This can help mentally prepare but can also disturb the focus on the current task"); //TODO make timerShowNextTask a numerical value to show 0/1/2/3 next tasks and start one by clicking on it

    static PrefEntry timerShowRemainingForNextTask
            = new PrefEntry("Show remaining time for next-coming task in Timer", "timerShowRemainingForNextTask", true, "**Will show the next task in a list at the bottom of the Timer screen. This can help mentally prepare but can also disturb the focus on the current task"); //TODO make timerShowNextTask a numerical value to show 0/1/2/3 next tasks and start one by clicking on it

//    static PrefEntry automaticallyStartTimerForNewItem = new PrefEntry("automaticallyStartTimerForNewItem", true);
    //TODO! setting update interval to minutes (60sec) should also prevent showing seconds in timer display (otherwise you may end up with eg 36s frozen, but changing sometimes due to relative imprecision of timer update!! and only minutes advancing)
    static PrefEntry timerShowSecondsInTimer
            = new PrefEntry("Show seconds in Timer", "showSecondsInTimer", true, "Hiding seconds may feel less stressful but doesn't visually show that the Timer is running");

    static PrefEntry timerBuzzerInterval
            = new PrefEntry("Buzz interval when Timer is running (minutes)", "timerBuzzerInterval", (int) 0, ""); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerUpdateInterval
            = new PrefEntry("Update Timer elapsed time every N seconds", "timerUpdateInterval", (int) 1, ""); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds
            = new PrefEntry("Minimum timer threshold (seconds)", "timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds", (int) 5, "Tasks will not be marked ongoing and the time will not be saved until after this number seconds. Useful to avoid that skipping a task in Timer marks it ongoing"); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerShowEffortEstimateDetails
            = new PrefEntry("Show Estimate, total time and Remaining time for task", "timerShowEffortEstimateDetails", false,
                    "Timer will show details on estimate etc**"); //, "Reminder vibration interval when Timer is running");

    //TODO!!!! in Timer: check waiting date when skipping, or not, waiting tasks
    static PrefEntry timerIncludeWaitingTasks
            = new PrefEntry("Time Waiting tasks even before the waiting date is met", "timerDoNotSkipWaitingTasks", false,
                    "Normally, the Timer will skip Waiting tasks until the waiting date. This setting will include them**");

    static PrefEntry timerIncludeDoneTasks
            = new PrefEntry("Time Done tasks", "timerIncludeDoneTasks", false,
                    "Normally, the Timer will skip Done tasks. This setting will include them**");

//    static PrefEntry timerBuzzerActive = 
//            new PrefEntry("Buzz when Timer is running","timerBuzzerActive", false, "Buzzer active when timer is running");
//    static PrefEntry timerShowDetailedEstimateInfo = 
//            new PrefEntry("timerDetailedEstimateInfo", false, "Show detailed information about task Estimated time, Total time and Remaining time");
//    static PrefEntry timerShowTaskContext = 
//            new PrefEntry("timerShowTaskContext", false, "Show the List and/or Project the task belongs to");
    static PrefEntry timerShowTotalActualInTimer
            = new PrefEntry("Show total time in timer", "timerShowTotalActualInTimer", false,
                    "Timer always shows the total amount of elapsed time, not just from this timer session");

    static PrefEntry keepScreenAlwaysOnInApp
            = new PrefEntry("Keep screen on when app is active", "keepScreenAlwaysOnInApp", false,
                    "Disables device screen saver while app is in foreground");
    static PrefEntry timerKeepScreenAlwaysOnInTimer
            = new PrefEntry("Keep screen on when Timer is active", "timerKeepScreenAlwaysOnInTimer", false,
                    "Prevents device screen saver from closing while Timer is active");

    static PrefEntry timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem
            = new PrefEntry("Show popup to ask to update Remaining time after timing a task", "timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem", true,
                    "Helps you not forget to update remaining when you stop working on a task**");

    static PrefEntry timerAlwaysExpandListHierarchy
            = new PrefEntry("Show task's parent list & project", "timerAlwaysExpandListHierarchy", true,
                    "**");

    static PrefEntry timerMaxTimerDurationInHoursXXX
            = new PrefEntry("Maximum time the Timer is allowed to run (hours)", "timerMaxTimerDurationInHours", 12,
                    "**DOESN'T MAKE SENSE since you'd still hae to fxi whatever time the timer stopped at*** Timer stops automatically when this maximum in reached. Can be used to avoid the Timer running wild since a popup is shown when reached. NB. Whatever this value, The Timer can never exceed 23h59m.");

    static PrefEntry timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList
            = new PrefEntry("Show Timer popup when no more tasks", "timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList", true,
                    "**");

    static PrefEntry timerAlwaysStartWithNewTimerInSmallWindow
            = new PrefEntry("Always show new timers in current screenshow", "timerAlwaysShowNewTimerInSmallWindow", true,
                    "Instead of showing a started timer in full screen...**");

    static PrefEntry timerEnableShowingSmallTimerWindow
            = new PrefEntry("Show small timer in other views", "timerEnableSmallTimerWindow", true,
                    "**");

    static PrefEntry timerItemOrItemListCanInterruptExistingItemOrItemList
            = new PrefEntry("Possible to start **Always show new timers in current screenshow", "timerItemOrItemListCanInterruptExistingItemOrItemList", true,
                    "Instead of showing a started timer in full screen...**");

    static PrefEntry timerInterruptTaskCanInterruptAlreadyRunningInterruptTask
            = new PrefEntry("Possible to start **Always show new timers in current screenshow (PRO)", "timerInterruptTaskCanInterruptAlreadyRunningInterruptTask", true,
                    "Instead of showing a started timer in full screen...**");

    static PrefEntry timerCanBeSwipeStartedEvenOnInvalidItem
            = new PrefEntry("Allow to swipe-start Timer on any task/project **", "timerCanBeSwipeStartedEvenOnInvalidItem", true,
                    "Start Timer directly on any swiped task or project"); //be force-started on tasks which are normally skipped, e.g. Done, Cancelled or Waiting tasks (depend on settings)**");
    static PrefEntry timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject
            = new PrefEntry("If the currently timed task is no longer in the timed list or project, then start Timer on first task", "timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject",
                    false, "**");
    static PrefEntry enableTimerToRestartOnLists
            = new PrefEntry("Enable Timer to restart over on a timed list, for example if the previously timed task is no longer in the list",
                    "enableTimerToRestartOnLists", false,
                    "**");
    static PrefEntry timerMayPauseAlreadyRunningTimer
            = new PrefEntry("Starting Timer on a new task or list will pause an already running timer", "timerMayPauseAlreadyRunningTimer", true,
                    "**");
    static PrefEntry timerAskBeforeStartingOnNewElement
            = new PrefEntry("Ask before starting Timer starts on new task or list always If Possible to start **Always show new timers in current screenshow",
                    "timerAskBeforeStartingOnNewElement", true, "**");

    //COMMENTS
    static PrefEntry commentsAddToBeginningOfComment
            = new PrefEntry("Add automatic comments to beginning of comment", "commentsAddToBeginningOfComment", false, "Add automatic comments to beginning of comment instead of at the end");

//    static PrefEntry commentsAddTimedEntriesWithDateButNoTime
//            = new PrefEntry("commentsAddTimedEntriesWithDateButNoTime", false);
    static PrefEntry commentsAddTimedEntriesWithDateANDTime
            = new PrefEntry("Add time stamp to Comments time stamps", "commentsAddTimedEntriesWithDateANDTime", false, "When adding time stamp to Comments, also add current time");

    static PrefEntry taskMaxSizeInChars
            = new PrefEntry("**", "taskMaxSizeInChars", 254, "**"); //TODO make max task size a PRO subscription dependendant setting

    static PrefEntry commentMaxSizeInChars
            = new PrefEntry("**", "commentMaxSizeInChars", 512, "**"); //TODO make max comment size a PRO subscription dependendant setting

    //CATEGORY
    static PrefEntry showCategoryDescriptionInCategoryList
            = new PrefEntry("**", "showCategoryDescriptionInCategoryList", false, "Show Category description in category lists");
//    static PrefEntry categoryAllowDuplicateNamesDescriptionInCategoryList
//            = new PrefEntry("**", "showCategoryDescriptionInCategoryList", false, "Show Category description in category lists");
    static PrefEntry addNewCategoriesToBeginningOfCategoryList
            = new PrefEntry("Add new categories to the beginning of the list", "addNewCategoriesToBeginningOfCategoryList", false, "**");

    //ALARMS
//    static PrefEntry alarmLastDateUptoWhichAlarmsHaveBeenSet = new PrefEntry("alarmLastDateUptoWhichAlarmsHaveBeenSet", new Date(0));
    static PrefEntry alarmsActivatedOnThisDevice
            = new PrefEntry("Enable Reminders on this device", "alarmsActivatedOnThisDevice", true, "**"); //alarms activated by default

    static PrefEntry alarmSoundFile
            = new PrefEntry("**", "alarmSoundFile", "notification_sound_Cuckoo_bird_sound.mp3", "**");
    static PrefEntry alarmPlayBuiltinAlarmSound
            = new PrefEntry("Use built in alarm sound", "alarmPlayBuiltinAlarmSound", true, "When adjusting the snooze time of a task manually, the value is kept as default for other tasks, making it easy to snooze several tasks with the same duration. When not set, the Picker is initialized with the default value"); //alarms activated by default

    static PrefEntry alarmIntervalBetweenAlarmsRepeatsMillisInMinutes
            = new PrefEntry("Minutes between reminder notifications", "alarmIntervalBetweenAlarmsRepeatsMillisInMinutes", 1, "Defines the minutes between the repeats of a reminder. Chose 0 to not repeat."); //alarms activated by default

    static PrefEntry alarmDefaultSnoozeTimeInMinutes
            = new PrefEntry("Snooze time in minutes", "alarmDefaultSnoozeTimeInMinutes", 1, "Set the time an alarm is snoozed"); //alarms activated by default
    static PrefEntry alarmReuseIndividuallySetSnoozeDurationForLongPress
            = new PrefEntry("Remember Snooze time set on long press", "alarmReuseIndividuallySetSnoozeDurationForLongPress", true, "When adjusting the snooze time of a task manually, the value is kept as default for other tasks, making it easy to snooze several tasks with the same duration. When not set, the Picker is initialized with the default value"); //alarms activated by default
    static PrefEntry alarmReuseIndividuallySetSnoozeDurationForNormalSnooze
            = new PrefEntry("If snooze time has been set on long press, use that value ", "alarmReuseIndividuallySetSnoozeDurationForNormalSnooze", true, "When adjusting the snooze time of a task manually, the value is kept as default for other tasks, making it easy to snooze several tasks with the same duration. When not set, the Picker is initialized with the default value. NB. The value is only kept while in the same screen"); //alarms activated by default

    static PrefEntry alarmFutureIntervalInWhichToSetAlarmsInHours
            = new PrefEntry("**", "alarmFutureIntervalInWhichToSetAlarmsInHours", 24, "How many days ahead are local notifications activated**"); //alarms activated by default

    static PrefEntry alarmMaxNumberItemsForWhichToSetupAlarms
            = new PrefEntry("**", "alarmMaxNumberItemsForWhichToSetupAlarms", 32, "Maximum number of tasks for which to set up alarms for the period alarmFutureIntervalInWhichToSetAlarmsInDays. Used to limit how many Items are fetched in background from"); //alarms activated by default

    static PrefEntry alarmTimeOfDayWhenToUpdateAlarmsInMinutes
            = new PrefEntry("**", "alarmTimeOfDayWhenToUpdateAlarmsInMinutes", 10, "How many days ahead are alarmsminutes should an alarm snooze"); //10 = 10 minutes after midnight

    static PrefEntry alarmShowDueTimeAtEndOfNotificationText
            = new PrefEntry("Include " + Item.DUE_DATE + " in notifications", "alarmShowDueTimeAtEndOfNotificationText", true, "Include " + Item.DUE_DATE + " in alarm notifications");

    static PrefEntry alarmDaysAheadToFetchFutureAlarms
            = new PrefEntry("**", "alarmDaysAheadToFetchFutureAlarms", 30, "**"); //10 = 10 minutes after midnight

    //ITEM
    static PrefEntry checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress
            = new PrefEntry("Show " + Item.STATUS + " menu on click", "checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress", false, "**");

    static PrefEntry changeSubtasksStatusWithoutConfirmationXXX
            = new PrefEntry("**", "changeSubtasksStatusWithoutConfirmation", false, "**");

    static PrefEntry alwaysShowSubtasksExpandedInScreenItemXXX
            = new PrefEntry("**", "alwaysShowSubtasksExpandedInScreenItem", false, "**");

    static PrefEntry neverChangeProjectsSubtasksWhenChangingProjectStatusXXX
            = new PrefEntry("**", "neverChangeProjectsSubtasksWhenChangingProjectStatus", false, "**");

    static PrefEntry waitingAskToSetWaitingDateWhenMarkingTaskWaiting
            = new PrefEntry("Popup to set " + Item.WAIT_UNTIL_DATE + " and " + Item.WAITING_ALARM_DATE + " when setting a task to " + ItemStatus.WAITING, "waitingAskToSetWaitingDateWhenMarkingTaskWaiting", true, "**does nothing if both of the waiting dates are already set");

    static PrefEntry updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem
            = new PrefEntry("Copy initial values between " + Item.EFFORT_ESTIMATE + " and " + Item.EFFORT_REMAINING + " the first time these values are being edited for a task",
                    "updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItem", true, "**");

    //NB probably not really useful since the easy way to update the other field is to delete it before setting the other... More intuitive (difficult to explain why different values cannot be set)
    static PrefEntry alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItemXXX
            = new PrefEntry("Always copy values between " + Item.EFFORT_ESTIMATE + " and " + Item.EFFORT_REMAINING + " the first time these values are being edited for a task",
                    "alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItem", false, "NB. Makes it impossible to set different initial values for two fields");

    static PrefEntry showDetailsForAllTasks
            = new PrefEntry("Always show details for tasks", "showDetailsForAllTasks", false, "**");

    static PrefEntry itemMaxNbSubTasksToChangeStatusForWithoutConfirmation
            = new PrefEntry("Confirm changing status for this many subtasks", "itemMaxNbSubTasksToChangeStatusForWithoutConfirmation", 2, "For a project, ask for confirmation when changing the status of this many subtasks");

    static PrefEntry askToEnterActualIfMarkingTaskDoneOutsideTimer
            = new PrefEntry("Show popup to enter Actual effort on Done", "askToEnterActualIfMarkingTaskDoneOutsideTimer",
                    true, "When marking a task Done outside the timer, show popup to enter Actual effort");
    static PrefEntry askToEnterActualIfMarkingTaskDoneOutsideTimerOnlyWhenActualIsZero
            = new PrefEntry("Enter Actual effort when none was set", "askToEnterActualIfMarkingTaskDoneOutsideTimerOnlyWhenActualIsZero",
                    true, "When marking a task Done outside the timer, show popup to enter Actual effort");
    static PrefEntry askBeforeInsertingTemplateIntoAndUnderAnAlreadyCreatedItem //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Confirm before inserting a template into an existing task", "askBeforeInsertingTemplateIntoAndUnderAnAlreadyCreatedItem",
                    false, "**");
    static PrefEntry keepDoneTasksVisibleTheDayTheyreCompleted //TODO!!! No, only ask if overwriting an alreadyd defined value!
//            = new PrefEntry("If Completed tasks are hidden, keep them visible until midnight of the day they were completed", //"Don't hide Completed tasks the day they were completed
            = new PrefEntry("Show just Completed tasks until midnight", //"Don't hide Completed tasks the day they were completed
                    "keepDoneTasksVisibleTheDayTheyreCompleted",
                    true, "**");
    static PrefEntry itemDueDateDefaultDaysAheadInTime //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Default number of days ahead in time for Due dates (0 disables)", //"Don't hide Completed tasks the day they were completed
                    "itemDueDateDefaultDaysAheadInTime",
                    7, "**");

    static PrefEntry itemDefaultAlarmTimeBeforeDueDateInMinutes //TODO!!! No, only ask if overwriting an alreadyd defined value!
            = new PrefEntry("Default for how long before the Due date the alarm is set (", //"Don't hide Completed tasks the day they were completed
                    "itemDefaultAlarmTimeBeforeDueDateInMinutes",
                    60, "**");

    // ************** inherit values from owning Project *************
    final static String INHERITS = "Subtasks inherit ";
    static PrefEntry itemInheritOwnerProjectProperties
            = new PrefEntry("Subtasks inherit properties from their project", "itemInheritOwnerProjectProperties", true, "Subtasks inherit properties due date**, priorities etc** from the project they belong to");
    //TODO!! set false for easy startup

    static PrefEntry itemInheritOwnerProjectChallenge
            //            = new PrefEntry("Subtasks inherit "+Item.CHALLENGE+" from their project", "itemInheritOwnerProjectChallenge", true, "**");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.CHALLENGE), "itemInheritOwnerProjectChallenge", false,
                    INHERITS + Item.CHALLENGE);

    static PrefEntry itemInheritOwnerStarredProperties
            //            = new PrefEntry(Format.f("Subtasks inherit {0} from their project","propert", "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.STARRED), "itemInheritOwnerStarredProperties", true,
                    INHERITS + Item.STARRED);

    static PrefEntry itemInheritOwnerProjectPriority
            //            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectPriority", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.PRIORITY), "itemInheritOwnerProjectPriority", true,
                    INHERITS + Item.PRIORITY);

    static PrefEntry itemInheritOwnerProjectDreadFun
            //            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectDreadFun", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.FUN_DREAD), "itemInheritOwnerProjectDreadFun", true,
                    INHERITS + Item.FUN_DREAD);

    static PrefEntry itemInheritOwnerProjectImportance
            //            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectImportance", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.IMPORTANCE), "itemInheritOwnerProjectImportance", true,
                    INHERITS + Item.IMPORTANCE);

    static PrefEntry itemInheritOwnerProjectUrgency
            //            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectUrgency", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.URGENCY), "itemInheritOwnerProjectUrgency", true,
                    INHERITS + Item.URGENCY);

    static PrefEntry itemInheritOwnerProjectDueDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.DUE_DATE), "itemInheritOwnerProjectDueDate", true,
                    INHERITS + Item.DUE_DATE);

    static PrefEntry itemInheritOwnerProjectExpiresOnDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.EXPIRES_ON_DATE), "itemInheritOwnerProjectExpiresOnDate", true,
                    INHERITS + Item.EXPIRES_ON_DATE);

    static PrefEntry itemInheritOwnerProjectStartByDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.START_BY_TIME), "itemInheritOwnerProjectStartByDate", true,
                    INHERITS + Item.START_BY_TIME);

    static PrefEntry itemInheritOwnerProjectWaitingTillDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.WAIT_UNTIL_DATE), "itemInheritOwnerProjectWaitingTillDate", true,
                    INHERITS + Item.WAIT_UNTIL_DATE);

    static PrefEntry itemInheritOwnerProjectDateWhenSetWaiting
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.DATE_WHEN_SET_WAITING), "itemInheritOwnerProjectDateWhenSetWaiting", true,
                    INHERITS + Item.DATE_WHEN_SET_WAITING);

    static PrefEntry itemInheritOwnerProjectHideUntilDate
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.HIDE_UNTIL), "itemInheritOwnerProjectHideUntilDate", true,
                    INHERITS + Item.HIDE_UNTIL);

    static PrefEntry itemInheritOwnerProjectTemplate //doesn't make sense to inherit this, each template subtask should have this set directly?! 
            = new PrefEntry(Format.f("Subtasks inherit {0} from their project", Item.TEMPLATE), "itemInheritOwnerProjectTemplate", true,
                    INHERITS + Item.TEMPLATE);
    static PrefEntry itemInheritEvenDoneSubtasksInheritOwnerValues
            = new PrefEntry(Format.f("Even completed subtasks inherit valhes from their project", Item.TEMPLATE), "itemInheritEvenDoneSubtasksInheritOwnerValues", false,
                    INHERITS + Item.TEMPLATE);

    static PrefEntry useDefaultFilterInItemsWhenNoneDefined
            = new PrefEntry("By default, hide Done and Cancelled tasks in lists", "useDefaultFilterInItemsWhenNoneDefined", true, "**");

    // ************** END inherit values from owning Project *************
    static PrefEntry itemExtractRemainingEstimateFromStringInTaskText
            = new PrefEntry("Extract remaining time from task text", "itemExtractRemainingEstimateFromStringInTaskText", true, "**");

    static PrefEntry itemKeepRemainingEstimateStringInTaskText
            = new PrefEntry("Keep remaining time in task text", "itemKeepRemainingEstimateStringInTaskText", false, "**");

    static PrefEntry itemProjectPropertiesDerivedFromSubtasks
            = new PrefEntry("Project properties like [STARTED_ON] shows values from subtasks", "itemProjectPropertiesDerivedFromSubtasks", true, "**");

    static PrefEntry workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime
            = new PrefEntry(Format.f("Prioritize {0} from Categories", Item.WORTIME), "workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime", true, "If one of a tasks categories has work time, use that to calculate the finish time instead of the work time of the Porject or List the task belongs to");

    //INTERNAL / TECHNICAL / CACHE
    static PrefEntry backgroundFetchIntervalInSeconds = new PrefEntry("**", "backgroundFetchIntervalInSeconds", 1 * MyDate.HOUR_IN_MILISECONDS / 1000, "interval for updating eg local notifications and badge count when app is in background**");

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
            = new PrefEntry("Show ObjectIds when editing", "showObjectIdsInEditScreens", true, "show internal unique ID when editing - NOT END USER");

    static PrefEntry showSourceItemInEditScreens
            = new PrefEntry("For a copy, show the original", "showSourceItemInEditScreens", true, "**");

    static PrefEntry showSourceWorkSlotInEditScreens
            = new PrefEntry("For a copy, show the original", "showSourceWorkSlotInEditScreens", true, "**");

    static PrefEntry showDebugInfoInLabelsEtc
            = new PrefEntry("Show debug info, e.g. add ^ to subtask", "showDebugInfoInLabelsEtc", true, "** - NOT END USER");

    static PrefEntry reloadChangedDataInBackground
            = new PrefEntry("Refresh changed data in background", "reloadChangedDataInBackground", false, "** - NOT END USER");

    //ITEMLIST
    static PrefEntry useDefaultFilterInItemListsWhenNoneDefined
            = new PrefEntry("By default, hide Done and Cancelled tasks in lists", "useDefaultFilterInItemListsWhenNoneDefined", true, "**");

    //LOOK AND FEEL
    static PrefEntry themeNameWithoutBackslash
            = new PrefEntry("**", "themeNameWithoutBackslash", "theme", "name of the graphical theme");
    static PrefEntry titleAutoSize
            = new PrefEntry("Reduce Screen title font size for long texts", "titleAutoSize", true, "name of the graphical theme");

    //OTHER / SYSTEM-LEVEL
    static PrefEntry enableCancelInAllScreens
            = new PrefEntry("**", "enableCancelInAllScreens", false, "temporarily used to disable Cancel everywhere due to problems eg with too complex to Cancel when inserting Templates with subtasks");
    static PrefEntry dateShowDatesInUSFormat
            = new PrefEntry("**", "dateShowDatesInUSFormat", false, "**");
    static PrefEntry disableGoogleAnalytics
            = new PrefEntry("**", "disableGoogleAnalytics", false, "diable sending anonymous usage pattern information to Google Analytics (used to help improve TDC");
    static PrefEntry enableRepairCommandsInMenus
            = new PrefEntry("enableRepairCommandsInMenus", "enableRepairCommandsInMenus", true, "**");
    static PrefEntry pinchInsertEnabled
            = new PrefEntry("Enable pinch insert in lists", "pinchInsertEnabled", true, "**");

    //REPEATRULE
    static PrefEntry repeatMaxInterval = new PrefEntry("repeatMaxInterval**", "repeatMaxInterval", 365, "maximun value for repeat interval**");
    static PrefEntry repeatMaxNumberFutureInstancesToGenerateAhead
            = new PrefEntry("repeatMaxNumberFutureInstancesToGenerateAhead", "repeatMaxNumberFutureInstancesToGenerateAhead", 10, "0 disables - TODO**");
    static PrefEntry repeatMaxNumberFutureDaysToGenerateAhead = new PrefEntry("repeatMaxNumberFutureDaysToGenerateAhead**", "repeatMaxNumberFutureDaysToGenerateAhead", 31, "**");
    static PrefEntry maxNumberRepeatInstancesToDeleteWithoutConfirmation
            = new PrefEntry("Ask for confirmation if deleting more repeat instances that this", "maxNumberRepeatInstancesToDeleteWithoutConfirmation", 3, "**");
    static PrefEntry repeatSetRelativeFieldsWhenCreatingRepeatInstances
            = new PrefEntry("Set Alarm, Hide until, Start by and Expires on fields relative to Due date", "repeatSetRelativeFieldsWhenCreatingRepeatInstances", true,
                    "set the relative date fields like Alarm/HideUntil/StartBy/Autoexpire** etc to same time before/after due date**");
    static PrefEntry repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule
            = new PrefEntry("Reuse existing instances when changing " + RepeatRuleParseObject.REPEAT_RULE, "repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule",
                    true, "When changing a repeat rule, reuse any task instances that already exist, this will keep any edits to such instances");
    static PrefEntry repeatHidePreviousTasksDetails
            = new PrefEntry("When show previously generated repeat instances, expand to show their details", "repeatHidePreviousTasksDetails", false, "When editing an existing repeat rule, expand the list of existing tasks");
    static PrefEntry repeatMaxNumberOfRepeatsToGenerate
            = new PrefEntry("To avoid that too many repeats get generated and overflod the server or the app, 0 disables", "repeatMaxNumberOfRepeatsToGenerate", 20,
                    "more of an internal limitation for now**"); //TODO: what happens if the max is reached? Will the algorithms still work?
    static PrefEntry repeatCancelNotDeleteSuperflousInstancesWhenUpdatingRule
            = new PrefEntry("When changing a repeat rule, Cancel, not Delete, future instances with recorded work time that are removed due to the change", "repeatCancelNotDeleteSuperflousInstancesWhenUpdatingRule", true,
                    "This ensures that any recorded work time is not lost, e.g. for statistics**"); //TODO: what happens if the max is reached? Will the algorithms still work?

    //ITEMS IN LIST
    static PrefEntry itemListAlwaysShowHideUntilDate = new PrefEntry("Show Hide Until date", "itemListAlwaysShowHideUntilDate", true, "**");
    static PrefEntry itemListAlwaysShowStartByDate = new PrefEntry("Show Start By dates", "itemListAlwaysShowStartByDate", true, "**");
    static PrefEntry itemListExpiresByDate = new PrefEntry("Show Expires By date", "itemListExpiresByDate", true, "**");
    static PrefEntry itemListWaitingTillDate = new PrefEntry("Show Waiting Till date", "itemListWaitingTillDate", true, "**");
    static PrefEntry itemListShowRemainingEvenIfZero = new PrefEntry("In lists of tasks, show even zero [REMAINING]**", "itemListShowRemainingEvenIfZero", false, "**");
    static PrefEntry itemListShowActualIfNonZeroEvenIfNotDone = new PrefEntry("In lists of tasks, show even zero [REMAINING]**",
            "itemListShowActualIfNonZeroEvenIfNotDone", false, "**");
    static PrefEntry itemListEffortEstimate = new PrefEntry("Show Effort Estimate in list details**", "itemListEffortEstimate", true, "**");
    static PrefEntry itemListAllowDuplicateListNames = new PrefEntry("Show Effort Estimate in list details**", "itemListAllowDuplicateListNames", false, "**");
    static PrefEntry earnedValueDecimals = new PrefEntry("Number of decimals shown for task Value**", "earnedValueDecimals", 2, "**");
    static PrefEntry itemListDontShowValueIfEarnedValuePerHourIsNonZero = new PrefEntry("Show Effort Estimate in list details**",
            "itemListDontShowValueIfEarnedValuePerHourIsNonZero", true, "**");

    //
    static PrefEntry creationLogInterval = new PrefEntry("yyy", "creationLogInterval", 30, "How many days back in time are included in " + ScreenMain.SCREEN_CREATION_LOG_TITLE);
    static PrefEntry completionLogInterval = new PrefEntry("rrr", "completionLogInterval", 30, "How many days back in time are included in " + ScreenMain.SCREEN_COMPLETION_LOG_TITLE);
    static PrefEntry overdueLogInterval = new PrefEntry("Past days in " + ScreenMain.SCREEN_OVERDUE_TITLE, "overdueLogInterval", 30, "How many days back in time are included in " + ScreenMain.SCREEN_OVERDUE_TITLE);
    static PrefEntry touchedLogInterval = new PrefEntry("eee", "touchedLogInterval", 30, "How many days back in time are included in " + ScreenMain.SCREEN_TOUCHED);
    static PrefEntry nextInterval = new PrefEntry("eee", "nextInterval", 30, "How many days ahead in time are included in " + ScreenMain.SCREEN_NEXT_TITLE);

    static PrefEntry useSmartdatesForThisManyDaysOverdueDueOrFinishDates = new PrefEntry("Use Smart format for past " + Item.DUE_DATE + " or " + Item.FINISH_WORK_TIME + " when overdue by less than this many days",
            "useSmartdatesForThisManyDaysOverdueDueOrFinishDates", 30, "**Show overdue dates in Smart format, e.g. Wed17h30. This is useful for overdue dates in the near past, but can get confusing for");

    //STATISTICS SCREEN
    static PrefEntry statisticsScreenNumberPastDaysToShow = new PrefEntry("Past days to include", "statisticsScreenNumberPastDaysToShow", 30, "How many past days to include");
//    static PrefEntry numberPastWeeksToShowInStatisticsScreen = new PrefEntry("eee", "numberPastDaysToShowInStatisticsScreen", 30, "How many past days to show Show completed tasks grouped by");
//    static PrefEntry numberPastMonthsToShowInStatisticsScreen = new PrefEntry("eee", "numberPastDaysToShowInStatisticsScreen", 30, "How many past days to show Show completed tasks grouped by");
    static PrefEntry statisticsGroupBy = new PrefEntry("Group done tasks by", "statisticsGroupBy", ScreenStatistics.ShowGroupedBy.day.toString(), "Show completed tasks grouped by");
//    static PrefEntry statisticsGroupByDateInterval = new PrefEntry("eee", "statisticsGroupByDateInterval", ScreenStatistics.ShowGroupedBy.day, "Show completed tasks grouped by");
    static PrefEntry statisticsSortBy = new PrefEntry("Sort by", "statisticsSortBy", ScreenStatistics.SortStatsOn.dateAndTime.name(), "Sort task by");
//    static PrefEntry statisticsSortByXX = new PrefEntry("Sort by", "statisticsSortBy", ScreenStatistics.SortStatsOn.dateAndTime.name(), "Sort task by");

//    static PrefEntry statisticsGroupByCategoryInsteadOfList = new PrefEntry("eee", "statisticsGroupByCategoryInsteadOfList", false, "Show completed tasks grouped by Category instead of Lists (NB. If a task has multiple categories, only the first is used)");
    static PrefEntry statisticsGroupTasksUnderTheirProject = new PrefEntry("Show subtasks grouped under their project", "statisticsGroupTasksUnderTheirProject", true, "Show completed subtasks grouped under their top-level project");
    static PrefEntry statisticsShowDetailsForAllLists = new PrefEntry("Always show details for statistics", "statisticsShowDetailsForAllLists", false, "**");
    static PrefEntry statisticsShowMostRecentFirst = new PrefEntry("Show most recent first", "statisticsShowMostRecentFirst", true, "**");

    //LIST OF CATEGORIES
    static PrefEntry listOfCategoriesShowNumberUndoneTasks = new PrefEntry("Show number of tasks", "listOfCategoriesShowNumberUndoneTasks", true, "**");
    static PrefEntry listOfCategoriesShowNumberDoneTasks = new PrefEntry("Show number of completed tasks, e.g. 7/23", "listOfCategoriesShowNumberDoneTasks", true, "**");
    static PrefEntry listOfCategoriesShowRemainingEstimate = new PrefEntry("Show sum of Remaining estimates for tasks",
            "listOfCategoriesShowRemainingEstimate", true, "**"); //, "e.g. 3h20", Only shown if not-zero. NB. May make displaying the list slower for very large lists or slow devices
    static PrefEntry listOfCategoriesShowTotalTime = new PrefEntry("Show sum of Total time for tasks",
            "listOfCategoriesShowTotalTime", true, "**");
    static PrefEntry listOfCategoriesShowWorkTime = new PrefEntry("Show sum of defined work time for the list",
            "listOfCategoriesShowWorkTime", true, "**"); //"e.g. 1h10/23h12/[4h00]
    static PrefEntry listOfCategoriesShowTotalNumberOfLeafTasks = new PrefEntry("Show number of leaf tasks instead of number of projects",
            "listOfCategoriesShowTotalNumberOfLeafTasks", true, "**");

    //LIST OF ITEMLISTS
    static PrefEntry listOfItemListsShowNumberUndoneTasks = new PrefEntry("Show number of tasks", "listOfItemListsShowNumberUndoneTasks", true, "**");
    static PrefEntry listOfItemListsShowNumberDoneTasks = new PrefEntry("Show number of completed tasks, e.g. 7/23", "listOfItemListsShowNumberDoneTasks", true, "**");
    static PrefEntry listOfItemListsShowRemainingEstimate = new PrefEntry("Show sum of Remaining estimates for tasks",
            "listOfItemListsShowRemainingEstimate", true, "**"); //, "e.g. 3h20", Only shown if not-zero. NB. May make displaying the list slower for very large lists or slow devices
    static PrefEntry listOfItemListsShowTotalTime = new PrefEntry("Show sum of Total time for tasks",
            "listOfItemListsShowTotalTime", true, "**");
    static PrefEntry listOfItemListsShowWorkTime = new PrefEntry("Show sum of defined work time for the list",
            "listOfItemListsShowWorkTime", true, "**"); //"e.g. 1h10/23h12/[4h00]
    static PrefEntry listOfItemListsShowTotalNumberOfLeafTasks = new PrefEntry("Show number of leaf tasks instead of number of projects",
            "listOfItemListsShowTotalNumberOfLeafTasks", true, "**");

    //GLOBAL
    //localization
    static PrefEntry localeUserSelected = new PrefEntry("Language for text", "localeUserSelected", "en", "Determines the language used. Use this setting to override the default language used on your device");

    static PrefEntry dateTimePickerMinuteStep = new PrefEntry("Picker minute steps", "dateTimePickerMinuteStep", 1, "Minutes in pickers, chose 1, 5 10, 15");
    static PrefEntry durationPickerMinuteStep = new PrefEntry("Duration picker minute steps", "durationPickerMinuteStep", 1, "Minutes in pickers, chose 1, 5, 10, 15");
    static PrefEntry durationPickerShowSecondsIfLessThanXMinutes = new PrefEntry("Show seconds in time if under N minutes (0 disables)", "durationPickerShowSecondsIfLessThanXMinutes", 1, "**");
    static PrefEntry durationPickerShowSecondsIfLessThan1Minute = new PrefEntry("Show seconds in time if less than 1 minute", "durationPickerShowSecondsIfLessThan1Minute", true, "**");
    static PrefEntry weeksStartOnMondays = new PrefEntry("Weeks start on Mondays (instead of Sundays)", "weeksStartOnMondays", true, "**");

//LOGIN
    static PrefEntry loginStoreEmail = new PrefEntry("Show email on login", "loginStoreEmail", true, "**");
//    static PrefEntry loginStayLoggedIn = new PrefEntry("Stay logged in", "loginStayLoggedIn", true, "**");//DOESN'T make sense to log user out automatically (would happen each time app is switched to background since destroy is unreliable)
    static PrefEntry loginEmail = new PrefEntry("Your email:", "loginEmail", "", "**");
    static PrefEntry loginIncognitoMode = new PrefEntry("Is current user Incognito", "loginIncognitoMode", false, "**");
    static PrefEntry loginFirstTimeLogin = new PrefEntry("Is this the first time someone opens TodoCtatalyst on this device (NOT an end-user setting)", "loginFirstTimeLogin", true, "**");

    //WORKSLOT
    static PrefEntry workSlotDefaultDurationInMinutes = new PrefEntry("Default work slot duration (min)",
            "workSlotDefaultDurationInMinutes", 60, "**");
    static PrefEntry workSlotDefaultStartDateIsNow = new PrefEntry("Use current time as Default work slot start time",
            "workSlotDefaultStartDateIsNow", true, "**");
    static PrefEntry workSlotDurationStepIntervalInMinutes = new PrefEntry("Step in WorkSlot duration (min)",
            "workSlotDurationStepIntervalInMinutes", 5, "**");
    static PrefEntry workSlotContinueAddingInlineWorkslots = new PrefEntry("Continue adding a new workslot below one inserted with pinch",
            "workSlotContinueAddingInlineWorkslots", true, "**");
    static PrefEntry workSlotUseSmartDates = new PrefEntry("Use smart dates",
            "workSlotUseSmartDates", true, "**");

    MyPrefs() {
//         int x=7:
//        super();
    }

    static class PrefEntry {

        PrefEntry(String fieldDescription, String settingId, MyEnumInterface defaultValue, String helpText) {
            this.fieldDescription = fieldDescription;
            this.settingId = settingId;
            this.defaultValue = defaultValue;
            this.helpText = helpText;
        }

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

        public Date getDate() {
            return new Date(Preferences.get(settingId, ((Date) defaultValue).getTime()));
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
        return new Date(Preferences.get(setting.settingId, ((Date) setting.defaultValue).getTime()));
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
