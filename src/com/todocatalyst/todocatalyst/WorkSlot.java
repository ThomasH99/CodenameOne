/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Log;
import com.codename1.io.Util;
import com.codename1.ui.events.DataChangedListener;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.todocatalyst.todocatalyst.Item.CopyMode;
import com.todocatalyst.todocatalyst.Item.EstimateResult;
import static com.todocatalyst.todocatalyst.Item.PARSE_DELETED_DATE;
import static com.todocatalyst.todocatalyst.MyUtil.removeTrailingPrecedingSpacesNewLinesEtc;
import static com.todocatalyst.todocatalyst.RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * a slot has a duration, which is either defined directly, or the difference
 * between a start time and an end time. Start and end time are optional. If
 * both an end time and a duration is set, the end time takes priority
 *
 * @author Thomas
 */
public class WorkSlot extends ParseObject /*extends BaseItem*/
        implements RepeatRuleObjectInterface,
        //SumField, 
        ItemAndListCommonInterface,
        Externalizable, Work //Comparable /* * , IComparable 
{

    public final static String CLASS_NAME = "WorkSlot";

//    public static int MINUTES_IN_MILLISECONDS = MyDate.MINUTE_IN_MILLISECONDS; //60 * 1000;
    final static String WORKSLOT = "Workslot";
    final static String DESCRIPTION = "Description";//"Name";
    final static String DESCRIPTION_HELP = "Optional description of the " + WORKSLOT;//"Name";
    final static String DESCRIPTION_HINT = "Optional description";//"Name";
//    final static String INACTIVE = "Inactive";
    final static String DURATION = "Duration";
    final static String DURATION_HELP = "Define the duration of the " + WORKSLOT;
    final static String START_TIME = "Start by"; //"Start time";
    final static String START_TIME_HELP = "Define when the " + WORKSLOT + " starts"; //"Start time";

    final static String END_TIME = "End by"; //"Start time";
    final static String END_TIME_HELP = "Define when the " + WORKSLOT + " ends"; //"Start time";

    final static String REPEAT_DEFINITION = Item.REPEAT_RULE; //"Repeat";
    final static String REPEAT_DEFINITION_HELP = Item.REPEAT_RULE_HELP; //"Repeat";

    final static String SOURCE = Item.SOURCE; //Template or Task that this one is a copy of, "Task copy of"
    final static String SOURCE_HELP = "Shows the " + WORKSLOT + " was copied from. E.g. for repeating or copy/paste.?"; //Template or Task that this one is a copy of, "Task copy of"

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
//    private final static FieldDef[] FIELDS = {
//        //        new FieldDef(FIELD_DESCRIPTION, "Description", Expr.VALUE_FIELD_TYPE_BOOLEAN),
//        new FieldDef(FIELD_DESCRIPTION, DESCRIPTION, Expr.VALUE_FIELD_TYPE_STRING),
//        new FieldDef(FIELD_INACTIVE, INACTIVE, Expr.VALUE_FIELD_TYPE_BOOLEAN),
//        new FieldDef(FIELD_DURATION, DURATION, Expr.VALUE_FIELD_TYPE_DURATION),
//        new FieldDef(FIELD_START_TIME, START_TIME, Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_REPEAT_DEFINITION, REPEAT_DEFINITION, Expr.VALUE_FIELD_TYPE_STRING), //TODO: what field type to use for RepeatRule?
//    };
    final static String PARSE_OWNER_LIST = "ownerList";
    final static String PARSE_OWNER_CATEGORY = "ownerCategory";
    final static String PARSE_OWNER_ITEM = "ownerItem";

    final static String PARSE_REPEAT_RULE = "repeatRule"; //"repeatRule"

    final static String PARSE_TEXT = "text"; //"description";
//    final static String PARSE_COMMENT = "comment"; //not used
    final static String PARSE_START_TIME = "startTime";
    final static String PARSE_DURATION = "duration";
    final static String PARSE_END_TIME = "endTime"; //always set automatically, based on changes to startTime or duration
    final static String PARSE_ORIGINAL_SOURCE = Item.PARSE_ORIGINAL_SOURCE;
//    final static String PARSE_DELETED_DATE = "deletedDate"; //has this object been deleted on some device?

//    private List<Item> itemsWithSlicesOfThisWorkSlot = new ArrayList(); //unsorted /for now
    private List itemsWithSlicesOfThisWorkSlot = new ArrayList(); //unsorted /for now
    private List<Runnable> opsAfterSubtaskUpdates = new ArrayList(); //operations to run once all changes to Item's fields have been made, e.g. repeatRules

    public WorkSlot() {
        super(CLASS_NAME);
//        super(true); //saveDirectly
//        setTypeId(BaseItemTypes.WORKSLOT);
    }

//    public WorkSlot(String description, Date start, int durationInMinutes, RepeatRuleParseObject myRepeatRule) {//, boolean importedFromPIM) {
    public WorkSlot(String description, Date start, long durationInMillis, RepeatRuleParseObject myRepeatRule) {//, boolean importedFromPIM) {
        this();
        setText(description);
        setStartTime(start);
        setDurationInMillis(durationInMillis);
        setRepeatRule(myRepeatRule);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * constructor for test values
     *
     * @param description
     * @param start
     * @param duration
     * @param myRepeatRule
     */
//    public WorkSlot(String description, int hoursFromNow, int durationInMinutes) {//, boolean importedFromPIM) {
////        this(description, new Date(hoursFromNow * 3600000), durationInMinutes * 60000, null);
////        this(description, new Date(hoursFromNow * 3600000), durationInMinutes * 60000);
//        this(description, new Date(hoursFromNow * MyDate.HOUR_IN_MILISECONDS), durationInMinutes * 60000);
//    }
//    public WorkSlot(String description, Date start, long duration) {
//        this(description, start, duration, null);
//    }
    /**
     * create a copy of workSlot
     *
     * @param workSlot
     */
//    public WorkSlot(WorkSlot workSlot) {
//        this(workSlot.getText(), workSlot.getStartTime(), workSlot.getDurationInMillis(), workSlot.getRepeatRule()); //, workSlot.isImportedFromPIM());
//    }
//</editor-fold>
    /**
     * used to create repeat copies of a WorkSlot, e.g. for Repeat instances
     */
    public WorkSlot(WorkSlot sourceWorkSlot, Date startDate) {
        this(sourceWorkSlot.getText(), startDate, sourceWorkSlot.getDurationInMinutes(), sourceWorkSlot.getRepeatRule());
//        if (sourceWorkSlot.getMyRepeatRule()!=null)
//            this.repeatRule=sourceWorkSlot.getMyRepeatRule();
    }

    public WorkSlot(Date startTime, long duration) {
        this("", startTime, duration, null);
//        if (sourceWorkSlot.getMyRepeatRule()!=null)
//            this.repeatRule=sourceWorkSlot.getMyRepeatRule();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public static FieldDef getFieldDef(int fieldId) {
//        for (int i = 0, size = FIELDS.length; i < size; i++) {
//            if (FIELDS[i].id == fieldId) {
//                return FIELDS[i];
//            }
//        }
//        ASSERT.that("ItemListFlatten.getFieldName(" + fieldId + ") - value not defined");
//        return null;
//    }
//</editor-fold>
//    @Override
    public ItemList getOwnerItemList() {
//        return owner; //TODO: Parsify
//        ParseObject owner = getParseObject(PARSE_OWNER);
        ItemList ownerList = (ItemList) getParseObject(PARSE_OWNER_LIST);
        ownerList = (ItemList) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerList);
        return ownerList;
//        return (ItemList) getParseObject(PARSE_OWNER_LIST);
//        return (status == null) ? ItemStatus.STATUS_CREATED : ItemStatus.valueOf(status); //Created is initial value
    }

//    @Override
    private void setOwnerItemList(ItemList ownerList) {
//        setOwnerDirectly(owner);
//        ItemAndListCommonInterface oldOwner = getOwner();
//        this.owner = owner; //TODO: Parsify
//        return oldOwner;
//        }
//        if (has(PARSE_OWNER_LIST) || ownerList != null) {
//            put(PARSE_OWNER_LIST, ownerList);
//        }
        if (ownerList != null) {
            put(PARSE_OWNER_LIST, ownerList);
        } else {
            remove(PARSE_OWNER_LIST);
        }
//        return ownerList;
    }

    public ItemList getOwnerCategory() {
        Category ownerCategory = (Category) getParseObject(PARSE_OWNER_CATEGORY);
        ownerCategory = (Category) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerCategory);
        return ownerCategory;
//      return (Category) getParseObject(PARSE_OWNER_CATEGORY);
    }

//    @Override
    private void setOwnerCategory(Category category) {
//        if (has(PARSE_OWNER_CATEGORY) || category != null) {
//            put(PARSE_OWNER_CATEGORY, category);
//        }
        if (category != null) {
            put(PARSE_OWNER_CATEGORY, category);
        } else {
            remove(PARSE_OWNER_CATEGORY);
        }
    }

    /**
     * Owner of an item can be either a list or a project (an item in a task's
     * sublist).
     *
     * @return null if no owner
     */
//    @Override
//    final public ItemAndListCommonInterface getOwner() {
//    private ItemAndListCommonInterface getOwnerItem() {
    private Item getOwnerItem() {
//        return owner; //TODO: Parsify
//        ParseObject owner = getParseObject(PARSE_OWNER);
        Item ownerItem = (Item) getParseObject(PARSE_OWNER_ITEM);
        ownerItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerItem);
        return ownerItem;

//        return (Item) getParseObject(PARSE_OWNER_ITEM);
//        return (status == null) ? ItemStatus.STATUS_CREATED : ItemStatus.valueOf(status); //Created is initial value
    }

