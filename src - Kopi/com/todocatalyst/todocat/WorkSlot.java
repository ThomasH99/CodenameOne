/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import com.parse4cn1.ParseObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * a slot has a duration, which is either defined directly, or the difference
 * between a start time and an end time. Start and end time are optional. If
 * both an end time and a duration is set, the end time takes priority
 *
 * @author Thomas
 */
public class WorkSlot
        extends ParseObject /*extends BaseItem*/
        implements RepeatRuleObjectInterface, SumField, ItemAndListCommonInterface, Externalizable, Comparable /*
 * , IComparable
 */ {

    final static int FIELD_DESCRIPTION = 0;
    final static int FIELD_DURATION = 1;
    final static int FIELD_START_TIME = 2;
    final static int FIELD_INACTIVE = 3;
    final static int FIELD_REPEAT_DEFINITION = 4;
//    final static String FIELD_DESCRIPTION = "Description";
//    final static String FIELD_DURATION = "Duration";
//    final static String FIELD_START_TIME = "Start time";
//    final static String FIELD_INACTIVE = "Inactive";
//    final static String FIELD_REPEAT_DEFINITION = Item.getFieldHelpText(Item.FIELD_REPEAT_RULE);
    private final static FieldDef[] FIELDS = {
        //        new FieldDef(FIELD_DESCRIPTION, "Description", Expr.VALUE_FIELD_TYPE_BOOLEAN),
        new FieldDef(FIELD_DESCRIPTION, "Name", Expr.VALUE_FIELD_TYPE_STRING),
        new FieldDef(FIELD_INACTIVE, "Inactive", Expr.VALUE_FIELD_TYPE_BOOLEAN),
        new FieldDef(FIELD_DURATION, "Duration", Expr.VALUE_FIELD_TYPE_DURATION),
        new FieldDef(FIELD_START_TIME, "Start time", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_REPEAT_DEFINITION, "Repeat", Expr.VALUE_FIELD_TYPE_STRING), //TODO: what field type to use for RepeatRule?
    };

    public static FieldDef getFieldDef(int fieldId) {
        for (int i = 0, size = FIELDS.length; i < size; i++) {
            if (FIELDS[i].id == fieldId) {
                return FIELDS[i];
            }
        }
        ASSERT.that("ItemListFlatten.getFieldName(" + fieldId + ") - value not defined");
        return null;
    }

    /**
     * returns an Object containing the value of the given field (Object, since
     * this is used in Expr for filtering)
     *
     * @param fieldId
     * @return
     */
    public Object getFilterField(int fieldId) {
        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
            case FIELD_DESCRIPTION:
                return getText(); //optimization: replace by variable description directly
            case FIELD_INACTIVE:
                return new Boolean(isActive());
            case FIELD_START_TIME:
                return new Long(getStartTime());
            case FIELD_DURATION:
                return new Long(getDuration());
            case FIELD_REPEAT_DEFINITION:
                return getRepeatRule();
            default:
                ASSERT.that("Item: Field Identifier not defined " + fieldId);
        }
        return null;
    }

    public void setFilterField(int fieldId, Object fieldValue) {
        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
            case FIELD_DESCRIPTION:
                setText((String) fieldValue);
                break;
            case FIELD_INACTIVE:
                setActive((Boolean) fieldValue);
                break;
            case FIELD_START_TIME:
                setStartTime((Long) fieldValue);
                break;
            case FIELD_DURATION:
                setDuration((Long) fieldValue);
                break;
            case FIELD_REPEAT_DEFINITION:
                setRepeatRule((RepeatRuleParseObject) fieldValue);
                break;
            default:
                ASSERT.that("Item.setFilterField: Field Identifier not defined " + fieldId);
        }
    }
    /**
     * used to store a copy of the text of the work
     */
//    private String description; // = "";
    /**
     * when does the workslot start
     */
//    private long startTime;
//    private long end;
    /**
     * first estimate of duration of this workSlot. Allows to keep track of
     * whether you work more or less of a given slot than originally planned
     */
//    private int initialDuration;
    /**
     * duration in milliseconds of this workslot
     */
    /**
     * the duration of workslot. Either the original defined value if the
     * workslot has not started yet (start > now) or if the workslot has
     * started, adjusted for remaining time or zero if the workslot is in the
     * past.
     *
     * OLD: how much time of this workSlot remains. Used to keep track of how
     * much time is remaining of the workslot. E.g. when working on tasks during
     * the timeSlot, keep track of working on tasks during the given WorkSlot
     */
