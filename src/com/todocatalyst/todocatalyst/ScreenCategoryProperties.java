package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
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
public class ScreenCategoryProperties extends MyForm {

//    static Map<String, GetParseValue> parseIdMap = new HashMap<String, GetParseValue>() ;
    Category category;
    private static String screenTitle = "Category";
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Button backButton;
//    private Command backCommand;
// protected static String FORM_UNIQUE_ID = "ScreenEditCategory"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
//     private UpdateField updateActionOnDone;

//    ScreenCategory(Category category, MyForm previousForm) { //throws ParseException, IOException {
//        this(category, previousForm, () -> {
//        });
//    }
//</editor-fold>
    ScreenCategoryProperties(Category category, MyForm previousForm, Runnable doneAction) { //throws ParseException, IOException {
        super(category.getText(), previousForm, doneAction);
        setUniqueFormId("ScreenEditCategory");
//        ScreenItemP.item = item;
        this.category = category;
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>

//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        setScrollableY(true); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
makeContainerBoxY();
//        previousValues = new SaveEditedValuesLocally(getUniqueFormId());

        addCommandsToToolbar(getToolbar());//, theme);

//        setCheckOnExit(()->checkCategoryIsValidForSaving(this.category));
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    /**
     * return true if (possibly modified) category can be saved
     */
//    public static String checkCategoryIsValidForSaving(String categoryName, Category category) {
//        return checkCategoryIsValidForSaving(categoryName, category, true);
//    }
    public static String checkCategoryIsValidForSaving(String categoryName, Category category, boolean showErrorDialog) {
        //TODO extend to check valid subcategories, auto-words, ...
        String errorMsg = null;
//        String type = listOrCategory instanceof Category?Category.CATEGORY:ItemList.ITEM_LIST;
        categoryName = MyUtil.removeTrailingPrecedingSpacesNewLinesEtc(categoryName);
        if (categoryName.isEmpty() && category.getObjectIdP() != null) {
            errorMsg = Format.f("{0 category_or_list} name cannot be empty", Category.CATEGORY); //cannot delete text
        } else if (CategoryList.getInstance().findCategoryWithName(categoryName, true) != null
                && CategoryList.getInstance().findCategoryWithName(categoryName, true) != category) { //                return "Category \"" + description.getText() + "\" already exists";
            //                return Format.f("Category \"{1 just_entered_category_name}\" already exists",categoryName.getText());
            errorMsg = Format.f("{0 category_or_itemlist} \"{1 just_entered_category_name}\" already exists.\n\n Only one {0} with same name is allowed. Please set a different name.", Category.CATEGORY, categoryName);
        }

        if (errorMsg != null) {
            if (showErrorDialog) {
                Dialog.show("Error", errorMsg, "OK", null);
            }
            return errorMsg; //false;
        } else {
            return errorMsg; //true;
        }
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommandsNO_EFFECT(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
//        getContentPane().removeAll();
        container.removeAll();
//        buildContentPane(getContentPane());
        buildContentPane(container);
//        restoreKeepPos();
        super.refreshAfterEdit();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    return true if (possibly modified) category can be saved
//    */
//    public static boolean checkCategoryIsValidForSaving(Category category) {
//        //TODO extend to check valid subcategories, auto-words, ...
//        String errorMsg = null;
//        if (category.getText().isEmpty())
//            errorMsg = "Category name cannot be empty";
//        else if (CategoryList.getInstance().findCategoryWithName(category.getText()) != null)
//            //                return "Category \"" + description.getText() + "\" already exists";
//            //                return Format.f("Category \"{1 just_entered_category_name}\" already exists",categoryName.getText());
//            errorMsg = Format.f("Category \"{0 just_entered_category_name}\" already exists", category.getText());
//
//        if (errorMsg != null) {
//            Dialog.show("Error", errorMsg, "OK", null);
//            return false;
//        } else return true;
//    }
//</editor-fold>
    public void addCommandsToToolbar(Toolbar toolbar) { //, Resources theme) {
        super.addCommandsToToolbar(toolbar);
//<editor-fold defaultstate="collapsed" desc="comment">
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
//        toolbar.setBackCommand(backCommand = makeDoneUpdateWithParseIdMapCommand());
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
//</editor-fold>
        addStandardBackCommand();

//        //DELETE
////<editor-fold defaultstate="collapsed" desc="comment">
//        if (category.getObjectIdP() != null) { //only Delete categories already on Parse, not one you're just creating (use Cancel instead)
//            toolbar.addCommandToOverflowMenu(CommandTracked.createMaterial("Delete", Icons.iconDelete, (e) -> {
////            Log.p("Clicked");
////            item.revert(); //forgetChanges***/refresh
////            previousForm.showBack(); //drop any changes
////            DAO.getInstance().delete(category);
////            category.softDelete();
////            previousForm.refreshAfterEdit();
//////            previousForm.revalidate();
////            previousForm.showBack(); //drop any changes
////            showPreviousScreenOrDefault(previousForm, true);
//                DAO.getInstance().delete(category, false, true);
//                showPreviousScreen(true);
//            }));
//        }
////</editor-fold>
        //CANCEL
        if (true || MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
//            toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> {
////<editor-fold defaultstate="collapsed" desc="comment">
////            Log.p("Clicked");
////            item.revert(); //forgetChanges***/refresh
////            previousForm.showBack(); //drop any changes
////            previousForm.revalidate();
//
////            previousForm.show(); //drop any changes
////                showPreviousScreenOrDefault(previousForm, false);
////</editor-fold>
//                showPreviousScreen(false);
//            });
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }

        if (Config.TEST) {
            if (MyPrefs.getBoolean(MyPrefs.enableRepairCommandsInMenus)) {
                toolbar.addCommandToOverflowMenu("Show data issues", null, (e) -> {
                    DAO.getInstance().cleanUpCategory(category, false);
                });
            }
            if (MyPrefs.getBoolean(MyPrefs.enableRepairCommandsInMenus)) {
                toolbar.addCommandToOverflowMenu("Repair data issues", null, (e) -> {
                    DAO.getInstance().cleanUpCategory(category, true);
                });
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
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
        parseIdMap2.parseIdMapReset();

        boolean hide = MyPrefs.hideIconsInEditTaskScreen.getBoolean();

//<editor-fold defaultstate="collapsed" desc="comment">
//        Container content = new Container();
//        if (false) {
//            TableLayout tl;
////        int spanButton = 2;
//            int nbFields = 8;
//            if (Display.getInstance().isTablet()) {
//                tl = new TableLayout(nbFields, 2);
//            } else {
//                tl = new TableLayout(nbFields * 2, 1);
////            spanButton = 1;
//            }
//            tl.setGrowHorizontally(true);
//            content.setLayout(tl);
//        } else {
//
//        }
//</editor-fold>
        content.add(makeSpacerThin());

//        MyTextField description = new MyTextField("Category name", parseIdMap2, () -> category.getText(), (s) -> category.setText(s));
        MyTextField description = new MyTextField(Item.DESCRIPTION_HINT, 20, 1, 1, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY);
        description.getHintLabel().setUIID("ScreenItemTaskTextHint");
        description.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //start with initial caps automatically - TODO!!!! NOT WORKING LIKE THIS!!
//        content.add(new Label(Category.CATEGORY)).add(categoryName);
        description.addActionListener((e) -> setTitle(description.getText())); //update the form title when text is changed
        initField(Item.PARSE_TEXT, description,
                () -> category.getText(),
                (t) -> category.setText((String) t),
                () -> description.getText(),
                (t) -> description.setText((String) t));
        if (category.getText().isEmpty())
        setEditOnShow(description); //UI: start editing this field, only when empty
//        content.add(layoutN(Category.CATEGORY, categoryName, "**", true));
        content.add(description);

        content.add(makeSpacerThin());

//        MyTextArea description = new MyTextArea("Description", parseIdMap2, () -> category.getComment(), (s) -> category.setComment(s));
//        MyTextField comment = new MyTextField("Description", parseIdMap2, () -> category.getComment(), (s) -> category.setComment(s));
        MyTextField comment = new MyTextField(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
        comment.setSingleLineTextArea(false);
        comment.setUIID("ScreenItemComment");
        comment.getHintLabel().setUIID("ScreenItemCommentHint");
//        content.add(new Label(Category.DESCRIPTION)).add(description);
//        content.add(layoutN(Category.DESCRIPTION, description, "**", true));
        initField(Item.PARSE_COMMENT, comment,
                () -> category.getComment(),
                (t) -> category.setComment((String) t),
                () -> comment.getText(),
                (t) -> comment.setText((String) t));
        content.add(comment);

        content.add(makeSpacerThin());
                content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_NUMBER_UNDONE_TASKS_TEXT, Item.SHOW_NUMBER_UNDONE_TASKS_HELP,
                () -> category.getShowNumberUndoneTasks(), (b) -> category.setShowNumberUndoneTasks(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_NUMBER_DONE_TASKS_TEXT, Item.SHOW_NUMBER_DONE_TASKS_HELP,
                () -> category.getShowNumberDoneTasks(), (b) -> category.setShowNumberDoneTasks(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_LEAF_TASKS_TEXT, Item.SHOW_LEAF_TASKS_HELP,
                () -> category.getShowNumberLeafTasks(), (b) -> category.setShowNumberLeafTasks(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_REMAINING_TEXT, Item.SHOW_REMAINING_HELP,
                () -> category.getShowRemaining(), (b) -> category.setShowRemaining(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_TOTAL_TEXT, Item.SHOW_TOTAL_HELP,
                () -> category.getShowTotal(), (b) -> category.setShowTotal(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_WORK_TIME_TEXT, Item.SHOW_WORK_TIME_HELP,
                () -> category.getShowWorkTime(), (b) -> category.setShowWorkTime(b)));

        content.add(makeSpacerThin());
        

        //CREATED
        Label createdDate = new Label(category.getCreatedAt() == null || category.getCreatedAt().getTime() == 0 ? "<none>" : L10NManager.getInstance().formatDateShortStyle(category.getCreatedAt()));
//        content.add(new Label(Item.CREATED_DATE)).add(createdDate);
        content.add(layoutN(Item.PARSE_CREATED_AT, Item.CREATED_DATE, createdDate, "**", true, hide ? null : Icons.iconCreatedDate));

        //MODIFIED
        Label lastModifiedDate = new Label(category.getUpdatedAt() == null || category.getUpdatedAt().getTime() == 0 ? "<none>" : L10NManager.getInstance().formatDateShortStyle(category.getUpdatedAt()));
//        content.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
        content.add(layoutN(Item.PARSE_UPDATED_AT,Item.UPDATED_DATE, lastModifiedDate, "**", true, hide ? null : Icons.iconModifiedDate));

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            //TODO!! make the validator work, e.g. show Toastbar message
//            //https://www.codenameone.com/blog/validation-regex-masking.html and https://www.codenameone.com/javadoc/com/codename1/ui/validation/Validator.html#setValidationFailureHighlightMode-com.codename1.ui.validation.Validator.HighlightMode-
//            Validator v = new Validator();
//            v
//                    //                .addConstraint(categoryName, new LengthConstraint(1, "Category name cannot be empty")) //NOT needed since an empty category will be ignored where added (
//                    .addConstraint(categoryName, new Constraint() {
//                        @Override
//                        public boolean isValid(Object value) {
////                String catName = ((MyTextField) value).getText();
//                            String catName = (String) value;
////                        return DAO.getInstance().getAllCategories().findCategoryWithName(catName) == null;
//                            return CategoryList.getInstance().findCategoryWithName(catName) == null;
//                        }
//
//                        @Override
//                        public String getDefaultFailMessage() {
//                            return "Category \"" + categoryName.getText() + "\" already exists";
//                        }
//                    });
////                .addSubmitButtons(backButton);
////                v.setValidationFailureHighlightMode(Validator.HighlightMode.EMBLEM); //show error message
//            v.setValidateOnEveryKey(true);
////                v.setErrorMessageUIID(String errorMessageUIID);
//            v.addSubmitButtons(getToolbar().findCommandComponent(backCommand)); // http://stackoverflow.com/questions/39690474/how-to-attach-a-command-to-longpress-on-a-command-in-the-toolbar
//            v.setShowErrorMessageForFocusedComponent(true);
//        }
//</editor-fold>
        if (MyPrefs.enableShowingSystemInfo.getBoolean() && MyPrefs.showObjectIdsInEditScreens.getBoolean()) {
            Label itemObjectId = new Label(category.getObjectIdP() == null ? "<set on save>" : category.getObjectIdP(), "ScreenItemValueUneditable");
            content.add(layoutN(Item.PARSE_OBJECT_ID_VIRT,Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true, hide ? null : Icons.iconObjectId));
         
            Label itemObjectId2 = new Label(category.getGuid());
            content.add(layoutN(Item.PARSE_GUID_VIRT,Item.GUID, itemObjectId2, Item.OBJECT_GUID_HELP, true, hide ? null : Icons.iconObjectId));
        }

        setCheckIfSaveOnExit(()
                -> checkCategoryIsValidForSaving(description.getText(), category, true) == null);

        return content;
    }
}
