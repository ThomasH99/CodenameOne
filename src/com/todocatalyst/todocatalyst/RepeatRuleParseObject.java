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
import com.parse4cn1.ParseException;
//import com.codename1.ui.List;
import com.parse4cn1.ParseObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    public static String CLASS_NAME = "RepeatRule";
    public static String REPEAT_RULE = "Repeat rule";

    final static int REPEAT_TYPE_NO_REPEAT = 0; //11;
    final static int REPEAT_TYPE_FROM_COMPLETED_DATE = 1; //11;
    final static int REPEAT_TYPE_FROM_DUE_DATE = 2;// 33;
//    final static int REPEAT_TYPE_FROM_SPECIFIED_DATE = 3; //7; //TODO: implement as additional option?

    private final static String PARSE_SPECIFIED_START_DATE = "specifiedStartDate";
    private final static String PARSE_REPEAT_TYPE = "repeatType";
    private final static String PARSE_FREQUENCY = "frequency";
    private final static String PARSE_INTERVAL = "interval";

    private final static String PARSE_END_DATE = "endDate";
    private final static String PARSE_COUNT = "count";

    private final static String PARSE_DAYS_IN_WEEK = "daysInWeek";
    private final static String PARSE_WEEKS_IN_MONTH = "weeksInMonth";
    private final static String PARSE_WEEKDAYS_IN_MONTH = "weekdaysInMonth";

    private final static String PARSE_MONTHS_IN_YEAR = "monthsInYear";
    private final static String PARSE_DAY_IN_MONTH = "dayInMonth";
    private final static String PARSE_DAY_IN_YEAR = "dayInYear";

    private final static String PARSE_NUMBER_FUTURE_REPEATS_TO_GENERATE_AHEAD = "numberFutureRepeatsToGenerateAhead";
    private final static String PARSE_NUMBER_OF_DAYS_TO_GENERATE_AHEAD = "numberOfDaysRepeatsAreGeneratedAhead";

    private final static String PARSE_REPEAT_INSTANCE_ITEMLIST = "repeatInstanceItemList";
//    private final static String PARSE_DATES_LIST = "datesList";
//    final static String LAST_DATE_GENERATED_FOR = "lastDateGeneratedFor";
    private final static String PARSE_LAST_DATE_GENERATED = "lastGeneratedDate";
    private final static String PARSE_NEXTCOMING_REPEAT_DATE = "nextcomingDate"; //the nextcoming date for which a new repeat instance, also used to check if a RepeatRule need to generate new repeat instances
    final static String PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR = "countOfInstancesGeneratedSoFar";
    final static String PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED = "dateLastCompleted";
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
    private final static int[] WEEKS_IN_MONTH_NUMBERS = {RepeatRule.FIRST, RepeatRule.SECOND, RepeatRule.THIRD, RepeatRule.FOURTH, RepeatRule.FIFTH,
        RepeatRule.LAST, RepeatRule.SECONDLAST, RepeatRule.THIRDLAST, RepeatRule.FOURTHLAST, RepeatRule.FIFTHLAST};
    private final static int[] WEEKS_IN_MONTH_NUMBERS_SHORT = {RepeatRule.FIRST, RepeatRule.SECOND, RepeatRule.THIRD, RepeatRule.FOURTH, RepeatRule.FIFTH, RepeatRule.LAST};
//    private final static String[] WEEKS_IN_MONTH_NAMES = {FIRST, SECOND, THIRD, FOURTH, FIFTH, LAST, SECOND_LAST, THIRD_LAST, FOURTH_LAST, FIFTH_LAST};
    private final static String[] WEEKS_IN_MONTH_NAMES = {FIRST_SHORT, SECOND_SHORT, THIRD_SHORT, FOURTH_SHORT, FIFTH_SHORT, LAST_SHORT, SECOND_LAST_SHORT, THIRD_LAST_SHORT, FOURTH_LAST_SHORT, FIFTH_LAST_SHORT};
    private final static String[] WEEKS_IN_MONTH_NAMES_SHORT = {FIRST_SHORT, SECOND_SHORT, THIRD_SHORT, FOURTH_SHORT, FIFTH_SHORT, LAST_SHORT};
    //NB! below DAY_IN_WEEK_NUMBERS list requires that MyDate sequence of weekdays is the same!!
    private final static int[] DAY_IN_WEEK_NUMBERS_MONDAY_FIRST = {RepeatRule.MONDAY, RepeatRule.TUESDAY, RepeatRule.WEDNESDAY, RepeatRule.THURSDAY, RepeatRule.FRIDAY, RepeatRule.SATURDAY, RepeatRule.SUNDAY};
    private final static int[] DAY_IN_WEEK_NUMBERS_MONDAY_FIRST_INCL_WEEKDAYS = {RepeatRule.MONDAY, RepeatRule.TUESDAY, RepeatRule.WEDNESDAY,
        RepeatRule.THURSDAY, RepeatRule.FRIDAY, RepeatRule.SATURDAY, RepeatRule.SUNDAY, RepeatRule.WEEKDAYS, RepeatRule.WEEKENDS};
    //NB! below DAY_IN_WEEK_NUMBERS list requires that MyDate sequence of months is the same!!
    private final static int[] MONTH_IN_YEAR_NUMBERS = {RepeatRule.JANUARY, RepeatRule.FEBRUARY, RepeatRule.MARCH, RepeatRule.APRIL, RepeatRule.MAY, RepeatRule.JUNE, RepeatRule.JULY, RepeatRule.AUGUST, RepeatRule.SEPTEMBER, RepeatRule.OCTOBER, RepeatRule.NOVEMBER, RepeatRule.DECEMBER};
    private final static int[] REPEAT_RULE_FREQUENCY_NUMBERS = {RepeatRule.DAILY, RepeatRule.WEEKLY, RepeatRule.MONTHLY, RepeatRule.YEARLY};
//    private final static String[] REPEAT_RULE_FREQUENCY_NAMES = {"on a daily basis", "on a weekly basis", "on a monthly basis", "on a yearly basis"};
    private final static String[] REPEAT_RULE_FREQUENCY_NAMES = {"Daily", "Weekly", "Monthly", "Yearly"};
//    private final static int[] REPEAT_RULE_TYPE_NUMBERS = {MyRepeatRule.REPEAT_TYPE_FROM_COMPLETED_DATE, MyRepeatRule.REPEAT_TYPE_FROM_DUE_DATE, MyRepeatRule.REPEAT_TYPE_FROM_SPECIFIED_DATE};
//    private final static String[] REPEAT_RULE_TYPE_NAMES = {"completion date", "due date", "today"};
    private final static int[] REPEAT_RULE_TYPE_NUMBERS = {RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT, RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE, RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE};
//    private final static String[] REPEAT_RULE_TYPE_NAMES = {"completion date", "due date"}; 
//    private final static String[] REPEAT_RULE_TYPE_NAMES = {"completion", "due"}; //TODO!!: add repeat from TODAY, e.g. I create a new task today and it should repeat every two weeks from now (<=> DueDate==today??)
    private final static String REPEAT_RULE_NO_REPEAT = "None";
    private final static String[] REPEAT_RULE_TYPE_NAMES = {REPEAT_RULE_NO_REPEAT, "On completion", " From date"}; //TODO!!: add repeat from TODAY, e.g. I create a new task today and it should repeat every two weeks from now (<=> DueDate==today??)

    public RepeatRuleParseObject() {
        super(CLASS_NAME);
        //TODO!!!! revisit default values
        if (false) { //setting these values falsely marks a new RR as dirty
//            setRepeatType(REPEAT_TYPE_FROM_COMPLETED_DATE);
            setRepeatType(REPEAT_TYPE_NO_REPEAT);
            setFrequency(RepeatRule.DAILY); //default: daily repeat
            setInterval(1); //default: every day
            setNumberOfRepeats(Integer.MAX_VALUE); //= 0; //1; //-start with 0 (undefined => starts with Repeat forever)
//        setEndDate(Long.MAX_VALUE);
            setEndDate(MyDate.MAX_DATE);
            setDaysInWeek(0);
            setWeeksInMonth(0);
            setWeekdaysInMonth(0);
            setMonthsInYear(0);
            setDayInMonth(0);
            setDayInYear(0); //TODO: choosing the middle day of the year, eg365/2=182 would make it faster to choose right date in a scrolling list, but is less intuitive
            setNumberFutureRepeatsToGenerateAhead(1);
            setNumberOfDaysRepeatsAreGeneratedAhead(0);
            setSpecifiedStartDate(new Date()); //default start date today/now
//        setLastDateGeneratedFor(Long.MIN_VALUE); //=0; //
//        setLastGeneratedDate(Long.MIN_VALUE); //=0; //
            setLastGeneratedDate(new Date(MyDate.MIN_DATE)); //=0; //
            setListOfUndoneRepeatInstances(new ArrayList()); // = 1; //
//            setDatesListXXX(new ArrayList()); // = 1; //
        }
    }

    public RepeatRuleParseObject(RepeatRuleParseObject repeatRule) {
        this();
        if (repeatRule != null) {
            repeatRule.copyMeInto(this);
        }
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
        if (useCount()) {
//            repeatRule.setInt(RepeatRule.COUNT, getCount()-getCountOfInstancesGeneratedSoFar()); //-getCountOfInstancesGeneratedSoFar() since we call this repeatedly but still want to limit the overall amount generated
            repeatRule.setInt(RepeatRule.COUNT, getNumberOfRepeats());
//        } else if (getEndDate() != Long.MAX_VALUE) {
        } else if (getEndDate() != MyDate.MAX_DATE) {
//            repeatRule.setDate(RepeatRule.END, getEndDate());
            repeatRule.setDate(RepeatRule.END, MyDate.getEndOfDay(getEndDateD()).getTime()); //set to end of day to not miss 
//            repeatRule.setInt(RepeatRule.COUNT, 0); //0 means repeat forever, or until end date //-necessary when end date is set? (is default count == 0?)
        } else if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
            repeatRule.setInt(RepeatRule.COUNT, 1);
        }
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
        return repeatRule;
    }

    private RepeatRule getRepeatRuleOLD() {
        RepeatRule repeatRule = new RepeatRule();
        repeatRule.setInt(RepeatRule.FREQUENCY, getFrequency());
        //TODO!!!: not necessary to set below values since they're also set when calculating dates
        if (useCount()) {
//            repeatRule.setInt(RepeatRule.COUNT, getCount()-getCountOfInstancesGeneratedSoFar()); //-getCountOfInstancesGeneratedSoFar() since we call this repeatedly but still want to limit the overall amount generated
            repeatRule.setInt(RepeatRule.COUNT, getNumberOfRepeats());
//        } else if (getEndDate() != Long.MAX_VALUE) {
        } else if (getEndDate() != MyDate.MAX_DATE) {
//            repeatRule.setDate(RepeatRule.END, getEndDate());
            repeatRule.setDate(RepeatRule.END, MyDate.getEndOfDay(getEndDateD()).getTime()); //set to end of day to not miss 
//            repeatRule.setInt(RepeatRule.COUNT, 0); //0 means repeat forever, or until end date //-necessary when end date is set? (is default count == 0?)
        } else if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
            repeatRule.setInt(RepeatRule.COUNT, 1);
        }
        if (getInterval() > 1) {
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
        return repeatRule;
    }

    public void setListOfUndoneRepeatInstances(List list) {
        if (list != null && !list.isEmpty()) {
            put(PARSE_REPEAT_INSTANCE_ITEMLIST, list);
        } else {
            remove(PARSE_REPEAT_INSTANCE_ITEMLIST);
        }
    }

    /**
     * keep track of undone (not Done/Cancelled/Deleted) repeat instances (not used for WorkSlots?!). Used
     * to delete repeat instances if repeatRule is deleted, or to reuse existing
     * instances if the rule is changed.
     *
     * @return
     */
