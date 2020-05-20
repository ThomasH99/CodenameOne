/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.CheckBox;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;

/**
 *
 * @author Thomas
 */
public class Icons {

    //TODO smarter pattern for Icons https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
    private static Icons INSTANCE;
//    public static Font iconFont = Font.createTrueTypeFont("myIconFont", "myiconfont.ttf"); //name and filename
    public static Font iconFont = Font.createTrueTypeFont("myiconfont", "myiconfont.ttf"); //name and filename, file MUST reside in /src/ root!
    Label label = new Label();

    private Icons() {

    }

//    public static Icons get() {
//        if (INSTANCE == null) {
//
////            iconInsertNewTaskExdend = iconInsertNewTaskIndent.flipHorizontally(true); //flip the above image
//            INSTANCE = new Icons();
////            Display.getInstance().set
//        }
//        return INSTANCE;
//    }

    public static Image makeT(char material) {
//        return FontImage.createMaterial(material, UIManager.getInstance().getComponentStyle("TitleCommand"));
        return make(material, "TitleCommand");
    }

    public static Image makeL(char material) {
//        return FontImage.createMaterial(material, UIManager.getInstance().getComponentStyle("Label"));
        return make(material, "Label");
    }

    public static Image make(char material, String styleUIID) {
        return FontImage.createMaterial(material, UIManager.getInstance().getComponentStyle(styleUIID));
    }

//    private final static Style labelStyle = new Label().getStyle();
//    private final static Style toolBarStyle = new Toolbar().getStyle();
    //TODO!!! CN1 uses style "TitleCommand" for toolbar commands
//    private Style toolbarStyle = (getnew Label()).getStyle();

//    static Image iconShowMoreLabelStyle() {
//        return makeT(FontImage.MATERIAL_EXPAND_MORE);
//    }
//    final static char iconShowMoreLabelStyleXXX = FontImage.MATERIAL_EXPAND_MORE; //MORE=v
    final static char iconShowMore = FontImage.MATERIAL_CHEVRON_RIGHT; //FontImage.MATERIAL_EXPAND_MORE;, LESS=^MATERIAL_EXPAND_MORE
    public final static char iconExpandListStickyHeader = FontImage.MATERIAL_CHEVRON_RIGHT; //FontImage.MATERIAL_EXPAND_MORE;, LESS=^MATERIAL_EXPAND_MORE
//    final static Image iconShowMoreLabelStyleXXX = FontImage.createMaterial(iconShowMore, labelStyle);

    final static char iconShowLess = FontImage.MATERIAL_EXPAND_LESS; //MORE=v, MATERIAL_EXPAND_LESS
    public final static char iconCollapseListStickyHeader = FontImage.MATERIAL_EXPAND_MORE; //MORE=v, LESS=^  MATERIAL_EXPAND_LESS
//    final static Image iconShowLessLabelStyle = FontImage.createMaterial(iconShowLess, labelStyle);

    final static char iconAddTimeStampToComment = FontImage.MATERIAL_ACCESS_TIME;
//    final static Image iconAddTimeStampToCommentLabelStyle = FontImage.createMaterial(iconAddTimeStampToComment, labelStyle);
    //drag and drop handle icon
    final static char iconDragHandle = FontImage.MATERIAL_DRAG_HANDLE;
//    final static Image iconDragHandleLabelStyle = FontImage.createMaterial(iconDragHandle, labelStyle);

