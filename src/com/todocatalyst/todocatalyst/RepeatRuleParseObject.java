/*
 * an efficient way to store a RepeatRule and methods to translate back and forth between this format, the
 * RepeatRule fields, and the values used in drop-down menus.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Log;
import com.codename1.io.Util;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseException;
//import com.codename1.ui.List;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.Item.COPY_EXCLUDE_DUE_DATE;
//import static com.todocatalyst.todocatalyst.Item.PARSE_DELETED_DATE;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
//import javax.microedition.pim.RepeatRule;

/**
 * RepeatRules: - Are stored separately and referenced from each not-Done
 * instance (so the rule can be edited from that instance). - When an item with
 * a (new) repeat rule is saved, the repeat instances should be created. - To
 * support calculation of available time, many future instances can be generated
 * at once. This can be limited in time (eg one month ahead) or in number of
 * instances generated (eg 4 instances generated). - Keeps a list of all
 * generated items (to be able to update/delete them if the rule is changed). -
 * Reference to a rule is removed when an item is Done. - When rule is edited,
 * all already generated items not yet Done are replaced by the correct items
 * according to the new rule. - When a repeatItem is Done, the repeatRule is
 * called to generate more instances. - New generated repeat items are inserted
 * into the same owner list as the original item (in a list, or a subtask list).
 * - Repeat rules must not be activated for templates (e.g. when creating one),
 * but should be activated once a template is instantiated.
 *
 * @author Thomas
 */
public class RepeatRuleParseObject
        extends ParseObject /*extends BaseItem*/
        //        implements Externalizable, MyCommandEditScreen.ButtonEditInterface {
        implements Externalizable {
    //TODO!!!!
    //TODO!!!! when editing an existing rule, should it be treated as a new one or a continuation of the existing rule?? Eg. #instances is reset to 0, dueDate taken from edited instance, ...? Most likely case is that you edit a rule because it wasn't quite right
    //TODO!!!! for yearly repeat, months, you can only see about 6 of the the 12 months in the Group. Wrap?
    //TODO!!!! for weekly repeat, months, you can not see all the week days
    //TODO when generating yearly, enable 'last' day of the year for leap years

    private boolean datePatternChanged = false; //true if the rule was changed in a way to affect the date sequence, eg repeat on different weekdays or monthly instead of weekly

    public static String CLASS_NAME = "RepeatRule";
    public static String REPEAT_RULE = "Repeat rule";

    final static int REPEAT_TYPE_NO_REPEAT = 0; //11;
    final static int REPEAT_TYPE_FROM_COMPLETED_DATE = 1; //11;
    final static int REPEAT_TYPE_FROM_DUE_DATE = 2;// 33;
//    final static int REPEAT_TYPE_FROM_SPECIFIED_DATE = 3; //7; //TODO: implement as additional option?

    final static String PARSE_UPDATED_AT = Item.PARSE_UPDATED_AT;
    final static String PARSE_SPECIFIED_START_DATE_XXX = "specifiedStartDate";
    final static String PARSE_REPEAT_TYPE = "repeatType";
    final static String PARSE_FREQUENCY = "frequency";
    final static String PARSE_INTERVAL = "interval";

    final static String PARSE_END_DATE = "endDate";
    final static String PARSE_COUNT = "count";

    final static String PARSE_DAYS_IN_WEEK = "daysInWeek";
    final static String PARSE_WEEKS_IN_MONTH = "weeksInMonth";
    final static String PARSE_WEEKDAYS_IN_MONTH = "weekdaysInMonth";

    final static String PARSE_MONTHS_IN_YEAR = "monthsInYear";
    final static String PARSE_DAY_IN_MONTH = "dayInMonth";
    final static String PARSE_DAY_IN_YEAR = "dayInYear";

    final static String PARSE_NUMBER_SIMULTANEOUS_REPEATS_TO_GENERATE_AHEAD = "numberFutureRepeatsToGenerateAhead";
    final static String PARSE_NUMBER_OF_DAYS_TO_GENERATE_AHEAD = "numberOfDaysRepeatsAreGeneratedAhead";

//    private final static String PARSE_REPEAT_INSTANCE_ITEMLIST = "repeatInstanceItemList";
    final static String PARSE_UNDONE_INSTANCES = "undoneInstances";
    final static String PARSE_DONE_INSTANCES = "doneInstances";
//    private final static String PARSE_DATES_LIST = "datesList";
//    final static String LAST_DATE_GENERATED_FOR = "lastDateGeneratedFor";
//    private final static String PARSE_LAST_DATE_GENERATED = "lastGeneratedDate";
//    private final static String PARSE_NEXTCOMING_REPEAT_DATE = "nextcomingDate"; //the nextcoming date for which a new repeat instance, also used to check if a RepeatRule need to generate new repeat instances
    final static String PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR_XXX = "countOfInstancesGeneratedSoFar";
    final static String PARSE_COUNT_OF_INSTANCES_DONE_SO_FAR_XXX = "countOfDoneInstancesSoFar";
    final static String PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED_XXX = "dateLastCompleted";

    final static String PARSE_DATE_ON_COMPLETION_REPEATS = "onCompletionDated"; //true if onCompletion repeats should be dated (and thus have a repeatRule pattern assigned)
    final static String PARSE_DELETED_DATE = Item.PARSE_DELETED_DATE; //true if onCompletion repeats should be dated (and thus have a repeatRule pattern assigned)
//    final static String PARSE_DELETED_DATE = "deletedDate"; //has this object been deleted on some device?

    /**
     * list of generated dates for the repeat rule
     */
//    private DateBuffer datesList = new DateBuffer(); // = 1; //
//    private List datesList = new ArrayList<Date>(); // = 1; //
    /**
     * number of dates to generate ahead in each 'batch' of new dates
     */
    final static int COUNTS_AHEAD = 4;//Settings.getInstance().getDefaultNumberOfRepeatInstancesBuffered();
//    final static long MAX_DATE = 253370764800000L; //Human time (GMT): Fri, 01 Jan 9999 00:00:00 GMT, using http://www.epochconverter.com/. NEeded to avoid overflow of json dates
//    final static long MIN_DATE = -93692592000000L; //Human time (GMT): Thu, 01 Jan -999 00:00:00 GMT, using http://www.epochconverter.com/. NEeded to avoid overflow of json dates

    /**
     * a calculation of new repeat rule instances is already on-going. necessary
     * to avoid that when inserting just generated new instances, then new
     * calculations are triggered for them.
     */
//    boolean repeatRuleNewInstancesCalculationOngoing;
    private final static String FIRST = "First";
    private final static String FIRST_SHORT = "1st";
    private final static String SECOND = "Second";
    private final static String SECOND_SHORT = "2nd";
    private final static String THIRD = "Third";
    private final static String THIRD_SHORT = "3rd";
    private final static String FOURTH = "Fourth";
    private final static String FOURTH_SHORT = "4th";
    private final static String FIFTH = "Fifth";
    private final static String FIFTH_SHORT = "5th";
    private final static String LAST = "Last";
    private final static String LAST_SHORT = "Last";
    private final static String SECOND_LAST = "Second last";
    private final static String THIRD_LAST = "Third last";
    private final static String FOURTH_LAST = "Fourth last";
    private final static String FIFTH_LAST = "Fifth last";
    private final static String SECOND_LAST_SHORT = "2nd last";
    private final static String THIRD_LAST_SHORT = "3rd last";
    private final static String FOURTH_LAST_SHORT = "4th last";
    private final static String FIFTH_LAST_SHORT = "5th last";
    private final static Object[] WEEKS_IN_MONTH_NUMBERS = {RepeatRule.FIRST, RepeatRule.SECOND, RepeatRule.THIRD, RepeatRule.FOURTH, RepeatRule.FIFTH,
        RepeatRule.LAST, RepeatRule.SECONDLAST, RepeatRule.THIRDLAST, RepeatRule.FOURTHLAST, RepeatRule.FIFTHLAST};
    private final static Object[] WEEKS_IN_MONTH_NUMBERS_SHORT = {RepeatRule.FIRST, RepeatRule.SECOND, RepeatRule.THIRD, RepeatRule.FOURTH, RepeatRule.FIFTH, RepeatRule.LAST};
//    private final static String[] WEEKS_IN_MONTH_NAMES = {FIRST, SECOND, THIRD, FOURTH, FIFTH, LAST, SECOND_LAST, THIRD_LAST, FOURTH_LAST, FIFTH_LAST};
    private final static String[] WEEKS_IN_MONTH_NAMES = {FIRST_SHORT, SECOND_SHORT, THIRD_SHORT, FOURTH_SHORT, FIFTH_SHORT, LAST_SHORT, SECOND_LAST_SHORT, THIRD_LAST_SHORT, FOURTH_LAST_SHORT, FIFTH_LAST_SHORT};
    private final static String[] WEEKS_IN_MONTH_NAMES_SHORT = {FIRST_SHORT, SECOND_SHORT, THIRD_SHORT, FOURTH_SHORT, FIFTH_SHORT, LAST_SHORT};
    //NB! below DAY_IN_WEEK_NUMBERS list requires that MyDate sequence of weekdays is the same!!
    private final static Object[] DAY_IN_WEEK_NUMBERS_MONDAY_FIRST = {RepeatRule.MONDAY, RepeatRule.TUESDAY, RepeatRule.WEDNESDAY, RepeatRule.THURSDAY, RepeatRule.FRIDAY, RepeatRule.SATURDAY, RepeatRule.SUNDAY};
    private final static Object[] DAY_IN_WEEK_NUMBERS_MONDAY_FIRST_INCL_WEEKDAYS = {RepeatRule.MONDAY, RepeatRule.TUESDAY, RepeatRule.WEDNESDAY,
        RepeatRule.THURSDAY, RepeatRule.FRIDAY, RepeatRule.SATURDAY, RepeatRule.SUNDAY, RepeatRule.WEEKDAYS, RepeatRule.WEEKENDS};
    //NB! below DAY_IN_WEEK_NUMBERS list requires that MyDate sequence of months is the same!!
    private final static Object[] MONTH_IN_YEAR_NUMBERS = {RepeatRule.JANUARY, RepeatRule.FEBRUARY, RepeatRule.MARCH, RepeatRule.APRIL, RepeatRule.MAY, RepeatRule.JUNE, RepeatRule.JULY, RepeatRule.AUGUST, RepeatRule.SEPTEMBER, RepeatRule.OCTOBER, RepeatRule.NOVEMBER, RepeatRule.DECEMBER};
    private final static int[] REPEAT_RULE_FREQUENCY_NUMBERS = {RepeatRule.DAILY, RepeatRule.WEEKLY, RepeatRule.MONTHLY, RepeatRule.YEARLY};
    private final static Object[] REPEAT_RULE_FREQUENCY_NUMBERS_AS_OBJECTS = {RepeatRule.DAILY, RepeatRule.WEEKLY, RepeatRule.MONTHLY, RepeatRule.YEARLY};
//    private final static String[] REPEAT_RULE_FREQUENCY_NAMES = {"on a daily basis", "on a weekly basis", "on a monthly basis", "on a yearly basis"};
    private final static String[] REPEAT_RULE_FREQUENCY_NAMES = {"Daily", "Weekly", "Monthly", "Yearly"};
//    private final static int[] REPEAT_RULE_TYPE_NUMBERS = {MyRepeatRule.REPEAT_TYPE_FROM_COMPLETED_DATE, MyRepeatRule.REPEAT_TYPE_FROM_DUE_DATE, MyRepeatRule.REPEAT_TYPE_FROM_SPECIFIED_DATE};
//    private final static String[] REPEAT_RULE_TYPE_NAMES = {"completion date", "due date", "today"};
    private final static int[] REPEAT_RULE_TYPE_NUMBERS = {RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT, RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE, RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE};
//    private final static Object[] REPEAT_RULE_TYPE_NUMBERS_AS_OBJECTS = {RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT, RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE, RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE};
    private final static Object[] REPEAT_RULE_TYPE_NUMBERS_AS_OBJECTS = {RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT, RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE, RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE};
//    private final static String[] REPEAT_RULE_TYPE_NAMES = {"completion date", "due date"}; 
//    private final static String[] REPEAT_RULE_TYPE_NAMES = {"completion", "due"}; //TODO!!: add repeat from TODAY, e.g. I create a new task today and it should repeat every two weeks from now (<=> DueDate==today??)

    final static String REPEAT_RULE_NO_REPEAT = "Never"; //"None";
    final static String REPEAT_RULE_COMPLETED = "On completion";
    final static String REPEAT_RULE_DUE_DATES = "From due date";
    final static String REPEAT_RULE_WORKSLOT = "On expiry";
    private final static String[] REPEAT_RULE_TYPE_NAMES = {REPEAT_RULE_NO_REPEAT, REPEAT_RULE_DUE_DATES, REPEAT_RULE_COMPLETED}; //TODO!!: add repeat from TODAY, e.g. I create a new task today and it should repeat every two weeks from now (<=> DueDate==today??)

    final static String REPEAT_RULE_TYPE = "When";//"Repeat when";
    final static String REPEAT_RULE_TYPE_HELP = Format.f("Seelct when should happen: \"{0}\" no repeat, \"{1}\" repeat on defined date intervals, \"{2}\" repeat after the date when task is completed",
            RepeatRuleParseObject.REPEAT_RULE_NO_REPEAT, RepeatRuleParseObject.REPEAT_RULE_DUE_DATES, RepeatRuleParseObject.REPEAT_RULE_COMPLETED);
    final static String REPEAT_RULE_INTERVAL = "Frequency";//"Repeat when";
    final static String REPEAT_RULE_INTERVAL_HELP = "Repeat with an interval based on days/weeks/months/years";
    final static String REPEAT_RULE_DATED_COMPLETION = "Repeat on specific dates after the date when task is completed";
    final static String REPEAT_RULE_DATED_COMPLETION_HELP = "Repeat on the first specified date after the day when task is completed";
    final static String REPEAT_RULE_FREQUENCY = "Every";
    final static String REPEAT_RULE_FREQUENCY_HELP = "How often to repeat, for example every 2 months";
    final static String REPEAT_RULE_DAYS_IN_WEEK = "On";
    final static String REPEAT_RULE_DAYS_IN_WEEK_HELP = "On which days in the week";
    final static String REPEAT_RULE_DAY_IN_YEAR = "Number day in year";
    final static String REPEAT_RULE_DAY_IN_YEAR_HELP = "On which day between 1 and 365 in the year";
    final static String REPEAT_RULE_JAN_DEC = "Months in year";
    final static String REPEAT_RULE_JAN_DEC_HELP = "On which months in the year";
    final static String REPEAT_RULE_MON_SUN = "Days in week";
    final static String REPEAT_RULE_MON_SUN_HELP = "On which days in the week";
    final static String REPEAT_RULE_DAY_IN_MONTH = "Day in month";
    final static String REPEAT_RULE_DAY_IN_MONTH_HELP = "On which day in the month";
    final static String REPEAT_RULE_WEEKS_IN_MONTH = "Weeks in month";
    final static String REPEAT_RULE_WEEKS_IN_MONTH_HELP = "In which full week in the month should the chosen day occur. "
            + "Be aware that the first and last weeks of a month typically do not contain all week days, "
            + "so if yu chose 1st week, Monday, that will happen rarely, whereas 1st week, Saturday will happen more often.";
    final static String REPEAT_RULE_WEEKDAYS_IN_MONTH = "Week days in month";
    final static String REPEAT_RULE_WEEKDAYS_IN_MONTH_HELP = "On which week day in the month";
    final static String REPEAT_RULE_MONTHLY_TYPE = "Repeat each month on";
    final static String REPEAT_RULE_MONTHLY_TYPE_HELP = "When to repeat in a month";
    final static String REPEAT_RULE_YEARLY_TYPE = "Repeat yearly";
    final static String REPEAT_RULE_YEARLY_TYPE_HELP = "Repeat yearly based on day in year or based on months";
    final static String REPEAT_RULE_UNTIL = "Repeat how long";
    final static String REPEAT_RULE_UNTIL_HELP = "How long should the task repeat"; //"When to stop repeating";
    final static String REPEAT_RULE_UNTIL_DATE = "Repeat until";
    final static String REPEAT_RULE_UNTIL_DATE_HELP = "Repeat will stop after this date";
    final static String REPEAT_RULE_UNTIL_TIMES = "Repeat this many times";
    final static String REPEAT_RULE_UNTIL_TIMES_HELP = "Stop after this many repeats"; //"Repeat will stop after this many repeats";
    final static String REPEAT_RULE_NUMBER_REPEATS = "Create several future repeats"; //"Create multiple future repeats";
    final static String REPEAT_RULE_NUMBER_REPEATS_HELP = "Create more than one future repeats";
    final static String FOREVER = "Forever";
    final static String UNTIL = "Until"; //"Until date";
    final static String COUNT = "Times";

    public RepeatRuleParseObject() {
        super(CLASS_NAME);
        //TODO!!!! revisit default values
//<editor-fold defaultstate="collapsed" desc="comment">
        if (false) { //setting these values falsely marks a new RR as dirty
//            setRepeatType(REPEAT_TYPE_FROM_COMPLETED_DATE);
//            setRepeatType(REPEAT_TYPE_NO_REPEAT);
//            setFrequency(RepeatRule.DAILY); //default: daily repeat
//            setInterval(1); //default: every day
//            setNumberOfRepeats(Integer.MAX_VALUE); //= 0; //1; //-start with 0 (undefined => starts with Repeat forever)
////        setEndDate(Long.MAX_VALUE);
//            setEndDate(MyDate.MAX_DATE);
//            setDaysInWeek(0);
//            setWeeksInMonth(0);
//            setWeekdaysInMonth(0);
//            setMonthsInYear(0);
//            setDayInMonth(0);
//            setDayInYear(0); //TODO: choosing the middle day of the year, eg365/2=182 would make it faster to choose right date in a scrolling list, but is less intuitive
//            setNumberSimultaneousRepeats(0);
//            setNumberOfDaysRepeatsAreGeneratedAhead(0);
//            setSpecifiedStartDate(new MyDate()); //default start date today/now
////        setLastDateGeneratedFor(Long.MIN_VALUE); //=0; //
////        setLastGeneratedDate(Long.MIN_VALUE); //=0; //
//            setLastGeneratedDate(new Date(MyDate.MIN_DATE)); //=0; //
//            setListOfUndoneRepeatInstances(new ArrayList()); // = 1; //
////            setDatesListXXX(new ArrayList()); // = 1; //
        }
//</editor-fold>
    }

    public RepeatRuleParseObject(RepeatRuleParseObject repeatRule) {
        this(repeatRule, false);
    }

    public RepeatRuleParseObject(RepeatRuleParseObject repeatRule, boolean copyListsOfRepeatInstances) {
        this();
        if (repeatRule != null) {
            repeatRule.copyMeInto(this, copyListsOfRepeatInstances);
        }
    }

    /**
     * keep a list of elements modified that need to saved and which are not,
     * cannot, be found via the normal DAO save, e.g. previous owners of deleted
     * repeat instances.
     */
    Collection<ParseObject> needsSaving;

    private void addToNeedsSaving(Collection<ParseObject> modified) {
        if (needsSaving == null) {
            needsSaving = new HashSet();
        }
        needsSaving.addAll(modified);
    }

    Collection<ParseObject> getNeedsSaving() {
        return needsSaving;
    }

    private void clearNeedsSaving() {
        needsSaving = null;
    }

    /**
     * sets all the fields of MyRepeatRule to the values stored in RepeatRule
     * (should never be used as we will always edit a RepeatRule indirectly via
     * MyRepeatRule??!!
     */
    private RepeatRule getRepeatRule() {
        RepeatRule repeatRule = new RepeatRule();
        repeatRule.setInt(RepeatRule.FREQUENCY, getFrequency());
        //TODO!!!: not necessary to set below values since they're also set when calculating dates
//        if (useCount()) {
        if (getNumberOfRepeats() != Integer.MAX_VALUE) {
//            repeatRule.setInt(RepeatRule.COUNT, getCount()-getCountOfInstancesGeneratedSoFar()); //-getCountOfInstancesGeneratedSoFar() since we call this repeatedly but still want to limit the overall amount generated
            repeatRule.setInt(RepeatRule.COUNT, getNumberOfRepeats());
//        } else if (getEndDate() != Long.MAX_VALUE) {
        } else if (getEndDate() != MyDate.MAX_DATE) {
//            repeatRule.setDate(RepeatRule.END, getEndDate());
            repeatRule.setDate(RepeatRule.END, MyDate.getEndOfDay(getEndDateD()).getTime()); //set to end of day to not miss 
//            repeatRule.setInt(RepeatRule.COUNT, 0); //0 means repeat forever, or until end date //-necessary when end date is set? (is default count == 0?)
        } else if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE && isDatedCompletion()) {
            repeatRule.setInt(RepeatRule.COUNT, 1);
        }
        if (getRepeatType() != RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE || isDatedCompletion()) {
            if (getInterval() >= 1) {
                repeatRule.setInt(RepeatRule.INTERVAL, getInterval());
            }
            switch (getFrequency()) {
                case RepeatRule.DAILY:
                    //nothing further to set than interval or count set above
                    break;
                case RepeatRule.WEEKLY:
                    setWeek(repeatRule);
                    break;
                case RepeatRule.MONTHLY:
                    setMonth(repeatRule);
                    break;
                case RepeatRule.YEARLY:
                    setYear(repeatRule);
                    break;
            }
        }
        return repeatRule;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private RepeatRule getRepeatRuleOLD() {
//        RepeatRule repeatRule = new RepeatRule();
//        repeatRule.setInt(RepeatRule.FREQUENCY, getFrequency());
//        //TODO!!!: not necessary to set below values since they're also set when calculating dates
////        if (useCount()) {
//        if (getNumberOfRepeats() != Integer.MAX_VALUE) {
////            repeatRule.setInt(RepeatRule.COUNT, getCount()-getCountOfInstancesGeneratedSoFar()); //-getCountOfInstancesGeneratedSoFar() since we call this repeatedly but still want to limit the overall amount generated
//            repeatRule.setInt(RepeatRule.COUNT, getNumberOfRepeats());
////        } else if (getEndDate() != Long.MAX_VALUE) {
//        } else if (getEndDate() != MyDate.MAX_DATE) {
////            repeatRule.setDate(RepeatRule.END, getEndDate());
//            repeatRule.setDate(RepeatRule.END, MyDate.getEndOfDay(getEndDateD()).getTime()); //set to end of day to not miss
////            repeatRule.setInt(RepeatRule.COUNT, 0); //0 means repeat forever, or until end date //-necessary when end date is set? (is default count == 0?)
//        } else if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
//            repeatRule.setInt(RepeatRule.COUNT, 1);
//        }
//        if (getInterval() > 1) {
//            repeatRule.setInt(RepeatRule.INTERVAL, getInterval());
//        }
//        switch (getFrequency()) {
//            case RepeatRule.DAILY:
//                //nothing further to set than interval or count set above
//                break;
//            case RepeatRule.WEEKLY:
//                setWeek(repeatRule);
//                break;
//            case RepeatRule.MONTHLY:
//                setMonth(repeatRule);
//                break;
//            case RepeatRule.YEARLY:
//                setYear(repeatRule);
//                break;
//        }
//        return repeatRule;
//    }
//</editor-fold>
    private List cachedUndoneInstances; //need to cache this list which may become long and gets called multiple time by equals

    public void setListOfUndoneInstances(List list) {
        if (list != null && !list.isEmpty()) {
            put(PARSE_UNDONE_INSTANCES, list);
        } else {
            remove(PARSE_UNDONE_INSTANCES);
        }
        cachedUndoneInstances = list;
    }

    /**
     * keep track of undone (not Done/Cancelled/Deleted) repeat instances (not
     * used for WorkSlots?!). Used to delete repeat instances if repeatRule is
     * deleted, or to reuse existing instances if the rule is changed.
     *
     * @return
     */
//    public List<ParseObject> getListOfUndoneRepeatInstances() {
//    public List<ItemAndListCommonInterface> getListOfUndoneRepeatInstances() {
//    public List<ItemAndListCommonInterface> getListOfUndoneRepeatInstances() {
    public List<RepeatRuleObjectInterface> getListOfUndoneInstances() {
        //TODO!!!! used for WorkSlots? (is externalized as if WorkSlots could be in list)
        if (cachedUndoneInstances == null) { //            DAO.getInstance().fetchAllElementsInSublist(list, false);
            cachedUndoneInstances = getList(PARSE_UNDONE_INSTANCES);
            if (Config.TEST && cachedUndoneInstances != null) {
                Log.p("RepeatRuleObject.getListOfUndoneInstances() called with list size=" + cachedUndoneInstances.size());
            }
            if (cachedUndoneInstances != null) {
                DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(cachedUndoneInstances);
//                cachedUndoneInstances = list;
            } else {
                cachedUndoneInstances = new ArrayList();
            }
        }
        return cachedUndoneInstances;
    }

    private List cachedDoneInstances; //need to cache this list which may become long and gets called multiple time by equals

    public void setListOfDoneInstances(List list) {
        if (list != null && !list.isEmpty()) {
            put(PARSE_DONE_INSTANCES, list);
        } else {
            remove(PARSE_DONE_INSTANCES);
        }
        cachedDoneInstances = list;
    }

    /**
     * keep track of done (Done/Cancelled/Deleted) repeat instances (not used
     * for WorkSlots?!). Used to delete repeat instances if repeatRule is
     * deleted, or to reuse existing instances if the rule is changed.
     *
     * @return
     */
    public List<RepeatRuleObjectInterface> getListOfDoneInstances() {
        //TODO!!!! used for WorkSlots? (is externalized as if WorkSlots could be in list)
        if (cachedDoneInstances == null) {
            cachedDoneInstances = getList(PARSE_DONE_INSTANCES);
            if (false && Config.TEST && cachedDoneInstances != null) {
                Log.p("RepeatRuleObject.getListOfDoneInstances() called with list size=" + cachedDoneInstances.size());
            }
            if (cachedDoneInstances != null) {
                DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(cachedDoneInstances);
//                cachedDoneInstances = list;
            } else {
                cachedDoneInstances = new ArrayList();
            }
        }
        return cachedDoneInstances;
    }

    /**
     * return the repeat instance for a RepeatRule that should be used when
     * generating a template from a repeating task/project, return null if none
     * found (eg rule has not executed yet). Use last Undone task, if none, last
     * Done task.
     *
     * @return
     */
    public Item getItemForTemplateN() {
        List listUndone = getListOfUndoneInstances();
        if (listUndone.size() > 0) {
            return (Item) listUndone.get(listUndone.size() - 1);
        } else {
            List listDone = getListOfDoneInstances();
            if (listDone.size() > 0) {
                return (Item) listDone.get(listDone.size() - 1);
            }
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void removeFromListOfUndoneRepeatInstancesXXX(Object doneRepeatInstance) {
//        List list = getListOfUndoneRepeatInstances();
//        list.remove(doneRepeatInstance);
//        setListOfUndoneRepeatInstances(list);
//    }
//
//    private void setDatesListXXX(List<Date> list) {
////        put(PARSE_DATES_LIST, list);
//    }
//
//    private List<Date> getDatesListXXX() {
////        return (List<Long>)getList("datesList");
////        return getList(PARSE_DATES_LIST);
//        List list = getList(PARSE_DATES_LIST);
//        if (list == null) {
//            list = new ArrayList();
//        }
//        return list;
//    }
//</editor-fold>
    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME;
    }

    public void addOriginatorToRule(RepeatRuleObjectInterface orginatingElement) {
        ASSERT.that(getListOfDoneInstances().size() == 0, "ERROR: new repeatRule already has elements in DoneInstances=" + getListOfDoneInstances());
        ASSERT.that(getListOfUndoneInstances().size() == 0, "ERROR: new repeatRule already has elements in UndoneInstances=" + getListOfUndoneInstances());
        if (orginatingElement instanceof Item) {
            if (((Item) orginatingElement).isDone()) {
                List<RepeatRuleObjectInterface> done = getListOfDoneInstances();
                done.add(orginatingElement);
                setListOfDoneInstances(done);
            } else {
                List<RepeatRuleObjectInterface> undone = getListOfUndoneInstances();
                undone.add(orginatingElement);
                setListOfUndoneInstances(undone);
            }
        } else if (orginatingElement instanceof WorkSlot) {
            if (((WorkSlot) orginatingElement).getEndTimeD().getTime() <= MyDate.currentTimeMillis()) {
                List<RepeatRuleObjectInterface> done = getListOfDoneInstances();
                done.add(orginatingElement);
                setListOfDoneInstances(done);
            } else {
                List<RepeatRuleObjectInterface> undone = getListOfUndoneInstances();
                undone.add(orginatingElement);
                setListOfUndoneInstances(undone);
            }
        } else {
            ASSERT.that(false, "RepeatRuleParseObject.addOriginatorToRule: Adding an element neither Item, nor WorkSlot, elt=" + orginatingElement);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override //TODO!!! how to make this externaizable (in case needed) - look at parse4cn1 approach (getExternizable())
//    public String getObjectId() {
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    /**
     * sets up the repeatRule to generate the a new batch of dates
     *
     * @param repeatRule
     * @param totalNumberDatesGeneratedSoFar
     * @return
     */
//    private Vector<Date> getBatchOfRepeatRuleDates(RepeatRule repeatRule, int totalNumberDatesGeneratedSoFar, int COUNTS_AHEAD) {
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Vector<Date> getBatchOfRepeatRuleDatesXXX(RepeatRule repeatRule, int maxNbDatesToGenerate) {
//        int totalNumberDatesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar();
//
//        if (useCount()) {
//            repeatRule.setInt(RepeatRule.COUNT, Math.min(getNumberOfRepeats() - totalNumberDatesGeneratedSoFar + 1, maxNbDatesToGenerate + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//        } else {
//            repeatRule.setInt(RepeatRule.COUNT, maxNbDatesToGenerate + 1); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//        }
//
//        long subsetBeginningGen;
//        if (totalNumberDatesGeneratedSoFar == 0) {
//            subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
//        } else {
//            subsetBeginningGen = getLastGeneratedDate(); //get the last date we've generated so far
//        }
//
//        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, getEndDate()); //CREATE new dates
//
//        return newDates;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private class DateBuffer {
//
//        DateBuffer() {
//        }
//        /**
//         * stores the last generated date, so we can start from that one next
//         * time, and then drop it
//         */
////        long lastDateGenerated;
//        int COUNTS_AHEAD = 2;//Settings.getInstance().getDefaultNumberOfRepeatInstancesBuffered();
//        /**
//         * stores (buffers) the dates that are already generated
//         */
//        VectorBuffered datesList = new VectorBuffered(COUNTS_AHEAD + 1);
//        int totalNumberDatesGenerated = 0;
//
//        class VectorBuffered {
//
////            Vector vector;
//            ArrayList vector;
////            int indexOffset = 0;
//
//            VectorBuffered(int initialSize) {
////                vector = new Vector(initialSize);
//                vector = new ArrayList(initialSize);
//            }
//
////            public int sizeTotal() {
////                return vector.size() + indexOffset;
////            }
//            public int size() {
//                return vector.size();
//            }
//
//            /**
//             * take first element (head) out of list and return it. Returns null
//             * if list empty
//             */
//            Object removeAndReturnFirst() {
//                if (vector.size() > 0) {
////                    Object temp = vector.firstElement();
//                    Object temp = vector.remove(0);
////                    indexOffset++;
//                    return temp;
//                } else {
//                    return null;
//                }
//            }
//
//            Object firstElement() {
////                return vector.firstElement();
//                if (vector.size() > 0) {
//                    return vector.get(0);
//                } else {
//                    return null;
//                }
//            }
//
//            Object lastElement() {
////                return vector.lastElement();
//                if (vector.size() > 0) {
//                    return vector.get(vector.size() - 1);
//                } else {
//                    return null;
//                }
//            }
//
//            void addElement(Object element) {
////                vector.addElement(element);
//                vector.add(element);
//            }
//        }
//
//        public void externalize(DataOutputStream dos) throws IOException {
////            dos.writeLong(lastDateGenerated);
//
//            int vectorSize = datesList.vector.size();
//            dos.writeInt(vectorSize);
//            for (int i = 0; i < vectorSize; i++) {
////                dos.writeLong(((Date) datesList.vector.elementAt(i)).getTime());
//                dos.writeLong(((Date) datesList.vector.get(i)).getTime());
//            }
////            dos.writeInt(datesList.indexOffset);
////            dos.writeInt(datesList.totalNumberDatesGenerated);
//        }
//
//        public void internalize(int version, DataInputStream dis) throws IOException {
////            lastDateGenerated = dis.readLong();
//
//            int vectorSize = dis.readInt();
////            datesList.vector = new Vector(vectorSize);
//            datesList.vector = new ArrayList(vectorSize);
//            for (int i = 0; i < vectorSize; i++) {
////                datesList.vector.addElement(new Date(dis.readLong()));
//                datesList.vector.add(new Date(dis.readLong()));
//            }
////            datesList.indexOffset = dis.readInt();
//        }
//
//        /**
//         * generate next batch of repeat dates. Returns empty List if the repeat
//         * rule has ended (no more dates).
//         */
////<editor-fold defaultstate="collapsed" desc="comment">
////        private static Vector updateNew(RepeatRule repeatRule, long specifiedStartDate, long endDate, long lastRepeatDate, int totalMaxCountToGenerate, int countAlreadyGenerated, int numberOfRepeatsToGenerate) {
////
//////            if (datesList.sizeActual() > 1 || datesList.size() >= getCount()) { //need to generate more instances while there is we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
//////            if (datesList.sizeTotal() == 0 || (datesList.size() == 1 && datesList.sizeTotal() < getCount())) { //need to generate more instances while there is at least one of previous 'batch' left, since we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
//////                RepeatRule repeatRule = getRepeatRule();
//////                repeatRule.setInt(RepeatRule.COUNT, Math.min(getCount() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//////                if (getCount() != Integer.MAX_VALUE) {
//////                    repeatRule.setInt(RepeatRule.COUNT, Math.min(getCount() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//////                } else {
//////                    repeatRule.setInt(RepeatRule.COUNT, COUNTS_AHEAD + 1); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//////                }
////            //if numberOfRepeatsToGenerate is less than the total remaining to generate, then generate only numberOfRepeatsToGenerate instances,
////            //otherwise generate totalMaxCountToGenerate-countAlreadyGenerated which is the total of still missing instances to generate
////            int numberOfInstancesToGenerate = Math.min(numberOfRepeatsToGenerate, totalMaxCountToGenerate - countAlreadyGenerated);
////            repeatRule.setInt(RepeatRule.COUNT, numberOfInstancesToGenerate); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//////                long subsetBeginningGen;
//////                if (datesList.sizeTotal() == 0) {
//////                    subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
//////                } else {
////////                    subsetBeginningGen = ((Long) datesList.lastElement()).longValue(); //get the last date we've generated so far
//////                    subsetBeginningGen = ((Date) datesList.lastElement()).getTime(); //get the last date we've generated so far
//////                }
////            long subsetBeginningGen = Math.max(specifiedStartDate, lastRepeatDate + 1); //lastRepeatDate+1 to ensure that we don't regenerate the last already generated date, but the one after that
//////                Vector newDates = repeatRule.datesAsVector(specifiedStartDate, subsetBeginningGen, endDate);
////            return repeatRule.datesAsVector(specifiedStartDate, subsetBeginningGen, endDate);
//////                if (!newDates.isEmpty()) { //if vector not empty
//////                    newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that
//////                }
//////                while (!newDates.isEmpty()) {
//////                    datesList.addElement(newDates.firstElement()); //store new batch of dates
//////                    newDates.removeElementAt(0);
//////                }
//////                ASSERT.that(datesList.sizeTotal() <= getCount(), "generated too many instances total=" + datesList.sizeTotal() + " getCount=" + getCount() + " #rule=" + this);
//////            }
////        }
////</editor-fold>
//        private void update() {
//            //TODO simplify logic with generating more once there is only one previous left to: simply store last date generated
////            if (datesList.sizeActual() > 1 || datesList.size() >= getCount()) { //need to generate more instances while there is we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
////            if (datesList.sizeTotal() == 0 || (datesList.size() == 1 && datesList.sizeTotal() < getNumberOfRepeats())) { //need to generate more instances while there is at least one of previous 'batch' left, since we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
//            List<Long> datesList = getDatesList();
//            if (datesList.size() == 0 || (datesList.size() == 1 && getTotalNumberOfInstancesGeneratedSoFar() < getNumberOfRepeats())) { //need to generate more instances while there is at least one of previous 'batch' left, since we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
//                RepeatRule repeatRule = getRepeatRule();
//                totalNumberDatesGenerated = getTotalNumberOfInstancesGeneratedSoFar();
////                repeatRule.setInt(RepeatRule.COUNT, Math.min(getCount() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//                if (getNumberOfRepeats() != Integer.MAX_VALUE) {
////                    repeatRule.setInt(RepeatRule.COUNT, Math.min(getNumberOfRepeats() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//                    repeatRule.setInt(RepeatRule.COUNT, Math.min(getNumberOfRepeats() - totalNumberDatesGenerated + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//                } else {
//                    repeatRule.setInt(RepeatRule.COUNT, COUNTS_AHEAD + 1); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//                }
//                long subsetBeginningGen;
////                if (datesList.sizeTotal() == 0) {
//                if (totalNumberDatesGenerated == 0) {
//                    subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
//                } else {
////                    subsetBeginningGen = ((Long) datesList.lastElement()).longValue(); //get the last date we've generated so far
////                    subsetBeginningGen = ((Date) datesList.lastElement()).getTime(); //get the last date we've generated so far
//                    subsetBeginningGen = getLastGeneratedDate(); //get the last date we've generated so far
//                }
//
//                Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, getEndDate()); //CREATE new dates
//
//                if (!newDates.isEmpty()) { //if vector not empty
//                    newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//                }
//                while (!newDates.isEmpty()) {
//                    Date date = (Date) newDates.firstElement();
//                    if (newDates.size() == 1) {
//                        setLastGeneratedDate(date);
//                    }
////                    datesList.addElement(date); //store new batch of dates
//                    datesList.add(date); //store new batch of dates
//                    totalNumberDatesGenerated++;
//                    newDates.removeElementAt(0);
//                }
//                setTotalNumberOfInstancesGeneratedSoFar(totalNumberDatesGenerated);
////                ASSERT.that(datesList.sizeTotal() <= getNumberOfRepeats(), "generated too many instances total=" + datesList.sizeTotal() + " getCount=" + getNumberOfRepeats() + " #rule=" + this);
//                ASSERT.that(getTotalNumberOfInstancesGeneratedSoFar() <= getNumberOfRepeats(), "generated too many instances total=" + getTotalNumberOfInstancesGeneratedSoFar() + " getCount=" + getNumberOfRepeats() + " #rule=" + this);
//            }
//        }
//
//        boolean hasMoreDates() {
//            update();
////            return nextDateIndex < datesList.size();
//            return datesList.size() > 0;
//        }
//
//        /**
//         * removes the first date and returns it
//         */
//        Date takeNext() {
//            update();
//            return (Date) datesList.removeAndReturnFirst();
//        }
//
//        /**
//         * returns the date of the next Date in line, or MAX_VALUE if no more
//         * values
//         */
//        long getNextDate() {
//            update();
//            if (datesList.size() > 0) {
//                return ((Date) datesList.firstElement()).getTime();
//            } else {
//                return Long.MAX_VALUE;
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private List<Date> updateDatesListXXX(List<Date> datesList, RepeatRule repeatRule) {
//        //DONE simplify logic with generating more once there is only one previous left to: simply store last date generated
//        if (datesList.size() == 0) {
//            int totalNumberDatesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar();
////<editor-fold defaultstate="collapsed" desc="moved to getBatchOfRepeatRuleDates">
////            if (false) {
////                if (useCount()) {
////                    repeatRule.setInt(RepeatRule.COUNT, Math.min(getNumberOfRepeats() - totalNumberDatesGeneratedSoFar + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
////                } else {
////                    repeatRule.setInt(RepeatRule.COUNT, COUNTS_AHEAD + 1); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
////                }
////                long subsetBeginningGen;
////                if (totalNumberDatesGeneratedSoFar == 0) {
////                    subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
////                } else {
////                    subsetBeginningGen = getLastGeneratedDate(); //get the last date we've generated so far
////                }
////
////                Vector<Date> newDatesXXX = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, getEndDate()); //CREATE new dates
////            }
////</editor-fold>
//            Vector<Date> newDates = getBatchOfRepeatRuleDatesXXX(repeatRule, COUNTS_AHEAD); //CREATE new dates
//
////            if (!newDates.isEmpty() && getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//            if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//                newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//            }
//            while (!newDates.isEmpty()) {
//                Date date = newDates.remove(0); //take first date in list
//                if (newDates.size() == 0) { //if last of the generated dates
//                    setLastGeneratedDate(date); //store that date for next iteration
//                }
//                datesList.add(date); //store new batch of dates
//                totalNumberDatesGeneratedSoFar++;
//            }
//            setTotalNumberOfInstancesGeneratedSoFar(totalNumberDatesGeneratedSoFar);
//            ASSERT.that(getTotalNumberOfInstancesGeneratedSoFar() <= getNumberOfRepeats(), "generated too many instances total=" + getTotalNumberOfInstancesGeneratedSoFar() + " getCount=" + getNumberOfRepeats() + " #rule=" + this);
//        }
//        return datesList;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="updateDatesListOLD">
//    private List<Date> updateDatesListOLD(List<Date> datesList, RepeatRule repeatRule) {
//        //TODO simplify logic with generating more once there is only one previous left to: simply store last date generated
////            if (datesList.sizeActual() > 1 || datesList.size() >= getCount()) { //need to generate more instances while there is we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
////            if (datesList.sizeTotal() == 0 || (datesList.size() == 1 && datesList.sizeTotal() < getNumberOfRepeats())) { //need to generate more instances while there is at least one of previous 'batch' left, since we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
////        List<Date> datesList = getDatesList();
////        if (datesList.size() == 0 || (datesList.size() == 1 && totalNumberDatesGenerated < getNumberOfRepeats())) { //need to generate more instances while there is at least one of previous 'batch' left, since we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
//        if (datesList.size() == 0) {
//            int totalNumberDatesGenerated = getTotalNumberOfInstancesGeneratedSoFar();
////            RepeatRule repeatRule = getRepeatRule();
////                repeatRule.setInt(RepeatRule.COUNT, Math.min(getCount() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
////            if (getNumberOfRepeats() != Integer.MAX_VALUE) {
//            if (useCount()) {
////                    repeatRule.setInt(RepeatRule.COUNT, Math.min(getNumberOfRepeats() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//                repeatRule.setInt(RepeatRule.COUNT, Math.min(getNumberOfRepeats() - totalNumberDatesGenerated + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//            } else {
//                repeatRule.setInt(RepeatRule.COUNT, COUNTS_AHEAD + 1); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//            }
//            long subsetBeginningGen;
////                if (datesList.sizeTotal() == 0) {
//            if (totalNumberDatesGenerated == 0) {
//                subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
//            } else {
////                    subsetBeginningGen = ((Long) datesList.lastElement()).longValue(); //get the last date we've generated so far
////                    subsetBeginningGen = ((Date) datesList.lastElement()).getTime(); //get the last date we've generated so far
//                subsetBeginningGen = getLastGeneratedDate(); //get the last date we've generated so far
//            }
//
//            Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, getEndDate()); //CREATE new dates
//
//            if (!newDates.isEmpty() && getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//                newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//            }
//            while (!newDates.isEmpty()) {
//                Date date = newDates.remove(0);
//                if (newDates.size() == 0) { //if last of the generated dates
//                    setLastGeneratedDate(date); //store that date for next iteration
//                }
////                    datesList.addElement(date); //store new batch of dates
//                datesList.add(date); //store new batch of dates
//                totalNumberDatesGenerated++;
////                newDates.removeElementAt(0);
//            }
//            setTotalNumberOfInstancesGeneratedSoFar(totalNumberDatesGenerated);
////                ASSERT.that(datesList.sizeTotal() <= getNumberOfRepeats(), "generated too many instances total=" + datesList.sizeTotal() + " getCount=" + getNumberOfRepeats() + " #rule=" + this);
//            ASSERT.that(getTotalNumberOfInstancesGeneratedSoFar() <= getNumberOfRepeats(), "generated too many instances total=" + getTotalNumberOfInstancesGeneratedSoFar() + " getCount=" + getNumberOfRepeats() + " #rule=" + this);
////            setDatesList(newDates);
//        }
//        return datesList;
//    }
//</editor-fold>
    /**
     * remove and return´next date or null if no more dates (repeatRule reached
     * termination). Updates the datesList first.
     *
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date removeNextDateXXX(RepeatRule repeatRule) {
////        List<Long> datesList = getDatesList();
//        List<Date> datesList = getDatesList();
////        updateDatesList(datesList);
//        datesList = updateDatesListXXX(datesList, repeatRule);
//        setDatesList(datesList);
////        DAO.getInstance().save(this); //DON'T SAVE HERE, only in externally accessible methods
//
//        if (datesList.size() > 0) {
//            return (datesList.remove(0));
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getNextDateXXX(RepeatRule repeatRule) {
////        List<Long> datesList = getDatesList();
//        List<Date> datesList = getDatesList();
//        datesList = updateDatesListXXX(datesList, repeatRule);
//        setDatesList(datesList);
////        DAO.getInstance().save(this);
//        if (datesList.size() > 0) {
//            return (datesList.get(0));
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns true if the repeatRule has terminated (reached maximum of defined
     * number of repeats or generated the last date possible up to the defined
     * endDate)
     *
     * @return
     */
//    private boolean isRepeatRuleTerminatedXXX() {
//        return getTotalNumberOfInstancesGeneratedSoFar() >= getNumberOfRepeats()
//                || getLastGeneratedDate() >= getEndDate();
//    }
    /**
     * returns the count value to use for the repeatRule. If the repeat rule is
     * defined as a limited to a certain number of repeats, it will return
     * either the number of remaining repeats or maxNbDatesToGenerate, whatever
     * is the smaller. If the rule is not defined by a max number of repeats (if
     * it defines a specific endDate) it will always return
     * maxNbDatesToGenerate.
     *
     * @param maxNbDatesToGenerate the maximum number of dates to generate
     * @return
     */
//    private int getCountXXX(int maxNbDatesToGenerate) {
//        if (useCount()) {
////            return Math.min(getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar() + 1, maxNbDatesToGenerate + 1); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//            return Math.min(getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar(), maxNbDatesToGenerate); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//        } else {
//            return maxNbDatesToGenerate; //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//        }
//    }
//</editor-fold>
    /**
     * remove the first generated date if it duplicates the subsetBeginDate
     * (since this date usually comes from an already existing and just
     * completed item) and reduce the number of dates if more than
     * numberInstancesToGenerate are generated
     *
     * @param newDates
     * @param subsetBeginDate
     * @param numberInstancesToGenerate
     * @param keepFirstDateGenerated
     */
    private static void removeUnneededDates(List<Date> newDates, Date subsetBeginDate, int numberInstancesToGenerate, boolean keepFirstDateGenerated) {
        if (!newDates.isEmpty()) {
            Date firstDate = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
            if (!keepFirstDateGenerated && firstDate.getTime() == subsetBeginDate.getTime()) {
                newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
            } //else { //happens when RR doesn't generate the subsetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)

            //if still too many dates, remove until only numberInstancesToGenerate left:
            while (newDates.size() > 0 && newDates.size() > numberInstancesToGenerate) {
                newDates.remove(newDates.size() - 1); //remove extraneous elements
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * when generating dates for workslots, we must generate all past workslots
     * and then the appropriate future workslots, so cannot use the same algo as
     * for
     *
     * @param subsetBeginDate
     * @param subsetEndDate
     * @param numberInstancesToGenerate
     * @param genAtLeastOneDate
     * @param keepFirstDateGenerated
     * @return
     */
//    public List<Date> generateListOfDatesForWorkSlotsXXX(Date subsetBeginDate, Date subsetEndDate, int numberInstancesToGenerate, boolean genAtLeastOneDate, boolean keepFirstDateGenerated) {
//
//        if (subsetEndDate == null || subsetEndDate.getTime() == 0) {
//            subsetEndDate = subsetBeginDate;
//        }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false) {
////            int requestedCount = numberInstancesToGenerate == -1 ? Integer.MAX_VALUE - 1 : numberInstancesToGenerate; // +1 since we need to generate the last generated date again
////            int countMaxRemaining;
////            int countFutureRepeats = Integer.MAX_VALUE;
////            if (useCount()) {
////                //if rule is limited to number of counts, then take remaining number of repeats into account
////                countMaxRemaining = Math.min(requestedCount, getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar()); //cap count number of remaining repeats
////                count = countMaxRemaining;
////            } else {
////                count = requestedCount;
////            }
////            if (getNumberFutureRepeatsToGenerateAhead() != 0) {
////                countFutureRepeats = getNumberFutureRepeatsToGenerateAhead();
////                count = Math.min(count, countFutureRepeats); //cap count to getNumberFutureRepeatsToGenerateAhead()
////            }
////        }
////</editor-fold>
//        int count = numberInstancesToGenerate;
//
//        if (count == Integer.MAX_VALUE && subsetEndDate.getTime() == MyDate.MAX_DATE) {
//            count = 1; //if infinite repeat, generate just one
//        }
//
//        if (MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt() != 0) {
//            count = Math.min(count, MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt()); //+1
//        }
//        if (count < Integer.MAX_VALUE) { //test for MAX to avoid wrapping around the int
//            count++; // +1 since we may generate the last generated date (subsetBeginDate) again
//        }
////        count = Math.max(count, 1); //COUNT must never be <1
//
//        RepeatRule repeatRule = getRepeatRule();
//        repeatRule.setInt(RepeatRule.COUNT, count);
//        Date startDate = getSpecifiedStartDate();
////<editor-fold defaultstate="collapsed" desc="comment">
////        long subsetBegin = endRepeatDate != null && endRepeatDate.getTime() != 0 ? endRepeatDate.getTime() : MAX_DATE;
////        long subsetBegin = subsetBeginDate.getTime();
////        long subsetEnd = subsetEndDate != null && subsetEndDate.getTime() != 0 ? subsetEndDate.getTime() : MAX_DATE;
////        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), lastGeneratedDate.getTime(), endDate); //CREATE new dates
////</editor-fold>
//
//        Vector<Date> newDates = repeatRule.datesAsVector(startDate, subsetBeginDate, subsetEndDate); //CREATE new dates
//
//        //if first generated date equals subsetStart, then remove it. And remove superfluous instances
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && !newDates.isEmpty()) {
////            Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
////            ASSERT.that(date.getTime() >= subsetBeginDate.getTime());
////            ASSERT.that(subsetEndDate == null || date.getTime() <= subsetEndDate.getTime());
////            if (date.getTime() == subsetBeginDate.getTime()) {
////                date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////            } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
////                if (numberInstancesToGenerate != Integer.MAX_VALUE) {
////                    ASSERT.that(newDates.size() <= numberInstancesToGenerate + 1, "newDates should max have 1 date more than count");
////                    while (newDates.size() > numberInstancesToGenerate) {
////                        newDates.remove(newDates.size() - 1); //remove extraneous elements
////                    }
////                } else {
////                    ASSERT.that(newDates.get(newDates.size() - 1).getTime() <= subsetEndDate.getTime(), "**");
////                    //when generating towards endDate, will always generate right number?!
////                }
////            }
////        }
////</editor-fold>
//        removeSuperfluousDates(newDates, subsetBeginDate, numberInstancesToGenerate, keepFirstDateGenerated);
////
////        if (genAtLeastOneDate && numberInstancesToGenerate == Integer.MAX_VALUE && newDates.size() == 0) {
//        //if no new date before endDate, and rule not terminated, then regenerate again with infinite endDate and count==1 (or 2?) to ensure at least ONE future date is generated!
//        if (genAtLeastOneDate && numberInstancesToGenerate > 0 && newDates.size() == 0) {
//            repeatRule.setInt(RepeatRule.COUNT, 2); //gen 2 dates since 1st may be startDate
//            newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginDate, MyDate.getEndOfDay(getEndDateD())); //CREATE new dates
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (false && !newDates.isEmpty()) {
////                Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
////                if (date.getTime() == subsetBeginDate.getTime()) {
////                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////                } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
////                    ASSERT.that(newDates.size() <= numberInstancesToGenerate + 1, "newDates should max have 1 date more than count");
////                    while (newDates.size() > numberInstancesToGenerate) {
////                        newDates.remove(newDates.size() - 1); //remove extraneous elements
////                    }
////                }
////            }
////</editor-fold>
//            removeSuperfluousDates(newDates, subsetBeginDate, 1, keepFirstDateGenerated); //1 : keep at least one instance
//        }
////
//        return new ArrayList(newDates);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private List<Date> generateListOfDatesOLD(Date subsetBeginDate, Date subsetEndDate, int numberInstancesToGenerate, boolean genAtLeastOneDate, boolean keepFirstDateGenerated) {
//        RepeatRule repeatRule = getRepeatRule();
//        int count;
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false) {
////            int requestedCount = numberInstancesToGenerate == -1 ? Integer.MAX_VALUE - 1 : numberInstancesToGenerate; // +1 since we need to generate the last generated date again
////            int countMaxRemaining;
////            int countFutureRepeats = Integer.MAX_VALUE;
////            if (useCount()) {
////                //if rule is limited to number of counts, then take remaining number of repeats into account
////                countMaxRemaining = Math.min(requestedCount, getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar()); //cap count number of remaining repeats
////                count = countMaxRemaining;
////            } else {
////                count = requestedCount;
////            }
////            if (getNumberFutureRepeatsToGenerateAhead() != 0) {
////                countFutureRepeats = getNumberFutureRepeatsToGenerateAhead();
////                count = Math.min(count, countFutureRepeats); //cap count to getNumberFutureRepeatsToGenerateAhead()
////            }
////        }
////</editor-fold>
//        count = numberInstancesToGenerate;
//        if (count < Integer.MAX_VALUE) { //test for MAX to avoid wrapping around the int
//            count++; // +1 since we may generate the last generated date (subsetBeginDate) again
//        }
//        count = Math.max(count, 1); //COUNT must never be <1
//        if (MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt() != 0) {
//            count = Math.min(count, MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt() + 1);
//        }
//        repeatRule.setInt(RepeatRule.COUNT, count);
//        Date startDate = getSpecifiedStartDate();
////<editor-fold defaultstate="collapsed" desc="comment">
////        long subsetBegin = endRepeatDate != null && endRepeatDate.getTime() != 0 ? endRepeatDate.getTime() : MAX_DATE;
////        long subsetBegin = subsetBeginDate.getTime();
////        long subsetEnd = subsetEndDate != null && subsetEndDate.getTime() != 0 ? subsetEndDate.getTime() : MAX_DATE;
////        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), lastGeneratedDate.getTime(), endDate); //CREATE new dates
////</editor-fold>
//
//        Vector<Date> newDates = repeatRule.datesAsVector(startDate, subsetBeginDate, subsetEndDate); //CREATE new dates
//
//        //if first generated date equals subsetStart, then remove it. And remove superfluous instances
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && !newDates.isEmpty()) {
////            Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
////            ASSERT.that(date.getTime() >= subsetBeginDate.getTime());
////            ASSERT.that(subsetEndDate == null || date.getTime() <= subsetEndDate.getTime());
////            if (date.getTime() == subsetBeginDate.getTime()) {
////                date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////            } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
////                if (numberInstancesToGenerate != Integer.MAX_VALUE) {
////                    ASSERT.that(newDates.size() <= numberInstancesToGenerate + 1, "newDates should max have 1 date more than count");
////                    while (newDates.size() > numberInstancesToGenerate) {
////                        newDates.remove(newDates.size() - 1); //remove extraneous elements
////                    }
////                } else {
////                    ASSERT.that(newDates.get(newDates.size() - 1).getTime() <= subsetEndDate.getTime(), "**");
////                    //when generating towards endDate, will always generate right number?!
////                }
////            }
////        }
////</editor-fold>
//        removeSuperfluousDates(newDates, subsetBeginDate, numberInstancesToGenerate, keepFirstDateGenerated);
////
////        if (genAtLeastOneDate && numberInstancesToGenerate == Integer.MAX_VALUE && newDates.size() == 0) {
//        //if no new date before endDate, and rule not terminated, then regenerate again with infinite endDate and count==1 (or 2?) to ensure at least ONE future date is generated!
//        if (genAtLeastOneDate && numberInstancesToGenerate > 0 && newDates.size() == 0) {
//            repeatRule.setInt(RepeatRule.COUNT, 2); //gen 2 dates since 1st may be startDate
//            newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginDate, MyDate.getEndOfDay(getEndDateD())); //CREATE new dates
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (false && !newDates.isEmpty()) {
////                Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
////                if (date.getTime() == subsetBeginDate.getTime()) {
////                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////                } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
////                    ASSERT.that(newDates.size() <= numberInstancesToGenerate + 1, "newDates should max have 1 date more than count");
////                    while (newDates.size() > numberInstancesToGenerate) {
////                        newDates.remove(newDates.size() - 1); //remove extraneous elements
////                    }
////                }
////            }
////</editor-fold>
//            removeSuperfluousDates(newDates, subsetBeginDate, 1, keepFirstDateGenerated); //1 : keep at least one instance
//        }
////
//        return new ArrayList(newDates);
//    }
//</editor-fold>
//    private Date getFirstNotEqualDate(Vector<Date> newDates, Date dateCompleted) {
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getFirstNotEqualDateXXX(List<Date> newDates, Date dateCompleted) {
////        Date date=null;
//        if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//            Date date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////            if (!date.equals(dateCompleted)) {
//            if (date.getTime() != dateCompleted.getTime()) {
//                return date;
//            } else {
//                if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//                    return date;
//                }
//            }
//        }
//        removeUnneededTasks(newDates, dateCompleted, Integer.MAX_VALUE);
//
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private static Date getFirstNotEqualDate(List<Date> newDates, Date dateCompleted) {
////        Date date=null;
//        if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//            Date date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////            if (!date.equals(dateCompleted)) {
//            if (date.getTime() != dateCompleted.getTime()) {
//                return date;
//            } else {
//                if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//                    return date;
//                }
//            }
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getNextCompletedFromDateOLD(Date dateCompleted) {
//        RepeatRule repeatRule = getRepeatRule();
//        if (isRepeatRuleTerminated()) {
//            return null;
//        }
//        repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
////<editor-fold defaultstate="collapsed" desc="comment">
////        repeatRule.setInt(RepeatRule.COUNT, getCount(COUNTS_AHEAD));
////DONE!!! need to add 1 day to subsetBeginningDate to avoid repeat same date as last generated Date???
////        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), getEndDate()); //CREATE new dates
////        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), MAX_DATE); //CREATE new dates
////</editor-fold>
//        Vector<Date> newDates = repeatRule.datesAsVector(dateCompleted.getTime(), dateCompleted.getTime(), MAX_DATE); //CREATE new dates
//        //DONE!!!! how to know that the repeatRule has reached the enddate (since it may not generate a date for endDate)? Always generate with infinite endDate and stop when a date is *after* endDate (and discard this date!)
//        Date date;
////        if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
////        if (newDates.size() >= 2) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//        if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//            date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////            if (date.getTime()==dateCompleted.getTime())
//            if (date.equals(dateCompleted)) {
//                if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//                } else {
//                    date = null;
//                }
//            }
////            if (date.getTime() == dateCompleted.getTime()) { //if we've generated the same date, skip it and take the next
//////if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
////                date = newDates.remove(0);
////            }
//            setLastGeneratedDate(date); //store that date for next iteration (if date>endDate this will indicate the RR has terminated)
//            if (date.getTime() > getEndDate()) {
//                return null;
//            } else {
//                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//                return date;
//            }
//        } else {
//            ASSERT.that(false, "shouldn't happen?!");  //otherwise set numberRepeats to MAX or endDate to Max to indicate rule has terminated
//            return null;
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        return null;
////        if (newDates.size()>=2) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//////        if (!newDates.isEmpty()) {
////             date = newDates.remove(1); //discard first date (, use setake first date in list
////            setLastGeneratedDate(date); //store that date for next iteration (if date>endDate this will indicate the RR has terminated)
////            if (date.getTime() > getEndDate()) {
////                return null;
////            } else {
////                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
////                return date;
////            }
////        } else {
////            ASSERT.that(false, "shouldn't happen?!");  //otherwise set numberRepeats to MAX or endDate to Max to indicate rule has terminated
////            return null;
////        }
////</editor-fold>
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getNextDateXXX(Date dateCompleted) {
////        RepeatRule repeatRule = getRepeatRule();
//        if (getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE) {
//            return getNextDueDate();
//        } else {
//            return getNextCompletedFromDate(dateCompleted);
//        }
//    }
//</editor-fold>
//    private List<Date> createDates(int maxRepeats) {
////        Date startDate = getSpecifiedStartDate();
//        Date startDate = getStartDateForRefreshRule();
//        Date endDate = getEndDateD();
////        nextDate = startDate;
//        int repeats = Math.min(calcNumberOfRepeats(), maxRepeats);
////        return generateListOfDates(startDate, calcSubsetEndDate(startDate), repeats,true);
//        return generateListOfDates(startDate, endDate, repeats, true, false);
//    }
//    private static void listDates(List<Date> dates) {
//        for (Date date : dates) {
//            Log.p(MyDate.formatDateNew(date, false, true, true, true, MyPrefs.dateShowDatesInUSFormat.getBoolean()));
//        }
//    }
//    private void listDatesTest(int nbInstances) {
//        listDates(createDates(nbInstances));
//    }
//    private boolean simulateDates = false; //when simulating the generated dates, disable any max number set by settings
    /**
     * show a pop-up dialog with the generated dates, shown with 20 each time
     */
    public void showRepeatDueDates() {
//        int MAX_REPEATS = 100;
//        showListOfRepeatRuleGeneratedDates(MAX_REPEATS);
//    }
//    public void showListOfRepeatRuleGeneratedDates(int maxInstances) {
        int maxInstances = 100;
        Vector datesVector = new Vector();
        Date startDate = getStartDateForRefreshRule(new MyDate());
        Date endDate = getEndDateD();
        int repeats = Math.min(calcNumberOfRepeats(true, 0), maxInstances);
        List<Date> dates = generateListOfDates(startDate, endDate, repeats, true, false);
        Dialog datesList = new Dialog(BoxLayout.y());
        datesList.setScrollableY(true);
        datesList.setDisposeWhenPointerOutOfBounds(true);
        datesList.setTitle("Dates");
//        datesList.addCommand(new Command("Done"));
        datesList.placeButtonCommands(new Command[]{new Command("Done")});
        for (Date date : dates) {
//            datesList.add(new Label(MyDate.formatDateNew(date)));
            Label l = new Label(MyDate.formatDateNew(date, false, true, true, true, MyPrefs.dateShowDatesInUSFormat.getBoolean()), "DateLabelCentered");
//            l.setTextPosition(Label.CENTER);
            datesList.add(l);
        }
        if (dates.size() == maxInstances) {
            datesList.add(new Label(Format.f("Showing {0} first...", "" + maxInstances), "DateLabelCentered"));
        }
        datesList.show();
    }

//    public void showListOfRepeatRuleGeneratedDatesOLD(int maxInstances) {
//        Vector datesVector = new Vector();
////        int i = 0;
//        int nextI = 0;
//        int nbDatesToShowInEachStep = 20;
//
//        Command showMoreCmd = new Command("Show " + nbDatesToShowInEachStep + " more");
////<editor-fold defaultstate="collapsed" desc="comment">
////        Date nextDate = new Date();
////        Date startDate = new Date();
////        nextDate = startDate;
////        int repeats = Math.min(calcNumberOfRepeats(), MAX_REPEATS);
////        List<Date> dates = generateListOfDates(startDate, calcSubsetEndDate(startDate), repeats);
////        simulateDates = true;
////</editor-fold>
////        List<Date> dates = createDates(maxInstances);
////        Date startDate = getSpecifiedStartDate();
//        Date startDate = getStartDateForRefreshRule(new MyDate());
//        Date endDate = getEndDateD();
////        nextDate = startDate;
//        int repeats = Math.min(calcNumberOfRepeats(true), maxInstances);
////        return generateListOfDates(startDate, calcSubsetEndDate(startDate), repeats,true);
//        List<Date> dates = generateListOfDates(startDate, endDate, repeats, true, false);
//
//        do {
//            for (int i = nextI, size = Math.min(dates.size(), nextI + nbDatesToShowInEachStep - 1); i < size; i++) { //Math.min(dates.size in case the dates list is shorter than nbDatesToShowInEachStep
//                datesVector.addElement(MyDate.formatDateNew(dates.get(i), false, true, true, true, MyPrefs.dateShowDatesInUSFormat.getBoolean()));
//            }
//            nextI += nbDatesToShowInEachStep;
//        } while (Dialog.show("Dates", new com.codename1.ui.List(datesVector),
//                //                new Command[]{new Command("Exit"), nextI >= maxInstances ? showMoreCmd : new Command("No more")}) == showMoreCmd);
//                new Command[]{new Command("Exit"), nextI >= maxInstances ? showMoreCmd : new Command("That's all")}) == showMoreCmd);
////        simulateDates = false;
//    }
    //<editor-fold defaultstate="collapsed" desc="comment">
//    public Date getNextDueDateOLD() {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && isRepeatRuleTerminatedXXX()) { //DO NOT test here, since dates need to be re-generated when changing the rule
////            return null;
////        }
////        repeatRule.setInt(RepeatRule.COUNT, getCount(COUNTS_AHEAD));
////        repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false) {
////            RepeatRule repeatRule = getRepeatRule();
////            repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
////            //TODO!!! need to add 1 day to subsetBeginningDate to avoid repeat same date as last generated Date???
//////        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), getEndDate()); //CREATE new dates
////            ASSERT.that(getLastGeneratedDateD().getTime() != MyDate.MIN_DATE, "getLastGeneratedDate() should have been defined when we get to here");
//////        long subsetBeginningDate = getLastGeneratedDate() != 0 ? Math.min(getLastGeneratedDate(), getEndDate()) : getEndDate(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
////        }
////        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), MAX_DATE); //CREATE new dates
////        Date startDate = getSpecifiedStartDateD();
////        Vector<Date> newDates = repeatRule.datesAsVector(startDate.getTime(), subsetBeginningDate.getTime(), MAX_DATE); //CREATE new dates
////        Date date = getFirstNotEqualDate(dates, new Date(getSubsetBeginningDate()));
////        Date date = getFirstNotEqualDate(dates, subsetBeginningDate);
////        Date subsetBeginningDate = new Date(Math.max(getLastGeneratedDateD().getTime(), getSpecifiedStartDateD().getTime())); //get the last date we've generated so far, Math.min since endDate may get set to larger than
////        Date subsetBeginningDate = calcSubsetBeginDate(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
////</editor-fold>
////        Date subsetBeginningDate;
////        subsetBeginningDate=calcSubsetEndDate(subsetBeginningDate);
////        subsetBeginningDate = new Date(Math.max(getLastGeneratedDateD().getTime(), getSpecifiedStartDateD().getTime())); //get the last date we've generated so far, Math.min since endDate may get set to larger than
////        Date subsetBeginningDate = getLastGeneratedDateD(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
//        Date subsetBeginningDate = null;
//        Date lastGenerated = getLastGeneratedDateD(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
//        if (lastGenerated.getTime() != MyDate.MIN_DATE) {
//            subsetBeginningDate = lastGenerated;
//        } else {
//            subsetBeginningDate = getSpecifiedStartDate();
//        }
////        Date subsetEndDate = calcSubsetEndDate(subsetBeginningDate);
//        Date subsetEndDate = calcSubsetEndDate();
//        int numberRepeats = calcNumberOfRepeats();
////        boolean generateAtLeastOneDate = getNumberOfDaysRepeatsAreGeneratedAhead() != 0;
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false) {
////            ASSERT.that(numberRepeats != Integer.MAX_VALUE || subsetEndDate.getTime() != MyDate.MAX_DATE,
////                    "attempting to generate infinite number of repeats"); //assert wrong since an infinite repeat CAN be defined (and is covered in test below)
////        }
////        if (false && numberRepeats == Integer.MAX_VALUE && subsetEndDate.getTime() == MyDate.MAX_DATE) { //now done in generateListOfDates()
////            numberRepeats = 1; //if infinite repeat, generate just one
////        }
////        if (false && numberRepeats > MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt() && MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt() != 0
////                && !simulateDates && getNumberOfDaysRepeatsAreGeneratedAhead() == 0) { //avoid flooding with too many repeats
////            numberRepeats = MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt() + 1; //+1 because we normally exclude the repeat originator?
////        }        //get the last date we've generated so far, Math.min since endDate may get set to larger than
////</editor-fold>
//        List<Date> dates = generateListOfDates(subsetBeginningDate, subsetEndDate, numberRepeats, getNumberOfDaysRepeatsAreGeneratedAhead() != 0, false);
////        Date date = null;
//        if (!dates.isEmpty()) {
//            return dates.get(0);
//        }
//        return null;
//    }
//</editor-fold>
    private void checkRefs() {
        List<RepeatRuleObjectInterface> undoneList = getListOfUndoneInstances();
//        for (Item item:(List<Item>)undoneList) {
        for (RepeatRuleObjectInterface p : undoneList) {
            if (p instanceof Item) {
                if (((Item) p).getObjectIdP() == null) {
                    ASSERT.that(true, "error");
                }
            } else if (p instanceof WorkSlot) {
                if (((WorkSlot) p).getObjectIdP() == null) {
                    ASSERT.that(true, "error");
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public RepeatRuleObjectInterface updateRepeatInstancesOnDoneCancelOrDeleteOLD(RepeatRuleObjectInterface item) {
//
//        List<RepeatRuleObjectInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
//        updateLatestDateCompletedOrCancelled(item.getRepeatStartTime(true));
//        ASSERT.that(repeatInstanceItemList.contains(item), "Error: " + item + " not in list of already generated repeat instances");
////        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
//        repeatInstanceItemList.remove(item);
//
//        RepeatRuleObjectInterface repeatInstance = null;
//        Date nextRepeatTime = getNextDate(item.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE)); //get next date
//        if (nextRepeatTime != null) {
//            repeatInstance = item.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            item.insertIntoListAndSaveListAndInstance(repeatInstance); //insert new created instance in appropriate list and save it
//            repeatInstanceItemList.add(repeatInstance);
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//            Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//        } else { // no more repeat instances to handle
////            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
//        }
//        setListOfUndoneRepeatInstances(repeatInstanceItemList);
//        DAO.getInstance().save(this);
//        return repeatInstance;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns true if repeatInstance is in the list of Active (Undone) repeat
     * instances, or if this is a new rule (no generated instances yet). Used to
     * test eg if the repeatRule of an already done (and therefore disconnected
     * from the repeatRule) task is being attempted to edit.
     *
     * @param repeatInstance
     * @return
     */
//    private boolean isRepeatInstanceInListOfActiveInstancesXXX(RepeatRuleObjectInterface repeatInstance) {
////        return getListOfUndoneRepeatInstances().contains(repeatInstance)
////                || (getTotalNumberOfInstancesGeneratedSoFar() == 0 && getListOfUndoneRepeatInstances().isEmpty());
//        List<RepeatRuleObjectInterface> listOfUndone = getListOfUndoneInstances();
//        boolean contains = listOfUndone.contains(repeatInstance);
////        assert getTotalNumberOfInstancesGeneratedSoFar() != 0 || getListOfUndoneRepeatInstances().isEmpty() : "both should always be";
////        ASSERT.that(!(getListOfUndoneRepeatInstances().isEmpty()) || getTotalNumberOfInstancesGeneratedSoFar() == 0 , "if empty, counter should be 0");
//        boolean firstTime = getObjectIdP() == null; //getTotalNumberOfInstancesGeneratedSoFar() == 0 && getListOfUndoneRepeatInstances().isEmpty());
////        boolean repeatFromCompleted = getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE;
//        return contains || firstTime;
////        return getListOfUndoneRepeatInstances().contains(repeatInstance);
//    }
//</editor-fold>
    /**
     * can the repeatRule be edited for this object?
     *
     * @param repeatInstance
     * @return explanation string if rule cannot be edited, otherwise null <=>
     * OK to edit
     */
    public boolean canRepeatRuleBeEdited(RepeatRuleObjectInterface repeatInstance) {
//        List<RepeatRuleObjectInterface> listOfUndone = getListOfUndoneInstances();
//        boolean contains = listOfUndone.contains(repeatInstance);
//        boolean firstTime = getObjectIdP() == null; //(getTotalNumberOfInstancesGeneratedSoFar() == 0 && getListOfUndoneRepeatInstances().isEmpty());
////        boolean repeatFromCompleted = getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE;
//        return contains || firstTime; // || repeatFromCompleted;
        return true; //repeatInstance instanceof Item; //UI: now OK to edit repeatRules even from complated tasks, TODO!!!! support this for workslots
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateRepeatInstancesOnDoneCancelOrDeleteOLD2(RepeatRuleObjectInterface repeatRuleObject) {
//        RepeatRule repeatRule = getRepeatRule();
//        List<RepeatRuleObjectInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
//        int repeatType = getRepeatType();
//        Date nextRepeatTime;
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (repeatType == REPEAT_TYPE_FROM_COMPLETED_DATE) {
//////            nextRepeatTime = getNextCompletedFromDate(repeatRule, repeatRuleObject);
////            nextRepeatTime = getNextCompletedFromDate(repeatRule, repeatRuleObject);
////        } else if (repeatType == REPEAT_TYPE_FROM_DUE_DATE) {
////            //remove the just Completed item *before* generating new ones to ensure that the completed instance is missing in the count of items
////            ASSERT.that(repeatInstanceItemList.contains(repeatRuleObject), "updateRepeatInstancesOnDone: RepeatInstance " + repeatRuleObject + " NOT in list of active repeat instances (list=" + repeatInstanceItemList + ")");
//////            nextRepeatTime = removeNextDate(repeatRule); //get next date
////            nextRepeatTime = getNextDate(repeatRule, true); //get next date
////        } else {
////            nextRepeatTime = null;
////        }
////</editor-fold>
//        nextRepeatTime = getNextDate(repeatRuleObject.getRepeatStartTime(repeatType == REPEAT_TYPE_FROM_COMPLETED_DATE)); //get next date
//
//        if (nextRepeatTime == null) { // no more repeat instances to handle
//            repeatInstanceItemList.remove(repeatRuleObject);
////            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
//        } else {
//            RepeatRuleObjectInterface repeatInstance = repeatRuleObject.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            //insert new created instance in appropriate list
//            repeatRuleObject.insertIntoListAndSaveListAndInstance(repeatInstance); //save udpated list
//            repeatInstanceItemList.add(repeatInstance);
//            Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//        }
//        setListOfUndoneRepeatInstances(repeatInstanceItemList);
//        DAO.getInstance().save(this);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateRepeatInstancesOnDoneCancelOrDeleteOLD(RepeatRuleObjectInterface repeatRuleObject) {
////        updateRepeatInstancesOnDone(repeatRuleObject, repeatRuleObject.getInsertNewRepeatInstancesIntoList(), MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : repeatRuleObject.getInsertNewRepeatInstancesIntoList().size());
////    }
////
////    private void updateRepeatInstancesOnDone(RepeatRuleObjectInterface repeatRuleObject, List listForNewCreatedRepeatInstances, int insertIndex) {
////        RepeatRule repeatRule = getRepeatRule();
//        List<RepeatRuleObjectInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
//        //remove the just Completed item *before* generating new ones to ensure that the completed instance is missing in the count of items
//        ASSERT.that(repeatInstanceItemList.contains(repeatRuleObject), "updateRepeatInstancesOnDone: RepeatInstance " + repeatRuleObject + " NOT in list of active repeat instances (list=" + repeatInstanceItemList + ")");
////        repeatInstanceItemList.remove(repeatRuleObject); //if repeatRuleObject is still there after regeneration??, then remove it
////        RepeatRuleObjectInterface repeatInstance;
////            Date nextDate = datesList.takeNext();
////        List<Date> datesList = getDatesList();
////        Date nextRepeatTime = removeNextDate(repeatRule); //get next date
////        Date nextRepeatTime = getNextDate(repeatRule, true); //get next date
//        Date nextRepeatTime = null; //getNextDate(repeatRule, true); //get next date
//        if (nextRepeatTime == null) {
//            repeatInstanceItemList.remove(repeatRuleObject);
////            this.delete(); //no more repeat instances to handle, delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if necessary)
//        } else {
//            RepeatRuleObjectInterface repeatInstance = repeatRuleObject.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            //insert new created instance in appropriate list
//            repeatRuleObject.insertIntoListAndSaveListAndInstance(repeatInstance);
////            listForNewCreatedRepeatInstances.add(insertIndex, repeatInstance);
////            List<RepeatRuleObjectInterface> repeatInstanceItemList = getAllActiveRepeatInstances();
//            repeatInstanceItemList.add(repeatInstance);
////            setDatesList(datesList); //save udpated list
//            Log.p("**new repeat instance generated = " + repeatInstance);
//        }
//        setListOfUndoneRepeatInstances(repeatInstanceItemList);
//        DAO.getInstance().save(this);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void generateRepeatInstancesOLD(RepeatRuleObjectInterface repeatRuleOriginator, ItemList motherListForNewInstances) {
//        datesList = new DateBuffer(); //
//        Vector newRepeatInstanceList = new Vector(); //hold new created instances
//        newRepeatInstanceList.addElement(repeatRuleOriginator); //always add the originator
//        RepeatRuleObjectInterface repeatInstance;
//        long now = MyDate.getNow();
//        long nextRepeatTime;
//        while (datesList.hasMoreDates()
//                && ((getNumberFutureRepeatsToGenerateAhead() != 0 && newRepeatInstanceList.size() < getNumberFutureRepeatsToGenerateAhead()) //+1 since we want to show both originator and numberOfFuture instances at the same time
//                || (getNumberOfDaysRepeatsAreGeneratedAhead() != 0 && (datesList.getNextDate() <= now + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS) //we generate some days ahead, generate for all datea that are less than the number of days we generate ahead
//                ))) {
//            nextRepeatTime = datesList.takeNext().getTime(); //remove next date from list
//            if (Settings.getInstance().reuseExistingInstancesAfterChangingRepeatRule()
//                    && (repeatInstance = repeatInstanceItemList.getAndRemoveRepeatInstanceWithDate(nextRepeatTime)) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these)
//                //nothing done about the existing repeatInstance since it is simply left in its place in the original list
//                Log.p("**repeat instance reused = " + repeatInstance);
//            } else { //no previous instance with same date exists, create a new one
//                repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
//                //insert new created instance in appropriate list
//                motherListForNewInstances.addItemAtSpecialPosition(repeatInstance, null, Settings.getInstance().insertPositionOfInitialRepeatInstances.getInt());
//                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
//                Log.p("**new repeat instance generated = " + repeatInstance);
//            }
//            newRepeatInstanceList.addElement(repeatInstance); //add all instances (reused or new)
//        }
//        repeatInstanceItemList.remove(repeatRuleOriginator); //remove repeatRuleOriginator since it is going to be reused (avoid it being deleted with other obsolete instances)
//        repeatInstanceItemList.clear(); //delete any&all remaining items (since they are not generated by the new repeatRule, and thus not reused)
//        //transfer all repeatinstances to list:
//        Object temp;
//        while (!newRepeatInstanceList.isEmpty()) {
//            temp = newRepeatInstanceList.elementAt(0);
//            newRepeatInstanceList.removeElementAt(0);
//            repeatInstanceItemList.add(temp);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * creates (from scratch) a new set of repeat instances, possibly reusing
//     * previously generated instances (in case they were edited manually).
//     * Called whenever a repeat rule has been created or edited.
//     */
//    private void generateRepeatInstancesFirstTimeXXX(RepeatRuleObjectInterface repeatRuleOriginator, List listForNewCreatedRepeatInstances, int insertIndex) {
//        Vector newRepeatInstanceList = new Vector(); //hold new created instances
////        List<RepeatRuleObjectInterface> repeatInstanceItemList = getRepeatInstanceItemList();
//        newRepeatInstanceList.addElement(repeatRuleOriginator); //always add the originator
//        RepeatRuleObjectInterface repeatInstance;
//        Date nextRepeatTime;
////        List<Long> datesList = getDatesList();
//        if (useNumberFutureRepeatsToGenerateAhead()) {
//            int numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead();
//            int instancesGeneratedCount = 0;
//            for (instancesGeneratedCount = 0; instancesGeneratedCount < numberInstancesToGenerate; instancesGeneratedCount++) {
//                nextRepeatTime = removeNextDate();
//                if (nextRepeatTime == null) {
//                    break;
//                } else {
//                    repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
//                    //insert new created instance in appropriate list
//                    listForNewCreatedRepeatInstances.add(insertIndex + instancesGeneratedCount, repeatInstance); //+count => insert each instance *after* the previously inserted one
////                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
//                }
//            }
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + instancesGeneratedCount - 1); //count-1 => if we break out of the for loop, count will be 1 too high
//        } else {
//            //generate instances for a certain amount of time ahead
//            int instancesGeneratedCount = 0;
//            long now = System.currentTimeMillis(); //MyDate.getNow();
//            int numberOfDaysRepeatsAreGeneratedAhead = getNumberOfDaysRepeatsAreGeneratedAhead();
//            while ((nextRepeatTime = removeNextDate()) != null && (nextRepeatTime.getTime() <= now + numberOfDaysRepeatsAreGeneratedAhead)) {
//                repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
//                //insert new created instance in appropriate list
//                listForNewCreatedRepeatInstances.add(insertIndex + instancesGeneratedCount, repeatInstance); //+count => insert each instance *after* the previously inserted one
//                instancesGeneratedCount++;
//            }
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + instancesGeneratedCount);
//        }
////        setDatesList(datesList);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * will return a repeatInstance for the given date. Either reused from
     * oldRepeatInstanceList (as long as there are instances to reuse) or
     * created as a copy of the originator. Returns null if there
     *
     * @param repeatRuleOriginator
     * @param oldRepeatInstanceList
     * @param nextRepeatTime
     * @return
     */
//    private static RepeatRuleObjectInterface reuseOrMakeNextRepeatInstanceXXX(RepeatRuleObjectInterface repeatRuleOriginator,
//            List<RepeatRuleObjectInterface> oldRepeatInstanceList, Date nextRepeatTime) {
//
//        if (false && nextRepeatTime == null) {
//            ASSERT.that(false, "shouldn't happen");
//            return null;
//        }
//
//        RepeatRuleObjectInterface repeatInstance;
//
//        if (MyPrefs.repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule.getBoolean() && oldRepeatInstanceList.size() >= 1) { //reuse already generated instances
//            repeatInstance = oldRepeatInstanceList.remove(0);
//            if (false && repeatInstance.equals(repeatRuleOriginator)) {
//                return repeatInstance; //don't alter the originator==owner
//            }            //no need to add them to any lists, they are simply left where they already are
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (false) {
////                if (repeatInstance.equals(repeatRuleOriginator)) {
//////                newRepeatInstanceList.add(repeatRuleOriginator); //don't change the date for the originator //NO, no matter which instance with the RepeatRule is changed, update/reuse all current instances (otherwise the instances might become inconsistent w the rule def)
////                } else {
////                    repeatInstance.setRepeatStartTime(nextRepeatTime); //upate repeat time
//////                newRepeatInstanceList.add(oldInstance);
////                }
////            }
////</editor-fold>
////            repeatInstance.setRepeatStartTime(nextRepeatTime); //upate repeat time
//            repeatInstance.updateRepeatInstanceRelativeDates(nextRepeatTime); //upate repeat time
//        } else {
//            repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
////            repeatInstance.setRepeatRuleForRepeatInstance(this); //link back to repeat rule //NO, done in createRepeatCopy
////<editor-fold defaultstate="collapsed" desc="comment">
////insert new created instance in appropriate list
////DONE!!! how to insert 'naturally' into a list? In order generated (first dates first), either at beginning/end of list, or right after originator?? -> handled in
////                    listForNewCreatedRepeatInstances.addToList(insertIndex + instancesGeneratedCount, (ItemAndListCommonInterface) repeatInstance); //+count => insert each instance *after* the previously inserted one
////                    repeatRuleOriginator.getInsertNewRepeatInstancesIntoList().add(repeatInstance);
////            repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
////                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
////</editor-fold>
//        }
//        return repeatInstance;
//    }
//</editor-fold>
    /**
     * if there is an instance in to reuse, return that, else return null
     *
     * @param repeatRuleOriginator
     * @param oldRepeatInstanceList
     * @param nextRepeatTime
     * @return
     */
//    private static RepeatRuleObjectInterface reuseNextRepeatInstance(RepeatRuleObjectInterface repeatRuleOriginator, List<RepeatRuleObjectInterface> oldRepeatInstanceList, Date nextRepeatTime) {
//    private static Item reuseNextRepeatItemXXX(List<Item> oldRepeatInstanceList, Date nextRepeatTime) {
//
//        Item repeatInstance = null;
//
//        if (MyPrefs.repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule.getBoolean() && oldRepeatInstanceList.size() >= 1) { //reuse already generated instances
//            repeatInstance = oldRepeatInstanceList.remove(0);
//            if (MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances.getBoolean()) {
//                repeatInstance.updateRelativeDates(nextRepeatTime); //upate repeat time
//            }
//        }
//        return repeatInstance;
//    }
    private static Item reuseOrMakeNextRepeatItem(List<Item> oldRepeatInstanceList, Date nextRepeatTime, Item instanceToCreateCopiesFrom) {
        Item newRepeatInstance = null;

        if (MyPrefs.repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule.getBoolean() && oldRepeatInstanceList.size() >= 1) { //reuse already generated instances
            newRepeatInstance = oldRepeatInstanceList.remove(0);
            if (MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances.getBoolean()) {
//                repeatInstance.updateRelativeDates(nextRepeatTime); //upate repeat time //NOW done after calling reuseNextRepeatItem
            }
        }
//        if (newRepeatInstance != null) {
//            newRepeatInstance.updateRelativeDates(nextRepeatTime);
//        } else {
//            newRepeatInstance = (Item) instanceToCreateCopiesFrom.createRepeatCopy(nextRepeatTime); //create next instance
//
//        } //else: if newRepeatInstance is reused, there's no need to insert it into the owner list, it is already there
        return newRepeatInstance;
    }

    private static RepeatRuleObjectInterface reuseNextRepeatInstance(List<RepeatRuleObjectInterface> oldRepeatInstanceList, Date nextRepeatTime) {

        RepeatRuleObjectInterface repeatInstance = null;

        if (MyPrefs.repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule.getBoolean() && oldRepeatInstanceList.size() >= 1) { //reuse already generated instances
            repeatInstance = oldRepeatInstanceList.remove(0);
            if (MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances.getBoolean()) {
                repeatInstance.updateRelativeDates(nextRepeatTime); //upate repeat time
            }
        }
        return repeatInstance;
    }

//    private Date calcSubsetBeginDateXXX() {
//        return new Date(Math.max(getLastGeneratedDateD().getTime(), getSpecifiedStartDateD().getTime())); //get the last date we've generated so far, Math.min since endDate may get set to larger than 
//    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void updateRepeatInstancesWhenItemIsDoneOrCancelled(RepeatRuleObjectInterface repeatRuleOriginator, ItemAndListCommonInterface listForNewCreatedRepeatInstances, int insertIndex) {
    //        assert false;
    //    }
    //    public void updateRepeatInstancesWhenRuleWasCreatedOrEdited(RepeatRuleObjectInterface repeatRuleOriginator, ItemAndListCommonInterface listForNewCreatedRepeatInstances, int insertIndex) {
    //
    //    }
    //</editor-fold>
    /**
     * creates (from scratch) a new set of repeat instances, possibly reusing
     * previously generated instances (in case they were edited manually).
     * Called whenever a repeat rule has been created or edited.
     *
     * @param repeatRuleOriginator
     */
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void updateRepeatInstancesWhenRuleWasCreatedOLD(RepeatRuleObjectInterface repeatRuleOriginator) {
    //        assert (getTotalNumberOfInstancesGeneratedSoFar() == 0);
    //
    //        if (getRepeatType() != REPEAT_TYPE_FROM_DUE_DATE) {
    //            return;
    //        }
    //        ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
    //
    ////        ASSERT.that(newRepeatInstanceList.size() == 0, "newRepeatInstanceList not empty");
    //        newRepeatInstanceList.add(repeatRuleOriginator); //add repeatRuleOriginator to instanceList since instanceList is used to check if an item is still allowed to edit the rule
    //
    //        Date startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    //
    //        List<Date> dates;
    //        if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    //            dates = generateListOfDates(startRepeatFromTime, null,
    //                    getNumberFutureRepeatsToGenerateAhead());
    //        } else {
    //            dates = generateListOfDates(startRepeatFromTime,
    //                    new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS),
    //                    -1);
    //        }
    //
    ////        List<Date> dates = generateListOfDates(startRepeatFromTime, subsetEndDate, numberInstancesToGenerate);
    //        RepeatRuleObjectInterface newRepeatInstance;
    //        RepeatRuleObjectInterface repeatRef = repeatRuleOriginator; //used to insert multiple instances one after the other
    //        Date nextRepeatTime;
    //        for (int i = 0, size = dates.size(); i < size; i++) {
    //            nextRepeatTime = dates.get(i);
    //            newRepeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
    ////            repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
    //            repeatRef.insertIntoListAndSaveListAndInstance(newRepeatInstance); //insert new after the previously generated/inserted
    //            repeatRef = newRepeatInstance;
    //            newRepeatInstanceList.add(newRepeatInstance);
    //            setLastGeneratedDate(nextRepeatTime); //save last date generated
    //        }
    //
    //        setListOfUndoneRepeatInstances(newRepeatInstanceList);
    //        setTotalNumberOfInstancesGeneratedSoFar(newRepeatInstanceList.size() - 1); //-1: don't include originator
    //        DAO.getInstance().save(this);
    //    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void updateRepeatInstancesWhenRuleWasEditedOLD(RepeatRuleObjectInterface repeatRuleOriginator) {
    ////<editor-fold defaultstate="collapsed" desc="comment">
    //        /**
    //         * //algorithm: ~removeFromCache (reuse existing rule, to avoid
    //         * creating/deleting when edited) //get any already created repeat
    //         * instances //create dates for new/edited rule //for as many instances
    //         * as should be generated (encapsulate this! w moreInstances() and
    //         * ruleFinished(), moreInstances based on either #futureInstances vs
    //         * length of list or #daysAhead vs lastDayInList) //if an existing
    //         * instance exists, use it and set the date (whether new date is same or
    //         * not) //if no more instances, generate one //at the end, if there are
    //         * still existing instances, delete them (or cancel if actual!=0)
    //         *
    //         */
    ////</editor-fold>
    //        assert (true || getTotalNumberOfInstancesGeneratedSoFar() > 0); // ">0" - not the case if only one simultaneous instance
    //
    //        List<RepeatRuleObjectInterface> oldRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
    //        if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) { //handle case where a rule is switched from repeat from due, to repeat from completed
    //            List list = getListOfUndoneRepeatInstances();
    //            list.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list so it won't get deleted
    //            while (list.size() > 0) { //delete all already generated instances
    //                ParseObject repeatRuleObject = (ParseObject) list.remove(0);
    //                DAO.getInstance().delete(repeatRuleObject);
    //            }
    //            //reset all counters in case the rule is later switched back to REPEAT_TYPE_FROM_DUE_DATE
    //            setListOfUndoneRepeatInstances(null);
    //            setTotalNumberOfInstancesGeneratedSoFar(0);
    //            setLastGeneratedDate(null);
    //            setLatestDateCompletedOrCancelled(null);
    //            DAO.getInstance().save(this);
    //        } else { //getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE
    //            ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
    //            setLastGeneratedDate(null); //reset the last date
    //            RepeatRuleObjectInterface repeatInstance;
    //            int oldTotalNumberInstancesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar(); //- oldRepeatInstanceList.size()-1; //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later), -1: alwlays remove the initial
    //            int oldNumberOfUndoneRepeatInstances = oldRepeatInstanceList.size();
    //
    //            //not the first time, so start repetition from the first still active instance
    //            ASSERT.that(false, "shouldn't happen?! repeatRule=" + this + ", item=" + repeatRuleOriginator);
    //
    //            Date startRepeatFromTime = getLatestDateCompletedOrCancelled(); //Strart repetition from the last
    //            ASSERT.that(oldRepeatInstanceList.size() == 0 || oldRepeatInstanceList.contains(repeatRuleOriginator), "repeatRuleOriginator must always be getListOfUndoneRepeatInstances() unless empty, repeatRule=" + this + ", repeatRuleOriginator" + repeatRuleOriginator);
    //
    //            List<Date> dates;
    //            if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    //                dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead() + 1); //need to *re-*generated the full number of dates if possible (including the originator)
    //            } else {
    //                dates = generateListOfDates(startRepeatFromTime,
    //                        new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS),
    //                        -1);
    //            }
    //
    //            for (int i = 0, size = dates.size(); i < size; i++) {
    //                Date nextRepeatTime = dates.get(i);
    //                repeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldRepeatInstanceList, nextRepeatTime);
    //                repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
    //                newRepeatInstanceList.add(repeatInstance);
    //                setLastGeneratedDate(nextRepeatTime); //save last date generated
    //            }
    //
    //            //delete any 'left over' instances //TODO check that deleting them doesn't affect
    //            while (oldRepeatInstanceList.size() > 0) {
    //                RepeatRuleObjectInterface obsoleteInstance = oldRepeatInstanceList.remove(0);
    //                if (obsoleteInstance instanceof ParseObject) {
    //                    if (obsoleteInstance instanceof Item && ((Item) obsoleteInstance).getActualEffort() != 0) { //for Items, not WorkSlots
    //                        ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //Cancel instead of delete if time has been registered (to avoid losing it)
    //                        DAO.getInstance().save((Item) obsoleteInstance); //must save it
    //                    } else {
    ////                obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
    //                        DAO.getInstance().delete((ParseObject) obsoleteInstance);   //TODO!!!! check that this deletes it in all lists, categories etc!
    //                    }
    //                } //else: not saved to Parse
    //            }
    //            setListOfUndoneRepeatInstances(newRepeatInstanceList);
    ////        oldTotalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
    //            assert oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar() : "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule";
    //            setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + newRepeatInstanceList.size());
    //            DAO.getInstance().save(this);
    //        }
    //    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void updateRepeatInstancesWhenRuleWasCreatedOrEditedPrev(RepeatRuleObjectInterface repeatRuleOriginator) {
    ////<editor-fold defaultstate="collapsed" desc="comment">
    //        /**
    //         * //algorithm: ~removeFromCache (reuse existing rule, to avoid
    //         * creating/deleting when edited) //get any already created repeat
    //         * instances //create dates for new/edited rule //for as many instances
    //         * as should be generated (encapsulate this! w moreInstances() and
    //         * ruleFinished(), moreInstances based on either #futureInstances vs
    //         * length of list or #daysAhead vs lastDayInList) //if an existing
    //         * instance exists, use it and set the date (whether new date is same or
    //         * not) //if no more instances, generate one //at the end, if there are
    //         * still existing instances, delete them (or cancel if actual!=0)
    //         *
    //         */
    ////</editor-fold>
    //        assert (true || getTotalNumberOfInstancesGeneratedSoFar() > 0); // ">0" - not the case if only one simultaneous instance
    //
    //        if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) { //handle case where a rule is switched from repeat from due, to repeat from completed
    //            //Called either on very first time (and should do nothing other than add originator to listOfUndone), or when rule is changed from Due to Completed
    //            List undoneList = getListOfUndoneRepeatInstances();
    //            ASSERT.that(undoneList.size() == 0 || undoneList.contains(repeatRuleOriginator), "getListOfUndoneRepeatInstances does not contain originator=" + repeatRuleOriginator + " list=" + undoneList);
    //            int instancesToBeDeleted = undoneList.size();
    //            if (instancesToBeDeleted > 0) {
    //                undoneList.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list so it won't get deleted
    //                instancesToBeDeleted = undoneList.size();
    //                while (undoneList.size() > 0) { //delete all already generated instances
    //                    ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
    //                    DAO.getInstance().delete(repeatRuleObject);
    //                }
    //            }
    //            undoneList.add(repeatRuleOriginator); //add, or re-add
    //            setListOfUndoneRepeatInstances(undoneList);
    //            //reset all counters in case the rule is later switched back to REPEAT_TYPE_FROM_DUE_DATE
    //            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() - instancesToBeDeleted);
    //            if (false) {
    //                setLatestDateCompletedOrCancelled(null); //no need to reset this
    //            }//            setLastGeneratedDateIfGreaterThanLastDate(null);
    //            setLastGeneratedDate(repeatRuleOriginator.getRepeatStartTime(false)); //set LastGeneratedDate to due date of the originator
    //
    //            DAO.getInstance().save(this);
    //        } else { //getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE
    //            //new rule, changed REPEAT_TYPE_FROM_DUE_DATE, or rule was changed from REPEAT_TYPE_FROM_COMPLETED_DATE to REPEAT_TYPE_FROM_DUE_DATE
    //            List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
    //
    //            ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
    //            setLastGeneratedDate(null); //when rule is edited, and recalculated, first reset the previous value of lastDateGenerated
    //            RepeatRuleObjectInterface repeatInstance;
    //            int oldTotalNumberInstancesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar(); //- oldRepeatInstanceList.size()-1; //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later), -1: alwlays remove the initial
    //            int oldNumberOfUndoneRepeatInstances = oldUndoneRepeatInstanceList.size();
    //
    //            //first time we generate instances for a rule, we need to add the originator and only generate additional instances
    //            //next time all dates mu be generated and the originator may change date
    ////            boolean firstTime
    ////                    = oldTotalNumberInstancesGeneratedSoFar == 0 || oldUndoneRepeatInstanceList.size() == 0
    ////                    || (oldUndoneRepeatInstanceList.size() == 1 && oldUndoneRepeatInstanceList.contains(repeatRuleOriginator));
    //////                    || !oldUndoneRepeatInstanceList.contains(repeatRuleOriginator);
    //            boolean firstTime = oldUndoneRepeatInstanceList.size() == 0; // || !oldUndoneRepeatInstanceList.contains(repeatRuleOriginator);
    //            if (oldUndoneRepeatInstanceList.size() == 1) {
    //                oldUndoneRepeatInstanceList.remove(repeatRuleOriginator); //in case we're switching rule from Completed to Due
    //            }
    //            ASSERT.that(newRepeatInstanceList.size() == 0, "no instances should be");
    //            if (firstTime) {
    //                newRepeatInstanceList.add(repeatRuleOriginator);
    //                oldNumberOfUndoneRepeatInstances++; //add 1 to compensentae for adding originator (when deducting newRepeatInstanceList.size()
    //            }
    //
    //            //not the first time, so start repetition from the first still active instance
    //            ASSERT.that(true, "shouldn't happen?! repeatRule=" + this + ", item=" + repeatRuleOriginator);
    //
    //            Date startRepeatFromTime;
    //            if (oldTotalNumberInstancesGeneratedSoFar == 0) {
    //                startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    //            } else {
    //                startRepeatFromTime = getLatestDateCompletedOrCancelled(); //Re-start repeat from the last date a task was cancelled/done
    //            }
    ////            ASSERT.that(oldRepeatInstanceList.size() == 0 || oldRepeatInstanceList.contains(repeatRuleOriginator), "repeatRuleOriginator must always be getListOfUndoneRepeatInstances() unless empty, repeatRule=" + this + ", repeatRuleOriginator" + repeatRuleOriginator);
    //
    //            List<Date> dates;
    //            if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    //                dates = generateListOfDates(startRepeatFromTime, null,
    //                        getNumberFutureRepeatsToGenerateAhead() + (firstTime ? 0 : 1)); //1: need to *re-*generate the full number of dates if possible (including possibly the originator)
    //            } else {
    //                dates = generateListOfDates(startRepeatFromTime,
    //                        new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS),
    //                        -1);
    //            }
    //
    //            Date nextRepeatTime = null;
    //            for (int i = 0, size = dates.size(); i < size; i++) {
    //                nextRepeatTime = dates.get(i);
    //                repeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
    //                repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
    //                newRepeatInstanceList.add(repeatInstance);
    ////                setLastGeneratedDate(nextRepeatTime); //save last date generated
    //            }
    //            if (nextRepeatTime != null) {
    //                setLastGeneratedDate(nextRepeatTime); //save last date generated
    //            }
    //
    //            //delete any 'left over' instances //TODO check that deleting them doesn't affect
    //            while (oldUndoneRepeatInstanceList.size() > 0) {
    //                RepeatRuleObjectInterface obsoleteInstance = oldUndoneRepeatInstanceList.remove(0);
    //                if (obsoleteInstance instanceof ParseObject) {
    //                    if (obsoleteInstance instanceof Item && ((Item) obsoleteInstance).getActualEffort() != 0) { //for Items, not WorkSlots
    //                        ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //Cancel instead of delete if time has been registered (to avoid losing it)
    //                        DAO.getInstance().save((Item) obsoleteInstance); //must save it
    //                    } else {
    ////                obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
    //                        DAO.getInstance().delete((ParseObject) obsoleteInstance);   //TODO!!!! check that this deletes it in all lists, categories etc!
    //                    }
    //                } //else: not saved to Parse
    //            }
    //            setListOfUndoneRepeatInstances(newRepeatInstanceList);
    ////        oldTotalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
    //            assert oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar() : "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule";
    //            setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + newRepeatInstanceList.size());
    //            DAO.getInstance().save(this);
    //        }
    //    }
    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * hard-delete all instances in undoneList in background (does not change
//     * undoneList)
//     *
//     * @param undoneList
//     * @return
//     */
//    private void deleteUnneededWorkSlotInstancesXXX(List<ItemAndListCommonInterface> undoneList) {
////        List<ItemAndListCommonInterface> deleteList = new ArrayList(undoneList);
//        List deleteList = new ArrayList(undoneList);
////<editor-fold defaultstate="collapsed" desc="comment">
////                    while (undoneList.size() > 0) { //delete all already generated instances
////                ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
////                DAO.getInstance().deleteAndWait(repeatRuleObject); //wait to ensure instances have disappeared from list
////                if (repeatRuleObject instanceof Item) {
////                    ((Item) repeatRuleObject).hardDelete();
////                } else {
////                    ((WorkSlot) repeatRuleObject).hardDelete();
////                }
////            }
////</editor-fold>
////        for (Object p : deleteList) {
//////            ((ItemAndListCommonInterface) p).softDelete();
////        }
//        DAO.getInstance().deleteAll(deleteList, true, false); //hard-delete
////                DAO.getInstance().deleteBatch((List<ParseObject>)deleteList);
////        DAO.getInstance().deleteBatch(deleteList);
////        DAO.getInstance().deleteBatch(deleteList);
//    }
//</editor-fold>
    /**
     * delete any 'left over' instances //TODO check that deleting them doesn't
     * affect. Returns list of updated owners (that will need saving)
     *
     * @param oldUndoneRepeatInstanceList
     */
//    private static Set<ItemAndListCommonInterface> deleteUnneededItemOrWorkSlotInstances(List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList) {
////        Set<ItemAndListCommonInterface> updatedOwners = new HashSet<>();
//        Set<ItemAndListCommonInterface> updatedParseObjects = new HashSet<>();
//        while (oldUndoneRepeatInstanceList.size() > 0) {
//            RepeatRuleObjectInterface obsoleteInstance = oldUndoneRepeatInstanceList.remove(0);
////            if (obsoleteInstance instanceof Item) {
//            if (obsoleteInstance instanceof Item) {
//                if (((Item) obsoleteInstance).getActualForTaskItself() != 0
//                        && MyPrefs.repeatCancelNotDeleteSuperflousInstancesWithActualRecorded.getBoolean()) { //for Items, not WorkSlots
//                    ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //UI: Cancel instead of delete if time has been registered (to avoid losing it)
//                    ItemAndListCommonInterface oldOwner = ((Item) obsoleteInstance).removeFromOwner();
//                    if (true || !updatedParseObjects.contains(oldOwner)) { //true -> not needed to check with a set
//                        updatedParseObjects.add(oldOwner);
//                    }
//                    DAO.getInstance().saveNew((Item) obsoleteInstance); //must save it, but do batched with other updates (in method calling deleteSuperfluousRepeatInstances())
//                } else {
//                    ItemAndListCommonInterface oldOwner = ((ItemAndListCommonInterface) obsoleteInstance).removeFromOwner();
//                    if (true || !updatedParseObjects.contains(oldOwner)) {
//                        updatedParseObjects.add(oldOwner);
//                    }
////                DAO.getInstance().delete((ItemAndListCommonInterface) obsoleteInstance, true, false);   //DONE!!!! check that this deletes it in all lists, categories etc! IT DOES (via Item.delete())
//                    DAO.getInstance().deleteLater((ParseObject) obsoleteInstance, true);   //DONE!!!! check that this deletes it in all lists, categories etc! IT DOES (via Item.delete())
//                }
//            } else if (obsoleteInstance instanceof WorkSlot) {
//                ItemAndListCommonInterface oldOwner = ((WorkSlot) obsoleteInstance).removeFromOwner();
//                updatedParseObjects.add(oldOwner);
////                DAO.getInstance().delete((ParseObject) obsoleteInstance, true, false);   //DONE!!!! check that this deletes it in all lists, categories etc! IT DOES (via Item.delete())
//                DAO.getInstance().deleteLater((ParseObject) obsoleteInstance, true);   //DONE!!!! check that this deletes it in all lists, categories etc! IT DOES (via Item.delete())
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            } else if (obsoleteInstance instanceof WorkSlot) {
////                    ItemAndListCommonInterface oldOwner = ((WorkSlot) obsoleteInstance).removeFromOwner();
////                    DAO.getInstance().delete((ParseObject) obsoleteInstance);   //TODO!!!! check that this deletes it in all lists, categories etc!
////            } else if (obsoleteInstance instanceof ParseObject) {
//////                obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
////                DAO.getInstance().delete((ParseObject) obsoleteInstance);   //TODO!!!! check that this deletes it in all lists, categories etc!
////            } else if (obsoleteInstance instanceof WorkSlot) {
////
////            } else{
////                ASSERT.that("error - deleteSuperfluousRepeatInstances called with other elements than Item, list=" + oldUndoneRepeatInstanceList);
////            }
////else: not saved to Parse
////</editor-fold>
//        }
//        return updatedParseObjects;
//    }
    private static Set<ItemAndListCommonInterface> deleteUnneededItemInstances(List<Item> oldUndoneRepeatInstanceList) {
//        Set<ItemAndListCommonInterface> updatedOwners = new HashSet<>();
        Set<ItemAndListCommonInterface> updatedParseObjects = new HashSet<>();
        while (oldUndoneRepeatInstanceList.size() > 0) {
            Item obsoleteInstance = oldUndoneRepeatInstanceList.remove(0);
//            if (obsoleteInstance instanceof Item) {
            if ((obsoleteInstance).getActualForTaskItself() != 0
                    && MyPrefs.repeatCancelNotDeleteSuperflousInstancesWithActualRecorded.getBoolean()) { //for Items, not WorkSlots
                obsoleteInstance.setStatus(ItemStatus.CANCELLED);   //UI: Cancel instead of delete if time has been registered (to avoid losing it)
                ItemAndListCommonInterface oldOwner = obsoleteInstance.removeFromOwner();
                if (true || !updatedParseObjects.contains(oldOwner)) { //true -> not needed to check with a set
                    updatedParseObjects.add(oldOwner);
                }
//                DAO.getInstance().saveNew(obsoleteInstance); //must save it, but do batched with other updates (in method calling deleteSuperfluousRepeatInstances())
                DAO.getInstance().saveToParseLater(obsoleteInstance); //must save it, but do batched with other updates (in method calling deleteSuperfluousRepeatInstances())
            } else {
                ItemAndListCommonInterface oldOwner = obsoleteInstance.removeFromOwner();
                if (true || !updatedParseObjects.contains(oldOwner)) {
                    updatedParseObjects.add(oldOwner);
                }
                DAO.getInstance().deleteLater(obsoleteInstance, true);   //DONE!!!! check that this deletes it in all lists, categories etc! IT DOES (via Item.delete())
            }
        }
        return updatedParseObjects;
    }

    private static Set<ItemAndListCommonInterface> deleteUnneededWorkSlotInstances(List<WorkSlot> oldUndoneRepeatInstanceList) {
//        Set<ItemAndListCommonInterface> updatedOwners = new HashSet<>();
        Set<ItemAndListCommonInterface> updatedParseObjects = new HashSet<>();
        while (oldUndoneRepeatInstanceList.size() > 0) {
            WorkSlot obsoleteInstance = oldUndoneRepeatInstanceList.remove(0);
            ItemAndListCommonInterface oldOwner = obsoleteInstance.removeFromOwner();
            updatedParseObjects.add(oldOwner);
            DAO.getInstance().deleteLater(obsoleteInstance, true);   //DONE!!!! check that this deletes it in all lists, categories etc! IT DOES (via Item.delete())
        }
        return updatedParseObjects;
    }

    /**
     * calculate the number of dates to calculate. Capped to total number of
     * repeats defined minus the number of repeats already generated, as well as
     * the max number of future simultaneous repeats to generate. Returns
     * Integer.MAX_VALUE if no limits is defined for number of repeats.
     *
     * @return
     */
    private int calcNumberOfRepeats(boolean ignoreMaxSimultaneous, int currentTotalNumberOfUndoneInstances) {
        //if rule is limited to number of counts, then take remaining number of repeats into account
//        int countMaxRemaining = (getNumberOfRepeats() != Integer.MAX_VALUE) ? getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar() : Integer.MAX_VALUE; //cap count number of remaining repeats, +1=compensate for the originator in list of generated
        int countMaxRemaining = (getNumberOfRepeats() != Integer.MAX_VALUE)
                ? Math.max(0, getNumberOfRepeats() - getTotalNumberOfDoneInstances() - getTotalNumberOfUndoneInstances())
                : Integer.MAX_VALUE; //cap count number of remaining repeats, +1=compensate for the originator in list of generated
//        if (getNumberSimultaneousRepeats() > 0) {

//        ASSERT.that(getNumberSimultaneousRepeats() >= 1, "ERROR getNumberSimultaneousRepeats() returns value <1 =" + getNumberSimultaneousRepeats());
//<editor-fold defaultstate="collapsed" desc="comment">
//        int nbFutureRepeatsRemaining = Integer.MAX_VALUE; //0;
//        if (getNumberSimultaneousRepeats() > 1) { //only use value if defined, otherwise numberOfDaysToGenerateAhead determines
//            nbFutureRepeatsRemaining = getNumberSimultaneousRepeats() - getListOfUndoneRepeatInstances().size(); //deduct any already generated, and still valid, instances
//            ASSERT.that(nbFutureRepeatsRemaining >= 0, "ERROR negative in calcNumberOfRepeats(): getNumberSimultaneousRepeats()=" + getNumberSimultaneousRepeats() + ", getListOfUndoneRepeatInstances().size()=" + getListOfUndoneRepeatInstances().size());
////        int countFutureRepeats = getNumberFutureRepeatsToGenerateAhead() == 0 ? Integer.MAX_VALUE
////        int count = Math.min(countMaxRemaining, countFutureRepeats); //cap count to getNumberFutureRepeatsToGenerateAhead()
////        int count = Math.min(countMaxRemaining, countFutureRepeats); //cap count to getNumberFutureRepeatsToGenerateAhead()
//        }
//</editor-fold>
//        int countSimultaneousRemaining = getNumberSimultaneousRepeats() > 1
        int countSimultaneousRemaining = Integer.MAX_VALUE;
        if (!ignoreMaxSimultaneous) {
            countSimultaneousRemaining = getNumberOfDaysRepeatsAreGeneratedAhead() == 0
                    //                ? getNumberSimultaneousRepeats() - getListOfUndoneInstances().size() //deduct any already generated, and still valid, instances
                    ? Math.max(0, getNumberSimultaneousRepeats() - currentTotalNumberOfUndoneInstances) //deduct any already generated, and still valid, instances
                    : Integer.MAX_VALUE;
        }
        int count = Math.min(countMaxRemaining, countSimultaneousRemaining);
        count = Math.max(count, 0); //if editing the repeatRule to generate less instances
        return count;
    }

    private int calcNumberOfRepeats(int currentTotalNumberOfUndoneInstances) {
        return calcNumberOfRepeats(false, currentTotalNumberOfUndoneInstances);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private int calcNumberOfRepeatsOLD() {
////        int requestedCount = numberInstancesToGenerate == -1 ? Integer.MAX_VALUE - 1 : numberInstancesToGenerate; // +1 since we need to generate the last generated date again
//        int count = Integer.MAX_VALUE;
//        int countMaxRemaining;
//        int countFutureRepeats = 0;//Integer.MAX_VALUE;
////        if (useCount()) {
//        if (getNumberOfRepeats() != Integer.MAX_VALUE) {
//            //if rule is limited to number of counts, then take remaining number of repeats into account
//            countMaxRemaining = Math.min(count, getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar()); //cap count number of remaining repeats
//            count = countMaxRemaining;
//        }
//        if (getNumberSimultaneousRepeats() != 0) {
//            countFutureRepeats = getNumberSimultaneousRepeats();
//            count = Math.min(count, countFutureRepeats); //cap count to getNumberFutureRepeatsToGenerateAhead()
//        }
//        return count;
//    }
//</editor-fold>
    /**
     * returns the last date to generate for. If
     * getNumberFutureRepeatsToGenerateAhead is defined it will return MAX_DATE
     * (since then no end date is defined), otherwise it will return the
     * smallest of
     *
     * @param getNextRepeatAfterDate
     * @return
     */
//    private Date calcSubsetEndDate(Date earliestDate) {
    private Date calcSubsetEndDate() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (useCount()) {
//new Date();
//        if (getNumberSimultaneousRepeats() != 0) {
//        if (getNumberSimultaneousRepeats() > 1) {
//        if (getNumberOfDaysRepeatsAreGeneratedAhead()==0) {
//</editor-fold>
        int numberOfDaysRepeatsAreGeneratedAhead = getNumberOfDaysRepeatsAreGeneratedAhead();
        if (true || numberOfDaysRepeatsAreGeneratedAhead == 0) { //true=> feature current de-activated!
            return new MyDate(MyDate.MAX_DATE);
        } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//            ASSERT.that(getRepeatType() != REPEAT_TYPE_FROM_DUE_DATE || getNumberOfDaysRepeatsAreGeneratedAhead() != 0,
//                    "Both getNumberFutureRepeatsToGenerateAhead() and getNumberOfDaysRepeatsAreGeneratedAhead() are 0 for RepeatRule=" + this);
//            ASSERT.that(getRepeatType() != REPEAT_TYPE_FROM_DUE_DATE || getNumberOfDaysRepeatsAreGeneratedAhead() != 0,
//                    "getNumberOfDaysRepeatsAreGeneratedAhead() is 0 for RepeatRule w type REPEAT_TYPE_FROM_DUE_DATE =" + this);
//            Date endDate = getEndDateD(); //NOT needed since the repeatRule itself uses this date as a 'hard' limit
//            ASSERT.that(numberOfDaysRepeatsAreGeneratedAhead > 0);
//            if (nbDaysToGenAhead == 0) {
//                return new Date(MyDate.MAX_DATE);
//            } else {
//</editor-fold>
            Date startDaysAheadFromDate = new MyDate(Math.max(MyDate.currentTimeMillis(), getSpecifiedStartDateZZZ().getTime())); //handle case where startDate is in the future (so > than now)
            Date lastDateForSimultRepeats
                    //                    = MyDate.getEndOfDay(new Date(MyDate.currentTimeMillis() + numberOfDaysRepeatsAreGeneratedAhead * MyDate.DAY_IN_MILLISECONDS)); //UI: if nbDays==1, we generate until end of tomorrow
                    = MyDate.getEndOfDay(new MyDate(startDaysAheadFromDate.getTime() + numberOfDaysRepeatsAreGeneratedAhead * MyDate.DAY_IN_MILLISECONDS)); //UI: if nbDays==1, we generate until end of tomorrow
//<editor-fold defaultstate="collapsed" desc="comment">
//            Date subsetEnd = new Date(Math.min(Math.min(lastDateForSimultRepeats.getTime(), earliestDate.getTime()), endDate.getTime()));
//            Date subsetEnd = new Date(Math.max(lastDateForSimultRepeats.getTime(), getNextRepeatAfterDate.getTime()));
//            if (lastDateForSimultRepeats.getTime() >= earliestDate.getTime()) {
//                return lastDateForSimultRepeats;
//            } else {
//                return earliestDate;
//            }
//            return subsetEnd;
//</editor-fold>
            return lastDateForSimultRepeats;
        }
    }

    /**
     * calculate the appropriate date from which to calculate the new ones,
     * typically starting from the latest calculated date and thendropping the
     * first since the same as latest
     *
     * @param lastGenerated
     * @return
     */
//    private Date calcSubsetBeginningDateXXX(Date lastGenerated) {
//        Date subsetBeginningDate;
//        if (lastGenerated.getTime() != MyDate.MIN_DATE) {
//            subsetBeginningDate = lastGenerated;
//        } else {
////            subsetBeginningDate = getSpecifiedStartDate();
//            subsetBeginningDate = getSpecifiedStartDateXXXZZZ();
//        }
//        return subsetBeginningDate;
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public RepeatRuleObjectInterface updateRepeatInstancesOnDoneCancelOrDeleteXXX(WorkSlot workSlot) {
//        List<RepeatRuleObjectInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
//        RepeatRuleObjectInterface repeatInstance = null;
//        Date nextRepeatTime;
//        updateLatestDateCompletedOrCancelled(workSlot.getRepeatStartTime(true));
//        nextRepeatTime = getNextDate(workSlot.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE)); //get next date
//        ASSERT.that(repeatInstanceItemList.contains(workSlot), "Error: " + workSlot + " not in list of already generated repeat instances");
////        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
//        repeatInstanceItemList.remove(workSlot);
//        if (nextRepeatTime != null) {
//            repeatInstance = workSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            workSlot.insertIntoListAndSaveListAndInstance(repeatInstance); //insert new created instance in appropriate list and save it
//            repeatInstanceItemList.add(repeatInstance);
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//            Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//        } else { // no more repeat instances to handle
////            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
//        }
//        setListOfUndoneRepeatInstances(repeatInstanceItemList);
//        DAO.getInstance().save(this);
//        return repeatInstance;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getNextDueDate() {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && isRepeatRuleTerminatedXXX()) { //DO NOT test here, since dates need to be re-generated when changing the rule
////            return null;
////        }
////        repeatRule.setInt(RepeatRule.COUNT, getCount(COUNTS_AHEAD));
////        repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
////</editor-fold>
//        RepeatRule repeatRule = getRepeatRule();
//        repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
//        //TODO!!! need to add 1 day to subsetBeginningDate to avoid repeat same date as last generated Date???
////        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), getEndDate()); //CREATE new dates
//        ASSERT.that(getLastGeneratedDate() != MIN_DATE, "getLastGeneratedDate() should have been defined when we get to here");
////        long subsetBeginningDate = getLastGeneratedDate() != 0 ? Math.min(getLastGeneratedDate(), getEndDate()) : getEndDate(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
//        Date subsetBeginningDate = getLastGeneratedDateD(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
//
////        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), MAX_DATE); //CREATE new dates
//        Date startDate = getSpecifiedStartDateD();
//        Vector<Date> newDates = repeatRule.datesAsVector(startDate.getTime(), subsetBeginningDate.getTime(), MAX_DATE); //CREATE new dates
////<editor-fold defaultstate="collapsed" desc="comment">
////        Date date=null;
////        //DONE!!!! how to know that the repeatRule has reached the enddate (since it may not generate a date for endDate)? Always generate with infinite endDate and stop when a date is *after* endDate (and discard this date!)
////        if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//////            newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////            date=newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////            if (date.getT)
////        }
////        if (false && !newDates.isEmpty()) {
////            Date date = newDates.remove(0); //take first date in list
////            if (date.getTime() == getSubsetBeginningDate()) {
////                if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
////                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////                } else {
//////                    date = null;
////                    return null;
////                }
////            }
////            setLastGeneratedDate(date); //store that date for next iteration (if date>endDate this will indicate the RR has terminated)
////            if (date.getTime() > getEndDate()) {
////                return null;
////            } else {
////                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
////                return date;
////            }
////        }
////</editor-fold>
//        Date date = getFirstNotEqualDate(newDates, new Date(getSubsetBeginningDate()));
////<editor-fold defaultstate="collapsed" desc="comment">
////        else {
////            ASSERT.that(false, "shouldn't happen?!"); //yes, may happen for rules that set conflicting constraints
////            return null;
////        }
////        return null;
////</editor-fold>
//        return date;
//    }
//</editor-fold>
    //    private List<Date> generateListOfDates(Date subsetBeginDate, Date subsetEndDate, int numberInstancesToGenerate) {
//        return generateListOfDates(subsetBeginDate, subsetEndDate, numberInstancesToGenerate, false);
//    }
    /**
     *
     *
     * NB. Must generate at least one future repeat for rules where the next
     * repeat date is *after* the endDate - otherwise there will be no more
     * future repeat date which would wrongly stop/terminate the repeat
     * sequence.
     *
     * @param subsetBeginDate
     * @param subsetEndDate
     * @param numberInstancesToGenerate
     * @param genAtLeastOneDate if no dates are generated, for example if the
     * future interval of dates is too short, this option will ensure that one
     * new instance is generated to ensure that a sequence is not wrongly
     * terminated because no new instance was generated when a task was
     * Completed
     * @param keepFirstDateGenerated normally the first date generated is
     * dropped because it corresponds to the date from the previous (just
     * completed/canelled) task, but when editing a rule, the first date should
     * be reused and therefore not dropped
     * @return
     */
//    private List<Date> generateListOfDates(Date subsetBeginDate, Date subsetEndDate, int numberInstancesToGenerate, boolean genAtLeastOneDate) {
//        return generateListOfDates(subsetBeginDate, subsetEndDate, numberInstancesToGenerate, genAtLeastOneDate, false);
//    }
    private List<Date> generateListOfDates(Date startDate, Date subsetBeginDate, Date subsetEndDate, int numberInstancesToGenerate, boolean genAtLeastOneDate, boolean keepFirstDateGenerated) {

        ASSERT.that(subsetEndDate != null && subsetBeginDate.getTime() != 0 && subsetBeginDate.getTime() < subsetEndDate.getTime(), "generateListOfDates called with illegal interval: subsetBeginDate=" + subsetBeginDate + ", subsetEndDate?" + subsetEndDate);
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            if (subsetEndDate == null || subsetEndDate.getTime() == 0) {
//                subsetEndDate = subsetBeginDate;
//            }
//        }

//        if (false) {
//            int requestedCount = numberInstancesToGenerate == -1 ? Integer.MAX_VALUE - 1 : numberInstancesToGenerate; // +1 since we need to generate the last generated date again
//            int countMaxRemaining;
//            int countFutureRepeats = Integer.MAX_VALUE;
//            if (useCount()) {
//                //if rule is limited to number of counts, then take remaining number of repeats into account
//                countMaxRemaining = Math.min(requestedCount, getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar()); //cap count number of remaining repeats
//                count = countMaxRemaining;
//            } else {
//                count = requestedCount;
//            }
//            if (getNumberFutureRepeatsToGenerateAhead() != 0) {
//                countFutureRepeats = getNumberFutureRepeatsToGenerateAhead();
//                count = Math.min(count, countFutureRepeats); //cap count to getNumberFutureRepeatsToGenerateAhead()
//            }
//        }
//</editor-fold>
        int count = numberInstancesToGenerate;

        if (count == Integer.MAX_VALUE && subsetEndDate.getTime() == MyDate.MAX_DATE) {
            count = 1; //if infinite repeat, generate just one
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt() != 0) { //NOT necessary here, is limited in the UI
//            count = Math.min(count, MyPrefs.repeatMaxNumberOfRepeatsToGenerate.getInt()); //+1
//        }
//</editor-fold>
        if (count < Integer.MAX_VALUE) { //test for MAX to avoid wrapping around the int
            count++; // +1 since we may generate the last generated date (subsetBeginDate) again 
        }
//        count = Math.max(count, 1); //COUNT must never be <1

        RepeatRule repeatRule = getRepeatRule();
        repeatRule.setInt(RepeatRule.COUNT, count);
//        Date startDate = getSpecifiedStartDate();
//        if (startDate.getTime() == 0) {
//            startDate = new MyDate(MyDate.MIN_DATE); //if no 
//        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        long subsetBegin = endRepeatDate != null && endRepeatDate.getTime() != 0 ? endRepeatDate.getTime() : MAX_DATE;
//        long subsetBegin = subsetBeginDate.getTime();
//        long subsetEnd = subsetEndDate != null && subsetEndDate.getTime() != 0 ? subsetEndDate.getTime() : MAX_DATE;
//        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), lastGeneratedDate.getTime(), endDate); //CREATE new dates
//</editor-fold>

        Vector<Date> newDates = repeatRule.datesAsVector(startDate, subsetBeginDate, subsetEndDate); //CREATE new dates

        //if first generated date equals subsetStart, then remove it. And remove superfluous instances
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && !newDates.isEmpty()) {
//            Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
//            ASSERT.that(date.getTime() >= subsetBeginDate.getTime());
//            ASSERT.that(subsetEndDate == null || date.getTime() <= subsetEndDate.getTime());
//            if (date.getTime() == subsetBeginDate.getTime()) {
//                date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//            } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
//                if (numberInstancesToGenerate != Integer.MAX_VALUE) {
//                    ASSERT.that(newDates.size() <= numberInstancesToGenerate + 1, "newDates should max have 1 date more than count");
//                    while (newDates.size() > numberInstancesToGenerate) {
//                        newDates.remove(newDates.size() - 1); //remove extraneous elements
//                    }
//                } else {
//                    ASSERT.that(newDates.get(newDates.size() - 1).getTime() <= subsetEndDate.getTime(), "**");
//                    //when generating towards endDate, will always generate right number?!
//                }
//            }
//        }
//</editor-fold>
        removeUnneededDates(newDates, subsetBeginDate, numberInstancesToGenerate, keepFirstDateGenerated);
//
//        if (genAtLeastOneDate && numberInstancesToGenerate == Integer.MAX_VALUE && newDates.size() == 0) {
        //if no new date before endDate, and rule not terminated, then regenerate again with infinite endDate and count==1 (or 2?) to ensure at least ONE future date is generated!
        if (genAtLeastOneDate && numberInstancesToGenerate > 0 && newDates.size() == 0) {
            repeatRule.setInt(RepeatRule.COUNT, 2); //gen 2 dates since 1st may be startDate
//            newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginDate, MyDate.getEndOfDay(getEndDateD())); //CREATE new dates
            newDates = repeatRule.datesAsVector(startDate, subsetBeginDate, MyDate.getEndOfDay(getEndDateD())); //CREATE new dates
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false && !newDates.isEmpty()) {
//                Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
//                if (date.getTime() == subsetBeginDate.getTime()) {
//                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//                } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
//                    ASSERT.that(newDates.size() <= numberInstancesToGenerate + 1, "newDates should max have 1 date more than count");
//                    while (newDates.size() > numberInstancesToGenerate) {
//                        newDates.remove(newDates.size() - 1); //remove extraneous elements
//                    }
//                }
//            }
//</editor-fold>
            removeUnneededDates(newDates, subsetBeginDate, 1, keepFirstDateGenerated); //1 : keep at least one instance
        }
//
        return new ArrayList(newDates);
    }

    /**
     *
     * @param subsetBeginDate
     * @param subsetEndDate
     * @param numberInstancesToGenerate
     * @param genAtLeastOneDate
     * @param keepFirstDateGenerated
     * @return
     */
    private List<Date> generateListOfDates(Date subsetBeginDate, Date subsetEndDate, int numberInstancesToGenerate, boolean genAtLeastOneDate, boolean keepFirstDateGenerated) {
//        Date startDate = getSpecifiedStartDate();
//        if (startDate.getTime() == 0) {
//            startDate = new MyDate(MyDate.MIN_DATE); //if no 
//        }
        Date startDate = subsetBeginDate;
        return generateListOfDates(startDate, subsetBeginDate, subsetEndDate, numberInstancesToGenerate, genAtLeastOneDate, keepFirstDateGenerated);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Date> generateListOfDatesXXX() {
//        //FROM ITEM updateItemsWhenRuleCreatedOrEdited(RepeatRuleObjectInterface repeatRuleOriginator, boolean firstTime)
//        Date startRepeatFromTime = getSpecifiedStartDate();
//
////        Date latestDoneDate = getLatestDateCompletedOrCancelled();
////        Date getNextRepeatAfterDate = latestDoneDate.getTime() != MyDate.MIN_DATE ? latestDoneDate : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
//        // FROM WORKSLOTS public void updateIfExpiredOrDeletedWorkslots(WorkSlot deletedWorkSlotN)
//        Date latestDateCompletedOrCancelled = getLatestDateCompletedOrCancelled(); //expensive, so only call once
////        Date getNextRepeatAfterDate = latestDateCompletedOrCancelled.getTime() != 0 ? latestDateCompletedOrCancelled : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
//        Date getNextRepeatAfterDate = calcSubsetBeginningDate(latestDateCompletedOrCancelled); //Re-start repeat from the last date a task was cancelled/done
//
//        List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, false); //recalcCurrentDate = 1;
//        return dates;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns the appropriate subsetbeignning date to use (for the very first
     * generated repeat date this corresponds to the specificed start date,
     * otherwise to the last date generated so far). NB. This means the rule
     * will generate the same date as the last one generated, this one must be
     * discarded. //TODO!! Alternatively, add one day to start the repeat
     * sequence from the day *after* the last one generated.
     *
     * @return
     */
//    private long getSubsetBeginningDateXXX() {
//        if (getTotalNumberOfInstancesGeneratedSoFar() == 0) {
//            return getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
//        } else {
////            return getLastGeneratedDate(); //get the last date we've generated so far
//            return getLastGeneratedDate() != 0 ? Math.min(getLastGeneratedDate(), getEndDate()) : getEndDate(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Date> getNextDueDateListXXX() {
//        Date lastGenerated = getLastGeneratedDateD(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
////<editor-fold defaultstate="collapsed" desc="comment">
////        Date subsetBeginningDate = null;
////        if (lastGenerated.getTime() != MyDate.MIN_DATE) {
////            subsetBeginningDate = lastGenerated;
////        } else {
////            subsetBeginningDate = getSpecifiedStartDate();
////        }
////</editor-fold>
//        Date subsetBeginningDate = calcSubsetBeginningDate(lastGenerated);
//        Date subsetEndDate = calcSubsetEndDate();
//        int numberRepeats = calcNumberOfRepeats();
//        List<Date> dates = generateListOfDates(subsetBeginningDate, subsetEndDate, numberRepeats, getNumberOfDaysRepeatsAreGeneratedAhead() != 0, false);
//        return dates;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * return only the first of the next coming due dates
     *
     * @return
     */
//    private Date getNextDueDateN() {
//        List<Date> dates = getNextDueDateList();
//        if (!dates.isEmpty()) {
//            return dates.get(0);
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getNextDateOLD(RepeatRule repeatRule, boolean removeDateFromList) {
////        List<Long> datesList = getDatesList();
//        List<Date> datesList = getDatesList();
////        datesList = updateDatesList(datesList, repeatRule);
//        if (datesList.size() == 0) {
//            int totalNumberDatesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar();
////            Vector<Date> newDates = getBatchOfRepeatRuleDates(repeatRule, COUNTS_AHEAD); //CREATE new dates
//            repeatRule.setInt(RepeatRule.COUNT, getCount(COUNTS_AHEAD));
//            Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), getEndDate()); //CREATE new dates
//            //TODO!!!! how to know that the repeatRule has reached the enddate (since it may not generate a date for endDate)? Always generate with infinite endDate and stop when a date is *after* endDate (and discard this date!)
//            if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//                newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//            }
//            while (!newDates.isEmpty()) {
//                Date date = newDates.remove(0); //take first date in list
//                if (date.getTime() > getEndDate()) {
//                    setLastGeneratedDate(date); //since date>endDate this will indicate the RR has terminated
//                    break;
//                } else {
//                    if (newDates.size() == 0) { //if last of the generated dates
//                        setLastGeneratedDate(date); //store that date for next iteration
//                    }
//                    datesList.add(date); //store new batch of dates
//                    totalNumberDatesGeneratedSoFar++;
//                }
//            }
//            setTotalNumberOfInstancesGeneratedSoFar(totalNumberDatesGeneratedSoFar);
//            ASSERT.that(getTotalNumberOfInstancesGeneratedSoFar() <= getNumberOfRepeats(), "generated too many instances total=" + getTotalNumberOfInstancesGeneratedSoFar() + " getCount=" + getNumberOfRepeats() + " #rule=" + this);
//        }
//        setDatesList(datesList);
////        DAO.getInstance().save(this);
//        if (datesList.size() > 0) {
//            if (removeDateFromList) {
//                return (datesList.get(0));
//            } else {
//                return (datesList.remove(0));
//            }
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getNextCompletedFromDate(Date dateCompleted) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && isRepeatRuleTerminatedXXX()) { //NO need to test, no dates should be generated if terminated
////            return null;
////        }
////</editor-fold>
//        ASSERT.that(dateCompleted.getTime() != 0, "called before dateCompleted was set on Item/WorkSlot");
//        RepeatRule repeatRule = getRepeatRule();
//        repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
//
//        Vector<Date> newDates = repeatRule.datesAsVector(dateCompleted, dateCompleted, new Date(MyDate.MAX_DATE)); //CREATE new dates
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
////            date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////            if (date.equals(dateCompleted)) {
////                if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
////                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
////                } else {
////                    date = null;
////                }
////            }
////</editor-fold>
//        Date date = getFirstNotEqualDate(newDates, dateCompleted);
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && date != null) {
////            setLastGeneratedDate(date); //store that date for next iteration (if date>endDate this will indicate the RR has terminated)
////            if (date.getTime() > getEndDate()) {
////                return null;
////            } else {
////                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
////                return date;
////            }
////        }
////        } else {
////            ASSERT.that(false, "shouldn't happen?!");  //otherwise set numberRepeats to MAX or endDate to Max to indicate rule has terminated
////        return null;
////</editor-fold>
//        return date;
////<editor-fold defaultstate="collapsed" desc="comment">
////        }
////        return null;
////        if (newDates.size()>=2) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//////        if (!newDates.isEmpty()) {
////             date = newDates.remove(1); //discard first date (, use setake first date in list
////            setLastGeneratedDate(date); //store that date for next iteration (if date>endDate this will indicate the RR has terminated)
////            if (date.getTime() > getEndDate()) {
////                return null;
////            } else {
////                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
////                return date;
////            }
////        } else {
////            ASSERT.that(false, "shouldn't happen?!");  //otherwise set numberRepeats to MAX or endDate to Max to indicate rule has terminated
////            return null;
////        }
////</editor-fold>
//    }
    /**
     *
     * @return
     */
//    private Date getNextRepeatStartFromDateN() {
//        Date getNextRepeatAfterDate = getLatestDateCompletedOrCancelled().getTime() != 0 ? getLatestDateCompletedOrCancelled() : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getFirstRepeatDateStartingFromNowNXXX() {
//        List<Date> nextDates = generateListOfDates(new MyDate(MyDate.currentTimeMillis()), new MyDate(MyDate.MAX_DATE), 1, true, true);
//        if (!nextDates.isEmpty()) {
//            return nextDates.get(0);
//        }
//        return null;
//    }
//</editor-fold>
    /**
     * find and return the suitable date from which to start the repeat
     * sequence. 1: task due date (if defined); 2: lastCompletedDate (if some
     * tasks already completed, continue sequence starting from the last one
     * completed); 3: the first calculated repeat date using 'now' as starting
     * point (for when no other dates were defined). About (2) use
     * lastCompletedDate and not due date of first repeat instance since the
     * first repeat instance may change if you change the rule, e.g. instead of
     * repeating on Wed it should now repeat on Tue. In case, even the first
     * instance should move from Wed back to the Tue just before, where starting
     * repeat sequence from Wed would give the next-coming Tue instead!
     *
     * @return
     */
//    public static Date getRepeatStartFromDateWhenEditingRuleN(Item item, RepeatRuleParseObject repeatRule) {
    public Date getFirstRepeatDateAfterTodayForWhenEditingRuleWithoutPredefinedDueDateN() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        List<RepeatRuleObjectInterface> doneList = getListOfDoneInstances();
//        Date lastDoneDate = new Date(MyDate.MIN_DATE);
//
//        if (!doneList.isEmpty() && doneList.get(doneList.size() - 1).getRepeatStartTime(false).getTime() != 0) {
//            Date lastDoneDate = doneList.get(doneList.size() - 1).getRepeatStartTime(false);
//            return lastDoneDate; //never null
//        } else {
//            Date firstRepeatDateStartingFromNow = getFirstRepeatDateStartingFromNowN();
//            return firstRepeatDateStartingFromNow; //may be null
//        }
//        Date lastDoneDate =getLatestDate(getListOfDoneInstances(), true);
//</editor-fold>
        Date lastDoneDate = getLatestDateDone();
        if (lastDoneDate.getTime() == MyDate.MIN_DATE) {
//            lastDoneDate = getFirstRepeatDateStartingFromNowN();
            List<Date> nextDates = generateListOfDates(new MyDate(MyDate.currentTimeMillis()), new MyDate(MyDate.MAX_DATE), 1, true, true);
            if (!nextDates.isEmpty()) {
                return nextDates.get(0);
            }
        }
//        return lastDoneDate;
        return null;
    }

    private boolean containsOnlyCancelledInstancesXXX(List<RepeatRuleObjectInterface> list) {
        boolean notCancelledFound = false;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i) instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) list.get(i)).getStatus() == ItemStatus.CANCELLED) {
                notCancelledFound = true;
            }
        }
        return true;
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Date getRepeatStartFromDateWhenEditingRuleNOLD() {
////        if (item != null && item.getDueDateD().getTime() != 0) {
////            return item.getDueDateD(); //never null
////        } else {
//        List<RepeatRuleObjectInterface> doneList = getListOfDoneInstances();
//        if (!doneList.isEmpty() && doneList.get(doneList.size() - 1).getRepeatStartTime(false).getTime() != 0) {
//            Date lastDoneDate = doneList.get(doneList.size() - 1).getRepeatStartTime(false);
//            return lastDoneDate; //never null
//        } else {
//            Date firstRepeatDateStartingFromNow = getFirstRepeatDateStartingFromNowN();
//            return firstRepeatDateStartingFromNow; //may be null
//        }
////        }
//    }
//</editor-fold>
//    private Date getDueDateOrNowIfUndefinedXXX(RepeatRuleObjectInterface repeatRuleOriginator, Date now) {
//        Date startRepeatFromDate = repeatRuleOriginator.getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
//        if (startRepeatFromDate.getTime() == 0) { //UI: this shouldn't happen (since ScreenRR ensures due date is always set), BUT could happen if user deletes the due date
//            startRepeatFromDate = now; //if no existing date to start from, then start from Now
//        }//                Date startRepeatFromTime = latestDoneDate != null ? latestDoneDate : now;
//        return startRepeatFromDate;
//    }
//    public void updateItemsWhenRuleCreatedOrEdited(RepeatRuleObjectInterface repeatRuleOriginator, boolean firstTime) {

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateItemsWhenRuleCreatedOrEditedOLD(Item editedItem) {
////<editor-fold defaultstate="collapsed" desc="comment">
//        /**
//         * //algorithm: ~refresh (reuse existing rule, to avoid
//         * creating/deleting when edited) //get any already created repeat
//         * instances //create dates for new/edited rule //for as many instances
//         * as should be generated (encapsulate this! w moreInstances() and
//         * ruleFinished(), moreInstances based on either #futureInstances vs
//         * length of list or #daysAhead vs lastDayInList) //if an existing
//         * instance exists, use it and set the date (whether new date is same or
//         * not) //if no more instances, generate one //at the end, if there are
//         * still existing instances, delete them (or cancel if actual!=0)
//         *
//         */
////</editor-fold>
//        if (Config.TEST) {
//            ASSERT.that(getListOfDoneInstances().contains(editedItem) || getListOfUndoneInstances().contains(editedItem), "originator [" + editedItem + "] neither in done or undone lists, for RR=" + this);
//        }
//
////        Item editedItem = (Item) editedItemOriginator;
////        assert (true || getTotalNumberOfInstancesGeneratedSoFar() > 0); // ">0" - not the case if only one simultaneous instance
//        if (getRepeatType() == REPEAT_TYPE_NO_REPEAT) { //handle case where a rule is deleted/disabled
////<editor-fold defaultstate="collapsed" desc="NO repeat">
////            if (false) {
////                List undoneList = getListOfUndoneInstances();
////                List doneList = getListOfDoneInstances();
//////<editor-fold defaultstate="collapsed" desc="comment">
//////            if (undoneList.size() == getTotalNumberOfInstancesGeneratedSoFar() + 1 && undoneList.size() > 0) {
//////                //no repeating tasks have been completed so far
//////                undoneList.remove(0); //UI: keep the first instance (original originator, even though the rule may be deleted on another instance)
//////                while (undoneList.size() > 0) {
//////                    ItemAndListCommonInterface elt = (ItemAndListCommonInterface) undoneList.remove(0);
//////                    DAO.getInstance().deleteInBackground((ParseObject) elt); //delete future instances
//////                }
//////            }
//////</editor-fold>
////                if (false) {
////                    undoneList.remove(0); //UI: keep the first instance (original originator, even though the rule may be deleted on another instance)
////                }
////                if (false) { //NO, even originator may be deleted if rule stops repeating!
////                    undoneList.remove(editedItem); //UI:keep the originator (the item editing the rule); remove repeatRuleOriginator from list In case it's still there) so it won't get deleted
////                }
//////<editor-fold defaultstate="collapsed" desc="comment">
//////            int instancesToBeDeleted = undoneList.size();
//////            if (instancesToBeDeleted > 0) {
////////                undoneList.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list In case it's still there) so it won't get deleted
//////                instancesToBeDeleted = undoneList.size();
//////            while (undoneList.size() > 0) { //delete all already generated instances
//////                ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
//////                DAO.getInstance().deleteAndWait(repeatRuleObject); //wait to ensure instances have disappeared from list
//////                if (repeatRuleObject instanceof Item) {
//////                    ((Item) repeatRuleObject).hardDelete();
//////                } else {
//////                    ((WorkSlot) repeatRuleObject).hardDelete();
//////                }
//////            }
//////</editor-fold>
////                if (doneList.size() + undoneList.size() != 1) { //UI: don't delete if repeatRuleOriginator is only instance, must not delete the only item just because the RR is deleted, must not delete a done instance (since it represents work), even if other item is cancelled, then keep that one (use case: cancel one, then stop repeatrule)
////                    Collection updatedOwners = deleteUnneededItemInstances(undoneList); //never delete done instances
//////            DAO.getInstance().saveNew(this, true); //update the rule
//////            DAO.getInstance().saveNew(this); //update the rule
//////            DAO.getInstance().saveNew(updatedOwners); //update the rule
//////            DAO.getInstance().saveNewTriggerUpdate();
////                    DAO.getInstance().saveToParseLater(updatedOwners);
////                } else {
////                    ASSERT.that(doneList.contains(editedItem) || undoneList.contains(editedItem),
////                            () -> "ERROR, only 1 element in done+undone lists, but NOT originator, originator=" + editedItem + "; done=" + doneList + "; undone=" + undoneList);
////                }
////                DAO.getInstance().saveToParseNow(this); //update the rule
////            } else {
//            deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(editedItem);
////            }
////                DAO.getInstance().deleteBatch((List<ParseObject>)undoneList);
////            }
////don't change setTotalNumberOfInstancesGeneratedSoFar()!! UI: Keep the old count of generated instances (but should then show already generated instances to make it possible to understand what happens!)
////</editor-fold>
//        } else if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) { //handle case where a rule is switched from repeat from due, to repeat from completed
////<editor-fold defaultstate="collapsed" desc="repeat from completed date">
////Called either on very first time (and should do nothing other than add originator to listOfUndone), or when rule is changed from Due to Completed
//            if (true) {
//                //cases: changed from/to datedCompletion, from Due to to undated/dateCompletion: in all cases, get rid of other future instances
//                if (isDatedCompletion()) { //
//                    List<Item> undoneList = (List) getListOfUndoneInstances();
//                    setListOfUndoneInstances(null);
//                    //calculate if RR will generate any more instances to know if the first undone instance (if any) should be kept:
//                    Date startRepeatDate = new MyDate();
//                    List<Date> dates = generateListOfDates(startRepeatDate, calcSubsetEndDate(), 1, false, false);
//
////                    undoneList.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list so it won't get deleted
//                    RepeatRuleObjectInterface keep = null;
//                    if (dates.size() > 0 && undoneList.size() > 0) {
//                        keep = undoneList.remove(0); //remove the first element in the list from list so it won't get deleted
//                        if (MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances.getBoolean()) {
//                            keep.updateRelativeDates(dates.get(0));
//                        }
//                    }
////<editor-fold defaultstate="collapsed" desc="comment">
////            instancesToBeDeleted = undoneList.size();
////                while (undoneList.size() > 0) { //delete all already generated instances
////                    ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
////                    DAO.getInstance().deleteInBackground(repeatRuleObject); //optimization: deleteInBackground(List<Item>)!!
////                }
////</editor-fold>
//                    Collection updatedOwners = deleteUnneededItemInstances(undoneList);
////            undoneList.add(repeatRuleOriginator); //add, or re-add originator
//                    List newUndoneList = new ArrayList();
////                    newList.add(repeatRuleOriginator);
//                    if (keep != null) {
//                        newUndoneList.add(keep);
//                    }
//                    setListOfUndoneInstances(newUndoneList);
//                    //NB NO calculation of new instances - that haapens only on completion!
////<editor-fold defaultstate="collapsed" desc="comment">
////reset all counters in case the rule is later switched back to REPEAT_TYPE_FROM_DUE_DATE
////            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() - instancesToBeDeleted);
////            if (false) {
////                setLatestDateCompletedOrCancelled(null); //no need to reset this, will keeping it make it possible to change the rule back again?!
////            }            //            setLastGeneratedDateIfGreaterThanLastDate(null);
////            if (repeatRuleOriginator.getRepeatStartTime(false).getTime() != 0) {
////            if (repeatRuleOriginator.getRepeatStartTime(true).getTime() != 0) { //if rpeating from CompletedDate, then no need to start lastGeneratedDate (TODO!!!: only in case the date will be used if converting the RR to another type?!)
//////                setLastGeneratedDate(repeatRuleOriginator.getRepeatStartTime(false)); //initialize LastGeneratedDate to due date of the originator (TODO is this correct?)
////                setLastGeneratedDate(repeatRuleOriginator.getRepeatStartTime(true), true); //initialize LastGeneratedDate to due date of the originator (TODO is this correct?)
////            }
////</editor-fold>
//                    if (Config.TEST) {
//                        checkRefs();
//                    }
////                DAO.getInstance().saveNew(this, true);
////                    DAO.getInstance().saveNew(this);
////                    DAO.getInstance().saveNew((ParseObject) keep);
////                    DAO.getInstance().saveNew(updatedOwners);
////                    DAO.getInstance().saveNewTriggerUpdate();
//                    DAO.getInstance().saveToParseLater(updatedOwners);
////                    DAO.getInstance().saveToParseNow(this, (ParseObject) keep);
//                    DAO.getInstance().saveToParseNow((ParseObject) keep);
//
//                } else { //if completion RR is NOT dated, then we always keep one instance (infinite repeat)
//
//                    List<Item> undoneList = (List) getListOfUndoneInstances();
//                    if (Config.TEST) {
//                        ASSERT.that(undoneList.size() == 0 || undoneList.contains(editedItem), "getListOfUndoneRepeatInstances does not contain originator=" + editedItem + " list=" + undoneList);
//                    }
//                    RepeatRuleObjectInterface keep = null;
//                    if (undoneList.size() > 0) {
//                        keep = undoneList.remove(0); //remove the first element in the list from list so it won't get deleted
//                    }
//                    Collection updatedOwners = deleteUnneededItemInstances(undoneList);
//                    List newList = new ArrayList();
////                    newList.add(repeatRuleOriginator);
//                    if (keep != null) {
//                        newList.add(keep);
//                    }
//                    setListOfUndoneInstances(newList);
//                    //NB NO calculation of new instances - that haapens only on completion!
////                    DAO.getInstance().saveNew(this);
////                    DAO.getInstance().saveNew(updatedOwners);
////                    DAO.getInstance().saveNewTriggerUpdate();
//                    DAO.getInstance().saveToParseLater(updatedOwners);
//                    DAO.getInstance().saveToParseNow(this);
//                }
//            } else {
//                //ACTUALLY nothing to do for a RR on completion: only one instance, changes to RR takes effect upon completion!
//                //NOOO. The rule may have changed from repeatFromDue to repeatOnCompleted!!!
//            }
////</editor-fold>
//        } else { //getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE
//            ASSERT.that(getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE);
////            if (false && firstTime) { //code in else branch now works whether first time or not
//            if (false) { //code in else branch now works whether first time or not
////<editor-fold defaultstate="collapsed" desc="first time">
//////                ASSERT.that(getTotalNumberOfInstancesGeneratedSoFar() == 0, "getTotalNumberOfInstancesGeneratedSoFar() not 0 on firstTime call to repeat rule " + getTotalNumberOfInstancesGeneratedSoFar());
////                List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneInstances();  //reuse any existing instances
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                if (false) {
//////                    newRepeatInstanceList.add(repeatRuleOriginator); //now done when callinScreen on new rule
//////                    setListOfUndoneInstances(newRepeatInstanceList); //MUST set the list to ensure the right number of existing instances is used in calcNumberOfRepeats() used below
//////                }
//////</editor-fold>
//////                Date specifiedStartDate = getSpecifiedStartDate();
//////                Date startRepeatFromTime = calcSubsetBeginningDate(specifiedStartDate);
////                Date now = new MyDate();
////                Date startRepeatFromDate = getStartDateForRefreshRule(now);
//////                Date startRepeatFromTime = calcSubsetBeginningDate(startDate);
//////                Date startRepeatFromTime = latestDoneDate != null ? latestDoneDate : now;
////                ASSERT.that(startRepeatFromDate != null);
////                boolean keepFirstGenerated = startRepeatFromDate.equals(now);
////                //if we're repeating from now, then DO keep the first generated
////                List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstGenerated);
////
////                ItemAndListCommonInterface owner = null;
////                RepeatRuleObjectInterface newRepeatInstance = null;
////                RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
////
////                ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
////                Set<ParseObject> updatedOwners = new HashSet<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
////
////                for (Date nextRepeatTime : dates) {
////                    newRepeatInstance = reuseNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
////                    if (newRepeatInstance == null) {
////                        newRepeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
//////                    owner = repeatRefForInsertion.insertIntoList(newRepeatInstance); //store ownerlist to know if needs to be saved below
////                        owner = insertIntoList2(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
////                        updatedOwners.add((ParseObject) owner);
////                    }
////                    repeatRefForInsertion = newRepeatInstance; //ensure we always insert *after* any earlier inserted instances
////                    newRepeatInstanceList.add(newRepeatInstance);
//////                    setLastGeneratedDate(nextRepeatTime); //if at least one additional repeatinstance was used, then use that date
////                }
//////                setTotalNumberOfInstancesGeneratedSoFar(newRepeatInstanceList.size() - 1); //deduct 1 for the repeatRuleOriginator added above
////                setListOfUndoneInstances(newRepeatInstanceList);
////
////                DAO.getInstance().saveNew(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
////                DAO.getInstance().saveNew((ParseObject) this, (ParseObject) owner); //must save RR & new instances *before* saving list with new instances. save list where new instances were inserted
////                DAO.getInstance().saveNewExecuteUpdate(); //must save RR & new instances *before* saving list with new instances. save list where new instances were inserted
////</editor-fold>
//            } else { //!firstTime <=> rule EDITED
////<editor-fold defaultstate="collapsed" desc="SAME approach, whether first time or not">
////NOT first time - rule has been changed/edited -> recalculate/regenerate the complete set of future instances
////UI:
//                List<Item> oldUndoneRepeatInstanceList = (List) getListOfUndoneInstances();  //reuse any existing instances (onlu holds originator if changed from onCompletion)
//                setListOfUndoneInstances(null); //"forget the previous repeat instances and start all over!" - delete old list of instances before calling calcNumberOfRepeats() and generateListOfDates() below!!
//
//                List<Item> doneInstances = (List) getListOfDoneInstances();
//
//                //on the very first call, both lists are empty so add originator to appropriate list depending on whether it is done or not (usually it should not be done, but could happen, e.g if new task is forced done on creation)
////                if (oldUndoneRepeatInstanceList.isEmpty() && doneInstances.isEmpty()) {
////                    if (editedItem.isDone()) {
////                        doneInstances.add(editedItem);
////                    } else {
////                        oldUndoneRepeatInstanceList.add(editedItem);
////                    }
////                }
//                int currentTotalNumberOfUndoneInstances = oldUndoneRepeatInstanceList.size();
//
//                if (Config.TEST) {
//                    ASSERT.that(currentTotalNumberOfUndoneInstances != 0 || !doneInstances.isEmpty(), "RepeatRule called with both undone and done instances empty, at least one should contain an item");
//                }
//
//                int numberOfRepeats = calcNumberOfRepeats(currentTotalNumberOfUndoneInstances);
//
//                Set<ParseObject> updatedOwners = new HashSet<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
//
//                if (numberOfRepeats >= 1) { //check if we should keep the originator (without this check we may keep the originator even if no more instances should be generated!)
//
//                    List newRepeatInstanceList = new ArrayList(); //hold new created instances
////<editor-fold defaultstate="collapsed" desc="comment">
////                setLastGeneratedDate(null); //when rule is edited, and recalculated, first reset the previous value of lastDateGenerated (which is used for xxx)
////                int oldTotalNumberInstancesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar(); //- oldRepeatInstanceList.size()-1; //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later), -1: alwlays remove the initial
////                int oldNumberOfUndoneRepeatInstances = oldUndoneRepeatInstanceList.size(); //always one more than getTotalNumberOfInstancesGeneratedSoFar() since it inclu
////                //if the
////                int completedNumberOfUndoneRepeatInstances = oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances
////                        - (oldNumberOfUndoneRepeatInstances > oldTotalNumberInstancesGeneratedSoFar ? 1 : 0); //oldNumber > oldTotal, then the originator
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (oldNumberOfUndoneRepeatInstances > oldTotalNumberInstancesGeneratedSoFar) { //if true, it means RR originator is included in list so need to +1 to compensate
////                    setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + 1); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!!
////                } else {
////                    setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!!
////                }
////</editor-fold>
////                setTotalNumberOfInstancesGeneratedSoFar(completedNumberOfUndoneRepeatInstances); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!! AND to keep precise track of already completed
////                    if (false) { //do not treat originator differently, always update all
////                        oldUndoneRepeatInstanceList.remove(repeatRuleOriginator); //remove originator so it's not reused //NO! could be reused
////                        newRepeatInstanceList.add(repeatRuleOriginator); //we keep originator
////                        setListOfUndoneInstances(newRepeatInstanceList); //must add originator back again here to ensure future dates are calculated correctly
////                    } else {
////                        setListOfUndoneInstances(null); //must add originator back again here to ensure future dates are calculated correctly
////                    }
////</editor-fold>
//                    setListOfUndoneInstances(null); //must add originator back again here to ensure future dates are calculated correctly
////<editor-fold defaultstate="collapsed" desc="comment">
////  ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == oldNumberOfUndoneRepeatInstances - 1, "inconsistency btw firstTime (empty list of previously generated instances) and nb of prev instances=" + oldTotalNumberInstancesGeneratedSoFar);
////                Date latestDoneDate = getLatestDateCompletedOrCancelled();
////                Date getNextRepeatAfterDate = latestDoneDate.getTime() != 0 ? latestDoneDate : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
////                Date getNextRepeatAfterDate = latestDoneDate.getTime() != MyDate.MIN_DATE ? latestDoneDate : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
////                Date getNextRepeatAfterDate = calcSubsetBeginningDate(latestDoneDate); //Re-start repeat from the last date a task was cancelled/done
////                Date getNextRepeatAfterDate = getStartDateForRefreshRule(); //Re-start repeat from the last date a task was cancelled/done
////</editor-fold>
//                    RepeatRuleObjectInterface instanceToCreateCopiesFrom; //= repeatRuleOriginator;
//
//                    Date startRepeatFromDate;
//                    Date defaultStartDate = MyDate.getStartOfToday(); //ex-now: now is not good since it may miss instances generated for earlier today
//                    boolean keepFirstGenerated;
//                    if (isDatePatternChanged()) { //if rule edited from a completed item, replace by either the first unDone if available, or
////                        if (repeatRuleOriginatorItem.isDone()) {
//                        //UI: editing any completed task is equivalent to editing the first undone (if any), otherwise repetition will start from Now using the edited item for copies
////                        if (!oldUndoneRepeatInstanceList.isEmpty()) {
////<editor-fold defaultstate="collapsed" desc="comment">
//                        if (false) {
//                            if (!doneInstances.isEmpty()) {
////                            instanceToCreateCopiesFrom = null; //instanceToCreateCopiesFrom will be set below when iterating over dates and reusing oldUndone instances // oldUndoneRepeatInstanceList.get(0);
////                            instanceToCreateCopiesFrom = doneInstances.get(doneInstances.size()-1); //instanceToCreateCopiesFrom will be set below when iterating over dates and reusing oldUndone instances // oldUndoneRepeatInstanceList.get(0);
//                                instanceToCreateCopiesFrom = oldUndoneRepeatInstanceList.get(0); //instanceToCreateCopiesFrom will be set below when iterating over dates and reusing oldUndone instances // oldUndoneRepeatInstanceList.get(0);
////                                startRepeatFromDate = instanceToCreateCopiesFrom.getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
////                            startRepeatFromDate = oldUndoneRepeatInstanceList.get(0).getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
//                                startRepeatFromDate = defaultStartDate; //instanceToCreateCopiesFrom.getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
//                                keepFirstGenerated = true;
//                            } else { //no old instance to continue from
//                                if (true || !doneInstances.isEmpty()) {
////                                instanceToCreateCopiesFrom = doneInstances.get(doneInstances.size() - 1);
//                                    instanceToCreateCopiesFrom = oldUndoneRepeatInstanceList.get(0);
//                                    startRepeatFromDate = instanceToCreateCopiesFrom.getRepeatStartTime(false);
//                                    keepFirstGenerated = true;
//                                } //else: even if there might be later doneInstances than the one edited, the rule will start from the edited one
//                                else {
//                                    instanceToCreateCopiesFrom = null; //SHOULD never happen, since not both undones/dones can be empty; //repeatRuleOriginator;
//                                }                                //UI: even if we *could* generate past instances by continuing the original date sequence (by continuing from due of last done instance)
//                                //UI: it is unlikely that the user would have done the (past) task without it being in the list. So pretty safe to assume you only want to generate *future* instances
//                                //TODO!!! add option here ot continue generated from last done task to ensure a continuous sequence of dates based on new definition of rule - but generating tasks in the past with a new rule makes little sense
//                                startRepeatFromDate = defaultStartDate; //start repeating from now, no
//                                keepFirstGenerated = false;
//                            }
//                        }
////</editor-fold>
//                        if (!oldUndoneRepeatInstanceList.isEmpty()) { //if available start copies from first undone instance (most likely to have right values), otherwise **
//                            instanceToCreateCopiesFrom = oldUndoneRepeatInstanceList.get(0); //instanceToCreateCopiesFrom will be set below when iterating over dates and reusing oldUndone instances // oldUndoneRepeatInstanceList.get(0);
//                        } else if (!doneInstances.isEmpty()) {
//                            instanceToCreateCopiesFrom = doneInstances.get(doneInstances.size() - 1); //instanceToCreateCopiesFrom will be set below when iterating over dates and reusing oldUndone instances // oldUndoneRepeatInstanceList.get(0);
//                        } else {
//                            instanceToCreateCopiesFrom = editedItem;
//                            if (((Item) editedItem).isDone()) {
//                                doneInstances.add(editedItem);
//                            } else {
//                                newRepeatInstanceList.add(editedItem);
//                            }
//                        }
//                        startRepeatFromDate = defaultStartDate; //instanceToCreateCopiesFrom.getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
//                        keepFirstGenerated = true;
////<editor-fold defaultstate="collapsed" desc="comment">
////                        } else {
////                            instanceToCreateCopiesFrom = repeatRuleOriginator;
//////                Date startRepeatFromDate = getStartDateForRefreshRule(now);
////                            startRepeatFromDate = repeatRuleOriginator.getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
//////                        if (startRepeatFromDate.getTime() == 0) { //UI: this shouldn't happen (since ScreenRR ensures due date is always set), BUT could happen if user deletes the due date
//////                            startRepeatFromDate = now; //if no existing date to start from, then start from Now
//////                        }//                Date startRepeatFromTime = latestDoneDate != null ? latestDoneDate : now;
////                        }
////</editor-fold>
////                        keepFirstGenerated = startRepeatFromDate.equals(now);
//                        setDatePatternChanged(false); //reset to false so only used once
//                    } else { //date pattern NOT changed, meaning we will only add/remove repeat instances (potentially remove all if edited RR doesnt' generate any add'l instances)
//                        if (oldUndoneRepeatInstanceList.size() > 0) {
//                            startRepeatFromDate = oldUndoneRepeatInstanceList.get(0).getRepeatStartTime(false); //start calc from already generated date and keep it (avoid regenerating from a past task where due date might have been changed)
//                            instanceToCreateCopiesFrom = oldUndoneRepeatInstanceList.get(0); //we know we'll generate at least one
//                            keepFirstGenerated = true;
//                        } else {
//                            instanceToCreateCopiesFrom = doneInstances.get(doneInstances.size() - 1);
//                            startRepeatFromDate = defaultStartDate;
//                            keepFirstGenerated = true;
//                        }
////                        instanceToCreateCopiesFrom =;
//                    }
//                    if (Config.TEST) {
//                        ASSERT.that(startRepeatFromDate != null && startRepeatFromDate.getTime() != 0, "no startRepeatFromDate=" + startRepeatFromDate);
//                    }
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (startRepeatFromDate == null || startRepeatFromDate.getTime() == 0) {
////                        startRepeatFromDate = now;
////                    }
////                    ASSERT.that(startRepeatFromDate != null);
////                    boolean keepFirstGenerated = startRepeatFromDate.equals(now);
////                boolean keepFirstDate = !getNextRepeatAfterDate.equals(repeatRuleOriginator.getRepeatStartTime(false)); //drop first date if it the same
////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, true); //recalcCurrentDate = 1;
////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstDate); //recalcCurrentDate = 1;
////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, false); //recalcCurrentDate = 1;
////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstGenerated); //recalcCurrentDate = 1;
////</editor-fold>
//                    //call calcNumberOfRepeats() to re-calculate number of repeat after changes above
//                    List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(0), false, keepFirstGenerated); //recalcCurrentDate = 1;
////                List<Date> dates2 = generateListOfDates();
//
////                ItemAndListCommonInterface owner = null;
//                    RepeatRuleObjectInterface newRepeatInstance;
////                    RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
//                    RepeatRuleObjectInterface repeatRefForInsertion = instanceToCreateCopiesFrom; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
//
////                Set<ParseObject> updatedOwners = new HashSet<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
////                updatedOwners.add(((IrepeatRuleOriginator.get)
//                    for (Date nextRepeatTime : dates) {
////                        newRepeatInstance = reuseNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
//                        newRepeatInstance = reuseNextRepeatItemXXX(oldUndoneRepeatInstanceList, nextRepeatTime);
//                        if (newRepeatInstance == null) {
////                            newRepeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
//                            newRepeatInstance = instanceToCreateCopiesFrom.createRepeatCopy(nextRepeatTime); //create next instance
////                        ownerList = repeatRefForInsertion.insertIntoList(newRepeatInstance); //store ownerlist to know if needs to be saved below
//                            ItemAndListCommonInterface owner = insertRepeatInstanceAtRightPositionInOwner(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
//                            updatedOwners.add((ParseObject) owner);
//                        } //else: if newRepeatInstance is reused, there's no need to insert it into the owner list, it is already there
//                        repeatRefForInsertion = newRepeatInstance;
//                        instanceToCreateCopiesFrom = newRepeatInstance; //always create copies in a chain (copying the just preceding instance)
//                        newRepeatInstanceList.add(newRepeatInstance);
////                    setLastGeneratedDate(nextRepeatTime); //save last date generated
//                    }
//                    setListOfUndoneInstances(newRepeatInstanceList);
////                    DAO.getInstance().saveNew(newRepeatInstanceList); //save new instances
//                    if (false) {
//                        DAO.getInstance().saveToParseLater(newRepeatInstanceList); //save new instances
//                    }
//                }
//
////                 updatedOwners = deleteUnneededItemInstances(oldUndoneRepeatInstanceList);
//                for (ItemAndListCommonInterface updateOwner : deleteUnneededItemInstances(oldUndoneRepeatInstanceList)) { //delete unneeded instances and add their owners to list that need to be saved
//                    updatedOwners.add((ParseObject) updateOwner);
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar(), "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule");
////                int newTotal = completedNumberOfUndoneRepeatInstances + newRepeatInstanceList.size();
////                setTotalNumberOfInstancesGeneratedSoFar(newTotal);
////                if (false&&Config.TEST) {
////                    checkRefs();
////                }
////                DAO.getInstance().saveInBackground(this);
////                if (ownerList != null)
////                    DAO.getInstance().saveInBackground((ParseObject) ownerList); //save list where new instances were inserted
////                    DAO.getInstance().saveInBackground((List<ParseObject>)updatedOwners); //save list where new instances were inserted
////                setListOfUndoneInstances(newRepeatInstanceList);
////                DAO.getInstance().saveNew(newRepeatInstanceList); //save new instances
////                DAO.getInstance().saveNew((List<ParseObject>) updatedOwners, true); //save list where new instances were inserted
////</editor-fold>
////                DAO.getInstance().saveNew((ParseObject) this); //save list where new instances were inserted
////                DAO.getInstance().saveNew(updatedOwners); //save list where new instances were inserted
////                DAO.getInstance().saveNewTriggerUpdate(); //save list where new instances were inserted
//                if (false) { //not needed since saved when saving the edited task
//                    DAO.getInstance().saveToParseLater(updatedOwners); //save list where new instances were inserted
//                    DAO.getInstance().saveToParseNow((ParseObject) this); //save list where new instances were inserted
//                }
//            }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////            //new rule, changed REPEAT_TYPE_FROM_DUE_DATE, or rule was changed from REPEAT_TYPE_FROM_COMPLETED_DATE to REPEAT_TYPE_FROM_DUE_DATE
//////            List<? extends RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
//////            List<ItemAndListCommonInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
////
////            //first time we generate instances for a rule, we need to add the originator and only generate additional instances
////            //next time all dates mu be generated and the originator may change date
//////            boolean firstTime = oldTotalNumberInstancesGeneratedSoFar == 0; // || !oldUndoneRepeatInstanceList.contains(repeatRuleOriginator);
////            ASSERT.that(!firstTime || oldTotalNumberInstancesGeneratedSoFar == 0, "inconsistency btw firstTime (empty list of previously generated instances) and nb of prev instances=" + oldTotalNumberInstancesGeneratedSoFar);
////            Date startRepeatFromTime;
////            List<Date> dates;
////            int recalcCurrentDate = 0;
////            if (firstTime || getLatestDateCompletedOrCancelled() == null) {
////                if (false) {
////                    newRepeatInstanceList.add(repeatRuleOriginator);
////                    oldNumberOfUndoneRepeatInstances++; //add 1 to compensentae for adding originator (when deducting newRepeatInstanceList.size()
////                }
////                if (false) startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
////                startRepeatFromTime = getSpecifiedStartDateD();
////                if (startRepeatFromTime == null) {
////                    ASSERT.that(false, "no date from getSpecifiedStartDateD() -shouldn't happen, repeatRule=" + this);
////                    startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
////                    setSpecifiedStartDate(startRepeatFromTime);
////                }
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                if (false) {
//////                    if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
////////                    dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead());
//////                        dates = generateListOfDates(startRepeatFromTime, calcSubEndDate(), calcNumberOfRepeats());
//////                    } else {
//////                        Date subEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
////////                    dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
//////                        dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
//////                    }
//////                }
//////</editor-fold>
////            } else {
////                startRepeatFromTime = getLatestDateCompletedOrCancelled(); //Re-start repeat from the last date a task was cancelled/done
////                recalcCurrentDate = 1;
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                if (false) {
//////                    if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
//////                        dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead() + 1); //1: need to *re-*generate the full number of dates if possible (including possibly the originator)
//////                    } else {
//////                        Date subEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
//////                        dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
//////                    }
//////                    ASSERT.that(oldUndoneRepeatInstanceList.contains(repeatRuleOriginator));
//////                    //move to head of list to make sure the originator becomes the first instance in the repeat sequence:
//////                    if (false) { //DON'T since thiws may/will not keep the items in due date order
//////                        oldUndoneRepeatInstanceList.remove(repeatRuleOriginator);
//////                        oldUndoneRepeatInstanceList.add(0, repeatRuleOriginator);
//////                    }
//////                }
//////</editor-fold>
////            }
//////            Date startRepeatFromTime = (firstTime||getLatestDateCompletedOrCancelled()==null)?getSpecifiedStartDateD():getLatestDateCompletedOrCancelled():
////            dates = generateListOfDates(startRepeatFromTime, calcSubsetEndDate(startRepeatFromTime), calcNumberOfRepeats() + recalcCurrentDate);
////
////            //not the first time, so start repetition from the first still active instance
////            ASSERT.that(true, "shouldn't happen?! repeatRule=" + this + ", item=" + repeatRuleOriginator);
////
////            ItemAndListCommonInterface ownerList = null;
////            RepeatRuleObjectInterface newRepeatInstance;
////            RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
////            Date nextRepeatTime = null;
////            for (int i = 0, size = dates.size(); i < size; i++) {
////                nextRepeatTime = dates.get(i);
////                newRepeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
////                ownerList = repeatRefForInsertion.insertIntoList(newRepeatInstance);
////                repeatRefForInsertion = newRepeatInstance;
////                newRepeatInstanceList.add(newRepeatInstance);
//////                setLastGeneratedDate(nextRepeatTime); //save last date generated
////            }
////            if (nextRepeatTime != null) {
////                setLastGeneratedDate(nextRepeatTime); //save last date generated
////            }
////
////            //delete any 'left over' instances //TODO check that deleting them doesn't affect
//////<editor-fold defaultstate="collapsed" desc="comment">
//////            while (oldUndoneRepeatInstanceList.size() > 0) {
//////                RepeatRuleObjectInterface obsoleteInstance = oldUndoneRepeatInstanceList.remove(0);
//////                if (obsoleteInstance instanceof ParseObject) {
//////                    if (obsoleteInstance instanceof Item && ((Item) obsoleteInstance).getActualEffort() != 0) { //for Items, not WorkSlots
//////                        ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //Cancel instead of delete if time has been registered (to avoid losing it)
//////                        DAO.getInstance().save((Item) obsoleteInstance); //must save it
//////                    } else {
////////                obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
//////                        DAO.getInstance().delete((ParseObject) obsoleteInstance);   //TODO!!!! check that this deletes it in all lists, categories etc!
//////                    }
//////                } //else: not saved to Parse
//////            }
//////</editor-fold>
////            deleteSuperfluousRepeatInstances(oldUndoneRepeatInstanceList);
////
////            if (this.getObjectIdP() == null)
////                DAO.getInstance().saveInBackground(this); //save repeatRule if not already done, needed so that repeatInstances reference a rule with an ObjectId
////            DAO.getInstance().saveInBackground(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
////            ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar(), "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule");
////            setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + newRepeatInstanceList.size());
////            if (firstTime) {
////                newRepeatInstanceList.add(0, repeatRuleOriginator);
//////                oldNumberOfUndoneRepeatInstances++; //add 1 to compensentae for adding originator (when deducting newRepeatInstanceList.size()
////            }
////            setListOfUndoneRepeatInstances(newRepeatInstanceList);
//////        oldTotalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
////            DAO.getInstance().saveInBackground(this);
////            if (ownerList != null)
////                DAO.getInstance().saveInBackground((ParseObject) ownerList); //save list where new instances were inserted
////        }
////</editor-fold>
//        }
////        DAO.getInstance().saveInBackground(this);
//    }
//</editor-fold>
    private Date getStartDateWhenOnlyNumberOccurencesChanged(List<Item> undoneInstances, List<Item> doneInstances, Date defaultStartDate) {
        Date startRepeatFromDate = defaultStartDate;
        if (undoneInstances.size() > 0) {
            startRepeatFromDate = undoneInstances.get(0).getRepeatStartTime(false); //start calc from already generated date and keep it (avoid regenerating from a past task where due date might have been changed)
//                            instanceToCreateCopiesFrom = undoneInstances.get(0); //we know we'll generate at least one 
//                            keepFirstGenerated = true;
        } else {
//                            instanceToCreateCopiesFrom = doneInstances.get(doneInstances.size() - 1);
            startRepeatFromDate = defaultStartDate;
//                            keepFirstGenerated = true;
        }
        return startRepeatFromDate;
    }

    /**
     * return the Item
     *
     * @param oldUndoneRepeatInstanceList
     * @param doneInstances
     * @param editedItem
     * @return
     */
    private Item getItemToUseForUpdatingOrCreationNewRepeatInstances(List<Item> oldUndoneRepeatInstanceList, List<Item> doneInstances, Item editedItem) {
        boolean startFromEditedItem = true; //this won't work if you eg want to move dates *back* say from Thursday to Wednesday
        boolean startFromToday = true; //start repeating from Now
        boolean startFromLastDoneInstance = false;
        boolean startFromFirstUndoneInstance = false;
        Item instanceToCreateCopiesFrom;

        if (startFromLastDoneInstance && !doneInstances.isEmpty()) {
            instanceToCreateCopiesFrom = doneInstances.get(doneInstances.size() - 1);
        } else if (startFromFirstUndoneInstance && !oldUndoneRepeatInstanceList.isEmpty()) {
            instanceToCreateCopiesFrom = oldUndoneRepeatInstanceList.get(0);
        } else {
            instanceToCreateCopiesFrom = editedItem;
        }

        //if possible, recalculate new repeat sequence as a continuation from the last Done instance
        return instanceToCreateCopiesFrom;
    }

    /**
     * determine the date from which new or updated repeat instances should
     * start from. For the first calculation this will always be the due date of
     * the edited task it is defined, otherwise it will the start of the today
     * so that first repeat date is the first-coming matching date on today or
     * after.
     *
     * @param oldUndoneRepeatInstanceList
     * @param doneInstances
     * @param editedItem
     * @return
     */
    private Date getStartRepeatUpdateFrom(List<Item> oldUndoneRepeatInstanceList, List<Item> doneInstances, Item editedItem) {
        boolean startFromFirstUndoneRepeat = false; //this won't work if you eg want to move dates *back* say from Thursday to Wednesday
        boolean startFromToday = true; //start repeating from Now
        boolean startFromLastDoneRepeat = false;
        boolean startFromDueDateOfEditedItem;
//                        Date defaultStartDate = MyDate.getStartOfToday(); //ex-now: now is not good since it may miss instances generated for earlier today
        Date startRepeatFromDate;
        if (oldUndoneRepeatInstanceList.size() + doneInstances.size() == 1 && editedItem.getDueDate().getTime() != 0) { //by default start new/update 
            //on first time, only the new item is in the lists
            startRepeatFromDate = editedItem.getDueDate();
        } else if (startFromLastDoneRepeat && !doneInstances.isEmpty() && doneInstances.get(doneInstances.size() - 1).getDueDate().getTime() != 0) {
            startRepeatFromDate = doneInstances.get(doneInstances.size() - 1).getDueDate();
            startRepeatFromDate.setTime(startRepeatFromDate.getTime() + 1); //+1: avoid recalculating the same date (works?!)
        } else if (startFromFirstUndoneRepeat && !oldUndoneRepeatInstanceList.isEmpty() && oldUndoneRepeatInstanceList.get(0).getDueDate().getTime() != 0) {
            startRepeatFromDate = oldUndoneRepeatInstanceList.get(0).getDueDate();
        } else {
            startRepeatFromDate = MyDate.getStartOfToday();
        }
        return startRepeatFromDate;
    }

    public void updateItemsWhenRuleCreatedOrEdited(Item editedItem) {
        if (Config.TEST) {
            ASSERT.that(getListOfDoneInstances().contains(editedItem) || getListOfUndoneInstances().contains(editedItem), "originator [" + editedItem + "] neither in done or undone lists, for RR=" + this);
        }

        if (getRepeatType() == REPEAT_TYPE_NO_REPEAT) { //handle case where a rule is deleted/disabled
            deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(editedItem);
        } else if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) { //handle case where a rule is switched from repeat from due, to repeat from completed
//Called either on very first time (and should do nothing other than add originator to listOfUndone), or when rule is changed from Due to Completed
            //ACTUALLY nothing to do for a RR on completion: only one instance, changes to RR takes effect upon completion!
            //NOOO. The rule may have changed from repeatFromDue to repeatOnCompleted!!!

            //cases: changed from/to datedCompletion, from Due to to undated/dateCompletion: in all cases, get rid of other future instances
            if (isDatedCompletion()) {
                List<Item> undoneList = (List) getListOfUndoneInstances();
                setListOfUndoneInstances(null);
                //calculate if RR will generate any more instances to know if the first undone instance (if any) should be kept:
                Date startRepeatDate = new MyDate();
                List<Date> dates = generateListOfDates(startRepeatDate, calcSubsetEndDate(), 1, false, false);

                RepeatRuleObjectInterface keep = null;
                if (dates.size() > 0 && undoneList.size() > 0) {
                    keep = undoneList.remove(0); //remove the first element in the list from list so it won't get deleted
                    if (MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances.getBoolean()) {
                        keep.updateRelativeDates(dates.get(0));
                    }
                }
                Collection updatedOwners = deleteUnneededItemInstances(undoneList);
                List newUndoneList = new ArrayList();
                if (keep != null) {
                    newUndoneList.add(keep);
                }
                setListOfUndoneInstances(newUndoneList);
                //NB NO calculation of new instances - that haapens only on completion!
                DAO.getInstance().saveToParseLater(updatedOwners);
//                DAO.getInstance().saveToParseNow((ParseObject) keep);

            } else { //if completion RR is NOT dated, then we always keep one instance (infinite repeat)
                List<Item> undoneList = (List) getListOfUndoneInstances();
                if (Config.TEST) {
                    ASSERT.that(undoneList.size() == 0 || undoneList.contains(editedItem), "getListOfUndoneRepeatInstances does not contain originator=" + editedItem + " list=" + undoneList);
                }
                RepeatRuleObjectInterface keep = null;
                if (undoneList.size() > 0) {
                    keep = undoneList.remove(0); //remove the first element in the list from list so it won't get deleted
                }
                Collection updatedOwners = deleteUnneededItemInstances(undoneList);
                List newList = new ArrayList();
                if (keep != null) {
                    newList.add(keep);
                }
                setListOfUndoneInstances(newList);
                //NB NO calculation of new instances - that haapens only on completion!
                DAO.getInstance().saveToParseLater(updatedOwners);
//                DAO.getInstance().saveToParseNow(this);
            }
        } else { //getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE
            ASSERT.that(getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE);
            List<Item> undoneInstances = (List) getListOfUndoneInstances();  //reuse any existing instances (onlu holds originator if changed from onCompletion)
//            setListOfUndoneInstances(null); //"forget the previous repeat instances and start all over!" - delete old list of instances before calling calcNumberOfRepeats() and generateListOfDates() below!!
            List<Item> doneInstances = (List) getListOfDoneInstances();

            if (Config.TEST) {
                int currentTotalNumberOfUndoneInstances = undoneInstances.size();
                ASSERT.that(currentTotalNumberOfUndoneInstances != 0 || !doneInstances.isEmpty(), "RepeatRule called with both undone and done instances empty, at least one should contain an item");
            }
//            int numberOfRepeats = calcNumberOfRepeats(currentTotalNumberOfUndoneInstances);
            Set<ParseObject> updatedOwners = new HashSet<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)

            List newRepeatInstanceList = new ArrayList(); //hold new created instances
            Item instanceToCreateCopiesFrom
                    = getItemToUseForUpdatingOrCreationNewRepeatInstances(undoneInstances, doneInstances, editedItem); //= repeatRuleOriginator;
//<editor-fold defaultstate="collapsed" desc="comment">
//                Date startRepeatFromDate;
//                Date defaultStartDate = MyDate.getStartOfToday(); //ex-now: now is not good since it may miss instances generated for earlier today
//                boolean keepFirstGenerated;
//                if (isDatePatternChanged()) { //if rule edited from a completed item, replace by either the first unDone if available, or
//                    if (!oldUndoneRepeatInstanceList.isEmpty()) { //if available start copies from first undone instance (most likely to have right values), otherwise **
//                        instanceToCreateCopiesFrom = oldUndoneRepeatInstanceList.get(0); //instanceToCreateCopiesFrom will be set below when iterating over dates and reusing oldUndone instances // oldUndoneRepeatInstanceList.get(0);
//                    } else if (!doneInstances.isEmpty()) {
//                        instanceToCreateCopiesFrom = doneInstances.get(doneInstances.size() - 1); //instanceToCreateCopiesFrom will be set below when iterating over dates and reusing oldUndone instances // oldUndoneRepeatInstanceList.get(0);
//                    } else {
//                        instanceToCreateCopiesFrom = editedItem;
//                        if (((Item) editedItem).isDone()) {
//                            doneInstances.add(editedItem);
//                        } else {
//                            newRepeatInstanceList.add(editedItem);
//                        }
//                    }
//                    startRepeatFromDate = defaultStartDate; //instanceToCreateCopiesFrom.getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
//                    keepFirstGenerated = true;
//                    setDatePatternChanged(false); //reset to false so only used once
//                } else { //date pattern NOT changed, meaning we will only add/remove repeat instances (potentially remove all if edited RR doesnt' generate any add'l instances)
//                    if (oldUndoneRepeatInstanceList.size() > 0) {
//                        startRepeatFromDate = oldUndoneRepeatInstanceList.get(0).getRepeatStartTime(false); //start calc from already generated date and keep it (avoid regenerating from a past task where due date might have been changed)
//                        instanceToCreateCopiesFrom = oldUndoneRepeatInstanceList.get(0); //we know we'll generate at least one
//                        keepFirstGenerated = true;
//                    } else {
//                        instanceToCreateCopiesFrom = doneInstances.get(doneInstances.size() - 1);
//                        startRepeatFromDate = defaultStartDate;
//                        keepFirstGenerated = true;
//                    }
//                }
//</editor-fold>
            Date startRepeatFromDate = getStartRepeatUpdateFrom(undoneInstances, doneInstances, editedItem);
            if (!isDatePatternChanged()) { //if only number of repeats changed (less or more) don't recalculate dates, just extend/reduce existing list
                startRepeatFromDate = getStartDateWhenOnlyNumberOccurencesChanged(undoneInstances, doneInstances, startRepeatFromDate);
            }
            if (startRepeatFromDate.getTime() == 0) {
                startRepeatFromDate = MyDate.getStartOfToday(); //UI: start repetition from Now if no other date exists to use
            }
            setDatePatternChanged(false); //always reset!
//            setListOfUndoneInstances(null); //must add originator back again here to ensure future dates are calculated correctly
//            int numberOfRepeats = calcNumberOfRepeats(0);
            if (Config.TEST) {
                ASSERT.that(startRepeatFromDate != null && startRepeatFromDate.getTime() != 0, "no startRepeatFromDate=" + startRepeatFromDate);
            }

            List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(0), false, true); //recalcCurrentDate = 1;

            Item newRepeatInstance;
            Item repeatRefForInsertion = instanceToCreateCopiesFrom; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
            for (Date nextRepeatTime : dates) {
                newRepeatInstance = reuseOrMakeNextRepeatItem(undoneInstances, nextRepeatTime, instanceToCreateCopiesFrom);
//<editor-fold defaultstate="collapsed" desc="comment">
                if (newRepeatInstance != null) {
                    if (true || MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances.getBoolean()) { //settings doesn't make sense, you'll always want to update other dates
                        newRepeatInstance.updateRelativeDates(nextRepeatTime);
                    }
                } else {
                    newRepeatInstance = (Item) instanceToCreateCopiesFrom.createRepeatCopy(nextRepeatTime); //create next instance
//                    ItemAndListCommonInterface owner = insertIntoList2(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
                    ItemAndListCommonInterface updatedOwner = insertRepeatInstanceAtRightPositionInOwner(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
                    updatedOwners.add((ParseObject) updatedOwner);
                } //else: if newRepeatInstance is reused, there's no need to insert it into the owner list, it is already there
//</editor-fold>
//                ItemAndListCommonInterface updatedOwner = insertRepeatInstanceAtRightPositionInOwner(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
//                updatedOwners.add((ParseObject) updatedOwner);
                repeatRefForInsertion = newRepeatInstance;
                instanceToCreateCopiesFrom = newRepeatInstance; //always create copies in a chain (copying the just preceding instance)
                newRepeatInstanceList.add(newRepeatInstance);
            }
            setListOfUndoneInstances(newRepeatInstanceList);

            for (ItemAndListCommonInterface updateOwner : deleteUnneededItemInstances(undoneInstances)) { //delete unneeded instances and add their owners to list that need to be saved
                updatedOwners.add((ParseObject) updateOwner);
            }
            DAO.getInstance().saveToParseLater(updatedOwners);
        }
    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private void updateItemsWhenRuleCreatedOrEditedUsingOriginator(RepeatRuleObjectInterface repeatRuleOriginator, boolean firstTime) {
    ////<editor-fold defaultstate="collapsed" desc="comment">
    //        /**
    //         * //algorithm: ~refresh (reuse existing rule, to avoid
    //         * creating/deleting when edited) //get any already created repeat
    //         * instances //create dates for new/edited rule //for as many instances
    //         * as should be generated (encapsulate this! w moreInstances() and
    //         * ruleFinished(), moreInstances based on either #futureInstances vs
    //         * length of list or #daysAhead vs lastDayInList) //if an existing
    //         * instance exists, use it and set the date (whether new date is same or
    //         * not) //if no more instances, generate one //at the end, if there are
    //         * still existing instances, delete them (or cancel if actual!=0)
    //         *
    //         */
    ////</editor-fold>
    //        Item repeatRuleOriginatorItem = (Item) repeatRuleOriginator;
    ////        assert (true || getTotalNumberOfInstancesGeneratedSoFar() > 0); // ">0" - not the case if only one simultaneous instance
    //        if (getRepeatType() == REPEAT_TYPE_NO_REPEAT) { //handle case where a rule is deleted/disabled
    ////<editor-fold defaultstate="collapsed" desc="NO repeat">
    //            List undoneList = getListOfUndoneInstances();
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////            if (undoneList.size() == getTotalNumberOfInstancesGeneratedSoFar() + 1 && undoneList.size() > 0) {
    ////                //no repeating tasks have been completed so far
    ////                undoneList.remove(0); //UI: keep the first instance (original originator, even though the rule may be deleted on another instance)
    ////                while (undoneList.size() > 0) {
    ////                    ItemAndListCommonInterface elt = (ItemAndListCommonInterface) undoneList.remove(0);
    ////                    DAO.getInstance().deleteInBackground((ParseObject) elt); //delete future instances
    ////                }
    ////            }
    ////</editor-fold>
    //            if (Config.TEST) {
    //                ASSERT.that(/*undoneList.size() == 0 ||*/undoneList.contains(repeatRuleOriginator), "getListOfUndoneRepeatInstances does not contain originator=" + repeatRuleOriginator + " list=" + undoneList);
    //            }
    //            if (false) {
    //                undoneList.remove(0); //UI: keep the first instance (original originator, even though the rule may be deleted on another instance)
    //            }
    //            undoneList.remove(repeatRuleOriginator); //UI:keep the originator (the item editing the rule); remove repeatRuleOriginator from list In case it's still there) so it won't get deleted
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////            int instancesToBeDeleted = undoneList.size();
    ////            if (instancesToBeDeleted > 0) {
    //////                undoneList.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list In case it's still there) so it won't get deleted
    ////                instancesToBeDeleted = undoneList.size();
    ////            while (undoneList.size() > 0) { //delete all already generated instances
    ////                ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
    ////                DAO.getInstance().deleteAndWait(repeatRuleObject); //wait to ensure instances have disappeared from list
    ////                if (repeatRuleObject instanceof Item) {
    ////                    ((Item) repeatRuleObject).hardDelete();
    ////                } else {
    ////                    ((WorkSlot) repeatRuleObject).hardDelete();
    ////                }
    ////            }
    ////</editor-fold>
    //            deleteUnneededItemInstances(undoneList);
    ////            DAO.getInstance().saveNew(this, true); //update the rule
    //            DAO.getInstance().saveNew(this); //update the rule
    //            DAO.getInstance().saveNewExecuteUpdate();
    ////                DAO.getInstance().deleteBatch((List<ParseObject>)undoneList);
    ////            }
    ////don't change setTotalNumberOfInstancesGeneratedSoFar()!! UI: Keep the old count of generated instances (but should then show already generated instances to make it possible to understand what happens!)
    ////</editor-fold>
    //        } else if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) { //handle case where a rule is switched from repeat from due, to repeat from completed
    ////<editor-fold defaultstate="collapsed" desc="repeat from completed date">
    ////Called either on very first time (and should do nothing other than add originator to listOfUndone), or when rule is changed from Due to Completed
    //            if (true) {
    //                //cases: changed from/to datedCompletion, from Due to to undated/dateCompletion: in all cases, get rid of other future instances
    //                if (true || isDatedCompletion()) { //
    //                    List undoneList = getListOfUndoneInstances();
    //                    ASSERT.that(undoneList.size() == 0 || undoneList.contains(repeatRuleOriginator), "getListOfUndoneRepeatInstances does not contain originator=" + repeatRuleOriginator + " list=" + undoneList);
    ////            int instancesToBeDeleted = 0;
    ////            if (undoneList.size() > 0) {
    //                    ASSERT.that(undoneList.contains(repeatRuleOriginator), "updateRepeatInstancesWhenRuleWasCreatedOrEdited from an originator (" + repeatRuleOriginator + ") NOT in the undone list (" + undoneList);
    //                    undoneList.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list so it won't get deleted
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////            instancesToBeDeleted = undoneList.size();
    ////                while (undoneList.size() > 0) { //delete all already generated instances
    ////                    ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
    ////                    DAO.getInstance().deleteInBackground(repeatRuleObject); //optimization: deleteInBackground(List<Item>)!!
    ////                }
    ////</editor-fold>
    //                    deleteUnneededItemInstances(undoneList);
    ////            undoneList.add(repeatRuleOriginator); //add, or re-add originator
    //                    List newList = new ArrayList();
    //                    newList.add(repeatRuleOriginator);
    //                    setListOfUndoneInstances(newList);
    //                    //NB NO calculation of new instances - that haapens only on completion!
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////reset all counters in case the rule is later switched back to REPEAT_TYPE_FROM_DUE_DATE
    ////            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() - instancesToBeDeleted);
    ////            if (false) {
    ////                setLatestDateCompletedOrCancelled(null); //no need to reset this, will keeping it make it possible to change the rule back again?!
    ////            }            //            setLastGeneratedDateIfGreaterThanLastDate(null);
    ////            if (repeatRuleOriginator.getRepeatStartTime(false).getTime() != 0) {
    ////            if (repeatRuleOriginator.getRepeatStartTime(true).getTime() != 0) { //if rpeating from CompletedDate, then no need to start lastGeneratedDate (TODO!!!: only in case the date will be used if converting the RR to another type?!)
    //////                setLastGeneratedDate(repeatRuleOriginator.getRepeatStartTime(false)); //initialize LastGeneratedDate to due date of the originator (TODO is this correct?)
    ////                setLastGeneratedDate(repeatRuleOriginator.getRepeatStartTime(true), true); //initialize LastGeneratedDate to due date of the originator (TODO is this correct?)
    ////            }
    ////</editor-fold>
    //                    if (Config.TEST) {
    //                        checkRefs();
    //                    }
    ////                DAO.getInstance().saveNew(this, true);
    //                    DAO.getInstance().saveNew(this);
    //                    DAO.getInstance().saveNewExecuteUpdate();
    //                } else {
    //
    //                }
    //            } else {
    //                //ACTUALLY nothing to do for a RR on completion: only one instance, changes to RR takes effect upon completion!
    //                //NOOO. The rule may have changed from repeatFromDue to repeatOnCompleted!!!
    //            }
    ////</editor-fold>
    //        } else { //getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE
    //            ASSERT.that(getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE);
    //            if (false && firstTime) { //code in else branch now works whether first time or not
    ////<editor-fold defaultstate="collapsed" desc="first time">
    //////                ASSERT.that(getTotalNumberOfInstancesGeneratedSoFar() == 0, "getTotalNumberOfInstancesGeneratedSoFar() not 0 on firstTime call to repeat rule " + getTotalNumberOfInstancesGeneratedSoFar());
    ////                List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneInstances();  //reuse any existing instances
    //////<editor-fold defaultstate="collapsed" desc="comment">
    //////                if (false) {
    //////                    newRepeatInstanceList.add(repeatRuleOriginator); //now done when callinScreen on new rule
    //////                    setListOfUndoneInstances(newRepeatInstanceList); //MUST set the list to ensure the right number of existing instances is used in calcNumberOfRepeats() used below
    //////                }
    //////</editor-fold>
    //////                Date specifiedStartDate = getSpecifiedStartDate();
    //////                Date startRepeatFromTime = calcSubsetBeginningDate(specifiedStartDate);
    ////                Date now = new MyDate();
    ////                Date startRepeatFromDate = getStartDateForRefreshRule(now);
    //////                Date startRepeatFromTime = calcSubsetBeginningDate(startDate);
    //////                Date startRepeatFromTime = latestDoneDate != null ? latestDoneDate : now;
    ////                ASSERT.that(startRepeatFromDate != null);
    ////                boolean keepFirstGenerated = startRepeatFromDate.equals(now);
    ////                //if we're repeating from now, then DO keep the first generated
    ////                List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstGenerated);
    ////
    ////                ItemAndListCommonInterface owner = null;
    ////                RepeatRuleObjectInterface newRepeatInstance = null;
    ////                RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
    ////
    ////                ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
    ////                Set<ParseObject> updatedOwners = new HashSet<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
    ////
    ////                for (Date nextRepeatTime : dates) {
    ////                    newRepeatInstance = reuseNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
    ////                    if (newRepeatInstance == null) {
    ////                        newRepeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
    //////                    owner = repeatRefForInsertion.insertIntoList(newRepeatInstance); //store ownerlist to know if needs to be saved below
    ////                        owner = insertIntoList2(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
    ////                        updatedOwners.add((ParseObject) owner);
    ////                    }
    ////                    repeatRefForInsertion = newRepeatInstance; //ensure we always insert *after* any earlier inserted instances
    ////                    newRepeatInstanceList.add(newRepeatInstance);
    //////                    setLastGeneratedDate(nextRepeatTime); //if at least one additional repeatinstance was used, then use that date
    ////                }
    //////                setTotalNumberOfInstancesGeneratedSoFar(newRepeatInstanceList.size() - 1); //deduct 1 for the repeatRuleOriginator added above
    ////                setListOfUndoneInstances(newRepeatInstanceList);
    ////
    ////                DAO.getInstance().saveNew(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
    ////                DAO.getInstance().saveNew((ParseObject) this, (ParseObject) owner); //must save RR & new instances *before* saving list with new instances. save list where new instances were inserted
    ////                DAO.getInstance().saveNewExecuteUpdate(); //must save RR & new instances *before* saving list with new instances. save list where new instances were inserted
    ////</editor-fold>
    //            } else { //!firstTime <=> rule EDITED
    ////<editor-fold defaultstate="collapsed" desc="NOT first time">
    ////NOT first time - rule has been changed/edited -> recalculate/regenerate the complete set of future instances
    ////UI:
    //                List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneInstances();  //reuse any existing instances (onlu holds originator if changed from onCompletion)
    //
    //                setListOfUndoneInstances(null); //delete old list of instances before calling calcNumberOfRepeats() and generateListOfDates() below!!
    //
    //                int numberOfRepeats = calcNumberOfRepeats();
    //
    //                Set<ParseObject> updatedOwners = new HashSet<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
    //
    //                if (numberOfRepeats >= 1) { //check if we should keep the originator (without this check we may keep the originator even if no more instances should be generated!)
    //
    //                    oldUndoneRepeatInstanceList.remove(repeatRuleOriginator); //remove originator so it's not reused
    //                    List newRepeatInstanceList = new ArrayList(); //hold new created instances
    //                    newRepeatInstanceList.add(repeatRuleOriginator); //we keep originator
    //
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                setLastGeneratedDate(null); //when rule is edited, and recalculated, first reset the previous value of lastDateGenerated (which is used for xxx)
    ////                int oldTotalNumberInstancesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar(); //- oldRepeatInstanceList.size()-1; //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later), -1: alwlays remove the initial
    ////                int oldNumberOfUndoneRepeatInstances = oldUndoneRepeatInstanceList.size(); //always one more than getTotalNumberOfInstancesGeneratedSoFar() since it inclu
    ////                //if the
    ////                int completedNumberOfUndoneRepeatInstances = oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances
    ////                        - (oldNumberOfUndoneRepeatInstances > oldTotalNumberInstancesGeneratedSoFar ? 1 : 0); //oldNumber > oldTotal, then the originator
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                if (oldNumberOfUndoneRepeatInstances > oldTotalNumberInstancesGeneratedSoFar) { //if true, it means RR originator is included in list so need to +1 to compensate
    ////                    setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + 1); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!!
    ////                } else {
    ////                    setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!!
    ////                }
    ////</editor-fold>
    ////                setTotalNumberOfInstancesGeneratedSoFar(completedNumberOfUndoneRepeatInstances); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!! AND to keep precise track of already completed
    ////</editor-fold>
    //                    setListOfUndoneInstances(newRepeatInstanceList); //must add originator back again here to ensure future dates are calculated correctly
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////  ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == oldNumberOfUndoneRepeatInstances - 1, "inconsistency btw firstTime (empty list of previously generated instances) and nb of prev instances=" + oldTotalNumberInstancesGeneratedSoFar);
    ////                Date latestDoneDate = getLatestDateCompletedOrCancelled();
    ////                Date getNextRepeatAfterDate = latestDoneDate.getTime() != 0 ? latestDoneDate : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
    ////                Date getNextRepeatAfterDate = latestDoneDate.getTime() != MyDate.MIN_DATE ? latestDoneDate : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
    ////                Date getNextRepeatAfterDate = calcSubsetBeginningDate(latestDoneDate); //Re-start repeat from the last date a task was cancelled/done
    ////                Date getNextRepeatAfterDate = getStartDateForRefreshRule(); //Re-start repeat from the last date a task was cancelled/done
    ////</editor-fold>
    //                    RepeatRuleObjectInterface instanceToCreateCopiesFrom; //= repeatRuleOriginator;
    //                    Date startRepeatFromDate;
    //                    Date now = new MyDate();
    //
    //                    //if rule edited from a completed item, replace by either the first unDone if available, or
    //                    if (repeatRuleOriginatorItem.isDone()) {
    //                        //UI: editing any completed task is equivalent to editing the first undone (if any), otherwise repetition will start from Now using the edited item for copies
    //                        if (!oldUndoneRepeatInstanceList.isEmpty()) {
    //                            instanceToCreateCopiesFrom = oldUndoneRepeatInstanceList.get(0);
    //                            startRepeatFromDate = instanceToCreateCopiesFrom.getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
    //                        } else {
    //                            //else: even if there might be later doneInstances than the one edited, the rule will start from the edited one
    //                            instanceToCreateCopiesFrom = repeatRuleOriginator;
    //                            //UI: even if we *could* generate past instances by continuing the original date sequence (by continuing from due of last done instance)
    //                            //UI: it is unlikely that the user would have done the (past) task without it being in the list. So pretty safe to assume you only want to generate *future* instances
    //                            startRepeatFromDate = now; //start repeating from now, no
    //                        }
    //                    } else {
    //                        instanceToCreateCopiesFrom = repeatRuleOriginator;
    ////                Date startRepeatFromDate = getStartDateForRefreshRule(now);
    //                        startRepeatFromDate = repeatRuleOriginator.getRepeatStartTime(false); //UI: if editing a repeatRule, repeating will start from the edited rule
    ////                        if (startRepeatFromDate.getTime() == 0) { //UI: this shouldn't happen (since ScreenRR ensures due date is always set), BUT could happen if user deletes the due date
    ////                            startRepeatFromDate = now; //if no existing date to start from, then start from Now
    ////                        }//                Date startRepeatFromTime = latestDoneDate != null ? latestDoneDate : now;
    //                    }
    //                    if (startRepeatFromDate == null || startRepeatFromDate.getTime() == 0) {
    //                        startRepeatFromDate = now;
    //                    }
    ////                    ASSERT.that(startRepeatFromDate != null);
    //                    boolean keepFirstGenerated = startRepeatFromDate.equals(now);
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                boolean keepFirstDate = !getNextRepeatAfterDate.equals(repeatRuleOriginator.getRepeatStartTime(false)); //drop first date if it the same
    ////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, true); //recalcCurrentDate = 1;
    ////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstDate); //recalcCurrentDate = 1;
    ////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, false); //recalcCurrentDate = 1;
    ////</editor-fold>
    ////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstGenerated); //recalcCurrentDate = 1;
    //                    //call calcNumberOfRepeats() to re-calculate number of repeat after changes above
    //                    List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstGenerated); //recalcCurrentDate = 1;
    ////                List<Date> dates2 = generateListOfDates();
    //
    ////                ItemAndListCommonInterface owner = null;
    //                    RepeatRuleObjectInterface newRepeatInstance;
    ////                    RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
    //                    RepeatRuleObjectInterface repeatRefForInsertion = instanceToCreateCopiesFrom; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
    //
    ////                Set<ParseObject> updatedOwners = new HashSet<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
    ////                updatedOwners.add(((IrepeatRuleOriginator.get)
    //                    for (Date nextRepeatTime : dates) {
    ////                        newRepeatInstance = reuseNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
    //                        newRepeatInstance = reuseNextRepeatInstance(oldUndoneRepeatInstanceList, nextRepeatTime);
    //                        if (newRepeatInstance == null) {
    ////                            newRepeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
    //                            newRepeatInstance = instanceToCreateCopiesFrom.createRepeatCopy(nextRepeatTime); //create next instance
    ////                        ownerList = repeatRefForInsertion.insertIntoList(newRepeatInstance); //store ownerlist to know if needs to be saved below
    //                            ItemAndListCommonInterface owner = insertIntoList2(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
    //                            updatedOwners.add((ParseObject) owner);
    //                        } //else: if newRepeatInstance is reused, there's no need to insert it into the owner list, it is already there
    //                        repeatRefForInsertion = newRepeatInstance;
    //                        instanceToCreateCopiesFrom = newRepeatInstance; //always create copies in a chain (copying the just preceding instance)
    //                        newRepeatInstanceList.add(newRepeatInstance);
    ////                    setLastGeneratedDate(nextRepeatTime); //save last date generated
    //                    }
    //                    setListOfUndoneInstances(newRepeatInstanceList);
    //                    DAO.getInstance().saveNew(newRepeatInstanceList); //save new instances
    //                }
    //
    //                for (ItemAndListCommonInterface updateOwner : deleteUnneededItemInstances(oldUndoneRepeatInstanceList)) { //delete unneeded instances and add their owners to list that need to be saved
    //                    updatedOwners.add((ParseObject) updateOwner);
    //                }
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar(), "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule");
    ////                int newTotal = completedNumberOfUndoneRepeatInstances + newRepeatInstanceList.size();
    ////                setTotalNumberOfInstancesGeneratedSoFar(newTotal);
    ////                if (false&&Config.TEST) {
    ////                    checkRefs();
    ////                }
    ////                DAO.getInstance().saveInBackground(this);
    ////                if (ownerList != null)
    ////                    DAO.getInstance().saveInBackground((ParseObject) ownerList); //save list where new instances were inserted
    ////                    DAO.getInstance().saveInBackground((List<ParseObject>)updatedOwners); //save list where new instances were inserted
    ////                setListOfUndoneInstances(newRepeatInstanceList);
    ////                DAO.getInstance().saveNew(newRepeatInstanceList); //save new instances
    ////                DAO.getInstance().saveNew((List<ParseObject>) updatedOwners, true); //save list where new instances were inserted
    ////</editor-fold>
    //                DAO.getInstance().saveNew((ParseObject) this); //save list where new instances were inserted
    //                DAO.getInstance().saveNew(updatedOwners); //save list where new instances were inserted
    //                DAO.getInstance().saveNewExecuteUpdate(); //save list where new instances were inserted
    //            }
    ////</editor-fold>
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////            //new rule, changed REPEAT_TYPE_FROM_DUE_DATE, or rule was changed from REPEAT_TYPE_FROM_COMPLETED_DATE to REPEAT_TYPE_FROM_DUE_DATE
    //////            List<? extends RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
    //////            List<ItemAndListCommonInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
    ////
    ////            //first time we generate instances for a rule, we need to add the originator and only generate additional instances
    ////            //next time all dates mu be generated and the originator may change date
    //////            boolean firstTime = oldTotalNumberInstancesGeneratedSoFar == 0; // || !oldUndoneRepeatInstanceList.contains(repeatRuleOriginator);
    ////            ASSERT.that(!firstTime || oldTotalNumberInstancesGeneratedSoFar == 0, "inconsistency btw firstTime (empty list of previously generated instances) and nb of prev instances=" + oldTotalNumberInstancesGeneratedSoFar);
    ////            Date startRepeatFromTime;
    ////            List<Date> dates;
    ////            int recalcCurrentDate = 0;
    ////            if (firstTime || getLatestDateCompletedOrCancelled() == null) {
    ////                if (false) {
    ////                    newRepeatInstanceList.add(repeatRuleOriginator);
    ////                    oldNumberOfUndoneRepeatInstances++; //add 1 to compensentae for adding originator (when deducting newRepeatInstanceList.size()
    ////                }
    ////                if (false) startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    ////                startRepeatFromTime = getSpecifiedStartDateD();
    ////                if (startRepeatFromTime == null) {
    ////                    ASSERT.that(false, "no date from getSpecifiedStartDateD() -shouldn't happen, repeatRule=" + this);
    ////                    startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    ////                    setSpecifiedStartDate(startRepeatFromTime);
    ////                }
    //////<editor-fold defaultstate="collapsed" desc="comment">
    //////                if (false) {
    //////                    if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    ////////                    dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead());
    //////                        dates = generateListOfDates(startRepeatFromTime, calcSubEndDate(), calcNumberOfRepeats());
    //////                    } else {
    //////                        Date subEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
    ////////                    dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
    //////                        dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
    //////                    }
    //////                }
    //////</editor-fold>
    ////            } else {
    ////                startRepeatFromTime = getLatestDateCompletedOrCancelled(); //Re-start repeat from the last date a task was cancelled/done
    ////                recalcCurrentDate = 1;
    //////<editor-fold defaultstate="collapsed" desc="comment">
    //////                if (false) {
    //////                    if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    //////                        dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead() + 1); //1: need to *re-*generate the full number of dates if possible (including possibly the originator)
    //////                    } else {
    //////                        Date subEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
    //////                        dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
    //////                    }
    //////                    ASSERT.that(oldUndoneRepeatInstanceList.contains(repeatRuleOriginator));
    //////                    //move to head of list to make sure the originator becomes the first instance in the repeat sequence:
    //////                    if (false) { //DON'T since thiws may/will not keep the items in due date order
    //////                        oldUndoneRepeatInstanceList.remove(repeatRuleOriginator);
    //////                        oldUndoneRepeatInstanceList.add(0, repeatRuleOriginator);
    //////                    }
    //////                }
    //////</editor-fold>
    ////            }
    //////            Date startRepeatFromTime = (firstTime||getLatestDateCompletedOrCancelled()==null)?getSpecifiedStartDateD():getLatestDateCompletedOrCancelled():
    ////            dates = generateListOfDates(startRepeatFromTime, calcSubsetEndDate(startRepeatFromTime), calcNumberOfRepeats() + recalcCurrentDate);
    ////
    ////            //not the first time, so start repetition from the first still active instance
    ////            ASSERT.that(true, "shouldn't happen?! repeatRule=" + this + ", item=" + repeatRuleOriginator);
    ////
    ////            ItemAndListCommonInterface ownerList = null;
    ////            RepeatRuleObjectInterface newRepeatInstance;
    ////            RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
    ////            Date nextRepeatTime = null;
    ////            for (int i = 0, size = dates.size(); i < size; i++) {
    ////                nextRepeatTime = dates.get(i);
    ////                newRepeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
    ////                ownerList = repeatRefForInsertion.insertIntoList(newRepeatInstance);
    ////                repeatRefForInsertion = newRepeatInstance;
    ////                newRepeatInstanceList.add(newRepeatInstance);
    //////                setLastGeneratedDate(nextRepeatTime); //save last date generated
    ////            }
    ////            if (nextRepeatTime != null) {
    ////                setLastGeneratedDate(nextRepeatTime); //save last date generated
    ////            }
    ////
    ////            //delete any 'left over' instances //TODO check that deleting them doesn't affect
    //////<editor-fold defaultstate="collapsed" desc="comment">
    //////            while (oldUndoneRepeatInstanceList.size() > 0) {
    //////                RepeatRuleObjectInterface obsoleteInstance = oldUndoneRepeatInstanceList.remove(0);
    //////                if (obsoleteInstance instanceof ParseObject) {
    //////                    if (obsoleteInstance instanceof Item && ((Item) obsoleteInstance).getActualEffort() != 0) { //for Items, not WorkSlots
    //////                        ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //Cancel instead of delete if time has been registered (to avoid losing it)
    //////                        DAO.getInstance().save((Item) obsoleteInstance); //must save it
    //////                    } else {
    ////////                obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
    //////                        DAO.getInstance().delete((ParseObject) obsoleteInstance);   //TODO!!!! check that this deletes it in all lists, categories etc!
    //////                    }
    //////                } //else: not saved to Parse
    //////            }
    //////</editor-fold>
    ////            deleteSuperfluousRepeatInstances(oldUndoneRepeatInstanceList);
    ////
    ////            if (this.getObjectIdP() == null)
    ////                DAO.getInstance().saveInBackground(this); //save repeatRule if not already done, needed so that repeatInstances reference a rule with an ObjectId
    ////            DAO.getInstance().saveInBackground(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
    ////            ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar(), "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule");
    ////            setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + newRepeatInstanceList.size());
    ////            if (firstTime) {
    ////                newRepeatInstanceList.add(0, repeatRuleOriginator);
    //////                oldNumberOfUndoneRepeatInstances++; //add 1 to compensentae for adding originator (when deducting newRepeatInstanceList.size()
    ////            }
    ////            setListOfUndoneRepeatInstances(newRepeatInstanceList);
    //////        oldTotalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
    ////            DAO.getInstance().saveInBackground(this);
    ////            if (ownerList != null)
    ////                DAO.getInstance().saveInBackground((ParseObject) ownerList); //save list where new instances were inserted
    ////        }
    ////</editor-fold>
    //        }
    ////        DAO.getInstance().saveInBackground(this);
    //    }
    //</editor-fold>
    ////<editor-fold defaultstate="collapsed" desc="comment">
    //    private void updateItemsWhenRuleWasCreatedOrEditedOLDXXX(RepeatRuleObjectInterface repeatRuleOriginator, boolean firstTime) {
    ////<editor-fold defaultstate="collapsed" desc="comment">
    //        /**
    //         * //algorithm: ~refresh (reuse existing rule, to avoid
    //         * creating/deleting when edited) //get any already created repeat
    //         * instances //create dates for new/edited rule //for as many instances
    //         * as should be generated (encapsulate this! w moreInstances() and
    //         * ruleFinished(), moreInstances based on either #futureInstances vs
    //         * length of list or #daysAhead vs lastDayInList) //if an existing
    //         * instance exists, use it and set the date (whether new date is same or
    //         * not) //if no more instances, generate one //at the end, if there are
    //         * still existing instances, delete them (or cancel if actual!=0)
    //         *
    //         */
    ////</editor-fold>
    ////        assert (true || getTotalNumberOfInstancesGeneratedSoFar() > 0); // ">0" - not the case if only one simultaneous instance
    //        if (getRepeatType() == REPEAT_TYPE_NO_REPEAT) { //handle case where a rule is deleted/disabled
    ////<editor-fold defaultstate="collapsed" desc="NO repeat">
    //            List undoneList = getListOfUndoneInstances();
    ////            if (undoneList.size() == getTotalNumberOfInstancesGeneratedSoFar() + 1 && undoneList.size() > 0) {
    ////                //no repeating tasks have been completed so far
    ////                undoneList.remove(0); //UI: keep the first instance (original originator, even though the rule may be deleted on another instance)
    ////                while (undoneList.size() > 0) {
    ////                    ItemAndListCommonInterface elt = (ItemAndListCommonInterface) undoneList.remove(0);
    ////                    DAO.getInstance().deleteInBackground((ParseObject) elt); //delete future instances
    ////                }
    ////            }
    //            if (Config.TEST) {
    //                ASSERT.that(/*undoneList.size() == 0 ||*/undoneList.contains(repeatRuleOriginator), "getListOfUndoneRepeatInstances does not contain originator=" + repeatRuleOriginator + " list=" + undoneList);
    //            }
    //            if (false) {
    //                undoneList.remove(0); //UI: keep the first instance (original originator, even though the rule may be deleted on another instance)
    //            }
    //            undoneList.remove(repeatRuleOriginator); //UI:keep the originator (the item editing the rule); remove repeatRuleOriginator from list In case it's still there) so it won't get deleted
    ////            int instancesToBeDeleted = undoneList.size();
    ////            if (instancesToBeDeleted > 0) {
    //////                undoneList.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list In case it's still there) so it won't get deleted
    ////                instancesToBeDeleted = undoneList.size();
    ////            while (undoneList.size() > 0) { //delete all already generated instances
    ////                ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
    ////                DAO.getInstance().deleteAndWait(repeatRuleObject); //wait to ensure instances have disappeared from list
    ////                if (repeatRuleObject instanceof Item) {
    ////                    ((Item) repeatRuleObject).hardDelete();
    ////                } else {
    ////                    ((WorkSlot) repeatRuleObject).hardDelete();
    ////                }
    ////            }
    //            deleteSuperfluousInstances(undoneList);
    //            DAO.getInstance().saveNew(this, true); //update the rule
    ////                DAO.getInstance().deleteBatch((List<ParseObject>)undoneList);
    ////            }
    ////don't change setTotalNumberOfInstancesGeneratedSoFar()!! UI: Keep the old count of generated instances (but should then show already generated instances to make it possible to understand what happens!)
    ////</editor-fold>
    //        } else if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) { //handle case where a rule is switched from repeat from due, to repeat from completed
    ////<editor-fold defaultstate="collapsed" desc="repeat from completed date">
    ////Called either on very first time (and should do nothing other than add originator to listOfUndone), or when rule is changed from Due to Completed
    //            List undoneList = getListOfUndoneInstances();
    //            ASSERT.that(undoneList.size() == 0 || undoneList.contains(repeatRuleOriginator), "getListOfUndoneRepeatInstances does not contain originator=" + repeatRuleOriginator + " list=" + undoneList);
    //            int instancesToBeDeleted = 0;
    ////            if (undoneList.size() > 0) {
    //            ASSERT.that(undoneList.contains(repeatRuleOriginator), "updateRepeatInstancesWhenRuleWasCreatedOrEdited from an originator (" + repeatRuleOriginator + ") NOT in the undone list (" + undoneList);
    //            undoneList.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list so it won't get deleted
    //            instancesToBeDeleted = undoneList.size();
    ////                while (undoneList.size() > 0) { //delete all already generated instances
    ////                    ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
    ////                    DAO.getInstance().deleteInBackground(repeatRuleObject); //optimization: deleteInBackground(List<Item>)!!
    ////                }
    //            deleteSuperfluousInstances(undoneList);
    ////            }
    ////            undoneList.add(repeatRuleOriginator); //add, or re-add originator
    //            List newList = new ArrayList();
    //            newList.add(repeatRuleOriginator);
    //            setListOfUndoneInstances(newList);
    ////reset all counters in case the rule is later switched back to REPEAT_TYPE_FROM_DUE_DATE
    ////            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() - instancesToBeDeleted);
    ////            if (false) {
    ////                setLatestDateCompletedOrCancelled(null); //no need to reset this, will keeping it make it possible to change the rule back again?!
    ////            }            //            setLastGeneratedDateIfGreaterThanLastDate(null);
    //////            if (repeatRuleOriginator.getRepeatStartTime(false).getTime() != 0) {
    ////            if (repeatRuleOriginator.getRepeatStartTime(true).getTime() != 0) { //if rpeating from CompletedDate, then no need to start lastGeneratedDate (TODO!!!: only in case the date will be used if converting the RR to another type?!)
    //////                setLastGeneratedDate(repeatRuleOriginator.getRepeatStartTime(false)); //initialize LastGeneratedDate to due date of the originator (TODO is this correct?)
    ////                setLastGeneratedDate(repeatRuleOriginator.getRepeatStartTime(true), true); //initialize LastGeneratedDate to due date of the originator (TODO is this correct?)
    ////            }
    //            if (Config.TEST) {
    //                checkRefs();
    //            }
    //
    //            if (true) { //done below
    //                DAO.getInstance().saveNew(this, true);
    //            }
    ////</editor-fold>
    //        } else { //getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE
    //            ASSERT.that(getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE);
    //            if (firstTime) {
    ////<editor-fold defaultstate="collapsed" desc="first time">
    ////                ASSERT.that(getTotalNumberOfInstancesGeneratedSoFar() == 0, "getTotalNumberOfInstancesGeneratedSoFar() not 0 on firstTime call to repeat rule " + getTotalNumberOfInstancesGeneratedSoFar());
    ////                List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = new ArrayList<>();  //reuse any existing instances
    //                ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                if (false) {
    ////                    if (this.getObjectIdP() == null) {
    ////                        if (Config.TEST) {
    ////                            checkRefs();
    ////                        }
    ////                        DAO.getInstance().saveInBackground(this); //save in case it's a new created repeat rule
    ////                    }
    ////                    if (((ParseObject) repeatRuleOriginator).getObjectIdP() == null) {
    ////                        DAO.getInstance().saveInBackground((ParseObject) repeatRuleOriginator); //save in case it's a new created item
    ////                    }
    ////                }
    ////</editor-fold>
    //                newRepeatInstanceList.add(repeatRuleOriginator);
    //                setListOfUndoneInstances(newRepeatInstanceList); //MUST set the list to ensure the right number of existing instances is used in calcNumberOfRepeats() used below
    //
    //                Date startRepeatFromTime = getSpecifiedStartDate();
    //                ASSERT.that(startRepeatFromTime != null);
    //
    ////                List<Date> dates = generateListOfDates(startRepeatFromTime, calcSubsetEndDate(startRepeatFromTime), calcNumberOfRepeats());
    //                List<Date> dates = generateListOfDates(startRepeatFromTime, calcSubsetEndDate(), calcNumberOfRepeats(), false, false);
    //
    //                ItemAndListCommonInterface owner = null;
    //                RepeatRuleObjectInterface newRepeatInstance = null;
    //                RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
    //
    //                for (Date nextRepeatTime : dates) {
    //                    newRepeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
    ////                    owner = repeatRefForInsertion.insertIntoList(newRepeatInstance); //store ownerlist to know if needs to be saved below
    //                    owner = insertIntoList2(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
    //                    repeatRefForInsertion = newRepeatInstance; //ensure we always insert *after* any earlier inserted instances
    //                    newRepeatInstanceList.add(newRepeatInstance);
    ////                    setLastGeneratedDate(nextRepeatTime); //if at least one additional repeatinstance was used, then use that date
    //                }
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                if (false) {
    ////                    if (false && this.getObjectIdP() == null) {
    ////                        if (Config.TEST) {
    ////                            checkRefs();
    ////                        }
    ////                        DAO.getInstance().saveInBackground(this);
    ////                    } //save repeatRule if not already done, needed so that repeatInstances reference a rule with an ObjectId
    ////
    ////                    DAO.getInstance().saveInBackground(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
    ////                }
    ////</editor-fold>
    ////                setTotalNumberOfInstancesGeneratedSoFar(newRepeatInstanceList.size() - 1); //deduct 1 for the repeatRuleOriginator added above
    //                setListOfUndoneInstances(newRepeatInstanceList);
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                if (Config.TEST) {
    ////                    checkRefs();
    ////                }
    ////                if (false) {
    ////                    DAO.getInstance().saveInBackground(this);
    ////
    ////                    if (ownerList != null) {
    ////                        DAO.getInstance().saveInBackground((ParseObject) ownerList); //save list where new instances were inserted
    ////                    }
    ////                }
    ////</editor-fold>
    ////                if (false) {
    ////                    DAO.getInstance().saveInBackground(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
    ////                    DAO.getInstance().saveInBackground((ParseObject) this, (ParseObject) owner); //save list where new instances were inserted
    ////                }
    //                DAO.getInstance().saveNew(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
    //                DAO.getInstance().saveNew(true, (ParseObject) this, (ParseObject) owner); //must save RR & new instances *before* saving list with new instances. save list where new instances were inserted
    ////</editor-fold>
    //            } else { //!firstTime
    ////<editor-fold defaultstate="collapsed" desc="NOT first time">
    ////NOT first time - rule has been changed/edited -> recalculate/regenerate the complete set of future instances
    ////UI:
    //                List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneInstances();  //reuse any existing instances
    //////<editor-fold defaultstate="collapsed" desc="comment">
    ////                setLastGeneratedDate(null); //when rule is edited, and recalculated, first reset the previous value of lastDateGenerated (which is used for xxx)
    //
    ////                int oldTotalNumberInstancesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar(); //- oldRepeatInstanceList.size()-1; //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later), -1: alwlays remove the initial
    ////                int oldNumberOfUndoneRepeatInstances = oldUndoneRepeatInstanceList.size(); //always one more than getTotalNumberOfInstancesGeneratedSoFar() since it inclu
    ////                //if the
    ////                int completedNumberOfUndoneRepeatInstances = oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances
    ////                        - (oldNumberOfUndoneRepeatInstances > oldTotalNumberInstancesGeneratedSoFar ? 1 : 0); //oldNumber > oldTotal, then the originator
    //////                if (oldNumberOfUndoneRepeatInstances > oldTotalNumberInstancesGeneratedSoFar) { //if true, it means RR originator is included in list so need to +1 to compensate
    //////                    setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + 1); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!!
    //////                } else {
    //////                    setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!!
    //////                }
    ////                setTotalNumberOfInstancesGeneratedSoFar(completedNumberOfUndoneRepeatInstances); //update before calling calcNumberOfRepeats() in call to generateListOfDates() below!! AND to keep precise track of already completed
    //////</editor-fold>
    //                setListOfUndoneInstances(null); //delete old list of instances before calling generateListOfDates() below!!
    //
    ////  ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == oldNumberOfUndoneRepeatInstances - 1, "inconsistency btw firstTime (empty list of previously generated instances) and nb of prev instances=" + oldTotalNumberInstancesGeneratedSoFar);
    //                Date latestDateCompletedOrCancelled = getLatestDateCompletedOrCancelled();
    //                Date getNextRepeatAfterDate = latestDateCompletedOrCancelled.getTime() != 0 ? latestDateCompletedOrCancelled : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
    ////                List<Date> dates = generateListOfDates(startRepeatFromTime, calcSubsetEndDate(startRepeatFromTime), calcNumberOfRepeats() + 1); //recalcCurrentDate = 1;
    //                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, true); //recalcCurrentDate = 1;
    //
    //                ItemAndListCommonInterface ownerList = null;
    //                RepeatRuleObjectInterface newRepeatInstance;
    //                RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
    ////                List<ItemAndListCommonInterface> updatedOwners = new ArrayList<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
    ////                List<ItemAndListCommonInterface> updatedOwners = new ArrayList<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
    //
    //                List newRepeatInstanceList = new ArrayList(); //hold new created instances
    ////                List<ParseObject> updatedOwners = new ArrayList<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
    //                Set<ParseObject> updatedOwners = new HashSet<>(); //gather all updated owners (in case eg a repeat instance was moved to another owner!)
    //
    //                for (Date nextRepeatTime : dates) {
    ////                    newRepeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
    //                    newRepeatInstance = reuseNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
    //                    if (newRepeatInstance == null) {
    //                        newRepeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
    ////                        ownerList = repeatRefForInsertion.insertIntoList(newRepeatInstance); //store ownerlist to know if needs to be saved below
    //                        ownerList = insertIntoList2(repeatRefForInsertion, newRepeatInstance); //store ownerlist to know if needs to be saved below
    ////                        if (!updatedOwners.contains(ownerList)) {
    //                        updatedOwners.add((ParseObject) ownerList);
    ////                        }
    //                    } //else: if newRepeatInstance is reused, there's no need to insert it into the owner list, it is already there
    //                    repeatRefForInsertion = newRepeatInstance;
    //                    newRepeatInstanceList.add(newRepeatInstance);
    ////                    setLastGeneratedDate(nextRepeatTime); //save last date generated
    //                }
    //
    ////                deleteSuperfluousRepeatInstances(oldUndoneRepeatInstanceList);
    //                for (ItemAndListCommonInterface elt : deleteSuperfluousRepeatInstances(oldUndoneRepeatInstanceList)) { //delete unneeded instances and add their owners to list that need to be saved
    ////                    if (!updatedOwners.contains(elt)) {
    //                    updatedOwners.add((ParseObject) elt);
    ////                    }
    //                }
    //
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                if (false && this.getObjectIdP() == null) {
    ////                    if (Config.TEST) {
    ////                        checkRefs();
    ////                    }
    ////                    DAO.getInstance().saveInBackground(this);
    ////                } //save repeatRule if not already done, needed so that repeatInstances reference a rule with an ObjectId
    ////
    ////                if (false) { //false: unsaved instances will be saved in background
    ////                    DAO.getInstance().saveInBackground(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
    ////                }
    ////</editor-fold>
    ////                ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar(), "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule");
    ////                setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + newRepeatInstanceList.size());
    ////                int newTotal = completedNumberOfUndoneRepeatInstances + newRepeatInstanceList.size();
    ////                setTotalNumberOfInstancesGeneratedSoFar(newTotal);
    //                setListOfUndoneInstances(newRepeatInstanceList);
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                if (false&&Config.TEST) {
    ////                    checkRefs();
    ////                }
    ////                DAO.getInstance().saveInBackground(this);
    //
    ////                if (ownerList != null)
    ////                    DAO.getInstance().saveInBackground((ParseObject) ownerList); //save list where new instances were inserted
    ////                    DAO.getInstance().saveInBackground((List<ParseObject>)updatedOwners); //save list where new instances were inserted
    ////</editor-fold>
    //                DAO.getInstance().saveNew(newRepeatInstanceList, false); //save new instances
    //                DAO.getInstance().saveNew((ParseObject) this, false); //save list where new instances were inserted
    ////                DAO.getInstance().saveNew((List<ParseObject>) updatedOwners, true); //save list where new instances were inserted
    //                DAO.getInstance().saveNew(updatedOwners, true); //save list where new instances were inserted
    //            }
    ////</editor-fold>
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////            //new rule, changed REPEAT_TYPE_FROM_DUE_DATE, or rule was changed from REPEAT_TYPE_FROM_COMPLETED_DATE to REPEAT_TYPE_FROM_DUE_DATE
    //////            List<? extends RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
    //////            List<ItemAndListCommonInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
    ////
    ////            //first time we generate instances for a rule, we need to add the originator and only generate additional instances
    ////            //next time all dates mu be generated and the originator may change date
    //////            boolean firstTime = oldTotalNumberInstancesGeneratedSoFar == 0; // || !oldUndoneRepeatInstanceList.contains(repeatRuleOriginator);
    ////            ASSERT.that(!firstTime || oldTotalNumberInstancesGeneratedSoFar == 0, "inconsistency btw firstTime (empty list of previously generated instances) and nb of prev instances=" + oldTotalNumberInstancesGeneratedSoFar);
    ////            Date startRepeatFromTime;
    ////            List<Date> dates;
    ////            int recalcCurrentDate = 0;
    ////            if (firstTime || getLatestDateCompletedOrCancelled() == null) {
    ////                if (false) {
    ////                    newRepeatInstanceList.add(repeatRuleOriginator);
    ////                    oldNumberOfUndoneRepeatInstances++; //add 1 to compensentae for adding originator (when deducting newRepeatInstanceList.size()
    ////                }
    ////                if (false) startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    ////                startRepeatFromTime = getSpecifiedStartDateD();
    ////                if (startRepeatFromTime == null) {
    ////                    ASSERT.that(false, "no date from getSpecifiedStartDateD() -shouldn't happen, repeatRule=" + this);
    ////                    startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    ////                    setSpecifiedStartDate(startRepeatFromTime);
    ////                }
    //////<editor-fold defaultstate="collapsed" desc="comment">
    //////                if (false) {
    //////                    if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    ////////                    dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead());
    //////                        dates = generateListOfDates(startRepeatFromTime, calcSubEndDate(), calcNumberOfRepeats());
    //////                    } else {
    //////                        Date subEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
    ////////                    dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
    //////                        dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
    //////                    }
    //////                }
    //////</editor-fold>
    ////            } else {
    ////                startRepeatFromTime = getLatestDateCompletedOrCancelled(); //Re-start repeat from the last date a task was cancelled/done
    ////                recalcCurrentDate = 1;
    //////<editor-fold defaultstate="collapsed" desc="comment">
    //////                if (false) {
    //////                    if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    //////                        dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead() + 1); //1: need to *re-*generate the full number of dates if possible (including possibly the originator)
    //////                    } else {
    //////                        Date subEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
    //////                        dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
    //////                    }
    //////                    ASSERT.that(oldUndoneRepeatInstanceList.contains(repeatRuleOriginator));
    //////                    //move to head of list to make sure the originator becomes the first instance in the repeat sequence:
    //////                    if (false) { //DON'T since thiws may/will not keep the items in due date order
    //////                        oldUndoneRepeatInstanceList.remove(repeatRuleOriginator);
    //////                        oldUndoneRepeatInstanceList.add(0, repeatRuleOriginator);
    //////                    }
    //////                }
    //////</editor-fold>
    ////            }
    //////            Date startRepeatFromTime = (firstTime||getLatestDateCompletedOrCancelled()==null)?getSpecifiedStartDateD():getLatestDateCompletedOrCancelled():
    ////            dates = generateListOfDates(startRepeatFromTime, calcSubsetEndDate(startRepeatFromTime), calcNumberOfRepeats() + recalcCurrentDate);
    ////
    ////            //not the first time, so start repetition from the first still active instance
    ////            ASSERT.that(true, "shouldn't happen?! repeatRule=" + this + ", item=" + repeatRuleOriginator);
    ////
    ////            ItemAndListCommonInterface ownerList = null;
    ////            RepeatRuleObjectInterface newRepeatInstance;
    ////            RepeatRuleObjectInterface repeatRefForInsertion = repeatRuleOriginator; //UI: used to insert multiple instances one after the other, to avoid eg if inserting at head of list that they get inserted in decreasing due date order
    ////            Date nextRepeatTime = null;
    ////            for (int i = 0, size = dates.size(); i < size; i++) {
    ////                nextRepeatTime = dates.get(i);
    ////                newRepeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
    ////                ownerList = repeatRefForInsertion.insertIntoList(newRepeatInstance);
    ////                repeatRefForInsertion = newRepeatInstance;
    ////                newRepeatInstanceList.add(newRepeatInstance);
    //////                setLastGeneratedDate(nextRepeatTime); //save last date generated
    ////            }
    ////            if (nextRepeatTime != null) {
    ////                setLastGeneratedDate(nextRepeatTime); //save last date generated
    ////            }
    ////
    ////            //delete any 'left over' instances //TODO check that deleting them doesn't affect
    //////<editor-fold defaultstate="collapsed" desc="comment">
    //////            while (oldUndoneRepeatInstanceList.size() > 0) {
    //////                RepeatRuleObjectInterface obsoleteInstance = oldUndoneRepeatInstanceList.remove(0);
    //////                if (obsoleteInstance instanceof ParseObject) {
    //////                    if (obsoleteInstance instanceof Item && ((Item) obsoleteInstance).getActualEffort() != 0) { //for Items, not WorkSlots
    //////                        ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //Cancel instead of delete if time has been registered (to avoid losing it)
    //////                        DAO.getInstance().save((Item) obsoleteInstance); //must save it
    //////                    } else {
    ////////                obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
    //////                        DAO.getInstance().delete((ParseObject) obsoleteInstance);   //TODO!!!! check that this deletes it in all lists, categories etc!
    //////                    }
    //////                } //else: not saved to Parse
    //////            }
    //////</editor-fold>
    ////            deleteSuperfluousRepeatInstances(oldUndoneRepeatInstanceList);
    ////
    ////            if (this.getObjectIdP() == null)
    ////                DAO.getInstance().saveInBackground(this); //save repeatRule if not already done, needed so that repeatInstances reference a rule with an ObjectId
    ////            DAO.getInstance().saveInBackground(newRepeatInstanceList); //save all instances (including the possibly unsaved Item that created the repeatRule
    ////            ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar(), "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule");
    ////            setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + newRepeatInstanceList.size());
    ////            if (firstTime) {
    ////                newRepeatInstanceList.add(0, repeatRuleOriginator);
    //////                oldNumberOfUndoneRepeatInstances++; //add 1 to compensentae for adding originator (when deducting newRepeatInstanceList.size()
    ////            }
    ////            setListOfUndoneRepeatInstances(newRepeatInstanceList);
    //////        oldTotalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
    ////            DAO.getInstance().saveInBackground(this);
    ////            if (ownerList != null)
    ////                DAO.getInstance().saveInBackground((ParseObject) ownerList); //save list where new instances were inserted
    ////        }
    ////</editor-fold>
    //        }
    ////        DAO.getInstance().saveInBackground(this);
    //    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void updateRepeatInstancesWhenRuleWasCreatedOrEditedXXX(RepeatRuleObjectInterface repeatRuleOriginator) {
    ////<editor-fold defaultstate="collapsed" desc="comment">
    //        /**
    //         * //algorithm: ~removeFromCache (reuse existing rule, to avoid
    //         * creating/deleting when edited) //get any already created repeat
    //         * instances //create dates for new/edited rule //for as many instances
    //         * as should be generated (encapsulate this! w moreInstances() and
    //         * ruleFinished(), moreInstances based on either #futureInstances vs
    //         * length of list or #daysAhead vs lastDayInList) //if an existing
    //         * instance exists, use it and set the date (whether new date is same or
    //         * not) //if no more instances, generate one //at the end, if there are
    //         * still existing instances, delete them (or cancel if actual!=0)
    //         *
    //         */
    ////</editor-fold>
    //        boolean firstTime = (getTotalNumberOfInstancesGeneratedSoFar() == 0);
    //
    //        if (getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE) {
    //            List<RepeatRuleObjectInterface> oldRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
    //            ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
    //            RepeatRuleObjectInterface repeatInstance;
    ////            Date nextRepeatTime;
    //            Date startRepeatFromTime;
    //            int totalNumberInstancesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar();
    //
    //            if (firstTime) {
    //                ASSERT.that(newRepeatInstanceList.size() == 0, "newRepeatInstanceList not empty");
    //                newRepeatInstanceList.add(repeatRuleOriginator); //add repeatRuleOriginator to instanceList since instanceList is used to check if an item is still allowed to edit the rule
    //                totalNumberInstancesGeneratedSoFar -= 1; //deduct one from count since originator doesn't count
    //                startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    //            } else {
    //                //not the first time, so start repetition from the first still active instance
    //                ASSERT.that(false, "shouldn't happen?! repeatRule=" + this + ", item=" + repeatRuleOriginator);
    //                totalNumberInstancesGeneratedSoFar -= oldRepeatInstanceList.size(); //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later)
    //                startRepeatFromTime = getLatestDateCompletedOrCancelled();
    //            }
    //            ASSERT.that(oldRepeatInstanceList.size() == 0 || oldRepeatInstanceList.contains(repeatRuleOriginator), "repeatRuleOriginator must always be getListOfUndoneRepeatInstances() unless empty, repeatRule=" + this + ", repeatRuleOriginator" + repeatRuleOriginator);
    //
    //            int numberInstancesToGenerate;
    //            Date subsetEndDate;
    //            if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    //                if (firstTime) {
    //                    numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead() - 1; //generate one less than max since the originator already exists
    //                } else { //    Math.min(getNumberFutureRepeatsToGenerateAhead() - 1, //generate one less than max since the originator already exists
    //                    numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead() - 1; //generate one less than max since the originator already exists
    //                }
    //                subsetEndDate = null;
    //            } else {
    //                subsetEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
    //                numberInstancesToGenerate = -1;
    //            }
    //
    //            List<Date> dates = generateListOfDates(startRepeatFromTime, subsetEndDate, numberInstancesToGenerate);
    //
    ////            for (Date nextRepeatTime: dates) {
    //            for (int i = 0, size = dates.size(); i < size; i++) {
    //                Date nextRepeatTime = dates.get(i);
    //                repeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldRepeatInstanceList, nextRepeatTime);
    //                repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
    //                newRepeatInstanceList.add(repeatInstance);
    //                setLastGeneratedDate(nextRepeatTime); //save last date generated
    //            }
    //
    //            //delete any 'left over' instances //TODO check that deleting them doesn't affect
    //            while (oldRepeatInstanceList.size() > 0) {
    //                RepeatRuleObjectInterface obsoleteInstance = oldRepeatInstanceList.remove(0);
    //                if (obsoleteInstance instanceof Item && ((Item) obsoleteInstance).getActualEffort() != 0) { //for Items, not WorkSlots
    //                    ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //Cancel instead of delete if time has been registered (to avoid losing it)
    //                    DAO.getInstance().save((Item) obsoleteInstance); //must save it
    //                } else {
    ////                    obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
    //                    DAO.getInstance().delete((ParseObject) obsoleteInstance);
    //                }
    //            }
    //
    //            setListOfUndoneRepeatInstances(newRepeatInstanceList);
    //            totalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
    //            setTotalNumberOfInstancesGeneratedSoFar(totalNumberInstancesGeneratedSoFar);
    //            DAO.getInstance().save(this);
    //        }
    //    }
    //
    //    public void updateRepeatInstancesWhenRuleWasCreatedOrEditedOLD(RepeatRuleObjectInterface repeatRuleOriginator) {
    ////<editor-fold defaultstate="collapsed" desc="comment">
    //        /**
    //         * //algorithm: ~removeFromCache (reuse existing rule, to avoid
    //         * creating/deleting when edited) //get any already created repeat
    //         * instances //create dates for new/edited rule //for as many instances
    //         * as should be generated (encapsulate this! w moreInstances() and
    //         * ruleFinished(), moreInstances based on either #futureInstances vs
    //         * length of list or #daysAhead vs lastDayInList) //if an existing
    //         * instance exists, use it and set the date (whether new date is same or
    //         * not) //if no more instances, generate one //at the end, if there are
    //         * still existing instances, delete them (or cancel if actual!=0)
    //         *
    //         */
    ////</editor-fold>
    //        boolean firstTime = (getTotalNumberOfInstancesGeneratedSoFar() == 0);
    ////        RepeatRule repeatRule = getRepeatRule();
    //        if (getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE) {
    //            List<RepeatRuleObjectInterface> oldRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
    //            ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
    //            RepeatRuleObjectInterface repeatInstance;
    //            Date nextRepeatTime;
    //            Date startRepeatFromTime;
    //            int totalNumberInstancesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar();
    //            if (firstTime) {
    //                ASSERT.that(newRepeatInstanceList.size() == 0, "newRepeatInstanceList not empty");
    //                newRepeatInstanceList.add(repeatRuleOriginator); //add repeatRuleOriginator to instanceList since instanceList is used to check if an item is still allowed to edit the rule
    //                totalNumberInstancesGeneratedSoFar -= 1; //deduct one from count since originator doesn't count
    //                startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    //            } else {
    //                //not the first time, so start repetition from the first still active instance
    ////                if (oldRepeatInstanceList.size() > 0) {
    ////                    startRepeatFromTime = oldRepeatInstanceList.get(0).getRepeatStartTime(false);
    ////                } else {
    //                ASSERT.that(false, "shouldn't happen?! repeatRule=" + this + ", item=" + repeatRuleOriginator);
    ////                    startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
    //                totalNumberInstancesGeneratedSoFar -= oldRepeatInstanceList.size(); //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later)
    //                startRepeatFromTime = getLatestDateCompletedOrCancelled();
    ////                }
    //            }
    //            ASSERT.that(oldRepeatInstanceList.size() == 0 || oldRepeatInstanceList.contains(repeatRuleOriginator), "repeatRuleOriginator must always be getListOfUndoneRepeatInstances() unless empty, repeatRule=" + this + ", repeatRuleOriginator" + repeatRuleOriginator);
    //
    //            if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
    ////            int numberInstancesToGenerate = Math.min(getNumberFutureRepeatsToGenerateAhead(), getNumberOfRepeats()-getTotalNumberOfInstancesGeneratedSoFar());
    ////                int numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead() - 1; //DatesBuffer should keep track of max number of repeats to generate!!
    //                int numberInstancesToGenerate;
    //                if (firstTime) {
    //                    numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead() - 1; //generate one less than max since the originator already exists
    //                } else //    Math.min(getNumberFutureRepeatsToGenerateAhead() - 1, //generate one less than max since the originator already exists
    //                //                        getNumberOfRepeats()- getTotalNumberOfInstancesGeneratedSoFar())
    //                //                numberInstancesToGenerate = Math.min(getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar();
    //                //            int instancesGeneratedCount = 0;
    //                {
    //                    numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead() - 1; //generate one less than max since the originator already exists
    //                }
    //
    ////                List<Date> dates = getNextDateNoDatesListBuffer(numberInstancesToGenerate)
    //                for (int instancesGeneratedCount = 0; instancesGeneratedCount < numberInstancesToGenerate; instancesGeneratedCount++) {
    ////                nextRepeatTime = removeNextDate(repeatRule);
    ////                nextRepeatTime = getNextDate(repeatRule, true);
    ////                    nextRepeatTime = getNextDate(repeatRuleOriginator.getRepeatStartTime(false));
    //                    nextRepeatTime = getNextDate(startRepeatFromTime);
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                if (false) {
    ////                    if (nextRepeatTime == null) {
    ////                        break;
    ////                    } else if (oldRepeatInstanceList.size() >= 1) { //reuse already generated instances
    ////                        RepeatRuleObjectInterface oldInstance = oldRepeatInstanceList.remove(0);
    ////                        //no need to add them to any lists, they are simply left where they already are
    ////                        if (oldInstance.equals(repeatRuleOriginator)) {
    ////                            newRepeatInstanceList.add(repeatRuleOriginator); //don't change the date for the originator
    ////                        } else {
    ////                            oldInstance.setRepeatStartTime(nextRepeatTime); //upate repeat time
    ////                            newRepeatInstanceList.add(oldInstance);
    ////                        }
    ////                    } else {
    ////                        repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
    ////                        //insert new created instance in appropriate list
    ////                        //DONE!!! how to insert 'naturally' into a list? In order generated (first dates first), either at beginning/end of list, or right after originator?? -> handled in
    //////                    listForNewCreatedRepeatInstances.addToList(insertIndex + instancesGeneratedCount, (ItemAndListCommonInterface) repeatInstance); //+count => insert each instance *after* the previously inserted one
    //////                    repeatRuleOriginator.getInsertNewRepeatInstancesIntoList().add(repeatInstance);
    ////                        repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
    ////                        newRepeatInstanceList.add(repeatInstance);
    //////                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
    ////                    }
    ////                } else
    ////</editor-fold>
    ////                    if (nextRepeatTime != null) {
    //                    if (nextRepeatTime != null) { //&& (!(getEndDateD() != null) || nextRepeatTime.getTime() <= getEndDateD().getTime())) { //either endDate is undefined, or nextRepeatTime must be smaller or equal //NOT needed, handled by RR itself
    //                        repeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldRepeatInstanceList, nextRepeatTime);
    ////                        if (!repeatRuleOriginator.equals(repeatInstance)) {
    ////                        if (false && !repeatRuleOriginator.equals(repeatInstance)) {
    ////                            repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance); //NO, doesn't matter which via which instance the RR was edited
    ////                        }
    //                        repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
    //                        newRepeatInstanceList.add(repeatInstance);
    //                        setLastGeneratedDate(nextRepeatTime); //save last date generated
    //                    }
    //                }
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////instancesGeneratedCount correspond to #generated (starts at 0, is incremented in last iteration
    ////            setCountOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + instancesGeneratedCount); //count-1 => if we break out of the for loop, count will be 1 too high
    ////            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + newRepeatInstanceList.size());
    ////</editor-fold>
    //            } else { //generate instances for a certain amount of time ahead
    ////            int instancesGeneratedCount = 0;
    //                long now = System.currentTimeMillis(); //MyDate.getNow();
    //                int numberOfDaysRepeatsAreGeneratedAhead = getNumberOfDaysRepeatsAreGeneratedAhead();
    ////            while ((nextRepeatTime = removeNextDate(datesList)) != null && (nextRepeatTime.longValue() <= now + numberOfDaysRepeatsAreGeneratedAhead)) {
    ////                while ((nextRepeatTime = getNextDate(repeatRule, false)) != null && (nextRepeatTime.getTime() <= now + numberOfDaysRepeatsAreGeneratedAhead * MyDate.DAY_IN_MILLISECONDS)) {
    //                while ((nextRepeatTime = getNextDate(null)) != null
    //                        && (nextRepeatTime.getTime() <= now + numberOfDaysRepeatsAreGeneratedAhead * MyDate.DAY_IN_MILLISECONDS)) //                        && repeatDateBeforeOrEqualEndDate(nextRepeatTime)) {
    //                //                        && nextRepeatTime.getTime() <= getEndDateD().getTime())  //NOT necessary, RR takes care of not generating beyond the endDate
    //                {
    ////                removeNextDate(repeatRule);
    ////                    getNextDate(repeatRule, true);
    //                    repeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldRepeatInstanceList, nextRepeatTime);
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                    if (false && !repeatRuleOriginator.equals(repeatInstance)) {
    ////                        repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
    ////                    }
    ////</editor-fold>
    //                    repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
    //                    newRepeatInstanceList.add(repeatInstance);
    //                    setLastGeneratedDate(nextRepeatTime); //save last date generated
    //                }
    //            }
    //
    //            //delete any 'left over' instances //TODO check that deleting them doesn't affect
    ////            for (RepeatRuleObjectInterface obsoleteInstance : oldRepeatInstanceList) {
    ////            while (oldRepeatInstanceList.size() > 0) {
    ////                long now = System.currentTimeMillis(); //MyDate.getNow();
    ////                int numberOfDaysRepeatsAreGeneratedAhead = getNumberOfDaysRepeatsAreGeneratedAhead();
    ////                while ((nextRepeatTime = getNextDate(null)) != null
    ////                        && (nextRepeatTime.getTime() <= now + numberOfDaysRepeatsAreGeneratedAhead * MyDate.DAY_IN_MILLISECONDS)
    ////                        && repeatDateBeforeOrEqualEndDate(nextRepeatTime)) {
    ////        }
    //            while (oldRepeatInstanceList.size() > 0) {
    //                RepeatRuleObjectInterface obsoleteInstance = oldRepeatInstanceList.remove(0);
    ////            obsoleteInstance.setRepeatRule(null); //necessary?
    //                if (obsoleteInstance instanceof Item && ((Item) obsoleteInstance).getActualEffort() != 0) { //for Items, not WorkSlots
    //                    ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //Cancel instead of delete if time has been registered (to avoid losing it)
    //                    DAO.getInstance().save((Item) obsoleteInstance); //must save it
    //                } else {
    ////                    obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
    //                    DAO.getInstance().delete((ParseObject) obsoleteInstance);
    //                }
    //            }
    //
    //            setListOfUndoneRepeatInstances(newRepeatInstanceList);
    //            totalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
    //            setTotalNumberOfInstancesGeneratedSoFar(totalNumberInstancesGeneratedSoFar);
    //            DAO.getInstance().save(this);
    //        }
    //    }
    //    public ItemAndListCommonInterface insertIntoList(ItemAndListCommonInterface insertAfterThis, ItemAndListCommonInterface newRepeatRuleInstance) {
    //    private ItemAndListCommonInterface insertIntoListXXX(RepeatRuleObjectInterface insertAfterThis, RepeatRuleObjectInterface newRepeatRuleInstance) {
    //        ItemAndListCommonInterface owner = ((ItemAndListCommonInterface) insertAfterThis).getOwner();
    //        if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()) {// && (ownerList.indexOf(this)) != -1) {
    //            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, (ItemAndListCommonInterface) insertAfterThis, true); ///NB. no need to check if 'this' is in list since this insert defaults normal add if not
    //        } else {
    //            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, !MyPrefs.insertNewItemsInStartOfLists.getBoolean());
    //        }
    //        return owner;
    //    }
    //</editor-fold>

    private RepeatRuleObjectInterface getLatestDueDateInstance(RepeatRuleObjectInterface ifNoneUseThis) {
        List<RepeatRuleObjectInterface> undone = getListOfUndoneInstances();
        if (undone.size() > 0) {
            return undone.get(undone.size() - 1);
        } else {
            return ifNoneUseThis;
        }
    }

    /**
     * insert a new repeat instance in the place (either at the end of list -
     * usual position, or after the justCompleted, or after the last already
     * generated repeat instance to have them in sequential order)
     *
     * @param justCompleted
     * @param newRepeatRuleInstance
     * @return
     */
//    private ItemAndListCommonInterface insertRepeatInstanceAtRightPositionInOwnerOLD(RepeatRuleObjectInterface justCompleted, RepeatRuleObjectInterface newRepeatRuleInstance) {
//        ItemAndListCommonInterface owner;
//        if (!MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()) {// && (ownerList.indexOf(this)) != -1) {
//            //insert in normal position (head or end of list 
//            owner = ((ItemAndListCommonInterface) justCompleted).getOwner();
//            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, !MyPrefs.insertNewItemsInStartOfLists.getBoolean());
//        } else {
//            if (!MyPrefs.repeatInsertAfterLastDueDateInstanceInsteadOfJustCompleted.getBoolean()) {
//                //insert after just completed
//                owner = ((ItemAndListCommonInterface) justCompleted).getOwner();
//                owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, (ItemAndListCommonInterface) justCompleted, true); ///NB. no need to check if 'this' is in list since this insert defaults normal add if not
//            } else {
//                //insert after the last generated instance
//                RepeatRuleObjectInterface latestDueDateInstance = getLatestDueDateInstance(justCompleted);
//                owner = ((ItemAndListCommonInterface) latestDueDateInstance).getOwner();
//                owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, (ItemAndListCommonInterface) latestDueDateInstance, true); ///NB. no need to check if 'this' is in list since this insert defaults normal add if not
//            }
//        }
//        return owner;
//    }
    private ItemAndListCommonInterface insertRepeatInstanceAtRightPositionInOwner(RepeatRuleObjectInterface justCompleted, RepeatRuleObjectInterface newRepeatRuleInstance) {
        ItemAndListCommonInterface owner;
        if (MyPrefs.repeatInsertNewRepeatTaskAfterJustCompletedTask.getBoolean()) {// && (ownerList.indexOf(this)) != -1) {
            //insert after just completed
            owner = ((ItemAndListCommonInterface) justCompleted).getOwner();
            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, (ItemAndListCommonInterface) justCompleted, true); ///NB. no need to check if 'this' is in list since this insert defaults normal add if not
        } else if (MyPrefs.repeatInsertNewRepeatTaskAfterLatestRepeatInstance.getBoolean()) {
            //insert after the last generated instance
            RepeatRuleObjectInterface latestDueDateInstance = getLatestDueDateInstance(justCompleted);
            owner = ((ItemAndListCommonInterface) latestDueDateInstance).getOwner();
            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, (ItemAndListCommonInterface) latestDueDateInstance, true); ///NB. no need to check if 'this' is in list since this insert defaults normal add if not
        } else {
            //insert in normal position (head or end of list 
            owner = ((ItemAndListCommonInterface) justCompleted).getOwner();
            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, !MyPrefs.insertNewItemsInStartOfLists.getBoolean());
        }
        return owner;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private RepeatRuleObjectInterface getInsertionReferenceXXX(boolean useLastestDueDateInstance, RepeatRuleObjectInterface ifNoneUseThis) {
//        if (useLastestDueDateInstance) {
//            return getLatestDueDateInstance(ifNoneUseThis);
//        } else {
//            return ifNoneUseThis;
//        }
//    }
//</editor-fold>
    /**
     * called when an item (RepeatRuleObjectInterface) is Done.Removes the item
     * from the stored list of instances and create new repeat instances (as a
     * copy of the just completed item) to replace the just done one. If the
     * repeatRule has no more instances (the just Done item was the last) the
     * repeat rule deletes itself. OLD: removes the repeatRuleObject from the
     * list of instances. Update the instances. If no more instances left (eg
     * repeatRuleObject was the last repeating instance of a task to be
     * completed), and the rule has finished (no more instances will be
     * generated in the future), then deleteRuleAndAllRepeatInstancesExceptThis
     * deletes the RepeatRule itself. This call will always generate *at least*
     * one new instance, to avoid e.g. a situation where a repeat rule repeats
     * every two months, but only generate 30 days ahead - this would create a
     * deadlock since new instances are only generated when a previous instance
     * is Completed.
     *
     * @param completedItem
     * @param updateInstances if true, updates (recalculates) repeat instances.
     * Used eg to avoid to calculate instances if a just created Item with a
     * repeatRule is cancelled (deleted without being comitted)
     * @param workSlot
     */
//    public RepeatRuleObjectInterface updateRepeatInstancesOnDoneCancelOrDelete(RepeatRuleObjectInterface orgRepeatObject) {
//    public RepeatRuleObjectInterface updateRepeatInstancesOnDoneCancelOrDelete(RepeatRuleObjectInterface repeatInstanceOrg) {
    public void updateItemsOnDoneCancelOrDelete(Item completedItem) {

        List undoneInstances = getListOfUndoneInstances();
//        if (!undoneInstances.contains(completedItem)) {
//            //updateRepeatInstancesOnDoneCancelOrDelete can also be called from Item.delete when the repeatRule is deleting superflous instances when rule is changed
//        } else {
        if (undoneInstances.contains(completedItem)) { //only process undone (so a done item marked undone does not generate new repeat instances!)
            if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) {
                if (isDatedCompletion()) {
                    //for REPEAT_TYPE_FROM_COMPLETED_DATE create a new instance
                    updateWithCompletedItem(completedItem);
                    Date dateCompleted = completedItem.getRepeatStartTime(true);
//                Date dateCompleted = getStartDateForCompletion(new MyDate(),getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE);
//                List<Date> dates = generateListOfDates(calcSubsetBeginningDate(dateCompleted), calcSubsetEndDateZZZ(), 1, false, false);
                    List<Date> dates = generateListOfDates(dateCompleted, calcSubsetEndDate(), 1, false, false);

                    Date nextRepeatTime = null;
                    if (!dates.isEmpty()) {
                        nextRepeatTime = dates.get(0);
                    }
                    if (nextRepeatTime != null) {
                        Item newRepeatInstance = (Item) completedItem.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
                        ItemAndListCommonInterface owner = insertRepeatInstanceAtRightPositionInOwner(completedItem, newRepeatInstance); //insert new created instance in appropriate list and save it
                        Log.p("**new1 repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + newRepeatInstance);
                        addNewRepeatInstanceToUndone(newRepeatInstance);
//                        DAO.getInstance().saveNew(newRepeatInstance, this, (ParseObject) owner); //OK if repeatInstance or owner are null
//                        DAO.getInstance().saveNewTriggerUpdate();
//                        DAO.getInstance().saveToParseNow(newRepeatInstance, this, (ParseObject) owner); //OK if repeatInstance or owner are null
//                        DAO.getInstance().saveToParseNow(newRepeatInstance); //OK if repeatInstance or owner are null
                    } else {
//                        DAO.getInstance().saveNew(this); //OK if repeatInstance or owner are null
//                        DAO.getInstance().saveNewTriggerUpdate();
//                        DAO.getInstance().saveToParseNow(this); //OK if repeatInstance or owner are null
                    }
                } else {
                    //Create a new repeat instance *without* a due date
                    updateWithCompletedItem(completedItem);
//                    Item newRepeatInstance = (Item) completedItem.cloneMe(Item.CopyMode.COPY_TO_REPEAT_INSTANCE, COPY_EXCLUDE_DUE_DATE, true); //copy RR w/o update
                    Item newRepeatInstance = (Item) completedItem.cloneMe(Item.CopyMode.COPY_TO_REPEAT_INSTANCE, COPY_EXCLUDE_DUE_DATE); //copy RR w/o update
                    ItemAndListCommonInterface owner = insertRepeatInstanceAtRightPositionInOwner(completedItem, newRepeatInstance); //insert new created instance in appropriate list and save it
                    Log.p("**new repeat on completion instance generated *without* repeat date, instance=" + newRepeatInstance);
                    addNewRepeatInstanceToUndone(newRepeatInstance);
//                DAO.getInstance().saveNew(true, newRepeatInstance, this, (ParseObject) owner); //OK if repeatInstance or owner are null
//                    DAO.getInstance().saveNew(newRepeatInstance, this, (ParseObject) owner); //OK if repeatInstance or owner are null
//                    DAO.getInstance().saveNewTriggerUpdate();
//                    DAO.getInstance().saveToParseNow(newRepeatInstance, this, (ParseObject) owner); //OK if repeatInstance or owner are null
//                    DAO.getInstance().saveToParseNow(newRepeatInstance); //OK if repeatInstance or owner are null
                }
            } else { //getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE
                ASSERT.that(getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE);
                //for REPEAT_TYPE_FROM_COMPLETED_DATE or REPEAT_TYPE_FROM_DUE_DATE create a new instance
                updateWithCompletedItem(completedItem);
//                Date nextRepeatTime = getNextRepeatFromDate(completedItem);
                Date nextRepeatTime = null;
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) {
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(true)); //get next date
////            nextRepeatTime = getNextCompletedFromDate(completedItem.getRepeatStartTime(true)); //get next date
//
//                    Date dateCompleted = completedItem.getRepeatStartTime(true);
////<editor-fold defaultstate="collapsed" desc="comment">
////                    ASSERT.that(dateCompleted.getTime() != 0, "called before dateCompleted was set on Item/WorkSlot");
////        RepeatRule repeatRule = getRepeatRule();
////        repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
////        Vector<Date> newDates = repeatRule.datesAsVector(dateCompleted, dateCompleted, new Date(MyDate.MAX_DATE)); //CREATE new dates
////        nextRepeatTime = getFirstNotEqualDate(newDates, dateCompleted);
////</editor-fold>
////                    generateListOfDates(dateCompleted, calcSubsetEndDate(), 1, false, false);
//                    List<Date> dates = generateListOfDates(calcSubsetBeginningDate(dateCompleted), calcSubsetEndDate(), 1, false, false);
//
////                ASSERT.that(repeatInstanceItemList.size() <= 1, "Error: Repeat from Completed - too many instances in listOfUndoneRepeatInstances=" + repeatInstanceItemList);
//                } else if (getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE) {
//                    if (Config.TEST) {
//                        ASSERT.that(getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE);
//                    }
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(false)); //get next date
////                    nextRepeatTime = getNextDueDateN(); //get next date
//                    nextRepeatTime = getNextDueDateN(); //get next date
//                } else {
//                    ASSERT.that("unknown repeatType="+getRepeatType());
//                }
//</editor-fold>
//                Date dateCompleted = completedItem.getRepeatStartTime(true);
//                Date dateCompleted = getStartDateForCompletion();
//                Date dateCompleted = getStartDateForCompletion(new MyDate(), getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE);
//                Date dateCompleted = getStartDateForCompletion();
                Date dateCompleted = getLatestDueDateDoneOrUndone();
                if (dateCompleted.getTime() == MyDate.MIN_DATE) {//|| dateCompleted.getTime()<completedItem.getRepeatStartTime(false): NO, is already in one of done/undone lists, so only edge case is if a previously done task was created via an earlier RR with a *later* date then the ones from the new rule
                    dateCompleted = new MyDate();
                }
//                List<Date> dates = generateListOfDates(calcSubsetBeginningDate(dateCompleted), calcSubsetEndDateZZZ(), 1, false, false);
                List<Date> dates = generateListOfDates(dateCompleted, calcSubsetEndDate(), 1, false, false);
                if (!dates.isEmpty()) {
                    nextRepeatTime = dates.get(0);
                }
                if (nextRepeatTime != null) {
                    Item newRepeatInstance = (Item) completedItem.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
                    ItemAndListCommonInterface owner = insertRepeatInstanceAtRightPositionInOwner(completedItem, newRepeatInstance); //insert new created instance in appropriate list and save it
                    Log.p("**new2 repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + newRepeatInstance);
                    addNewRepeatInstanceToUndone(newRepeatInstance);
//                    DAO.getInstance().saveNew(true, newRepeatInstance, this, (ParseObject) owner); //OK if repeatInstance or owner are null
//                    DAO.getInstance().saveNew(newRepeatInstance, this, (ParseObject) owner); //OK if repeatInstance or owner are null
//                    DAO.getInstance().saveNewTriggerUpdate();
//                    DAO.getInstance().saveToParseNow(newRepeatInstance, this, (ParseObject) owner); //OK if repeatInstance or owner are null
                    if (false) {
                        DAO.getInstance().saveToParseNow(newRepeatInstance); //OK if repeatInstance or owner are null //DON'T save here, always part of another change that will trigger save
                    }
                } else {
//                    DAO.getInstance().saveNew(true, this); //OK if repeatInstance or owner are null
//                    DAO.getInstance().saveNew(this); //OK if repeatInstance or owner are null
//                    DAO.getInstance().saveNewTriggerUpdate();
                    if (false) {
                        DAO.getInstance().saveToParseNow(this); //OK if repeatInstance or owner are null //DON'T save here, always part of another change that will trigger save
                    }
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void updateItemsOnDoneCancelOrDeleteOLD(Item completedItem) {
//
////        List<ItemAndListCommonInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
////        List<RepeatRuleObjectInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
//        List repeatInstanceItemList = getListOfUndoneInstances();
//        if (!repeatInstanceItemList.contains(completedItem)) {
//            //updateRepeatInstancesOnDoneCancelOrDelete can also be called from Item.delete when the repeatRule is deleting superflous instances when rule is changed
//        } else {
//            setLatestDateCompletedOrCancelledIfGreaterThanLast(completedItem.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
//            ASSERT.that(repeatInstanceItemList.contains(completedItem), "Error: " + completedItem + " not in list of already generated repeat instances");
////        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
//            repeatInstanceItemList.remove(completedItem);
//
//            updateWithCompletedItem(completedItem);
//
//            Date nextRepeatTime;
////        Date nextRepeatTime = getNextDate(item.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE)); //get next date
//            if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) {
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(true)); //get next date
//                nextRepeatTime = getNextCompletedFromDate(completedItem.getRepeatStartTime(true)); //get next date
//                ASSERT.that(repeatInstanceItemList.size() <= 1, "Error: Repeat from Completed - too many instances in listOfUndoneRepeatInstances=" + repeatInstanceItemList);
//            } else {
//                if (Config.TEST) {
//                    ASSERT.that(getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE);
//                }
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(false)); //get next date
//                nextRepeatTime = getNextDueDateN(); //get next date
//            }
//
//            nextRepeatTime = getNextRepeatFromDate(completedItem);
//
//            ItemAndListCommonInterface owner = null;
//            Item newRepeatInstance = null;
////            Item repeatInstance = null;
//            if (nextRepeatTime != null) {
////            repeatInstance = repeatInstance.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//                newRepeatInstance = (Item) completedItem.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//                owner = insertIntoList2(completedItem, newRepeatInstance); //insert new created instance in appropriate list and save it
////            if (getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE) {
//                repeatInstanceItemList.add(newRepeatInstance); //only keep track of instances when Due
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (false) {
////                    DAO.getInstance().saveInBackground(repeatInstanceItemList); //save generated repeat instances
////                }
////</editor-fold>
////                setLastGeneratedDate(nextRepeatTime); //TODO!!!! doesn't make sense to update this for RepeatFromCompleted
////            }
////                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//
//                Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + newRepeatInstance);
//            } else { // no more repeat instances to handle
////            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
//            }
////            DAO.getInstance().saveInBackground(repeatInstanceItemList); //save generated repeat instances
//            setListOfUndoneInstances(repeatInstanceItemList);
//            addNewRepeatInstance(newRepeatInstance);
//            if (Config.TEST) {
//                checkRefs();
//            }
//            DAO.getInstance().saveNew(true, newRepeatInstance, this, (ParseObject) owner);
////            if (owner != null) {
////                DAO.getInstance().saveInBackground((ParseObject) owner); //save list into which the new repeatCopy was inserted above
////            }
////            DAO.getInstance().triggerParseUpdate();
////        return repeatInstance;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateRepeatInstancesOnCancelDeleteOrExpiredXXX(WorkSlot workSlot) {
////        List<WorkSlot> workSlotInstanceItemList = getListOfUndoneRepeatInstances();
//        List<RepeatRuleObjectInterface> workSlotInstanceItemList = getListOfUndoneRepeatInstances();
////        WorkSlotList workSlotInstanceItemList = new WorkgetListOfUndoneRepeatInstances();
//        setLatestDateCompletedOrCancelledIfGreaterThanLast(workSlot.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
//        ASSERT.that(workSlotInstanceItemList.size() == 0 || workSlotInstanceItemList.contains(workSlot), "Error: \"" + workSlot + "\" not in list of already generated repeat instances");
////        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
//        workSlotInstanceItemList.remove(workSlot);
//        ItemAndListCommonInterface ownerList = null;
//
//        Date nextRepeatTime;
//        nextRepeatTime = getNextDueDate(); //get next date
//        WorkSlot repeatInstance = null;
//        if (nextRepeatTime != null) {
//            repeatInstance = (WorkSlot) workSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            ownerList = workSlot.insertIntoList(repeatInstance); //insert new created instance in appropriate list and save it
//            workSlotInstanceItemList.add(repeatInstance);
//
//            setLastGeneratedDate(nextRepeatTime);
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//
//            Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//        } else { // no more repeat instances to handle
////            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
//        }
//
//        WorkSlot newWorkSlot;
//        //for workSlots, time may have passed since so need to update to ensure all workslots are in the future (or overlaps the future)
//        if (workSlotInstanceItemList.size() > 0) {
//            newWorkSlot = (WorkSlot) workSlotInstanceItemList.get(0);
//        } else {
//            newWorkSlot = null;
//        }
//
//        while (newWorkSlot != null && (newWorkSlot.getEndTime() <= MyDate.currentTimeMillis()) && (nextRepeatTime = getNextDueDate()) != null) {
//            //when we get here, the current/first in list workslot is in the past and we have a new repeatTime (so repeatRule is not expired)
//            workSlotInstanceItemList.remove(workSlot); //remove workSlot in the past
//            repeatInstance = (WorkSlot) workSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due/Start date)
//            ownerList = workSlot.insertIntoList(repeatInstance); //insert new created instance in appropriate list and save it
//            workSlotInstanceItemList.add(repeatInstance);
//
//            setLastGeneratedDate(nextRepeatTime);
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//
//            if (workSlotInstanceItemList.size() > 0) {
//                workSlot = (WorkSlot) workSlotInstanceItemList.get(0); //get next workslot
////                nextRepeatTime = getNextDueDate(); //get next date
//            } else {
//                workSlot = null;
//            }
//        }
//
//        //below save order is delicate: first save RepeatRule (in case it's all new), then save new repeatCopies (which refer to RepeatRule), then add repeatCopies to RepeatRule and save again, then save the owner of thew new repeatCopies
//        if (false && this.getObjectIdP() == null)
//            DAO.getInstance().saveAndWait(this); //save first time to avoid references to unsaved rule in the instances
//        setListOfUndoneRepeatInstances(workSlotInstanceItemList);
//        DAO.getInstance().saveInBackground(this);
//        if (ownerList != null)
//            DAO.getInstance().saveInBackground((ParseObject) ownerList);
////        return repeatInstance;
//    }
//</editor-fold>
    enum UpdateType {
        refresh, create, modified;
    }

    enum RepeatChangeType {
        recalcDatesFromNow, //only the duration of the rule has changed, e.g. 
        recalcDatesFromLastDone,
        recalcDatesFromOriginator,
        recalcDatesFromFirstUndone; //the date pattern has changed so previsoulsy calculated, future, instances need to be recalculated
    }

    /**
     * when reusing an existing workSlot, copy the values from the new
     * originator (start, duration, test, repeatRule, leave
     *
     * @param destination
     * @param orgWS
     */
    private void copyValuesToExistingRepeatWorkSLot(WorkSlot destination, WorkSlot orgWS) {
        destination.setStartTime(orgWS.getStartTimeD());
        destination.setDurationInMinutes(orgWS.getDurationInMinutes());
        destination.setText(orgWS.getText()); //NB: most intuitive is use potentially text from just edited WS when generating new ones (just like start+duration are used)
        destination.setComment(orgWS.getComment()); //Comments not currently supported for WorkSlots
//        destination.setOwner(orgWS.getOwner()); //need to set owner here, since not done eg when creating repeat copies
        //Contrary to Item, always use the original originator as source:
        destination.setSource(orgWS); //otherwise (this is first copy) use this
        ASSERT.that(Objects.equals(destination.getRepeatRuleN(), orgWS.getRepeatRuleN()), "when copying values into reused workslot, the RepeatRule should be the same");
//        destination.setRepeatRuleInParse(orgWS.getRepeatRuleN());
    }

    /**
     * when updating repeatRule for workslots, all past workslots based on the
     * previous unedited version of the rule are already calculated, so it
     * wouldn't make sense to create additional past workslots => only
     * reasonable choice is to start from Now!
     *
     * @param editedElt cannot be null!
     * @param firstTime
     */
//    public void updateWorkSlotsWhenRuleCreatedOrEdited(WorkSlot sourceWorkSlot, boolean firstTime) {
    public void updateWorkSlotsWhenRuleCreatedOrEdited(RepeatRuleObjectInterface editedElt, boolean firstTime) {
//            //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
//        long now = MyDate.currentTimeMillis();
        Date now = new MyDate();

        //refresh to ensure any already expired workslots are not removed and new ones are generated, don't save since it will cause 2 save delayes
        updateIfExpiredWorkslots(false, now);

        List<WorkSlot> oldUndoneRepeatInstanceList = (List) getListOfUndoneInstances();
        if (getRepeatType() == REPEAT_TYPE_NO_REPEAT) { //rule deleted so delete all future instances!

//            deleteUnneededWorkSlotInstancesXXX(oldUndoneRepeatInstanceList); //UI: will delete started but not yet expired workslots <=> delete all workslots that are still 'active'
//            Set<ItemAndListCommonInterface> updatedOwners = deleteUnneededItemOrWorkSlotInstances(oldUndoneRepeatInstanceList); //UI: will delete started but not yet expired workslots <=> delete all workslots that are still 'active'
            Set updatedOwners = deleteUnneededWorkSlotInstances(oldUndoneRepeatInstanceList); //UI: will delete started but not yet expired workslots <=> delete all workslots that are still 'active'
//            DAO.getInstance().saveNew(updatedOwners);
            DAO.getInstance().saveToParseLater(updatedOwners); //no need to save now, updateWorkSlotsWhenRuleCreatedOrEdited is called when updating a workslot

        } else {

            WorkSlot editedWS = (WorkSlot) editedElt;

            WorkSlot workSlotToCreateCopiesFrom = editedWS; //always create copies from edited WS first, use to always create copies from the earlier instance (keep a chain) 
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false && !doneList.isEmpty()) {
//                //UI: if editing RR from an expired workslot, automatically use the last expired one to create an 'unbroken chain' of copies.
//                //NB. May be unintuitive if past workslot or current was edited (e.g. you extended the duration of the future workslots)??!
//                createCopyFromThis = (WorkSlot) doneList.get(doneList.size() - 1);
//                createCopyFromThis.update(editedWS); //UI: create unbroken chain but use values from the workslot currently edited
//            } else {
//                createCopyFromThis = editedWS; //always create copies from edited WS first, use to always create copies from the earlier instance (keep a chain)
//            }
//            Date startRepeatFromTime = now; //UI: for new or edited rule for workslots, always start repeating from Now
//</editor-fold>
            Date startRepeatFromTime = editedWS.getStartTimeD(); //UI: for new or edited rule for workslots, always start repeating from startTime of just edited WS. Means possible to generate WS in the past which makes it simpler to understand what happens and possible to easily create past WS if wanted

            boolean keepFirstGenerated = false; //!(editedWS.getEndTimeD().getTime() > now.getTime()); //don't keep first generated if orginator has *not* expired //true;

//            setListOfUndoneInstances(null); //remove all workslots so regeneration 'starts from scratch'
            List<WorkSlot> newRepeatWorkSlots = new ArrayList(); //hold new created instances
            ItemAndListCommonInterface owner = editedWS.getOwner(); //UI: assumes all workslots have same owner (no support for moving repeat workslots to other
            //UI: delete all previuously generated workslots (no reuse of workslot instances since very unlikely they contain manually edited data (other than name)
//            setListOfUndoneInstances(null); //remove all workslots so regeneration 'starts from scratch'
            //generate all  workslots from new rule
            List doneList = getListOfDoneInstances();
            List<Date> dates;
            oldUndoneRepeatInstanceList.remove(editedWS); //remove editedWS so it is not reused below
            if (editedWS.getEndTimeD().getTime() < now.getTime()) { //if the editedWS is in the past, we should first generate all past WS
                dates = (generateListOfDates(startRepeatFromTime, now, Integer.MAX_VALUE, false, false)); //past dates, any number
//        List<Date> dates = generateListOfDates(subsetBeginningDate, subsetEndDate, numberRepeats, getNumberOfDaysRepeatsAreGeneratedAhead() != 0, false);
                dates.addAll(generateListOfDates(now, calcSubsetEndDate(), calcNumberOfRepeats(0), false, true)); //ongoing/future dates, number=NbOfRepeats (since edited WS is in the past)
                doneList.add(editedWS);
            } else {
                dates = (generateListOfDates(startRepeatFromTime, calcSubsetEndDate(), calcNumberOfRepeats(1), false, false)); //ongoing/future dates, number=NbOfRepeats-1 since editedWS is already there
                newRepeatWorkSlots.add(editedWS);
            }

            WorkSlot newRepeatInstance;
            for (Date nextRepeatTime : dates) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false && editedWS != null) { //false: must check what happens when the RR should delete the originator (which is edited in ScreenWorkSlow that called the RR edit)
//                    newRepeatInstance = editedWS;
//                    //only remove from undoneList here, since if RR is not creating any instances, it must be deleted
//                    //if editing an *expired* workslot this should also work:
//                    oldUndoneRepeatInstanceList.remove(editedWS); //if source is not expired, remove so it is not deleted, but left in place (and repeating start from its startDate
//                    editedWS = null; //only do this once, for first new instance
//                } else {
//                    newRepeatInstance = (WorkSlot)createCopyFromThis.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
//                }
//</editor-fold>
                if (!oldUndoneRepeatInstanceList.isEmpty()) {
                    newRepeatInstance = (WorkSlot) oldUndoneRepeatInstanceList.remove(0);
                    copyValuesToExistingRepeatWorkSLot(newRepeatInstance, workSlotToCreateCopiesFrom);
                } else {
                    newRepeatInstance = (WorkSlot) workSlotToCreateCopiesFrom.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
                    owner.addWorkSlot(newRepeatInstance); //assumes that workslots are always sorted on insertion
                }
                workSlotToCreateCopiesFrom = newRepeatInstance; //link next back to this instance
//            owner = sourceWorkSlot.insertIntoList(repeatInstance);
                newRepeatWorkSlots.add(newRepeatInstance); //gather all new instances and update/save in batch below
                if (Config.TEST) {
                    Log.p("**new3 repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + newRepeatInstance);
                }
//            owner.addWorkSlots(newRepeatWorkSlots); //add all new generated instances to the original owner
                if (newRepeatInstance.getEndTimeD().getTime() < now.getTime()) {
                    doneList.add(newRepeatInstance); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
                } else {
                    newRepeatWorkSlots.add(newRepeatInstance); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
                }
            }
//            Set updatedOwners = deleteUnneededItemOrWorkSlotInstances(oldUndoneRepeatInstanceList); //UI: will delete started but not yet expired workslots <=> delete all workslots that are still 'active'
//            DAO.getInstance().saveToParseLater(updatedOwners); //, false);
            deleteUnneededWorkSlotInstances(oldUndoneRepeatInstanceList); //UI: will delete started but not yet expired workslots <=> delete all workslots that are still 'active'
            setListOfUndoneInstances(newRepeatWorkSlots); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
            setListOfDoneInstances(doneList); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
//<editor-fold defaultstate="collapsed" desc="comment">
//            deleteUnneededWorkSlotInstances(oldUndoneRepeatInstanceList); //only set here since otherwise sourceWS may be deleted before crepeat opies are made
//            deleteUnneededItemOrWorkSlotInstances(oldUndoneRepeatInstanceList); //only set here since otherwise sourceWS may be deleted before crepeat opies are made
//            DAO.getInstance().saveNew(updatedOwners);
//
//            DAO.getInstance().saveNew(newRepeatWorkSlots); //, false);
////        DAO.getInstance().saveNew(true, this, (ParseObject) owner);
//            DAO.getInstance().saveNew(this, (ParseObject) owner);
//            DAO.getInstance().saveNew(updatedOwners); //, false);
//            DAO.getInstance().saveNewTriggerUpdate();

//            DAO.getInstance().saveToParseLater(newRepeatWorkSlots); //, false); //saved via RR
//            DAO.getInstance().saveToParseNow(this, (ParseObject) owner);
//</editor-fold>
//            DAO.getInstance().saveToParseLater(this, (ParseObject) owner); //no need to save now, updateWorkSlotsWhenRuleCreatedOrEdited is called when updating a workslot
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateWorkSlotsWhenRuleCreatedOrEditedOLD(RepeatRuleObjectInterface originator, boolean firstTime) {
////            //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
////        long now = MyDate.currentTimeMillis();
//        Date now = new MyDate();
//
//        //refresh to ensure any already expired workslots are not removed and new ones are generated, don't save since it will cause 2 save delayes
//        updateIfExpiredWorkslots(false, now);
//
//        List oldUndoneRepeatInstanceList = getListOfUndoneInstances();
//        if (getRepeatType() == REPEAT_TYPE_NO_REPEAT) { //rule deleted
//            deleteUnneededWorkSlotInstances(oldUndoneRepeatInstanceList); //UI: will delete all started but not yet expired workslots
//        } else {
//            WorkSlot originatorWorkSlot = (WorkSlot) originator;
////        ASSERT.that(oldUndoneRepeatInstanceList.contains(sourceWorkSlot));
//            oldUndoneRepeatInstanceList.remove(originator); //if source is not expired, remove so it is not deleted, but left in place (and repeating start from its startDate
//            //UI: delete all previuously generated workslots (no reuse of workslot instances since very unlikely they contain manually edited data (other than name)
//            setListOfUndoneInstances(null); //remove all workslots so regeneration 'starts from scratch'
//            List newRepeatWorkSlots = new ArrayList(); //hold new created instances
//            ItemAndListCommonInterface owner = originatorWorkSlot.getOwner(); //assumes all workslots have same owner (no support for moving repeat workslots to other
//
//            //generate all  workslots from new rule
////        List<Date> dates = getNextDueDateList();
////            Date lastGenerated = getLatestDateGenerated(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
////        Date subsetBeginningDate = calcSubsetBeginningDate(lastGenerated);
////            Date now = new MyDate();
//            RepeatRuleObjectInterface createCopyFromThis; //use to always create copies from the earlier instance (keep a chain)
//            if (originatorWorkSlot.isInThePast(now.getTime()) && !oldUndoneRepeatInstanceList.isEmpty()) {
//                //UI: if editing RR from an expired workslot, automatically use the first non-expired one
//                createCopyFromThis = (RepeatRuleObjectInterface) oldUndoneRepeatInstanceList.get(0);
//            } else {
//                createCopyFromThis = originator; //use to always create copies from the earlier instance (keep a chain)
//            }
//
//            Date startRepeatFromTime = null;
//            boolean keepFirstGenerated = true;
////<editor-fold defaultstate="collapsed" desc="comment">
//////            Date startRepeatFromTime = sourceWorkSlot.getStartTimeD(); //now; //getStartDateForRefreshRule(now);
//////            Date startRepeatFromTime = createCopyFromThis.getStartTimeD(); //now; //getStartDateForRefreshRule(now);
////             startRepeatFromTime = createCopyFromThis.getRepeatStartTime(false); //now; //getStartDateForRefreshRule(now);
////            if (startRepeatFromTime == null || startRepeatFromTime.getTime() == 0) {
////                ASSERT.that("shouldn't happen (since ScreenRepeatRule should ensure a startDate is always set");
////                startRepeatFromTime = now;
////                keepFirstGenerated = true; //true; //startRepeatFromTime.equals(now);
////            } else {
////                keepFirstGenerated = false; //true; //startRepeatFromTime.equals(now);
////            }
////</editor-fold>
//            RepeatChangeType repeatUpdateType = RepeatChangeType.recalcDatesFromNow;
//            switch (repeatUpdateType) {
//                case recalcDatesFromNow: //repeat starting from now
//                    startRepeatFromTime = now;
//                    keepFirstGenerated = false; //true; //startRepeatFromTime.equals(now);
//                    break;
//                case recalcDatesFromLastDone: //repeat starting from last past (or now if none)
//                    List<RepeatRuleObjectInterface> doneList = getListOfDoneInstances();
//                    if (!doneList.isEmpty()) {
//                        startRepeatFromTime = doneList.get(doneList.size() - 1).getRepeatStartTime(false);
//                        keepFirstGenerated = false; //true; //startRepeatFromTime.equals(now);
//                    } else {
//                        startRepeatFromTime = now;
//                        keepFirstGenerated = true; //true; //startRepeatFromTime.equals(now);
//                    }
//                    break;
//                case recalcDatesFromOriginator: //repeat starting from orginator
//                    startRepeatFromTime = createCopyFromThis.getRepeatStartTime(false); //now; //getStartDateForRefreshRule(now);
//                    if (startRepeatFromTime == null || startRepeatFromTime.getTime() == 0) {
//                        ASSERT.that("shouldn't happen (since ScreenRepeatRule should ensure a startDate is always set");
//                        startRepeatFromTime = now;
//                        keepFirstGenerated = true; //true; //startRepeatFromTime.equals(now);
//                    } else {
//                        keepFirstGenerated = false; //true; //startRepeatFromTime.equals(now);
//                    }
//                    break;
//                case recalcDatesFromFirstUndone: //repeat starting from first-coming future instance - if regeneratingWHY do this??
//                    List<RepeatRuleObjectInterface> undoneList = getListOfUndoneInstances();
//                    if (!undoneList.isEmpty()) {
//                        startRepeatFromTime = undoneList.get(undoneList.size() - 1).getRepeatStartTime(false);
//                        keepFirstGenerated = true; //true; //startRepeatFromTime.equals(now);
//                    } else {
//                        startRepeatFromTime = now;
//                        keepFirstGenerated = false; //true; //startRepeatFromTime.equals(now);
//                    }
//                    break;
//            }
//
//            Date subsetEndDate = calcSubsetEndDate();
//            int numberRepeats = calcNumberOfRepeats();
////        List<Date> dates = generateListOfDates(subsetBeginningDate, subsetEndDate, numberRepeats, getNumberOfDaysRepeatsAreGeneratedAhead() != 0, false);
//            List<Date> dates = generateListOfDates(startRepeatFromTime, subsetEndDate, numberRepeats, false, keepFirstGenerated);
//
////        ItemAndListCommonInterface owner = sourceWorkSlot!=null?sourceWorkSlot.getOwner():null;
////            ItemAndListCommonInterface owner = originatorWorkSlot.getOwner();
//            RepeatRuleObjectInterface repeatInstance;
//            for (Date nextRepeatTime : dates) {
//                repeatInstance = createCopyFromThis.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
//                createCopyFromThis = repeatInstance;
////            owner = sourceWorkSlot.insertIntoList(repeatInstance);
//                newRepeatWorkSlots.add(repeatInstance); //gather all new instances and update/save in batch below
//                if (Config.TEST) {
//                    Log.p("**new3 repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//                }
//            }
//            if (true || owner != null) {
//                owner.addWorkSlots(newRepeatWorkSlots);
//            }
//            setListOfUndoneInstances(newRepeatWorkSlots); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
//
//            deleteUnneededWorkSlotInstances(oldUndoneRepeatInstanceList); //only set here since otherwise sourceWS may be deleted before crepeat opies are made
//
//            DAO.getInstance().saveNew(newRepeatWorkSlots); //, false);
////        DAO.getInstance().saveNew(true, this, (ParseObject) owner);
//            DAO.getInstance().saveNew(this, (ParseObject) owner);
//            DAO.getInstance().saveNewExecuteUpdate();
//        }
//    }
//</editor-fold>
    /**
     * update lists by moving expired workslots to expiredWorkslots and create a
     * temporary copy in justExpiredWorkslots to use when generating new repeat
     * instances
     *
     * @param undoneList
     * @param doneList
     * @param justExpiredWorkslots
     * @param now
     */
    private static void moveExpiredWorkSlots(List undoneList, List doneList, Date now) {
//        for (Object w : orgUndoneRepeatInstanceList) {
        while (undoneList.size() > 0 && ((WorkSlot) undoneList.get(0)).getEndTime() <= now.getTime()) {
//            if (((WorkSlot) w).getEndTime() <= now.getTime()) {
            WorkSlot w = (WorkSlot) undoneList.remove(0);
            doneList.add((WorkSlot) w);
//            justExpiredWorkslots.add((WorkSlot) w);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * check if workSlot should repeat and if so, create new workslot instances,
     * add them to workslot's owners workslotlist
     *
     * @param sourceWorkSlot NO: null if just check if repeatRule needs to
     * update the generated workSlots (if null, workSlotDeleted is ignored)
     * @param workSlotIsGettingDeleted true means the workSlot is being deleted,
     * false, means first time generation based on this workSlot
     * @return
     */
//    private void updateWorkslotsForNewRepeatRuleXXX(RepeatRuleObjectInterface sourceWorkSlot) {
////            //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
//        //generate all missing (past and future) workslots
////        Date nextRepeatTime;
//        List<RepeatRuleObjectInterface> newRepeatWorkSlots = new ArrayList<>();
////        while ((nextRepeatTime = getNextDueDateN()) != null) {
//        List<Date> dates = getNextDueDateList();
//        ItemAndListCommonInterface owner = null;
//        for (Date nextRepeatTime : dates) {
//            RepeatRuleObjectInterface repeatInstance = sourceWorkSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
//            owner = sourceWorkSlot.insertIntoList(repeatInstance);
//            newRepeatWorkSlots.add(repeatInstance); //gather all new instances and update/save in batch below
//            if (Config.TEST) {
//                Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//            }
//        }
//        setListOfUndoneInstances(newRepeatWorkSlots); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
////        owner.addWorkSlots(newRepeatWorkSlots); //only add *generated* workslots (source is being added elsewhere)
////        newRepeatWorkSlots.add(0, sourceWorkSlot); //must add the source work slot (but do *after* generating new repeat instances since algo won't work otherwise)
////        setListOfUndoneRepeatInstances(newRepeatWorkSlots); //need to set again after adding sourceWorkSlot
//        if (Config.TEST) {
//            checkRefs();
//        }
//        DAO.getInstance().saveNew((List) newRepeatWorkSlots);
//        DAO.getInstance().saveNew(true, this, (ParseObject) owner);
//        //        return newRepeatWorkSlots.size() > 0; //true if any new workslots where created
//        {
//        }
//    }
//
//    private void updateWorkslotsForNewRepeatRuleOLD(WorkSlot sourceWorkSlot) {
////            //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
//        ItemAndListCommonInterface owner = sourceWorkSlot.getOwner();
//
//        //generate all missing (past and future) workslots
//        Date nextRepeatTime;
////        List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances(); //list will be empty first time
//        List newRepeatWorkSlots = new ArrayList<>();
//        while ((nextRepeatTime = getNextDueDateN()) != null) {
////            setLastGeneratedDate(nextRepeatTime); //update the last generated date (necessary to get right date on next call to getNextDueDate())
////            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//
//            WorkSlot repeatInstance = (WorkSlot) sourceWorkSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
////            oldUndoneRepeatInstanceList.add(repeatInstance); //always add to end => should ensure is always sorted!
////            setListOfUndoneRepeatInstances(oldUndoneRepeatInstanceList); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
////            if (Config.TEST) ASSERT.that(MyUtil.isSorted(oldUndoneRepeatInstanceList, (ws1, ws2) -> (int) (((WorkSlot) ws2).getStartTime() - ((WorkSlot) ws1).getStartTime())), "ERROR: new workslots not generated in sorted order!! workslots=" + oldUndoneRepeatInstanceList);
//            newRepeatWorkSlots.add(repeatInstance); //gather all new instances and update/save in batch below
//            setListOfUndoneInstances(newRepeatWorkSlots); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
//            if (Config.TEST) {
//                Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//            }
//        }
////        oldUndoneRepeatInstanceList.add(0,sourceWorkSlot); //must add the source work slot (but do *after* generating new repeat instances since algo won't work otherwise)
////        setListOfUndoneRepeatInstances(oldUndoneRepeatInstanceList); //need to set again after adding sourceWorkSlot
////        newRepeatWorkSlots.add(sourceWorkSlot); //add source here to save it (next line) to get an ObjId so it can be saved in the repeatRule
//        owner.addWorkSlots(newRepeatWorkSlots); //only add *generated* workslots (source is being added elsewhere)
//        newRepeatWorkSlots.add(0, sourceWorkSlot); //must add the source work slot (but do *after* generating new repeat instances since algo won't work otherwise)
//        setListOfUndoneInstances(newRepeatWorkSlots); //need to set again after adding sourceWorkSlot
//        if (Config.TEST) {
//            checkRefs();
//        }
//        DAO.getInstance().saveNew((List<ParseObject>) newRepeatWorkSlots);
//        DAO.getInstance().saveNew(true, this, (ParseObject) owner);
////        return newRepeatWorkSlots.size() > 0; //true if any new workslots where created
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateWhenWorkSlotDeleted(WorkSlot deletedWorkSlot) {//WorkSlot deletedWorkSlot) {
//        updateIfExpiredOrDeletedWorkslots(deletedWorkSlot);
//    }
    /**
     * return true if there are expired workslots meaning new repeating ones
     * need to be calculated
     *
     * @return
     */
//    private boolean isExpiredWorkslotsXXX() {
//        List<RepeatRuleObjectInterface> unexpired = getListOfUndoneRepeatInstances();
//        if (!unexpired.isEmpty()) {
//            RepeatRuleObjectInterface first = unexpired.get(0);
//            if (first instanceof WorkSlot) {
//                if (((WorkSlot) first).getEndTimeD().getTime() < MyDate.currentTimeMillis()) {
//                    return true;
//                } else {
//                    return false;
//                }
//            } else {
//                ASSERT.that(false);
//            }
//        }
//        return false;
//    }
//</editor-fold>
    /**
     * TRY TO COMBINE ALL WORKSLOT UPDATES INTO ONE METHOD (UPDATE WHEN TIME
     * PASSES, UPDATE IF RULE EDITED, UPDATE IF WORKSLOT DELETED). if
     * deletedWorkSlotN is not null, then hard-delete that workSlot and
     * recalculate workslots (in case there are expired workslots). If not null,
     * simply update workSlots.
     *
     * @param deletedWorkSlotN if null, simply recalculate any new workslots
     */
//    public void updateForWorkslots(WorkSlot deletedWorkSlotN, boolean workSlotDeleted, boolean refreshOnEdit) {
    public void updateIfExpiredWorkslots() {
        updateIfExpiredWorkslots(true, new MyDate());
    }

    /**
     *
     * @param executeSaveNow don't execute save if called when RepeatRule is
     * created or edited
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateIfExpiredWorkslots(boolean executeSaveNow, Date now) {
////        List<RepeatRuleObjectInterface> orgUndoneRepeatInstanceList = getListOfUndoneInstances();
//        List<RepeatRuleObjectInterface> undoneList = getListOfUndoneInstances(); //orgUndoneRepeatInstanceList
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false &&deletedWorkSlotN != null) { //NO impact on RR to delete a workslot (only mark it deleted and remove from owner
////            //a deleted workslot is just remove from its owner, but stays in the RR's lists to avoid e.g regenerating the same time again
////            ItemAndListCommonInterface owner = deletedWorkSlotN.getOwner();
////            List<RepeatRuleObjectInterface> expiredWorkSlots = getListOfDoneInstances();
////            if (orgUndoneRepeatInstanceList.remove(deletedWorkSlotN)) {
////                setListOfUndoneInstances(orgUndoneRepeatInstanceList);
////                expiredWorkSlots.add(deletedWorkSlotN);
////                setListOfDoneInstances(expiredWorkSlots);
//////                DAO.getInstance().delete(deletedWorkSlotN, true, false); //hadr-delete, but at end of this method
////            } else {
////                if (expiredWorkSlots.remove(deletedWorkSlotN)) {
////                    setListOfDoneInstances(expiredWorkSlots);
//////                    DAO.getInstance().delete(deletedWorkSlotN, true, false);
////                }
////            }
////            owner.removeWorkSlot(deletedWorkSlotN);
////        }
////</editor-fold>
//        //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
//        List<RepeatRuleObjectInterface> doneList = getListOfDoneInstances(); //expiredWorkSlots
////<editor-fold defaultstate="collapsed" desc="comment">
////remove all past workslots (this will also ensure the right number of new ones are generated)
////        long now = MyDate.currentTimeMillis();
////        Date now = new MyDate();
////        boolean undoneEmpty = orgUndoneRepeatInstanceList.isEmpty();;
////        if (false) {
////            ASSERT.that(!undoneList.isEmpty(), "error, no workslots to generate new copies from, repeatRule={" + this + "}");
////        }
////</editor-fold>
//        //if at least one workslot has expired since last update
//        if (undoneList.size() > 0 && ((WorkSlot) undoneList.get(0)).getEndTime() <= now.getTime()) {
//            List<ParseObject> newRepeatWorkSlotsToSave = new ArrayList();
//            List<ParseObject> justExpiredWorkslots = new ArrayList();
//            HashSet<ParseObject> editedOwners = new HashSet(); //store list of edited owners in case a future implementation makes it possible to move workslots to new owner
//            ItemAndListCommonInterface workSlotOwner = null;
//
//            moveExpiredWorkSlots(orgUndoneRepeatInstanceList, expiredWorkSlots, justExpiredWorkslots, now);
//
//            //generate all missing (past and future) workslots
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (true||expiredOrOriginatorWorkSlotN != null) {
////            Date startRepeatFromDate = getLatestDueDateDoneOrUndone(); //get the lastest date (could latest done date if no more future workslots, or it could be latest undone if not all workslots are expired
////</editor-fold>
////            Date startRepeatFromDate = getLatestDueDate(doneList, undoneList); //get the lastest date (could latest done date if no more future workslots, or it could be latest undone if not all workslots are expired
////            RepeatRuleObjectInterface latestElementN = getLatestN(doneList, undoneList,false); //get the lastest date (could latest done date if no more future workslots, or it could be latest undone if not all workslots are expired
//            RepeatRuleObjectInterface latestElement;//=  undoneList.size()>0?undoneList.get(undoneList.size()-1):doneList.get(doneList.size()-1); //get the lastest date (could latest done date if no more future workslots, or it could be latest undone if not all workslots are expired
////            ASSERT.that(latestElementN!=null, "should never be possible to no elements in both done and undone lists");  //NOT necessary due to test above: if (undoneList.size() > 0
////            boolean keepFirstGenerated;
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (startRepeatFromDate.getTime() == MyDate.MIN_DATE) { //if first time
////                startRepeatFromDate = now; //new MyDate(); //start repeating from now
////                keepFirstGenerated = true;
////            } else {
////                keepFirstGenerated = false; //drop first generated since it will be identifical to startRepeatFromDate
////            }
////</editor-fold>
////            if (latestElementN.getTime() == MyDate.MIN_DATE) { //if first time
////                startRepeatFromDate = now; //new MyDate(); //start repeating from now
////                keepFirstGenerated = true;
////            } else {
////                keepFirstGenerated = false; //drop first generated since it will be identifical to startRepeatFromDate
////            }
//            Date startRepeatFromDate = latestElement.getRepeatStartTime(false); //new MyDate(); //start repeating from now
//
//            //generate dates the first time
////            List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstGenerated); //keepFirstDateGenerated since we're starting from 'now' so highly unlikely we recalculate now as a date, generate future workslots
////            List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(undoneList.size()), false, false); //keepFirstDateGenerated since we're starting from 'now' so highly unlikely we recalculate now as a date, generate future workslots
////            ASSERT.that(!undoneEmpty, "error, undone.isEmpty => not workslots to generate new copies from, for dates=" + dates);
//            if (Config.TEST) {
//                ASSERT.that(dates.size() == justExpiredWorkslots.size(), "error, we should never generate more dates than what corresponds to the number of justExpired workslots, dates=[" + dates + "], justExpired=[" + justExpiredWorkslots + "]");
//            }
//
//            int nbRepeatsToGenerate;
//            workSlotOwner = ((WorkSlot) latestElement).getOwner(); //asume all workslots have same owner!
//            do { //keep repeating as long as there are still new dates to generate
//                moveExpiredWorkSlots(undoneList, doneList, justExpiredWorkslots, now); //move expired workslots to doneList and return them in justExpiredWorkslots
//                latestElement = undoneList.size() > 0 ? undoneList.get(undoneList.size() - 1) : doneList.get(doneList.size() - 1); //get the lastest date (could latest done date if no more future workslots, or it could be latest undone if not all workslots are expired
////<editor-fold defaultstate="collapsed" desc="comment">
////            RepeatRuleObjectInterface workSlotToCreateRepeatCopyFrom = expiredOrOriginatorWorkSlotN; //initialize
////                RepeatRuleObjectInterface workSlotToCreateRepeatCopyFrom = orgUndoneRepeatInstanceList.get(orgUndoneRepeatInstanceList.size() - 1); //create copies from latests future workslot
////            List<WorkSlot> newRepeatWorkSlotsToSave = new ArrayList();
////                List<ParseObject> newRepeatWorkSlotsToSave = new ArrayList();
////                WorkSlot nextExpired;
////                ItemAndListCommonInterface workSlotOwner = null;
////</editor-fold>
//                 nbRepeatsToGenerate = calcNumberOfRepeats(undoneList.size());
//                List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), nbRepeatsToGenerate, false, false); //keepFirstDateGenerated since we're starting from 'now' so highly unlikely we recalculate now as a date, generate future workslots
//                for (Date nextRepeatTime : dates) {
////<editor-fold defaultstate="collapsed" desc="comment">
////                             nextExpired = orgUndoneRepeatInstanceList.size() > 0 && ((WorkSlot) orgUndoneRepeatInstanceList.get(0)).getEndTime() <= now.getTime()
////                    ? ((WorkSlot) orgUndoneRepeatInstanceList.get(0))
////                    : null;
////                    WorkSlot nextExpired = justExpiredWorkslots.isEmpty()? (WorkSlot) justExpiredWorkslots.remove(0);
////                    WorkSlot nextExpired = (WorkSlot) orgUndoneRepeatInstanceList.remove(0);
////</editor-fold>
////                    WorkSlot nextExpired = null;//= (WorkSlot) justExpiredWorkslots.remove(0);
////                    if (true) {
////                        if (justExpiredWorkslots.size() > 0) {
////                            nextExpired = (WorkSlot) justExpiredWorkslots.remove(0);
////                        }
////                    } else {
////                        nextExpired = (WorkSlot) justExpiredWorkslots.remove(0);
////                    }
////                    if (Config.TEST) {
////                        ASSERT.that(nextExpired == null || nextExpired.getEndTime() <= now.getTime());
////                    }
////                    expiredWorkSlots.add(nextExpired); //NOW done above in moveExpiredWorkSlots(). //move to expired
//
//                    WorkSlot newRepeatInstance = (WorkSlot) latestElement.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
//                    //get the owner
////                    workSlotOwner = ((WorkSlot) nextExpired).getOwner();
//                    workSlotOwner.addWorkSlot(newRepeatInstance);
////                    editedOwners.add((ParseObject) workSlotOwner);
////                    if (Config.TEST) {
////                        ASSERT.that(editedOwners.size() <= 1, "shouldn't have more than one edited owner in the current implementation, editedOwners=[" + editedOwners + "]");
////                    }
////                    workSlotToCreateRepeatCopyFrom = nextRepeatInstance; //UI: each new workslot is a copy of the previous one (keep a chain)
//
//                    undoneList.add(newRepeatInstance); //initially add all new workslots to undone, then (in a following iteration) move all expired (already expired or just generated) to expired
//                    newRepeatWorkSlotsToSave.add(newRepeatInstance); //gather all new instances and update/save in batch below
//                    if (Config.TEST) {
//                        Log.p("**new4 repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + newRepeatInstance);
//                    }
//                }
//                moveExpiredWorkSlots(undoneList, doneList, justExpiredWorkslots, now); //move expired workslots to doneList and return them in justExpiredWorkslots
//
//                int justExpiredWorkslotsSize = justExpiredWorkslots.size();
//                moveExpiredWorkSlots(undoneList, doneList, justExpiredWorkslots, now);
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (false) { //not necessary here since getListOfDoneInstances will return the same list instance as already used, enough to do when exiting this method
////                    setListOfDoneInstances(doneList);
////                    setListOfUndoneInstances(undoneList); //MUST update listOfUndone to calc correct numberOfRepeats
////                }
////</editor-fold>
//                //if the RR hasn't run for a long time, and only generates eg 2 future instances each time, it may need to executed multiples times to catch up
//                startRepeatFromDate = getLatestDueDate(doneList, undoneList); //get the lastest, can not be MIN_DATE here
////                keepFirstGenerated = false; //can only be true first time
//                dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(undoneList.size()), false, false); //keepFirstDateGenerated since we're starting from 'now' so highly unlikely we recalculate now as a date, generate future workslots
//                if (Config.TEST) {
//                    ASSERT.that(dates.size() == 0 || justExpiredWorkslotsSize == 0, "error, there shouldn't be any new dates generated if there are still justExpired workslots lets, dates=["
//                            + dates + "], justExpired=[" + justExpiredWorkslots + "]");
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                while (!orgUndoneRepeatInstanceList.isEmpty() && orgUndoneRepeatInstanceList.get(0)) {
////                    if (nextRepeatInstance.getEndTime() <= now.getTime()) { //if new workslot expired (endDate<=now)
////                        expiredWorkSlots.add(nextRepeatInstance);
////                    } else {
////                        orgUndoneRepeatInstanceList.add(nextRepeatInstance);
////                    }
////
////            }
////</editor-fold>
////            } while (!dates.isEmpty());
//            } while (undoneList.size() < nbRepeatsToGenerate);
////<editor-fold defaultstate="collapsed" desc="comment">
////            workSlotOwner.addWorkSlots(newRepeatWorkSlotsToSave);
//
////            if (deletedWorkSlotN != null) {
////                DAO.getInstance().delete(deletedWorkSlotN, true, false);
////            }
////</editor-fold>
//            setListOfDoneInstances(doneList);
//            setListOfUndoneInstances(undoneList); //MUST update listOfUndone to calc correct numberOfRepeats
////<editor-fold defaultstate="collapsed" desc="comment">
////            DAO.getInstance().saveNew(newRepeatWorkSlotsToSave);
////            DAO.getInstance().saveNew(editedOwners);
////            DAO.getInstance().saveNew(this); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
////            if (executeSave) {
////                DAO.getInstance().saveNewTriggerUpdate(); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
////            }
////            DAO.getInstance().saveToParseLater(newRepeatWorkSlotsToSave);
////</editor-fold>
//            if (false) {
//                DAO.getInstance().saveToParseLater(editedOwners);//should be saved via RR
//            }
//            if (executeSaveNow) {
//                DAO.getInstance().saveToParseNow(this); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
//            } else {
//                DAO.getInstance().saveToParseLater(this); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
//            }
//        } //END if (undoneList.size() > 0 && ((WorkSlot) undoneList.get(0)).getEndTime() <= now.getTime())
//    }
//</editor-fold>
    public void updateIfExpiredWorkslots(boolean executeSaveNow, Date now) {
        List<RepeatRuleObjectInterface> undoneList = getListOfUndoneInstances();
        //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
        List<RepeatRuleObjectInterface> doneList = getListOfDoneInstances(); //expiredWorkSlots
        //if at least one workslot has expired since last update
        if (undoneList.size() > 0 && ((WorkSlot) undoneList.get(0)).getEndTime() <= now.getTime()) {
            //generate all missing (past and future) workslots
            RepeatRuleObjectInterface latestElement;//=  undoneList.size()>0?undoneList.get(undoneList.size()-1):doneList.get(doneList.size()-1); //get the lastest date (could latest done date if no more future workslots, or it could be latest undone if not all workslots are expired
            Date startRepeatFromDate;//= latestElement.getRepeatStartTime(false); //new MyDate(); //start repeating from now
            ItemAndListCommonInterface workSlotOwner = null;
            int nbRepeatsToGenerate;

            moveExpiredWorkSlots(undoneList, doneList, now); //move expired workslots to doneList and return them in justExpiredWorkslots
            do { //keep repeating as long as there are still new dates to generate (there may many to catch up with)
                latestElement = undoneList.size() > 0 ? undoneList.get(undoneList.size() - 1) : doneList.get(doneList.size() - 1); //get the lastest date (could latest done date if no more future workslots, or it could be latest undone if not all workslots are expired
                startRepeatFromDate = latestElement.getRepeatStartTime(false); //new MyDate(); //start repeating from now
                workSlotOwner = ((WorkSlot) latestElement).getOwner(); //asume all workslots have same owner!
                nbRepeatsToGenerate = calcNumberOfRepeats(undoneList.size());

                List<Date> dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), nbRepeatsToGenerate, false, false); //keepFirstDateGenerated since we're starting from 'now' so highly unlikely we recalculate now as a date, generate future workslots

                for (Date nextRepeatTime : dates) {
                    WorkSlot newRepeatInstance = (WorkSlot) latestElement.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
                    latestElement = newRepeatInstance; //ensure chain of copies
                    workSlotOwner.addWorkSlot(newRepeatInstance);
                    undoneList.add(newRepeatInstance); //initially add all new workslots to undone, then (in a following iteration) move all expired (already expired or just generated) to expired
                }
                moveExpiredWorkSlots(undoneList, doneList, now); //move expired workslots to doneList and return them in justExpiredWorkslots              
                //if the RR hasn't run for a long time, and only generates eg 2 future instances each time, it may need to executed multiples times to catch up
//                startRepeatFromDate = getLatestDueDate(doneList, undoneList); //get the lastest, can not be MIN_DATE here
//                dates = generateListOfDates(startRepeatFromDate, calcSubsetEndDate(), calcNumberOfRepeats(undoneList.size()), false, false); //keepFirstDateGenerated since we're starting from 'now' so highly unlikely we recalculate now as a date, generate future workslots
            } while (undoneList.size() < nbRepeatsToGenerate); //repeat if there are not yet enough future workslots (if some of the new ones were still in the past)
            setListOfDoneInstances(doneList);
            setListOfUndoneInstances(undoneList); //MUST update listOfUndone to calc correct numberOfRepeats
            if (executeSaveNow) {
                DAO.getInstance().saveToParseNow(this); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
            } else {
                DAO.getInstance().saveToParseLater(this); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
            }
        } //END if (undoneList.size() > 0 && ((WorkSlot) undoneList.get(0)).getEndTime() <= now.getTime())
    }

    public void updateWhenWorkslotDeleted(WorkSlot deletedWorkSlotN) {
        //do nothing (for now?!) - no need to update RepeatRule, since workslot instances are only generated whey old ones expire (unlike RR for tasks where new ones are generated on deletion)
    }

    /**
     * called to updated
     *
     * @param modifiedWorkSlotN
     */
    public void updateWorkslotInstancesWhenWorkSlotModifiedXXX(WorkSlot modifiedWorkSlot) {
        //do nothing (for now?!) - no need to update RepeatRule, since workslot instances are only generated whey old ones expire (unlike RR for tasks where new ones are generated on deletion)
        List undone = new ArrayList(getListOfUndoneInstances());
        undone.remove(modifiedWorkSlot); //don't update the modified workslot
        for (Object workslot : undone) {
            ((WorkSlot) workslot).update(modifiedWorkSlot);
        }
//        DAO.getInstance().saveNew(undone); //save all updated (will be only saved if actually modified
//        DAO.getInstance().saveToParseNow(undone); //save all updated (will be only saved if actually modified
        DAO.getInstance().saveToParseLater(undone); //save all updated (will be only saved if actually modified), triggered on saving modified WorkSlot
//        DAO.getInstance().triggerParseUpdate(); //no need to trigger save here since this is only called from ScreenWorkSlot who will call triggerSave
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateIfExpiredOrDeletedWorkslotsXXX(WorkSlot deletedWorkSlotN) {
//        List<RepeatRuleObjectInterface> orgUndoneRepeatInstanceList = getListOfUndoneInstances();
//        int oldUndoneListSize = orgUndoneRepeatInstanceList.size();
//        if (deletedWorkSlotN != null) {
//            //a deleted workslot is just remove from its owner, but stays in the RR's lists to avoid e.g regenerating the same time again
//            ItemAndListCommonInterface owner = deletedWorkSlotN.getOwner();
//            List<RepeatRuleObjectInterface> expiredWorkSlots = getListOfDoneInstances();
//            if (orgUndoneRepeatInstanceList.remove(deletedWorkSlotN)) {
//                setListOfUndoneInstances(orgUndoneRepeatInstanceList);
//                expiredWorkSlots.add(deletedWorkSlotN);
//                setListOfDoneInstances(expiredWorkSlots);
////                DAO.getInstance().delete(deletedWorkSlotN, true, false); //hadr-delete, but at end of this method
//            } else {
//                if (expiredWorkSlots.remove(deletedWorkSlotN)) {
//                    setListOfDoneInstances(expiredWorkSlots);
////                    DAO.getInstance().delete(deletedWorkSlotN, true, false);
//                }
//            }
//            owner.removeWorkSlot(deletedWorkSlotN);
//        }
//
////            //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
//        List<RepeatRuleObjectInterface> expiredWorkSlots = getListOfDoneInstances();
//        List<RepeatRuleObjectInterface> reuseWorkSlotsToGenerateNewRepeatCopies = new ArrayList(); //this list may contain the deleted element as well
//        if (deletedWorkSlotN != null) {
//            reuseWorkSlotsToGenerateNewRepeatCopies.add(deletedWorkSlotN); //use to generate new workSlot copy from / get owner from
//        }
//
//        //remove all past workslots (this will also ensure the right number of new ones are generated)
//        long now = MyDate.currentTimeMillis();
//        Date nowD = new MyDate(now);
//        WorkSlot expiredWorkSlotN = null;
//        while (orgUndoneRepeatInstanceList.size() > 0 && ((WorkSlot) orgUndoneRepeatInstanceList.get(0)).getEndTime() <= now) { //optimization: this runs through all workslots, not necessary if we first sort them on endTime
//            expiredWorkSlotN = (WorkSlot) orgUndoneRepeatInstanceList.remove(0); //remove expired workslot
//            //UI: keep expired workslots attached to their owner
////            expiredWorkSlot.removeFromOwner();
//            reuseWorkSlotsToGenerateNewRepeatCopies.add(expiredWorkSlotN);
//            expiredWorkSlots.add(expiredWorkSlotN);
//        }
//
//        if (orgUndoneRepeatInstanceList.size() < oldUndoneListSize) { //if at least one workslot had expired
//            //save updated lists
//            setListOfDoneInstances(expiredWorkSlots);
//            setListOfUndoneInstances(orgUndoneRepeatInstanceList); //update so correct size is used when calculating additional repeat dates
//
//            //find owner
//            ItemAndListCommonInterface workSlotOwnerN = null;
//            if (deletedWorkSlotN != null) {
//                workSlotOwnerN = deletedWorkSlotN.getOwner();
//            }
//
//            //generate all missing (past and future) workslots
//            List newRepeatWorkSlots = new ArrayList<>();
//            if (expiredWorkSlotN == null) {
//                expiredWorkSlotN = deletedWorkSlotN; //UI: make copies from (latest) expired workslot, if any. Otherwise from oldWorkSlotN
//            }
//
//            if (expiredWorkSlotN != null) {
////                Date latestDateCompletedOrCancelled = getLatestDateDone(); //expensive, so only call once
////                Date getNextRepeatAfterDate = latestDateCompletedOrCancelled.getTime() != 0 ? latestDateCompletedOrCancelled : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
//                Date startRepeatFromDate = getStartDateForCompletion(nowD, false);//Re-start repeat from the last date a task was cancelled/done
//                boolean keepFirstGenerated = startRepeatFromDate.equals(now);
//                //NB. generate dates must catch up to 'now', e.g. if repeating daily, generating 3 ahead, then if updating after several weeks it must create ALL the intermediate instances!!
////                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, true); //recalcCurrentDate = 1;
//                List<Date> pastDates = generateListOfDates(startRepeatFromDate, nowD, Integer.MAX_VALUE, false, true); //generate past (workslots (that ahould already have been generated but may have been skipped)
//
//                List<Date> futureDates = generateListOfDates(nowD, calcSubsetEndDate(), calcNumberOfRepeats(), false, keepFirstGenerated); //keepFirstDateGenerated since we're starting from 'now' so highly unlikely we recalculate now as a date, generate future workslots
//                List<Date> dates = pastDates;
//                dates.addAll(futureDates);
//
//                RepeatRuleObjectInterface workSlotToCreateRepeatCopyFrom = null;
////                while (expiredWorkSlotN != null && (nextRepeatTime = getNextDueDateN()) != null) {
//                ASSERT.that(!reuseWorkSlotsToGenerateNewRepeatCopies.isEmpty(), "error, not workslots to generate new copies from");
//                while (!dates.isEmpty()) {
//                    Date nextRepeatTime = dates.remove(0);
//
//                    if (!reuseWorkSlotsToGenerateNewRepeatCopies.isEmpty()) { //UI: create copies from each past individual workslot //TODO!!!: support updating all to new dates
//                        workSlotToCreateRepeatCopyFrom = reuseWorkSlotsToGenerateNewRepeatCopies.remove(0);
//                    }
//
//                    //get the owner if not already found
//                    if (workSlotOwnerN == null) {
//                        workSlotOwnerN = ((WorkSlot) workSlotToCreateRepeatCopyFrom).getOwner();
//                    }
//
//                    WorkSlot nextRepeatInstance = (WorkSlot) workSlotToCreateRepeatCopyFrom.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
//                    if (nextRepeatInstance.getEndTime() <= now) {
//                        expiredWorkSlots.add(nextRepeatInstance);
//                    } else {
//                        orgUndoneRepeatInstanceList.add(nextRepeatInstance);
//                    }
////                    workSlotOwnerN.addWorkSlot(nextRepeatInstance); //add to owner //DONE below for all new instances in one go
//
//                    newRepeatWorkSlots.add(nextRepeatInstance); //gather all new instances and update/save in batch below
//                    if (Config.TEST) {
//                        Log.p("**new5 repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + nextRepeatInstance);
//                    }
//                }
//
//                workSlotOwnerN.addWorkSlots(newRepeatWorkSlots);
//                setListOfDoneInstances(expiredWorkSlots);
//                setListOfUndoneInstances(orgUndoneRepeatInstanceList);
//
//                if (deletedWorkSlotN != null) {
//                    DAO.getInstance().delete(deletedWorkSlotN, true, false);
//                }
////                DAO.getInstance().saveNew(newRepeatWorkSlots, false);
//                DAO.getInstance().saveNew(newRepeatWorkSlots);
////                DAO.getInstance().saveNew(true, this, (ParseObject) workSlotOwnerN); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
//                DAO.getInstance().saveNew(this, (ParseObject) workSlotOwnerN); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
//                DAO.getInstance().saveNewExecuteUpdate(); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void updateWhenWorkSlotDeleted(WorkSlot deletedWorkSlotN) {
//        if (deletedWorkSlotN == null) {
//            return;
//        }
//        ItemAndListCommonInterface owner = deletedWorkSlotN.getOwner();
//        List<RepeatRuleObjectInterface> orgUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();
//        int oldUndoneListSize = orgUndoneRepeatInstanceList.size();
//        if (orgUndoneRepeatInstanceList.remove(deletedWorkSlotN)) {
//            setListOfUndoneInstances(orgUndoneRepeatInstanceList);
//            DAO.getInstance().delete(deletedWorkSlotN, true, false); //hadr-delete, but at end of this method
//        } else {
//            List<RepeatRuleObjectInterface> expiredWorkSlots = getListOfDoneInstances();
//            if (expiredWorkSlots.remove(deletedWorkSlotN)) {
//                setListOfDoneInstances(expiredWorkSlots);
//                DAO.getInstance().delete(deletedWorkSlotN, true, false);
//            }
//        }
//
//        if (orgUndoneRepeatInstanceList.size() < oldUndoneListSize) { //if at least one workslot had expired
//            //generate all missing (past and future) workslots
//            Date nextRepeatTime;
//            List newRepeatWorkSlots = new ArrayList<>();
////            if (oldWorkSlotN != null) {
////                expiredWorkSlot = oldWorkSlotN; //UI: make repeat copies from the deleted workslot rather than any expired ones
////            }
//            if (expiredWorkSlotN == null) {
//                expiredWorkSlotN = deletedWorkSlotN; //UI: make copies from (latest) expired workslot, if any. Otherwise from oldWorkSlotN
//            }
//
//            if (expiredWorkSlotN != null) {
//                Date latestDateCompletedOrCancelled = getLatestDateCompletedOrCancelled(); //expensive, so only call once
//                Date getNextRepeatAfterDate = latestDateCompletedOrCancelled.getTime() != 0 ? latestDateCompletedOrCancelled : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
//                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, true); //recalcCurrentDate = 1;
//
//                while (expiredWorkSlotN != null && (nextRepeatTime = getNextDueDateN()) != null) {
////                setLastGeneratedDate(nextRepeatTime); //update the last generated date (necessary to get right date on next call to getNextDueDate())
//
//                    WorkSlot repeatInstance = (WorkSlot) expiredWorkSlotN.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
//                    xxx;
//                    orgUndoneRepeatInstanceList.add(repeatInstance); //always add to end => should ensure is always sorted!
//                    setListOfUndoneInstances(orgUndoneRepeatInstanceList); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
////                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (Config.TEST) {
////                    ASSERT.that(MyUtil.isSorted(orgUndoneRepeatInstanceList, (ws1, ws2) -> (int) (((WorkSlot) ws2).getStartTime() - ((WorkSlot) ws1).getStartTime())), "ERROR: new workslots not generated in sorted order!! workslots=" + orgUndoneRepeatInstanceList);
////                }
////</editor-fold>
//                    newRepeatWorkSlots.add(repeatInstance); //gather all new instances and update/save in batch below
//                    if (Config.TEST) {
//                        Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//                    }
//                }
//                workSlotOwnerN.addWorkSlots(newRepeatWorkSlots);
//
////            DAO.getInstance().saveNew((List<ParseObject>) updatedOwners);
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (false) {
////                DAO.getInstance().saveInBackground((List<ParseObject>) newRepeatWorkSlots);
////            }
////            if (Config.TEST) {
////                checkRefs();
////            }
////</editor-fold>
//                DAO.getInstance().saveNew(newRepeatWorkSlots, false);
//                DAO.getInstance().saveNew(true, this, (ParseObject) workSlotOwnerN); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
////            return newRepeatWorkSlots.size() > 0; //true if any new workslots where created
//            }
//        }
//    }
//
//    private void updateIfExpiredOrDeletedWorkslotsOLDXXX(WorkSlot deletedWorkSlotN) {//WorkSlot deletedWorkSlot) {
////            //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
//        long now = MyDate.currentTimeMillis();
//
//        List<RepeatRuleObjectInterface> expiredWorkSlots = getListOfDoneInstances();
//        List<RepeatRuleObjectInterface> orgUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();
//        List<RepeatRuleObjectInterface> expiredTempList = new ArrayList();
//        int oldUndoneListSize = orgUndoneRepeatInstanceList.size();
//
//        if (deletedWorkSlotN != null) {
//            ASSERT.that(orgUndoneRepeatInstanceList.contains(deletedWorkSlotN), "removing a deleted workslot which is NOT in list of still active workslots, deletedWorkSlot=" + deletedWorkSlotN);
//            orgUndoneRepeatInstanceList.remove(deletedWorkSlotN);
//            //no need to remove deletedWorkSlot from owner, that is done in the delete method
//        }
//
//        //remove all past workslots (this will also ensure the right number of new ones are generated)
////        Set updatedOwners = new HashSet(); //only ONE owner allowed
//        WorkSlot expiredWorkSlotN = null;
//        while (orgUndoneRepeatInstanceList.size() > 0 && ((WorkSlot) orgUndoneRepeatInstanceList.get(0)).getEndTime() <= now) { //optimization: this runs through all workslots, not necessary if we first sort them on endTime
//            expiredWorkSlotN = (WorkSlot) orgUndoneRepeatInstanceList.remove(0); //remove expired workslot
//            //and remove expired workSlot from owner's list
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (workSlotOwnerN == null) { //if oldWorkSlotN==null
////                workSlotOwnerN = expiredWorkSlot.getOwner();
////            }
////            if (workSlotOwnerN != null) {
////                WorkSlotList workSlotListN = workSlotOwnerN.getWorkSlotListN();
////                if (workSlotListN != null) {
////                    workSlotListN.remove(expiredWorkSlot);
////                    workSlotOwnerN.setWorkSlotList(workSlotListN);
//////                    updatedOwners.add(workSlotOwnerN);
////                }
////            }
////</editor-fold>
////            expiredWorkSlot.removeFromOwner(); //UI: keep expired workslots attached to their owner
//            expiredWorkSlots.add(expiredWorkSlotN);
//        }
//
////        setListOfUndoneRepeatInstances(oldUndoneRepeatInstanceList); //update so correct size is used when calculating additional repeat dates
////        if (expiredWorkSlot == null || oldSize-oldUndoneRepeatInstanceList.size()>0) { //if at least one workslot had expired
////        if (oldUndoneListSize - orgUndoneRepeatInstanceList.size() > 0) { //if at least one workslot had expired
//        if (orgUndoneRepeatInstanceList.size() < oldUndoneListSize) { //if at least one workslot had expired
//            ItemAndListCommonInterface workSlotOwnerN = deletedWorkSlotN != null ? deletedWorkSlotN.getOwner() : null;
//
//            setListOfDoneInstances(expiredWorkSlots);
//            setListOfUndoneInstances(orgUndoneRepeatInstanceList); //update so correct size is used when calculating additional repeat dates
////            DAO.getInstance().saveInBackground(this, (ParseObject) owner);
////        } else {// (expiredWorkSlot != null) { //if at least one workslot had expired
//            //generate all missing (past and future) workslots
//            Date nextRepeatTime;
//            List newRepeatWorkSlots = new ArrayList<>();
////            if (oldWorkSlotN != null) {
////                expiredWorkSlot = oldWorkSlotN; //UI: make repeat copies from the deleted workslot rather than any expired ones
////            }
//            if (expiredWorkSlotN == null) {
//                expiredWorkSlotN = deletedWorkSlotN; //UI: make copies from (latest) expired workslot, if any. Otherwise from oldWorkSlotN
//            }
//
//            if (expiredWorkSlotN != null) {
//                Date latestDateCompletedOrCancelled = getLatestDateCompletedOrCancelled(); //expensive, so only call once
//                Date getNextRepeatAfterDate = latestDateCompletedOrCancelled.getTime() != 0 ? latestDateCompletedOrCancelled : getSpecifiedStartDate(); //Re-start repeat from the last date a task was cancelled/done
//                List<Date> dates = generateListOfDates(getNextRepeatAfterDate, calcSubsetEndDate(), calcNumberOfRepeats(), false, true); //recalcCurrentDate = 1;
//
//                while (expiredWorkSlotN != null && (nextRepeatTime = getNextDueDateN()) != null) {
////                setLastGeneratedDate(nextRepeatTime); //update the last generated date (necessary to get right date on next call to getNextDueDate())
//
//                    WorkSlot repeatInstance = (WorkSlot) expiredWorkSlotN.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
//                    xxx;
//                    orgUndoneRepeatInstanceList.add(repeatInstance); //always add to end => should ensure is always sorted!
//                    setListOfUndoneInstances(orgUndoneRepeatInstanceList); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
////                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (Config.TEST) {
////                    ASSERT.that(MyUtil.isSorted(orgUndoneRepeatInstanceList, (ws1, ws2) -> (int) (((WorkSlot) ws2).getStartTime() - ((WorkSlot) ws1).getStartTime())), "ERROR: new workslots not generated in sorted order!! workslots=" + orgUndoneRepeatInstanceList);
////                }
////</editor-fold>
//                    newRepeatWorkSlots.add(repeatInstance); //gather all new instances and update/save in batch below
//                    if (Config.TEST) {
//                        Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//                    }
//                }
//                workSlotOwnerN.addWorkSlots(newRepeatWorkSlots);
//
////            DAO.getInstance().saveNew((List<ParseObject>) updatedOwners);
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (false) {
////                DAO.getInstance().saveInBackground((List<ParseObject>) newRepeatWorkSlots);
////            }
////            if (Config.TEST) {
////                checkRefs();
////            }
////</editor-fold>
//                DAO.getInstance().saveNew(newRepeatWorkSlots, false);
//                DAO.getInstance().saveNew(true, this, (ParseObject) workSlotOwnerN); //saving RR will save repeatInstances, which must be done before saving workSlotOwner!
////            return newRepeatWorkSlots.size() > 0; //true if any new workslots where created
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateIfExpiredWorkslotsXXX() {//WorkSlot deletedWorkSlot) {
////        updateIfExpiredOrDeletedWorkslots(null);
//        updateWhenWorkslotDeleted(null);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void updateWorkSlotsWhenRuleCreatedOrEditedOLD(WorkSlot sourceWorkSlot, boolean firstTime) {
////            //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
////boolean firstTime=getListOfDoneInstances().isEmpty()&&getListOfUndoneRepeatInstances().isEmpty();
//        long now = MyDate.currentTimeMillis();
//        ItemAndListCommonInterface owner = null;
//
////        WorkSlot existingWorkSlot = null;
//        //UI: delete all previuously generated workslots (no reuse of workslot instances since very unlikely they contain manually edited data (other than name)
//        updateIfExpiredWorkslots(); //refresh to ensure any already expired workslots are not removed and new ones are generated
//
//        List oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();
////        boolean originatorInList = oldUndoneRepeatInstanceList.size() > getTotalNumberOfInstancesGeneratedSoFar();
//        ASSERT.that(oldUndoneRepeatInstanceList.contains(sourceWorkSlot));
//        oldUndoneRepeatInstanceList.remove(sourceWorkSlot);
////        setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() - oldUndoneRepeatInstanceList.size() + (originatorInList ? 1 : 0)); //reduce count for deleted instances
////<editor-fold defaultstate="collapsed" desc="comment">
//        while (oldUndoneRepeatInstanceList.size() > 0) { //deal with all previously generated, and still not prcocessed, workslots
//            //remove all past workslots (this will also ensure the right number of new ones are generated)
//            //all past or future already generated workslots are removed from their owner as well as oldUndoneRepeatInstanceList
//            WorkSlot existingWorkSlot = (WorkSlot) oldUndoneRepeatInstanceList.remove(0); //remove workslot
//            //and remove workSlot from owner's list
//            owner = existingWorkSlot.getOwner();
//            WorkSlotList workSlotList = owner.getWorkSlotListN(); //workSlotList cannot be null since existingWorkSlot comes from it
//            workSlotList.remove(existingWorkSlot);
//            owner.setWorkSlotList(workSlotList);
////            if (existingWorkSlot.getEndTime() >= now) {
////                DAO.getInstance().deleteInBackground(existingWorkSlot);
//            boolean hardDeleteNotYetExpiredWorkSlot = existingWorkSlot.getEndTime() >= now; //UI: hard-delete any future (or ongoing since likely they triggered user editing, OR user understands what he does and that not-yet expired workslots will be affected) instances (since no work has been done in them)
//            DAO.getInstance().delete(existingWorkSlot, hardDeleteNotYetExpiredWorkSlot, false); //UI: hard-delete any future instances (since no work has been done in them), but keep ongoing
////            }
//        }
////</editor-fold>
////        owner = sourceWorkSlot.getOwner();
////        owner.removeWorkSlots(oldUndoneRepeatInstanceList); //UI: remove
////        DAO.getInstance().deleteAll(oldUndoneRepeatInstanceList, true, false); //UI: hard-delete any future instances (since no work has been done in them), but keep ongoing
//        setListOfUndoneInstances(null); //remove all workslots so regeneration 'starts from scratch'
//
//        //generate all  workslots from new rule
//        Date nextRepeatTime;
//        List newRepeatWorkSlots = new ArrayList<>();
//        while ((nextRepeatTime = getNextDueDateN()) != null) {
////            setLastGeneratedDate(nextRepeatTime); //update the last generated date (necessary to get right date on next call to getNextDueDate())
//            WorkSlot repeatInstance = (WorkSlot) sourceWorkSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date). //UI: Always makes a copy of the *last* expired workslots
////<editor-fold defaultstate="collapsed" desc="comment">
////            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
////            oldUndoneRepeatInstanceList.add(repeatInstance); //always add to end => should ensure is always sorted!
////            setListOfUndoneRepeatInstances(oldUndoneRepeatInstanceList); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
////            if (Config.TEST) {
////                ASSERT.that(MyUtil.isSorted(oldUndoneRepeatInstanceList, (ws1, ws2) -> (int) (((WorkSlot) ws2).getStartTime() - ((WorkSlot) ws1).getStartTime())), "ERROR: new workslots not generated in sorted order!! workslots=" + oldUndoneRepeatInstanceList);
////            }
////            if (Config.TEST) {
////                Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
////            }
////</editor-fold>
//            newRepeatWorkSlots.add(repeatInstance); //gather all new instances and update/save in batch below
//        }
//        owner.addWorkSlots(newRepeatWorkSlots);
////        setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + newRepeatWorkSlots.size());
////<editor-fold defaultstate="collapsed" desc="comment">
////        DAO.getInstance().saveInBackground((List<ParseObject>) newRepeatWorkSlots);
////        if (Config.TEST) {
////            checkRefs();
////        }
////</editor-fold>
//        DAO.getInstance().saveNew(newRepeatWorkSlots, false);
//        DAO.getInstance().saveNew(true, this, (ParseObject) owner);
////            return newRepeatWorkSlots.size() > 0; //true if any new workslots where created
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean updateWorkslotsXXX(WorkSlot expiredWorkSlot, boolean workSlotIsGettingDeleted) {
////        if (true) return false;
////        if (workSlot.getEndTime()<System.currentTimeMillis()) { //if workSlot has expired
////            //NB. When we call this update, there may be several expired instances of same workslot, so we should calculate all that should exist by now (as well as past workslots that never got created!)
////        }
//        long now = MyDate.currentTimeMillis();
//
//        List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
//        if (workSlotIsGettingDeleted)
//            oldUndoneRepeatInstanceList.remove(expiredWorkSlot); //this will force generation of an additional workSlot
//        else if (getTotalNumberOfInstancesGeneratedSoFar() == 0)
//            oldUndoneRepeatInstanceList.add(expiredWorkSlot); //add workSlot originator the first time the rule is being executed
//        else { //workSlot==null => some workSlots should already have been generated (or NOT, since it may only repeat one slot at a time!)
////        if (Config.TEST) ASSERT.that(oldUndoneRepeatInstanceList.size()>0, "RepeatRule.updateWorkslots called with workSlot==null AND no previous workSlots in ListOfUndoneRepeatInstances");
//            if (oldUndoneRepeatInstanceList.size() > 0 && ((WorkSlot) oldUndoneRepeatInstanceList.get(0)).getEndTime() > now)
//                return false; //first workSlot has not expired yet, so nothing to do, just return
//            else {
//                //remove all past workslots (this will also ensure the right number of new ones are generated)
//                while (oldUndoneRepeatInstanceList.size() > 0 && ((WorkSlot) oldUndoneRepeatInstanceList.get(0)).getEndTime() <= now) {
//                    oldUndoneRepeatInstanceList.remove(0);
//                }
//            }
//        }
//
//        setListOfUndoneRepeatInstances(oldUndoneRepeatInstanceList); //update so correct size is used when calculating additional repeat dates
////<editor-fold defaultstate="collapsed" desc="comment">
//        //do nothing if *next* date is still in the future (ie quit the update when call is irrelevant)
////        if (false && getNextcomingDateD().getTime() > now)
////            return false;
////remove expired workslots from list of future generated workslots: //NO, don't thiink this is necessary!
////        List<RepeatRuleObjectInterface> workSlotInstanceItemList = getListOfUndoneRepeatInstances();
////        WorkSlotList workSlotInstanceItemList = new WorkgetListOfUndoneRepeatInstances();
////        setLatestDateCompletedOrCancelledIfGreaterThanLast(workSlot.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
////        ASSERT.that(workSlotInstanceItemList.size() == 0 || workSlotInstanceItemList.contains(workSlot), "Error: \"" + workSlot + "\" not in list of already generated repeat instances");
//////        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
////        workSlotInstanceItemList.remove(workSlot);
////        while (oldUndoneRepeatInstanceList.size() > 0 && ((WorkSlot) oldUndoneRepeatInstanceList.get(0)).getEndTime() <= now) {
////            oldUndoneRepeatInstanceList.remove(0);
////        }
////        setListOfUndoneRepeatInstances(oldUndoneRepeatInstanceList);
////</editor-fold>
//        //generate all missing (past and future) workslots
//        Date nextRepeatTime;
//        List newRepeatWorkSlots = new ArrayList<>();
//        while ((nextRepeatTime = getNextDueDate()) != null) {
//            setLastGeneratedDate(nextRepeatTime); //update the last generated date
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//
//            WorkSlot repeatInstance = (WorkSlot) expiredWorkSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            oldUndoneRepeatInstanceList.add(repeatInstance);
//            setListOfUndoneRepeatInstances(oldUndoneRepeatInstanceList); //need to update to ensure getNextDueDate will return the right results (it depends on the nb of already generatred instances)
//
//            newRepeatWorkSlots.add(repeatInstance); //gather all new instances and update/save in batch below
//            if (Config.TEST) Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//        }
//
////        setNextcomingDate(getNextDueDate()); //update nextcoming date
////            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
//        //add and save all new workslots in batch
//        ItemAndListCommonInterface owner = expiredWorkSlot.getOwner();
//        for (WorkSlot newWorkSlot : (List<WorkSlot>) newRepeatWorkSlots) {
//            owner.addWorkSlot(newWorkSlot);
//        }
//        DAO.getInstance().saveInBackground((List<ParseObject>) newRepeatWorkSlots);
////<editor-fold defaultstate="collapsed" desc="comment">
////        //for workSlots, time may have passed since so need to update to ensure all workslots are in the future (or overlaps the future)
////        if (workSlotInstanceItemList.size() > 0) {
////            workSlot = (WorkSlot) workSlotInstanceItemList.get(0);
////        } else {
////            workSlot = null;
////        }
////
////        while (workSlot != null && (workSlot.getEndTime() <= System.currentTimeMillis()) && (nextRepeatTime = getNextDueDate()) != null) {
////            //when we get here, the current/first in list workslot is in the past and we have a new repeatTime (so repeatRule is not expired)
////            workSlotInstanceItemList.remove(workSlot); //remove workSlot in the past
////            repeatInstance = (WorkSlot) workSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due/Start date)
////            workSlot.insertIntoListAndSaveListAndInstance(repeatInstance); //insert new created instance in appropriate list and save it
////            workSlotInstanceItemList.add(repeatInstance);
////
////            setLastGeneratedDate(nextRepeatTime);
////            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
////
////            if (workSlotInstanceItemList.size() > 0) {
////                workSlot = (WorkSlot) workSlotInstanceItemList.get(0); //get next workslot
//////                nextRepeatTime = getNextDueDate(); //get next date
////            } else {
////                workSlot = null;
////            }
////        }
////
////        setListOfUndoneRepeatInstances(workSlotInstanceItemList);
////</editor-fold>
//        DAO.getInstance().saveInBackground(this, (ParseObject) owner);
//        return newRepeatWorkSlots.size() > 0; //true if any new workslots where created
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean updateWorkslots(WorkSlot workSlot) {
//        return updateWorkslots(workSlot, false); //false or true, doesn't matter since not used with workSlot!=null
//    }
    /**
     * check if any workslots in the list have repeatRules, and if yes, refresh
     * the repeats
     *
     * @param workslots
     * @return
     */
//    public static boolean updateWorkSlotListXXX(List<WorkSlot> workslots) {
//        boolean updated = false;
////        //Algorithm: in sorted workslots, start from last, move backwards, if expired, check if needs update from repeatRule. If yes, check if updated on server, if not, update and save
////        //check if any workSlots have repeat rules
////        RepeatRuleParseObject repeatRule;
////        for (WorkSlot workSlot : workslots) {
////            if ((repeatRule = workSlot.getRepeatRule()) != null) {
////                if (repeatRule.updateWorkslots(workSlot))
////                    updated = true;
////            }
////        }
//        return updated;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public RepeatRuleObjectInterface updateRepeatInstancesOnDoneCancelOrDeleteOLD_ITEM(Item repeatInstanceOrg) {
//
//        List<Item> repeatInstanceItemList = getListOfUndoneRepeatInstances();
//        setLatestDateCompletedOrCancelledIfGreaterThanLast(repeatInstanceOrg.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
//        ASSERT.that(repeatInstanceItemList.contains(repeatInstanceOrg), "Error: " + repeatInstanceOrg + " not in list of already generated repeat instances");
////        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
//        repeatInstanceItemList.remove(repeatInstanceOrg);
//
//        Date nextRepeatTime;
////        Date nextRepeatTime = getNextDate(item.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE)); //get next date
//        if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) {
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(true)); //get next date
//            nextRepeatTime = getNextCompletedFromDate(repeatInstanceOrg.getRepeatStartTime(true)); //get next date
//        } else {
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(false)); //get next date
//            nextRepeatTime = getNextDueDate(); //get next date
//        }
//        Item repeatInstance = null;
//        if (nextRepeatTime != null) {
////            repeatInstance = repeatInstance.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            repeatInstance = (Item) repeatInstanceOrg.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            repeatInstanceOrg.insertIntoListAndSaveListAndInstance(repeatInstance); //insert new created instance in appropriate list and save it
//            if (getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE) {
//                repeatInstanceItemList.add(repeatInstance); //only keep track of instances when Due
//
//                setLastGeneratedDate(nextRepeatTime);
//            }
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//
//            Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//        } else { // no more repeat instances to handle
////            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
//        }
//        setListOfUndoneRepeatInstances(repeatInstanceItemList);
//        DAO.getInstance().save(this);
//        return repeatInstance;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public RepeatRuleObjectInterface updateRepeatInstancesOnDoneCancelOrDeleteOLDGeneric(RepeatRuleObjectInterface repeatInstanceOrg) {
//
//        List<RepeatRuleObjectInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
//        setLatestDateCompletedOrCancelledIfGreaterThanLast(repeatInstanceOrg.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
//        ASSERT.that(repeatInstanceItemList.contains(repeatInstanceOrg), "Error: " + repeatInstanceOrg + " not in list of already generated repeat instances");
////        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
//        repeatInstanceItemList.remove(repeatInstanceOrg);
//
//        Date nextRepeatTime;
////        Date nextRepeatTime = getNextDate(item.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE)); //get next date
//        if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) {
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(true)); //get next date
//            nextRepeatTime = getNextCompletedFromDate(repeatInstanceOrg.getRepeatStartTime(true)); //get next date
//        } else {
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(false)); //get next date
//            nextRepeatTime = getNextDueDate(); //get next date
//        }
//        RepeatRuleObjectInterface repeatInstance = null;
//        if (nextRepeatTime != null) {
////            repeatInstance = repeatInstance.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            repeatInstance = repeatInstanceOrg.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
//            repeatInstanceOrg.insertIntoListAndSaveListAndInstance(repeatInstance); //insert new created instance in appropriate list and save it
//            repeatInstanceItemList.add(repeatInstance);
//
//            setLastGeneratedDate(nextRepeatTime);
//            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//
//            Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
//        } else { // no more repeat instances to handle
////            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
//        }
//        setListOfUndoneRepeatInstances(repeatInstanceItemList);
//        DAO.getInstance().save(this);
//        return repeatInstance;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * is the generated date within the repeat range
     *
     * @param nextRepeatTime
     * @return
     */
//    private boolean repeatDateBeforeOrEqualEndDate(Date nextRepeatTime) {
//        boolean beforeEndDate = (!(getEndDateD() != null) || nextRepeatTime.getTime() <= getEndDateD().getTime());
//        return beforeEndDate;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateRepeatInstancesWhenRuleIsCreatedXXX(RepeatRuleObjectInterface repeatRuleOriginator) {
////<editor-fold defaultstate="collapsed" desc="comment">
//        /**
//         * //algorithm: ~removeFromCache (reuse existing rule, to avoid
//         * creating/deleting when edited) //get any already created repeat
//         * instances //create dates for new/edited rule //for as many instances
//         * as should be generated (encapsulate this! w moreInstances() and
//         * ruleFinished(), moreInstances based on either #futureInstances vs
//         * length of list or #daysAhead vs lastDayInList) //if an existing
//         * instance exists, use it and set the date (whether new date is same or
//         * not) //if no more instances, generate one //at the end, if there are
//         * still existing instances, delete them (or cancel if actual!=0)
//         *
//         */
////</editor-fold>
////        RepeatRule repeatRule = getRepeatRule();
//        if (getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE) {
//            List<RepeatRuleObjectInterface> oldRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
//            ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
//            RepeatRuleObjectInterface repeatInstance;
//            Date nextRepeatTime;
//            Date startRepeatFromTime;
//            ASSERT.that(getTotalNumberOfInstancesGeneratedSoFar() == 0, "newRepeatInstanceList not empty");
//            int totalNumberInstancesGeneratedSoFar = 0; //getTotalNumberOfInstancesGeneratedSoFar();
////            if (totalNumberInstancesGeneratedSoFar == 0) {
//            newRepeatInstanceList.add(repeatRuleOriginator); //add repeatRuleOriginator to instanceList since instanceList is used to check if an item is still allowed to edit the rule
//            totalNumberInstancesGeneratedSoFar -= 1; //deduct one from count since originator doesn't count
//            startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
////            }
////            ASSERT.that(oldRepeatInstanceList.size() == 0 || oldRepeatInstanceList.contains(repeatRuleOriginator), "repeatRuleOriginator must always be getListOfUndoneRepeatInstances() unless empty, repeatRule=" + this + ", repeatRuleOriginator" + repeatRuleOriginator);
////            totalNumberInstancesGeneratedSoFar -= oldRepeatInstanceList.size(); //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later)
//
//            if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
//                int numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead() - 1; //DatesBuffer should keep track of max number of repeats to generate!!
//                for (int instancesGeneratedCount = 0; instancesGeneratedCount < numberInstancesToGenerate; instancesGeneratedCount++) {
//                    nextRepeatTime = getNextDate(startRepeatFromTime);
//                    if (nextRepeatTime != null
//                            && (!(getEndDateD() != null) || nextRepeatTime.getTime() <= getEndDateD().getTime())) {
//                        repeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldRepeatInstanceList, nextRepeatTime);
////                        if (false && !repeatRuleOriginator.equals(repeatInstance)) {
////                            repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
////                        }
//                        repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
//                        newRepeatInstanceList.add(repeatInstance);
//                    } else {
//                        break;
//                    }
//                }
//            } else { //generate instances for a certain amount of time ahead
//                long now = System.currentTimeMillis(); //MyDate.getNow();
//                int numberOfDaysRepeatsAreGeneratedAhead = getNumberOfDaysRepeatsAreGeneratedAhead();
//                while ((nextRepeatTime = getNextDate(null)) != null
//                        && (nextRepeatTime.getTime() <= now + numberOfDaysRepeatsAreGeneratedAhead * MyDate.DAY_IN_MILLISECONDS)
//                        && repeatDateBeforeOrEqualEndDate(nextRepeatTime)) {
//                    repeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldRepeatInstanceList, nextRepeatTime);
//                    repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
//                    newRepeatInstanceList.add(repeatInstance);
//                }
//            }
//
//            setListOfUndoneRepeatInstances(newRepeatInstanceList);
//            totalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
//            setTotalNumberOfInstancesGeneratedSoFar(totalNumberInstancesGeneratedSoFar);
//            DAO.getInstance().save(this);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * called whenever the MyRepeatRule has changed
     */
//    public void changed() {
////        super.changed();
////        setDirty(true); //repeatrules should never save themselves, this is always done by the owning item since we need to recalculate instances before saving
//        setDirty(ChangeValue.CHANGED_REPEATRULE_ANY_CHANGE); //repeatrules should never save themselves, this is always done by the owning item since we need to recalculate instances before saving
////        repeatRuleChanged = true;
//    }
    /**
     * deleteRuleAndAllRepeatInstancesExceptThis Delete the RepeatRule *and* all
     * repeat instances that are (still) linked to it. Should never be called
     * when only deleting a RepeatRule itself from an existing Item/WorkSlot
     * (since that would delete the item owning the rule)!! So, before calling
     * this, make sure to set the repeatRule for the owning item to null.
     */
//    public void delete() throws ParseException {
//    @Override
//    public void delete() {
//        delete(null);
//    }
//    public void delete(ParseObject initiatorItem) {
//        if (getListOfUndoneRepeatInstances().size() > MyPrefs.maxNumberRepeatInstancesToDeleteWithoutConfirmation.getInt()
//                && !Dialog.show("Warning", "Deleting the " + Item.REPEAT_RULE + " will delete " + getListOfUndoneRepeatInstances().size() + " created instances", "OK", "Cancel")) {
//            return;
//        }
//        while (getListOfUndoneRepeatInstances().size() > 0) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
//            ParseObject item = (ParseObject) getListOfUndoneRepeatInstances().get(0);
//            getListOfUndoneRepeatInstances().remove(0); //remove item first to avoid call back to this list
//            if (item != null && !item.equals(initiatorItem)) {
////                item.delete(); //generated callback that removes the item from the list
//                DAO.getInstance().delete(item);
//            }
////            item.setRepeatRuleNoUpdate(null); //avoid callback to this rule when deleting
//        }
//        DAO.getInstance().delete(this);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * remove the repeatInstance from the repeatRule (to ensure it is not
     * deleted) and then delete the rule and all other repeatInstances
     *
     * @param repeatInstance keep this instance when deleting the rule (by
     * removing it from the
     */
//    public void xdeleteAfterRemovingInstance(RepeatRuleObjectInterface repeatInstance) {
//        repeatInstanceItemList.removeItem(repeatInstance);
//        delete(); //delete the RepeatRule in DAO, call changelisteners(???)
//    }
//</editor-fold>
    /**
     * deletes the repeatRule. If other instances than repeatRuleObject exists,
     * then pops up a dialog to ask if they should also be deleted (DISABLED for
     * now for simplicity). If yes, then deletes all other instances. If there
     * are no repeat instances to keep (done empty) it will delete the
     * repeatRule itself, otherwise it will keep it.
     *
     * @param editedInstance
     * @param itemList
     */
    public void deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(RepeatRuleObjectInterface editedInstance) {
//        return deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(keepThisRepeatInstance, true);
//        return true; //disable this feature for now
        List undone = getListOfUndoneInstances();
        if (false) {
            if (undone.size() > MyPrefs.maxNumberRepeatInstancesToDeleteWithoutConfirmation.getInt()
                    //                && !Dialog.show("Warning", "Deleting the " + Item.REPEAT_RULE + " will delete " + getListOfUndoneInstances().size() + " created instances", "OK", "Cancel")) {
                    && !Dialog.show("Warning", Format.f("Deleting the {0 RepeatRule} will remove {1 number} created instances", Item.REPEAT_RULE, "" + (undone.size() - 1)), "OK", "Cancel")) {
                return;
            }
        }

        List doneInstances = getListOfDoneInstances();
        boolean keepEditedInstance;
        keepEditedInstance = (doneInstances.isEmpty() || doneInstances.contains(editedInstance)); //keep the edited instance iff there are no done instances (so no old instances will be kept) or 
        if (keepEditedInstance) {
            undone.remove(editedInstance);
        }
//        List elementsToDelete = new ArrayList();
        Set ownersToUpdate = new HashSet();
//<editor-fold defaultstate="collapsed" desc="comment">
//        while (undone.size() > 0) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
//            ItemAndListCommonInterface element = (ItemAndListCommonInterface) undone.remove(0);
//            ItemAndListCommonInterface ownerN = element.removeFromOwner(); //also removed the element from the owner's list
//            if (ownerN != null) {
//                ownersToUpdate.add(ownerN);
//            } else {
//                ASSERT.that(false, "owner of a deleted element in RepeatRule is null");
//            }
////            undone.remove(0);
//            if (element != null && !element.equals(editedInstance)) {
//                elementsToDelete.add(element);
//            }
//        }
//</editor-fold>
        for (Object element : undone) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
//            ItemAndListCommonInterface element = (ItemAndListCommonInterface) undone.remove(0);
            ItemAndListCommonInterface ownerN = ((Item) element).removeFromOwner(); //also removed the element from the owner's list
//            if (ownerN != null) {
            ASSERT.that(ownerN != null, "owner of a deleted element in RepeatRule is null");
            ownersToUpdate.add(ownerN);
//            } else {
//            }
//            undone.remove(0);
//            if (element != null && !element.equals(editedInstance)) {
//                elementsToDelete.add(element);
//            }
        }
//        setListOfUndoneInstances(undone); //update list (just in case, shouldn't really be necessary since we're deleting the RR
        setListOfUndoneInstances(null); //update list (just in case, shouldn't really be necessary since we're deleting the RR
//        DAO.getInstance().deleteAll(elementsToDelete, true, false); //trigger will be called from the editing of the workslot/item itself
        DAO.getInstance().deleteLater(undone, true); //trigger will be called from the editing of the workslot/item itself
//        this.delete();
//        DAO.getInstance().saveNew(ownersToUpdate);
        DAO.getInstance().saveToParseLater(ownersToUpdate);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean deleteAskIfDeleteRuleAndAllOtherItemsExceptThisZZZ(RepeatRuleObjectInterface keepThisRepeatInstance, boolean dontAskForConfirmation) {
////        if (getListOfUndoneRepeatInstances().size() > 1) { //if there are more instances (than this one):
//////            if (Settings.getInstance().alwaysDeleteAllInstancesWhenDeletingARepeatRule() || !Dialog.show("Delete Repeat Rule", "Deleting this repeat rule will remove $0 already generated instances", "Only this", "All")) {
////            if (Settings.getInstance().alwaysDeleteAllInstancesWhenDeletingARepeatRule()
////                    || !Dialog.show("Delete Repeating Task",
////                            "This task is part of a repeating series. Delete only this task, or all incomplete tasks?", "Only this", "All")) {
//        if (!dontAskForConfirmation && getListOfUndoneRepeatInstances().size() > MyPrefs.maxNumberRepeatInstancesToDeleteWithoutConfirmation.getInt()
//                && !Dialog.show("Warning", "Deleting the " + Item.REPEAT_RULE + " will delete " + getListOfUndoneRepeatInstances().size() + " created instances", "OK", "Cancel")) {
//            return false;
//        }
//        //"All"
//        //remove the calling object first since it is already being deleted (otherwise infinite loop)
////                delete(); //delete the repeatRule will also delete all instances
////                deleteRuleAndAllRepeatInstancesExceptThis(keepThisRepeatInstance);
//        List list = getListOfUndoneRepeatInstances();
//
//        int numberUndoneInstances = list.size();
////        int numberTotalGeneratedInstances = getTotalNumberOfInstancesGeneratedSoFar();
//
//        list.remove(keepThisRepeatInstance); //remove keepThisRepeatInstance from list so it won't get deleted
//        while (list.size() > 0) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
//            ParseObject repeatRuleObject = (ParseObject) list.remove(0);
////            getListOfUndoneRepeatInstances().remove(0); //remove item first to avoid call back to this list
////            list.remove(0); //remove item first to avoid call back to this list
////            if (repeatRuleObject != null && !repeatRuleObject.equals(keepThisRepeatInstance)) {
//////                item.delete(); //generated callback that removes the item from the list
////                DAO.getInstance().delete(repeatRuleObject);
////            }
//            DAO.getInstance().delete((ItemAndListCommonInterface) repeatRuleObject, false, true);
////            item.setRepeatRuleNoUpdate(null); //avoid callback to this rule when deleting
//        }
////        DAO.getInstance().delete(this); //delete the repeatRule will also delete all instances
////        if (false) //TODO!!! must keep RR since past items may refer to it, but can delete RR if it is deleted *before* any items were made 'past' (Done tasks or expired workslots), eg if the rule was deleted immediately after creation
////        if (numberUndoneInstances == numberTotalGeneratedInstances + 1) { //no Completed instances <=> all are future and have been deleted, so no references to RR which can therefore be deleted
//        if (getTotalNumberOfDoneInstances() == 0) { //no Completed instances <=> all are future and have been deleted, so no references to RR which can therefore be deleted
//            DAO.getInstance().delete((ItemAndListCommonInterface) this, false, true);
//        }
////            } else {
////                //"Only this" //TODO!!!! check deletion of repeatRules/instances
//////                updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(isCommitted(), repeatRuleObject, itemList); //only update instances if the RepeatRule was already committed (otherwise the rule is being deleted before ever having been instantiated)
//////                updateRepeatInstancesOnDone(isCommitted(), keepThisRepeatInstance); //only update instances if the RepeatRule was already committed (otherwise the rule is being deleted before ever having been instantiated)
////            }
////        }
//        return true;
//    }
//
//    public boolean deleteAskIfDeleteRuleAndAllOtherWorkSlotsExceptThisZZZ(WorkSlot workSlot, boolean dontAskForConfirmation) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (getListOfUndoneRepeatInstances().size() > 1) { //if there are more instances (than this one):
//////            if (Settings.getInstance().alwaysDeleteAllInstancesWhenDeletingARepeatRule() || !Dialog.show("Delete Repeat Rule", "Deleting this repeat rule will remove $0 already generated instances", "Only this", "All")) {
////            if (Settings.getInstance().alwaysDeleteAllInstancesWhenDeletingARepeatRule()
////                    || !Dialog.show("Delete Repeating Task",
////                            "This task is part of a repeating series. Delete only this task, or all incomplete tasks?", "Only this", "All")) {
////</editor-fold>
//        if (!dontAskForConfirmation && getListOfUndoneRepeatInstances().size() > MyPrefs.maxNumberRepeatInstancesToDeleteWithoutConfirmation.getInt()
//                && !Dialog.show("Warning", "Deleting the " + Item.REPEAT_RULE + " will delete " + getListOfUndoneRepeatInstances().size() + " created instances", "OK", "Cancel")) {
//            return false;
//        }
//        //"All"
//        List list = getListOfUndoneRepeatInstances();
//
//        int numberUndoneInstances = list.size();
////        int numberTotalGeneratedInstances = getTotalNumberOfInstancesGeneratedSoFar();
//
//        //remove the calling object first since it is already being deleted (otherwise infinite loop)
//        list.remove(workSlot); //remove keepThisRepeatInstance from list so it won't get deleted
//        while (list.size() > 0) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
//            ParseObject repeatRuleObject = (ParseObject) list.remove(0);
//            DAO.getInstance().delete((ItemAndListCommonInterface) repeatRuleObject, true, true);
//        }
////        if (numberUndoneInstances == numberTotalGeneratedInstances + 1) { //no Completed instances <=> all are future and have been deleted, so no references to RR which can therefore be deleted
//        if (getTotalNumberOfDoneInstances() == 0) { //no Completed instances <=> all are future and have been deleted, so no references to RR which can therefore be deleted
//            DAO.getInstance().delete((ItemAndListCommonInterface) this, true, true); //TODO: is this correct?!
//        }
//        return true;
//    }
//
////    public boolean softDelete(boolean removeReferences) {
////        //TODO!!! do we really want to delete references from instantiated (future, done or undone) tasks to the rule that generated them?
////        //when will we actually delete a repeatRule? Not when removing from a task.
////        //maybe only if there are no tasks generated 8or the single tasks are softDeleted)?
////        //Since all tasks generated from a rule will normally be in same list (except of course if we've moved some of them)
////        //then softDeleting the list could lead to softdeleting the rule. But then all refernces to the rule will become
////        //unavailable anyway...????
////        setDeletedDate(new MyDate());
////        DAO.getInstance().saveInBackground(this);
////        return true;
////    }
//    public void deleteThisRepeatInstanceFromRepeatRuleListOfInstancesXXX(RepeatRuleObjectInterface repeatInstanceBeingDeleted) {
//        List list = getListOfUndoneRepeatInstances();
//        ASSERT.that(list.contains(repeatInstanceBeingDeleted), "error: repeatInstanceBeingDeleted NOT in repeatRule's getListOfUndoneRepeatInstances, instance=" + repeatInstanceBeingDeleted + ", rule=" + this);
//        list.remove(repeatInstanceBeingDeleted);
//        setListOfUndoneInstances(list);
//    }
//</editor-fold>
    private String toMonthlyRepeatString() {
        String s = "";
        if (getDayInMonth() != 0) {
            s += ", on the " + MyDate.addNthPostFix("" + getDayInMonth());
        } else if (getWeeksInMonth() != 0) {
//                        s += ", week" +(weeksInMonthBitsToVector(getWeeksInMonth()).size() > 1?"s ":" ")+ getWeeksInMonthAsString(getWeeksInMonth());
            s += ", " + getWeeksInMonthAsString(getWeeksInMonth()) + " week";
            s += ", on " + getDaysInWeekAsString(getDaysInWeek(), "+");
        } else {
            s += ", " + getWeeksInMonthAsString(getWeekdaysInMonth());
            s += " " + getDaysInWeekAsString(getDaysInWeek(), "+");
        }
        return s;
    }

    /**
     * return the text describing the repeatRule
     *
     * @return
     */
    public String getText() {
        String s = "";
        String sep = "";
        if (getRepeatType() == REPEAT_TYPE_NO_REPEAT) {
            s = ""; //REPEAT_RULE_NO_REPEAT;
        } else {
            if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
//                s += " (when completed)";
//                s += " When completed";
                s += "On completion";
                sep = ", ";
            } else if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) { //            s+=" (from "+Item.getFieldName(Item.FIELD_DUE_DATE)+")";
//            s += " (from " + new MyDate(getReferenceItemForDueDateAndFutureCopies().getDueDate()).formatDate(false) + ")";
//            s += " (from " + new MyDate(getReferenceObjectForRepeatTime().getRepeatStartTime()).formatDate(false) + ")";
//            s += " (from " + new MyDate(getSpecifiedStartDate()).formatDate(false) + ")";
//                s += " (from " + MyDate.formatDateNew(getSpecifiedStartDate()) + ")";
//                s = "Starting " + MyDate.formatDateNew(getSpecifiedStartDate()) + " " + s;
//                s += "Starting " + MyDate.formatDateNew(getSpecifiedStartDate());
//hide the (internal now) starting date
//                s += "Starting " + (getSpecifiedStartDate().getTime() != 0 ? MyDate.formatDateSmart(getSpecifiedStartDate(), true, false) : "<not set>");
            }

            if (getRepeatType() != RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE || isDatedCompletion()) {
                int freq = getFrequency();
//        String s = getFreqText(freq);
                if (getInterval() > 1) {
//                    s += ", every " + MyDate.addNthPostFix("" + getInterval()) + " " + getFreqText(freq, false, false, false);
                    s += sep + "Every " + MyDate.addNthPostFix("" + getInterval()) + " " + getFreqText(freq, false, false, false);
                } else {
//                    s += ", " + getFreqText(freq, true, true, false);
                    s += sep + getFreqText(freq, true, true, false);
                }
                sep = "";
                switch (freq) {
                    case RepeatRule.YEARLY:
//                ASSERT.that(false); //TODO!!!! show yearly repeat as string
                        if (getDayInYear() != 0) {
//                    s += ", on the " + MyDate.addNthPostFix("" + getDayInYear()) + " day";
//                        s += ", on the " + MyDate.addNthPostFix("" + getDayInYear()) + " day";
                            s += Format.f(", on the {0} day", MyDate.addNthPostFix("" + getDayInYear()));
                        } else {
                            s += ", " + getMonthsInYearAsString(getMonthsInYear());
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (getWeeksInMonth() != 0) {
//                        s += ", weeks " + getWeeksInMonthAsString(getWeeksInMonth());
//                        s += ", on " + getDaysInWeekAsString(getDaysInWeek(), "+");
//                    } else {
//                        s += ", " + getWeeksInMonthAsString(getWeekdaysInMonth());
//                        s += " " + getDaysInWeekAsString(getDaysInWeek(), "+");
//                    }
//                    s += " " + toMonthlyRepeatString();
//</editor-fold>
                            s += toMonthlyRepeatString();
                        } //                break; //Idea: fall-though to simply add details about months, weeks in following case statements??!!
                        break;
                    case RepeatRule.MONTHLY:
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (getDayInMonth() != 0) {
//                    s += ", on the " + MyDate.addNthPostFix("" + getDayInMonth());
//                } else if (getWeeksInMonth() != 0) {
////                    if (weeksInMonthBitsToVector(getWeeksInMonth()).size() > 1) {
////                        s += ", weeks " + getWeeksInMonthAsString(getWeeksInMonth());
////                    } else {
////                        s += ", week " + getWeeksInMonthAsString(getWeeksInMonth());
////                    }
//                    s += ", " + getWeeksInMonthAsString(getWeeksInMonth()) + " week";
//                    s += ", on " + getDaysInWeekAsString(getDaysInWeek(), "+");
//                } else {
//                    s += ", " + getWeeksInMonthAsString(getWeekdaysInMonth());
//                    s += " " + getDaysInWeekAsString(getDaysInWeek(), "+");
//                }
//</editor-fold>
                        s += toMonthlyRepeatString();
                        break;
                    case RepeatRule.WEEKLY:
                        s += " on " + getDaysInWeekAsString(getDaysInWeek(), "+");
                        break;
                    case RepeatRule.DAILY:
                        break;
                }

//            if (useCount()) {
                if (getNumberOfRepeats() != Integer.MAX_VALUE) {
                    if (getNumberOfRepeats() == 1) {
                        s += ". Repeat once";
                    } else {
                        s += Format.f(". Repeat {0} times", "" + getNumberOfRepeats());
                    }
//        } else if (getEndDate() != 0) {
//        } else if (getEndDate() != Long.MAX_VALUE) {
                } else if (getEndDate() != MyDate.MAX_DATE) {
//            s += " until " + new MyDate(getEndDate()).formatDate(false);
                    s += ". Repeat until " + MyDate.formatDateNew(getEndDate());
                } else {
                    s += ". Repeat forever";
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//        else {
//            s += " (from " + new MyDate(getStartDate()).formatDate(false) + ")";
//        }
//            if (getNumberFutureRepeatsToGenerateAhead() != 0) {//|| getNumberOfDaysRepeatsAreGeneratedAhead() == 0) {
////                s += " show next " + getNumberFutureRepeatsToGenerateAhead() + " instances";
//                s += ", create next " + getNumberFutureRepeatsToGenerateAhead()
//                        + " repeat" + (getNumberFutureRepeatsToGenerateAhead() > 1 ? "s" : "");
//            } else if (getNumberOfDaysRepeatsAreGeneratedAhead() != 0) {
////                s += " show instances for next " + getNumberOfDaysRepeatsAreGeneratedAhead() + " days";
//                s += ", create repeats for next " + getNumberOfDaysRepeatsAreGeneratedAhead() + " day"
//                        + (getNumberOfDaysRepeatsAreGeneratedAhead() > 1 ? "s" : "");
//            if (getNumberSimultaneousRepeats() != 0) {//|| getNumberOfDaysRepeatsAreGeneratedAhead() == 0) {
//</editor-fold>
                if (getNumberOfDaysRepeatsAreGeneratedAhead() == 0) {//|| getNumberOfDaysRepeatsAreGeneratedAhead() == 0) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                s += " show next " + getNumberFutureRepeatsToGenerateAhead() + " instances";
//                if (getNumberFutureRepeatsToGenerateAhead() > 1)
//                s += ", " + (getNumberSimultaneousRepeats() + 1) + " repeats";
//</editor-fold>
//                s += ". Create next " + (getNumberSimultaneousRepeats()) + " repeat" + (getNumberSimultaneousRepeats() > 1 ? "s" : "");
                    s += getNumberSimultaneousRepeats() > 1
                            ? Format.f(". Create {0} future repeats", "" + getNumberSimultaneousRepeats())
                            //                        : ". Create 1 repeat";
                            : "";
                } else {//if (getNumberOfDaysRepeatsAreGeneratedAhead() != 0) {
//                s += " show instances for next " + getNumberOfDaysRepeatsAreGeneratedAhead() + " days";
//                s += ". Create repeats for next " + getNumberOfDaysRepeatsAreGeneratedAhead()
//                        + " day" + (getNumberOfDaysRepeatsAreGeneratedAhead() > 1 ? "s" : "");
                    s += (getNumberOfDaysRepeatsAreGeneratedAhead() > 1
                            ? Format.f(". Create repeats for next {0} days", "" + getNumberOfDaysRepeatsAreGeneratedAhead())
                            : ". Create repeats for next day");
                } //else {assert false;           }
            }
        }
        return s;
    }

    @Override
    public String toString() {
//        return getText();
        return toStringWObjId();
    }

    public String toStringWObjId() {
        return "RepRu=[" + ((getObjectIdP() == null ? "no ObjId" : getObjectIdP())) + "/" + getGuid() + "]\"" + getText()
                + "\"; DONE(" + getListOfDoneInstances().size() + ")= [" + getListOfDoneInstances() + "]"
                + "; UNDONE(" + getListOfUndoneInstances().size() + ")= [" + getListOfUndoneInstances() + "]";
    }

    public void copyMeInto(RepeatRuleParseObject destiny) {
        copyMeInto(destiny, false);
    }

    public void copyMeInto(RepeatRuleParseObject destiny, boolean copyState) {
        copyMeInto(destiny, copyState, false);
    }

    public void copyMeInto(RepeatRuleParseObject destiny, boolean copyState, boolean makeTemplateCopyXXX) {

        RepeatRuleParseObject dest = (RepeatRuleParseObject) destiny;
//        super.copyMeInto(destiny);

//        rr.setGuid(getGuid()); //-how to avoid that a
//        dest.frequency = frequency;
//        dest.setSpecifiedStartDate(getSpecifiedStartDate());
        dest.setRepeatType(getRepeatType());
        dest.setFrequency(getFrequency());
        dest.setInterval(getInterval());

        dest.setEndDate(getEndDate());
        dest.setNumberOfRepeats(getNumberOfRepeats()); //==COUNT

        dest.setDaysInWeek(getDaysInWeek());
        dest.setWeeksInMonth(getWeeksInMonth());
        dest.setWeekdaysInMonth(getWeekdaysInMonth());

        dest.setMonthsInYear(getMonthsInYear());
        dest.setDayInMonth(getDayInMonth());
        dest.setDayInYear(getDayInYear());

        dest.setNumberSimultaneousRepeats(getNumberSimultaneousRepeats());
        dest.setNumberOfDaysRepeatsAreGeneratedAhead(getNumberOfDaysRepeatsAreGeneratedAhead());
        dest.setDatedCompletion(isDatedCompletion());

        if (false) {
            dest.setTotalNumberOfDoneInstancesZZZ(getTotalNumberOfDoneInstances());
        }
//        dest.setTotalNumberOfInstancesGeneratedSoFarXXX(getTotalNumberOfInstancesGeneratedSoFar());

        if (false && !makeTemplateCopyXXX) {
            dest.setListOfDoneInstances(getListOfDoneInstances()); //copy same instances over (NOT make a copy of them)
            dest.setListOfUndoneInstances(getListOfUndoneInstances()); //copy same instances over (NOT make a copy of them)
        }

//        if (false) {
//            dest.setLatestDateCompletedOrCancelled(getLatestDateDone());
//        }
//        dest.setLastestDateCompletedOrCancelled(getLastGeneratedDateD());
        if (copyState) {
            //copyState means including evrything, the complete state of the object
//            dest.setLastGeneratedDate(getLastGeneratedDateD());

//            List oldList = getListOfUndoneInstances();
//            List newList = new ArrayList();
//            for (int i = 0, size = oldList.size(); i < size; i++) {
//                newList.add(oldList.get(i)); //only copy *references* to item (not make copies of the items)
//            }
//            dest.setListOfUndoneInstances(newList);
            //make a copy of the state of the rule (previously generated instances etc) to enable compare when setting??
            dest.setListOfDoneInstances(new ArrayList(getListOfDoneInstances()));
            dest.setListOfUndoneInstances(new ArrayList(getListOfUndoneInstances()));
            if (false) {
                dest.setLatestDateCompletedOrCancelled(getLatestDateDone(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
//            dest.setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar());
                dest.setTotalNumberOfDoneInstancesZZZ(getTotalNumberOfDoneInstances());
            }
        }
    }

    /**
     * @return @inherit
     */
    public RepeatRuleParseObject cloneMe() {
        RepeatRuleParseObject newCopy = new RepeatRuleParseObject();
        copyMeInto(newCopy);
        return newCopy;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(RepeatRuleObject keepThisRepeatInstance) {
//        deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(keepThisRepeatInstance, keepThisRepeatInstance.getListForNewCreatedRepeatInstances());
//    }
    /**
     * deletes (by calling .deleteRuleAndAllRepeatInstancesExceptThis()) all
     * RepeatInstanceObjects except keepThisRepeatInstance Used when deleting a
     * RepeatRule
     *
     * @param keepThisRepeatInstance
     */
//    private void deleteRuleAndAllRepeatInstancesExceptThis(RepeatRuleObjectInterface keepThisRepeatInstance) {
////        repeatInstanceItemList.deleteRepeatInstancesExceptThis(keepThisRepeatInstance);
//        repeatInstanceItemList.remove(keepThisRepeatInstance); //remove before deleting all the elements in the list
////        repeatInstanceItemList.deleteAllRepeatInstances();
////        repeatInstanceItemList.deleteItemListAndAllItsItems();
//        delete();
////        delete();
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * generate next batch of repeat dates. Returns empty List if the repeat
     * rule has ended (no more dates).
     */
//        private static Vector updateNew(RepeatRule repeatRule, long specifiedStartDate, long endDate, long lastRepeatDate, int totalMaxCountToGenerate, int countAlreadyGenerated, int numberOfRepeatsToGenerate) {
//    private Vector generateMoreDates() {//RepeatRule repeatRule, long specifiedStartDate, long endDate, long lastRepeatDate, int totalMaxCountToGenerate, int countAlreadyGenerated, int numberOfRepeatsToGenerate) {
//        //if numberOfRepeatsToGenerate is less than the total remaining to generate, then generate only numberOfRepeatsToGenerate instances,
//        //otherwise generate totalMaxCountToGenerate-countAlreadyGenerated which is the total of still missing instances to generate
//        //We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
//        int numberOfInstancesToGenerate = Math.min(Math.min(getNumberFutureRepeatsToGenerateAhead(), getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar()), COUNTS_AHEAD);
//        long subsetBeginningGen = Math.max(getSpecifiedStartDate(), getLastGeneratedDate() + 1); //lastRepeatDate+1 to ensure that we don't regenerate the last already generated date, but the one after that
////                Vector newDates = repeatRule.datesAsVector(specifiedStartDate, subsetBeginningGen, endDate);
//        RepeatRule repeatRule = getRepeatRule();
////        repeatRule.setInt(RepeatRule.COUNT, numberOfInstancesToGenerate);
//        repeatRule.setInt(RepeatRule.COUNT, COUNTS_AHEAD);
//        Vector<Long> moreDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, getEndDate());
//        if (moreDates.size() > 0) {
//            setLastGeneratedDate(moreDates.get(moreDates.size()));
//        }
//        setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + moreDates.size()); //update how many dates have been generated in total so far
//        return moreDates;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns the next date in the repeat sequence (leaving it in the list to
     * make it possible to test the value before removing/consuming it).
     * Automatically generates additional dates if needed. Returns null if all
     * dates are used (meaning the repeatRule has expired).
     *
     * @return
     */
//    private Long getNextDate(List<Long> datesList) {
//        Long date = null;
////        List<Long> datesList = getDatesList();
//        if (datesList.size() == 0) {
//            datesList.addAll(generateMoreDates()); //calculate and add more repeat dates
//        }
//        if (datesList.size() > 0) {
////            date = datesList.remove(0);
//            date = datesList.get(0);
//        } else {
////            this.delete(); //no more repeat instances to handle, delete the repeat rule
//        }
////        setDatesList(datesList);
//        return date;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * externalize (and internalize) are used to store an edited RepeatRule
     * locally (for Replay). Only end-user edited fields need to be saved, since
     * either the user edited an existing rule in which case the old rule will
     * not be updated until saving, or he edited a new rule, in which case there
     * are no calculated fields (repeat instances)
     *
     * @param dos
     * @throws IOException
     */
//    @Override
    //IS a specific externalize really needed for a RepeatRule (it causes problems for Item references with infinite loop back to RR)??
//    public void externalizeXXX(DataOutputStream dos) throws IOException {
////        super.writeObject(dos);
////        if (DAO.getInstance().cache.LOCK) {
////            super.externalize(dos);
////            return;
////        }
//        if (getObjectIdP() != null) {
//            dos.writeUTF(getObjectIdP());
//        } else {
//            dos.writeUTF("");
//        }
//
//        dos.writeInt(getFrequency());
//        dos.writeInt(getInterval());
//        dos.writeInt(getNumberOfRepeats());
//
//        dos.writeLong(getEndDate());
//        dos.writeInt(getDaysInWeek());
//        dos.writeInt(getWeeksInMonth());
//
//        dos.writeInt(getWeekdaysInMonth());
//        dos.writeInt(getMonthsInYear());
//        dos.writeInt(getDayInMonth());
//
//        dos.writeInt(getDayInYear());
//        dos.writeInt(getRepeatType());
//
//        dos.writeInt(getNumberSimultaneousRepeats());
//        dos.writeInt(getNumberOfDaysRepeatsAreGeneratedAhead());
//        dos.writeLong(getSpecifiedStartDate());
//
//        dos.writeLong(getLatestDateCompletedOrCancelled().getTime()); //=0; //
////        dos.writeLong(getLastGeneratedDateD().getTime()); //=0; //
////        dos.writeInt(getTotalNumberOfInstancesGeneratedSoFar());
//
//        dos.writeInt(getTotalNumberOfDoneInstances());
//
////        doNotExternalizeObjectDataForReferencesParseObjects = true; //for all further writes
////        Util.writeObject(getListOfUndoneRepeatInstances(), dos);
//        //store list of undone instances as: size; type of elements [Item/WorkSlot]; list of objectIds:
////        List<ItemAndListCommonInterface> instances = getListOfUndoneRepeatInstances();
//        List<RepeatRuleObjectInterface> repeatInstances = getListOfUndoneRepeatInstances();
//        int instanceSize = repeatInstances.size();
////        dos.writeInt(instances.size());
//        dos.writeInt(instanceSize);
//        if (repeatInstances.size() > 0) { //store the type of objects
//            if (repeatInstances.get(0) instanceof WorkSlot) {
//                dos.writeUTF(WorkSlot.CLASS_NAME);
//            } else {
//                dos.writeUTF(Item.CLASS_NAME);
//            }
////        for (ItemAndListCommonInterface p : getListOfUndoneRepeatInstances()) {
////            for (ItemAndListCommonInterface p : getListOfUndoneRepeatInstances()) {
//            for (int i = 0; i < instanceSize; i++) {
//                ItemAndListCommonInterface p = (ItemAndListCommonInterface) repeatInstances.get(i);
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (p instanceof Item) {
////                    dos.writeUTF(((Item) p).getObjectIdP());
////                } else {
////                    dos.writeUTF(((WorkSlot) p).getObjectIdP());
////                }
////</editor-fold>
//                if (p == null) {
//                    if (Config.DEBUG_LOGGING) {
//                        ASSERT.that(true, "RepeatRule: " + this.toStringWObjId() + " references null elt!! "); //TODO!! remove such elements if detected (both from local cache and Parse Server)
//                    }
//                } else {
//                    if (Config.DEBUG_LOGGING && p.getObjectIdP() == null) {
//                        ASSERT.that(p.getObjectIdP() != null, "RepeatRule= \"" + this.toStringWObjId() + "\" references with getObjectIdP()==null, elt=\"" + p + "\"");
//                    }
//                    dos.writeUTF(p.getObjectIdP());
//                }
//            }
//        }
//
//        List<RepeatRuleObjectInterface> doneInstances = getListOfDoneInstances();
//        int doneSize = doneInstances.size();
////        dos.writeInt(instances.size());
//        dos.writeInt(doneSize);
//        if (doneInstances.size() > 0) { //store the type of objects
//            if (doneInstances.get(0) instanceof WorkSlot) {
//                dos.writeUTF(WorkSlot.CLASS_NAME);
//            } else {
//                dos.writeUTF(Item.CLASS_NAME);
//            }
////        for (ItemAndListCommonInterface p : getListOfUndoneRepeatInstances()) {
////            for (ItemAndListCommonInterface p : getListOfUndoneRepeatInstances()) {
//            for (int i = 0; i < doneSize; i++) {
//                ItemAndListCommonInterface p = (ItemAndListCommonInterface) doneInstances.get(i);
//                if (p == null) {
//                    if (Config.DEBUG_LOGGING) {
//                        ASSERT.that(true, "RepeatRule: " + this.toStringWObjId() + " references null elt!! "); //TODO!! remove such elements if detected (both from local cache and Parse Server)
//                    }
//                } else {
//                    if (Config.DEBUG_LOGGING && p.getObjectIdP() == null) {
//                        ASSERT.that(p.getObjectIdP() != null, "RepeatRule= \"" + this.toStringWObjId() + "\" references with getObjectIdP()==null, elt=\"" + p + "\"");
//                    }
//                    dos.writeUTF(p.getObjectIdP());
//                }
//            }
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        int vectorSize = repeatInstanceVector.size();
////        dos.writeInt(vectorSize);
////        for (int i = 0; i < vectorSize; i++) {
////            dos.writeInt(((BaseItem) repeatInstanceVector.elementAt(i)).getGuid());
////        }
////        repeatInstanceVector.writeObject(dos, true);
////        BaseItemDAO.getInstance().writeObject(repeatInstanceVector, dos, true);
////        BaseItemDAO.getInstance().writeObject(repeatInstanceItemList, dos);
////</editor-fold>
////        Util.writeObject(getListOfUndoneRepeatInstances(), dos);
////
////        datesList.writeObject(dos);
////        Util.writeObject(getDatesList(), dos); //TODO!! cannot externalize since Date is not handled by CN1, must rewrite to loop writing long as below
////        if (false) {
////            List<Date> list = getDatesListXXX();
//////        int vectorSize = datesList.vector.size();
////            dos.writeInt(list.size());
////            for (int i = 0, size = list.size(); i < size; i++) {
//////                dos.writeLong(((Date) datesList.vector.elementAt(i)).getTime());
////                dos.writeLong(list.get(i).getTime());
////            }
////        }
////</editor-fold>
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
////    @Override
//    public void internalizeXXX(int version, DataInputStream dis) throws IOException {
////<editor-fold defaultstate="collapsed" desc="comment">
////        super.readObject(version, dis);
////        frequency = dis.readInt();
////        interval = dis.readInt();
////        count = dis.readInt();
////
////        endDate = dis.readLong();
////        daysInWeek = dis.readInt();
////        weeksInMonth = dis.readInt();
////
////        weekdaysInMonth = dis.readInt();
////        monthsInYear = dis.readInt();
////        dayInMonth = dis.readInt();
////
////        dayInYear = dis.readInt();
////        repeatType = dis.readInt();
////
////        numberOfRepeatsGeneratedAhead = dis.readInt();
////        numberOfDaysRepeatsAreGeneratedAhead = dis.readInt();
////        specifiedStartDate = dis.readLong();
////
////        lastDateGeneratedFor = dis.readLong(); //=0; //
////        lastGeneratedDate = dis.readLong(); //=0; //
////        countOfInstancesGeneratedSoFar = dis.readInt();
////</editor-fold>
//        String objId = dis.readUTF();
//        if (!objId.isEmpty()) {
//            setObjectId(objId);
//        }
//
//        setFrequency(dis.readInt());
//        setInterval(dis.readInt());
//        setNumberOfRepeats(dis.readInt());
//
//        setEndDate(dis.readLong());
//        setDaysInWeek(dis.readInt());
//        setWeeksInMonth(dis.readInt());
//
//        setWeekdaysInMonth(dis.readInt());
//        setMonthsInYear(dis.readInt());
//        setDayInMonth(dis.readInt());
//
//        setDayInYear(dis.readInt());
//        setRepeatType(dis.readInt());
//
//        setNumberSimultaneousRepeats(dis.readInt());
//        setNumberOfDaysRepeatsAreGeneratedAhead(dis.readInt());
//        setSpecifiedStartDate(dis.readLong());
//
//        setLatestDateCompletedOrCancelled(new Date(dis.readLong())); //=0; //
////        setLastGeneratedDate(new Date(dis.readLong())); //=0; //
////        setTotalNumberOfInstancesGeneratedSoFar(dis.readInt());
//
//        setTotalNumberOfDoneInstances(dis.readInt());
//
////        setListOfUndoneRepeatInstances((List) Util.readObject(dis));
//        int instancesSize = dis.readInt();
//        if (instancesSize > 0) {
//            String instanceType = dis.readUTF();
//            boolean isListOfTypeItems = instanceType.equals(Item.CLASS_NAME);
//
//            String objectId;
//            Item item = null;
//            WorkSlot workSlot = null;
//            List instanceList = new ArrayList();
//            for (int i = 0; i < instancesSize; i++) {
//                objectId = dis.readUTF();
//                if (isListOfTypeItems) { //use !equals since faster than equals
//                    if (true) {
//                        item = DAO.getInstance().fetchItem(objectId); //NOOO: not needed since will be done in getListOfUndoneRepeatInstances
//                    }
//                    instanceList.add(item);
//                } else {
//                    if (true) {
//                        workSlot = DAO.getInstance().fetchWorkSlot(objectId);
//                    }
//                    instanceList.add(workSlot);
//                }
//            }
//            setListOfUndoneRepeatInstances(instanceList);
//        }
//
//        int doneSize = dis.readInt();
//        if (doneSize > 0) {
//            String instanceType = dis.readUTF();
//            boolean isListOfTypeItems = instanceType.equals(Item.CLASS_NAME);
//
//            String objectId;
//            Item item = null;
//            WorkSlot workSlot = null;
//            List instanceList = new ArrayList();
//            for (int i = 0; i < doneSize; i++) {
//                objectId = dis.readUTF();
//                if (isListOfTypeItems) { //use !equals since faster than equals
//                    if (true) {
//                        item = DAO.getInstance().fetchItem(objectId); //NOOO: not needed since will be done in getListOfUndoneRepeatInstances
//                    }
//                    instanceList.add(item);
//                } else {
//                    if (true) {
//                        workSlot = DAO.getInstance().fetchWorkSlot(objectId);
//                    }
//                    instanceList.add(workSlot);
//                }
//            }
//            setListOfDoneInstances(instanceList);
//        }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////<editor-fold defaultstate="collapsed" desc="comment">
////        int vectorSize = dis.readInt();
////        repeatInstanceVector = new RepeatInstanceVector();
////        for (int i = 0; i < vectorSize; i++) {
////            repeatInstanceVector.addElement(BaseItemDAO.getInstance().getBaseItem(dis.readInt()));
////        }
////        repeatInstanceVector = new RepeatInstanceVector();
////        repeatInstanceVector.readBaseItem(version, dis);
////        repeatInstanceVector = (RepeatInstanceVector) BaseItemDAO.getInstance().readBaseItem(dis, this);
////        repeatInstanceVector = (RepeatInstanceVector) BaseItemDAO.getInstance().readBaseItem(this, dis);
////        repeatInstanceItemList = (RepeatInstanceItemList) BaseItemDAO.getInstance().readObject(this, dis);
////        repeatInstanceItemList = (RepeatInstanceItemList) Util.readObject(dis);
////        repeatInstanceItemList = (List) Util.readObject(dis);
////</editor-fold>
////        setListOfUndoneRepeatInstances((List) Util.readObject(dis));
////<editor-fold defaultstate="collapsed" desc="comment">
////        datesList = new DateBuffer();
////        datesList.readObject(999, dis);
////        datesList = (DateBuffer) Util.readObject(dis);
////        datesList = (DateBuffer) Util.readObject(dis);
////</editor-fold>
////        setDatesList((List) Util.readObject(dis));
////        int vectorSize = dis.readInt();
//////            datesList.vector = new Vector(vectorSize);
////        List<Date> datesList = new ArrayList(vectorSize);
////        for (int i = 0; i < vectorSize; i++) {
//////                datesList.vector.addElement(new Date(dis.readLong()));
////            datesList.add(new Date(dis.readLong()));
////        }
////        setDatesListXXX(datesList);
////</editor-fold>
//    }
//</editor-fold>
    /**
     * https://www.sitepoint.com/how-to-implement-javas-hashcode-correctly/
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
//        hash = 23 * hash + (int) (this.getSpecifiedStartDate().getTime() ^ (this.getSpecifiedStartDate().getTime() >>> 32));
        hash = 23 * hash + this.getRepeatType();
        hash = 23 * hash + this.getFrequency();
        hash = 23 * hash + this.getInterval();

        hash = 23 * hash + (int) (this.getEndDate() ^ (this.getEndDate() >>> 32));
        hash = 23 * hash + this.getNumberOfRepeats();

        hash = 23 * hash + this.getDaysInWeek();
        hash = 23 * hash + this.getWeeksInMonth();
        hash = 23 * hash + this.getWeekdaysInMonth();

        hash = 23 * hash + this.getMonthsInYear();
        hash = 23 * hash + this.getDayInMonth();
        hash = 23 * hash + this.getDayInYear();

        hash = 23 * hash + this.getNumberSimultaneousRepeats();
        hash = 23 * hash + this.getNumberOfDaysRepeatsAreGeneratedAhead();
        return hash;
    }

    /**
     * we need a real equals implementation since we use equality to test
     * whether a copy of a repeatRule has been edited by the user. Tests if the
     * repeat parameters are the same, e.g. but does NOT check if parseObjectId
     * is different.
     * https://www.sitepoint.com/how-to-implement-javas-hashcode-correctly/
     *
     * @param repeatRule
     * @return
     */
    @Override
    public boolean equals(Object o) {
        // ASSERT.that("equals() called on MyRepeatRule"); //DONE!!!: not updated (is it needed?) //called when setting a new/update
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }

        RepeatRuleParseObject repeatRule = (RepeatRuleParseObject) o;

//        if (getSpecifiedStartDate().getTime() != repeatRule.getSpecifiedStartDate().getTime()) {
//            return false;
//        }
        if (Objects.equals(getGuid(), repeatRule.getGuid())) {
            if (Config.TEST) {
                Log.p("comparing two different RepeatRuleParseObjects with same guid, this=" + this + "; other=" + o);
            }
            return true;
        }
        if (getRepeatType() != repeatRule.getRepeatType()) {
            return false;
        }
        if (getFrequency() != repeatRule.getFrequency()) {
            return false;
        }
        if (getInterval() != repeatRule.getInterval()) {
            return false;
        }

        if (getEndDate() != repeatRule.getEndDate()) {
            return false;
        }
        if (getNumberOfRepeats() != repeatRule.getNumberOfRepeats()) {
            return false;
        }

        if (getDaysInWeek() != repeatRule.getDaysInWeek()) {
            return false;
        }
        if (getWeeksInMonth() != repeatRule.getWeeksInMonth()) {
            return false;
        }
        if (getWeekdaysInMonth() != repeatRule.getWeekdaysInMonth()) {
            return false;
        }

        if (getMonthsInYear() != repeatRule.getMonthsInYear()) {
            return false;
        }
        if (getDayInMonth() != repeatRule.getDayInMonth()) {
            return false;
        }
        if (getDayInYear() != repeatRule.getDayInYear()) {
            return false;
        }

        if (getNumberSimultaneousRepeats() != repeatRule.getNumberSimultaneousRepeats()) {
            return false;
        }
        if (getNumberOfDaysRepeatsAreGeneratedAhead() != repeatRule.getNumberOfDaysRepeatsAreGeneratedAhead()) {
            return false;
        }
        if (false) {//TODO: disabled, causes some weird loops, can't figure out what
            if (getListOfDoneInstances().size() != repeatRule.getListOfDoneInstances().size()) {
                return false;
            }
            if (getListOfUndoneInstances().size() != repeatRule.getListOfUndoneInstances().size()) {
                return false;
            }
            if (!getListOfDoneInstances().equals(repeatRule.getListOfDoneInstances())) {
                return false;
            }
            if (!getListOfUndoneInstances().equals(repeatRule.getListOfUndoneInstances())) {
                return false;
            }
        }

        if (isDatedCompletion() != repeatRule.isDatedCompletion()) {
            return false;
        }
//        if (repeatRuleOriginator != repeatRule.repeatRuleOriginator) {
//            return false;
//        }
        return true;
    }

    /**
     * update the repeat rule with any differences (edits) in the RR given in
     * argument
     *
     * @param editedRepeatRule
     * @return true if any changes were made (meaning the updated rule needs to
     * be saved)
     */
    public boolean updateToValuesInEditedRepeatRule(RepeatRuleParseObject editedRepeatRule) {
        // ASSERT.that("equals() called on MyRepeatRule"); //DONE!!!: not updated (is it needed?) //called when setting a new/update
        if (editedRepeatRule == this) {
            ASSERT.that(false, "updateToValuesInEditedRepeatRule called with the same repeatRule=" + this);
            return false;
        }
        boolean updated = false;

//        RepeatRuleParseObject editedRepeatRule = (RepeatRuleParseObject) editedRepeatRule;
//        if (getSpecifiedStartDate().getTime() != editedRepeatRule.getSpecifiedStartDate().getTime()) {
//            setSpecifiedStartDate(editedRepeatRule.getSpecifiedStartDate());
//            updated = true;
//        }
        if (getRepeatType() != editedRepeatRule.getRepeatType()) {
            setRepeatType(editedRepeatRule.getRepeatType());
            updated = true;
        }
        if (getFrequency() != editedRepeatRule.getFrequency()) {
            setFrequency(editedRepeatRule.getFrequency());
            updated = true;
        }
        if (getInterval() != editedRepeatRule.getInterval()) {
            setInterval(editedRepeatRule.getInterval());
            updated = true;
        }

        if (getEndDate() != editedRepeatRule.getEndDate()) {
            setEndDate(editedRepeatRule.getEndDate());
            updated = true;
        }
        if (getNumberOfRepeats() != editedRepeatRule.getNumberOfRepeats()) {
            setNumberOfRepeats(editedRepeatRule.getNumberOfRepeats());
            updated = true;
        }

        if (getDaysInWeek() != editedRepeatRule.getDaysInWeek()) {
            setDaysInWeek(editedRepeatRule.getDaysInWeek());
            updated = true;
        }
        if (getWeeksInMonth() != editedRepeatRule.getWeeksInMonth()) {
            setWeeksInMonth(editedRepeatRule.getWeeksInMonth());
            updated = true;
        }
        if (getWeekdaysInMonth() != editedRepeatRule.getWeekdaysInMonth()) {
            setWeekdaysInMonth(editedRepeatRule.getWeekdaysInMonth());
            updated = true;
        }

        if (getMonthsInYear() != editedRepeatRule.getMonthsInYear()) {
            setMonthsInYear(editedRepeatRule.getMonthsInYear());
            updated = true;
        }
        if (getDayInMonth() != editedRepeatRule.getDayInMonth()) {
            setDayInMonth(editedRepeatRule.getDayInMonth());
            updated = true;
        }
        if (getDayInYear() != editedRepeatRule.getDayInYear()) {
            setDayInYear(editedRepeatRule.getDayInYear());
            updated = true;
        }

        if (getNumberSimultaneousRepeats() != editedRepeatRule.getNumberSimultaneousRepeats()) {
            setNumberSimultaneousRepeats(editedRepeatRule.getNumberSimultaneousRepeats());
            updated = true;
        }
        if (getNumberOfDaysRepeatsAreGeneratedAhead() != editedRepeatRule.getNumberOfDaysRepeatsAreGeneratedAhead()) {
            setNumberOfDaysRepeatsAreGeneratedAhead(editedRepeatRule.getNumberOfDaysRepeatsAreGeneratedAhead());
            updated = true;
        }
        if (false) { //DON'T update these from edited rule, since they will always be empty!!
            if (!getListOfDoneInstances().equals(editedRepeatRule.getListOfDoneInstances())) {
                setListOfDoneInstances(editedRepeatRule.getListOfDoneInstances());
                updated = true;
            }
            if (!getListOfUndoneInstances().equals(editedRepeatRule.getListOfUndoneInstances())) {
                setListOfUndoneInstances(editedRepeatRule.getListOfUndoneInstances());
                updated = true;
            }
        }
        if (isDatedCompletion() != editedRepeatRule.isDatedCompletion()) {
            setDatedCompletion(editedRepeatRule.isDatedCompletion());
            updated = true;
        }
//        if (repeatRuleOriginator != repeatRule.repeatRuleOriginator) {
//            return false;
//        }
        return updated;
    }

//////////////////////////////////////////////////////////////////////////////////////////////    
//BELOW HERE ONLY SIMPLE SETTERS AND GETTERS
//////////////////////////////////////////////////////////////////////////////////////////////
    static String getFreqText(int freq, boolean capitalLetter, boolean lyEndings, boolean plural) {
        Character c;
        if (lyEndings) {
            if (capitalLetter) {
                return (freq == RepeatRule.DAILY ? "Daily" : (freq == RepeatRule.WEEKLY ? "Weekly" : (freq == RepeatRule.MONTHLY ? "Monthly" : "Yearly")));
            } else {
                return (freq == RepeatRule.DAILY ? "daily" : (freq == RepeatRule.WEEKLY ? "weekly" : (freq == RepeatRule.MONTHLY ? "monthly" : "yearly")));
            }
        } else if (capitalLetter) {
            return (freq == RepeatRule.DAILY ? "Day" : (freq == RepeatRule.WEEKLY ? "Week" : (freq == RepeatRule.MONTHLY ? "Month" : "Year"))) + (plural ? "s" : "");
        } else {
            return (freq == RepeatRule.DAILY ? "day" : (freq == RepeatRule.WEEKLY ? "week" : (freq == RepeatRule.MONTHLY ? "month" : "year"))) + (plural ? "s" : "");
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * date up till which repeat instances have been generated so far. Used
     * inside MyRepeatRule to keep track of the latest date for which instances
     * have been created (instead of e.g. just using the latest date stored in
     * the list of generated instances). This is used to ensure that e.g.
     * deleted or done instances do not re-appear
     */
//    private long getLastDateGeneratedFor() {
////        return lastDateGeneratedFor;
//        return getLong(LAST_DATE_GENERATED_FOR);
//    }
    /**
     * date up till which repeat instances have been generated so far. Used
     * inside MyRepeatRule to keep track of the latest date for which instances
     * have been created (instead of e.g. just using the latest date stored in
     * the list of generated instances). This is used to ensure that e.g.
     * deleted or done instances do not re-appear
     */
//    private void setLastDateGeneratedFor(long lastDateGeneratedFor) {
////        if (this.lastDateGeneratedFor != lastDateGeneratedFor) {
////            this.lastDateGeneratedFor = lastDateGeneratedFor;
////            changed();
////        }
//        put(LAST_DATE_GENERATED_FOR, lastDateGeneratedFor);
//    }
    /**
     * store the latest date generated by the repeat rule (to make sure the same
     * date is of any of the instances generated
     */
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getLastGeneratedDateXXX() {
////        return lastGeneratedDate;
////        return getLong(LAST_DATE_GENERATED_FOR);
//        return getLastGeneratedDateD().getTime();
//    }
//    private void setLastGeneratedDateIfGreaterThanLastDate(long lastGeneratedDate) {
//        setLastGeneratedDateIfGreaterThanLastDate(new Date(lastGeneratedDate));
//    }
    /**
     * the latest date of any of the instances generated if
     * lastGeneratedDate==null, then remove old value
     */
//    private void setLastGeneratedDate(Date lastGeneratedDate, boolean storeEvenEarlierDates) {
//        Date oldLastGeneratedDate = getLastGeneratedDateD();
////        ASSERT.that(getDate(PARSE_LAST_DATE_GENERATED) == null || lastGeneratedDate == null
////        ASSERT.that(oldLastGeneratedDate.getTime() == 0 || lastGeneratedDate == null
////                //                || (storeEvenEarlierDates || lastGeneratedDate.getTime() > getDate(PARSE_LAST_DATE_GENERATED).getTime()),
////                || (storeEvenEarlierDates || lastGeneratedDate.getTime() > oldLastGeneratedDate.getTime()),
////                "new lastGeneratedDate should always be higher than previous, lastDate=" + getDate(PARSE_LAST_DATE_GENERATED) + ", newDate=" + lastGeneratedDate);
//        ASSERT.that(lastGeneratedDate == null || lastGeneratedDate.getTime() == MyDate.MIN_DATE
//                || storeEvenEarlierDates || lastGeneratedDate.getTime() > oldLastGeneratedDate.getTime(),
//                "new lastGeneratedDate NOT higher than previous, oldDate=" + oldLastGeneratedDate + ", new=" + lastGeneratedDate);
//        if (lastGeneratedDate == null || lastGeneratedDate.getTime() == MyDate.MIN_DATE) {
////             if (storeEvenEarlierDates || getDate(PARSE_LAST_DATE_GENERATED)==null || lastGeneratedDate.getTime() > getDate(PARSE_LAST_DATE_GENERATED).getTime()) {
//            remove(PARSE_LAST_DATE_GENERATED);
//        } else {
//            if (storeEvenEarlierDates || lastGeneratedDate.getTime() > oldLastGeneratedDate.getTime()) {
//                put(PARSE_LAST_DATE_GENERATED, lastGeneratedDate);
//            }
//        }
//    }
//    private void setLastGeneratedDate(Date lastGeneratedDate) {
//        setLastGeneratedDate(lastGeneratedDate, false);
//    }
//    public Date getNextcomingDateDXXX() {
//        Date date = getDate(PARSE_NEXTCOMING_REPEAT_DATE);
//        return (date == null) ? new Date(MyDate.MIN_DATE) : null;
//    }
//    public long getLastGeneratedDateXXX() {
////        return lastGeneratedDate;
////        return getLong(LAST_DATE_GENERATED_FOR);
//        return getLastGeneratedDateD().getTime();
//    }
//    private void setLastGeneratedDateIfGreaterThanLastDate(long lastGeneratedDate) {
//        setLastGeneratedDateIfGreaterThanLastDate(new Date(lastGeneratedDate));
//    }
    /**
     * the latest date of any of the instances generated
     */
//    private void setNextcomingDateXXX(Date nextcomingRepeatDate) {
//        ASSERT.that(getDate(PARSE_NEXTCOMING_REPEAT_DATE) == null || nextcomingRepeatDate == null
//                || nextcomingRepeatDate.getTime() > getDate(PARSE_NEXTCOMING_REPEAT_DATE).getTime(),
//                "new lastGeneratedDate should always be higher than previous");
//        if (nextcomingRepeatDate != null && nextcomingRepeatDate.getTime() != MyDate.MIN_DATE) {
//            put(PARSE_NEXTCOMING_REPEAT_DATE, nextcomingRepeatDate);
//        } else {
//            remove(PARSE_NEXTCOMING_REPEAT_DATE);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setCount(int count) { //now called getNumberOfRepeats()
//        if (count != 0) {
//            put(COUNT, count);
//        } else {
//            remove(COUNT);
//        }
//    }
//
//    public int getCount() {
//        int count = getInt(COUNT);
//        return (count == 0) ? 0 : count;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * the latest date of any of the instances generated
     */
//    public void setLastGeneratedDateIfBigger(long lastGeneratedDate) {
////        if (lastGeneratedDate > this.lastGeneratedDate) {
////            this.lastGeneratedDate = lastGeneratedDate;
////            changed();
////        }
//        put("lastGeneratedDate", lastGeneratedDate);
//    }
//</editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public RepeatRuleObject getReferenceObjectForRepeatTime() {
//        return referenceObjectForRepeatTime;
//    }
//
//    public void setReferenceObjectForRepeatTime(RepeatRuleObject referenceObjectForRepeatTime) {
//        if (this.referenceObjectForRepeatTime != referenceObjectForRepeatTime) {
//            this.referenceObjectForRepeatTime = referenceObjectForRepeatTime;
//            changed();
//        }
//    }
//    public long getStartDate() {
//        return specifiedStartDate;
//    }
//
//    public void setStartDate(long startDate) {
//        if (this.specifiedStartDate != startDate) {
//            this.specifiedStartDate = startDate;
////            this.referenceObjectForRepeatTime = null;
//            changed();
//        }
//    }// </editor-fold>
    /**
     *
     * @return
     */
//    public long getSpecifiedStartDateXXX() {
//        return getSpecifiedStartDateXXXZZZ().getTime();
//    }
//
    public Date getSpecifiedStartDateZZZ() {
//        return specifiedStartDate;
//        return getLong(SPECIFIED_START_DATE);
        Date date = getDate(PARSE_SPECIFIED_START_DATE_XXX);
        return (date == null) ? new MyDate(0) : date;
    }

//    private Date getStartDateForCompletion(Date defaultDate, boolean completionDate) {
//        Date date = getLatestDateGenerated(completionDate); //when we generate a new date upon completion, it should be based on the latest date generated, whether already done/completed or not
////        return (date.equals(new MyDate(MyDate.MIN_DATE))) ? defaultDate : date;
//        return (date.getTime() == MyDate.MIN_DATE) ? defaultDate : date;
//    }
//    private Date getStartDateForCompletion() {
//        return getStartDateForCompletion(new MyDate()); //use now for default
//    }
//    private Date getStartDateForCompletion(boolean completionDate) {
//        return getStartDateForCompletion(new MyDate()); //use now for default
//    }
    private Date getStartDateForRefreshRule(Date defaultDate) {
        Date date = getLatestDateDone(false);
        return (date.getTime() == MyDate.MIN_DATE) ? defaultDate : date;
    }

    private Date getStartDateForRefreshRule() {
        return getStartDateForRefreshRule(new MyDate(MyDate.MIN_DATE));
    }

    /**
     *
     * @param specifiedStartDate
     */
//    public void setSpecifiedStartDateXXX(long specifiedStartDate) {
//        setSpecifiedStartDate(new Date(specifiedStartDate));
//    }
    public void setSpecifiedStartDateXXXZZZ(Date specifiedStartDate) {
//        if (this.specifiedStartDate != specifiedStartDate) {
//            this.specifiedStartDate = specifiedStartDate;
//            changed();
//        }
//        put(SPECIFIED_START_DATE, specifiedStartDate);
        if (specifiedStartDate != null && specifiedStartDate.getTime() != 0) {
            put(PARSE_SPECIFIED_START_DATE_XXX, specifiedStartDate);
        } else {
            remove(PARSE_SPECIFIED_START_DATE_XXX);
        }
    }

    /**
     * returns set values separated with , Eg. "Mon, Tue, Fri" or "Feb"
     */
    private static String getOredValuesAsString(int ordValues, Object[] intArray, String[] strArray, String sepStr) {
        String s = "";
        String sep = "";
        for (int i = 0; i < intArray.length; i++) {
            if ((ordValues & (int) intArray[i]) != 0) {
                s += sep + strArray[i];
                sep = sepStr;
            }
        }
        return s;
    }

    static String getDaysInWeekAsString(int dayInWeek, String seperator) {
//        return getOredValuesAsString(dayInWeek, DAY_IN_WEEK_NUMBERS_MONDAY_FIRST, MyDate.DAY_NAMES_MONDAY_FIRST_SHORT, seperator);
        return getOredValuesAsString(dayInWeek, DAY_IN_WEEK_NUMBERS_MONDAY_FIRST_INCL_WEEKDAYS, MyDate.DAY_NAMES_MONDAY_FIRST_INCL_WEEKDAYS_SHORT, seperator);
    }

    static String getWeeksInMonthAsString(int weeksInMonth) {
//        return getOredValuesAsString(weeksInMonth, WEEKS_IN_MONTH_NUMBERS, getWeekInMonthNames(), "/");
        return getOredValuesAsString(weeksInMonth, WEEKS_IN_MONTH_NUMBERS, getWeekInMonthNames(), "+");

    }

    static String getMonthsInYearAsString(int monthsInYear) {
//        return getOredValuesAsString(monthsInYear, MONTH_IN_YEAR_NUMBERS, MyDate.MONTH_NAMES_SHORT, "/");
        return getOredValuesAsString(monthsInYear, MONTH_IN_YEAR_NUMBERS, MyDate.MONTH_NAMES_SHORT, "+");
    }

    private void setWeek(RepeatRule repeatRule) {
//        repeatRule.setInt(RepeatRule.DAY_IN_WEEK, daysInWeek);
        repeatRule.setInt(RepeatRule.DAY_IN_WEEK, getDaysInWeek());
    }

    private void setMonth(RepeatRule repeatRule) {
//        if (dayInMonth > 0) {
//            repeatRule.setInt(RepeatRule.DAY_IN_MONTH, dayInMonth > 31 ? 31 : dayInMonth); //covers case where dayInMonth is 31 (=="Last")
//        } else if (weeksInMonth != 0) {
////                    if (weeksInMonth >= 0) {
//            repeatRule.setInt(RepeatRule.WEEK_IN_MONTH, weeksInMonth);
//            setWeek(repeatRule);
//        } else if (weekdaysInMonth != 0) {
//            repeatRule.setInt(RepeatRule.WEEKDAYS_IN_MONTH, weekdaysInMonth);
//            setWeek(repeatRule);
//        } else {
////#mdebug
//            ASSERT.that("Neither day nor weeks set for monthly repeat");
////#enddebug
//        }
        if (getDayInMonth() > 0) {
            repeatRule.setInt(RepeatRule.DAY_IN_MONTH, getDayInMonth() > 31 ? 31 : getDayInMonth()); //covers case where dayInMonth is 31 (=="Last")
        } else if (getWeeksInMonth() != 0) {
//                    if (weeksInMonth >= 0) {
            repeatRule.setInt(RepeatRule.WEEK_IN_MONTH, getWeeksInMonth());
            setWeek(repeatRule);
        } else if (getWeekdaysInMonth() != 0) {
            repeatRule.setInt(RepeatRule.WEEKDAYS_IN_MONTH, getWeekdaysInMonth());
            setWeek(repeatRule);
        } else {
//#mdebug
            ASSERT.that("Neither day nor weeks set for monthly repeat");
//#enddebug
        }
    }

    private void setYear(RepeatRule repeatRule) {
        if (getDayInYear() > 0) {
            repeatRule.setInt(RepeatRule.DAY_IN_YEAR, getDayInYear());
        } else {
            repeatRule.setInt(RepeatRule.MONTH_IN_YEAR, getMonthsInYear());
            setMonth(repeatRule);
        }
    }

    void setRepeatType(int repeatType) {
//        if (this.repeatType != repeatType) {
//            this.repeatType = repeatType;
//            changed();
//        }
//        if (repeatType != REPEAT_TYPE_FROM_COMPLETED_DATE) {
        if (repeatType != REPEAT_TYPE_NO_REPEAT) {
            put(PARSE_REPEAT_TYPE, repeatType);
        } else {
            remove(PARSE_REPEAT_TYPE);
        }
    }

    int getRepeatType() {
//        return repeatType;
//        return getInt(REPEAT_TYPE);
        Integer i = getInt(PARSE_REPEAT_TYPE);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
//        return i != null ? i : REPEAT_TYPE_FROM_COMPLETED_DATE;
        return i != null ? i : REPEAT_TYPE_NO_REPEAT;
    }

    void setFrequency(int frequency) {
        ASSERT.that(frequency == RepeatRule.DAILY || frequency == RepeatRule.WEEKLY || frequency == RepeatRule.MONTHLY || frequency == RepeatRule.YEARLY, "Illegal frequencey value set " + frequency);
//        if (this.frequency != frequency) {
//            this.frequency = frequency;
//            changed();
//        }
        if (frequency != RepeatRule.DAILY) {
            put(PARSE_FREQUENCY, frequency);
        } else {
            remove(PARSE_FREQUENCY);
        }
    }

    int getFrequency() {
//        return frequency;
//        return getInt(FREQUENCY);
        Integer i = getInt(PARSE_FREQUENCY);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : RepeatRule.DAILY;
    }

    void setInterval(int interval) {
//        if (this.interval != interval) {
//            this.interval = interval;
//            changed();
//        }
        if (interval != 1) {
            put(PARSE_INTERVAL, interval);
        } else {
            remove(PARSE_INTERVAL);
        }
    }

    int getInterval() {
//        return interval;
//        return getInt(INTERVAL);
        Integer i = getInt(PARSE_INTERVAL);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 1;

    }

    void setDatedCompletion(boolean dateOnCompletionRepeats) {
        if (dateOnCompletionRepeats) {
            put(PARSE_DATE_ON_COMPLETION_REPEATS, true);
        } else {
            remove(PARSE_DATE_ON_COMPLETION_REPEATS);
        }
    }

    boolean isDatedCompletion() {
        Boolean b = getBoolean(PARSE_DATE_ON_COMPLETION_REPEATS);
        return b != null ? b : false;
    }

    /**
     * use count field (if it's defined, meaning a value other than 0 is set?)
     * or endDate field?
     */
    boolean useCount() {
//        return count > 1;
//        return count > 0;
        return getNumberOfRepeats() != Integer.MAX_VALUE;
    }

    /**
     * how many times should an event repeat. 1 means one repeat in addition to
     * the originally created event. returns Integer.MAX_VALUE if not defined.
     *
     */
    int getNumberOfRepeats() {
//        return count;
//        return getInt(COUNT);
        Integer i = getInt(PARSE_COUNT);
//        if (i != null) {
//            return i;
//        } else {
//            return Integer.MAX_VALUE;
//        }
        return i != null ? i : Integer.MAX_VALUE;

    }

    /**
     * defines how many times an even will repeat. 1 means one repeat in
     * addition to the originally created event.
     *
     * @param count
     */
    void setNumberOfRepeatsInParse(int count) {
        if (count != Integer.MAX_VALUE) {
            put(PARSE_COUNT, count);
//            if (false) {
//                put(PARSE_END_DATE, new Date(MAX_DATE)); //ensure end date is maximum as to not block count
//            }
        } else {
            remove(PARSE_COUNT);
//            put(END_DATE, new Date(MAX_DATE));
        }
//        setEndDate(Long.MAX_VALUE);
//        put(END_DATE, new Date(Long.MAX_VALUE));
    }

    void setNumberOfRepeats(int count) {
//        int nbPrevInstances = getListOfDoneInstances().size();
//        setNumberOfRepeatsInParse(count + nbPrevInstances); //to cover the case where number repeats for a rule with existing done instances. E.g. if count is set to lessOrEquals number that would (unintutively) stop the repeat rule
        setNumberOfRepeatsInParse(count); //to cover the case where number repeats for a rule with existing done instances. E.g. if count is set to lessOrEquals number that would (unintutively) stop the repeat rule
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * defines how many times an even will repeat. 1 means one repeat in
     * addition to the originally created event.
     *
     * @param count
     */
//    void setNumberOfRepeatsXXX(int count) {
////        if (this.count != count) {
////            this.count = count;
////            this.endDate = Long.MAX_VALUE;
////            changed();
////        }
//        put(COUNT, count);
////        put("endDate", Long.MAX_VALUE);
//    }
//</editor-fold>
    /**
     * returns endDate. Default value Long.MAX_VALUE
     *
     * @return
     */
    public long getEndDate() {
        return getEndDateD().getTime();
    }

    public Date getEndDateD() {
//        return endDate;
//        return getInt(END_DATE);
//        Long l = getLong(END_DATE);
//        if (l != null) {
//            return l;
//        } else {
//            return 0;
//        }
        Date date = getDate(PARSE_END_DATE);
//        return (date == null) ? new Date(0) : date;
        return (date == null) ? new Date(MyDate.MAX_DATE) : date;
    }

    /**
     *
     * @param endDate
     */
    public void setEndDate(long endDate) {
        setEndDate(new MyDate(endDate));
    }

    public void setEndDate(Date endDate) {
//        if (this.endDate != endDate) {
//            this.endDate = endDate;
//            this.count = Integer.MAX_VALUE; //=0;
//            changed();
//        }
//        put(END_DATE, endDate);
//        if (endDate.getTime() != 0) {
        if (endDate.getTime() != MyDate.MAX_DATE) {
            put(PARSE_END_DATE, endDate);
//            if (false) {
//                put(PARSE_COUNT, Integer.MAX_VALUE); //ensure count is max to not block for endDate
//            }
        } else {
            remove(PARSE_END_DATE);
        }
    }

    /**
     *
     * @param daysInWeek
     */
    public void setDaysInWeek(int daysInWeek) {
//        if (this.daysInWeek != daysInWeek) {
//            this.daysInWeek = daysInWeek;
//            changed();
//        }
        datePatternChanged = datePatternChanged || daysInWeek != getDaysInWeek();

        if (daysInWeek != 0) {
            put(PARSE_DAYS_IN_WEEK, daysInWeek);
        } else {
            remove(PARSE_DAYS_IN_WEEK);
        }
    }

    /**
     *
     * @return
     */
    public int getDaysInWeek() {
//        return daysInWeek;
//        return getInt(DAYS_IN_WEEK);
        Integer i = getInt(PARSE_DAYS_IN_WEEK);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;

    }

    public Vector getDaysInWeekAsVector() {
        return daysInWeeksBitsToVector(getDaysInWeek());
    }

    public Vector getDaysInWeekAsVectorInclWeekdays() {
        return daysInWeeksBitsToVectorInclWeekends(getDaysInWeek());
    }

    /**
     *
     * @param monthsInYear
     */
    public void setMonthsInYear(int monthsInYear) {
//        if (this.monthsInYear != monthsInYear) {
//            this.monthsInYear = monthsInYear;
//            this.dayInYear = 0;
//            changed();
//        }
        datePatternChanged = datePatternChanged || monthsInYear != getMonthsInYear();

        if (monthsInYear != 0) {
            put(PARSE_MONTHS_IN_YEAR, monthsInYear);
        } else {
            remove(PARSE_MONTHS_IN_YEAR);
        }
//        setDayInYear(0);
        if (false) {
            put(PARSE_DAY_IN_YEAR, 0);
        }
    }

    /**
     *
     * @return
     */
    public int getMonthsInYear() {
//        return monthsInYear;
//        return getInt(MONTHS_IN_YEAR);
        Integer i = getInt(PARSE_MONTHS_IN_YEAR);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;

    }

    public Vector getMonthInYearAsVector() {
        return monthInYearAsVector(getMonthsInYear());
    }

    /**
     * convert the set bit values for days in week (RepeatRule.SATURDAY..SUNDAY)
     * to a Vector of Integers with the individual values
     */
    private static Vector monthInYearAsVector(int monthInYears) {
        Vector monthInYearVector = new Vector();
        int val = RepeatRule.JANUARY;
        do {
            if ((monthInYears & val) != 0) {
                monthInYearVector.addElement(new Integer(val));
            }
            val *= 2;
        } while (val <= RepeatRule.DECEMBER);
        return monthInYearVector;
    }

    /**
     * NB: This method is just to get the vector to initialize the picker in
     * ScreenRepeatRule based on the defined due date!
     *
     * @param date
     * @return
     */
    static Vector monthInYearAsVector(Date date) {
        Vector monthInYearVector = new Vector();
        int val = RepeatRule.JANUARY;
        int month = val << (MyDate.getMonthInYear(date) - 1);
        monthInYearVector.addElement(new Integer(month));
        return monthInYearVector;
    }

    /**
     * convert the set bit values for days in week (RepeatRule.SATURDAY..SUNDAY)
     * to a Vector of Integers with the individual values
     */
    private static Vector daysInWeeksBitsToVector(int daysInWeeks) {
        Vector daysInWeeksVector = new Vector();
        int val = RepeatRule.SATURDAY;
        do {
            if ((daysInWeeks & val) != 0) {
                daysInWeeksVector.addElement(new Integer(val));
            }
            val *= 2;
        } while (val <= RepeatRule.SUNDAY);
        return daysInWeeksVector;
    }

    /**
     * NB: This method is just to get the vector to initialize the picker in
     * ScreenRepeatRule based on the defined due date!
     *
     * @param date
     * @return
     */
    static Vector dayInWeeksToVector(Date date) {
        //SUNDAY==1, MON=2, TUE=3, WED=4, THU=5. FRI=6,          SAT=7
        //                                       FRIDAY = 0x800, SAT=0x400, 
        Vector daysInWeeksVector = new Vector();
        int dayInWeek = MyDate.getDayInWeek(date); //SUNDAY==1, MON=2, TUE=3, WED=4, THU=5. FRI=6, SAT=7
        int shiftRightPos = 7 - dayInWeek; //starting with SAT=0x400, must shift 
        int val = RepeatRule.SATURDAY; //NB!!: SATURDAY = 0x400; FRIDAY = 0x800; => must shift RIGHT
        int dayInWeekRR = val;
//        if (dayInWeek != Calendar.SATURDAY) //        int dayInWeekRR = val << (dayInWeek); //SUNDAY==1, so starting with Saturday and shifting one less than SUNDAY
        dayInWeekRR = val << shiftRightPos; //SUNDAY==1, so starting with Saturday and shifting one less than SUNDAY
        daysInWeeksVector.addElement(new Integer(dayInWeekRR));
        return daysInWeeksVector;
    }

    /**
     * returns . NB: This method is just to get the vector to initialize the
     * picker in ScreenRepeatRule based on the defined due date!
     *
     * @param date
     * @return
     */
    static Vector dayInMonthToVector(Date date) {
        Vector daysInWeeksVector = new Vector();
        int dayInWeek = MyDate.getWeekdayInMonth(date); //first occurrence of the weekday is 1
        int dayInWeekRR = RepeatRule.FIRST << dayInWeek - 1;
        daysInWeeksVector.addElement(new Integer(dayInWeekRR));
        return daysInWeeksVector;
    }

    /**
     * returns the week in the month which the date occurs. E.g. the 1st of the
     * month will always occur in week 1, but the following days may occur in
     * the week2 NB: This method is just to get the vector to initialize the
     * picker in ScreenRepeatRule based on the defined due date!
     *
     * @param date
     * @return
     */
    static Vector weekInMonthToVector(Date date) {
        Vector daysInWeeksVector = new Vector();
        int weekInWeek = MyDate.getWeekNbInMonth(date); //first occurrence of the weekday is 1
        int dayInWeekRR = RepeatRule.FIRST << weekInWeek - 1;
        daysInWeeksVector.addElement(new Integer(dayInWeekRR));
        return daysInWeeksVector;
    }

    private static Vector daysInWeeksBitsToVectorInclWeekends(int daysInWeeks) {
        Vector daysInWeeksVector = new Vector();
        int val = RepeatRule.SATURDAY;
        do {
            if ((daysInWeeks & val) != 0) {
                daysInWeeksVector.addElement(new Integer(val));
            }
            val *= 2;
        } while (val <= RepeatRule.SUNDAY);
        if ((daysInWeeks & RepeatRule.WEEKDAYS) != 0) {
            daysInWeeksVector.addElement(new Integer(RepeatRule.WEEKDAYS));
        }
        if ((daysInWeeks & RepeatRule.WEEKENDS) != 0) {
            daysInWeeksVector.addElement(new Integer(RepeatRule.WEEKENDS));
        }
        return daysInWeeksVector;
    }

    private static Vector weeksInMonthBitsToVector(int weeksInMonth) {
        Vector weeksInMonthVector = new Vector();
        int val = RepeatRule.FIRST;
        do {
            if ((weeksInMonth & val) != 0) {
                weeksInMonthVector.addElement(new Integer(val));
            }
            val *= 2;
        } while (val <= RepeatRule.FIFTHLAST);
        return weeksInMonthVector;
    }

    public Vector getWeekInMonthAsVector() {
//        Vector weeksInMonthVector = new Vector();
//        int val = RepeatRule.FIRST;
//        do {
//            if ((weeksInMonth & val) != 0) {
//                weeksInMonthVector.addElement(new Integer(val));
//            }
//            val *= 2;
//        } while (val =< RepeatRule.FIFTHLAST);
//        return weeksInMonthVector;
//        return weeksInMonthBitsToVector(weeksInMonth);
        return weeksInMonthBitsToVector(getWeeksInMonth());
    }

    public static int bitOrIntegerVectorToInt(Vector vectorOfIntegers) {
        int bitValue = 0;
        for (int i = 0, size = vectorOfIntegers.size(); i < size; i++) {
            bitValue |= ((Integer) vectorOfIntegers.elementAt(i)).intValue();
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//            switch() {
//                case RepeatRule.FIRST:
//                case RepeatRule.SECOND:
//                case RepeatRule.THIRD:
//                case RepeatRule.FOURTH:
//                case RepeatRule.FIFTH:
//                case RepeatRule.SECONDLAST:
//                case RepeatRule.THIRDLAST:
//                case RepeatRule.FOURTHLAST:
//                case RepeatRule.FIFTHLAST:
//            }
//        }
//        int val = RepeatRule.FIRST;
//        do {
//            weeksInMonth.addElement(new Integer(val));
//        } while (val <RepeatRule.FIFTHLAST);
//</editor-fold>
        return bitValue;
    }

    /**
     *
     * @param weeksInMonth
     */
    public void setWeeksInMonth(int weeksInMonth) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.weeksInMonth = weeksInMonth;
//        this.weekdaysInMonth = 0;
//        this.dayInMonth = 0;
//        this.dayInYear = 0;
//</editor-fold>
        datePatternChanged = datePatternChanged || weeksInMonth != getWeeksInMonth();

        if (weeksInMonth != 0) {
            put(PARSE_WEEKS_IN_MONTH, weeksInMonth);
        } else {
            remove(PARSE_WEEKS_IN_MONTH);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        setWeekdaysInMonth(0);
//        setDayInMonth(0);
//        setDayInYear(0);
//        if (false) {
//            if (getWeekdaysInMonth() != 0) {
//                setWeekdaysInMonth(0);
//            }
//            put(PARSE_WEEKDAYS_IN_MONTH, 0);
//            put(PARSE_DAY_IN_MONTH, 0);
//            put(PARSE_DAY_IN_YEAR, 0);
//        }
//</editor-fold>
    }

    /**
     *
     * @return
     */
    public int getWeeksInMonth() {
//        return weeksInMonth;
//        return getInt(WEEKS_IN_MONTH);
        Integer i = getInt(PARSE_WEEKS_IN_MONTH);
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
//</editor-fold>
        return i != null ? i : 0;
    }

    public Vector getWeekdaysInMonthAsVector() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Vector weekDays = new Vector();
//        int val = RepeatRule.FIRST;
//        do {
//            if ((weekdaysInMonth & val) != 0) {
//                weekDays.addElement(new Integer(val));
//            }
//            val *= 2;
//        } while (val < RepeatRule.FIFTHLAST);
////        for (int i = 0, size = getIterationSize(); i < size; i++) {
////            if ((setBitValues & values[i]) != 0) {
////                selected[i] = true;
////            }
////        }
//        return weekDays;
//        return weeksInMonthBitsToVector(weekdaysInMonth);
//</editor-fold>
        return weeksInMonthBitsToVector(getWeekdaysInMonth());
    }

    /**
     * contains the set of selected weekdays in month (FIRST/SECOND/... applied
     * to Monday/Tuesday/...), as OR'd values. Is mutually exclusive wtih
     * weeksInMonth
     */
    public void setWeekdaysInMonth(int weekdaysInMonth) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (this.weekdaysInMonth != weekdaysInMonth) {
//            this.weekdaysInMonth = weekdaysInMonth;
//            this.weeksInMonth = 0;
//            this.dayInMonth = 0;
//            this.dayInYear = 0;
//            changed();
//        }
//</editor-fold>
        datePatternChanged = datePatternChanged || weekdaysInMonth != getWeekdaysInMonth();

        if (weekdaysInMonth != 0) {
            put(PARSE_WEEKDAYS_IN_MONTH, weekdaysInMonth);
        } else {
            remove(PARSE_WEEKDAYS_IN_MONTH);
        }
//        setWeeksInMonth(0);
//        setDayInMonth(0);
//        setDayInYear(0);
        if (false) {
            put(PARSE_WEEKS_IN_MONTH, 0);
            put(PARSE_DAY_IN_MONTH, 0);
            put(PARSE_DAY_IN_YEAR, 0);
        }
    }

    /**
     * contains the set of selected weeks in month, as OR'd values. Is mutually
     * exclusive wtih dayInMonth
     */
    public int getWeekdaysInMonth() {
//        return weekdaysInMonth;
//        return getInt(WEEKDAYS_IN_MONTH);
        Integer i = getInt(PARSE_WEEKDAYS_IN_MONTH);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;

    }

    /**
     * 0 means undefined/not set
     *
     * @return
     */
    public int getDayInMonth() {
//        return dayInMonth;
//        return getInt(DAY_IN_MONTH);
        Integer i = getInt(PARSE_DAY_IN_MONTH);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;

    }

    /**
     *
     * @param dayInMonth
     */
    public void setDayInMonth(int dayInMonth) {
//        this.dayInMonth = dayInMonth>31?31:dayInMonth; //- won't work, since it won't re-establish 'last' in choicelist on next edit
//        if (this.dayInMonth != dayInMonth) {
//            this.dayInMonth = dayInMonth;
//            this.weeksInMonth = 0;
//            this.weekdaysInMonth = 0;
//            this.daysInWeek = 0;
//            this.dayInYear = 0;
        datePatternChanged = datePatternChanged || dayInMonth != getDayInMonth();

        if (dayInMonth != 0) {
            put(PARSE_DAY_IN_MONTH, dayInMonth);
        } else {
            remove(PARSE_DAY_IN_MONTH);
        }
//        setWeeksInMonth(0);
//        setWeekdaysInMonth(0);
//        setDaysInWeek(0);
//        setDayInYear(0);
        if (false) {
            put(PARSE_WEEKS_IN_MONTH, 0);
            put(PARSE_WEEKDAYS_IN_MONTH, 0);
            put(PARSE_DAYS_IN_WEEK, 0);
            put(PARSE_DAY_IN_YEAR, 0);
        }
//            changed();
//        }
    }

    /**
     *
     * @return
     */
    public int getDayInYear() {
//        return dayInYear;
//        return getInt(DAY_IN_YEAR);
        Integer i = getInt(PARSE_DAY_IN_YEAR);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;

    }

    /**
     *
     * @param dayInYear
     */
    public void setDayInYear(int dayInYear) {
//        if (this.dayInYear != dayInYear) {
//            this.dayInYear = dayInYear;
//            this.dayInMonth = 0;
//            this.monthsInYear = 0;
//            this.weeksInMonth = 0;
//            this.weekdaysInMonth = 0;
//            changed();
//        }
        datePatternChanged = datePatternChanged || dayInYear != getDayInYear();

        if (dayInYear != 0) {
            put(PARSE_DAY_IN_YEAR, dayInYear);
        } else {
            remove(PARSE_DAY_IN_YEAR);
        }
//        setDayInMonth(0);
//        setMonthsInYear(0);
//        setWeeksInMonth(0);
//        setWeekdaysInMonth(0);
        if (false) {
            put(PARSE_DAY_IN_MONTH, 0);
            put(PARSE_MONTHS_IN_YEAR, 0);
            put(PARSE_WEEKS_IN_MONTH, 0);
            put(PARSE_WEEKDAYS_IN_MONTH, 0);
        }
    }

    /**
     * if true, then use getNumberFutureRepeatsGeneratedAhead() to get how many
     * repeats are generated ahead, otherwise use
     * getNumberOfDaysRepeatsAreGeneratedAhead() to get number of days repeats
     * are generated ahead. This actually controls the whole logic of how we
     * generate future instances, since if we do by count, we can't use date
     * information to control from which dates we start the generation, or by
     * which date we end the generated.
     *
     * @return
     */
    public boolean useNumberFutureRepeatsToGenerateAhead() {
//#mdebug
//        ASSERT.that(getNumberOfDaysRepeatsAreGeneratedAhead() != 0 || getNumberFutureRepeatsToGenerateAhead() != 0);
//#enddebug
//        return numberOfRepeatsGeneratedAhead != 0;
//        return getNumberSimultaneousRepeats() != 0 || getNumberOfDaysRepeatsAreGeneratedAhead() == 0; //"getNumberOfDaysRepeatsAreGeneratedAhead() == 0" => if neither is defined, use NumberFutureRepeats by default
//        return getNumberSimultaneousRepeats() != 1 || getNumberOfDaysRepeatsAreGeneratedAhead() == 0; //"getNumberOfDaysRepeatsAreGeneratedAhead() == 0" => if neither is defined, use NumberFutureRepeats by default
        return true; //TODO: number of future days ahead disable for now
    }

    /**
     * how many future repeats (total number, including the originator!) are
     * generated at any point in time.Indicates how many instances are generated
     * in *addition* to the current one (either originator or next item), which
     * the user will always expect to see. Used to avoid generating a too high
     * number of repeats at any one time. Mutually exclusive with
     * numberOfDaysRepeatsAreGeneratedAhead (only one of the two can be used at
     * any one time)
     *
     * @return 1 be default which is the same as not defining any specific value
     */
    public int getNumberSimultaneousRepeats() {
//        return numberOfRepeatsGeneratedAhead;
        Integer i = getInt(PARSE_NUMBER_SIMULTANEOUS_REPEATS_TO_GENERATE_AHEAD);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
//        return i != null ? i : 0;
        return i != null ? i : 1;
    }

    /**
     * -1 means undefined. 0 means only generate
     *
     * @param numberSimultaneousRepeatsGeneratedAhead
     */
    public void setNumberSimultaneousRepeats(int numberSimultaneousRepeatsGeneratedAhead) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (this.numberOfRepeatsGeneratedAhead != numberFutureRepeatsGeneratedAhead) {
//            if (numberFutureRepeatsGeneratedAhead != 0) {
//                this.numberOfDaysRepeatsAreGeneratedAhead = 0;
//            }
//            this.numberOfRepeatsGeneratedAhead = numberFutureRepeatsGeneratedAhead;
//            changed();
//        }
//        if (getNumberFutureRepeatsToGenerateAhead() != numberFutureRepeatsGeneratedAhead) {
//            if (numberFutureRepeatsGeneratedAhead != 0) {
//                setNumberOfDaysRepeatsAreGeneratedAhead(0);
//            }
//            put(NUMBER_FUTURE_REPEATS_TO_GENERATE_AHEAD, numberFutureRepeatsGeneratedAhead);
//</editor-fold>
//            setNumberOfDaysRepeatsAreGeneratedAhead(0);
        if (numberSimultaneousRepeatsGeneratedAhead > 1) {
            put(PARSE_NUMBER_SIMULTANEOUS_REPEATS_TO_GENERATE_AHEAD, numberSimultaneousRepeatsGeneratedAhead);
        } else {
            remove(PARSE_NUMBER_SIMULTANEOUS_REPEATS_TO_GENERATE_AHEAD);
        }
    }

    /**
     *
     * @param numberOfDaysRepeatsAreGeneratedAhead
     */
    public void setNumberOfDaysRepeatsAreGeneratedAhead(int numberOfDaysRepeatsAreGeneratedAhead) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (this.numberOfDaysRepeatsAreGeneratedAhead != numberOfDaysRepeatsAreGeneratedAhead) {
//            if (numberOfDaysRepeatsAreGeneratedAhead != 0) {
//                this.numberOfRepeatsGeneratedAhead = 0; //reset this value to zero to ensure only one is set at any time
//            }
//            this.numberOfDaysRepeatsAreGeneratedAhead = numberOfDaysRepeatsAreGeneratedAhead;
//            changed();
//        }
//        if (getNumberOfDaysRepeatsAreGeneratedAhead() != numberOfDaysRepeatsAreGeneratedAhead) {
//        if (numberOfDaysRepeatsAreGeneratedAhead != 0) {
//            setNumberFutureRepeatsToGenerateAhead(0); //reset this value to zero to ensure only one is set at any time
//        }
//        put(NUMBER_OF_DAYS_REPEATS_ARE_GENERATED_AHEAD, numberOfDaysRepeatsAreGeneratedAhead);
//</editor-fold>
        if (numberOfDaysRepeatsAreGeneratedAhead != 0) {
            put(PARSE_NUMBER_OF_DAYS_TO_GENERATE_AHEAD, numberOfDaysRepeatsAreGeneratedAhead);
//            setNumberSimultaneousRepeats(1); //reset this value //done in ScreenRR
        } else {
            remove(PARSE_NUMBER_OF_DAYS_TO_GENERATE_AHEAD);
        }
    }

    /**
     * defines how many days ahead the repeat rule should generate instances,
     * e.g. 14 means create all instances for the next 2 weeks
     */
    public int getNumberOfDaysRepeatsAreGeneratedAhead() {
//        return numberOfDaysRepeatsAreGeneratedAhead;
//        return getInt(NUMBER_OF_DAYS_REPEATS_ARE_GENERATED_AHEAD);
        Integer i = getInt(PARSE_NUMBER_OF_DAYS_TO_GENERATE_AHEAD);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;
    }

    public void setLatestDateCompletedOrCancelled(Date newDateOfCompleted) {
        if (newDateOfCompleted != null && newDateOfCompleted.getTime() != 0) {
            put(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED_XXX, newDateOfCompleted);
        } else {
            remove(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED_XXX);
        }
    }

    /**
     * keep track of the latest/most recent (biggest in calendar terms) Due date
     * of completed/cancelled tasks (used when recalculating repeat instances in
     * a *modified* rule). Even if tasks are completed/cancelled out of order,
     * only the latest date is stored.
     *
     * @return
     */
    private void setLatestDateCompletedOrCancelledIfGreaterThanLast(Date newDateOfCompleted) {
//        Date lastDateOfCompleted = getDate(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
        Date latestDateCompletedOrCancelled = getLatestDateDone();
//        Date lastDateOfCompleted = getLatestDateCompletedOrCancelled();
        if (latestDateCompletedOrCancelled.getTime() == 0 || newDateOfCompleted.getTime() > latestDateCompletedOrCancelled.getTime()) {
            setLatestDateCompletedOrCancelled(newDateOfCompleted);
        }
    }

    private void updateWithCompletedItem(Item completedItem) {
        if (false) {
            setLatestDateCompletedOrCancelledIfGreaterThanLast(completedItem.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
        }
        ASSERT.that(completedItem.isDone(), "update called with item which is NOT completed/done");

        List undoneInstances = getListOfUndoneInstances();
        undoneInstances.remove(completedItem);
        setListOfUndoneInstances(undoneInstances);

        List doneInstances = getListOfDoneInstances();
        doneInstances.add(completedItem);
        setListOfDoneInstances(doneInstances);

        if (false) {
            int countCompleted = getTotalNumberOfDoneInstances();
//        countCompleted++;
            setTotalNumberOfDoneInstancesZZZ(countCompleted);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Date getNextRepeatFromDate(Item completedItem) {
//        Date nextRepeatTime;
//        if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) {
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(true)); //get next date
//            nextRepeatTime = getNextCompletedFromDate(completedItem.getRepeatStartTime(true)); //get next date
////                ASSERT.that(repeatInstanceItemList.size() <= 1, "Error: Repeat from Completed - too many instances in listOfUndoneRepeatInstances=" + repeatInstanceItemList);
//        } else {
//            if (Config.TEST) {
//                ASSERT.that(getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE);
//            }
////            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(false)); //get next date
//            nextRepeatTime = getNextDueDateN(); //get next date
//        }
//        return nextRepeatTime;
//    }
//</editor-fold>
    private void addNewRepeatInstanceToUndone(Item newRepeatInstance) {
        List repeatInstanceItemList = getListOfUndoneInstances();
        repeatInstanceItemList.add(newRepeatInstance); //only keep track of instances when Due
        setListOfUndoneInstances(repeatInstanceItemList);

//        setLastGeneratedDate(newRepeatInstance.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE)); //TODO!!!! doesn't make sense to update this for RepeatFromCompleted
//        setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
    }

//    private void setLatestDateCompletedOrCancelledIfGreaterThanLastOLD(Date newDateOfCompleted) {
//        Date lastDateOfCompleted = getDate(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
////        Date lastDateOfCompleted = getLatestDateCompletedOrCancelled();
//        if (lastDateOfCompleted == null || newDateOfCompleted.getTime() > lastDateOfCompleted.getTime()) {
//            put(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED, newDateOfCompleted);
//        } else {
//            remove(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
//        }
//    }
    /**
     * return the latest getRepeatStartTime from the elements
     *
     * @param elements
     * @param fromCompletedDate
     * @return
     */
    private static Date getLatestDate(List<RepeatRuleObjectInterface> elements, boolean fromCompletedDate) {
        RepeatRuleObjectInterface latestElement = getLatestN(elements, fromCompletedDate);
        return latestElement != null ? latestElement.getRepeatStartTime(fromCompletedDate) : new MyDate(MyDate.MIN_DATE);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private static RepeatRuleObjectInterface getLatest(List<RepeatRuleObjectInterface> elements, boolean fromCompletedDate) {
//        Date latestDate = new MyDate(MyDate.MIN_DATE);
////        List<RepeatRuleObjectInterface> elements = getListOfDoneInstances();
////optimization: assume elements are sorted by time, so just return the last (would require sorting, except maybe for WorkSlots which will always be processed by time)?!
//        for (RepeatRuleObjectInterface r : elements) {
//            Date date = r.getRepeatStartTime(fromCompletedDate);
//            if (date.getTime() > latestDate.getTime()) {
//                latestDate = date;
//            }
//        }
//        return latestDate;
//    }
//</editor-fold>

    private static RepeatRuleObjectInterface getLatestN(List<RepeatRuleObjectInterface> elements, boolean fromCompletedDate) {
//        Date latestDate = new MyDate(MyDate.MIN_DATE);
        RepeatRuleObjectInterface latestElement = null;
        //optimization: assume elements are sorted by time, so just return the last (would require sorting, except maybe for WorkSlots which will always be processed by time)?!
        for (int i = elements.size() - 1; i >= 0; i--) {
            RepeatRuleObjectInterface r = elements.get(i);
            if (latestElement == null) {
                latestElement = r;
            } else {
                if (Config.TEST) {
                    ASSERT.that(r.getRepeatStartTime(fromCompletedDate).getTime() <= latestElement.getRepeatStartTime(fromCompletedDate).getTime(),
                            "error: last element in last is NOT latest, i=" + i + "; r=" + r + "; latestElement" + latestElement);
                }
                if (r.getRepeatStartTime(fromCompletedDate).getTime() > latestElement.getRepeatStartTime(fromCompletedDate).getTime()) {
                    latestElement = r;
                }
            }
        }
        return latestElement;
    }

    private Date getLatestDateDone(boolean fromCompletedDate) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Date lastDateOfCompleted = getDate(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
//        return lastDateOfCompleted != null ? lastDateOfCompleted : new Date(0);
//        Date latestDoneDate = new MyDate(MyDate.MIN_DATE);
//        List<RepeatRuleObjectInterface> dones = getListOfDoneInstances();
//        for (RepeatRuleObjectInterface r : dones) {
//            Date date = r.getRepeatStartTime(false);
//            if (date.getTime() > latestDoneDate.getTime()) {
//                latestDoneDate = date;
//            }
//        }
//        return latestDoneDate;
//</editor-fold>
        return getLatestDate(getListOfDoneInstances(), fromCompletedDate);
    }

    private Date getLatestDateDone() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Date lastDateOfCompleted = getDate(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
//        return lastDateOfCompleted != null ? lastDateOfCompleted : new Date(0);
//        Date latestDoneDate = new MyDate(MyDate.MIN_DATE);
//        List<RepeatRuleObjectInterface> dones = getListOfDoneInstances();
//        for (RepeatRuleObjectInterface r : dones) {
//            Date date = r.getRepeatStartTime(false);
//            if (date.getTime() > latestDoneDate.getTime()) {
//                latestDoneDate = date;
//            }
//        }
//        return latestDoneDate;
//</editor-fold>
        return getLatestDateDone(false);
    }

    private Date getLatestDateUndone(boolean fromCompletedDate) {
        return getLatestDate(getListOfUndoneInstances(), fromCompletedDate);
    }

    private Date getLatestDateUndone() {
        return getLatestDateUndone(false);
    }

    private Date getLatestDueDateDoneOrUndone() {
        return new MyDate(Math.max(getLatestDateDone(false).getTime(), getLatestDateUndone(false).getTime()));
    }

    private static Date getLatestDueDate(List<RepeatRuleObjectInterface> list1, List<RepeatRuleObjectInterface> list2) {
        return new MyDate(Math.max(getLatestDate(list1, false).getTime(), getLatestDate(list2, false).getTime()));
    }

    private static RepeatRuleObjectInterface getLatest(List<RepeatRuleObjectInterface> list1, List<RepeatRuleObjectInterface> list2) {
        return getLatestN(list1, list2, false);
    }

    private static RepeatRuleObjectInterface getLatestN(List<RepeatRuleObjectInterface> list1, List<RepeatRuleObjectInterface> list2, boolean fromCompletedDate) {
        RepeatRuleObjectInterface d1 = getLatestN(list1, fromCompletedDate);
        RepeatRuleObjectInterface d2 = getLatestN(list2, fromCompletedDate);
        return d1 == null ? d2
                : (d2 == null ? d1
                        : (d1.getRepeatStartTime(fromCompletedDate).getTime() > d2.getRepeatStartTime(fromCompletedDate).getTime() ? d1 : d2));
    }

    private Date getLatestDateGenerated(boolean fromCompletedDate) {
        return new MyDate(Math.max(getLatestDateDone(fromCompletedDate).getTime(), getLatestDateUndone(fromCompletedDate).getTime()));
    }
//    private Date getLatestDateGenerated() {
//        return new MyDate(Math.max(getLatestDateDone().getTime(), getLatestDateUndone().getTime()));
//    }

    /**
     * date up till which dates have been generated so far (stored in
     * datesList). Used inside MyRepeatRule to keep track of the latest date for
     * which instances have been created (instead of e.g. just using the latest
     * date stored in the list of generated instances). This is used to ensure
     * that e.g. deleted or done instances do not re-appear
     */
//    public Date getLastGeneratedDateDXXX() {
////<editor-fold defaultstate="collapsed" desc="comment">
////        Date date = getDate(PARSE_LAST_DATE_GENERATED);
////        return (date == null) ? new Date(MyDate.MIN_DATE) : date;
////        List generatedInstances = getListOfUndoneRepeatInstances();
////        if (generatedInstances.size() == 0) {
//////            return new Date(MyDate.MIN_DATE);
////            return getSpecifiedStartDateD();
////        } else {
////            return ((RepeatRuleObjectInterface) generatedInstances.get(generatedInstances.size() - 1)).getRepeatStartTime(false);
////        }
////</editor-fold>
////optimization: calculate as we go along? or sort long lists first?
//        Date lastDate = new MyDate(MyDate.MIN_DATE);
//        //also include done instances in case e.g. the last instance was completed first --> UI: side effect: if an earlier instance was set to a later date than the last instance and then closed, that date would be used
//        List<RepeatRuleObjectInterface> done = getListOfDoneInstances();
//        for (RepeatRuleObjectInterface t : done) {
//            Date d = t.getRepeatStartTime(false);
//            if (d.getTime() > lastDate.getTime()) {
//                lastDate = d;
//            }
//        }
//
//        List<RepeatRuleObjectInterface> undone = getListOfUndoneInstances();
//        for (RepeatRuleObjectInterface t : undone) {
//            Date d = t.getRepeatStartTime(false);
//            if (d.getTime() > lastDate.getTime()) {
//                lastDate = d;
//            }
//        }
//        return lastDate;
//    }
    /**
     * used to keep track of when a repeatrule has repeated the indicated number
     * of times (to stop the repeat then).
     *
     * @return
     */
    public int getTotalNumberOfInstancesGeneratedSoFar() {
//        Integer i = getInt(PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR);
//        return i != null ? i : 0;
        return getTotalNumberOfDoneInstances() + getListOfUndoneInstances().size();
    }

    /**
     *
     * @param countOfInstancesGeneratedSoFar
     */
//    private void setTotalNumberOfInstancesGeneratedSoFarXXX(int countOfInstancesGeneratedSoFar) {
//        if (countOfInstancesGeneratedSoFar != 0) {
//            put(PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR, countOfInstancesGeneratedSoFar);
//        } else {
//            remove(PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR);
//        }
//    }
    int getTotalNumberOfDoneInstances() {
//        Integer i = getInt(PARSE_COUNT_OF_INSTANCES_DONE_SO_FAR);
//        return i != null ? i : 0;
        return getListOfDoneInstances().size();
    }

    int getTotalNumberOfUndoneInstances() {
//        Integer i = getInt(PARSE_COUNT_OF_INSTANCES_DONE_SO_FAR);
//        return i != null ? i : 0;
        return getListOfUndoneInstances().size();
    }

    /**
     *
     * @param countOfInstancesDoneSoFar
     */
    private void setTotalNumberOfDoneInstancesZZZ(int countOfInstancesDoneSoFar) {
        if (countOfInstancesDoneSoFar != 0) {
            put(PARSE_COUNT_OF_INSTANCES_DONE_SO_FAR_XXX, countOfInstancesDoneSoFar);
        } else {
            remove(PARSE_COUNT_OF_INSTANCES_DONE_SO_FAR_XXX);
        }
    }

    static int[] getRepeatRuleFrequencyNumbers() {
        return REPEAT_RULE_FREQUENCY_NUMBERS;
    }

    static Object[] getRepeatRuleFrequencyNumbersAsObjects() {
        return REPEAT_RULE_FREQUENCY_NUMBERS_AS_OBJECTS;
    }

    static String[] getRepeatRuleFrequencyNames() {
        return REPEAT_RULE_FREQUENCY_NAMES;
    }

    static int[] getRepeatRuleTypeNumbers() {
        return REPEAT_RULE_TYPE_NUMBERS;
    }

    static Object[] getRepeatRuleTypeNumbersAsObjects() {
        return REPEAT_RULE_TYPE_NUMBERS_AS_OBJECTS;
    }

    static String[] getRepeatRuleTypeNames() {
        return REPEAT_RULE_TYPE_NAMES;
    }

    /**
     * returns list of display text strings indicating week in month (FIRST,
     * THIRD, LAST, ...)
     */
    static String[] getWeekInMonthNames() {
        return WEEKS_IN_MONTH_NAMES;
    }

    static String[] getWeekInMonthNamesShort() {
        return WEEKS_IN_MONTH_NAMES_SHORT;
    }

    static Object[] getWeekInMonthNumbers() {
        return WEEKS_IN_MONTH_NUMBERS;
    }

    static Object[] getWeekInMonthNumbersShort() {
        return WEEKS_IN_MONTH_NUMBERS_SHORT;
    }

//    Vector getWeekInMonthNamesAsVector() {
////        return ItemList.createVector(WEEKS_IN_MONTH_NAMES);
//        return new Vector(WEEKS_IN_MONTH_NAMES);
//    }
    static Object[] getDayInWeekNumbers() {
        return DAY_IN_WEEK_NUMBERS_MONDAY_FIRST;
    }

    static Object[] getDayInWeekNumbersInclWeekdays() {
        return DAY_IN_WEEK_NUMBERS_MONDAY_FIRST_INCL_WEEKDAYS;
    }

    static Object[] getMonthInYearNumbers() {
        return MONTH_IN_YEAR_NUMBERS;
    }

    public boolean hasSaveableData() {
        return getRepeatType() != REPEAT_TYPE_NO_REPEAT;
    }

    private void setTest(Date start, int repeatType, int frequency, int interval, int intOptions, boolean genDaysAhead, int daysOrInstances, int foreverUntilNumber, Object untilOrNumber) {
        setSpecifiedStartDateXXXZZZ(start); // eg REPEAT_TYPE_FROM_COMPLETED_DATE
        setRepeatType(repeatType); // eg REPEAT_TYPE_FROM_COMPLETED_DATE
        setFrequency(frequency);
        setInterval(interval);
        if (frequency == RepeatRule.WEEKLY) {
            setDaysInWeek(intOptions);
        } else if (frequency == RepeatRule.MONTHLY) {
            setDayInMonth(intOptions);
        } else if (frequency == RepeatRule.YEARLY) {
            setDayInYear(intOptions);
        }
        if (genDaysAhead) {
            setNumberOfDaysRepeatsAreGeneratedAhead(daysOrInstances);
            setNumberSimultaneousRepeats(1); //reset to default value
        } else {
            setNumberSimultaneousRepeats(daysOrInstances);
            setNumberOfDaysRepeatsAreGeneratedAhead(0);
        }
        if (foreverUntilNumber == ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_UNTIL) {
            setEndDate((Date) untilOrNumber);
            setNumberOfRepeats(Integer.MAX_VALUE);
        } else if (foreverUntilNumber == ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_NUMBER) {
            setNumberOfRepeats((Integer) untilOrNumber);
            setEndDate(new MyDate(MyDate.MAX_DATE));
        } else if (foreverUntilNumber == ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_FOREVER) {
            setNumberOfRepeats(Integer.MAX_VALUE);
            setEndDate(new MyDate(MyDate.MAX_DATE));
        }
    }

    private static Date makeDate(int day, int month, int year, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour); //set hour to 1 to avoid pbs with daylight saving changes
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private static void runTest(Date start, int repeatType, int frequency, int interval, int intOptions, boolean genDaysAhead, int daysOrInstances, int foreverUntilNumber, Object untilOrNumber) {
        RepeatRuleParseObject rule = new RepeatRuleParseObject();
        rule.setTest(start, repeatType, frequency, interval, intOptions, genDaysAhead, daysOrInstances, foreverUntilNumber, untilOrNumber);
        Log.p(rule.toString());
        Log.p(rule.getRepeatRule().toString());
//        rule.listDatesTest(10);
        Log.p("------------------------------------------");
    }

    public static boolean testRepeatRules() {
        Date start = makeDate(1, 8, 2017, 23, 10);
        Date end = makeDate(1, 10, 2017, 23, 10);
//(Date start, int fromDue, int frequency, int interval, int intOptions, boolean genDaysAhead, int daysOrInstances, int foreverUntilNumber, Object untilOrNumber)
        runTest(start, REPEAT_TYPE_FROM_COMPLETED_DATE, RepeatRule.WEEKLY, 1, RepeatRule.WEDNESDAY, true, 5, ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_FOREVER, null);
        runTest(start, REPEAT_TYPE_FROM_COMPLETED_DATE, RepeatRule.WEEKLY, 1, RepeatRule.WEDNESDAY, true, 5, ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_NUMBER, 4);
        runTest(start, REPEAT_TYPE_FROM_COMPLETED_DATE, RepeatRule.WEEKLY, 1, RepeatRule.WEDNESDAY, true, 5, ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_UNTIL, end);

        return false;
    }

    public void setSoftDeletedDate(Date dateDeleted) {
        if (dateDeleted != null && dateDeleted.getTime() != 0) {
            put(PARSE_DELETED_DATE, dateDeleted);
        } else {
            remove(PARSE_DELETED_DATE); //delete when setting to default value
        }
    }

    public Date getSoftDeletedDate() {
        Date date = getDate(PARSE_DELETED_DATE);
//        return (date == null) ? new Date(0) : date;
        return date; //return null to indicate NOT deleted
    }

    public boolean isSoftDeleted() {
        return getSoftDeletedDate() != null;
    }

    public boolean delete(Date deleteDate) {
//            updateRepeatInstancesOnDoneCancelOrDelete(this); //UI: if you delete (like if you cancel) a repeating task, new instances will be generated as necessary (just like if it is marked done) - NB. Also necessary to ensure that the repeatrule 'stays alive' and doesn't go stall because all previously generated instances were cancelled/deleted...
        if (getListOfDoneInstances().size() == 0 && getListOfUndoneInstances().size() == 0) { //if no links to tasks (and thus no links from tasks to repeatRules) we can hard-delete the RR
            DAO.getInstance().delete(this, true, false);
        } else {
            setSoftDeletedDate(deleteDate);
            DAO.getInstance().delete((ParseObject) this, false, false);
        }
//            DAO.getInstance().saveNew(myRepeatRule);
        return true;
    }

    /**
     *
     * @param repeatType REPEAT_TYPE_FROM_DUE_DATE
     * @param frequency RepeatRule.WEEKLY
     * @param interval
     * @param daysInWeek {RepeatRule.MONDA | RepeatRule.TUESDAY,
     * RepeatRule.WEDNESDAY, RepeatRule.THURSDAY, RepeatRule.FRIDAY,
     * RepeatRule.SATURDAY, RepeatRule.SUNDAY};
     */
    public static RepeatRuleParseObject createTestDated(int repeatType, int frequency, int interval, int daysInWeek, int numberRepeats, Date endDate,
            int nbSimult) {
        RepeatRuleParseObject r = new RepeatRuleParseObject();
        r.setRepeatType(repeatType);
        switch (repeatType) {
            case REPEAT_TYPE_FROM_DUE_DATE:
                break;
            case REPEAT_TYPE_FROM_COMPLETED_DATE:
                break;
        }
        r.setFrequency(frequency);
        r.setInterval(interval);
        switch (frequency) {
            case RepeatRule.DAILY:
                break;
            case RepeatRule.WEEKLY:
                r.setDaysInWeek(daysInWeek);
                break;
            case RepeatRule.MONTHLY:
                break;
            case RepeatRule.YEARLY:
                break;
            case REPEAT_TYPE_FROM_COMPLETED_DATE:
                break;
        }
        if (numberRepeats > 0) {
            r.setNumberOfRepeats(numberRepeats);
        } else if (endDate != null) {
            r.setEndDate(endDate);
        }
        if (nbSimult > 0) {
            r.setNumberSimultaneousRepeats(nbSimult);
        }
        return r;
    }

    public static RepeatRuleParseObject updateTestDated(RepeatRuleParseObject r, int repeatType, int frequency, int interval, int daysInWeek, int numberRepeats, Date endDate) {
        if (repeatType != -1) {
            r.setRepeatType(repeatType);
        }
        if (frequency != -1) {
            r.setFrequency(frequency);
        }
        if (interval != -1) {
            r.setInterval(interval);
        }
        switch (frequency) {
            case RepeatRule.DAILY:
                break;
            case RepeatRule.WEEKLY:
                if (daysInWeek != -1) {
                    r.setDaysInWeek(daysInWeek);
                }
                break;
            case RepeatRule.MONTHLY:
                break;
            case RepeatRule.YEARLY:
                break;
            case REPEAT_TYPE_FROM_COMPLETED_DATE:
                break;
        }
        if (numberRepeats > 0) {
            r.setNumberOfRepeats(numberRepeats);
        }
        if (endDate != null) {
            r.setEndDate(endDate);
        }
        return r;
    }

    /**
     * has any of the RepeatRule's data that affect the dates it repeats on been
     * changed? Set in each setter which affects the date sequence
     *
     * @return
     */
    boolean isDatePatternChanged() {
        return datePatternChanged;
    }

    void setDatePatternChanged(boolean datePatternChanged) {
        this.datePatternChanged = datePatternChanged;
    }

    private boolean updateIsPending; //=true; //by default a new rule always need to be calculated(?)

    public void setUpdatePending(boolean updateIsPending) {
        this.updateIsPending = updateIsPending;
    }

    /**
     * true if the repeatRule has been edited, but the repeat instances have not
     * been recalculated
     *
     * @return
     */
    public boolean isUpdatePending() {
        return updateIsPending;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void receiveChangeEvent(ChangeEvent changeEvent) {
//
////        Object changedObject = changeEvent.getListObject();
////#mdebug
//        ASSERT.that(changeEvent.getListObject() instanceof RepeatRuleObject, "Error in MyRepeatRule.receiveChangeEvent [rule=" + this + "]: changeObject not of type RepeatRuleObject, changeEvent=" + changeEvent);
////#enddebug
//        ItemList changedItemList = (ItemList) changeEvent.getSource();
//        if (changedItemList == repeatInstanceItemList) {
//            RepeatRuleObject changedObject = (RepeatRuleObject) changeEvent.getListObject();
//            int changeType = changeEvent.getChangeId();
//
////#mdebug
//            ASSERT.that((changeType != ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED || repeatInstanceItemList.getItemIndex(changedObject) == -1), "changeEvent=" + changeEvent + " received from object not in repeatInstanceItemList, changedObject=" + changedObject + " repeatInstanceItemList=" + repeatInstanceItemList);
////#enddebug
////            ASSERT.that(repeatInstanceItemList.getItemIndex(changedObject) != -1, "changeEvent=" + changeEvent + " received from object not in repeatInstanceItemList, changedObject=" + changedObject + " repeatInstanceItemList=" + repeatInstanceItemList);
//
////            if (changeType == ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED // Item was removed from list (deleted)
////                    || (changeType == ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED && changedObject.isDoneOrExpired())) {
//            if (ChangeValue.isSetEither(changeType, ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED, // Item was removed from list (deleted)
//                    ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED)) {
////                this.updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(true, changedObject, changedObject.getListForNewCreatedRepeatInstances());
//                if (changedObject.isNoLongerRelevant()) {
//                    this.updateRepeatInstancesOnDone(true, changedObject);
//                }
//                ASSERT.that(ChangeValue.isSetEither(changeType, ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED),
//                        "MyRepeatRule.receiveChangeEvent: Unexpected #changeEvent=" + changeEvent + " received, #item=" + changedObject + " #rule=" + this);
////            } else {
////                Log.that(false, "Error in MyRepeatRule.receiveChangeEvent [rule=" + this + "]: unknown object type received = " + changedObject);
//            }
//        } else {
//            ASSERT.that("Error in MyRepeatRule.receiveChangeEvent [rule=" + this + "]: changeEvent received from wrong list= " + changeEvent.getSource() + "; object=" + changeEvent.getListObject());
//        }
//    }
//</editor-fold>
}

// <editor-fold defaultstate="collapsed" desc="comment">
//    Vector xxxgetMonths() {
//        return ItemList.createVector(MyDate.MONTH_NAMES);
//    }
//
//    Vector xxxgetMonthsSet() {
//        Vector monthsSet = new Vector(12);
//        if ((monthsInYear & RepeatRule.JANUARY) != 0) {
//            monthsSet.addElement(MyDate.JANUARY);
//        }
//        if ((monthsInYear & RepeatRule.FEBRUARY) != 0) {
//            monthsSet.addElement(MyDate.FEBRUARY);
//        }
//        if ((monthsInYear & RepeatRule.MARCH) != 0) {
//            monthsSet.addElement(MyDate.MARCH);
//        }
//        if ((monthsInYear & RepeatRule.APRIL) != 0) {
//            monthsSet.addElement(MyDate.APRIL);
//        }
//        if ((monthsInYear & RepeatRule.MAY) != 0) {
//            monthsSet.addElement(MyDate.MAY);
//        }
//        if ((monthsInYear & RepeatRule.JUNE) != 0) {
//            monthsSet.addElement(MyDate.JUNE);
//        }
//        if ((monthsInYear & RepeatRule.JULY) != 0) {
//            monthsSet.addElement(MyDate.JULY);
//        }
//        if ((monthsInYear & RepeatRule.AUGUST) != 0) {
//            monthsSet.addElement(MyDate.AUGUST);
//        }
//        if ((monthsInYear & RepeatRule.SEPTEMBER) != 0) {
//            monthsSet.addElement(MyDate.SEPTEMBER);
//        }
//        if ((monthsInYear & RepeatRule.OCTOBER) != 0) {
//            monthsSet.addElement(MyDate.OCTOBER);
//        }
//        if ((monthsInYear & RepeatRule.NOVEMBER) != 0) {
//            monthsSet.addElement(MyDate.NOVEMBER);
//        }
//        if ((monthsInYear & RepeatRule.DECEMBER) != 0) {
//            monthsSet.addElement(MyDate.DECEMBER);
//        }
//        return monthsSet;
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    Vector xxxgetDaysSet() {
//        Vector daysSet = new Vector(7);
//        if ((daysInWeek & RepeatRule.MONDAY) != 0) {
//            daysSet.addElement(MyDate.MONDAY);
//        }
//        if ((daysInWeek & RepeatRule.TUESDAY) != 0) {
//            daysSet.addElement(MyDate.TUESDAY);
//        }
//        if ((daysInWeek & RepeatRule.WEDNESDAY) != 0) {
//            daysSet.addElement(MyDate.WEDNESDAY);
//        }
//        if ((daysInWeek & RepeatRule.THURSDAY) != 0) {
//            daysSet.addElement(MyDate.THURSDAY);
//        }
//        if ((daysInWeek & RepeatRule.FRIDAY) != 0) {
//            daysSet.addElement(MyDate.FRIDAY);
//        }
//        if ((daysInWeek & RepeatRule.SATURDAY) != 0) {
//            daysSet.addElement(MyDate.SATURDAY);
//        }
//        if ((daysInWeek & RepeatRule.SUNDAY) != 0) {
//            daysSet.addElement(MyDate.SUNDAY);
//        }
//        return daysSet;
//    }
//
//    Vector xxxgetWeeksSet() {
//        Vector weeksSet = new Vector(12);
//        if ((weeksInMonth & RepeatRule.FIRST) != 0) {
//            weeksSet.addElement(FIRST);
//        }
//        if ((weeksInMonth & RepeatRule.SECOND) != 0) {
//            weeksSet.addElement(SECOND);
//        }
//        if ((weeksInMonth & RepeatRule.THIRD) != 0) {
//            weeksSet.addElement(THIRD);
//        }
//        if ((weeksInMonth & RepeatRule.FOURTH) != 0) {
//            weeksSet.addElement(FOURTH);
//        }
//        if ((weeksInMonth & RepeatRule.FIFTH) != 0) {
//            weeksSet.addElement(FIFTH);
//        }
//        if ((weeksInMonth & RepeatRule.LAST) != 0) {
//            weeksSet.addElement(LAST);
//        }
//        if ((weeksInMonth & RepeatRule.SECONDLAST) != 0) {
//            weeksSet.addElement(SECOND_LAST);
//        }
//        if ((weeksInMonth & RepeatRule.THIRDLAST) != 0) {
//            weeksSet.addElement(THIRD_LAST);
//        }
//        if ((weeksInMonth & RepeatRule.FOURTHLAST) != 0) {
//            weeksSet.addElement(FOURTH_LAST);
//        }
//        if ((weeksInMonth & RepeatRule.FIFTHLAST) != 0) {
//            weeksSet.addElement(FIFTH_LAST);
//        }
//        return weeksSet;
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    void generateOrUpdateInstances(Item repeatSourceItem, long start, long subsetBeginning, long subsetEnding) {
//        if (repeatSourceItem == null) {
//            this.repeatSourceItem = repeatSourceItem;
//        }
//        Vector repeatInstances = new Vector();
//        RepeatRule repeatRule = new RepeatRule();
//        Enumeration repeatDates;
//
//        repeatDates = repeatRule.dates(start, subsetBeginning, subsetEnding);
//
//        int maxInstances = Settings.getInstance().getMaxRecurringTaskInstances();
//        while (repeatDates.hasMoreElements() && maxInstances > 0) {
//            Date date = (Date) repeatDates.nextElement();
//            Item itemClone = (Item) repeatSourceItem.clone();
//            itemClone.setDueDate(date.getRepeatStartTime());
//            if (Settings.getInstance().adjustRecurringInstanceAlarmToSameIntervalAsSource()) {
//                if (repeatSourceItem.getAlarmDate() != 0L) {
//                    long alarmInterval = repeatSourceItem.getDueDate() - repeatSourceItem.getAlarmDate();
//                    itemClone.setAlarmDate(itemClone.getDueDate() - alarmInterval); //UI:
//                }
//            }
//            itemClone.setRepeatRule(this);
////            itemClone.setRepeatSourceItem(item); //-obtained via item instance's link to RepeatRule
//            itemClone.commit();
//            repeatInstances.addElement(itemClone);
//            maxInstances--;
//        }
//        if (this.repeatInstances == null) {
//            this.repeatInstances = repeatInstances;
//        } else { //instances already exist
//            this.repeatInstances = repeatInstances;
//            try {
//                throw new Exception("Missing");
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
////        return repeatInstances;
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    private void setValuesFromBits(RepeatRule repeatRule, int repeatRuleField, int bitValues) {
//        int bitValue = 1;
//        for (int i = 0, size = 32; i < size; i++) { //TODO: optimize to skip leading and trailing '0'
//            if ((bitValue & bitValues) != 0) {
//                repeatRule.setInt(repeatRuleField, (bitValue | bitValues));
//            }
//            bitValue = bitValue << 1;
//        }
//    }
//    private void setDayInWeek(RepeatRule repeatRule) {
//        setValuesFromBits(repeatRule, RepeatRule.DAY_IN_WEEK, getDaysInWeek());
//    }
//
//    private void setWeekInMonth(RepeatRule repeatRule) {
//        setValuesFromBits(repeatRule, RepeatRule.WEEK_IN_MONTH, getWeekInMonth());
//    }
//
//    private void setMonthInYear(RepeatRule repeatRule) {
//        setValuesFromBits(repeatRule, RepeatRule.MONTH_IN_YEAR, getMonthInYear());
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
/**
 * returns a RepeatRule set with all fields defined by MyRepeatRule
 */
//    RepeatRule getRepeatRule() {
//        RepeatRule repeatRule = new RepeatRule();
//        repeatRule.setInt(RepeatRule.FREQUENCY, getFrequency());
//        if (endDate != 0L) {
//            repeatRule.setDate(RepeatRule.END, getEndDate());
//        } else if (count > 0) {
//            repeatRule.setInt(RepeatRule.COUNT, getCount());
//        }
//        if (interval > 0) {
//            repeatRule.setInt(RepeatRule.INTERVAL, getInterval());
//        }
//        switch (frequency) {
//            case RepeatRule.DAILY:
//                break;
//            case RepeatRule.WEEKLY:
//                setDayInWeek(repeatRule);
//                break;
//            case RepeatRule.MONTHLY:
//                setDayInWeek(repeatRule);
//                setWeekInMonth(repeatRule);
//                setWeekInMonth(repeatRule);
//                break;
//            case RepeatRule.YEARLY:
//                setDayInWeek(repeatRule);
//                if (getDayInYear() != 0) {
//                    repeatRule.setInt(RepeatRule.DAY_IN_YEAR, getDayInYear());
//                } else {
//                    if (getMonthInYear() != 0) {
//                        setMonthInYear(repeatRule);
//                    }
//                    if (getWeekInMonth() != 0) {
//                        setWeekInMonth(repeatRule);
//                    }
//                    break;
//                }
//        }
//        return repeatRule;
//    }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void generateRepeatInstances(RepeatRuleObjectInterface repeatRuleOriginator, ItemList motherListForNewInstances) {
//        if (isDirty()) {  //only recalculate if the rule is new or has been changed
//            // || !isCommitted()) { //-not possible to be both committed() and dirty
////            repeatRuleNewInstancesCalculationOngoing = true;
//            datesList = new DateBuffer(); //
////            RepeatInstanceItemList newRepeatInstanceList = new RepeatInstanceItemList(this); //hold new created instances
////            ItemList newRepeatInstanceList = new ItemList(); //hold new created instances
//            Vector newRepeatInstanceList = new Vector(); //hold new created instances
////            newRepeatInstanceList.addItem(repeatRuleOriginator); //always add the originator
//            newRepeatInstanceList.addElement(repeatRuleOriginator); //always add the originator
////            repeatInstanceVector.getAndRemoveRepeatInstanceWithDate(nextRepeatTime);
//            RepeatRuleObjectInterface repeatInstance;
//            long now = MyDate.getNow();
//            long nextRepeatTime;
////            while (datesList.hasMoreDates()
////                    //                    && (getNumberFutureRepeatsGeneratedAhead() == 0 || newRepeatInstanceList.size() < getNumberFutureRepeatsGeneratedAhead() + 1) //+1 since we want to show both originator and numberOfFuture instances at the same time
////                    && (getNumberFutureRepeatsGeneratedAhead() == 0 || newRepeatInstanceList.size() < getNumberFutureRepeatsGeneratedAhead()) //+1 since we want to show both originator and numberOfFuture instances at the same time
////                    && (getNumberOfDaysRepeatsAreGeneratedAhead() == 0 || datesList.getNextDate() <= now + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS)) {
//            while (datesList.hasMoreDates()
//                    && ((getNumberFutureRepeatsGeneratedAhead() != 0 && newRepeatInstanceList.size() < getNumberFutureRepeatsGeneratedAhead()) //+1 since we want to show both originator and numberOfFuture instances at the same time
//                    || (getNumberOfDaysRepeatsAreGeneratedAhead() != 0 && (datesList.getNextDate() <= now + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS) //we generate some days ahead, generate for all datea that are less than the number of days we generate ahead
//                    //                    || (newRepeatInstanceList.size() == 0) //??should never be the case?? //no need to generate at least one item, since the 'owner' of the repeat rule will exist
//                    ))) {
////                nextRepeatTime = datesList.getNextDate();
//                nextRepeatTime = datesList.takeNext().getTime(); //remove next date from list
//                if (Settings.getInstance().reuseExistingInstancesAfterChangingRepeatRule()
//                        && (repeatInstance = repeatInstanceItemList.getAndRemoveRepeatInstanceWithDate(nextRepeatTime)) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these)
//                    //nothing done about the existing repeatInstance since it is simply left in its place in the original list
////#mdebug
//                    Log.p("**repeat instance reused = " + repeatInstance);
////#enddebug
//                } else { //no previous instance with same date exists, create a new one
//                    repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
//                    //insert new created instance in appropriate list
////                    if (Settings.getInstance().getInsertPositionOfNewRepeatInstances())
////                    motherListForNewInstances.addItem(repeatInstance); //UI: insert new repeatInstance at the end of the list
//                    motherListForNewInstances.addItemAtSpecialPosition(repeatInstance, null, Settings.getInstance().insertPositionOfInitialRepeatInstances.getInt());
//                    setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
////                newInstance.commit(); //- not necessary because done when inserted into a committed list
////#mdebug
//                    Log.p("**new repeat instance generated = " + repeatInstance);
////#enddebug
//                }
////                newRepeatInstanceList.addItem(repeatInstance);
//                newRepeatInstanceList.addElement(repeatInstance); //add all instances (reused or new)
//            }
//
//            repeatInstanceItemList.remove(repeatRuleOriginator); //remove repeatRuleOriginator since it is going to be reused (avoid it being deleted with other obsolete instances)
////            repeatInstanceItemList.deleteRepeatInstancesExceptThis(repeatRuleOriginator); //delete any&all remaining items (since they are not generated by the new repeatRule (and thus not reused))
////            repeatInstanceItemList.deleteAllRepeatInstances(); //delete any&all remaining items (since they are not generated by the new repeatRule (and thus not reused))
////            repeatInstanceItemList.deleteItemListAndAllItsItems(); //delete any&all remaining items (since they are not generated by the new repeatRule, and thus not reused)
////            repeatInstanceItemList.deleteAllItemsInList(); //delete any&all remaining items (since they are not generated by the new repeatRule, and thus not reused)
//            repeatInstanceItemList.clear(); //delete any&all remaining items (since they are not generated by the new repeatRule, and thus not reused)
////            repeatInstanceItemList.removeAllItems(); //remove all items to stop listening to them //-done in deleteRepeatInstancesExceptThis
////            repeatInstanceItemList = null; //Help GC
////            repeatInstanceItemList = newRepeatInstanceList;
//            //transfer all repeatinstances to list:
//            Object temp;
//            while (!newRepeatInstanceList.isEmpty()) {
//                temp = newRepeatInstanceList.elementAt(0);
//                newRepeatInstanceList.removeElementAt(0);
//                repeatInstanceItemList.add(temp);
//            }
////            changed(); //ensures auto-save
////            repeatRuleNewInstancesCalculationOngoing = false;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void xinitializeStateVariables() {
//        setLastDateGeneratedFor(Long.MIN_VALUE);
//        setLastGeneratedDate(Long.MIN_VALUE);
//        setCountOfInstancesGeneratedSoFar(0);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void xgenerateRepeatInstances(RepeatRuleObject repeatRuleOriginator, ItemList motherListForNewInstances) {
//// <editor-fold defaultstate="collapsed" desc="comment">
////        if (repeatRuleNewInstancesCalculationOngoing) {
////            return; //do nothng if we're already calculating repeat instances for this rule (to avoid that when adding new instances, the rule is invoked again
////        } else {
////            repeatRuleNewInstancesCalculationOngoing = true;
////        }
//        //    void generateRepeatInstances(boolean firstTime, /*boolean repeatRuleChanged, boolean repeatObjectChange,*/ /*long startDate, long endDate,*/ RepeatRuleObject repeatRuleOwner, ItemList listForNewCreatedRepeatInstances) { //, long start, long subsetBeginning, long subsetEnding) {
////    void generateRepeatInstances(long startDate, long endDate, RepeatRuleObject repeatRuleOwner) { //, long start, long subsetBeginning, long subsetEnding) {// </editor-fold>
////        if (repeatRuleNewInstancesCalculationOngoing) return;
//        if (isDirty()) { // || !isCommitted()) { //-not possible to be both committed() and dirty
//            repeatRuleNewInstancesCalculationOngoing = true;
//            xinitializeStateVariables(); //then start calculation of instances from scratch (while keeping repeatInstanceVector to reuse any previous instances)
//            Enumeration repeatDates;
//// <editor-fold defaultstate="collapsed" desc="comment">
////        ItemList listForNewCreatedRepeatInstances = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //-can't implement this for WorkSlots
////        if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE && repeatRuleOwner instanceof Item) {
////            repeats = generateDates(((Item) repeatRuleOwner).getCompletedDate()); // + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
////        } else {
////            repeats = generateDates(repeatRuleOwner.getRepeatStartTime());// + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
////        }// </editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
////        int repeatType = getRepeatFromDueOrCompleted();
////        long startDate = Long.MIN_VALUE;
////        switch (getRepeatFromDueOrCompleted()) {
////            case REPEAT_TYPE_FROM_COMPLETED_DATE:
////                startDate = repeatRuleOwner.getRepeatStartTime(true);
////                break;
////            case REPEAT_TYPE_FROM_DUE_DATE:
//////                startDate = repeatRuleOwner.getRepeatStartTime(false);
//////                break;
////            case REPEAT_TYPE_FROM_SPECIFIED_DATE:
////                startDate = getSpecifiedStartDate();
////                break;
////            default:
////        }
////        repeats = generateDates(repeatRuleOwner.getRepeatStartTime((getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE)));// + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
////        repeatDates = generateDates(startDate); // + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated// </editor-fold>
//            repeatDates = xgenerateDates(repeatRuleOriginator.getRepeatStartTime(true)); // + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
//// <editor-fold defaultstate="collapsed" desc="comment">
////        if (firstTime) {
//////#mdebug
////            ASSERT.that(repeatInstanceVector.size() == 0, "repeatInstanceVector not empty on firstTime call");
//////#enddebug
////            repeatInstanceVector.addElement(repeatRuleOwner); //first time the vector is created, explicitly add the 'owner'/originator item
////            repeatRuleOriginator = repeatRuleOwner;
////        }// </editor-fold>
//            RepeatInstanceItemList newRepeatInstanceList = new RepeatInstanceItemList(this); //hold new created instances
//            newRepeatInstanceList.addItem(repeatRuleOriginator); //always add the originator
//// <editor-fold defaultstate="collapsed" desc="comment">
////        if (repeatRuleOriginator != null) { //if repeatRuleOriginator still exists, then transfer it explicitly to new list (since it will not be generated by the rule)
////            newRepeatInstanceList.addElement(repeatRuleOriginator);
////            repeatInstanceVector.removeElement(repeatRuleOriginator);
////        } else {
////            repeatRuleOriginator=repeatRuleOwner;
////        }
////        if (repeatDates.hasMoreElements()) { //if one or more dates exist, then make sure another instance generated
////            setLastDateGeneratedFor(Long.MIN_VALUE); //reset lastDateGeneratedFor to ensure that we capture the largest of the new dates, even if the value should be smaller than the date from a previous update
////        }// </editor-fold>
//            RepeatRuleObject repeatInstance;
////        Date nextRepeatDate;
//            long nextRepeatTime;
//            while (repeatDates.hasMoreElements()
//                    && (getNumberFutureRepeatsGeneratedAhead() == 0 || newRepeatInstanceList.getSize() < getNumberFutureRepeatsGeneratedAhead() + 1)) { //+1 since we want to show both originator and numberOfFuture instances at the same time
////            nextRepeatDate = (Date) repeatDates.nextElement();
//                nextRepeatTime = ((Date) repeatDates.nextElement()).getTime();
//                setLastGeneratedDateIfBigger(nextRepeatTime);
////            repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime());
////            if (repeatInstanceVector.size()>0 && ((RepeatRuleObject)repeatInstanceVector.lastElement()).getRepeatStartTime(false)==nextRepeatTime) {
//                if (repeatRuleOriginator.getRepeatStartTime(false) == nextRepeatTime) {
////            if (new MyDate(repeatRuleOwner.getRepeatStartTime(false)).equalsDate(new MyDate(nextRepeatTime))) { //dates() only returns date, with hour set to midnight
//                    repeatInstanceItemList.getAndRemoveRepeatInstanceWithDate(nextRepeatTime);
//                    continue;
////                    repeatInstance = repeatRuleOriginator;
////                    repeatInstanceVector.getAndRemoveRepeatInstanceWithDate(nextRepeatTime);
//                } else if (Settings.getInstance().reuseExistingInstancesAfterChangingRepeatRule()
//                        //                    && (repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDate(nextRepeatDate.getTime())) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these
//                        && (repeatInstance = repeatInstanceItemList.getAndRemoveRepeatInstanceWithDate(nextRepeatTime)) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these
//                    //newRepeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime())
//                    setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //need to cound reused items as well since we're regenerating from scratch
////#mdebug
//                    Log.l("**repeat instance reused = " + repeatInstance);
////#enddebug
//                } else { //no previous instance with same date exists, create a new one
////                repeatInstance = repeatRuleOwner.createRepeatCopy(nextRepeatDate.getTime());
//                    repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
//                    setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
//                    //insert new created instance in appropriate list
//                    motherListForNewInstances.addItem(repeatInstance); //UI: insert new repeatInstance at the end of the list
//// <editor-fold defaultstate="collapsed" desc="comment">
////                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //increase count by one. Only count *new* instances. UI: user deleted instances are still counted (item.deleteRuleAndAllRepeatInstancesExceptThis() doesn't reduce count)
////                ItemList parentItemList = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //UI: add repeatInstances to the parentList of the source
////                if (listForNewCreatedRepeatInstances != null) { //-the list *mustn't* be null since new instances would not be visible anywhere
////                    int repeatRuleObjectIndex = parentItemList.getItemIndex(repeatRuleObject);
////                    parentItemList.addItemAtIndex(newInstance, repeatRuleObjectIndex+1); //UI: +1: insert new repeatInstance after the previous one (or
////                }// </editor-fold>
////#mdebug
//                    Log.l("**new repeat instance generated = " + repeatInstance);
////#enddebug
////                newInstance.commit(); //- not necessary because done when inserted into a committed list
//                }
////            if (nextRepeatDate.getTime() > getLastDateGeneratedFor()) { //only update if new date is larger than all previous (eg in case generateDates() doesn't generate in strict order)
////                setLastDateGeneratedFor(nextRepeatDate.getTime()); //update for each generated date to
////            }
////                setLastGeneratedDateIfBigger(nextRepeatTime);
//                newRepeatInstanceList.addItem(repeatInstance);
//            }
//
//            repeatInstanceItemList.deleteRepeatInstancesExceptThis(repeatRuleOriginator); //delete any&all remaining items (since they are not covered by the new repeatRule)
//            repeatInstanceItemList = null; //Help GC
//            repeatInstanceItemList = newRepeatInstanceList;
////            setCountOfInstancesGeneratedSoFar(newRepeatInstanceList.size());
////        repeatRuleNewInstancesCalculationOngoing = false;
//            changed(); //ensures auto-save
//            repeatRuleNewInstancesCalculationOngoing = false;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * calculates new/additional repeat instances, when an Item has been
//     * completed or deleted
//     */
//    private void updateRepeatInstancesImpl(RepeatRuleObject repeatRuleOriginator, ItemList motherListForNewInstances) {
//        RepeatRuleObject repeatInstance;
//        long nextRepeatTime;
//        Date nextDate;
//        if ((nextDate = datesList.takeNext()) != null) { //if another instance generated
//            nextRepeatTime = nextDate.getTime();
//            repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
//            //insert new created instance in appropriate list
//            motherListForNewInstances.addItem(repeatInstance); //UI: insert new repeatInstance at the end of the list
////#mdebug
//            Log.l("**new repeat instance generated = " + repeatInstance);
////#enddebug
//            repeatInstanceItemList.addItem(repeatInstance);
//        }
//        changed(); //ensures auto-save
//    }
//</editor-fold>
/**
 * calculates new/additional repeat instances, when an Item has been completed
 * or deleted
 */
//    private void xupdateRepeatInstancesImpl(RepeatRuleObject repeatRuleOriginator, ItemList motherListForNewInstances) {
//// <editor-fold defaultstate="collapsed" desc="comment">
////        if (repeatRuleNewInstancesCalculationOngoing) {
////            return; //do nothng if we're already calculating repeat instances for this rule (to avoid that when adding new instances, the rule is invoked again
////        } else {
////            repeatRuleNewInstancesCalculationOngoing = true;
////        }
//        //    void generateRepeatInstances(boolean firstTime, /*boolean repeatRuleChanged, boolean repeatObjectChange,*/ /*long startDate, long endDate,*/ RepeatRuleObject repeatRuleOwner, ItemList listForNewCreatedRepeatInstances) { //, long start, long subsetBeginning, long subsetEnding) {
////    void generateRepeatInstances(long startDate, long endDate, RepeatRuleObject repeatRuleOwner) { //, long start, long subsetBeginning, long subsetEnding) {// </editor-fold>
//        Enumeration repeatDates;
//// <editor-fold defaultstate="collapsed" desc="comment">
////        ItemList listForNewCreatedRepeatInstances = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //-can't implement this for WorkSlots
////        if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE && repeatRuleOwner instanceof Item) {
////            repeats = generateDates(((Item) repeatRuleOwner).getCompletedDate()); // + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
////        } else {
////            repeats = generateDates(repeatRuleOwner.getRepeatStartTime());// + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
////        }// </editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
////        int repeatType = getRepeatFromDueOrCompleted();
////        long startDate = Long.MIN_VALUE;
////        switch (getRepeatFromDueOrCompleted()) {
////            case REPEAT_TYPE_FROM_COMPLETED_DATE:
////                startDate = repeatRuleOwner.getRepeatStartTime(true);
////                break;
////            case REPEAT_TYPE_FROM_DUE_DATE:
//////                startDate = repeatRuleOwner.getRepeatStartTime(false);
//////                break;
////            case REPEAT_TYPE_FROM_SPECIFIED_DATE:
////                startDate = getSpecifiedStartDate();
////                break;
////            default:
////        }
////        repeats = generateDates(repeatRuleOwner.getRepeatStartTime((getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE)));// + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
////        repeatDates = generateDates(startDate); // + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated// </editor-fold>
//        repeatDates = xgenerateDates(repeatRuleOriginator.getRepeatStartTime(true)); // + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
//// <editor-fold defaultstate="collapsed" desc="comment">
////        if (firstTime) {
//////#mdebug
////            ASSERT.that(repeatInstanceVector.size() == 0, "repeatInstanceVector not empty on firstTime call");
//////#enddebug
////            repeatInstanceVector.addElement(repeatRuleOwner); //first time the vector is created, explicitly add the 'owner'/originator item
////            repeatRuleOriginator = repeatRuleOwner;
////        }// </editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
//////        RepeatInstanceVector newRepeatInstanceList = new RepeatInstanceVector(); //hold new created instances
////        if (repeatRuleOriginator != null) { //if repeatRuleOriginator still exists, then transfer it explicitly to new list (since it will not be generated by the rule)
////            newRepeatInstanceList.addElement(repeatRuleOriginator);
////            repeatInstanceVector.removeElement(repeatRuleOriginator);
////        }
////        if (repeatDates.hasMoreElements()) { //if one or more dates exist, then make sure another instance generated
////            setLastDateGeneratedFor(Long.MIN_VALUE); //reset lastDateGeneratedFor to ensure that we capture the largest of the new dates, even if the value should be smaller than the date from a previous update
////        }// </editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
//        RepeatRuleObject repeatInstance;
////        Date nextRepeatDate;
//        long nextRepeatTime;
//        while (repeatDates.hasMoreElements()) { //if another instance generated
////            nextRepeatDate = (Date) repeatDates.nextElement();
//            nextRepeatTime = ((Date) repeatDates.nextElement()).getTime();
//            if (repeatRuleOriginator.getRepeatStartTime(false) == nextRepeatTime) {
//                repeatInstance = repeatRuleOriginator;
//            } else {
////            repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime());
////            if (Settings.getInstance().reuseExistingInstancesAfterChangingRepeatRule()
//////                    && (repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDate(nextRepeatDate.getTime())) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these
////                    && (repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDate(nextRepeatTime)) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these
////                //newRepeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime())
//////#mdebug
////                Log.l("**repeat instance reused = " + repeatInstance);
//////#enddebug
////            } else { //no previous instance with same date exists, create a new one
////                repeatInstance = repeatRuleOwner.createRepeatCopy(nextRepeatDate.getTime());
//                repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
//                //insert new created instance in appropriate list
//                motherListForNewInstances.addItem(repeatInstance); //UI: insert new repeatInstance at the end of the list
//                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //increase count by one. Only count *new* instances. UI: user deleted instances are still counted (item.deleteRuleAndAllRepeatInstancesExceptThis() doesn't reduce count)
// </editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
////                ItemList parentItemList = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //UI: add repeatInstances to the parentList of the source
////                if (listForNewCreatedRepeatInstances != null) { //-the list *mustn't* be null since new instances would not be visible anywhere
////                    int repeatRuleObjectIndex = parentItemList.getItemIndex(repeatRuleObject);
////                    parentItemList.addItemAtIndex(newInstance, repeatRuleObjectIndex+1); //UI: +1: insert new repeatInstance after the previous one (or
////                }
////#mdebug
//                Log.l("**new repeat instance generated = " + repeatInstance);
////#enddebug
//            }
// </editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
////                newInstance.commit(); //- not necessary because done when inserted into a committed list
////            }
////            if (nextRepeatDate.getTime() > getLastDateGeneratedFor()) { //only update if new date is larger than all previous (eg in case generateDates() doesn't generate in strict order)
////                setLastDateGeneratedFor(nextRepeatDate.getTime()); //update for each generated date to
////            }
//            repeatInstanceItemList.addItem(repeatInstance);
//        }
// </editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
////        repeatInstanceVector.deleteRepeatInstancesExceptThis(repeatRuleOwner); //delete any&all remaining items (since they are not covered by the new repeatRule)
////        repeatInstanceVector = null; //Help GC
////        repeatInstanceVector = newRepeatInstanceList;
////        setCountOfInstancesGeneratedSoFar(newRepeatInstanceList.size());
////        repeatRuleNewInstancesCalculationOngoing = false;
//        changed(); //ensures auto-save
//    }
// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void xxgenerateOrUpdateRepeatInstancesImpl(RepeatRuleObject repeatRuleOwner, ItemList motherListForNewInstances) {
//// <editor-fold defaultstate="collapsed" desc="comment">
////        if (repeatRuleNewInstancesCalculationOngoing) {
////            return; //do nothng if we're already calculating repeat instances for this rule (to avoid that when adding new instances, the rule is invoked again
////        } else {
////            repeatRuleNewInstancesCalculationOngoing = true;
////        }
//        //    void generateRepeatInstances(boolean firstTime, /*boolean repeatRuleChanged, boolean repeatObjectChange,*/ /*long startDate, long endDate,*/ RepeatRuleObject repeatRuleOwner, ItemList listForNewCreatedRepeatInstances) { //, long start, long subsetBeginning, long subsetEnding) {
////    void generateRepeatInstances(long startDate, long endDate, RepeatRuleObject repeatRuleOwner) { //, long start, long subsetBeginning, long subsetEnding) {// </editor-fold>
//        /** true when we are regenerating instances when a rule has been changed. The difference is that in this case we may reuse existing instances with same dates. When not regenerating (that is,
//        we simply add new instances to those already created), we reuse/keep all already generated instances. */
//        boolean regeneratingInstances = false;
//        if (isDirty()) { //if rule has been edited...
//            xinitializeStateVariables(); //then start calculation of instances from scratch (while keeping repeatInstanceVector to reuse any previous instances)
//            regeneratingInstances = true; //
//        }
//        Enumeration repeatDates;
//// <editor-fold defaultstate="collapsed" desc="comment">
////        ItemList listForNewCreatedRepeatInstances = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //-can't implement this for WorkSlots
////        if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE && repeatRuleOwner instanceof Item) {
////            repeats = generateDates(((Item) repeatRuleOwner).getCompletedDate()); // + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
////        } else {
////            repeats = generateDates(repeatRuleOwner.getRepeatStartTime());// + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
////        }// </editor-fold>
////        int repeatType = getRepeatFromDueOrCompleted();
//        long startDate = Long.MIN_VALUE;
//        switch (getRepeatFromDueOrCompleted()) {
//            case REPEAT_TYPE_FROM_COMPLETED_DATE:
//                startDate = repeatRuleOwner.getRepeatStartTime(true);
//                break;
//            case REPEAT_TYPE_FROM_DUE_DATE:
////                startDate = repeatRuleOwner.getRepeatStartTime(false);
////                break;
//            case REPEAT_TYPE_FROM_SPECIFIED_DATE:
//                startDate = getSpecifiedStartDate();
//                break;
//            default:
//        }
////        repeats = generateDates(repeatRuleOwner.getRepeatStartTime((getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE)));// + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
//        repeatDates = xgenerateDates(startDate); // + MyDate.DAY_IN_MILLISECONDS); //+1 to avoid that the starting date itself is generated
//// <editor-fold defaultstate="collapsed" desc="comment">
////        if (firstTime) {
//////#mdebug
////            ASSERT.that(repeatInstanceVector.size() == 0, "repeatInstanceVector not empty on firstTime call");
//////#enddebug
////            repeatInstanceVector.addElement(repeatRuleOwner); //first time the vector is created, explicitly add the 'owner'/originator item
////            repeatRuleOriginator = repeatRuleOwner;
////        }// </editor-fold>
//        RepeatInstanceItemList newRepeatInstanceList = new RepeatInstanceItemList(this); //hold new created instances
////        if (repeatRuleOriginator != null) { //if repeatRuleOriginator still exists, then transfer it explicitly to new list (since it will not be generated by the rule)
////            newRepeatInstanceList.addElement(repeatRuleOriginator);
////            repeatInstanceVector.removeElement(repeatRuleOriginator);
////        }
////        if (repeatDates.hasMoreElements()) { //if one or more dates exist, then make sure another instance generated
////            setLastDateGeneratedFor(Long.MIN_VALUE); //reset lastDateGeneratedFor to ensure that we capture the largest of the new dates, even if the value should be smaller than the date from a previous update
////        }
//        RepeatRuleObject repeatInstance;
//        Date nextRepeatDate;
//        while (repeatDates.hasMoreElements()) { //if another instance generated
//            nextRepeatDate = (Date) repeatDates.nextElement();
////            repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime());
//            if (regeneratingInstances && Settings.getInstance().reuseExistingInstancesAfterChangingRepeatRule()
//                    && (repeatInstance = repeatInstanceItemList.getAndRemoveRepeatInstanceWithDate(nextRepeatDate.getTime())) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these
//                //newRepeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime())
////#mdebug
//                Log.l("**repeat instance reused = " + repeatInstance);
////#enddebug
//            } else { //no previous instance with same date exists, create a new one
//                repeatInstance = repeatRuleOwner.createRepeatCopy(nextRepeatDate.getTime());
//                //insert new created instance in appropriate list
//                motherListForNewInstances.addItem(repeatInstance); //UI: insert new repeatInstance at the end of the list
//                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //increase count by one. Only count *new* instances. UI: user deleted instances are still counted (item.deleteRuleAndAllRepeatInstancesExceptThis() doesn't reduce count)
////                ItemList parentItemList = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //UI: add repeatInstances to the parentList of the source
////                if (listForNewCreatedRepeatInstances != null) { //-the list *mustn't* be null since new instances would not be visible anywhere
////                    int repeatRuleObjectIndex = parentItemList.getItemIndex(repeatRuleObject);
////                    parentItemList.addItemAtIndex(newInstance, repeatRuleObjectIndex+1); //UI: +1: insert new repeatInstance after the previous one (or
////                }
////#mdebug
//                Log.l("**new repeat instance generated = " + repeatInstance);
////#enddebug
////                newInstance.commit(); //- not necessary because done when inserted into a committed list
//            }
////            if (nextRepeatDate.getTime() > getLastDateGeneratedFor()) { //only update if new date is larger than all previous (eg in case generateDates() doesn't generate in strict order)
////                setLastDateGeneratedFor(nextRepeatDate.getTime()); //update for each generated date to
////            }
//            newRepeatInstanceList.addItem(repeatInstance);
//        }
//
//        repeatInstanceItemList.deleteRepeatInstancesExceptThis(repeatRuleOwner); //delete any&all remaining items (since they are not covered by the new repeatRule)
//        repeatInstanceItemList = null; //Help GC
//        repeatInstanceItemList = newRepeatInstanceList;
//        repeatRuleNewInstancesCalculationOngoing = false;
//        changed(); //ensures auto-save
//    }
//</editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    void generateRepeatInstances(RepeatRuleObject repeatRuleObject) { //, long start, long subsetBeginning, long subsetEnding) {
/**
 * @inherit generateRepeatInstances
 *
 * @param repeatRuleObject
 * @param listForNewCreatedRepeatInstances
 */
//    void generateRepeatInstances(RepeatRuleObject repeatRuleObject, ItemList listForNewCreatedRepeatInstances) {
//        generateRepeatInstances(true, Long.MIN_VALUE, Long.MAX_VALUE, repeatRuleObject, listForNewCreatedRepeatInstances);
//    }
//
//    void generateRepeatInstances(Item repeatRuleObject) {
//        generateRepeatInstances(true, Long.MIN_VALUE, Long.MAX_VALUE, repeatRuleObject, (ItemList)repeatRuleObject.getParent());
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    void generateOrUpdateItemInstances(WorkSlot refWorkSlot) { //, long start, long subsetBeginning, long subsetEnding) {
//        Enumeration repeats = generateDates(refWorkSlot.getStart());
//
//        Vector newRepeatInstances = new Vector(); //hold new created instances
//        while (repeats.hasMoreElements()) { //if another instance generated
//            setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //increase count by one
//            Date nextRepeatDate = (Date) repeats.nextElement();
//            Object newInstance;
//            if ((newInstance = getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getRepeatStartTime())) != null) {
//            } else {
//                newInstance = refWorkSlot.createRepeatCopy(nextRepeatDate.getRepeatStartTime());
////#mdebug
//                Log.l("new instance generated = " + newInstance);
////#enddebug
////                newInstance.commit(); //- not necessary because done when inserted into a committed list
//            }
//        }
//        deleteRepeatInstancesExceptThis(refWorkSlot); //delete all remaining items (since they are not covered by the new repeatRule)
//        repeatInstances = null; //Help GC
//        repeatInstances = newRepeatInstances;
////        }
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    void generateOrUpdateInstances2(Item editedOrJustCompletedItemUsedAsSeed) { //, long start, long subsetBeginning, long subsetEnding) {
//        RepeatRule repeatRule = getRepeatRule();
////        Item completedItem = repeatSourceItem;
//        int countGen = -1;
//        long startDateGen = Long.MIN_VALUE;
//        long subsetBeginningGen = Long.MIN_VALUE;
//        long subsetEndingGen = Long.MAX_VALUE;
//        Enumeration repeats;
//
//        switch (getRepeatFromDueOrCompleted()) {
//            case REPEAT_TYPE_FROM_COMPLETED_DATE:
////        if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE) {//generate one instance
//                startDateGen = editedOrJustCompletedItemUsedAsSeed.getCompletedDate();
//                if ((getCount() > 0 && getCountOfInstancesGeneratedSoFar() < getCount()) //we have not generated all defined repeats yet
//                        || (getCount() == 0) && getEndDate() != 0 && getEndDate() > MyDate.getNow() // or we have not passed the defined end date yet //TODO!!!!: must compare by date and not time of day to avoid that an instance for today is not generated for example because we've passed the (invisible) time of day hidden in the long time
//                        || (getCount() == 0 && getEndDate() == 0)) //or neither count nor end date have been specified => indefinite repeat
//                {
//                    countGen = 1; //generate only a single instance
//                    startDateGen = MyDate.getNow(); //generate next instance from now
//                } // else: leave countGen as -1, meaning that countGen won't be used to control the generation, meaning no instances will be generated (the RepeatRule is finished)
//                break;
//            case REPEAT_TYPE_FROM_DUE_DATE:
//            case REPEAT_TYPE_FROM_SPECIFIED_DATE:
////        } else if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_DUE_DATE || getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_SPECIFIED_DATE) {
////            startDateGen = completedItem.getDueDate();
//                if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_DUE_DATE) {
////                startDateGen = referenceItemForDueDateAndFutureCopies.getDueDate();
//                    startDateGen = referenceObjectForRepeatTime.getRepeatStartTime();
//                } else { //REPEAT_TYPE_FROM_SPECIFIED_DATE
//                    startDateGen = specifiedStartDate;
//                }
////            if (getNumberOfDaysRepeatsAreGeneratedAhead() != 0) {
//                if (useNumberFutureRepeatsGeneratedAhead()) {
//                    //how many instances to create ahead: normally as many as missing to reach how many we generate ahead, unless we're approaching the maximum repeats specified (count-instancesSoFar)
//                    countGen = Math.min(getNumberFutureRepeatsGeneratedAhead() - repeatInstanceVector.getRepeatInstancesSize(), getCount() - getCountOfInstancesGeneratedSoFar()); //generate as many as are missing to reach max simultaneous instances (count)
////                subsetEndingGen = Integer.MAX_VALUE; //let count be the restricting value, not endDate //-not needed, set as initial value
//                } else {
//                    //TODO!!!: what happens if eg set to 14 days, biweekly repeats, is there a risk that no instance will be generated due to next instance falling on 14d+1second?
//                    subsetEndingGen = MyDate.getNow() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS;
//                }
//                ;
//                break;
//            default:
//        }
////        subsetBeginningGen = getRepeatInstancesLatestDefinedDueDate(); //only generate instances later than the last ones already generated (?? - what if some instances were deleted, then they'll regenerate now??! Instead store latest date generated for!)
//        if (lastDateGeneratedFor != 0) {
//            subsetBeginningGen = lastDateGeneratedFor; //only generate instances coming later than the last date generated for last time
//        }
////        if (subsetBeginningGen == 0) {
////            subsetBeginningGen = startDateGen; //actually not needed since the dates() algorithm starts from the biggest of the two dates
////        }
//        subsetEndingGen = Math.min(subsetEndingGen, getEndDate() == 0 ? Long.MAX_VALUE : getEndDate()); //if subsetEndingGen calculated so far is bigger than a defined end date, then use the end date
//        lastDateGeneratedFor = subsetEndingGen;
//        if (countGen > 0) {
//            repeatRule.setInt(RepeatRule.COUNT, countGen);
//        }
//        if (interval > 0) {
//            repeatRule.setInt(RepeatRule.INTERVAL, interval);
//        }
//
////#mdebug
//        Log.l("generateOrUpdateInstances2: RepeatRule.toString = " + repeatRule.toString());
//        Log.l("generateOrUpdateInstances2: generateOrUpdateInstances2.repeatRule.dates(start=" + new MyDate(startDateGen) + ", subStart=" + new MyDate(subsetBeginningGen) + ", subEnd=" + new MyDate(subsetEndingGen) + ")");
////#enddebug
//
//        repeats = repeatRule.dates(startDateGen, subsetBeginningGen, subsetEndingGen);
//
////        if (repeats.hasMoreElements()) { //if another instance generated //-even if no repeats are generated by new rule, then update (eg to ensure previously generated items are deleted)
//        Vector newRepeatInstances = new Vector(); //hold new created instances
//        while (repeats.hasMoreElements()) { //if another instance generated
//            setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //increase count by one
//            Date nextRepeatDate = (Date) repeats.nextElement();
//            Item newInstance;
//            if ((newInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getRepeatStartTime())) != null) {
//            } else {
//                newInstance = (Item) editedOrJustCompletedItemUsedAsSeed.createRepeatCopy(nextRepeatDate.getRepeatStartTime());
////#mdebug
//                Log.l("new instance generated = " + newInstance);
////#enddebug
//                newInstance.commit();
//            }
//        }
//        repeatInstanceVector.deleteRepeatInstancesExceptThis(editedOrJustCompletedItemUsedAsSeed); //delete all remaining items (since they are not covered by the new repeatRule)
//        repeatInstances = null; //Help GC
//        repeatInstances = newRepeatInstances;
////        }
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    private int getRepeatInstancesSize() {
//        return repeatInstances.size();
//    }
//
//    private long getRepeatInstancesLatestDefinedDueDate() {
//        if (getRepeatInstancesSize() > 0) {
//            return ((Item) repeatInstances.lastElement()).getDueDate(); //this assumes (as stated by the implementation of RepeatRule) that dates() returns an enumeration ordered by date
//        } else {
//            return 0L;
//        }
//    }
//
//    /** if there already is an item with the given due date created, then return that so it can be reused.
//     * UI: this means that any manuel edits made to instances are retained as long as the date remains the same!!
//    Helps avoid that already created items that have been edited are deleted (and recreated) even if they occur
//    on the same date - for the user this would not be easily acceptable */
//    private RepeatRuleObject getAndRemoveRepeatInstanceWithDate(long repeatDate) {
////        for (int i = getRepeatInstancesSize(); i > 0; i--) {
//        for (int i = 0, size = getRepeatInstancesSize(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
//            if (((Item) repeatInstances.elementAt(i)).getDueDate() == repeatDate) {
//                RepeatRuleObject item = (RepeatRuleObject) repeatInstances.elementAt(i);
//                repeatInstances.removeElementAt(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
//                return item;
//            }
//        }
//        return null;
//    }
//
//    private Item getAndRemoveRepeatInstanceWithDueDate(long dueTime) {
////        for (int i = getRepeatInstancesSize(); i > 0; i--) {
//        for (int i = 0, size = getRepeatInstancesSize(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
//            if (((Item) repeatInstances.elementAt(i)).getDueDate() == dueTime) {
//                Item item = (Item) repeatInstances.elementAt(i);
//                repeatInstances.removeElementAt(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
//                return item;
//            }
//        }
//        return null;
//    }
//
//    /** deletes all the repeat instances (both from repeatInstances vector, and entirely: from RMS, lists etc),
//     * except the referenceItem. If the
//    referenceItem is not found in the list (should this every happen??) then the
//    first item in the list is left undeleted */
//    public void deleteRepeatInstancesExceptThis(RepeatRuleObject repeatRuleObject) {
////        boolean refItemFound = false;
////        for (int i = getRepeatInstancesSize(); i > 0; i--) {
//        if (getRepeatInstancesSize() > 0) { //don't do anything if first time generation (no previous instances)
//            int i = 0;
//            while (getRepeatInstancesSize() > i) { //repeat as long as repeatInstances non empty, OR, there are more instances after the referenceItem which is head of list
//                if (repeatInstances.elementAt(i) == repeatRuleObject) {
////                refItemFound = true;
//                    i = 1; //referenceItem now at position 0, continue with following items in list (if any)
//                } else {
////                if (i > 0 || refItemFound) {
//                    ((Item) repeatInstances.elementAt(i)).deleteRuleAndAllRepeatInstancesExceptThis(); //fully deleteRuleAndAllRepeatInstancesExceptThis
//                    repeatInstances.removeElementAt(i); //only remove the last item at position 0 if the referenceItem was found
////                }                //else:
//                }
//            }
////#mdebug
//            ASSERT.that(repeatInstances.size() == 1 && repeatInstances.elementAt(0) == repeatRuleObject, "referenceItem not found");
////#enddebug
//        }
//    }
//
//    public void deleteRepeatInstancesExceptThis(Item referenceItem) {
////        boolean refItemFound = false;
////        for (int i = getRepeatInstancesSize(); i > 0; i--) {
//        if (getRepeatInstancesSize() > 0) { //don't do anything if first time generation (no previous instances)
//            int i = 0;
//            while (getRepeatInstancesSize() > i) { //repeat as long as repeatInstances non empty, OR, there are more instances after the referenceItem which is head of list
//                if (repeatInstances.elementAt(i) == referenceItem) {
////                refItemFound = true;
//                    i = 1; //referenceItem now at position 0, continue with following items in list (if any)
//                } else {
////                if (i > 0 || refItemFound) {
//                    ((Item) repeatInstances.elementAt(i)).deleteRuleAndAllRepeatInstancesExceptThis(); //fully deleteRuleAndAllRepeatInstancesExceptThis
//                    repeatInstances.removeElementAt(i); //only remove the last item at position 0 if the referenceItem was found
////                }                //else:
//                }
//            }
////#mdebug
//            ASSERT.that(repeatInstances.size() == 1 && repeatInstances.elementAt(0) == referenceItem, "referenceItem not found");
////#enddebug
//        }
//    }
//
//    /** deletes the referenceItem from the list of repeat instances. Used for example when a repeating instance is marked Done */
//    public void removeElement(Item referenceItem) {
//        repeatInstances.removeElement(referenceItem);
//    }
//
//    void removeItem(Item item) {
//        boolean wasInVector = repeatInstances.removeElement(item);
//        ASSERT.that(!wasInVector, "Item removed that was not in list");
//    }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//Fields used to create RepeatRule:
//    private int frequency = RepeatRule.DAILY; //default: daily repeat
//    private int interval = 1; //default: every day
//    private int count = Integer.MAX_VALUE; //= 0; //1; //-start with 0 (undefined => starts with Repeat forever)
//    private long endDate = Long.MAX_VALUE;
/**
 * RepeatRule.DAY_IN_WEEK Represents the day in the week that this event will
 * occur. This index is 1-based. In other words, the value 2 represents the 2nd
 * day of the week. Values can be OR'd together to indicate a multple day event.
 */
//    private int daysInWeek = 0;
/**
 * WEEK_IN_MONTH Represents the week in the month that this event will occur.
 * This index is 1-based. In other words, the value 2 represents the 2nd week of
 * the month. Values can be OR'd together to set multple weeks. //Contains the
 * set of selected weeks in month, as OR'd values. Is mutually exclusive wtih
 * dayInMonth
 */
//    private int weeksInMonth = 0;
/**
 * contains the set of selected months in year, as OR'd values
 */
/**
 * WEEKDAYS_IN_MONTH Represents the week in the month that this event will
 * occur. This index is 1-based. In other words, the value 2 represents the 2nd
 * week of the month. Values can be OR'd together to set multple weeks.
 * //Contains the set of selected weekdays in month (FIRST/SECOND/... applied to
 * Monday/Tuesday/...), as OR'd values. Is mutually exclusive wtih weeksInMonth
 */
//    private int weekdaysInMonth = 0;
/**
 * contains the set of selected months in year, as OR'd values
 */
//    private int monthsInYear = 0;
/**
 * contains the day of the month selected. Is mutually exclusive with
 * weeksInMonth
 */
//    private int dayInMonth = 0;
//    private int dayInYear = 0; //TODO: choosing the middle day of the year, eg365/2=182 would make it faster to choose right date in a scrolling list, but is less intuitive
/**
 * used to select from when to generate new instances
 */
//    private int repeatType = REPEAT_TYPE_FROM_COMPLETED_DATE;
/**
 * how many future repeats are generated at any point in time. Indicates how
 * many instances are generated in *addition* to the current one (either
 * originator or next item), which the user will always expect to see. Used to
 * avoid generating a too high number of repeats at any one time. Mutually
 * exclusive with numberOfDaysRepeatsAreGeneratedAhead (only one of the two can
 * be used at any one time)
 */
//    private int numberOfRepeatsGeneratedAhead = 1;
/**
 * defines how many days ahead the repeat rule should generate instances, e.g.
 * 14 means create all instances for the next 2 weeks
 */
//    private int numberOfDaysRepeatsAreGeneratedAhead = 0;
/**
 * date from which the calculation of repeats start if user chooses 'from
 * today'. (it is NOT used if calculated from due date, in which case the
 * referenceItemForDueDate is used, or if calculated from completed date in
 * which case the By default now(). Is not the same as the first day on which
 * the task repeats since the rule may define a later day
 */
//    private long specifiedStartDate;
//VARIABLES used to track the state of the repeatRule concerning what has been generated so far. These variables are only used as long as the rule itself isn't changed, that is, they're only used when items are completed or deleted. If the rule is changed/edited, all instances must be recalculated from scratch, and all variables should therefore be reset.
/**
 * date up till which repeat instances have been generated so far. Used inside
 * MyRepeatRule to keep track of the latest date for which instances have been
 * created (instead of e.g. just using the latest date stored in the list of
 * generated instances). This is used to ensure that e.g. deleted or done
 * instances do not re-appear
 */
//    private long lastDateGeneratedFor = Long.MIN_VALUE; //=0; //
/**
 * the latest date of any of the instances generated
 */
//    private long lastGeneratedDate = Long.MIN_VALUE; //=0; //
/**
 * counts how many instances have been created so far (over the lifetime of the
 * rule) to ensure that the rule stops when 'count' instances have been created
 */
//    private int countOfInstancesGeneratedSoFar;
/**
 * link to the originating (first creator) item of this rule. Stored as long as
 * the originator is not marked Done. used to distinguish the originator item
 * from generated instances to avoid that the originator item is not kept when
 * regenerating instances for example when changing the RepeatRule
 */
//    private RepeatRuleObject repeatRuleOriginator;
//    private Vector repeatInstances = new Vector(); // = 1; //
/**
 * list of repeat instances generated from repeatRule (those still linked to
 * RepeatRule, that is not done or deleted)
 */
//    private RepeatInstanceItemList repeatInstanceItemList = new RepeatInstanceItemList(this); // = 1; //
//    private List repeatInstanceItemList = new ArrayList(); // = 1; //
// <editor-fold defaultstate="collapsed" desc="comment">
/**
 * used to keep track of whether the MyRepeatRule has been changed during edit,
 * in which case the repeat instances need to be regenerated
 */
//    private boolean repeatRuleChanged; //-use dirty instead!
//
//    /** returns true if the repeatRule has been changed, AND resets the value to false. */
//    public boolean isRepeatRuleChangedAndResetValue() {
//        if (repeatRuleChanged) {
//            repeatRuleChanged = false;
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public void setRepeatRuleChanged(boolean repeatRuleChanged) {
//        this.repeatRuleChanged = repeatRuleChanged;
//    }
//    /** if this Item is generated from a repeating task, then repeatSourceItem points to the source item.
//    This field can be used by repeat items to check if they're the source or an instance: if (this==repeatSourceItem) */
//    Item repeatSourceItem;// </editor-fold>
/**
 * this item points to the item that originally was used to create the repeat
 * rule. It is notably used as a reference for getting the due date that is used
 * as starting point for calculating the repeating items. If due date is not
 * used, then it can be left null. If it is marked complete, then it the repeat
 * rule should keep a reference to it. If it is deleted then a warning should
 * pop up allowing the user to either not
 * deleteRuleAndAllRepeatInstancesExceptThis, or
 * deleteRuleAndAllRepeatInstancesExceptThis and use the first instance as a new
 * reference. If it's due date is changed then the repeating items should be
 * re-calculated.
 */
// <editor-fold defaultstate="collapsed" desc="comment">
//    private RepeatRuleObject referenceObjectForRepeatTime;
//    /** links to the owner - used e.g. when deleting a repeat rule to not deleteRuleAndAllRepeatInstancesExceptThis the 'owning' instance (the one currently being edited - only deleteRuleAndAllRepeatInstancesExceptThis all other instances) - NO better pass this as a parameter from the screen deleting the rule */
//    private RepeatRuleObject itemOrWorkSlotOwner;
//    private Item referenceItemForDueDateAndFutureCopies;
//    /** used for classes that accept RepeatRules, Items and WorkSlots */
//    interface RepeatRuleObject {
//        long getRepeatStartTime();
//        RepeatRuleObject createRepeatCopy(long repeatTime);
//    }// </editor-fold>
/**
 * create a repeatRule with repeatRuleObject as originator
 */
//    MyRepeatRule(RepeatRuleObject repeatRuleOriginator) {
//        this();
////        this.repeatRuleOriginator = repeatRuleOriginator; //keep track of which item was the originator
////        repeatInstanceVector.addElement(repeatRuleOriginator); //add it to the list (since it won't be generated by the rule itself)
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private class xRepeatInstanceVector extends Vector {
//
////        Vector repeatInstances = new Vector(); // = 1; //
////        int getRepeatInstancesSize() {
////            return repeatInstances.size();
////        }
////        void addElement(RepeatRuleObject repeatRuleObject) {
////            repeatInstances.addElement(repeatRuleObject);
////        }
////        RepeatRuleObject elementAt(int index) {
////            return (RepeatRuleObject) repeatInstances.elementAt(index);
////        }
//        /** deletes the referenceItem from the list of repeat instances. Used for example when a repeating instance is marked Done */
////        public void removeElement(RepeatRuleObject repeatRuleObject) {
////            repeatInstances.removeElement(repeatRuleObject);
////        }
//        long getRepeatInstancesLatestDefinedDueDate() {
////            if (getRepeatInstancesSize() > 0) {
//            if (size() > 0) {
////                return ((Item) repeatInstances.lastElement()).getDueDate(); //this assumes (as stated by the implementation of RepeatRule) that dates() returns an enumeration ordered by date
//                return ((Item) lastElement()).getDueDate(); //this assumes (as stated by the implementation of RepeatRule) that dates() returns an enumeration ordered by date
//            } else {
//                return 0L;
//            }
//        }
//
//        /** if there already is an item with the given due date created, then return that so it can be reused.
//         * UI: this means that any manuel edits made to instances are retained as long as the date remains the same!!
//        Helps avoid that already created items that have been edited are deleted (and recreated) even if they occur
//        on the same date - for the user this would not be easily acceptable */
//        RepeatRuleObject xxgetAndRemoveRepeatInstanceWithDate(long repeatDate) {
////        for (int i = getRepeatInstancesSize(); i > 0; i--) {
////            for (int i = 0, size = getRepeatInstancesSize(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
//            for (int i = 0, size = size(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
////                if (((Item) repeatInstances.elementAt(i)).getDueDate() == repeatDate) {
//                if (((Item) elementAt(i)).getDueDate() == repeatDate) {
////                    RepeatRuleObject item = (RepeatRuleObject) repeatInstances.elementAt(i);
//                    RepeatRuleObject item = (RepeatRuleObject) elementAt(i);
////                    repeatInstances.removeElementAt(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
//                    removeElementAt(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
//                    return item;
//                }
//            }
//            return null;
//        }
//
//        /**
//         * returns
//         * @param repeatDate
//         * @return
//         */
//        RepeatRuleObject getAndRemoveRepeatInstanceWithDate(long repeatDate) {
////        for (int i = getRepeatInstancesSize(); i > 0; i--) {
////            for (int i = 0, size = getRepeatInstancesSize(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
//            for (int i = 0, size = size(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
////                if (((RepeatRuleObject) repeatInstances.elementAt(i)).getRepeatStartTime() == time) { //TODO: is this too exact matching? E.g. if just one millisecond's difference
////                if (((RepeatRuleObject) elementAt(i)).getRepeatStartTime(false) == repeatDate) { //TODO: is this too exact matching? E.g. if just one millisecond's difference
//                if (new MyDate(((RepeatRuleObject) elementAt(i)).getRepeatStartTime(false)).equalsDate(new MyDate(repeatDate))) { //TODO: is this too exact matching? E.g. if just one millisecond's difference
////                    RepeatRuleObject item = (RepeatRuleObject) repeatInstances.elementAt(i);
//                    RepeatRuleObject item = (RepeatRuleObject) elementAt(i);
////                    repeatInstances.removeElementAt(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
//                    removeElementAt(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
//                    return item;
//                }
//            }
//            return null;
//        }
//
//        /** deletes all the repeat instances (both from repeatInstances vector, and entirely: from RMS, lists etc),
//         * except the referenceItem. If the referenceItem is not found in the list (should this ever happen??) then the
//         * first item in the list is left undeleted
//         */
//        public void deleteRepeatInstancesExceptThis(RepeatRuleObject repeatRuleObject) {
//            repeatRuleObject.setRepeatRule(null); //remove reference to repeatRule
//            removeElement(repeatRuleObject); //remove from vector so it won't be deleted
//            for (int i = 0, size = size(); i < size; i++) {
//                ((RepeatRuleObject) elementAt(i)).setRepeatRule(null); //remove the link to the repeat rule *before* deleting (otherwise deleting
//                ((RepeatRuleObject) elementAt(i)).deleteRepeatInstance(); //fully deleteRuleAndAllRepeatInstancesExceptThis
//            }
//            removeAllElements();
//        }
//
//        public void xdeleteRepeatInstancesExceptThis(RepeatRuleObject repeatRuleObject) {
////        boolean refItemFound = false;
////        for (int i = getRepeatInstancesSize(); i > 0; i--) {
////            if (repeatInstances.size() > 0) { //don't do anything if first time generation (no previous instances)
//            if (size() > 0) { //don't do anything if first time generation (no previous instances)
//                int i = 0;
////                while (i < repeatInstances.size()) { //repeat as long as repeatInstances non empty, OR, there are more instances after the referenceItem which is head of list
//                while (i < size()) { //repeat as long as repeatInstances non empty, OR, there are more instances after the referenceItem which is head of list
////                    if (repeatInstances.elementAt(i) == repeatRuleObject) {
//                    if (elementAt(i) == repeatRuleObject) {
////                refItemFound = true;
//                        i = 1; //repeatRuleObject now at position 0, continue with following items in list (if any)
//                    } else {
////                        ((RepeatRuleObject) repeatInstances.elementAt(i)).setRepeatRule(null); //remove the link to the repeat rule *before* deleting
//                        ((RepeatRuleObject) elementAt(i)).setRepeatRule(null); //remove the link to the repeat rule *before* deleting (otherwise deleting
////                        ((RepeatRuleObject) repeatInstances.elementAt(i)).deleteRepeatInstance(); //fully deleteRuleAndAllRepeatInstancesExceptThis
//                        ((RepeatRuleObject) elementAt(i)).deleteRepeatInstance(); //fully deleteRuleAndAllRepeatInstancesExceptThis
////                        repeatInstances.removeElementAt(i);
//                        removeElementAt(i);
//                    }
//                }
////#mdebug
////                ASSERT.that(repeatInstances.size() == 1 && repeatInstances.elementAt(0) == repeatRuleObject, "referenceItem not found");
//                ASSERT.that(size() == 1 && elementAt(0) == repeatRuleObject, "referenceItem not found");
////#enddebug
//            }
//        }
//
//        /** deletes all the repeat instances (both from repeatInstances vector, and entirely: from RMS, lists etc),
//         * except the referenceItem. If the referenceItem is not found in the list (should this every happen??) then the
//         * first item in the list is left undeleted
//         */
//        public void deleteAllRepeatInstances() {
//            int i = 0;
//            while (i < size()) { //repeat as long as repeatInstances non empty, OR, there are more instances after the referenceItem which is head of list
//                ((RepeatRuleObject) elementAt(i)).setRepeatRule(null); //remove the link to the repeat rule *before* deleting
//                ((RepeatRuleObject) elementAt(i)).deleteRepeatInstance(); //fully deleteRuleAndAllRepeatInstancesExceptThis
//                removeElementAt(i);
//            }
////#mdebug
////                ASSERT.that(repeatInstances.size() == 1 && repeatInstances.elementAt(0) == repeatRuleObject, "referenceItem not found");
//            ASSERT.that(size() == 0, "not all repeat instances were deleted");
////#enddebug
//        }
//    }
//</editor-fold>
/**
 * generate next batch of repeat dates. Returns empty List if the repeat rule
 * has ended (no more dates).
 */
//        private Vector updateNew(RepeatRule repeatRule, long specifiedStartDate, long endDate, long lastRepeatDate, int totalMaxCountToGenerate, int countAlreadyGenerated, int numberOfRepeatsToGenerate) {
////            if (datesList.sizeActual() > 1 || datesList.size() >= getCount()) { //need to generate more instances while there is we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
////            if (datesList.sizeTotal() == 0 || (datesList.size() == 1 && datesList.sizeTotal() < getCount())) { //need to generate more instances while there is at least one of previous 'batch' left, since we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
////                RepeatRule repeatRule = getRepeatRule();
////                repeatRule.setInt(RepeatRule.COUNT, Math.min(getCount() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
////                if (getCount() != Integer.MAX_VALUE) {
////                    repeatRule.setInt(RepeatRule.COUNT, Math.min(getCount() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
////                } else {
////                    repeatRule.setInt(RepeatRule.COUNT, COUNTS_AHEAD + 1); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
////                }
//            //if numberOfRepeatsToGenerate is less than the total remaining to generate, then generate only numberOfRepeatsToGenerate instances,
//            //otherwise generate totalMaxCountToGenerate-countAlreadyGenerated which is the total of still missing instances to generate
//            int numberOfInstancesToGenerate = Math.min(numberOfRepeatsToGenerate, totalMaxCountToGenerate - countAlreadyGenerated);
//            repeatRule.setInt(RepeatRule.COUNT, numberOfInstancesToGenerate); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
////                long subsetBeginningGen;
////                if (datesList.sizeTotal() == 0) {
////                    subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
////                } else {
//////                    subsetBeginningGen = ((Long) datesList.lastElement()).longValue(); //get the last date we've generated so far
////                    subsetBeginningGen = ((Date) datesList.lastElement()).getTime(); //get the last date we've generated so far
////                }
//            long subsetBeginningGen = Math.max(specifiedStartDate, lastRepeatDate + 1); //lastRepeatDate+1 to ensure that we don't regenerate the last already generated date, but the one after that
////                Vector newDates = repeatRule.datesAsVector(specifiedStartDate, subsetBeginningGen, endDate);
//            return repeatRule.datesAsVector(specifiedStartDate, subsetBeginningGen, endDate);
////                if (!newDates.isEmpty()) { //if vector not empty
////                    newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that
////                }
////                while (!newDates.isEmpty()) {
////                    datesList.addElement(newDates.firstElement()); //store new batch of dates
////                    newDates.removeElementAt(0);
////                }
////                ASSERT.that(datesList.sizeTotal() <= getCount(), "generated too many instances total=" + datesList.sizeTotal() + " getCount=" + getCount() + " #rule=" + this);
////            }
//        }
//
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        private void xupdate() {
////            int COUNTS_AHEAD = Settings.getInstance().getDefaultNumberOfRepeatInstancesBuffered();
////            if (datesList == null) {
////                datesList = new Vector(COUNTS_AHEAD);
////            }
//            if (nextDateIndex >= datesList.size()) { //we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
//                RepeatRule repeatRule = getRepeatRule();
////                int countGen = 0; //= -1;
////                long subsetBeginningGen; // = Math.max(getLastGeneratedDate(), getLastDateGeneratedFor()); // OK if getLast returns Long.MIN_VALUE since dates() uses Max(start,subsetBeginning) //=startDate; //=0;
//                if (!ruleFinished) {
////                    if (getInterval() > 0) {
////                        repeatRule.setInt(RepeatRule.INTERVAL, getInterval()); //- done in getRepeatRule()
////                    }
////                    boolean startFromLastGeneratedDate = false;
////                    if (useNumberFutureRepeatsGeneratedAhead()) {
////                        startFromLastGeneratedDate = true;
////                    } else {
////                    }
////                    int countGen = Math.min(getCount(), totalCount + COUNTS_AHEAD + (startFromLastGeneratedDate ? 1 : 0));
//                    int countGen;
////                    countGen = Math.min(getCount(), totalCount + COUNTS_AHEAD + 1); // +1 always generate an extra which is then dropped (because it is the same date as the originating item)
//                    countGen = Math.min(getCount() - totalCount, COUNTS_AHEAD); // +1 always generate an extra which is then dropped (because it is the same date as the originating item)
////                    if (countGen != Integer.MAX_VALUE) { //NB. not allowed to set 0 as value for COUNT
//                    if (countGen >= 1) { //NB. not allowed to set 0 as value for COUNT
//                        repeatRule.setInt(RepeatRule.COUNT, countGen);
//                    }
////                    repeatRule.setDate(RepeatRule.END, getEndDate());
//                    long subsetBeginningGen; // = startFromLastGeneratedDate ? ((Date) datesList.elementAt(nextDateIndex)).getTime() : getSpecifiedStartDate();
////                    if (startFromLastGeneratedDate) {
////                        subsetBeginningGen = ((Date) datesList.elementAt(nextDateIndex)).getTime();
////                    } else {
////                        subsetBeginningGen = lastDateGeneratedFor;
////                    }
////                    long startDate;
////                    startDate = getSpecifiedStartDate();
////                    if (datesList.size() == 0) {
////                        subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
////                    } else {
////                        //                        subsetBeginningGen = ((Date) datesList.elementAt(nextDateIndex)).getTime();
////                        subsetBeginningGen = ((Date) datesList.lastElement()).getTime(); //start from date of last generated date (which is then dropped again to avoid repeating it)
////                    }
//                    subsetBeginningGen = lastDateGenerated;
////                    long subsetEndGen = getEndDate();
////                    Vector newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, subsetEndGen);
////                    Vector newDates = repeatRule.datesAsVector(startDate, subsetBeginningGen, subsetEndGen);
//                    Vector newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, getEndDate());
////                    if (startFromLastGeneratedDate && !newDates.isEmpty()) {
////                    if (!newDates.isEmpty() && !(newDates.size() < COUNTS_AHEAD + 1)) { //if vector not empty, and if we have generated an extra instance (COUNTS_AHEAD + 1 above) then drop
////                        if (((Date) newDates.elementAt(0)).getTime() == subsetBeginningGen) {
////                            newDates.removeElementAt(0); //drop first element
////                        } else {
////                            newDates.removeElementAt(newDates.size() - 1); //if first (extra) element does not repeat the already used date, then drop last element instead
////                        }
////                    }
//                    if (!newDates.isEmpty()) { //if vector not empty, and if we have generated an extra instance (COUNTS_AHEAD + 1 above) then drop
//                        lastDateGenerated = ((Long) newDates.lastElement()).longValue();
//                        newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that
//                    }
//                    totalCount += newDates.size();
//                        ASSERT.that(totalCount <= getCount(), "generated too many instances");
//                    if (totalCount == getCount()) {
//                        ruleFinished = true; //first time less than COUNTS_AHEAD is generated it means the rule is finished and all dates haven been generated
//                    }
////                    while (!newDates.isEmpty()) {
////                        datesList.addElement(newDates.firstElement()); //store new batch of dates
////                        newDates.removeElementAt(0);
////                    }
////                    datesList = newDates; //store new batch of dates. TODO!!!!!: can we be sure these are always stored in RMS??
//                    nextDateIndex = 0; //start from beginning of new list
//                }
//            }
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        /**
//         * generates just a single repeat used for repeat by Completed
//         */
//        private void update(long completedDate) {
////            int COUNTS_AHEAD = Settings.getInstance().getDefaultNumberOfRepeatInstancesBuffered();
////            datesList = null; //help GC
////                datesList = new Vector(2);
//            RepeatRule repeatRule = getRepeatRule();
////                if (!ruleFinished) {
////            if (!ruleFinished && totalCount < getCount()) {
//            if (!ruleFinished) {
//                //when repeating from completedDate, generate always only 1+1 ahead (+1 always generate an extra which is then dropped)
//                repeatRule.setInt(RepeatRule.COUNT, 2);
//                Vector newDates = repeatRule.datesAsVector(completedDate, completedDate, getEndDate());
//                if (!newDates.isEmpty()) {
//                    if (((Date) newDates.elementAt(0)).getTime() == completedDate) {
//                        newDates.removeElementAt(0); //drop first element
//                    } else {
//                        newDates.removeElementAt(newDates.size() - 1); //if first (extra) element does not repeat the already used date, then drop last element instead
//                    }
//                }
//                totalCount += newDates.size();
//                if (newDates.size() < 1 || totalCount >= getCount()) {
//                    ruleFinished = true; //first time less than COUNTS_AHEAD is generated it means the rule is finished and all dates haven been generated
//                }
//                datesList = null; //help GC
//                datesList = newDates; //store new batch of dates
//                nextDateIndex = 0; //start from beginning of new lsit
//            }
//        }
//</editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * deletes (by calling .deleteRuleAndAllRepeatInstancesExceptThis()) all RepeatInstanceObjects except the first one
//     */
//    public void xxdelete() {
//        deleteRuleAndAllRepeatInstancesExceptThis(repeatInstanceVector.getRepeatInstancesSize() > 0 ? repeatInstanceVector.elementAt(0) : null);
//    }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean xallInstancesAreCreated() {
//// <editor-fold defaultstate="collapsed" desc="comment">
////        if ((getCount() > 0 && getCountOfInstancesGeneratedSoFar() >= getCount()) //we have generated all defined repeats
//////                || (getCount() == 0 && getEndDate() != 0 && MyDate.getNow() < getEndDate()) // or endDate is defined and not yet passed  //TODO!!!!: must compare by date and not time of day to avoid that an instance for today is not generated for example because we've passed the (invisible) time of day hidden in the long time
////                || (getCount() == 0 && MyDate.getNow() > getEndDate()) // or endDate is defined and not yet passed  //TODO!!!!: must compare by date and not time of day to avoid that an instance for today is not generated for example because we've passed the (invisible) time of day hidden in the long time
//////                || (getCount() == 0 && getEndDate() == Long.MAX_VALUE)
////                ) //or neither count nor end date have been specified => indefinite repeat
////        {
////            return true;
////        }// </editor-fold>
//        if (useCount()) {
//            return getCountOfInstancesGeneratedSoFar() >= getCount(); //we have generated all defined repeats
//        } else if (getEndDate() != Long.MAX_VALUE) {
////            return MyDate.getNow() > getEndDate(); // or endDate is passed  //TODO!!!!: must compare by date and not time of day to avoid that an instance for today is not generated for example because we've passed the (invisible) time of day hidden in the long time
//            return getLastDateGeneratedFor() >= getEndDate(); // or endDate is passed  //TODO!!!!: must compare by date and not time of day to avoid that an instance for today is not generated for example because we've passed the (invisible) time of day hidden in the long time
//        } else {
//            return false; //or neither count nor end date have been specified => indefinite repeat
//        }
////                || (getCount() == 0 && getEndDate() == Long.MAX_VALUE)
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
/**
 * returns true if the repeatRule is finished (will never generate any more
 * instances) and can be removed. What happens if we only generate events 30
 * days ahead, but next instance is 2 months ahead, and we've just marked the
 * last item Done??????? Even we can recalculate instances later on, where to
 * get the item to instantiate??
 */
//    private boolean isRepeatRuleFinished() {
////        return (xallInstancesAreCreated() && repeatInstanceItemList.getSize() == 0);
//        return !datesList.hasMoreDates(); //rule is finished when there are no more dates to generate instances for
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean xisRepeatRuleFinished() {
//        if (repeatInstanceItemList.getSize() == 0
//                && ((getCount() != 0 && getCountOfInstancesGeneratedSoFar() >= getCount()) //we've generated all the instances specified
//                //                || (getEndDate()!=0 && lastDateGeneratedFor >= getEndDate()) //we've already generated instances up to endDate
//                //                || (lastDateGeneratedFor >= getEndDateMaxIfZero()) //we've already generated instances up to endDate
//                || (getLastDateGeneratedFor() >= getEndDate()) //we've already generated instances up to endDate
//                //                || (getEndDate()!=0 && MyDate.getNow() >= getEndDate()) //or we've passed the endDate //TODO: are there cases where we should generate anyway, eg we just haven't managed to regenerate before time passed?
//                //                || false //TODO!!!!! complete the expression
//                )) { //if vector is empty, then rule is finished and can be deleted() //TODO!!!! NO - what if rule generates bi-monthly events, but only generates 30 days ahead?!
//            return true;
//        } else if (getEndDate() == 0 //repeat forever
//                //                || (getEndDate() != 0 && MyDate.getNow() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS < getEndDate())) { //we haven't
//                //                || (MyDate.getNow() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS < getEndDateMaxIfZero())) { //we haven't
//                || (MyDate.getNow() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS < getEndDate())) { //we haven't
//            return false;
//        }
//        ASSERT.that("Unknown condition in MyRepeatRule [" + this + "]");
//        return true;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
/**
 * returns true if the new generated date will repeat the
 */
//    private boolean isLastGeneratedDateRepeated() {
//        boolean val = getLastGeneratedDate() > getLastDateGeneratedFor() || getSpecifiedStartDate() ==; //
//        return val;
//    }
/**
 * generates the dates for the repeat rule, taking into account all the
 * constraints defined by the rule, e.g. what type of repeat (from Due date or
 * Completed), how many instances to generate (from count).
 *
 * @param completedDate is the date to use as basis for the calcaulation and is
 * defined by the repeating object (either due date, or completed by date).
 */
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Enumeration xgenerateDates(long completedDate) { //, long start, long subsetBeginning, long subsetEnding) {
//        RepeatRule repeatRule = getRepeatRule();
//        int countGen = 0; //= -1;
//        long startDateGen = getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE ? completedDate : getSpecifiedStartDate(); //=startDate;
////        long subsetBeginningGen = getLastDateGeneratedFor() == Long.MIN_VALUE ? startDateGen : getLastDateGeneratedFor(); //=startDate; //=0;
////        long subsetBeginningGen = getLastDateGeneratedFor(); // OK if getLast returns Long.MIN_VALUE since dates() uses Max(start,subsetBeginning) //=startDate; //=0;
//        long subsetBeginningGen = Math.max(getLastGeneratedDate(), getLastDateGeneratedFor()); // OK if getLast returns Long.MIN_VALUE since dates() uses Max(start,subsetBeginning) //=startDate; //=0;
//        /** */
////        boolean startingDateEqualsLastGenerated = getLastGeneratedDate() > getLastDateGeneratedFor();
//        /** skip the first generated date, for example because it is the same as the previously generated (due to dates() returning*/
//        boolean skipFirstGeneratedDate = getLastGeneratedDate() > getLastDateGeneratedFor();
//        long subsetEndingGen = getEndDate(); //Long.MAX_VALUE;
//
//        if (!xallInstancesAreCreated()) {
//            if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE) {//generate one instance
//                countGen = 1; //generate only a single instance
//                skipFirstGeneratedDate = true;
////                    startDateGen = MyDate.getNow(); //generate next instance from now
//            } else {
//                if (useNumberFutureRepeatsGeneratedAhead()) {
//                    //how many instances to create ahead: normally as many as missing to reach how many we generate ahead, unless we're approaching the maximum repeats specified (count-instancesSoFar)
//                    //generate as many as are missing to reach max simultaneous instances (count)
//                    long missingFutureInstances = getNumberFutureRepeatsGeneratedAhead() - repeatInstanceItemList.getSize(); //how many active instances do we want minus how many there currently are
//                    ASSERT.that(getCountOfInstancesGeneratedSoFar() <= getCount(), "instances generated so far must never become bigger than count");
//                    int totalMissingFutureInstances = useCount() ? getCount() - getCountOfInstancesGeneratedSoFar() : Integer.MAX_VALUE; //how many should be generated maximum (user specified), minus how many we've generated so far over the life time of the RepeatRule. If count undefined, use MAX here
//                    countGen = (int) Math.min(missingFutureInstances, totalMissingFutureInstances);
////                subsetEndingGen = Long.MAX_VALUE; //let count be the restricting value, not endDate //-not needed, set as initial value
//                } else { //use getNumberOfDaysRepeatsAreGeneratedAhead
//                    //DO!!!: what happens if eg set to 14 days, biweekly repeats, is there a risk that no instance will be generated due to next instance falling on 14d+1second?
//                    subsetEndingGen = Math.min(MyDate.getNow() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS, getEndDate()); //generated the specified number of days ahead, unless it extends beyond endDate
//                    setLastDateGeneratedFor(subsetEndingGen); //TODO!!!!: do not set if genFromCompleted and Long.MAX was selected
//                }
//            }
//// <editor-fold defaultstate="collapsed" desc="comment">
////            if (getLastDateGeneratedFor() != 0) { //first time it will be zero
////            if (getLastDateGeneratedFor() != Long.MIN_VALUE) { //first time it will be MIN_VALUE
////                subsetBeginningGen = getLastDateGeneratedFor(); //only generate instances coming later than the last date generated for last time
////            }
////            subsetEndingGen = Math.min(subsetEndingGen, getEndDate()); //if subsetEndingGen calculated so far is bigger than a defined end date, then use the defined end date
////            setLastDateGeneratedFor(subsetEndingGen != Long.MAX_VALUE ? subsetEndingGen : Long.MIN_VALUE); //DO!!!!: do not send if genFromCompleted and Long.MAX was selected
////            if (subsetEndingGen != Long.MAX_VALUE)
////                setLastDateGeneratedFor(subsetEndingGen); //TODO!!!!: do not send if genFromCompleted and Long.MAX was selected// </editor-fold>
//            if (getInterval() > 0) {
//                repeatRule.setInt(RepeatRule.INTERVAL, getInterval());
//            }
//            if (countGen > 0) { //NB. not allowed to set 0 as value for COUNT
////                if (startingDateEqualsLastGenerated) {
////                    countGen++;
////                }
////                repeatRule.setInt(RepeatRule.COUNT, ((int) countGen)+(startingDateEqualsLastGenerated?1:0)); //+1); //+1 because dates() also returns the starting date, which we'll skip
//                repeatRule.setInt(RepeatRule.COUNT, countGen + 1); //+1 always generate one too much: if ); //+1 because dates() also returns the starting date, which we'll skip
//            }
//
//            Enumeration dates;
//            dates = repeatRule.dates(startDateGen, subsetBeginningGen, subsetEndingGen);
////            if (dates.hasMoreElements())
////                dates.nextElement(); //skip/drop the first created element since it is the same as the starting date (which was the date of the just removed item)
////            if (countGen > 0 && dates.hasMoreElements()) {
////                dates.nextElement(); //skip/drop the first created element since it is the same as the starting date (which was the date of the just removed item)
////            }
////            if (startingDateEqualsLastGenerated)
//            if (skipFirstGeneratedDate && dates.hasMoreElements()) {
//                dates.nextElement(); //skip/drop the first created element since it is the same as the starting date (which was the date of the just removed item)
//            }
//            return dates;
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Enumeration xxgenerateDates(long startDate) { //, long start, long subsetBeginning, long subsetEnding) {
//        RepeatRule repeatRule = getRepeatRule();
//        int countGen = 0; //= -1;
//        long startDateGen = startDate;
//        long subsetBeginningGen = startDate; //=0;
//        long subsetEndingGen = Long.MAX_VALUE;
//
//        if (getInterval() > 0) {
//            repeatRule.setInt(RepeatRule.INTERVAL, getInterval());
//        }
//        if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE) {//generate one instance
////            startDateGen = editedOrJustCompletedItemUsedAsSeed.getCompletedDate();
//            if ((getCount() > 0 && getCountOfInstancesGeneratedSoFar() < getCount()) //we have not generated all defined repeats yet
//                    //                    || (getCount() == 0 && getEndDate() != 0 && getEndDate() > MyDate.getNow()) // or we have not passed the defined end date yet //TODO!!!!: must compare by date and not time of day to avoid that an instance for today is not generated for example because we've passed the (invisible) time of day hidden in the long time
//                    || (getCount() == 0 && getEndDate() != 0 && MyDate.getNow() < getEndDate()) // or endDate is defined and not yet passed  //TODO!!!!: must compare by date and not time of day to avoid that an instance for today is not generated for example because we've passed the (invisible) time of day hidden in the long time
//                    || (getCount() == 0 && getEndDate() == 0)) //or neither count nor end date have been specified => indefinite repeat
//            {
//                countGen = 1; //generate only a single instance
//                startDateGen = MyDate.getNow(); //generate next instance from now
//                subsetBeginningGen = startDateGen;
//                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //one more instance generated
//            } // else: leave countGen as -1, meaning that countGen won't be used to control the generation, meaning no instances will be generated (the RepeatRule is finished)
//        } else if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_DUE_DATE || getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_SPECIFIED_DATE) {
//            if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_DUE_DATE) {
////                startDateGen = getSpecifiedStartDate();
//                startDateGen = startDate;
//            } else { //REPEAT_TYPE_FROM_SPECIFIED_DATE
//                startDateGen = getSpecifiedStartDate();
////                startDateGen = startDate;
//            }
//            if (useNumberFutureRepeatsGeneratedAhead()) {
////                countGen = Math.min(getNumberFutureRepeatsGeneratedAhead() - getRepeatInstancesSize(), getCount() - getCountOfInstancesGeneratedSoFar()); //generate as many as are missing to reach max simultaneous instances (count)
////                countGen = Math.min(getNumberFutureRepeatsGeneratedAhead() - repeatInstanceVector.getRepeatInstancesSize(), getCount() - getCountOfInstancesGeneratedSoFar()); //generate as many as are missing to reach max simultaneous instances (count)
////                countGen = Math.min(getNumberFutureRepeatsGeneratedAhead() - repeatInstanceVector.size(), getCount() - getCountOfInstancesGeneratedSoFar());
//                //how many instances to create ahead: normally as many as missing to reach how many we generate ahead, unless we're approaching the maximum repeats specified (count-instancesSoFar)
//                //generate as many as are missing to reach max simultaneous instances (count)
//                int missingFutureInstances = getNumberFutureRepeatsGeneratedAhead() - repeatInstanceItemList.getSize(); //how many active instances do we want minus how many there currently are
//                int totalMissingFutureInstances = getCount() != 0 ? getCount() - getCountOfInstancesGeneratedSoFar() : Integer.MAX_VALUE; //how many should be generated maximum (user specified), minus how many we've generated so far over the life time of the RepeatRule. If count undefined, use MAX here
//                countGen = Math.min(missingFutureInstances, totalMissingFutureInstances);
////                subsetEndingGen = Integer.MAX_VALUE; //let count be the restricting value, not endDate //-not needed, set as initial value
//            } else { //use getNumberOfDaysRepeatsAreGeneratedAhead
//                //TODO!!!: what happens if eg set to 14 days, biweekly repeats, is there a risk that no instance will be generated due to next instance falling on 14d+1second?
////                subsetEndingGen = Math.min(MyDate.getNow() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS, getEndDateMaxIfZero()); //generated the specified number of days ahead, unless it extends beyond endDate
//                subsetEndingGen = Math.min(MyDate.getNow() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS, getEndDate()); //generated the specified number of days ahead, unless it extends beyond endDate
//            }
//        }
////        subsetBeginningGen = getRepeatInstancesLatestDefinedDueDate(); //only generate instances later than the last ones already generated (?? - what if some instances were deleted, then they'll regenerate now??! Instead store latest date generated for!)
//        if (getLastDateGeneratedFor() != 0) { //first time it will be zero
//            subsetBeginningGen = getLastDateGeneratedFor(); //only generate instances coming later than the last date generated for last time
//        }
////        subsetEndingGen = Math.min(subsetEndingGen, getEndDateMaxIfZero()); //if subsetEndingGen calculated so far is bigger than a defined end date, then use the defined end date
//        subsetEndingGen = Math.min(subsetEndingGen, getEndDate()); //if subsetEndingGen calculated so far is bigger than a defined end date, then use the defined end date
////        setLastDateGeneratedFor(subsetEndingGen != Long.MAX_VALUE ? subsetEndingGen : 0); //TODO!!!!: do not send if genFromCompleted and Long.MAX was selected
//        setLastDateGeneratedFor(subsetEndingGen != Long.MAX_VALUE ? subsetEndingGen : Long.MIN_VALUE); //TODO!!!!: do not send if genFromCompleted and Long.MAX was selected
////        if (subsetBeginningGen == 0) {
////            subsetBeginningGen = startDateGen; //actually not needed since the dates() algorithm starts from the biggest of the two dates
////        }
////        subsetEndingGen = Math.min(subsetEndingGen, getEndDate() == 0 ? Long.MAX_VALUE : getEndDate()); //if subsetEndingGen calculated so far is bigger than a defined end date, then use the end date
//        if (countGen > 0) { //not allowed to set 0 as value for COUNT
//            repeatRule.setInt(RepeatRule.COUNT, countGen);
//        }
//
//        Enumeration dates;
//        dates = repeatRule.dates(startDateGen, subsetBeginningGen, subsetEndingGen);
//        return dates;
////        return repeatRule.dates(startDateGen, subsetBeginningGen, subsetEndingGen);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(boolean updateInstances, RepeatRuleObjectInterface repeatRuleObject) { //, ItemList motherList) {
////        if (repeatRuleNewInstancesCalculationOngoing) {
////            return;
////        } else {
////            repeatRuleNewInstancesCalculationOngoing = true;
////        }
//        //remove the just Completed item *before* generating new ones to ensure that the completed instance is missing in the count of items
//        getRepeatInstanceItemList().remove(repeatRuleObject); //if repeatRuleObject is still there after regeneration??, then remove it
//        if (updateInstances) {
////            updateRepeatInstancesImpl(repeatRuleObject, motherList);
//            RepeatRuleObjectInterface repeatInstance;
//            long nextRepeatTime;
//            Date nextDate = datesList.takeNext();
//            if (nextDate != null) { //if another instance generated
//                nextRepeatTime = nextDate.getTime();
//                repeatInstance = repeatRuleObject.createRepeatCopy(nextRepeatTime);
//                //insert new created instance in appropriate list
//</editor-fold>
//                //<editor-fold defaultstate="collapsed" desc="comment">
//                //                motherList.addItem(repeatInstance); //UI: insert new repeatInstance at the end of the list
//                //                motherList.addItemAtIndex(repeatInstance, Settings.getInstance().getAddRepeatInstancesToEndOfList() ? motherList.getSize() : 0); //UI: insert new repeatInstance at the end of the list
//                //                if (Settings.getInstance().getInsertPositionOfNewRepeatInstances() == Settings.getInstance().INSERT_AT_HEAD_OF_LIST) {
//                //                    motherList.addItemAtIndex(repeatInstance, 0); //UI: insert new repeatInstance at the end of the list
//                //                } else if (Settings.getInstance().getInsertPositionOfNewRepeatInstances() == Settings.getInstance().INSERT_AT_END_OF_LIST) {
//                //                    motherList.addItemAtIndex(repeatInstance, motherList.getSize()); //UI: insert new repeatInstance at the end of the list
//                //                } //add new created repeatInstance just before the just finished one
//                //                else if (Settings.getInstance().getInsertPositionOfNewRepeatInstances() == Settings.getInstance().INSERT_BEFORE_CURRENT_ITEM) {
//                //                    motherList.addItemAtIndex(repeatInstance, motherList.getItemIndex(repeatRuleObject)); //UI: insert new repeatInstance at position of previous
//                //                }
//                //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                repeatRuleObject.getListForNewCreatedRepeatInstances().addItemAtSpecialPosition(repeatInstance, repeatRuleObject, Settings.getInstance().insertPositionOfNewRepeatInstances.getInt());
////#mdebug
//                Log.p("**new repeat instance generated = " + repeatInstance);
////#enddebug
//                getRepeatInstanceItemList().add(repeatInstance);
//            }
//        }
////        changed(); //ensures auto-save        }
//        if (getRepeatInstanceItemList().size() == 0) { //if vector is empty, then check if rule is finished and can be deleted()
////            if (isRepeatRuleFinished()) {
//            //if no more pre-generated repeat dates are available
//            if (!datesList.hasMoreDates()) {
//                this.delete();
//            } else {
//                //TODO!!!! NO - what if rule generates bi-monthly events, but only generates 30 days ahead?!
//                //force the generation of firstcoming next instance, even if it's beyond the //UI: needed to keep one visible instance to eg edit the repeatRule
//                ASSERT.that("Not implemented");
//            }
//        }
////        changed();
////        repeatRuleNewInstancesCalculationOngoing = false;
//    }
//
//    public void updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(boolean updateInstances, RepeatRuleObject repeatRuleObject) {
//        updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(updateInstances, repeatRuleObject, repeatRuleObject.getListForNewCreatedRepeatInstances());
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void xupdateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(boolean updateInstances, RepeatRuleObject repeatRuleObject, ItemList motherList) {
////        if (repeatRuleNewInstancesCalculationOngoing) {
////            return; //do nothng if we're already calculating repeat instances for this rule (to avoid that when adding new instances, the rule is invoked again
////        } else {
////            repeatRuleNewInstancesCalculationOngoing = true;
////        }
////        if (repeatRuleOriginator == repeatRuleObject) {
////            repeatRuleOriginator = null; //delete originator once it is no longer valid
////        }
//        //remove the just Completed item *before* generating new ones to ensure that the completed instance is missing in the count of items
//        repeatInstanceItemList.removeItem(repeatRuleObject); //if repeatRuleObject is still there after regeneration, then remove it
//        if (updateInstances) {
////            generateRepeatInstances(true, repeatRuleObject, motherList);
////            generateOrUpdateRepeatInstancesImpl(repeatRuleObject, motherList);
//            updateRepeatInstancesImpl(repeatRuleObject, motherList);
//        }
////        repeatInstanceVector.removeElement(repeatRuleObject); //if repeatRuleObject is still there after regeneration, then remove it
////        if (repeatInstanceVector.size() == 0) { //if vector is empty, then rule is finished and can be deleted() //TODO!!!! NO - what if rule generates bi-monthly events, but only generates 30 days ahead?!
//        if (repeatInstanceItemList.getSize() == 0) { //if vector is empty, then check if rule is finished and can be deleted()
//            if (isRepeatRuleFinished()) {
//                this.delete();
//            } else {
//                //TODO!!!! NO - what if rule generates bi-monthly events, but only generates 30 days ahead?!
//                //force the generation of firstcoming next instance, even if it's beyond the //UI: needed to keep one visible instance to eg edit the repeatRule
//                ASSERT.that("Not implemented");
//            }
//        }
//        changed();
////        repeatRuleNewInstancesCalculationOngoing = false;
//    }
//</editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(RepeatRuleObject repeatRuleObject, ItemList motherList) {
//        updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(true, repeatRuleObject, motherList);
//    }
//    public void updateRemoveItemAndRemoveRuleIfNoMoreInstances(RepeatRuleObject repeatRuleObject, ItemList motherList) {
//        updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(false, repeatRuleObject, motherList);
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//     * removes the repeatRuleObject from the list of instances.
//     * If no more instances left (eg repeatRuleObject was the last 
//     * repeating instance of a task to be completed), 
//     * then deleteRuleAndAllRepeatInstancesExceptThis the RepeatRule itself.
//     * @param repeatRuleObject
//     */
//    void removeRepeatInstanceAndDeleteRepeatRuleIfNoMoreInstances(RepeatRuleject repeatRuleObject) {
//        if (repeatRuleOriginator == repeatRuleObject) {
//            repeatRuleOriginator = null; //delete originator once it is no longer valid
//        }
//        repeatInstanceVector.removeElement(repeatRuleObject);
//        if (repeatInstanceVector.size() == 0) {
//            this.deleteRuleAndAllRepeatInstancesExceptThis();
//        }
//    }
//    /** called when a RepeatRule of an item is changed to recalculate the instances.
//     * If called in cases where the repeatRule has *not* changed, then this call must not
//     * have any side effects.
//     * @param repeatRuleObject
//     * @param motherList
//     */
//    public void repeatRuleChanged(RepeatRuleObject repeatRuleObject, ItemList motherList) {
//
//        if (repeatRuleOriginator != repeatRuleObject) {
//            repeatRuleOriginator = repeatRuleObject; //if RepeatRule was changed from another instance than the originator, then make the new item originator
//        }
//
//    }
//
//    /** called when an item with a RepeatRule is changed, e.g. to update other instances with the
//     * changed values
//     * @param repeatRuleObject
//     * @param motherList
//     */
//    public void itemChanged(RepeatRuleObject repeatRuleObject, ItemList motherList) {
//        //do nothing for the moment
//    }
//
//    /* called when a new Item with a RepeatRule is created, or when an Item has a repeatRule added for the first time. Adds the current item to the
//    instance list and generates the necessary number of additionastances. */
//    public void updateAfterNewItemOrNeepeatRule(RepeatRuleObject repeatRuleObject, ItemList motherList) {
////#mdebug
//        ASSERT.that(repeatRuleOriginator == null, "repeatRuleOriginator must be null when calling newItemOrNewRepeatRule");
////#enddebug
////        repeatRuleOriginator = repeatRuleObject;
//        generateRepeatInstances(true, endDate, endDate, repeatRuleObject, motherList);
//    }
/**
 * called when an item is marked Done. Removes the item from the list of
 * instances. UPdates/regenerates instances, using repeatRuleObject as a
 * reference (this works well when regenerating on Completed, but what about
 * other cases? if no additional instances exist, there is no other option than
 * to use the just completed one, but if one other ones exist, they may be
 * 'cleaner' (less comments/changes to fields) than the just completed. ANYWAY:
 * too complex, so sply always use the just completed one!). If the Done item is
 * the last instance (no new ones generated), then the repeatRule itself is
 * deleted.
 */
//    public void itemDone(RepeatRuleObject repeatRuleObject, ItemList motherList) {
//        //
////        if (repeatRuleOriginator == repeatRuleObject) {
////            repeatRuleOriginator = null; //delete originator once it is no longer valid
////        }
//        removeRepeatInstanceAndDeleteRepeatRuleIfNoMoreInstances(repeatRuleObject);
//    }
// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    private Enumeration xxgenerateDates(long startDate) { //, long start, long subsetBeginning, long subsetEnding) {
//        RepeatRule repeatRule = getRepeatRule();
////        Item completedItem = repeatSourceItem;
//        int countGen = -1;
//        long startDateGen = 0;
//        long subsetBeginningGen = 0;
//        long subsetEndingGen = Long.MAX_VALUE;
////        Enumeration repeats;
//
//        if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE) {//generate one instance
////            startDateGen = editedOrJustCompletedItemUsedAsSeed.getCompletedDate();
//            startDateGen = startDate;
//            if ((getCount() > 0 && getCountOfInstancesGeneratedSoFar() < getCount()) //we have not generated all defined repeats yet
//                    || (getCount() == 0) && getEndDate() != 0 && getEndDate() > MyDate.getNow() // or we have not passed the defined end date yet //TODO!!!!: must compare by date and not time of day to avoid that an instance for today is not generated for example because we've passed the (invisible) time of day hidden in the long time
//                    || (getCount() == 0 && getEndDate() == 0)) //or neither count nor end date have been specified => indefinite repeat
//            {
//                countGen = 1; //generate only a single instance
//                startDateGen = MyDate.getNow(); //generate next instance from now
//            } // else: leave countGen as -1, meaning that countGen won't be used to control the generation, meaning no instances will be generated (the RepeatRule is finished)
//        } else if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_DUE_DATE || getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_SPECIFIED_DATE) {
////            startDateGen = completedItem.getDueDate();
//            if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_DUE_DATE) {
////                startDateGen = referenceItemForDueDateAndFutureCopies.getDueDate();
////                startDateGen = getReferenceObjectForRepeatTime().getRepeatStartTime();
//                startDateGen = getSpecifiedStartDate();
//            } else { //REPEAT_TYPE_FROM_SPECIFIED_DATE
//                startDateGen = startDate;
//            }
////            if (getNumberOfDaysRepeatsAreGeneratedAhead() != 0) {
//            if (useNumberFutureRepeatsGeneratedAhead()) {
//                //how many instances to create ahead: normally as many as missing to reach how many we generate ahead, unless we're approaching the maximum repeats specified (count-instancesSoFar)
////                countGen = Math.min(getNumberFutureRepeatsGeneratedAhead() - getRepeatInstancesSize(), getCount() - getCountOfInstancesGeneratedSoFar()); //generate as many as are missing to reach max simultaneous instances (count)
////                countGen = Math.min(getNumberFutureRepeatsGeneratedAhead() - repeatInstanceVector.getRepeatInstancesSize(), getCount() - getCountOfInstancesGeneratedSoFar()); //generate as many as are missing to reach max simultaneous instances (count)
//                //generate as many as are missing to reach max simultaneous instances (count)
//                countGen = Math.min(getNumberFutureRepeatsGeneratedAhead() - repeatInstanceVector.size(), getCount() - getCountOfInstancesGeneratedSoFar());
////                subsetEndingGen = Integer.MAX_VALUE; //let count be the restricting value, not endDate //-not needed, set as initial value
//            } else {
//                //TODO!!!: what happens if eg set to 14 days, biweekly repeats, is there a risk that no instance will be generated due to next instance falling on 14d+1second?
//                subsetEndingGen = MyDate.getNow() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS;
//            }
//        }
////        subsetBeginningGen = getRepeatInstancesLatestDefinedDueDate(); //only generate instances later than the last ones already generated (?? - what if some instances were deleted, then they'll regenerate now??! Instead store latest date generated for!)
//        if (getLastDateGeneratedFor() != 0) {
//            subsetBeginningGen = getLastDateGeneratedFor(); //only generate instances coming later than the last date generated for last time
//        }
////        if (subsetBeginningGen == 0) {
////            subsetBeginningGen = startDateGen; //actually not needed since the dates() algorithm starts from the biggest of the two dates
////        }
//        subsetEndingGen = Math.min(subsetEndingGen, getEndDate() == 0 ? Long.MAX_VALUE : getEndDate()); //if subsetEndingGen calculated so far is bigger than a defined end date, then use the end date
//        setLastDateGeneratedFor(subsetEndingGen); //TODO!!!!: do not send if genFromCompleted and Long.MAX was selected
//        if (countGen > 0) {
//            repeatRule.setInt(RepeatRule.COUNT, countGen);
//        }
//        if (getInterval() > 0) {
//            repeatRule.setInt(RepeatRule.INTERVAL, getInterval());
//        }
//
//        return repeatRule.dates(startDateGen, subsetBeginningGen, subsetEndingGen);
//    }
//
//    private void setRepeatRuleOriginator(RepeatRuleObject repeatRuleOriginator) {
//        this.repeatRuleOriginator = repeatRuleOriginator;
//        if (!repeatInstanceVector.contains(repeatRuleOriginator)) {
//            repeatInstanceVector.addElement(repeatRuleOriginator);
//        }
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    void generateOrUpdateItemInstances(Item editedOrJustCompletedItemUsedAsSeed) { //, long start, long subsetBeginning, long subsetEnding) {
//        Enumeration repeats = generateDates(editedOrJustCompletedItemUsedAsSeed.getCompletedDate());
//
//        Vector newRepeatInstances = new Vector(); //hold new created instances
//        while (repeats.hasMoreElements()) { //if another instance generated
//            setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //increase count by one
//            Date nextRepeatDate = (Date) repeats.nextElement();
//            Item newInstance;
//            if ((newInstance = getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getRepeatStartTime())) != null) {
//            } else {
//                newInstance = editedOrJustCompletedItemUsedAsSeed.createRepeatCopy(nextRepeatDate.getRepeatStartTime());
////#mdebug
//                Log.l("new instance generated = " + newInstance);
////#enddebug
//                newInstance.commit();
//            }
//        }
//        deleteRepeatInstancesExceptThis(editedOrJustCompletedItemUsedAsSeed); //delete all remaining items (since they are not covered by the new repeatRule)
//        repeatInstances = null; //Help GC
//        repeatInstances = newRepeatInstances;
////        }
//    }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * generates or updates the list of instances for this repeatRule.
//     * repeatRuleOwner is either the item just completed, so used to generate a
//     * new instance, or it's the owner of the (possibly edited, or just created)
//     * repeatRule.
//     *
//     * @param firstTime
//     * @param repeatRuleChanged
//     * @param repeatObjectChange
//     * @param repeatRuleOwner
//     */
////    void xxgenerateOrUpdateRepeatInstances(RepeatRuleObject repeatRuleOwner, ItemList listForNewCreatedRepeatInstances) {
////        if (isDirty()) { // || !isCommitted()) { //-not possible to be both committed() and dirty
////// <editor-fold defaultstate="collapsed" desc="comment">
//////            if (repeatRuleNewInstancesCalculationOngoing) {
//////                return; //do nothng if we're already calculating repeat instances for this rule (to avoid that when adding new instances, the rule is invoked again //TODO!!!: find a better solution for this?!
//////            } else {// </editor-fold>
////            repeatRuleNewInstancesCalculationOngoing = true;
////            generateRepeatInstancesImpl(repeatRuleOwner, listForNewCreatedRepeatInstances);
////            repeatRuleNewInstancesCalculationOngoing = false;
////// <editor-fold defaultstate="collapsed" desc="comment">
//////            }
////            //    void generateRepeatInstances(boolean firstTime, /*boolean repeatRuleChanged, boolean repeatObjectChange,*/ /*long startDate, long endDate,*/ RepeatRuleObject repeatRuleOwner, ItemList listForNewCreatedRepeatInstances) { //, long start, long subsetBeginning, long subsetEnding) {
//////            setLastDateGeneratedFor(Long.MIN_VALUE); //reset to ensure values are generated all over when rule is changed
//////            generateOrUpdateRepeatInstancesImpl(repeatRuleOwner, listForNewCreatedRepeatInstances);
//////            generateRepeatInstancesImpl(repeatRuleOwner, listForNewCreatedRepeatInstances);// </editor-fold>
////// <editor-fold defaultstate="collapsed" desc="comment">
////////    void generateRepeatInstances(long startDate, long endDate, RepeatRuleObject repeatRuleOwner) { //, long start, long subsetBeginning, long subsetEnding) {
//////        Enumeration repeats;
////////        ItemList listForNewCreatedRepeatInstances = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //-can't implement this for WorkSlots
//////        if (getRepeatFromDueOrCompleted() == REPEAT_TYPE_FROM_COMPLETED_DATE && repeatRuleOwner instanceof Item) {
//////            repeats = generateDates(((Item) repeatRuleOwner).getCompletedDate());
//////        } else {
//////            repeats = generateDates(repeatRuleOwner.getRepeatStartTime()); //Completed: just
//////        }
//////        if (firstTime) {
////////#mdebug
//////            ASSERT.that(repeatInstanceVector.size() == 0, "repeatInstanceVector not empty on firstTime call");
////////#enddebug
//////            repeatInstanceVector.addElement(repeatRuleOwner); //first time the vector is created, explicitly add the 'owner'/originator item
//////            repeatRuleOriginator = repeatRuleOwner;
//////        }
//////        RepeatInstanceVector newRepeatInstanceList = new RepeatInstanceVector(); //hold new created instances
//////        if (repeatRuleOriginator != null) { //if repeatRuleOriginator still exists, then transfer it explicitly to new list (since it will not be generated by the rule)
//////            newRepeatInstanceList.addElement(repeatRuleOriginator);
//////            repeatInstanceVector.removeElement(repeatRuleOriginator);
//////        }
//////        while (repeats.hasMoreElements()) { //if another instance generated
//////            setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //increase count by one
//////            Date nextRepeatDate = (Date) repeats.nextElement();
//////            RepeatRuleObject newRepeatInstance;
//////            if ((newRepeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime())) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these
//////                //newRepeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime())
////////#mdebug
//////                Log.l("repeat instance reused = " + newRepeatInstance);
////////#enddebug
//////            } else { //no previous instance with same date exists, create a new one
//////                newRepeatInstance = repeatRuleOwner.createRepeatCopy(nextRepeatDate.getTime());
////////                ItemList parentItemList = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //UI: add repeatInstances to the parentList of the source
//////                if (listForNewCreatedRepeatInstances != null) {
////////                    int repeatRuleObjectIndex = parentItemList.getItemIndex(repeatRuleObject);
////////                    parentItemList.addItemAtIndex(newInstance, repeatRuleObjectIndex+1); //UI: +1: insert new repeatInstance after the previous one (or
//////                    listForNewCreatedRepeatInstances.addItem(newRepeatInstance); //UI: insert new repeatInstance at the end of the list
//////                }
////////#mdebug
//////                Log.l("**new repeat instance generated = " + newRepeatInstance);
////////#enddebug
////////                newInstance.commit(); //- not necessary because done when inserted into a committed list
//////            }
//////            newRepeatInstanceList.addElement(newRepeatInstance);
//////        }
//////
//////        repeatInstanceVector.deleteRepeatInstancesExceptThis(repeatRuleOwner); //delete all remaining items (since they are not covered by the new repeatRule)
//////        repeatInstanceVector = null; //Help GC
//////        repeatInstanceVector = newRepeatInstanceList;
//////        repeatRuleNewInstancesCalculationOngoing = false;
//////        changed(); //ensures auto-save// </editor-fold>
////        }
////    }
    //</editor-fold>
