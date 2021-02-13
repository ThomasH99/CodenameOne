package com.todocatalyst.todocat;

//import com.codename1.io.Log;
import com.codename1.io.Log;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Button;
import com.codename1.ui.TextArea;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.Resources;
import com.parse4cn1.ParseException;
import java.io.IOException;
import java.util.Map;
//import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.spinner.Picker;
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
public class ScreenItem extends MyForm {

//    static Map<String, GetParseValue> parseIdMap = new HashMap<String, GetParseValue>() ;
     Map<Object, UpdateField> parseIdMap2 = new HashMap<Object, UpdateField>();
     MyForm previousForm;
     Item item;

    ScreenItem(Item item, MyForm previousForm) { //throws ParseException, IOException {
        super("Main Form");
//        ScreenItemP.item = item;
        this.item = item;
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

    public  void addCommandsToToolbar(Toolbar toolbar, Resources theme) {

//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle());
//        toolbar.addCommandToLeftBar("Done", icon, (e) -> Log.p("Clicked"));
//        Command.create(name, icon, ev);
//        Command cmd = Command.create("Done", icon, (e) -> {
        Command cmd = Command.create("", icon, (e) -> {
//            Log.p("Clicked");
////            putEditedValues(parseIdMap);
            putEditedValues2(parseIdMap2);
            try {
                item.save();
            } catch (ParseException ex) {
//                Logger.getLogger(ScreenItemP.class.getName()).log(Level.SEVERE, null, ex);
                Log.p(ScreenItem.class.getName(), Log.ERROR); //TODO: add dialog/popup info when save does not succeed
            }
//            previousForm.showBack();
            previousForm.refreshAfterEdit();
            previousForm.showBack();
        });
//        cmd.putClientProperty("android:showAsAction", "withText");
        toolbar.addCommandToLeftBar(cmd);

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens))
//        toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> {
//            Log.p("Clicked");
////            item.revert(); //forgetChanges***/refresh
////            previousForm.showBack(); //drop any changes
//            previousForm.revalidate();
//            previousForm.showBack(); //drop any changes
//        });
        toolbar.addCommandToOverflowMenu(makeCancelCommand());
        
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
        int nbFields = 10;
        if (Display.getInstance().isTablet()) {
            tl = new TableLayout(nbFields, 2);
        } else {
            tl = new TableLayout(nbFields * 2, 1);
//            spanButton = 1;
        }
        tl.setGrowHorizontally(true);
        content.setLayout(tl);

//        MyTextField(String title, String hint, int columns, int constraint, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
        MyTextField description = new MyTextField("Task description", 20, TextArea.ANY, parseIdMap2, () -> item.getText(), (s) -> item.setText(s));
        MyCheckBox status = new MyCheckBox(null, parseIdMap2, () -> item.isDone(), (b) -> item.setDone(b));
        Container taskCont = new Container(new BoxLayout(BoxLayout.X_AXIS));
        taskCont.add(status).add(description);
        content.add(new Label("Task")).add(taskCont);

//        MyTextField comment = new MyTextField("Details", "Comments", 20, TextArea.ANY, parseIdMap, item, Item.PARSE_COMMENT);
        MyTextField comment = new MyTextField("Notes", 20, TextArea.ANY, parseIdMap2, () -> item.getComment(), (s) -> item.setComment(s));
        content.add(new Label("Details")).add(comment);

//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<click to set an alarm>", parseIdMap, item, Item.PARSE_ALARM_DATE);
        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<click to set an alarm>", parseIdMap2, () -> item.getAlarmDateD(), (d) -> item.setAlarmDateD(d));
        content.add(new Label("Alarm")).add(alarmDate);

        MyDatePicker dueDate = new MyDatePicker("<click to set a due date>", parseIdMap2, () -> item.getDueDateD(), (d) -> item.setDueDateD(d));
        content.add(new Label("Due")).add(dueDate);

        MyTimePicker effortEstimate = new MyTimePicker(parseIdMap2, () -> (int) item.getEffortEstimate(), (i) -> item.setEffortEstimate((int) i));
        content.add(new Label("Estimate")).add(effortEstimate);

        MyStringPicker priority = new MyStringPicker(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}, parseIdMap2, () -> item.getPriority(), (i) -> item.setPriority(i));
        content.add(new Label("Priority")).add(priority);

        MyOnOffSwitch interruptTask = new MyOnOffSwitch(parseIdMap2, () -> item.isInteruptTask(), (b) -> item.setInteruptTask(b));
        content.add(new Label("Interrupt task")).add(interruptTask);

        MyComponentGroup dreadFun = new MyComponentGroup(Item.getDreadFunStringArray(), parseIdMap2, () -> item.getDreadFunValue(), (i) -> item.setDreadFunValue(i));
        content.add(new Label("Fun or Dread")).add(dreadFun);

        Button categories = new Button(new Command(item.getCategories().toString()) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                new ScreenCategoryPicker(DAO.getInstance().getCategories(), item.getCategories(), ScreenItem.this).show();
                new ScreenCategoryPicker(DAO.getInstance().getCategories(), item, ScreenItem.this).show();
            }
        });
        categories.setUIID("Label");
        content.add(new Label("Categories")).add(categories);

        Button subtasks = new Button(new Command("Eidt "+item.getItemList().getSize()+" subtasks") {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                new ScreenCategoryPicker(DAO.getInstance().getCategories(), item.getCategories(), ScreenItem.this).show();
                new ScreenItemList("Subtasks for \""+item.getText(), item.getItemList(), ScreenItem.this, ScreenItemList.ITEM_TYPE_ITEM).show();
            }
        });
        subtasks.setUIID("Label");
        content.add(new Label("Subtasks")).add(subtasks);

//        TableLayout.Constraint cn = tl.createConstraint();
//        cn.setHorizontalSpan(spanButton);
//        cn.setHorizontalAlign(Component.RIGHT);
        return content;
    }
    
    @Override
    void refreshAfterEdit() {
        getContentPane().removeAll(); //clear old content pane
       buildContentPane(getContentPane()); //rebuild and refresh
       revalidate(); //refresh form
//       super();
    }
}
