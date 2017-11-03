/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.List;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.list.ListCellRenderer;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.table.TableLayout;
import java.util.Vector;

/**
 *
 * @author Thomas
 */
public class MySubMenu extends MyCommand {

    private Command[] commands;
//    Vector commands;
//    Command createSubMenuCommand;
//    Form parentForm;

//    MySubMenu(String command, Vector commands, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText, Form parentForm) {
//    MySubMenu(String command, Vector commands, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText) {
    MySubMenu(String command, Command[] commands, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText) {
//        super(command+(Settings.getInstance().addDotsToSubMenus()?"...":""), icon, addShortcutHint, keyCode, isGameKey, helpText);
        super(command + (Settings.getInstance().addDotsToSubMenus() ? "..." : ""), icon, keyCode == Integer.MIN_VALUE ? null : new ShortCut[]{new ShortCut(keyCode, isGameKey)}, helpText);
//        this.parentForm = parentForm;
        setCommands(commands);
    }
// <editor-fold defaultstate="collapsed" desc="comment">
//    MySubMenu(String command, Command[] commands, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText, Form parentForm) {
//    MySubMenu(String command, Command[] commands, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText) {
//        this(command, ItemList.createVector(commands), icon, addShortcutHint, keyCode, isGameKey, helpText);
//    }// </editor-fold>

    MySubMenu(String command, Command[] commands, String helpText) {
        this(command, commands, null, true, Integer.MIN_VALUE, false, helpText);
    }

    MySubMenu(String command, Command[] commands, int keyCode, String helpText) {
        this(command, commands, null, true, keyCode, false, helpText);
    }

    MySubMenu(String command, Command[] commands, int keyCode) {
        this(command, commands, null, true, keyCode, false, "");
    }

    /**
     * use to create a submenu. Overwrite getCommands() and possibly
     * getCommandName()
     */
    MySubMenu(int keyCode, boolean isGameKey, String helpText) {
        this("", null, null, false, keyCode, isGameKey, helpText);
    }

    /**
     * creates a context menu on Fire
     */
    MySubMenu(String command, Command[] commands) {
        this(command, commands, null, true, Display.GAME_FIRE, false, "");
    }

