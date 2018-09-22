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

    //ESTIMATES
    static PrefEntry automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining
            = new PrefEntry("**","automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining", true, "**");

    static PrefEntry automaticallyUpdateRemainingToEffortMinusActualWhenActualEffortIsUpdated
            = new PrefEntry("**","automaticallyUpdateRemainingToEffortMinusActualWhenActualEffortIsUpdated", false,"**");

    static PrefEntry automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero
            = new PrefEntry("**","automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero", true,"**");

    static PrefEntry automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual
            = new PrefEntry("**","automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual", true,"**");

    static PrefEntry itemRemoveTrailingPrecedingSpacesAndNewlines
            = new PrefEntry("**","itemRemoveTrailingPrecedingSpacesAndNewlines", true,"**");

    static PrefEntry estimateDefaultValueForZeroEstimatesInMinutes
            = new PrefEntry("Default time estimate","estimateDefaultValueForZeroEstimatesInMinutes", 4, 
                    "Default time estimate to use for non-estimated tasks. Set to typical average value of actual effort for small tasks to avoid having to estimate these. Set to 0 to de-activate.");

    //INSERTION OF NEW ITEMS
//    static PrefEntry insertNewItemsInStartOfCategory = new PrefEntry("insertNewItemsInStartOfCategory", true, "Always insert new tasks at the beginning of lists (instead of at the end)");
    static PrefEntry insertNewItemsInStartOfLists
            = new PrefEntry("Insert created at top of list", "insertNewItemsInStartOfLists", true, "Always insert new tasks at the beginning of lists (instead of at the end)");
    static PrefEntry insertNewSubtasksInScreenItemInStartOfLists
            = new PrefEntry("**Insert new subtasks created at top of list", "insertNewSubtasksInScreenItemInStartOfLists", false, "**Always insert new subtasks at the beginning of lists (instead of at the end)");
    //"Insert created at top of list", "Insert new tasks at beginning of list"

    static PrefEntry insertNewCategoriesForItemsInStartOfIList
            = new PrefEntry("Add new categories to beginning of list**(make this setting public??)", "insertNewCategoriesForItemsInStartOfIList", true, "Always insert added categories at the beginning (makes most recently added categories appear first)");

    static PrefEntry insertNewRepeatInstancesInStartOfLists
            = new PrefEntry("**","insertNewRepeatInstancesInStartOfLists", false, "Always insert new repeat tasks at the beginning of lists (instead of after the repeating tasks)"); //"at the end"

    static PrefEntry insertNewRepeatInstancesJustAfterRepeatOriginator
            = new PrefEntry("**","insertNewRepeatInstancesJustAfterRepeatOriginator", true, "Always insert new repeat tasks after the repeating task"); //"at the end"

    static PrefEntry dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList
            = new PrefEntry("When dragging a task to a Category, insert at the top of its list of tasks","dropItemAtBeginningOfUnexpandedCategorySubtaskList", true, "**"); 

    static PrefEntry dropZoneWidthInPercentForDroppingAsSubtaskOrSuperTask
            = new PrefEntry("Defines the width of the drop zone that will drop dragged items as either subtasks (right side of drop target) or supertasks (left side)",
                    "dropZoneWidthInPercentForDroppingAsSubtaskOrSuperTask", 10, "**"); 
    
    static PrefEntry insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList
                                                                = new PrefEntry("When tasks are dropped as subtask under a task with unexpanded subtasks, insert it as the last subtask (end of subtask list)", "insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList", false, "**");

    //TIMER
    static PrefEntry timerAutomaticallyStartTimer
            = new PrefEntry("Automatically start timer for a task", "automaticallyStartTimer", true, ""); //one single option to start Timer for new tasks/interrupt tasks, or when working though an itemlist in the Timer (having separate options for New Item and for Next Item is too complex)

    static PrefEntry timerAutomaticallyGotoNextTask
            = new PrefEntry("Automatically continue on next task", "timerAutomaticallyGotoNextTask", true, "Automatically go to the next task in the list or project after the current task is done");

    static PrefEntry timerShowNextTaskWithRemainingTime
            = new PrefEntry("Show "+Item.EFFORT_REMAINING+" for next-coming task", "timerShowNextTaskWithRemainingTime", true, 
                    "Will show the "+Item.EFFORT_REMAINING+" in square brackets like [1:15] after next task at the bottom of the Timer screen."); //TODO make timerShowNextTask a numerical value to show 0/1/2/3 next tasks and start one by clicking on it
    static PrefEntry timerShowNextTask
            = new PrefEntry("Show next-coming task in Timer", "timerShowNextTask", true, "Will show the next task in a list at the bottom of the Timer screen. This can help mentally prepare but can also disturb the focus on the current task"); //TODO make timerShowNextTask a numerical value to show 0/1/2/3 next tasks and start one by clicking on it

