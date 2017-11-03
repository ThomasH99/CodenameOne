/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.io.Log;
import com.codename1.io.Util;
import com.codename1.ui.Dialog;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;

/**
 * Split btw WorkTimeDef and ItemList: WTD calculates work slots and finds
 * date/time corresponding to a workload sum; recalculated if work slots change.
 * IL adds an additional structure to calculate sum of workload for each item,
 * indexed by index. IL adds a convenience method to get start/endWorkTime for
 * each item (based on Index). IL can calculate this for subItems
 * (Projects)/Sublists/Categories.
 *
 * Maintains a list of sorted, non-overlapping slots. The list is created by
 * combining manually defined slots (dated or not, possibly repeating) and slots
 * imported from Calendar. The basic function is to look up with a certain
 * amount of effort and then get the time/date back at which point this sum can
 * be achieved. E.g. if work load sum of the tasks preceding a taskA is 130h,
 * then the result can be that the task can start 30min into workSlotB, which
 * could be June12, 14:35. If the effort of taskA is 3h, the work load sum where
 * it can finish is 133h and it may finish 45min into workSlotC which could be
 * June13, 9:45. The key operations are: recalculate slots when slots are edited
 * or added/removed (infrequent); look up (very frequent since used to display
 * lists of tasks). Implementation phases: define basic (possibly overlapping)
 * slots: recalculate completely from invalidated slot. . Possibly to find an
 * algorithm to avoid recalculating everything *after* an added/removed/changed
 * slot? E.g. once the sum is calculated, adding/removing/changing simply Time 1
 * 3 8 12 1719 26 30 AAA BBBBB CCC DDDDD Sum 0 3 4 7 8910 11 15
 *
 * Lookup(sum=6) => (time=10;slot=slotB;starts-into-slot=2) Lookup(sum=9) =>
 * (time=18;slot=slotC;starts-into-slot=1)
 *
 *
 * Simple algorithm (prioritizing the slot starting the earliest, no negative
 * slots): sort by starting time, ignore slots with start or end time before NOW
 * (remove them from WorkDef as they're detected?!), to find sum, iterate over
 * slots, add up sum, if overlapping slots, use max(slotA-end-time,
 * slotB-start-time) which will ignore the part of following slots that started
 * before max-time. Should work both for multiple overlapping slots, and very
 * long slots overlapping many following ones.
 *
 * Another (lazy) algorithm: assuming that you (mainly) scroll back&forth in the
 * same list (and usually start scrolling from the top, so can calculate lazily
 * as you scroll down) is to simply keep the already looked up values with
 * results to quickly serve them back. E.g. in example above, store sum=6 and
 * sum=9 with the given results. Assumes any change to the work definition can
 * be reliably detected to invalidate such stored results.
 *
 * Each workslot can have a name to give feedback to the user on in which slot a
 * task is being started or finished. Use 'Interval Trees' for more efficient
 * search: https://en.wikipedia.org/wiki/Interval_tree,
 * http://algs4.cs.princeton.edu/93intersection/IntervalST.java.html,
 *
 * Indexed tree map: * https://code.google.com/p/indexed-tree-map/ Java 8 Sorted
 * List:
 * http://docs.oracle.com/javase/8/javafx/api/javafx/collections/transformation/SortedList.html
 *
 * How are slots calculated when iterating through a vector with many slot
 * definitions: ____ add ... keep
 *
 * ... discard ____ keep
 *
 * ________ combine both and keep ________ ________ do ___
 *
 * ....... combine both and keep ...... ....... do ...
 *
 * ________ add first part .... keep
 *
 * ________ keep last part .... keep
 *
 * ........ keep ____ discard
 *
 * ........ keep ____ discard
 *
 * Processing of undated slots u (---): ___ ____ s1 s2 --- u.end = s2.start
 *
 * __ ____ s1 s2 -- -- u1.end = u2.end = s2.start maintains a list of work slots
 * (time intervals in which work can be done). Overlapping positive slots are
 * not double-counted. Negative slots always overrule positive slots. Adjacent
 * slots (next starting at exactly the time the previous stops) are combined
 *
 *
 *
 * @author Thomas
 */
