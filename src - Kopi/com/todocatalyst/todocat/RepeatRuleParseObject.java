/*
 * an efficient way to store a RepeatRule and methods to translate back and forth between this format, the
 * RepeatRule fields, and the values used in drop-down menus.
 */
package com.todocatalyst.todocat;

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

    static final int REPEAT_TYPE_FROM_DUE_DATE = 33;
    static final int REPEAT_TYPE_FROM_COMPLETED_DATE = 11;
    static final int REPEAT_TYPE_FROM_SPECIFIED_DATE = 7;

    /**
     * list of generated dates for the repeat rule
     */
//    private DateBuffer datesList = new DateBuffer(); // = 1; //
    private List datesList = new ArrayList<Date>(); // = 1; //
    /**
     * number of dates to generate ahead in each 'batch' of new dates
     */
    int COUNTS_AHEAD = 10;//Settings.getInstance().getDefaultNumberOfRepeatInstancesBuffered();

    /**
     * a calculation of new repeat rule instances is already on-going. necessary
     * to avoid that when inserting just generated new instances, then new
     * calculations are triggered for them.
     */
//    boolean repeatRuleNewInstancesCalculationOngoing;
    private static final String FIRST = "First";
    private static final String SECOND = "Second";
    private static final String THIRD = "Third";
    private static final String FOURTH = "Fourth";
    private static final String FIFTH = "Fifth";
    private static final String LAST = "Last";
    private static final String SECOND_LAST = "Second last";
    private static final String THIRD_LAST = "Third last";
    private static final String FOURTH_LAST = "Fourth last";
    private static final String FIFTH_LAST = "Fifth last";
    private static final int[] WEEKS_IN_MONTH_NUMBERS = {RepeatRule.FIRST, RepeatRule.SECOND, RepeatRule.THIRD, RepeatRule.FOURTH, RepeatRule.FIFTH, RepeatRule.LAST, RepeatRule.SECONDLAST, RepeatRule.THIRDLAST, RepeatRule.FOURTHLAST, RepeatRule.FIFTHLAST};
    private static final String[] WEEKS_IN_MONTH_NAMES = {FIRST, SECOND, THIRD, FOURTH, FIFTH, LAST, SECOND_LAST, THIRD_LAST, FOURTH_LAST, FIFTH_LAST};
    //NB! below DAY_IN_WEEK_NUMBERS list requires that MyDate sequence of weekdays is the same!!
    private static final int[] DAY_IN_WEEK_NUMBERS_MONDAY_FIRST = {RepeatRule.MONDAY, RepeatRule.TUESDAY, RepeatRule.WEDNESDAY, RepeatRule.THURSDAY, RepeatRule.FRIDAY, RepeatRule.SATURDAY, RepeatRule.SUNDAY};
    private static final int[] DAY_IN_WEEK_NUMBERS_MONDAY_FIRST_INCL_WEEKDAYS = {RepeatRule.MONDAY, RepeatRule.TUESDAY, RepeatRule.WEDNESDAY, RepeatRule.THURSDAY, RepeatRule.FRIDAY, RepeatRule.SATURDAY, RepeatRule.SUNDAY, RepeatRule.WEEKDAYS, RepeatRule.WEEKENDS};
    //NB! below DAY_IN_WEEK_NUMBERS list requires that MyDate sequence of months is the same!!
    private static final int[] MONTH_IN_YEAR_NUMBERS = {RepeatRule.JANUARY, RepeatRule.FEBRUARY, RepeatRule.MARCH, RepeatRule.APRIL, RepeatRule.MAY, RepeatRule.JUNE, RepeatRule.JULY, RepeatRule.AUGUST, RepeatRule.SEPTEMBER, RepeatRule.OCTOBER, RepeatRule.NOVEMBER, RepeatRule.DECEMBER};
    private static final int[] REPEAT_RULE_FREQUENCY_NUMBERS = {RepeatRule.DAILY, RepeatRule.WEEKLY, RepeatRule.MONTHLY, RepeatRule.YEARLY};
//    private static final String[] REPEAT_RULE_FREQUENCY_NAMES = {"on a daily basis", "on a weekly basis", "on a monthly basis", "on a yearly basis"};
    private static final String[] REPEAT_RULE_FREQUENCY_NAMES = {"Daily", "Weekly", "Monthly", "Yearly"};
//    private static final int[] REPEAT_RULE_TYPE_NUMBERS = {MyRepeatRule.REPEAT_TYPE_FROM_COMPLETED_DATE, MyRepeatRule.REPEAT_TYPE_FROM_DUE_DATE, MyRepeatRule.REPEAT_TYPE_FROM_SPECIFIED_DATE};
//    private static final String[] REPEAT_RULE_TYPE_NAMES = {"completion date", "due date", "today"};
    private static final int[] REPEAT_RULE_TYPE_NUMBERS = {RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE, RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE};
//    private static final String[] REPEAT_RULE_TYPE_NAMES = {"completion date", "due date"}; //TODO!!: add repeat from TODAY, e.g. I create a new task today and it should repeat every two weeks from now (<=> DueDate==today??)
    private static final String[] REPEAT_RULE_TYPE_NAMES = {"completion", "due"}; //TODO!!: add repeat from TODAY, e.g. I create a new task today and it should repeat every two weeks from now (<=> DueDate==today??)

    RepeatRuleParseObject() {
        super("MyRepeatRule");
        setFrequency(RepeatRule.DAILY); //default: daily repeat
        setInterval(1); //default: every day
        setNumberOfRepeats(Integer.MAX_VALUE); //= 0; //1; //-start with 0 (undefined => starts with Repeat forever)
        setEndDate(Long.MAX_VALUE);
        setDaysInWeek(0);
        setWeeksInMonth(0);
        setWeekdaysInMonth(0);
        setMonthsInYear(0);
        setDayInMonth(0);
        setDayInYear(0); //TODO: choosing the middle day of the year, eg365/2=182 would make it faster to choose right date in a scrolling list, but is less intuitive
        setRepeatType(REPEAT_TYPE_FROM_COMPLETED_DATE);
        setNumberFutureRepeatsToGenerateAhead(1);
        setNumberOfDaysRepeatsAreGeneratedAhead(0);
        setLastDateGeneratedForxxx(Long.MIN_VALUE); //=0; //
        setLastGeneratedDate(Long.MIN_VALUE); //=0; //
        setRepeatInstanceItemList(new ArrayList()); // = 1; //
        setDatesList(new ArrayList()); // = 1; //
    }

    @Override
    public void externalize(DataOutputStream dos) throws IOException {
//        super.writeObject(dos);
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

        dos.writeLong(getLastDateGeneratedForxxx()); //=0; //
        dos.writeLong(getLastGeneratedDate()); //=0; //
        dos.writeInt(getCountOfInstancesGeneratedSoFar());

//        int vectorSize = repeatInstanceVector.size();
//        dos.writeInt(vectorSize);
//        for (int i = 0; i < vectorSize; i++) {
//            dos.writeInt(((BaseItem) repeatInstanceVector.elementAt(i)).getGuid());
//        }
//        repeatInstanceVector.writeObject(dos, true);
//        BaseItemDAO.getInstance().writeObject(repeatInstanceVector, dos, true);
//        BaseItemDAO.getInstance().writeObject(repeatInstanceItemList, dos);
        Util.writeObject(getRepeatInstanceItemList(), dos);

//        datesList.writeObject(dos);
        Util.writeObject(getDatesList(), dos);
    }

    @Override
    public void internalize(int version, DataInputStream dis) throws IOException {
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
        setLastDateGeneratedForxxx(dis.readLong()); //=0; //
        setLastGeneratedDate(dis.readLong()); //=0; //
        setCountOfInstancesGeneratedSoFar(dis.readInt());

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
        setRepeatInstanceItemList((List) Util.readObject(dis));

//        datesList = new DateBuffer();
//        datesList.readObject(999, dis);
//        datesList = (DateBuffer) Util.readObject(dis);
//        datesList = (DateBuffer) Util.readObject(dis);
        setDatesList((List) Util.readObject(dis));
    }

    void setRepeatInstanceItemList(List list) {
        put("repeatInstanceItemList", list);
    }

    List getRepeatInstanceItemList() {
        return getList("repeatInstanceItemList");
    }

    void setDatesList(List list) {
        put("datesList", list);
    }

    List<Long> getDatesList() {
//        return (List<Long>)getList("datesList");
        return getList("datesList");
    }

