/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.FontImage;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import java.util.Date;

/**
 * checkbox handling the ItemStatus states + longPress menu to change
 *
 * @author Thomas
 */
//public class MyCheckBox extends CheckBox {
public class MyCheckBox extends Button {
    //TODO left-justify the status choices in the popup menu

    interface ProcessItemStatusChange {

//        void process(Item item, ItemStatus newStatus);
        void processNewStatusValue(ItemStatus oldStatus, ItemStatus newStatus);
    }

    interface IsItemOngoing {

        boolean isOngoing();
    }

    private ProcessItemStatusChange statusChangeHandler;
    private IsItemOngoing itemOngoing;
    private ItemStatus itemStatus;
    private static String singleIconStyleUIID; //style for the single status icon shown for example in items in lists
    private static String popupIconStyleUIID; //style for icons in popup menu (to select any status)
    private static String groupStyleUIID; //style for popup menu (a ComponentGroup)
    private static Style singleIconStyle;
    private static Style popupIconStyle;
    private static Image[] iconsSingleStatus; // = new Image[]{Icons.iconCheckboxCreated, Icons.iconCheckboxOngoing, Icons.iconCheckboxWaiting, Icons.iconCheckboxDone, Icons.iconCheckboxCancelled};
    private static Image[] iconsPopup; // = new Image[]{Icons.iconCheckboxCreated, Icons.iconCheckboxOngoing, Icons.iconCheckboxWaiting, Icons.iconCheckboxDone, Icons.iconCheckboxCancelled};

//    private Dialog d;
    /**
     * activate menu to choose status on a single click
     */
    private boolean activateFullMenuOnSingleClick;

    public MyCheckBox() {
        this(ItemStatus.CREATED, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), null, null, null, null);
    }

    public MyCheckBox(ItemStatus itemStatus, ProcessItemStatusChange statusChangeHandler) {//, IsItemOngoing itemOngoing) {
        this(itemStatus, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), statusChangeHandler, null, null, null);
    }

    public MyCheckBox(ItemStatus itemStatus) {//, IsItemOngoing itemOngoing) {
        this(itemStatus, null);
    }

