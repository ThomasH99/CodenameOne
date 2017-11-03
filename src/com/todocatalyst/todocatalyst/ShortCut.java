/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.todocatalyst.todocatalyst;

//import com.sun.lwuit.Display;

import com.codename1.ui.Display;


/**
 *
 * @author Thomas
 */
    public class ShortCut {

        int keyCode;
        boolean isGameKey;
        boolean addShortCutHint;
        boolean caseInsensitive;
        boolean addKeyShortCutHintsForNonQwerty;

        ShortCut(int keyCode, boolean isGameKey, boolean addShortCutHint, boolean caseInsensitive, boolean addKeyShortCutHintsForNonQwerty) {
            this.keyCode = keyCode;
            this.isGameKey = isGameKey;
            this.addShortCutHint = addShortCutHint;
            this.caseInsensitive = caseInsensitive;
            this.addKeyShortCutHintsForNonQwerty = addKeyShortCutHintsForNonQwerty;
        }

        ShortCut(int keyCode, boolean isGameKey) {
            this(keyCode, isGameKey, true, true, true);
        }

        ShortCut(int keyCode) {
            this(keyCode, false, true, true, true);
        }

        public String toString() {
            String s="ShortCut{";
            s+=" keyCode="+keyCode;
            s+=" isGameKey="+isGameKey;
            s+=" addShortCutHint="+addShortCutHint;
            s+=" caseInsensitive="+caseInsensitive;
            s+=" addKeyShortCutHintsForNonQwerty="+addKeyShortCutHintsForNonQwerty;
            s+="}";
            return s;
        }
/**
 * returns the appropriate character for a shortcut. If it's a gamechar it returns e.g. arrows, if it's a letter, it returns the letter (possibly forced to uppercase if
 * Setting forceKeyboardShortCutsToUpperCase() is true)
 * @return
 */
        char getShortcutChar() {
            if (isGameKey) {
                int gameKey = Display.getInstance().getGameAction(keyCode);
                if (gameKey == Display.GAME_RIGHT) {
                    return '?'; //arrow right
                } else if (gameKey == Display.GAME_LEFT) {
                    return '?'; //arrow right
                } else if (gameKey == Display.GAME_UP) {
                    return '?'; //arrow right
                } else if (gameKey == Display.GAME_DOWN) {
                    return '?'; //arrow right
                } else if (gameKey == Display.GAME_FIRE) {
//                return = '?'; //alternatives: â—˜â€¢Í?
                    return '?'; //alternatives: ?•?
                } else {
                    return ' ';
                }
            } else {
//            boolean hasQwerty = Display.getInstance().getKeyboardType() == Display.KEYBOARD_TYPE_QWERTY;
//                if (Display.getInstance().getKeyboardType() == Display.KEYBOARD_TYPE_QWERTY || addKeyShortCutHintsForNonQwerty || Settings.getInstance().forceKeyboardShortCutsToUpperCase()) {
                if (Settings.getInstance().forceKeyboardShortCutsToUpperCase()) {
                    return Character.toUpperCase((char) keyCode);
                } else {
                    return (char) keyCode;
                }
            }
        }
    }