//    public List<ParseObject> getListOfUndoneRepeatInstances() {
//    public List<ItemAndListCommonInterface> getListOfUndoneRepeatInstances() {
//    public List<ItemAndListCommonInterface> getListOfUndoneRepeatInstances() {
    public List<RepeatRuleObjectInterface> getListOfUndoneRepeatInstances() {
        //TODO!!!! used for WorkSlots? (is externalized as if WorkSlots could be in list)
        List list = getList(PARSE_REPEAT_INSTANCE_ITEMLIST);
        if (list != null) {
//            DAO.getInstance().fetchAllElementsInSublist(list, false);
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
        } else {
            list = new ArrayList();
        }
        return list;
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
    private void removeUnneededTasks(List<Date> newDates, Date subsetBeginDate, int numberInstancesToGenerate) {
        boolean modified = false;
        if (!newDates.isEmpty()) {
            Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
            ASSERT.that(date.getTime() >= subsetBeginDate.getTime());
//            ASSERT.that(subsetEndDate==null || date.getTime() <= subsetEndDate.getTime());
            if (date.getTime() == subsetBeginDate.getTime()) {
                date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
                modified = true;
            } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
                while (newDates.size() > numberInstancesToGenerate) {
                    newDates.remove(newDates.size() - 1); //remove extraneous elements
                    modified = true;
                }
            }
        }
//        return modified;
    }

    private List<Date> generateListOfDates(Date subsetBeginDate, Date subsetEndDate, int numberInstancesToGenerate) {
        return generateListOfDates(subsetBeginDate, subsetEndDate, numberInstancesToGenerate, false);
    }

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
     * @return
     */
    private List<Date> generateListOfDates(Date subsetBeginDate, Date subsetEndDate, int numberInstancesToGenerate, boolean genAtLeastOneDate) {
        RepeatRule repeatRule = getRepeatRule();
        int count;
//<editor-fold defaultstate="collapsed" desc="comment">
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
        count = numberInstancesToGenerate;
        if (count < Integer.MAX_VALUE) {
            count++; // +1 since we may generate the last generated date (subsetBeginDate) again 
        }
        count = Math.max(count, 1); //COUNT must never be <1
        repeatRule.setInt(RepeatRule.COUNT, count);
        Date startDate = getSpecifiedStartDateD();
//<editor-fold defaultstate="collapsed" desc="comment">
//        long subsetBegin = endRepeatDate != null && endRepeatDate.getTime() != 0 ? endRepeatDate.getTime() : MAX_DATE;
//        long subsetBegin = subsetBeginDate.getTime();
//        long subsetEnd = subsetEndDate != null && subsetEndDate.getTime() != 0 ? subsetEndDate.getTime() : MAX_DATE;
//        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), lastGeneratedDate.getTime(), endDate); //CREATE new dates
//</editor-fold>

        Vector<Date> newDates = repeatRule.datesAsVector(startDate.getTime(), subsetBeginDate.getTime(), subsetEndDate.getTime()); //CREATE new dates

        //if first generated date equals subsetStart, then remove it. And remove superfluous instances
        if (false && !newDates.isEmpty()) {
            Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
            ASSERT.that(date.getTime() >= subsetBeginDate.getTime());
            ASSERT.that(subsetEndDate == null || date.getTime() <= subsetEndDate.getTime());
            if (date.getTime() == subsetBeginDate.getTime()) {
                date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
            } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
                if (numberInstancesToGenerate != Integer.MAX_VALUE) {
                    ASSERT.that(newDates.size() <= numberInstancesToGenerate + 1, "newDates should max have 1 date more than count");
                    while (newDates.size() > numberInstancesToGenerate) {
                        newDates.remove(newDates.size() - 1); //remove extraneous elements
                    }
                } else {
                    ASSERT.that(newDates.get(newDates.size() - 1).getTime() <= subsetEndDate.getTime(), "**");
                    //when generating towards endDate, will always generate right number?!
                }
            }
        }
        removeUnneededTasks(newDates, subsetBeginDate, numberInstancesToGenerate);
//
        //if no new date before endDate, and rule not terminated, then regenerate again with infinite endDate and count==1 (or 2?)
//        if (genAtLeastOneDate && numberInstancesToGenerate == Integer.MAX_VALUE && newDates.size() == 0) {
        if (genAtLeastOneDate && numberInstancesToGenerate > 0 && newDates.size() == 0) {
            repeatRule.setInt(RepeatRule.COUNT, 2); //gen 2 dates since 1st may be startDate
            newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginDate.getTime(), MyDate.getEndOfDay(getEndDateD()).getTime()); //CREATE new dates
            if (false && !newDates.isEmpty()) {
                Date date = newDates.get(0); //take first date in list (corresponds to lastGeneratedDate)
                if (date.getTime() == subsetBeginDate.getTime()) {
                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
                } else { //happens when RR doesn't generate the susetBeginDate (e.g. rule starts on Monday, but repeats on Tuesdays)
                    ASSERT.that(newDates.size() <= numberInstancesToGenerate + 1, "newDates should max have 1 date more than count");
                    while (newDates.size() > numberInstancesToGenerate) {
                        newDates.remove(newDates.size() - 1); //remove extraneous elements
                    }
                }
            }
            removeUnneededTasks(newDates, subsetBeginDate, numberInstancesToGenerate);
        }