public class WorkTimeDefinition { //extends ItemList {

//    private int daysUpdateAhead = 30; //TODO!!! make this a user-definable setting
//    private String positiveMatchString = "Test";
//    private boolean positiveMatchStringMustMatchStartOfString = true;
//    private boolean positiveMatchStringCaseInsensitive;
//    private String negativeMatchString;
//    private boolean keepExpiredCalendarSlotsForHistoricalCalculations;
    private boolean hideExpiredOrInactiveWorkSlots;
    /**
     * contains the source slots (either defined manually or imported from PIM).
     * From these the available time slots are calculated and added to the
     * WorkSlotList (and then available via
     */
    private List workSlots;// = new ItemList(); //lazy
    private ItemList manuallyDefinedSlots;// = new ItemList(); //lazy
//    private final static boolean writeWorkSlotGuidsOnly = true;
//    private final static boolean saveManuallyDefinedSlotsInline=true;
//    private ItemList calendarImportedSlots;// = new ItemList(); //lazy
//    private final static boolean savecalendarImportedSlotsInline=true;
    /**
     * raw list of all source slots (e.g. repeating, imported), potentially
     * overlapping and with negative slots. From this raw list the
     * WorkSlotList's actually available slots are calculated
     */
//    ItemList rawSourceSlotList = new ItemList();
//    private CalendarImportDefinition calendarImportDefinition = new CalendarImportDefinition(this);
    /**
     * true if the WorkSlotList needs to be rebuild due to changes. Can be
     * checked each time the WorkSlotList is accessed, or by change callbacks?
     */
//    private boolean dirty;
//    final static ComparatorInterface workSlotComparatorStartDate = new ComparatorInterface() {
//    final static java.util.Collections.Comparator workSlotComparatorStartDate = new java.util.Collections.Comparator() {
    final static Comparator workSlotComparatorStartDate = (Comparator) (Object o1, Object o2) -> Item.compareLong(((WorkSlot) o1).getStartTime(), ((WorkSlot) o2).getStartTime());

//    private WorkTimeDefinition(Object ownerAndListener, boolean saveDirectly) {
////        super(null, ownerAndListener, true, saveDirectly, false, false, true, false); //only write guids of workTime slots //don't listen to slots added from manuallyDefinedSlots/calendarImportedSlots
////        setTypeId(BaseItemTypes.WORKTIMEDEFINITION);
//    }
//    WorkTimeDefinition(Object ownerAndListener) {
//        this(ownerAndListener,false);
//    }
    WorkTimeDefinition() {
//        this(null, false);
    }

    final static int IMPORT_OPTION_AUTOMATIC = 0;
    final static int IMPORT_OPTION_ASK = 1;
    final static int IMPORT_OPTION_MANUAL = 2;
    final static String[] importOptionsNames = new String[]{"Automatic", "Ask", "Manual"};
    final static int[] importOptionsValues = new int[]{IMPORT_OPTION_AUTOMATIC, IMPORT_OPTION_ASK, IMPORT_OPTION_MANUAL};

    public void externalize(DataOutputStream dos) throws IOException {

//        super.writeObject(dos); //use when overwriting this method in specialized classes
        dos.writeBoolean(hideExpiredOrInactiveWorkSlots);
//        BaseItemDAO.getInstance().writeObject(manuallyDefinedSlots, dos); //true=write WorkSlots inline
//        BaseItemDAO.getInstance().writeObject(calendarImportedSlots, dos); //stores imported calendar slots so same view as before can be shown when app is restarted
//        BaseItemDAO.getInstance().writeObject(calendarImportDefinition, dos);
        Util.writeObject(manuallyDefinedSlots, dos); //true=write WorkSlots inline
//        Util.writeObject(calendarImportedSlots, dos); //stores imported calendar slots so same view as before can be shown when app is restarted
//        Util.writeObject(calendarImportDefinition, dos);
    }

    public void internalize(int version, DataInputStream dis) throws IOException {

//        super.readObject(999, dis); //use when overwriting this method in specialized classes
        hideExpiredOrInactiveWorkSlots = dis.readBoolean();
//        manuallyDefinedSlots = (ItemList) BaseItemDAO.getInstance().readObject(dis);
        manuallyDefinedSlots = (ItemList) Util.readObject(dis);
//        calendarImportedSlots = (ItemList) BaseItemDAO.getInstance().readObject(dis);
//        calendarImportDefinition = (CalendarImportDefinition) BaseItemDAO.getInstance().readObject(dis);
    }

