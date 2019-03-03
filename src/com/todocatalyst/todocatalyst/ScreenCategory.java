package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
//import com.codename1.ui.*;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.Validator;

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
    Category category;
    private static String screenTitle = "Category";
    private Button backButton;
    private Command backCommand;
// protected static String FORM_UNIQUE_ID = "ScreenEditCategory"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
//     private UpdateField updateActionOnDone;

//    ScreenCategory(Category category, MyForm previousForm) { //throws ParseException, IOException {
//        this(category, previousForm, () -> {
//        });
//    }
    ScreenCategory(Category category, MyForm previousForm, UpdateField doneAction) { //throws ParseException, IOException {
        super(category.getText(), previousForm, doneAction);
        setUniqueFormId("ScreenEditCategory");
//        ScreenItemP.item = item;
        this.category = category;
//        ScreenItemP.previousForm = previousForm;
//        this.previousForm = previousForm;
//        this.updateActionOnDone = doneAction;
        // we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
//        form = this;
        // we use border layout so the list will take up all the available space
//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        setToolbar(new Toolbar());
//        setTitle(screenTitle);

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers

        addCommandsToToolbar(getToolbar());//, theme);
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        restoreKeepPos();
        super.refreshAfterEdit();
    }

    public void addCommandsToToolbar(Toolbar toolbar) { //, Resources theme) {

//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle());
//        toolbar.addCommandToLeftBar("Done", icon, (e) -> Log.p("Clicked"));
//        Command.create(name, icon, ev);
//        Command cmd = Command.create("Done", icon, (e) -> {
//        Command cmd = Command.create("", iconDone, (e) -> {
//            putEditedValues2(parseIdMap2);
//            updateActionOnDone.update();
//            previousForm.refreshAfterEdit();
//            previousForm.revalidate();
////            previousForm.showBack();
//            previousForm.show();
//        });
//        cmd.putClientProperty("android:showAsAction", "withText");
//        Command backCmd = makeDoneUpdateWithParseIdMapCommand();
//        backButton = new Button(backCmd);
//        toolbar.addCommandToLeftBar(backCmd);
        toolbar.setBackCommand(backCommand = makeDoneUpdateWithParseIdMapCommand());

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> {
//            Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
//            previousForm.revalidate();

//            previousForm.show(); //drop any changes
//                showPreviousScreenOrDefault(previousForm, false);
                showPreviousScreenOrDefault(false);
            });
        }

        //DELETE
        toolbar.addCommandToOverflowMenu("Delete", null, (e) -> {
//            Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
            DAO.getInstance().delete(category);
//            previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//            previousForm.showBack(); //drop any changes
//            showPreviousScreenOrDefault(previousForm, true);
            showPreviousScreenOrDefault( true);
        });
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
        parseIdMapReset();
//        Container content = new Container();
        if (false) {
            TableLayout tl;
//        int spanButton = 2;
            int nbFields = 8;
            if (Display.getInstance().isTablet()) {
                tl = new TableLayout(nbFields, 2);
            } else {
                tl = new TableLayout(nbFields * 2, 1);
//            spanButton = 1;
            }
            tl.setGrowHorizontally(true);
            content.setLayout(tl);
        } else {

        }

        MyTextField categoryName = new MyTextField("Category name", parseIdMap2, () -> category.getText(), (s) -> category.setText(s));
//        content.add(new Label(Category.CATEGORY)).add(categoryName);
        setEditOnShow(categoryName); //UI: start editing this field
        content.add(layout(Category.CATEGORY, categoryName, "**"));

//        MyTextArea description = new MyTextArea("Description", parseIdMap2, () -> category.getComment(), (s) -> category.setComment(s));
        MyTextField description = new MyTextField("Description", parseIdMap2, () -> category.getComment(), (s) -> category.setComment(s));
//        content.add(new Label(Category.DESCRIPTION)).add(description);
        content.add(layout(Category.DESCRIPTION, description, "**"));
        description.addActionListener((e) -> setTitle(description.getText())); //update the form title when text is changed

        Label createdDate = new Label(category.getCreatedAt() == null || category.getCreatedAt().getTime() == 0 ? "<none>" : L10NManager.getInstance().formatDateShortStyle(category.getCreatedAt()));
//        content.add(new Label(Item.CREATED_DATE)).add(createdDate);
        content.add(layout(Item.CREATED_DATE, createdDate, "**"));

        Label lastModifiedDate = new Label(category.getUpdatedAt() == null || category.getUpdatedAt().getTime() == 0 ? "<none>" : L10NManager.getInstance().formatDateShortStyle(category.getUpdatedAt()));
//        content.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
        content.add(layout(Item.UPDATED_DATE, lastModifiedDate, "**"));

        if (false) {
            //TODO!! make the validator work, e.g. show Toastbar message
            //https://www.codenameone.com/blog/validation-regex-masking.html and https://www.codenameone.com/javadoc/com/codename1/ui/validation/Validator.html#setValidationFailureHighlightMode-com.codename1.ui.validation.Validator.HighlightMode-
            Validator v = new Validator();
            v
                    //                .addConstraint(categoryName, new LengthConstraint(1, "Category name cannot be empty")) //NOT needed since an empty category will be ignored where added (
                    .addConstraint(categoryName, new Constraint() {
                        @Override
                        public boolean isValid(Object value) {
//                String catName = ((MyTextField) value).getText();
                            String catName = (String) value;
//                        return DAO.getInstance().getAllCategories().findCategoryWithName(catName) == null;
                            return CategoryList.getInstance().findCategoryWithName(catName) == null;
                        }

                        @Override
                        public String getDefaultFailMessage() {
                            return "Category \"" + categoryName.getText() + "\" already exists";
                        }
                    });
//                .addSubmitButtons(backButton);
//                v.setValidationFailureHighlightMode(Validator.HighlightMode.EMBLEM); //show error message
            v.setValidateOnEveryKey(true);
//                v.setErrorMessageUIID(String errorMessageUIID);
            v.addSubmitButtons(getToolbar().findCommandComponent(backCommand)); // http://stackoverflow.com/questions/39690474/how-to-attach-a-command-to-longpress-on-a-command-in-the-toolbar
            v.setShowErrorMessageForFocusedComponent(true);
        }
        
                if (MyPrefs.showObjectIdsInEditScreens.getBoolean()){
        Label itemObjectId = new Label(category.getObjectIdP() == null ? "<set on save>" : category.getObjectIdP(), "LabelFixed");
        content.add(layoutN(Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true));
        }

        return content;
    }
}
