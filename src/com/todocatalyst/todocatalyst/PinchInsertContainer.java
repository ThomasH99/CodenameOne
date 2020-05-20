/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.TextArea;

/**
 * generic root class to easily recognize all InlineInsertNewContainers
 *
 * @author thomashjelm
 */
abstract public class PinchInsertContainer extends Container {//implements InsertNewElementFunc {

//    @Override
//    public InsertNewElementFunc make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
    abstract public PinchInsertContainer make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category);
//    {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        return null;
//    }

//    @Override
//    public TextArea getTextArea() {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        return null;
//    }
    abstract public TextArea getTextArea();

//    @Override
//    public Command getEditTaskCmd() {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        return null;
//    }
    abstract public Command getEditTaskCmd();

//    public boolean removePinchContainer() {
//        Form f = getComponentForm();
//        if (f instanceof MyForm) {
//             SaveEditedValuesLocally prevVals = ((MyForm)f).previousValues;
//             if (prevVals!=null) {
//                 prevVals
//             }
//        }
//                    MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParent(this);
//
//    }
    abstract public void closePinchContainer(boolean stopAddingInlineContainers);

//    abstract public void done();
//    protected static final String SAVE_LOCALLY_INLINE_INSERT_TEXT = "InlineInsertSavedText"; //used to save inline text from within the InlineInsert container
}