//    @Override
//    public String getEditButtonText() {
//        return toString();
//    }

    @Override
    public int getVersion() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getObjectId() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private class DateBuffer /* extends Vector*/ {

        DateBuffer() {
//            update();
        }
        /**
         * how many dates have been generated in total, that is: previously
         * consumed dates + those currently stored in datesList
         */
//        int totalCount = 0;
//        long lastDateGeneratedFor;
//        int nextDateIndex = 0;
//        boolean ruleFinished = false;
        /**
         * stores the last generated date, so we can start from that one next
         * time, and then drop it
         */
        long lastDateGenerated;
        int COUNTS_AHEAD = 2;//Settings.getInstance().getDefaultNumberOfRepeatInstancesBuffered();
        /**
         * stores (buffers) the dates that are already generated
         */
//        Vector datesList = new Vector(COUNTS_AHEAD);
        VectorBuffered datesList = new VectorBuffered(COUNTS_AHEAD + 1);

        class VectorBuffered /*
         * extends Vector
         */ {

            Vector vector;
            int indexOffset = 0;

            VectorBuffered(int initialSize) {
                vector = new Vector(initialSize);
            }

            public int sizeTotal() {
//                return super.size() + indexOffset;
                return vector.size() + indexOffset;
            }

            public int size() {
//                return super.size();
                return vector.size();
            }

//            public Object elementAt(int index) {
////                return super.elementAt(index - indexOffset);
//                return vector.elementAt(index - indexOffset);
//            }
//            public Object elementAtActual(int index) {
//                return super.elementAt(index);
//            }
            /**
             * take first element (head) out of list and return it. Returns null
             * if list empty
             */
            Object removeAndReturnFirst() {
                if (vector.size() > 0) {
                    Object temp = vector.firstElement();
                    vector.removeElementAt(0);
                    indexOffset++;
                    return temp;
                } else {
                    return null;
                }
            }
            //<editor-fold defaultstate="collapsed" desc="comment">
            //            Object takeFirst() {
            //                if (super.size() > 0) {
            //                    Object temp = super.firstElement();
            //                    super.removeElementAt(0);
            //                    indexOffset++;
            //                    return temp;
            //                } else {
            //                    return null;
            //                }
            //            }
            //</editor-fold>

            Object firstElement() {
                return vector.firstElement();
            }

            Object lastElement() {
                return vector.lastElement();
            }

            void addElement(Object element) {
                vector.addElement(element);
            }
//            public void addElement(Object element) {
//                this.addElement(element);
//            }
        }

        public void externalize(DataOutputStream dos) throws IOException {
//            dos.writeInt(totalCount);
//            dos.writeInt(nextDateIndex);
//            dos.writeBoolean(ruleFinished);
            dos.writeLong(lastDateGenerated);

            int vectorSize = datesList.vector.size();
            dos.writeInt(vectorSize);
            for (int i = 0; i < vectorSize; i++) {
                dos.writeLong(((Date) datesList.vector.elementAt(i)).getTime());
            }
            dos.writeInt(datesList.indexOffset);
        }

        public void internalize(int version, DataInputStream dis) throws IOException {
//            totalCount = dis.readInt();
//            nextDateIndex = dis.readInt();
//            ruleFinished = dis.readBoolean();
            lastDateGenerated = dis.readLong();

            int vectorSize = dis.readInt();
            datesList.vector = new Vector(vectorSize);
            for (int i = 0; i < vectorSize; i++) {
                datesList.vector.addElement(new Date(dis.readLong()));
            }
            datesList.indexOffset = dis.readInt();

        }

        /**
         * generate next batch of repeat dates. Returns empty List if the repeat
         * rule has ended (no more dates).
         */
//        private static Vector updateNew(RepeatRule repeatRule, long specifiedStartDate, long endDate, long lastRepeatDate, int totalMaxCountToGenerate, int countAlreadyGenerated, int numberOfRepeatsToGenerate) {
//
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

        private void update() {
//            if (datesList.sizeActual() > 1 || datesList.size() >= getCount()) { //need to generate more instances while there is we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
            if (datesList.sizeTotal() == 0 || (datesList.size() == 1 && datesList.sizeTotal() < getNumberOfRepeats())) { //need to generate more instances while there is at least one of previous 'batch' left, since we'll pick the last generated date next time, so we need to generate more repeat dates (must always generate *before* the last previous is used, because we uswe that for startdate)
                RepeatRule repeatRule = getRepeatRule();
//                repeatRule.setInt(RepeatRule.COUNT, Math.min(getCount() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
                if (getNumberOfRepeats() != Integer.MAX_VALUE) {
                    repeatRule.setInt(RepeatRule.COUNT, Math.min(getNumberOfRepeats() - datesList.sizeTotal() + 1, COUNTS_AHEAD + 1)); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
                } else {
                    repeatRule.setInt(RepeatRule.COUNT, COUNTS_AHEAD + 1); //"datesList.sizeTotal()+1" to generate 1 extra for removing. We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
                }
                long subsetBeginningGen;
                if (datesList.sizeTotal() == 0) {
                    subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
                } else {
//                    subsetBeginningGen = ((Long) datesList.lastElement()).longValue(); //get the last date we've generated so far
                    subsetBeginningGen = ((Date) datesList.lastElement()).getTime(); //get the last date we've generated so far
                }
                Vector newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, getEndDate());
                if (!newDates.isEmpty()) { //if vector not empty
                    newDates.removeElementAt(0); //we always drop first element, first time rule is generated, it recreates the originating item, following times it recreates the ite that 
                }
                while (!newDates.isEmpty()) {
                    datesList.addElement(newDates.firstElement()); //store new batch of dates
                    newDates.removeElementAt(0);
                }
                ASSERT.that(datesList.sizeTotal() <= getNumberOfRepeats(), "generated too many instances total=" + datesList.sizeTotal() + " getCount=" + getNumberOfRepeats() + " #rule=" + this);
            }
        }

        boolean hasMoreDates() {
            update();
//            return nextDateIndex < datesList.size();
            return datesList.size() > 0;
        }

        /**
         * removes the first date and returns it
         */
        Date takeNext() {
// <editor-fold defaultstate="collapsed" desc="comment">
//            int COUNTS_AHEAD = Settings.getInstance().getDefaultNumberOfRepeatInstancesBuffered();
//            if (datesList == null) {
//                datesList = new Vector(COUNTS_AHEAD);
//            }
//            if (nextDateIndex >= totalCount) { //need to fetch more dates
//            if (nextDateIndex >= datesList.size()) { //need to fetch more dates// </editor-fold>
            update();
// <editor-fold defaultstate="collapsed" desc="comment">
//                RepeatRule repeatRule = getRepeatRule();
////                int countGen = 0; //= -1;
////                long subsetBeginningGen; // = Math.max(getLastGeneratedDate(), getLastDateGeneratedFor()); // OK if getLast returns Long.MIN_VALUE since dates() uses Max(start,subsetBeginning) //=startDate; //=0;
//                if (!ruleFinished) {
//                    if (getInterval() > 0) {
//                        repeatRule.setInt(RepeatRule.INTERVAL, getInterval());
//                    }
////                    boolean startFromLastGeneratedDate = false;
////                    if (useNumberFutureRepeatsGeneratedAhead()) {
////                        startFromLastGeneratedDate = true;
////                    } else {
////                    }
////                    int countGen = Math.min(getCount(), totalCount + COUNTS_AHEAD + (startFromLastGeneratedDate ? 1 : 0));
//                    int countGen = Math.min(getCount(), totalCount + COUNTS_AHEAD + 1); // +1 always generate an extra which is then dropped
//                    if (getCount() > 0) { //NB. not allowed to set 0 as value for COUNT
//                        repeatRule.setInt(RepeatRule.COUNT, countGen);
//                    }
//                    repeatRule.setDate(RepeatRule.END, getEndDate());
//                    long subsetBeginningGen; // = startFromLastGeneratedDate ? ((Date) datesList.elementAt(nextDateIndex)).getTime() : getSpecifiedStartDate();
////                    if (startFromLastGeneratedDate) {
////                        subsetBeginningGen = ((Date) datesList.elementAt(nextDateIndex)).getTime();
////                    } else {
////                        subsetBeginningGen = lastDateGeneratedFor;
////                    }
//                    if (datesList.size() > 0) //                        subsetBeginningGen = ((Date) datesList.elementAt(nextDateIndex)).getTime();
//                    {
//                        subsetBeginningGen = ((Date) datesList.lastElement()).getTime(); //start from date of last generated date (which is then dropped again to avoid repeating it)
//                    } else {
//                        subsetBeginningGen = getSpecifiedStartDate(); //first time we generated, we start from specified date. This value is then dropped since it corresponds to the rule originator
//                    }
//                    long subsetEndGen = getEndDate();
//                    Vector newDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, subsetEndGen);
////                    if (startFromLastGeneratedDate && !newDates.isEmpty()) {
//                    if (!newDates.isEmpty()) {
//                        newDates.removeElementAt(0); //drop first element
//                    }
//                    totalCount += newDates.size();
//                    if (newDates.size() < COUNTS_AHEAD) {
//                        ruleFinished = true; //first time less than COUNTS_AHEAD is generated it means the rule is finished and all dates haven been generated
//                    }
//                    datesList = newDates; //store new batch of dates
//                    nextDateIndex = 0; //start from beginning of new lsit
//                }
//            }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
            //            if (nextDateIndex < datesList.size()) {
            ////                return (Date) datesList.elementAt(nextDateIndex++); //++ increase count for next time
            //                return (Date) datesList.elementAt(nextDateIndex++); //++ increase count for next time
            //            } else {
            //                return null; //rule is finished
            //            }
            //</editor-fold>
            return (Date) datesList.removeAndReturnFirst();
        }

        //<editor-fold defaultstate="collapsed" desc="comment">
        //        Date getNext(long completedDate) {
        //            update(completedDate);
        //            if (nextDateIndex < datesList.size()) {
        //                return (Date) datesList.elementAt(nextDateIndex++); //++ increase count for next time
        //            } else {
        //                return null; //rule is finished
        //            }
        //        }
        //</editor-fold>
        /**
         * returns the date of the next Date in line, or MAX_VALUE if no more
         * values
         */
        long getNextDate() {
            update();
            //<editor-fold defaultstate="collapsed" desc="comment">
            //            if (datesList.size() > 0) {
            //            if (datesList.size() < getCount()) {
            //            if (nextDateIndex < getCount()) {
            //                return ((Date) datesList.elementAt(nextDateIndex)).getTime();
            //            } else {
            //                return Long.MAX_VALUE;
            //            }
            //            if (nextDateIndex < getCount()) {
            //                return ((Date) datesList.elementAt(nextDateIndex)).getTime();
            //            } else {
            //                return Long.MAX_VALUE;
            //            }
            //            if (datesList.size() < getCount()) {
            //</editor-fold>
            if (datesList.size() > 0) {
                return ((Date) datesList.firstElement()).getTime();
            } else {
                return Long.MAX_VALUE;
            }
        }
    }

    /**
     * show a pop-up dialog with the generated dates, shown with 20 each time
     */
    void showRepeatDates() {
        DateBuffer tempDateList = new DateBuffer();
        Vector datesVector = new Vector();
        int i = 0;
        int nbDatesToShowInEachStep = 20;
        Command showMore = new Command("Show " + nbDatesToShowInEachStep + " more");
        do {
            while (i < nbDatesToShowInEachStep && tempDateList.hasMoreDates()) {
                datesVector.addElement(tempDateList.takeNext());
                i++;
            }
            i = 0;
        } while (Dialog.show(FIRST, new com.codename1.ui.List(datesVector), new Command[]{new Command("Exit"), tempDateList.hasMoreDates() ? showMore : new Command("No more")}) == showMore && tempDateList.hasMoreDates());
    }

    /**
     *
     * @return
     */
