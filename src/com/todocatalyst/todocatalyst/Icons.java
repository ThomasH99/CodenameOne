/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.CheckBox;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;

/**
 *
 * @author Thomas
 */
public class Icons {

    //TODO smarter pattern for Icons https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
    private static Icons INSTANCE;

    private Icons() {

    }

    public static Icons get() {
        if (INSTANCE == null) {

//            iconInsertNewTaskExdend = iconInsertNewTaskIndent.flipHorizontally(true); //flip the above image

            INSTANCE = new Icons();
//            Display.getInstance().set
        }
        return INSTANCE;
    }
    
    public static Image makeT(char material){
//        return FontImage.createMaterial(material, UIManager.getInstance().getComponentStyle("TitleCommand"));
        return make(material, "TitleCommand");
    }
    public static Image makeL(char material){
//        return FontImage.createMaterial(material, UIManager.getInstance().getComponentStyle("Label"));
        return make(material, "Label");
    }

    public static Image make(char material, String styleUIID){
        return FontImage.createMaterial(material, UIManager.getInstance().getComponentStyle(styleUIID));
    }

    private final static Style labelStyle = new Label().getStyle();
    private final static Style toolBarStyle = new Toolbar().getStyle();
    //TODO!!! CN1 uses style "TitleCommand" for toolbar commands
//    private Style toolbarStyle = (getnew Label()).getStyle();

    static Image iconShowMoreLabelStyle(){return makeT(FontImage.MATERIAL_EXPAND_MORE);}
    final static char iconShowMoreLabelStyleX = FontImage.MATERIAL_EXPAND_MORE;
    final static Image iconShowMoreLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_EXPAND_MORE, labelStyle);
    final static Image iconShowLessLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_EXPAND_LESS, labelStyle);
    final static Image iconEditSymbolLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, labelStyle);
    final static Image iconAddTimeStampToCommentLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_ACCESS_TIME, labelStyle);

    //TIMER
//    final static Image iconTimerSymbolToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_TIMER, toolBarStyle);
    static Image iconTimerSymbolToolbarStyle(){return makeT(FontImage.MATERIAL_TIMER);}
//    final static Image iconTimerSymbolToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_TIMER, UIManager.getInstance().getComponentStyle("TitleCommand"));
    final static Image iconTimerSymbolLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_TIMER, labelStyle);
    static Image iconTimerOffToolbarStyle(){return makeT(FontImage.MATERIAL_TIMER_OFF);}
//    final static Image iconTimerOffToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_TIMER_OFF, toolBarStyle);
    static Image iconTimerAutoStartTimerOnNextTaskToolbarStyle(){return makeT(FontImage.MATERIAL_HISTORY);}
//    final static Image iconTimerAutoStartTimerOnNextTaskToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_HISTORY, toolBarStyle); //History: clock with arrow around
//    final static Image iconTimerAutoStartTimerOnNextTaskToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_AUTORENEW, toolBarStyle); //Autorenew: two circular arrows
    final static Image iconTimerAutoGotoNextTaskLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_PAUSE_CIRCLE_FILLED, labelStyle);
    final static Image iconTimerStartLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_PLAY_CIRCLE_OUTLINE, labelStyle);
    final static Image iconTimerPauseLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_PAUSE_CIRCLE_FILLED, labelStyle);
//    final static Image iconTimerBuzzerOnOffLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_TRENDING_FLAT, labelStyle); //Trending flat = long right arrow
    final static Image iconTimerBuzzerOnOffLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_FAST_FORWARD, labelStyle); //Fast Forward = Play next, 
    final static Image iconTimerScreenAlwaysOnLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_LIGHTBULB_OUTLINE, labelStyle); //Fast Forward = Play next, 
    final static Image iconTimerInterruptedTaskLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_FLASH_OFF, labelStyle); //Warning: triangle with '!', or use Flash Off to show it was interrupted
    final static Image iconTimerStopExitTimer = FontImage.createMaterial(FontImage.MATERIAL_STOP, labelStyle); //Warning: triangle with '!', or use Flash Off to show it was interrupted

    //ALARM
    final static Image iconSnoozeLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_SNOOZE, labelStyle);
    final static Image iconAlarmOffLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_ALARM_OFF, labelStyle);

    //ITEM
    final static Image iconAlarmSetLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_ALARM_ON, labelStyle);
    final static Image iconSettingsLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_SETTINGS, labelStyle);
    final static Image iconSettingsApplicationLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_SETTINGS_APPLICATIONS, labelStyle);
    final static Image iconWorkTimeSettingsLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_WATCH_LATER, labelStyle);
    final static Image iconLowPriorityLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_WATCH_LATER, labelStyle);
    final static Image iconStarUnselectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_STAR_BORDER, labelStyle);
    final static Image iconStarSelectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_STAR, labelStyle);
