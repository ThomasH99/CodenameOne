/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 *
 * @author thomashjelm
 */
//public class TodaySortOrder {
/**
 * defines the sort order of tasks in the Today view. Can be extended or
 * modified together with getTodaySortOrder() to change the Today view.
 */
public enum TodaySortOrder {
    OVERDUE_TODAY(1, "Overdue"),
    DUE_TODAY_ONGOING(5, "Due today (" + ItemStatus.ONGOING + ")"),
    DUE_TODAY_WAITING(10, "Due today (" + ItemStatus.WAITING + ")"),
    DUE_TODAY_CREATED(15, "Due today"),
    WAITING_TODAY(20, ItemStatus.WAITING + " until today"),
    WAITING_ALARM(25, Item.ALARM_DATE + " today"),
    ALARM_TODAY(30, "Reminder today"),
    //        STARTING_TODAY_ONGOING(35, "Starting today (Ongoing)"),
    //        STARTING_TODAY_WAITING(40, "Starting today (Waiting)"),
    STARTING_TODAY_CREATED(45, "Starting today"),
    STARTING_TODAY_WORKSLOT(55, WorkSlot.WORKSLOT),
    TODAY_OTHER(99, "Other");
    int sortOrder;
    String displayText;

    TodaySortOrder(int sortOrder, String displayText) {
        this.displayText = displayText;
        this.sortOrder = sortOrder;
    }

    int getSortOrder() {
        return sortOrder;
    }
//    }

}