    //TIMER
//    final static Image iconTimerSymbolToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_TIMER, toolBarStyle);
//    static Image iconTimerSymbolToolbarStyle() {
//        return makeT(FontImage.MATERIAL_TIMER);
//    }
    static char iconLaunchTimer = FontImage.MATERIAL_TIMER;
    static char iconLaunchTimerAlreadyRunning = FontImage.MATERIAL_SHUTTER_SPEED;
//    final static Image iconTimerSymbolToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_TIMER, UIManager.getInstance().getComponentStyle("TitleCommand"));
//    final static Image iconTimerSymbolLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_TIMER, labelStyle);

//    static Image iconTimerOffToolbarStyle() {
//        return makeT(FontImage.MATERIAL_TIMER_OFF);
//    }
//    final static Image iconTimerOffToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_TIMER_OFF, toolBarStyle);

//    static Image iconTimerAutoStartTimerOnNextTaskToolbarStyle() {
//        return makeT(FontImage.MATERIAL_HISTORY);
//    }
//    final static Image iconTimerAutoStartTimerOnNextTaskToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_HISTORY, toolBarStyle); //History: clock with arrow around
//    final static Image iconTimerAutoStartTimerOnNextTaskToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_AUTORENEW, toolBarStyle); //Autorenew: two circular arrows
//    final static Image iconTimerAutoGotoNextTaskLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_PAUSE_CIRCLE_FILLED, labelStyle);
//    final static Image iconTimerAutoGotoNextTaskLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_PAUSE_CIRCLE_FILLED, labelStyle);
//    final static Image iconTimerStartLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_PLAY_CIRCLE_OUTLINE, labelStyle);
    final static char iconTimerStart = FontImage.MATERIAL_PLAY_CIRCLE_FILLED; //FontImage.MATERIAL_PLAY_CIRCLE_OUTLINE;
//    final static Image iconTimerPauseLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_PAUSE_CIRCLE_FILLED, labelStyle);
    final static char iconTimerPause = FontImage.MATERIAL_PAUSE_CIRCLE_FILLED;
//    final static Image iconTimerBuzzerOnOffLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_TRENDING_FLAT, labelStyle); //Trending flat = long right arrow
//    final static Image iconTimerBuzzerOnOffLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_FAST_FORWARD, labelStyle); //Fast Forward = Play next, 
//    final static Image iconTimerScreenAlwaysOnLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_LIGHTBULB_OUTLINE, labelStyle); //Fast Forward = Play next, 
//    final static Image iconTimerInterruptedTaskLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_FLASH_OFF, labelStyle); //Warning: triangle with '!', or use Flash Off to show it was interrupted
    final static char iconTimerStopExitTimer = FontImage.MATERIAL_STOP; //Warning: triangle with '!', or use Flash Off to show it was interrupted
//    final static Image iconTimerStopExitTimerLabelStyle = FontImage.createMaterial(iconTimerStopExitTimer, labelStyle); //Warning: triangle with '!', or use Flash Off to show it was interrupted
    final static char iconTimerNextTask = FontImage.MATERIAL_SKIP_NEXT; //like skip to next song
//    final static Image iconTimerNextTaskLabelStyle = FontImage.createMaterial(iconTimerNextTask, labelStyle); //like skip to next song

    //ALARM
    final static char iconSnooze = FontImage.MATERIAL_SNOOZE;
//    final static Image iconSnoozeLabelStyle = FontImage.createMaterial(iconSnooze, labelStyle);
    final static char iconAlarmOff = FontImage.MATERIAL_ALARM_OFF;
//    final static Image iconAlarmOffLabelStyle = FontImage.createMaterial(iconAlarmOff, labelStyle);
    final static char iconAlarmEdit = FontImage.MATERIAL_NOTIFICATIONS_NONE;

    //ITEM
//    final static Image iconAlarmSetLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_ALARM_ON, labelStyle);
//    final static Image iconAlarmSetLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_NOTIFICATIONS, labelStyle);
    final static char iconAlarmDate = FontImage.MATERIAL_NOTIFICATIONS_NONE;
    final static char iconAlarmTriggered = FontImage.MATERIAL_NOTIFICATIONS_ACTIVE; //TODO: custom icon
    final static char iconHideUntilDate = FontImage.MATERIAL_VISIBILITY_OFF; //TODO: custom icon
    final static char iconAutoCancelByDate = FontImage.MATERIAL_CANCEL_PRESENTATION; //TODO: custom icon
    final static char iconStartedOnDate = FontImage.MATERIAL_TIMELAPSE; //TODO: custom icon
    final static char iconCreatedDate = FontImage.MATERIAL_TRIP_ORIGIN; //TODO: custom icon
    final static char iconModifiedDate = FontImage.MATERIAL_EDIT; //TODO: custom icon
    
    final static char iconPriority = FontImage.MATERIAL_PRIORITY_HIGH; //TODO: custom icon
    final static char iconImportance =FontImage.MATERIAL_GRID_ON; // FontImage.MATERIAL_ISO; //TODO: custom icon
    final static char iconUrgency = FontImage.MATERIAL_APPS; //TODO: custom icon

