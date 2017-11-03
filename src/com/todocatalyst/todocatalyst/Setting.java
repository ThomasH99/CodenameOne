/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Vector;

/**
 * encapsulates a settings
 *
 * @author Thomas
 */
class Setting {

    boolean listOfSettings;
    int id; //unique ID of setting
    int type; //Integer, String, ...
    String title;
    String helpText;
    Object value;
    Object defaultValue;
    Object min;
    Object max;
    String[] names; //logical names for the values
    Object[] values; //accepted values
    Settings settings;

    Setting() {
    }

    Setting(String title, int defaultIntValue, int min, int max, String helpText, Vector settingsList) {
        this();
        this.title = title;
        value = new Integer(defaultIntValue);
        defaultValue = new Integer(defaultIntValue);
        this.min = new Integer(min);
        this.max = new Integer(max);
        this.helpText = helpText;
        settingsList.add(this);
    }

    Setting(String title, int defaultIntValue, String[] names, int[] values, String helpText) {
        this();
        this.title = title;
        value = new Integer(defaultIntValue);
        defaultValue = new Integer(defaultIntValue);
        this.names = names;
        this.values = new Integer[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = new Integer(values[i]);
        }
        this.helpText = helpText;
    }

    Setting(String title, boolean defaultBooleanValue, String helpText) {
        this(title, defaultBooleanValue, new String[]{"Yes", "No"}, helpText);
    }

    Setting(String title, boolean defaultBooleanValue, String[] names, String helpText) {
        this();
        this.title = title;
        value = new Boolean(defaultBooleanValue);
        defaultValue = new Boolean(defaultBooleanValue);
        this.names = names;
        this.helpText = helpText;
    }

    void changed() {
//        settings.changed();
    }

    public void setValue(Object newValue) {
        if (newValue != null && newValue != value && !newValue.equals(value)) {
            if (newValue instanceof Integer) {
                value = newValue;
            }
            changed();
        }
    }

    void setParent(Settings settings) {
        this.settings = settings;
    }

    public int getInt() {
        return ((Integer) value).intValue();
    }

    public boolean getBoolean() {
        return ((Boolean) value).booleanValue();
    }

    public String getString() {
        return (String) value;
    }

    public MyDate getMyDate() {
        return ((MyDate) value);
    }

    public Duration getDuration() {
        return ((Duration) value);
    }
//        Container getEditContainer() {
//            if (value instanceof Integer) {
//                if (min != null || max != null) {
//                    return new EditField(title, (Integer) value, ((Integer) min).intValue(), ((Integer) max).intValue(), helpText);
//                } else if (names != null) {
//                    return new EditField(title, (Integer) value, names, helpText);
//                } else {
//                    return new EditField(title, (Integer) value, helpText);
//                }
//            } else if (value instanceof Boolean) {
//                return new EditField(title, (Boolean) value, helpText);
//            } else if (value instanceof String) {
//                return new EditField(title, (String) value, helpText);
//            } else {
//                return null;
//            }
//        }
    //getter
    //setter
    //writeObject
    //readObject
    //editField for settings screen
    //editButton (to easily edit the setting from anywhere)
}