    /**
     *
     * @param manualImportRequestedByUser true indicates that a manual import
     * was already requested by the user (and thus no confirmation should be
     * asked here).
     */
//    public void importCalendarEventsAndUpdate(boolean manualImportRequestedByUser) {
//        if (calendarImportDefinition.updateApproach == IMPORT_OPTION_ASK && Dialog.show("Import calendar work slots?", "Import work slots defined in Calendar?", "OK", "Cancel")
//                || calendarImportDefinition.updateApproach == IMPORT_OPTION_ASK
//                || manualImportRequestedByUser) {
//            setImportedFromCalendarSlots(PIM_ToDo.importPIMEvents(MyDate.getNow(), MyDate.getNow() + calendarImportDefinition.daysUpdateAhead * MyDate.DAY_IN_MILLISECONDS)); //always re-import
//            if (!Settings.getInstance().silentCalendarImport()) {
//                if (!Dialog.show("Calendar import", getImportedFromCalendarSlots().getSize() + " calendar slots imported for the next " + calendarImportDefinition.daysUpdateAhead + " days.", "OK", "Don't show status")) {
//                    Settings.getInstance().setSilentCalendarImport(false);
//                }
//            }
//            changed();
//        } //else ignore command
//    }
    /**
     * updates the final, calculated list of available workSlots. E.g. removes
     * negative slots, recalculates repeating instances, re-imports from
     * Calendar(?!)
     */
    /**
     * updates the actually available workslots based on the source work slots
     * (which may overlap or be undated)
     *
     * @param reimportCalendarEvents true triggers (re-)import of calendarEvents
     */
    public void updateWorkSlots() { //boolean reimportCalendarEvents) {
        //clean up slots: mark slots in the past inactive, re
        //delete previously imported calendard slots (leave those in the past for historical data - implement this later?!)
        //re-import future calendar slots
//        ItemList calendarEvents = PIM_ToDo.importPIMEvents(MyDate.getNow(), MyDate.getNow() + daysUpdateAhead * MyDate.DAY_IN_MILLISECONDS);
//        calendarImportedSlots = PIM_ToDo.importPIMEvents(MyDate.getNow(), MyDate.getNow() + calendarImportDefinition.daysUpdateAhead * MyDate.DAY_IN_MILLISECONDS);
        //TODO!!!!: optimize this, e.g. do not sort calendar slots if they are sorted on import, or add manually defined slots last using insertSorted since they're likely to be fewer
//        setImportedFromCalendarSlots(PIM_ToDo.importPIMEvents(MyDate.getNow(), MyDate.getNow() + calendarImportDefinition.daysUpdateAhead * MyDate.DAY_IN_MILLISECONDS)); //always re-import
//        this.uncommit(); //block call backs during update of the list
//        if (reimportCalendarEvents) importCalendarEventsAndUpdate(false);
//        calculateAndAddAvailableSlots(calendarImportedSlots); //add, sort and remove overlaps
//        addAllItemsSorted(getManuallyDefinedSlots(), workSlotComparatorStartDate);
//        ItemList rawList = new ItemList(false,false);
        List rawList = new ArrayList();
//        getManuallyDefinedSlots().quicksort(workSlotComparatorStartDate); //sort the manually defined slots (they may not be inserted in order) //optimization: always insert sorted when creating slots?
        rawList.addAll(getManuallyDefinedSlots());
//        rawList.addAllItems(getImportedFromCalendarSlots());
//        calculateAndAddAvailableSlots(rawList); //add, sort and remove overlaps
        calculateAndAddAvailableSlots(rawList, false, slotEvaluator, false); //add, sort and remove overlaps
//        changed(); //send change event to e.g. ItemList
//        this.commit();
        //re-calculate repeating slots (and mark those in the past inactive)
        //calculate really available slots (don't double-count overlapping slots, remove 'negative' slots, ...)
//        changed(); //- no need to save just because we've recalculated (since result isn't saved anyway)
    }

    /**
     * updates the actually available workslots based on the source work slots
     * (which may overlap or be undated). Always reimports the calendar events
     * (use updateWorkSlots(false) to avoid this)
     */
//    public void updateWorkSlots() {
//        updateWorkSlots(true); //by 
//    }
//    public void importCalendarEventsAndUpdate_ForTest() {
//        calculateAndAddAvailableSlots(PIM_ToDo.importPIMEvents(MyDate.getNow(), MyDate.getNow() + (long) calendarImportDefinition.daysUpdateAhead * MyDate.DAY_IN_MILLISECONDS));
//        changed();
//    }
    public String toString() { //TODO: update
        String str = "{WORKTIMEDEFINTION:\n";
        str += "InputSlots=" + (manuallyDefinedSlots == null ? "<no manual slots defined>" : manuallyDefinedSlots.toString()) + "\n";
        str += "Result=" + super.toString() + "}";
        return str;
    }

    /**
     * adds a slot where work can be done. Overlapping intervals of slots are
     * only counted once.
     *
     * @param workSlot
     */
    public void addManuallyDefinedSlotSorted_ForTest(WorkSlot slot) {
//        getManuallyDefinedSlots().insertSortedIntoSortedList(slot, workSlotComparatorStartDate);
        getManuallyDefinedSlots().insertSortedIntoSortedList(slot, (Comparator) (Object o1, Object o2) -> Item.compareLong(((WorkSlot) o1).getStartTime(), ((WorkSlot) o2).getStartTime()));
//        changed();
    }

//    public void setMatchString(String string) {
//        calendarImportDefinition.positiveMatchString = string;
//    }
    interface SlotEvaluator {

        /**
         * returns true for WorkSlots than can be used to schedule tasks in
         */
        boolean positive(WorkSlot workSlot);

