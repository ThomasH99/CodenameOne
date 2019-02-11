/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.events.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * replays the sequence of commands and screens to reposition the app in the
 * same screen as when it was shot down (make the restart transparent)
 *
 * @author thomashjelm
 */
public class ReplayLog {

//TODO!!!! store that a new Item has been created and was being edited, not as Replay feature, just check on creation of such a screen    
//TODO!! add support for storing active popups (eg show Tasks/Workslots in RepeatRuleScreen (but also requires logging when exiting!)
    //TODO!!!! How to handle Timer? Just store if its active but no replay (meaning you will go back to main menu afterwards)? Better to store full path, but will it then activate in same state as before (continue based on previously stored timerActivatedTime)???
    final static String REPLAY_LOG_FILE_NAME = "ReplayLog";
    private static ReplayLog INSTANCE;

    private ArrayList<String> replayStack = null; //stores the stack of replay commands
    private Map<String, MyReplayCommand> screenCommands = new HashMap<>(); //helper - temporarily stores all commands for a screen to be able to find the right one
    private int currentIndex = -1;// -1; //0; //always set to 0 when ReplayLog is initialized
//    private boolean storeAllCommandsForScreen = true; //turn on/off ???
    private MyReplayCommand replayCommandToReplay = null; //stores the command for the current screen that should be replayed
    private boolean replayingInProgress = false; //true while replaying commands on startup, used to avoid ??
//    private boolean firstTime = true;
    private String screenName;

