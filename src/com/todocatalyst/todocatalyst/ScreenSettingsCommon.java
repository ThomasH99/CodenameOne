package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
import java.util.Map;

/**
used as basis for all (future) settings screens (one per screen)
 * @author Thomas
 */
public class ScreenSettingsCommon extends MyForm {

    TableLayout tl;
    int nbFields = 30;
    TableLayout.Constraint rightAdj;// = tl.createConstraint().horizontalAlign(Component.RIGHT);
    TableLayout.Constraint span2Cols;// = tl.createConstraint().horizontalSpan(2);
    TableLayout.Constraint maxDescriptionSize;
    final static int MAX_SETTING_DESCRIPTION_WIDTH_PERCENT = 80; //don't let the setting description label take up more than this percent of the screen width
    protected boolean tableLayout = false;
 protected static String FORM_UNIQUE_ID = "ScreenSettingsCommon"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics

//    MyForm mainScreen;
    ScreenSettingsCommon(MyForm previousScreen, UpdateField doneAction) {
        this("Settings- " + previousScreen.SCREEN_TITLE, previousScreen, doneAction);
    }

    ScreenSettingsCommon(String title, MyForm previousScreen, UpdateField doneAction) { // throws ParseException, IOException {
        super(title, previousScreen, doneAction); // ScreenTimer.SCREEN_TITLE + " settings"
        setUniqueFormId("ScreenSettingsCommon");
//        this.previousForm = previousScreen;
//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));

        if (tableLayout) {
            if (Display.getInstance().isTablet() || true) {
                tl = new TableLayout(nbFields, 2);
            } else {
                tl = new TableLayout(nbFields * 2, 1);
            }
            tl.setGrowHorizontally(true);
//        rightAdj = tl.createConstraint().horizontalAlign(Component.RIGHT);
            maxDescriptionSize = tl.createConstraint().widthPercentage(MAX_SETTING_DESCRIPTION_WIDTH_PERCENT); //
//        rightAdj = tl.createConstraint().horizontalAlign(Component.RIGHT).widthPercentage(30); //
            rightAdj = tl.createConstraint().horizontalAlign(Component.RIGHT); //
            span2Cols = tl.createConstraint().horizontalSpan(2);
            getContentPane().setLayout(tl);
        } else {
            setLayout(new BoxLayout(BoxLayout.Y_AXIS));
            setScrollableY(true);
        }

        addCommandsToToolbar();
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        revalidate();
        restoreKeepPos();
        super.refreshAfterEdit();
    }

    public void addCommandsToToolbar() {
        Toolbar toolbar = getToolbar();
        //DONE/BACK
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }

        toolbar.addCommandToOverflowMenu(new CommandTracked("Reset to default**")); //reset to default values
    }

    protected void addSettingBoolean(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry) {

//        ASSERT.that(prefEntry.getFieldScription() != null && prefEntry.getFieldScription().length()==0 ,
        ASSERT.that(prefEntry.getFieldScription() != null && prefEntry.getFieldScription().length() != 0, "trying to define a setting for a field without description, settingId=" + prefEntry.settingId);

        if (tableLayout) {
            cont.add(new SpanLabel(prefEntry.getFieldScription()));
            cont.add(maxDescriptionSize, new SpanLabel(prefEntry.getFieldScription()));

            cont.add(rightAdj, new MyOnOffSwitch(parseIdMap2, () -> {
                return MyPrefs.getBoolean(prefEntry);
            }, (b) -> {
                MyPrefs.setBoolean(prefEntry, b);
            }));

            if (!prefEntry.getHelpText().equals("")) {
                cont.add(span2Cols, new SpanLabel(prefEntry.getHelpText()));
            }
        } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//            cont.add(BorderLayout.center(new SpanLabel(prefEntry.getFieldScription())).add(BorderLayout.EAST, new MyOnOffSwitch(parseIdMap2, () -> {
//                return MyPrefs.getBoolean(prefEntry);
//            }, (b) -> {
//                MyPrefs.setBoolean(prefEntry, b);
//            })).add(BorderLayout.SOUTH, new SpanLabel(prefEntry.getHelpText())));
//</editor-fold>
            cont.add(layout(prefEntry.getFieldScription(), new MyOnOffSwitch(parseIdMap2, () -> {
                return MyPrefs.getBoolean(prefEntry);
            }, (b) -> {
                MyPrefs.setBoolean(prefEntry, b);
            }), prefEntry.getHelpText()));
        }
    }

    protected void addSettingTimeInMinutes(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry) {

//        assert prefEntry.getFieldScription() != null && prefEntry.getFieldScription().length()==0 : "trying to define a setting for a field without description";
        ASSERT.that(prefEntry.getFieldScription() != null && prefEntry.getFieldScription().length() != 0, "trying to define a setting for a field without description, settingId=" + prefEntry.settingId);

        if (tableLayout) {
            cont.add(maxDescriptionSize, new SpanLabel(prefEntry.getFieldScription()));

//            cont.add(rightAdj, new MyDurationPicker(parseIdMap2, () -> {
////                return MyPrefs.getInt(prefEntry);
//                return prefEntry.getInt()*MyDate.MINUTE_IN_MILLISECONDS;
//            }, (l) -> {
//                MyPrefs.setInt(prefEntry, (int)(l/MyDate.MINUTE_IN_MILLISECONDS));
//            }));
            MyDurationPicker durationPicker = new MyDurationPicker(prefEntry.getInt() * MyDate.MINUTE_IN_MILLISECONDS);
            durationPicker.addActionListener((e) -> MyPrefs.setInt(prefEntry, ((int) durationPicker.getDuration() / MyDate.MINUTE_IN_MILLISECONDS)));
            cont.add(rightAdj, durationPicker);

            if (!prefEntry.getHelpText().equals("")) {
                cont.add(span2Cols, new SpanLabel(prefEntry.getHelpText()));
            }
        } else {
//            cont.add(BorderLayout.center(new SpanLabel(prefEntry.getFieldScription())).add(BorderLayout.EAST, new MyDurationPicker(parseIdMap2, () -> {
//                return MyPrefs.getInt(prefEntry);
//            }, (i) -> {
//                MyPrefs.setInt(prefEntry, i);
//            })).add(BorderLayout.SOUTH, new SpanLabel(prefEntry.getHelpText())));
            MyDurationPicker durationPicker2 = new MyDurationPicker(prefEntry.getInt() * MyDate.MINUTE_IN_MILLISECONDS);
            durationPicker2.addActionListener((e) -> MyPrefs.setInt(prefEntry, ((int) durationPicker2.getDuration() / MyDate.MINUTE_IN_MILLISECONDS)));
            cont.add(layout(prefEntry.getFieldScription(), durationPicker2, prefEntry.getHelpText()));
        }
    }

    protected void addSettingInt(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry, int minValue, int maxValue, int step) {

//        ASSERT.that( prefEntry.getFieldScription() != null && prefEntry.getFieldScription().length()!=0 , "trying to define a setting for a field without description");
        ASSERT.that(prefEntry.getFieldScription() != null && prefEntry.getFieldScription().length() != 0, "trying to define a setting for a field without description, settingId=" + prefEntry.settingId);

        if (tableLayout) {
            cont.add(maxDescriptionSize, new SpanLabel(prefEntry.getFieldScription()));

            cont.add(rightAdj, new MyIntPicker(parseIdMap2, () -> {
//                return MyPrefs.getInt(prefEntry);
                return prefEntry.getInt();
            }, (i) -> {
                MyPrefs.setInt(prefEntry, i);
            }, minValue, maxValue, step));

            if (!prefEntry.getHelpText().equals("")) {
                cont.add(span2Cols, new SpanLabel(prefEntry.getHelpText()));
            }
        } else {
//            cont.add(BorderLayout.center(new SpanLabel(prefEntry.getFieldScription())).add(BorderLayout.EAST, new MyIntPicker(parseIdMap2, () -> {
//                return MyPrefs.getInt(prefEntry);
//            }, (i) -> {
//                MyPrefs.setInt(prefEntry, i);
//            }, minValue, maxValue, step)).add(BorderLayout.SOUTH, new SpanLabel(prefEntry.getHelpText())));
            cont.add(layout(prefEntry.getFieldScription(), new MyIntPicker(parseIdMap2, () -> {
//                return MyPrefs.getInt(prefEntry);
                return prefEntry.getInt();
            }, (i) -> {
                MyPrefs.setInt(prefEntry, i);
            }, minValue, maxValue, step), prefEntry.getHelpText()));

        }
    }

