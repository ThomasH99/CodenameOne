/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.Command;
//import com.codename1.ui.CommandTracked;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

/**
 * special command that maintains a log of commands that can be/should be
 * replayed. Should only be used for commands that change to another screen.
 *
 * @author thomashjelm
 */
public class MyReplayCommand extends CommandTracked {

    private String cmdUniqueID;
    private boolean keep; //indicates a replaycommand that should NOT be deleted when rebuilding the list of replaycommands for a screen (on refreshAfterEdit)
    private ActionListener actionListener;
    private MyForm.GetBool pushCmd;

//    public MyReplayCommand(String command, Image icon) {
//        super(command, icon);
//        assert command != null && command.length() > 0 : "when using command name/String as unique ID it must not be null or empty";
//        setCmdUniqueID(command);
//        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
//    }
    private MyReplayCommand(String cmdUniqueID, String commandName, Image icon) {
        this(cmdUniqueID, commandName, icon, null);
    }

    private MyReplayCommand(String cmdUniqueID, String commandName, Image icon, ActionListener actionListener) {
        super(commandName, icon);
        this.actionListener = actionListener;
        assert (cmdUniqueID != null && !cmdUniqueID.isEmpty()) || (commandName != null && commandName.length() > 0) : "when using command name/String as unique ID it must not be null or empty";
        if (cmdUniqueID != null && !cmdUniqueID.isEmpty()) {
            setCmdUniqueID(cmdUniqueID);
        } else {
            setCmdUniqueID(commandName);
        }
        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
    }

    private MyReplayCommand(String cmdUniqueID, String commandName, char icon) {
        this(cmdUniqueID, commandName, icon, null, null);
    }

    public MyReplayCommand(String cmdUniqueID, String commandName, char icon, ActionListener actionListener) {
        this(cmdUniqueID, commandName, icon, null, actionListener);
        setPushCmd(() -> true);
    }

    private MyReplayCommand(String cmdUniqueID, String commandName, char icon, Font font, ActionListener actionListener) {
        super(commandName);
        this.actionListener = actionListener;
        if (font != null) {
            setIconFont(font);
        }
        setMaterialIcon(icon);
//        setMaterialIconSize(MyPrefs.defaultIconSizeInMM.getFloat());
        assert (cmdUniqueID != null && !cmdUniqueID.isEmpty()) || (commandName != null && commandName.length() > 0) : "when using command name/String as unique ID it must not be null or empty";
        if (cmdUniqueID != null && !cmdUniqueID.isEmpty()) {
            setCmdUniqueID(cmdUniqueID);
        } else {
            setCmdUniqueID(commandName);
        }
        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
    }

    public MyReplayCommand(String cmdUniqueID, String commandName) {
//        super(command);
//        setCmdUniqueID(cmdUniqueID);
//        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
        this(cmdUniqueID, commandName, null);
    }

    private MyReplayCommand(String commandName) {
//        super(command);
//        setCmdUniqueID(command); //use command string as unique ID (should be OK when
//        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
        this(commandName, commandName, null);
    }

//    @Override
//    public String toStringXXX() {
//        return "UniqID=" + cmdUniqueID + "; keep=" + keep;
//    }
    //TODO: toString is used (I think) in eg menu commands to get the string to display, should use getCommandName() instead - puch fix
//    @Override
//    public String toString() {
////        return getCommandName()+"/"+getCmdUniqueID();
//        return cmdUniqueID;
//    }
//    @Override
//    public void actionPerformed(ActionEvent evt) {
////        ReplayLog.getInstance().pushCmd(this);
//        super.actionPerformed(evt);
//    }
    void setCmdUniqueID(String cmdUniqueID) {
        this.cmdUniqueID = cmdUniqueID;
    }

    String getCmdUniqueID() {
        return cmdUniqueID;
    }

    private void setPushCmd(MyForm.GetBool pushCmd) {
        this.pushCmd = pushCmd;
    }

    void setKeep(boolean keep) {
        this.keep = keep;
    }

    boolean isKeep() {
        return keep;
    }

    public static MyReplayCommand create(String cmdUniqueId, String cmdUniquePostfix, String commandName, Image icon, final ActionListener ev, boolean keep) {
        return create(cmdUniqueId, cmdUniquePostfix, commandName, icon, ev, keep, () -> true);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (pushCmd.getVal()) {
            ReplayLog.getInstance().pushCmd(this); //DON'T call here, is called in MyReplayCommand.actionPerformed which is called below!
        }//                ev.actionPerformed(evt);
        if (actionListener != null) {
//                    MyAnalyticsService.event(Display.getInstance().getCurrent(), cmdUniqueID);
            actionListener.actionPerformed(evt);
        } else {
            ASSERT.that("NOT REALLY SURE THIS WORKS!!!");
//                    super.actionPerformed(evt);
        }
        super.actionPerformed(evt);
    }