//    @Override
    private void setOwnerItem(Item ownerItem) {
//        setOwnerDirectly(owner);
//        ItemAndListCommonInterface oldOwner = getOwner();
//        this.owner = owner; //TODO: Parsify
//        return oldOwner;
//        }
//        if (has(PARSE_OWNER_ITEM) || ownerItem != null) {
//            put(PARSE_OWNER_ITEM, ownerItem);
//        }
        if (ownerItem != null) {
            put(PARSE_OWNER_ITEM, ownerItem);
        } else {
            remove(PARSE_OWNER_ITEM);
        }
//        return ownerList;
    }

    @Override
    public void setOwner(ItemAndListCommonInterface owner) {
        //set Owner of new workSlot
        if (owner instanceof Category) {
            setOwnerCategory((Category) owner);
            //TODO if we allow a workSlot to be moved to another type of owner (eg from ItemList to Category) then we must also reset any old owner
            setOwnerItemList(null);
            setOwnerItem(null);
        } else if (owner instanceof ItemList) {
            setOwnerItemList((ItemList) owner);
            setOwnerCategory(null);
            setOwnerItem(null);
        } else if (owner instanceof Item) {
            setOwnerItem((Item) owner);
            setOwnerCategory(null);
            setOwnerItemList(null);
//                    } else { //TODO: add flagging of new/unknown owner for workslots
//                        throw RuntimeException("Unknown type of owner");
        } else if (owner == null) {
            setOwnerCategory(null);
            setOwnerItemList(null);
            setOwnerItem(null);
        } else {
            assert false : "owner should never be set to this type:" + owner;
        }
    }

    @Override
    public ItemAndListCommonInterface getOwner() {
        ItemAndListCommonInterface owner;
        if ((owner = getOwnerCategory()) != null) {
            return owner;
        } else if ((owner = getOwnerItemList()) != null) {
            return owner;
        } else if ((owner = getOwnerItem()) != null) {
            return owner;
        } else {
            return null;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * returns an Object containing the value of the given field (Object, since
//     * this is used in Expr for filtering)
//     *
//     * @param fieldId
//     * @return
//     */
//    public Object getFilterField(int fieldId) {
//        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
//            case FIELD_DESCRIPTION:
//                return getText(); //optimization: replace by variable description directly
////            case FIELD_INACTIVE:
////                return new Boolean(isActive());
//            case FIELD_START_TIME:
//                return getStartTimeD().getTime();
//            case FIELD_DURATION:
//                return new Long(getDurationInMillis());
//            case FIELD_REPEAT_DEFINITION:
//                return getRepeatRule();
//            default:
//                ASSERT.that("Item: Field Identifier not defined " + fieldId);
//        }
//        return null;
//    }
//
//    public void setFilterField(int fieldId, Object fieldValue) {
//        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
//            case FIELD_DESCRIPTION:
//                setText((String) fieldValue);
//                break;
////            case FIELD_INACTIVE:
////                setActive((Boolean) fieldValue);
////                break;
//            case FIELD_START_TIME:
//                setStartTime((Date) fieldValue);
//                break;
//            case FIELD_DURATION:
//                setDurationInMillis((Long) fieldValue);
//                break;
//            case FIELD_REPEAT_DEFINITION:
//                setRepeatRule((RepeatRuleParseObject) fieldValue);
//                break;
//            default:
//                ASSERT.that("Item.setFilterField: Field Identifier not defined " + fieldId);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
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
//    private long consumedDuration;
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
//    private long alarmTime;
//    public boolean isImportedFromPIM() {
//        return importedFromPIM;
//    }
//
//    public final void setImportedFromPIM(boolean importedFromPIM) {
//        this.importedFromPIM = importedFromPIM;
//    }
//</editor-fold>
    /**
     * returns the RepeatRule or null if none is defined.
     *
     * @return
     */
    public RepeatRuleParseObject getRepeatRule() {
//        return repeatRule;
//        ParseObject.fetchFromCacheOnly("RepeatRule", repeatRule.getObjectId());
        RepeatRuleParseObject repeatRule = (RepeatRuleParseObject) getParseObject(PARSE_REPEAT_RULE);
        repeatRule = (RepeatRuleParseObject) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(repeatRule);
//        return (RepeatRuleParseObject) getParseObject(PARSE_REPEAT_RULE);
        return repeatRule;
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
    public final void setRepeatRuleForRepeatInstance(RepeatRuleParseObject repeatRule) {
//        setRepeatRule(repeatRule);
        setRepeatRuleInParse(repeatRule);
    }

    public final void setRepeatRule(RepeatRuleParseObject newRepeatRuleN) {
        RepeatRuleParseObject oldRepeatRule = getRepeatRule();
        if (oldRepeatRule == null) { //setting a RR for the first time
            if (newRepeatRuleN != null) {
//                if (newRepeatRule.isDirty() || newRepeatRule.getObjectIdP() == null) { //                    DAO.getInstance().saveAndWait(newRepeatRule); //must save to get an ObjectId before creating repeat instances (so they can refer to the objId)
//                    DAO.getInstance().saveInBackground(newRepeatRule); //must save to get an ObjectId before creating repeat instances (so they can refer to the objId)
//                }
                setRepeatRuleInParse(newRepeatRuleN); //MUST set repeat rule *before* creating repeat instances in next line to ensure repeatInstance copies point back to the repeatRule
//                newRepeatRule.updateWorkslotsForNewRepeatRule(this);
//                newRepeatRule.updateWorkSlotsWhenRuleCreatedOrEdited(this, true);
//                opsAfterSubtaskUpdates.add(() -> newRepeatRule.updateItemsWhenRuleCreatedOrEdited(this, true)); //will also save RR
                opsAfterSubtaskUpdates.add(() -> newRepeatRuleN.updateWorkSlotsWhenRuleCreatedOrEdited(this, true)); //will also save RR
            } else {
                //setting null when already null - do nothing
            }
        } else { //oldRepeatRule != null
            if (newRepeatRuleN == null || newRepeatRuleN.getRepeatType() == REPEAT_TYPE_NO_REPEAT) { //deleting the existing RR
//                if (oldRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this)) {
//                    setRepeatRuleInParse(null);
//                }
                setRepeatRuleInParse(null);
                opsAfterSubtaskUpdates.add(() -> oldRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this)); //will also save RR

            } else { //possibly modified
                if (!newRepeatRuleN.equals(oldRepeatRule)) { //do nothing if rule is not edited!!
                    oldRepeatRule.updateToValuesInEditedRepeatRule(newRepeatRuleN); //update existing rule with updated values
//                oldRepeatRule.updateWorkSlotsWhenRuleCreatedOrEdited(this); //
//                oldRepeatRule.updateWorkSlotsWhenRuleCreatedOrEdited(this, false); //will also save RR
                    setRepeatRuleInParse(oldRepeatRule);
//                        opsAfterSubtaskUpdates.add(() -> oldRepeatRule.updateItemsWhenRuleCreatedOrEdited(this, false)); //will also save RR
                    opsAfterSubtaskUpdates.add(() -> oldRepeatRule.updateWorkSlotsWhenRuleCreatedOrEdited(this, false)); //will also save RR
                }
            }
        }
    }

//    public final void setRepeatRuleOLD2(RepeatRuleParseObject newRepeatRule) {
//        RepeatRuleParseObject oldRepeatRule = getRepeatRule();
//        if (newRepeatRule != null) {
//            setRepeatRuleInParse(newRepeatRule); //MUST set repeat rule *before* creating repeat instances in next line to ensure repeatInstance copies point back to the repeatRule
//            if (oldRepeatRule == null) {
////                newRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//                newRepeatRule.createWorkslotsForNewRepeatRule(this);
//            } else {
////                newRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//                oldRepeatRule.updateToValuesInEditedRepeatRule(newRepeatRule); //update existing rule with updated values
//                oldRepeatRule.updateWorkslotsForEditedRepeatRule(this); //
//            }
//        } else if (oldRepeatRule != null) { //if the user deleted the repeatRule, /* repeatRule == null && */ 
//            if (oldRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this))
//                //            setRepeatRuleNoUpdate(newRepeatRule);
//                setRepeatRuleInParse(null);
//        } //else both old and new RR are null, do nothing
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public final void setRepeatRuleOLD(RepeatRuleParseObject repeatRule) {
//        //TODO!!!!! copy code from Item.setRepeatRule()
////        if (this.repeatRule != repeatRule) {
////            this.repeatRule = repeatRule;
////            changed();
////        }
////        if (true) {
////        if (this.repeatRule != repeatRule) {
////            this.repeatRule = repeatRule; //TODO!!!!: shouldn't repeat instances be calculated/updated whenever a repeat rule sit set?! (Currently done in copyMeInto)
//////            changed();
////        }
////        ParseObject.fetchFromCacheOnly("RepeatRule", repeatRule.getObjectId());
//        setRepeatRuleInParse(repeatRule); //MUST set repeatRule before creating repeatInstances!
//        if (repeatRule != null) {
////            repeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//            boolean newRepeatRule = getRepeatRule() == null;
//            setRepeatRuleInParse(repeatRule); //MUST set repeat rule *before* creating repeat instances in next line
////                repeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//            if (newRepeatRule) {
////                repeatRule.updateRepeatInstancesWhenRuleWasCreated(this);
//                repeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//            } else {
////                repeatRule.updateRepeatInstancesWhenRuleWasEdited(this);
//                repeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//            }
//        } else if (getRepeatRule() != null) { //if the user deleted the repeatRule,
//            getRepeatRule().deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this);
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (repeatRule != null) {
////            put(PARSE_REPEAT_RULE, repeatRule);
////        } else {
////            remove(PARSE_REPEAT_RULE);
////        }
////        setRepeatRuleNoUpdate(repeatRule);
////        } else {
////            if (this.repeatRule != null && repeatRule != null && this.repeatRule != repeatRule) {
////                //TODO!!!!: normally the user cannot change from one repeatRule to another (but we could programatically) - ensure item is removed from old rule
////                this.repeatRule.deleteRuleAndAllRepeatInstancesExceptThis(this);
////            }
////            if ((repeatRule != this.repeatRule)) { //if it's a new repeat rule then replace the old one
////                this.repeatRule = repeatRule; //TODO!!!!: shouldn't repeat instances be calculated/updated whenever a repeat rule sit set?! (Currently done in copyMeInto)
////            }
////            if (this.repeatRule != null && this.repeatRule.isDirty()) { //only call changed (on Item) if the repeat rule has actually been edited
////                changed();
////            }
////        }
////</editor-fold>
//    }
//</editor-fold>
    public final void setRepeatRuleInParse(RepeatRuleParseObject repeatRule) {
        if (repeatRule != null) {
            put(PARSE_REPEAT_RULE, repeatRule);
        } else {
            remove(PARSE_REPEAT_RULE);
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
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
    //        ASSERT.that(getDurationInMillis() <= slotA.getDurationInMillis() + slotB.getDurationInMillis(), "Slot A [" + slotA + "] and Slot B [" + slotB + "] are disjoint (not overlapping)");
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
//</editor-fold>
    @Override
    public Date getRepeatStartTime(boolean fromCompletedDate) {
        return getStartTimeD();
    }

    @Override
    public void setRepeatStartTime(Date repeatStartTime) {
//        setStartTime(new Date(repeatStartTime));
        setStartTime(repeatStartTime);
    }

    public WorkSlot getSource() {
        WorkSlot source = (WorkSlot) getParseObject(PARSE_ORIGINAL_SOURCE);
//        return (Item) getParseObject(PARSE_ORIGINAL_SOURCE);
        return (WorkSlot) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(source);
    }

    /**
     * store a task that this task depends on. The task this one depends on must
     * be Done before isDependingOnTasksDone returns true. This enables
     * filtering on (hiding) all tasks that are depending on other tasks until
     * they are done. In a first time, only dependency on a single task is
     * supported.
     *
     * @param originalWorkSlotThisOneIsACopyOf
     */
//    public void setTaskInterrupted(Item taskInterruptedByThisInterruptTask) {
    public void setSource(WorkSlot originalWorkSlotThisOneIsACopyOf) {
        if (originalWorkSlotThisOneIsACopyOf != null) {
            put(PARSE_ORIGINAL_SOURCE, originalWorkSlotThisOneIsACopyOf);
        } else {
            remove(PARSE_ORIGINAL_SOURCE);
        }
    }

    @Override
    public RepeatRuleObjectInterface createRepeatCopy(Date referenceTime) {
//        return new WorkSlot(this, referenceTime);
        WorkSlot newCopy = cloneMe(CopyMode.COPY_TO_REPEAT_INSTANCE);
        newCopy.setStartTime(referenceTime);
        return newCopy;
    }

    @Override
    public WorkSlot cloneMe(CopyMode copyFieldDefinition) {
        WorkSlot newCopy = new WorkSlot();
        copyMeInto(newCopy, copyFieldDefinition);
        return newCopy;
    }

    /**
     * deep copy
     *
     * @param destination
     * @param copyFieldDefintion defines type of type
     * @param copyExclusions set to COPY_EXCLUDE_COMMENT | COPY_EXCLUDE_PRIORITY
     * to exlude fields from copy. Applied recursively
     */
    void copyMeInto(WorkSlot destination) {
        copyMeInto(destination, CopyMode.COPY_ALL_FIELDS);
    }

    void copyMeInto(WorkSlot destination, CopyMode copyFieldDefinition) {
        destination.setStartTime(getStartTimeD());
        destination.setDurationInMinutes(getDurationInMinutes());
        destination.setText(getText());
        destination.setOwner(getOwner()); //need to set owner here, since not done eg when creating repeat copies
        //Contrary to Item, always use the original originator as source:
        if (getSource() != null) {
            destination.setSource(getSource()); //link to the very first originator/source if available
        } else {
            destination.setSource(this); //otherwise (this is first copy) use this
        }
        if (copyFieldDefinition == CopyMode.COPY_TO_REPEAT_INSTANCE) {
            RepeatRuleParseObject repeatRule = getRepeatRule();
            boolean notSaved = false;
            if (Config.TEST) {
                notSaved = repeatRule.getObjectIdP() == null || repeatRule.getObjectIdP().isEmpty(); //repeatRule.isDirty() ||
            }
            destination.setRepeatRuleInParse(getRepeatRule());
        } else {
            destination.setRepeatRule(getRepeatRule());
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void externalizeXXX(DataOutputStream dos) throws IOException {
////        try {
////            if (sourceSlots==null) { //only store sourceSlots
////            if (true || !importedFromPIM || (Settings.getInstance().storePastCalendarItemWorkSlots() && getEnd() < MyDate.getNow())) {
////            super.writeObject(dos);
////        dos.writeUTF(description);
////        BaseItemDAO.getInstance().writeString(description, dos);
////        BaseItemDAO.getInstance().writeObject(description, dos);
//        MyUtil.writeObject(getText(), dos);
//        dos.writeLong(getStartTimeD().getTime());
//
//        dos.writeLong(getDurationInMillis());
////        dos.writeLong(consumedDuration);
////        dos.writeBoolean(negativeSlot);
////        dos.writeBoolean(isNegativeSlot());
//
////        dos.writeBoolean(inactive);
////        dos.writeBoolean(isActive());
////        BaseItem.writeGuidInline(dos, repeatRule);
////        BaseItemDAO.getInstance().writeObject(repeatRule, dos); //, true);
//        MyUtil.writeObject(getRepeatRule(), dos); //, true);
////            dos.writeInt(sourceSlotA != null ? sourceSlotA.getGuid() : 0);
////            dos.writeInt(sourceSlotB != null ? sourceSlotB.getGuid() : 0);
////            }
////        } catch (IOException ex) {
////            ex.printStackTrace();
////        }
//    }
//
//    public void internalizeXXX(int version, DataInputStream dis) throws IOException {
////        try {
////            super.readBaseItem(dis);
////        description = dis.readUTF();
////        description = BaseItemDAO.getInstance().readString(dis);
////        description = (String) BaseItemDAO.getInstance().readObject(dis);
////        description = (String) MyUtil.readUTF(dis);
//        setText((String) MyUtil.readUTF(dis));
////        startTime = dis.readLong();
//        setStartTime(new Date(dis.readLong()));
//
////        duration = dis.readLong();
//        setDurationInMillis(dis.readLong());
////        consumedDuration = dis.readLong();
////        negativeSlot = dis.readBoolean();
////        setNegativeSlot(dis.readBoolean());
//
////        inactive = dis.readBoolean();
////        setActive(dis.readBoolean());
////        repeatRule = (MyRepeatRule) BaseItem.readGuidInline(dis, BaseItemTypes.MYREPEATRULE);
////        repeatRule = (MyRepeatRule) BaseItemDAO.getInstance().readObject(dis);
////        repeatRule = (MyRepeatRule) MyUtil.readObject(dis);
//        setRepeatRule((RepeatRuleParseObject) MyUtil.readObject(dis));
//</editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
////            int guid;
////            guid = dis.readInt();
////            if ((guid = dis.readInt()) != 0) {
////                sourceSlotA = (WorkSlot) BaseItemDAO.getInstance().getBaseItem(guid);
////            }
////            if ((guid = dis.readInt()) != 0) {
////                sourceSlotB = (WorkSlot) BaseItemDAO.getInstance().getBaseItem(guid);
////            }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
////        } catch (IOException ex) {
////            ex.printStackTrace();
////        }
//    }
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
//    public String getShowTextXXX() {
//        if (getStartTimeD().getTime() == 0) {
//            return (getText().equals("") ? "<no text>" : " \"" + getText() + " \"");
//        } else {
////            return (getText().equals("") ? "" : " \"" + getText() + " \"" + " ") + new MyDate(getStart()).formatDate(false);
////            return (new MyDate(getStartTime()).formatDateNatural(MyDate.FORMAT_DDMM_HHMM)
//            return (new MyDate(getStartTimeD()).formatDateNew(MyDate.FORMAT_DDMM_HHMM)
//                    //                    + "-" + new MyDate(getEndTime()).formatDateNatural(MyDate.FORMAT_DDMM_HHMM)
//                    + "-" + new MyDate(getEndTime()).formatDateNew(MyDate.FORMAT_DDMM_HHMM)
//                    + (getText().equals("") ? "" : " \"" + getText() + " \"" + " "));
//        }
//    }
//</editor-fold>
    public void setText(String text) {
//        if (this.description == null || !this.description.equals(text)) {
//            this.description = text;
////            changed();
//        }
        if (MyPrefs.itemRemoveTrailingPrecedingSpacesAndNewlines.getBoolean()) {
            text = removeTrailingPrecedingSpacesNewLinesEtc(text);
        }
        EstimateResult estim = Item.getEffortEstimateFromTaskText(text);
        if (estim != null) {
            text = estim.cleaned;
            if (estim.minutes != 0) {
                setDurationInMillis(estim.minutes * MyDate.MINUTE_IN_MILLISECONDS);
            }
        }
//        if ((has(PARSE_TEXT) || !text.equals(""))) { //don't test for val != null to avoid silent failure on this error condition
//            put(PARSE_TEXT, text);
//        }
        if (text != null && text.length() != 0) { //don't test for val != null to avoid silent failure on this error condition
            put(PARSE_TEXT, text);
        } else {
            remove(PARSE_TEXT);
        }
    }

    @Override
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
        String s = getString(PARSE_TEXT);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    @Override
    public String toString() {
//        return "SLOT[" + getText() + "|Start=" + new MyDate(getStartTime()).formatDate(false) + "|End=" + new MyDate(getEnd()).formatDate(false) + "|Duration=" + Duration.formatDuration(getDurationInMillis()) + "]";
        return (getRepeatRule() != null ? "*" : "") + "WS:" + MyDate.formatDateTimeNew(getStartTimeD()) + " D:" + MyDate.formatDurationShort(getDurationInMinutes() * MyDate.MINUTE_IN_MILLISECONDS, true)
                //                + " " + getText() + "[" + getObjectIdP() + "]" + (getOwner() != null ? " Owner:" + getOwner().getText() : "") + " [" + getObjectIdP() + "]";
                + (getObjectIdP() == null ? " [NoObjId]" : (" [" + getObjectIdP() + "]"))
                + (" Owner:" + (getOwner() != null ? (getOwner().getText() + "/" + getOwner().getObjectIdP()) : "None"));
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public String shortString() {
//        return "SLOT(" + getText() + ";S=" + new MyDate(getStartTime()).formatDate(true) + ";E=" + new MyDate(getEndTime()).formatDate(true) + ";D=" + Duration.formatDuration(getDurationInMillis()) + ")";
//    }
//    @Override
//    public long getSumField(int fieldId) {
//        return getSumField();
//    }
//
//    @Override
//    public long getSumField() {
//        return getDurationAdjusted();
//    }
//
//    @Override
//    public boolean ignoreSumField() {
////        return !isActive();
//        return isNoLongerRelevant();
//    }
//</editor-fold>
    @Override
    public Date getStartTimeD() {
//        return start;
        //ensures that start is updated to a later value if duration is reduced as the workSlot is eaten up by tasks
        //(otherwise as you complete tasks, the next incomplete task will always start at the original start time).
        //if duration is not updated, then initialDuration-duration==0 meaning original start is always used
//        return start + (initialDuration - duration);
        Date startTime = getDate(PARSE_START_TIME);
        return (startTime == null) ? new Date(0) : startTime; //TODO!!! is 'Date(0)' best undefined time?! Rather use MyDate.MAXDATE?!

//        return startTime;
    }

//    public long getStartTime() {
//        return getStartTimeD().getTime();
//    }
    /**
     * adjusts start in case the slot covers the current time, or in case some
     * of the duration has already been consumed (this is necessary to make it
     * possible to reduce a time slot as it is consumed by individual tasks,
     * otherwise each time a task is completed, the next will start at start
     * time of the slot). If startDate is in the past, return NOW
     * (MyDate.getNow())
     */
//    public long getStartAdjustedXXX() {
////        return getStartAdjusted(new Date().getTime());
//        return getStartAdjusted(MyDate.currentTimeMillis());
//    }
    public long getStartAdjusted(long now) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        long start = getStart();
//        long now = MyDate.getNow();
// <editor-fold defaultstate="collapsed" desc="comment">
//        if (start < now) {
//            return now;
//        } else {
//            return Math.max(getStart() + (initialDuration - duration), now); //"start + (initialDuration - duration)" add the difference btw initialDuration and current duration (=time consumed) to original start, return the latest time of this new start, and NOW// </editor-fold>
//        return Math.max((getStartTime() + consumedDuration), now); //"start + (initialDuration - duration)" add the difference btw initialDuration and current duration (=time consumed) to original start, return the latest time of this new start, and NOW
//long startTime =getStartTimeD().getTime();
//if (startTime<now  ) {
//    if ()
//}
//</editor-fold>
        return Math.max((getStartTimeD().getTime()), now); //"start + (initialDuration - duration)" add the difference btw initialDuration and current duration (=time consumed) to original start, return the latest time of this new start, and NOW
//        }
    }

    public final void setStartTime(Date start) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (this.startTime != start) {
//            this.startTime = start;
////            changed();
//        }
//        if (has(PARSE_START_TIME) || (start != null && start.getTime() != 0)) {
//            put(PARSE_START_TIME, start);
//            updateEndTimeWithNewStartTime(start);
//        }
//</editor-fold>
        if ((start != null && start.getTime() != 0)) {
            put(PARSE_START_TIME, start);
            setEndTime(new Date(start.getTime() + getDurationInMillis()));
//            updateEndTimeWithNewStartTime(start);
        } else {
            remove(PARSE_START_TIME);
            setEndTime(null);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        updateEndTimeWithNewStartTime(start == null ? new Date(0) : start);
//        Date testNewEndDateTmp = (start == null) ? (new Date(0)) : (new Date(start.getTime() + getDurationInMillis()));
//        Date testNewEndDateTmp2 = new Date(start == null ? 0 : start.getTime() + getDurationInMillis());
//        Date testNewEndDate;
//        if (start == null) {
//            testNewEndDate = new Date(0);
//        } else {
//            testNewEndDate = new Date(start.getTime() + getDurationInMillis());
//        }
//
////        setEndTime((start == null) ? (new Date(0)) : (new Date(start.getTime() + getDurationInMillis()))); //!!!Java compiler error?? When date is a valid date, this somehow gives a 0 date as parameter to setEndTime()!!!
//        setEndTime(testNewEndDate);
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private final void updateEndTimeWithNewDuration(long newDuration) {
////        put(PARSE_END_TIME, new Date(getStartTimeD().getTime() + newDuration));
//        setEndTime(new Date(getStartTimeD().getTime() + newDuration));
//    }
//
//    private final void updateEndTimeWithNewStartTime(Date newStartTime) {
////        put(PARSE_END_TIME, new Date(newStartTime.getTime() + getDurationInMillis()));
//        setEndTime(new Date(newStartTime.getTime() + getDurationInMillis()));
//    }
//</editor-fold>
    /**
     * even though redundant (can be calculated from startTime and duration) we
     * need to store endTime explicitly to allow Parse searches on its value
     *
     * @param endTime
     */
    private final void setEndTime(Date endTime) {
        if ((endTime != null && endTime.getTime() != 0)) {
            put(PARSE_END_TIME, endTime);
        } else {
            remove(PARSE_END_TIME);
        }
    }

    /**
     * returns the time when the workSlot ends (
     *
     * @return
     */
    @Override
    public long getEndTime() {
//        return end!=0L?end:start+duration;
//        return start + duration;
//        return getStartTimeD().getTime() + getDurationInMillis(); //unchanged, even as start is in the past, or consumedDuration>0
        return getEndTimeD().getTime();
    }

    @Override
    public Date getEndTimeD() {
//        return end!=0L?end:start+duration;
//        return start + duration;
//        return new Date(getStartTimeD().getTime() + getDurationInMillis()); //unchanged, even as start is in the past, or consumedDuration>0
        Date time = getDate(PARSE_END_TIME);
        return (time == null) ? new Date(0) : time; //TODO!!! is 'Date(0)' best undefined time?! Rather use MyDate.MAXDATE?!

    }

    /**
     * does't make sense because time is in minutes, so setting a specific
     * endTime will be very imprecise!!
     *
     * @param endTimeInMillis
     */
    public void setEndTime(long endTimeInMillis) {
        setDurationInMillis(Math.max(0, endTimeInMillis - getStartTimeD().getTime())); //max=> avoid negative duration
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * if the workslot has expired (end time is in the past) check if there is a
     * repeatRule, return new repeat instance or null if no more repeat
     * instances. If not expired, return the workslot itself.
     *
     * @return
     */
//    public WorkSlot checkIfExpiredAndReturnNewRepeatInstance(long now) {
//        //If workslot fully in the past, but still in list of active instances, then it has expired since last loading
//        if (getEndTime() < now) {
//            RepeatRuleParseObject repeatRule = getRepeatRule();
//            if (getRepeatRule() != null) { //if repeating workslot
//                //TODO!!!! continue generating new instances until we get enough future ones as defined by repeatRule
//                if (repeatRule.isRepeatInstanceInListOfActiveInstances(this)) {
//                    WorkSlot newWorkSlot;
//                    do {
//                        newWorkSlot = (WorkSlot) repeatRule.updateRepeatInstancesOnDoneCancelOrDelete(this);
//                    } while (newWorkSlot != null && newWorkSlot.getEndTime() < now);
////                    workSlot = (WorkSlot) newWorkSlot;
//                    return (WorkSlot) newWorkSlot;
//                }
//            }
//            return null;
//        } else {
//            return this;
//        }
//    }
//</editor-fold>
    /**
     * returns defined duration in milli seconds of work slot (as defined by
     * user, unadjusted in case part of the slot is in the past)
     *
     * @return
     */
    public long getDurationInMillis() {
// <editor-fold defaultstate="collapsed" desc="comment">
//        if (start != 0L && end != 0L) {
//            return (int) (end - start);
//        } else {
//            return duration;
//        }
//        return inactive ? 0 : duration;
//        if (has(PARSE_DURATION)) {
////            return getLong(PARSE_DURATION)*MINUTES_IN_MILLISECONDS; //only store the time as minutues (more readable in parse)
//            return getLong(PARSE_DURATION) * MINUTES_IN_MILLISECONDS; //only store the time as minutues (more readable in parse)
//        } else {
//            return 0;
//        }
//        Long duration= getLong(PARSE_DURATION) ; //only store the time as minutues (more readable in parse)
//        if (duration!=null) {
//            return duration*MINUTES_IN_MILLISECONDS; //only store the time as minutues (more readable in parse)
//        } else {
//            return 0;
//        }
// </editor-fold>
        return ((long) getDurationInMinutes()) * MyDate.MINUTE_IN_MILLISECONDS;
    }

    public int getDurationInMinutes() {
//        return getDurationInMillis() / MINUTES_IN_MILLISECONDS;
        Integer duration = getInt(PARSE_DURATION); //only store the time as minutues (more readable in parse)
        if (duration != null) {
            return duration; //only store the time as minutues (more readable in parse)
        } else {
            return 0;
        }
    }

    /**
     * returns actually available duration of work slot. The available time is
     * calculated as duration value minus whatever is biggest of:
     * consumedDuration or what is left of the slot if the start time is already
     * passed. If the slot is marked inactive, it always returns 0.
     *
     * @return
     */
//    public long getDurationAdjustedXXX() {
////        return getDurationAdjusted(new Date().getTime());
//        return getDurationAdjusted(MyDate.currentTimeMillis());
//    }
//    public long getDurationAdjustedInMinutesXXX() {
////        return getDurationAdjusted(new Date().getTime()) / MyDate.MINUTE_IN_MILLISECONDS;
//        return getDurationAdjusted(MyDate.currentTimeMillis()) / MyDate.MINUTE_IN_MILLISECONDS;
//    }
//    public long getDurationAdjustedInMinutesXXX(long now) {
//        return getDurationAdjusted(now) / MyDate.MINUTE_IN_MILLISECONDS;
//    }
    public long getDurationAdjusted(long now) {
// <editor-fold defaultstate="collapsed" desc="comment">
//        long now = MyDate.getNow();
//        return inactive ? 0 : (int)(duration - (getStartAdjusted()-start));
//        return inactive ? 0 : (int) (initialDuration - (getStartAdjusted() - start));
//        return inactive ? 0 : Math.max((int) (duration-consumedDuration) - duration(getStartAdjusted() - start));
//                inactive //always inactive if inactive is set
//                || (!inactive && start!=0 && getEnd()<=MyDate.getNow()) //inactive even if !inactive, iff it is dated and end date is in the past
//                || (!inactive && consumedDuration>=duration) //inactive if time has been consumed// </editor-fold>
        long startTime = getStartTimeD().getTime();
//        long now = new Date().getTime();
        long durationInMillis = getDurationInMillis();
        if (startTime + durationInMillis < now) {
            return 0; //workslot is entirely in the past
        } else if (now > startTime) { //part of workslot is in the past
            return durationInMillis - (now - startTime); //workslot starttime is already passed so deduct the already passed part of the workslot
        } else { //workslot in the future
            return durationInMillis;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        return inactive ? 0
//        return !isActive() ? 0
////                : startTime == 0 ? getDurationInMillis() - consumedDuration
//                : startTime == 0 ? getDurationInMillis()
//                        : //for undated slots, workSlot can only be reduced by consuming it, not by the passing of time
////                        getDurationInMillis() - Math.max(consumedDuration, //0 unless set
//                        getDurationInMillis() - ( //0 unless set
//                                Settings.getInstance().adjustWorkSlotsWhenStartIsInThePast()
//                                        ? //only
//                                        ((int) Math.min(getDurationInMillis(), //with min() ensures that can never get bigger than duration
//                                                Math.max(0, (MyDate.getNow() - startTime)))) : 0); //now-start==what is eaten out of duration if start is in the past; min(duration,*) to avoid that now-start can get bigger than int
//</editor-fold>
    }

    /**
     * returns the duration that falls in the interval between fromTime and
     * toTime. Return 0 if the duration falls outside this interval.
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    public long getDurationAdjusted(long fromTime, long toTime) {
        if (Config.TEST) {
            Date actualEnd = new Date(
                    Math.min(getStartTimeD().getTime() + getDurationInMillis(), toTime)
            ); //get the earliest endDate
            Date actualStart = new Date(
                    Math.max(getStartTimeD().getTime(), fromTime)
            ); //get the latest startDate

            long duration = actualEnd.getTime() - actualStart.getTime(); //get the duration (may be negative)
            long finalDuration = Math.max(0, duration);
        }
        return Math.max(0,
                Math.min(getStartTimeD().getTime() + getDurationInMillis(), toTime)
                - Math.max(getStartTimeD().getTime(), fromTime)
        );
    }

    /**
     * returns true if the workSlot has some work time that falls within the
     * interval between fromTime and endTime
     *
     * @param fromTime
     * @param endTime
     * @return
     */
    public boolean hasDurationInInterval(long fromTime, long endTime) {
        return getDurationAdjusted(fromTime, endTime) > 0;
    }

    public final void setDurationInMillis(long durationInMilliSeconds) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (this.duration != duration) {
//            this.duration = duration;
////            changed();
//        }
//        if (has(PARSE_DURATION) || durationInMilliSeconds != 0) {
//            put(PARSE_DURATION, durationInMilliSeconds / MINUTES_IN_MILLISECONDS); //store duration in minutes for readability
//            updateEndTimeWithNewDuration(durationInMilliSeconds);
//        }
//        if (durationInMilliSeconds != 0) {
//            put(PARSE_DURATION, durationInMilliSeconds / MINUTES_IN_MILLISECONDS); //store duration in minutes for readability
//            updateEndTimeWithNewDuration(durationInMilliSeconds);
//        }
//</editor-fold>
        setDurationInMinutes((int) (durationInMilliSeconds / MyDate.MINUTE_IN_MILLISECONDS));
    }

    public final void setDurationInMinutes(int durationInMinutes) {
//        setDurationInMillis(durationInMinutes * MINUTES_IN_MILLISECONDS);
        if (durationInMinutes != 0) {
            put(PARSE_DURATION, durationInMinutes); //store duration in minutes for readability
//            updateEndTimeWithNewDuration(((long)durationInMinutes) * MyDate.MINUTE_IN_MILLISECONDS);
        } else {
            remove(PARSE_DURATION);
        }

        Date startDate = getStartTimeD();
        if (startDate.getTime() != 0) {
            setEndTime(new Date(getStartTimeD().getTime() + getDurationInMillis()));
        } else {
            setEndTime(null);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public final void setConsumedDuration(long consumedDuration) {
//        if (this.consumedDuration != consumedDuration) {
//            this.consumedDuration = consumedDuration;
////            changed();
//        }
//    }
//
//    /**
//     * returns consumed duration of work slot
//     *
//     * @return
//     */
//    public long getConsumedDuration() {
//        return consumedDuration;
//    }
    /**
     * true if this work slot is active (available for tasks) otherwise false
     */
//    public final void setActive(boolean active) {
////        if (this.inactive == active) { //NB! ==, not !=
////            inactive = !active;
//////            changed();
////        }
//        if (has("active") || active == true) {
//            put("active", active);
//        }
//    }
//
//    public final boolean isActive() {
//        if (has("active")) {
//            return getBoolean("active");
//        } else {
//            return false;
//        }
////        return !inactive;
//    }
    /**
     * true if this work slot is active (has available work time for tasks)
     * otherwise false. A slot can become inactive either by being marked
     * inactive, or if its end time is in the past (automatically
     */
//    public final boolean isActiveAdjusted() {
////        inactive = getEnd()<=MyDate.getNow(); //automatically mark work slot permanently inactive //- not more efficient than below OR statement
//        return getDurationAdjusted() > 0; //ensures result is consistent with getDurationAdjusted
//</editor-fold>
//// <editor-fold defaultstate="collapsed" desc="comment">
////        return !( //easier to define when it's inactive and then negate:
////                //inactive if:
////                inactive //always inactive if inactive is set
////                || (!inactive && start!=0 && getEnd()<=MyDate.getNow()) //inactive even if !inactive, iff it is dated and end date is in the past
////                || (!inactive && consumedDuration>=duration) //inactive if time has been consumed
////                );
////        return !inactive;// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    }
//    public boolean isNegativeSlot() {
//        if (has("negativeSlot")) {
//            return getBoolean("negativeSlot");
//        } else {
//            return false;
//        }
////        return negativeSlot;
//    }
//
//    public void setNegativeSlot(boolean negativeSlot) {
//        if (has("negativeSlot") || negativeSlot == true) {
//            put("negativeSlot", negativeSlot);
//        }
////        if (this.negativeSlot != negativeSlot) {
////            this.negativeSlot = negativeSlot;
//////            changed();
////        }
//    }
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
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
    public void setSoftDeletedDate(Date dateDeleted) {
        if (dateDeleted != null && dateDeleted.getTime() != 0) {
            put(Item.PARSE_DELETED_DATE, dateDeleted);
        } else {
            remove(Item.PARSE_DELETED_DATE); //delete when setting to default value
        }
    }

    @Override
    public Date getSoftDeletedDateN() {
        Date date = getDate(Item.PARSE_DELETED_DATE);
//        return (date == null) ? new Date(0) : date;
        return date;
    }

//    @Override
//    public void deleteXX() throws ParseException {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates. 
//    }
//    public void deleteXXX() throws ParseException {
//        RepeatRuleParseObject myRepeatRule = getRepeatRule();
//        if (myRepeatRule != null) {
////            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this, (ItemList) getOwner());
//            myRepeatRule.deleteThisRepeatInstanceFromRepeatRuleListOfInstances(this);
//            //TODO!!! if no more workSlots referenced fromrepeatrule, then also delete repeatrule
//            DAO.getInstance().saveInBackground(myRepeatRule);
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        try {
////            //        ((WorkSlotList) getOwner()).removeItem(this);
//////        ((ItemList) getOwner()).removeItem(this);
////            super.delete();
////        } catch (ParseException ex) {
////            Log.e(ex);
////        }
////        DAO.getInstance().delete(this);
////        super.delete();
////</editor-fold>
//        //remove from owner (but don't remove owner from this workSlot, that allows us to recreate/undelete
//        ItemAndListCommonInterface owner = getOwner();
//        if (owner != null) {
//            WorkSlotList workSlotList = owner.getWorkSlotListN();
//            if (workSlotList != null) {
//                workSlotList.remove(this);
//                owner.setWorkSlotList(workSlotList);
//                DAO.getInstance().saveInBackground((ParseObject) owner);
//            }
////            owner.removeFromList(this); //TODO implement removeFromList on WorkSlotList?
//            DAO.getInstance().saveInBackground(this);
//        }
//
////        put(Item.PARSE_DELETED_DATE, new Date());
//        setDeletedDate(new MyDate());
//        DAO.getInstance().saveInBackground(this);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public ItemList getListForNewCreatedRepeatInstances() {
//        return (ItemList) getOwner(); //should always be a WorkSlotList which is an ItemList
//    }
//    @Override
//    public void deleteRepeatInstance() {
//        DAO.getInstance().delete(this);
//    }
//</editor-fold>
//    @Override
//    public boolean isNoLongerRelevant() {
////        return !isActiveAdjusted();
//        return getDurationAdjusted() == 0;
//    }
// <editor-fold defaultstate="collapsed" desc="comment">
//    private int compareLong(long d1, long d2) {
//        if (d1 < d2) {
//            return -1;
//        } else if (d2 < d1) {
//            return 1;
//        }
//        return 0;
//    }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
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
//    public String getComment() {
//        String s = getString(PARSE_COMMENT);
//        if (s == null) {
//            return "";
//        } else {
//            return s;
//        }
//    }
//
//    public void setComment(String val) {
////        if (val != null && (has("comment") || !val.equals(""))) {
//        if ((has(PARSE_COMMENT) || !val.equals(""))) { //don't test for val != null to avoid silent failure on this error condition
//            put(PARSE_COMMENT, val);
//        }
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

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List getInsertNewRepeatInstancesIntoListXXX() {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////        return getOwner();
//        return null;
//    }
//
//    public void saveInsertListXXX() {
////        Object owner = getOwner();
////        if (owner instanceof ItemList) {
////            DAO.getInstance().save((ItemList) owner);
////        } else if (owner instanceof Item) {
////            DAO.getInstance().save((Item) owner);
////        }
//    }
//</editor-fold>
    @Override
//    public void insertIntoListAndSaveListAndInstance(RepeatRuleObjectInterface orgInstance, RepeatRuleObjectInterface repeatRuleObject) {
    public ItemAndListCommonInterface insertIntoList(RepeatRuleObjectInterface workSlotRepeatCopy) {
        ItemAndListCommonInterface owner = getOwner();
        owner.addWorkSlot((WorkSlot) workSlotRepeatCopy);
//        DAO.getInstance().saveInBackground((ParseObject) workSlotRepeatCopy);
        return owner;
    }

//    @Override
    public boolean equalsXXX(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof WorkSlot)) {
            return false;
        }
//        return ((Item) obj).getObjectId().equals(getObjectId());
        if (getObjectIdP() != null && ((WorkSlot) obj).getObjectIdP() != null) {
            //compare isDirty in case we have two instances of the same 
//            return getObjectId().equals(((Item) obj).getObjectId()) && isDirty()==((Item) obj).isDirty();
            ASSERT.that(isDirty() != ((WorkSlot) obj).isDirty(), "comparing dirty and not dirty instance of same object=" + this);
            boolean sameObjId = getObjectIdP().equals(((WorkSlot) obj).getObjectIdP());
            ASSERT.that(!sameObjId, "equals() comparing two different instances with SAME objectId, this=" + this + ", obj=" + obj);
            return sameObjId; //getObjectIdP().equals(((WorkSlot) obj).getObjectIdP());
        }
        return true; //this == (WorkSlot) obj;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public static void sortWorkSlotList(List<WorkSlot> workSlots) {
//        Collections.sort(workSlots, (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
//    }
//    public static long sumWorkSlotList(List<WorkSlot> workSlots) {
//        return sumWorkSlotList(workSlots, System.currentTimeMillis());
//    }
//
//    public static long sumWorkSlotList(List<WorkSlot> workSlots, long now) {
//        long sum = 0;
//        for (WorkSlot workSlot : workSlots) {
//            sum += workSlot.getDurationAdjusted(now);
//        }
//        return sum;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static WorkSlot checkIfWorkSlotOverlapsWithOtherWorkSlots(WorkSlot newWorkSlot, List<WorkSlot> workSlots, boolean needsSorting) {
//    public static WorkSlot checkIfWorkSlotOverlapsWithOtherWorkSlotsXXX(WorkSlot newWorkSlot, WorkSlotList workSlots, boolean needsSorting) {
//        if (needsSorting) {
////            sortWorkSlotList(workSlots);
//            workSlots.sortWorkSlotList();
//        }
//        for (int i = 0, size = workSlots.size(); i < size; i++) {
//            WorkSlot workSlot = workSlots.get(i);
//            long startTime = newWorkSlot.getStartTimeD().getTime();
//            long endTime = newWorkSlot.getEndTime();
//            if ((startTime >= workSlot.getStartTimeD().getTime() && startTime <= workSlot.getEndTime()) || (endTime >= workSlot.getStartTimeD().getTime() && endTime <= workSlot.getEndTime())) {
//                return workSlot;
//            }
//        }
//        return null;
//    }
//</editor-fold>
    @Override
    public void updateRepeatInstanceRelativeDates(Date newDueDateTime) {
        setStartTime(newDueDateTime);
    }

    public boolean hasSaveableData() {
        return true || (getStartTimeD().getTime() != 0 || getDurationInMinutes() != 0); //TODO! should also check for new/changed repeat rule
    }

    /**
     * returns the first tasks in the list or project that can fit into this
     * workslot. Used in Today view to show today's workslots and which tasks
     * fit into them.
     *
     * @return
     */
    public List<Item> getItemsInWorkSlot() {
        return getItemsInWorkSlot(false);
    }

    /**
     * return the top-level tasks that this workslot has allocated time to (not
     * lower-level subtasks that the top-level tasks has subsequently allocated
     * their time to)
     *
     * @param includeDoneTasks
     * @return
     */
    public List<Item> getItemsInWorkSlot(boolean includeDoneTasks) {
        ItemAndListCommonInterface owner = getOwner();
        List<Item> items = new ArrayList<>();
        if (owner != null) {
            for (Object i : owner.getListFull()) { //go through everyone of the WorkSLot's Owner's tasks
                if (i instanceof Item) {
                    Item item = (Item) i;
//                    WorkTimeSlices wTime = item.getAllocatedWorkTimeN();
                    WorkTimeSlices wTime = owner.getAllocatedWorkTimeN(item);
                    if (wTime != null) {
                        List<WorkSlotSlice> slices = wTime.getWorkSlotSlices();
                        if (slices != null) {
                            for (WorkSlotSlice slice : slices) {
//                                if (slice.workSlot == this && !items.contains(item)) {
                                //if slice is from this workslot, and item not already added (necessary??!) and slice is non-zero duration (to exclude house-keeping allocations) and task is not done
//                                if (slice.workSlot == this && !items.contains(item) && slice.getDuration() > 0 && (!item.isDone() || includeDoneTasks)) {
                                if (slice.workSlot == this && (slice.getDurationInMillis() > 0 || includeDoneTasks)) {// || Config.TEST)) {
                                    items.add(item);
                                }
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    public String getWorkSlotAllocationsAsStringForTEST() {
        ItemAndListCommonInterface owner = getOwner();
        List<Item> items = new ArrayList<>();
        if (owner != null) {
            WorkTimeAllocator wta = owner.getWorkTimeAllocatorN();
            if (wta != null) {
                return wta.toString();
            }
        }
        return "WorkSlotAllocation EMPTY";
    }

    public long getRemainingAvailableTime(long now) {
        if (getDurationAdjusted(now) == 0) {
            return 0;
        }
        long lastAllocatedTimeOfWorkSlot = 0;
        ItemAndListCommonInterface owner = getOwner();
        List<Item> items = new ArrayList<>();
        if (owner != null) {
            for (Object i : owner.getListFull()) { //go through everyone of the WorkSLot's Owner's tasks
                if (i instanceof Item) {
                    Item item = (Item) i;
                    WorkTimeSlices wTime = owner.getAllocatedWorkTimeN(item);
                    if (wTime != null) {
                        List<WorkSlotSlice> slices = wTime.getWorkSlotSlices();
                        if (slices != null) {
                            for (WorkSlotSlice slice : slices) {
                                if (slice.workSlot == this && (slice.getDurationInMillis() > 0 && slice.getEndTime() > lastAllocatedTimeOfWorkSlot)) {// || Config.TEST)) {
                                    lastAllocatedTimeOfWorkSlot = slice.getEndTime();
                                }
                            }
                        }
                    }
                }
            }
        }
        long unalloc = getEndTime() - lastAllocatedTimeOfWorkSlot; //may get negative in case last item which got 
        return unalloc > 0 ? unalloc : 0;
    }

//    public long getUnallocatedTimeOLD(long now) {
//        if (getDurationAdjusted(now) == 0) {
//            return 0;
//        }
//        List<Item> items = getItemsInWorkSlot();
//        if (items == null || items.size() == 0) {
//            return 0;
//        } else {
//            //TODO!!!! not sure this algo will work in all cases
//            long unalloc = getEndTime() - items.get(items.size() - 1).getFinishTime(); //may get negative in case last item which got 
////            return getEndTime() - items.get(items.size() - 1).getFinishTime();
//            return unalloc > 0 ? unalloc : 0; //in
//        }
//    }
    /**
     * keep track of which items have been allocated a slide of this workslot
     *
     * @param item
     */
    public void addItemWithSlice(Item item) {
        itemsWithSlicesOfThisWorkSlot.add(item);
    }

    public void resetItemWithSlice() {
        itemsWithSlicesOfThisWorkSlot = new ArrayList();
    }

////<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns the first tasks in the list or project that can fit into this
     * workslot. Used in Today view to show today's workslots and which tasks
     * fit into them.
     *
     * @return
     */
//    public List<Item> getTasksInWorkSlotForToday() {
////        ItemAndListCommonInterface owner = getOwner();
////        WorkTimeAllocator wta = owner.getWorkTimeAllocatorN();
////        List<ItemAndListCommonInterface> workTimeAllocators = owner.getPotentialWorkTimeProvidersInPrioOrder();
////        for (WorkTimeAllocator w : workTimeAllocators) {
////            if (w.getAllocatedWorkTime(FIELD_INACTIVE, FIELD_DURATION)) {
////
////            }
////        }
////        wta.g List
////        <ItemAndListCommonInterface > items = new ArrayList<>();
////        long workTime = getDurationAdjusted();
////        for (ItemAndListCommonInterface i : owner.getList()) {
////            items.add(i);
////            workTime -= i.getRemainingEffort();
////            if (workTime <= 0) {
////                break;
////            }
////        }
////        return items;
//if not already done, force calculation of workTime!!!
//return itemsWithSlidesOfThisWorkSlot;
//    }
//    public List<ItemAndListCommonInterface> getTasksInWorkSlotForTodayOLD() {
//        ItemAndListCommonInterface owner = getOwner();
//        List<ItemAndListCommonInterface> items = new ArrayList<>();
//        long workTime = getDurationAdjusted();
//        for (ItemAndListCommonInterface i : owner.getList()) {
//            items.add(i);
//            workTime -= i.getRemainingEffort();
//            if (workTime <= 0) {
//                break;
//            }
//        }
//        return items;
//    }
////</editor-fold>
    public Item getItemsInWorkSlotAsArticialItem() {
//        List<? extends ItemAndListCommonInterface> subtasks = getTasksInWorkSlotForToday();
        ItemAndListCommonInterface owner = getOwner();
        owner.forceCalculationOfWorkTime();
        Item artificialItem = new Item();
//        artificialItem.setList(getTasksInWorkSlotForToday());
        artificialItem.setList(new ArrayList(itemsWithSlicesOfThisWorkSlot)); //work on a copy!
        artificialItem.setText(
                WorkSlot.WORKSLOT
                + " " + MyDate.formatTimeNew(getStartTimeD()) + "-" + MyDate.formatTimeNew(getEndTimeD())
                + (getOwner() != null ? " in " + getOwner().getText() : "")
        );
        return artificialItem;
    }

////<editor-fold defaultstate="collapsed" desc="comment">
    @Override
    public boolean isDone() {
        return ItemList.getNumberOfUndoneItems(getListFull(), true) == 0; //workSlot is considered 'done' if no undone tasks have slices from it
    }

    @Override
    public void setDone(boolean done) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ItemStatus getStatus() {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getRemaining() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return 0;
    }

    @Override
    public long getEstimate() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return 0;
    }

    @Override
    public long getActual() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return 0;
    }

//    @Override
//    public WorkSlotList getWorkSlotListN() {
//        return getOwner().getWorkSlotListN();
//    }
    @Override
    public WorkSlotList getWorkSlotListN(boolean refreshWorkSlotListFromDAO) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfUndoneItems(boolean includeSubTasks) {
        return ItemList.getNumberOfUndoneItems(getItemsInWorkSlot(), false); //count undone tasks within workslot (//UI: only first level, not leaf-tasks
//        return 0; //done to circumvent a problem where Today lsit got added to Lists (I think)
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getNumberOfItems(boolean onlyUndone, boolean countLeafTasks) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return getItemsInWorkSlot().size(); //count tasks within workslot

    }

    @Override
    public int getNumberOfSubtasks(boolean onlyUndone, boolean countLeafTasks) {
        return ItemList.getNumberOfItems(getListFull(), onlyUndone, countLeafTasks);
    }

    @Override
    public String getComment() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return "";
    }

    @Override
    public void setComment(String val) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public boolean isExpandable() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public String toString(ToStringFormat format) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ItemAndListCommonInterface cloneMe() {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ItemAndListCommonInterface cloneMe(CopyMode copyFieldDefintion, int copyExclusions) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copyMeInto(ItemAndListCommonInterface destiny) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatus(ItemStatus itemStatus) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSize() {
        return getItemsInWorkSlot().size(); //only count undone tasks
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface subItemOrList) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public boolean addToList(int index, ItemAndListCommonInterface subItemOrList) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public boolean addToList(ItemAndListCommonInterface item, ItemAndListCommonInterface subItemOrList, boolean addAfterItem) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeFromList(ItemAndListCommonInterface subItemOrList, boolean removeReferences) {
//        ItemAndListCommonInterface owner = getOwner();
//        if (owner!=null)
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ItemAndListCommonInterface removeFromOwner() {
        ItemAndListCommonInterface owner = getOwner();
        if (owner != null) {
            owner.removeWorkSlot(this);
            return owner;
        }
        return null;
    }

    @Override
    public int getItemIndex(ItemAndListCommonInterface subItemOrList) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ItemAndListCommonInterface> getList() {
        return getListFull(); //(List<ItemAndListCommonInterface>) itemsWithSlicesOfThisWorkSlot;
    }

    @Override
    public List<ItemAndListCommonInterface> getListFull() {
        return (List<ItemAndListCommonInterface>) itemsWithSlicesOfThisWorkSlot;
    }

    @Override
    public void setList(List listOfSubObjects) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isNoSave() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

//    @Override
//    public WorkTimeAllocator getWorkTimeAllocatorN(boolean reset) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    @Override
//    public WorkTimeAllocator getWorkTimeAllocatorN() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public void resetWorkTimeDefinition() {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//do nothing, may be called from Today screen in Main
    }

//    @Override
//    public Date getFinishTime(ItemAndListCommonInterface item) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    @Override
    public List getChildrenList(Object parent) {
//        return getTasksInWorkSlotForToday();
        return getItemsInWorkSlot();
    }

    @Override
    public boolean isLeaf(Object node) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ItemAndListCommonInterface> getOtherPotentialWorkTimeProvidersInPrioOrderN() {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public WorkTimeSlices getAllocatedWorkTimeN() {
////        return getWorkTimeAllocatorN().getWorkTime(this);
//        throw new Error("Not supported yet."); //not supported by WorkSlot
//    }
    @Override
    public void setNewFieldValue(String fieldParseId, Object objectBefore, Object objectAfter) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//</editor-fold>

    @Override
    public int getNumberOfItemsThatWillChangeStatus(boolean recurse, ItemStatus newStatus, boolean changingFromDone) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCountOfSubtasksWithStatus(boolean recurse, List statuses) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setWorkSlotList(WorkSlotList workSlotList) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Object get(int index) {
        return getListFull().get(index);
    }

    @Override
    public FilterSortDef getFilterSortDef() {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFilterSortDef(FilterSortDef filterSortDef) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public WorkTimeAllocator getWorkTimeAllocatorN() {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setWorkTimeAllocator(WorkTimeAllocator workTimeAllocator) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public WorkTimeSlices getAllocatedWorkTimeN() {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean deletePrepare(Date deletedDate) {

        //DELETE IN OWNER
        ItemAndListCommonInterface owner = getOwner();
        ASSERT.that(owner != null, "softDeleting workSlot with no owner, workSlot=" + this);
        if (owner != null) {
            WorkSlotList workSlotList = owner.getWorkSlotListN();
            if (Config.PRODUCTION_RELEASE || workSlotList != null) { //don't crash in case there is an error
                workSlotList.remove(this);
                owner.setWorkSlotList(workSlotList);
//                DAO.getInstance().saveInBackground((ParseObject) owner);
                DAO.getInstance().saveNew((ParseObject) owner, false);
            }
        }

        RepeatRuleParseObject myRepeatRule = getRepeatRule();
        if (myRepeatRule != null) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this); //if we
//            myRepeatRule.deleteThisRepeatInstanceFromRepeatRuleListOfInstances(this);
//            myRepeatRule.updateRepeatInstancesOnDoneCancelOrDelete(this); //UI: if you delete (like if you cancel) a repeating task, new instances will be generated as necessary (just like if it is marked done) - NB. Also necessary to ensure that the repeatrule 'stays alive' and doesn't go stall because all previously generated instances were cancelled/deleted...
//            myRepeatRule.updateIfExpiredOrDeletedWorkslots(this); //UI: if you delete (like if you cancel) a repeating task, new instances will be generated as necessary (just like if it is marked done) - NB. Also necessary to ensure that the repeatrule 'stays alive' and doesn't go stall because all previously generated instances were cancelled/deleted...
//            myRepeatRule.updateWhenWorkSlotDeleted(this); //UI: if you delete (like if you cancel) a repeating task, new instances will be generated as necessary (just like if it is marked done) - NB. Also necessary to ensure that the repeatrule 'stays alive' and doesn't go stall because all previously generated instances were cancelled/deleted...
//</editor-fold>
            myRepeatRule.updateIfExpiredOrDeletedWorkslots(this); //UI: if you delete (like if you cancel) a repeating task, new instances will be generated as necessary (just like if it is marked done) - NB. Also necessary to ensure that the repeatrule 'stays alive' and doesn't go stall because all previously generated instances were cancelled/deleted...
            //NB. We don't delete the item's refs to repeatrule
            DAO.getInstance().saveNew(myRepeatRule, false);
        }

        //TODO!!!! need to delete in Sources - see comments/thoughts on this in comments inside Item.softDelete()
//        put(Item.PARSE_DELETED_DATE, new MyDate());
        setSoftDeletedDate(deletedDate);
//        if (hardDelete)
//        DAO.getInstance().deleteAndWaitXXX((ParseObject) this);
//            else
//        DAO.getInstance().saveInBackground((ParseObject) this);
        return true;
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface subItemOrList, boolean addToEndOfList) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * returns by how much time (duration) the two workslots overlap
     *
     * @param workSlot
     * @return
     */
    public long overlappingDuration(WorkSlot workSlot) {
        if (getStartTimeD().getTime() >= workSlot.getEndTimeD().getTime() || workSlot.getStartTimeD().getTime() >= getEndTimeD().getTime()) {
            return 0;
        } else { // !(S1>E2 || S2>E1) <=> (S1<=E2 && S2<=E1)
            long start = Math.max(getStartTimeD().getTime(), workSlot.getStartTimeD().getTime());
            long end = Math.min(getEndTimeD().getTime(), workSlot.getEndTimeD().getTime());
            return end - start;
        }
    }

    @Override
    public void updateBeforeSave() {
        if (opsAfterSubtaskUpdates != null) {
            while (!opsAfterSubtaskUpdates.isEmpty()) {
                Runnable f = opsAfterSubtaskUpdates.remove(0); //ensures that each operation is only called once, even if iterating (the run() calls an operation which calls saveInBackground triggering 
                f.run();
            }
        }
    }

}
