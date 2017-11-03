/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.CheckBox;
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
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.Image;
import com.codename1.ui.util.Resources;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

     Map<String, GetParseValue> parseIdMap;
     MyForm previousForm;
     List categories;
     Set<Category> selectedCategories;
     Set<Category> selectedCategoriesOriginal;
    Item item;
    

//    ScreenCategoryPicker(List categories, Set<Category> selectedCategories, Form previousForm) { //throws ParseException, IOException {
    ScreenCategoryPicker(List categories, Item item, MyForm previousForm) { //throws ParseException, IOException {
        this("Select categories", categories, item, previousForm);
    }

    ScreenCategoryPicker(String title, List<Category> categories, Item item, MyForm previousForm) { //throws ParseException, IOException {
//        this(title, categories, item.getCategories(), previousForm);
//    }
//    ScreenCategoryPicker(String title, List<Category> categories, Set<Category> selectedCategories, Form previousForm) { //throws ParseException, IOException {
        super(title);
        this.categories = categories;
        this.selectedCategories = item.getCategories();
        this.selectedCategoriesOriginal = new HashSet(selectedCategories);
        this.previousForm = previousForm;
        this.item = item;

        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollable(false); //disable scrolling of form, necessary to let lists handle their own 
        setScrollable(false); //disable scrolling of form, necessary to let lists handle their own scrolling 
        addSearchToTitle();
        setToolbar(new Toolbar());
        addCommandsToToolbar(getToolbar(), theme);
        buildContentPane(getContentPane(), categories, this.selectedCategories);
    }

    void addSearchToTitle() {
        //below code from Toolbar section https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#toolbar
        Toolbar.setGlobalToolbar(true);
        Style s = UIManager.getInstance().getComponentStyle("Title");

        Form hi = new Form("Toolbar", new BoxLayout(BoxLayout.Y_AXIS));
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

    public void addCommandsToToolbar(Toolbar toolbar, Resources theme) {

        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
//        tool.addCommandToLeftBar("Done", icon, (e) -> Log.p("Clicked"));
        toolbar.addCommandToRightBar("", icon, (e) -> {
            new ScreenCategory(new Category(), this).show();
        });
        icon = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle());
        toolbar.addCommandToLeftBar("", icon, (e) -> {
                    item.setCategories(selectedCategories);
                    previousForm.revalidate();
                    previousForm.showBack();
        });
        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens))
        toolbar.addCommandToOverflowMenu(
                "Cancel", null, (e) -> {
                    //restore originally selected categories
                    selectedCategories.clear();
                    selectedCategories.addAll(selectedCategoriesOriginal);
                    previousForm.revalidate();
                    previousForm.showBack();
                }
        );
        
        addSearchToTitle();

//        ADDITIONAL COMMANDS
//        Add new category
//        Sort categories by Name (default), by creation date (most recent first), by most used (#tasks), by used most recently?, by ??
//        Search w autocomplete
    }

    private Container buildContentPane(Container cont, List categories, Set<Category> selectedCategories) {

        InfiniteContainer ic = new InfiniteContainer() {
            @Override
            public Component[] fetchComponents(int index, int amount) {
//        java.util.List<Map<String, Object>> data = fetchPropertyData("Leeds");
                List<Category> data = categories.subList(index, Math.min(index + amount - 1, categories.size()));
                CheckBox[] cmps = new CheckBox[data.size()];
                for (int iter = 0; iter < cmps.length; iter++) {
                    Category cat = data.get(iter);
                    if (cat == null) {
                        return null;
                    }
                    cmps[iter] = CheckBox.createToggle(cat.getText());
                    cmps[iter].setSelected(selectedCategories.contains(cat));
                    cmps[iter].addActionListener((ActionEvent evt) -> {
                        if (((CheckBox) evt.getSource()).isSelected()) {
                            selectedCategories.add(cat);
                        } else {
                            selectedCategories.remove(cat);
                        }
                    });
                }
                return cmps;
            }
        };
        cont.add(ic);
        return cont;
    }

}
