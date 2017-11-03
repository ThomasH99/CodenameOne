/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

/**
 * implements the interface needed by RepeatRules to instantiate copies. used
 * for classes that accept RepeatRules: Items and WorkSlots
 *
 * @author Thomas
 */
public interface RepeatRuleObjectInterface {

    /**
     * returns the start time used to calculate all repeat instances from. In
     * case of a completed task: use completedDate. For
     *
     * @param fromCompletedDate
     * @return
     */
    long getRepeatStartTime(boolean fromCompletedDate);

    /**
     * set repeat start time in case it was edited, e.g. while editing the
     * repeatR
     *
     * @param repeatStartTime
     */
    void setRepeatStartTime(long repeatStartTime);
//        long getRepeatStartTime();

    /**
     * creates a repeat copy of the object, using repeatTime as the start time.
     *
     * @param referenceTime
     * @return
     */
    RepeatRuleObjectInterface createRepeatCopy(long referenceTime);

    /**
     * returns the parentList of an RepeatRuleObject. Eg Used to determine in
     * which list to insert new created instances.
     *
     * @return
     */
    ItemList getListForNewCreatedRepeatInstances();

    /**
     * deletes the repeatInstance
     */
    void deleteRepeatInstance();

    /**
     * sets the repeat rule (eg if new or changed)
     *
     * @param myRepeatRule
     */
    void setRepeatRule(RepeatRuleParseObject myRepeatRule);

    /**
     * returns true if the RepeatRuleObject is dealt with, e.g. for an Item is
     * is Completed/Done, for a WorkSlot is is marked inactive or expired
     *
     * @return
     */
    boolean isNoLongerRelevant();
}