    /**
     *
     * @param cmdUniqueId
     * @param cmdUniquePostfix
     * @param commandName
     * @param icon
     * @param ev
     * @param keep
     * @param pushCmd if this function returns false, the command will NOT be
     * pushed (and thus not replayed later) - used for deciding whether to push
     * Timer (should only be done if BigTimer is launched, not if smallTimer is
     * launched)
     * @return
     */
    public static MyReplayCommand createXXX(String cmdUniqueId, String cmdUniquePostfix, String commandName, Image icon, final ActionListener ev, boolean keep, MyForm.GetBool pushCmd) {
//        String cmdUniqueIdFull = cmdUniqueId + cmdUniquePostfix;
        String cmdUniqueIdFull = cmdUniqueId + cmdUniquePostfix;
        MyReplayCommand cmd = new MyReplayCommand(cmdUniqueIdFull, commandName, icon) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (pushCmd.getVal()) {
                    ReplayLog.getInstance().pushCmd(this); //DON'T call here, is called in MyReplayCommand.actionPerformed which is called below!
                }//                ev.actionPerformed(evt);
                if (ev != null) {
//                    MyAnalyticsService.event(Display.getInstance().getCurrent(), cmdUniqueID);
                    ev.actionPerformed(evt);
                } else {
                    ASSERT.that("NOT REALLY SURE THIS WORKS!!!");
//                    super.actionPerformed(evt);
                }
                super.actionPerformed(evt);
            }
        };
        cmd.setAnalyticsActionId(cmdUniqueIdFull);
        cmd.setKeep(keep);
//        if (Config.TEST) cmd.setName("ReplayCmd-" + cmdUniqueId);

        return cmd;
    }

    public static MyReplayCommand create(String cmdUniqueId, String cmdUniquePostfix, String commandName, Image icon, final ActionListener ev, boolean keep, MyForm.GetBool pushCmd) {
        String cmdUniqueIdFull = cmdUniqueId + cmdUniquePostfix;
        MyReplayCommand cmd = new MyReplayCommand(cmdUniqueIdFull, commandName, icon, ev);
        cmd.setPushCmd(pushCmd);
        cmd.setAnalyticsActionId(cmdUniqueIdFull);
        cmd.setKeep(keep);
//        if (Config.TEST) cmd.setName("ReplayCmd-" + cmdUniqueId);

        return cmd;
    }

    public static MyReplayCommand create(String cmdUniqueId, String cmdUniquePostfix, String commandName, char icon,
            final ActionListener ev, boolean keep, MyForm.GetBool pushCmd) {
        return create(cmdUniqueId, cmdUniquePostfix, commandName, icon, null, ev, keep, pushCmd);
    }

    public static MyReplayCommand create(String cmdUniqueId, String cmdUniquePostfix, String commandName, char icon,
            Font iconFont, final ActionListener ev, boolean keep, MyForm.GetBool pushCmd) {
        return create(cmdUniqueId, cmdUniquePostfix, commandName, icon, iconFont, ev, keep, pushCmd, cmdUniqueId + cmdUniquePostfix);
    }

//    public static MyReplayCommand createXXX(String cmdUniqueId, String cmdUniquePostfix, String commandName, char icon,
//            Font iconFont, final ActionListener ev, boolean keep, MyForm.GetBool pushCmd, String cmdUniqueIdFull) {
////        String cmdUniqueIdFull = cmdUniqueId + cmdUniquePostfix;
////        String cmdUniqueIdFull = cmdUniqueId + cmdUniquePostfix;
//        MyReplayCommand cmd = new MyReplayCommand(cmdUniqueIdFull, commandName, icon, iconFont) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                if (pushCmd.getVal()) {
//                    ReplayLog.getInstance().pushCmd(this); //DON'T call here, is called in MyReplayCommand.actionPerformed which is called below!
//                }//                ev.actionPerformed(evt);
//                if (ev != null) {
////                    MyAnalyticsService.event(Display.getInstance().getCurrent(), cmdUniqueID);
//                    ev.actionPerformed(evt);
//                } else {
//                    ASSERT.that("NOT REALLY SURE THIS WORKS!!!");
////                    super.actionPerformed(evt);
//                }
//                super.actionPerformed(evt);
//            }
//        };
//        cmd.setAnalyticsActionId(cmdUniqueIdFull);
//        cmd.setKeep(keep);
////        if (Config.TEST) cmd.setName("ReplayCmd-" + cmdUniqueId);
//
//        return cmd;
//    }
    public static MyReplayCommand create(String cmdUniqueId, String cmdUniquePostfix, String commandName, char icon,
            Font iconFont, final ActionListener ev, boolean keep, MyForm.GetBool pushCmd, String cmdUniqueIdFull) {
        MyReplayCommand cmd = new MyReplayCommand(cmdUniqueIdFull, commandName, icon, iconFont, ev);
        cmd.setPushCmd(pushCmd);
        cmd.setAnalyticsActionId(cmdUniqueIdFull);
        cmd.setKeep(keep);
//        if (Config.TEST) cmd.setName("ReplayCmd-" + cmdUniqueId);

        return cmd;
    }

    public static MyReplayCommand create(String cmdUniquePrefix, String cmdUniquePostfix, String commandName, Image icon, final ActionListener ev) {
        return create(cmdUniquePrefix, cmdUniquePostfix, commandName, icon, ev, false, () -> true);
    }

    public static MyReplayCommand create(String cmdUniquePrefix, String cmdUniquePostfix, String commandName, char icon, final ActionListener ev) {
        return create(cmdUniquePrefix, cmdUniquePostfix, commandName, icon, ev, false, () -> true);
    }

    public static MyReplayCommand create(String cmdUniqueID, String commandName, Image icon, final ActionListener ev) {
        return create(cmdUniqueID, "", commandName, icon, ev, false, () -> true);
    }

    public static MyReplayCommand create(String cmdUniqueID, String commandName, char icon, final ActionListener ev) {
        return create(cmdUniqueID, "", commandName, icon, ev, false, () -> true);
    }

    public static MyReplayCommand create(String cmdUniqueID, String commandName, char icon, Font iconFont, final ActionListener ev) {
        return create(cmdUniqueID, "", commandName, icon, iconFont, ev, false, () -> true);
    }

    public static MyReplayCommand create(String commandNameAndUniqueId, char icon, final ActionListener ev) {
        return create(commandNameAndUniqueId, "", commandNameAndUniqueId, icon, ev, false, () -> true);
    }

    public static MyReplayCommand create(String commandNameAndUniqueId, char icon, Font iconFont, final ActionListener ev) {
        return create(commandNameAndUniqueId, "", commandNameAndUniqueId, icon, iconFont, ev, false, () -> true);
    }
