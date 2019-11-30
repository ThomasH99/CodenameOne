/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

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
public class PriorityImpUrgencyPair extends Priority {

////    static String HIGH = "High";
////    static String MED = "Medium";
////    static String LOW = "Low";
//    final static String HIGH_STRING = "High";
//    final static String MED_STRING = "Med"; //"Medium"
//    final static String LOW_STRING = "Low";
//    final static int LOW = 0;
//    final static int MED = 1;
//    final static int HIGH = 2;;//    static Vector importanceAxis;
//
//    final static String[] importanceAxis= new String[]{LOW_STRING,MED_STRING,HIGH_STRING};
//    final static int[] importanceValues =new int[]{LOW, MED, HIGH};
//    
////    static Vector urgencyAxis; //
////    static String[] urgencyAxis;
//    final static String[] urgencyAxis = importanceAxis; //new String[]{LOW_STRING,MED_STRING,HIGH_STRING};
//    final static int[] urgencyValues =importanceValues; //new int[]{LOW, MED, HIGH};
//    
//    static Vector matrixVector;
////    PriorityPair priorityPair;
////    String importance = LOW;
////    String urgency = LOW;
//    int importance = LOW; //default value
//    int urgency = LOW; //default value
//
////    PriorityImpUrgencyPair(BaseBaseItem owner) {
//    PriorityImpUrgencyPair(ItemAndListCommonInterface owner) {
////        this();
////        super(owner);
////        setOwner(owner);
//        super(owner);
////        setTypeId(BaseItemTypes.PRIORITY_IMP_URG);
//        priorityType = PRIORITY_TYPE_IMP_URG;
//        setup();
//        setPriority(Settings.getInstance().getDefaultPriority());
//    }
//
//    PriorityImpUrgencyPair() {
////        super();
//        this(null);
////        setTypeId(BaseItemTypes.PRIORITY_IMP_URG);
////        priorityType = PRIORITY_TYPE_IMP_URG;
////        setup();
////        setPriority(Settings.getInstance().getDefaultPriority());
//    }
//
//    class PriorityPair {
//
////        String importance;
////        String urgency;
//        int importance;
//        int urgency;
//        int priority;
//
////        PriorityPair(String importance, String urgency, int priority) {
//        PriorityPair(int importance, int urgency, int priority) {
//            this.importance = importance;
//            this.urgency = urgency;
//            this.priority = priority;
//        }
//    }
////    int priority;
////    public void setPriority(int priority) {
////        this.priority = priority;
////    }
////
////    public int getPriority() {
////        return ;
////    }
//
////    PriorityPair[] matrixArray = new PriorityPair[]{
////        new PriorityPair(HIGH, HIGH, 9), new PriorityPair(HIGH, MED, 8), new PriorityPair(HIGH, LOW, 7),
////        new PriorityPair(MED, HIGH, 6), new PriorityPair(MED, MED, 5), new PriorityPair(MED, LOW, 4),
////        new PriorityPair(LOW, HIGH, 3), new PriorityPair(LOW, MED, 2), new PriorityPair(LOW, LOW, 1)};
//
//    public void writeObject(DataOutputStream dos) throws IOException {
//        //dos =
//        super.writeObject(dos);
//        try {
//            //WRITE DATA FIELDS - ALWAYS add new data at the end of the list!!
////            dos.writeUTF(importance);
////            dos.writeUTF(urgency);
//            dos.writeInt(importance);
//            dos.writeInt(urgency);
//            //dos.writeLong(nextGuid);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        //return dos;
//    }
//
//    public void readObject(int version, DataInputStream dis) throws IOException {
//        super.readObject(999, dis);
//        try {
//            //TODO: if (settings.version == xxx)
////            importance = dis.readUTF();
////            urgency = dis.readUTF();
//            importance = dis.readInt();
//            urgency = dis.readInt();
//            //nextGuid = dis.readLong();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        //return dis;
//    }
//
//    private void setup() {
////        matrixVector = ItemList.createVector(matrixArray);
//
//        if (matrixVector == null) {
//            matrixVector = new Vector(9);
//            // Importance, Urgency
////            matrixVector.addElement(new PriorityPair(HIGH, HIGH, 9));
////            matrixVector.addElement(new PriorityPair(HIGH, MED, 8));
////            matrixVector.addElement(new PriorityPair(HIGH, LOW, 5));
////            matrixVector.addElement(new PriorityPair(MED, HIGH, 7));
////            matrixVector.addElement(new PriorityPair(MED, MED, 6));
////            matrixVector.addElement(new PriorityPair(MED, LOW, 4));
////            matrixVector.addElement(new PriorityPair(LOW, HIGH, 3));
////            matrixVector.addElement(new PriorityPair(LOW, MED, 2));
////            matrixVector.addElement(new PriorityPair(LOW, LOW, 1));
//            //NB!! The matrixVector must have the highest value first,
//            //and be sorted from highest value to smaller, otherwise the found priority searches below won't work correctly
////            matrixVector.addElement(new PriorityPair(IMP, URG, 8));
////            matrixVector = new PriorityPair[]{new PriorityPair(LOW, LOW, Settings.getInstance().getDefaultPriority()),};
//            matrixVector.addElement(new PriorityPair(HIGH, HIGH, 8));
//            matrixVector.addElement(new PriorityPair(HIGH, MED, 7));
//            matrixVector.addElement(new PriorityPair(MED, HIGH, 6));
//            matrixVector.addElement(new PriorityPair(MED, MED, 5));
//            matrixVector.addElement(new PriorityPair(HIGH, LOW, 4));
//            matrixVector.addElement(new PriorityPair(MED, LOW, 3));
//            matrixVector.addElement(new PriorityPair(LOW, HIGH, 2));
//            matrixVector.addElement(new PriorityPair(LOW, MED, 1));
//            matrixVector.addElement(new PriorityPair(LOW, LOW, Settings.getInstance().getDefaultPriority())); //Must be 0 or less!!
//        }
//        if (importanceAxis == null) {
////            importanceAxis = new Vector(3);
////            importanceAxis.addElement(LOW);
////            importanceAxis.addElement(MED);
////            importanceAxis.addElement(HIGH);
////            importanceAxis = new String[3];
////            importanceAxis = new String[3];
////            importanceAxis[0]=LOW;
////            importanceAxis[1]=MED;
////            importanceAxis[2]=HIGH;
////            importanceAxis[0]=LOW_STRING;
////            importanceAxis[1]=MED_STRING;
////            importanceAxis[2]=HIGH_STRING;
//        }
//        if (urgencyAxis == null) {
////            urgencyAxis = new Vector(3);
////            urgencyAxis.addElement(LOW);
////            urgencyAxis.addElement(MED);
////            urgencyAxis.addElement(HIGH);
////            urgencyAxis = new String[3];
////            urgencyAxis[0]=LOW_STRING;
////            urgencyAxis[1]=MED_STRING;
////            urgencyAxis[2]=HIGH_STRING;
//        }
//    }
//
//    /**
//     * Returns the priority associated with the pair of (importance, urgency).
//     * Assumes that only valid values of priorities are used, but if no match is found,
//     * returns the minimum priority value defined (or Integer.MIN_VALUE if empty matrixVector).
//     * Ideally, if mapping existing priority values into the matrixVector, and back again, shouldn't change the
//     * priority value.
//     * @param importance
//     * @param urgency
//     * @return
//     */
////    private int findPriority(String importance, String urgency) {
//    private int findPriority(int importance, int urgency) {
//        int minPrioValue = Integer.MAX_VALUE;
//        for (int i = 0, size = matrixVector.size(); i < size; i++) {
//            PriorityPair pair = (PriorityPair) matrixVector.elementAt(i);
//            //set minPrioValue to the smallest priority value defined in the matrixVector. We will iterate through all values if importance and urgency strings are not found
//            if (pair.priority < minPrioValue) {
//                minPrioValue = pair.priority; //collect the minimum priority value defined in the matrix
//            }
////            if (pair.importance.equals(importance) && pair.urgency.equals(urgency)) {
//            if (pair.importance ==importance && pair.urgency ==urgency) {
//                return pair.priority;
//            }
//        }
//        if (minPrioValue == Integer.MAX_VALUE) {
//            minPrioValue = 0; //use 0 if no value found in matrixVector (if matrix empty)
//        }
//        return minPrioValue;
//    }
//
//    /**
//     * keep the old priority value, unless the new defined pair of importance/urgency maps to value in a new range.
//     * E.g. oldPrio==12, prio(H,H)==9, then keep 12.
//     * @param oldPriority
//     * @param importance
//     * @param urgency
//     * @return
//     */
////    private int getPriority(String importance, String urgency) {
//    private int getPriority(int importance, int urgency) {
////    private int getPriority(int oldPriority, PriorityPair priorityPair) {
//        return findPriority(importance, urgency);
//    }
//
////    private int getPriorityKeepOldPriority(int oldPriority, String importance, String urgency) {
////        if (findPriorityPair(findPriority(importance, urgency)) != findPriorityPair(oldPriority) || Settings.getInstance().forcePriorityValuesIntoImpUrgencyMatrixValues()) {
////            return findPriority(importance, urgency);
////        } else {
////            return oldPriority;
////        }
////    }
//
//    /**
//     * returns the pair of importance/urgency best corresponding to priority, that is, the
//     * first pair with a priority value smaller or equal to priority.
//     * If no pair is found, the pair with the smallest value is returned.
//     * Returns null if no pair is found (eg if matrix is empty)
//     * For example, is smallest pair defined has aggregate priority 1, and we search for priority 0, then null is returned.
//     * @param priority
//     * @return
//     */
//    private PriorityPair findPriorityPair(int priority) {
//        PriorityPair foundPair = null;
//        PriorityPair pair = null;
//        for (int i = 0, size = matrixVector.size(); i < size; i++) {
//            pair = (PriorityPair) matrixVector.elementAt(i);
////            if (foundPair == null) {
////                foundPair = pair;
////            }
////            if (priority >= pair.priority) { //select the pair as long as its priority is lower/equalt to the priority searched (ensures we find the pair with the value the closest below the priority)
//            if (foundPair == null && pair.priority <= priority) { //select the first! pair as long as its priority is lower/equalt to the priority searched (ensures we find the pair with the value the closest below the priority)
//                foundPair = pair;
//            }
//        }
//        if (foundPair == null) { //if no pair was found, use the last (smallest) value in the matrixVector
//            foundPair = pair;
//        }
//        return foundPair;
//    }
//
//    public static String[] getImportanceStringArray() {
//        return importanceAxis;
//    }
//    
//    public static String[] getImpUrgStringArray() {
//        return importanceAxis;
//    }
//    
//    public static int[] getImportanceIntArray() {
//        return importanceValues;
//    }
//    
//    public static int[] getImpUrgIntArray() {
//        return importanceValues;
//    }
//    
//    public static int[] getUrgencyIntArray() {
//        return urgencyValues;
//    }
//    
//    public void setImportance(int importance) {
//        this.importance= importance;
//    }
//
//    public void setUrgency(int urgency) {
//        this.urgency= urgency;
//    }
//
//    public int getImportance() {
//        return importance;
//    }
//
//    public int getUrgency() {
//        return urgency;
//    }
//
//    public static String[] getUrgencyStringArray() {
//        return urgencyAxis;
//    }
//
////    public void setPriority(PriorityPair priorityPair) {
////    public void setPriority(String importance, String urgency) {
//    public void setPriority(int importance, int urgency) {
//        if (this.importance != importance || this.urgency != urgency) {
//            this.importance = importance;
//            this.urgency = urgency;
////            changed();
//        }
//    }
//
//    public void setPriority(int priority) {
//        if (priority != getPriority()) {
//            PriorityPair priorityPair = findPriorityPair(priority);
//            this.importance = priorityPair.importance;
//            this.urgency = priorityPair.urgency;
////            changed();
//        }
//    }
//
//    public int getPriority() {
//        return getPriority(importance, urgency);
//    }
}