    final static char iconEditFilterSort = FontImage.MATERIAL_IMPORT_EXPORT; //MATERIAL_REORDER; //MATERIAL_LOW_PRIORITY;
    final static char iconFilter = FontImage.MATERIAL_FILTER_LIST;
    final static char iconHideDoneTasks = FontImage.MATERIAL_CHECK_CIRCLE;
    final static char iconShowDoneTasks = FontImage.MATERIAL_CHECK_CIRCLE_OUTLINE;
    final static char iconEditProperties = FontImage.MATERIAL_EDIT;
    final static char iconAddFromTemplate = FontImage.MATERIAL_FLIP_TO_FRONT; //FontImage.MATERIAL_ADD_BOX;
    final static char iconSaveAsTemplate = FontImage.MATERIAL_FLIP_TO_BACK;
    final static char iconDelete = FontImage.MATERIAL_DELETE_FOREVER;
    final static char iconRepair = FontImage.MATERIAL_BUILD;
    final static char iconHelp = FontImage.MATERIAL_LIVE_HELP;
    final static char iconReportIssue = FontImage.MATERIAL_BUG_REPORT; //FontImage.MATERIAL_ANNOUNCEMENT
    final static char iconCancel = FontImage.MATERIAL_UNDO;

    //screenItem2 tab icons
    final static char iconMainTab = FontImage.MATERIAL_VIEW_STREAM;
    final static char iconTimeTab = FontImage.MATERIAL_ACCESS_TIME;
    final static char iconPrioTab = FontImage.MATERIAL_PRIORITY_HIGH;
    final static char iconStatusTab = FontImage.MATERIAL_MORE_HORIZ;

    final static char iconSettings = FontImage.MATERIAL_SETTINGS;
    final static char iconInsertTaskAbove = FontImage.MATERIAL_CALL_MADE;
    final static char iconInsertTaskBelow = FontImage.MATERIAL_SUBDIRECTORY_ARROW_RIGHT; //TODO: make as horizontal flip of MATERIAL_CALL_MADE

    //Main screen menu icons
    final static char iconMainOverdue = FontImage.MATERIAL_ASSIGNMENT_LATE;
    final static char iconMainToday = FontImage.MATERIAL_TODAY;
    final static char iconMainNext = FontImage.MATERIAL_ASSIGNMENT_RETURNED;
    final static char iconMainInbox = FontImage.MATERIAL_INBOX;
    final static char iconMainLists = FontImage.MATERIAL_LIST; //_ALT; //FontImage.MATERIAL_FOLDER_OPEN;
    final static char iconMainCategories = FontImage.MATERIAL_FOLDER_OPEN; //FontImage.MATERIAL_FOLDER_SPECIAL;
    final static char iconMainProjects = FontImage.MATERIAL_FORMAT_ALIGN_LEFT;
    final static char iconMainWorkSlots = FontImage.MATERIAL_WORK_OUTLINE; //=iconWorkSlot; //FontImage.MATERIAL_WORK_OUTLINE; //MATERIAL_NEXT_WEEK; //WORK;  //TODO: MATERIAL_WORK_OUTLINE
    final static char iconMainTemplates = FontImage.MATERIAL_BORDER_STYLE;
    final static char iconMainStatistics = FontImage.MATERIAL_EVENT_AVAILABLE;
    final static char iconMainImprove = FontImage.MATERIAL_LOUPE; //or feedback, perspective/helicopter, flight_takeoff
    final static char iconMainCompletionLog = FontImage.MATERIAL_PLAYLIST_ADD_CHECK; //.MATERIAL_EVENT_AVAILABLE;  
    final static char iconMainCreationLog = FontImage.MATERIAL_PLAYLIST_ADD; //FontImage.MATERIAL_ASSESSMENT;  //   FontImage.MATERIAL_POLL;  
    final static char iconMainTouched = FontImage.MATERIAL_PLAYLIST_PLAY; //FontImage.MATERIAL_DATE_RANGE; 
    final static char iconMainAllTasks = FontImage.MATERIAL_FORMAT_LIST_BULLETED; //FontImage.MATERIAL_LIST;
    final static char iconMainTutorial = FontImage.MATERIAL_HELP_OUTLINE;
    final static char iconMainInspirationLists = FontImage.MATERIAL_FAVORITE_BORDER; //TOUCH_APP
    final static char iconMainWeb = FontImage.MATERIAL_WEB;
    final static char iconMainAlarms = FontImage.MATERIAL_NOTIFICATIONS_ACTIVE; //TODO: MATERIAL_NOTIFICATION_IMPORTANT
    final static char iconInspiration = FontImage.MATERIAL_HIGHLIGHT; 
    
