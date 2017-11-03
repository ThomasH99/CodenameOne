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
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BoxLayout;
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

    ProcessItemStatusChange statusChangeHandler;
    IsItemOngoing itemOngoing;
    private ItemStatus itemStatus;
//    private Dialog d;
    /**
     * activate menu to choose status on a single click
     */
    private boolean activateFullMenuOnSingleClick;

    public MyCheckBox(ItemStatus itemStatus, ProcessItemStatusChange statusChangeHandler, IsItemOngoing itemOngoing) {
        this(itemStatus, MyPrefs.getBoolean(MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress), statusChangeHandler, itemOngoing);
    }

//    public MyCheckBox(Item item, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler) {
    public MyCheckBox(ItemStatus itemStatus, boolean activateFullMenuOnSingleClick, ProcessItemStatusChange statusChangeHandler, IsItemOngoing itemOngoing) {
        super();
        setUIID("MyCheckBox");
//        String s = ItemStatus.values()[item.getStatus().ordinal()].fullDescription;
//        this.saveOnChange = saveOnChange;
        setStatus(itemStatus);
        this.activateFullMenuOnSingleClick = activateFullMenuOnSingleClick;
        this.statusChangeHandler = statusChangeHandler;
        this.itemOngoing = itemOngoing;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (MyCheckBox.this.activateFullMenuOnSingleClick) {
                    selectNewStatus();
                } else {
                    //TODO!! move below logic into static method in Item to avoid duplication
//                ItemStatus itemStatus = itemStatus.getStatus();
                    switch (MyCheckBox.this.itemStatus) {
                        case CREATED:
                        case WAITING:
                        case ONGOING:
                            setStatus(ItemStatus.DONE);
                            break;
                        case DONE:
                        case CANCELLED:
                            if (MyCheckBox.this.itemOngoing.isOngoing()) {
                                setStatus(ItemStatus.ONGOING);
                            } else {
                                setStatus(ItemStatus.CREATED);
                            }
                            break;
                        default:
                            assert false : "unknown ItemStatus=" + MyCheckBox.this.itemStatus;
                    }
                }
            }
        });
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

    /**
     * sets new status value. Does nothing is new itemStatus is the same as old
     * value.
     *
     * @param itemStatus
     */
    void setStatus(ItemStatus itemStatus) {
        //run statusChangeHandler.process *first* before changing status to have access to old status value
        if (itemStatus != this.itemStatus) {
            ItemStatus oldStatus = this.itemStatus;
            this.itemStatus = itemStatus; //update before making call below to avoid infinite loop
            if (statusChangeHandler != null) {
                statusChangeHandler.processNewStatusValue(oldStatus, itemStatus);
            }
//            setIcon(ItemStatus.icons[itemStatus.ordinal()]);
            setStatusIcon(itemStatus);
            repaint();
        }
    }

    /**
     * updates the statusIcon without any other effects
     *
     * @param itemStatus
     */
    void setStatusIcon(ItemStatus itemStatus) {
//        setIcon(ItemStatus.icons[itemStatus.ordinal()]);
        setIcon(ItemStatus.getStatusIcon(itemStatus));
    }

    ItemStatus getStatus() {
        return itemStatus;
    }

    private void selectNewStatus() {
        //TODO move this logic to Item.xxx
//        d = new Dialog("Select");
//        d.setLayout(BoxLayout.y());
//        d.setAutoDispose(true);
        MyPopupMenu d = new MyPopupMenu(true, Command.create(ItemStatus.CREATED.getName(), ItemStatus.icons[ItemStatus.CREATED.ordinal()], (e) -> {
            setStatus(ItemStatus.CREATED);
//            d.dispose();
        }), Command.create(ItemStatus.ONGOING.getName(), ItemStatus.icons[ItemStatus.ONGOING.ordinal()], (e) -> {
            setStatus(ItemStatus.ONGOING);
//            statusChangeHandler.process(ItemStatus.ONGOING);
//            d.dispose();
        }),
                Command.create(ItemStatus.WAITING.getName(), ItemStatus.icons[ItemStatus.WAITING.ordinal()], (e) -> {
                    setStatus(ItemStatus.WAITING);
//            statusChangeHandler.process(item, ItemStatus.WAITING);
//            d.dispose();
                }),
                Command.create(ItemStatus.DONE.getName(), ItemStatus.icons[ItemStatus.DONE.ordinal()], (e) -> {
                    setStatus(ItemStatus.DONE);
//            statusChangeHandler.process(item, ItemStatus.DONE);
//            d.dispose();
                }),
                Command.create(ItemStatus.CANCELLED.getName(), ItemStatus.icons[ItemStatus.CANCELLED.ordinal()], (e) -> {
                    setStatus(ItemStatus.CANCELLED);
//            statusChangeHandler.process(item, ItemStatus.CANCELLED);
//            setStatus(ItemStatus.CANCELLED);
//            d.dispose();
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
    public void longPointerPress(int x, int y) {
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
