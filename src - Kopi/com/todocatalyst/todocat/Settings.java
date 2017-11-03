package com.todocatalyst.todocat;

import com.codename1.io.Externalizable;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.parse4cn1.ParseUser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * The memory layout of Settings: RMS record 1: Settings Settings contain all
 * the predefined lists that the user may access at start up?! Implemented based
 * on http://en.wikipedia.org/wiki/Singleton_pattern#The_solution_of_Bill_Pugh
 * (Bill Pugh's solution) TODO!: change Settings: create GlobalSettings shared
 * by the whole application, and Settings that each
 *
 * @author Thomas
 */
public class Settings extends ParseUser implements Externalizable {

    static String CLASS_NAME = "Settings"; //filename for saved items
    private static String settingsLogicalName = "Settings"; //filename for saved items
    static String alarmServerFilename = "AlarmServer";
    static String inboxFilename = "Inbox";
    static String categoriesFileName = "Categories";
    static String RMSFilename = "TodoRMS"; //filename for saved items
//    private String filename = "TodoRMS"; //filename for saved items
//    private String themesFilename = "/businessTheme13.res"; //filename for saved items
//    private String themesFilename = "/TODOtheme1.res"; //filename for saved items
//    private String themesFilename = "/TODOtheme1.res"; //filename for saved items
//    private String themesFilename = "/iPhoneTheme.res"; //filename for saved items
//    private String themesFilename = "/myresFile.res"; //filename for saved items
//    private String defaultThemeName = "iPhone Theme"; 
//    private String defaultThemeName = "myTheme"; //filename for saved items
    /**
     * all fonts are small
     */
    final static int FONT_SIZE_ALLSMALL = 1;
    /**
     * normal font is medium, secondary text is in small font
     */
    final static int FONT_SIZE_MEDIUMSMALL = 2;
    /**
     * normal font is large, secondary text is in medium font
     */
    final static int FONT_SIZE_LARGEMEDIUM = 3;
    /**
     * all fonts are Large
     */
    final static int FONT_SIZE_ALLLARGE = 4;
    int fontsize = FONT_SIZE_MEDIUMSMALL;
    final static int SCREEN_LAYOUT_PORTRAIT = 1;
    final static int SCREEN_LAYOUT_LANDSCAPE = 2;
//    private int screenOrientation = SCREEN_LAYOUT_PORTRAIT; //NOT used, replaced by call to Display
//    private int screenLayout = SCREEN_NORMAL; //SCREEN_COMPACT;
//    final static int SCREEN_NORMAL = 1;
//    final static int SCREEN_COMPACT = 2;
    /**
     * use compact layout, where e.g. labels and input fields are forced onto
     * scame line even in portrain screens to have space. Label can be tickering
     */
    private boolean screenLayoutCompact;
//    final static int PORTRAIT = 0;
//    final static int LANDSCAPE = 1;
//    final static int COMPACT = 2;
//    private int screenLayout = PORTRAIT;
    private String defaultDateFormat = "D/L Y";
    private String defaultDateAndTimeFormat = "D/L Y H:N";
    private String defaultDurationFormat = "k:N:S";
    String smallFontName = "small";
    Font small;
    String normalFontName = "normal";
    Font normal;
    private boolean askToUpdateActual = true;
    private boolean xaskToUpdateActual = true;
    private boolean askToUpdateRemaining = true;
    private boolean alwaysUseRemainingAsEstimateWhenActualIsZero = true;
    private boolean alwaysUpdateRemainingToEffortMinusActualWhenEffortIsUpdated = true;
    private boolean alwaysSetEstimateToFirstRemaining = true;
    private boolean markDoneIfRemainingReducedToZero = true;
    private boolean markDoneIfCompletedDateSet = true;
    private boolean xautoUpdateRemainingWhenActualReduced = true;
    private boolean addMothersInGetLeafs = true;
    private int defaultListIndentInPixels = 10;
    private int charsInShortDates = 3; //number of characters shown for abbreviated weekdays, months in ListView
    private int charsInShortMonths = 3; //number of characters shown for abbreviated weekdays, months in ListView
    private byte storageFormatVersion = 0; //format in which RMS record should be saved (is stored in BaseItem and used to ensure that old files are read in correctly when upgrading to a new application version)
    private boolean addItemsToBeginningOfCategories = true;
    private boolean addItemsToBeginningOfInvisbleSubLists = true;
    private boolean addItemsToBeginningOfSubLists = true;
    private boolean setStatusOngoingWhenActualEffortSetFirstTime = true;
    private boolean setStatusToCreatedIfActualReducedToZero = true;
    private boolean isCategoryExportCaseSensitive; //=true;
    private boolean isCategoryImportCaseSensitive; //=true;
    private int itemTimerWarningLimit = MyDate.MINUTE_IN_MILLISECONDS * 60;
    private boolean forcePriorityValuesIntoImpUrgencyMatrixValues; //=true;
    private int timerReminderInterval = MyDate.SECOND_IN_MILLISECONDS * 5; //TODO!!!!: MyDate.MINUTE_IN_MILLISECONDS*5; //miliseconds
    private int timerScreenRefreshInterval = MyDate.SECOND_IN_MILLISECONDS * 60;
    private int timerReminderVibrateRepeats = 2;
    private int timerReminderVibraterDuration = 200; //miliseconds
    private int timerReminderVibraterPause = 500; //miliseconds
    private int timerReminderFlashRepeats = 3;
    private int timerReminderFlashDuration = 200; //miliseconds
    private int timerReminderFlashPause = 200; //miliseconds
    private boolean continueTimerWithPreviousActual = true; //TODO!!!!: back to false (true just to try it out)
    private int alarmSoundRepeats = 4;
    private int alarmSoundDuration = MyDate.SECOND_IN_MILLISECONDS * 2;
    private int alarmSoundPause = MyDate.SECOND_IN_MILLISECONDS * 4; //miliseconds
    private boolean addDatesToWaitingComments = true;
    private String waitingCommentsStandardText = "WAIT "; //"Set to Waiting ";
    private long defaultWaitingTime = MyDate.DAY_IN_MILLISECONDS * 7;
    private int defaultSnoozeTime = 5000; //TODO!!!!: set back to default MyDate.MINUTE_IN_MILLISECONDS * 7;
    private int defaultSnoozeTimeSpecial = MyDate.MINUTE_IN_MILLISECONDS * 60;
    private int priorityValueMin = -10;
    private int priorityValueMax = 10;
    private boolean forceQwertyMode = false;
    private int remainingIncreaseLimit1 = MyDate.MINUTE_IN_MILLISECONDS * 30;
    private int remainingIncreaseLimit2 = MyDate.MINUTE_IN_MILLISECONDS * 60;
    private int remainingIncreaseBelowLimit1 = MyDate.MINUTE_IN_MILLISECONDS * 5;
    private int remainingIncreaseAboveLimit1 = MyDate.MINUTE_IN_MILLISECONDS * 10;
    private int remainingIncreaseAboveLimit2 = MyDate.MINUTE_IN_MILLISECONDS * 15;
    private String locale; // = ""; //by default no locale is set
    private String language; // = ""; //by default no locale is set
    private boolean weeksStartOnMondays = true;
    private boolean iSOWeekNumbering = true;
    private boolean storePastCalendarItemWorkSlots = false;
    private int defaultClassAccessibility = 0;
    private int defaultPriority = 0;
    private boolean waitingReminderByDefault;
    private int defaultNumberOfDaysAheadCalendarWorkSlotImport = WorkTimeDefinition.IMPORT_OPTION_AUTOMATIC;
    private boolean silentCalendarImport;
    private boolean adjustWorkSlotsWhenStartIsInThePast = true;
    private boolean alwaysDeleteAllInstancesWhenDeletingARepeatRule = false; //expected default behaviour is delete all instances //-NO, since you may want to delete just a single of amny generated instances (eg too many from template)
    private boolean reuseExistingInstancesAfterChangingRepeatRule = true; //TODO!!! should be off be default?!
    private int defaultNumberOfRepeatInstancesBuffered = 3;
    private int defaultDaysAddedToDue = 7;
    private boolean allowNumericKeysToSelectMenuCommand = false;
    private int[] groupingDurationIntervalsInMinutes = new int[]{0, 15, 30, 60, 120, 180, 240, 300};
    private String groupingDurationIntervalUnitString = "min";
    private int groupingUnitInMilliseconds = MyDate.MINUTE_IN_MILLISECONDS;
    private int groupingDefaultGroupDatesByValue = 0; //GroupingDefinition.GROUP_DATES_BY_MONTH;
    private int nextGuid = 1; //initial value 1
//    private int insertPositionOfNewRepeatInstances = ItemList.INSERT_BEFORE_REFERENCE_ITEM;
//    private int insertPositionOfNewRepeatInstances = INSERT_BEFORE_REFERENCE_ITEM;
//    private int insertPositionOfInitialRepeatInstances = ItemList.INSERT_AT_HEAD_OF_LIST;
//    private int insertPositionOfInitialRepeatInstances = INSERT_AT_HEAD_OF_LIST;
//    private boolean addRepeatInstancesToEndOfList = false; //-not used
    private int numberOfChangedItemsRequiringConfirmation = 2;
//    private boolean addNewInboxItemsToHeadOfInbox = true;
//    private int defaultHourOfDay = 8;
//    private int defaultMinuteOfDay = 00;
//    private int getNumberDecimalsForEarnedPointsPerHour = 2;
//    private int nbSubtasksToChangeStatusWithoutConfirmation = 2;
    //////////////////// ARRAY OF ALL SETTINGS ////////////////////////////
    /////////// update with every setting /////////////////
//    static Setting[] settings = new Setting[100];
//    static Setting[] globalSettings = new Setting[100];
//    static Setting[] categoriesSettings = new Setting[100];
//    static Setting[] timerSettings = new Setting[100];
//    {
////        new Setting() {
////            void updateValue() {
////            }
////        }
//    };
    int settingIndex = 0;
    int globalIndex = 1;
    int categoriesIndex = 2;
    int workslotIndex = 3;
    int timerIndex = 4;
    int repeatIndex = 5;
    int tasksIndex = 6;
    final static int GROUP_GLOBAL = 0;
    final static int GROUP_CATEGORIES = 1;
    final static int GROUP_WORKSLOT = 2;
    final static int GROUP_TIMER = 3;
    final static int GROUP_REPEAT = 4;
    final static int GROUP_TASKS = 5;
    final static int GROUP_X = 5;
    final static int GROUP_LAST = GROUP_X;
    static Setting[][] settings = new Setting[GROUP_LAST][100];
    static int[] settingsIndex = new int[GROUP_LAST];
    
    Vector settingsVector = new Vector();
    Vector effortSettings = new Vector();
    //create a long array of all settings for fast read/write
    //create structure for accessing and editing them in a structured way (with headings etc)
    //make it possible to access editing a setting from anywhere with a simple constructure

    @Override
    public int getVersion() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class SettingsList {
    }
    

//    private Setting getSetting(int id) {
//        for (int i = 0, size = settings.length; i < size; i++) {
//            if (settings[i].id == id) {
//                return settings[i];
//            }
//        }
//        return null;
//    }
//
//    int getInt(int id) {
//        for (int i = 0, size = settings.length; i < size; i++) {
//            if (settings[i].id == id) {
//                return ((Integer) settings[i].value).intValue();
//            }
//        }
//        return 0;
//    }
    public void aawriteObject(DataOutputStream dos) {
        for (int i = 0, size = settings.length; i < size; i++) {
            for (int j = 0, size2 = GROUP_LAST; j < size2; j++) {
                if (settings[j][i].value instanceof Integer) {
                    try {
//                    dos.writeInt(settings[j][i].id);
                        dos.writeInt(((Integer) settings[j][i].value).intValue());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void aareadObject(int version, DataInputStream dis) {
        for (int i = 0, size = settings.length; i < size; i++) {
            for (int j = 0, size2 = GROUP_LAST; j < size2; j++) {
                if (settings[j][i].value instanceof Integer) {
                    try {
//                    int id = dis.readInt();
//                    Setting setting = getSetting(id);
                        if (settings[j][i].value instanceof Integer) {
                            settings[j][i].value = new Integer(dis.readInt());
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
    //GLOBAL SETTINGS:
    //screenlayout: portrait or landscape // use to e.g. put tabs on the right side of the screen instead of on top
    //private long nextGuid = 1; --now moved to BaseItemDAO
    //private ItemList jotList;
    //</editor-fold>
    public Settings() {
//        super("Settings"); //can *only* be saveDirectly
        super(); //can *only* be saveDirectly
//        setTypeId(BaseItemTypes.SETTINGS);
//        readBaseItem(BaseItemDAO.getInstance().getDIS(RMSFilename));
//        if (INSTANCE == null) {
//            INSTANCE = (Settings) BaseItemDAO.getInstance().getBaseItem(settingsFilename);
//            if (INSTANCE == null) {
//                INSTANCE = new Settings();
//                INSTANCE.commit(settingsFilename);
//            }
//        }
        //setCommitted(true);
        //commit(); //TODO!: check that Settings are not committed anywhere else the first time they are created //-are saved in BaseItemDAO!
    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private static class SingletonHolder {
    //
    ////        private final static Settings INSTANCE = new Settings();
    //        private static Settings INSTANCE; // = new Settings();
    //
    //        SingletonHolder() {
    //            if (INSTANCE == null) {
    //                INSTANCE = (Settings) BaseItemDAO.getInstance().getBaseItem(settingsFilename);
    //                if (INSTANCE == null) {
    //                    INSTANCE = new Settings();
    //                    INSTANCE.commit(settingsFilename);
    //                }
    //            }
    //        }
    //    }
    //    private static final Settings INSTANCE = new Settings();
    //</editor-fold>
    private static Settings INSTANCE; // = new Settings(); //- can't initialize here since we need to load it from DAO

    public Vector getSettingsVector() {
        return settingsVector;
    }
    
    public static Settings getInstance() {
//        return SingletonHolder.INSTANCE;
        if (INSTANCE == null) {
//            INSTANCE = (Settings) BaseItemDAO.getInstance().getBaseItem(settingsLogicalName);
            if (INSTANCE == null) {
                INSTANCE = new Settings();
//                INSTANCE.commit(settingsLogicalName);
            }
        }
        return INSTANCE;
    }

    public void initialize() {
        //nothing to do (yet) - all init handled in getInstance and constructor
    }
//    public int nbSubtasksToChangeStatusWithoutConfirmation() {
//        return nbSubtasksToChangeStatusWithoutConfirmation;
//    }
    /**
     */
    Setting neverChangeProjectsSubtasksWhenChangingProjectStatus = new Setting("When changing a project's status, do not change the status of its subtasks", false, "");
    /**
     */
    Setting changeSubtasksStatusWithoutConfirmation = new Setting("Never ask confirmation when setting the status of a project's subtasks", false, "");
    /**
     * returns the number of subtasks that are OK to set to a new status in
     * 'batch' without asking for confirmation. E.g. if you set a Project to
     * done and thus affect all subtasks. Since it cannot be undone (except
     * chaning each sub-task manually) you want to make sure it's not by
     * mistake.
     */
    Setting nbSubtasksToChangeStatusWithoutConfirmation = new Setting("Number subtasks to change without confirmation", 2, 0, 999, "", settingsVector);
    /**
     * xxxreturns the hour of day used as default for dates. E.g. when creating
     * a new date, this hour of day is used. Is interpreted as local time in
     * current time zone. Eg 8 means 8am in local time zone and dayligth saving
     * zone.
     */
//    public int getNumberDecimalsForEarnedPointsPerHour() {
//        return getNumberDecimalsForEarnedPointsPerHour;
//    }
    Setting getNumberDecimalsForEarnedPointsPerHour = new Setting("Number decimals when calculating " + Item.getFieldName(Item.FIELD_EARNED_POINTS_PER_HOUR) + " ", 2, 0, 2, "", settingsVector);
    /**
     * returns the hour of day used as default for dates. E.g. when creating a
     * new date, this hour of day is used. Is interpreted as local time in
     * current time zone. Eg 8 means 8am in local time zone and dayligth saving
     * zone.
     */
//    public int getDefaultHourOfDay() {
//        return defaultHourOfDay;
//    }
    Setting defaultHourOfDay = new Setting("Default hour of day for dates", 8, 0, 23, "", settingsVector);
    /**
     * returns the minute of day used as default for dates. E.g. when creating a
     * new date, this minute of day is used. Is interpreted as local time in
     * current time zone. Eg 8 means 8am in local time zone and dayligth saving
     * zone.
     */
//    public int getDefaultMinuteOfDay() {
//        return defaultMinuteOfDay;
//    }
    Setting defaultMinuteOfDay = new Setting("Default minutes of day for dates", 0, 0, 59, "", settingsVector);

    /**
     * are soft buttons supported by this device? TODO!!! cleanup
     */
    public boolean isSoftButtonsDevice() {
        return !Display.getInstance().isTouchScreenDevice(); //TODO!!! apparently no direct test of soft buttons?? 
    }

    /**
     * how many affected items should trigger a confirmation dialog? E.g. if
     * marking several items in a Category as Done in one go, or subitems.
     * TODO!!! cleanup
     */
    public boolean isUseNumericKeysForMoveAndOtherShortCuts() {
        return true;
    }
    /**
     * xxxhow many affected items should trigger a confirmation dialog? E.g. if
     * marking several items in a Category as Done in one go, or subitems.
     */
//    public boolean addNewInboxItemsToHeadOfInbox() {
//        return addNewInboxItemsToHeadOfInbox;
//    }
    Setting addNewInboxItemsToHeadOfInbox = new Setting("Where to add tasks to Inbox", true, new String[]{"Top", "Bottom"}, "");

    /**
     * how many affected items should trigger a confirmation dialog? E.g. if
     * marking several items in a Category as Done in one go, or subitems.
     */
    public int numberOfChangedItemsRequiringConfirmation() {
        return numberOfChangedItemsRequiringConfirmation;
    }
    //replaced by nbSubtasksToChangeStatusWithoutConfirmation
    public static final int INSERT_AT_HEAD_OF_LIST = 1;
    public static final int INSERT_AT_END_OF_LIST = 2;
    public static final int INSERT_BEFORE_REFERENCE_ITEM = 3;
    public static final int INSERT_AFTER_REFERENCE_ITEM = 4;
    /**
     * return position where to insert new repeat instances (generated by a
     * RepeatRule) in the 'mother' list. *
     */
//    int getInsertPositionOfNewRepeatInstances() {
//        return insertPositionOfNewRepeatInstances;
//    }
    Setting insertPositionOfNewRepeatInstances = new Setting("Insert new repeat instances", INSERT_BEFORE_REFERENCE_ITEM,
            new String[]{"Before completed", "After completed", "Top", "Bottom"}, new int[]{INSERT_AT_HEAD_OF_LIST, INSERT_AT_END_OF_LIST, INSERT_BEFORE_REFERENCE_ITEM, INSERT_AFTER_REFERENCE_ITEM}, "");
    /**
     * return position where to insert the initially generated (first time the
     * repeat rule is activated) repeat instances (generated by a RepeatRule) in
     * the 'mother' list. *
     */
    //TODO!!!!! replace this setting with 
//    int getInsertPositionOfInitialRepeatInstances() {
//        return insertPositionOfInitialRepeatInstances;
//    }
    Setting insertPositionOfInitialRepeatInstances = new Setting("Insert new repeat instances", INSERT_AT_HEAD_OF_LIST,
            new String[]{"Before repeat task", "After repeat task", "Top", "Bottom"}, new int[]{INSERT_AT_HEAD_OF_LIST, INSERT_AT_END_OF_LIST, INSERT_BEFORE_REFERENCE_ITEM, INSERT_AFTER_REFERENCE_ITEM}, "");

    /**
     * should new repeat instances (generated by a RepeatRule) be added to the
     * end of the 'mother' list (or to the beginning) *
     */
    //-not used
//    boolean getAddRepeatInstancesToEndOfList() {
//        return addRepeatInstancesToEndOfList;
//    }
    int getNextGuidAndIncrease() {
        int guid = nextGuid++;
//        changed();
        return guid;
    }

    int[] groupingDurationIntervalsInMinutes() {
        return groupingDurationIntervalsInMinutes;
    }

    String groupingDurationIntervalUnitString() {
        return groupingDurationIntervalUnitString;
    }

    int groupingUnitInMilliseconds() {
        return groupingUnitInMilliseconds;
    }

    int groupingDefaultGroupDatesByValue() {
        return groupingDefaultGroupDatesByValue;
    }

    /**
     * true if you can select commands in menus using the numeric keys, used
     * with LWUIT setNumericKeyActions()
     */
    boolean allowNumericKeysToSelectMenuCommand() {
        return allowNumericKeysToSelectMenuCommand;
    }

    long getDefaultDaysAddedToDue() {
        return defaultDaysAddedToDue * MyDate.DAY_IN_MILLISECONDS;
    }

    /**
     * default value for how many repeat instances are calculated and stored
     * whenever all previously instances are 'consumed'. Whenever that number of
     * instances have been completed, it will trigger a call to the repeatRule
     * which may give a noticeable delay for the user, especially for long
     * running rules since basically the calculation starts from first date each
     * time. If the value is too high, then for example rules with just a new
     * repeats will take too long to calculate since too many dates are
     * calculated
     */
    int getDefaultNumberOfRepeatInstancesBuffered() {
        return defaultNumberOfRepeatInstancesBuffered;
    }

    /**
     * default value for how many days ahead a user-specified date in a
     * repeatRule starts with. Makes best sense to start from today/0 since
     * that's a very likely case
     */
    int getDefaultRepeatFromSpecifiedDateDaysAhead() {
        return 0;
    }

    /**
     * true if existing instances of a repeating item should be reused whenever
     * the repeatRule has been changed. This will for example ensure that any
     * manual edits are kept
     */
    boolean reuseExistingInstancesAfterChangingRepeatRule() {
        return reuseExistingInstancesAfterChangingRepeatRule;
    }

    /**
     * true if deleting a RepeatRule should always/automatically delete all
     * instances created based on the rule
     */
    boolean alwaysDeleteAllInstancesWhenDeletingARepeatRule() {
        return alwaysDeleteAllInstancesWhenDeletingARepeatRule;
    }

    /**
     * true if WorkSlots getStart and getDurationAdjusted() should be adjusted
     * if startDate is in the past, e.g. to ensure that a slot which is in the
     * past is still counted
     */
    boolean adjustWorkSlotsWhenStartIsInThePast() {
        return adjustWorkSlotsWhenStartIsInThePast;
    }

    /**
     * true if requested calendar imports should NOT show a status dialog
     */
    boolean silentCalendarImport() {
        return silentCalendarImport;
    }

    /**
     * default value for how many days ahead a workSlot import from calendar is
     * set to
     */
    int getDefaultNumberOfDaysAheadCalendarWorkSlotImportMax() {
        return 90;
    }

    /**
     * default value for how many days ahead a workSlot import from calendar is
     * set to
     */
    int getNumberOfScrollLines() {
        return 6;
    }

    /**
     * default value for how many days ahead a workSlot import from calendar is
     * set to
     */
    int getDefaultNumberOfDaysAheadCalendarWorkSlotImport() {
        return defaultNumberOfDaysAheadCalendarWorkSlotImport;
    }

    /**
     * true if a reminder should be set by default whenever marking an Iten as
     * Waiting
     */
    void setSilentCalendarImport(boolean on) {
        waitingReminderByDefault = on;
    }

    /**
     * true if a reminder should be set by default whenever marking an Iten as
     * Waiting
     */
    boolean setWaitingReminderByDefault() {
        return waitingReminderByDefault;
    }

    int getDefaultPriority() {
        return defaultPriority;
    }

    int getDefaultClassAccessibility() {
        return defaultClassAccessibility;
    }

    boolean storePastCalendarItemWorkSlots() {
        return storePastCalendarItemWorkSlots;
    }

    /**
     * returns true if deletes of e.g. Items in lists should NOT be confirmed in
     * a pop up dialog
     */
    public boolean doNotConfirmDeletes() {
        return false;
    }

    public boolean weeksStartOnMondays() {
        return weeksStartOnMondays;
    }

    public boolean useISOWeekNumbering() {
        return iSOWeekNumbering;
    }

    /**
     * returns the maximum allowed number of future instances that can be
     * generated at a time. Needed to avoid generating e.g. thousands or milions
     * of instances by error. Currently set to 14 since that covers a daily
     * repetition over the next two weeks. TODO!!: Could be made user-definable,
     * or pop-up a a confirmation window if higher number is generated.
     *
     * @return
     */
    public int getMaxFutureRepeatInstances() {
        return 14;
    }

    /**
     * max number of days ahead, 14 = 2 weeks, 31 = 1 month
     */
    public int getMaxRepeatInstancesDaysAhead() {
        return 31;
    }

    public int getMaxRepeatCount() {
        return 999; //TODO!!!: any reason to limit the number of repeats a user can define??
    }

    /**
     * default number of repeat instances proposed when a user creates a new
     * repeat definition
     *
     * @return
     */
    public int getDefaultNumberOfRepeatInstances() {
        return 1;
    }

    /**
     * returns the current locale, e.g. "en" for English. Using this setting
     * allows the user to 'force' another locale than the one currently set for
     * the phone. It should be initialized automatically to the phone's locale
     * at first start-up.
     *
     * @return
     */
    public String getLocale() {
        if (locale == null || locale.equals("")) {
            locale = System.getProperty("microedition.locale");
        }
        return "en"; //TODO!!!!: only during testing/development - change to return actual locale
    }
    final static int LOCALE_US = 0;
    final static int LOCALE_DK = 1;
    final static int LOCALE_OTHER = 99;

    public int getLocaleAsInt() {
        String locale = getLocale();
        if (locale.equals("us")) {
            return LOCALE_US;
        } else if (locale.equals("dk")) {
            return LOCALE_DK;
        } else {
            return LOCALE_OTHER;
        }
    }

    /**
     * TODO!!!!!
     *
     * @return
     */
    public String getLanguage() {
        if (language == null || language.equals("")) {
            language = System.getProperty("microedition.locale");
        }
        return "en"; //TODO!!!!: only during testing/development - change to return actual locale
    }

    /**
     * returns the amount that Remaining effort should be increased with.
     * Stepwise so that
     *
     * @param currentEffort current Remaining effort
     * @return
     */
    int getRemainingIncrease() {
        return MyDate.MINUTE_IN_MILLISECONDS * 10; //default 10 minutes
    }

    int getRemainingIncrease(int currentEffort) {
        if (currentEffort < remainingIncreaseLimit1) {
            return remainingIncreaseBelowLimit1;
        } else if (currentEffort < remainingIncreaseLimit2) {
            return remainingIncreaseAboveLimit1;
        } else {
            return remainingIncreaseAboveLimit2;
        }
    }

    /**
     * Should 'reverse' the previous increase step, so that calling Decrease
     * right after Increase sets the value back to the same.
     *
     * estimate back to its previous value.
     *
     * @param currentEffort
     * @return
     */
    int getRemainingDecrease(int currentEffort) {
        if (currentEffort > remainingIncreaseLimit2) {
            return remainingIncreaseAboveLimit2;
        } else if (currentEffort > remainingIncreaseLimit1) {
            return remainingIncreaseAboveLimit1;
        } else {
            return remainingIncreaseBelowLimit1;
        }
    }

    /**
     * returns the smallest accepted value for Priority
     *
     * @return
     */
    int getPriorityValueMin() {
        return priorityValueMin;
    }

    /**
     * returns the highest accepted value for Priority
     *
     * @return
     */
    int getPriorityValueMax() {
        return priorityValueMin;
    }

    String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    String getDefaultDateAndTimeFormat() {
        return defaultDateAndTimeFormat;
    }

    String getDefaultDurationFormat() {
        return defaultDurationFormat;
    }

    /**
     * should date be added automatically to Waiting comments? Default true.
     *
     * @return
     */
    boolean addDatesToWaitingComments() {
        return addDatesToWaitingComments;
    }

    /**
     * the text that is automatically added to Waiting comments in the Notes
     * field
     *
     * @return
     */
    String waitingCommentsStandardText() {
        return waitingCommentsStandardText;
    }

    /**
     * returns default snooze time
     *
     * @return
     */
    int getDefaultSnoozeTime() {
        return defaultSnoozeTime;
    }

    /**
     * returns default snooze time proposed when user chooses to set a Special
     * snooze time
     *
     * @return
     */
    int getDefaultSnoozeTimeSpecial() {
        return defaultSnoozeTimeSpecial;
    }

    /**
     * the default delay used when marking a task waiting (in milliseconds int
     * => 1193hours = 49days)
     *
     * @return
     */
    long getDefaultWaitingTime() {
        return defaultWaitingTime; //TODO!!!!
    }

    int getTimerReminderVibrateRepeats() {
        return timerReminderVibrateRepeats;
    }

    int getTimerReminderFlashRepeats() {
        return timerReminderFlashRepeats;
    }

    int getTimerReminderFlashPause() {
        return timerReminderFlashPause;
    }

    int getTimerReminderVibraterPause() {
        return timerReminderVibraterPause;
    }

    /**
     * length of vibrator to indicate that the timer is running
     *
     * @return
     */
    int getTimerReminderVibraterDuration() {
        return timerReminderVibraterDuration;
    }

    /**
     * length of flashing the phone notifdication light to indicate that the
     * timer is running
     *
     * @return
     */
    int getTimerReminderFlashDuration() {
        return timerReminderFlashDuration;
    }

    /**
     * interval between each reminder buzz while timer is active
     *
     * @return
     */
    int getTimerReminderInterval() {
        return timerReminderInterval;
    }

    /**
     * interval between each refresh of the timer time (currently once every
     * minute, since we don't want to stress the user with showing seconds
     *
     * @return
     */
    int getTimerScreenRefreshInterval() {
//        return timerScreenRefreshInterval;
        return MyDate.SECOND_IN_MILLISECONDS * 2; //TODO!!!!: replace (only for testing)
    }

    /**
     * should the Timer for an Item display the (cumulative) time already worked
     * on the item, or only the time spend in the current session (the active
     * Dialog window)
     *
     * @return
     */
    boolean continueTimerWithPreviousActual() {
        return continueTimerWithPreviousActual;
    }

    int getAlarmSoundRepeats() {
        return alarmSoundRepeats;
    }

    int getAlarmSoundDuration() {
        return alarmSoundDuration;
    }

    int getAlarmSoundPause() {
        return alarmSoundPause;
    }

    /**
     * returns the limit for a Timer on an item above (or equal) which a warning
     * should be shown to the user (to oimit the risk of recording too high
     * times if the timer was forgotten).
     *
     * @return
     */
    boolean forcePriorityValuesIntoImpUrgencyMatrixValues() {
        return forcePriorityValuesIntoImpUrgencyMatrixValues;
    }

    /**
     * returns the limit for a Timer on an item above (or equal) which a warning
     * should be shown to the user (to oimit the risk of recording too high
     * times if the timer was forgotten).
     *
     * @return
     */
    int getItemTimerWarningLimit() {
        return itemTimerWarningLimit;
    }

    /**
     * returns the default time estimate to use for tasks that have not been
     * estimated (used eg to calculate earned points per hour)
     *
     * @return
     */
    int getDefaultEffortForUnestimatedTasks() {
        return MyDate.MINUTE_IN_MILLISECONDS * 15;
    }

    /**
     * should alarms for instances of Repeating tasks be adjusted to same
     * interval between Due Date and Alarm Date as the source of the repeating
     * events?
     *
     * @return
     */
    boolean adjustRecurringInstanceAlarmToSameIntervalAsSource() {
        return true;
    }

    /**
     * how many instances are allowed to be created for recurring tasks (to
     * avoid eg
     *
     * @return
     */
    int getMaxRecurringTaskInstances() {
        return 50; //TODO!!!: make this a user setting
    }

    /**
     * force all keybaord shortguts to upper case, e.g. both 'a' and 'A' will
     * trigger same command. This is useful because showing shortcut hints in
     * menus as "[A]" is more understandable than "[a]". Can be turned of if
     * more shortcuts are needed - basically upper case shortcuts (eg shift-A)
     * could be used for rarely used shortcuts, or for 'power' versions of
     * commands (e.g. shift-X applies to all selected, whereas x applies only to
     * currently selected)
     *
     * @return
     */
    boolean forceKeyboardShortCutsToUpperCase() {
        return false;
    }
    final static int SHORTCUT_NO_CHANGE = 0;
    final static int SHORTCUT_UPPERCASE = 1;
    final static int SHORTCUT_LOWERCASE = 2;

    int forceKeyboardShortCuts() {
        return SHORTCUT_NO_CHANGE;
    }

    /**
     * show alphabetic shortcuts in menus even on non-qwerty devices
     *
     * @return
     */
    boolean forceShowQwertyShortCutsOnNonQwertyDeviceCase() {
        return false;
    }

    /**
     * true if alphanumeric shortcut hints should be added to commands in menus
     * (used in MyCommand.getShortcutStr())
     *
     * @return
     */
    boolean addNonITUKeyboardShortCutHintsToCommands() {
//    boolean addQwertyShortCutHintsToCommands() {
        return isQwertyDevice() || forceShowQwertyShortCutsOnNonQwertyDeviceCase();
    }

    /**
     * returns true if this device should be treated as a qwerty device even if
     * Display.getInstance().getKeyboardType() says it's not qerty.
     *
     * @return
     */
    boolean forceQwertyMode() {
        return forceQwertyMode;
    }

    /**
     * show alphabetic shortcuts in menus even on non-qwerty devices
     *
     * @return
     */
    boolean isQwertyDevice() {
        int qwerty = Display.getInstance().getKeyboardType();
        return qwerty == Display.KEYBOARD_TYPE_QWERTY || qwerty == Display.KEYBOARD_TYPE_HALF_QWERTY || forceQwertyMode();
    }

    /**
     * should dots "..." be added to the text of SubMenus
     *
     * @return
     */
    boolean addDotsToSubMenus() {
        return true;
    }

    /**
     * should Groups be collapsed by default
     *
     * @return
     */
    boolean autoCollapseGroups() {
        return true;
    }

    /**
     * when exporting items to the PIM, should categories be case sensitive,
     * that is, category Work is different from category WORK
     *
     * @return
     */
    boolean categoryExportCaseSensitive() {
        return isCategoryExportCaseSensitive;
    }

    /**
     * when importing items from the PIM, should categories be case sensitive,
     * that is, category Work is different from category WORK. If true, then
     * different categories will be created for Work and WORK
     *
     * @return
     */
    boolean categoryImportCaseSensitive() {
        return isCategoryImportCaseSensitive;
    }

    /**
     * returns true if when an Item has added a Category C, then T is inserted
     * into the bginning (head) of C's list of Items.
     *
     * @return
     */
    boolean addItemsToBeginningOfCategories() {
        return addItemsToBeginningOfCategories;
    }

    /**
     * returns true if when an Item has added a an 'invisible' subList (eg if
     * all items of that sublist are filtered)
     *
     * @return
     */
    boolean addItemsToBeginningOfInvisbleSubLists() {
        return addItemsToBeginningOfInvisbleSubLists;
    }

    /**
     * returns true if when an Item has added a an 'invisible' subList (eg if
     * all items of that sublist are filtered)
     *
     * @return
     */
    boolean addItemsToBeginningOfSubLists() {
        return addItemsToBeginningOfSubLists;
    }

    /**
     * if true, then update Item status to Ongoing first time that actual effort
     * is recorded
     *
     * @return
     */
    boolean setStatusOngoingWhenActualEffortSetFirstTime() {
        return setStatusOngoingWhenActualEffortSetFirstTime;
    }

    /**
     * if true, then update Item status to Ongoing first time that actual effort
     * is recorded
     *
     * @return
     */
    boolean setStatusToCreatedIfActualReducedToZero() {
        return setStatusToCreatedIfActualReducedToZero;
    }

    /**
     * indicates that when adding 'leaf' subtasks in Focus (Here&Now) view, then
     * the preceeding levels of mother-tasks should be added before the leafs.
     * This ensures that the context in which the leaf sub-tasks appears is
     * clearly visible. Otherwise a 'free-hanging' leaf sub-tasks may be
     * difficult to understand if shown outside the context of its project.
     */
    boolean addMothersInGetLeafs() {
        return addMothersInGetLeafs;
    }
    private boolean addNewWorkStreamItemsToBeginningOfList = true;

    /**
     * /** should items be added to beginning of a WorkStream's list of items
     * when adding a new source (or if false, to end of list)
     */
    boolean addNewWorkStreamItemsToBeginningOfList() {
        return addNewWorkStreamItemsToBeginningOfList;
    }

    /**
     * should RM records be stored in the same format version they were read in
     * (to maintain backwards compatibility)? Avoid using this: default is to
     * read date in the same format it was stored, but always store in new
     * format.
     *
     * @return
     */
//    byte getStorageFormatVersion() {
//        return storageFormatVersion;
//    }
    public int getCharsInShortMonths() {
        return charsInShortMonths;
    }

    public int getCharsInShortDates() {
        return charsInShortDates;
    }

    public int getDefaultListIndentInPixels() {
        return defaultListIndentInPixels;
    }

    public boolean xisAutoUpdateRemainingWhenActualReduced() {
        return xautoUpdateRemainingWhenActualReduced;
    }

    public boolean markDoneIfCompletedDateSet() {
        return markDoneIfCompletedDateSet;
    }

    public boolean xisAskToUpdateActualWhenRemainingReduced() {
        return xaskToUpdateActual;
    }

    public boolean askToUpdateActualWhenDone() {
        return askToUpdateActual;
    }

    public boolean askToUpdateRemainingWhenActualIncreased() {
        return askToUpdateRemaining;
    }

    /**
     *
     */
    public boolean alwaysUpdateRemainingToEffortMinusActualWhenEffortIsUpdated() {
        return alwaysUseRemainingAsEstimateWhenActualIsZero;
    }

    /**
     * Text: "Save first value entered for Remaining effort as Estimate?"
     */
    public boolean alwaysUseRemainingAsEstimateWhenActualIsZero() {
        return alwaysUseRemainingAsEstimateWhenActualIsZero;
    }

    /**
     * Text: "Save first value entered for Remaining effort as Estimate?"
     */
    public boolean alwaysSetFirstEstimateToInitialEstimate() {
        return alwaysSetEstimateToFirstRemaining;
    }

    public boolean markDoneIfRemainingReducedToZero() {
        return markDoneIfRemainingReducedToZero;
    }
    /**
     * @return the NextGuid
     */
    /*public //filename for saved items
     long getNextGuid() {
     //Long guid = nextGuid++
     return nextGuid++;
     }*/
    /**
     * interval added to current time to get the default due date, e.g. could be
     * 7 or 14 days
     */
    private long getDefaultDueInterval = 0;

    public long getDefaultDueInterval() {
        return getDefaultDueInterval;
    }
//    new Setting(getDefaultDueInterval, 0, "interval added to current time to get the default due date, e.g. could be 7 or 14 days");

//    public String getFilename() {
//        return filename;
//    }
    public String getThemesFilename() {
//        return themesFilename;
//        return "/theme.res";
//        return "/themeAndGUI.res";
//        return "/themeAndGUI.res";
        return "/TodoCatProto.res";
    }
    private int saveFormatVersion; //version of the save format

    public int getSaveFormatVersion() {
        return saveFormatVersion;
    }

    public void setSaveFormatVersion(int saveFormatVersion) {
        this.saveFormatVersion = saveFormatVersion;
//        changed();
    }

//    public boolean isPortraitLayout() {
//        return screenOrientation == SCREEN_LAYOUT_PORTRAIT;
//    }
    public int getScreenOrientation() {
//        return screenOrientation;
        return SCREEN_LAYOUT_PORTRAIT;
//        if ((Display.getInstance().getDisplayHeight() > Display.getInstance().getDisplayWidth())) {
//            return SCREEN_LAYOUT_PORTRAIT;
//        } else {
//            return SCREEN_LAYOUT_LANDSCAPE;
//        }
    }

    String xgetFileNameMyLists() {
        return "MyLists";
    }
    static String getFileNameMyLists = "MyLists";

    String xgetFileNameMyProjects() {
        return "Projects";
    }
    static String getFileNameMyProjects = "Projects";

    String getFileNameTemplates() {
        return "Templates";
    }
    static String getFileNameTemplates = "Templates";
    static String getFileNameMergeLists = "Mergelists";

//    String getFileNameOwnerless() {
//        return "Ownerless";
//    }
//    static String getFileNameOwnerless = "Ownerless";
    public void externalize(DataOutputStream dos) throws IOException {
        //dos =
//        super.writeObject(dos);
        try {
            //WRITE DATA FIELDS - ALWAYS add new data at the end of the list!!
            dos.writeInt(saveFormatVersion);
//            dos.writeUTF(filename);
            //dos.writeLong(nextGuid);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //return dos;
    }

    public void internalize(int version, DataInputStream dis) throws IOException {
//        super.readObject(999, dis);
        try {
            //TODO: if (settings.version == xxx)
            saveFormatVersion = dis.readInt();
//            filename = dis.readUTF();
            //nextGuid = dis.readLong();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //return dis;
    }
}