        /**
         * returns true for WorkSlots than can NOT be used to schedule tasks in
         * (which are 'deducted' from positive WorkSlots
         */
        boolean negative(WorkSlot workSlot);
    }
    //TODO!!!!: need three results for slots: positive=can work, negative=cannot work: neutral=ignore. OR, evaluate slots beforehand and simply skip neutral ones?
    final SlotEvaluator slotEvaluator = new SlotEvaluator() {

        /**
         * only imported items must match the positiveMatchString. Manual slots
         * just need to be not negative
         */
        public boolean positive(WorkSlot workSlot) {
//            return WorkSlotList.positive(workSlot);
//            if (workSlot.isImportedFromPIM()) {
//                return calendarImportDefinition.positiveMatchStringCaseSensitive
//                        ? calendarImportDefinition.positiveMatchStringMustMatchStartOfString ? workSlot.getText().toUpperCase().startsWith(calendarImportDefinition.positiveMatchString.toUpperCase()) : workSlot.getText().indexOf(calendarImportDefinition.positiveMatchString.toUpperCase()) != -1
//                        : calendarImportDefinition.positiveMatchStringMustMatchStartOfString ? workSlot.getText().startsWith(calendarImportDefinition.positiveMatchString) : workSlot.getText().indexOf(calendarImportDefinition.positiveMatchString) != -1;
//            } else {
            return !workSlot.isNegativeSlot();
//            }
//            return positiveOnMatchString(workSlot);
//            ASSERT.that("Not supported yet.");
        }

        public boolean negative(WorkSlot workSlot) {
            ASSERT.that("Not supported yet.");
            return false;
//            return negative(workSlot);
        }
    };

    private void insertSortedIntoSortedList(List<WorkSlot> slotsList, WorkSlot newWorkSlot) {
//        int index = Collections.binarySearch(slotsList, newWorkSlot, (WorkSlot s1, WorkSlot s2) -> new Long(s2.getStartTime()).compareTo(s1.getStartTime()));
//        int index = Collections.binarySearch(slotsList, newWorkSlot, (WorkSlot s1, WorkSlot s2) -> Long.compare(s2.getStartTime(), s1.getStartTime()));
        int index = Collections.binarySearch(slotsList, newWorkSlot,
                //                (WorkSlot s1, WorkSlot s2) -> s2.getStartTime() == s1.getStartTime() ? 0 : (s1.getStartTime() < s2.getStartTime() ? -1 : 1));
                (WorkSlot s1, WorkSlot s2) -> s1.getStartTime() < s2.getStartTime() ? -1 : (s1.getStartTime() == s2.getStartTime() ? 0 : 1));
        int insertionPoint = index < 0 ? -index - 1 : index; //see JavaDoc for Collections.binarySearch, if slot not found then index = -insertionPoint-1 <=> insertionPoint = -index-1
        slotsList.add(insertionPoint, newWorkSlot);
    }