//    static PrefEntry automaticallyStartTimerForNewItem = new PrefEntry("automaticallyStartTimerForNewItem", true);
    static PrefEntry timerShowSecondsInTimer
            = new PrefEntry("Show seconds in Timer", "showSecondsInTimer", true, "Hiding seconds may feel less stressful but doesn't visually show that the Timer is running");

    static PrefEntry timerBuzzerInterval
            = new PrefEntry("Buzz interval when Timer is running", "timerBuzzerInterval", (int) 0, ""); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerUpdateInterval
            = new PrefEntry("Interval between Timer updates (seconds)", "timerUpdateInterval", (int) 1, ""); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds
            = new PrefEntry("Minimum timer threshold (seconds)", "timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds", (int) 5, "Tasks will not be marked ongoing and the time will not be saved until after this number seconds. Useful to avoid that skipping a task in Timer marks it ongoing"); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerShowEffortEstimateDetails
            = new PrefEntry("Show Estimate, total time and Remaining time for task", "timerShowEffortEstimateDetails", false, "Timer will show details on estimate etc**"); //, "Reminder vibration interval when Timer is running");

    static PrefEntry timerIncludeWaitingTasks
            = new PrefEntry("Include Waiting tasks even before the waiting date is met", "timerDoNotSkipWaitingTasks", false, "Normally, the Timer will skip Waiting tasks until the waiting date. This setting will include them**");

//    static PrefEntry timerBuzzerActive = 
//            new PrefEntry("Buzz when Timer is running","timerBuzzerActive", false, "Buzzer active when timer is running");
//    static PrefEntry timerShowDetailedEstimateInfo = 
//            new PrefEntry("timerDetailedEstimateInfo", false, "Show detailed information about task Estimated time, Total time and Remaining time");
//    static PrefEntry timerShowTaskContext = 
//            new PrefEntry("timerShowTaskContext", false, "Show the List and/or Project the task belongs to");
    static PrefEntry timerShowTotalActualInTimer
            = new PrefEntry("Show total time in timer (PRO)", "timerShowTotalActualInTimer", false, "Timer always shows the total amount of elapsed time, not just from this timer session");

    static PrefEntry timerKeepScreenAlwaysOnInTimer
            = new PrefEntry("Keep screen on when Timer is active", "timerKeepScreenAlwaysOnInTimer", false, "Prevents device screen saver from closing while Timer is active");

    static PrefEntry timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem
            = new PrefEntry("Show popup to ask to update Remaining time after timing a task", "timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem", true,
                    "Helps you not forget to update remaining when you stop working on a task**");

    static PrefEntry timerAlwaysExpandListHierarchy
            = new PrefEntry("Show the parent list/project of the task", "timerAlwaysExpandListHierarchy", true, "**");

    static PrefEntry timerMaxTimerDurationInHours
            = new PrefEntry("Maximum time the Timer is allowed to run (hours)", "timerMaxTimerDurationInHours", 12, "Timer stops automatically when this maximum in reached. Can be used to avoid the Timer running wild since a popup is shown when reached. NB. Whatever this value, The Timer can never exceed 23h59m.");

    //COMMENTS
    static PrefEntry commentsAddToBeginningOfComment
            = new PrefEntry("Add automatic comments to beginning of comment", "commentsAddToBeginningOfComment", false, "Add automatic comments to beginning of comment instead of at the end");