    //LIST/CATEGORY icons
    final static char iconList = FontImage.MATERIAL_LIST;
    final static char iconCategory = FontImage.MATERIAL_FOLDER_OPEN; //FontImage.MATERIAL_FOLDER;
    final static char iconListNew = FontImage.MATERIAL_CREATE_NEW_FOLDER; //TODO custom icon: outline with + inside
    final static char iconCategoryNew = FontImage.MATERIAL_CREATE_NEW_FOLDER;
    final static char iconOwner = FontImage.MATERIAL_LIST_ALT;
    final static char iconObjectId = FontImage.MATERIAL_VPN_KEY;

//    final static char iconFilter = FontImage.MATERIAL_FILTER_LIST;
//    final static Image iconSettingsLabelStyle = FontImage.createMaterial(iconSettings, labelStyle);

//    final static Image iconSettingsApplicationLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_SETTINGS_APPLICATIONS, labelStyle);

//    final static Image iconLowPriorityLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_WATCH_LATER, labelStyle);

    final static char iconStarUnselected = FontImage.MATERIAL_STAR_BORDER;
//    final static char iconStarUnselectedLabelStyleMaterial = FontImage.MATERIAL_STAR_BORDER;
//    final static Image iconStarUnselectedLabelStyle = FontImage.createMaterial(iconStarUnselected, labelStyle);

    final static char iconStarSelected = FontImage.MATERIAL_STAR;
    final static char iconStarLabel = FontImage.MATERIAL_STAR_HALF; //FontImage.MATERIAL_STARS;
//    final static Image iconStarSelectedLabelStyle = FontImage.createMaterial(iconStarSelected, labelStyle);
//    final static Image iconSelectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_RADIO_BUTTON_CHECKED, labelStyle, 2); //3mm

    final static char iconSelected = FontImage.MATERIAL_CHECK_BOX; //FontImage.MATERIAL_SELECT_ALL
//    final static Image iconSelectedLabelStyle = FontImage.createMaterial(iconSelected, labelStyle, 2); //3mm

    final static char iconSelectAll = FontImage.MATERIAL_DONE_ALL; //TODO: find better symbol (with unchecked boxes)
    final static char iconSetMultiple = FontImage.MATERIAL_VIEW_LIST; //TODO: find better symbol (with unchecked boxes)
    final static char iconSetDeleteAll = FontImage.MATERIAL_DELETE_SWEEP; //TODO: find better symbol (with unchecked boxes)
    final static char iconMoveAllToTop = FontImage.MATERIAL_ARROW_UPWARD; //TODO: find better symbol (with unchecked boxes)
    final static char iconMoveAllToEnd = FontImage.MATERIAL_ARROW_DOWNWARD; //TODO: find better symbol (with unchecked boxes)
    final static char iconInvertSelection = FontImage.MATERIAL_FLIP; //TODO: find better symbol (with unchecked boxes)

    final static char iconCompletedDate = FontImage.MATERIAL_DONE; //TODO: find better symbol (with unchecked boxes)
    
    final static char iconRepeatOverview = FontImage.MATERIAL_FILTER; //or: DYNAMIC_FEED

