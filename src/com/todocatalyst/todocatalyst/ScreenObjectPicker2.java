/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Font;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Screen to select Categories for an Item. Features: text search; sorting
 * (name, definable eg by last used/modified or most used); have expandable
 * sublists eg to pick either a list, project or task. Select multiple elements
 * (for Categories), with max and automatic deselection/or better with beep when
 * too many selected;
 *
 * @author Thomas
 */
public class ScreenObjectPicker2<E> extends MyForm {

    //TODO!!!! change how selection of Lists/Projects selector is shown (now as checkmark)
    //TODO!!! keep same Lists/Projects button shown as selected when re-entering the objectPicker
    //TODO!!! support expanding projects to show subprojects and task to allow to pick any subproject level - or replace this screen by ScreenListOfItems??
    //DONE parameter with list(s!) of elements to pick from. 
    //TODO enable expansion of eg Templates to see their subtasks, and to edit them, so basically same list as 
    //TODO!! implement sorting of objects (eg categories), default manual, alphabetically, most recently used, #tasks, #hours of work
    //TODO! implement auto-selection of categories matching search string, e.g. if only one category shown, and press Back, then select that category (or more) ->?? not intuitive when it 'triggers' and not visible which are selected
    //DONE implement search on objects (eg categories)
    private List listOfAllLists1;
    private List listOfAllListsSelectionBackup = new ArrayList<E>();
    private List listOfAllTopLevelProjects1;
    private List listOfAllTopLevelProjectsSelectionBackup = new ArrayList<E>();
//    private List displayedList;
//    private List listOfAllTasks;
//    private List selectedObjects;  
//    private List<String> allLabels;//= new ArrayList();
//    Set<Category> selectedCategories;
    private List selectedObjects;
    Collection[] cachedLists;
//    private CheckBox[] checkBoxes; //must always contain a checkbox for every selectable object, and *in same order* as the selectable objects are define in listOfAllObjects (to correlate checkboxes and objects)
    private GetStringFrom labelMaker;
    private int minNbOfSelected;
    private int maxNbOfSelected;
//    private Object lastSelectedObj;
//    private int maxNbOfSelected;
//    private boolean removeFirstAddedObjectIfMoreThanMaxAreAdded = true;
    private ListSelector listSelector;
    private boolean scrollToFirstSelected = false;
    private boolean exitWhenMaxObjectsIsSelected = false; //immediately exit as soon as max objects are selected (avoid imposing use of Back)
    private Command backCommand;
//    private Container listCont;
    private String errorMsgInSelection;
    private Tabs tabs;
    private Container container;
    ActionListener search;

//    class SelectionState {
//
//    }
    interface GetLists {