//
        return new ArrayList(newDates);
    }

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
    private Date getFirstNotEqualDate(List<Date> newDates, Date dateCompleted) {
//        Date date=null;
        if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
            Date date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//            if (!date.equals(dateCompleted)) {
            if (date.getTime() != dateCompleted.getTime()) {
                return date;
            } else {
                if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
                    return date;
                }
            }
        }
        return null;
    }

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
    private Date getNextCompletedFromDate(Date dateCompleted) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && isRepeatRuleTerminatedXXX()) { //NO need to test, no dates should be generated if terminated
//            return null;
//        }
//</editor-fold>
        assert dateCompleted.getTime() != 0 : "called before dateCompleted was set on Item/WorkSlot";
        RepeatRule repeatRule = getRepeatRule();
        repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)

        Vector<Date> newDates = repeatRule.datesAsVector(dateCompleted.getTime(), dateCompleted.getTime(), MyDate.MAX_DATE); //CREATE new dates
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//            date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//            if (date.equals(dateCompleted)) {
//                if (!newDates.isEmpty()) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
//                    date = newDates.remove(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that //NO eg when generating from 1st in month, next month shouldn't be dropped
//                } else {
//                    date = null;
//                }
//            }
//</editor-fold>
        Date date = getFirstNotEqualDate(newDates, dateCompleted);
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && date != null) {
//            setLastGeneratedDate(date); //store that date for next iteration (if date>endDate this will indicate the RR has terminated)
//            if (date.getTime() > getEndDate()) {
//                return null;
//            } else {
//                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);
//                return date;
//            }
//        }
//        } else {
//            ASSERT.that(false, "shouldn't happen?!");  //otherwise set numberRepeats to MAX or endDate to Max to indicate rule has terminated
//        return null;
//</editor-fold>
        return date;
//<editor-fold defaultstate="collapsed" desc="comment">
//        }
//        return null;
//        if (newDates.size()>=2) { //if vector not empty AND we repeat from due date (if from completed, we don't skip first date)
////        if (!newDates.isEmpty()) {
//             date = newDates.remove(1); //discard first date (, use setake first date in list
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
//</editor-fold>
    }

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
    /**
     * show a pop-up dialog with the generated dates, shown with 20 each time
     */
    public void showRepeatDueDates() {
        int MAX_REPEATS = 100;
        showRepeatDueDates(MAX_REPEATS);
    }

    private List<Date> createDates(int maxRepeats) {
        Date startDate = getSpecifiedStartDateD();
        Date endDate = getEndDateD();
//        nextDate = startDate;
        int repeats = Math.min(calcNumberOfRepeats(), maxRepeats);
//        return generateListOfDates(startDate, calcSubsetEndDate(startDate), repeats,true);
        return generateListOfDates(startDate, endDate, repeats, true);
    }

    private void listDates(List<Date> dates) {
        for (Date date : dates) {
            Log.p(MyDate.formatDateNew(date, false, true, true, true, MyPrefs.dateShowDatesInUSFormat.getBoolean()));
        }
    }

    private void listDates(int nbInstances) {
        listDates(createDates(nbInstances));
    }

    public void showRepeatDueDates(int maxInstances) {
        Vector datesVector = new Vector();
//        int i = 0;
        int nextI = 0;
        int nbDatesToShowInEachStep = 20;

        Command showMoreCmd = new Command("Show " + nbDatesToShowInEachStep + " more");
//        Date nextDate = new Date();
//        Date startDate = new Date();
//        nextDate = startDate;
//        int repeats = Math.min(calcNumberOfRepeats(), MAX_REPEATS);
//        List<Date> dates = generateListOfDates(startDate, calcSubsetEndDate(startDate), repeats);
        List<Date> dates = createDates(maxInstances);
        do {
            for (int i = nextI, size = Math.min(dates.size(), nextI + nbDatesToShowInEachStep - 1); i < size; i++) { //Math.min(dates.size in case the dates list is shorter than nbDatesToShowInEachStep
                datesVector.addElement(MyDate.formatDateNew(dates.get(i), false, true, true, true, MyPrefs.dateShowDatesInUSFormat.getBoolean()));
            }
            nextI += nbDatesToShowInEachStep;
        } while (Dialog.show("Dates", new com.codename1.ui.List(datesVector), new Command[]{new Command("Exit"), nextI >= maxInstances ? showMoreCmd : new Command("No more")}) == showMoreCmd);
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
    /**
     * returns true if repeatInstance is in the list of Active (Undone) repeat
     * instances, or if this is a new rule (no generated instances yet). Used to
     * test eg if the repeatRule of an already done (and therefore disconnected
     * from the repeatRule) task is being attempted to edit.
     *
     * @param repeatInstance
     * @return
     */
    public boolean isRepeatInstanceInListOfActiveInstances(RepeatRuleObjectInterface repeatInstance) {
//        return getListOfUndoneRepeatInstances().contains(repeatInstance)
//                || (getTotalNumberOfInstancesGeneratedSoFar() == 0 && getListOfUndoneRepeatInstances().isEmpty());
        boolean contains = getListOfUndoneRepeatInstances().contains(repeatInstance);
//        assert getTotalNumberOfInstancesGeneratedSoFar() != 0 || getListOfUndoneRepeatInstances().isEmpty() : "both should always be";
//        ASSERT.that(!(getListOfUndoneRepeatInstances().isEmpty()) || getTotalNumberOfInstancesGeneratedSoFar() == 0 , "if empty, counter should be 0");
        boolean firstTime = (getTotalNumberOfInstancesGeneratedSoFar() == 0 && getListOfUndoneRepeatInstances().isEmpty());
//        boolean repeatFromCompleted = getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE;
        return contains || firstTime;
//        return getListOfUndoneRepeatInstances().contains(repeatInstance);
    }

    /**
     * can the repeatRule be edited for this object?
     *
     * @param repeatInstance
     * @return
     */
    public boolean canRepeatRuleBeEdited(RepeatRuleObjectInterface repeatInstance) {
        boolean contains = getListOfUndoneRepeatInstances().contains(repeatInstance);
        boolean firstTime = (getTotalNumberOfInstancesGeneratedSoFar() == 0 && getListOfUndoneRepeatInstances().isEmpty());
//        boolean repeatFromCompleted = getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE;
        return contains || firstTime; // || repeatFromCompleted;
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
    private static RepeatRuleObjectInterface reuseOrMakeNextRepeatInstance(RepeatRuleObjectInterface repeatRuleOriginator,
            List<RepeatRuleObjectInterface> oldRepeatInstanceList, Date nextRepeatTime) {

        if (false && nextRepeatTime == null) {
            ASSERT.that(false, "shouldn't happen");
            return null;
        }

        RepeatRuleObjectInterface repeatInstance;

        if (MyPrefs.repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule.getBoolean() && oldRepeatInstanceList.size() >= 1) { //reuse already generated instances
            repeatInstance = oldRepeatInstanceList.remove(0);
            if (false && repeatInstance.equals(repeatRuleOriginator)) {
                return repeatInstance; //don't alter the originator==owner
            }            //no need to add them to any lists, they are simply left where they already are
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                if (repeatInstance.equals(repeatRuleOriginator)) {
////                newRepeatInstanceList.add(repeatRuleOriginator); //don't change the date for the originator //NO, no matter which instance with the RepeatRule is changed, update/reuse all current instances (otherwise the instances might become inconsistent w the rule def)
//                } else {
//                    repeatInstance.setRepeatStartTime(nextRepeatTime); //upate repeat time
////                newRepeatInstanceList.add(oldInstance);
//                }
//            }
//</editor-fold>
//            repeatInstance.setRepeatStartTime(nextRepeatTime); //upate repeat time
            repeatInstance.updateRepeatInstanceRelativeDates(nextRepeatTime); //upate repeat time
        } else {
            repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
//            repeatInstance.setRepeatRuleForRepeatInstance(this); //link back to repeat rule //NO, done in createRepeatCopy
//<editor-fold defaultstate="collapsed" desc="comment">
//insert new created instance in appropriate list
//DONE!!! how to insert 'naturally' into a list? In order generated (first dates first), either at beginning/end of list, or right after originator?? -> handled in
//                    listForNewCreatedRepeatInstances.addToList(insertIndex + instancesGeneratedCount, (ItemAndListCommonInterface) repeatInstance); //+count => insert each instance *after* the previously inserted one
//                    repeatRuleOriginator.getInsertNewRepeatInstancesIntoList().add(repeatInstance);
//            repeatRuleOriginator.insertIntoListAndSaveListAndInstance(repeatInstance);
//                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
//</editor-fold>
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
    /**
     * delete any 'left over' instances //TODO check that deleting them doesn't
     * affect
     *
     * @param oldUndoneRepeatInstanceList
     */
    private void deleteSuperfluousRepeatInstances(List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList) {
        while (oldUndoneRepeatInstanceList.size() > 0) {
            RepeatRuleObjectInterface obsoleteInstance = oldUndoneRepeatInstanceList.remove(0);
            if (obsoleteInstance instanceof ParseObject) {
                if (obsoleteInstance instanceof Item && ((Item) obsoleteInstance).getActual() != 0) { //for Items, not WorkSlots
                    ((Item) obsoleteInstance).setStatus(ItemStatus.CANCELLED);   //Cancel instead of delete if time has been registered (to avoid losing it)
                    DAO.getInstance().save((Item) obsoleteInstance); //must save it
                } else {
//                obsoleteInstance.deleteRepeatInstance();   //TODO!!!! check that this deletes it in all lists, categories etc!
                    DAO.getInstance().delete((ParseObject) obsoleteInstance);   //TODO!!!! check that this deletes it in all lists, categories etc!
                }
            } //else: not saved to Parse
        }
    }

    public void updateRepeatInstancesWhenRuleWasCreatedOrEdited(RepeatRuleObjectInterface repeatRuleOriginator) {
//<editor-fold defaultstate="collapsed" desc="comment">
        /**
         * //algorithm: ~refresh (reuse existing rule, to avoid
         * creating/deleting when edited) //get any already created repeat
         * instances //create dates for new/edited rule //for as many instances
         * as should be generated (encapsulate this! w moreInstances() and
         * ruleFinished(), moreInstances based on either #futureInstances vs
         * length of list or #daysAhead vs lastDayInList) //if an existing
         * instance exists, use it and set the date (whether new date is same or
         * not) //if no more instances, generate one //at the end, if there are
         * still existing instances, delete them (or cancel if actual!=0)
         *
         */
//</editor-fold>
        assert (true || getTotalNumberOfInstancesGeneratedSoFar() > 0); // ">0" - not the case if only one simultaneous instance

        if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) { //handle case where a rule is switched from repeat from due, to repeat from completed
            //Called either on very first time (and should do nothing other than add originator to listOfUndone), or when rule is changed from Due to Completed
            List undoneList = getListOfUndoneRepeatInstances();
            ASSERT.that(undoneList.size() == 0 || undoneList.contains(repeatRuleOriginator), "getListOfUndoneRepeatInstances does not contain originator=" + repeatRuleOriginator + " list=" + undoneList);
            int instancesToBeDeleted = undoneList.size();
            if (instancesToBeDeleted > 0) {
                undoneList.remove(repeatRuleOriginator); //remove repeatRuleOriginator from list so it won't get deleted
                instancesToBeDeleted = undoneList.size();
                while (undoneList.size() > 0) { //delete all already generated instances
                    ParseObject repeatRuleObject = (ParseObject) undoneList.remove(0);
                    DAO.getInstance().delete(repeatRuleObject);
                }
            }
            undoneList.add(repeatRuleOriginator); //add, or re-add
            setListOfUndoneRepeatInstances(undoneList);
            //reset all counters in case the rule is later switched back to REPEAT_TYPE_FROM_DUE_DATE
            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() - instancesToBeDeleted);
            if (false) {
                setLatestDateCompletedOrCancelled(null); //no need to reset this
            }//            setLastGeneratedDateIfGreaterThanLastDate(null);
            if (repeatRuleOriginator.getRepeatStartTime(false).getTime() != 0) {
                setLastGeneratedDate(repeatRuleOriginator.getRepeatStartTime(false)); //set LastGeneratedDate to due date of the originator
            }
            DAO.getInstance().save(this);
        } else { //getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE
            //new rule, changed REPEAT_TYPE_FROM_DUE_DATE, or rule was changed from REPEAT_TYPE_FROM_COMPLETED_DATE to REPEAT_TYPE_FROM_DUE_DATE
//            List<? extends RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
//            List<ItemAndListCommonInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances
            List<RepeatRuleObjectInterface> oldUndoneRepeatInstanceList = getListOfUndoneRepeatInstances();  //reuse any existing instances

            ArrayList newRepeatInstanceList = new ArrayList(); //hold new created instances
            setLastGeneratedDate(null); //when rule is edited, and recalculated, first reset the previous value of lastDateGenerated
//            RepeatRuleObjectInterface repeatInstance;
            int oldTotalNumberInstancesGeneratedSoFar = getTotalNumberOfInstancesGeneratedSoFar(); //- oldRepeatInstanceList.size()-1; //old instances may be reused or discarded so remove them from the count (and add newRepeatInstanceList.size() later), -1: alwlays remove the initial
            int oldNumberOfUndoneRepeatInstances = oldUndoneRepeatInstanceList.size();

            //first time we generate instances for a rule, we need to add the originator and only generate additional instances
            //next time all dates mu be generated and the originator may change date
            boolean firstTime = oldUndoneRepeatInstanceList.size() == 0; // || !oldUndoneRepeatInstanceList.contains(repeatRuleOriginator);

            Date startRepeatFromTime;
            List<Date> dates;
            int recalcCurrentDate = 0;
            if (firstTime) {
                newRepeatInstanceList.add(repeatRuleOriginator);
                oldNumberOfUndoneRepeatInstances++; //add 1 to compensentae for adding originator (when deducting newRepeatInstanceList.size()
                startRepeatFromTime = repeatRuleOriginator.getRepeatStartTime(false);
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) {
//                    if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
////                    dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead());
//                        dates = generateListOfDates(startRepeatFromTime, calcSubEndDate(), calcNumberOfRepeats());
//                    } else {
//                        Date subEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
////                    dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
//                        dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
//                    }
//                }
//</editor-fold>
            } else {
                startRepeatFromTime = getLatestDateCompletedOrCancelled(); //Re-start repeat from the last date a task was cancelled/done
                recalcCurrentDate = 1;
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) {
//                    if (useNumberFutureRepeatsToGenerateAhead()) { //generate a certain number of instances
//                        dates = generateListOfDates(startRepeatFromTime, null, getNumberFutureRepeatsToGenerateAhead() + 1); //1: need to *re-*generate the full number of dates if possible (including possibly the originator)
//                    } else {
//                        Date subEndDate = new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS);
//                        dates = generateListOfDates(startRepeatFromTime, subEndDate, -1);
//                    }
//                    ASSERT.that(oldUndoneRepeatInstanceList.contains(repeatRuleOriginator));
//                    //move to head of list to make sure the originator becomes the first instance in the repeat sequence:
//                    if (false) { //DON'T since thiws may/will not keep the items in due date order
//                        oldUndoneRepeatInstanceList.remove(repeatRuleOriginator);
//                        oldUndoneRepeatInstanceList.add(0, repeatRuleOriginator);
//                    }
//                }
//</editor-fold>
            }
            dates = generateListOfDates(startRepeatFromTime, calcSubsetEndDate(startRepeatFromTime), calcNumberOfRepeats() + recalcCurrentDate);

            //not the first time, so start repetition from the first still active instance
            ASSERT.that(true, "shouldn't happen?! repeatRule=" + this + ", item=" + repeatRuleOriginator);

            RepeatRuleObjectInterface newRepeatInstance;
            RepeatRuleObjectInterface repeatRef = repeatRuleOriginator; //used to insert multiple instances one after the other
            Date nextRepeatTime = null;
            for (int i = 0, size = dates.size(); i < size; i++) {
                nextRepeatTime = dates.get(i);
                newRepeatInstance = reuseOrMakeNextRepeatInstance(repeatRuleOriginator, oldUndoneRepeatInstanceList, nextRepeatTime);
                repeatRef.insertIntoListAndSaveListAndInstance(newRepeatInstance);
                repeatRef = newRepeatInstance;
                newRepeatInstanceList.add(newRepeatInstance);
//                setLastGeneratedDate(nextRepeatTime); //save last date generated
            }
            if (nextRepeatTime != null) {
                setLastGeneratedDate(nextRepeatTime); //save last date generated
            }

            //delete any 'left over' instances //TODO check that deleting them doesn't affect
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
            deleteSuperfluousRepeatInstances(oldUndoneRepeatInstanceList);
            setListOfUndoneRepeatInstances(newRepeatInstanceList);
//        oldTotalNumberInstancesGeneratedSoFar += newRepeatInstanceList.size(); //includes the count for the original item
            ASSERT.that(oldTotalNumberInstancesGeneratedSoFar == getTotalNumberOfInstancesGeneratedSoFar(), "getTotalNumberOfInstancesGeneratedSoFar() shouldn't change during update of repeat rule");
            setTotalNumberOfInstancesGeneratedSoFar(oldTotalNumberInstancesGeneratedSoFar - oldNumberOfUndoneRepeatInstances + newRepeatInstanceList.size());
            DAO.getInstance().save(this);
        }
    }
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
    //</editor-fold>

    /**
     * calculate the number of dates to calculate. Capped to total number of
     * reepeats defined minus the number of repeats already generated, as well
     * as the max number of future repeats to generate. Returns
     * Integer.MAX_VALUE if no limits is defined for number of repeats.
     *
     * @return
     */
    private int calcNumberOfRepeats() {
//        int requestedCount = numberInstancesToGenerate == -1 ? Integer.MAX_VALUE - 1 : numberInstancesToGenerate; // +1 since we need to generate the last generated date again 
        int count = Integer.MAX_VALUE;
        int countMaxRemaining;
        int countFutureRepeats = 0;//Integer.MAX_VALUE;
        if (useCount()) {
            //if rule is limited to number of counts, then take remaining number of repeats into account
            countMaxRemaining = Math.min(count, getNumberOfRepeats() - getTotalNumberOfInstancesGeneratedSoFar()); //cap count number of remaining repeats
            count = countMaxRemaining;
        }
        if (getNumberFutureRepeatsToGenerateAhead() != 0) {
            countFutureRepeats = getNumberFutureRepeatsToGenerateAhead();
            count = Math.min(count, countFutureRepeats); //cap count to getNumberFutureRepeatsToGenerateAhead()
        }
        return count;
    }

//    private Date calcSubsetEndDate(Date earliestDate) {
    private Date calcSubsetEndDate(Date earliestDate) {
//        if (useCount()) {
        if (getNumberFutureRepeatsToGenerateAhead() != 0) {
            return new Date(MyDate.MAX_DATE);
        } else {
            ASSERT.that(getNumberOfDaysRepeatsAreGeneratedAhead() != 0, "Both getNumberFutureRepeatsToGenerateAhead() and getNumberOfDaysRepeatsAreGeneratedAhead() are 0 for RepeatRule=" + this);
            Date subEndDate = MyDate.getEndOfDay(new Date(System.currentTimeMillis() + getNumberOfDaysRepeatsAreGeneratedAhead() * MyDate.DAY_IN_MILLISECONDS));
            if (subEndDate.getTime() >= earliestDate.getTime()) {
                return subEndDate;
            } else {
                return earliestDate;
            }
        }
    }

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
//<editor-fold defaultstate="collapsed" desc="comment">
//    private long getSubsetBeginningDateXXX() {
//        if (getTotalNumberOfInstancesGeneratedSoFar() == 0) {
//            return getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
//        } else {
////            return getLastGeneratedDate(); //get the last date we've generated so far
//            return getLastGeneratedDate() != 0 ? Math.min(getLastGeneratedDate(), getEndDate()) : getEndDate(); //get the last date we've generated so far, Math.min since endDate may get set to larger than
//        }
//    }
//</editor-fold>
    private Date getNextDueDate() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && isRepeatRuleTerminatedXXX()) { //DO NOT test here, since dates need to be re-generated when changing the rule
//            return null;
//        }
//        repeatRule.setInt(RepeatRule.COUNT, getCount(COUNTS_AHEAD));
//        repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
//</editor-fold>
        if (false) {
            RepeatRule repeatRule = getRepeatRule();
            repeatRule.setInt(RepeatRule.COUNT, 2); //1 or 2 (2 if we generate the last generated date again)
            //TODO!!! need to add 1 day to subsetBeginningDate to avoid repeat same date as last generated Date???
//        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), getEndDate()); //CREATE new dates
            ASSERT.that(getLastGeneratedDateD().getTime() != MyDate.MIN_DATE, "getLastGeneratedDate() should have been defined when we get to here");
//        long subsetBeginningDate = getLastGeneratedDate() != 0 ? Math.min(getLastGeneratedDate(), getEndDate()) : getEndDate(); //get the last date we've generated so far, Math.min since endDate may get set to larger than 
        }
//        Vector<Date> newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), getSubsetBeginningDate(), MAX_DATE); //CREATE new dates
//        Date startDate = getSpecifiedStartDateD();
//        Vector<Date> newDates = repeatRule.datesAsVector(startDate.getTime(), subsetBeginningDate.getTime(), MAX_DATE); //CREATE new dates
//        Date date = getFirstNotEqualDate(dates, new Date(getSubsetBeginningDate()));
//        Date date = getFirstNotEqualDate(dates, subsetBeginningDate);
//        Date subsetBeginningDate = new Date(Math.max(getLastGeneratedDateD().getTime(), getSpecifiedStartDateD().getTime())); //get the last date we've generated so far, Math.min since endDate may get set to larger than 
//        Date subsetBeginningDate = calcSubsetBeginDate(); //get the last date we've generated so far, Math.min since endDate may get set to larger than 
        Date subsetBeginningDate
                = new Date(Math.max(getLastGeneratedDateD().getTime(), getSpecifiedStartDateD().getTime())); //get the last date we've generated so far, Math.min since endDate may get set to larger than 
        //get the last date we've generated so far, Math.min since endDate may get set to larger than 
        List<Date> dates = generateListOfDates(subsetBeginningDate, calcSubsetEndDate(subsetBeginningDate),
                calcNumberOfRepeats(), getNumberOfDaysRepeatsAreGeneratedAhead() != 0);
//        Date date = null;
        if (!dates.isEmpty()) {
            return dates.get(0);
        }
        return null;
    }

    /**
     * called when an item (RepeatRuleObjectInterface) is Done. Removes the item
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
     * @param updateInstances if true, updates (recalculates) repeat instances.
     * Used eg to avoid to calculate instances if a just created Item with a
     * repeatRule is cancelled (deleted without being comitted)
     * @param workSlot
     */
//    public RepeatRuleObjectInterface updateRepeatInstancesOnDoneCancelOrDelete(RepeatRuleObjectInterface orgRepeatObject) {
//    public RepeatRuleObjectInterface updateRepeatInstancesOnDoneCancelOrDelete(RepeatRuleObjectInterface repeatInstanceOrg) {
    public void updateRepeatInstancesOnDoneCancelOrDelete(Item item) {

//        List<ItemAndListCommonInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
        List<RepeatRuleObjectInterface> repeatInstanceItemList = getListOfUndoneRepeatInstances();
        if (!repeatInstanceItemList.contains(item)) {
            //updateRepeatInstancesOnDoneCancelOrDelete can also be called from Item.delete when the repeatRule is deleting superflous instances when rule is changed
        } else {

            setLatestDateCompletedOrCancelledIfGreaterThanLast(item.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
            ASSERT.that(repeatInstanceItemList.contains(item), "Error: " + item + " not in list of already generated repeat instances");
//        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
            repeatInstanceItemList.remove(item);

            Date nextRepeatTime;
//        Date nextRepeatTime = getNextDate(item.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE)); //get next date
            if (getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE) {
//            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(true)); //get next date
                nextRepeatTime = getNextCompletedFromDate(item.getRepeatStartTime(true)); //get next date
                ASSERT.that(repeatInstanceItemList.size() <= 1, "Error: Repeat from Completed - too many instances in listOfUndoneRepeatInstances=" + repeatInstanceItemList);
            } else {
//            nextRepeatTime = getNextDate(repeatInstanceOrg.getRepeatStartTime(false)); //get next date
                nextRepeatTime = getNextDueDate(); //get next date
            }
//            Item repeatInstance = null;
            if (nextRepeatTime != null) {
//            repeatInstance = repeatInstance.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
                Item repeatInstance = (Item) item.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
                item.insertIntoListAndSaveListAndInstance(repeatInstance); //insert new created instance in appropriate list and save it
//            if (getRepeatType() == REPEAT_TYPE_FROM_DUE_DATE) {
                repeatInstanceItemList.add(repeatInstance); //only keep track of instances when Due
                setLastGeneratedDate(nextRepeatTime); //TODO!!!! doesn't make sense to update this for RepeatFromCompleted
//            }
                setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);

                Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
            } else { // no more repeat instances to handle
//            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
            }
            setListOfUndoneRepeatInstances(repeatInstanceItemList);
            DAO.getInstance().save(this);
//        return repeatInstance;
        }
    }

    public void updateRepeatInstancesOnCancelDeleteOrExpired(WorkSlot workSlot) {
//        List<WorkSlot> workSlotInstanceItemList = getListOfUndoneRepeatInstances();
        List<RepeatRuleObjectInterface> workSlotInstanceItemList = getListOfUndoneRepeatInstances();
//        WorkSlotList workSlotInstanceItemList = new WorkgetListOfUndoneRepeatInstances();
        setLatestDateCompletedOrCancelledIfGreaterThanLast(workSlot.getRepeatStartTime(getRepeatType() == REPEAT_TYPE_FROM_COMPLETED_DATE));
        ASSERT.that(workSlotInstanceItemList.size() == 0 || workSlotInstanceItemList.contains(workSlot), "Error: \"" + workSlot + "\" not in list of already generated repeat instances");
//        removeFromListOfUndoneRepeatInstances(repeatRuleObject);
        workSlotInstanceItemList.remove(workSlot);

        Date nextRepeatTime;
        nextRepeatTime = getNextDueDate(); //get next date
        WorkSlot repeatInstance = null;
        if (nextRepeatTime != null) {
            repeatInstance = (WorkSlot) workSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
            workSlot.insertIntoListAndSaveListAndInstance(repeatInstance); //insert new created instance in appropriate list and save it
            workSlotInstanceItemList.add(repeatInstance);

            setLastGeneratedDate(nextRepeatTime);
            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);

            Log.p("**new repeat instance generated with repeat date=" + nextRepeatTime + ", instance=" + repeatInstance);
        } else { // no more repeat instances to handle
//            this.delete(); //delete the repeat rule //TODO!! for the moment keep the RepeatRules even when run out (easy to delete on server side if ever necessary)
        }

        //for workSlots, time may have passed since so need to update to ensure all workslots are in the future (or overlaps the future)
        if (workSlotInstanceItemList.size() > 0) {
            workSlot = (WorkSlot) workSlotInstanceItemList.get(0);
        } else {
            workSlot = null;
        }

        while (workSlot != null && (workSlot.getEndTime() <= System.currentTimeMillis()) && (nextRepeatTime = getNextDueDate()) != null) {
            //when we get here, the current/first in list workslot is in the past and we have a new repeatTime (so repeatRule is not expired)
            workSlotInstanceItemList.remove(workSlot); //remove workSlot in the past
            repeatInstance = (WorkSlot) workSlot.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due/Start date)
            workSlot.insertIntoListAndSaveListAndInstance(repeatInstance); //insert new created instance in appropriate list and save it
            workSlotInstanceItemList.add(repeatInstance);

            setLastGeneratedDate(nextRepeatTime);
            setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar() + 1);

            if (workSlotInstanceItemList.size() > 0) {
                workSlot = (WorkSlot) workSlotInstanceItemList.get(0); //get next workslot
//                nextRepeatTime = getNextDueDate(); //get next date
            } else {
                workSlot = null;
            }
        }

        setListOfUndoneRepeatInstances(workSlotInstanceItemList);
        DAO.getInstance().save(this);