    final static char iconSelectedLabelStyleMaterial = FontImage.MATERIAL_CHECK_BOX; //3mm
//    final static Image iconUnselectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_RADIO_BUTTON_UNCHECKED, labelStyle, 2);
//    final static Image iconUnselectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_BOX_OUTLINE_BLANK, labelStyle, 2);
    final static char iconUnselectedLabelStyleMaterial = FontImage.MATERIAL_CHECK_BOX_OUTLINE_BLANK;
//    final static Image iconTemplateStatusSymbolLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_DO_NOT_DISTURB_ALT, labelStyle);

//    static Image iconSetDueDateToToday() {
//        return makeT(FontImage.MATERIAL_TODAY);
//    }
    static char iconSetDueDateToTodayMaterial = FontImage.MATERIAL_TODAY;
    static char iconSetDueDateToTodayFontImageMaterial = FontImage.MATERIAL_TODAY;
//    final static Image iconSetDueDateToToday = FontImage.createMaterial(FontImage.MATERIAL_TODAY, labelStyle);
//    final static Image iconIndentExdendInsertNewTask = FontImage.createMaterial(FontImage.MATERIAL_COMPARE_ARROWS, labelStyle);
//    final static char iconIndentExdendInsertNewTask = FontImage.MATERIAL_TRANSFORM; //TODO: make icons showing a task indended to subtask and a subtask exdended to super-task
    final static char iconIndentExdendInsertNewTask = FontImage.MATERIAL_SWAP_HORIZ; //Other possible icons: FIRST_PAGE/LAST_PAGE, SUBDIRECTORY_ARROW_RIGHT/LEFT
//    final static Image iconInsertNewTaskIndent = FontImage.createMaterial(FontImage.MATERIAL_FORWARD, labelStyle); //fat arrow
//    final static Image iconInsertNewTaskIndent = FontImage.createMaterial(FontImage.MATERIAL_ARROW_FORWARD, labelStyle); //MATERIAL_TRENDING_FLAT:thin arrow, TODO!! should be MATERIAL_KEYBOARD_RETURN flipped, MATERIAL_SUBDIRECTORY_ARROW_RIGHT
//    final static Image iconInsertNewTaskExdend = iconInsertNewTaskIndent.flipHorizontally(true); //flip the above image
//    static Image iconInsertNewTaskExdend = FontImage.createMaterial(FontImage.MATERIAL_TRENDING_FLAT, labelStyle); //thin arrow
//    static Image iconInsertNewTaskExdend = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, labelStyle); //thin arrow, MATERIAL_KEYBOARD_BACKSPACE
//    static Image iconInsertNewTaskExdend; //init in constructor above // = iconInsertNewTaskIndent.flipHorizontally(true); //flip the above image
//    static Image iconInsertNewTaskExdend= iconInsertNewTaskIndent.rotate180Degrees(true); //flip the above image

//    final static Image iconCreateSubTask = FontImage.createMaterial(FontImage.MATERIAL_PLAYLIST_PLAY, labelStyle);
    final static char iconSubTasks = FontImage.MATERIAL_FORMAT_INDENT_INCREASE; //TODO custom icon //MATERIAL_FORMAT_ALIGN_LEFT

    //TEMPLATE
    final static char iconNewItemFromTemplate = FontImage.MATERIAL_LIBRARY_ADD;
//    final static Image iconNewItemFromTemplateImage = FontImage.createMaterial(iconNewItemFromTemplate, labelStyle);

    //COMMANDS
    final static char iconCmdSortOnOff = FontImage.MATERIAL_SORT;
//    final static Image iconCmdSortOnOffToolbarStyle = FontImage.createMaterial(iconCmdSortOnOff, toolBarStyle);
    

    final static char iconCmdShowTaskDetails = FontImage.MATERIAL_SUBTITLES;
//    final static Image iconCmdShowTaskDetailsLabelStyle = FontImage.createMaterial(iconCmdShowTaskDetails, toolBarStyle);

    //FUTURE
//    final static Image iconEncryptedSecurityLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_SECURITY, labelStyle); //mark encrypted tasks
//    final static Image iconGraphsLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_POLL, labelStyle); //mark encrypted tasks

//    final static Image iconFunLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_SENTIMENT_SATISFIED, iconStyle);
    //CLEAR FIELD
    final static char iconCloseCircle = FontImage.MATERIAL_CANCEL; //MATERIAL_CLOSE; //or: FontImage.MATERIAL_HIGHLIGHT_OFF
//    final static Image iconCloseCircleLabelSty = FontImage.createMaterial(iconCloseCircle, labelStyle);
//    final static Image iconCloseCircleLabelStyle = FontImage.createMaterial(iconCloseCircle, labelStyle);

