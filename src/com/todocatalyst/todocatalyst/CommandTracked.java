/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Command;
import com.codename1.ui.Font;
import com.codename1.ui.Image;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;

/**
 * overrides CN1 command to intercept action event and track them via Analytics
 *
 * @author thomashjelm
 */
public class CommandTracked extends Command {

    private String actionId = null;

    public String getActionId() {
        return actionId;
    }

    public void setAnalyticsActionId(String actionId) {
        this.actionId = actionId;
    }

    CommandTracked(String command) {
        super(command);
    }

    CommandTracked(String command, String analyticsActionId) {
        super(command);
        setAnalyticsActionId(analyticsActionId);
    }

    CommandTracked(String command, Image icon) {
        super(command, icon);
//        actionId=analyticsActionId;
    }

    CommandTracked(String command, char icon) {
//        super(command, icon);
        super(command);
        setMaterialIcon(icon);
        setMaterialIconSize(MyPrefs.defaultIconSizeInMM.getFloat());
//        actionId=analyticsActionId;
    }
    
    CommandTracked(String command, char icon, Font iconFont) {
//        super(command, icon);
        super(command);
        setIconFont(iconFont);
        setMaterialIcon(icon);
        setMaterialIconSize(MyPrefs.defaultIconSizeInMM.getFloat());
//        actionId=analyticsActionId;
    }

    CommandTracked(String command, Image icon, String analyticsActionId) {
        super(command, icon);
        setAnalyticsActionId(analyticsActionId);
    }

    CommandTracked(String command, char icon, String analyticsActionId) {
//        super(command, icon);
        super(command);
        setMaterialIcon(icon);
        setMaterialIconSize(MyPrefs.defaultIconSizeInMM.getFloat());
        setAnalyticsActionId(analyticsActionId);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (MyAnalyticsService.isEnabled()) {
            if (getActionId() != null) {
                MyAnalyticsService.event(getActionId());
            } else if (this instanceof MyReplayCommand) {
                MyAnalyticsService.event(((MyReplayCommand) this).getCmdUniqueID());
            } else {
                MyAnalyticsService.event(getCommandName());
            }
        }
        super.actionPerformed(evt);
    }

//      public static com.codename1.ui.CommandTracked create(String name, Image icon, final ActionListener ev) {
    public static CommandTracked create(String name, Image icon, final ActionListener ev, String analyticsActionId) {
//        com.codename1.ui.CommandTracked cmd = new com.codename1.ui.CommandTracked(name) {
        CommandTracked cmd = new CommandTracked(name, icon) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                MyAnalyticsService.event(name);
                super.actionPerformed(evt);
                ev.actionPerformed(evt);
            }
        };
        cmd.setAnalyticsActionId(analyticsActionId);
        return cmd;
    }

    public static CommandTracked create(String name, char icon, final ActionListener ev, String analyticsActionId) {
//        com.codename1.ui.CommandTracked cmd = new com.codename1.ui.CommandTracked(name) {
        CommandTracked cmd = new CommandTracked(name, icon) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                MyAnalyticsService.event(name);
                super.actionPerformed(evt);
                ev.actionPerformed(evt);
            }
        };
        cmd.setAnalyticsActionId(analyticsActionId);
        return cmd;
    }
    public static CommandTracked create(String name, char icon, Font iconFont, final ActionListener ev, String analyticsActionId) {
//        com.codename1.ui.CommandTracked cmd = new com.codename1.ui.CommandTracked(name) {
        CommandTracked cmd = new CommandTracked(name, icon, iconFont) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                MyAnalyticsService.event(name);
                super.actionPerformed(evt);
                ev.actionPerformed(evt);
            }
        };
        cmd.setAnalyticsActionId(analyticsActionId);
        return cmd;
    }
////      @Override
//       public static CommandTracked createMaterial(String name, char icon, final ActionListener ev,String analyticsActionId) {
//        CommandTracked cmd = new CommandTracked(name,icon){
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                MyAnalyticsService.event(name);
//                ev.actionPerformed(evt);
//            }
//        };
//        cmd.setMaterialIcon(icon);
//        return cmd;
//    }
}