//    final static Image iconSelectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_RADIO_BUTTON_CHECKED, labelStyle, 2); //3mm
    final static Image iconSelectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_BOX, labelStyle, 2); //3mm
//    final static Image iconUnselectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_RADIO_BUTTON_UNCHECKED, labelStyle, 2);
    final static Image iconUnselectedLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_BOX_OUTLINE_BLANK, labelStyle, 2);
    final static Image iconTemplateStatusSymbolLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_DO_NOT_DISTURB_ALT, labelStyle);
    static Image iconSetDueDateToToday(){return makeT(FontImage.MATERIAL_TODAY);}
//    final static Image iconSetDueDateToToday = FontImage.createMaterial(FontImage.MATERIAL_TODAY, labelStyle);
//    final static Image iconIndentExdendInsertNewTask = FontImage.createMaterial(FontImage.MATERIAL_COMPARE_ARROWS, labelStyle);
//    final static Image iconIndentExdendInsertNewTask = FontImage.createMaterial(FontImage.MATERIAL_SWAP_HORIZ, labelStyle);
//    final static Image iconInsertNewTaskIndent = FontImage.createMaterial(FontImage.MATERIAL_FORWARD, labelStyle); //fat arrow
//    final static Image iconInsertNewTaskIndent = FontImage.createMaterial(FontImage.MATERIAL_ARROW_FORWARD, labelStyle); //MATERIAL_TRENDING_FLAT:thin arrow, TODO!! should be MATERIAL_KEYBOARD_RETURN flipped, MATERIAL_SUBDIRECTORY_ARROW_RIGHT
//    final static Image iconInsertNewTaskExdend = iconInsertNewTaskIndent.flipHorizontally(true); //flip the above image
//    static Image iconInsertNewTaskExdend = FontImage.createMaterial(FontImage.MATERIAL_TRENDING_FLAT, labelStyle); //thin arrow
//    static Image iconInsertNewTaskExdend = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, labelStyle); //thin arrow, MATERIAL_KEYBOARD_BACKSPACE
//    static Image iconInsertNewTaskExdend; //init in constructor above // = iconInsertNewTaskIndent.flipHorizontally(true); //flip the above image
//    static Image iconInsertNewTaskExdend= iconInsertNewTaskIndent.rotate180Degrees(true); //flip the above image

    final static Image iconCreateSubTask = FontImage.createMaterial(FontImage.MATERIAL_PLAYLIST_PLAY, labelStyle);

    //TEMPLATE
    final static Image iconNewItemFromTemplate = FontImage.createMaterial(FontImage.MATERIAL_LIBRARY_ADD, labelStyle);

    //COMMANDS
    final static Image iconCmdSortOnOff = FontImage.createMaterial(FontImage.MATERIAL_SORT, toolBarStyle);
    final static Image iconCmdShowTaskDetails = FontImage.createMaterial(FontImage.MATERIAL_SUBTITLES, toolBarStyle);

    //FUTURE
    final static Image iconEncryptedSecurityLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_SECURITY, labelStyle); //mark encrypted tasks
    final static Image iconGraphsLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_POLL, labelStyle); //mark encrypted tasks

//    final static Image iconFunLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_SENTIMENT_SATISFIED, iconStyle);
    //CLEAR FIELD
    final static Image iconCloseCircle = FontImage.createMaterial(FontImage.MATERIAL_CLOSE, labelStyle);
    final static Image iconCloseCircleLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_HIGHLIGHT_OFF, labelStyle);

    //TASK STATUS
//<editor-fold defaultstate="collapsed" desc="comment">
//    final static Image iconCheckboxCreated = FontImage.createMaterial(FontImage.MATERIAL_CHECK_BOX_OUTLINE_BLANK, labelStyle);
//    final static char iconCheckboxCreatedChar = FontImage.MATERIAL_RADIO_BUTTON_UNCHECKED;
//    final static char iconCheckboxCancelledChar = FontImage.MATERIAL_REMOVE_CIRCLE; //dark circle to illustrate 'done with'
//    final static char iconCheckboxOngoingChar = FontImage.MATERIAL_TIMELAPSE;
//    final static char iconCheckboxDoneChar = FontImage.MATERIAL_CHECK_CIRCLE;
//    final static char iconCheckboxWaitingChar = FontImage.MATERIAL_PAUSE_CIRCLE_OUTLINE;
//</editor-fold>

    final static Image iconCheckboxCreated = FontImage.createMaterial(ItemStatus.iconCheckboxCreatedChar, labelStyle); //TODO NOT GOOD - same symbol as multiple selection