//    public int getRepeatInstancesSize() {
////        return repeatInstanceVector.getRepeatInstancesSize();
//        return getRepeatInstanceItemList().size();
//    }
    //TODO!!! generate user-readable string e.g. Every 2 years, Jan,Feb,Jul,Aug, for showing in button to edit...
    /**
     *
     * @return
     */
    @Override
    public String toString() {
        int freq = getFrequency();
//        String s = getFreqText(freq);
        String s = "";
        if (getInterval() > 1) {
            s += "Every " + MyDate.addNthPostFix("" + getInterval()) + " " + getFreqText(freq, false, false, false);
        } else {
            s += getFreqText(freq, true, true, false);
        }
        switch (freq) {
            case RepeatRule.YEARLY:
                ASSERT.that(false); //TODO!!!!
                if (getDayInYear() != 0) {
//                    s += ", on the " + MyDate.addNthPostFix("" + getDayInYear()) + " day";
                    s += ", on the " + MyDate.addNthPostFix("" + getDayInYear()) + " day";
                } else {
                    s += ", " + getMonthsInYearAsString(getMonthsInYear());
                } //                break; //Idea: fall-though to simply add details about months, weeks in following case statements??!!
                break;
            case RepeatRule.MONTHLY:
                if (getDayInMonth() != 0) {
                    s += ", on the " + MyDate.addNthPostFix("" + getDayInMonth());
                } else if (getWeeksInMonth() != 0) {
                    s += ", weeks " + getWeeksInMonthAsString(getWeeksInMonth());
                    s += ", on " + getDaysInWeekAsString(getDaysInWeek(), "+");
                } else {
                    s += ", " + getWeeksInMonthAsString(getWeekdaysInMonth());
                    s += " " + getDaysInWeekAsString(getDaysInWeek(), "+");
                }
                break;
            case RepeatRule.WEEKLY:
                s += " on " + getDaysInWeekAsString(getDaysInWeek(), "+");
                break;
            case RepeatRule.DAILY:
                break;
        }
        if (useCount()) {
            s += " " + getNumberOfRepeats() + " times";
//        } else if (getEndDate() != 0) {
        } else if (getEndDate() != Long.MAX_VALUE) {
            s += " until " + new MyDate(getEndDate()).formatDate(false);
        } else {
            s += " forever";
        }
        if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
            s += " (when completed)";
        } else if (getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) //            s+=" (from "+Item.getFieldName(Item.FIELD_DUE_DATE)+")";
        {
//            s += " (from " + new MyDate(getReferenceItemForDueDateAndFutureCopies().getDueDate()).formatDate(false) + ")";
//            s += " (from " + new MyDate(getReferenceObjectForRepeatTime().getRepeatStartTime()).formatDate(false) + ")";
            s += " (from " + new MyDate(getSpecifiedStartDate()).formatDate(false) + ")";
        }