//    public MyCheckBox(Item item, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler) {
//    public MyCheckBox(ItemStatus initialItemStatus, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler, IsItemOngoing itemOngoingXXX) {
//        this(initialItemStatus, activateFullMenuOnSingleClick, statusChangeHandler, itemOngoingXXX, null, null, null);
//    }
    public MyCheckBox(ItemStatus initialItemStatus, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler, //IsItemOngoing itemOngoing,
            String singleIconStyleUIID, String popupIconStyleUIID, String groupStyleUIID) {
        super();
        setUIID("MyCheckBox");
//        String s = ItemStatus.values()[item.getStatus().ordinal()].fullDescription;
//        this.saveOnChange = saveOnChange;
//        setStatus(initialItemStatus);
//        this.activateFullMenuOnSingleClick = activateFullMenuOnSingleClick;
//        this.statusChangeHandler = statusChangeHandler;

//        if (popupIconStyleUIID != null && !popupIconStyleUIID.equals(this.popupIconStyleName)) {
//            this.popupIconStyleName = singleIconStyleUIID;
//        } else {
//            this.popupIconStyleName = "ItemStatusPopupIcon";
//        }
        this.itemOngoing = itemOngoing;

        this.popupIconStyleUIID = singleIconStyleUIID;
        if (this.popupIconStyleUIID == null) {
            this.popupIconStyleUIID = "ItemStatusPopupIcon";
        }
        Style s = null;
//        Style s = UIManager.getInstance().getComponentStyle(singleIconStyle).get; //never returns null
        if (iconsPopup == null //if no icons defined
                || (popupIconStyleUIID != null && !popupIconStyleUIID.equals(this.popupIconStyleUIID)) //or if no name defined, or style name has changed
                || (s = UIManager.getInstance().getComponentStyle(popupIconStyleUIID)).equals(this.popupIconStyle)) { //or if style has dynamically changed via CSS
//            this.singleIconStyleName = singleIconStyleUIID;
            if (popupIconStyleUIID != null && !popupIconStyleUIID.equals(this.popupIconStyleUIID)) {
                this.popupIconStyleUIID = popupIconStyleUIID;
            }
            if (this.popupIconStyleUIID == null) {
                this.popupIconStyleUIID = "ItemStatusIcon";
            }
            if (this.popupIconStyle == null) {
                this.popupIconStyle = UIManager.getInstance().getComponentStyle(this.popupIconStyleUIID);
            }
//            ); //never returns null
            if (s != null && !s.equals(popupIconStyle)) {
                this.popupIconStyle = new Style(s);//keep a *copy* of the style (to ensure that CSS refresh works?!
            }

            iconsPopup = new Image[ItemStatus.values().length];
            iconsPopup[0] = FontImage.createMaterial(ItemStatus.iconCheckboxCreatedChar, popupIconStyle);
            iconsPopup[1] = FontImage.createMaterial(ItemStatus.iconCheckboxOngoingChar, popupIconStyle);
            iconsPopup[2] = FontImage.createMaterial(ItemStatus.iconCheckboxWaitingChar, popupIconStyle);
            iconsPopup[3] = FontImage.createMaterial(ItemStatus.iconCheckboxDoneChar, popupIconStyle);
            iconsPopup[4] = FontImage.createMaterial(ItemStatus.iconCheckboxCancelledChar, popupIconStyle);
        }
//        if (groupStyle != null && !groupStyle.equals(this.groupStyleName)) {
//        } else {
//        }
        this.groupStyleUIID = groupStyleUIID;
        if (this.groupStyleUIID == null) {
            this.groupStyleUIID = "ItemStatusPopup";
        }

//        if (singleIconStyle != null && !singleIconStyle.equals(this.singleIconStyleName)) {
//            this.singleIconStyleName = singleIconStyle;
//        } else {
//            this.singleIconStyleName = "ItemStatusIcon";
//        }
        s = null;
//        Style s = UIManager.getInstance().getComponentStyle(singleIconStyle).get; //never returns null
        if (iconsSingleStatus == null //if no icons defined
                || (singleIconStyleUIID != null && !singleIconStyleUIID.equals(this.singleIconStyleUIID)) //or if no name defined, or style name has changed
                || (s = UIManager.getInstance().getComponentStyle(singleIconStyleUIID)).equals(this.singleIconStyle)) { //or if style has dynamically changed via CSS
//            this.singleIconStyleName = singleIconStyleUIID;
            if (singleIconStyleUIID != null && !singleIconStyleUIID.equals(this.singleIconStyleUIID)) {
                this.singleIconStyleUIID = singleIconStyleUIID;
            }
            if (this.singleIconStyleUIID == null) {
                this.singleIconStyleUIID = "ItemStatusIcon";
            }
            if (this.singleIconStyle == null) {
                this.singleIconStyle = UIManager.getInstance().getComponentStyle(this.singleIconStyleUIID);
            }
//            ); //never returns null
            if (s != null && !s.equals(singleIconStyle)) {
                this.singleIconStyle = new Style(s); //keep a *copy* of the style (to ensure that CSS refresh works?!
            }
            iconsSingleStatus = new Image[ItemStatus.values().length];
            iconsSingleStatus[0] = FontImage.createMaterial(ItemStatus.iconCheckboxCreatedChar, singleIconStyle);
            iconsSingleStatus[1] = FontImage.createMaterial(ItemStatus.iconCheckboxOngoingChar, singleIconStyle);
            iconsSingleStatus[2] = FontImage.createMaterial(ItemStatus.iconCheckboxWaitingChar, singleIconStyle);
            iconsSingleStatus[3] = FontImage.createMaterial(ItemStatus.iconCheckboxDoneChar, singleIconStyle);
            iconsSingleStatus[4] = FontImage.createMaterial(ItemStatus.iconCheckboxCancelledChar, singleIconStyle);
        }

        setStatus(initialItemStatus); //NB! Do this *after* initializing the icons above, but *before* setting statusChangeHandler to avoid infinite loop
        this.activateFullMenuOnSingleClick = activateFullMenuOnSingleClick;
        this.statusChangeHandler = statusChangeHandler;

        addActionListener(
                new ActionListener() { //Handle single-click
            @Override
            public void actionPerformed(ActionEvent evt
            ) {
                if (MyCheckBox.this.activateFullMenuOnSingleClick) {
                    selectNewStatus();
                } else {
                    //TODO!! move below logic into static method in Item to avoid duplication
//                ItemStatus itemStatus = itemStatus.getStatus();
//                    switch (MyCheckBox.this.itemStatus) { //OLD STATUS
                    switch (itemStatus) { //OLD STATUS
                        case CREATED:
                        case WAITING:
                        case ONGOING:
                            setStatus(ItemStatus.DONE);
                            break;
                        case DONE:
                        case CANCELLED:
//                            if (MyCheckBox.this.itemOngoing.isOngoing()) { //NO LONGER necessary to check if item is ongoing, it will be handled in setStatus (setting to Ongoing)
//                                setStatus(ItemStatus.ONGOING);
//                            } else {
//                                setStatus(ItemStatus.CREATED);
//                            }
                            setStatus(ItemStatus.CREATED);
                            break;
                        default:
                            assert false : "unknown ItemStatus=" + itemStatus;
                    }
                }
            }
        }
        );
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public MyCheckBox(ItemStatus itemStatus, boolean updateAndSaveItemImmediately) {
//    public MyCheckBox(Item item, boolean updateAndSaveItemImmediately) {
//        this(item, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), null);
//        statusChangeHandler = new ProcessItemStatusChange() {
//            @Override
//            public void process(Item item, ItemStatus newStatus) {
//                if (updateAndSaveItemImmediately) {
//                    item.setStatus(newStatus);
//                    DAO.getInstance().save(item);
//                }
//                d.dispose();
//                repaint();
//            }
//        };
//    }
//</editor-fold>
    void setIsItemOngoing(IsItemOngoing itemOngoing) {
        this.itemOngoing = itemOngoing;
    }

    public void setStatusChangeHandler(ProcessItemStatusChange statusChangeHandler) {
        this.statusChangeHandler = statusChangeHandler;
    }

    /**
     * sets new status value. Does nothing is new itemStatus is the same as old
     * value.
     *
     * @param itemStatus
     */
    void setStatus(ItemStatus itemStatus) {
        setStatus(itemStatus, true);
    }

    void setStatus(ItemStatus itemStatus, boolean runStatusChangeHandler) {
        //run statusChangeHandler.process *first* before changing status to have access to old status value
        if (itemStatus != this.itemStatus) {
            ItemStatus oldStatus = this.itemStatus;
            this.itemStatus = itemStatus; //update before making call below to avoid infinite loop
            if (runStatusChangeHandler && statusChangeHandler != null) {
                statusChangeHandler.processNewStatusValue(oldStatus, itemStatus);
            }
//            setIcon(ItemStatus.icons[itemStatus.ordinal()]);
            setStatusIcon(itemStatus);
            repaint();
        }
    }

    void setStatus(String itemStatus) {
        setStatus(ItemStatus.valueOf(itemStatus));
    }

    /**
     * updates the statusIcon without any other effects
     *
     * @param itemStatus
     */
    void setStatusIcon(ItemStatus itemStatus) {
//        setIcon(ItemStatus.icons[itemStatus.ordinal()]);
//        setIcon(ItemStatus.getStatusIcon(itemStatus));
        setIcon(iconsSingleStatus[itemStatus.ordinal()]);
    }

    ItemStatus getStatus() {
        return itemStatus;
    }

    private Button create(String cmdName, Image materialIcon, final ActionListener ev) {
        Button b = new Button(new Command(cmdName) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ev.actionPerformed(evt);
            }
        });
//        b.setUIID();
        b.setIcon(materialIcon);
//        return c;
        return b;
    }

    private void selectNewStatus() {
        //TODO move this logic to Item.xxx
//        d = new Dialog("Select");
//        d.setLayout(BoxLayout.y());
//        d.setAutoDispose(true);
//        Style s = UIManager.getInstance().getComponentStyle(popupIconStyleUIID); //never returns null
//        if (iconsPopup == null || (this.singleIconStyleUIID == null || !this.singleIconStyleUIID.equals(singleIconStyleUIID) || !s.equals(singleIconStyle))) { //compare styles in case they've dynamically changed via CSS
//            this.singleIconStyleUIID = singleIconStyleUIID;
//            this.singleIconStyle = s;
//
//            if (iconsPopup == null //if no icons defined
//                    || (!this.singleIconStyleUIID.equals(singleIconStyleUIID)) //or if no name defined, or style name has changed
//                    || (s = UIManager.getInstance().getComponentStyle(singleIconStyleUIID)).equals(this.singleIconStyle)) { //or if style has dynamically changed via CSS
////            this.singleIconStyleName = singleIconStyleUIID;
////            ); //never returns null
//                if (!s.equals(singleIconStyle)) {
//                    this.singleIconStyle = s;
//                }

        MyPopupMenu d = new MyPopupMenu(groupStyleUIID, create("Cancel", null, (e) -> {
        }),
                //<editor-fold defaultstate="collapsed" desc="comment">
                //                Command.create(ItemStatus.CREATED.getName(), ItemStatus.icons[ItemStatus.CREATED.ordinal()], (e) -> {
                //                    setStatus(ItemStatus.CREATED);
                //                }),
                //                Command.create(ItemStatus.ONGOING.getName(), ItemStatus.icons[ItemStatus.ONGOING.ordinal()], (e) -> {
                //                    setStatus(ItemStatus.ONGOING);
                //                }),
                //                Command.create(ItemStatus.WAITING.getName(), ItemStatus.icons[ItemStatus.WAITING.ordinal()], (e) -> {
                //                    setStatus(ItemStatus.WAITING);
                //                }),
                //                Command.create(ItemStatus.DONE.getName(), ItemStatus.icons[ItemStatus.DONE.ordinal()], (e) -> {
                //                    setStatus(ItemStatus.DONE);
                //                }),
                //                Command.create(ItemStatus.CANCELLED.getName(), ItemStatus.icons[ItemStatus.CANCELLED.ordinal()], (e) -> {
                //                    setStatus(ItemStatus.CANCELLED);
                //                }));
                //</editor-fold>
                create(ItemStatus.CREATED.getName(), iconsPopup[ItemStatus.CREATED.ordinal()], (e) -> {
                    setStatus(ItemStatus.CREATED);
                }),
                create(ItemStatus.ONGOING.getName(), iconsPopup[ItemStatus.ONGOING.ordinal()], (e) -> {
                    setStatus(ItemStatus.ONGOING);
                }),
                create(ItemStatus.WAITING.getName(), iconsPopup[ItemStatus.WAITING.ordinal()], (e) -> {
                    setStatus(ItemStatus.WAITING);
                }),
                create(ItemStatus.DONE.getName(), iconsPopup[ItemStatus.DONE.ordinal()], (e) -> {
                    setStatus(ItemStatus.DONE);
                }),
                create(ItemStatus.CANCELLED.getName(), iconsPopup[ItemStatus.CANCELLED.ordinal()], (e) -> {
                    setStatus(ItemStatus.CANCELLED);
                }));
        d.popup(); //showDialog(x) = x is the component the dialog will 'point' to
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void selectNewStatusOLD() {
//        //TODO move this logic to Item.xxx
//        d = new Dialog("Select");
//        d.setLayout(BoxLayout.y());
////        d.setAutoDispose(true);
//        d.addComponent(new Button(Command.create(ItemStatus.CREATED.getName(), ItemStatus.icons[ItemStatus.CREATED.ordinal()], (e) -> {
//            setStatus(ItemStatus.CREATED);
//            d.dispose();
//        })));
//        d.addComponent(new Button(Command.create(ItemStatus.ONGOING.getName(), ItemStatus.icons[ItemStatus.ONGOING.ordinal()], (e) -> {
//            setStatus(ItemStatus.ONGOING);
////            statusChangeHandler.process(ItemStatus.ONGOING);
//            d.dispose();
//        })));
//        d.addComponent(new Button(Command.create(ItemStatus.WAITING.getName(), ItemStatus.icons[ItemStatus.WAITING.ordinal()], (e) -> {
//            setStatus(ItemStatus.WAITING);
////            statusChangeHandler.process(item, ItemStatus.WAITING);
//            d.dispose();
//        })));
//        d.addComponent(new Button(Command.create(ItemStatus.DONE.getName(), ItemStatus.icons[ItemStatus.DONE.ordinal()], (e) -> {
//            setStatus(ItemStatus.DONE);
////            statusChangeHandler.process(item, ItemStatus.DONE);
//            d.dispose();
//        })));
//        d.addComponent(new Button(Command.create(ItemStatus.CANCELLED.getName(), ItemStatus.icons[ItemStatus.CANCELLED.ordinal()], (e) -> {
//            setStatus(ItemStatus.CANCELLED);
////            statusChangeHandler.process(item, ItemStatus.CANCELLED);
////            setStatus(ItemStatus.CANCELLED);
//            d.dispose();
//        })));
//        d.showPopupDialog(this); //showDialog(x) = x is the component the dialog will 'point' to
//    }
//</editor-fold>
    @Override
    public void longPointerPress(int x, int y
    ) {
        super.longPointerPress(x, y);
//<editor-fold defaultstate="collapsed" desc="comment">
//        ItemStatus status = item.getStatus();
//                MyStringPicker selectStatus = new MyStringPicker(ItemStatus.CANCELLED, parseIdMap, get, set);
//                switch (status) {
//                    case STATUS_CREATED:
//                }
//TODO simplify code below to have only one command
//        Command setWaiting = Command.create(ItemStatus.WAITING.getDescription(), Icons.iconCheckboxWaiting, (e) -> {
//            ItemStatus oldStatus = item.getStatus();
//            item.setStatus(ItemStatus.WAITING);
//            statusChangeHandler.process(item, oldStatus, oldStatus);
//            if (saveOnChange) {
//                DAO.getInstance().save(item);
//            }
//            setIcon(Icons.iconCheckboxWaiting);
//        });
//        Command setOngoing = Command.create(ItemStatus.ONGOING.getDescription(), Icons.iconCheckboxOngoingLabelStyle, (e) -> {
//            item.setStatus(ItemStatus.ONGOING);
//            if (saveOnChange) {
//                DAO.getInstance().save(item);
//            }
//            setIcon(Icons.iconCheckboxOngoingLabelStyle);
//        });
//        Command setCancelled = Command.create(ItemStatus.CANCELLED.getDescription(), Icons.iconCheckboxCancelled, (e) -> {
//            item.setStatus(ItemStatus.CANCELLED);
//            if (saveOnChange) {
//                DAO.getInstance().save(item);
//            }
//            setIcon(Icons.iconCheckboxCancelled);
//        });
////                Container dialogBody = new Container().add(new Button("test")).add(create);
//        final ActionListener al = new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//
//                d.dispose();
//            }
//        };
//</editor-fold>
        selectNewStatus();
//<editor-fold defaultstate="collapsed" desc="comment">
//        d = new Dialog("Select");
//        d.setLayout(BoxLayout.y());
////        d.setAutoDispose(true);
//        d.addComponent(new Button(Command.create(ItemStatus.CREATED.shortName, ItemStatus.icons[ItemStatus.CREATED.ordinal()], (e) -> {
//            setStatus(ItemStatus.CREATED);
////            d.dispose();
//        })));
//        d.addComponent(new Button(Command.create(ItemStatus.ONGOING.shortName, ItemStatus.icons[ItemStatus.ONGOING.ordinal()], (e) -> {
//            statusChangeHandler.process(item, ItemStatus.ONGOING);
////            d.dispose();
//        })));
//        d.addComponent(new Button(Command.create(ItemStatus.WAITING.shortName, ItemStatus.icons[ItemStatus.WAITING.ordinal()], (e) -> {
//            statusChangeHandler.process(item, ItemStatus.WAITING);
////            d.dispose();
//        })));
//        d.addComponent(new Button(Command.create(ItemStatus.DONE.shortName, ItemStatus.icons[ItemStatus.DONE.ordinal()], (e) -> {
//            statusChangeHandler.process(item, ItemStatus.DONE);
////            d.dispose();
//        })));
//        d.addComponent(new Button(Command.create(ItemStatus.CANCELLED.shortName, ItemStatus.icons[ItemStatus.CANCELLED.ordinal()], (e) -> {
//            statusChangeHandler.process(item, ItemStatus.CANCELLED);
////            setStatus(ItemStatus.CANCELLED);
////            d.dispose();
//        })));
//        d.showPopupDialog(this); //showDialog(x) = x is the component the dialog will 'point' to
//                Command userChoice = Dialog.show("Task state", dialogBody, new Command[]{setOngoing, setWaiting, setCancelled}, Dialog.TYPE_CONFIRMATION, null); //TODO!!!! make Dialog a nice popup
//                userChoice.actionPerformed(null);
//                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
//</editor-fold>
    }

}