    class xxMyDialog extends Dialog {

//        MyDialog() {
//            super();
//        }
//        Container getFormContentPane() {
////            return contentPane; //form super.getContentPane();
////            return ((Form)super).getContentPane();
//            return super.supergetContentPane();
//        }
        /**
         *
         * @param x position where to place the submenu (ne
         * @param y
         */
        Command xxshowSubMenu(int x, int y) {
            //most code copied and altered from Dialog.showPacked(String position, boolean modal) {
//                    this.position = position;
            int height = Display.getInstance().getDisplayHeight();
            int width = Display.getInstance().getDisplayWidth();
//        if(top > -1){
//            refreshTheme();
//        }
            Container contentPane = super.getContentPane();
            int maxWidth = 0; //maximum width of any of the elements shown in the submenu
            int w = 0; //optimization
            for (int i = 0, size = contentPane.getComponentCount(); i < size; i++) {
                if ((w = contentPane.getComponentAt(i).getWidth()) > maxWidth) {
                    maxWidth = w;
                }
            }
//        contentPane.setWidth(Math.max(maxWidth, width));

//        Component title = super.getTitleComponent();

            // hide the title if no text is there to allow the styles of the dialog title to disappear, we need this code here since otherwise the
            // preferred size logic of the dialog won't work with large title borders
//        if(dialogTitle != null && getUIManager().isThemeConstant("hideEmptyTitleBool", false)) {
//            boolean b = getTitle().length() > 0;
//            getTitleArea().setVisible(b);
//            getTitleComponent().setVisible(b);
//        }

            Style contentPaneStyle = getDialogStyle();

//        int menuHeight = calcMenuHeight();

            // allows a text area to recalculate its preferred size if embedded within a dialog
//        revalidate();
            int prefHeight = contentPane.getPreferredH();
            int prefWidth = contentPane.getPreferredW();
            prefWidth = Math.min(maxWidth, width);
//            if (contentPaneStyle.getBorder() != null) {
//                prefWidth = Math.max(contentPaneStyle.getBorder().getMinimumWidth(), prefWidth);
//                prefHeight = Math.max(contentPaneStyle.getBorder().getMinimumHeight(), prefHeight);
//            }
//        height = height - /*menuHeight - title.getPreferredH()*/;
//            int topBottom = Math.max(0, (height - prefHeight) / 2);
//            int leftRight = Math.max(0, (width - prefWidth) / 2);

            int margin = 10; //the minimum free space left around the submenu to avoid it covering/hiding all the form below

            int leftGap = x;
            int rightGap = width - prefWidth - x + margin;
            if (rightGap < margin) { //if rightGap<0 means menu would go beyond the right side of the screen
                leftGap = leftGap + rightGap - margin; //move the leftGap the same amount of pixels to the left
                rightGap = margin;
            }
            if (leftGap < margin) { //if leftGap negative means menu would stretch beyond the left side of the screen
                leftGap = margin; //set it to minimum
            }

            int top = y;
            int bottom = height - y - prefHeight + margin;
            if (bottom < margin) {
                top = top + bottom - margin;
                bottom = margin;
            }
            if (top < margin) {
                top = margin;
            }

//        if(position.equals(BorderLayout.CENTER)) {
//            return show(topBottom, topBottom, leftRight, leftRight, true, true); //modal
            return show(top, bottom, leftGap, rightGap, false, true); //modal
//            return lastCommandPressed;
//        } 
        }
    }
// <editor-fold defaultstate="collapsed" desc="comment">
//    MySubMenu(String command, Vector commands, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText) {
//        this(command, commands, icon, addShortcutHint, keyCode, isGameKey, helpText);
//    }
//
//    MySubMenu(String command, Command[] commands, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText) {
//        this(command, ItemList.createVector(commands), icon, addShortcutHint, keyCode, isGameKey, helpText);
//    }

//    MySubMenu(String command) {
//        super(command+(Settings.getInstance().addDotsToSubMenus()?"...":""));
//    }
//    MySubMenu(String command, Vector commands, Form parentForm) {
////        this(command, commands, null, true, -1, false, null, parentForm);
//        this(command, commands, null, true, -1, false, null);
//    }
//    MySubMenu(String command, Command createSubMenuCommand) {
//        this(command, null, null, true, -1, false, null);
//        this.createSubMenuCommand=createSubMenuCommand;
//    }
//    MySubMenu(String command, Command[] commands, Form parentForm) {
//        this(command, ItemList.createVector(commands), parentForm);
//    }
//    MySubMenu(String command, Command[] commands) {
////        this(command, commands, null);
//        this(command, commands);
//    }
//    public void addCommandAndShortCuts(Form form) {
//        if (form!=null && form instanceof MyForm) {
//            for (int i=0, size=shortCuts.length; i<size; i++) {
//
//            }
//            ((MyForm)form).myAddCommands(commands);
////            for (int i=0, size=commands.size();i<size;i++) {
////                parentForm
////            }
//        }
//    }// </editor-fold>
//    final void setCommands(Vector commands) {
    final void setCommands(Command[] commands) {
        this.commands = commands;
//        if (parentForm!=null && parentForm instanceof MyForm) {
//            ((MyForm)parentForm).myAddCommands(commands);
////            for (int i=0, size=commands.size();i<size;i++) {
////                parentForm
////            }
//        }
    }

    private void showSubMenu(Command[] commands, int x, int y) {
        showSubMenu(commands, x, y, -1, true);
    }