//        return repeatInstance;
    }

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
     * then pops up a dialog to ask if they should also be deleted. If yes, then
     * deletes all other instances, and the repeatRule itself. If no, then
     * deletes only repeatRuleObject and creates any new instances.
     *
     * @param keepThisRepeatInstance
     * @param itemList
     */
    public void deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(RepeatRuleObjectInterface keepThisRepeatInstance) {
        deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(keepThisRepeatInstance, false);
    }

    public void deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(RepeatRuleObjectInterface keepThisRepeatInstance, boolean dontAskForConfirmation) {
//        if (getListOfUndoneRepeatInstances().size() > 1) { //if there are more instances (than this one):
////            if (Settings.getInstance().alwaysDeleteAllInstancesWhenDeletingARepeatRule() || !Dialog.show("Delete Repeat Rule", "Deleting this repeat rule will remove $0 already generated instances", "Only this", "All")) {
//            if (Settings.getInstance().alwaysDeleteAllInstancesWhenDeletingARepeatRule()
//                    || !Dialog.show("Delete Repeating Task",
//                            "This task is part of a repeating series. Delete only this task, or all incomplete tasks?", "Only this", "All")) {
        if (!dontAskForConfirmation && getListOfUndoneRepeatInstances().size() > MyPrefs.maxNumberRepeatInstancesToDeleteWithoutConfirmation.getInt()
                && !Dialog.show("Warning", "Deleting the " + Item.REPEAT_RULE + " will delete " + getListOfUndoneRepeatInstances().size() + " created instances", "OK", "Cancel")) {
            return;
        }
        //"All"
        //remove the calling object first since it is already being deleted (otherwise infinite loop)
//                delete(); //delete the repeatRule will also delete all instances
//                deleteRuleAndAllRepeatInstancesExceptThis(keepThisRepeatInstance);
        List list = getListOfUndoneRepeatInstances();
        list.remove(keepThisRepeatInstance); //remove keepThisRepeatInstance from list so it won't get deleted
        while (list.size() > 0) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
            ParseObject repeatRuleObject = (ParseObject) list.remove(0);
//            getListOfUndoneRepeatInstances().remove(0); //remove item first to avoid call back to this list
//            list.remove(0); //remove item first to avoid call back to this list
//            if (repeatRuleObject != null && !repeatRuleObject.equals(keepThisRepeatInstance)) {
////                item.delete(); //generated callback that removes the item from the list
//                DAO.getInstance().delete(repeatRuleObject);
//            }
            DAO.getInstance().delete(repeatRuleObject);
//            item.setRepeatRuleNoUpdate(null); //avoid callback to this rule when deleting
        }
