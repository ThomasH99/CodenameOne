/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Command;
import com.codename1.ui.Image;

/**
 *
 * @author Thomas
 */
public class MyCommand extends Command { //TODO!!!! remove this unused class

    final static int UNDEFINED = 0;
    final static int KEY_CODE = 1;
    final static int GAME_KEY = 2;
//    shortCut[] shortCuts;
    private ShortCut[] shortCuts;
//    private int keyCode;
    /* 0=undefined, 1=keyCode, 2=gameKey */
//    private int keyCodeStatus;
//    boolean keyCodeIsGameKey;
    private String helpText;
//    ContextCommand contextCommand;
//    MyActiveInterface activeInterface;

//    interface ContextCommand {
//
//        /** returns true if this command is active when it is being displayed - eg if active returns false, show command greyed out */
//        public boolean active(MyCommand myCommand);
//
//        /** returns the text to display for the command in the given context. Returns null if no text defined */
//        public String text(MyCommand myCommand);
//    }
    /**
     * Creates a new instance of Command
     * 
     * @param command the string that will be placed on the Soft buttons\Menu
     * @param icon the icon representing the command
     * @param addShortcutHint
     * @param keyCode key or gamekey code, -1 disables
     * @param isGameKey must be true if keyCode contains a gameKey code
     * @param helpText
     */
//    public MyCommand(String command, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText) {
    public MyCommand(String command, Image icon, ShortCut[] shortCuts, String helpText) {
//        super(getShortcutStr(command, shortCuts), icon);
        super(command, icon);
        this.shortCuts = shortCuts;
        this.helpText = helpText;
//        this.contextCommand = context;
    }
//    public MyCommand(MyActiveInterface activeInterface, Image icon, ShortCut[] shortCuts, String helpText) {
//        this("<undef>"/*activeInterface.getText(null)//-avoid calling before initialized*/, icon, shortCuts, helpText);
//        this.activeInterface = activeInterface;
////        this.setCommand(this.activeInterface.getText(this));
//    }

//    public MyCommand(String command, Image icon, ShortCut[] shortCuts, String helpText) {
//        this(command, icon, shortCuts, helpText);
////        super(getShortcutStr(command, shortCuts), icon);
////        this.shortCuts = shortCuts;
////        this.helpText = helpText;
//    }
    public MyCommand(String command, Image icon, int keyCode, String helpText) {
        this(command, icon, new ShortCut[]{new ShortCut(keyCode)}, helpText);
    }        
    
    public MyCommand(String command, ShortCut[] shortCuts, String helpText) {
        this(command, null, shortCuts, helpText);
    }

//    public MyCommand(MyActiveInterface activeInterface, ShortCut[] shortCuts, String helpText) {
//        this(activeInterface, null, shortCuts, helpText);
//    }
    public MyCommand(String command, int keyCode, String helpText) {
        this(command, null, new ShortCut[]{new ShortCut(keyCode)}, helpText);
    }

    public MyCommand(String command, int keyCode) {
        this(command, null, new ShortCut[]{new ShortCut(keyCode)}, null);
    }

//    public MyCommand(MyActiveInterface activeInterface, int keyCode, String helpText) {
//        this(activeInterface, new ShortCut[]{new ShortCut(keyCode)}, helpText);
//    }
    public MyCommand(int keyCode, String helpText) {
        this("", new ShortCut[]{new ShortCut(keyCode)}, helpText);
    }

    public MyCommand(String command, Image icon, String helpText) {
        this(command, null, null, helpText);
    }        
    
    public MyCommand(String command, Image icon) {
        this(command, icon, "");
    }        
    
    public MyCommand(Image icon) {
        this("", icon, "");
    }        
    

//    public MyCommand(String command, int keyCode, String helpText) {
//        this(command, null, true, ShortCut[] {new ShortCut(keyCode)}, false, helpText);
//    }
    public MyCommand(String command, String helpText) {
        this(command, null, null, helpText);
    }

//    public MyCommand(MyActiveInterface activeInterface, String helpText) {
//        this(activeInterface, null, null, helpText);
//    }
//    public MyCommand(MyActiveInterface activeInterface) {
//        this(activeInterface, null);
//    }
    public MyCommand(String command) {
        this(command, "");
    }

    public String fullString() {
        String s = "MyCommand{";
        s += " commandName=" + getCommandName();
//        s+=" icon="+icon;
        s += " helpText=" + helpText;
        if (shortCuts == null) {
            s += " shortcuts=<null>";
        } else {
            s += " shortcuts.length=" + shortCuts.length;
            s += "[";
            for (int i = 0, size = shortCuts.length; i < size; i++) { //UI: a command may have several shortcuts (e.g. both numeric for ITU keyboard and letter for qwerty device)
                s += shortCuts[i];
            }
        }
        s += "] }";
        return s;
    }

