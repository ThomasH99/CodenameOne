/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import java.util.List;
//import com.java4less.rchart.*;
//import javax.microedition.io.ConnectionNotFoundException;
//import javax.microedition.io.PushRegistry;
//import javax.microedition.m3g.Background;
//import org.joda.time.base.BaseInterval;

/**
 * Screen to select Categories for an Item
 *
 * @author Thomas
 */
public class ScreenCategoryPicker extends MyForm {

    //TODO 
    //TODO implement sorting categories, default manual, by alphabetical, most recently used, #tasks, #hours of work
    //TODO implement search on categories
    //TODO implement sorting categories by location (set a location for a category)??
//<editor-fold defaultstate="collapsed" desc="comment">
//    Map<String, GetParseValue> parseIdMap;
//    MyForm previousForm;
//    List<Category> listOfAllCategories;
//</editor-fold>
    CategoryList listOfAllCategories;
//    Set<Category> selectedCategories;
    List<Category> selectedCategories;
    Command backCommand;
//<editor-fold defaultstate="collapsed" desc="comment">
//    Set<Category> orgSelectedCategories;
//    Set<Category> addedCategories=new HashSet(); //set of categories added by user on this screen
//    Set<Category> unselectedCategories=new HashSet(); //set of categories unselected by user on this screen
//    Set<Category> selectedCategoriesOriginal;
//    Item item;
    
//    ScreenCategoryPicker(List categories, Set<Category> selectedCategories, Form previousForm) { //throws ParseException, IOException {
//    ScreenCategoryPicker(List categories, Item item, MyForm previousForm, UpdateField updateAfterEdit) { //throws ParseException, IOException {
//        this(categories, item, previousForm);
//    }
//
//    ScreenCategoryPicker(List categories, Item item, MyForm previousForm) { //throws ParseException, IOException {
//        this("Select categories", categories, item, previousForm);
//    }
//    ScreenCategoryPicker(List<Category> listOfAllCategories, Set<Category> selectedCategories, MyForm previousForm) {
//    ScreenCategoryPicker(CategoryList listOfAllCategories, Set<Category> selectedCategories, MyForm previousForm) {
//</editor-fold>
    ScreenCategoryPicker(CategoryList listOfAllCategories, List<Category> selectedCategories, MyForm previousForm, UpdateField updateOnDone) {
        this("Select Categories", listOfAllCategories, selectedCategories, previousForm, updateOnDone);
    }

//    ScreenCategoryPicker(String title, CategoryList listOfAllCategories, Set<Category> selectedCategories, MyForm previousForm, UpdateField updateOnDone) { //throws ParseException, IOException {
    ScreenCategoryPicker(String title, CategoryList listOfAllCategories, List<Category> selectedCategories, MyForm previousForm, UpdateField updateOnDone) { //throws ParseException, IOException {
//        this(title, categories, item.getCategories(), previousForm);
//    }
//    ScreenCategoryPicker(String title, List<Category> categories, Set<Category> selectedCategories, Form previousForm) { //throws ParseException, IOException {
        super(title, previousForm, updateOnDone);
//        super(title, updateOnDone);
        this.listOfAllCategories = listOfAllCategories;
//        this.orgSelectedCategories = selectedCategories;
//        this.selectedCategories = new HashSet(selectedCategories);
        this.selectedCategories = selectedCategories;
//        this.selectedCategoriesOriginal = new HashSet(selectedCategories);
//        this.previousForm = previousForm;
//        this.updateOnDone = updateOnDone;
//        this.item = item;
        setPinchInsertEnabled(true);
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        setScrollable(false); //disable scrolling of form, necessary to let lists handle their own 
        setScrollableY(true); //disable scrolling of form, necessary to let lists handle their own scrolling 
//        addSearchToTitle();
//        setToolbar(new Toolbar());
        addCommandsToToolbar(getToolbar());
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) getToolbar().addSearchCommand((e) -> {
//            String text = (String) e.getSource();
//            Container compList = getContentPane();
//            boolean showAll = text == null || text.length() == 0;
////            for (int i = 0, size = this.listOfAllCategories.size(); i < size; i++) {
//            for (int i = 0, size = this.listOfAllCategories.getList().size(); i < size; i++) {
//                //TODO!!! compare same case (upper/lower)
//                //https://www.codenameone.com/blog/toolbar-search-mode.html:
//                compList.getComponentAt(i).setHidden(((Category) this.listOfAllCategories.get(i)).getText().toLowerCase().indexOf(text) < 0);
//            }
//            compList.animateLayout(150);
//        });
//</editor-fold>
        getToolbar().addSearchCommand(makeSearchFunctionSimple(listOfAllCategories,()->getContentPane()));
//        buildContentPane(getContentPane(), listOfAllCategories); //, this.selectedCategories);
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
//        buildContentPane(getContentPane(), listOfAllCategories); //, this.selectedCategories);
        buildContentPane(getContentPane(), listOfAllCategories.getListFull()); //, this.selectedCategories); //normally full to show all categories (could be filtered later)
//        revalidate();
//        restoreKeepPos();
        super.refreshAfterEdit();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    Set<Category> getUnselectedCategories() {
//        return unselectedCategories;
//    }
//
//    Set<Category> getAddedCategories() {
//        return addedCategories;
//    }
//    Set<Category> getSelectedCategories() {
//        return selectedCategories;
//    }
//</editor-fold>
    void addSearchToTitle() {
        //TODO update to use standard title bar search
        //below code from Toolbar section https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#toolbar
        Toolbar.setGlobalToolbar(true);
        Style s = UIManager.getInstance().getComponentStyle("Title");

        Form hi = new Form("Toolbar", new BoxLayout(BoxLayout.Y_AXIS));
        //SEARCH
        TextField searchField = new TextField("", "Toolbar Search");
        searchField.getHintLabel().setUIID("Title");
        searchField.setUIID("Title");
        searchField.getAllStyles().setAlignment(Component.LEFT);
        hi.getToolbar().setTitleComponent(searchField);
        FontImage searchIcon = FontImage.createMaterial(FontImage.MATERIAL_SEARCH, s);
        searchField.addDataChangeListener((i1, i2) -> {
            String t = searchField.getText();
            if (t.length() < 1) {
                for (Component cmp : hi.getContentPane()) {
                    cmp.setHidden(false);
                    cmp.setVisible(true);
                }
            } else {
                t = t.toLowerCase();
                for (Component cmp : hi.getContentPane()) {
                    String val = null;
                    if (cmp instanceof Label) {
                        val = ((Label) cmp).getText();
                    } else if (cmp instanceof TextArea) {
                        val = ((TextArea) cmp).getText();
                    } else {
                        val = (String) cmp.getPropertyValue("text");
                    }
                    boolean show = val != null && val.toLowerCase().indexOf(t) > -1;
                    cmp.setHidden(!show);
                    cmp.setVisible(show);
                }
            }
            hi.getContentPane().animateLayout(250);
        });
        hi.getToolbar().addCommandToRightBar("", searchIcon, (e) -> {
            searchField.startEditingAsync();
        });
        //abovecode from Toolbar section https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#toolbar

    }

    public void addCommandsToToolbar(Toolbar toolbar) {

//<editor-fold defaultstate="collapsed" desc="comment">
//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
////        tool.addCommandToLeftBar("Done", icon, (e) -> Log.p("Clicked"));
//        toolbar.addCommandToRightBar("", icon, (e) -> {
//            new ScreenCategory(new Category(), this).show();
//        });
//</editor-fold>
        toolbar.addCommandToRightBar(ScreenListOfCategories.makeNewCategoryCmd(listOfAllCategories, ScreenCategoryPicker.this, () -> refreshAfterEdit()));

//<editor-fold defaultstate="collapsed" desc="comment">
//        icon = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle());
//        toolbar.addCommandToLeftBar("", icon, (e) -> {
////                    item.setCategories(selectedCategories);
////                    previousForm.refreshAfterEdit();
//            if (updateOnDone != null) {
//                updateOnDone.update();
//            }
//            previousForm.showBack();
//        });
//        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand());
//</editor-fold>
//backCommand = makeDoneUpdateWithParseIdMapCommand(true); //false);
//        toolbar.setBackCommand(backCommand); //false: don't refresh ScreenItem when returning from Category selector
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(true)); //false: don't refresh ScreenItem when returning from Category selector

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu(
                    "Cancel", null, (e) -> {
                        //restore originally selected categories
//<editor-fold defaultstate="collapsed" desc="comment">
//                    selectedCategories.clear();
//                    selectedCategories.addAll(selectedCategoriesOriginal);
//                    previousForm.revalidate();
//                    previousForm.showBack();
//</editor-fold>
//                        showPreviousScreenOrDefault(previousForm, false);
                        showPreviousScreenOrDefault( false);
                    }
            );
        }

        addSearchToTitle();

//        ADDITIONAL COMMANDS
//        DONE: Add new category
//        Sort categories by Name (default), by creation date (most recent first), by most used (#tasks), by used most recently?, by ??
//        Search w autocomplete
    }

    private Container buildContentPane(Container cont, List categories) {//, Set<Category> selectedCategories) {

        for (int i = 0, size = categories.size(); i < size; i++) {
            Category cat = (Category) categories.get(i);
            CheckBox cmps = CheckBox.createToggle(cat.getText());
            cmps.setSelected(selectedCategories.contains(cat));
            cmps.addActionListener((ActionEvent evt) -> {
                if (((CheckBox) evt.getSource()).isSelected()) {
                    selectedCategories.add(cat);
                } else {
                    selectedCategories.remove(cat);
                }
            });
            cont.add(cmps);
        }
        return cont;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container buildContentPaneOLD(Container cont, List categories) {//, Set<Category> selectedCategories) {
//
//        //TODO!!!! replace InfiniteContainer by a simpler structure
//        InfiniteContainer ic = new InfiniteContainer() {
//            @Override
//            public Component[] fetchComponents(int index, int amount) {
////        java.util.List<Map<String, Object>> data = fetchPropertyData("Leeds");
//                List<Category> data = categories.subList(index, Math.min(index + amount - 1, categories.size()));
//                CheckBox[] cmps = new CheckBox[data.size()];
//                for (int iter = 0; iter < cmps.length; iter++) {
//                    Category cat = data.get(iter);
//                    if (cat == null) {
//                        return null;
//                    }
//                    cmps[iter] = CheckBox.createToggle(cat.getText());
//                    cmps[iter].setSelected(selectedCategories.contains(cat));
//                    cmps[iter].addActionListener((ActionEvent evt) -> {
//                        if (((CheckBox) evt.getSource()).isSelected()) {
//                            selectedCategories.add(cat);
////<editor-fold defaultstate="collapsed" desc="comment">
////add new selected categories to addedCategories
////                            if (!orgSelectedCategories.contains(cat)) {
////                                addedCategories.add(cat);
////                            }
////                            //remove previously unselected categories from unselectedCategories
////                            unselectedCategories.remove(cat);
////</editor-fold>
//                        } else {
//                            selectedCategories.remove(cat);
////<editor-fold defaultstate="collapsed" desc="comment">
////                            if (orgSelectedCategories.contains(cat)) {
////                                unselectedCategories.add(cat);
////                            }
////                            //remove previously added categories from addedCategories
////                            addedCategories.remove(cat);
////</editor-fold>
//                        }
//                    });
//                }
//                return cmps;
//            }
//        };
//        cont.add(ic);
//        return cont;
//    }
//</editor-fold>

}
