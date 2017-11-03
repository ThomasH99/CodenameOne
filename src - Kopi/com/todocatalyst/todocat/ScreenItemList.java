/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.components.SpanButton;
import com.codename1.io.Log;
import com.codename1.l10n.L10NManager;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import static com.codename1.ui.events.ActionEvent.Type.Exception;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.tree.Tree;
import com.codename1.ui.tree.TreeModel;
import com.codename1.ui.util.Resources;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseQuery;
//import generated.StateMachineBase;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
//import com.java4less.rchart.*;
//import javax.microedition.io.ConnectionNotFoundException;
//import javax.microedition.io.PushRegistry;
//import javax.microedition.m3g.Background;
//import org.joda.time.base.BaseInterval;

/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenItemList extends MyForm {

//     Map<String, GetParseValue> parseIdMap;
     MyForm previousForm;
     ItemList itemList;
     int itemType;
    final static int ITEM_TYPE_ITEM = 1;
    final static int ITEM_TYPE_CATEGORY = 2;

    ScreenItemList(String title, ItemList itemList, MyForm previousForm, int itemType) { //throws ParseException, IOException {
        super(title);
        this.itemList = itemList;
        this.previousForm = previousForm;
        this.itemType = itemType;

        // we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
//        form = this;
        // we use border layout so the list will take up all the available space
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setToolbar(new Toolbar());
        addCommandsToToolbar(getToolbar(), theme);
        setScrollable(false); //disable scrolling of form, necessary to let lists handle their own scrolling 
//        getContentPane().setScrollableY(true);
        buildContentPane(getContentPane(), itemList);
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

    public void addCommandsToToolbar(Toolbar toolbar, Resources theme) {

        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
//        tool.addCommandToLeftBar("Done", icon, (e) -> Log.p("Clicked"));
        toolbar.addCommandToRightBar("", icon, (e) -> {
            Log.p("Clicked");
            switch (itemType) {
                case ITEM_TYPE_ITEM:
                    new ScreenItem(new Item(), this).show();
                    break;
                case ITEM_TYPE_CATEGORY:
                    new ScreenCategory(new Category(), this).show();
                    break;
            }
        });
        icon = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle());
        toolbar.addCommandToLeftBar("", icon, (e) -> {
            previousForm.refreshAfterEdit();
            previousForm.showBack();
        });
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    class MyTree extends Tree {
//
//        int myDepthIndent = 15;
//
//        public MyTree(TreeModel model) {
//            super(model);
//        }
//
//        /**
//         * Since a node may be any component type developers should override
//         * this method to add support for binding the click listener to the
//         * given component.
//         *
//         * @param l listener interface
//         * @param node node component returned by createNode
//         */
//        @Override
//        protected void bindNodeListener(ActionListener l, Component node) {
//            if (node instanceof Container) {
////            ((Container)node).addActionListener(l);
////                ((Button) ((Container) node).getClientProperty("subTasksButton")).addActionListener(l);
//                Button button = (Button) (((Container) node).getClientProperty("subTasksButton"));
//                if (button != null) {
//                    button.addActionListener(l); //button will be null if Item doesn't have any subtasks to expand
//                }
//                return;
//            }
//            try {
//                throw new Exception("Unknown element type in list");
//            } catch (java.lang.Exception ex) {
//                Log.e(ex);
//            }
//        }
//
//        /**
//         * Sets the icon for the given node similar in scope to bindNodeListener
//         *
//         * @param icon the icon for the node
//         * @param node the node instance
//         */
//        @Override
//        protected void setNodeIcon(Image icon, Component node) {
//            if (node instanceof Button) {
//                ((Button) node).setIcon(icon);
//                return;
//            }
//            ((SpanButton) node).setIcon(icon);
//        }
//
//        /**
//         * Creates a node within the tree, this method is protected allowing
//         * tree to be subclassed to replace the rendering logic of individual
//         * tree buttons.
//         *
//         * @param node the node object from the model to display on the button
//         * @param depth the depth within the tree (normally represented by
//         * indenting the entry)
//         * @return a button representing the node within the tree
//         */
//        @Override
//        protected Component createNode(Object node, int depth) {
//            Container cmp = null;
//            if (node instanceof Item) {
//                cmp = buildItemContainer((Item) node);
//            } else if (node instanceof Category) {
//                cmp = buildItemContainer((Category) node);
////            } else if (node instanceof ItemList) {
////                cmp = buildItemContainer((ItemList) node);
//            }
//            cmp.getSelectedStyle().setMargin(LEFT, depth * myDepthIndent);
//            return cmp;
//        }
//    }
//</editor-fold>

    /**
     * format: 1a) Category text | {(#subtasks)] | [FinishDate] 1b) sub-tree (in
     * SOUTH container)
     *
     * @param content
     * @return
     */
    private Container buildItemContainer(Category category) {
        Container cont = new Container();
        cont.setLayout(new BorderLayout());
//        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
        Button editItemButton = new Button();
        editItemButton.setCommand(new Command(category.getText()) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Category category = (Category) editItemButton.getClientProperty("category");
                new ScreenCategory(category, ScreenItemList.this).show();
//                super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
            }
        }
        );
        editItemButton.putClientProperty("category", category);
        editItemButton.setUIID("Label");
        cont.addComponent(BorderLayout.CENTER, editItemButton);

        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        Button subTasksButton = new Button();
        if (!category.getComment().equals("")) {
            Label description = new Label(" (" + category.getComment() + ")");
            east.addComponent(description);
        }

        if (category.getSize() != 0) {
            Label nbTasks = new Label("[" + category.getSize() + "]");
            east.addComponent(nbTasks);
        }

//        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
        cont.addComponent(BorderLayout.EAST, east);

        return cont;
    }

    /**
     * format: 1a) Prio/Star | Task text | {(#subtasks)] | [DueDate] |
     * [FinishDate] 1b) sub-tree (in SOUTH container)
     *
     * @param content
     * @return
     */
    private Container buildItemContainer(Item item) {
        Container cont = new Container();
        cont.setLayout(new BorderLayout());
//        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
        Button editItemButton = new Button();
        editItemButton.setCommand(new Command(item.getText()) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Item item = (Item) editItemButton.getClientProperty("item");
                new ScreenItem(item, ScreenItemList.this).show();
//                super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
            }
        }
        );
        editItemButton.putClientProperty("item", item);
        editItemButton.setUIID("Label");
        cont.addComponent(BorderLayout.CENTER, editItemButton);

        Container west = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
        if (item.getPriority() != 0) {
            west.add(new Label(item.getPriority() + ""));
        } else {
            west.add(new Label(" "));
        }
        cont.addComponent(BorderLayout.WEST, west);

        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
        Button subTasksButton = new Button();
        if (item.getItemListSize() != 0) {
            subTasksButton.setCommand(new Command("[" + item.getItemListSize() + "]") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
                }
            }
            );
            cont.putClientProperty("subTasksButton", subTasksButton);
            east.addComponent(subTasksButton);
        }
        subTasksButton.setUIID("Label");

//        east.addComponent(new Label(SimpleDateFormat.new Date(item.getDueDate())));
//        east.addComponent(new Label(new SimpleDateFormat().format(new Date(item.getDueDate()))));
//                        setText(L10NManager.getInstance().formatDateShortStyle((Date)value));
        if (item.getDueDateD().getTime() != 0) {
            east.addComponent(new Label(L10NManager.getInstance().formatDateShortStyle(new Date(item.getDueDate()))));
        }

//        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
        cont.addComponent(BorderLayout.EAST, east);
//        cont.setLeadComponent(east);

        return cont;
    }

    private Container buildContentPane(Container cont, ItemList itemList) {

        MyTree dt = new MyTree(itemList);
        cont.add(dt);
        return cont;

    }

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
    private void showItemForm(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {

    }

}
