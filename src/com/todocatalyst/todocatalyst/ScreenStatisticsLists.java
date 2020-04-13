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
public class ScreenStatisticsLists extends MyForm {
    //TODO store login+password locally (or at least user key?)
    //TODO Settings option to log out
    //DONE support option to update email (and change login id since email is normally used)
    //DONE skip login screen when already logged in

    public final static String SCREEN_TITLE = "Statistics"; // "Sharpen the saw (statistics)"
    
    
//    MyForm mainScreen;
    ScreenStatisticsLists(MyForm mainScreen) { // throws ParseException, IOException {
        super(SCREEN_TITLE, mainScreen, () -> {
        });
//        this.previousForm = mainScreen;
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        addCommandsToToolbar();
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        restoreKeepPos();
                super.refreshAfterEdit();

    }

    private String CURRENT_USER_STORAGE_ID = "parseCurrentUser";

    public void addCommandsToToolbar() {
        Toolbar toolbar = getToolbar();
        //DONE/BACK
//        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand());
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
        addStandardBackCommand();

//        toolbar.addCommandToOverflowMenu(makeCancelCommand());

//        toolbar.addCommandToOverflowMenu(new Command("Reset to default")); //reset to default values
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    private Container buildContentPane(Container content) {
        parseIdMap2.parseIdMapReset();
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
        content.add(new Button( MyReplayCommand.create("Cancelled tasks with time recorded")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Most interrupted tasks")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Days/times with most interrupts")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Over estimated tasks")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Under estimated tasks")));  //take on challenging/difficult, dreaded tasks, important
        content.add(new Button(MyReplayCommand.create("Big difference between estimates of Done Projects and subtasks' actual**")));  //Badly estimated projects: for Done Projects, with estimates at Project level, which have the big difference with sum of actuals of subtasks?

        return content;
    }
}
