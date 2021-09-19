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
        /**
         * return true if change is maintained, false if the change shall be
         * ignored (e.g. changing too many subtasks and therefore cancelling)
         *
         * @param oldStatus
         * @param newStatus
         * @return true if newStatus was set was different from oldStatus
         */
        boolean processNewStatusValue(ItemStatus oldStatus, ItemStatus newStatus);
    }

//    interface IsItemOngoing {
//
//        boolean isOngoing();
//    }
    private ProcessItemStatusChange statusChangeHandler;
//    private IsItemOngoing itemOngoing;
    private ItemStatus itemStatus = null;
//    private static String singleIconStyleUIID; //style for the single status icon shown for example in items in lists
    private static String popupIconStyleUIID; //style for icons in popup menu (to select any status)
    private static String groupStyleUIID; //style for popup menu (a ComponentGroup)
//    private static Style singleIconStyle;
//    private static Style popupIconStyle;
//    private static Image[] iconsSingleStatus; // = new Image[]{Icons.iconCheckboxCreated, Icons.iconCheckboxOngoing, Icons.iconCheckboxWaiting, Icons.iconCheckboxDone, Icons.iconCheckboxCancelled};
//    private static Image[] iconsPopup; // = new Image[]{Icons.iconCheckboxCreated, Icons.iconCheckboxOngoing, Icons.iconCheckboxWaiting, Icons.iconCheckboxDone, Icons.iconCheckboxCancelled};
//    private static char[] iconsPopup; // = new Image[]{Icons.iconCheckboxCreated, Icons.iconCheckboxOngoing, Icons.iconCheckboxWaiting, Icons.iconCheckboxDone, Icons.iconCheckboxCancelled};
    private static char[] iconsPopupChar; // = new Image[]{Icons.iconCheckboxCreated, Icons.iconCheckboxOngoing, Icons.iconCheckboxWaiting, Icons.iconCheckboxDone, Icons.iconCheckboxCancelled};
    private boolean longPressed; //used to detect longPress and ignore the associated, unavoidable, shortPress event = actionEvent
//    private Dialog d;
    /**
     * activate menu to choose status on a single click
     */
    private boolean activateFullMenuOnSingleClick;
    private boolean inactive;

    public MyCheckBox() {
        this(ItemStatus.CREATED, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), null, null, null, null);
    }

    public MyCheckBox(ItemStatus itemStatus, ProcessItemStatusChange statusChangeHandler) {//, IsItemOngoing itemOngoing) {
        this(itemStatus, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), statusChangeHandler, null, null, null);
    }

    public MyCheckBox(ItemStatus itemStatus) {//, IsItemOngoing itemOngoing) {
        this(itemStatus, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), null, null, null, null, false);
    }

    public MyCheckBox(Item item) {//, IsItemOngoing itemOngoing) {
        this(item != null ? item.getStatus() : ItemStatus.CREATED, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), (oldStatus, newStatus) -> {
            if (item != null
                    && newStatus != oldStatus
                    && item.setStatus(newStatus)) {
                DAO.getInstance().saveToParseNow(item); //save (and trigger change event)
                return true;
            };
            return false;
        }, null, null, null, false);
    }

    public MyCheckBox(ItemStatus itemStatus, boolean makeInactive) {//, IsItemOngoing itemOngoing) {
        this(itemStatus, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), null, null, null, null, makeInactive);
    }

