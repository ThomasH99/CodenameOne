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
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import java.util.ArrayList;
import java.util.Arrays;
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
public class ScreenObjectPicker<E> extends MyForm {

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
    private List displayedList;
//    private List listOfAllTasks;
//    private List selectedObjects;  
    private List<String> allLabels;//= new ArrayList();
//    Set<Category> selectedCategories;
    private List selectedObjects1;
    private CheckBox[] checkBoxes; //must always contain a checkbox for every selectable object, and *in same order* as the selectable objects are define in listOfAllObjects (to correlate checkboxes and objects)
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
    private Container listCont;
    private String errorMsgInSelection;

    class SelectionState {

    }

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
    ScreenObjectPicker(String title, List listOfAllObjects, ItemAndListCommonInterface owner, MyForm previousForm, Runnable updateOnDone) {
        this(title, listOfAllObjects, null, new ArrayList(Arrays.asList(owner)), previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
    }

    ScreenObjectPicker(String title, List listOfAllObjects, List<ItemAndListCommonInterface> ownerList, MyForm previousForm, Runnable updateOnDone) {
        this(title, listOfAllObjects, null, ownerList, previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
    }

    ScreenObjectPicker(String title, List listOfAllObjects, List listOfAllTopLevelProjects, List<ItemAndListCommonInterface> ownerList, MyForm previousForm, Runnable updateOnDone) {
        this(title, listOfAllObjects, listOfAllTopLevelProjects, ownerList, previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
    }

//    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllProjects, List listOfAllTasks, List<ItemAndListCommonInterface> ownerList, MyForm previousForm, UpdateField updateOnDone) {
//        this(title, listOfAllLists, listOfAllProjects,listOfAllTasks,ownerList, previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
//    }
    ScreenObjectPicker(String title, List listOfAllObjects, List selectedObjects, MyForm previousForm) {
        this(title, listOfAllObjects, null, selectedObjects, previousForm, () -> {
        }, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
    }

    ScreenObjectPicker(String title, List listOfAllObjects, List selectedObjects, MyForm previousForm, int maxNbOfSelected) {
        this(title, listOfAllObjects, null, selectedObjects, previousForm, () -> {
        }, null, maxNbOfSelected, false, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=false to avoid invisible deselect of previous selected
    }

//    ScreenObjectPicker(String title, List listOfAllObjects, List selectedObjects, MyForm previousForm, UpdateField updateOnDone,
//            GetStringFrom labelMaker, int maxNbOfSelected, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
//        this(title, listOfAllObjects, null, null, selectedObjects, previousForm, updateOnDone, labelMaker, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
//    }
    ScreenObjectPicker(String title, List listOfAllLists, List selectedObjects, MyForm previousForm, Runnable updateOnDone,
            GetStringFrom labelMaker, int maxNbOfSelected, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
        this(title, listOfAllLists, null, selectedObjects, previousForm, updateOnDone,
                labelMaker, 1, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
    }
//    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllProjects, List listOfAllTasks, List selectedObjects, MyForm previousForm, UpdateField updateOnDone,

    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllTopLevelProjects, List selectedObjects,
            MyForm previousForm, Runnable updateOnDone, GetStringFrom labelMaker, int maxNbOfSelected, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected,
            boolean exitWhenMaxObjectsIsSelected) {
        this(title, listOfAllLists, listOfAllTopLevelProjects, selectedObjects, previousForm, updateOnDone,
                labelMaker, 1, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
    }

    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllTopLevelProjects, Object selectedObject,
            MyForm previousForm, Runnable updateOnDone, GetStringFrom labelMaker, int minNbOfSelected, int maxNbOfSelected,
            boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
        this(title, listOfAllLists, listOfAllTopLevelProjects, (List) new ArrayList(Arrays.asList(selectedObject)), previousForm, updateOnDone, labelMaker, minNbOfSelected,
                maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
    }

    /**
     *
     * @param title
     * @param listOfAllLists
     * @param listOfAllTopLevelProjects
     * @param selectedObjects
     * @param previousForm
     * @param updateOnDone
     * @param labelMaker
     * @param minNbOfSelected
     * @param maxNbOfSelected
     * @param removeFirstAddedObjectIfMoreThanMaxAreAdded
     * @param scrollToFirstSelected
     * @param exitWhenMaxObjectsIsSelected
     */
    public ScreenObjectPicker(String title, List listOfAllLists, List listOfAllTopLevelProjects, List selectedObjects,
            MyForm previousForm, Runnable updateOnDone, GetStringFrom labelMaker, int minNbOfSelected, int maxNbOfSelected,
            boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
        super(title, previousForm, updateOnDone);
        assert maxNbOfSelected >= minNbOfSelected && maxNbOfSelected >= 1;
        if (listOfAllLists != null) //may be null
        {
            this.listOfAllLists1 = new ArrayList(listOfAllLists); //make a copy to it can be modified if adding missing elements in code below
        }
        if (listOfAllTopLevelProjects != null) //may be null
        {
            this.listOfAllTopLevelProjects1 = new ArrayList(listOfAllTopLevelProjects); //make a copy to it can be modified if adding missing elements in code below
        }//        this.listOfAllTasks = listOfAllTasks;
        this.selectedObjects1 = selectedObjects;
        this.minNbOfSelected = minNbOfSelected;
        this.maxNbOfSelected = maxNbOfSelected;
        this.labelMaker = labelMaker;
        this.scrollToFirstSelected = scrollToFirstSelected;
        this.exitWhenMaxObjectsIsSelected = exitWhenMaxObjectsIsSelected;

        //add any current elements in the selectedObjects which are NOT in the listOfAllLists/listOfAllTopLevelProjects, necessary eg for Inbox which is not a 'user' list (NB. This approach might be a bit of hack)
        if (selectedObjects1 != null && selectedObjects1.size() > 0) {
            if (selectedObjects1.get(0) instanceof ItemList) {
                for (ItemList itemList : (List<ItemList>) selectedObjects1) {
                    if (!listOfAllLists1.contains(itemList)) {
                        listOfAllLists1.add(0, itemList); //TODO!!!! if multiple elements in selectedObjects, then they are added in *reverse* order to the list (not an issue as long as ObjectPicker is only used to pick Owner)
                    }
                }
            } else if (selectedObjects1.get(0) instanceof Item) {
                for (Item item : (List<Item>) selectedObjects1) {
                    if (!listOfAllTopLevelProjects1.contains(item)) {
                        listOfAllTopLevelProjects1.add(0, item);
                    }
                }
            }
        }

        listSelector = new ListSelector(this.selectedObjects1, false, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, (obj, selected) -> {
            int idx = -1;
            if (displayedList == this.listOfAllLists1) {
                idx = this.listOfAllLists1.indexOf(obj);
            } else if (displayedList == this.listOfAllTopLevelProjects1) {
                idx = this.listOfAllTopLevelProjects1.indexOf(obj);
            }
            if (idx != -1) {
                checkBoxes[idx].setSelected(selected);
            }
        }, minNbOfSelected == 0); //allow no selection

        //set error message if wrong number selected when clicking Back
//        this.errorInSelection = "Please select "+(minNbOfSelected>0?"":"")+(maxNbOfSelected>0?"":"");
        if (this.minNbOfSelected > 0) {
            if (this.maxNbOfSelected == this.minNbOfSelected) {
//                errorMsgInSelection = "Please select 1 element");
//                errorMsgInSelection = "Please select %i elements";
                errorMsgInSelection = "Please select " + this.minNbOfSelected + " element" + (this.minNbOfSelected > 1 ? "s" : "");
            } else if (this.maxNbOfSelected > this.minNbOfSelected) {
//                errorMsgInSelection = "Please select between %i and %i elements";
                errorMsgInSelection = "Please select between " + this.minNbOfSelected + " and " + this.maxNbOfSelected + " elements";
            }
//            else {
//                errorMsgInSelection = "Please select at least " + minNbOfSelected + " elements";
//            }
        } else {//if (maxNbOfSelected > 0) { //minNbOfSelected==0
//            errorMsgInSelection = "Please select 1 element at most";
//            errorMsgInSelection = "Please select %i elements at most";
//            errorMsgInSelection = "Please select " + (this.maxNbOfSelected + 1) + " element" + (this.maxNbOfSelected > 1 ? "s" : "") + " at most";
            if (this.maxNbOfSelected > 1) {
                errorMsgInSelection = "Please select " + (this.maxNbOfSelected + 1) + " elements at most";
            } else {
                errorMsgInSelection = "Please select " + (this.maxNbOfSelected + 1) + " element at most";
            }
        }

        if (this.labelMaker == null) {
            this.labelMaker = (l) -> (String) l;  //default label maker, no transformation of string
        }

//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(false); //disable scrolling of form, necessary to let lists handle their own scrolling 
        listCont = new Container(BoxLayout.y());
        listCont.setScrollableY(true); //disable scrolling of form, necessary to let lists handle their own scrolling 
        getContentPane().add(BorderLayout.CENTER, listCont);
        addCommandsToToolbar(getToolbar());

        getToolbar().addSearchCommand((e) -> {
            String text = (String) e.getSource();
            boolean showAll = (text == null || text.length() == 0);
//            for (int i = 0, size = this.listOfAllLists.size(); i < size; i++) {
            for (int i = 0, size = this.checkBoxes.length; i < size; i++) {
//                    checkBoxes[i].setHidden(!(showAll || labelMaker.get(listOfAllCategories.get(i)).equals(text))); //TODO!!! compare same case (upper/lower)
//                checkBoxes[i].setHidden(!(showAll || labelMaker.get(this.listOfAllObjects.get(i)).toLowerCase().indexOf(text) > -1)); //https://www.codenameone.com/blog/toolbar-search-mode.html
                checkBoxes[i].setHidden(!(showAll || allLabels.get(i).indexOf(text) > -1)); //https://www.codenameone.com/blog/toolbar-search-mode.html
//                    checkBoxes[i].setVisible((showAll || labelMaker.get(listOfAllCategories.get(i)).toLowerCase().indexOf(text)>-1)); //https://www.codenameone.com/blog/toolbar-search-mode.html
            }
//            getContentPane().animateLayout(150);
            animateMyForm();
        }, MyPrefs.defaultIconSizeInMM.getFloat());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
//        getContentPane().removeAll();
//        buildContentPane(getContentPane()); //, this.selectedCategories);
        listCont.removeAll();
        buildContentPane(listCont); //, this.selectedCategories);
        revalidate();
        super.refreshAfterEdit();
//        animateMyForm();
    }

    /**
     * return true if (possibly modified) category can be saved
     */
//    public  boolean checkItemListIsValidForSaving(ItemList itemList) {
    public boolean checkObjectChoiceIsValid(int nbSelectedItems) {
        String errorMsg = (nbSelectedItems >= minNbOfSelected && nbSelectedItems <= maxNbOfSelected)
                ? null : (minNbOfSelected == maxNbOfSelected ? "Please select 1 element" : Format.f("Please select {0} to {1} elements", "" + minNbOfSelected, "" + maxNbOfSelected));
        if (errorMsg != null) {
            Dialog.show("Error", errorMsg, "OK", null);
            return false;
        } else {
            return true;
        }
    }

    public void addCommandsToToolbar(Toolbar toolbar) {

        super.addCommandsToToolbar(toolbar);
        //if (objectCreator!=null)
//        toolbar.addCommandToRightBar(ScreenListOfCategories.makeNewCategoryCmd(listOfAllObjects, ScreenObjectPicker.this)); //TODO!!!! enable adding new elements to picker screen
        setCheckIfSaveOnExit(() -> checkObjectChoiceIsValid(selectedObjects1.size()));
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(true)); //false: don't refresh ScreenItem when returning from Category selector
        addStandardBackCommand();

        if (true || MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) { //UI: always enable Cancel to make it easy to regret any changes
            toolbar.addCommandToOverflowMenu(
                    "Cancel", null, (e) -> {
//                        showPreviousScreenOrDefault(previousForm, false); //restore originally selected categories
                        if (false) {
                            showPreviousScreen(false); //restore originally selected categories
                        } else {
                            showPreviousScreen(true); //use true for consistency
                        }
                    }
            );
        }

    }

    private Container buildList(List listOfAllObjects, Container cont) {
        parseIdMap2.parseIdMapReset();
        cont.removeAll();

        //maintain a backup of previously selected objects when having more than one list
        if (displayedList != null && listOfAllObjects != displayedList) {
            if (displayedList == listOfAllLists1) {
//        if (displayedList==listOfAllLists && listOfAllObjects!=displayedList){
                //save current selection for listOfAllLists to its backup
                listOfAllListsSelectionBackup.clear();
                listOfAllListsSelectionBackup.addAll(selectedObjects1);
                //move previous selection into selectedObjects
                selectedObjects1.clear();
                selectedObjects1.addAll(listOfAllTopLevelProjectsSelectionBackup);
            } else if (listOfAllObjects != displayedList && displayedList == listOfAllTopLevelProjects1) {
                //save current selection for listOfAllTopLevelProjects to its backup
                listOfAllTopLevelProjectsSelectionBackup.clear();
                listOfAllTopLevelProjectsSelectionBackup.addAll(selectedObjects1);
                //move previous selection into selectedObjects
                selectedObjects1.clear();
                selectedObjects1.addAll(listOfAllListsSelectionBackup);
            }
        }

        displayedList = listOfAllObjects;
        checkBoxes = new CheckBox[listOfAllObjects.size()];
        allLabels = new ArrayList();
//        selectedObjects = new ArrayList();
        CheckBox firstSelectedChk = null;
        //DONE!!!! replace InfiniteContainer by a simpler structure
        for (int i = 0, size = listOfAllObjects.size(); i < size; i++) {
            ItemAndListCommonInterface obj = (ItemAndListCommonInterface) listOfAllObjects.get(i);
            String label = labelMaker.get(obj.getText());
            allLabels.add(label.toLowerCase()); //store the label in lowercase for faster search
            CheckBox chk = CheckBox.createToggle(label);
            boolean selected = selectedObjects1.contains(obj);
            chk.setSelected(selected);
            if (scrollToFirstSelected && selected && firstSelectedChk == null) {
                firstSelectedChk = chk;
            }
            checkBoxes[i] = chk;
            cont.add(chk);
            chk.addActionListener((ActionEvent evt) -> {
                listSelector.flipSelection(obj);
                if (exitWhenMaxObjectsIsSelected && listSelector.isMaxNumberSelected()) {
                    backCommand.actionPerformed(null);
                }
            }
            );
        }
        if (scrollToFirstSelected && firstSelectedChk != null) {
            scrollComponentToVisible(firstSelectedChk);
        }
        return cont;
//        }
    }

    private Container buildContentPane(Container cont) {//, Set<Category> selectedCategories) {
        boolean listOfLists = true;
        if (listOfLists) {
//            Button cmdLists = null;
            RadioButton buttonLists = (listOfAllLists1 != null && listOfAllLists1.size() > 0) ? new RadioButton("Lists", null) : null;
//            Button cmdProjects = null;
            RadioButton buttonProjects = (listOfAllTopLevelProjects1 != null && listOfAllTopLevelProjects1.size() > 0) ? new RadioButton("Projects", null) : null;
//<editor-fold defaultstate="collapsed" desc="comment">
//            Button cmdTasks = null;
//            List<Button> cmds = new ArrayList();

//            if (listOfAllLists != null && listOfAllLists.size() > 0) {
////                cmdLists = new Button(Command.create("Lists", null, (e) -> {
//                buttonLists = new RadioButton("Lists", null);
////                , (e) -> {
////                    buildList(listOfAllLists, cont);
//////                    cmdLists.setToggle(listOfLists);
////                    animateMyForm();
////                }));
////                cmds.add(cmdLists);
//            }
//</editor-fold>
            if (buttonLists != null) {
                buttonLists.setToggle(true);
                buttonLists.setUIID("ObjectSelectorRadioButton");
                buttonLists.addActionListener((e) -> {
                    buildList(listOfAllLists1, cont);
//                    cmdLists.setToggle(listOfLists);
                    animateMyForm();
                });
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (listOfAllTopLevelProjects != null && listOfAllTopLevelProjects.size() > 0) {
//                buttonProjects = new RadioButton("Projects", null);
////                cmdProjects = new Button(Command.create("Projects", null, (e) -> {
////                    buildList(listOfAllTopLevelProjects, cont);
////                    animateMyForm();
////                }));
////                cmds.add(cmdProjects);
//                buttonProjects.addActionListener((e2) -> {
//                    buildList(listOfAllTopLevelProjects, cont);
//                    buttonLists.setSelected(false);
//                    animateMyForm();
//                });
//            }
//</editor-fold>
            if (buttonProjects != null) {
                buttonProjects.setToggle(true);
                buttonProjects.setUIID("ObjectSelectorRadioButton");
                buttonProjects.addActionListener((e2) -> {
                    buildList(listOfAllTopLevelProjects1, cont);
                    buttonLists.setSelected(false);
                    animateMyForm();
                });
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (listOfAllTasks != null && listOfAllTasks.size() > 0) {
//                cmdTasks = new Button(Command.create("Tasks", null, (e) -> {
//                    buildList(listOfAllTasks, cont);
//                    animateMyForm();
//                }));
//                cmds.add(cmdTasks);
//            }
//            assert cmds.size() > 0 : "must have at least one list";
//            if (cmds.size() == 1) {
////                cmds.get(0).getCommand().actionPerformed(null); //run command to show the 
//            } else {
//</editor-fold>
            if (buttonLists != null && buttonProjects != null) {
                ButtonGroup buttonGroup = new ButtonGroup(buttonLists, buttonProjects); //NB, buttonGroup ensure selection mutuality btw the buttons
//                Container butCont = new Container(new GridLayout(cmds.size()));
                Container butCont = new Container(new GridLayout(2));
//                for (Button b : cmds) {
//                    butCont.add(b);
//                }
                butCont.add(buttonLists);
                butCont.add(buttonProjects);
                add(BorderLayout.SOUTH, butCont);
            }
            ItemAndListCommonInterface firstSelectedObj;
            if (selectedObjects1.size() > 0) { //if we already have element(s) selected
                firstSelectedObj = (ItemAndListCommonInterface) selectedObjects1.get(0); //get first selected object (or any element, just to get its type)
                if (firstSelectedObj instanceof ItemList) {
//                    cmdLists.getCommand().actionPerformed(null);
                    buildList(listOfAllLists1, cont);
                    buttonLists.setSelected(true);
                } else if (firstSelectedObj instanceof Item) {
                    if (true || ((Item) firstSelectedObj).isProject()) { //selected element a project //TODO!!!: no need to test on isProject??
//                        cmdProjects.getCommand().actionPerformed(null);
                        buildList(listOfAllTopLevelProjects1, cont);
                        buttonProjects.setSelected(true);
                    }
                } else { //UI: if no selected elements, always show list of Lists first
                    buildList(listOfAllLists1, cont);
                }
//                else {
//                    cmdTasks.getCommand().actionPerformed(null);
//                }
//            } else {
//                cmdLists.getCommand().actionPerformed(null); //UI: Lists default
            } else {
                buildList(listOfAllLists1, cont);
            }

        } else { //not currently activated!
            parseIdMap2.parseIdMapReset();
            cont.removeAll();
            checkBoxes = new CheckBox[listOfAllLists1.size()];
            allLabels = new ArrayList();
//        selectedObjects = new ArrayList();
            CheckBox firstSelectedChk = null;
            //DONE!!!! replace InfiniteContainer by a simpler structure
            for (int i = 0, size = listOfAllLists1.size(); i < size; i++) {
                ItemAndListCommonInterface obj = (ItemAndListCommonInterface) listOfAllLists1.get(i);
                String label = labelMaker.get(obj.getText());
                allLabels.add(label.toLowerCase()); //store the label in lowercase for faster search
                CheckBox chk = CheckBox.createToggle(label);
                if (scrollToFirstSelected && firstSelectedChk == null) {
                    firstSelectedChk = chk;
                }
                checkBoxes[i] = chk;
                chk.setSelected(selectedObjects1.contains(obj));
                cont.add(chk);
                chk.addActionListener((ActionEvent evt) -> {
                    listSelector.flipSelection(obj);
                    if (exitWhenMaxObjectsIsSelected && listSelector.isMaxNumberSelected()) {
                        backCommand.actionPerformed(null);
                    }
                }
                );
            }
            if (scrollToFirstSelected) {
                scrollComponentToVisible(firstSelectedChk);
            }
        }
        return cont;
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
