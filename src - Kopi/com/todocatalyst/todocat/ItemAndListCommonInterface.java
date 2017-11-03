/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

/**
 *
 * @author THJ
 */
public interface ItemAndListCommonInterface {

//    public boolean isDone();
//    boolean isTemplate();
//    void setTemplate(boolean on);
//    public void setDone(boolean done);
    public Item.ItemStatus getStatus();

    public int getRemainingTime();

    public long getRemainingEffort();

    public int getNumberOfUndoneItems(boolean includeSubTasks);

    public int getNumberOfUndoneItems();

    public String getComment();

    public void setComment(String val);

    /* returns the key text string for the subtypes of BaseItem, e.g. Item
     * returns Description, Category categoryName, ... getText() in BaseItem is
     * not supposed to be used directly but must be overwritten by subtypes.
     *
     * @return
     */
    public String getText();

    /**
     * sets the key text string for the subtypes of BaseItem, e.g. for Item
     * Description, for Category categoryName, ... setText() in BaseItem is not
     * supposed to be used directly but must be overwritten by subtypes.
     *
     * @return
     */
    public void setText(String text);

    public boolean isExpandable();

    public ItemAndListCommonInterface getOwner();

    /**
     * returns the previous value of owner
     *
     * @param owner
     * @return
     */
    public ItemAndListCommonInterface setOwner(ItemAndListCommonInterface owner);

    /**
     * returns a printable string that uniquely identifies this item
     *
     * @return
     */
    public String getItemIdStr();

    public enum ToStringFormat {
        TOSTRING_COMMA_SEPARATED_LIST,
        TOSTRING_DEFAULT;
    }

    public String toString(ToStringFormat format);

    /**
     * creates a complete (deep) copy of this item. Only elements that are
     * 'owned' by the item are copied. Elements that are only referenced, like
     * categories, are NOT copied. Lists are special case: copyMeInto will
     * create a new list referencing the same elements, whereas clone() will
     * also clone the elements in the list.
     */
    public ItemAndListCommonInterface cloneMe();
//        BaseItem newCopy = new BaseItem();
//        copyMeInto(newCopy);
//        return newCopy;
//    }

    //<editor-fold defaultstate="collapsed" desc="comment">
    //    /** returns the int value used by getSumAt() to calculate the sum of lists. Can be overwritten by eg. ItemLists to return some meaningful value to add up in
    //    lists of lists */
    //    int getSumField(int fieldId) {
    //        Object itemField;
    //        if (this instanceof Item && (itemField = (((Item) this).getFilterField(fieldId))) instanceof Integer) {
    //            return ((Integer) itemField).intValue();
    //        } else {
    //            return 0;
    //        }
    //    }
    //</editor-fold>
    /**
     * copy this object into the given destiny object
     *
     * @param destiny
     */
//    public void copyMeInto(ItemAndListCommonInterface destiny, boolean copyText);
////        super.copyMeInto(destiny); //- no fields in BaseBaseItem
//        //super.copy(source);
//        //destiny.setGuid(0); //must NOT clone guid //- not necessary since guid is always initialized to 0
//        //destiny.setRmsIdx(getRmsIdx()); //- must NOT be cloned since the clone would then be written to same RMS position (overwriting the original item)
////        destiny.setOwner(getOwner()); //-don't reuse owner
////        destiny.setLogicalName(getLogicalName()); //-don't reuse Logical name
//        destiny.storedFormatVersion = storedFormatVersion;
//        destiny.setTypeId(getTypeId());
//        if (copyText) {
//            destiny.setText(new String(getText())); //make a copy of text string (to be able to edit new string separately)
//        }
//        //setCommitted(false); // do not set autocommit to true for cloned objects, it should only be set once they are actively saved to avoid inconsistencies btw RMS and memory //-should be default for new objects
//        //xxxsetConsistency(false); // do not set consistency to true for cloned objects, it should only be set once they are actively saved to avoid inconsistencies btw RMS and memory //-should be default for new objects
//    }
    /**
     * copy this object into the given destiny object
     *
     * @param destiny
     */
    public void copyMeInto(ItemAndListCommonInterface destiny);
//        copyMeInto(destiny, true);
//    }

    /**
     * returns a short string for displaying to the user the item in a list
     *
     * @return
     */
    public String shortString();
////        return "typeId=" + BaseItemTypes.toString(typeId)+ "|guid=" + guid;
////        return BaseItemTypes.toString(getTypeId()) + (getText().length() != 0 ? "\"" + getText() + "\"" : "Gui" + getGuid());
//        return BaseItemTypes.toString(getTypeId()) + "\"" + getText() + "\"";
//    }

    /**
     * deletes this instance
     */
    public void delete();
    public void setStatus(Item.ItemStatus itemStatus);

}
