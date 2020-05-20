/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.UIManager;

/**
 * returns a command (to add to toolbar) that will create a search container and
 * add it to NORTH of the contentPane
 *
 * @author thomashjelm
 */
public class MySearchCommand extends Command {

//    private Toolbar toolbar;
    private Container searchHolder;
     Container cont;
    private TextField search;
    private Object positionInHolder;
    String hintText;
//    private OnSearch onSearch;
    private ActionListener onSearch;
    char clearTextIcon;
    private KeepInSameScreenPosition keepPos; //restore scroll position after search

    MySearchCommand() {
        super("");
    }
//    interface OnSearch {
//
//        void doSearch(String text);
//    }
//    MySearchBar(Container searchHolder, Object positionInContainer, String hintTxt, char clearTextIcon, ActionListener onSrch){//OnSearch onSearch) {
//    public static MySearchBar createSearch(Object positionInContainer, String hintTxt, char clearTextIcon, ActionListener onSrch){//OnSearch onSearch) {

    public MySearchCommand(Container searchHolder, Object positionInHolder, String hintTxt, char clearTextIcon, ActionListener onSrch) {//OnSearch onSearch) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.searchHolder = searchHolder;
//        MySearchBar searchCmdXXX = new MySearchBar() {
////        this.onSearch = onSrch;
//        };
//</editor-fold>
        super("");
        this.searchHolder = searchHolder;
        this.positionInHolder = positionInHolder;
        this.hintText = hintTxt;
        this.clearTextIcon = clearTextIcon;
        this.onSearch=onSrch;
          setMaterialIcon(FontImage.MATERIAL_SEARCH);
         
//<editor-fold defaultstate="collapsed" desc="comment">
//        MySearchBar searchCmd = this;
//
//        searchCmd.cont = new Container();
//
////        searchHolder.addCommandToRightBar(Command.createMaterial("", FontImage.MATERIAL_SEARCH, (e) -> {hideShow();}));
//        searchCmd.setMaterialIcon(FontImage.MATERIAL_SEARCH);
//
//        if (hintTxt == null) {
//            hintTxt = "Search";
//        }
//        searchCmd.cont.setLayout(new BorderLayout());
//        searchCmd.cont.setUIID("SearchFieldContainer");
//
//        searchCmd.search = new TextField();
//        searchCmd.search.setUIID("SearchFieldText");
////<editor-fold defaultstate="collapsed" desc="comment">
////        search.putClientProperty("searchField", Boolean.TRUE);
////        Image img;
////        if(iconSize > 0) {
////            img = FontImage.createMaterial(FontImage.MATERIAL_SEARCH, UIManager.getInstance().getComponentStyle("TextHintSearch"), iconSize);
////        } else {
////            img = FontImage.createMaterial(FontImage.MATERIAL_SEARCH, UIManager.getInstance().getComponentStyle("TextHintSearch"));
////        }
////        String s = getUIManager().localize("m.search", "Search");
////</editor-fold>
//        if (clearTextIcon == -1) {
//            clearTextIcon = Icons.iconCloseCircle; //FontImage.MATERIAL_SEARCH;
//        }
//        if (false) {
//            Label hint = searchCmd.search.getHintLabel(); //new Label(s, img);
////            hint.setMaterialIcon(clearTextIcon);
//            hint.setUIID("TextHintSearch");
//        } else {
//            searchCmd.search.setHint(hintTxt);
//            Label hint = searchCmd.search.getHintLabel(); //new Label(s, img);
////            hint.setMaterialIcon(clearTextIcon);
//            hint.setUIID("TextHintSearch");
//        }
////        search.setHintLabelImpl(hint);
//
//        searchCmd.search.addDataChangedListener(new DataChangedListener() {
//
//            public void dataChanged(int type, int index) {
////                    onSearch.doSearch(search.getText());
//                searchCmd.onSearch.actionPerformed(new ActionEvent(searchCmdsearch.getText()));
//            }
//        });
//
//        Button clearButton = new Button(Command.createMaterial("", clearTextIcon, e -> {
//            searchCmd.search.clear();
//            searchCmd.search.startEditingAsync(); //seems necessary to stay in field after clearing
//        }));
//
//        searchCmd.cont.add(BorderLayout.CENTER, searchCmd.search);
//        searchCmd.cont.add(BorderLayout.EAST, clearButton);
//
////            toolbar.add(BorderLayout.SOUTH, this);
//        Container form = searchCmd.cont.getComponentForm();
//        if (form instanceof MyForm) {
//            Container contentPane = ((MyForm) form).getContentPane();
//            if (contentPane.getLayout() instanceof BorderLayout && ((BorderLayout) contentPane.getLayout()).getNorth() == null) {
//                contentPane.add(BorderLayout.NORTH, searchCmd.cont);
//            }
//
//            cont.setHidden(true); //initially hidden
//        }
//</editor-fold>
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
        MySearchCommand searchCmd = this;
        if (searchCmd.cont == null) {
            searchCmd.cont = new Container();

//        searchHolder.addCommandToRightBar(Command.createMaterial("", FontImage.MATERIAL_SEARCH, (e) -> {hideShow();}));
//            searchCmd.setMaterialIcon(FontImage.MATERIAL_SEARCH);
            
            if (searchCmd.hintText == null) {
                searchCmd.hintText = "Search";
            }
            searchCmd.cont.setLayout(new BorderLayout());
            searchCmd.cont.setUIID("SearchFieldContainer");
            
            searchCmd.search = new TextField();
            searchCmd.search.setUIID("SearchFieldText");
//<editor-fold defaultstate="collapsed" desc="comment">
//        search.putClientProperty("searchField", Boolean.TRUE);
//        Image img;
//        if(iconSize > 0) {
//            img = FontImage.createMaterial(FontImage.MATERIAL_SEARCH, UIManager.getInstance().getComponentStyle("TextHintSearch"), iconSize);
//        } else {
//            img = FontImage.createMaterial(FontImage.MATERIAL_SEARCH, UIManager.getInstance().getComponentStyle("TextHintSearch"));
//        }
//        String s = getUIManager().localize("m.search", "Search");
//</editor-fold>
            if (clearTextIcon == -1) {
                clearTextIcon = Icons.iconCloseCircle; //FontImage.MATERIAL_SEARCH;
            }
            if (false) {
                Label hint = searchCmd.search.getHintLabel(); //new Label(s, img);
//            hint.setMaterialIcon(clearTextIcon);
                hint.setUIID("TextHintSearch");
            } else {
                searchCmd.search.setHint(searchCmd.hintText);
                Label hint = searchCmd.search.getHintLabel(); //new Label(s, img);
//            hint.setMaterialIcon(clearTextIcon);
                hint.setUIID("TextHintSearch");
            }
//        search.setHintLabelImpl(hint);

            searchCmd.search.addDataChangedListener(new DataChangedListener() {
                
                public void dataChanged(int type, int index) {
//                    onSearch.doSearch(search.getText());
                    searchCmd.onSearch.actionPerformed(new ActionEvent(searchCmd.search.getText()));
                }
            });
            
            Button clearButton = new Button(Command.createMaterial("", clearTextIcon, e -> {
                searchCmd.search.clear();
                searchCmd.search.startEditingAsync(); //seems necessary to stay in field after clearing
            }));
            
            searchCmd.cont.add(BorderLayout.CENTER, searchCmd.search);
            searchCmd.cont.add(BorderLayout.EAST, clearButton);

//            toolbar.add(BorderLayout.SOUTH, this);
//            Container form = searchCmd.cont.getComponentForm();
            Form form = searchCmd.searchHolder.getComponentForm();
            if (form != null) {
                Container contentPane = form.getContentPane();
                if (contentPane.getLayout() instanceof BorderLayout && ((BorderLayout) contentPane.getLayout()).getNorth() == null) {
                    contentPane.add(BorderLayout.NORTH, searchCmd.cont);
//                    contentPane.animateHierarchy(300);
                }
            }
            cont.setHidden(true);  //hide by default
        }
//                hideShow();
        boolean isHidden = cont.isHidden();
        Form f = cont.getComponentForm();
        MyForm myForm = null; //only support 
        if ((f instanceof MyForm)) {
            myForm = (MyForm) cont.getComponentForm();
        }
        if (isHidden) { //start showing searchbar
            //            search.startEditingAsync();
//            f.setKeepPos();
            if (myForm != null) {
                keepPos = new KeepInSameScreenPosition(myForm); //f.getKeepPos();
            }
            //copied from CN1 SearchBar:
            if (searchHolder.getComponentForm() == Display.getInstance().getCurrent()) {
                search.startEditingAsync();
            } else {
                if (searchHolder.getComponentForm() != null) {
                    searchHolder.getComponentForm().setEditOnShow(search);
                }
            }
            onSearch.actionPerformed(new ActionEvent(search.getText()));
        } else { //start hiding searchbar
            //            search.clear();
//            onSearch.doSearch(""); //keep search text in field it user wants to search again
            onSearch.actionPerformed(new ActionEvent(""));
            search.stopEditing();
            if (myForm != null) {
                myForm.setKeepPos(keepPos);
                myForm.restoreKeepPos();
            }
        }
            
        cont.setHidden(!isHidden);
//        getParent().animateHierarchy(300);
//        getComponentForm().animateHierarchy(300);
        cont.getParent().animateLayout(300);
//        toolbar.animateHierarchy(300);
    }
    
