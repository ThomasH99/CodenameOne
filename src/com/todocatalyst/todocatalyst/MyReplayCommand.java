/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Command;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

/**
 * special command that maintains a log of commands that can be/should be
 * replayed. Should only be used for commands that change to another screen.
 *
 * @author thomashjelm
 */
public class MyReplayCommand extends Command {

    private String cmdUniqueID;

    public MyReplayCommand(String command, Image icon) {
        super(command, icon);
        assert command != null && command.length() > 0 : "when using command name/String as unique ID it must not be null or empty";
        setCmdUniqueID(command);
        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
    }

    public MyReplayCommand(String command) {
        super(command);
        setCmdUniqueID(command); //use command string as unique ID (should be OK when
        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
    }

    public MyReplayCommand(String cmdUniqueID, String command, Image icon) {
        super(command, icon);
        setCmdUniqueID(cmdUniqueID);
        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
    }

    public MyReplayCommand(String cmdUniqueID, String command) {
        super(command);
        setCmdUniqueID(cmdUniqueID);
        ReplayLog.getInstance().addToSetOfScreenCommands(this); //automatically add this command to the current screen's set (assumes that the ReplayLog's map of commands has been cleared in the screens' constructor)
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ReplayLog.getInstance().pushCmd(this);
        super.actionPerformed(evt);
    }

    void setCmdUniqueID(String cmdUniqueID) {
        this.cmdUniqueID = cmdUniqueID;
    }

    String getCmdUniqueID() {
        return cmdUniqueID;
    }

    public static MyReplayCommand create(String cmdUniqueID, String name, Image icon, final ActionListener ev) {
        MyReplayCommand cmd = new MyReplayCommand(cmdUniqueID, name, icon) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ReplayLog.getInstance().pushCmd(this);
                ev.actionPerformed(evt);
            }
        };
        return cmd;
    }

    /**
     *
     * @param name
     * @param icon
     * @param ev
     * @return
     */
//    public static Command create(String name, Image icon, final ActionListener ev) {
    public static MyReplayCommand create(String name, Image icon, final ActionListener ev) {
        return create(name, name, icon, ev);
    }
    
//    @Override
//    public String toString() {
//        return getCommandName()+"/"+getCmdUniqueID();
//    }

}