    /**
     * calculates the actually available workSlots over which tasks can be
     * spread. Overlapping slots are not double-counted. Negative slots
     * overlapping positive ones are deducted, negative slots not overlapping
     * positive ones are removed. The end result is stored in the WorkSlotList's
     * list inherited from ItemList (accessible via eg addItem(), getItemAt()).
     * Indicentally, this approach makes it easy to combine several
     * WorkSlotLists (not used yet).
     *
     * @param sourceSlotsList the list of slots that the
     * @param preSorted is the sourceSlotList already sorted (if so, no need to
     * do it here)
     * @param slotEvaluator used to determine if a slot is positive or negative
     * @param createWorkingCopy create a working copy of the sourceSlotList (not
     * necessary if the sourceSlotList is a raw list that can be changed/thrown
     * away)
     */
    private void calculateAndAddAvailableSlots(List sourceSlotsList, boolean preSorted, SlotEvaluator slotEvaluator, boolean createWorkingCopy) {
        if (sourceSlotsList.size() == 0) {
            return; //return if no slots
        }
        workSlots.retainAll(new HashSet()); //remove all existing items before calculating and adding new ones
        List<WorkSlot> slotsList;
        if (createWorkingCopy || hideExpiredOrInactiveWorkSlots) {
//            slotsList = new ItemList((ItemListModel) sourceSlotsList); //create a working copy of the input list
            slotsList = new ArrayList(sourceSlotsList); //create a working copy of the input list
            if (hideExpiredOrInactiveWorkSlots) {
                int i = 0;
                while (i < slotsList.size()) {
                    if (!((WorkSlot) slotsList.get(i)).isActiveAdjusted()) {
                        slotsList.remove(i);
                    } else {
                        i++;
                    }
                }
            }
        } else {
            slotsList = sourceSlotsList; //work directly on provide list
        }
        if (!preSorted) {
//            slotsList.quicksort(workSlotComparatorStartDate);
//            slotsList.sort();
//            slotsList.sort(workSlotComparatorStartDate);
//            slotsList.sort((Comparator) (Object o1, Object o2) -> Item.compareLong(((WorkSlot) o1).getStartTime(), ((WorkSlot) o2).getStartTime()));
//            slotsList.sort((Comparator) (Object o1, Object o2) -> Item.compareLong(((WorkSlot) o1).getStartTime(), ((WorkSlot) o2).getStartTime()));
        }
        if (slotsList instanceof ArrayList) {
//            ((ArrayList) slotsList).sort((Comparator) (Object o1, Object o2) -> Item.compareLong(((WorkSlot) o1).getStartTime(), ((WorkSlot) o2).getStartTime()));
            Object[] list = slotsList.toArray();
            Arrays.sort(list); //.sort((Comparator) (Object o1, Object o2) -> Item.compareLong(((WorkSlot) o1).getStartTime(), ((WorkSlot) o2).getStartTime()));
            
        } else {
            throw new RuntimeException();
        }
        
        WorkSlot slotA; //invariant: A.start <= B.start, A.start always smallest of all remaining in vector
        WorkSlot slotB;
        //all slots sorted by starting time
        //initialize: get first start time of any slot A (positive or negative)
//        slotA = (WorkSlot) slotsList.getAndRemoveItemAt(0);
        slotA = (WorkSlot) slotsList.remove(0);
        //optimization: calculate positive only when a slot is changed
        while (slotsList.size() > 0) {
            slotB = (WorkSlot) slotsList.remove(0); // get next slot in line
            Log.p("WorkSlotList.calculate...: slotA=[" + slotA + "] slotB=[" + slotB + "]");
            if (!slotA.isActive()) {
                slotA = slotB;
            } else if (!slotB.isActive()) {
            }

            if (slotA.getEnd() <= slotB.getStartTime()) { //no overlap
// <editor-fold defaultstate="collapsed" desc="comment">
/* cases:
                 *         1:         2:
                 * slot A: +++        ---
                 * slot B:     xxx        xxx
                 * results:
                 *         +++        ---
                 *         add A      discard A
                 */// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
/* if B starts exactly when A ends, we could combine into a single continous slot to avoid counting a task split across the two as having a break
                however, better to keep the two slots separate since it's easier to understand for the user, and then fix this case in the break-counting algorithm */
//                    //combine into a single slot
                if (slotEvaluator.positive(slotA)) { //if slot negative, then it is just discarded since it won't overlap with anything
                    workSlots.add(slotA);
                    Log.p("WorkSlotList.calculate...: (1) adding available slot=[" + slotA + "]");
                } // else //discard negative slots
                slotA = slotB; //continue with next slot (with slotB as the next slotA)
            } else { //A and B overlaps (and A.start <= B.start since input list is sorted)
                boolean slotAPositive = slotEvaluator.positive(slotA);
                boolean slotBPositive = slotEvaluator.positive(slotB);
                if (slotAPositive == slotBPositive) { //if both slots either positive, or negative
// <editor-fold defaultstate="collapsed" desc="comment">
/* cases:
                     *         1:        2:        3:           4:         5:       6:        7:        8:      9:
                     * slot A: ++++      ----      ++++++++     ++++++     ------   ------    -----     -----   +++++
                     * slot B:   ++++      ----      +++        ++++++++     --     ----      --------  -----   +++++
                     * 'tests'    ---       +++++
                     * results:
                     *         ++++      ----      ++++++++     ++++++++   ------   ------    --------  -----   +++++
                     *             ++        --    ++++++++     ++++++++   ------   ------    --------  -----   +++++
                     *         keep A    keep A    keep A       keep B     keep A   keep A    keep B    keep A* keep A*
                     *         reduce B  reduce B  drop B       drop A     drop B   drop B    drop A    drop B  drop B
                     *
                     *         ++++++    ------    ++++++++     ++++++++   ------   ------    --------  -----   +++++
                     *   (*): could be B, but favor first slot
                     */// </editor-fold>
                    if (slotB.getStartTime() == slotA.getStartTime() && slotB.getEnd() > slotA.getEnd()) { //case 4+7: if B starts at same time as A, and is *longer*, then keep B, drop A (using '>' ensures case 8+9 are handled below)
                        slotA = slotB;
//                            slotB = (WorkSlot) slotsList.getAndRemoveItemAt(0); //-done at start of next iteration loop
                    } else if (slotB.getEnd() <= slotA.getEnd()) { //keep A, drop B: case 3+5+6+8+9: if B is contained within A, we skip B and continue with next (A cannot be contained in B since it starts at earlier or same time as B)
//                            slotB = (WorkSlot) slotsList.getAndRemoveItemAt(0);
                    } else { //keep A, reduce B to keep what is not overlapping with A (the part of B what comes after A):
//                        slotsList.insertSortedIntoSortedList(new WorkSlot(slotA.getEnd(), slotB.getEnd(), slotB), workSlotComparatorStartDate); //UI: reduce slotB to only the part that doesn't overlap with slotA, this means that all tasks that fit with slotA (first positive slot) will be shown with that slot, and only the part of slotB that comes after slotA will be used for tasks
//                        WorkSlot newWorkSlot = new WorkSlot(slotA.getEnd(), slotB.getEnd(), slotB);
                        //UI: reduce slotB to only the part that doesn't overlap with slotA, this means that all tasks that fit with slotA (first positive slot) will be shown with that slot, and only the part of slotB that comes after slotA will be used for tasks
//                        int index = Collections.binarySearch(slotsList, newWorkSlot, (WorkSlot s1, WorkSlot s2) -> new Long(s2.getStartTime()).compareTo(s1.getStartTime()));
//                        int insertionPoint = index < 0 ? -index - 1 : index; //see JavaDoc for Collections.binarySearch, if slot not found then index = -insertionPoint-1 <=> insertionPoint = -index-1
//                        slotsList.add(insertionPoint, newWorkSlot);
//                        insertSortedIntoSortedList(slotsList, newWorkSlot);
                        insertSortedIntoSortedList(slotsList, new WorkSlot(slotA.getEnd(), slotB.getEnd(), slotB));
//                        Item.compareLong(((WorkSlot) o1).getStartTime(), ((WorkSlot) o2).getStartTime())
                    }
                } else { // slots overlapping, one slot is positive, the other negative
// <editor-fold defaultstate="collapsed" desc="comment">
/* cases:
                     *         1:        2:        3:           4:           5:      6:       7:     8:
                     * slot P: ++++        ++++       +++       +++++++++    +++     ++++++   +++++  +++++
                     * slot N:   ----    ----      ---------       ---       ------  ---      -----    ---
                     *
                     * results:
                     *         ++        ----      ---------    +++   +++    ------  ---      -----  ++
                     *           ----        ++                    ---                  +++            ---
                     */// </editor-fold>
                    WorkSlot positiveSlot = slotAPositive ? slotA : slotB;
                    WorkSlot negativeSlot = slotAPositive ? slotB : slotA;

                    if (positiveSlot.getStartTime() < negativeSlot.getStartTime()) { //case 1+4+8: if the positiveSlot starts *before* the negative slot, then add a new slot with the positive part (negative slot remains unchanged)
                        WorkSlot temp = new WorkSlot(positiveSlot.getStartTime(), negativeSlot.getStartTime(), positiveSlot, negativeSlot);
                        workSlots.add(temp); //first part of positive slot added to results
                        Log.p("WorkSlotList.calculate...: (2) adding available slot=[" + temp + "]");
                    }
                    if (negativeSlot.getEnd() < positiveSlot.getEnd()) { //case 2+4+6: if the negativeSlot ends *before* the positive slot, then add a new slot with the positive part (negative slot remains unchanged)
//                        slotsList.insertSortedIntoSortedList(new WorkSlot(negativeSlot.getEnd(), positiveSlot.getEnd(), positiveSlot, negativeSlot), workSlotComparatorStartDate); //last part of positive slot added to slots for further processing
                        insertSortedIntoSortedList(slotsList, new WorkSlot(negativeSlot.getEnd(), positiveSlot.getEnd(), positiveSlot, negativeSlot));
                    }
                    //case 3+5+7: do nothing (positive slot is dropped)
                    slotA = negativeSlot; //keep and continue with the negative slot (until it doesn't overlap anything and can be skipped)
                }
            }
        } //while
        if (slotEvaluator.positive(slotA)) { //if whatever last slot remains is positive, then add it
            workSlots.add(slotA);
            Log.p("WorkSlotList.calculate...: (3) adding available slot=[" + slotA + "]");
        }
    }

//    private void calculateAndAddAvailableSlots(ItemList slotsList, boolean preSorted) {
//        calculateAndAddAvailableSlots(slotsList, preSorted, slotEvaluator, true);
//    }
//
//    private void calculateAndAddAvailableSlots(boolean createWorkingCopy, ItemList slotsList) {
//        calculateAndAddAvailableSlots(slotsList, false, slotEvaluator, createWorkingCopy); //if in doubt, sort
//    }
    void calculateAndAddAvailableSlots(List slotsList) {
        calculateAndAddAvailableSlots(slotsList, false, slotEvaluator, true); //if in doubt, sort
    }

//    protected SumVector remainingEffortSumVector = new SumVector(this, new SumVector.SumFieldGetter() {
//        public long getFieldValue(int index) {
//            return ((Item) workSlots.getItemAt(index)).getRemainingEffort();
//        }
//    });
    private SumVector remainingEffortSumVector = new SumVector(
            () -> workSlots.size(),
            (index) -> ((Item) workSlots.get(index)).getRemainingEffort()
    );
//     protected SumVector remainingEffortSumVectorx
//            = new SumVector(
//                    () -> ItemList.this.getSize(),
//                    (index) -> ((Item) getItemAt(index)).getRemainingEffort());