//        DAO.getInstance().delete(this); //delete the repeatRule will also delete all instances
        DAO.getInstance().delete(this);
//            } else {
//                //"Only this" //TODO!!!! check deletion of repeatRules/instances
////                updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(isCommitted(), repeatRuleObject, itemList); //only update instances if the RepeatRule was already committed (otherwise the rule is being deleted before ever having been instantiated)
////                updateRepeatInstancesOnDone(isCommitted(), keepThisRepeatInstance); //only update instances if the RepeatRule was already committed (otherwise the rule is being deleted before ever having been instantiated)
//            }
//        }
    }

    public void deleteThisRepeatInstanceFromRepeatRuleListOfInstances(RepeatRuleObjectInterface repeatInstanceBeingDeleted) {
        List list = getListOfUndoneRepeatInstances();
        ASSERT.that(list.contains(repeatInstanceBeingDeleted), "error: repeatInstanceBeingDeleted NOT in repeatRule's getListOfUndoneRepeatInstances, instance=" + repeatInstanceBeingDeleted + ", rule=" + this);
        list.remove(repeatInstanceBeingDeleted);
        setListOfUndoneRepeatInstances(list);
    }

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
     * @return
     */
    public String getText() {
        String s = "";
        if (getRepeatType() == REPEAT_TYPE_NO_REPEAT) {
            s = REPEAT_RULE_NO_REPEAT;
        } else {
            if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
//                s += " (when completed)";
//                s += " When completed";
                s += "On completed";
            } else if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) //            s+=" (from "+Item.getFieldName(Item.FIELD_DUE_DATE)+")";
            {
//            s += " (from " + new MyDate(getReferenceItemForDueDateAndFutureCopies().getDueDate()).formatDate(false) + ")";
//            s += " (from " + new MyDate(getReferenceObjectForRepeatTime().getRepeatStartTime()).formatDate(false) + ")";
//            s += " (from " + new MyDate(getSpecifiedStartDate()).formatDate(false) + ")";
//                s += " (from " + MyDate.formatDateNew(getSpecifiedStartDate()) + ")";
//                s = "Starting " + MyDate.formatDateNew(getSpecifiedStartDate()) + " " + s;
                s += "Starting " + MyDate.formatDateNew(getSpecifiedStartDate());
            }

            int freq = getFrequency();
