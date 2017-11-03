/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.Dialog;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.ToDo;
import javax.microedition.pim.ToDoList;

/**
 *
 * @author Thomas
 */
public class PIM_ToDo_MIDP {

    /**
     * Test if the PIM API is supported.
     * @return true if the PIM API is supported, false otherwise
     */
    private static boolean isPimApiSupported() {
        boolean isSupported = true;
        String pimApiVersion =
                System.getProperty("microedition.pim.version");
        if (pimApiVersion == null) {
            isSupported = false;
        }
        return isSupported;
        /*
         *
        // Test if PIM API is supported
        if (isPimApiSupported() == true)
        Log.l("PIM API IS supported");
        else
        Log.l("PIM API is NOT supported");
         */

    }

    /**
     * Helper method to test if calendar events database types are supported.
     *
     * @return true if calendar/events databases are supported, and false
     * if event databases are not supported or if permission to use use the
     * PIM API is denied.
     */
    private static boolean isEventListSupported() {
        boolean retVal;
        EventList el = null;
        try {
            // Try to open the event list; this will tell us if it is supported
            el = (EventList) PIM.getInstance().openPIMList(PIM.EVENT_LIST, PIM.READ_WRITE);
            retVal = true;
        } catch (SecurityException e) {
            retVal = false; // Unknown since access to API was denied
        } catch (PIMException e) {
            retVal = false;
        } finally {
            if (el != null) {
                try {
                    // Close the event list since we only opened it to
                    // see if it is supported.
                    el.close();
                } catch (PIMException ignore) {
                    // ignore
                }
            }
        }
        return retVal;
    }

    /** creates a new Item, and copies ToDo fields to it. If no values defined for any of the normal fields, then an empty item is returned */
    private static Item copyPimToItem(ToDoList todoList, ToDo todo /*, Item item*/) {
        Item item = new Item();
        int[] fields = todo.getFields(); //get all fields that have values defined - this ensures that all the todo.getxxx below won't fail due to undefined values
        for (int i = 0; i < fields.length; i++) {
            switch (fields[i]) {
                case ToDo.CLASS:
                    if (todoList.isSupportedField(ToDo.CLASS)) { //optimization: these checks can be removed since iterating over the defined fields should guarantee that only valid fields are used(??!)
                        item.setClassAccessibilityPimValue(todo.getInt(ToDo.CLASS, PIMItem.ATTR_NONE));
                    }
                    break;
                case ToDo.COMPLETION_DATE:
                    if (todoList.isSupportedField(ToDo.COMPLETION_DATE)) {
                        item.setDueDate(todo.getDate(ToDo.COMPLETION_DATE, PIMItem.ATTR_NONE));
                    }
                    break;
                case ToDo.COMPLETED:
                    if (todoList.isSupportedField(ToDo.COMPLETED)) {
                        item.setDone(todo.getBoolean(ToDo.COMPLETED, PIMItem.ATTR_NONE));
                    }
                    break;
                case ToDo.DUE:
                    if (todoList.isSupportedField(ToDo.DUE)) {
                        item.setDueDate(todo.getDate(ToDo.DUE, PIMItem.ATTR_NONE));
                    }
                    break;
                case ToDo.NOTE:
                    if (todoList.isSupportedField(Event.NOTE)) {
                        item.setComment(todo.getString(ToDo.NOTE, PIMItem.ATTR_NONE));
                    }
                    break;
                case ToDo.PRIORITY:
                    if (todoList.isSupportedField(ToDo.PRIORITY)) {
                        item.setPriorityPim(todo.getInt(ToDo.PRIORITY, PIMItem.ATTR_NONE));
//                        todo.addInt(ToDo.PRIORITY, PIMItem.ATTR_NONE, 2);
                    }
                    break;
                case ToDo.REVISION:
                    if (todoList.isSupportedField(ToDo.REVISION)) {
                        item.setLastModifiedDate(todo.getDate(ToDo.REVISION, PIMItem.ATTR_NONE));
                    }
                    break;
                case ToDo.SUMMARY:
                    if (todoList.isSupportedField(Event.SUMMARY)) {
                        item.setText(todo.getString(ToDo.SUMMARY, PIMItem.ATTR_NONE));
                    }
                    break;
                case ToDo.UID:
                    if (todoList.isSupportedField(ToDo.UID)) {
                        item.setPimUid(todo.getString(ToDo.UID, PIMItem.ATTR_NONE));
//                        todo.addInt(ToDo.PRIORITY, PIMItem.ATTR_NONE, 2);
                    }
                    break;
                default:
//#mdebug
                    Log.l("PIM_ToDo.copyPimToItem: unknown field encountered=" + fields[i]);
//#enddebug
            }
        }
//        String[] categories = todoList.getCategories(); //get all categories
        String[] todoCategories = todo.getCategories(); //get all categories
        Object cat;
        for (int i = 0, size = todoCategories.length; i < size; i++) {
            cat = Categories.getInstance().findItemWithText(todoCategories[i]);
            if (cat != null) {
                item.getCategories().addItem(cat);
            } else {
                Category newCategory = new Category(todoCategories[i]);
                Categories.getInstance().addItem(newCategory);
                item.getCategories().addItem(cat);
                Log.l("new category \"" + newCategory + "\" created");
            }
        }

        return item;
    }

