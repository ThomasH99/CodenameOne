/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;
import java.io.IOException;

/**
 * encapsulates the problem of getting the right icons, e.g. right resolution
 * depending on screen resolution and other stuff. E.g. set screen resolution,
 * and add a postfix to icon ids to get the right resolution.
 *
 * @author Thomas
 */
public class IconServer {

//    final static int CREATED = Item.ItemStatus.valueOf("STATUS_CREATED"); //1; 
    final static int CREATED = 1; //Item.ItemStatus.STATUS_CREATED; //1; 
    final static int DONE = 2; //Item.STATUS_DONE;
    final static int ONGOING = 3; //Item.STATUS_ONGOING;
    final static int WAITING = 4; //Item.STATUS_WAITING;
    final static int CANCELLED = 5; //Item.STATUS_CANCELLED;
    final static int UNSELECTED = 10;
    final static int SELECTED = 11;
    final static int TASK_DETAILS_TAB = 12;
    final static int TASK_WORK_TAB = 13;
    final static int TASK_HIST_TAB = 14;
    final static int TASK_TIME_TAB = 15;
    final static int TASK_PRIO_TAB = 16;
    final static int ADD = 17;
    final static int SEARCH = 18;
    final static int EDITWORKSLOTS = 19;
    final static int VIEW = 20;
    final static int MOVE = 21;
    final static int HELP = 22;
    final static int SETTINGS = 23;
    final static int INTERRUPTTASK = 24;
    final static int TIMER = 25;
    final static int LOGBOOK = 26;
    final static int DIARY = 27;
    final static int TEMPLATES = 28;
    final static int LISTS = 29;
    final static int CATEGORIES = 30;
    final static int INBOX = 31;
    final static int ALLITEMS = 32;
    final static int PROJECTS = 33;
    final static int EDIT = 34;
    final static int EXPAND_COLLAPSE = 35;

//    static Resources r = Resources.open(Settings.getInstance().getThemesFilename());
    IconServer() {
//        try {
//            r = Resources.open(Settings.getInstance().getThemesFilename());
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    static Image getIcon(int id) {
        String idconId = "undefined.png";
        try {
            //        return r.getImage(id+".png");
            switch (id) {
                case CREATED:
                    idconId = "created.svg";
                    break;
                case DONE:
//                    idconId = "done_sketch.svg";
                    idconId = "done.svg";
                    break;
                case CANCELLED:
                    idconId = "cancelled.svg";
                    break;
                case WAITING:
                    idconId = "waiting.svg";
                    break;
                case ONGOING:
                    idconId = "ongoing.svg";
                    break;
                case SELECTED:
                    idconId = "selected.svg";
                    break;
                case UNSELECTED:
                    idconId = "unselected.svg";
                    break;
                case TASK_DETAILS_TAB:
                    idconId = "taskdetailstab.svg";
                    break;
                case TASK_WORK_TAB:
                    idconId = "taskworktab.svg";
                    break;
                case TASK_HIST_TAB:
                    idconId = "taskhisttab.svg";
                    break;
                case TASK_PRIO_TAB:
                    idconId = "taskpriotab.svg";
                    break;
                case TASK_TIME_TAB:
                    idconId = "tasktimetab.svg";
                    break;
                case ADD:
                    idconId = "add.svg";
                    break;
                case SEARCH:
                    idconId = "search.svg";
                    break;
                case EDITWORKSLOTS:
                    idconId = "workingtime.svg";
                    break;
                case VIEW:
                    idconId = "view.svg";
                    break;
                case MOVE:
                    idconId = "movemode.svg";
                    break;
                case HELP:
                    idconId = "help.svg";
                    break;
                case SETTINGS:
                    idconId = "settings.svg";
                    break;
                case INTERRUPTTASK:
                    idconId = "interrupttask.svg";
                    break;
                case TIMER:
                    idconId = "timer.svg";
                    break;
                case LOGBOOK:
                    idconId = "logbook.svg";
                    break;
                case DIARY:
                    idconId = "diary.svg";
                    break;
                case TEMPLATES:
                    idconId = "template.svg";
                    break;
                case LISTS:
                    idconId = "lists.svg";
                    break;
                case CATEGORIES:
                    idconId = "categories.svg";
                    break;
                case INBOX:
                    idconId = "inbox.svg";
                    break;
                case ALLITEMS:
                    idconId = "allitems.svg";
                    break;
                case PROJECTS:
                    idconId = "projects.svg";
                    break;
                case EDIT:
                    idconId = "edit.svg";
                    break;
                case EXPAND_COLLAPSE:
                    idconId = "expandlist.svg";
                    break;
            }
//        return Resources.open(Settings.getInstance().getThemesFilename()).Image(idconId); //optimization!!!!
            Image image = Resources.open(Settings.getInstance().getThemesFilename()).getImage(idconId); //optimization!!!!
//            Image image2 = Resources.open(Settings.getInstance().getThemesFilename()).getImage("add.svg"); //optimization!!!!
//            image.getGraphics().drawImage(image2, 5, 5);
//            image.getGraphics().drawRect(0, 0, 10, 10);
//            image.getGraphics().drawRect(0, 0, 20, 20);
//            image.getGraphics().drawRect(-10, -10, 20, 20);
//            image.getGraphics().drawRect(-10, 10, 20, 20);
//            image.getGraphics().drawRect(10, -10, 20, 20);
//            Log.l("Image.isSVGSupported()= " + Image.isSVGSupported());
//        if (Image.isSVGSupported()) {
//            Image.createSVG(idconId, true, data);
//        }
            return image;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static Image getIcon(String id) {
        try {
            //        return r.getImage(id+".png");
            return Resources.open(Settings.getInstance().getThemesFilename()).getImage(id + ".png"); //optimization!!!!
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static Image getIconFullName(String id) {
        try {
            //        return r.getImage(id+".png");
            return Resources.open(Settings.getInstance().getThemesFilename()).getImage(id); //optimization!!!!
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
