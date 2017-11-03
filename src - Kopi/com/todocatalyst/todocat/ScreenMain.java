/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.Toolbar;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Thomas
 */
public class ScreenMain extends MyForm {

    private Resources theme;

    private static final String SCREEN_MAIN_NAME = "Todo Catalyst";

//    private void showEditItemContainer() {
//        Container dishes = createEditItemContainer();
//        getContentPane().replace(getContentPane().getComponentAt(0), dishes, CommonTransitions.createSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 300));
//    }
//    private Container createEditItemContainer() {
//        Container cnt = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cnt.setScrollableY(true);
//
//        // allows elements to slide into view
//        for (Dish d : DISHES) {
//            Component dish = createDishComponent(d);
//            cnt.addComponent(dish);
//        }
//        return cnt;
//    }
//    private Container createEditItemComponent(Item item) {
////        Image img = theme.getImage(item.getImageName());
//        Container mb = new Container(new BorderLayout());
////        mb.getUnselectedStyle().setBgImage(img);
////        mb.getSelectedStyle().setBgImage(img);
////        mb.getPressedStyle().setBgImage(img);
////        mb.getUnselectedStyle().setBackgroundType(Style.BACKGROUND_IMAGE_SCALED_FILL);
////        mb.getSelectedStyle().setBackgroundType(Style.BACKGROUND_IMAGE_SCALED_FILL);
////        mb.getPressedStyle().setBackgroundType(Style.BACKGROUND_IMAGE_SCALED_FILL);
//
//        Map fieldMap = new HashMap();
//
//        Container box = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        Button title = new Button(item.getDishName());
////        title.setUIID("DishTitle");
//        Label highlights = new Label(item.getHighlights());
//        TextArea details = new TextArea(item.getFullDescription());
////        details.setUIID("DishBody");
//        highlights.setUIID("DishBody");
//        Label price = new Label(item.getPrice());
////        price.setUIID("DishPrice");
//        box.addComponent(new Label("Task"));
//        box.addComponent(new TextArea(item.getText()));
//        box.addComponent(highlights);
//
//        Container boxAndPrice = new Container(new BorderLayout());
//        boxAndPrice.addComponent(BorderLayout.CENTER, box);
//        boxAndPrice.addComponent(BorderLayout.EAST, price);
//        mb.addComponent(BorderLayout.SOUTH, boxAndPrice);
//
//        mb.setLeadComponent(title);
//
//        title.addActionListener((e) -> {
//            if (highlights.getParent() != null) {
//                box.removeComponent(highlights);
//                box.addComponent(details);
//            } else {
//                box.removeComponent(details);
//                box.addComponent(highlights);
//            }
//            mb.getParent().animateLayout(300);
//        });
//        return mb;
//    }
    public ScreenMain(Resources theme) throws ParseException, IOException {
        super("");

//        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        int totalCount = query.count();
//        query.whereEqualTo(Item.PARSE_STATUS, Item.ItemStatus.STATUS_DONE.toString());
//        int countDone = query.count();

        int totalCount = DAO.getInstance().getItemCount(false);
        int countDone = DAO.getInstance().getItemCount(true);
        setTitle("TodoCatalyst" + " " + (totalCount - countDone) + " [" + totalCount + "]");

        this.theme = theme;
        Toolbar toolbar = new Toolbar();
        setToolbar(toolbar);
        toolbar.setScrollOffUponContentPane(true);
        addCommandsToToolbar(toolbar, theme);

        setLayout(new BorderLayout());

//        addComponent(BorderLayout.CENTER,);
//        revalidate();

        Style iconStyle = UIManager.getInstance().getComponentStyle("SideCommandIcon");

    }

    public void addCommandsToToolbar(Toolbar toolbar, Resources theme) {

        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
//        Command cmd = Command.create("", icon, (e) -> {
//            new ScreenItemP(new Item(), this).show();
//        });
//        cmd.putClientProperty("android:showAsAction", "withText");
//        toolbar.addCommandToRightBar(cmd);

        toolbar.addCommandToSideMenu(new Command("Edit tasks", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                ParseQuery<Item> query2 = ParseQuery.getQuery(Item.CLASS_NAME);
//                java.util.List<Item> results = null;
//                try {
//                    results = query2.find();
//                } catch (ParseException ex) {
////                    Log.egetLogger(ScreenItemListP.class.getName()).log(Level.SEVERE, null, ex);
//                    Log.e(ex);
//                }
//                new ScreenItemListP("ItemList", new ItemList(results), null).show();
                new ScreenItemList("ItemList", new ItemList(DAO.getInstance().getItems()), ScreenMain.this, ScreenItemList.ITEM_TYPE_ITEM).show();
            }
        });

//        toolbar.addCommandToSideMenu(new Command("New Category", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenCategory(new Category(), ScreenMainP.this).show();
//            }
//        });

        toolbar.addCommandToSideMenu(new Command("Edit categories", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                ParseQuery<Category> query2 = ParseQuery.getQuery(Category.CLASS_NAME);
//                java.util.List<Category> results = null;
//                try {
//                    results = query2.find();
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//                new ScreenItemListP("Categories", new ItemList(results), null).show();
                new ScreenItemList("Categories", new ItemList(DAO.getInstance().getCategories()), ScreenMain.this, ScreenItemList.ITEM_TYPE_CATEGORY).show();
            }
        });

        toolbar.addCommandToSideMenu(new Command("Home page", null/*FontImage.create(" \ue838 ", iconStyle)*/) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Display.getInstance().execute("http://todocatalyst.com");
            }
        });

//        toolbar.addCommandToRightBar(new Command("", theme.getImage("synch.png")) {
//
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                Display.getInstance().callSerially(new Runnable() {
//
//                    public void run() {
////                                updateScreenFromNetwork(cats, "cat");
////                                cats.revalidate();
//                    }
//                });
//            }
//        });
    }

}