    public static ReplayLog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReplayLog();
        }
        if (INSTANCE.replayStack == null) {
            if (Storage.getInstance().exists(REPLAY_LOG_FILE_NAME)) {
                INSTANCE.replayStack = (ArrayList<String>) Storage.getInstance().readObject(REPLAY_LOG_FILE_NAME);
                if (INSTANCE.replayStack.size() > 0) {
                    INSTANCE.replayingInProgress = true;
                }
            }
            if (INSTANCE.replayStack == null) {
                INSTANCE.replayStack = new ArrayList<String>();
            }
        }
        return INSTANCE;
    }

    @Override
    public String toString() {
        String s = "ReplayStack = [";
        String sep = "";
        for (int i = 0, size = replayStack.size(); i < size; i++) {
            String r = replayStack.get(i);
            s += sep + i + ": " + r;
            sep = "; ";
        }
        return s+"]";
    }

    /**
     * reset (delete MyReplayCommands from previous MyForm) when a new MyForm is
     * being created. Called in MyForm constructor.
     */
    void deleteAllReplayCommandsFromPreviousScreen() {
        deleteAllReplayCommandsFromPreviousScreen(null);
    }

    void deleteAllReplayCommandsFromPreviousScreen(String screenName) {
//        if (storeAllCommandsForScreen) {
        this.screenName = screenName;
        screenCommands = new HashMap<>();
//        } else {
//            replayCommandToReplay = null;
//
//        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void init() {
////        if (logList == null && Storage.getInstance().exists(REPLAY_LOG_FILE_NAME)) {
////            logList = (ArrayList<String>) Storage.getInstance().readObject(REPLAY_LOG_FILE_NAME);
////        }
//        nextIndex = 0;
//    }

//    public void addCmdToReplayLog(String cmdUIID) {
//</editor-fold>
    /**
     * add a new command to the replay log stack. No effect if the replayCommand
     * is the command being replayed.
     *
     * @param replayCommand
     */
    public void pushCmd(MyReplayCommand replayCommand) {
//        if (logList == null && Storage.getInstance().exists(REPLAY_LOG_FILE_NAME)) {
//            logList = (ArrayList<String>) Storage.getInstance().readObject(REPLAY_LOG_FILE_NAME);
//        }
//        if (replayCommand != screenCommands.get(getPreviousCmdToReplayLog())) { //do not store command if it is the command being replayed
//        if (nextIndex == -1 || nextIndex < logList.size()) { //do not store command if it is the command being replayed
        if (!replayingInProgress) { //do not store command if it is the command being replayed
            boolean doubleDecl = replayStack.contains(replayCommand.getCmdUniqueID());
            ASSERT.that(!doubleDecl, () -> "Unique command ID \"" + replayCommand.getCmdUniqueID() + "\" already in list: " + replayStack);
            if (doubleDecl) {
                //remove a previous command (and all commands after it) which is stuck by mistake (due to crash??)
                String cmdStr;
                do {
                    ASSERT.that(replayStack.size() > 0, "logList should not be empty, if so, the test !cmdStr.equals(replayCommand.getCmdUniqueID()) has not worked correctly (except maybe if error happens for very first command)");
                    cmdStr = replayStack.get(replayStack.size() - 1);
                    replayStack.remove(replayStack.size() - 1);
                } while (!cmdStr.equals(replayCommand.getCmdUniqueID()));
            }
            replayStack.add(replayCommand.getCmdUniqueID());
            Storage.getInstance().writeObject(REPLAY_LOG_FILE_NAME, replayStack);
            Log.p("+++ ReplayCommand: " + replayCommand.getCmdUniqueID());
        }
    }

    /**
     * remove the last command when exiting a screen
     */
    public void popCmd() {
//        if (true) { //deactivate while testing
        if (replayStack.size() > 0) { //while debugging //TODO!!!!! remove DEBUG once ReplayCommands have been added everywhere
            if (Config.TEST) {
                Log.p("--- ReplayCommand: " + replayStack.get(replayStack.size() - 1));
            }
            replayStack.remove(replayStack.size() - 1); //TODO!!! add check that logList.size>=1 (not during testing to provoke stacktrace
        }
//        }
        Storage.getInstance().writeObject(REPLAY_LOG_FILE_NAME, replayStack);
    }

    /**
     * return the uniqueID of the next command on the replay log/stack (the next
     * one to replay, starting from the bottom of the stack)
     *
     * @return
     */
    private MyReplayCommand fetchNextCmdFromReplayLog() {
//        if (isReplaying()) {
        String cmdUIID;// = null;
//        if (nextIndex < logList.size()) {
        currentIndex++;
        if (replayingInProgress && currentIndex < replayStack.size()) {
            cmdUIID = replayStack.get(currentIndex); //DON'T remove since we replay the screens, so must still keep same order
//            nextIndex++;
//                if (replayingNow && nextIndex > logList.size()) {
//            if (nextIndex > logList.size()-1) {
//                replayingNow = false; //we've reached the end of the replay log, so stop replay (replay can never be triggered again in this session)
//            }
            if (cmdUIID != null) {
//                if (storeAllCommandsForScreen) {
//                return screenCommands.get(cmdUIID);
                MyReplayCommand cmd = screenCommands.get(cmdUIID);
                if (cmd != null) {
                    return cmd;
                } else {
//                        +Display.getInstance().getCurrent() instanceof MyForm?((MyForm)Display.getInstance().getCurrent()).SCREEN_TITLE:"<not a MyForm>"Display.getInstance().getCurrent().getTitle());
//                    in screen" + Display.getInstance().getCurrent().getTitle()
                    Log.p("!! ReplayCommand: " + cmdUIID + " not defined" + (screenName != null ? (" in screen " + screenName) : "")+ " ScreenCmds="+screenCommands+" "+this);
                }
            }
        } else {
            replayingInProgress = false;
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void updateReplayingNow() {
//        if (currentIndex > logList.size() - 1) {
//            replayingNow = false; //we've reached the end of the replay log, so stop replay (replay can never be triggered again in this session)
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private String fetchNextCmdFromReplayLogXXX() {
//        if (isReplaying()) {
//            if (nextIndex < logList.size()) {
//                String cmdUIID = logList.get(nextIndex); //DON'T remove since we replay the screens, so must still keep same order
//                nextIndex++;
//                if (replayingNow && nextIndex > logList.size()) {
//                    replayingNow = false;
//                }
////        Storage.getInstance().writeObject(REPLAY_LOG_FILE_NAME, logList);
//return cmdUIID;
//            } else {
//                return null;
//            }
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
    public boolean isReplayInProgress() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (firstTime) {**
//            if (logList.size() > 0) {
//                replayingNow = true;
//            }
//            firstTime = false; //replay can only be triggered once, on the very first call
//        }
//        if (nextIndex == -1 && logList.size() > 0) {
//            nextIndex = 0; //initialize replay
//        } else if (nextIndex >= logList.size()) {
//            nextIndex = -1; //replay finished
//        }
//        return nextIndex != -1;
//</editor-fold>
        return replayingInProgress;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean startReplayXXX() {
//        return nextIndex == -1 && logList.size() > 0;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private String getPreviousCmdToReplayLogXXX() {
//        if (nextIndex-1 < logList.size()&&nextIndex-1>=0) {
//            String cmdUIID = logList.get(nextIndex-1); //DON'T remove since we replay the screens, so must still keep same order
//            return cmdUIID;
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
    /**
     * get the current entry in the log or null if nextIndex is not pointing to
     * one (no replay ongoing)
     *
     * @return
     */
//    private String getCurrentReplayCmdIDXXX() {
////        return nextIndex >= 0 && nextIndex < logList.size() ? logList.get(nextIndex) : null;
//        return logList.get(currentIndex); //next
//    }
    /**
     * takes the next command and replays
     *
     * @return true if a command was replayed, false if not (if false the screen
     * in question should behave normally and do show())
     */
    public boolean replayCmd(ActionEvent evt) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (!replayingNow) {
//            if (logList.size() > 0) {
//                replayingNow = true;
//            } else if (replayingNow && nextIndex > logList.size()) {
//                replayingNow = false;
//            }
//        }
//</editor-fold>
        if (replayingInProgress) {
//        MyReplayCommand cmd = screenCommands.get(getNextCmdToReplayLog());
            MyReplayCommand cmd = fetchNextCmdFromReplayLog();
            if (cmd != null) { //if ever logged command is not found (eg the Item has beend deleted), the replay stops here
                Log.p("x ReplayCommand: " + cmd.getCmdUniqueID());
                cmd.actionPerformed(evt);
//                updateReplayingNow(); //update *after* replaying the command to avoid storing it again
                return true;
            } else {
//                ASSERT.that(replayingNow == false, "");
                replayingInProgress = false;
//                while (logList.size() >= currentIndex - 1) { //if force breaking the replay, remove all non-replayed commands including the broken one at nextIndex-1 (so log is correct as other actions are added)
                while (replayStack.size() - 1 > currentIndex - 1) { //if force breaking the replay, remove all non-replayed commands including the broken one at nextIndex-1 (so log is correct as other actions are added)
                    //eg log={cmd1, cmd2, cmd3}, cmd1 replayes well, cmd2 breaks (nextIndex is then moved ahead to 2) => remove all commands until size==1 (size:2 >= nextIndex-1)
                    Log.p("!!!! Replay Command not found and removed from ReplayLog: " + replayStack.get(replayStack.size() - 1)+" "+this);
                    replayStack.remove(replayStack.size() - 1);
                }
                Storage.getInstance().writeObject(REPLAY_LOG_FILE_NAME, replayStack); //ensure reduced log is stored 
            }
        }
        return false;
    }

    /**
     * ********************************************************
     */
    /**
     * clear the set of Commands for the current screen. Called in each screens
     * constructor.
     */
    void clearSetOfScreenCommands() {
        screenCommands.clear();
    }

    /**
     * helps maintain the map of the current screen's commands, to easily find
     * the one to replay
     *
     * @param cmd
     */
    public void addToSetOfScreenCommands(MyReplayCommand replayCommand) {

//        if (storeAllCommandsForScreen) {
//            if (false) {
//                ASSERT.that(screenCommands.get(replayCommand.getCmdUniqueID()) == null, "MyReplayCommand created twice:" + replayCommand.getCmdUniqueID() + " cmd=" + replayCommand);
//            }
        screenCommands.put(replayCommand.getCmdUniqueID(), replayCommand);
//        } else {
////            if (replayCommand.getCmdUniqueID().equals(getCurrentReplayCmdID())) {
//            if (replayCommand.getCmdUniqueID().equals(replayStack.get(currentIndex))) {
//                ASSERT.that(replayCommandToReplay == null, "MyReplayCommand created twice for this screen:" + replayCommand.getCmdUniqueID() + " cmd=" + replayCommand);
//                this.replayCommandToReplay = replayCommand;
//            }
//        }
    }

}
