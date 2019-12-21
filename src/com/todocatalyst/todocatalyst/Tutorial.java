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
public class Tutorial {

    ItemList tut = new ItemList();

    Tutorial() {
        Item section;
        section = section("Tasks")
                .topic("Add new new tasks with + or pinchInsert")
                .topic("The task status is use: use two fingers to 'pinch out' in the list where you want to insert a new element")
                .topic("Long press Status to set a task Waiting or Cancel it")
                .topic("pinchInsert: use two fingers to 'pinch out' in the list where you want to insert a new element")
                .topic("pinchInsert can be used to insert tasks in lists, new subtasks,or new lists or categories")
                .topic("Use Comments to capture additional information. ")
                .topic("Easily add date (and optionally time) to comments. You can set whether to insert new dated lines at the beginning or end of comment");
        tut.add(section);

        section = section("Inbox")
                .topic("xx")
                .topic("xx");
        tut.add(section);
        
        section = section("Today")
                .topic("xx")
                .topic("xx");
        tut.add(section);
        
        section = section("Lists")
                .topic("xx")
                .topic("xx");
        tut.add(section);
        
        section = section("Categories")
                .topic("xx")
                .topic("You can assign multiple categories to each task");
        tut.add(section);
        
        section = section("Other useful lists")
                .topic("Next shows you the tasks due in the next 30 days")
                .topic("Log shows you all tasks you have completed the last 30 days (you can change the number of days)")
                .topic("Diary shows you the tasks you have created in the last 30 days")
                .topic("Expired Alarms shows you alarms you have not not yet accepted, snoozed or rescheduled")
                .topic("xx");
        tut.add(section);

        section = section("Projects")
                .topic("xx")
                .topic("xx");
        tut.add(section);

        section = section("Inheritance")
                .topic("xx")
                .topic("xx");
        tut.add(section);

        section = section("Templates")
                .topic("xx")
                .topic("xx");
        tut.add(section);

        section = section("Inbox")
                .topic("xx")
                .topic("xx");
        tut.add(section);

        section = section("Inbox")
                .topic("xx")
                .topic("xx");
        tut.add(section);
    }

    Item section(String text) {
        Item newSection = new Item(text);
        tut.add(newSection);
        return newSection;
    }

    Item topic(String text) {
        Item newSection = new Item(text);
        tut.add(newSection);
        return newSection;
    }
}
