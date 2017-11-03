/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Hashtable;

/**
 * Edit a priority definition/calculation.
 * Enables supports multiple priority drivers, each can have a weight that is multiplied onto the value.
 *
 * @author Thomas
 */
public class PriorityUserDrivers extends Priority {

    class PriorityPair {
        String description="";
        int weight=1;
    }

    static ItemList userDriversList; //TODO: move to Settings?!
    Hashtable driverValues;

    int getDriverWeight(String driver) {
        for (int i=0, size=userDriversList.getSize(); i<size;i++) { //TODO: optimization: replace list by a hashtable for faster search
            if (((PriorityPair)userDriversList.getItemAt(i)).description.equals(driver)) {
                return ((PriorityPair)userDriversList.getItemAt(i)).weight;
            }
        }
        return 0;
    }

    public void setPriority(int priority) {
        this.priority = priority;

    }

    public int getPriority() {
        return calcPriority();
    }

    public void setDriverValues(Hashtable driverValues) {
       this.driverValues=driverValues;
    }

    /**
     * calculate the value of priority, based on the user defined values for each driver, multiplied by the weight defined for
     * each driver.
     * @return
     */
    int calcPriority() {
        return 0; //TODO!!!!
    }

    
}