//    private long duration;
    /**
     * indicates how much of the available duration has been consumed. By
     * separating duration and this, it is possible to keep the original
     * duration for historical analysis
     */
    private long consumedDuration;
    /**
     * if true, this WorkSlot is 'negative', meaning not available for working
     * (no work can or should be done here). Used eg to block certain periods of
     * otherwise positive WorkSlots
     */
//    private boolean negativeSlot;
    /**
     * true when the workslot is no longer valid/active, e.g. if an undated
     * workslot has been consumed or the user doesn't find it valid anymore
     */
//    private boolean inactive; //=false; //active by default
    /**
     * maintains a list of WorkSlots that have contributed positively or
     * negatively to this WorkSlot. Needed to expand/show the source(s) of a
     * given available/actual WorkSlot, as well as any negative WorkSlots that
     * have reduced the available time
     */
//    private Vector zzsourceSlots;
//    private WorkSlot sourceSlotA;
//    private WorkSlot sourceSlotB;
//    private MyRepeatRule repeatRule;
    /**
     * true if this slot is imported from the Calendar, meaning it should be
     * deleted again when re-importing (except if in the past and kept for
     * historical data)
     */
//    private boolean importedFromPIM;
    /**
     * used to keep track of when a workslot was imported (0 if not) to e.g.
     * delete future previously imported workslots before importing new ones
     */
//    private long calendarImportDate;
    /**
     * set an alarm for a workslot to remind that it starts. Use a boolean
     * instead with a default remind time? No specific alarm more fliexible (if
     * kept consistent)
     */
    private long alarmTime;

//    public boolean isImportedFromPIM() {
//        return importedFromPIM;
//    }
//
//    public final void setImportedFromPIM(boolean importedFromPIM) {
//        this.importedFromPIM = importedFromPIM;
//    }
    public RepeatRuleParseObject getRepeatRule() {
//        return repeatRule;
//        ParseObject.fetch("RepeatRule", repeatRule.getObjectId());
        return (RepeatRuleParseObject) getParseObject("repeatRule");
    }

    /**
     * sets a repeatRule. If it is the same repeatRule as before, there's no
     * change. If is a new repeatRule, and there is no previous one, then the
     * new rule is simply set. If the new rule is null, and there was a previous
     * rule, then the rule was deleted, in which case all the processing related
     * to the deletion is done in the repeatRule.delete(). If it's a new
     * repeatRule replacing a previous one, then The repeatInstances are not
     * calculated at this time, because the user may Cancel the update of the
     * item.
     *
     * @param repeatRule
     */
    @Override
    public final void setRepeatRule(RepeatRuleParseObject repeatRule) {
//        if (this.repeatRule != repeatRule) {
//            this.repeatRule = repeatRule;
//            changed();
//        }
//        if (true) {
//        if (this.repeatRule != repeatRule) {
//            this.repeatRule = repeatRule; //TODO!!!!: shouldn't repeat instances be calculated/updated whenever a repeat rule sit set?! (Currently done in copyMeInto)
////            changed();
//        }
//        ParseObject.fetch("RepeatRule", repeatRule.getObjectId());
        put("repeatRule", repeatRule);
//        } else {
//            if (this.repeatRule != null && repeatRule != null && this.repeatRule != repeatRule) {
//                //TODO!!!!: normally the user cannot change from one repeatRule to another (but we could programatically) - ensure item is removed from old rule
//                this.repeatRule.deleteRuleAndAllRepeatInstancesExceptThis(this);
//            }
//            if ((repeatRule != this.repeatRule)) { //if it's a new repeat rule then replace the old one
//                this.repeatRule = repeatRule; //TODO!!!!: shouldn't repeat instances be calculated/updated whenever a repeat rule sit set?! (Currently done in copyMeInto)
//            }
//            if (this.repeatRule != null && this.repeatRule.isDirty()) { //only call changed (on Item) if the repeat rule has actually been edited
//                changed();
//            }
//        }
    }

    WorkSlot() {
        super("WorkSlot");
//        super(true); //saveDirectly
//        setTypeId(BaseItemTypes.WORKSLOT);
    }

    WorkSlot(String description, long start, long duration, RepeatRuleParseObject myRepeatRule, boolean importedFromPIM) {
        this();
//        this.start = start;
//        this.description = description;
        setText(description);
//        this.startTime = start;
        setStartTime(start);
//        this.duration = duration;
        setDuration(duration);
//        this.repeatRule = repeatRule;
        setRepeatRule(myRepeatRule);
//        setImportedFromPIM(importedFromPIM);
//        this.importedFromPIM = importedFromPIM;
    }

    WorkSlot(String description, long start, long duration, RepeatRuleParseObject myRepeatRule) {
        this(description, start, duration, myRepeatRule, false);
    }

    /**
     * create a copy of workSlot
     *
     * @param workSlot
     */
    WorkSlot(WorkSlot workSlot) {
        this(workSlot.getText(), workSlot.getStartTime(), workSlot.getDuration(), workSlot.getRepeatRule()); //, workSlot.isImportedFromPIM());
    }

    WorkSlot(String text, long start, int duration) {
        this(text, start, duration, null, false);
    }

    WorkSlot(long start, long end) {
        this("", start, (int) (end - start), null, false);
////        this.start = start;
//        setStart( start);
////        this.end = end;
//        setDuration((int)(end-start));
    }

    /**
     * used to create repeat copies of a WorkSlot, e.g. for Repeat instances
     */
    WorkSlot(WorkSlot sourceWorkSlot, long startDate) {
        this(sourceWorkSlot.getText(), startDate, sourceWorkSlot.getDuration(), sourceWorkSlot.getRepeatRule(), false);
//        if (sourceWorkSlot.getMyRepeatRule()!=null)
//            this.repeatRule=sourceWorkSlot.getMyRepeatRule();
    }

    WorkSlot(String text, long start, long end) {
//        this(start, end);
//        setText(text);
//        this(text, start, (int) (end - start), null, false);
        this(text, start, (end - start), null, false);
    }

    WorkSlot(long start, long end, WorkSlot positiveSourceSlot) {
        this(positiveSourceSlot.getText(), start, end);
//        zzaddSourceSlot(positiveSourceSlot);
//        sourceSlots=new Vector(1);
//        sourceSlots.addElement(sourceSlot);
//        sourceSlotA = sourceSlot;
    }

    WorkSlot(long start, long end, WorkSlot positiveSourceSlot, WorkSlot negativeSourceSlot) {
        this(positiveSourceSlot.getText(), start, end); //use text from positive workSlot
//        zzaddSourceSlots(positiveSourceSlot, negativeSourceSlot);
// <editor-fold defaultstate="collapsed" desc="comment">
//        sourceSlots=new Vector(1);
//        sourceSlots.addElement(sourceSlot);
//        sourceSlotA = sourceSlot;// </editor-fold>
    }