//    protected void addSettingStringValues(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry, String[] displayValues, boolean unselectAllowed) {
    protected void addSettingStringValues(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry, Object[] displayValues, boolean unselectAllowed) {

        if (tableLayout) {
        } else {
            cont.add(layout(prefEntry.getFieldScription(), new MyComponentGroup(displayValues, parseIdMap2, () -> {
                return prefEntry.getString();
            }, (s) -> {
                MyPrefs.setString(prefEntry, s);
            }, unselectAllowed),
                    prefEntry.getHelpText()));

        }
    }

//    protected void addSettingEnum(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry, Object[] displayValues, GetString getEnumStrFromName, PutString putNameAs, boolean unselectAllowed) {
//    protected void addSettingEnumXXX(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry, Enum enumValue, String enumClassName, boolean unselectAllowed) {
//        //http://stackoverflow.com/questions/14846853/passing-a-class-as-an-argument-to-a-method-in-java
//        //https://www.codenameone.com/javadoc/java/lang/Class.html#asSubclass-java.lang.Class-
//        if (tableLayout) {
//        } else {
////            enumValue.
//             Class myClass;
//             Enum classEnum;
//            try {
//                myClass = Class.forName(enumClassName);
////                classEnum = Class.forName(enumClassName);
//            } catch (ClassNotFoundException ex) {
//                Log.e(ex);
//            }
//        //    if (myClass.isInstance(obj)) 
////           Object[] enumValues= myClass.values();
//           Object[] enumValues= null;
//            cont.add(layout(prefEntry.getFieldScription(), new MyComponentGroup(enumValues, parseIdMap2, () -> {
//                for (Object e : enumValues) {
//                    if (((Enum)e).name().equals(prefEntry.getString()))
//                    return e.toString();
//                }
//                return null;
//            }, (s) -> {
//                for (Object e : enumValues) {
//                    if (e.toString().equals(s)) {
//                        MyPrefs.setString(prefEntry, ((Enum)e).name());
//                        return;
//                    }
//                }
//            }, unselectAllowed),
//                    prefEntry.getHelpText()));
//
//        }
//    }
    protected void addSettingEnum(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry, Object[] enumValues, boolean unselectAllowed) {

        if (tableLayout) {
        } else {
            cont.add(layout(prefEntry.getFieldScription(), new MyComponentGroup(enumValues, parseIdMap2, () -> {
                for (Object e : enumValues) {
                    if (((Enum) e).name().equals(prefEntry.getString())) {
                        return e.toString();
                    }
                }
                return null;
            }, (s) -> {
                for (Object e : enumValues) {
                    if (e.toString().equals(s)) {
                        MyPrefs.setString(prefEntry, ((Enum) e).name());
                        return;
                    }
                }
            }, unselectAllowed),
                    prefEntry.getHelpText()));

        }
    }

//    protected void addSettingStringValuesXXX(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry, MyEnumInterface enumSetting, MyForm.GetString get, MyForm.PutString set, boolean unselectAllowed) {
//    protected void addSettingStringValuesXXX(Container cont, Map<Object, UpdateField> parseIdMap2, MyPrefs.PrefEntry prefEntry, MyEnumInterface enumSetting, boolean unselectAllowed) {
//
//        if (tableLayout) {
//        } else {
////            cont.add(layout(prefEntry.getFieldScription(),new MyComponentGroup(enumSetting.getDescriptionList(), parseIdMap2, enumSetting.getValue(->prefEntry.getString(), set, unselectAllowed),
////                    prefEntry.getHelpText()));
//        }
//    }
    protected void buildContentPane(Container cont) {
        parseIdMapReset();
    }
}