    /**
     * will copy the event into a WorkSlot. Returns null if not all of START, END and SUMMARY are defined
     * @param events
     * @param event
     * @return
     */
    private static WorkSlot copyEventToSlot(EventList events, Event event) {
//        if (events.isSupportedField(Event.START)) {
//            slot.setStart(event.getInt(Event.START, PIMItem.ATTR_NONE));
//        }
//        if (events.isSupportedField(Event.END)) {
//            slot.setEnd(event.getInt(Event.END, PIMItem.ATTR_NONE));
//        }
//        if (events.isSupportedField(Event.SUMMARY)) {
//            slot.setText(event.getString(Event.SUMMARY, PIMItem.ATTR_NONE));
//        }
        WorkSlot slot = null;
        if (events.isSupportedField(Event.START) && events.isSupportedField(Event.END) && events.isSupportedField(Event.SUMMARY)) {
            slot = new WorkSlot();
            slot.setStart(event.getDate(Event.START, PIMItem.ATTR_NONE));
            slot.setEnd(event.getDate(Event.END, PIMItem.ATTR_NONE));
            slot.setText(event.getString(Event.SUMMARY, PIMItem.ATTR_NONE));
        }
        return slot;
    }

    /**
     * returns index of category in categories, or -1 if not found
     * @param categories
     * @param category
     * @return
     */
//    private static int isDefinedCategory(String[] categories, Category category) {
    private static int isDefinedCategory(String[] categories, String category) {
//        String catText = category.getText();
        for (int i = 0, size = categories.length; i < size; i++) {
            if (Settings.getInstance().categoryExportCaseSensitive() ? categories[i].equals(category) : categories[i].equalsIgnoreCase(category)) //UI:
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * copies (exprts) item to the phone's ToDo list
     * @param item
     */
    public static void copyItemToPim(Item item) {
        ToDoList todos = null;
        try {
            todos = (ToDoList) PIM.getInstance().openPIMList(PIM.TODO_LIST, PIM.READ_WRITE);
            //todos.getCategories();
        } catch (PIMException e) {
            // An error occurred
            return;
        }
        ToDo todo = todos.createToDo();
        copyItemToPimTodoInstance(todos, item, todo);
        try {
            todo.commit();
        } catch (PIMException ex) {
            ex.printStackTrace();
        }
        // <editor-fold defaultstate="collapsed" desc="comment">
        //        if (todos.isSupportedField(ToDo.CLASS)) {
        //            todo.addInt(ToDo.CLASS, PIMItem.ATTR_NONE, item.getClassAccessibilityPimValue());
        //        }
        //        if (todos.isSupportedField(ToDo.COMPLETION_DATE)) {
        //            if (item.isDone()) { // only set completed date if item is actually completed (otherwise most phones will force it to Done
        //                todo.addDate(ToDo.COMPLETION_DATE, PIMItem.ATTR_NONE, item.getCompletedDate());
        //            }
        //        }
        //        if (todos.isSupportedField(ToDo.COMPLETED)) {
        //            todo.addBoolean(ToDo.COMPLETED, PIMItem.ATTR_NONE, item.isDone());
        //        }
        //        if (todos.isSupportedField(ToDo.DUE)) {
        //            todo.addDate(ToDo.DUE, PIMItem.ATTR_NONE, item.getDueDate());
        //        }
        //        if (todos.isSupportedField(ToDo.NOTE)) {
        //            todo.addString(ToDo.NOTE, PIMItem.ATTR_NONE, item.getComment());
        //        }
        //        if (todos.isSupportedField(ToDo.PRIORITY)) {
        //            todo.addInt(ToDo.PRIORITY, PIMItem.ATTR_NONE, item.getPriorityPim());
        //        }
        //        if (todos.isSupportedField(ToDo.REVISION)) {
        //            todo.addDate(ToDo.REVISION, PIMItem.ATTR_NONE, item.getLastModifiedDate());
        //        }
        //        if (todos.isSupportedField(ToDo.SUMMARY)) {
        //            todo.addString(ToDo.SUMMARY, PIMItem.ATTR_NONE, item.getText());
        //        }
        //
        //        if (item.getCategoriesSize()>0) {
        //            ItemList categories = item.getCategories();
        //            for (int i=0,size=Math.min(categories.getSize(), todo.maxCategories()); i<size; i++ ) {
        //                try {
        //                    int index = isDefinedCategory(todos.getCategories(), (Category)categories.getItemAt(i));
        //                    if (index!=-1) {
        //                        todo.addToCategory(todos.getCategories()[index]);
        //                    }
        //                } catch (PIMException ex) {
        //                    ex.printStackTrace();
        //                }
        //            }
        //        }
        //        if (todos.isSupportedField(ToDo.UID)) {
        //            todo.addString(ToDo.UID, PIMItem.ATTR_NONE, item.getPimUid());
        //        }
        //        try {
        //            if (todos.maxCategories() != 0) { // && todos.isCategory("Work")) {
        //                Log.l("" + todos.getCategories());
        ////                        todos.getCategories()
        ////                todo.addToCategory("Work");
        //            }
        //        } catch (PIMException ex) {
        //            ex.printStackTrace();
        //        }
        //        try {
        //            todo.commit();
        ////            todo.getAttributes(ToDo.UID, index);
        //        } catch (PIMException ex) {
        //            ex.printStackTrace();
        //            // An error occured
        //        }
        //        try {
        //            todos.close();
        //        } catch (PIMException ex) {
        //            ex.printStackTrace();
        //        }// </editor-fold>
    }

    /**
     *
     * @param todoList
     * @param item
     * @param todo
     */
    private static void copyItemToPimTodoInstance(ToDoList todoList, Item item, ToDo todo) {
        if (todoList.isSupportedField(ToDo.CLASS)) {
            todo.addInt(ToDo.CLASS, PIMItem.ATTR_NONE, item.getClassAccessibilityPimValue());
        }
        if (todoList.isSupportedField(ToDo.COMPLETION_DATE)) {
            if (item.isDone()) { // only set completed date if item is actually completed (otherwise most phones will force it to Done
                todo.addDate(ToDo.COMPLETION_DATE, PIMItem.ATTR_NONE, item.getCompletedDate());
            }
        }
        if (todoList.isSupportedField(ToDo.COMPLETED)) {
            todo.addBoolean(ToDo.COMPLETED, PIMItem.ATTR_NONE, item.isDone());
        }
        if (todoList.isSupportedField(ToDo.DUE)) {
            todo.addDate(ToDo.DUE, PIMItem.ATTR_NONE, item.getDueDate());
        }
        if (todoList.isSupportedField(ToDo.NOTE)) {
            todo.addString(ToDo.NOTE, PIMItem.ATTR_NONE, item.getComment());
        }
        if (todoList.isSupportedField(ToDo.PRIORITY)) {
            todo.addInt(ToDo.PRIORITY, PIMItem.ATTR_NONE, item.getPriorityPim());
        }
        if (todoList.isSupportedField(ToDo.REVISION)) {
            todo.addDate(ToDo.REVISION, PIMItem.ATTR_NONE, item.getLastModifiedDate());
        }
        if (todoList.isSupportedField(ToDo.SUMMARY)) {
            todo.addString(ToDo.SUMMARY, PIMItem.ATTR_NONE, item.getText());
        }

        if (item.getCategoriesSize() > 0) {
            ItemList categories = item.getCategories();
            int maxCategories = todo.maxCategories();
            if (maxCategories == -1) {
                maxCategories = Integer.MAX_VALUE;
            }
            int maxCategoriesList = todoList.maxCategories();
            if (maxCategoriesList == -1) {
                maxCategoriesList = Integer.MAX_VALUE;
            }
            for (int i = 0, size = Math.min(categories.getSize(), maxCategories); i < size; i++) {
                try {
                    int index = isDefinedCategory(todoList.getCategories(), ((Category) categories.getItemAt(i)).getText());
                    if (index != -1) {
                        todo.addToCategory(todoList.getCategories()[index]);
                    } else {
                        if (todoList.getCategories().length < maxCategoriesList) { //check that more categoreis can be added
                            todoList.addCategory(((Category) categories.getItemAt(i)).getText()); //add this category
//#mdebug
                            Log.l("PIM_ToDo.copyItemToPim: created new category in phone's ToDo list: " + ((Category) categories.getItemAt(i)).getText());
//#enddebug
                            index = isDefinedCategory(todoList.getCategories(), ((Category) categories.getItemAt(i)).getText()); //get index of just added category
                            if (index != -1) {
                                todo.addToCategory(todoList.getCategories()[index]); //add category if found
                                //java.lang.NullPointerException - if category is null.
                                //PIMException - may be thrown if category is not in the list's category list and the list prevents that condition from occurring. Also thrown if categories are not supported in the implementation. Also thrown if the max categories that this item can be assigned to is exceeded.
                            }
                        }
                    }
                } catch (PIMException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * returns an ItemList of imported Slots. When adding slots, they are sorted using the comparator. If comparator is null, they are added
     * in the order returned by the phone (using EventList.items()).
     */
    public static ItemList importPIMEvents(long startDate, long endDate, ComparatorInterface sortingComparator) {
//#mdebug
         Log.l("PIM_Todo.impportPIMEvents: startDate="+new MyDate(startDate).formatDate(false)+" endDate="+new MyDate(endDate).formatDate(false));
//#enddebug
//        Vector v = new Vector();
//        WorkSlotList slotList = new WorkSlotList();
//        ItemList slotList = new ItemList();
//        ItemList slotList = WorkSlotDefinition.create ItemList();
//        ItemList slotList = new WorkSlotList();
        ItemList slotList = new ItemList(BaseItemTypes.WORKSLOT);
        EventList eventList = null;
        PIM pim = PIM.getInstance();
        String listNames[] = pim.listPIMLists(PIM.EVENT_LIST);
        if (listNames.length > 0) {
            int listIdx = 0;
            try {
                if (listNames.length > 1) {
                    Dialog.show(null, null, null, null);
                    listIdx = 0; //TODO!!!!: let user select list and save in settings
                }
                eventList = (EventList) pim.openPIMList(PIM.EVENT_LIST, PIM.READ_ONLY, listNames[0]);
//                Enumeration etest = eventList.items(EventList.OCCURRING, startDate, endDate, false); //false= initialEventOnly
                Enumeration e = eventList.items(EventList.OCCURRING, startDate, endDate, false); //false= initialEventOnly
//#mdebug
                Log.l("PIM_Todo.impportPIMEvents: eventList"+eventList+" Enumeration e = "+e.hasMoreElements());
//#enddebug
                for (; e.hasMoreElements();) {
                    Event event = (Event) e.nextElement();
                    WorkSlot workSlot = copyEventToSlot(eventList, event);
                    workSlot.setImportedFromPIM(true);
//#mdebug
                    Log.l("PIM_Todo.impportPIMEvents: importing "+event+" workSlot="+workSlot);
//#enddebug
//                    v.addElement(copyEventToSlot(eventList, (Event)e.nextElement()));
//                    slotList.addSlot(copyEventToSlot(eventList, (Event) e.nextElement()));
                    if (sortingComparator != null) {
                        slotList.insertSortedIntoSortedList(workSlot, sortingComparator); //
                    } else {
                        slotList.addItem(workSlot); //
                    }
                }
                eventList.close();
            } catch (PIMException ex) {
                ex.printStackTrace();
            }
        } else {
//#mdebug
            Log.l("No Event list found");
//#enddebug
        }
        return slotList;
    }

    public static ItemList importPIMEvents(long startDate, long endDate) {
        return importPIMEvents(startDate, endDate, null);
    }


    /**
     * imports ToDo events from the phone's PIM. NOTE that items can only be selected by *either* start/end date, OR by importDoneItems, priority, myClassAccessibilityPimValue and onlyCategories.
     * It is not currently possible to combine
     *
     * @param addImportedTodosToList list to which imported items are added (kept as an argument to make it easy to import to a given, existing, list, or even Category)
     * @param importDoneItems 0=all, 1=only open, 2=only completed
     * @param byCompletedDate
     * @param startDate if different from 0L, indicates the
     * @param endDate
     * @param priority (end-user visible value, NOT PIM values 0-9, since it is converted internally)
     * @param myClassAccessibilityPimValue
     * @return
     */
    public static int importPIMToDos(ItemList addImportedTodosToList, int importDoneItems, boolean byCompletedDate, long startDate, long endDate, int priority, int myClassAccessibilityPimValue, ItemList onlyCategories) {
        ToDoList todoList = null;
        PIM pim = PIM.getInstance();
        String listNames[] = pim.listPIMLists(PIM.TODO_LIST);
        int listIdx = 0;
        int impCount = 0;
        if (listNames.length > 0) {
            try {
                if (listNames.length > 1) {
//                        Dialog.show(null, null, null, null); //TODO!!!! define Dialog that allows user to
                    listIdx = 0; //TODO!!!!: let user select list and save in settings
                }
                todoList = (ToDoList) pim.openPIMList(PIM.TODO_LIST, PIM.READ_ONLY, listNames[listIdx]);
                int dateField = byCompletedDate ? ToDo.COMPLETION_DATE : ToDo.DUE;
                ToDo matchTodo = todoList.createToDo();

                if (startDate != 0L && endDate == 0L) {
                    endDate = Long.MAX_VALUE; //if startDate specified, but no endDate, use max possible endDate
                }

                if (priority != -1) {
                    if (todoList.isSupportedField(ToDo.PRIORITY)) {
                        matchTodo.addInt(ToDo.PRIORITY, PIMItem.ATTR_NONE, Item.toPIMPriority(priority));
                    }
                }

                if (myClassAccessibilityPimValue != Item.CLASS_NONE_DEFINED) {
                    if (todoList.isSupportedField(ToDo.CLASS)) {
                        matchTodo.addInt(ToDo.CLASS, PIMItem.ATTR_NONE, Item.toClassAccessibilityPimValue(myClassAccessibilityPimValue));
                    }
                }

                if (onlyCategories != null) {
                    String[] categories = todoList.getCategories(); //get all defined categories from PIM
                    Object cat;
                    for (int i = 0, size = categories.length; i < size; i++) { //optimization: instead of iterating over categories, iterate over onlyCategories instead
                        cat = onlyCategories.findItemWithText(categories[i], true, false); //find i
//                            cat = (Category) onlyCategories.getItemAt(i);
                        if (cat != null) {
                            matchTodo.addToCategory(categories[i]);
                        }
                    }
                }

                Enumeration e;
                if (startDate != 0L || endDate != 0L) {
                    e = todoList.items(dateField, startDate, endDate); //false= initialEventOnly
                } else {
                    e = todoList.items(matchTodo); //select only items that match the properties set for matchTodo
                }
                for (; e.hasMoreElements();) {
//                    v.addElement(copyEventToSlot(eventList, (Event)e.nextElement()));
//                        slotList.addSlot(copyEventToSlot(eventList, (Event) e.nextElement()));
                    addImportedTodosToList.addItem(copyPimToItem(todoList, (ToDo) e.nextElement()));
                    impCount++;
                }
                todoList.close();
            } catch (PIMException ex) {
                ex.printStackTrace();
            }
        } else {
            Log.l("No Event list found");
        }
//        return addImportedTodosToList.getSize();
        return impCount;
    }

//    void instantiateRepeatRule(long startDate, long endDate) {
//    }

    /**
     *
     * @param exportList
     * @param exportDoneItems
     * @param categoriesCaseSensitive
     * @return
     */
    public static int exportPIMToDos(ItemList exportList, boolean categoriesCaseSensitive) {
        ToDoList todoList = null;
        PIM pim = PIM.getInstance();
        String listNames[] = pim.listPIMLists(PIM.TODO_LIST);
        int expCount = 0;
        int listIdx = 0;
        if (listNames.length > 0) {
            try {
                if (listNames.length > 1) {
                    //                        Dialog.show(null, null, null, null); //TODO!!!! define Dialog that allows user to
                    listIdx = 0; //TODO!!!!: let user select list and save in settings
                }
                todoList = (ToDoList) pim.openPIMList(PIM.TODO_LIST, PIM.WRITE_ONLY, listNames[listIdx]);
                if (todoList.maxCategories() == 0) {
                    Dialog.show("Your phone does not support categories", "Categories will be ignored**", "OK", "OK");
                } else if (todoList.maxCategories() != -1) {
                    Dialog.show("Your phone only supports " + todoList.maxCategories() + " categories", "If you have used more categories they will be ignored**", "OK", "OK");
                }
                for (int i = 0, size = exportList.getSize(); i < size; i++) {
                    try {
                        Object obj = exportList.getItemAt(i);
                        if (obj instanceof Item) {
                            Item item = (Item) obj;
                            ToDo todo = todoList.createToDo();
                            copyItemToPimTodoInstance(todoList, (Item) exportList.getItemAt(i), todo);
                            todo.commit();
                            expCount++;
                        }
                    } catch (PIMException ex) {
                        ex.printStackTrace();
                    }
                }
                todoList.close();
            } catch (PIMException ex) {
                ex.printStackTrace();
            }
        }
//        try {
//            throw new Exception("exportPIMToDos not defined");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        return expCount;
    }
}