//    public MyCheckBox(Item item, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler) {
//    public MyCheckBox(ItemStatus initialItemStatus, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler, IsItemOngoing itemOngoingXXX) {
//        this(initialItemStatus, activateFullMenuOnSingleClick, statusChangeHandler, itemOngoingXXX, null, null, null);
//    }
    public MyCheckBox(ItemStatus initialItemStatus, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler, //IsItemOngoing itemOngoing,
            String singleIconStyleUIID, String popupIconStyleUIID, String groupStyleUIID) {
        this(initialItemStatus, activateFullMenuOnSingleClick, statusChangeHandler, singleIconStyleUIID, popupIconStyleUIID, groupStyleUIID, false);
    }

    public MyCheckBox(ItemStatus initialItemStatus, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler, //IsItemOngoing itemOngoing,
            String singleIconStyleUIID, String popupIconStyleUIID, String groupStyleUIID, boolean makeInactive) {
        super();
        inactive = makeInactive;
        setUIID("MyCheckBox");
//<editor-fold defaultstate="collapsed" desc="comment">
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
//        this.itemOngoing = itemOngoing;
//</editor-fold>
        this.popupIconStyleUIID = singleIconStyleUIID;
        if (this.popupIconStyleUIID == null) {
            this.popupIconStyleUIID = "ItemStatusPopupIcon";
        }
        Style s = null;
//<editor-fold defaultstate="collapsed" desc="comment">
//        Style s = UIManager.getInstance().getComponentStyle(singleIconStyle).get; //never returns null
//        if (iconsPopup == null //if no icons defined
//                || (popupIconStyleUIID != null && !popupIconStyleUIID.equals(this.popupIconStyleUIID)) //or if no name defined, or style name has changed
//                || (s = UIManager.getInstance().getComponentStyle(popupIconStyleUIID)).equals(this.popupIconStyle)) { //or if style has dynamically changed via CSS
////            this.singleIconStyleName = singleIconStyleUIID;
//            if (popupIconStyleUIID != null && !popupIconStyleUIID.equals(this.popupIconStyleUIID)) {
//                this.popupIconStyleUIID = popupIconStyleUIID;
//            }
//            if (this.popupIconStyleUIID == null) {
//                this.popupIconStyleUIID = "ItemStatusIcon";
//            }
//            if (this.popupIconStyle == null) {
//                this.popupIconStyle = UIManager.getInstance().getComponentStyle(this.popupIconStyleUIID);
//            }
////            ); //never returns null
//            if (s != null && !s.equals(popupIconStyle)) {
//                this.popupIconStyle = new Style(s);//keep a *copy* of the style (to ensure that CSS refresh works?!
//            }
//</editor-fold>
        iconsPopupChar = new char[ItemStatus.values().length];
//        iconsPopupChar[0] = Icons.iconItemStatusCreated;
//        iconsPopupChar[1] = Icons.iconItemStatusOngoing;
//        iconsPopupChar[2] = Icons.iconItemStatusWaiting;
//        iconsPopupChar[3] = Icons.iconItemStatusDone;
//        iconsPopupChar[4] = Icons.iconItemStatusCancelled;
        iconsPopupChar[0] = Icons.iconItemStatusCreatedCust;
        iconsPopupChar[1] = Icons.iconItemStatusOngoingCust;
        iconsPopupChar[2] = Icons.iconItemStatusWaitingCust;
        iconsPopupChar[3] = Icons.iconItemStatusDoneCust;
        iconsPopupChar[4] = Icons.iconItemStatusCancelledCust;
//        }
//        if (groupStyle != null && !groupStyle.equals(this.groupStyleName)) {
//        } else {
//        }
        this.groupStyleUIID = groupStyleUIID;
        if (this.groupStyleUIID == null) {
            this.groupStyleUIID = "ItemStatusPopup";
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (singleIconStyle != null && !singleIconStyle.equals(this.singleIconStyleName)) {
//            this.singleIconStyleName = singleIconStyle;
//        } else {
//            this.singleIconStyleName = "ItemStatusIcon";
//        }
//</editor-fold>
        s = null;
//<editor-fold defaultstate="collapsed" desc="comment">
//        Style s = UIManager.getInstance().getComponentStyle(singleIconStyle).get; //never returns null
//        if (iconsSingleStatus == null //if no icons defined
//                || (singleIconStyleUIID != null && !singleIconStyleUIID.equals(this.singleIconStyleUIID)) //or if no name defined, or style name has changed
//                || (s = UIManager.getInstance().getComponentStyle(singleIconStyleUIID)).equals(this.singleIconStyle)) { //or if style has dynamically changed via CSS
////            this.singleIconStyleName = singleIconStyleUIID;
//            if (singleIconStyleUIID != null && !singleIconStyleUIID.equals(this.singleIconStyleUIID)) {
//                this.singleIconStyleUIID = singleIconStyleUIID;
//            }
//            if (this.singleIconStyleUIID == null) {
//                this.singleIconStyleUIID = "ItemStatusIcon";
//            }
//            if (this.singleIconStyle == null) {
//                this.singleIconStyle = UIManager.getInstance().getComponentStyle(this.singleIconStyleUIID);
//            }
////            ); //never returns null
//            if (s != null && !s.equals(singleIconStyle)) {
//                this.singleIconStyle = new Style(s); //keep a *copy* of the style (to ensure that CSS refresh works?!
//            }
//            iconsSingleStatus = new Image[ItemStatus.values().length];
//            iconsSingleStatus[0] = FontImage.createMaterial(Icons.iconItemStatusCreated, singleIconStyle);
//            iconsSingleStatus[1] = FontImage.createMaterial(Icons.iconItemStatusOngoing, singleIconStyle);
//            iconsSingleStatus[2] = FontImage.createMaterial(Icons.iconItemStatusWaiting, singleIconStyle);
//            iconsSingleStatus[3] = FontImage.createMaterial(Icons.iconItemStatusDone, singleIconStyle);
//            iconsSingleStatus[4] = FontImage.createMaterial(Icons.iconItemStatusCancelled, singleIconStyle);
//        }
//</editor-fold>

        setStatus(initialItemStatus, false); //NB! Do this *after* initializing the icons above, but *before* setting statusChangeHandler to avoid infinite loop
        this.activateFullMenuOnSingleClick = activateFullMenuOnSingleClick;
        this.statusChangeHandler = statusChangeHandler;

//Handle single-click
        if (!inactive) {
            addActionListener((evt) -> {
                if (longPressed) { //ignore actionevent after longpress
                    longPressed = false;
                } else {
                    if (this.activateFullMenuOnSingleClick) {
                        selectNewStatusOnSingleClick();
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
            });
            addLongPressListener((evt) -> {
                longPressed = true;
                selectNewStatusOnSingleClick();
//                fireActionEvent(-1, -1); //trigger action event
            });
        }
    }

    public String toString() {
        return itemStatus + " " + super.toString();
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
//    void setIsItemOngoing(IsItemOngoing itemOngoing) {
//        this.itemOngoing = itemOngoing;
//    }
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
//            this.itemStatus = itemStatus; //update before making call below to avoid infinite loop
//            if (!inactive && runStatusChangeHandler && statusChangeHandler != null) {
//                statusChangeHandler.processNewStatusValue(oldStatus, itemStatus);
//            }
            if (statusChangeHandler == null || (!runStatusChangeHandler || statusChangeHandler.processNewStatusValue(oldStatus, itemStatus))) {
                this.itemStatus = itemStatus; //update before making call below to avoid infinite loop
                setStatusIcon(itemStatus);
            }
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
        setFontIcon(Icons.myIconFont, iconsPopupChar[itemStatus.ordinal()]);
    }

    ItemStatus getStatus() {
        return itemStatus;
    }

    private Button create(String cmdName, Image icon, final ActionListener ev) {
        Command c = Command.create(cmdName, icon, ev);
        Button b = new Button(c);
//<editor-fold defaultstate="collapsed" desc="comment">
//        Button b = new Button(new Command(cmdName) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                if (!inactive) {
//                    ev.actionPerformed(evt);
//                }
//            }
//        });
////        b.setUIID();
//        b.setIcon(icon);
//        return c;
//</editor-fold>
        return b;
    }

    private Button create(String cmdName, char materialIcon, final ActionListener ev) {
        Command c = Command.createMaterial(cmdName, materialIcon, ev);
        c.setIconFont(Icons.myIconFont);
        Button b = new Button(c);
//        b.setFontIcon(Icons.myIconFont, materialIcon);
        return b;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Button createOLD(String cmdName, char materialIcon, final ActionListener ev) {
//        Button b = new Button(new Command(cmdName) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                if (!inactive) {
//                    ev.actionPerformed(evt);
//                }
//            }
//        });
////        b.setUIID();
////        b.setIcon(materialIcon);
////        b.setMaterialIcon(materialIcon);
//        b.setFontIcon(Icons.myIconFont, materialIcon);
////        return c;
//        return b;
//    }
//</editor-fold>
    private void selectNewStatusOnSingleClick() {
        //TODO move this logic to Item.xxx
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
        MyPopupMenu d = new MyPopupMenu(groupStyleUIID,
                create("Cancel", null, (e) -> {
                }),
                create(ItemStatus.CREATED.getName(), iconsPopupChar[ItemStatus.CREATED.ordinal()], (e) -> {
                    setStatus(ItemStatus.CREATED);
                }),
                create(ItemStatus.ONGOING.getName(), iconsPopupChar[ItemStatus.ONGOING.ordinal()], (e) -> {
                    setStatus(ItemStatus.ONGOING);
                }),
                create(ItemStatus.WAITING.getName(), iconsPopupChar[ItemStatus.WAITING.ordinal()], (e) -> {
                    setStatus(ItemStatus.WAITING);
                }),
                create(ItemStatus.DONE.getName(), iconsPopupChar[ItemStatus.DONE.ordinal()], (e) -> {
                    setStatus(ItemStatus.DONE);
                }),
                create(ItemStatus.CANCELLED.getName(), iconsPopupChar[ItemStatus.CANCELLED.ordinal()], (e) -> {
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
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public void longPointerPressXXX(int x, int y) {
////        super.longPointerPress(x, y);
////<editor-fold defaultstate="collapsed" desc="comment">
////        ItemStatus status = item.getStatus();
////                MyStringPicker selectStatus = new MyStringPicker(ItemStatus.CANCELLED, parseIdMap, get, set);
////                switch (status) {
////                    case STATUS_CREATED:
////                }
////TODO simplify code below to have only one command
////        Command setWaiting = Command.create(ItemStatus.WAITING.getDescription(), Icons.iconCheckboxWaiting, (e) -> {
////            ItemStatus oldStatus = item.getStatus();
////            item.setStatus(ItemStatus.WAITING);
////            statusChangeHandler.process(item, oldStatus, oldStatus);
////            if (saveOnChange) {
////                DAO.getInstance().save(item);
////            }
////            setIcon(Icons.iconCheckboxWaiting);
////        });
////        Command setOngoing = Command.create(ItemStatus.ONGOING.getDescription(), Icons.iconCheckboxOngoingLabelStyle, (e) -> {
////            item.setStatus(ItemStatus.ONGOING);
////            if (saveOnChange) {
////                DAO.getInstance().save(item);
////            }
////            setIcon(Icons.iconCheckboxOngoingLabelStyle);
////        });
////        Command setCancelled = Command.create(ItemStatus.CANCELLED.getDescription(), Icons.iconCheckboxCancelled, (e) -> {
////            item.setStatus(ItemStatus.CANCELLED);
////            if (saveOnChange) {
////                DAO.getInstance().save(item);
////            }
////            setIcon(Icons.iconCheckboxCancelled);
////        });
//////                Container dialogBody = new Container().add(new Button("test")).add(create);
////        final ActionListener al = new ActionListener() {
////            @Override
////            public void actionPerformed(ActionEvent evt) {
////
////                d.dispose();
////            }
////        };
////</editor-fold>
//        if (!inactive) {
//            selectNewStatusOnSingleClick();
//            fireActionEvent(-1, -1); //trigger action event
//        }
//        super.longPointerPress(x, y); //do *after* the above
////        fireActionEvent(x, y); //ensure that longPress to select any new status will trigger updates //NECESSARY? Or will the pointerReleased trigger a normal actionEvent?
////<editor-fold defaultstate="collapsed" desc="comment">
////        d = new Dialog("Select");
////        d.setLayout(BoxLayout.y());
//////        d.setAutoDispose(true);
////        d.addComponent(new Button(Command.create(ItemStatus.CREATED.shortName, ItemStatus.icons[ItemStatus.CREATED.ordinal()], (e) -> {
////            setStatus(ItemStatus.CREATED);
//////            d.dispose();
////        })));
////        d.addComponent(new Button(Command.create(ItemStatus.ONGOING.shortName, ItemStatus.icons[ItemStatus.ONGOING.ordinal()], (e) -> {
////            statusChangeHandler.process(item, ItemStatus.ONGOING);
//////            d.dispose();
////        })));
////        d.addComponent(new Button(Command.create(ItemStatus.WAITING.shortName, ItemStatus.icons[ItemStatus.WAITING.ordinal()], (e) -> {
////            statusChangeHandler.process(item, ItemStatus.WAITING);
//////            d.dispose();
////        })));
////        d.addComponent(new Button(Command.create(ItemStatus.DONE.shortName, ItemStatus.icons[ItemStatus.DONE.ordinal()], (e) -> {
////            statusChangeHandler.process(item, ItemStatus.DONE);
//////            d.dispose();
////        })));
////        d.addComponent(new Button(Command.create(ItemStatus.CANCELLED.shortName, ItemStatus.icons[ItemStatus.CANCELLED.ordinal()], (e) -> {
////            statusChangeHandler.process(item, ItemStatus.CANCELLED);
//////            setStatus(ItemStatus.CANCELLED);
//////            d.dispose();
////        })));
////        d.showPopupDialog(this); //showDialog(x) = x is the component the dialog will 'point' to
////                Command userChoice = Dialog.show("Task state", dialogBody, new Command[]{setOngoing, setWaiting, setCancelled}, Dialog.TYPE_CONFIRMATION, null); //TODO!!!! make Dialog a nice popup
////                userChoice.actionPerformed(null);
////                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
////</editor-fold>
//    }
//</editor-fold>
}