//        else {
//            s += " (from " + new MyDate(getStartDate()).formatDate(false) + ")";
//        }
        return s;
    }

// <editor-fold defaultstate="collapsed" desc="comment">
//    public String toString(RepeatRule rr) {
//        String s = "";
//        String sep = "";
//        int[] fields = rr.getFields();
//        long longVal = 0;
//        int intVal = 0;
//        for (int i = 0, size = fields.length; i < size; i++) {
//            s += sep;
//            sep = "|";
//            switch (fields[i]) {
//                case RepeatRule.FREQUENCY:
//                case RepeatRule.DAY_IN_MONTH:
//                case RepeatRule.DAY_IN_WEEK:
//                case RepeatRule.DAY_IN_YEAR:
//                case RepeatRule.MONTH_IN_YEAR:
//                case RepeatRule.WEEK_IN_MONTH:
//                case RepeatRule.COUNT:
//                case RepeatRule.INTERVAL:
//                    intVal = rr.getInt(fields[i]);
//                    break;
//                case RepeatRule.END:
//                    longVal = rr.getDate(fields[i]);
//                    break;
//            }
//            switch (fields[i]) {
//                case RepeatRule.FREQUENCY:
//                    s += "FREQUENCY=" + getFreqText(intVal, false, false, true);
//                    break;
//                case RepeatRule.DAY_IN_MONTH:
//                    s += "DAY_IN_MONTH=" + intVal;
//                    break;
//                case RepeatRule.DAY_IN_WEEK:
//                    s += "DAY_IN_WEEK=" + getDaysInWeekAsString(intVal);
//                    break;
//                case RepeatRule.DAY_IN_YEAR:
//                    s += "DAY_IN_YEAR=" + intVal;
//                    break;
//                case RepeatRule.MONTH_IN_YEAR:
//                    s += "MONTH_IN_YEAR=" + getMonthsInYearAsString(intVal);
//                    break;
//                case RepeatRule.WEEK_IN_MONTH:
//                    s += "WEEK_IN_MONTH=" + getWeeksInMonthAsString(intVal);
//                    break;
//                case RepeatRule.COUNT:
//                    s += "COUNT=" + intVal;
//                    break;
//                case RepeatRule.END:
//                    s += "END=" + new MyDate(rr.getDate(fields[i]));
//                    break;
//                case RepeatRule.INTERVAL:
//                    s += "INTERVAL=" + intVal;
//                    break;
//                default:
//                    s += "**UNKNOWN FIELD=" + fields[i] + "**";
//            }
//// <editor-fold defaultstate="collapsed" desc="comment">
////            s+="=";
////            if (fields[i]==END) {
////                s+=""+new MyDate(getDate(fields[i])).formatDate();
////            } else if (fields[i]==COUNT || fields[i]==INTERVAL || fields[i]==DAY_IN_YEAR) {
////                s+=getInt(fields[i]);
////            } else if (fields[i]==FREQUENCY) {
////            switch (getInt(fields[i])) {
////                case DAILY: s+="DAILY"; break;
////                case WEEKLY: s+="WEEKLY"; break;
////                case MONTHLY: s+="MONTHLY"; break;
////                case YEARLY: s+="YEARLY"; break;
////                default:s+="**UNKNOWN VALUE="+getInt(fields[i])+"**";
////            }
////            } else {
////            switch (getInt(fields[i])) {
////                case FIRST: s+="FIRST"; break;
////                case SECOND: s+="SECOND"; break;
////                case THIRD: s+="THIRD"; break;
////                case FOURTH: s+="FOURTH"; break;
////                case FIFTH: s+="FIFTH"; break;
////                case LAST: s+="LAST"; break;
////                case SECONDLAST: s+="SECONDLAST"; break;
////                case THIRDLAST: s+="THIRDLAST"; break;
////                case FOURTHLAST: s+="FOURTHLAST"; break;
////                case FIFTHLAST: s+="FIFTHLAST"; break;
////                case SATURDAY: s+="SATURDAY"; break;
////                case FRIDAY: s+="FRIDAY"; break;
////                case THURSDAY: s+="THURSDAY"; break;
////                case WEDNESDAY: s+="WEDNESDAY"; break;
////                case TUESDAY: s+="TUESDAY"; break;
////                case MONDAY: s+="MONDAY"; break;
////                case SUNDAY: s+="SUNDAY"; break;
////                case JANUARY: s+="JANUARY"; break;
////                case FEBRUARY: s+="FEBRUARY"; break;
////                case MARCH: s+="MARCH"; break;
////                case APRIL: s+="APRIL"; break;
////                case MAY: s+="MAY"; break;
////                case JUNE: s+="JUNE"; break;
////                case JULY: s+="JULY"; break;
////                case AUGUST: s+="AUGUST"; break;
////                case SEPTEMBER: s+="SEPTEMBER"; break;
////                case OCTOBER: s+="OCTOBER"; break;
////                case NOVEMBER: s+="NOVEMBER"; break;
////                case DECEMBER: s+="DECEMBER"; break;
////                default:s+="**UNKNOWN VALUE="+getInt(fields[i])+"**";
////            }
////        }// </editor-fold>
//        }
//        return s;
//    }
// </editor-fold>
    public void copyMeInto(RepeatRuleParseObject destiny) {

        RepeatRuleParseObject dest = (RepeatRuleParseObject) destiny;
//        super.copyMeInto(destiny);

//        rr.setGuid(getGuid()); //-how to avoid that a
//        dest.frequency = frequency;
        dest.setFrequency(getFrequency());
        dest.setInterval(getInterval());
//        dest.count = count;
        dest.setNumberOfRepeats(getNumberOfRepeats());
        dest.setEndDate(getEndDate());
        dest.setDaysInWeek(getDaysInWeek());
        dest.setWeeksInMonth(getWeeksInMonth());
        dest.setWeekdaysInMonth(getWeekdaysInMonth());
        dest.setMonthsInYear(getMonthsInYear());
        dest.setDayInMonth(getDayInMonth());
        dest.setDayInYear(getDayInYear());
        dest.setRepeatType(getRepeatType());
        dest.setNumberFutureRepeatsToGenerateAhead(getNumberFutureRepeatsToGenerateAhead());
        dest.setNumberOfDaysRepeatsAreGeneratedAhead(getNumberOfDaysRepeatsAreGeneratedAhead());
        dest.setSpecifiedStartDate(getSpecifiedStartDate());
        dest.setLastDateGeneratedForxxx(getLastDateGeneratedForxxx());
        dest.setLastGeneratedDate(getLastGeneratedDate());
        dest.setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar());
