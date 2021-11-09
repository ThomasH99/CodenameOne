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
import com.codename1.ui.StickyHeader;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.UIManager;
import static com.todocatalyst.todocatalyst.MyForm.ANIMATION_TIME_FAST;
import static com.todocatalyst.todocatalyst.MyTree2.KEY_OBJECT;

/**
 * returns a command (to add to toolbar) that will create a search container and
 * add it to NORTH of the contentPane 332
 *
 * @author thomashjelm
 */
public class MySearchCommand extends CommandTracked {
//public class MySearchCommand extends MyReplayCommand {

    public static String SEARCH_FIELD_VISIBLE_KEY = "SearchActive";
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

//    private MySearchCommand() {
//        super("");
//    }
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
//        super(analyticsId, "", FontImage.MATERIAL_SEARCH, onSrch); //USE with MyReplayCommand
        super("", FontImage.MATERIAL_SEARCH, analyticsId, onSrch); //use with CommandTracked
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
                MyForm.addToNorthOfContentPane(myForm.getContentPane(), searchCont, 1);
            };
        }
//        setUIID("SearchIcon");
//        setMaterialIcon(FontImage.MATERIAL_SEARCH);
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

    public MySearchCommand(MyForm myForm, ItemAndListCommonInterface itemListOrItemOrg) { //OnSearch onSearch) {
//        this((Container) ((BorderLayout) myForm.getContentPane().getLayout()).getNorth(), null, SEARCH_HINT, SEARCH_ICON, onSearch, null);
        this(myForm, SEARCH_HINT, SEARCH_ICON, makeSearchFunctionUpperLowerStickyHeaders(myForm, itemListOrItemOrg), "Search-" + myForm.getUniqueFormId(), null);
    }

    interface ComponentListForSearch {

        Container get();
    }