//        String s = getFreqText(freq);
            if (getInterval() > 1) {
                s += ", every " + MyDate.addNthPostFix("" + getInterval()) + " " + getFreqText(freq, false, false, false);
            } else {
                s += ", " + getFreqText(freq, true, true, false);
            }
            switch (freq) {
                case RepeatRule.YEARLY:
//                ASSERT.that(false); //TODO!!!! show yearly repeat as string
                    if (getDayInYear() != 0) {
//                    s += ", on the " + MyDate.addNthPostFix("" + getDayInYear()) + " day";
                        s += ", on the " + MyDate.addNthPostFix("" + getDayInYear()) + " day";
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

            if (useCount()) {
                s += " repeat " + getNumberOfRepeats() + " times";
//        } else if (getEndDate() != 0) {
//        } else if (getEndDate() != Long.MAX_VALUE) {
            } else if (getEndDate() != MyDate.MAX_DATE) {
//            s += " until " + new MyDate(getEndDate()).formatDate(false);
                s += " repeat until " + MyDate.formatDateNew(getEndDate());
            } else {
                s += " repeat forever";
            }

//        else {
//            s += " (from " + new MyDate(getStartDate()).formatDate(false) + ")";
//        }
            if (getNumberFutureRepeatsToGenerateAhead() != 0) {//|| getNumberOfDaysRepeatsAreGeneratedAhead() == 0) {
//                s += " show next " + getNumberFutureRepeatsToGenerateAhead() + " instances";
                s += ", create next " + getNumberFutureRepeatsToGenerateAhead()
                        + " repeat" + (getNumberFutureRepeatsToGenerateAhead() > 1 ? "s" : "");
            } else if (getNumberOfDaysRepeatsAreGeneratedAhead() != 0) {
//                s += " show instances for next " + getNumberOfDaysRepeatsAreGeneratedAhead() + " days";
                s += ", create repeats for next " + getNumberOfDaysRepeatsAreGeneratedAhead() + " day"
                        + (getNumberOfDaysRepeatsAreGeneratedAhead() > 1 ? "s" : "");
            } else {

            }
        }
        return s;
    }

    @Override
    public String toString() {
        return " [" + getObjectIdP() + "]" + getText();
    }

    public void copyMeInto(RepeatRuleParseObject destiny) {
        copyMeInto(destiny, false);
    }

    public void copyMeInto(RepeatRuleParseObject destiny, boolean copyState) {

        RepeatRuleParseObject dest = (RepeatRuleParseObject) destiny;
//        super.copyMeInto(destiny);

//        rr.setGuid(getGuid()); //-how to avoid that a
//        dest.frequency = frequency;
        dest.setSpecifiedStartDate(getSpecifiedStartDateD());
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

        dest.setNumberFutureRepeatsToGenerateAhead(getNumberFutureRepeatsToGenerateAhead());
        dest.setNumberOfDaysRepeatsAreGeneratedAhead(getNumberOfDaysRepeatsAreGeneratedAhead());

        if (copyState) {
            //copyState means including 
            dest.setLastGeneratedDate(getLastGeneratedDateD());

            List oldList = getListOfUndoneRepeatInstances();
            List newList = new ArrayList();
            for (int i = 0, size = oldList.size(); i < size; i++) {
                newList.add(oldList.get(i)); //only copy *references* to item (not make copies of the items)
            }
            dest.setListOfUndoneRepeatInstances(newList);

            dest.setLatestDateCompletedOrCancelled(getLatestDateCompletedOrCancelled());
            dest.setTotalNumberOfInstancesGeneratedSoFar(getTotalNumberOfInstancesGeneratedSoFar());
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
    /**
    externalize (and internalize) are used to store an edited RepeatRule locally (for Replay).
    Only end-user edited fields need to be saved, since either the user edited an existing rule 
    in which case the old rule will not be updated until saving, or he edited a new rule, 
    in which case there are no calculated fields (repeat instances)
    @param dos
    @throws IOException 
     */
    @Override
    public void externalize(DataOutputStream dos) throws IOException {
//        super.writeObject(dos);
//        if (DAO.getInstance().cache.LOCK) {
//            super.externalize(dos);
//            return;
//        }
        dos.writeUTF(getObjectIdP());

        dos.writeInt(getFrequency());
        dos.writeInt(getInterval());
        dos.writeInt(getNumberOfRepeats());

        dos.writeLong(getEndDate());
        dos.writeInt(getDaysInWeek());
        dos.writeInt(getWeeksInMonth());

        dos.writeInt(getWeekdaysInMonth());
        dos.writeInt(getMonthsInYear());
        dos.writeInt(getDayInMonth());

        dos.writeInt(getDayInYear());
        dos.writeInt(getRepeatType());

        dos.writeInt(getNumberFutureRepeatsToGenerateAhead());
        dos.writeInt(getNumberOfDaysRepeatsAreGeneratedAhead());
        dos.writeLong(getSpecifiedStartDate());

        dos.writeLong(getLatestDateCompletedOrCancelled().getTime()); //=0; //
        dos.writeLong(getLastGeneratedDateD().getTime()); //=0; //
        dos.writeInt(getTotalNumberOfInstancesGeneratedSoFar());

//        Util.writeObject(getListOfUndoneRepeatInstances(), dos);
        //store list of undone instances as: size; type of elements [Item/WorkSlot]; list of objectIds:
//        List<ItemAndListCommonInterface> instances = getListOfUndoneRepeatInstances();
        List<RepeatRuleObjectInterface> instances = getListOfUndoneRepeatInstances();
        int instanceSize = instances.size();
//        dos.writeInt(instances.size());
        dos.writeInt(instanceSize);
        if (instances.size() > 0) { //store the type of objects
            if (instances.get(0) instanceof WorkSlot) {
                dos.writeUTF(WorkSlot.CLASS_NAME);
            } else {
                dos.writeUTF(Item.CLASS_NAME);
            }
//        for (ItemAndListCommonInterface p : getListOfUndoneRepeatInstances()) {
//            for (ItemAndListCommonInterface p : getListOfUndoneRepeatInstances()) {
            for (int i = 0; i < instanceSize; i++) {
                ItemAndListCommonInterface p = (ItemAndListCommonInterface) instances.get(i);
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (p instanceof Item) {
//                    dos.writeUTF(((Item) p).getObjectIdP());
//                } else {
//                    dos.writeUTF(((WorkSlot) p).getObjectIdP());
//                }
//</editor-fold>
                if (p == null) {
                    if (Config.DEBUG_LOGGING) {
                        ASSERT.that(true, "RepeatRule: " + this + " references null elt!! "); //TODO!! remove such elements if detected (both from local cache and Parse Server)
                    }
                } else {
                    if (Config.DEBUG_LOGGING) {
                        ASSERT.that(p.getObjectIdP() != null, "RepeatRule: " + this + " references elt:" + p + " with getObjectIdP()==null");
                    }
                    dos.writeUTF(p.getObjectIdP());
                }
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">

//<editor-fold defaultstate="collapsed" desc="comment">
//        int vectorSize = repeatInstanceVector.size();
//        dos.writeInt(vectorSize);
//        for (int i = 0; i < vectorSize; i++) {
//            dos.writeInt(((BaseItem) repeatInstanceVector.elementAt(i)).getGuid());
//        }
//        repeatInstanceVector.writeObject(dos, true);
//        BaseItemDAO.getInstance().writeObject(repeatInstanceVector, dos, true);
//        BaseItemDAO.getInstance().writeObject(repeatInstanceItemList, dos);
//</editor-fold>
//        Util.writeObject(getListOfUndoneRepeatInstances(), dos);
//
//        datesList.writeObject(dos);
//        Util.writeObject(getDatesList(), dos); //TODO!! cannot externalize since Date is not handled by CN1, must rewrite to loop writing long as below
//        if (false) {
//            List<Date> list = getDatesListXXX();
////        int vectorSize = datesList.vector.size();
//            dos.writeInt(list.size());
//            for (int i = 0, size = list.size(); i < size; i++) {
////                dos.writeLong(((Date) datesList.vector.elementAt(i)).getTime());
//                dos.writeLong(list.get(i).getTime());
//            }
//        }
//</editor-fold>
    }

    @Override
    public void internalize(int version, DataInputStream dis) throws IOException {
//<editor-fold defaultstate="collapsed" desc="comment">
//        super.readObject(version, dis);
//        frequency = dis.readInt();
//        interval = dis.readInt();
//        count = dis.readInt();
//
//        endDate = dis.readLong();
//        daysInWeek = dis.readInt();
//        weeksInMonth = dis.readInt();
//
//        weekdaysInMonth = dis.readInt();
//        monthsInYear = dis.readInt();
//        dayInMonth = dis.readInt();
//
//        dayInYear = dis.readInt();
//        repeatType = dis.readInt();
//
//        numberOfRepeatsGeneratedAhead = dis.readInt();
//        numberOfDaysRepeatsAreGeneratedAhead = dis.readInt();
//        specifiedStartDate = dis.readLong();
//
//        lastDateGeneratedFor = dis.readLong(); //=0; //
//        lastGeneratedDate = dis.readLong(); //=0; //
//        countOfInstancesGeneratedSoFar = dis.readInt();
//</editor-fold>
        setObjectId(dis.readUTF());

        setFrequency(dis.readInt());
        setInterval(dis.readInt());
        setNumberOfRepeats(dis.readInt());

        setEndDate(dis.readLong());
        setDaysInWeek(dis.readInt());
        setWeeksInMonth(dis.readInt());

        setWeekdaysInMonth(dis.readInt());
        setMonthsInYear(dis.readInt());
        setDayInMonth(dis.readInt());

        setDayInYear(dis.readInt());
        setRepeatType(dis.readInt());

        setNumberFutureRepeatsToGenerateAhead(dis.readInt());
        setNumberOfDaysRepeatsAreGeneratedAhead(dis.readInt());
        setSpecifiedStartDate(dis.readLong());

        setLatestDateCompletedOrCancelled(new Date(dis.readLong())); //=0; //
        setLastGeneratedDate(new Date(dis.readLong())); //=0; //
        setTotalNumberOfInstancesGeneratedSoFar(dis.readInt());

//        setListOfUndoneRepeatInstances((List) Util.readObject(dis));
        int instancesSize = dis.readInt();
        if (instancesSize > 0) {
            String instanceType = dis.readUTF();
            boolean isListOfTypeItems = instanceType.equals(Item.CLASS_NAME);

            String objectId;
            Item item;
            WorkSlot workSlot;
            List instanceList = new ArrayList();
            for (int i = 0; i < instancesSize; i++) {
                objectId = dis.readUTF();
                if (isListOfTypeItems) { //use !equals since faster than equals
                    if (false) {
                        item = DAO.getInstance().fetchItem(objectId); //not needed since will be done in getListOfUndoneRepeatInstances
                        instanceList.add(item);
                    }
                } else {
                    if (false) {
                        workSlot = DAO.getInstance().fetchWorkSlot(objectId);
                        instanceList.add(workSlot);
                    }
                }
            }
            setListOfUndoneRepeatInstances(instanceList);
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//<editor-fold defaultstate="collapsed" desc="comment">
//        int vectorSize = dis.readInt();
//        repeatInstanceVector = new RepeatInstanceVector();
//        for (int i = 0; i < vectorSize; i++) {
//            repeatInstanceVector.addElement(BaseItemDAO.getInstance().getBaseItem(dis.readInt()));
//        }
//        repeatInstanceVector = new RepeatInstanceVector();
//        repeatInstanceVector.readBaseItem(version, dis);
//        repeatInstanceVector = (RepeatInstanceVector) BaseItemDAO.getInstance().readBaseItem(dis, this);
//        repeatInstanceVector = (RepeatInstanceVector) BaseItemDAO.getInstance().readBaseItem(this, dis);
//        repeatInstanceItemList = (RepeatInstanceItemList) BaseItemDAO.getInstance().readObject(this, dis);
//        repeatInstanceItemList = (RepeatInstanceItemList) Util.readObject(dis);
//        repeatInstanceItemList = (List) Util.readObject(dis);
//</editor-fold>
//        setListOfUndoneRepeatInstances((List) Util.readObject(dis));
//<editor-fold defaultstate="collapsed" desc="comment">
//        datesList = new DateBuffer();
//        datesList.readObject(999, dis);
//        datesList = (DateBuffer) Util.readObject(dis);
//        datesList = (DateBuffer) Util.readObject(dis);
//</editor-fold>
//        setDatesList((List) Util.readObject(dis));
//        int vectorSize = dis.readInt();
////            datesList.vector = new Vector(vectorSize);
//        List<Date> datesList = new ArrayList(vectorSize);
//        for (int i = 0; i < vectorSize; i++) {
////                datesList.vector.addElement(new Date(dis.readLong()));
//            datesList.add(new Date(dis.readLong()));
//        }
//        setDatesListXXX(datesList);
//</editor-fold>
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (this.getSpecifiedStartDate() ^ (this.getSpecifiedStartDate() >>> 32));
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

        hash = 23 * hash + this.getNumberFutureRepeatsToGenerateAhead();
        hash = 23 * hash + this.getNumberOfDaysRepeatsAreGeneratedAhead();
        return hash;
    }

    /**
     * tests if the repeat parameters are the same, e.g. but does NOT check if parseObjectId is different
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

        if (getSpecifiedStartDate() != repeatRule.getSpecifiedStartDate()) {
            return false;
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

        if (getNumberFutureRepeatsToGenerateAhead() != repeatRule.getNumberFutureRepeatsToGenerateAhead()) {
            return false;
        }
        if (getNumberOfDaysRepeatsAreGeneratedAhead() != repeatRule.getNumberOfDaysRepeatsAreGeneratedAhead()) {
            return false;
        }
//        if (repeatRuleOriginator != repeatRule.repeatRuleOriginator) {
//            return false;
//        }
        return true;
    }

    /**
    update the repeat rule with any differences (edits) in the RR given in argument
    @param editedRepeatRule
    @return true if any changes were made (meaning the updated rule needs to be saved)
     */
    public boolean update(RepeatRuleParseObject editedRepeatRule) {
        // ASSERT.that("equals() called on MyRepeatRule"); //DONE!!!: not updated (is it needed?) //called when setting a new/update
        if (editedRepeatRule == this) {
            return false;
        }
//        if (o == null) {
//            return false;
//        }

        boolean updated = false;

        RepeatRuleParseObject repeatRule = (RepeatRuleParseObject) editedRepeatRule;

        if (getSpecifiedStartDate() != repeatRule.getSpecifiedStartDate()) {
            setSpecifiedStartDate(repeatRule.getSpecifiedStartDate());
            updated = true;
        }
        if (getRepeatType() != repeatRule.getRepeatType()) {
            setRepeatType(repeatRule.getRepeatType());
            updated = true;
        }
        if (getFrequency() != repeatRule.getFrequency()) {
            setFrequency(repeatRule.getFrequency());
            updated = true;
        }
        if (getInterval() != repeatRule.getInterval()) {
            setInterval(repeatRule.getInterval());
            updated = true;
        }

        if (getEndDate() != repeatRule.getEndDate()) {
            setEndDate(repeatRule.getEndDate());
            updated = true;
        }
        if (getNumberOfRepeats() != repeatRule.getNumberOfRepeats()) {
            setNumberOfRepeats(repeatRule.getNumberOfRepeats());
            updated = true;
        }

        if (getDaysInWeek() != repeatRule.getDaysInWeek()) {
            setDaysInWeek(repeatRule.getDaysInWeek());
            updated = true;
        }
        if (getWeeksInMonth() != repeatRule.getWeeksInMonth()) {
            setWeeksInMonth(repeatRule.getWeeksInMonth());
            updated = true;
        }
        if (getWeekdaysInMonth() != repeatRule.getWeekdaysInMonth()) {
            setWeekdaysInMonth(repeatRule.getWeekdaysInMonth());
            updated = true;
        }

        if (getMonthsInYear() != repeatRule.getMonthsInYear()) {
            setMonthsInYear(repeatRule.getMonthsInYear());
            updated = true;
        }
        if (getDayInMonth() != repeatRule.getDayInMonth()) {
            setDayInMonth(repeatRule.getDayInMonth());
            updated = true;
        }
        if (getDayInYear() != repeatRule.getDayInYear()) {
            setDayInYear(repeatRule.getDayInYear());
            updated = true;
        }

        if (getNumberFutureRepeatsToGenerateAhead() != repeatRule.getNumberFutureRepeatsToGenerateAhead()) {
            setNumberFutureRepeatsToGenerateAhead(repeatRule.getNumberFutureRepeatsToGenerateAhead());
            updated = true;
        }
        if (getNumberOfDaysRepeatsAreGeneratedAhead() != repeatRule.getNumberOfDaysRepeatsAreGeneratedAhead()) {
            setNumberOfDaysRepeatsAreGeneratedAhead(repeatRule.getNumberOfDaysRepeatsAreGeneratedAhead());
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
    /**
     * date up till which dates have been generated so far (stored in
     * datesList). Used inside MyRepeatRule to keep track of the latest date for
     * which instances have been created (instead of e.g. just using the latest
     * date stored in the list of generated instances). This is used to ensure
     * that e.g. deleted or done instances do not re-appear
     */
    public Date getLastGeneratedDateD() {
        Date date = getDate(PARSE_LAST_DATE_GENERATED);
        return (date == null) ? new Date(MyDate.MIN_DATE) : date;
    }

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
    private void setLastGeneratedDate(Date lastGeneratedDate) {
        ASSERT.that(getDate(PARSE_LAST_DATE_GENERATED) == null || lastGeneratedDate == null
                || lastGeneratedDate.getTime() > getDate(PARSE_LAST_DATE_GENERATED).getTime(),
                "new lastGeneratedDate should always be higher than previous");
        if (lastGeneratedDate != null && lastGeneratedDate.getTime() != MyDate.MIN_DATE) {
            put(PARSE_LAST_DATE_GENERATED, lastGeneratedDate);
        } else {
            remove(PARSE_LAST_DATE_GENERATED);
        }
    }

    public Date getNextcomingDateD() {
        Date date = getDate(PARSE_NEXTCOMING_REPEAT_DATE);
        return (date == null) ? new Date(MyDate.MIN_DATE) : date;
    }

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
    private void setNextcomingDate(Date nextcomingRepeatDate) {
        ASSERT.that(getDate(PARSE_NEXTCOMING_REPEAT_DATE) == null || nextcomingRepeatDate == null
                || nextcomingRepeatDate.getTime() > getDate(PARSE_NEXTCOMING_REPEAT_DATE).getTime(),
                "new lastGeneratedDate should always be higher than previous");
        if (nextcomingRepeatDate != null && nextcomingRepeatDate.getTime() != MyDate.MIN_DATE) {
            put(PARSE_NEXTCOMING_REPEAT_DATE, nextcomingRepeatDate);
        } else {
            remove(PARSE_NEXTCOMING_REPEAT_DATE);
        }
    }

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
    public long getSpecifiedStartDate() {
        return getSpecifiedStartDateD().getTime();
    }

    public Date getSpecifiedStartDateD() {
//        return specifiedStartDate;
//        return getLong(SPECIFIED_START_DATE);
        Date date = getDate(PARSE_SPECIFIED_START_DATE);
        return (date == null) ? new Date(0) : date;
    }

    /**
     *
     * @param specifiedStartDate
     */
    public void setSpecifiedStartDate(long specifiedStartDate) {
        setSpecifiedStartDate(new Date(specifiedStartDate));
    }

    public void setSpecifiedStartDate(Date specifiedStartDate) {
//        if (this.specifiedStartDate != specifiedStartDate) {
//            this.specifiedStartDate = specifiedStartDate;
//            changed();
//        }
//        put(SPECIFIED_START_DATE, specifiedStartDate);
        if (specifiedStartDate.getTime() != 0) {
            put(PARSE_SPECIFIED_START_DATE, specifiedStartDate);
        } else {
            remove(PARSE_SPECIFIED_START_DATE);
        }
    }

    /**
     * returns set values separated with , Eg. "Mon, Tue, Fri" or "Feb"
     */
    private static String getOredValuesAsString(int ordValues, int[] intArray, String[] strArray, String sepStr) {
        String s = "";
        String sep = "";
        for (int i = 0; i < intArray.length; i++) {
            if ((ordValues & intArray[i]) != 0) {
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
     * the originally created event.
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
    void setNumberOfRepeats(int count) {
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
        setEndDate(new Date(endDate));
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
        return bitValue;
    }

    /**
     *
     * @param weeksInMonth
     */
    public void setWeeksInMonth(int weeksInMonth) {
//        this.weeksInMonth = weeksInMonth;
//        this.weekdaysInMonth = 0;
//        this.dayInMonth = 0;
//        this.dayInYear = 0;
        if (weeksInMonth != 0) {
            put(PARSE_WEEKS_IN_MONTH, weeksInMonth);
        } else {
            remove(PARSE_WEEKS_IN_MONTH);
        }
//        setWeekdaysInMonth(0);
//        setDayInMonth(0);
//        setDayInYear(0);
        if (false) {
            if (getWeekdaysInMonth() != 0) {
                setWeekdaysInMonth(0);
            }
            put(PARSE_WEEKDAYS_IN_MONTH, 0);
            put(PARSE_DAY_IN_MONTH, 0);
            put(PARSE_DAY_IN_YEAR, 0);
        }
    }

    /**
     *
     * @return
     */
    public int getWeeksInMonth() {
//        return weeksInMonth;
//        return getInt(WEEKS_IN_MONTH);
        Integer i = getInt(PARSE_WEEKS_IN_MONTH);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;
    }

    public Vector getWeekdaysInMonthAsVector() {
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
        return weeksInMonthBitsToVector(getWeekdaysInMonth());
    }

    /**
     * contains the set of selected weekdays in month (FIRST/SECOND/... applied
     * to Monday/Tuesday/...), as OR'd values. Is mutually exclusive wtih
     * weeksInMonth
     */
    public void setWeekdaysInMonth(int weekdaysInMonth) {
//        if (this.weekdaysInMonth != weekdaysInMonth) {
//            this.weekdaysInMonth = weekdaysInMonth;
//            this.weeksInMonth = 0;
//            this.dayInMonth = 0;
//            this.dayInYear = 0;
//            changed();
//        }
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
        return getNumberFutureRepeatsToGenerateAhead() != 0 || getNumberOfDaysRepeatsAreGeneratedAhead() == 0; //"getNumberOfDaysRepeatsAreGeneratedAhead() == 0" => if neither is defined, use NumberFutureRepeats by default
    }

    /**
     * how many future repeats are generated at any point in time. Indicates how
     * many instances are generated in *addition* to the current one (either
     * originator or next item), which the user will always expect to see. Used
     * to avoid generating a too high number of repeats at any one time.
     * Mutually exclusive with numberOfDaysRepeatsAreGeneratedAhead (only one of
     * the two can be used at any one time)
     */
    public int getNumberFutureRepeatsToGenerateAhead() {
//        return numberOfRepeatsGeneratedAhead;
        Integer i = getInt(PARSE_NUMBER_FUTURE_REPEATS_TO_GENERATE_AHEAD);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;
    }

    /**
     *
     * @param numberFutureRepeatsGeneratedAhead
     */
    public void setNumberFutureRepeatsToGenerateAhead(int numberFutureRepeatsGeneratedAhead) {
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
        if (numberFutureRepeatsGeneratedAhead != 0) {
            put(PARSE_NUMBER_FUTURE_REPEATS_TO_GENERATE_AHEAD, numberFutureRepeatsGeneratedAhead);
        } else {
            remove(PARSE_NUMBER_FUTURE_REPEATS_TO_GENERATE_AHEAD);
        }
    }

    /**
     *
     * @param numberOfDaysRepeatsAreGeneratedAhead
     */
    public void setNumberOfDaysRepeatsAreGeneratedAhead(int numberOfDaysRepeatsAreGeneratedAhead) {
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
        if (numberOfDaysRepeatsAreGeneratedAhead != 0) {
            put(PARSE_NUMBER_OF_DAYS_TO_GENERATE_AHEAD, numberOfDaysRepeatsAreGeneratedAhead);
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
            put(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED, newDateOfCompleted);
        } else {
            remove(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
        }
    }

    /**
     * keep track of the last/most recent (biggest in calendar terms) Due date
     * of completed/cancelled tasks (used when recalculating repeat instances in
     * a modified rule). Even if tasks are completed/cancelled out of order,
     * only the latest date is stored.
     *
     * @return
     */
    private void setLatestDateCompletedOrCancelledIfGreaterThanLast(Date newDateOfCompleted) {
//        Date lastDateOfCompleted = getDate(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
        Date lastDateOfCompleted = getLatestDateCompletedOrCancelled();
//        Date lastDateOfCompleted = getLatestDateCompletedOrCancelled();
        if (lastDateOfCompleted.getTime() == 0 || newDateOfCompleted.getTime() > lastDateOfCompleted.getTime()) {
            setLatestDateCompletedOrCancelled(newDateOfCompleted);
        }
    }

    private void setLatestDateCompletedOrCancelledIfGreaterThanLastOLD(Date newDateOfCompleted) {
        Date lastDateOfCompleted = getDate(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
//        Date lastDateOfCompleted = getLatestDateCompletedOrCancelled();
        if (lastDateOfCompleted == null || newDateOfCompleted.getTime() > lastDateOfCompleted.getTime()) {
            put(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED, newDateOfCompleted);
        } else {
            remove(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
        }
    }

    public Date getLatestDateCompletedOrCancelled() {
        Date lastDateOfCompleted = getDate(PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED);
        return lastDateOfCompleted != null ? lastDateOfCompleted : new Date(0);
    }

    /**
     * used to keep track of when a repeatrule has repeated the indicated number
     * of times (to stop the repeat then)
     *
     * @return
     */
    public int getTotalNumberOfInstancesGeneratedSoFar() {
//        return countOfInstancesGeneratedSoFar;
//        return getInt(COUNT_OF_INSTANCES_GENERATED_SO_FAR);
        Integer i = getInt(PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR);
//        if (i != null) {
//            return i;
//        } else {
//            return 0;
//        }
        return i != null ? i : 0;
    }

    /**
     *
     * @param countOfInstancesGeneratedSoFar
     */
    private void setTotalNumberOfInstancesGeneratedSoFar(int countOfInstancesGeneratedSoFar) {
//        if (this.countOfInstancesGeneratedSoFar != countOfInstancesGeneratedSoFar) {
//            this.countOfInstancesGeneratedSoFar = countOfInstancesGeneratedSoFar;
//            changed();
//        }
        if (countOfInstancesGeneratedSoFar != 0) {
            put(PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR, countOfInstancesGeneratedSoFar);
        } else {
            remove(PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR);
        }
    }

    static int[] getRepeatRuleFrequencyNumbers() {
        return REPEAT_RULE_FREQUENCY_NUMBERS;
    }

    static String[] getRepeatRuleFrequencyNames() {
        return REPEAT_RULE_FREQUENCY_NAMES;
    }

    static int[] getRepeatRuleTypeNumbers() {
        return REPEAT_RULE_TYPE_NUMBERS;
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

    static int[] getWeekInMonthNumbers() {
        return WEEKS_IN_MONTH_NUMBERS;
    }

    static int[] getWeekInMonthNumbersShort() {
        return WEEKS_IN_MONTH_NUMBERS_SHORT;
    }

//    Vector getWeekInMonthNamesAsVector() {
////        return ItemList.createVector(WEEKS_IN_MONTH_NAMES);
//        return new Vector(WEEKS_IN_MONTH_NAMES);
//    }
    static int[] getDayInWeekNumbers() {
        return DAY_IN_WEEK_NUMBERS_MONDAY_FIRST;
    }

    static int[] getDayInWeekNumbersInclWeekdays() {
        return DAY_IN_WEEK_NUMBERS_MONDAY_FIRST_INCL_WEEKDAYS;
    }

    static int[] getMonthInYearNumbers() {
        return MONTH_IN_YEAR_NUMBERS;
    }

    public boolean hasSaveableData() {
        return getRepeatType() != REPEAT_TYPE_NO_REPEAT;
    }

    private void setTest(Date start, int repeatType, int frequency, int interval, int intOptions, boolean genDaysAhead, int daysOrInstances, int foreverUntilNumber, Object untilOrNumber) {
        setSpecifiedStartDate(start); // eg REPEAT_TYPE_FROM_COMPLETED_DATE
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
        } else {
            setNumberFutureRepeatsToGenerateAhead(daysOrInstances);
        }
        if (foreverUntilNumber == ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_UNTIL) {
            setEndDate((Date) untilOrNumber);
            setNumberOfRepeats(Integer.MAX_VALUE);
        } else if (foreverUntilNumber == ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_NUMBER) {
            setNumberOfRepeats((Integer) untilOrNumber);
            setEndDate(new Date(MyDate.MAX_DATE));
        } else if (foreverUntilNumber == ScreenRepeatRule.REPEAT_HOW_LONG_OPTION_FOREVER) {
            setNumberOfRepeats(Integer.MAX_VALUE);
            setEndDate(new Date(MyDate.MAX_DATE));
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
        rule.listDates(10);
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
//        RepeatRuleObject repeatInstance;
////        Date nextRepeatDate;
//        long nextRepeatTime;
//        while (repeatDates.hasMoreElements()) { //if another instance generated
////            nextRepeatDate = (Date) repeatDates.nextElement();
//            nextRepeatTime = ((Date) repeatDates.nextElement()).getTime();
//            if (repeatRuleOriginator.getRepeatStartTime(false) == nextRepeatTime) {
//                repeatInstance = repeatRuleOriginator;
//            } else {
//// <editor-fold defaultstate="collapsed" desc="comment">
////            repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime());
////            if (Settings.getInstance().reuseExistingInstancesAfterChangingRepeatRule()
//////                    && (repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDate(nextRepeatDate.getTime())) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these
////                    && (repeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDate(nextRepeatTime)) != null) { //UI: reuse any existing instances with same date as before (avoids deleting changes made to these
////                //newRepeatInstance = repeatInstanceVector.getAndRemoveRepeatInstanceWithDueDate(nextRepeatDate.getTime())
//////#mdebug
////                Log.l("**repeat instance reused = " + repeatInstance);
//////#enddebug
////            } else { //no previous instance with same date exists, create a new one
////                repeatInstance = repeatRuleOwner.createRepeatCopy(nextRepeatDate.getTime());// </editor-fold>
//                repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime);
//                //insert new created instance in appropriate list
//                motherListForNewInstances.addItem(repeatInstance); //UI: insert new repeatInstance at the end of the list
//                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1); //increase count by one. Only count *new* instances. UI: user deleted instances are still counted (item.deleteRuleAndAllRepeatInstancesExceptThis() doesn't reduce count)
//// <editor-fold defaultstate="collapsed" desc="comment">
////                ItemList parentItemList = repeatRuleOwner.getListForNewCreatedRepeatInstances(); //UI: add repeatInstances to the parentList of the source
////                if (listForNewCreatedRepeatInstances != null) { //-the list *mustn't* be null since new instances would not be visible anywhere
////                    int repeatRuleObjectIndex = parentItemList.getItemIndex(repeatRuleObject);
////                    parentItemList.addItemAtIndex(newInstance, repeatRuleObjectIndex+1); //UI: +1: insert new repeatInstance after the previous one (or
////                }// </editor-fold>
////#mdebug
//                Log.l("**new repeat instance generated = " + repeatInstance);
////#enddebug
//            }
//// <editor-fold defaultstate="collapsed" desc="comment">
////                newInstance.commit(); //- not necessary because done when inserted into a committed list
////            }
////            if (nextRepeatDate.getTime() > getLastDateGeneratedFor()) { //only update if new date is larger than all previous (eg in case generateDates() doesn't generate in strict order)
////                setLastDateGeneratedFor(nextRepeatDate.getTime()); //update for each generated date to
////            }// </editor-fold>
//            repeatInstanceItemList.addItem(repeatInstance);
//        }
//// <editor-fold defaultstate="collapsed" desc="comment">
////        repeatInstanceVector.deleteRepeatInstancesExceptThis(repeatRuleOwner); //delete any&all remaining items (since they are not covered by the new repeatRule)
////        repeatInstanceVector = null; //Help GC
////        repeatInstanceVector = newRepeatInstanceList;
////        setCountOfInstancesGeneratedSoFar(newRepeatInstanceList.size());// </editor-fold>
////        repeatRuleNewInstancesCalculationOngoing = false;
//        changed(); //ensures auto-save
//    }
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
