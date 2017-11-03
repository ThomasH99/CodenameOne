/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

/**
 * used to define meta information about editable fields
 * @author Thomas
 */
    public class FieldDef {

        /** the Item field reference */
        int id;
        int type;
        String name;
        String hintOrEmptyFieldText; //TODO!!!! define helt/description per field
        String help; //TODO!!!! define helt/description per field

        FieldDef(int id, String name, int type, String help, String hintOrEmptyFieldText) {
            this.id = id;
            this.type = type;
            this.name = name;
            this.hintOrEmptyFieldText = hintOrEmptyFieldText;
            this.help = help;
        }

        FieldDef(int id, String name, int type, String help) {
            this(id, name, type, help, null);
        }

        FieldDef(int id, String name, int type) {
            this(id, name, type, null);
        }
    }