    public ShortCut[] getShortCuts() {
        return shortCuts;
    }

    public void setShortCuts(ShortCut[] shortCuts) {
        this.shortCuts = shortCuts;
    }

// <editor-fold defaultstate="collapsed" desc="comment">
//    public MyCommand(String command, String helpText) {
//        this(command, null, false, -1, false, helpText);
//    }
//    public MyCommand(String command, Image icon, boolean addShortcutHint, int keyCode, boolean isGameKey, String helpText) {
////    public MyCommand(String command, Image icon, boolean addShortcutHint, int[] keyCode, boolean[] isGameKey, String helpText) {
//        super((addShortcutHint && keyCode >= 0) ? getShortcutStr(command, keyCode, isGameKey, false) : command, icon);
//        this.helpText = helpText;
//        if (keyCode >= 0) {
////            if (isGameKey) {
////                this.keyCodeStatus = GAME_KEY;
////                this.keyCode = keyCode;
////            } else {
////                this.keyCodeStatus = KEY_CODE;
////                if (Settings.getInstance().forceKeyboardShortCutsToUpperCase()) {
////                    this.keyCode = Character.toLowerCase((char) keyCode);
////                } else {
////                    this.keyCode = keyCode;
////                }
////            }
//            addShortCut(!isGameKey && Settings.getInstance().forceKeyboardShortCutsToUpperCase() ? Character.toLowerCase((char) keyCode) : keyCode, isGameKey);
//        }
//    }
//    public MyCommand(String command, ShortCut[] shortCuts, String helpText) {
//        this(command, null, shortCuts, helpText);
//    }
//    static String getShortcutStr(String commandName, ShortCut[] shortCuts, boolean addKeyShortCutHintsForNonQwerty) {
//    }// </editor-fold>
    /**
     * creates a string with the appropriate text label for a command, based on the command text and the shortcut keycode, e.g.
     * "Indent", rightArrow => "Indent [→]". For other game keys than right/left/up/down/fire the String cmd is returned.
     * @param commandName
     * @param gameKey
     * @return
     */
    private String getShortcutStr(String commandName, ShortCut[] shortCuts) {
//    static String getShortcutStr(String commandName, int keyCode, boolean isGameKey, boolean alwaysAddKeyboardShortCut) {
        String str = "";
        String separator = "";
        if (shortCuts != null) {
            for (int i = 0, size = shortCuts.length; i < size; i++) { //UI: a command may have several shortcuts (e.g. both numeric for ITU keyboard and letter for qwerty device)
                char shortCutChar = shortCuts[i].getShortcutChar();
//                if (((shortCutChar>='a' && shortCutChar<='z') || (shortCutChar>='A' && shortCutChar<='Z') && Settings.getInstance().addQwertyShortCutHintsToCommands()))
                if (shortCuts[i].addKeyShortCutHintsForNonQwerty || ((shortCutChar >= '0' && shortCutChar <= '9') || shortCutChar == '#' || shortCutChar == '*') || Settings.getInstance().addNonITUKeyboardShortCutHintsToCommands()) {
                    str += shortCutChar + separator;
                    separator = "|"; //UI: multiple shortcuts are shown separated by '|' (or just show them
                }
            }
//            return str.equals("") ? commandName : commandName + " [" + str + "]";
        }
//        else {
//            return commandName;
//        }
        return str.equals("") ? commandName : commandName + " [" + str + "]";
    }

    private String getShortcutStr(String commandName) {
        return getShortcutStr(commandName, shortCuts);
    }

    public String getText(Object command) {
        return null;
    }

//    public String getText() {
//        return null;
//    }
    
