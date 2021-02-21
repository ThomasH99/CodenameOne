/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
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
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.UIManager;

/**
 * returns a command (to add to toolbar) that will create a search container and
 * add it to NORTH of the contentPane 332
 *
 * @author thomashjelm
 */
public class MySearchCommand extends CommandTracked {
    
    public static String SEARCH_KEY = "SearchActive";
    public static String SEARCH_TEXT_KEY = "SearchText";
    public static String SEARCH_HINT = "Search";
    public static char SEARCH_ICON = Icons.iconCloseCircle;
//    private Toolbar toolbar;
//    private Container searchContParent;
    Container searchCont;
    MyForm myForm;
    private TextField searchField;
//    private Object positionInHolder;
    private InsertSearchFct insertSearchContFct;
    String hintText;
//    private OnSearch onSearch;
    private ActionListener onSearch;
    char clearTextIcon;
    private KeepInSameScreenPosition keepPos; //restore scroll position after search

    interface InsertSearchFct {
        
        void insert(Component searchContainer);
    }
    
    private MySearchCommand() {
        super("");
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    interface OnSearch {
//
//        void doSearch(String text);
//    }
//    MySearchBar(Container searchHolder, Object positionInContainer, String hintTxt, char clearTextIcon, ActionListener onSrch){//OnSearch onSearch) {
//    public static MySearchBar createSearch(Object positionInContainer, String hintTxt, char clearTextIcon, ActionListener onSrch){//OnSearch onSearch) {

//    private MySearchCommand(Container searchHolder, Object positionInHolder, String hintTxt,
//            char clearTextIcon, ActionListener onSrch, String analyticsId) {//OnSearch onSearch) {
//        this(searchHolder, positionInHolder, hintTxt, clearTextIcon, onSrch, analyticsId, insertSearchContFct)
//    }
//    private MySearchCommand(MyForm myForm, Object positionInHolder, String hintTxt,
//</editor-fold>
    private MySearchCommand(MyForm myForm, String hintTxt,
            char clearTextIcon, ActionListener onSrch, String analyticsId, InsertSearchFct insertSearchContFct) {//OnSearch onSearch) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.searchHolder = searchHolder;
//        MySearchBar searchCmdXXX = new MySearchBar() {
////        this.onSearch = onSrch;
//        };
//</editor-fold>
        super("");
//        this.searchContParent = searchHolder;
//        this.positionInHolder = positionInHolder;
        this.myForm = myForm;
        this.hintText = hintTxt;
        this.clearTextIcon = clearTextIcon;
        this.onSearch = onSrch;
        this.insertSearchContFct = insertSearchContFct;
        if (this.insertSearchContFct == null) {
            //default insert Search Container in NORTH of contentPane
            this.insertSearchContFct = (searchCont) -> {
//                Container contentPane = myForm.getContentPane();
//                Layout contentPaneLayout = contentPane.getLayout();
//                if (contentPaneLayout instanceof BorderLayout) {
//                    Component prevNorthComp = ((BorderLayout) contentPaneLayout).getNorth();
//                    if (prevNorthComp instanceof Container) {
//                        ((Container) prevNorthComp).addComponent(0, searchCont); //add search at pos 0 (above eg a StickyHeader or HelpText)
//                    } else if (prevNorthComp != null) { //if only a single componentn in north, create a new container for both previous component and searchCont
//                        Container northCont = new Container(BoxLayout.y());
////                        Container parent = northComp.getParent();
//                        northCont.add(searchCont); //add search at pos 0
//                        prevNorthComp.remove();
//                        northCont.add(prevNorthComp); //and existing content (e.g. StickyHeader) at pos 1
////                        parent.add(northCont);
//                        contentPane.add(BorderLayout.NORTH, northCont);
//                    } else { //no North comp
//                        contentPane.add(BorderLayout.NORTH, searchCont);
//                    }
//                }
                MyForm.addToNorthOfContentPane(myForm.getContentPane(), searchCont);
            };
        }
//        setUIID("SearchIcon");
        setMaterialIcon(FontImage.MATERIAL_SEARCH);
        setAnalyticsActionId(analyticsId);

//<editor-fold defaultstate="collapsed" desc="comment">
//        ((MyForm) this.searchContParent.getComponentForm()).setSearchCmd(this); //always set searchCmd for Replay (NPE if 
//        MyForm myForm = (MyForm) searchHolder.getComponentForm();
//        if (myForm.previousValues != null && myForm.previousValues.get(MySearchCommand.SEARCH_KEY) != null) {
//            actionPerformed(null); //re-activate Search, null=>reuse locally stored text
//        }
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

//<editor-fold defaultstate="collapsed" desc="comment">
//    private MySearchCommand(Container containerForSearch, Object positionInHolder, String hintTxt, char clearTextIcon, ActionListener onSrch) {//OnSearch onSearch) {
//        this(containerForSearch, positionInHolder, hintTxt, clearTextIcon, onSrch,
//                containerForSearch != null && containerForSearch.getComponentForm() instanceof MyForm
//                ? "Search-" + ((MyForm) containerForSearch.getComponentForm()).getUniqueFormId()
//                : "Search-");
//    }
//    private MySearchCommand(Container containerForSearch, Object positionInContainer, ActionListener onSearch) { //OnSearch onSearch) {
//        this(containerForSearch, positionInContainer, SEARCH_HINT, SEARCH_ICON, onSearch);
//    }
//    public MySearchCommand(Container containerForSearch, ActionListener onSearch) { //OnSearch onSearch) {
//        this(containerForSearch, BorderLayout.NORTH, SEARCH_HINT, SEARCH_ICON, onSearch);
//    }
//</editor-fold>
    /**
     * installs
     *
     * @param myForm
     * @param onSearch
     */
    public MySearchCommand(MyForm myForm, ActionListener onSearch) { //OnSearch onSearch) {
//        this((Container) ((BorderLayout) myForm.getContentPane().getLayout()).getNorth(), null, SEARCH_HINT, SEARCH_ICON, onSearch, null);
        this(myForm, SEARCH_HINT, SEARCH_ICON, onSearch, "Search-" + myForm.getUniqueFormId(), null);
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
//        MySearchCommand searchCmd = this;

//        MyForm myForm = (MyForm) searchContParent.getComponentForm();
        //initialize search container on first activation:
        if (searchCont == null) {
            searchCont = new Container(new BorderLayout(), "SearchFieldContainer");

//        searchHolder.addCommandToRightBar(Command.createMaterial("", FontImage.MATERIAL_SEARCH, (e) -> {hideShow();}));
//            setMaterialIcon(FontImage.MATERIAL_SEARCH);
//            if (hintText == null) {
//                hintText = "Search";
//            }
            searchField = new TextField();
            searchField.setHint("Search text");
            searchField.setConstraint(TextField.ANY); //ensures *not*starting with uppercase first letter on Android?!
            searchField.setUIID("SearchFieldText");
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
//            if (clearTextIcon == -1) {
//                clearTextIcon = Icons.iconCloseCircle; //FontImage.MATERIAL_SEARCH;
//            }
            if (false) {
                Label hint = searchField.getHintLabel(); //new Label(s, img);
//            hint.setMaterialIcon(clearTextIcon);
                hint.setUIID("TextHintSearch");
            } else {
                searchField.setHint(hintText == null ? "Search" : hintText);
                Label hint = searchField.getHintLabel(); //new Label(s, img);
//            hint.setMaterialIcon(clearTextIcon);
                hint.setUIID("TextHintSearch");
            }
//        search.setHintLabelImpl(hint);

            searchField.addDataChangedListener(new DataChangedListener() {
                
                public void dataChanged(int type, int index) {
//                    onSearch.doSearch(search.getText());
                    onSearch.actionPerformed(new ActionEvent(searchField.getText()));
                }
            });
            
            Button clearButton = new Button(Command.createMaterial("", clearTextIcon == -1 ? Icons.iconCloseCircle : clearTextIcon, e -> {
                searchField.clear();
                searchField.startEditingAsync(); //seems necessary to stay in field after clearing
            }));
            
            searchCont.add(BorderLayout.CENTER, searchField);
            searchCont.add(BorderLayout.EAST, clearButton);
//<editor-fold defaultstate="collapsed" desc="comment">
//            toolbar.add(BorderLayout.SOUTH, this);
//            Container form = cont.getComponentForm();
//            MyForm myForm = (MyForm) searchContParent.getComponentForm();
//            if (true || myForm != null) { //should never be null
////                Container contentPane = myForm.getContentPane();
////                if (contentPane.getLayout() instanceof BorderLayout && ((BorderLayout) contentPane.getLayout()).getNorth() == null) {
////                    contentPane.add(BorderLayout.NORTH, searchCont);
//////                    contentPane.animateHierarchy(300);
////                } else {
////                    ASSERT.that("Trying to add MySearchCommand to ContentPane which is BorderLayout or where North is not empty, form=" + myForm.getUniqueFormId());
////                }
//                insertSearchContFct.insert(searchCont);
//            }
//</editor-fold>
            insertSearchContFct.insert(searchCont);
            searchCont.setHidden(true);  //hide by default (immediately unhidden below on first call)
        }
//                hideShow();
        boolean isHidden = searchCont.isHidden();
//<editor-fold defaultstate="collapsed" desc="comment">
//        Form f = cont.getComponentForm();
//        MyForm myForm = null; //only support
//        if ((f instanceof MyForm)) {
//        MyForm myForm = (MyForm) searchCont.getComponentForm();
//        }
//</editor-fold>
        if (isHidden) { //start showing searchbar
            //            search.startEditingAsync();
//            f.setKeepPos();
            if (true || myForm != null) {
                keepPos = new KeepInSameScreenPosition(myForm); //f.getKeepPos();
            }
            //copied from CN1 SearchBar:
//            Container searchContParent = searchCont.getParent();
//            if (searchContParent.getComponentForm() == Display.getInstance().getCurrent()) {
            if (myForm == Display.getInstance().getCurrent()) {
                searchField.startEditingAsync();
            } else {
//                if (searchContParent.getComponentForm() != null) {
//                    searchContParent.getComponentForm().setEditOnShow(search);
//                }
                myForm.setEditOnShow(searchField);
            }
            onSearch.actionPerformed(new ActionEvent(searchField.getText()));
            if (myForm.previousValues != null) { //may be null in CategorySelector form
                myForm.previousValues.put(SEARCH_KEY, true);
            }
        } else { //hide searchbar
            //            search.clear();
//            onSearch.doSearch(""); //keep search text in field it user wants to search again
            onSearch.actionPerformed(new ActionEvent(""));
            searchField.stopEditing();
            if (true || myForm != null) {
                myForm.setKeepPos(keepPos);
                myForm.restoreKeepPos();
            }
            if (myForm.previousValues != null) {
                myForm.previousValues.remove(SEARCH_KEY);
            }
        }
        
        searchCont.setHidden(!isHidden);
//        getParent().animateHierarchy(300);
//        getComponentForm().animateHierarchy(300);
        if (searchCont.getParent() != null) { //TODO NPE on this, but why can parent be null??!
            searchCont.getParent().animateLayout(300);
        }
//        toolbar.animateHierarchy(300);
    }
    
    public boolean isSearchActive() {
        return !searchCont.isHidden();
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
