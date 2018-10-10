/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseQuery;
//import generated.StateMachineBase;
//import com.java4less.rchart.*;
//import javax.microedition.io.ConnectionNotFoundException;
//import javax.microedition.io.PushRegistry;
//import javax.microedition.m3g.Background;
//import org.joda.time.base.BaseInterval;

/**
 * The list to be edited is passed to this screen, which edits it directly (add
 * new items, delete item, edit items in list). The caller is responsible for
 * saving the updated list. If the list is not saved, created objects (eg tasks,
 * categories, workslots) will not be deleted, leaving dangling tasks, which
 * could be cleaned up or caught in an 'Uncategorized' list.
 *
 * Edit an item with a list of Main screen should contain the following
 * elements: Views - user defined views Jot-list Add new item Categories - see
 * or edit categories People - list of people to assign tasks to Locations -
 * list of locations to assign tasks to Find(?) - or just a menu item in each
 * sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenListBase extends MyForm {

//    GetItemList updateItemListOnDone;

//     Map<String, GetParseValue> parseIdMap;
//    private static String screenTitle = "Category";
//    ScreenItemList(String title, ItemList itemList, MyForm previousForm, int itemType, UpdateField updateOnDone) { //throws ParseException, IOException {
//     
//    protected Container buildCategoryContainer(Category node) {
//        return null;
//    }
//    }

    ScreenListBase(String title, MyForm previousForm, UpdateField doneAction) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        this(title, itemList, previousForm, updateItemListOnDone, containerBuilder, createAndEdit);
        super(title, previousForm, doneAction);
//        this.updateItemListOnDone = updateItemListOnDone;
    }
    
//        public Command makeDoneCommand(String title, Image icon) {
//        Command cmd = new Command(title, icon) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                putEditedValues2(parseIdMap2);
////                updateActionOnDone.update();
//                updateItemListOnDone.update(itemList);
//                previousForm.refreshAfterEdit();
//                previousForm.revalidate();
////            previousForm.showBack();
//                previousForm.showBack();
//            }
//        };
//        cmd.putClientProperty("android:showAsAction", "withText");
//        return cmd;
//    }



    /**
     * edit a list of categories
     *
     * @param title
     * @param itemList
     * @param previousForm
     * @param category
     */
//    ScreenListBase(String title, ItemList itemList, MyForm previousForm, Category category) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        this(title, itemList, previousForm, 
//                 (itList) -> {
//                            category.setList(itList.getList());
//                            DAO.getInstance().save(category);
//                        },
//                        (node) -> {
//                            return buildCategoryContainer((Category) node);
//                        },
//                        (iList) -> {
//                            Item item = new Item();
//                            new ScreenItem(item, MyForm.this, () -> {
//                                iList.addItemAtIndex(item, 0);
//                                item.setOwner(iList);
//                                DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            }).show();
//                        });
//    }
//    ScreenItemList(String title, ItemList itemList, MyForm previousForm, int itemType, GetItemList updateItemListOnDone, ContainerBuilder containerBuilder) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        switch (itemType) {
//            case MyForm.ITEM_TYPE_ITEM:
//                this(title, itemList, previousForm, updateItemListOnDone, containerBuilder, createAndEdit);
//        }
//        
//    }
//    ScreenListBase(String title, ItemList itemList, MyForm previousForm, GetItemList updateItemListOnDone, ContainerBuilder containerBuilder, CreateAndEditListItem createAndEdit) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        super(title);
//        this.itemList = itemList;
////        this.itemList = new ItemList(updateList.getList());
//        this.previousForm = previousForm;
//        this.itemType = itemType;
////        this.updateList = updateList;
//        this.updateItemListOnDone = updateItemListOnDone;
//        this.containerBuilder = containerBuilder;
//        this.createAndEdit = createAndEdit;
////        this.updateOnDone = updateItemListOnDone;
//
//        // we initialize the main form and add the favorites command so we can navigate there
////        form = new Form("TodoCatalyst");
////        form = this;
//        // we use border layout so the list will take up all the available space
//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        setToolbar(new Toolbar());
//        setTitle(title);
//        addCommandsToToolbar(getToolbar(), theme);
//        setScrollable(false); //disable scrolling of form, necessary to let lists handle their own scrolling 
////        getContentPane().setScrollableY(true);
//        getContentPane().add(buildContentPaneForItemList(itemList));
//    }

//    class MyDropContainer2 extends Container {
//        MyDropContainer2() {
//        super();
//        }
//    }
    
//    class MyDropContainer extends Container {
//
//        private ItemList itemList;
//        private Item item;
//
//        MyDropContainer() {
//            
//        }
//        MyDropContainer(Layout layout, ItemList itemList, Item item) {
//            super();
//            this.setLayout(layout);
//            this.itemList = itemList;
//            this.item = item;
//            setDropTarget(true); //Item containers are both dropTargets and draggable
//            setDraggable(true);
//        }
//
//        @Override
//        public void drop(Component dragged, int x, int y) {
//            Item draggedItem = ((MyDropContainer) dragged).item;
////            ((MyDropContainer)dragged).itemList.removeItem(draggedItem); //remove dragged item from its own list
//            itemList.removeItem(draggedItem); //remove dragged item from its own list
//            int insertPosition = itemList.getItemIndex(item); //insert at the position of the drop target, pushing the drop target one further down the list
//            itemList.addItemAtIndex(draggedItem, insertPosition);
//            Log.p("Dropped x=" + x + ", y=" + y);
//        }
//    }

    @Override
    public void refreshAfterEdit() {
//        getContentPane().removeAll();
//        getContentPane().add(buildContentPaneForItemList(itemList));
//        revalidate();
        super.refreshAfterEdit();
    }

    void addSearchToTitlexx() {
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

//    public void addCommandsToToolbar(Toolbar toolbar, Resources theme) {
//
//        //NEW TASK or CATEGORY
//        toolbar.addCommandToRightBar(makeCreateNewItemCommand("", FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle()), itemList));
//
//        //BACK
//        toolbar.addCommandToLeftBar(makeDoneCommand("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle())));
//
//        //TIMER
//        toolbar.addCommandToLeftBar(makeTimerCommand("Timer", FontImage.createMaterial(FontImage.MATERIAL_AV_TIMER, toolbar.getStyle()), itemList));
//
//        //CANCEL - not relevant, all edits are done immediately so not possible to cancel
//    }
    void buildContentPaneInfinitexx(Container cont) {
        Form hi = new Form("InfiniteContainer", new BorderLayout());

        Style s = UIManager.getInstance().getComponentStyle("MultiLine1");
        FontImage p = FontImage.createMaterial(FontImage.MATERIAL_PORTRAIT, s);
        EncodedImage placeholder = EncodedImage.createFromImage(p.scaled(p.getWidth() * 3, p.getHeight() * 3), false);

//        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Item.CLASS_NAME);
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);

        InfiniteContainer ic = new InfiniteContainer() {
            @Override
            public Component[] fetchComponents(int index, int amount) {
                query.setLimit(amount).setSkip(index);
                java.util.List<Item> results = null;
                try {
                    results = query.find();
                } catch (ParseException ex) {
//                    Log.egetLogger(ScreenItemListP.class.getName()).log(Level.SEVERE, null, ex);
                    Log.e(ex);
                }
                return (Component[]) results.toArray();
            }
        };
        hi.add(BorderLayout.CENTER, ic);
    }

}
