/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Thomas
 */
    public class PreviouslyRunningTimerEntry implements Externalizable {

        final static String CLASS_NAME_PREVIOUSLY_RUNNING_TIMERS = "previouslyRunningTimerObjectId";

        String previouslyRunningTimerObjectId;
//        String task;
        long previouslyRunningTimersCountSoFarMillis;
        boolean running; // = false; //was the timer running when this task was interrupted? Default: false.
        boolean savedLocallyInTimer; // = false; //was the task saved in Parse only by the Timer (eg an interrupt task) - meaning it must be deleted if the user Cancels

//            ItemList previousItemList;
         public PreviouslyRunningTimerEntry() {
//            savedLocallyInTimer=false;
        }
        
        PreviouslyRunningTimerEntry(String previouslyRunningTimerObjectId, long previouslyRunningTimersCountSoFar) {
            this(previouslyRunningTimerObjectId, previouslyRunningTimersCountSoFar, null);
        }

        PreviouslyRunningTimerEntry(String previouslyRunningTimerObjectId, long previouslyRunningTimersCountSoFar, boolean running, boolean savedLocally) {
//            this();
            this.previouslyRunningTimerObjectId = previouslyRunningTimerObjectId;
            this.previouslyRunningTimersCountSoFarMillis = previouslyRunningTimersCountSoFar;
            this.running = running;
            this.savedLocallyInTimer = savedLocally;
        }

//        PreviouslyRunningTimerEntry(String previouslyRunningTimerObjectId, long previouslyRunningTimersCountSoFar, boolean running) {
//            this(previouslyRunningTimerObjectId, previouslyRunningTimersCountSoFar, itemList);
//        }

        PreviouslyRunningTimerEntry(String previouslyRunningTimerObjectId, long previouslyRunningTimersCountSoFar, ItemList previousItemList) {
            this(previouslyRunningTimerObjectId, previouslyRunningTimersCountSoFar, false, false);
//                this.previouslyRunningTimerObjectId = previouslyRunningTimerObjectId;
//                this.previouslyRunningTimersCountSoFarMillis = previouslyRunningTimersCountSoFar;
//                this.previousItemList = previousItemList;
        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public void externalize(DataOutputStream out) throws IOException {
//            out.writeUTF(previouslyRunningTimerObjectId);
            Util.writeObject(previouslyRunningTimerObjectId, out);
            out.writeLong(previouslyRunningTimersCountSoFarMillis);
            out.writeBoolean(running);
            out.writeBoolean(savedLocallyInTimer);
        }

        @Override
        public void internalize(int version, DataInputStream in) throws IOException {
//            previouslyRunningTimerObjectId = in.readUTF();
            previouslyRunningTimerObjectId = (String) Util.readObject(in);
            previouslyRunningTimersCountSoFarMillis = in.readLong();
            running = in.readBoolean();
            savedLocallyInTimer = in.readBoolean();
        }

        @Override
        public String getObjectId() {
//                return (previouslyRunningTimerObjectId == null ? "null" : previouslyRunningTimerObjectId);
//                return (previouslyRunningTimerObjectId);
            return (CLASS_NAME_PREVIOUSLY_RUNNING_TIMERS); //this is the 'type' name of this externalizable *class*, (NOT a unique ID per object)
        }

    }


