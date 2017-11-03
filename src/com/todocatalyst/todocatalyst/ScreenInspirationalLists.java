package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
import java.util.List;

//import com.codename1.components.SpanLabel;
//import com.codename1.io.Log;
//import com.codename1.ui.Button;
//import com.codename1.ui.Command;
//import com.codename1.ui.Container;
//import com.codename1.ui.Display;
//import com.codename1.ui.Form;
//import com.codename1.ui.Label;
//import com.codename1.ui.Toolbar;
//import com.codename1.ui.layouts.BoxLayout;
//import com.parse4cn1.ParseException;
////import com.codename1.ui.*;
//import com.codename1.ui.events.ActionEvent;
//import com.codename1.ui.table.TableLayout;
//import com.todocatalyst.todocatalyst.MyForm;
//import java.util.List;

//import com.codename1.ui.*;
//import com.codename1.ui.events.ActionEvent;
//import com.codename1.ui.layouts.BoxLayout;
//import com.codename1.ui.table.TableLayout;
//import com.codename1.ui.util.Resources;
//import com.parse4cn1.ParseException;
//import java.io.IOException;
//import java.util.Map;
/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenInspirationalLists extends MyForm {
    //TODO store login+password locally (or at least user key?)
    //TODO Settings option to log out
    //DONE support option to update email (and change login id since email is normally used)
    //DONE skip login screen when already logged in
    
   public final static String SCREEN_TITLE = "Inspirational lists";

//    MyForm mainScreen;
    ScreenInspirationalLists(MyForm mainScreen) { // throws ParseException, IOException {
        super(SCREEN_TITLE, null, () -> {
        });
        this.previousForm = mainScreen;
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);
        addCommandsToToolbar();
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        restoreKeepPos();

    }

    private String CURRENT_USER_STORAGE_ID = "parseCurrentUser";

    public void addCommandsToToolbar() {
        Toolbar toolbar = getToolbar();
        //DONE/BACK
//        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand());
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());

//        toolbar.addCommandToOverflowMenu(makeCancelCommand());

//        toolbar.addCommandToOverflowMenu(new Command("Reset to default")); //reset to default values
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    private Container buildContentPane(Container content) {
//        Container content = new Container();
        TableLayout tl;
        if (Display.getInstance().isTablet() || !Display.getInstance().isPortrait()) {
            tl = new TableLayout(7, 2);
        } else {
            tl = new TableLayout(14, 1);
        }
        tl.setGrowHorizontally(true);
        content.setLayout(tl);

//        Button doubleOwnerButton = new Button(new Command("Showtasks belonging to more than one list") {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                List<Item> list = DAO.getInstance().debugGetItemsInMultipleLists();
//                Form f = new Form("Select");
//                for (Item item:list) {
//                    f.add(new Button(Command.create(item.getText(), null, (e)->{})));
//                }
//            }
//        });
//        content.add(doubleOwnerButton);


        //TODO!!! Add an explanation to each of these (definition + why/when work on it)
        //TODO!! check old filters from first version of TodoCatalyst
        content.add(new Button(new MyReplayCommand("Longest duration between creation and start work or complete (procrastinating what type of tasks?)")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(new MyReplayCommand("On top of the mountain")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(new MyReplayCommand("What never gets done")));  //Important but not urgent, sorted by ROI
        content.add(new Button(new MyReplayCommand("Warm up")));  //getting started when 
        content.add(new Button(new MyReplayCommand("Quickies")));  //less than 2 minutes
        content.add(new Button(new MyReplayCommand("Fits a timeslot")));  //most important tasks I have the time to do in a certain time (e.g. I have just 1 hour)
        content.add(new Button(new MyReplayCommand("Here and Now")));  //due date today, waiting until today, starred
        content.add(new Button(new MyReplayCommand("Waited the longest time")));  //by due date
        content.add(new Button(new MyReplayCommand("Deadline coming up")));  //by due date
        content.add(new Button(new MyReplayCommand("Fast and easy")));  //low challenge, low estimate
        content.add(new Button(new MyReplayCommand("Almost finished"))); //lowest percentage remaining compared to total effort (actual+remaining)
        content.add(new Button(new MyReplayCommand("Instance gratification")));  
        content.add(new Button(new MyReplayCommand("High value for the effort"))); //highest total value divided by remaining effort
        content.add(new Button(new MyReplayCommand("Important and Urgent"))); 
        content.add(new Button(new MyReplayCommand("Important and Fast"))); //sort by lowest effort
        content.add(new Button(new MyReplayCommand("Urgent"))); 
        content.add(new Button(new MyReplayCommand("Dusty/Moldy/Clean up"))); //oldest created tasks still not started
        content.add(new Button(new MyReplayCommand("Stalled"))); //still incomplete tasks by age of 'last worked on' (last update or last time timer was used or actuals updated)
        content.add(new Button(new MyReplayCommand("Better start early")));  //projects that it might be a good idea to look at early (e.g. large effort, challenging, due date approaching)
        content.add(new Button(new MyReplayCommand("Lazy day")));  //??
//        content.add(new Button(Command.create("Show items in another list than the item's ownerList", null, (e)->{}))); 

        return content;
    }
}
