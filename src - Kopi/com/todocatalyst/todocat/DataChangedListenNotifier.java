/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.events.DataChangedListener;

/**
 *
 * @author Thomas
 */
public interface DataChangedListenNotifier {
    
//    private EventDispatcher listeners = new EventDispatcher();
    
        /**
     * Adds a listener for data change events it will be invoked for every change
     * made to the text field, notice most platforms will invoke only the 
     * DataChangedListener.CHANGED event
     * 
     * @param d the listener
     */
       public void addDataChangeListener(DataChangedListener d);
//               listeners.addListener(d);
//    }

       
    /**
     * Removes the listener for data change events 
     * 
     * @param d the listener
     */
    public void removeDataChangeListener(DataChangedListener d);
//        listeners.removeListener(d);
//    }    
    /**
     * Alert the TextField listeners the text has been changed on the TextField
     * @param type the event type: Added, Removed or Change
     * @param index cursor location of the event
     */
    public void fireDataChanged(int type, int index);
//        if(listeners != null) {
//            listeners.fireDataChangeEvent(index, type);
//        }
//    }    
}