    /**
     * returns the index of the first Object that has a sum value greater than
     * or equal to sum. If no such element is found, e.g. sum is bigger than
     * total sum of all items in list, then -1 is returned.
     *
     * @param sum
     * @return index or -1
     */
    public int getIndexAtSumOfRemainingEffort(long sum) {
        return remainingEffortSumVector.getIndexAtSum(sum);
    }

    /**
     * returns the Finish date corresponding to workSum. It does this by finding
     * the work slot into which the workSum 'falls'. It then calculates how far
     * into this slot workSum falls by deducing the sum of all previous
     * workSlots from workSum. Then the Finish date is the start date of the
     * work slot + the difference between workSum and sum of previous workslots.
     * If the corresponding workSlot is undated, returns 0 (TODO: change to
     * -1??).
     *
     * @param item
     * @param workSum
     * @return
     */
    public long getItemFinishDate(/*Item item,*/long workSum) {
        int workSlotIndex = getIndexAtSumOfRemainingEffort(workSum);
        if (workSlotIndex >= 0) { //if workslot found
            WorkSlot workSlot = (WorkSlot) workSlots.get(workSlotIndex);
//            long workSlotSum = getSumOfRemainingEffort(workSlotIndex); //what's the total sum of the workslots
            long workSlotSum = remainingEffortSumVector.getSumAt(workSlotIndex); //what's the total sum of the workslots
            if (workSlot.getStartTime() != 0) { //if found slot has defined start date
// <editor-fold defaultstate="collapsed" desc="comment">
                /**
                 * worksum S E Tasks: +---+---+-------+ Slots:
                 * +--+--+----+--------+ x s y e | workSlotIdx=3 |workSlot|
                 * workSlotSum
                 *
                 * y = e -
                 * x =
                 */// </editor-fold>
                //possible finish date for item is the Start of the workSlot + sum of all workslots [up to and including this last one] minus the sum of all items [up to and including this last one] (since item can spread across multiple work slots)
//                                long itemFinishDate = workSlot.getEnd() - (workSlotSum - workSum);
/*
 *                          
 * |----|-------------|-----|  
                 */
                return workSlot.getEnd() - (workSlotSum - workSum);
            }
        }
        return 0;
    }