        List getList();
    }

//    interface GetListTitles {
//
//        String getTitle();
//    }
    /**
     *
     * @param title
     * @param listOfAllObjects
     * @param selectedObjects modified by the picker so the changed selection is
     * 'returned' in this list
     * @param previousForm
     * @param updateOnDone
     * @param labelMaker
     * @param maxNbOfSelected if -1 as many objects as in listOfAllObjects can
     * be selected
     */
//    ScreenObjectPicker(String title, List listOfAllObjects, ItemAndListCommonInterface owner, MyForm previousForm, UpdateField updateOnDone) {
//    ScreenObjectPicker2(String title, List listOfAllObjects, ItemAndListCommonInterface owner, MyForm previousForm, Runnable updateOnDone) {
//        this(title, listOfAllObjects, null, new ArrayList(Arrays.asList(owner)), previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
//    }
//    ScreenObjectPicker2(String title, List listOfAllObjects, List<ItemAndListCommonInterface> ownerList, MyForm previousForm, Runnable updateOnDone) {
//        this(title, listOfAllObjects, null, ownerList, previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
//    }
//    ScreenObjectPicker2(String title, List listOfAllObjects, List listOfAllTopLevelProjects, List<ItemAndListCommonInterface> ownerList, MyForm previousForm, Runnable updateOnDone) {
//        this(title, listOfAllObjects, listOfAllTopLevelProjects, ownerList, previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
//    }
//    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllProjects, List listOfAllTasks, List<ItemAndListCommonInterface> ownerList, MyForm previousForm, UpdateField updateOnDone) {
//        this(title, listOfAllLists, listOfAllProjects,listOfAllTasks,ownerList, previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
//    }
//    ScreenObjectPicker2(String title, List listOfAllObjects, List selectedObjects, MyForm previousForm) {
//        this(title, listOfAllObjects, null, selectedObjects, previousForm, () -> {
//        }, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
//    }
//    ScreenObjectPicker2(String title, List listOfAllObjects, List selectedObjects, MyForm previousForm, int maxNbOfSelected) {
//        this(title, listOfAllObjects, null, selectedObjects, previousForm, () -> {
//        }, null, maxNbOfSelected, false, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=false to avoid invisible deselect of previous selected
//    }
//    ScreenObjectPicker(String title, List listOfAllObjects, List selectedObjects, MyForm previousForm, UpdateField updateOnDone,
//            GetStringFrom labelMaker, int maxNbOfSelected, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
//        this(title, listOfAllObjects, null, null, selectedObjects, previousForm, updateOnDone, labelMaker, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
//    }
//    ScreenObjectPicker2(String title, List listOfAllLists, List selectedObjects, MyForm previousForm, Runnable updateOnDone,
//            GetStringFrom labelMaker, int maxNbOfSelected, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
//        this(title, listOfAllLists, null, selectedObjects, previousForm, updateOnDone,
//                labelMaker, 1, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
//    }
////    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllProjects, List listOfAllTasks, List selectedObjects, MyForm previousForm, UpdateField updateOnDone,
//
//    ScreenObjectPicker2(String title, List listOfAllLists, List listOfAllTopLevelProjects, List selectedObjects,
//            MyForm previousForm, Runnable updateOnDone, GetStringFrom labelMaker, int maxNbOfSelected, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected,
//            boolean exitWhenMaxObjectsIsSelected) {
//        this(title, listOfAllLists, listOfAllTopLevelProjects, selectedObjects, previousForm, updateOnDone,
//                labelMaker, 1, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
//    }
//
//    ScreenObjectPicker2(String title, List listOfAllLists, List listOfAllTopLevelProjects, Object selectedObject,
//            MyForm previousForm, Runnable updateOnDone, GetStringFrom labelMaker, int minNbOfSelected, int maxNbOfSelected,
//            boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
//        this(title, listOfAllLists, listOfAllTopLevelProjects, (List) new ArrayList(Arrays.asList(selectedObject)), previousForm, updateOnDone, labelMaker, minNbOfSelected,
//                maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
//    }
    public ScreenObjectPicker2(String title, List getLst, Character icon, Font iconFont, List selectedObjs,
            MyForm previousForm, Runnable updateOnDone, GetStringFrom labelMaker, int minNbOfSelected, int maxNbOfSelected,
            boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
        this(title, new GetLists[]{() -> getLst}, null,
                new Character[]{icon}, new Font[]{iconFont},
                selectedObjs, previousForm, updateOnDone, labelMaker, minNbOfSelected, maxNbOfSelected,
                removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
    }

    /**
     *
     * @param title
     * @param listOfAllLists
     * @param listOfAllTopLevelProjects
     * @param selectedObjs
     * @param previousForm
     * @param updateOnDone
     * @param labelMaker
     * @param minNbOfSelected
     * @param maxNbOfSelected
     * @param removeFirstAddedObjectIfMoreThanMaxAreAdded
     * @param scrollToFirstSelected
     * @param exitWhenMaxObjectsIsSelected
     */
    public ScreenObjectPicker2(String title, GetLists[] getLists, String[] getListTitles, Character[] icons, Font[] iconFonts, List selectedObjs,
            MyForm previousForm, Runnable updateOnDone, GetStringFrom labelMaker, int minNbOfSelected, int maxNbOfSelected,
            boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
        super(title, previousForm, updateOnDone);
        assert maxNbOfSelected >= minNbOfSelected && maxNbOfSelected >= 1;

        //        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//             listCont = new Container(BoxLayout.y());
//            listCont.setScrollableY(true); //disable scrolling of form, necessary to let lists handle their own scrolling 
//            getContentPane().add(BorderLayout.CENTER, listCont);
//        setScrollableY(false); //disable scrolling of form, necessary to let lists handle their own scrolling 
        cachedLists = new Collection[getLists.length];

        this.selectedObjects = selectedObjs;
        this.minNbOfSelected = minNbOfSelected;
        this.maxNbOfSelected = maxNbOfSelected;
        this.labelMaker = labelMaker;
        if (this.labelMaker == null) {
            this.labelMaker = (l) -> {
                if (l instanceof String) {
                    return (String) l;
                } else {
                    return l.toString();
                }
            };  //default label maker, no transformation of string
        }
        this.scrollToFirstSelected = scrollToFirstSelected;
        this.exitWhenMaxObjectsIsSelected = exitWhenMaxObjectsIsSelected;

        listSelector = new ListSelector(this.selectedObjects, false, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, (obj, selected) -> {
        }, minNbOfSelected); //allow no selection

        //add any current elements in the selectedObjects which are NOT in the listOfAllLists/listOfAllTopLevelProjects, necessary eg for Inbox which is not a 'user' list (NB. This approach might be a bit of hack)
        if (false) {
            if (selectedObjects != null && selectedObjects.size() > 0) {
                if (selectedObjects.get(0) instanceof ItemList) {
                    for (ItemList itemList : (List<ItemList>) selectedObjects) {
                        if (!listOfAllLists1.contains(itemList)) {
                            listOfAllLists1.add(0, itemList); //TODO!!!! if multiple elements in selectedObjects, then they are added in *reverse* order to the list (not an issue as long as ObjectPicker is only used to pick Owner)
                        }
                    }
                } else if (selectedObjects.get(0) instanceof Item) {
                    for (Item item : (List<Item>) selectedObjects) {
                        if (!listOfAllTopLevelProjects1.contains(item)) {
                            listOfAllTopLevelProjects1.add(0, item);
                        }
                    }
                }
            }
        }

        if (getLists.length == 1) {
            setLayout(BoxLayout.y());
            container = getContentPane(); //new Container(BoxLayout.y());
            Component firstSelectedChk = buildList(container, getLists[0].getList(), this.selectedObjects, this.labelMaker, listSelector);
            if (this.scrollToFirstSelected && firstSelectedChk != null) {
                container.scrollComponentToVisible(firstSelectedChk);
            }
//            getToolbar().setTitle(getListTitles[0]);
            setTitle((getListTitles != null && getListTitles[0] != null ? getListTitles[0] : title), icons[0], iconFonts[0]);
//            getContentPane().add(container);
        } else {
            setLayout(new BorderLayout());
            tabs = new Tabs();
            tabs.setUIID("ObjPickerTabContainer");
            tabs.setTabPlacement(Tabs.BOTTOM);
//        tabs.setLayout(new GridLayout(4));
            tabs.setSwipeActivated(true || MyPrefs.itemEditEnableSwipeBetweenTabs.getBoolean());
            tabs.setEagerSwipeMode(false); //not useful
            tabs.setTabTextPosition(Tabs.RIGHT); //tabs text to the right of the icons
            tabs.setTabUIID("ObjPickerTab");
            Component firstSelectedChk = null;
            int selectedTab = -1;
            for (int i = 0, size = getLists.length; i < size; i++) {
                Container tab = new Container(new BoxLayout(BoxLayout.Y_AXIS));
                if (Config.TEST) {
                    tab.setName("MainTab");
                }
                tab.setScrollableY(true);

                Label l = new Label(getListTitles[i]);
                l.setMaterialIcon(icons[i]);
//                tabs.addTab(getListTitles[i], icons[i], ScreenItem2.TAB_ICON_SIZE_IN_MM, tab);
                if (iconFonts.length > 0 && iconFonts[i] != null) {
//                    tabs.addTab(getListTitles[i], icons[i], iconFonts[i], ScreenItem2.TAB_ICON_SIZE_IN_MM, tab);
                    tabs.addTab(getListTitles[i], icons[i], ScreenItem2.TAB_ICON_SIZE_IN_MM, tab);
                } else {
                    tabs.addTab(getListTitles[i], icons[i], ScreenItem2.TAB_ICON_SIZE_IN_MM, tab);
                }

                Component selected = buildList(tab, getLists[i].getList(), this.selectedObjects, this.labelMaker, listSelector);
                if (firstSelectedChk == null && selected != null) {
                    firstSelectedChk = selected;
                    selectedTab = i;
                }
            }
            if (this.scrollToFirstSelected && firstSelectedChk != null) {
                tabs.setSelectedIndex(selectedTab);
                scrollComponentToVisible(firstSelectedChk);
            }
            tabs.addSelectionListener((oldSel, i) -> {
                List listOfObjects = getLists[i].getList();
                Container tabCont = (Container) tabs.getTabComponentAt(i);
                for (int i2 = 0, size2 = listOfObjects.size(); i2 < size2; i2++) {
                    ItemAndListCommonInterface obj2 = (ItemAndListCommonInterface) listOfObjects.get(i2);
                    CheckBox c = (CheckBox) tabCont.getComponentAt(i2);
                    c.setSelected(listSelector.isSelected(obj2));
                }
//                if (i == 0 || i == -1 || i == oldSel) {
//                    previousValues.remove(LAST_TAB_SELECTED);
//                } else {
//                    previousValues.put(LAST_TAB_SELECTED, i);
//                }
            });
            getContentPane().add(BorderLayout.CENTER, tabs);
        }

        //set error message if wrong number selected when clicking Back
        errorMsgInSelection = listSelector.getErrorMessage();

        addCommandsToToolbar(getToolbar());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        super.refreshAfterEdit();
    }

    /**
     * return true if (possibly modified) category can be saved
     */
    //    public  boolean checkItemListIsValidForSaving(ItemList itemList) {
    public boolean checkObjectChoiceIsValid(int nbSelectedItems) {
//        String errorMsg = (nbSelectedItems >= minNbOfSelected && nbSelectedItems <= maxNbOfSelected)
//                ? null : (minNbOfSelected == maxNbOfSelected ? "Please select 1 element" : Format.f("Please select {0} to {1} elements", "" + minNbOfSelected, "" + maxNbOfSelected));
        String errorMsg = listSelector.checkObjectChoiceIsValid(selectedObjects.size());
        if (errorMsg != null) {
            Dialog.show("Error", errorMsg, "OK", null);
            return false;
        } else {
            return true;
        }
    }

    public void addCommandsToToolbar(Toolbar toolbar) {

        super.addCommandsToToolbar(toolbar);

        search = (e) -> {
            String text = (String) e.getSource();
            boolean showAll = (text == null || text.length() == 0);
            String textLowercase = text.toLowerCase();

            Container searchCont;
            if (container != null) {
                searchCont = container;
                for (int i = 0, size = searchCont.getComponentCount(); i < size; i++) {
                    CheckBox checkBox = (CheckBox) searchCont.getComponentAt(i);
                    checkBox.setHidden(!(showAll || checkBox.getText().toLowerCase().contains(textLowercase))); //https://www.codenameone.com/blog/toolbar-search-mode.html
                }
            } else {
                for (int t = 0, tsize = tabs.getTabCount(); t < tsize; t++) {
                    searchCont = (Container) tabs.getTabComponentAt(t);
                    for (int i = 0, size = searchCont.getComponentCount(); i < size; i++) {
                        CheckBox checkBox = (CheckBox) searchCont.getComponentAt(i);
                        checkBox.setHidden(!(showAll || checkBox.getText().toLowerCase().contains(textLowercase))); //https://www.codenameone.com/blog/toolbar-search-mode.html
                    }
                }
            }
            animateMyForm();
        };
        setSearchCmd(new MySearchCommand(this, search));
        getToolbar().addCommandToRightBar(getSearchCmd());

        setCheckIfSaveOnExit(() -> listSelector.checkObjectChoiceIsValidAndConfirmDialog(selectedObjects.size()));

        addStandardBackCommand();

        if (true || MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) { //UI: always enable Cancel to make it easy to regret any changes
            toolbar.addCommandToOverflowMenu(
                    "Cancel", null, (e) -> {
                        showPreviousScreen(true); //use true for consistency
                    }
            );
        }

    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container buildListOLD(List listOfAllObjects, Container cont) {
//        parseIdMap2.parseIdMapReset();
//        cont.removeAll();
//
//        //maintain a backup of previously selected objects when having more than one list
//        if (displayedList != null && listOfAllObjects != displayedList) {
//            if (displayedList == listOfAllLists1) {
////        if (displayedList==listOfAllLists && listOfAllObjects!=displayedList){
//                //save current selection for listOfAllLists to its backup
//                listOfAllListsSelectionBackup.clear();
//                listOfAllListsSelectionBackup.addAll(selectedObjects);
//                //move previous selection into selectedObjects
//                selectedObjects.clear();
//                selectedObjects.addAll(listOfAllTopLevelProjectsSelectionBackup);
//            } else if (listOfAllObjects != displayedList && displayedList == listOfAllTopLevelProjects1) {
//                //save current selection for listOfAllTopLevelProjects to its backup
//                listOfAllTopLevelProjectsSelectionBackup.clear();
//                listOfAllTopLevelProjectsSelectionBackup.addAll(selectedObjects);
//                //move previous selection into selectedObjects
//                selectedObjects.clear();
//                selectedObjects.addAll(listOfAllListsSelectionBackup);
//            }
//        }
//
//        displayedList = listOfAllObjects;
//        checkBoxes = new CheckBox[listOfAllObjects.size()];
//        allLabels = new ArrayList();
////        selectedObjects = new ArrayList();
//        CheckBox firstSelectedChk = null;
//        //DONE!!!! replace InfiniteContainer by a simpler structure
//        for (int i = 0, size = listOfAllObjects.size(); i < size; i++) {
//            ItemAndListCommonInterface obj = (ItemAndListCommonInterface) listOfAllObjects.get(i);
//            String label = labelMaker.get(obj.getText());
//            allLabels.add(label.toLowerCase()); //store the label in lowercase for faster search
//            CheckBox chk = CheckBox.createToggle(label);
//            boolean selected = selectedObjects.contains(obj);
//            chk.setSelected(selected);
//            if (scrollToFirstSelected && selected && firstSelectedChk == null) {
//                firstSelectedChk = chk;
//            }
//            checkBoxes[i] = chk;
//            cont.add(chk);
//            chk.addActionListener((ActionEvent evt) -> {
//                listSelector.flipSelection(obj);
//                if (exitWhenMaxObjectsIsSelected && listSelector.isMaxNumberSelected()) {
//                    backCommand.actionPerformed(null);
//                }
//            }
//            );
//        }
//        if (scrollToFirstSelected && firstSelectedChk != null) {
//            scrollComponentToVisible(firstSelectedChk);
//        }
//        return cont;
////        }
//    }
//</editor-fold>
    /**
     * return the selected element if in this list
     *
     * @param cont
     * @param listOfAllObjects
     * @param selectedObjects
     * @param labelMaker
     * @param listSelector
     * @returnlistOfObjects
     */
    private Component buildList(Container cont, List listOfObjects, Collection selectedObjects,
            GetStringFrom labelMaker, ListSelector listSelector) {

        //TODO: maintain a backup of previously selected objects when having more than one list
        CheckBox firstSelectedChk = null;
        for (int i = 0, size = listOfObjects.size(); i < size; i++) {
            ItemAndListCommonInterface obj = (ItemAndListCommonInterface) listOfObjects.get(i);
            String label = labelMaker.get(obj.getText());
            CheckBox chk = CheckBox.createToggle(label);
            boolean selected = selectedObjects.contains(obj);
            chk.setSelected(selected);
            if (scrollToFirstSelected && selected && firstSelectedChk == null) {
                firstSelectedChk = chk;
            }
            cont.add(chk);
            chk.addActionListener((ActionEvent evt) -> {
                listSelector.flipSelection(obj); //invert selection (if allowed by ListSelector)
                //refresh displayed list of checkboxes with new state
                for (int i2 = 0, size2 = listOfObjects.size(); i2 < size2; i2++) {
                    ItemAndListCommonInterface obj2 = (ItemAndListCommonInterface) listOfObjects.get(i2);
                    CheckBox c = (CheckBox) cont.getComponentAt(i2);
                    c.setSelected(listSelector.isSelected(obj2));
                }
                if (exitWhenMaxObjectsIsSelected && listSelector.isMaxNumberSelected()) {
                    backCommand.actionPerformed(null);
                }
            });
        }
        return firstSelectedChk;
    }

    public Object getFirstSelected() {

        return listSelector.getFirstSelected();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Button button;
//    private Command command;
//
//    public static Button getButton() {
//        Command command = Command.create(SCREEN_TITLE, null, (e) -> {
//                        ScreenObjectPicker objectPicker = new ScreenObjectPicker(CategoryList.getInstance(), locallyEditedCategories, ScreenItem.this);
//                objectPicker.setUpdateActionOnDone(() -> {
//                    button.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "")); //"<click to set categories>"
//                    parseIdMap2.put("EditedCategories", () -> {
//                        item.updateCategories(locallyEditedCategories); //TODO this won't work with Cancel - need to store the update in parsemap and only update the button text
//                        //DAO.getInstance().save(item); //NOT neeeded here since saved when exiting screen
//                    });
//                });
//                objectPicker.show();
//
//        command = Command.create(SCREEN_TITLE, null, (e) -> {
//            show();
//
//        });
//        button = new Button(command);
//        return button;
//    }
//</editor-fold>
}