//    static PrefEntry commentsAddTimedEntriesWithDateButNoTime
//            = new PrefEntry("commentsAddTimedEntriesWithDateButNoTime", false);
    static PrefEntry commentsAddTimedEntriesWithDateANDTime
            = new PrefEntry("Add time stamp to Comments time stamps", "commentsAddTimedEntriesWithDateANDTime", false, "When adding time stamp to Comments, also add current time");

    static PrefEntry taskMaxSizeInChars
            = new PrefEntry("**","taskMaxSizeInChars", 254,"**"); //TODO make max task size a PRO subscription dependendant setting

    static PrefEntry commentMaxSizeInChars
            = new PrefEntry("**","commentMaxSizeInChars", 512,"**"); //TODO make max comment size a PRO subscription dependendant setting
    
    static PrefEntry showCategoryDescriptionInCategoryList
            = new PrefEntry("**","showCategoryDescriptionInCategoryList", false,"Show Category description in category lists"); 

    //ALARMS
//    static PrefEntry alarmLastDateUptoWhichAlarmsHaveBeenSet = new PrefEntry("alarmLastDateUptoWhichAlarmsHaveBeenSet", new Date(0));
    static PrefEntry alarmsActivatedOnThisDevice
            = new PrefEntry("Enable Reminders on this device","alarmsActivatedOnThisDevice", true,"**"); //alarms activated by default

    static PrefEntry alarmSoundFile
            = new PrefEntry("**","alarmSoundFile", "notification_sound_Cuckoo_bird_sound.mp3","**");

    static PrefEntry alarmIntervalBetweenAlarmsRepeatsMillisInMinutes
            = new PrefEntry("**","alarmIntervalBetweenAlarmsRepeatsMillisInMinutes", 1, "Defines the minutes between the repeats of a reminder. Chose 0 to not repeat."); //alarms activated by default

    static PrefEntry alarmDefaultSnoozeTimeInMinutes
            = new PrefEntry("Snooze time in minutes", "alarmDefaultSnoozeTimeInMinutes", 1, "Set the time an alarm is snoozed"); //alarms activated by default

    static PrefEntry alarmFutureIntervalInWhichToSetAlarmsInHours
            = new PrefEntry("**","alarmFutureIntervalInWhichToSetAlarmsInHours", 24, "How many days ahead are local notifications activated**"); //alarms activated by default

    static PrefEntry alarmMaxNumberItemsForWhichToSetupAlarms
            = new PrefEntry("**","alarmMaxNumberItemsForWhichToSetupAlarms", 32, "Maximum number of tasks for which to set up alarms for the period alarmFutureIntervalInWhichToSetAlarmsInDays. Used to limit how many Items are fetched in background from"); //alarms activated by default

    static PrefEntry alarmTimeOfDayWhenToUpdateAlarmsInMinutes
            = new PrefEntry("**","alarmTimeOfDayWhenToUpdateAlarmsInMinutes", 10, "How many days ahead are alarmsminutes should an alarm snooze"); //10 = 10 minutes after midnight

    static PrefEntry alarmShowDueTimeAtEndOfNotificationText
            = new PrefEntry("Include "+ Item.DUE_DATE + "in notifications","alarmShowDueTimeAtEndOfNotificationText", true, "Include " + Item.DUE_DATE + " in alarm notifications");

    static PrefEntry alarmDaysAheadToFetchFutureAlarms
            = new PrefEntry("**","alarmDaysAheadToFetchFutureAlarms", 30, "**"); //10 = 10 minutes after midnight

    //ITEM
    static PrefEntry checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress
            = new PrefEntry("Show "+Item.STATUS+" menu on click","checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress", false, "**");

    static PrefEntry changeSubtasksStatusWithoutConfirmationXXX
            = new PrefEntry("**","changeSubtasksStatusWithoutConfirmation", false, "**");

    static PrefEntry alwaysShowSubtasksExpandedInScreenItem
            = new PrefEntry("**","alwaysShowSubtasksExpandedInScreenItem", false, "**");

    static PrefEntry neverChangeProjectsSubtasksWhenChangingProjectStatusXXX
            = new PrefEntry("**","neverChangeProjectsSubtasksWhenChangingProjectStatus", false, "**");

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

    static PrefEntry itemInheritOwnerProjectProperties
            = new PrefEntry("Subtasks inherit properties from their project", "itemInheritOwnerProjectProperties", true, "Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerProjectChallenge
//            = new PrefEntry("Subtasks inherit "+Item.CHALLENGE+" from their project", "itemInheritOwnerProjectChallenge", true, "**");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.CHALLENGE), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerStarredProperties
//            = new PrefEntry(Format.f("Subtasks inherit %1 from their project","propert", "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.STARRED), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerProjectPriority
//            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectPriority", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.PRIORITY), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerProjectDreadFun
//            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectDreadFun", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.FUN_DREAD), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerProjectImportance
//            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectImportance", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.IMPORTANCE), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerProjectUrgency
//            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectUrgency", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.URGENCY), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerProjectDueDate
//            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectDueDate", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.DUE_DATE), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerProjectStartDate
//            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectStartDate", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.START_BY_TIME), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemInheritOwnerProjectWaitingTillDate
//            = new PrefEntry("**Subtasks inherit properties from their project", "itemInheritOwnerProjectWaitingTillDate", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");
            = new PrefEntry(Format.f("Subtasks inherit %1 from their project",Item.WAIT_UNTIL_DATE), "itemInheritOwnerStarredProperties", true, "**Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemEffortEstimateExtractFromStringInTaskText
            = new PrefEntry("Extract task estimates from text", "itemEffortEstimateExtractFromStringInTaskText", true, "Subtasks inherit properties due date**, priorities etc** from the project they belong to");
    
    static PrefEntry itemEffortEstimateKeepStringInTaskText
            = new PrefEntry("Keep task estimates text after extraction", "itemEffortEstimateKeepStringInTaskText", false, "Subtasks inherit properties due date**, priorities etc** from the project they belong to");

    static PrefEntry itemProjectPropertiesDerivedFromSubtasks
            = new PrefEntry("Project properties like [STARTED_ON] shows values from subtasks", "itemProjectPropertiesDerivedFromSubtasks", true, "**");

    static PrefEntry workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime
            = new PrefEntry(Format.f("Prioritize %1 from Categories","work time"), "workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime", true, "If one of a tasks categories has work time, use that to calculate the finish time instead of the work time of the Porject or List the task belongs to");

    //INTERNAL / TECHNICAL / CACHE
    static PrefEntry backgroundFetchIntervalInSeconds = new PrefEntry("**","backgroundFetchIntervalInSeconds", 1 * MyDate.HOUR_IN_MILISECONDS / 1000, "interval for updating eg local notifications and badge count when app is in background**");

    static PrefEntry cacheDynamicSize = new PrefEntry("**","cacheDynamicSize", 10000, "number of tasks, lists, categories etc cached**");
    static PrefEntry cacheDynamicSizeWorkSlots = new PrefEntry("**","cacheDynamicSizeWorkSlots", 10000, "number of WorkSlots cached**");

    static PrefEntry cacheLocalStorageSize
            = new PrefEntry("**","cacheLocalStorageSize", 10000, "deactivated if 0** need to implement Externalizable before this will work!!");

    static PrefEntry cacheLocalStorageSizeWorkSlots
            = new PrefEntry("**","cacheLocalStorageSizeWorkSlots", 10000, "deactivated if 0** need to implement Externalizable before this will work!!");

    static PrefEntry cacheMaxNumberParseObjectsToFetchInQueries
            = new PrefEntry("**","cacheMaxNumberParseObjectsToFetchInQueries", 10000, "deactivated if 0** need to implement Externalizable before this will work!!");

    static PrefEntry cacheLoadChangedElementsOnAppStart
            = new PrefEntry("cacheLoadChangedElementsOnAppStart (INTERNAL)","cacheLoadChangedElementsOnAppStart", true, "used to speed up app start during testing - NOT END USER");

    //LOOK AND FEEL
    static PrefEntry themeNameWithoutBackslash
            = new PrefEntry("**","themeNameWithoutBackslash", "theme", "name of the graphical theme");

    //OTHER / SYSTEM-LEVEL
    static PrefEntry enableCancelInAllScreens
            = new PrefEntry("**","enableCancelInAllScreens", false, "temporarily used to disable Cancel everywhere due to problems eg with too complex to Cancel when inserting Templates with subtasks");
    static PrefEntry dateShowDatesInUSFormat
            = new PrefEntry("**","dateShowDatesInUSFormat", false, "**");
    
    //REPEATRULE
    static PrefEntry repeatMaxInterval = new PrefEntry("**","repeatMaxInterval", 365, "maximun value for repeat interval**");
    static PrefEntry repeatMaxNumberFutureInstancesToGenerateAhead = new PrefEntry("xxx", "repeatMaxNumberFutureInstancesToGenerateAhead", 10, "**");
    static PrefEntry repeatMaxNumberFutureDaysToGenerateAhead = new PrefEntry("zzz", "repeatMaxNumberFutureDaysToGenerateAhead", 31, "**");
    static PrefEntry maxNumberRepeatInstancesToDeleteWithoutConfirmation = new PrefEntry("ttt", "maxNumberRepeatInstancesToDeleteWithoutConfirmation", 1, "**");
    static PrefEntry repeatSetRelativeFieldsWhenCreatingRepeatInstances
            = new PrefEntry("kkk", "repeatSetRelativeFieldsWhenCreatingRepeatInstances", true,
                    "set the relative date fields like Alarm/HideUntil/StartBy/Autoexpire** etc to same time before/after due date**");
    static PrefEntry repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule = new PrefEntry("Reuse existing instances when changing "+RepeatRuleParseObject.REPEAT_RULE, "repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule", true, "When changing a repeat rule, reuse any task instances that already exist, this will keep any edits to such instances");
    static PrefEntry repeatHidePreviousTasksDetails = new PrefEntry("lll", "repeatHidePreviousTasksDetails", true, "When editing an existing repeat rule, expand the list of existing tasks");

    //ITEMS IN LIST
    static PrefEntry itemListAlwaysShowHideUntilDate = new PrefEntry("ppp", "itemListAlwaysShowHideUntilDate", true, "**");
    static PrefEntry itemListAlwaysShowStartByDate = new PrefEntry("ooo", "itemListAlwaysShowStartByDate", true, "**");
    static PrefEntry itemListExpiresByDate = new PrefEntry("iii", "itemListExpiresByDate", true, "**");
    static PrefEntry itemListWaitingTillDate = new PrefEntry("uuu", "itemListWaitingTillDate", true, "**");
    static PrefEntry itemListShowRemainingEvenIfZero = new PrefEntry("In lists of tasks, show even zero [REMAINING]**", "itemListShowRemainingEvenIfZero", true, "**");

    //
    static PrefEntry creationLogInterval = new PrefEntry("yyy", "creationLogInterval", 30, "How many days back in time are included in " + ScreenMain.SCREEN_CREATION_LOG_TITLE);
    static PrefEntry completionLogInterval = new PrefEntry("rrr", "completionLogInterval", 30, "How many days back in time are included in " + ScreenMain.SCREEN_COMPLETION_LOG_TITLE);
    static PrefEntry overdueLogInterval = new PrefEntry("Past days in "+ScreenMain.SCREEN_OVERDUE_TITLE, "overdueLogInterval", 30, "How many days back in time are included in " + ScreenMain.SCREEN_OVERDUE_TITLE);
    static PrefEntry touchedLogInterval = new PrefEntry("eee", "touchedLogInterval", 30, "How many days back in time are included in " + ScreenMain.SCREEN_TOUCHED);
    
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
    
    //GLOBAL
    //localization
    static PrefEntry localeUserSelected = new PrefEntry("Language for text", "localeUserSelected", "", "Determines the language used. Use this setting to override the default language used on your device");
    
//LOGIN
    static PrefEntry loginStoreEmail = new PrefEntry("Show email on login", "loginStoreEmail", true, "**");
//    static PrefEntry loginStayLoggedIn = new PrefEntry("Stay logged in", "loginStayLoggedIn", true, "**");//DOESN'T make sense to log user out automatically (would happen each time app is switched to background since destroy is unreliable)
    static PrefEntry loginEmail = new PrefEntry("Your email:", "loginEmail", "", "**");
    static PrefEntry loginIncognitoMode = new PrefEntry("Is current user Incognito", "loginIncognitoMode", false, "**");
    static PrefEntry loginFirstTimeLogin = new PrefEntry("Is this the first time someone opens TodoCtatalyst on this device (NOT an end-user setting)", "loginFirstTimeLogin", true, "**");

    //WORKSLOT
    static PrefEntry workSlotDefaultDuration = new PrefEntry("Default work slot duration (min)", "workSlotDefaultDuration", 60, "**");
    static PrefEntry workSlotDefaultStartDateIsNow = new PrefEntry("Use current time as Default work slot start time", "workSlotDefaultStartDateIsNow", true, "**");

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
