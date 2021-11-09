package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
import java.util.Date;

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
public class ScreenItemListProperties extends MyForm {

//    static Map<String, GetParseValue> parseIdMap = new HashMap<String, GetParseValue>() ;
//    Map<Object, UpdateField> parseIdMap2 = new HashMap<Object, UpdateField>();
//    MyForm previousForm;
    ItemList itemList;
    private static String screenTitle = "List";
// protected static String FORM_UNIQUE_ID = "ScreenItemListProperties"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
//     private UpdateField updateActionOnDone;

    ScreenItemListProperties(ItemList itemList, MyForm previousForm) { //throws ParseException, IOException {
        this(itemList, previousForm, null);
    }

    ScreenItemListProperties(ItemList itemList, MyForm previousForm, Runnable doneAction) { //throws ParseException, IOException {
        super("", previousForm, doneAction);
        setTitle(itemList instanceof Category ? Category.CATEGORY : ItemList.ITEM_LIST);
//        ScreenItemP.item = item;
        this.itemList = itemList;
        setUniqueFormId("ScreenItemListProperties");
        this.previousValues = new SaveEditedValuesLocally(getUniqueFormId());

//        ScreenItemP.previousForm = previousForm;
//        this.previousForm = previousForm;
//        this.updateActionOnDone = doneAction;
        // we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
//        form = this;
        // we use border layout so the list will take up all the available space
//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        setLayout(BoxLayout.y());
//        setScrollableY(true); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        makeContainerBoxY();

//        setToolbar(new Toolbar());
//        setTitle(screenTitle);
        addCommandsToToolbar(getToolbar());
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
//        getContentPane().removeAll();
        mainContentContainer.removeAll();
//        buildContentPane(getContentPane());
        buildContentPane(mainContentContainer);
//        restoreKeepPos();
        super.refreshAfterEdit();
    }

    /**
     * return null if no error and (possibly modified) category can be saved,
     * otherwise displays an error dialog and returns a non-null error string
     */
//    public  boolean checkItemListIsValidForSaving(ItemList itemList) {
    public static String checkItemListIsValidForSaving(String itemListName, ItemList itemList) {
        return checkItemListIsValidForSaving(itemListName, itemList, true);
    }