    //TASK STATUS
//<editor-fold defaultstate="collapsed" desc="comment">
//    final static Image iconCheckboxCreated = FontImage.createMaterial(FontImage.MATERIAL_CHECK_BOX_OUTLINE_BLANK, labelStyle);
//    final static char iconCheckboxCreatedChar = FontImage.MATERIAL_RADIO_BUTTON_UNCHECKED;
//    final static char iconCheckboxCancelledChar = FontImage.MATERIAL_REMOVE_CIRCLE; //dark circle to illustrate 'done with'
//    final static char iconCheckboxOngoingChar = FontImage.MATERIAL_TIMELAPSE;
//    final static char iconCheckboxDoneChar = FontImage.MATERIAL_CHECK_CIRCLE;
//    final static char iconCheckboxWaitingChar = FontImage.MATERIAL_PAUSE_CIRCLE_OUTLINE;
//</editor-fold>
    final static char iconItemStatusCreated = FontImage.MATERIAL_RADIO_BUTTON_UNCHECKED;
//    final static char iconCheckboxCancelledChar = FontImage.MATERIAL_REMOVE_CIRCLE; //dark circle to illustrate 'done with'
    final static char iconItemStatusCancelled = FontImage.MATERIAL_HIGHLIGHT_OFF; //dark circle to illustrate 'done with' //MATERIAL_REMOVE_CIRCLE_OUTLINE, MATERIAL_BLOCK, MATERIAL_NOT_INTERESTED
    final static char iconItemStatusOngoing = FontImage.MATERIAL_TIMELAPSE;
    final static char iconItemStatusDone = FontImage.MATERIAL_CHECK_CIRCLE; //TODO: should be MATERIAL_CHECK_CIRCLE_OUTLINE but not in CN1
    final static char iconItemStatusWaiting = FontImage.MATERIAL_PAUSE_CIRCLE_OUTLINE;
//    final static Image iconCheckboxCreatedLabelStyle = FontImage.createMaterial(iconItemStatusCreated, labelStyle); //TODO NOT GOOD - same symbol as multiple selection
//    final static Image iconCheckboxCancelled = FontImage.createMaterial(FontImage.MATERIAL_INDETERMINATE_CHECK_BOX, labelStyle);
//    final static Image iconCheckboxCancelledLabelStyle = FontImage.createMaterial(iconItemStatusCancelled, labelStyle); //dark circle to illustrate 'done with'
//    final static Image iconCheckboxDone = FontImage.createMaterial(FontImage.MATERIAL_CHECK_BOX, labelStyle);
//    final static Image iconCheckboxOngoingLabelStyle = FontImage.createMaterial(iconItemStatusOngoing, labelStyle);
//    final static char iconCheckboxDone = iconItemStatusDone;
//    final static Image iconCheckboxDoneLabelStyle = FontImage.createMaterial(iconItemStatusDone, labelStyle);
//    final static Image iconCheckboxWaiting = FontImage.createMaterial(FontImage.MATERIAL_ACCOUNT_BOX, iconStyle);
//    final static Image iconCheckboxWaiting = FontImage.createMaterial(FontImage.MATERIAL_HOURGLASS_FULL, labelStyle);
//    final static Image iconCheckboxWaitingLabelStyle = FontImage.createMaterial(iconItemStatusWaiting, labelStyle);

    final static char iconCommentTimeStamp = FontImage.MATERIAL_SCHEDULE;

    final static char iconWaitingAlarm = FontImage.MATERIAL_PAUSE; //TODO custom
    final static char iconWaitingDateMaterial = FontImage.MATERIAL_PAUSE; //TODO custom
    final static char iconSetWaitingDateMaterial = FontImage.MATERIAL_PAUSE_PRESENTATION;
//    final static Image iconWaitingDate = FontImage.createMaterial(iconWaitingDateMaterial, labelStyle);

    final static char iconStartByDate = FontImage.MATERIAL_PRESENT_TO_ALL; //FontImage.MATERIAL_EXIT_TO_APP; //TODO custom icon
    final static char iconExpireByDate = FontImage.MATERIAL_CANCEL;//FontImage.MATERIAL_EVENT_BUSY;

    final static char iconDreadFunNeutral = FontImage.MATERIAL_THUMBS_UP_DOWN; //MATERIAL_SENTIMENT_VERY_SATISFIED;
    final static char iconFun = FontImage.MATERIAL_THUMB_UP; //MATERIAL_SENTIMENT_VERY_SATISFIED
    final static char iconDread = FontImage.MATERIAL_THUMB_DOWN; //FontImage.MATERIAL_SENTIMENT_VERY_DISSATISFIED;

