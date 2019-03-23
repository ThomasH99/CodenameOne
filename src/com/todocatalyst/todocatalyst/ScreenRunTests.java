package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;

/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenRunTests extends MyForm {

//    private final static String CURRENT_USER_STORAGE_ID = "parseCurrentUser";
//    MyForm mainScreen;
    public final static String SCREEN_TITLE = "Internal/Repair";

    ScreenRunTests(MyForm mainScreen) { // throws ParseException, IOException {
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
        super.refreshAfterEdit();
    }

    public void addCommandsToToolbar() {
        Toolbar toolbar = getToolbar();
        //DONE/BACK
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());

//        toolbar.addCommandToOverflowMenu(makeCancelCommand());
//        toolbar.addCommandToOverflowMenu(new Command("Reset to default")); //reset to default values
    }

    ///////////////////////////////////////////////////////////
    public void setInsertItemValues(Object obj, Object sortField, Object objBefore, Object objAfter) {//, getValueFunction, makeNewValueFunction) {
        if (obj instanceof Item) {
            Item item = (Item) obj;

        } else if (obj instanceof WorkSlot) {
        } else if (obj instanceof Category) {
        } else if (obj instanceof ItemList) {

        }
    }

    //////////////////////////////////////////////////////////
    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    private Container buildContentPane(Container content) {
//        Container content = new Container();
        content.setScrollableY(true);
        TableLayout tl;
        if (Display.getInstance().isTablet() || !Display.getInstance().isPortrait()) {
            tl = new TableLayout(7, 2);
        } else {
            tl = new TableLayout(14, 1);
        }
        tl.setGrowHorizontally(true);
        content.setLayout(tl);

        content.add(new Button(Command.create("Refresh cache", null, (e) -> {
            DAO.getInstance().resetAndDeleteAndReloadAllCachedData();
            Dialog.show("Info", "Finished updating cache", "OK", null);
        })));

        content.add(new Button(new Command("Repair list of Categories") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DAO.getInstance().setExecuteCleanup(true);
                DAO.getInstance().cleanUpAllCategoriesFromParse();
                DAO.getInstance().setExecuteCleanup(false);
            }
        }));

        content.add(new Button(new Command("Repair list of ItemLists") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                DAO.getInstance().setExecuteCleanup(true);
                DAO.getInstance().cleanUpAllItemListsInParse();
                DAO.getInstance().setExecuteCleanup(false);
            }
        }));

        content.add(new Button(Command.create("Show all data inconsistencies", null, (e) -> {
            if (Dialog.show("INFO", "This will report all data inconsistencies and send them in a log file", "OK", "Cancel")) {
                DAO.getInstance().cleanUpAllBadObjectReferences(false);
                Log.sendLog();
            }
        })));

        content.add(
                new Button(Command.create("Clean up all data inconsistencies", null, (e) -> {
                    if (Dialog.show("WARNING", "This will log AND repair all data inconsistencies", "OK", "Cancel")) {
                        DAO.getInstance().cleanUpAllBadObjectReferences(true);
                        Log.sendLog();
                    }
                })));
        
        return content;
    }
}