    public static String checkItemListIsValidForSaving(String itemListName, ItemList itemList, boolean showErrorDialog) {
        //TODO extend to check valid subcategories, auto-words, ...
        String errorMsg = null;
//        String itemListName = itemList.getText();
//        String type = listOrCategory instanceof Category?Category.CATEGORY:ItemList.ITEM_LIST;
        itemListName = MyUtil.removeTrailingPrecedingSpacesNewLinesEtc(itemListName);
        if (itemListName.isEmpty() && itemList.getObjectIdP() != null) {
            errorMsg = Format.f("{0 category_or_list} name cannot be empty", ItemList.ITEM_LIST);
        } else if (ItemListList.getInstance().findItemListWithName(itemListName) != null
                && ItemListList.getInstance().findItemListWithName(itemListName) != itemList
                && !MyPrefs.itemListAllowDuplicateListNames.getBoolean()) {
//                return "Category \"" + description.getText() + "\" already exists";
            //                return Format.f("Category \"{1 just_entered_category_name}\" already exists",categoryName.getText());
            //            errorMsg = Format.f("{0 category_or_itemlist} \"{1 just_entered_category_name}\" already exists", ItemList.ITEM_LIST, itemListName);
            errorMsg = Format.f("{0 category_or_itemlist} \"{1 just_entered_category_name}\" already exists, and more than one {0} with same name is not allowed. Please set a different name.", ItemList.ITEM_LIST, itemListName);
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

    public void addCommandsToToolbar(Toolbar toolbar) {

        super.addCommandsToToolbar(toolbar);
//<editor-fold defaultstate="collapsed" desc="comment">
//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle());
//        Command cmd = Command.create("", icon, (e) -> {
//            putEditedValues2(parseIdMap2);
////            try {
////                (itemList.save();
////                DAO.getInstance().save(itemList);
//            if (updateActionOnDone != null) {
//                updateActionOnDone.update();
//            }
////            } catch (ParseException ex) {
////                Log.e(ex); //TODO: add dialog/popup info when save does not succeed
////            }
////            previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//            previousForm.showBack();
//        });
//        cmd.putClientProperty("android:showAsAction", "withText");
//        toolbar.addCommandToLeftBar(cmd);
//</editor-fold>
        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand());

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("ItemListSettings", "Settings", Icons.iconSettings, (e) -> {
            new ScreenSettingsItemListProperties(ScreenItemListProperties.this, () -> {
//                if (false) {
//                    refreshAfterEdit();
//                }
            }).show();
        }
        ));

        if (true || MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> {
//                Log.p("Clicked");
////            item.revert(); //forgetChanges***/refresh
////            previousForm.showBack(); //drop any changes
////                previousForm.revalidate();
////                previousForm.show(); //drop any changes
//                showPreviousScreen(true); //false);
//            });
//</editor-fold>
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }

        if (false && Config.TEST) {
            if (MyPrefs.getBoolean(MyPrefs.enableRepairCommandsInMenus)) {
                toolbar.addCommandToOverflowMenu("Show data issues", null, (e) -> {
                    DAO.getInstance().cleanUpItemList(itemList, itemList.getOwner().equals(TemplateList.getInstance()), false);
                });
            }
            if (MyPrefs.getBoolean(MyPrefs.enableRepairCommandsInMenus)) {
                toolbar.addCommandToOverflowMenu("Repair data issues", null, (e) -> {
                    DAO.getInstance().cleanUpItemList(itemList, itemList.getOwner().equals(TemplateList.getInstance()), true);
                });
            }
        }

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
//        }
//</editor-fold>
        content.add(makeSpacerThin());

//        MyTextField name = new MyTextField("List name", parseIdMap2, () -> itemList.getText(), (s) -> itemList.setText(s));
        MyTextField description = new MyTextField(Item.DESCRIPTION_HINT, 20, 1, 1, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY);
        description.getHintLabel().setUIID("ScreenItemTaskTextHint");
        description.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //start with initial caps automatically - TODO!!!! NOT WORKING LIKE THIS!!

//        content.add(new Label("List name")).add(name);
//        content.add(layoutN(false, "List name", name, "**"));
//        content.add(layoutN(null, name, "**", true));
//        AutoSaveTimer nameSaveTimer = new AutoSaveTimer(this, name, null, null); //normal that this appear as non-used!
        description.addActionListener((e) -> setTitle(description.getText())); //update the form title when text is changed
        initField(Item.PARSE_TEXT, description,
                () -> itemList.getText(),
                (t) -> itemList.setText((String) t),
                () -> description.getText(),
                (t) -> description.setText((String) t));
        if (itemList.getText().isEmpty()) {
            setEditOnShow(description); //UI: start editing this field
        }
        content.add(description);

        content.add(makeSpacerThin());

//        MyTextField description = new MyTextField("Description", parseIdMap2, () -> itemList.getComment(), (s) -> itemList.setComment(s));
//        MyTextField comment = new MyTextField("Description", parseIdMap2, () -> itemList.getComment(), (s) -> itemList.setComment(s));
        MyTextField comment = new MyTextField(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
        comment.setSingleLineTextArea(false);
        comment.setUIID("ScreenItemComment");
        comment.getHintLabel().setUIID("ScreenItemCommentHint");
//        content.add(new Label("Description")).add(description).add(new SpanLabel("If necessary, use the description to describe the purpose of the list. It will be shown at the top of the screen when showing the list of tasks."));
//        content.add(layoutN(false, "Description", description, "If necessary, use the description to describe the purpose of the list. It will be shown at the top of the screen when showing the list of tasks."));
//        content.add(layoutN( null, description, "If necessary, use the description to describe the purpose of the list. It will be shown at the top of the screen when showing the list of tasks.",true));
        initField(Item.PARSE_COMMENT, comment,
                () -> itemList.getComment(),
                (t) -> itemList.setComment((String) t),
                () -> comment.getText(),
                (t) -> comment.setText((String) t));
        content.add(comment);

        content.add(makeSpacerThin());

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_NUMBER_UNDONE_TASKS_TEXT, Item.SHOW_NUMBER_UNDONE_TASKS_HELP,
                () -> itemList.getShowNumberUndoneTasks(), (b) -> itemList.setShowNumberUndoneTasks(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_LEAF_TASKS_TEXT, Item.SHOW_LEAF_TASKS_HELP,
                () -> itemList.getShowNumberLeafTasks(), (b) -> itemList.setShowNumberLeafTasks(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_NUMBER_DONE_TASKS_TEXT, Item.SHOW_NUMBER_DONE_TASKS_HELP,
                () -> itemList.getShowNumberDoneTasks(), (b) -> itemList.setShowNumberDoneTasks(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_REMAINING_TEXT, Item.SHOW_REMAINING_HELP,
                () -> itemList.getShowRemaining(), (b) -> itemList.setShowRemaining(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_TOTAL_TEXT, Item.SHOW_TOTAL_HELP,
                () -> itemList.getShowTotal(), (b) -> itemList.setShowTotal(b)));

        content.add(ScreenSettingsCommon.makeEditBoolean(Item.SHOW_WORK_TIME_TEXT, Item.SHOW_WORK_TIME_HELP,
                () -> itemList.getShowWorkTime(), (b) -> itemList.setShowWorkTime(b)));

        content.add(makeSpacerThin());
//        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(this, description, null, null); //normal that this appear as non-used!

//        Label createdDate = new Label(itemList.getCreatedAt() == null || itemList.getCreatedAt().getTime() == 0 ? "<none>" : L10NManager.getInstance().formatDateShortStyle(itemList.getCreatedAt())); //"<date set when saved>"
        Label createdDate = new Label(itemList.getCreatedAt() == null || itemList.getCreatedAt().getTime() == 0 ? MyDate.formatDateNew(new MyDate()) : MyDate.formatDateNew(itemList.getCreatedAt())); //"<date set when saved>"
//        content.add(new Label(Item.CREATED_DATE)).add(createdDate);
        content.add(layoutN(Item.PARSE_CREATED_AT, Item.CREATED_DATE, createdDate, "**", true, hide ? null : Icons.iconCreatedDateCust, Icons.myIconFont));

//        Label lastModifiedDate = new Label(itemList.getUpdatedAt() == null || itemList.getUpdatedAt().getTime() == 0 ? "<none>" : L10NManager.getInstance().formatDateShortStyle(itemList.getUpdatedAt()));
        Label lastModifiedDate = new Label(itemList.getUpdatedAt() == null || itemList.getUpdatedAt().getTime() == 0 ? MyDate.formatDateNew(new MyDate()) : MyDate.formatDateNew(itemList.getUpdatedAt()));
//        content.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
        content.add(layoutN(Item.PARSE_UPDATED_AT, Item.UPDATED_DATE, lastModifiedDate, "**", true, hide ? null : Icons.iconModifiedDateCust,Icons.myIconFont));

        if (MyPrefs.enableShowingSystemInfo.getBoolean() && MyPrefs.showObjectIdsInEditScreens.getBoolean()) {
            Label itemObjectId = new Label(itemList.getObjectIdP() == null ? "<set on save>" : itemList.getObjectIdP(), "ScreenItemValueUneditable");
            content.add(layoutN(Item.PARSE_OBJECT_ID_VIRT, Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true, hide ? null : Icons.iconObjectId));
        }
        if (Config.TEST) {
            if (!itemList.getSystemName().isEmpty()) {
                Label systemNameLabel = new Label(itemList.getSystemName());
                content.add(layoutN(ItemList.PARSE_SYSTEM_NAME, Item.SYSTEM_NAME, systemNameLabel, "**", true));
            }
        }

//        setCheckIfSaveOnExit(() -> checkItemListIsValidForSaving(name.getText(), (ItemList) itemList.getOwner()));
        setCheckIfSaveOnExit(() -> checkItemListIsValidForSaving(description.getText(), itemList) == null);

        content.add(new Button(CommandTracked.create("Export to CSV", null, (e) -> {
            Storage.getInstance();
            itemList.exportToCsv((itemList.getText().isEmpty() ? "unnamedList" : itemList.getText()) + ".csv");
        }, "ExportListToCSV")));

        return content;
    }
}
