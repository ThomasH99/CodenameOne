/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Label;
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

    //TODO!!! support expanding projects to show subprojects and task to allow to pick any subproject level - or replace this screen by ScreenListOfItems??
    //DONE parameter with list(s!) of elements to pick from. 
    //TODO enable expansion of eg Templates to see their subtasks, and to edit them, so basically same list as 
    //TODO!! implement sorting of objects (eg categories), default manual, alphabetically, most recently used, #tasks, #hours of work
    //TODO! implement auto-selection of categories matching search string, e.g. if only one category shown, and press Back, then select that category (or more) ->?? not intuitive when it 'triggers' and not visible which are selected
    //DONE implement search on objects (eg categories)
    private List listOfAllLists;
    private List listOfAllListsSelectionBackup = new ArrayList<E>();
    private List listOfAllTopLevelProjects;
    private List listOfAllTopLevelProjectsSelectionBackup = new ArrayList<E>();
    private List displayedList;
//    private List listOfAllTasks;
//    private List selectedObjects;  
    private List<String> allLabels;//= new ArrayList();
//    Set<Category> selectedCategories;
    private List selectedObjects;
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
    ScreenObjectPicker(String title, List listOfAllObjects, ItemAndListCommonInterface owner, MyForm previousForm, UpdateField updateOnDone) {
        this(title, listOfAllObjects, null, Arrays.asList(owner), previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
    }

    ScreenObjectPicker(String title, List listOfAllObjects, List<ItemAndListCommonInterface> ownerList, MyForm previousForm, UpdateField updateOnDone) {
        this(title, listOfAllObjects, null, ownerList, previousForm, updateOnDone, null, 1, true, false, false); //removeFirstAddedObjectIfMoreThanMaxAreAdded=true since we can only select one and it's natural to deselect previously selected 
    }

    ScreenObjectPicker(String title, List listOfAllObjects, List listOfAllTopLevelProjects, List<ItemAndListCommonInterface> ownerList, MyForm previousForm, UpdateField updateOnDone) {
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
    ScreenObjectPicker(String title, List listOfAllLists, List selectedObjects, MyForm previousForm, UpdateField updateOnDone,
            GetStringFrom labelMaker, int maxNbOfSelected, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
        this(title, listOfAllLists, null, selectedObjects, previousForm, updateOnDone, labelMaker, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
    }
//    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllProjects, List listOfAllTasks, List selectedObjects, MyForm previousForm, UpdateField updateOnDone,

    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllTopLevelProjects, List selectedObjects,
            MyForm previousForm, UpdateField updateOnDone,
            GetStringFrom labelMaker, int maxNbOfSelected, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected,
            boolean exitWhenMaxObjectsIsSelected) {
        this(title, listOfAllLists, listOfAllTopLevelProjects, selectedObjects, previousForm, updateOnDone, labelMaker, 1, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, scrollToFirstSelected, exitWhenMaxObjectsIsSelected);
    }

    ScreenObjectPicker(String title, List listOfAllLists, List listOfAllTopLevelProjects, List selectedObjects,
            MyForm previousForm, UpdateField updateOnDone,
            GetStringFrom labelMaker, int minNbOfSelected, int maxNbOfSelected,
            boolean removeFirstAddedObjectIfMoreThanMaxAreAdded, boolean scrollToFirstSelected, boolean exitWhenMaxObjectsIsSelected) {
        super(title, previousForm, updateOnDone);
        assert maxNbOfSelected >= minNbOfSelected && maxNbOfSelected >= 1;
        this.listOfAllLists = listOfAllLists;
        this.listOfAllTopLevelProjects = listOfAllTopLevelProjects;
//        this.listOfAllTasks = listOfAllTasks;
        this.selectedObjects = selectedObjects;
        this.minNbOfSelected = minNbOfSelected;
        this.maxNbOfSelected = maxNbOfSelected;
        this.labelMaker = labelMaker;
        this.scrollToFirstSelected = scrollToFirstSelected;
        this.exitWhenMaxObjectsIsSelected = exitWhenMaxObjectsIsSelected;

        listSelector = new ListSelector(selectedObjects, false, maxNbOfSelected, removeFirstAddedObjectIfMoreThanMaxAreAdded, (obj, selected) -> {
            int idx = -1;
            if (displayedList == this.listOfAllLists) {
                idx = this.listOfAllLists.indexOf(obj);
            } else if (displayedList == this.listOfAllTopLevelProjects) {
                idx = this.listOfAllTopLevelProjects.indexOf(obj);
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
            errorMsgInSelection = "Please select 1 element at most";
            errorMsgInSelection = "Please select %i elements at most";
            errorMsgInSelection = "Please select " + (this.maxNbOfSelected + 1) + " element" + (this.maxNbOfSelected > 1 ? "s" : "") + " at most";
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
        });

        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
//        getContentPane().removeAll();
//        buildContentPane(getContentPane()); //, this.selectedCategories);
        listCont.removeAll();
        buildContentPane(listCont); //, this.selectedCategories);
        revalidate();
//        animateMyForm();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {

        //if (objectCreator!=null)
//        toolbar.addCommandToRightBar(ScreenListOfCategories.makeNewCategoryCmd(listOfAllObjects, ScreenObjectPicker.this)); //TODO!!!! enable adding new elements to picker screen
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(false,
                //                () -> (listSelector.getSelected().size() >= minNbOfSelected||listSelector.getSelected().size() <= maxNbOfSelected),
                () -> (selectedObjects.size() >= minNbOfSelected && selectedObjects.size() <= maxNbOfSelected),
                errorMsgInSelection)); //false: don't refresh ScreenItem when returning from Category selector

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu(
                    "Cancel", null, (e) -> {
                        showPreviousScreenOrDefault(previousForm, false); //restore originally selected categories
                    }
            );
        }

    }

    private Container buildList(List listOfAllObjects, Container cont) {
        parseIdMapReset();
        cont.removeAll();

        //maintain a backup of previously selected objects when having more than one list
        if (displayedList != null && listOfAllObjects != displayedList) {
            if (displayedList == listOfAllLists) {
//        if (displayedList==listOfAllLists && listOfAllObjects!=displayedList){
                //save current selection for listOfAllLists to its backup
                listOfAllListsSelectionBackup.clear();
                listOfAllListsSelectionBackup.addAll(selectedObjects);
                //move previous selection into selectedObjects
                selectedObjects.clear();
                selectedObjects.addAll(listOfAllTopLevelProjectsSelectionBackup);
            } else if (listOfAllObjects != displayedList && displayedList == listOfAllTopLevelProjects) {
                //save current selection for listOfAllTopLevelProjects to its backup
                listOfAllTopLevelProjectsSelectionBackup.clear();
                listOfAllTopLevelProjectsSelectionBackup.addAll(selectedObjects);
                //move previous selection into selectedObjects
                selectedObjects.clear();
                selectedObjects.addAll(listOfAllListsSelectionBackup);
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
            boolean selected = selectedObjects.contains(obj);
            if (scrollToFirstSelected && selected && firstSelectedChk == null) {
                firstSelectedChk = chk;
            }
            checkBoxes[i] = chk;
            chk.setSelected(selected);
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
        return cont;
//        }
    }

    private Container buildContentPane(Container cont) {//, Set<Category> selectedCategories) {
        boolean listOfLists = true;
        if (listOfLists) {
            Button cmdLists = null;
            Button cmdProjects = null;
//            Button cmdTasks = null;
            List<Button> cmds = new ArrayList();

            if (listOfAllLists != null && listOfAllLists.size() > 0) {
                cmdLists = new Button(Command.create("Lists", null, (e) -> {
                    buildList(listOfAllLists, cont);

                    animateMyForm();
                }));
                cmds.add(cmdLists);
            }
            if (listOfAllTopLevelProjects != null && listOfAllTopLevelProjects.size() > 0) {
                cmdProjects = new Button(Command.create("Projects", null, (e) -> {
                    buildList(listOfAllTopLevelProjects, cont);
                    animateMyForm();
                }));
                cmds.add(cmdProjects);
            }
//            if (listOfAllTasks != null && listOfAllTasks.size() > 0) {
//                cmdTasks = new Button(Command.create("Tasks", null, (e) -> {
//                    buildList(listOfAllTasks, cont);
//                    animateMyForm();
//                }));
//                cmds.add(cmdTasks);
//            }
            assert cmds.size() > 0 : "must have at least one list";
            if (cmds.size() == 1) {
//                cmds.get(0).getCommand().actionPerformed(null); //run command to show the 
            } else {
                Container butCont = new Container(new GridLayout(cmds.size()));
                for (Button b : cmds) {
                    butCont.add(b);
                }
                add(BorderLayout.SOUTH, butCont);
            }
            ItemAndListCommonInterface firstSelectedObj;
            if (selectedObjects.size() > 0) { //if we already have element(s) selected
                firstSelectedObj = (ItemAndListCommonInterface) selectedObjects.get(0); //get first selected object (or any element, just to get its type)
                if (firstSelectedObj instanceof ItemList) {
//                    cmdLists.getCommand().actionPerformed(null);
                    buildList(listOfAllLists, cont);
                } else if (firstSelectedObj instanceof Item) {
                    if (((Item) firstSelectedObj).isProject()) { //selected element a project
//                        cmdProjects.getCommand().actionPerformed(null);
                        buildList(listOfAllTopLevelProjects, cont);
                    }
                } else { //UI: if no selected elements, always show list of Lists first
                    buildList(listOfAllLists, cont);
                }
//                else {
//                    cmdTasks.getCommand().actionPerformed(null);
//                }
//            } else {
//                cmdLists.getCommand().actionPerformed(null); //UI: Lists default
            } else {
                buildList(listOfAllLists, cont);
            }

        } else {
            parseIdMapReset();
            cont.removeAll();
            checkBoxes = new CheckBox[listOfAllLists.size()];
            allLabels = new ArrayList();
//        selectedObjects = new ArrayList();
            CheckBox firstSelectedChk = null;
            //DONE!!!! replace InfiniteContainer by a simpler structure
            for (int i = 0, size = listOfAllLists.size(); i < size; i++) {
                ItemAndListCommonInterface obj = (ItemAndListCommonInterface) listOfAllLists.get(i);
                String label = labelMaker.get(obj.getText());
                allLabels.add(label.toLowerCase()); //store the label in lowercase for faster search
                CheckBox chk = CheckBox.createToggle(label);
                if (scrollToFirstSelected && firstSelectedChk == null) {
                    firstSelectedChk = chk;
                }
                checkBoxes[i] = chk;
                chk.setSelected(selectedObjects.contains(obj));
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
//                objectPicker.setDoneUpdater(() -> {
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