    void showSubMenu(Command[] commands, int x, int y, int defaultSelection, boolean selectSecondFromTop) {
        final Dialog subMenu = new Dialog();
//        final MyDialog subMenu = new MyDialog();
        subMenu.setAutoAdjustDialogSize(true);
        subMenu.setUIID("SubMenu");
        subMenu.setDisposeWhenPointerOutOfBounds(true); //works!!
//        subMenu.setTransitionInAnimator(CommonTransitions.createFastSlide(CommonTransitions.SLIDE_HORIZONTAL, false, 300)); //true: slide to the right
        subMenu.setTransitionInAnimator(CommonTransitions.createDialogPulsate()); //true: slide to the right
        BorderLayout layout = new BorderLayout();
//        layout.setCenterBehavior(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE);
//        subMenu.setLayout(layout);
        subMenu.setLayout(new MySubMenuLayout());
//        subMenu.addComponent(BorderLayout.CENTER, content);
//        subMenu.setLayout(new BoxLayout((BoxLayout.Y_AXIS)));
//        subMenu.setLayout(new FlowLayout(Component.CENTER));
//        subMenu.setLayout(new TableLayout());
        subMenu.setAutoDispose(true); //automatically closes as soon as user has selected an option
//        subMenu.

        //        final List content = new List(selection.getChildren());
//        final List content = new List(commands);
        Vector commandList = new Vector();
        for (int i = 0, size = commands.length; i < size; i++) {
            Command cmd = commands[i];
            //only show active commands
//            if ((cmd instanceof MyCommand &&((MyCommand)cmd).isActive(null))||cmd instanceof Command) {
            if (cmd instanceof MyCommand) {
                if (((MyCommand) cmd).isActive(null)) {
                    commandList.addElement(cmd);
                }
            } else if (cmd instanceof Command) {
                commandList.addElement(cmd);
            } else {
                ASSERT.that("All submenu items should be instances of either MyCommand or Command");
            }
        }
//        subMenu.setLayout(new GridLayout(commandList.size(), 1));
        final List menuList = new List(commandList);
        menuList.setCommandList(true);
        if (defaultSelection >= 0 && defaultSelection < menuList.size()) {
            menuList.setSelectedIndex(defaultSelection); //UI: select second top-most menu item to make it easier to move up/down to get sorrounding commands
        } else if (selectSecondFromTop && menuList.size() >= 3) {
            menuList.setSelectedIndex(1); //UI: select second top-most menu item to make it easier to move up/down to get sorrounding commands
        }
        menuList.setListSizeCalculationSampleCount(Math.min(commandList.size(), 20)); //UI: ensure size is calculated for all emnu items, default is just first 5
        menuList.setSmoothScrolling(true);
//        menuList.setFixedSelection(List.FIXED_NONE_ONE_ELEMENT_MARGIN_FROM_EDGE);
        menuList.setFixedSelection(List.FIXED_NONE_CYCLIC);
//        menuList.getStyle().setBgTransparency(0);
//        menuList.setNumericKeyActions(false); // avoid that numeric keyboard triggers a jump to displayed items in list
        menuList.setNumericKeyActions(Settings.getInstance().allowNumericKeysToSelectMenuCommand()); // avoid that numeric keyboard triggers a jump to displayed items in list
        menuList.setItemGap(0);
        menuList.setShouldCalcPreferredSize(true); //recalc minimim size
//        menuList.set
//        menuList.setUIID("SubMenu");
//        subMenu.addComponent(BorderLayout.WEST, menuList);
        subMenu.addComponent(menuList);
        subMenu.setShouldCalcPreferredSize(true);
        subMenu.revalidate();
        if (false) {
            subMenu.showPacked(null, true);
        }
//        subMenu.addComponent(menuList);


//    content.setListCellRenderer(new DefaultListCellRenderer(false));
//        menuList.setRenderer(xxcontextMenuCellRenderer);
        menuList.setRenderer(new ListCellRenderer() {
            Label text = new Label();
//        text.setUIID("Label"); //

            public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
                String str = "";
                if (value instanceof Command) {
                    str = ((Command) value).getCommandName();
                }
                text.setFocus(isSelected);
                text.setText(str);
                if (isSelected) {
                    text.getSelectedStyle().setBgTransparency(100);
                } else {
                    text.getSelectedStyle().setBgTransparency(0);
                }
                return text;
            }

            public Component getListFocusComponent(List list) {
//            return background;
                return null;
            }
        });

//        menuList.addActionListener(new ActionListener() { //-not necessary when using setCommandList?!
//            public void actionPerformed(ActionEvent evt) {
//                Command c = (Command) menuList.getSelectedItem();
//                ActionEvent e = new ActionEvent(c);
//                subMenu.dispose();
//                c.actionPerformed(e);
////                actionCommand(c);
//            }
//        });

        Command select = new Command("Select") {
            public void actionPerformed(ActionEvent evt) {
                final Command c = (Command) menuList.getSelectedItem();
                final ActionEvent e = new ActionEvent(c);
//                subMenu.dispose(); //added THJ
                // this code is executing in a separate thread
                Display.getInstance().callSeriallyAndWait(new Runnable() {
                    public void run() {
                        // this occurs on the EDT so I can make changes to UI components
                        c.actionPerformed(e);
                    }
                });
//                actionCommand(c);
            }
        };
//        final Dialog oldMenuDialog = menuDialog;
        Command cancel = new Command("Cancel") {
            public void actionPerformed(ActionEvent evt) {
//                oldMenuDialog.show();
//                subMenu.dispose(); //added THJ
            }
        };
//        subMenu.setDialogStyle(menuDialog.getDialogStyle());
        subMenu.addCommand(cancel);
        subMenu.addCommand(select);

