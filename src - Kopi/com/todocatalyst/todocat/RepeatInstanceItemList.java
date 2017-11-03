/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

/**
 *
 * @author Thomas
 */
    //    private class RepeatInstanceVector {
    class RepeatInstanceItemList extends ItemList {

        /**
         *
         * @param ownerAndListener
         */
        public RepeatInstanceItemList() {//BaseItem ownerAndListener) {
//            super(true); //ensure that inserted it is saved directly
//            super(null, ownerAndListener, true, false, false, true, true); //store items only once
            super();
//            setTypeId(BaseItemTypes.REPEATINSTANCEITEMLIST);
//            super(BaseItemTypes.NOTYPE, ownerAndListener, true, false, true); //ensure that inserted it is saved directly
        }

//        public RepeatInstanceItemList() {
//            this(null); 
//        }

        //<editor-fold defaultstate="collapsed" desc="comment">
        //        public RepeatInstanceItemList(BaseItem ownerAndListener, DataInputStream dis) {
        ////            super(true); //ensure that inserted it is saved directly
        //            super(ownerAndListener, dis); //store items only once
        //            setTypeId(BaseItemTypes.REPEATINSTANCEITEMLIST);
        ////            super(BaseItemTypes.NOTYPE, ownerAndListener, true, false, true); //ensure that inserted it is saved directly
        //        }
        
        //        Vector repeatInstances = new Vector(); // = 1; //
        //        int getRepeatInstancesSize() {
        //            return repeatInstances.size();
        //        }
        //        void addElement(RepeatRuleObject repeatRuleObject) {
        //            repeatInstances.addElement(repeatRuleObject);
        //        }
        //        RepeatRuleObject elementAt(int index) {
        //            return (RepeatRuleObject) repeatInstances.elementAt(index);
        //        }
        //</editor-fold>
        /**
         * deletes the referenceItem from the list of repeat instances. Used for
         * example when a repeating instance is marked Done
         */
//        public void removeElement(RepeatRuleObject repeatRuleObject) {
//            repeatInstances.removeElement(repeatRuleObject);
//        }
        long getRepeatInstancesLatestDefinedDueDate() {
//            if (getRepeatInstancesSize() > 0) {
            if (getSize() > 0) {
//                return ((Item) repeatInstances.lastElement()).getDueDate(); //this assumes (as stated by the implementation of RepeatRule) that dates() returns an enumeration ordered by date
                return ((Item) getItemAt(getSize())).getDueDate(); //this assumes (as stated by the implementation of RepeatRule) that dates() returns an enumeration ordered by date
            } else {
                return 0L;
            }
        }

        //<editor-fold defaultstate="collapsed" desc="comment">
        //        /**
        //         * if there already is an item with the given due date created, then
        //         * return that so it can be reused. UI: this means that any manuel edits
        //         * made to instances are retained as long as the date remains the same!!
        //         * Helps avoid that already created items that have been edited are
        //         * deleted (and recreated) even if they occur on the same date - for the
        //         * user this would not be easily acceptable
        //         */
        //        RepeatRuleObject xxgetAndRemoveRepeatInstanceWithDate(long repeatDate) {
        ////        for (int i = getRepeatInstancesSize(); i > 0; i--) {
        ////            for (int i = 0, size = getRepeatInstancesSize(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
        //            for (int i = 0, size = getSize(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
        ////                if (((Item) repeatInstances.elementAt(i)).getDueDate() == repeatDate) {
        //                if (((Item) getItemAt(i)).getDueDate() == repeatDate) {
        ////                    RepeatRuleObject item = (RepeatRuleObject) repeatInstances.elementAt(i);
        //                    RepeatRuleObject item = (RepeatRuleObject) getItemAt(i);
        ////                    repeatInstances.removeElementAt(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
        //                    removeItem(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
        //                    return item;
        //                }
        //            }
        //            return null;
        //        }
        //</editor-fold>

        /**
         * searches already generated repeat instances for an instance with same
         * date. If found, returns it for reuse. If not found, returns null.
         *
         * @param repeatDate
         * @return
         */
        RepeatRuleObjectInterface getAndRemoveRepeatInstanceWithDate(long repeatDate) {
//        for (int i = getRepeatInstancesSize(); i > 0; i--) {
            MyDate repeatMyDate = new MyDate(repeatDate);
//            for (int i = 0, size = getRepeatInstancesSize(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
            for (int i = 0, size = getSize(); i < size; i++) { //OK to use a for loop since we'll only remove one element and exit immediately after
//                if (((RepeatRuleObject) repeatInstances.elementAt(i)).getRepeatStartTime() == time) { //TODO: is this too exact matching? E.g. if just one millisecond's difference
//                if (((RepeatRuleObject) elementAt(i)).getRepeatStartTime(false) == repeatDate) { //TODO: is this too exact matching? E.g. if just one millisecond's difference
//                if (new MyDate(((RepeatRuleObject) getItemAt(i)).getRepeatStartTime(false)).equalsDate(repeatMyDate)) { //TODO: is this too exact matching? E.g. if just one millisecond's difference
                if (((RepeatRuleObjectInterface) getItemAt(i)).getRepeatStartTime(false)==repeatDate) { //TODO: is this too exact matching? E.g. if just one millisecond's difference
//                    RepeatRuleObject item = (RepeatRuleObject) repeatInstances.elementAt(i);
                    RepeatRuleObjectInterface item = (RepeatRuleObjectInterface) getItemAt(i);
//                    repeatInstances.removeElementAt(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
                    removeItem(i); //remove element so that when remaining items in the (old) repeatInstances list are deleted the reused ones are not
                    return item;
                }
            }
            return null;
        }

        //<editor-fold defaultstate="collapsed" desc="comment">
        //        /**
        //         * deletes all the repeat instances (both from repeatInstances vector,
        //         * and entirely: from RMS, lists etc), except the referenceItem. If the
        //         * referenceItem is not found in the list (should this ever happen??)
        //         * then the first item in the list is left undeleted
        //         */
        //        public void deleteRepeatInstancesExceptThis(RepeatRuleObject repeatRuleObject) {
        //            repeatRuleObject.setRepeatRule(null); //remove reference to repeatRule
        //            removeItem(repeatRuleObject); //remove from ItemList so it won't be deleted
        ////            for (int i = 0, size = getSize(); i < size; i++) {
        //            while (getSize()>0) {
        //                ((RepeatRuleObject) getItemAt(0)).setRepeatRule(null); //remove the link to the repeat rule *before* deleting (to avoid change event callback)
        //                ((RepeatRuleObject) getItemAt(0)).deleteRepeatInstance(); //fully deleteAllOtherRepeatInstancesExceptThis
        //            }
        //            removeAllItems(); //-not necessary?! Yes, since
        //        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="comment">
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
        ////                        ((RepeatRuleObject) repeatInstances.elementAt(i)).deleteRepeatInstance(); //fully deleteAllOtherRepeatInstancesExceptThis
        //                        ((RepeatRuleObject) elementAt(i)).deleteRepeatInstance(); //fully deleteAllOtherRepeatInstancesExceptThis
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
        //</editor-fold>
        /**
         * deletes all the repeat instances (both from repeatInstances vector,
         * and entirely: from RMS, lists etc), except the referenceItem. If the
         * referenceItem is not found in the list (should this every happen??)
         * then the first item in the list is left undeleted
         */
        public void xdeleteAllRepeatInstances() {
//            int i = 0;
            while (getSize()>0) { //repeat as long as repeatInstances non empty, OR, there are more instances after the referenceItem which is head of list
//                ((RepeatRuleObject) getItemAt(0)).setRepeatRule(null); //remove the link to the repeat rule *before* deleting
                ((RepeatRuleObjectInterface) getItemAt(0)).deleteRepeatInstance(); //fully deleteAllOtherRepeatInstancesExceptThis
//                removeItem(0);
            }
//#mdebug
//                ASSERT.that(repeatInstances.size() == 1 && repeatInstances.elementAt(0) == repeatRuleObject, "referenceItem not found");
            ASSERT.that(getSize() == 0, "not all repeat instances were deleted");
//#enddebug
        }
    }