//    protected ActionListener makeSearchFunctionUpperLowerStickyHeaders(ItemList itemListOrg, ComponentListForSearch getCompList) {
    protected static ActionListener makeSearchFunctionUpperLowerStickyHeaders(MyForm myForm, ItemAndListCommonInterface itemListOrg, ComponentListForSearch getCompList) {
        return (e) -> { //NB. if e.source=String: search on string; e==null=> reuse previous locally stored search string; e.source==null=hide search field <=> keep searchStr but unhide
            String searchText;
            Component firstVisibleCompToScrollToVisible = null;
//            MyForm myForm = (MyForm) getComponentForm();

//NB: on Replay, the source may be the screen that triggered the replay swe should reuse the previously stored search string stored below in local storage
            if (false) {
                if (e != null) {
                    if (e.getSource() instanceof String) {
                        searchText = (String) e.getSource();
                    } else if (e.getSource() == null) {
                        searchText = "";
                    } else {
                        searchText = "";
                    }
                } else { //e==null <=> use previous search text
                    //get TextField with search string
                    if (myForm.previousValues != null) {
                        searchText = (String) myForm.previousValues.get(MySearchCommand.SEARCH_TEXT_KEY); //reuse 
                    } else {
                        searchText = "";
                    }
                }
            }
            searchText = (String) e.getSource(); //search string ALWAYS passed this way
//            Container compList = null;
//            compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
            Container compList = getCompList.get();

            //UI: search will automatically remove an insert container (since it breaks the below algorithm)
            if (myForm != null && myForm.getPinchInsertContainer() != null) {
                Component topLevelComp = (Component) myForm.getPinchInsertContainer();
//                Component topLevelParent = topLevelComp.getParent();
                if (false) { //now remove pinchCont directly (not its parent)
                    while (topLevelComp != null && topLevelComp.getParent() != compList) {
                        topLevelComp = topLevelComp.getParent();
                    }
                }
//                MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnCont(topLevelComp);
                if (true || topLevelComp != null) { //should never be null?!
                    topLevelComp.remove();
                }
                myForm.setPinchInsertContainer(null);
            }

            //no matter where the search string came from (already saved or just entered) save it for later reuse
            if (false && myForm.previousValues != null) {
//                if (text != null && !text.isEmpty()) {
//                    myForm.previousValues.put(MySearchCommand.SEARCH_TEXT_KEY, text);
//                } else {
//                    myForm.previousValues.remove(MySearchCommand.SEARCH_TEXT_KEY);
//                }
                if (e != null && e.getSource() != null) {
                    myForm.previousValues.put(MySearchCommand.SEARCH_TEXT_KEY, searchText);
                }
            }

            if (compList != null && itemListOrg.size() > 0) { //do nothing on empty lists
                if (searchText == null) { //end of search is marked with a "" search string, show everything except elements after collapsed headers
                    StickyHeader preceedingHeader = null;
                    for (int i = 0, size = compList.getComponentCount(); i < size; i++) {
                        //https://www.codenameone.com/blog/toolbar-search-mode.html:
                        Component comp = compList.getComponentAt(i);
                        if (comp instanceof StickyHeader) { //if comp is a header
                            preceedingHeader = (StickyHeader) comp;
                            preceedingHeader.setHidden(false);
                        } else {
                            boolean unhide = preceedingHeader == null || !preceedingHeader.isCollapsed();
//                            boolean unhide = preceedingHeader != null && preceedingHeader.isCollapsed();
                            comp.setHidden(!unhide);
                        }
                    }
                } else {
                    int labelCount = 0;
                    int elementCount = 0;
                    boolean searchOnLowerCaseOnly;
                    StickyHeader preceedingHeader = null;
//                StickyHeader precedingStickyHeader = null;
                    boolean hide;
                    searchOnLowerCaseOnly = searchText.equals(searchText.toLowerCase()); //if search string is all lower case, then search on lower case only, otherwise search on 
                    for (int i = 0, size = compList.getComponentCount(); i < size; i++) {
                        //https://www.codenameone.com/blog/toolbar-search-mode.html:

                        Component comp = compList.getComponentAt(i);
//                    if (comp.isHidden()) { //NOT WORKING since search will hide/unhide, must check if preceding stickyheader is collapsed!
//                        continue; //ignore hidden elements (eg hidden by collapsing the stickyheader)
//                    }
                        if (firstVisibleCompToScrollToVisible == null) {
                            firstVisibleCompToScrollToVisible = comp;
                        }
//                    if (comp instanceof Label || comp instanceof StickyHeader) {
                        if (comp instanceof StickyHeader) { //if comp is a header
                            StickyHeader header = (StickyHeader) comp;
                            if (header.isCollapsed()) { //always hide all collapsed headers since they can never have visible items
                                header.setHidden(true);
                                elementCount = 0; //reset count on every header
                                labelCount++; //hack: StickyHeaders are Labels, so count them and add to count
                                preceedingHeader = header; //(StickyHeader) comp;
                            } else {
                                if (preceedingHeader != null) {
                                    preceedingHeader.setHidden(elementCount == 0); //UI: hide previous header if nothing is shown after it
                                    if (elementCount != 0 && firstVisibleCompToScrollToVisible == null) {
                                        firstVisibleCompToScrollToVisible = header; //comp;
                                    }
                                }
//                            if (true || comp instanceof StickyHeader) {
//                                precedingStickyHeader = header; //(StickyHeader) comp;
//                            }
                                elementCount = 0; //reset count on every header
                                labelCount++; //hack: StickyHeaders are Labels, so count them and add to count
                                preceedingHeader = header; //(StickyHeader) comp;
                            }
//                    } else if (comp instanceof InlineInsertNewContainer) {
//                        comp.remove();
                        } else { //if comp is an element after the header
//                        if (precedingStickyHeader == null || !precedingStickyHeader.isCollapsed()) {
                            if (preceedingHeader != null && preceedingHeader.isCollapsed()) {
                                hide = true;
                            } else if (searchOnLowerCaseOnly) {
//                                hide = ((ItemAndListCommonInterface) itemListOrg.get(i - labelCount)).getText().toLowerCase().indexOf(searchText) < 0;
                                hide = !((ItemAndListCommonInterface) itemListOrg.get(i - labelCount)).getText().toLowerCase().contains(searchText);
                            } else {
//                                hide = ((ItemAndListCommonInterface) itemListOrg.get(i - labelCount)).getText().indexOf(searchText) < 0;
                                hide = !((ItemAndListCommonInterface) itemListOrg.get(i - labelCount)).getText().contains(searchText);
                            }
                            comp.setHidden(hide);
                            if (!hide) {
                                if (firstVisibleCompToScrollToVisible == null) {
                                    firstVisibleCompToScrollToVisible = comp;
                                }
                                elementCount++;
                            }
//                        }
                        }
                    }
                    if (preceedingHeader != null) {
                        preceedingHeader.setHidden(elementCount == 0); //hide/unhide previous label if nothing is shown after it
                    }
                }
            } else { //compList==null - should never happen???!
                ASSERT.that("we should never have this case");
                for (int i = 0, size = compList.getComponentCount(); i < size; i++) {
                    Component comp = compList.getComponentAt(i);
                    Object sourceObj = comp.getClientProperty(KEY_OBJECT);
//                    comp.setHidden(sourceObj != null && sourceObj instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) sourceObj).getText().toLowerCase().indexOf(text) < 0);
                    boolean hide = sourceObj != null && sourceObj instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) sourceObj).getText().toLowerCase().indexOf(searchText) < 0;
                    if (firstVisibleCompToScrollToVisible == null && !hide) {
                        firstVisibleCompToScrollToVisible = comp;
                    }
                    comp.setHidden(hide);
                }
            }
            if (compList != null) {
                if (firstVisibleCompToScrollToVisible != null) {
                    compList.scrollComponentToVisible(firstVisibleCompToScrollToVisible);
                }
                compList.animateLayout(ANIMATION_TIME_FAST);
            }
        };
    }

