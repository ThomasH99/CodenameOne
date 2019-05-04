package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.io.Log;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
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

    ScreenItemListProperties(ItemList itemList, MyForm previousForm, UpdateField doneAction) { //throws ParseException, IOException {
        super("", previousForm, doneAction);
        setTitle(itemList instanceof Category ? Category.CATEGORY : ItemList.ITEM_LIST);
//        ScreenItemP.item = item;
        this.itemList = itemList;
        setUniqueFormId("ScreenItemListProperties");
//        ScreenItemP.previousForm = previousForm;
//        this.previousForm = previousForm;
//        this.updateActionOnDone = doneAction;
        // we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
//        form = this;
        // we use border layout so the list will take up all the available space
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers

//        setToolbar(new Toolbar());
//        setTitle(screenTitle);
        addCommandsToToolbar(getToolbar());
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

    /**
    return true if (possibly modified) category can be saved
     */
//    public  boolean checkItemListIsValidForSaving(ItemList itemList) {
    public static boolean checkItemListIsValidForSaving(String itemListName, ItemList itemList) {
        //TODO extend to check valid subcategories, auto-words, ...
        String errorMsg = null;
//        String itemListName = itemList.getText();
//        String type = listOrCategory instanceof Category?Category.CATEGORY:ItemList.ITEM_LIST;
        itemListName = MyUtil.removeTrailingPrecedingSpacesNewLinesEtc(itemListName);
        if (itemListName.isEmpty())
            errorMsg = Format.f("{0 category_or_list} name cannot be empty", ItemList.ITEM_LIST);
        else if (ItemListList.getInstance().findItemListWithName(itemListName) != null
                && ItemListList.getInstance().findItemListWithName(itemListName) != itemList
                && !MyPrefs.itemListAllowDuplicateListNames.getBoolean())
            //                return "Category \"" + description.getText() + "\" already exists";
            //                return Format.f("Category \"{1 just_entered_category_name}\" already exists",categoryName.getText());
            //            errorMsg = Format.f("{0 category_or_itemlist} \"{1 just_entered_category_name}\" already exists", ItemList.ITEM_LIST, itemListName);
            errorMsg = Format.f("{0 category_or_itemlist} \"{1 just_entered_category_name}\" already exists, and more than one {0} with same name is not allowed. Please set a different name.", ItemList.ITEM_LIST, itemListName);

        if (errorMsg != null) {
            Dialog.show("Error", errorMsg, "OK", null);
            return false;
        } else return true;
    }

    public void addCommandsToToolbar(Toolbar toolbar) {

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
        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand());

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> {
                Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
//                previousForm.revalidate();
//                previousForm.show(); //drop any changes
                showPreviousScreenOrDefault(true); //false);
            });
        }
        if (MyPrefs.getBoolean(MyPrefs.enableRepairCommandsInMenus)) {
            toolbar.addCommandToOverflowMenu("Show data issues", null, (e) -> {
                DAO.getInstance().cleanUpItemListOrCategory(itemList, false);
            });
        }
        if (MyPrefs.getBoolean(MyPrefs.enableRepairCommandsInMenus)) {
            toolbar.addCommandToOverflowMenu("Repair data issues", null, (e) -> {
                DAO.getInstance().cleanUpItemListOrCategory(itemList, false);
            });
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
        }

        MyTextField name = new MyTextField("List name", parseIdMap2, () -> itemList.getText(), (s) -> itemList.setText(s));
//        content.add(new Label("List name")).add(name);
        content.add(layout("List name", name, "**"));
        setEditOnShow(name); //UI: start editing this field

        MyTextField description = new MyTextField("Description", parseIdMap2, () -> itemList.getComment(), (s) -> itemList.setComment(s));
//        content.add(new Label("Description")).add(description).add(new SpanLabel("If necessary, use the description to describe the purpose of the list. It will be shown at the top of the screen when showing the list of tasks."));
        content.add(layout("Description", description, "If necessary, use the description to describe the purpose of the list. It will be shown at the top of the screen when showing the list of tasks."));
        description.addActionListener((e) -> setTitle(description.getText())); //update the form title when text is changed

//        Label createdDate = new Label(itemList.getCreatedAt() == null || itemList.getCreatedAt().getTime() == 0 ? "<none>" : L10NManager.getInstance().formatDateShortStyle(itemList.getCreatedAt())); //"<date set when saved>"
        Label createdDate = new Label(itemList.getCreatedAt() == null || itemList.getCreatedAt().getTime() == 0 ? MyDate.formatDateNew(new Date()) : MyDate.formatDateNew(itemList.getCreatedAt())); //"<date set when saved>"
//        content.add(new Label(Item.CREATED_DATE)).add(createdDate);
        content.add(layout(Item.CREATED_DATE, createdDate, "**"));

//        Label lastModifiedDate = new Label(itemList.getUpdatedAt() == null || itemList.getUpdatedAt().getTime() == 0 ? "<none>" : L10NManager.getInstance().formatDateShortStyle(itemList.getUpdatedAt()));
        Label lastModifiedDate = new Label(itemList.getUpdatedAt() == null || itemList.getUpdatedAt().getTime() == 0 ? MyDate.formatDateNew(new Date()) : MyDate.formatDateNew(itemList.getUpdatedAt()));
//        content.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
        content.add(layout(Item.UPDATED_DATE, lastModifiedDate, "**"));

        if (MyPrefs.showObjectIdsInEditScreens.getBoolean()) {
            Label itemObjectId = new Label(itemList.getObjectIdP() == null ? "<set on save>" : itemList.getObjectIdP(), "LabelFixed");
            content.add(layoutN(Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true));
        }

        setCheckIfSaveOnExit(() -> checkItemListIsValidForSaving(name.getText(), (ItemList) itemList.getOwner()));

        return content;
    }
}