        if (true) { //don't try to calculate the optimal size for the contextMenu, just pop it up
//            subMenu.showSubMenu(x, y);
            subMenu.show();
        } else if (x < 0 || y < 0) {
            subMenu.showPacked(BorderLayout.CENTER, true);
        } else {
            int height = Display.getInstance().getDisplayHeight();
            int width = Display.getInstance().getDisplayWidth();
//            int maxWidth = 0; //maximum width of any of the elements shown in the submenu
//            int w = 0; //optimization
//            for (int i = 0, size = contentPane.getComponentCount(); i < size; i++) {
//                if ((w = contentPane.getComponentAt(i).getWidth()) > maxWidth) {
//                    maxWidth = w;
//                }
//            }
//            int prefHeight = subMenu.getPreferredH();
//            int prefWidth = subMenu.getPreferredW();
//            Container contentPane = subMenu.getFormContentPane();
            Container contentPane = subMenu.getContentPane();
            contentPane.removeComponent(contentPane.getComponentAt(0)); //remove the title label (which stretches the component to screen width?)
            contentPane.revalidate();
//            Container contentPane = subMenu.getContentPane();
            int prefHeight = contentPane.getPreferredH();
            int prefWidth = contentPane.getPreferredW();

//            int topMargin = contentPane.getStyle().getMargin(Component.TOP);
//            int bottomMargin = contentPane.getStyle().getMargin(Component.BOTTOM);
//            int leftMargin = contentPane.getStyle().getMargin(Component.LEFT);
//            int rightMargin = contentPane.getStyle().getMargin(Component.RIGHT);

//            int borderWidth = 0;
//            int borderHeight = 0;
//            Style contentPaneStyle = subMenu.getDialogStyle();
//            if (contentPaneStyle.getBorder() != null) {
//                borderWidth = contentPaneStyle.getBorder().getMinimumWidth();
//                borderHeight = contentPaneStyle.getBorder().getMinimumHeight();
//                prefWidth = Math.max(prefWidth, contentPaneStyle.getBorder().getMinimumWidth()); //returns the width *including* the border
//                prefHeight = Math.max(prefHeight, contentPaneStyle.getBorder().getMinimumHeight());
//            }
            prefWidth = Math.min(prefWidth, width);
//            int temp = borderWidth + borderHeight;

            int margin = 5; //the minimum free space left around the submenu to avoid it covering/hiding all the form below
            int rightFingerMargin = 20; //how much to right of the push position to move the menu?
            int topFingerMargin = -10; //how much to right of the push position to move the menu?

            int leftGap = Math.max(x, margin) + rightFingerMargin;
            int rightGap = width - prefWidth - leftGap;
            if (rightGap < margin) { //if rightGap<0 means menu would go beyond the right side of the screen
                leftGap = leftGap + (rightGap - margin); //move the leftGap the same amount of pixels to the left
                rightGap = margin;
            }
            if (leftGap < margin) { //if leftGap negative means menu would stretch beyond the left side of the screen
                leftGap = margin; //set it to minimum
            }

            int topGap = y + topFingerMargin;
//            if (top < margin) {
//                top = margin;
//            }
            int bottomGap = height - prefHeight - topGap;
            if (bottomGap < margin) {
                topGap = topGap + (bottomGap - margin);
                bottomGap = margin;
            }
//            bottomGap -= 20;
            if (topGap < margin) {
                topGap = margin;
            }

            subMenu.showAtPosition(topGap, bottomGap, leftGap, rightGap, true); //modal
//            subMenu.showSubMenu(x, y);
        }
    }

    /**
     * can be overwritten when creating the submenu to allow for dynamic menus
     */
    public Command[] getCommands() {
        return commands;
    }

    public void actionPerformed(ActionEvent evt) {
//        if (createSubMenuCommand != null) {
//            createSubMenuCommand.actionPerformed(evt);
//        } else {
        showSubMenu(getCommands(), -1, -1);
//        }
    }

    public void showSubMenu(int x, int y) {
        showSubMenu(getCommands(), x, y);
    }

    public void showSubMenu(int x, int y, int selectedItem) {
        showSubMenu(getCommands(), x, y, selectedItem, false);
    }
}