//    public static MyReplayCommand create(String cmdUniqueID, String commandName, Image icon, final ActionListener ev, MyForm.GetBool pushCmd) {
//        return create(cmdUniqueID, "", commandName, icon, ev, false, pushCmd);
//    }

    public static MyReplayCommand create(String cmdUniqueID, String commandName, Image icon, final ActionListener ev, MyForm.GetBool pushCmd) {
        return create(cmdUniqueID, "", commandName, icon, ev, false, pushCmd);
    }

    public static MyReplayCommand create(String cmdUniqueID, String commandName, Image icon, final ActionListener ev, boolean keep) {
        return create(cmdUniqueID, "", commandName, icon, ev, keep, () -> true);
    }

    public static MyReplayCommand create(String cmdUniqueID, String commandName, char icon, final ActionListener ev, boolean keep) {
        return create(cmdUniqueID, "", commandName, icon, ev, keep, () -> true);
    }

//    public static MyReplayCommand create(String name, char icon, final ActionListener ev, String analyticsActionId) {
//        return create(name, name, name, icon, null, ev, false, () -> true, analyticsActionId);
//    }
    public static MyReplayCommand createKeepXXX(String cmdUniqueID, String commandName, Image icon, final ActionListener ev, MyForm.GetBool pushCmd) {
        return create(cmdUniqueID, "", commandName, icon, ev, true, pushCmd);
    }

    public static MyReplayCommand createKeepXXX(String cmdUniqueID, String commandName, char icon, final ActionListener ev, MyForm.GetBool pushCmd) {
        return create(cmdUniqueID, "", commandName, icon, ev, true, pushCmd);
    }

    public static MyReplayCommand createKeep(String cmdUniqueID, String commandName, Image icon, final ActionListener ev) {
        return create(cmdUniqueID, "", commandName, icon, ev, true, () -> true);
    }

    public static MyReplayCommand createKeep(String cmdUniqueID, String commandName, char icon, final ActionListener ev) {
        return create(cmdUniqueID, "", commandName, icon, ev, true, () -> true);
    }

    public static MyReplayCommand createKeep(String cmdUniqueCmdName, char icon, final ActionListener ev) {
        return create(cmdUniqueCmdName, "", cmdUniqueCmdName, icon, ev, true, () -> true);
    }

    public static MyReplayCommand create(String name) {
        ASSERT.that("NOT REALLY SURE THIS WORKS!!!");
        return create(name, "", name, null, null, false);
    }

    /**
     *
     * @param cmdUniqueNameAndId
     * @param icon
     * @param ev
     * @return
     */
//    public static CommandTracked create(String name, Image icon, final ActionListener ev) {
//    public static MyReplayCommand create(String name, String analyticsActionId, Image icon, final ActionListener ev) {
////        MyReplayCommand c = create(name, name, icon, ev);
////        c.setAnalyticsActionId(analyticsActionId);
////        return c;
//        return create(name, name, analyticsActionId, icon, ev);
//    }
    public static MyReplayCommand create(String cmdUniqueNameAndId, Image icon, final ActionListener ev) {
        return create(cmdUniqueNameAndId, "", cmdUniqueNameAndId, icon, ev, false);
    }

}