//    public void changed() {
////        changed(changeValue.CHANGED_BASEBASEITEM_UNKNOWN_CHANGE);
//        changed(ChangeValue.CHANGED_XX_WORKSLOT_ANYCHANGE);
//    }
// <editor-fold defaultstate="collapsed" desc="comment">
    /**
     * create a slot that combines slot A and B. Requires that the two slots
     * overlap, otherwise an exception!
     */
//    WorkSlot(WorkSlot slotA, WorkSlot slotB) {
////        start = Math.min(slotA.getStart(), slotB.getStart());
////        end = Math.max(slotA.getEnd(), slotB.getEnd());
//        this(Math.min(slotA.getStart(), slotB.getStart()), Math.max(slotA.getEnd(), slotB.getEnd()));
//        ASSERT.that(getDuration() <= slotA.getDuration() + slotB.getDuration(), "Slot A [" + slotA + "] and Slot B [" + slotB + "] are disjoint (not overlapping)");
////        sourceSlots=new Vector(2);
////        //add WorkSlot with smallest start first (or A if both slots start at same time)
////        sourceSlots.addElement(slotA.getStart()<=slotB.getStart()? slotA: slotB);
////        sourceSlots.addElement(slotA.getStart()<=slotB.getStart()? slotB: slotA);
//        //add WorkSlot with smallest start first (or A if both slots start at same time)
////        sourceSlotA = slotA;
////        sourceSlotB = slotB;
//    }// </editor-fold>
    /**
     * adds workSlot to this WorkSlot's list of 'source' WorkSlots.
     *
     * @param workSlot
     */
