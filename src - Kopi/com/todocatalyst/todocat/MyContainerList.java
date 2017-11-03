/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.io.Log;
import com.codename1.ui.list.ContainerList;
import com.codename1.ui.list.ListModel;

/**
 *
 * @author Thomas
 */
public class MyContainerList extends ContainerList {

    MyContainerList(ListModel list) {
        super(list);
        setUIID("List");
        Log.p("MyContainerList created");
    }
    
//    class MyEntry extends Entry {
//        
//    }

    
    public void setDraggable(boolean draggable) {
        for (int i=0, size=getComponentCount();i<size;i++) {
//        this.draggable = draggable;
            getComponentAt(i).setDraggable(true);
        }
    }
    
    @Override
    public void pointerPressed(int x, int y) {
        //<editor-fold defaultstate="collapsed" desc="comment">
        //        if(fixedSelection > FIXED_NONE_BOUNDRY) {
        //            // for a fixed list we need to store the initial drag position
        //            if(isSmoothScrolling()) {
        //                if(orientation != HORIZONTAL) {
        //                    fixedDraggedPosition = y;
        //                } else {
        //                    fixedDraggedPosition = x;
        //                }
        //                if(isDragActivated()){
        //                    int selected = getCurrentSelected();
        //                    model.setSelectedIndex(selected);
        //                    fixedDraggedMotion = null;
        //                    fixedDraggedAnimationPosition = 0;
        //                }
        //                fixedDraggedSelection = getModel().getSelectedIndex();
        //            }
        //        }
        //        // prevent a hover event from activating the drag in case of a click screen,
        //        // this is essential for the Storm device
        //        setDragActivated(false);
        //        int current = model.getSelectedIndex();
        //        int selection = pointerSelect(x, y);
        //
        //        if (selection > -1 && fixedSelection < FIXED_NONE_BOUNDRY) {
        //            model.setSelectedIndex(selection);
        //        }
        //        fireOnRelease = current == selection;
        //</editor-fold>
        Log.p("MyContainerList.pointerPressed(" + x + ", " + y + ")");
        super.pointerPressed(x, y);
    }
    @Override
    public void longPointerPress(int x, int y) {
        Log.p("MyContainerList.longPointerPress(" + x + ", " + y + ")");
        super.longPointerPress(x, y);
    }
    //    public int getSelectedIndex() {
//        return getModel().getSelectedIndex();
//    }
//    
//    public void setSelectedIndex(int index) {
//        getModel().setSelectedIndex(index);
//    }
}