   public String toString() { //need to overwrite this since toString otherwise accesses the command String directly without using getCommandName()
        return getCommandName();
    } 

// <editor-fold defaultstate="collapsed" desc="comment">
//    static String getShortcutStr(String commandName, int keyCode, boolean isGameKey, boolean alwaysAddKeyboardShortCut) {
////    static String getShortcutStr(String commandName, ShortCut[] shortCutsint keyCode, boolean isGameKey, boolean alwaysAddKeyboardShortCut) {
////        int gameKey = Display.getInstance().getGameAction(gameKey);
//        char gameChar;
//        if (isGameKey) {
//            int gameKey = Display.getInstance().getGameAction(keyCode);
//            if (gameKey == Display.GAME_RIGHT) {
//                gameChar = '→'; //arrow right
//            } else if (gameKey == Display.GAME_LEFT) {
//                gameChar = '�?'; //arrow right
//            } else if (gameKey == Display.GAME_UP) {
//                gameChar = '↑'; //arrow right
//            } else if (gameKey == Display.GAME_DOWN) {
//                gameChar = '↓'; //arrow right
//            } else if (gameKey == Display.GAME_FIRE) {
//                gameChar = '◙'; //alternatives: ◘•�?
//            } else {
////            gameChar = '?';
//                return commandName;
//            }
////            return commandName + " [" + gameChar + "]";
//        } else {
//            boolean hasQwerty = Display.getInstance().getKeyboardType() == Display.KEYBOARD_TYPE_QWERTY;
//            if (hasQwerty || alwaysAddKeyboardShortCut) {
//                gameChar = Settings.getInstance().forceKeyboardShortCutsToUpperCase() ? Character.toUpperCase((char) keyCode) : (char) keyCode;
//            } else {
//                return commandName;
//            }
//        }
//        return commandName + " [" + gameChar + "]";
//    }
//    char getGameShortCut(int keyCode) {
//        int gameKey = Display.getInstance().getGameAction(keyCode);
//        char gameChar = ' ';
//        if (gameKey == Display.GAME_RIGHT) {
//            gameChar = '→'; //arrow right
//        } else if (gameKey == Display.GAME_LEFT) {
//            gameChar = '�?'; //arrow right
//        } else if (gameKey == Display.GAME_UP) {
//            gameChar = '↑'; //arrow right
//        } else if (gameKey == Display.GAME_DOWN) {
//            gameChar = '↓'; //arrow right
//        } else if (gameKey == Display.GAME_FIRE) {
//            gameChar = '◙'; //alternatives: ◘•�?
//        }
//        return gameChar;
//    }
//    static String xgetCmdGameShortcut(String commandName, int gameKey) {
////        int gameKey = Display.getInstance().getGameAction(gameKey);
//        char gameChar;
//        if (gameKey == Display.GAME_RIGHT) {
//            gameChar = '→'; //arrow right
//        } else if (gameKey == Display.GAME_LEFT) {
//            gameChar = '�?'; //arrow right
//        } else if (gameKey == Display.GAME_UP) {
//            gameChar = '↑'; //arrow right
//        } else if (gameKey == Display.GAME_DOWN) {
//            gameChar = '↓'; //arrow right
//        } else if (gameKey == Display.GAME_FIRE) {
//            gameChar = '◙'; //alternatives: ◘•�?
//        } else {
////            gameChar = '?';
//            return commandName;
//        }
//        return commandName + " [" + gameChar + "]";
//    }
//    public void addCommandAndShortCuts(Form form) {
//        if (shortCuts != null) {
//            for (int i = 0, size = shortCuts.length; i < size; i++) {
//                ShortCut shortCut = shortCuts[i];
//                if (shortCut.isGameKey) {
//                    form.addGameKeyListener(shortCut.keyCode, this);
//                } else {
//                    form.addKeyListener(shortCut.keyCode, this);
//                    if (shortCut.caseInsensitive || Settings.getInstance().forceKeyboardShortCutsToUpperCase()) {
//                        form.addKeyListener(Character.toLowerCase((char) shortCut.keyCode), this); //UI: also add lower key shortcut
//                    }
//                }
//            }
//        }
//        ((Form)form).addCommand(this);
//    }
    /**
     * creates a string with the appropriate text label for a command, based on the command text and the shortcut keycode, e.g.
     * "Copy", 'c' => "Copy [C]" (NB lowercase letters are always shown as uppercase)
     * @param commandName
     * @param keyCode
     * @return
     */
//    static String xgetCmdNameWithShortcut(String commandName, int keyCode) {
//        return commandName + " [" + Character.toUpperCase((char) keyCode) + "]";
//    }
//    String getCommand() {
//        return getCommandName();
//    }// </editor-fold>
    public String getCommandName() {
        String s=getText(this);
        if (s != null) {
            return getShortcutStr(s);
        } else {
            return super.getCommandName();
        }
    }
//    public String getCommandName() {
//        if (activeInterface != null && activeInterface.getText(this) != null) {
//            return getShortcutStr(activeInterface.getText(this));
//        }
//        return super.getCommandName();
//    }



    /** returns true if this Command is active, e.g. to allow a rendered to show it as active, or currently disabled/unapplicable */
    public boolean isActive(Object component) {
//        if (activeInterface!=null)
//            return activeInterface.isActive(this);
//        else
        return true;
    }
    public boolean isActive() {
        return true;
    }
//    public boolean isActive() {
//        return true;
//    }
    
    
}