    /**
     * returns the Start date for item, under the assumption that for item's
     * list, the total sum of work, up to and including the item, is workSum. If
     * the corresponding workSlot is undated, returns 0.
     *
     * @param item
     * @param workSum
     * @return
     */
    public long getItemStartDate(long remainingWorkEffort, long workSum) {
//        int workSlotIndex = getIndexAtSumOfRemainingEffort(workSum);
//        if (workSlotIndex >= 0) { //if workslot found
//            int itemStartWorkSlotIndex = getIndexAtSumOfRemainingEffort(workSum - remainingWorkEffort); //get the index of the workSlot corresponding to the sum where the Item starts
//            WorkSlot itemStartWorkSlot = (WorkSlot) workSlots.get(itemStartWorkSlotIndex); //find the workslot that covers the time when the task starts
//            return itemStartWorkSlot.getEnd() - (getSumOfRemainingEffort(itemStartWorkSlotIndex) - (workSum - remainingWorkEffort)); // = itemStartDate
//// <editor-fold defaultstate="collapsed" desc="comment">
//            /**
//             *                        worksum
//             *                S       E
//             * Tasks: +---+---+-------+
//             * Slots: +--+--+----+--------+
//             *                x  s    y   e
//             *                   |
//             *                   workSlotIdx=3
//             *                     workSlot
//             *                            workSlotSum
//             *
//             * y = e -
//             * x =
//             */// </editor-fold>
//        }
//        return 0;
        return getItemFinishDate(workSum - remainingWorkEffort);
    }

    /**
     * return the number of *discontinuous* workslots a task is spanning over
     * (to show the user if a task is broken across several time slots at
     * different times). Discontinuous == with a non-zero break between them
     * (slot1.end < slot2.start) @param
     *
     * item @param workSum @return
     *
     */
    public int getNumberWorkSlotsSpannedByItem(Item item, long workSum) {
        int itemStartWorkSlotIndex = getIndexAtSumOfRemainingEffort(workSum - item.getRemainingEffort()); //get the index of the workSlot corresponding to the sum where the Item starts
        int itemEndWorkSlotIndex = getIndexAtSumOfRemainingEffort(workSum);
        if (itemStartWorkSlotIndex >= 0 && itemEndWorkSlotIndex >= 0) {
            return (itemEndWorkSlotIndex - itemStartWorkSlotIndex) + 1;
        }
        return 0;
    }

