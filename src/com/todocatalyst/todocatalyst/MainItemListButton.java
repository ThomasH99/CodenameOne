/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Font;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.MyTree2.KEY_EXPANDED;

/**
 *
 * @author thomashjelm
 */
public class MainItemListButton extends Container {

    Container east;

//    private Button makeEditListButton(ItemAndListCommonInterface itemElt) {
//        Button editItemListPropertiesButton = null;
//        editItemListPropertiesButton = new Button("", "IconEdit");
//        editItemListPropertiesButton.setName("ItemListEditButton");
//        //SHOW/EDIT SUBTASKS OF LIST
//        editItemListPropertiesButton.setCommand(MyReplayCommand.create("EditItemList-" + itemList.getReplayId(), "", Icons.iconEdit,
//                (ActionEvent e) -> {
//                    MyForm f = (MyForm) editItemListPropertiesButton.getComponentForm();
//                    f.setKeepPos(new KeepInSameScreenPosition());
//                    if (itemElt instanceof ItemList) {
//                        new ScreenListOfItems(() -> (ItemList) itemElt, f).show();
//                    } else if (itemElt instanceof Item) {
//                        new ScreenListOfItems(() -> (Item) itemElt, f).show();
//                    }
//                }
//        )
//        );
//        return editItemListPropertiesButton;
//    }
//
//    MainItemListButton(ItemAndListCommonInterface itemList) {
//        Container mainCont = new Container(new BorderLayout());
//        mainCont.setName("MainItemListContainer");
//        if (itemList instanceof ItemBucket) {
//            mainCont.setUIID("StatisticsItemListContainer" + ((ItemBucket) itemList).level);
//        } else {
//            mainCont.setUIID("ItemListContainer");
//        }
//    }

    MainItemListButton(String text, Character icon, Font iconFont, String durationText,String nbItemsText, ActionListener actionListener, String replayId) {
        setLayout(new BorderLayout());
//        Container mainCont = new Container(new BorderLayout());
        Container mainCont = this;
        mainCont.setName("MainItemListContainer");
        mainCont.setUIID("ItemListContainer");

        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS)); //NB. NO_GROW to avoid that eg expand sublist [3/5] grows in height
//        mainCont.add(BorderLayout.EAST, east);
        mainCont.addComponent(BorderLayout.EAST, BorderLayout.center(east));

        Button editItemListPropertiesButton = null;
        editItemListPropertiesButton = new Button("", "IconEdit");
        editItemListPropertiesButton.setName("ItemListEditButton");
        //SHOW/EDIT SUBTASKS OF LIST
        editItemListPropertiesButton.setCommand(MyReplayCommand.create(replayId, "", Icons.iconEdit, actionListener));

        east.addComponent(new Label(durationText, "ListOfItemListsRemainingTime")); //format: "remaining/workTime"
        east.addComponent(new Label(nbItemsText, "ListOfItemListsShowItems"));
        east.addComponent(editItemListPropertiesButton);
        
        SpanButton itemListLabel;
//        if (false) {
//            itemListLabel = new MyButtonInitiateDragAndDrop(text,
//                    null, () -> {
////                    boolean enabled = ((MyForm)get.isDragAndDropEnabled();
//                        boolean enabled = ((MyForm) mainCont.getComponentForm()).isDragAndDropEnabled();
//                        if (enabled && expandItemListSubTasksButton != null) {
//                            Object e = swipCont.getClientProperty(KEY_EXPANDED);
//                            if (e != null && e.equals("true")) { //                            subTasksButton.getCommand().actionPerformed(null);
//                                expandItemListSubTasksButton.pressed();//simulate pressing the button
//                                expandItemListSubTasksButton.released(); //trigger the actionLIstener to collapse
//                            }
//                        }
//                        return enabled;
//                    }); //D&D
//        } else {
            itemListLabel = new SpanButton(text);
            itemListLabel.setUIID("ListOfItemsTextCont");
            itemListLabel.setTextUIID("ListOfItemsText");
//        }
        if (iconFont == null) {
            itemListLabel.setMaterialIcon(icon); //FontImage.MATERIAL_LIST); //UI: ' '==blank icon?! Add white space to allow to customize list icons later
        } else {
            itemListLabel.setFontIcon(iconFont, icon); //FontImage.MATERIAL_LIST); //UI: ' '==blank icon?! Add white space to allow to customize list icons later
        }
        itemListLabel.setUIID("ListOfItemListsTextCont");
        itemListLabel.setTextUIID("ListOfItemListsText");
        itemListLabel.setIconUIID("ListOfItemListsIcon");
        itemListLabel.setName("ItemListsDDCont");

        mainCont.addComponent(BorderLayout.CENTER, itemListLabel);
    }

//    public Component setListInfo(int actualEffort, int effortStr, Button expandItemListSubTasksButton, String itemListCount, Button editItemListPropertiesButton) {
//        if (effortStr >= 0) {
//            east.addComponent(new Label(effortStr + "", "ListOfItemListsRemainingTime")); //format: "remaining/workTime"
//        }
//
//        Label actualTotalLabel = new Label(MyDate.formatDurationStd(actualTotal));
//        actualTotalLabel.setMaterialIcon(Icons.iconActualEffortCust);
//        east.addComponent(actualTotalLabel);
//
//        if (expandItemListSubTasksButton != null) {
//            east.addComponent(expandItemListSubTasksButton); //format: "remaining/workTime"
//        } else if (itemListCount != null) {
//            east.addComponent(itemListCount); //format: "remaining/workTime"
//        }
//        if (editItemListPropertiesButton != null) {
//            east.addComponent(editItemListPropertiesButton);
//        }
//        return this;
//    }

}
