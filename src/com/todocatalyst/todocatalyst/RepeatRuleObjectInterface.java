/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Date;
import java.util.List;

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
    Date getRepeatStartTime(boolean fromCompletedDate);

    /**
     * set repeat start time in case it was edited, e.g. while editing the
     * repeatR
     *
     * @param repeatStartTime
     */
    void setRepeatStartTime(Date repeatStartTime);
//        long getRepeatStartTime();

    /**
     * creates a repeat copy of the object, using repeatTime as the start time.
     *
     * @param referenceTime
     * @return
     */
    RepeatRuleObjectInterface createRepeatCopy(Date referenceTime);

    void updateRepeatInstanceRelativeDates(Date newDueDateTime);

    /**
     * returns the parentList of an RepeatRuleObject. Eg Used to determine in
     * which list to insert new created instances.
     *
     * @return
     */
//    ItemList getListForNewCreatedRepeatInstances();
    /**
     * deletes the repeatInstance
     */
//    void deleteRepeatInstance();
    /**
     * sets the repeat rule (eg if new or changed)
     *
     * @param myRepeatRule
     */
    void setRepeatRuleForRepeatInstance(RepeatRuleParseObject myRepeatRule);

    /**
     * returns true if the RepeatRuleObject is dealt with, e.g. for an Item is
     * is Completed/Done, for a WorkSlot is is marked inactive or expired
     *
     * @return
     */
//    boolean isNoLongerRelevant();
    /**
     * returns the list into which insert new repeat instances generated from
     * this. Typically the owner.
     *
     * @return
     */
//    List getInsertNewRepeatInstancesIntoList();
    /**
     * add the new repeatCopy to the right place in the owner list and return
     * the owner list so it can be saved
     *
     * @param newRepeatRuleInstance
     * @return
     */
//    void saveInsertList();
//    void insertIntoListAndSaveList(RepeatRuleObjectInterface repeatRuleObject);
//public void insertIntoListAndSaveListAndInstance(RepeatRuleObjectInterface orgInstance, RepeatRuleObjectInterface repeatRuleObject);   
    public static int INSERT_HEAD_OF_OWNER_LIST = 0;
    public static int INSERT_TAIL_OF_OWNER_LIST = 1;
    public static int INSERT_BEFORE_REF_ELEMENT = 2;
    public static int INSERT_AFTER_REF_ELEMENT = 3;

    public enum RepatInsertPosition {
        INSERT_HEAD_OF_OWNER_LIST,
        INSERT_TAIL_OF_OWNER_LIST, INSERT_BEFORE_REF_ELEMENT, INSERT_AFTER_REF_ELEMENT
    }

    public ItemAndListCommonInterface insertIntoList(RepeatRuleObjectInterface newRepeatRuleInstance);
//    default public ItemAndListCommonInterface insertIntoList(RepeatRuleObjectInterface newRepeatRuleInstance, RepatInsertPosition insertPosition);

    default public ItemAndListCommonInterface insertIntoList(RepeatRuleObjectInterface newRepeatRuleInstance, RepeatRuleObjectInterface.RepatInsertPosition insertPosition) {
//        ItemAndListCommonInterface owner = getOwner();
        ItemAndListCommonInterface owner = null;
        if (this instanceof ItemAndListCommonInterface) {
            owner = ((ItemAndListCommonInterface) this).getOwner();
        }
        if (owner != null && newRepeatRuleInstance instanceof ItemAndListCommonInterface) {
            ItemAndListCommonInterface refElt = (ItemAndListCommonInterface) newRepeatRuleInstance;
            switch (insertPosition) {
                case INSERT_HEAD_OF_OWNER_LIST:
                    owner.addToList(owner, false);
                    break;
                case INSERT_TAIL_OF_OWNER_LIST:
                    owner.addToList(owner, true);
                    break;
                case INSERT_BEFORE_REF_ELEMENT:
                    owner.addToList(owner, refElt, false);
                    break;
                case INSERT_AFTER_REF_ELEMENT:
                    owner.addToList(owner, refElt, true);
                    break;
            }
        }
//        if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()) {// && (ownerList.indexOf(this)) != -1) {
//            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, this, true); ///NB. no need to check if 'this' is in list since this insert defaults normal add if not
//        } else {
//            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, !MyPrefs.insertNewItemsInStartOfLists.getBoolean());
//        }
        return owner;
    }

    /**
     *
     * @param copyFieldDefintion
     * @param copyExclusions
     * @return
     */
//    public RepeatRuleObjectInterface cloneMeToRepeatInstance(Item.CopyMode copyFieldDefintion, int copyExclusions);
}