    /**
     * returns a calculated endDate for an unDated workSlot. It is calculated as
     * worst case, that is that the untimed workSlot ends when the next one in
     * the WorkSlotList begins. If there are several undated workslots after
     * each other, their dates are calculated. If there are no dated workSlot
     * after the undated one in the list (they're all undated, or it is the
     * last), no date is calculated, instead the Description of the WorkSlot is
     * returned.
     *
     * @param undatedWorkSlotIndex
     * @param undatedSlotDuration
     * @return
     */
    private long getEndDateForUndatedSlot(int undatedWorkSlotIndex, int undatedSlotDuration) { //TODO!!!!
        //
        if (undatedWorkSlotIndex >= workSlots.size()) //- NOT manuallyDefinedSlots since we only want to look at really available slots
        {
            return 0; //no dated slot was found
        } else {
            long start;
            if ((start = ((WorkSlot) getManuallyDefinedSlots().getItemAt(undatedWorkSlotIndex + 1)).getStartTime()) != 0) {
                return start;
            } else {
                //recurse on following work slot
                return getEndDateForUndatedSlot(undatedWorkSlotIndex + 1, undatedSlotDuration);
            }
        }
    }

//    public void xsetManuallyDefinedSlots(ItemList manuallyDefinedSlots) {
//        this.manuallyDefinedSlots = manuallyDefinedSlots;
//        this.manuallyDefinedSlots.setOwner(this);
////        this.manuallyDefinedSlots.setSaveDirectly(false) ; //-default is  false
//        manuallyDefinedSlots.quicksort(workSlotComparatorStartDate);
//        changed();
//    }
    public ItemList getManuallyDefinedSlots() {
        if (manuallyDefinedSlots == null) {
            manuallyDefinedSlots = new ItemList();//BaseItemTypes.WORKSLOT, this, false, true, true); //ItemListSaveInline(this); //addMultipleInstances=false(even though it gives an expensive test on each insertion it is safer so no insertLink can make a subtask appear several times)

        }
        return manuallyDefinedSlots;
    }

//    public void setImportedFromCalendarSlots(ItemList importedFromCalendarSlots) {
//        this.calendarImportedSlots = importedFromCalendarSlots;
//        this.calendarImportedSlots.setOwner(this);
//        importedFromCalendarSlots.quicksort(workSlotComparatorStartDate);
//        changed();
//    }
//
//    public ItemList getImportedFromCalendarSlots() {
//        if (calendarImportedSlots == null) {
//            calendarImportedSlots = new ItemList(BaseItemTypes.WORKSLOT, this, false, true, true); 
//        }
//        return calendarImportedSlots;
//    }
//    public CalendarImportDefinition getCalendarImportDefinition() {
//        return calendarImportDefinition;
//    }
//    public void setCalendarImportDefinition(CalendarImportDefinition calendarImportDefinition) {
//        this.calendarImportDefinition = calendarImportDefinition;
//        changed();
//    }
//
//    public void updateAndEnsureConsistencyJustBeforeSaving() {
//        super.updateAndEnsureConsistencyJustBeforeSaving();
//        if (manuallyDefinedSlots != null) {
////            manuallyDefinedSlots.quicksort(workSlotComparatorStartDate); //sort on saving first time
//            manuallyDefinedSlots.commit();
//        }
//        if (calendarImportedSlots != null) {
//            calendarImportedSlots.commit();
//        }
//    }
//    public void receiveChangeEvent(ChangeEvent changeEvent) {
////#mdebug
////        Log.l(" --receiveChangeEvent: " + this + "  RECEIVED-> " + changeEvent);
////#enddebug
//        Object source = changeEvent.getSource();
//        if (source == manuallyDefinedSlots ) {
//            if (ChangeValue.isSetEither(changeEvent.getChangeId(), ChangeValue.CHANGED_ITEMLIST_ITEM_ADDED, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED))
//                manuallyDefinedSlots.quicksort(workSlotComparatorStartDate); //sort on every insertion //optimization!!
//            updateWorkSlots(false);
//            //optimization: optimize recalculation based on specific change
//        } else if (source == calendarImportedSlots) {
//            //ignore
//        } else if (source == calendarImportDefinition) {
//            //ignore
//        } else { //from the calculated slots added to the WorkTimeDefinition list
//            ASSERT.that("WorkTimeDefinition.receiveChangeEvent, unexpected #changeEvent=" + changeEvent + " #WorkTimeDefinition=" + this);
//            super.receiveChangeEvent(changeEvent);
//        }
//    }
}