//        dest.repeatRuleOriginator = repeatRuleOriginator;

//        rr.repeatInstanceVector.repeatInstances.equals(repeatInstanceVector.repeatInstances); //NB! vector with instances is just copied over since a new (edited) rule will have to deal with already generated instances
//        dest.repeatInstanceVector.repeatInstances = repeatInstanceVector.repeatInstances; //NB! the vector is recteated by calling generate** after creating the copy of the repeatRule
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
    /*    public int hashCode() {
    int hash = 7;
    hash = 23 * hash + this.frequency;
    hash = 23 * hash + this.interval;
    hash = 23 * hash + this.count;
    hash = 23 * hash + (int) (this.endDate ^ (this.endDate >>> 32));
    hash = 23 * hash + this.daysInWeek;
    hash = 23 * hash + this.weeksInMonth;
    hash = 23 * hash + this.weekdaysInMonth;
    hash = 23 * hash + this.monthsInYear;
    hash = 23 * hash + this.dayInMonth;
    hash = 23 * hash + this.dayInYear;
    hash = 23 * hash + this.repeatType;
    hash = 23 * hash + this.numberOfRepeatsGeneratedAhead;
    hash = 23 * hash + this.numberOfDaysRepeatsAreGeneratedAhead;
    hash = 23 * hash + (int) (this.specifiedStartDate ^ (this.specifiedStartDate >>> 32));
    return hash;
    }
    
    public boolean equals(Object o) {
    //        ASSERT.that("equals() called on MyRepeatRule"); //TODO!!!: not updated (is it needed?)
    if (o == null) {
    return false;
    }
    if (o == this) {
    return true;
    }
    if (this.getClass() != o.getClass()) {
    return false;
    }
    RepeatRuleParseObject repeatRule = (RepeatRuleParseObject) o;
    // compare selectedIndex
    //if (o2.getSelectedIndex() != getSelectedIndex()) return false; //don't include in equal
    //if (o2.getSize() != getSize()) return false;
    //        if (!description.equals(repeatRule.description)) {
    //            return false;
    //        }
    if (repeatType != repeatRule.repeatType) {
    return false;
    }
    if (frequency != repeatRule.frequency) {
    return false;
    }
    if (interval != repeatRule.interval) {
    return false;
    }
    if (count != repeatRule.count) {
    return false;
    }
    if (endDate != repeatRule.endDate) {
    return false;
    }
    if (daysInWeek != repeatRule.daysInWeek) {
    return false;
    }
    if (dayInMonth != repeatRule.dayInMonth) {
    return false;
    }
    if (weeksInMonth != repeatRule.weeksInMonth) {
    return false;
    }
    if (weekdaysInMonth != repeatRule.weekdaysInMonth) {
    return false;
    }
    if (monthsInYear != repeatRule.monthsInYear) {
    return false;
    }
    if (dayInYear != repeatRule.dayInYear) {
    return false;
    }
    if (numberOfRepeatsGeneratedAhead != repeatRule.numberOfRepeatsGeneratedAhead) {
    return false;
    }
    if (numberOfDaysRepeatsAreGeneratedAhead != repeatRule.numberOfDaysRepeatsAreGeneratedAhead) {
    return false;
    }
    if (specifiedStartDate != repeatRule.specifiedStartDate) {
    return false;
    }
    //        if (repeatRuleOriginator != repeatRule.repeatRuleOriginator) {
    //            return false;
    //        }
    return true;
    }*/
    /**
     * called whenever the MyRepeatRule has changed
     */
//    public void changed() {
////        super.changed();
////        setDirty(true); //repeatrules should never save themselves, this is always done by the owning item since we need to recalculate instances before saving
//        setDirty(ChangeValue.CHANGED_REPEATRULE_ANY_CHANGE); //repeatrules should never save themselves, this is always done by the owning item since we need to recalculate instances before saving
////        repeatRuleChanged = true;
//    }
//</editor-fold>
    /**
     * deleteRuleAndAllRepeatInstancesExceptThis Delete the RepeatRule *and* all
     * repeat instances that are (still) linked to it. Should never be called
     * when only deleting a RepeatRule itself from an existing Item/WorkSlot
     * (since that would delete the item owning the rule)!! So, before calling
     * this, make sure to set the repeatRule for the owning item to null.
     */
    public void delete() throws ParseException {
//        repeatInstanceItemList.deleteAllRepeatInstances(); //delete instances *before* calling super.deleteRuleAndAllRepeatInstancesExceptThis()
//        repeatInstanceItemList.deleteItemListAndAllItsItems(); //delete instances *before* calling super.deleteRuleAndAllRepeatInstancesExceptThis()
//        uncommit(); //avoid listening
//        repeatInstanceItemList.removeChangeEventListener(this); //avoid listening to change change events due to deleting elements in the list
//        repeatInstanceItemList.deleteAllItemsInList(); //delete all the items in the list (even if owned by another list)
        while (getRepeatInstanceItemList().size() > 0) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
//            if (repeatInstanceItemList.getItemAt(0) instanceof Item) {
            Item item = (Item) getRepeatInstanceItemList().get(0);
            getRepeatInstanceItemList().remove(0); //remove item first to avoid call back to this list
            item.setRepeatRule(null); //avoid callback to this rule when deleting
            item.delete(); //generated callback that removes the item from the list
//            }
        }
        super.delete(); //delete the RepeatRule in DAO, call changelisteners(???)
        //delete all instances
    }

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
    /**
     * deletes the repeatRule. If other instances than repeatRuleObject exists,
     * then pops up a dialog to ask if they should also be deleted. If yes, then
     * deletes all other instances, and the repeatRule itself. If no, then
     * deletes only repeatRuleObject and creates any new instances.
     *
     * @param keepThisRepeatInstance
     * @param itemList
     */