//    private void zzaddSourceSlot(WorkSlot workSlot) {
//        if (zzsourceSlots == null) {
//            zzsourceSlots = new Vector();
//        }
////// <editor-fold defaultstate="collapsed" desc="comment">
//////        if (workSlot.sourceSlots != null) {
//////            for (int i = 0, size = workSlot.sourceSlots.size(); i < size; i++) {
//////                sourceSlots.addElement(workSlot.sourceSlots.elementAt(i));
//////
//////            }
//////
////        } else {// </editor-fold>
//        zzsourceSlots.addElement(workSlot);
////        }
//    }
//    private void zzaddSourceSlots(WorkSlot workSlot, WorkSlot workSlot2) {
//        if (zzsourceSlots == null) {
//            zzsourceSlots = new Vector();
//        }
//        zzsourceSlots.addElement(workSlot);
//        zzsourceSlots.addElement(workSlot2);
//    }
//    /** return an ItemList with all the WorkSlots that have contributed to making up this WorkSlot (e.g. positive slots added together, negative slots, ...
//    won't work because we can save references to workslots without a unique ID and some way of getting hold of them */
//    private ItemList zzgetSourceSlots() {
//        ItemList sourceList = new ItemList();
//        if (zzsourceSlots == null) {
//            return sourceList;
//        } else {
//            for (int i = 0, size = zzsourceSlots.size(); i < size; i++) {
//                sourceList.addAllItems(((WorkSlot) zzsourceSlots.elementAt(i)).zzgetSourceSlots());
//            }
//        }
//        return sourceList;
//    }
    //    WorkSlot createRepeatCopy(long startDate) {