//    final static Image iconCheckboxCancelled = FontImage.createMaterial(FontImage.MATERIAL_INDETERMINATE_CHECK_BOX, labelStyle);
    final static Image iconCheckboxCancelled = FontImage.createMaterial(ItemStatus.iconCheckboxCancelledChar, labelStyle); //dark circle to illustrate 'done with'
//    final static Image iconCheckboxDone = FontImage.createMaterial(FontImage.MATERIAL_CHECK_BOX, labelStyle);
    final static Image iconCheckboxOngoing = FontImage.createMaterial(ItemStatus.iconCheckboxOngoingChar, labelStyle);
    final static Image iconCheckboxDone = FontImage.createMaterial(ItemStatus.iconCheckboxDoneChar, labelStyle);
//    final static Image iconCheckboxWaiting = FontImage.createMaterial(FontImage.MATERIAL_ACCOUNT_BOX, iconStyle);
//    final static Image iconCheckboxWaiting = FontImage.createMaterial(FontImage.MATERIAL_HOURGLASS_FULL, labelStyle);
    final static Image iconCheckboxWaiting = FontImage.createMaterial(ItemStatus.iconCheckboxWaitingChar, labelStyle);

    static Image iconBackToPrevFormToolbarStyle(){return makeT(FontImage.MATERIAL_ARROW_BACK);}
    static Image iconBackToPrevFormLabelStyle(){return makeL(FontImage.MATERIAL_ARROW_BACK);}
    final static Image iconBackToPrevFormToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolBarStyle);
    static Image iconEditPropertiesToolbarStyle(){return makeT(FontImage.MATERIAL_CHEVRON_RIGHT);}
    final static Image iconEditPropertiesToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, toolBarStyle);
    final static Image iconEditPropertiesLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, labelStyle);
//    static Image iconNewTaskToolbarStyle(){return makeT(FontImage.MATERIAL_ADD_CIRCLE);}
    static Image iconNewTaskToolbarStyle(){return makeT(FontImage.MATERIAL_ADD_CIRCLE_OUTLINE);}
    static Image iconNewToolbarStyle(){return makeT(FontImage.MATERIAL_ADD);}
//    final static Image iconNewToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_ADD, toolBarStyle);
    static Image iconInterruptToolbarStyle(){return makeT(FontImage.MATERIAL_FLASH_ON);}
    final static Image iconInterruptToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_FLASH_ON, toolBarStyle);
    final static Image iconEditSymbolToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, toolBarStyle);
    //TASK STATUS
//    final static Image iconCheckedTaskToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, toolBarStyle);
//    final static Image iconUncheckedTaskToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, toolBarStyle);
//    final static Image iconCheckMarkToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, toolBarStyle);
    final static Image iconAlarmSetToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_ALARM_ON, toolBarStyle);
    //EXPAND lists
    final static Image iconShowMoreToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_EXPAND_MORE, toolBarStyle);
    final static Image iconShowLessToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_EXPAND_LESS, toolBarStyle);
    final static Image iconMoveUpDownToolbarStyle = FontImage.createMaterial(FontImage.MATERIAL_SWAP_VERT, toolBarStyle);
    
    //LOGIN
    final static Image iconPerson = FontImage.createMaterial(FontImage.MATERIAL_PERSON, toolBarStyle);
    final static Image iconPersonNew = FontImage.createMaterial(FontImage.MATERIAL_PERSON_ADD, toolBarStyle);
    final static Image iconPersonIngocnito = FontImage.createMaterial(FontImage.MATERIAL_HELP, toolBarStyle);
    
    //OTHER
    final static Image iconLightBulb= FontImage.createMaterial(FontImage.MATERIAL_LIGHTBULB_OUTLINE, toolBarStyle); //submit ideas, suggestions or bugs
    final static Image iconFeedback= FontImage.createMaterial(FontImage.MATERIAL_FEEDBACK, toolBarStyle); //feedback speach bubble (could be used for submit ideas, suggestions or bugs)
    

    static Image getCheckBoxIcon(ItemStatus itemStatus) {
        switch (itemStatus) {
            case CREATED:
                return iconCheckboxCreated;
            case DONE:
                return iconCheckboxDone;
            case ONGOING:
                return iconCheckboxOngoing;
            case WAITING:
                return iconCheckboxWaiting;
            case CANCELLED:
                return iconCheckboxCancelled;
            default:
                assert false : "undefined task status";
        }
        return null;
    }

}