//    public void deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(RepeatRuleObject repeatRuleObject, ItemList itemList) {
//    public void deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(RepeatRuleObject keepThisRepeatInstance) { //, ItemList itemList) {
    public void deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(RepeatRuleObjectInterface keepThisRepeatInstance) { //, ItemList itemList) {
        if (getRepeatInstanceItemList().size() > 1) { //if there are more instances (than this one):
//            if (Settings.getInstance().alwaysDeleteAllInstancesWhenDeletingARepeatRule() || !Dialog.show("Delete Repeat Rule", "Deleting this repeat rule will remove $0 already generated instances", "Only this", "All")) {
            if (Settings.getInstance().alwaysDeleteAllInstancesWhenDeletingARepeatRule()
                    || !Dialog.show("Delete Repeating Task",
                            "This task is part of a repeating series. Delete only this task, or all incomplete tasks?", "Only this", "All")) {
                //"All"
                //remove the calling object first since it is already being deleted (otherwise infinite loop)
//                delete(); //delete the repeatRule will also delete all instances
//                deleteRuleAndAllRepeatInstancesExceptThis(keepThisRepeatInstance);
            } else {
                //"Only this"
//                updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(isCommitted(), repeatRuleObject, itemList); //only update instances if the RepeatRule was already committed (otherwise the rule is being deleted before ever having been instantiated)
//                updateRepeatInstancesOnDone(isCommitted(), keepThisRepeatInstance); //only update instances if the RepeatRule was already committed (otherwise the rule is being deleted before ever having been instantiated)
            }
        }
    }

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

    /**
     * generate next batch of repeat dates. Returns empty List if the repeat
     * rule has ended (no more dates).
     */