    final static char iconChallengeVeryEasy = FontImage.MATERIAL_SENTIMENT_VERY_SATISFIED;
    final static char iconChallengeEasy = FontImage.MATERIAL_SENTIMENT_SATISFIED;
    final static char iconChallengeAverage = FontImage.MATERIAL_RADIO_BUTTON_UNCHECKED;
    final static char iconChallengeHard = FontImage.MATERIAL_SENTIMENT_DISSATISFIED;
    final static char iconChallengeVeryHard = FontImage.MATERIAL_SENTIMENT_VERY_DISSATISFIED;
    
    final static char iconEarnedValue = FontImage.MATERIAL_ATTACH_MONEY;
    final static char iconEarnedValuePerHour = FontImage.MATERIAL_MONETIZATION_ON;

    final static char iconWorkSlot = FontImage.MATERIAL_WORK_OUTLINE; //FontImage.MATERIAL_WORK;

//    final static Image iconWorkSlotLabelStyle = FontImage.createMaterial(iconWorkSlot, labelStyle);
//    final static Image iconFinishDate = FontImage.createMaterial(FontImage.MATERIAL_DONE, labelStyle);
//    final static Image iconFinishDate = FontImage.createMaterial(FontImage.MATERIAL_EVENT, labelStyle);
    final static char iconFinishDateMaterial = FontImage.MATERIAL_EVENT_AVAILABLE;
//    final static Image iconFinishDate = FontImage.createMaterial(FontImage.MATERIAL_EVENT_AVAILABLE, labelStyle);

//    final static char iconEstimateMaterial = FontImage.MATERIAL_SETTINGS_BACKUP_RESTORE;
    final static char iconEstimateMaterial = FontImage.MATERIAL_HOURGLASS_FULL;
//    final static char iconEstimateMaterial = FontImage.MATERIAL_RESTORE;
//    final static Image iconActualEffort = FontImage.createMaterial(FontImage.MATERIAL_TIMELAPSE, labelStyle);
    final static char iconActualEffort = FontImage.MATERIAL_HOURGLASS_FULL; //MATERIAL_WATCH_LATER, FontImage.MATERIAL_TIMELAPSE;
//    final static Image iconActualEffortLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_HOURGLASS_FULL, labelStyle);
//    final static char iconRemainingEffortMaterial = FontImage.MATERIAL_TIMELAPSE;
    final static char iconRemainingEffort = FontImage.MATERIAL_HOURGLASS_EMPTY; //MATERIAL_EVENT_AVAILABLE;
//    final static Image iconRemainingEffortLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_EVENT_AVAILABLE, labelStyle);
//    final static char iconRemainingEffortMaterial = FontImage.MATERIAL_EVENT_AVAILABLE;
//    final static char iconRemainingEffortMaterial = FontImage.MATERIAL_RESTORE;
    final static char iconEffortProject = FontImage.MATERIAL_MORE_VERT; //MATERIAL_EVENT_AVAILABLE;

    //REPEAT
    final static char iconRepeat = FontImage.MATERIAL_REPLAY; //MATERIAL_REPEAT; MATERIAL_SYNC; //MATERIAL_BACKUP; //MATERIAL_EVENT_AVAILABLE;
    final static char iconSimulateRepeatDates = FontImage.MATERIAL_REFRESH; //MATERIAL_EVENT_AVAILABLE;
    final static char iconShowGeneratedTasks = FontImage.MATERIAL_SETTINGS_BACKUP_RESTORE; //MATERIAL_EVENT_AVAILABLE;

    final static char iconBackToPreviousScreen = FontImage.MATERIAL_ARROW_BACK; //MATERIAL_EVENT_AVAILABLE;

//    static Image iconBackToPrevFormToolbarStyle() {
//        return makeT(iconBackToPreviousScreen);
//    }

//    static Image iconBackToPrevFormLabelStyle() {
//        return makeL(iconBackToPreviousScreen);
//    }
//    final static Image iconBackToPrevFormToolbarStyle = FontImage.createMaterial(iconBackToPreviousScreen, toolBarStyle);

