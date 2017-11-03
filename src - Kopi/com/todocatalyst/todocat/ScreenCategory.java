package com.todocatalyst.todocat;

//import com.codename1.io.Log;

import com.codename1.io.Log;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.Resources;
import com.parse4cn1.ParseException;
import java.util.Map;
//import com.codename1.ui.*;
import com.codename1.ui.table.TableLayout;
import java.util.HashMap;

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
public class ScreenCategory extends MyForm {

//    static Map<String, GetParseValue> parseIdMap = new HashMap<String, GetParseValue>() ;
     Map<Object, UpdateField> parseIdMap2 = new HashMap<Object, UpdateField>() ;
     Form previousForm;
     Category category;
    
    ScreenCategory(Category category, Form previousForm) { //throws ParseException, IOException {
        super("New Category");
//        ScreenItemP.item = item;
        this.category = category;
//        ScreenItemP.previousForm = previousForm;
        this.previousForm = previousForm;
        // we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
//        form = this;
        // we use border layout so the list will take up all the available space
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setToolbar(new Toolbar());
        addCommandsToToolbar(getToolbar(), theme);
        buildContentPane(getContentPane());
    }

    public void addCommandsToToolbar(Toolbar toolbar, Resources theme) {

//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD, toolbar.getStyle());
//        toolbar.addCommandToLeftBar("Done", icon, (e) -> Log.p("Clicked"));
//        Command.create(name, icon, ev);
//        Command cmd = Command.create("Done", icon, (e) -> {
        Command cmd = Command.create("Done", null, (e) -> {
//            Log.p("Clicked");
////            putEditedValues(parseIdMap);
            putEditedValues2(parseIdMap2);
            try {
                category.save();
            } catch (ParseException ex) {
//                Logger.getLogger(ScreenItemP.class.getName()).log(Level.SEVERE, null, ex);
                Log.p(ScreenCategory.class.getName(), Log.ERROR); //TODO: add dialog/popup info when save does not succeed
            }
            previousForm.revalidate();
//            previousForm.showBack();
            previousForm.show();
        });
        cmd.putClientProperty("android:showAsAction", "withText");
        toolbar.addCommandToLeftBar(cmd);

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens))
        toolbar.addCommandToRightBar("Cancel", null, (e) -> { //TODO!! replace with 
            Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
            previousForm.revalidate();
            previousForm.show(); //drop any changes
        });
        toolbar.addCommandToRightBar(makeCancelCommand());
//        toolbar.addCommandToSideMenu("New Task", icon, (e) -> {
//            Log.p("Clicked");
//            try {
//                new ScreenItemP(new Item(), form).getForm().show(); //edit new Item
//            } catch (ParseException | IOException ex) {
//                Log.p(ScreenItemP.class.getName()+ex, Log.ERROR );
//            }
//        });
//        toolbar.addCommandToOverflowMenu("Overflow", icon, (e) -> Log.p("Clicked"));
//        toolbar.addCommandToRightBar(new Command("", theme.getImage("synch.png")) {
//
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                Display.getInstance().callSerially(new Runnable() {
//
//                    public void run() {
////                                updateScreenFromNetwork(cats, "cat");
////                                cats.revalidate();
//                    }
//                });
//            }
//        });
    }

    /**
     * This method shows the main user interface of the app
     *
     * @param back indicates if we are currently going back to the main form
     * which will display it with a back transition
     * @param errorMessage an error message in case we are returning from a
     * search error
     * @param listings the listing of alternate spellings in case there was an
     * error on the server that wants us to prompt the user for different
     * spellings
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    private Container buildContentPane(Container content) {
//        Container content = new Container();
        TableLayout tl;
//        int spanButton = 2;
        int nbFields=8;
        if (Display.getInstance().isTablet()) {
            tl = new TableLayout(nbFields, 2);
        } else {
            tl = new TableLayout(nbFields*2, 1);
//            spanButton = 1;
        }
        tl.setGrowHorizontally(true);
        content.setLayout(tl);

        MyTextField categoryName = new MyTextField("Task description", parseIdMap2, ()->category.getText(), (s)->category.setText(s));
        content.add(new Label("Category")).add(categoryName);

        MyTextField description  = new MyTextField("Description", parseIdMap2, ()->category.getComment(), (s)->category.setComment(s));
        content.add(new Label("Description")).add(description);

        return content;
    }
}