//        private static Vector updateNew(RepeatRule repeatRule, long specifiedStartDate, long endDate, long lastRepeatDate, int totalMaxCountToGenerate, int countAlreadyGenerated, int numberOfRepeatsToGenerate) {
    private Vector generateMoreDates() {//RepeatRule repeatRule, long specifiedStartDate, long endDate, long lastRepeatDate, int totalMaxCountToGenerate, int countAlreadyGenerated, int numberOfRepeatsToGenerate) {
        //if numberOfRepeatsToGenerate is less than the total remaining to generate, then generate only numberOfRepeatsToGenerate instances,
        //otherwise generate totalMaxCountToGenerate-countAlreadyGenerated which is the total of still missing instances to generate
        //We only generate COUNTS_AHEAD ahead at any time, or less if we have almost generated all getCount() instances. +1 to ensure we always generate at least 2 instances, otherwise the algorith won't work (since we remove the first, duplicate, generated instance)
        int numberOfInstancesToGenerate = Math.min(Math.min(getNumberFutureRepeatsToGenerateAhead(), getNumberOfRepeats() - getCountOfInstancesGeneratedSoFar()), COUNTS_AHEAD);
        long subsetBeginningGen = Math.max(getSpecifiedStartDate(), getLastGeneratedDate() + 1); //lastRepeatDate+1 to ensure that we don't regenerate the last already generated date, but the one after that
//                Vector newDates = repeatRule.datesAsVector(specifiedStartDate, subsetBeginningGen, endDate);
        RepeatRule repeatRule = getRepeatRule();
//        repeatRule.setInt(RepeatRule.COUNT, numberOfInstancesToGenerate);
        repeatRule.setInt(RepeatRule.COUNT, COUNTS_AHEAD);
        Vector<Long> moreDates = repeatRule.datesAsVector(getSpecifiedStartDate(), subsetBeginningGen, getEndDate());
        if (moreDates.size() > 0) {
            setLastGeneratedDate(moreDates.get(moreDates.size()));
        }
        setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + moreDates.size()); //update how many dates have been generated in total so far
        return moreDates;
    }

    /**
     * returns the next date in the repeat sequence (leaving it in the list to
     * make it possible to test the value before removing/consuming it).
     * Automatically generates additional dates if needed. Returns null if all
     * dates are used (meaning the repeatRule has expired).
     *
     * @return
     */
    private Long getNextDate(List<Long> datesList) {
        Long date = null;
//        List<Long> datesList = getDatesList();
        if (datesList.size() == 0) {
            datesList.addAll(generateMoreDates()); //calculate and add more repeat dates 
        }
        if (datesList.size() > 0) {
//            date = datesList.remove(0);
            date = datesList.get(0);
        } else {
//            this.delete(); //no more repeat instances to handle, delete the repeat rule
        }
//        setDatesList(datesList);
        return date;
    }

    /**
     * returns the next date (assumes that the list is notn-empty (checked ina
     * previous call to getNextDate())
     *
     * @return
     */
    private Long removeNextDate(List<Long> datesList) {
//        List<Long> datesList = getDatesList();
        if (datesList.size() > 0) {
            return (datesList.remove(0));
        } else {
            return null;
        }
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
     * @param repeatRuleObject
     */
    private void updateRepeatInstancesOnDone(RepeatRuleObjectInterface repeatRuleObject, List listForNewCreatedRepeatInstances, int insertIndex) throws ParseException { //, ItemList motherList) {
        //remove the just Completed item *before* generating new ones to ensure that the completed instance is missing in the count of items
        getRepeatInstanceItemList().remove(repeatRuleObject); //if repeatRuleObject is still there after regeneration??, then remove it
//        RepeatRuleObjectInterface repeatInstance;
//            Date nextDate = datesList.takeNext();
        List<Long> datesList = getDatesList();
        Long nextRepeatTime = removeNextDate(datesList); //get next date
        if (nextRepeatTime == null) {
            this.delete(); //no more repeat instances to handle, delete the repeat rule
        } else {
            RepeatRuleObjectInterface repeatInstance = repeatRuleObject.createRepeatCopy(nextRepeatTime); //the createRepeatCopy knows what to do with the repeat date (e.g. use as Due date
            //insert new created instance in appropriate list
            listForNewCreatedRepeatInstances.add(insertIndex, repeatInstance);
            List<RepeatRuleObjectInterface> repeatInstanceItemList = getRepeatInstanceItemList();
            repeatInstanceItemList.add(repeatInstance);
            Log.p("**new repeat instance generated = " + repeatInstance);
            setDatesList(datesList); //save udpated list
            setRepeatInstanceItemList(repeatInstanceItemList);
        }
    }

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

    /**
     * creates (from scratch) a new set of repeat instances, possibly reusing
     * previously generated instances (in case they were edited manually).
     * Called whenever a repeat rule has been created or edited.
     */
    public void generateRepeatInstancesFirstTime(RepeatRuleObjectInterface repeatRuleOriginator, List listForNewCreatedRepeatInstances, int insertIndex) {
        Vector newRepeatInstanceList = new Vector(); //hold new created instances
        newRepeatInstanceList.addElement(repeatRuleOriginator); //always add the originator
        RepeatRuleObjectInterface repeatInstance;
        Long nextRepeatTime;
        List<Long> datesList = getDatesList();
        if (useNumberFutureRepeatsToGenerateAhead()) {
            int numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead();
            int instancesGeneratedCount = 0;
            for (instancesGeneratedCount = 0; instancesGeneratedCount < numberInstancesToGenerate; instancesGeneratedCount++) {
                nextRepeatTime = removeNextDate(datesList);
                if (nextRepeatTime == null) {
                    break;
                } else {
                    repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
                    //insert new created instance in appropriate list
                    listForNewCreatedRepeatInstances.add(insertIndex + instancesGeneratedCount, repeatInstance); //+count => insert each instance *after* the previously inserted one
//                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
                }
            }
            setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + instancesGeneratedCount - 1); //count-1 => if we break out of the for loop, count will be 1 too high
        } else {
            //generate instances for a certain amount of time ahead
            int instancesGeneratedCount = 0;
            long now = MyDate.getNow();
            int numberOfDaysRepeatsAreGeneratedAhead = getNumberOfDaysRepeatsAreGeneratedAhead();
            while ((nextRepeatTime = removeNextDate(datesList)) != null && (nextRepeatTime.longValue() <= now + numberOfDaysRepeatsAreGeneratedAhead)) {
                repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
                //insert new created instance in appropriate list
                listForNewCreatedRepeatInstances.add(insertIndex + instancesGeneratedCount, repeatInstance); //+count => insert each instance *after* the previously inserted one
                instancesGeneratedCount++;
            }
            setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + instancesGeneratedCount);
        }
        setDatesList(datesList);
    }
    /**
     * creates (from scratch) a new set of repeat instances, possibly reusing
     * previously generated instances (in case they were edited manually).
     * Called whenever a repeat rule has been created or edited.
     */
    public void updateRepeatInstancesWhenRuleWasEdited(RepeatRuleObjectInterface repeatRuleOriginator, List listForNewCreatedRepeatInstances, int insertIndex) {
        Vector newRepeatInstanceList = new Vector(); //hold new created instances
        newRepeatInstanceList.addElement(repeatRuleOriginator); //always add the originator
        RepeatRuleObjectInterface repeatInstance;
        Long nextRepeatTime;
        List<Long> datesList = getDatesList();
        if (useNumberFutureRepeatsToGenerateAhead()) {
            int numberInstancesToGenerate = getNumberFutureRepeatsToGenerateAhead();
            int instancesGeneratedCount = 0;
            for (instancesGeneratedCount = 0; instancesGeneratedCount < numberInstancesToGenerate; instancesGeneratedCount++) {
                nextRepeatTime = removeNextDate(datesList);
                if (nextRepeatTime == null) {
                    break;
                } else {
                    repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
                    //insert new created instance in appropriate list
                    listForNewCreatedRepeatInstances.add(insertIndex + instancesGeneratedCount, repeatInstance); //+count => insert each instance *after* the previously inserted one
//                setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + 1);
                }
            }
            setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + instancesGeneratedCount - 1); //count-1 => if we break out of the for loop, count will be 1 too high
        } else {
            //generate instances for a certain amount of time ahead
            int instancesGeneratedCount = 0;
            long now = MyDate.getNow();
            int numberOfDaysRepeatsAreGeneratedAhead = getNumberOfDaysRepeatsAreGeneratedAhead();
            while ((nextRepeatTime = removeNextDate(datesList)) != null && (nextRepeatTime.longValue() <= now + numberOfDaysRepeatsAreGeneratedAhead)) {
                repeatInstance = repeatRuleOriginator.createRepeatCopy(nextRepeatTime); //create next instance
                //insert new created instance in appropriate list
                listForNewCreatedRepeatInstances.add(insertIndex + instancesGeneratedCount, repeatInstance); //+count => insert each instance *after* the previously inserted one
                instancesGeneratedCount++;
            }
            setCountOfInstancesGeneratedSoFar(getCountOfInstancesGeneratedSoFar() + instancesGeneratedCount);
        }
        setDatesList(datesList);
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
        } else if (getEndDate() != Long.MAX_VALUE) {
            repeatRule.setDate(RepeatRule.END, getEndDate());
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

//BELOW HERE ONLY SIMPLE SETTERS AND GETTERS
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

    /**
     * date up till which repeat instances have been generated so far. Used
     * inside MyRepeatRule to keep track of the latest date for which instances
     * have been created (instead of e.g. just using the latest date stored in
     * the list of generated instances). This is used to ensure that e.g.
     * deleted or done instances do not re-appear
     */
    public long getLastDateGeneratedForxxx() {
//        return lastDateGeneratedFor;
        return getLong("lastDateGeneratedFor");
    }

    /**
     * date up till which repeat instances have been generated so far. Used
     * inside MyRepeatRule to keep track of the latest date for which instances
     * have been created (instead of e.g. just using the latest date stored in
     * the list of generated instances). This is used to ensure that e.g.
     * deleted or done instances do not re-appear
     */
    public void setLastDateGeneratedForxxx(long lastDateGeneratedFor) {
//        if (this.lastDateGeneratedFor != lastDateGeneratedFor) {
//            this.lastDateGeneratedFor = lastDateGeneratedFor;
//            changed();
//        }
        put("lastDateGeneratedFor", lastDateGeneratedFor);
    }

    /**
     * store the latest date generated by the repeat rule (to make sure the same
     * date is of any of the instances generated
     */
    /**
     * date up till which dates have been generated so far (stored in
     * datesList). Used inside MyRepeatRule to keep track of the latest date for
     * which instances have been created (instead of e.g. just using the latest
     * date stored in the list of generated instances). This is used to ensure
     * that e.g. deleted or done instances do not re-appear
     */
    public long getLastGeneratedDate() {
//        return lastGeneratedDate;
        return getLong("lastGeneratedDate");
    }

    /**
     * the latest date of any of the instances generated
     */
    public void setLastGeneratedDate(long lastGeneratedDate) {
//        if (this.lastGeneratedDate != lastGeneratedDate) {
//            this.lastGeneratedDate = lastGeneratedDate;
//            changed();
//        }
        put("lastGeneratedDate", lastGeneratedDate);
    }

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
//        return specifiedStartDate;
        return getLong("specifiedStartDate");
    }

    /**
     *
     * @param specifiedStartDate
     */
    public void setSpecifiedStartDate(long specifiedStartDate) {
//        if (this.specifiedStartDate != specifiedStartDate) {
//            this.specifiedStartDate = specifiedStartDate;
//            changed();
//        }
        put("specifiedStartDate", specifiedStartDate);
    }

    /**
     * returns set values separated with , Eg. "Mon, Tue, Fri" or "Feb"
     */
    private static String getOredValuesAsString(int ordValues, int[] intArray, String[] strArray, String sepStr) {
        String s = "";
        String sep = "";
        for (int i = 0; i
                < intArray.length; i++) {
            if ((ordValues & intArray[i]) != 0) {
                s += sep + strArray[i];
                sep = sepStr;
            }
        }
        return s;
    }

    static String getDaysInWeekAsString(int dayInWeek, String seperator) {
        return getOredValuesAsString(dayInWeek, DAY_IN_WEEK_NUMBERS_MONDAY_FIRST, MyDate.DAY_NAMES_MONDAY_FIRST_SHORT, seperator);
    }

    static String getWeeksInMonthAsString(int weeksInMonth) {
        return getOredValuesAsString(weeksInMonth, WEEKS_IN_MONTH_NUMBERS, WEEKS_IN_MONTH_NAMES, "/");

    }

    static String getMonthsInYearAsString(int monthsInYear) {
        return getOredValuesAsString(monthsInYear, MONTH_IN_YEAR_NUMBERS, MyDate.MONTH_NAMES_SHORT, "/");
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
        put("repeatType", repeatType);
    }

    int getRepeatType() {
//        return repeatType;
        return getInt("repeatType");
    }

    void setFrequency(int frequency) {
        ASSERT.that(frequency == RepeatRule.DAILY || frequency == RepeatRule.WEEKLY || frequency == RepeatRule.MONTHLY || frequency == RepeatRule.YEARLY, "Illegal frequencey value set " + frequency);
//        if (this.frequency != frequency) {
//            this.frequency = frequency;
//            changed();
//        }
        put("frequency", frequency);
    }

    int getFrequency() {
//        return frequency;
        return getInt("frequency");
    }

    void setInterval(int interval) {
//        if (this.interval != interval) {
//            this.interval = interval;
//            changed();
//        }
        put("interval", interval);
    }

    int getInterval() {
//        return interval;
        return getInt("interval");
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
     * defines how many times an even will repeat. 1 means one repeat in
     * addition to the originally created event.
     *
     * @param count
     */
    void setNumberOfRepeatsx(int count) {
//        if (this.count != count) {
//            this.count = count;
//            this.endDate = Long.MAX_VALUE;
//            changed();
//        }
        put("count", count);
//        put("endDate", Long.MAX_VALUE);
    }

    /**
     * how many times should an event repeat. 1 means one repeat in addition to
     * the originally created event.
     *
     * @param count
     */
    int getNumberOfRepeats() {
//        return count;
        return getInt("count");
    }

    /**
     * defines how many times an even will repeat. 1 means one repeat in
     * addition to the originally created event.
     *
     * @param count
     */
    void setNumberOfRepeats(int count) {
        put("count", count);
        setEndDate(Long.MAX_VALUE);
    }

    /**
     * returns endDate. Default value Long.MAX_VALUE
     *
     * @return
     */
    public long getEndDate() {
//        return endDate;
        return getInt("endDate");
    }

    /**
     *
     * @param endDate
     */
    public void setEndDate(long endDate) {
//        if (this.endDate != endDate) {
//            this.endDate = endDate;
//            this.count = Integer.MAX_VALUE; //=0;
//            changed();
//        }
        put("endDate", endDate);
        put("count", Integer.MAX_VALUE);
    }

    /**
     *
     * @return
     */
    public int getDaysInWeek() {
//        return daysInWeek;
        return getInt("daysInWeek");
    }

    public Vector getDaysInWeekAsVector() {
        return daysInWeeksBitsToVector(getDaysInWeek());
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
        put("daysInWeek", daysInWeek);
    }

    /**
     *
     * @return
     */
    public int getMonthsInYear() {
//        return monthsInYear;
        return getInt("monthsInYear");
    }

    public Vector getMonthInYearAsVector() {
        return monthInYearAsVector(getMonthsInYear());
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
        put("monthsInYear", monthsInYear);
        setDayInYear(0);
    }

    /**
     *
     * @return
     */
    public int getWeeksInMonth() {
//        return weeksInMonth;
        return getInt("weeksInMonth");
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
        put("weeksInMonth", weeksInMonth);
        setWeekdaysInMonth(0);
        setDayInMonth(0);
        setDayInYear(0);
    }

    /**
     * contains the set of selected weeks in month, as OR'd values. Is mutually
     * exclusive wtih dayInMonth
     */
    public int getWeekdaysInMonth() {
//        return weekdaysInMonth;
        return getInt("weekdaysInMonth");
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
        put("weekdaysInMonth", weekdaysInMonth);
        setWeeksInMonth(0);
        setDayInMonth(0);
        setDayInYear(0);
    }

    /**
     * 0 means undefined/not set
     *
     * @return
     */
    public int getDayInMonth() {
//        return dayInMonth;
        return getInt("dayInMonth");
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
        put("dayInMonth", dayInMonth);
        setWeeksInMonth(0);
        setWeekdaysInMonth(0);
        setDaysInWeek(0);
        setDayInYear(0);
//            changed();
//        }
    }

    /**
     *
     * @return
     */
    public int getDayInYear() {
//        return dayInYear;
        return getInt("dayInYear");
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
        put("dayInYear", dayInYear);
        setDayInMonth(0);
        setMonthsInYear(0);
        setWeeksInMonth(0);
        setWeekdaysInMonth(0);
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
        ASSERT.that(getNumberOfDaysRepeatsAreGeneratedAhead() != 0 || getNumberFutureRepeatsToGenerateAhead() != 0);
//#enddebug
//        return numberOfRepeatsGeneratedAhead != 0;
        return getNumberFutureRepeatsToGenerateAhead() != 0;
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
        return getInt("numberFutureRepeatsToGenerateAhead");
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
        if (getNumberFutureRepeatsToGenerateAhead() != numberFutureRepeatsGeneratedAhead) {
            if (numberFutureRepeatsGeneratedAhead != 0) {
                setNumberOfDaysRepeatsAreGeneratedAhead(0);
            }
            put("numberFutureRepeatsToGenerateAhead", numberFutureRepeatsGeneratedAhead);
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
        if (numberOfDaysRepeatsAreGeneratedAhead != 0) {
            setNumberFutureRepeatsToGenerateAhead(0); //reset this value to zero to ensure only one is set at any time
        }
        put("numberOfDaysRepeatsAreGeneratedAhead", numberOfDaysRepeatsAreGeneratedAhead);
    }

    /**
     * defines how many days ahead the repeat rule should generate instances,
     * e.g. 14 means create all instances for the next 2 weeks
     */
    public int getNumberOfDaysRepeatsAreGeneratedAhead() {
//        return numberOfDaysRepeatsAreGeneratedAhead;
        return getInt("numberOfDaysRepeatsAreGeneratedAhead");
    }

    /**
     *
     * @return
     */
    public int getCountOfInstancesGeneratedSoFar() {
//        return countOfInstancesGeneratedSoFar;
        return getInt("countOfInstancesGeneratedSoFar");
    }

    /**
     *
     * @param countOfInstancesGeneratedSoFar
     */
    public void setCountOfInstancesGeneratedSoFar(int countOfInstancesGeneratedSoFar) {
//        if (this.countOfInstancesGeneratedSoFar != countOfInstancesGeneratedSoFar) {
//            this.countOfInstancesGeneratedSoFar = countOfInstancesGeneratedSoFar;
//            changed();
//        }
        put("countOfInstancesGeneratedSoFar", countOfInstancesGeneratedSoFar);
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

    static int[] getWeekInMonthNumbers() {
        return WEEKS_IN_MONTH_NUMBERS;
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
//                    //TODO!!!: what happens if eg set to 14 days, biweekly repeats, is there a risk that no instance will be generated due to next instance falling on 14d+1second?
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
////            setLastDateGeneratedFor(subsetEndingGen != Long.MAX_VALUE ? subsetEndingGen : Long.MIN_VALUE); //TODO!!!!: do not send if genFromCompleted and Long.MAX was selected
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
