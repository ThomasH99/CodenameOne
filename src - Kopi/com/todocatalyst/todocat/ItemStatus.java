/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

/**
 *
 * @author Thomas
 */
public enum ItemStatus {
    //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
    CREATED("Created", "New task"), 
    /** work has started / is currently being worked on */ 
    ONGOING("In progress", "Work has started"), 
    /**work has started, but has been put on hold or is waiting for something from the outside (remaining can't do work on it until some external event or input happens) */ 
    WAITING("Waiting","Task is on hold and waiting for something"), 
    DONE("Done","Task is completed, no more work"), 
    CANCELLED("Cancelled","Task has been cancelled (similar to deleted, but is kept to maintain history)");

    private final String name;
    private final String description;
    
    ItemStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String toString() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    
}
