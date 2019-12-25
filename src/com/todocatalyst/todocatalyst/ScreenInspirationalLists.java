package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;

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
        super(SCREEN_TITLE, mainScreen, () -> {
        });
//        this.previousForm = mainScreen;
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
//        restoreKeepPos();
        super.refreshAfterEdit();
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
        //the highest value wrt remaining time (you 'earn' all the value by just finishing what is missing)
        content.add(new Button(
                MyReplayCommand.create(InspirationalLists.ROIoverRemaining_X.getCmdUniqueId(), InspirationalLists.ROIoverRemaining_X.getDefinition(), Icons.iconInspiration, (e) -> {
                    MyForm myForm = new ScreenListOfItems(InspirationalLists.ROIoverRemaining_X.getFilterName(), "No tasks",
                            () -> new ItemList(SCREEN_ALL_TASKS_TITLE, DAO.getInstance().getAllItems(), InspirationalLists.ROIoverRemaining_X, true),
                            ScreenInspirationalLists.this, (i) -> {
                            },
                            ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                            | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_WORK_TIME
                    );
                    myForm.setTextToShowIfEmptyList("No tasks created the last month");
                    myForm.show();
                })));

        content.add(new Button(
                MyReplayCommand.create(InspirationalLists.UrgentByCreated.getCmdUniqueId(), InspirationalLists.UrgentByCreated.getDefinition(), Icons.iconInspiration, (e) -> {
                    MyForm myForm = new ScreenListOfItems(InspirationalLists.UrgentByCreated.getFilterName(), "No tasks",
                            () -> new ItemList(SCREEN_ALL_TASKS_TITLE, DAO.getInstance().getAllItems(), InspirationalLists.UrgentByCreated, true),
                            ScreenInspirationalLists.this, (i) -> {
                            },
                            ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                            | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_WORK_TIME
                    );
                    myForm.setTextToShowIfEmptyList("No tasks created the last month");
                    myForm.show();
                })));

        content.add(new Button(MyReplayCommand.create("What's urgent")));  //Urgent tasks, sorted by due date(?)
        content.add(new Button(MyReplayCommand.create("What's important and forgotten")));  //Important tasks not touched/worked on since a long time
        content.add(new Button(MyReplayCommand.create("Warm up")));  //quick easy tasks to get that dopamine flowing
        content.add(new Button(MyReplayCommand.create("What never gets done: Important, not Urgent and Challenging or time consuming")));  //take on challenging/difficult, dreaded tasks, important

        content.add(new Button(MyReplayCommand.create("Just that last little bit - started tasks that are almost done")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Longest duration between creation and start work or complete (procrastinating what type of tasks?)")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Tasks in progress but without any actual time recorded")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Really that urgent?")));  //tasks marked Urgent that are not done after e.g. 7 days after Due Date
        content.add(new Button(MyReplayCommand.create("Get that almost finished project done completely")));  //projects where most tasks are done (high ratio done - not done)
        content.add(new Button(MyReplayCommand.create("On top of the mountain")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Should have been postponed?")));  //tasks marked were marked Import=Low but still done quickly after creation?
        content.add(new Button(MyReplayCommand.create("Maybe start focusing on what's Important?")));  //tasks marked were marked Import=High but still done/started after eg 30 days?
        content.add(new Button(MyReplayCommand.create("What never gets done")));  //Important but not urgent, sorted by ROI
        content.add(new Button(MyReplayCommand.create("Quickies")));  //less than 2 minutes
        content.add(new Button(MyReplayCommand.create("Fits a timeslot")));  //most important tasks I have the time to do in a certain time (e.g. I have just 1 hour)
        content.add(new Button(MyReplayCommand.create("Here and Now")));  //due date today, waiting until today, starred
        content.add(new Button(MyReplayCommand.create("Waited the longest time")));  //by due date
        content.add(new Button(MyReplayCommand.create("Deadline coming up")));  //by due date
        content.add(new Button(MyReplayCommand.create("Fast and easy")));  //low challenge, low estimate
        content.add(new Button(MyReplayCommand.create("Almost finished"))); //lowest percentage remaining compared to total effort (actual+remaining)
        content.add(new Button(MyReplayCommand.create("Instance gratification")));
        content.add(new Button(MyReplayCommand.create("High value for the effort"))); //highest total value divided by remaining effort
        content.add(new Button(MyReplayCommand.create("Important and Urgent")));
        content.add(new Button(MyReplayCommand.create("Important and Fast"))); //sort by lowest effort
        content.add(new Button(MyReplayCommand.create("Urgent")));
        content.add(new Button(MyReplayCommand.create("Dusty/Moldy/Clean up"))); //oldest created tasks still not started
        content.add(new Button(MyReplayCommand.create("Stalled"))); //still incomplete tasks by age of 'last worked on' (last update or last time timer was used or actuals updated)
        content.add(new Button(MyReplayCommand.create("Better start early")));  //projects that it might be a good idea to look at early (e.g. large effort, challenging, due date approaching)
        content.add(new Button(MyReplayCommand.create("Lazy day")));  //??easy and fun
//        content.add(new Button(Command.create("Show items in another list than the item's ownerList", null, (e)->{}))); 

        return content;
    }
}