//        return new WorkSlot(this, startDate);
//    }/** instantiates the MyRepatRule interface to be able to create repeat copies etc. */
    public long getRepeatStartTime(boolean fromCompletedDate) {
        return getStartTime();
    }

    public void setRepeatStartTime(long repeatStartTime) {
        setStartTime(repeatStartTime);
    }

    public RepeatRuleObjectInterface createRepeatCopy(long referenceTime) {
        return new WorkSlot(this, referenceTime);
    }

    @Override
    public void externalize(DataOutputStream dos) throws IOException {
//        try {
//            if (sourceSlots==null) { //only store sourceSlots
//            if (true || !importedFromPIM || (Settings.getInstance().storePastCalendarItemWorkSlots() && getEnd() < MyDate.getNow())) {
//            super.writeObject(dos);
//        dos.writeUTF(description);
//        BaseItemDAO.getInstance().writeString(description, dos);
//        BaseItemDAO.getInstance().writeObject(description, dos);
        Util.writeObject(getText(), dos);
        dos.writeLong(getStartTime());

        dos.writeLong(getDuration());
        dos.writeLong(consumedDuration);
//        dos.writeBoolean(negativeSlot);
        dos.writeBoolean(isNegativeSlot());

//        dos.writeBoolean(inactive);
        dos.writeBoolean(isActive());
//        BaseItem.writeGuidInline(dos, repeatRule);
//        BaseItemDAO.getInstance().writeObject(repeatRule, dos); //, true);
        Util.writeObject(getRepeatRule(), dos); //, true);
//            dos.writeInt(sourceSlotA != null ? sourceSlotA.getGuid() : 0);
//            dos.writeInt(sourceSlotB != null ? sourceSlotB.getGuid() : 0);
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    @Override
    public void internalize(int version, DataInputStream dis) throws IOException {
//        try {
//            super.readBaseItem(dis);
//        description = dis.readUTF();
//        description = BaseItemDAO.getInstance().readString(dis);
//        description = (String) BaseItemDAO.getInstance().readObject(dis);
//        description = (String) Util.readUTF(dis);
        setText((String) Util.readUTF(dis));
//        startTime = dis.readLong();
        setStartTime(dis.readLong());

//        duration = dis.readLong();
        setDuration(dis.readLong());
        consumedDuration = dis.readLong();
//        negativeSlot = dis.readBoolean();
        setNegativeSlot(dis.readBoolean());

//        inactive = dis.readBoolean();
        setActive(dis.readBoolean());
//        repeatRule = (MyRepeatRule) BaseItem.readGuidInline(dis, BaseItemTypes.MYREPEATRULE);
//        repeatRule = (MyRepeatRule) BaseItemDAO.getInstance().readObject(dis);
//        repeatRule = (MyRepeatRule) Util.readObject(dis);
        setRepeatRule((RepeatRuleParseObject) Util.readObject(dis));
// <editor-fold defaultstate="collapsed" desc="comment">
//            int guid;
//            guid = dis.readInt();
//            if ((guid = dis.readInt()) != 0) {
//                sourceSlotA = (WorkSlot) BaseItemDAO.getInstance().getBaseItem(guid);
//            }
//            if ((guid = dis.readInt()) != 0) {
//                sourceSlotB = (WorkSlot) BaseItemDAO.getInstance().getBaseItem(guid);
//            }// </editor-fold>
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

// <editor-fold defaultstate="collapsed" desc="comment">
//    WorkSlot(WorkSlot slotA, WorkSlot slotB, boolean positives) {
////        start = Math.min(slotA.getStart(), slotB.getStart());
////        end = Math.max(slotA.getEnd(), slotB.getEnd());
//        if (positives) {
//            if (slotA.getStart()<=slotB.getStart()) {
//
//            }
//        }
//        this(Math.min(slotA.getStart(), slotB.getStart()), Math.max(slotA.getEnd(), slotB.getEnd()));
////        sourceSlots=new Vector(2);
////        //add WorkSlot with smallest start first (or A if both slots start at same time)
////        sourceSlots.addElement(slotA.getStart()<=slotB.getStart()? slotA: slotB);
////        sourceSlots.addElement(slotA.getStart()<=slotB.getStart()? slotB: slotA);
//        //add WorkSlot with smallest start first (or A if both slots start at same time)
//        sourceSlotA = slotA;
//        sourceSlotB = slotB;
//    }// </editor-fold>
    /**
     * returns the text string to show in tasks to idnetify the work slot the
     * task fits into. E.g. if no startDate is defined, only the Description is
     * returned (for example "Week47" or "Weekend w47"). If StartDate is
     * defined, then returns start date + optional description (for example
     * "21/7/2011 Evening when kids are away").
     *
     * @return
     */
    public String getShowText() {
        if (getStartTime() == 0) {
            return (getText().equals("") ? "<no text>" : " \"" + getText() + " \"");
        } else {
//            return (getText().equals("") ? "" : " \"" + getText() + " \"" + " ") + new MyDate(getStart()).formatDate(false);
            return (new MyDate(getStartTime()).formatDate(MyDate.FORMAT_DDMM_HHMM)
                    + "-" + new MyDate(getEnd()).formatDate(MyDate.FORMAT_DDMM_HHMM)
                    + (getText().equals("") ? "" : " \"" + getText() + " \"" + " "));
        }
    }

    public final void setText(String text) {
//        if (this.description == null || !this.description.equals(text)) {
//            this.description = text;
////            changed();
//        }
        if ((has("text") || !text.equals(""))) { //don't test for val != null to avoid silent failure on this error condition
            put("text", text);
        }
    }

    public String getText() {
// <editor-fold defaultstate="collapsed" desc="comment">
//        if (sourceSlotA != null) {
//            return sourceSlotA.getText(); //TODO!!!: when two positive slots have been combined, we need a way to get the text of the second one (currently, only the first is returned)
//        } else {
//            return super.getText();
//        }// </editor-fold>
//        if (description == null) {
//            description = ""; //lazy init
//        }
//        return description;
        String s = getString("text");
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    public String toString() {
        return "SLOT[" + getText() + "|Start=" + new MyDate(getStartTime()).formatDate(false) + "|End=" + new MyDate(getEnd()).formatDate(false) + "|Duration=" + Duration.formatDuration(getDuration()) + "]";
    }

    public String shortString() {
        return "SLOT(" + getText() + ";S=" + new MyDate(getStartTime()).formatDate(true) + ";E=" + new MyDate(getEnd()).formatDate(true) + ";D=" + Duration.formatDuration(getDuration()) + ")";
    }

    public long getSumField(int fieldId) {
        return getSumField();
    }

    public long getSumField() {
        return getDurationAdjusted();
    }

    public boolean ignoreSumField() {
        return !isActive();
    }

    public long getStartTime() {
//        return start;
        //ensures that start is updated to a later value if duration is reduced as the workSlot is eaten up by tasks
        //(otherwise as you complete tasks, the next incomplete task will always start at the original start time).
        //if duration is not updated, then initialDuration-duration==0 meaning original start is always used
//        return start + (initialDuration - duration);
        Date startTime = getDate("startTime");
        return (startTime == null) ? 0 : startTime.getTime();

//        return startTime;
    }

    /**
     * adjusts start in case the slot covers the current time, or in case some
     * of the duration has already been consumed (this is necessary to make it
     * possible to reduce a time slot as it is consumed by individual tasks,
     * otherwise each time a task is completed, the next will start at start
     * time of the slot). If startDate is in the past, return NOW
     * (MyDate.getNow())
     */
    public long getStartAdjusted() {
//        long start = getStart();
        long now = MyDate.getNow();
// <editor-fold defaultstate="collapsed" desc="comment">
//        if (start < now) {
//            return now;
//        } else {
//            return Math.max(getStart() + (initialDuration - duration), now); //"start + (initialDuration - duration)" add the difference btw initialDuration and current duration (=time consumed) to original start, return the latest time of this new start, and NOW// </editor-fold>
        return Math.max((getStartTime() + consumedDuration), now); //"start + (initialDuration - duration)" add the difference btw initialDuration and current duration (=time consumed) to original start, return the latest time of this new start, and NOW
//        }
    }

    public final void setStartTime(long start) {
//        if (this.startTime != start) {
//            this.startTime = start;
////            changed();
//        }
        if (has("startTime") || start != 0) {
            put("start", start);
        }

    }

    public long getEnd() {
//        return end!=0L?end:start+duration;
//        return start + duration;
        return getStartTime() + getDuration(); //unchanged, even as start is in the past, or consumedDuration>0
    }

    public void setEnd(long end) {
        setDuration((int) (end - getStartTime()));
    }

    /**
     * returns defined duration of work slot (as defined by user, unadjusted in
     * case part of the slot is in the past)
     *
     * @return
     */
    public long getDuration() {
// <editor-fold defaultstate="collapsed" desc="comment">
//        if (start != 0L && end != 0L) {
//            return (int) (end - start);
//        } else {
//            return duration;
//        }
//        return inactive ? 0 : duration;// </editor-fold>
        if (has("duration")) {
            return getLong("duration");
        } else {
            return 0;
        }
//        return duration;
    }

    /**
     * returns actually available duration of work slot. The available time is
     * calculated as duration value minus whatever is biggest of:
     * consumedDuration or what is left of the slot if the start time is already
     * passed. If the slot is marked inactive, it always returns 0.
     *
     * @return
     */
    public long getDurationAdjusted() {
// <editor-fold defaultstate="collapsed" desc="comment">
//        long now = MyDate.getNow();
//        return inactive ? 0 : (int)(duration - (getStartAdjusted()-start));
//        return inactive ? 0 : (int) (initialDuration - (getStartAdjusted() - start));
//        return inactive ? 0 : Math.max((int) (duration-consumedDuration) - duration(getStartAdjusted() - start));
//                inactive //always inactive if inactive is set
//                || (!inactive && start!=0 && getEnd()<=MyDate.getNow()) //inactive even if !inactive, iff it is dated and end date is in the past
//                || (!inactive && consumedDuration>=duration) //inactive if time has been consumed// </editor-fold>
        long startTime = getStartTime();
//        return inactive ? 0
        return !isActive() ? 0
                : startTime == 0 ? getDuration() - consumedDuration
                        : //for undated slots, workSlot can only be reduced by consuming it, not by the passing of time
                        getDuration() - Math.max(consumedDuration, //0 unless set
                                Settings.getInstance().adjustWorkSlotsWhenStartIsInThePast()
                                        ? //only
                                        ((int) Math.min(getDuration(), //with min() ensures that can never get bigger than duration
                                                Math.max(0, (MyDate.getNow() - startTime)))) : 0); //now-start==what is eaten out of duration if start is in the past; min(duration,*) to avoid that now-start can get bigger than int
    }

    public final void setDuration(long duration) {
//        if (this.duration != duration) {
//            this.duration = duration;
////            changed();
//        }
        if (has("duration") || duration != 0) {
            put("duration", duration);
        }

    }

    public final void setConsumedDuration(long consumedDuration) {
        if (this.consumedDuration != consumedDuration) {
            this.consumedDuration = consumedDuration;
//            changed();
        }
    }

    /**
     * returns consumed duration of work slot
     *
     * @return
     */
    public long getConsumedDuration() {
        return consumedDuration;
    }

    /**
     * true if this work slot is active (available for tasks) otherwise false
     */
    public final void setActive(boolean active) {
//        if (this.inactive == active) { //NB! ==, not !=
//            inactive = !active;
////            changed();
//        }
        if (has("active") || active == true) {
            put("active", active);
        }
    }

    public final boolean isActive() {
        if (has("active")) {
            return getBoolean("active");
        } else {
            return false;
        }
//        return !inactive;
    }

    /**
     * true if this work slot is active (has available work time for tasks)
     * otherwise false. A slot can become inactive either by being marked
     * inactive, or if its end time is in the past (automatically
     */
    public final boolean isActiveAdjusted() {
//        inactive = getEnd()<=MyDate.getNow(); //automatically mark work slot permanently inactive //- not more efficient than below OR statement
        return getDurationAdjusted() > 0; //ensures result is consistent with getDurationAdjusted
// <editor-fold defaultstate="collapsed" desc="comment">
//        return !( //easier to define when it's inactive and then negate:
//                //inactive if:
//                inactive //always inactive if inactive is set
//                || (!inactive && start!=0 && getEnd()<=MyDate.getNow()) //inactive even if !inactive, iff it is dated and end date is in the past
//                || (!inactive && consumedDuration>=duration) //inactive if time has been consumed
//                );
//        return !inactive;// </editor-fold>
    }

    public boolean isNegativeSlot() {
        if (has("negativeSlot")) {
            return getBoolean("negativeSlot");
        } else {
            return false;
        }
//        return negativeSlot;
    }

    public void setNegativeSlot(boolean negativeSlot) {
        if (has("negativeSlot") || negativeSlot == true) {
            put("negativeSlot", negativeSlot);
        }
//        if (this.negativeSlot != negativeSlot) {
//            this.negativeSlot = negativeSlot;
////            changed();
//        }
    }

// <editor-fold defaultstate="collapsed" desc="comment">
//    public WorkSlot getSourceSlotA() {
//        return sourceSlotA;
//    }
//
//    public void setSourceSlotA(WorkSlot sourceSlotA) {
//        this.sourceSlotA = sourceSlotA;
//    }
//
//    public WorkSlot getSourceSlotB() {
//        return sourceSlotB;
//    }
//
//    public void setSourceSlotB(WorkSlot sourceSlotB) {
//        this.sourceSlotB = sourceSlotB;
//    }
    // </editor-fold>
//    public void updateAndEnsureConsistencyJustBeforeSaving() { ///* boolean afterSetSaveImmediately */) {
//        super.updateAndEnsureConsistencyJustBeforeSaving();
//        //do not calculate RepeatRule until Item is saved (to ensure the latest values are used, e.g. if item edited after repeatRule was edited, and possibly committed
//        MyRepeatRule myRepeatRule = getMyRepeatRule();
//        if (myRepeatRule != null) { // /** && !isTemplate() */) {
////            getRepeatRule().setSaveImmediately(true); //if not already saved, then do it now (does nothing if already saveImmediately)
//            if (!isActiveAdjusted()) {
////                myRepeatRule.updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(this, (ItemList) getParent());
////                myRepeatRule.updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(true, this, (ItemList) getOwner());
//                myRepeatRule.updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(true, this);
//                setRepeatRule(null); //UI: first time an Item is set Done, it is (permanently) disconnected from its repeatRule (hence, we only land in these lines of code if the task has just been completed
//            } else { //all other cases than where task was marked done: update RepeatRule
////                if (myRepeatRule.isDirty()|| !myRepeatRule.isCommitted()) { //only recalculate and commit if rule is either edited/dirty, or new/unComitted //-check moved into generateRepeatInstances()
//                if (!myRepeatRule.repeatRuleNewInstancesCalculationOngoing) { //if true, then this Item was generated by the RepeatRule and should therefore not update the rule or commit() it
//                    myRepeatRule.generateRepeatInstances(this, (ItemList) getOwner()); //true: first time this rule is called
////                    myRepeatRule.commit();
////                    myRepeatRule.setSaveImmediately(true);
//                }
//            }
//            myRepeatRule.commit(); //if not already saved, then do it now (does nothing if already saveImmediately)
//        }
//    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void xupdateAndEnsureConsistencyJustBeforeSaving() {
    //        //do not calculate RepeatRule until Item is saved (to ensure the latest values (start date) are used, e.g. if item edited after repeatRule was edited, and possibly committed
    ////        MyRepeatRule myRepeatRule = getMyRepeatRule();
    ////        if (myRepeatRule != null) {
    ////            if (!isActiveAdjusted()) { //if we save a workslot which is no longer active, then calculate next ones
    ////                myRepeatRule.updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(true, this, (ItemList) getOwner());
    ////                setRepeatRule(null);
    ////            } else { //all other cases than where task was marked done: update RepeatRule
    ////                if (!myRepeatRule.repeatRuleNewInstancesCalculationOngoing) { //if true, then this Item was generated by the RepeatRule and should therefore not update the rule or commit() it
    ////                    myRepeatRule.generateRepeatInstances(this, (ItemList) getOwner()); //true: first time this rule is called
    ////                    myRepeatRule.commit();
    ////                }
    ////            }
    ////            if (!myRepeatRule.isCommitted()) {
    ////                myRepeatRule.commit(); //save the rule if necessary
    ////            }
    ////        }
    ////        super.save();
    //        super.updateAndEnsureConsistencyJustBeforeSaving();
    //        //do not calculate RepeatRule until Item is saved (to ensure the latest values are used, e.g. if item edited after repeatRule was edited, and possibly committed
    //        MyRepeatRule myRepeatRule = getMyRepeatRule();
    //        if (myRepeatRule != null) {
    ////            getMyRepeatRule().setSaveImmediately(true); //if not already saved, then do it now (does nothing if already saveImmediately)
    //            getMyRepeatRule().commit(); //if not already saved, then do it now (does nothing if already saveImmediately)
    //            if (!isActiveAdjusted()) {
    //                myRepeatRule.updateRemoveItemUpdateInstancesAndRemoveRuleIfNoMoreInstances(true, this, (ItemList) getOwner());
    //                setRepeatRule(null); //UI: first time an Item is set Done, it is (permanently) disconnected from its repeatRule (hence, we only land in these lines of code if the task has just been completed
    //            } else { //all other cases than where task was marked done: update RepeatRule
    //                if (!myRepeatRule.repeatRuleNewInstancesCalculationOngoing) { //if true, then this Item was generated by the RepeatRule and should therefore not update the rule or commit() it
    //                    myRepeatRule.generateRepeatInstances(this, (ItemList) getOwner()); //true: first time this rule is called
    //                }
    //            }
    //        }
    //    }
    //</editor-fold>
    @Override
    public void delete() {
        RepeatRuleParseObject myRepeatRule = getRepeatRule();
        if (myRepeatRule != null) {
//            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this, (ItemList) getOwner());
            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this);
        }
//        ((WorkSlotList) getOwner()).removeItem(this);
//        ((ItemList) getOwner()).removeItem(this);
//        super.delete();
    }

    @Override
    public ItemList getListForNewCreatedRepeatInstances() {
        return (ItemList) getOwner(); //should always be a WorkSlotList which is an ItemList
    }

    @Override
    public void deleteRepeatInstance() {
        delete();
    }

    @Override
    public boolean isNoLongerRelevant() {
        return !isActiveAdjusted();
    }
// <editor-fold defaultstate="collapsed" desc="comment">
//    private int compareLong(long d1, long d2) {
//        if (d1 < d2) {
//            return -1;
//        } else if (d2 < d1) {
//            return 1;
//        }
//        return 0;
//    }// </editor-fold>
//    public int compareTo(Object o) {
//        WorkSlot workSlot = (WorkSlot) o;
//        if (getStart()==0 || workSlot.getStart()==0) return 0; //if one slot is undated, then return that they're equal (to avoid sorting/moving undated slots)
//        return Item.compareLong(getStart(), workSlot.getStart());
////        throw new RuntimeException("Not supported yet.");
//    }
//
//    public int compareTo(IComparable c, int compareOn) {
//        throw new RuntimeException("Not supported yet.");
//    }

    @Override
    public Item.ItemStatus getStatus() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getRemainingTime() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getRemainingEffort() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfUndoneItems(boolean includeSubTasks) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfUndoneItems() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getComment() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setComment(String val) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isExpandable() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ItemAndListCommonInterface getOwner() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ItemAndListCommonInterface setOwner(ItemAndListCommonInterface owner) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemIdStr() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString(ToStringFormat format) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ItemAndListCommonInterface cloneMe() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copyMeInto(ItemAndListCommonInterface destiny) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatus(Item.ItemStatus itemStatus) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getVersion() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(Object o) {
        long s1 = getStartTime();
        long s2 = ((WorkSlot)o).getStartTime();
        if (s1 < s2) return -1; else if (s2<s1) return 1; else return 0;
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
