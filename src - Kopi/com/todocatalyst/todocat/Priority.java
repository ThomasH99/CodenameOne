/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Edit a priority definition/calculation.
 * Maps a priority value to a definition of importance and priority.
 * Supports several levels of both importance and priority, e.g. H/L, H/M/L, VeryHigh/High/Medium/Low.
 *
 * @author Thomas
 */
public class Priority { //extends BaseItem {

    static int PRIORITY_TYPE_INTEGER = 0;
    static int PRIORITY_TYPE_IMP_URG = 1;
    protected int priorityType = PRIORITY_TYPE_INTEGER;
    protected int priority;

//    Priority(BaseBaseItem owner) {
    Priority(ItemAndListCommonInterface owner) {
//        super(owner);
//        super(owner, false, false);
//        setTypeId(BaseItemTypes.PRIORITY);
//        owner=this;
//        super.setOwner(this);
    }

    Priority() {
//        setTypeId(BaseItemTypes.PRIORITY);
        this((ItemAndListCommonInterface)null);
    }

    Priority(int prio) {
        priority=prio;
    }

    Priority(DataInputStream dis) {
        try {
            priority = dis.readInt();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getPriorityType() {
        return priorityType;
    }

    public void setPriority(int priority) {
        if (this.priority != priority) {
            this.priority = priority;
//            changed();
        }
    }

    public int getPriority() {
        return priority;
    }

    public void writeObject(DataOutputStream dos) throws IOException {
        //dos =
//        super.writeObject(dos);
//        try {
        //WRITE DATA FIELDS - ALWAYS add new data at the end of the list!!
        dos.writeInt(priorityType);
        dos.writeInt(priority);
        //dos.writeLong(nextGuid);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        //return dos;
    }

    public void readObject(int version, DataInputStream dis) throws IOException {
//        super.readObject(999, dis);
//        try {
        //TODO: if (settings.version == xxx)
        priorityType = dis.readInt();
        priority = dis.readInt();
        //nextGuid = dis.readLong();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
        //return dis;
    }
}