//    protected ActionListener makeSearchFunctionUpperLowerStickyHeaders(ItemList itemListOrg) {
    protected static ActionListener makeSearchFunctionUpperLowerStickyHeaders(MyForm myForm, ItemAndListCommonInterface itemListOrg) {
        return makeSearchFunctionUpperLowerStickyHeaders(myForm, itemListOrg, () -> (Container) ((BorderLayout) myForm.getContentPane().getLayout()).getCenter());
    }

    protected ActionListener makeSearchFunctionSimpleXXX(MyForm myForm, ItemList itemListList, ComponentListForSearch getCompList) {
        return (e) -> {
            String text = (String) e.getSource();
//            Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
            Container compList = getCompList.get();
//            MyForm myForm = (MyForm) getComponentForm();
            //UI: search will automatically remove an insert container (since it breaks the below algorithm)
            if (myForm != null && myForm.getPinchInsertContainer() != null) {
                Component topLevelComp = (Component) myForm.getPinchInsertContainer();
//                Component topLevelParent = topLevelComp.getParent();
                while (topLevelComp != null && topLevelComp.getParent() != compList) {
                    topLevelComp = topLevelComp.getParent();
                }
//                MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnCont(topLevelComp);
                if (true || topLevelComp != null) { //should never be null?!
                    topLevelComp.remove();
                }
                myForm.setPinchInsertContainer(null);
            }
            boolean showAll = text == null || text.length() == 0;
            for (int i = 0, size = itemListList.getSize(); i < size; i++) {
                //TODO!!! compare same case (upper/lower)
                //https://www.codenameone.com/blog/toolbar-search-mode.html:
//                compList.getComponentAt(i).setHidden(((ItemList) itemListList.get(i)).getText().toLowerCase().indexOf(text) < 0);
//                compList.getComponentAt(i).setHidden(((ItemAndListCommonInterface) itemListList.get(i)).getText().toLowerCase().indexOf(text) < 0);
                compList.getComponentAt(i).setHidden(!((ItemAndListCommonInterface) itemListList.get(i)).getText().toLowerCase().contains(text));
            }
            compList.animateLayout(ANIMATION_TIME_FAST);
        };
    }

    protected ActionListener makeSearchFunctionSimpleXXX(MyForm myForm, ItemList itemListList) {
        return makeSearchFunctionSimpleXXX(myForm, itemListList, () -> (Container) ((BorderLayout) myForm.getContentPane().getLayout()).getCenter());
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        //evt==null means search was re-activated automatically (on replay or refresh of screen), so the old search text should be reused
//        super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
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

                @Override
                public void dataChanged(int type, int index) {
//                    onSearch.doSearch(search.getText());
                    myForm.previousValues.put(MySearchCommand.SEARCH_TEXT_KEY, searchField.getText()); //always save latest text search str
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
            searchCont.setHidden(false); //unhide
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
//            if (false) {
//                if ((evt == null || !(evt.getSource() instanceof String)) && myForm.previousValues.get(SEARCH_TEXT_KEY) != null) {
//                    searchField.setText((String) myForm.previousValues.get(SEARCH_TEXT_KEY)); //will fire dataChanged listener!
//                } else {
////                onSearch.actionPerformed(evt == null ? null : new ActionEvent(searchField.getText()));
//                    onSearch.actionPerformed(new ActionEvent(searchField.getText()));
//                    searchField.setText((String) myForm.previousValues.get(SEARCH_TEXT_KEY)); //will fire dataChanged listener!
//                }
//            } else {
//                if (myForm.previousValues.get(SEARCH_TEXT_KEY) != null) {
//                    searchField.setText((String) myForm.previousValues.get(SEARCH_TEXT_KEY)); //will fire dataChanged listener!
//                }
//            }
//            }
            if (myForm.previousValues != null && myForm.previousValues.get(SEARCH_TEXT_KEY) != null) {
                searchField.setText((String) myForm.previousValues.get(SEARCH_TEXT_KEY)); //will ONLY fire dataChanged listener IFF new text is different, hence the code below!
                if (myForm.previousValues.get(SEARCH_TEXT_KEY).equals(searchField.getText())) {
                    onSearch.actionPerformed(new ActionEvent((String) myForm.previousValues.get(SEARCH_TEXT_KEY))); //show all filtered again
                }
            } else {
                searchField.setText("");
            }
            if (myForm.previousValues != null) { //may be null in CategorySelector form
                myForm.previousValues.put(SEARCH_FIELD_VISIBLE_KEY, true);
            }
        } else { //hide searchbar
            searchCont.setHidden(true); //hide
            //            search.clear();
//            onSearch.doSearch(""); //keep search text in field it user wants to search again
//            onSearch.actionPerformed(new ActionEvent(""));
//            onSearch.actionPerformed(evt == null ? null : new ActionEvent(null));
            onSearch.actionPerformed(new ActionEvent("")); //show all filtered again
            searchField.stopEditing();
            if (true || myForm != null) {
                myForm.setKeepPos(keepPos);
                myForm.restoreKeepPos();
            }
            if (myForm.previousValues != null) {
                myForm.previousValues.remove(SEARCH_FIELD_VISIBLE_KEY);
            }
        }

//        searchCont.setHidden(!isHidden);
//        getParent().animateHierarchy(300);
//        getComponentForm().animateHierarchy(300);
        if (searchCont.getParent() != null) { //TODO NPE on this, but why can parent be null??!
            searchCont.getParent().animateLayout(300);
        }
//        toolbar.animateHierarchy(300);
//        super.actionPerformed(evt); //only call search fct *after* showing the search field
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