    final static char iconEdit = FontImage.MATERIAL_NAVIGATE_NEXT; //MATERIAL_ARROW_FORWARD_IOS; //; //MATERIAL_ARROW_FORWARD; //MATERIAL_CHEVRON_RIGHT;
    final static char iconEditMyFont = '\ue809';

//    static Image iconEditPropertiesToolbarStyle() {
//        return makeT(iconEdit);
//    }
//    final static Image iconEditPropertiesToolbarStyle = FontImage.createMaterial(iconEdit, toolBarStyle);
//    final static Image iconEditPropertiesLabelStyle = FontImage.createMaterial(iconEdit, labelStyle);
//    static Image iconNewTaskToolbarStyle(){return makeT(FontImage.MATERIAL_ADD_CIRCLE);}
//    final static char iconEditSymbol = FontImage.MATERIAL_CHEVRON_RIGHT;
//    final static Image iconEditSymbolLabelStyle = FontImage.createMaterial(iconEdit, labelStyle);
    final static char iconEditSymbol = FontImage.MATERIAL_CHEVRON_RIGHT;

    final static char iconNewTaskToInbox = FontImage.MATERIAL_ADD_BOX;//FontImage.MATERIAL_ADD_CIRCLE_OUTLINE;

//    static Image iconNewTaskToolbarStyle() {
//        return makeT(iconNewTaskToInbox);
//    }

    final static char iconNew = FontImage.MATERIAL_ADD;

//    static Image iconNewToolbarStyle() {
//        return makeT(iconNew);
//    }
//    final static Image iconNewToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_ADD, toolBarStyle);

//    static Image iconInterruptToolbarStyle() {
//        return makeT(FontImage.MATERIAL_FLASH_ON);
//    }
    final static char iconInterrupt = FontImage.MATERIAL_FLASH_ON;
//    final static Image iconInterruptToolbarStyle = FontImage.createMaterial(iconInterrupt, toolBarStyle);
//    final static Image iconEditSymbolToolbarStyle = FontImage.createMaterial(iconEdit, toolBarStyle);
    //TASK STATUS
//    final static Image iconCheckedTaskToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, toolBarStyle);
//    final static Image iconUncheckedTaskToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, toolBarStyle);
//    final static Image iconCheckMarkToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, toolBarStyle);
//    final static Image iconAlarmSetToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_ALARM_ON, toolBarStyle);
    //EXPAND lists
//    final static Image iconShowMoreToolbarStyle = FontImage.createMaterial(iconShowMore, toolBarStyle);
//    final static Image iconShowLessToolbarStyle = FontImage.createMaterial(iconShowLess, toolBarStyle);
//    final static Image iconMoveUpDownToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_SWAP_VERT, toolBarStyle);
    final static char iconMoveUpDown = FontImage.MATERIAL_SWAP_VERT;

    //LOGIN
//    final static Image iconPerson = FontImage.createMaterial(FontImage.MATERIAL_PERSON, toolBarStyle);
    final static char iconPerson = FontImage.MATERIAL_PERSON;
//    final static Image iconPersonNew = FontImage.createMaterial(FontImage.MATERIAL_PERSON_ADD, toolBarStyle);
    final static char iconPersonNew = FontImage.MATERIAL_PERSON_ADD;
//    final static Image iconPersonIngocnito = FontImage.createMaterial(FontImage.MATERIAL_HELP, toolBarStyle);

    //OTHER
//    final static Image iconLightBulb = FontImage.createMaterial(FontImage.MATERIAL_LIGHTBULB_OUTLINE, toolBarStyle); //submit ideas, suggestions or bugs
//    final static Image iconFeedback = FontImage.createMaterial(FontImage.MATERIAL_FEEDBACK, toolBarStyle); //feedback speach bubble (could be used for submit ideas, suggestions or bugs)

//    static Image getCheckBoxIcon(ItemStatus itemStatus) {
//        switch (itemStatus) {
//            case CREATED:
//                return iconCheckboxCreatedLabelStyle;
//            case DONE:
//                return iconCheckboxDoneLabelStyle;
//            case ONGOING:
//                return iconCheckboxOngoingLabelStyle;
//            case WAITING:
//                return iconCheckboxWaitingLabelStyle;
//            case CANCELLED:
//                return iconCheckboxCancelledLabelStyle;
//            default:
//                assert false : "undefined task status";
//        }
//        return null;
//    }

}
