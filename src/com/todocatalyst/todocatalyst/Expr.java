/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author Thomas
 */
//public class Expr extends BaseBaseItem {
public class Expr {//implements Externalizable {

////    public final static int VALUE_FIELD_TYPE_STRING = 0x01;
////    public final static int VALUE_FIELD_TYPE_DURATION = 0x02;
////    public final static int VALUE_FIELD_TYPE_DATE = 0x03;
////    public final static int VALUE_FIELD_TYPE_BOOLEAN = 0x04;
////    public final static int EQUAL = 0x10;
////    public final static int CONTAINS = 0x110;
//    public final static int STRING_CONTAINS = 0;//STRING & CONTAINS;
//    public final static int STRING_BEGINSWITH = 1;//STRING & CONTAINS;
//    public final static int STRING_EQUAL = 2; //STRING & EQUAL;
//    public final static int STRING_NEQUAL = 3; //STRING & EQUAL;
//    public final static int STRING_NOTCONTAINS = 4;//STRING & CONTAINS;
////    public final static int STRING_EMPTY = 101;//STRING & CONTAINS;
////    public final static int STRING_NEMPTY = 102;//STRING & CONTAINS;
//    private final static int[] STRING_OP_VALUES = {STRING_CONTAINS, STRING_NOTCONTAINS, STRING_BEGINSWITH, STRING_EQUAL, STRING_NEQUAL};
////    private final static String[] STRING_OP_NAMES = {"Contains", "Does not contain", "Begins with", "Equal to", "Different from"};
//    private final static String[] STRING_OP_NAMES = {"Contains", "DoesNotContain", "BeginsWith", "EqualTo", "DifferentFrom"};
//    public final static int INT_EQUAL = 10; //DURATION & EQUAL;
//    public final static int INT_GT = 11; //DURATION & EQUAL;
//    public final static int INT_GTE = 12; //DURATION & EQUAL;
//    public final static int INT_LT = 13; //DURATION & EQUAL;
//    public final static int INT_LTE = 14; //DURATION & EQUAL;
//    public final static int INT_NEQUAL = 15; //DURATION & EQUAL;
//    private final static int[] INT_OP_VALUES = {INT_EQUAL, INT_GT, INT_GTE, INT_LT, INT_LTE, INT_NEQUAL};
////    private final static String[] INT_OP_NAMES = {"Equal to", "Greater than", "Greater than or equal", "Less than", "Less than or equal", "Different from"};
//    private final static String[] INT_OP_NAMES = {"EqualTo", "GreaterThan", "GreaterThanOrEqual", "LessThan", "LessThanOrEqual", "DifferentFrom"};
//    public final static int LONG_EQUAL = 20; //DURATION & EQUAL;
//    public final static int LONG_GT = 21; //DURATION & EQUAL;
//    public final static int LONG_GTE = 22; //DURATION & EQUAL;
//    public final static int LONG_LT = 23; //DURATION & EQUAL;
//    public final static int LONG_LTE = 24; //DURATION & EQUAL;
//    public final static int LONG_NEQUAL = 25; //DURATION & EQUAL;
//    public final static int LONG_DAYS_BEFORE_TODAY = 26; //DURATION & EQUAL;
//    public final static int LONG_DAYS_AFTER_TODAY = 27; //DURATION & EQUAL;
//    private final static int[] LONG_OP_VALUES = {LONG_EQUAL, LONG_GT, LONG_GTE, LONG_LT, LONG_LTE, LONG_NEQUAL, LONG_DAYS_BEFORE_TODAY, LONG_DAYS_AFTER_TODAY};
////    private final static String[] LONG_OP_NAMES = {"Equal to", "After", "After or on", "Before", "Before or on", "Different from", "Days before today", "Days after today"};
//    private final static String[] LONG_OP_NAMES = {"EqualTo", "After", "AfterOrOn", "Before", "BeforeOrOn", "DifferentFrom", "DaysBeforeToday", "DaysAfterToday"};
////    private final static int[] DOUBLE_OP_VALUES = {DOUBLE_EQUAL, DOUBLE_GT, DOUBLE_GTE, DOUBLE_LT, DOUBLE_LTE, DOUBLE_NEQUAL, DOUBLE_DAYS_BEFORE_TODAY, DOUBLE_DAYS_AFTER_TODAY};
////    private final static String[] DOUBLE_OP_NAMES = {"Equal to", "After", "After or on", "Before", "Before or on", "Different from", "Days before today", "Days after today"};
//    public final static int BOOLEAN_EQUAL = 30; //DURATION & EQUAL;
//    public final static int BOOLEAN_NEQUAL = 31; //DURATION & EQUAL;
//    public final static int[] BOOLEAN_OP_VALUES = {BOOLEAN_EQUAL, BOOLEAN_NEQUAL};
////    private final static String[] BOOLEAN_OP_NAMES = {"Equal to", "Different from"};
//    private final static String[] BOOLEAN_OP_NAMES = {"EqualTo", "DifferentFrom"};
//    private final static String[] BOOLEAN_NAMES = {"False", "True"};
//    public final static int ENUM_IN_ENUM_VALUES_VECTOR = 40;
////    public final static int ENUM_NEQUAL = 41; //DURATION & EQUAL; //- only support selecting the enum values that are EQUAL to a list of values
//    public final static int[] ENUM_OP_VALUES = {ENUM_IN_ENUM_VALUES_VECTOR};//, ENUM_NEQUAL};
////    private final static String[] ENUM_OP_NAMES = {"Equals"}; //, "Different from"};
//    private final static String[] ENUM_OP_NAMES = {"IsOneOf"}; //, "Different from"};
//    public static final int VALUE_FIELD_TYPE_STRING = 0; //index used in tables below to get list of names for type VALUE_FIELD_TYPE_STRING
//    public static final int VALUE_FIELD_TYPE_DURATION = 1;
//    public static final int VALUE_FIELD_TYPE_DATE = 2;
//    public static final int VALUE_FIELD_TYPE_BOOLEAN = 3;
//    public static final int VALUE_FIELD_TYPE_ENUM = 4;
//    public static final int VALUE_FIELD_TYPE_INTEGER = 5;
//    public static final int VALUE_FIELD_TYPE_DOUBLE = 6;
////    public static final int VALUE_FIELD_TYPE_DATE = 4;
////    public static final int VALUE_FIELD_TYPE_DURATION = 4;
//    private final static int[][] OP_VALUES = {STRING_OP_VALUES, INT_OP_VALUES, LONG_OP_VALUES, BOOLEAN_OP_VALUES, ENUM_OP_VALUES, INT_OP_VALUES};
//    private final static String[][] OP_NAMES = {STRING_OP_NAMES, INT_OP_NAMES, LONG_OP_NAMES, BOOLEAN_OP_NAMES, ENUM_OP_NAMES, INT_OP_NAMES};
//    final static int EXPR_TYPE_UNDEFINED = 1000;
//    final static int EXPR_TYPE_VALUE_DEF = 1001;
//    final static int EXPR_TYPE_AND = 1002;
//    final static int EXPR_TYPE_OR = 1003;
//    public final static int EXPR_TYPE_TRUE = 1004; //DURATION & EQUAL;
//    public final static int EXPR_TYPE_FALSE = 1005; //DURATION & EQUAL;
//
//    /**
//     * reduces an Expr logically (e.g. X AND true = X, or X OR false = X)
//     */
//    static void reduceExpr(Expr expr) {
//        if (expr.exprType == EXPR_TYPE_AND) {
//            if (expr.left.exprType == EXPR_TYPE_TRUE) {
//                expr.setExpr(expr.right);
//            } else if (expr.right.exprType == EXPR_TYPE_TRUE) {
//                expr.setExpr(expr.left);
//            } else if (expr.right.exprType == EXPR_TYPE_FALSE || expr.left.exprType == EXPR_TYPE_FALSE) {
//                expr.exprType = EXPR_TYPE_FALSE;
//            }
//        } else if (expr.exprType == EXPR_TYPE_OR) {
//            if (expr.left.exprType == EXPR_TYPE_FALSE) {
//                expr.setExpr(expr.right);
//            } else if (expr.right.exprType == EXPR_TYPE_FALSE) {
//                expr.setExpr(expr.left);
//            } else if (expr.right.exprType == EXPR_TYPE_TRUE || expr.left.exprType == EXPR_TYPE_TRUE) {
//                expr.exprType = EXPR_TYPE_TRUE;
//            }
//        }
//    }
//
//    /**
//     * simplifies an Expr by removing a true expr from left or right, e.g. X AND true = X, X OR true = X
//     */
//    static void simplifyExpr(Expr expr) {
//        if (expr.exprType == EXPR_TYPE_AND || expr.exprType == EXPR_TYPE_AND) {
//            if (expr.left.exprType == EXPR_TYPE_TRUE) {
//                expr.setExpr(expr.right);
//            } else if (expr.right.exprType == EXPR_TYPE_TRUE) {
//                expr.setExpr(expr.left);
//            }
//        }
//    }
//    /**
//     * determines whether Expr is of type AND, OR, or EXPR
//     */
//    int exprType = EXPR_TYPE_UNDEFINED;
//    /**
//     * what BaseItem type is this expression applied to? E.g. Item, Category,
//     * ... NOT currently used, for the moment Expr can only be applied to Item
//     * (hard-coded in several places)
//     */
////    int baseItemType = BaseItemTypes.BASEITEM;
//    /**
//     * itemFieldId used to get right value from the BaseItem in case
//     */
//    int itemFieldId; //only used for exprType==EXPR_TYPE_UNDEFINED
//    /**
//     * unique ID for the specific operator (also encoded the specific type of
//     * the field, e.g. LONG_EQUAL implies the type is Long, or Time)
//     */
//    int operatorId;
//    /**
//     * the type of the value, VALUE_FIELD_TYPE_DATE, LONG,
//     * VALUE_FIELD_TYPE_ENUM, ...
//     */
////    int valueType;
//    /**
//     * the 'reference' value for the expression. Either a constant entered by
//     * the user, or a reference to another field with which to compare
//     */
//    Object value;
//    /**
//     * left side of an AND/OR expression, e.g. "left AND right"
//     */
//    Expr left; //only used for exprType==EXPR_TYPE_AND/OR_EXPR_TYPE,
//    /**
//     * right side of an AND/OR expression, e.g. "left AND right"
//     */
//    Expr right; //only used for exprType==EXPR_TYPE_AND/OR_EXPR_TYPE
////    String strValue;
////    int intValue;
////    long longValue;
////    boolean booleanValue;
//
//    /**
//     * creates an AND/OR expression, use with exprType==Expr.EXPR_TYPE_AND/OR
//     */
//    Expr(int exprType, Expr left, Expr rigth) {
//        this.exprType = exprType;
//        this.left = left;
//        this.right = rigth;
//    }
//
////    Expr() {
////        this.exprType = EXPR_TYPE_UNDEFINED; //TRUE; //an expression is initially true
////    }
//    Expr() {
////        this.exprType = EXPR_TYPE_TRUE; //TRUE; //an expression is initially true
//    }
//
//    Expr(boolean value) {
//        this.exprType = value ? EXPR_TYPE_TRUE : EXPR_TYPE_FALSE; //TRUE; //an expression is initially true
//    }
//
//    /**
//     * create a new Expr. value can also be int[] for enum values, which is then
//     * converted to Vector(Integer)
//     *
//     * @param fieldId
//     * @param operatorId
//     * @param value eg Integer, Boolean
//     */
//    Expr(int fieldId, int operatorId, Object value) {
//        this.exprType = EXPR_TYPE_VALUE_DEF;
//        this.itemFieldId = fieldId;
//        this.operatorId = operatorId;
////        this.valueType = Item.getFieldType(fieldId); //use fieldId type as default value
//        if (value instanceof int[]) {
//            int[] arr = (int[]) value;
//            Vector vect = new Vector(((int[]) value).length);
//            for (int i = 0, size = arr.length; i < size; i++) {
//                vect.addElement(new Integer(arr[i]));
//            }
//            this.value = vect;
//        } else {
//            this.value = value;
//        }
////        this.strValue = strValue;
//    }
//
//    Expr(DataInputStream dis) {
////        readObject(Settings.getInstance().getVersion(), dis);
//    }
//
//    Expr(Expr f) {
//        setFilterExpr(f);
//    }
//
//    public void copyMeInto(Expr expr) {
//
////        super.copyMeInto(destiny);
////        expr.setFilterExpr(expr.clone());
//
//        expr.exprType = exprType;
//        if (exprType == EXPR_TYPE_AND || exprType == EXPR_TYPE_OR) {
//            expr.left = left.cloneMe();
//            expr.right = right.cloneMe();
//        } else {
//            expr.itemFieldId = itemFieldId;
//            expr.operatorId = operatorId;
////        this.valueType = f.valueType;
//            expr.value = value; //TODO!!!!: will this work or is a real copy of value needed??!!
//        }
//    }
//
//    /**
//     * sets Expr to the value of newExpr
//     */
//    public void setExpr(Expr newExpr) {
//        exprType = newExpr.exprType;
//        itemFieldId = newExpr.itemFieldId;
//        operatorId = newExpr.operatorId;
//        value = newExpr.value;
//        left = newExpr.left;
//        right = newExpr.right;
//    }
//
//    public Expr cloneMe() {
//        Expr newCopy = new Expr();
//        copyMeInto(newCopy);
//        return newCopy;
//    }
//
//    public boolean equals(Object o) {
//        if (o == null) {
//            return false;
//        }
//        if (o == this) {
//            return true;
//        }
//        if (this.getClass() != o.getClass()) {
//            return false;
//        }
//        Expr expr2 = (Expr) o;
//        if (exprType == EXPR_TYPE_AND || exprType == EXPR_TYPE_AND) {
//            return this.exprType == expr2.exprType
//                    && left.equals(expr2.left)
//                    && right.equals(expr2.right);
//        } else {
//            if (this.itemFieldId != expr2.itemFieldId) {
//                return false;
//            }
//            if (this.operatorId != expr2.operatorId) {
//                return false;
//            }
////                    if(this.value !=null && expr2.value!=null && !this.value.equals(expr2.value)) return false;
//            if ((this.value == null && expr2.value != null) || (this.value != null && !this.value.equals(expr2.value))) {
//                return false;
//            }
//            return true;
//            //<editor-fold defaultstate="collapsed" desc="comment">
//            //            return //                    this.fieldTypeId == expr2.fieldTypeId &&
//            //                    this.itemFieldId == expr2.itemFieldId
//            //                    && this.operatorId == expr2.operatorId
//            //                    //                    && this.valueType == expr2.valueType
//            //                    && (this.value ==expr2.value
//            //                    || this.value.equals(expr2.value));
//            //</editor-fold>
//        }
//    }
//
//    //TODO: move read/writeObject into a separate Interface to avoid always having to save them as BaseItems
//    public void externalize(DataOutputStream dos) {
//        try {
//            if (exprType == EXPR_TYPE_TRUE) {
//                dos.writeInt(exprType);
//                return;
//            }
//            dos.writeInt(exprType);
//            if (exprType == EXPR_TYPE_AND || exprType == EXPR_TYPE_OR) {
//                left.externalize(dos);
//                right.externalize(dos);
//            } else { //exprType == 
////                dos.writeByte(fieldTypeId);
//                dos.writeByte(itemFieldId);
//                dos.writeByte(operatorId);
////                dos.writeInt(Item.getFieldType(itemFieldId));
//                int fieldType = Item.getFieldType(itemFieldId);
//                dos.writeInt(fieldType); //writes eg Expr.VALUE_FIELD_TYPE_STRING, Expr.VALUE_FIELD_TYPE_BOOLEAN, Expr.VALUE_FIELD_TYPE_DATE
//
////                if (value instanceof FieldIdRef) {
////                    dos.writeBoolean(true); //true means that value is a FieldIdRef
//////                    dos.write(((FieldIdRef) value).compareFieldId);
////                    dos.writeInt(((FieldIdRef) value).compareFieldId);
////                } else 
//                {
//                    dos.writeBoolean(false);
////                switch (Item.getFieldType(itemFieldId)) {
////                    switch (Item.getFieldType(itemFieldId)) {
//                    switch (fieldType) {
//                        case Expr.VALUE_FIELD_TYPE_STRING:
//                            dos.writeUTF((String) value);
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_INTEGER:
//                            dos.writeInt(((Integer) value).intValue());
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_DURATION:
//                            dos.writeLong(((Long) value).longValue());
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_ENUM:
//                            Vector list = (Vector) value;
//                            dos.writeInt(list.size());
//                            for (int i = 0, size = list.size(); i < size; i++) {
//                                dos.writeInt(((Integer) list.elementAt(i)).intValue());
//                            }
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_DATE:
//                            dos.writeLong(((Long) value).longValue());
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_BOOLEAN:
//                            dos.writeBoolean(((Boolean) value).booleanValue());
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_DOUBLE:
//                            dos.writeDouble(((Double) value).doubleValue());
//                            break;
////#mdebug
//                        default:
//                            ASSERT.that("Error: unknown Expr.type");
////#enddebug
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void internalize(int version, DataInputStream dis) {
//        try {
//            exprType = dis.readInt();
//            if (exprType == EXPR_TYPE_TRUE) {
//                return; //no need to return anything since EXPR_TYPE_TRUE is the default value for new created Expr
//            }
//            if (exprType == EXPR_TYPE_AND || exprType == EXPR_TYPE_OR) {
//                left = new Expr(dis);
//                right = new Expr(dis);
//            } else {
////                fieldTypeId = dis.readByte();
//                itemFieldId = dis.readByte();
//                operatorId = dis.readByte();
//                int fieldType = dis.readInt();
//
//                boolean fieldIdRef = dis.readBoolean();
////                if (fieldIdRef) {
////                    value = new FieldIdRef(dis.readInt());
////                } else 
//                {
////                switch (Item.getFieldType(itemFieldId)) { //fieldTypeId) {
//                    switch (fieldType) { //fieldTypeId) {
//                        case Expr.VALUE_FIELD_TYPE_STRING:
//                            value = dis.readUTF();
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_INTEGER:
//                            value = new Integer(dis.readInt());
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_DURATION:
//                            value = new Long(dis.readLong());
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_ENUM:
//                            int listSize = dis.readInt();
//                            Vector list = new Vector(listSize);
//                            for (int i = 0, size = listSize; i < size; i++) {
//                                list.addElement(new Integer(dis.readInt()));
//                            }
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_DATE:
//                            value = new Long(dis.readLong());
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_BOOLEAN:
//                            value = new Boolean(dis.readBoolean());
//                            break;
//                        case Expr.VALUE_FIELD_TYPE_DOUBLE:
//                            value = new Double(dis.readDouble());
//                            break;
////#mdebug
//                        default:
//                            ASSERT.that("Error: unknown Expr.type");
////#enddebug
//                    }
//                }
//            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public static Object createObjectOfType(int typeId) {
//        switch (typeId) {
//            case VALUE_FIELD_TYPE_STRING:
//                return new String();
//            case VALUE_FIELD_TYPE_DURATION:
//                return new Duration(0);
//            case VALUE_FIELD_TYPE_DATE:
//                return new MyDate();
//            case VALUE_FIELD_TYPE_BOOLEAN:
//                return new Boolean(true);
//            case VALUE_FIELD_TYPE_ENUM:
//                return new Integer(0);
//            case VALUE_FIELD_TYPE_INTEGER:
//                return new Integer(0);
//            case VALUE_FIELD_TYPE_DOUBLE:
//                return new Double(0);
//            default:
//                ASSERT.that("should never happen");
//                return null;
//        }
//    }
//
//    public String toString() {
////        String str = "";
//        switch (exprType) {
//            case EXPR_TYPE_AND:
////                return "AND(" + left + "; " + right + ")";
////                return MessageFormat("("+left + ") AND (" + right + ")";
//                return "(" + left + ") AND (" + right + ")";
//            case EXPR_TYPE_OR:
////                return "OR(" + left + "; " + right + ")";
//                return "(" + left + ") OR (" + right + ")";
//            case EXPR_TYPE_TRUE:
////                return "<undefined>";
//                return "<define>";
//            case EXPR_TYPE_FALSE:
////                return "<undefined>";
//                return "FALSE";
//            case EXPR_TYPE_UNDEFINED:
//                return "UNDEFINED";
//            case EXPR_TYPE_VALUE_DEF:
////                return Item.getFieldName(itemFieldId) + " " + Expr.getOperatorString(Item.getFieldType(itemFieldId), operatorId) + " " + value;
//                String str;
//                str = Item.getFieldName(itemFieldId) + " " + Expr.getOperatorString(Item.getFieldType(itemFieldId), operatorId) + " ";
//                if (value instanceof FieldDef) {
//                    str += Item.getFieldName(((FieldDef) value).id);
//                } else {
//                    if (Item.getFieldType(itemFieldId) == Expr.VALUE_FIELD_TYPE_ENUM) {
//                        Vector statusVector = (Vector) value;
//                        str += "(";
//                        String sep = "";
//                        for (int i = 0, size = statusVector.size(); i < size; i++) {
//                            str += sep;
////                            str += Item.getStatusName(((Integer) statusVector.elementAt(i)).intValue());
//                            sep = ", ";
//                        }
//                        str += ")";
//                    }
//                    if (value instanceof String) {
//                        str += "\"" + value + "\"";
//                    } else {
//                        str += value;
//                    }
//                }
////                    if (value instanceof Item) {
////                        str+= Item.getFieldName(itemFieldId) + " " + Expr.getOperatorString(Item.getFieldType(itemFieldId), operatorId) + " " + value;
////                    } else if (value instanceof ItemListFlatten) {
////                        return ItemListFlatten.getFieldName(itemFieldId) + " " + Expr.getOperatorString(ItemListFlatten.getFieldType(itemFieldId), operatorId) + " " + value;
////                    } else if (value instanceof Category) {
////                        return Category.getFieldName(itemFieldId) + " " + Expr.getOperatorString(Category.getFieldType(itemFieldId), operatorId) + " " + value;
////                    } else {
////                        return "<unknown value type in Expr: " + value.getClass() + ">";
////                    }
////                } else {
////                    return value.toString();
////                }
//                return str;
//            default:
////                return "fieldId=" + Item.getFieldName(itemFieldId) + " operator=" + Expr.getOperatorString(Item.getFieldType(itemFieldId), operatorId) + " value=" + value;
////                return "<unexpected Expr: " + this + ">";//Item.getFieldName(itemFieldId) + " " + Expr.getOperatorString(Item.getFieldType(itemFieldId), operatorId) + " " + value;
//                return "<unexpected Expr>";//Item.getFieldName(itemFieldId) + " " + Expr.getOperatorString(Item.getFieldType(itemFieldId), operatorId) + " " + value;
//        }
////        ASSERT.that("unknown exprType="+exprType+" in Expr.toString");
//    }
//
//    /**
//     * returns the list of operatorId strings for the given type
//     * (Expr.VALUE_FIELD_TYPE_STRING etc). The returned array determines the
//     * order in which the operators are listed in the drop down menus
//     *
//     * @param typeIdx
//     * @return
//     */
//    static String[] getOperatorNames(int typeId) {
//        return OP_NAMES[typeId];
//    }
//
//    static int[] getOperatorIds(int typeId) {
//        return OP_VALUES[typeId];
//    }
//
//    /**
//     * returns the boolean values to display in dropdown box - to make
//     * internationalization easier to encapsulate
//     *
//     * @return
//     */
//    static String[] getBooleanNames() {
//        return BOOLEAN_NAMES;
//    }
//
//    /**
//     * used to translate from
//     *
//     * @param fieldTypeId Expr.VALUE_FIELD_TYPE_STRING,
//     * Expr.VALUE_FIELD_TYPE_DURATION, ...
//     * @param operatorIdx
//     * @return
//     */
//    static int getOperatorId(int typeId, int operatorIdx) {
//        return OP_VALUES[typeId][operatorIdx];
//    }
//
//    static String getOperatorStringFromOpIdx(int typeId, int operatorIdx) {
//        return OP_NAMES[typeId][operatorIdx];
//    }
//
//    static String getOperatorString(int typeId, int operatorId) {
//        return OP_NAMES[typeId][getOperatorIdx(typeId, operatorId)];
//    }
//
//    static int getOperatorIdx(int typeId, int operatorId) {
//        int[] opArr = OP_VALUES[typeId];
////        int size = opArr.length;
//        for (int i = 0, size = opArr.length; i < size; i++) {
//            if (opArr[i] == operatorId) {
//                return i;
//            }
//        }
//        ASSERT.that("Operator typeId=" + typeId + " operatorId=" + operatorId + " not found");
//        return -1;
//    }
//
//    void setFilterExpr(Expr f) {
//        this.itemFieldId = f.itemFieldId;
//        this.operatorId = f.operatorId;
////        this.valueType = f.valueType;
//        this.value = f.value;
////        this.strValue = f.strValue;
////        this.intValue = f.intValue;
////        this.longValue = f.longValue;
////        this.booleanValue = f.booleanValue;
//    }
//
////    class Filter { //Expr extends Filter {
//    /**
//     * switch on data itemFieldId, operatorId, and type of itemFieldId
//     */
////    private boolean evaluateExpr(FilterableObject v, Filter f) {
//    private boolean evaluateExpr(Item v) { //FilterableObject v) {
//        if (exprType == EXPR_TYPE_TRUE) {
//            return true;
//        }
//
//        Object field = v.getFilterField(itemFieldId);
////        int intValue;
////        boolean boolValue;
////        long longValue;
////        int fieldIdValue;
//        Object value2;
//
//        //if value is a reference to another fieldId, then get the value for that one and use it when evaluating the expression
////        if (value instanceof FieldIdRef) {
////            value2 = v.getFilterField(((FieldIdRef) value).compareFieldId);
////        } else 
//        { //otherwise use the value entered
//            value2 = value;
//        }
//
////        switch (valueType) {
//        switch (Item.getFieldType(itemFieldId)) {
//            case Expr.VALUE_FIELD_TYPE_STRING:
//                switch (operatorId) { //optimization: reorder switch to put most frequently used operators left
//                    case Expr.STRING_EQUAL:
//                        return field.equals(value2);
//                    case Expr.STRING_NEQUAL:
//                        return !field.equals(value2);
//                    case Expr.STRING_CONTAINS:
//                        return ((String) field).indexOf((String) value2) > -1;
//                    case Expr.STRING_NOTCONTAINS:
//                        return ((String) field).indexOf((String) value2) == -1;
//                    case Expr.STRING_BEGINSWITH:
//                        return ((String) field).indexOf((String) value2) == 0;
////            case Expr.STRING_EMPTY:
////                return ((String) field).length() == 0;
////            case Expr.STRING_NEMPTY:
////                return ((String) field).length() != 0;
//                }
//                break;
//
//            case Expr.VALUE_FIELD_TYPE_INTEGER:
////            case Expr.VALUE_FIELD_TYPE_DURATION:
//                switch (operatorId) { //optimization: reorder switch to put most frequently used operators left
//                    case Expr.INT_EQUAL:
////                        return ((Integer) field).compareTo((Integer) value2)==0;
//                        return ((Integer) field).intValue() == ((Integer) value2).intValue();
//                    case Expr.INT_GT:
//                        return ((Integer) field).intValue() > ((Integer) value2).intValue();
//                    case Expr.INT_GTE:
//                        return ((Integer) field).intValue() >= ((Integer) value2).intValue();
//                    case Expr.INT_LT:
//                        return ((Integer) field).intValue() < ((Integer) value2).intValue();
//                    case Expr.INT_LTE:
//                        return ((Integer) field).intValue() <= ((Integer) value2).intValue();
//                    case Expr.INT_NEQUAL:
//                        return ((Integer) field).intValue() != ((Integer) value2).intValue();
//                }
//                break;
//
//            case Expr.VALUE_FIELD_TYPE_DATE:
//            case Expr.VALUE_FIELD_TYPE_DURATION:
//                switch (operatorId) {
//                    case Expr.LONG_EQUAL:
//                        return ((Long) field).longValue() == ((Long) value2).longValue();
//                    case Expr.LONG_GT:
//                        return ((Long) field).longValue() > ((Long) value2).longValue();
//                    case Expr.LONG_GTE:
//                        return ((Long) field).longValue() >= ((Long) value2).longValue();
//                    case Expr.LONG_LT:
//                        return ((Long) field).longValue() < ((Long) value2).longValue();
//                    case Expr.LONG_LTE:
//                        return ((Long) field).longValue() <= ((Long) value2).longValue();
//                    case Expr.LONG_NEQUAL:
//                        return ((Long) field).longValue() != ((Long) value2).longValue();
//                    case Expr.LONG_DAYS_BEFORE_TODAY:
//                        //with long / 64bit signed,  max date is Sunday, December 4, 292,277,026,596, http://en.wikipedia.org/wiki/Year_2038_problem
////                return ((Math.min(((Long) field).longValue(), 365*100)*MyDate.DAY_IN_MILISECONDS)+new MyDate().getRepeatStartTime()) <= ((Long) value).longValue(); //UI: days correponding to max 20years can be entered
////                MyDate date = new MyDate(((Long) value).longValue()); //the filtered date
////                MyDate today = new MyDate(); //Today
//////                date.addDays((int)((Long) value).longValue());
////                MyDate date = new MyDate(((Long) value).longValue()); //optimization: avoid creating a variable, use this directly in expression below
////                return today.differenceInDays(date) <= ((Long) value).longValue();
////                return (((Long) value).longValue() <= (Math.min(((Long) field).longValue(), 365*100)*MyDate.DAY_IN_MILISECONDS)+new MyDate().getRepeatStartTime()); //UI: days correponding to max 20years can be entered
//                        return new MyDate().differenceInDays(new MyDate(((Long) value2).longValue())) <= ((Long) value2).longValue();
//                    case Expr.LONG_DAYS_AFTER_TODAY:
////                MyDate today = new MyDate(); //Today
//////                date.addDays((int)((Long) value).longValue());
////                MyDate date = new MyDate(((Long) value).longValue()); //optimization: avoid creating a variable, use this directly in expression below
////                return today.differenceInDays(date) <= ((Long) value).longValue();
////                return ((Math.min(((Long) field).longValue(), 365*100)*MyDate.DAY_IN_MILISECONDS)+System.currentTimeMillis()) >= ((Long) value).longValue(); //UI: days correponding to max 20years can be entered
////                return ((Long) field).longValue()*MyDate.DAY_IN_MILISECONDS >= ((Long) value).longValue();
//                        return new MyDate().differenceInDays(new MyDate(((Long) value2).longValue())) >= ((Long) value2).longValue();
//                }
//                break;
//
//            case Expr.VALUE_FIELD_TYPE_BOOLEAN:
//                switch (operatorId) { //optimization: reorder switch to put most frequently used operators left
//
//                    case Expr.BOOLEAN_NEQUAL:
//                        return ((Boolean) field).booleanValue() != ((Boolean) value2).booleanValue();
//                    case Expr.BOOLEAN_EQUAL:
//                        return ((Boolean) field).booleanValue() == ((Boolean) value2).booleanValue();
////            case Expr.EXPR_TYPE_TRUE:
////                return true;
//                }
//                break;
//
//            case VALUE_FIELD_TYPE_ENUM:
//                switch (operatorId) { //optimization: reorder switch to put most frequently used operators left
//                    //            case Expr.ENUM_NEQUAL:
////                return ((Integer) field).intValue() != ((Integer) value).intValue();
//                    case Expr.ENUM_IN_ENUM_VALUES_VECTOR:
////                return ((Integer) field).intValue() == ((Integer) value).intValue();
//                        Vector list = (Vector) value2; //Vector of Integer
//                        for (int i = 0, size = list.size(); i < size; i++) {
////                            if (list.elementAt(i).equals(value2)) { //value instanceof Integer
//                            if (((Integer) list.elementAt(i)).intValue() == ((Integer) value2).intValue()) { //value instanceof Integer
//                                return true;
//                            }
//                        }
//                        return false;
//                }
//                break;
//            case Expr.VALUE_FIELD_TYPE_DOUBLE:
//            /* NOT needed for the moment since only earnedPoints are a real/double */
//////            case Expr.VALUE_FIELD_TYPE_DURATION:
////                switch (operatorId) { //optimization: reorder switch to put most frequently used operators left
////                    case Expr.INT_EQUAL:
////                        return ((Integer) field).intValue() == ((Integer) value2).intValue();
////                    case Expr.INT_GT:
////                        return ((Integer) field).intValue() > ((Integer) value2).intValue();
////                    case Expr.INT_GTE:
////                        return ((Integer) field).intValue() >= ((Integer) value2).intValue();
////                    case Expr.INT_LT:
////                        return ((Integer) field).intValue() < ((Integer) value2).intValue();
////                    case Expr.INT_LTE:
////                        return ((Integer) field).intValue() <= ((Integer) value2).intValue();
////                    case Expr.INT_NEQUAL:
////                        return ((Integer) field).intValue() != ((Integer) value2).intValue();
////                }
////                break;
//
//
//            default:
//                try {
//                    throw new Exception("Undefined operator " + operatorId);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//        }
//        return true;
//    }
//
//    private boolean xevaluateExpr(Item v) { //FilterableObject v) {
//        Object field = v.getFilterField(itemFieldId);
////        int intValue;
////        boolean boolValue;
////        long longValue;
////        int fieldIdValue;
//        Object value2;
//
//        if (exprType == EXPR_TYPE_TRUE) {
//            return true;
//        }
//
////        if (value instanceof FieldIdRef) {
////            value2 = v.getFilterField(((FieldIdRef) value).compareFieldId);
////        } else 
//        {
//            value2 = value;
//        }
//
//        switch (operatorId) { //optimization: reorder switch to put most frequently used operators left
//            case Expr.STRING_EQUAL:
//                return field.equals(value2);
//            case Expr.STRING_NEQUAL:
//                return !field.equals(value2);
//            case Expr.STRING_CONTAINS:
//                return ((String) field).indexOf((String) value2) > -1;
//            case Expr.STRING_NOTCONTAINS:
//                return ((String) field).indexOf((String) value2) == -1;
//            case Expr.STRING_BEGINSWITH:
//                return ((String) field).indexOf((String) value2) == 0;
////            case Expr.STRING_EMPTY:
////                return ((String) field).length() == 0;
////            case Expr.STRING_NEMPTY:
////                return ((String) field).length() != 0;
//
//            case Expr.INT_EQUAL:
//                return ((Integer) field).intValue() == ((Integer) value2).intValue();
//            case Expr.INT_GT:
//                return ((Integer) field).intValue() > ((Integer) value2).intValue();
//            case Expr.INT_GTE:
//                return ((Integer) field).intValue() >= ((Integer) value2).intValue();
//            case Expr.INT_LT:
//                return ((Integer) field).intValue() < ((Integer) value2).intValue();
//            case Expr.INT_LTE:
//                return ((Integer) field).intValue() <= ((Integer) value2).intValue();
//            case Expr.INT_NEQUAL:
//                return ((Integer) field).intValue() != ((Integer) value2).intValue();
//
//            case Expr.LONG_EQUAL:
//                return ((Long) field).longValue() == ((Long) value2).longValue();
//            case Expr.LONG_GT:
//                return ((Long) field).longValue() > ((Long) value2).longValue();
//            case Expr.LONG_GTE:
//                return ((Long) field).longValue() >= ((Long) value2).longValue();
//            case Expr.LONG_LT:
//                return ((Long) field).longValue() < ((Long) value2).longValue();
//            case Expr.LONG_LTE:
//                return ((Long) field).longValue() <= ((Long) value2).longValue();
//            case Expr.LONG_NEQUAL:
//                return ((Long) field).longValue() != ((Long) value2).longValue();
//            case Expr.LONG_DAYS_BEFORE_TODAY:
//                //with long / 64bit signed,  max date is Sunday, December 4, 292,277,026,596, http://en.wikipedia.org/wiki/Year_2038_problem
////                return ((Math.min(((Long) field).longValue(), 365*100)*MyDate.DAY_IN_MILISECONDS)+new MyDate().getRepeatStartTime()) <= ((Long) value).longValue(); //UI: days correponding to max 20years can be entered
////                MyDate date = new MyDate(((Long) value).longValue()); //the filtered date
////                MyDate today = new MyDate(); //Today
//////                date.addDays((int)((Long) value).longValue());
////                MyDate date = new MyDate(((Long) value).longValue()); //optimization: avoid creating a variable, use this directly in expression below
////                return today.differenceInDays(date) <= ((Long) value).longValue();
//                return new MyDate().differenceInDays(new MyDate(((Long) value2).longValue())) <= ((Long) value2).longValue();
////                return (((Long) value).longValue() <= (Math.min(((Long) field).longValue(), 365*100)*MyDate.DAY_IN_MILISECONDS)+new MyDate().getRepeatStartTime()); //UI: days correponding to max 20years can be entered
//            case Expr.LONG_DAYS_AFTER_TODAY:
////                MyDate today = new MyDate(); //Today
//////                date.addDays((int)((Long) value).longValue());
////                MyDate date = new MyDate(((Long) value).longValue()); //optimization: avoid creating a variable, use this directly in expression below
////                return today.differenceInDays(date) <= ((Long) value).longValue();
//                return new MyDate().differenceInDays(new MyDate(((Long) value2).longValue())) >= ((Long) value2).longValue();
////                return ((Math.min(((Long) field).longValue(), 365*100)*MyDate.DAY_IN_MILISECONDS)+System.currentTimeMillis()) >= ((Long) value).longValue(); //UI: days correponding to max 20years can be entered
////                return ((Long) field).longValue()*MyDate.DAY_IN_MILISECONDS >= ((Long) value).longValue();
//
//            case Expr.BOOLEAN_NEQUAL:
//                return ((Boolean) field).booleanValue() != ((Boolean) value2).booleanValue();
//            case Expr.BOOLEAN_EQUAL:
//                return ((Boolean) field).booleanValue() == ((Boolean) value2).booleanValue();
////            case Expr.EXPR_TYPE_TRUE:
////                return true;
//
////            case Expr.ENUM_NEQUAL:
////                return ((Integer) field).intValue() != ((Integer) value).intValue();
//            case Expr.ENUM_IN_ENUM_VALUES_VECTOR:
////                return ((Integer) field).intValue() == ((Integer) value).intValue();
//                Vector list = (Vector) value2; //Vector of Integer
//                for (int i = 0, size = list.size(); i < size; i++) {
//                    if (list.elementAt(i).equals(value2)) { //value instanceof Integer
//                        return true;
//                    }
//                }
//                return false;
//            default:
//                try {
//                    throw new Exception("Undefined operator " + operatorId);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//        }
//        return true;
//    }
//
////    public boolean matches(FilterableObject v, Filter f) {
//    /**
//     * evalute whether this expression matches the filterable object v
//     *
//     * @param v
//     * @return true if match
//     */
//    boolean matches(Item v) { //FilterableObject v) {
////        if (f instanceof AndOrExpr) {
////            if (((AndOrExpr) f).and) { //AND
//        if (exprType == EXPR_TYPE_AND) { //AND
////            if (matches(v, ((And) f).left)) {
//            if (left.matches(v)) {
//                return right.matches(v);
//            } else {
//                return false;
//            }
////        } else if (f instanceof Or) { // OR
//        } else if (exprType == EXPR_TYPE_OR) { // OR
//            if (left.matches(v)) {
//                return true;
//            } else {
//                return right.matches(v);
//            }
//        } else { //Filter
//            return evaluateExpr(v); //optimization: replace this call by code directly
//        }
//    }
//
//    public int getVersion() {
//        return 1;
//    }
//
//    public String getObjectId() {
//        return "Expr";
//    }
}