    MySearchCommand(Container containerForSearch, Object positionInContainer, ActionListener onSearch) { //OnSearch onSearch) {
        this(containerForSearch, positionInContainer, "Search", Icons.iconCloseCircle, onSearch);
    }
    
    MySearchCommand(Container containerForSearch,  ActionListener onSearch) { //OnSearch onSearch) {
        this(containerForSearch, BorderLayout.NORTH, "Search", Icons.iconCloseCircle, onSearch);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * call from search Command to hide/show
     */
//    public void hideShow() {
//        boolean isHidden = this.isHidden();
//        MyForm f = (MyForm) getComponentForm();
//        if (isHidden) { //start showing searchbar
//            //            search.startEditingAsync();
////            f.setKeepPos();
//            keepPos = new KeepInSameScreenPosition(f); //f.getKeepPos();
//            //copied from CN1 SearhBar:
//            if (searchHolder.getComponentForm() == Display.getInstance().getCurrent()) {
//                search.startEditingAsync();
//            } else {
//                if (searchHolder.getComponentForm() != null) {
//                    searchHolder.getComponentForm().setEditOnShow(search);
//                }
//            }
//            onSearch.actionPerformed(new ActionEvent(search.getText()));
//        } else { //start hiding searchbar
//            //            search.clear();
////            onSearch.doSearch(""); //keep search text in field it user wants to search again
//            onSearch.actionPerformed(new ActionEvent(""));
//            search.stopEditing();
//            f.setKeepPos(keepPos);
//            f.restoreKeepPos();
//
//        }
//        this.setHidden(!isHidden);
////        getParent().animateHierarchy(300);
////        getComponentForm().animateHierarchy(300);
//        getComponentForm().animateLayout(300);
////        toolbar.animateHierarchy(300);
//    }
//</editor-fold>
}
