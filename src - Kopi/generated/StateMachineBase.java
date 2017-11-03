/**
 * This class contains generated code from the Codename One Designer, DO NOT MODIFY!
 * This class is designed for subclassing that way the code generator can overwrite it
 * anytime without erasing your changes which should exist in a subclass!
 * For details about this file and how it works please read this blog post:
 * http://codenameone.blogspot.com/2010/10/ui-builder-class-how-to-actually-use.html
*/
package generated;

import com.codename1.ui.*;
import com.codename1.ui.util.*;
import com.codename1.ui.plaf.*;
import com.codename1.ui.events.*;

public abstract class StateMachineBase extends UIBuilder {
    private Container aboutToShowThisContainer;
    /**
     * this method should be used to initialize variables instead of
     * the constructor/class scope to avoid race conditions
     */
    /**
    * @deprecated use the version that accepts a resource as an argument instead
    
**/
    protected void initVars() {}

    protected void initVars(Resources res) {}

    public StateMachineBase(Resources res, String resPath, boolean loadTheme) {
        startApp(res, resPath, loadTheme);
    }

    public Container startApp(Resources res, String resPath, boolean loadTheme) {
        initVars();
        UIBuilder.registerCustomComponent("RadioButton", com.codename1.ui.RadioButton.class);
        UIBuilder.registerCustomComponent("InfiniteProgress", com.codename1.components.InfiniteProgress.class);
        UIBuilder.registerCustomComponent("Dialog", com.codename1.ui.Dialog.class);
        UIBuilder.registerCustomComponent("TextArea", com.codename1.ui.TextArea.class);
        UIBuilder.registerCustomComponent("TextField", com.codename1.ui.TextField.class);
        UIBuilder.registerCustomComponent("Button", com.codename1.ui.Button.class);
        UIBuilder.registerCustomComponent("ComponentGroup", com.codename1.ui.ComponentGroup.class);
        UIBuilder.registerCustomComponent("ComboBox", com.codename1.ui.ComboBox.class);
        UIBuilder.registerCustomComponent("Tabs", com.codename1.ui.Tabs.class);
        UIBuilder.registerCustomComponent("Form", com.codename1.ui.Form.class);
        UIBuilder.registerCustomComponent("CheckBox", com.codename1.ui.CheckBox.class);
        UIBuilder.registerCustomComponent("Label", com.codename1.ui.Label.class);
        UIBuilder.registerCustomComponent("List", com.codename1.ui.List.class);
        UIBuilder.registerCustomComponent("Container", com.codename1.ui.Container.class);
        UIBuilder.registerCustomComponent("ContainerList", com.codename1.ui.list.ContainerList.class);
        if(loadTheme) {
            if(res == null) {
                try {
                    if(resPath.endsWith(".res")) {
                        res = Resources.open(resPath);
                        System.out.println("Warning: you should construct the state machine without the .res extension to allow theme overlays");
                    } else {
                        res = Resources.openLayered(resPath);
                    }
                } catch(java.io.IOException err) { err.printStackTrace(); }
            }
            initTheme(res);
        }
        if(res != null) {
            setResourceFilePath(resPath);
            setResourceFile(res);
            initVars(res);
            return showForm("main", null);
        } else {
            Form f = (Form)createContainer(resPath, "main");
            initVars(fetchResourceFile());
            beforeShow(f);
            f.show();
            postShow(f);
            return f;
        }
    }

    public Container createWidget(Resources res, String resPath, boolean loadTheme) {
        initVars();
        UIBuilder.registerCustomComponent("RadioButton", com.codename1.ui.RadioButton.class);
        UIBuilder.registerCustomComponent("InfiniteProgress", com.codename1.components.InfiniteProgress.class);
        UIBuilder.registerCustomComponent("Dialog", com.codename1.ui.Dialog.class);
        UIBuilder.registerCustomComponent("TextArea", com.codename1.ui.TextArea.class);
        UIBuilder.registerCustomComponent("TextField", com.codename1.ui.TextField.class);
        UIBuilder.registerCustomComponent("Button", com.codename1.ui.Button.class);
        UIBuilder.registerCustomComponent("ComponentGroup", com.codename1.ui.ComponentGroup.class);
        UIBuilder.registerCustomComponent("ComboBox", com.codename1.ui.ComboBox.class);
        UIBuilder.registerCustomComponent("Tabs", com.codename1.ui.Tabs.class);
        UIBuilder.registerCustomComponent("Form", com.codename1.ui.Form.class);
        UIBuilder.registerCustomComponent("CheckBox", com.codename1.ui.CheckBox.class);
        UIBuilder.registerCustomComponent("Label", com.codename1.ui.Label.class);
        UIBuilder.registerCustomComponent("List", com.codename1.ui.List.class);
        UIBuilder.registerCustomComponent("Container", com.codename1.ui.Container.class);
        UIBuilder.registerCustomComponent("ContainerList", com.codename1.ui.list.ContainerList.class);
        if(loadTheme) {
            if(res == null) {
                try {
                    res = Resources.openLayered(resPath);
                } catch(java.io.IOException err) { err.printStackTrace(); }
            }
            initTheme(res);
        }
        return createContainer(resPath, "main");
    }

    protected void initTheme(Resources res) {
            String[] themes = res.getThemeResourceNames();
            if(themes != null && themes.length > 0) {
                UIManager.getInstance().setThemeProps(res.getTheme(themes[0]));
            }
    }

    public StateMachineBase() {
    }

    public StateMachineBase(String resPath) {
        this(null, resPath, true);
    }

    public StateMachineBase(Resources res) {
        this(res, null, true);
    }

    public StateMachineBase(String resPath, boolean loadTheme) {
        this(null, resPath, loadTheme);
    }

    public StateMachineBase(Resources res, boolean loadTheme) {
        this(res, null, loadTheme);
    }

    public com.codename1.ui.Container findNamedListRenderer(Component root) {
        return (com.codename1.ui.Container)findByName("NamedListRenderer", root);
    }

    public com.codename1.ui.Container findNamedListRenderer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("NamedListRenderer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("NamedListRenderer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findFieldContainer(Component root) {
        return (com.codename1.ui.Container)findByName("FieldContainer", root);
    }

    public com.codename1.ui.Container findFieldContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("FieldContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("FieldContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTestLabelAndFieldContainer(Component root) {
        return (com.codename1.ui.Container)findByName("TestLabelAndFieldContainer", root);
    }

    public com.codename1.ui.Container findTestLabelAndFieldContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TestLabelAndFieldContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TestLabelAndFieldContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findViewText(Component root) {
        return (com.codename1.ui.Label)findByName("ViewText", root);
    }

    public com.codename1.ui.Label findViewText() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("ViewText", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("ViewText", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTest(Component root) {
        return (com.codename1.ui.Container)findByName("Test", root);
    }

    public com.codename1.ui.Container findTest() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Test", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Test", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findEditField(Component root) {
        return (com.codename1.ui.Container)findByName("EditField", root);
    }

    public com.codename1.ui.Container findEditField() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("EditField", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("EditField", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findFieldLabel(Component root) {
        return (com.codename1.ui.Label)findByName("FieldLabel", root);
    }

    public com.codename1.ui.Label findFieldLabel() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("FieldLabel", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("FieldLabel", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer31(Component root) {
        return (com.codename1.ui.Container)findByName("Container31", root);
    }

    public com.codename1.ui.Container findContainer31() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container31", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container31", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findTestButton(Component root) {
        return (com.codename1.ui.Button)findByName("TestButton", root);
    }

    public com.codename1.ui.Button findTestButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("TestButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("TestButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton321(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton321", root);
    }

    public com.codename1.ui.RadioButton findRadioButton321() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton321", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton321", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findWorkSlotEdit(Component root) {
        return (com.codename1.ui.Button)findByName("WorkSlotEdit", root);
    }

    public com.codename1.ui.Button findWorkSlotEdit() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("WorkSlotEdit", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("WorkSlotEdit", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findDateAlarm(Component root) {
        return (com.codename1.ui.Container)findByName("dateAlarm", root);
    }

    public com.codename1.ui.Container findDateAlarm() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("dateAlarm", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("dateAlarm", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findLabelAndFieldCont(Component root) {
        return (com.codename1.ui.Container)findByName("LabelAndFieldCont", root);
    }

    public com.codename1.ui.Container findLabelAndFieldCont() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("LabelAndFieldCont", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("LabelAndFieldCont", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findCategoryName(Component root) {
        return (com.codename1.ui.Label)findByName("CategoryName", root);
    }

    public com.codename1.ui.Label findCategoryName() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("CategoryName", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("CategoryName", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findSettingHelpText(Component root) {
        return (com.codename1.ui.Label)findByName("SettingHelpText", root);
    }

    public com.codename1.ui.Label findSettingHelpText() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("SettingHelpText", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("SettingHelpText", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findSettingEditContainer(Component root) {
        return (com.codename1.ui.Container)findByName("SettingEditContainer", root);
    }

    public com.codename1.ui.Container findSettingEditContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("SettingEditContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("SettingEditContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemAlarmIcon(Component root) {
        return (com.codename1.ui.Button)findByName("ItemAlarmIcon", root);
    }

    public com.codename1.ui.Button findItemAlarmIcon() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemAlarmIcon", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemAlarmIcon", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findDurationField(Component root) {
        return (com.codename1.ui.Container)findByName("DurationField", root);
    }

    public com.codename1.ui.Container findDurationField() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("DurationField", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("DurationField", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTopline(Component root) {
        return (com.codename1.ui.Container)findByName("topline", root);
    }

    public com.codename1.ui.Container findTopline() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("topline", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("topline", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTask(Component root) {
        return (com.codename1.ui.Container)findByName("Task", root);
    }

    public com.codename1.ui.Container findTask() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Task", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Task", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findSettingName(Component root) {
        return (com.codename1.ui.Label)findByName("SettingName", root);
    }

    public com.codename1.ui.Label findSettingName() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("SettingName", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("SettingName", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTimerProjectContainer(Component root) {
        return (com.codename1.ui.Container)findByName("TimerProjectContainer", root);
    }

    public com.codename1.ui.Container findTimerProjectContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TimerProjectContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TimerProjectContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findViewEditButton(Component root) {
        return (com.codename1.ui.Button)findByName("ViewEditButton", root);
    }

    public com.codename1.ui.Button findViewEditButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ViewEditButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ViewEditButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findReusableContainer(Component root) {
        return (com.codename1.ui.Container)findByName("reusableContainer", root);
    }

    public com.codename1.ui.Container findReusableContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("reusableContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("reusableContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTimerTaskDetails(Component root) {
        return (com.codename1.ui.Container)findByName("TimerTaskDetails", root);
    }

    public com.codename1.ui.Container findTimerTaskDetails() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TimerTaskDetails", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TimerTaskDetails", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findSelectStateButton(Component root) {
        return (com.codename1.ui.Button)findByName("SelectStateButton", root);
    }

    public com.codename1.ui.Button findSelectStateButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("SelectStateButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("SelectStateButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findWork(Component root) {
        return (com.codename1.ui.Container)findByName("Work", root);
    }

    public com.codename1.ui.Container findWork() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Work", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Work", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.ComboBox findComboBox(Component root) {
        return (com.codename1.ui.ComboBox)findByName("ComboBox", root);
    }

    public com.codename1.ui.ComboBox findComboBox() {
        com.codename1.ui.ComboBox cmp = (com.codename1.ui.ComboBox)findByName("ComboBox", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.ComboBox)findByName("ComboBox", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTime(Component root) {
        return (com.codename1.ui.Container)findByName("Time", root);
    }

    public com.codename1.ui.Container findTime() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Time", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Time", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemEditButton(Component root) {
        return (com.codename1.ui.Button)findByName("ItemEditButton", root);
    }

    public com.codename1.ui.Button findItemEditButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemEditButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemEditButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findDateField(Component root) {
        return (com.codename1.ui.Container)findByName("DateField", root);
    }

    public com.codename1.ui.Container findDateField() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("DateField", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("DateField", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findWorkSlotRendere(Component root) {
        return (com.codename1.ui.Container)findByName("WorkSlotRendere", root);
    }

    public com.codename1.ui.Container findWorkSlotRendere() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("WorkSlotRendere", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("WorkSlotRendere", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton11(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton11", root);
    }

    public com.codename1.ui.RadioButton findRadioButton11() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton11", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton11", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton12(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton12", root);
    }

    public com.codename1.ui.RadioButton findRadioButton12() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton12", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton12", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemDescription(Component root) {
        return (com.codename1.ui.Button)findByName("ItemDescription", root);
    }

    public com.codename1.ui.Button findItemDescription() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemDescription", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemDescription", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findViewRenderer(Component root) {
        return (com.codename1.ui.Container)findByName("ViewRenderer", root);
    }

    public com.codename1.ui.Container findViewRenderer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("ViewRenderer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("ViewRenderer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findViewDescription(Component root) {
        return (com.codename1.ui.Label)findByName("ViewDescription", root);
    }

    public com.codename1.ui.Label findViewDescription() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("ViewDescription", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("ViewDescription", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.ComponentGroup findComponentGroup1(Component root) {
        return (com.codename1.ui.ComponentGroup)findByName("ComponentGroup1", root);
    }

    public com.codename1.ui.ComponentGroup findComponentGroup1() {
        com.codename1.ui.ComponentGroup cmp = (com.codename1.ui.ComponentGroup)findByName("ComponentGroup1", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.ComponentGroup)findByName("ComponentGroup1", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findTimerNextTaskProject(Component root) {
        return (com.codename1.ui.Label)findByName("TimerNextTaskProject", root);
    }

    public com.codename1.ui.Label findTimerNextTaskProject() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("TimerNextTaskProject", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("TimerNextTaskProject", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTaskContainer(Component root) {
        return (com.codename1.ui.Container)findByName("TaskContainer", root);
    }

    public com.codename1.ui.Container findTaskContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TaskContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TaskContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findTimerTaskEdit(Component root) {
        return (com.codename1.ui.Button)findByName("TimerTaskEdit", root);
    }

    public com.codename1.ui.Button findTimerTaskEdit() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("TimerTaskEdit", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("TimerTaskEdit", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTaskcontent(Component root) {
        return (com.codename1.ui.Container)findByName("taskcontent", root);
    }

    public com.codename1.ui.Container findTaskcontent() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("taskcontent", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("taskcontent", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemSubtasksButton1(Component root) {
        return (com.codename1.ui.Button)findByName("ItemSubtasksButton1", root);
    }

    public com.codename1.ui.Button findItemSubtasksButton1() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemSubtasksButton1", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemSubtasksButton1", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.TextArea findFieldValueWrap(Component root) {
        return (com.codename1.ui.TextArea)findByName("FieldValueWrap", root);
    }

    public com.codename1.ui.TextArea findFieldValueWrap() {
        com.codename1.ui.TextArea cmp = (com.codename1.ui.TextArea)findByName("FieldValueWrap", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.TextArea)findByName("FieldValueWrap", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemStatus(Component root) {
        return (com.codename1.ui.Button)findByName("ItemStatus", root);
    }

    public com.codename1.ui.Button findItemStatus() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemStatus", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemStatus", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTimerLineInListView(Component root) {
        return (com.codename1.ui.Container)findByName("TimerLineInListView", root);
    }

    public com.codename1.ui.Container findTimerLineInListView() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TimerLineInListView", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TimerLineInListView", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findTimerProject(Component root) {
        return (com.codename1.ui.Label)findByName("TimerProject", root);
    }

    public com.codename1.ui.Label findTimerProject() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("TimerProject", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("TimerProject", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findHighLowToggle(Component root) {
        return (com.codename1.ui.Container)findByName("HighLowToggle", root);
    }

    public com.codename1.ui.Container findHighLowToggle() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("HighLowToggle", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("HighLowToggle", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTimeContainer(Component root) {
        return (com.codename1.ui.Container)findByName("TimeContainer", root);
    }

    public com.codename1.ui.Container findTimeContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TimeContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TimeContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findLabel(Component root) {
        return (com.codename1.ui.Label)findByName("Label", root);
    }

    public com.codename1.ui.Label findLabel() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("Label", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("Label", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findWorkSlotDescription(Component root) {
        return (com.codename1.ui.Label)findByName("WorkSlotDescription", root);
    }

    public com.codename1.ui.Label findWorkSlotDescription() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("WorkSlotDescription", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("WorkSlotDescription", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemRemaining(Component root) {
        return (com.codename1.ui.Button)findByName("ItemRemaining", root);
    }

    public com.codename1.ui.Button findItemRemaining() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemRemaining", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemRemaining", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findLine2(Component root) {
        return (com.codename1.ui.Container)findByName("line2", root);
    }

    public com.codename1.ui.Container findLine2() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("line2", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("line2", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton9(Component root) {
        return (com.codename1.ui.Button)findByName("Button9", root);
    }

    public com.codename1.ui.Button findButton9() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button9", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button9", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer4(Component root) {
        return (com.codename1.ui.Container)findByName("Container4", root);
    }

    public com.codename1.ui.Container findContainer4() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container4", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container4", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton8(Component root) {
        return (com.codename1.ui.Button)findByName("Button8", root);
    }

    public com.codename1.ui.Button findButton8() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button8", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button8", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.TextArea findFieldHelp(Component root) {
        return (com.codename1.ui.TextArea)findByName("FieldHelp", root);
    }

    public com.codename1.ui.TextArea findFieldHelp() {
        com.codename1.ui.TextArea cmp = (com.codename1.ui.TextArea)findByName("FieldHelp", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.TextArea)findByName("FieldHelp", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer3(Component root) {
        return (com.codename1.ui.Container)findByName("Container3", root);
    }

    public com.codename1.ui.Container findContainer3() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container3", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container3", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton7(Component root) {
        return (com.codename1.ui.Button)findByName("Button7", root);
    }

    public com.codename1.ui.Button findButton7() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button7", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button7", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer2(Component root) {
        return (com.codename1.ui.Container)findByName("Container2", root);
    }

    public com.codename1.ui.Container findContainer2() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container2", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container2", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton6(Component root) {
        return (com.codename1.ui.Button)findByName("Button6", root);
    }

    public com.codename1.ui.Button findButton6() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button6", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button6", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer1(Component root) {
        return (com.codename1.ui.Container)findByName("Container1", root);
    }

    public com.codename1.ui.Container findContainer1() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container1", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container1", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton5(Component root) {
        return (com.codename1.ui.Button)findByName("Button5", root);
    }

    public com.codename1.ui.Button findButton5() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button5", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button5", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer8(Component root) {
        return (com.codename1.ui.Container)findByName("Container8", root);
    }

    public com.codename1.ui.Container findContainer8() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container8", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container8", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton4(Component root) {
        return (com.codename1.ui.Button)findByName("Button4", root);
    }

    public com.codename1.ui.Button findButton4() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button4", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button4", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findTestLabel1(Component root) {
        return (com.codename1.ui.Label)findByName("TestLabel1", root);
    }

    public com.codename1.ui.Label findTestLabel1() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("TestLabel1", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("TestLabel1", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton3(Component root) {
        return (com.codename1.ui.Button)findByName("Button3", root);
    }

    public com.codename1.ui.Button findButton3() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button3", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button3", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer6(Component root) {
        return (com.codename1.ui.Container)findByName("Container6", root);
    }

    public com.codename1.ui.Container findContainer6() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container6", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container6", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findSelectWrapperContainer(Component root) {
        return (com.codename1.ui.Container)findByName("SelectWrapperContainer", root);
    }

    public com.codename1.ui.Container findSelectWrapperContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("SelectWrapperContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("SelectWrapperContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton2(Component root) {
        return (com.codename1.ui.Button)findByName("Button2", root);
    }

    public com.codename1.ui.Button findButton2() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button2", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button2", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer5(Component root) {
        return (com.codename1.ui.Container)findByName("Container5", root);
    }

    public com.codename1.ui.Container findContainer5() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container5", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container5", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findItemRenderer(Component root) {
        return (com.codename1.ui.Container)findByName("ItemRenderer", root);
    }

    public com.codename1.ui.Container findItemRenderer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("ItemRenderer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("ItemRenderer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findTestLabel(Component root) {
        return (com.codename1.ui.Label)findByName("TestLabel", root);
    }

    public com.codename1.ui.Label findTestLabel() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("TestLabel", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("TestLabel", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findItemListFlattenRenderer(Component root) {
        return (com.codename1.ui.Container)findByName("ItemListFlattenRenderer", root);
    }

    public com.codename1.ui.Container findItemListFlattenRenderer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("ItemListFlattenRenderer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("ItemListFlattenRenderer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findTimerNextTaskTaskTitle(Component root) {
        return (com.codename1.ui.Label)findByName("TimerNextTaskTaskTitle", root);
    }

    public com.codename1.ui.Label findTimerNextTaskTaskTitle() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("TimerNextTaskTaskTitle", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("TimerNextTaskTaskTitle", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findNamedListNumberTasksButton(Component root) {
        return (com.codename1.ui.Button)findByName("NamedListNumberTasksButton", root);
    }

    public com.codename1.ui.Button findNamedListNumberTasksButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("NamedListNumberTasksButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("NamedListNumberTasksButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton(Component root) {
        return (com.codename1.ui.Button)findByName("Button", root);
    }

    public com.codename1.ui.Button findButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findFieldValue(Component root) {
        return (com.codename1.ui.Container)findByName("FieldValue", root);
    }

    public com.codename1.ui.Container findFieldValue() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("FieldValue", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("FieldValue", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findLabel1(Component root) {
        return (com.codename1.ui.Label)findByName("Label1", root);
    }

    public com.codename1.ui.Label findLabel1() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("Label1", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("Label1", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findLabel3(Component root) {
        return (com.codename1.ui.Label)findByName("Label3", root);
    }

    public com.codename1.ui.Label findLabel3() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("Label3", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("Label3", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findLabel2(Component root) {
        return (com.codename1.ui.Label)findByName("Label2", root);
    }

    public com.codename1.ui.Label findLabel2() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("Label2", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("Label2", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findCategoryNumberTasksButton(Component root) {
        return (com.codename1.ui.Button)findByName("CategoryNumberTasksButton", root);
    }

    public com.codename1.ui.Button findCategoryNumberTasksButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("CategoryNumberTasksButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("CategoryNumberTasksButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findSettingEditButton(Component root) {
        return (com.codename1.ui.Button)findByName("SettingEditButton", root);
    }

    public com.codename1.ui.Button findSettingEditButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("SettingEditButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("SettingEditButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton31(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton31", root);
    }

    public com.codename1.ui.RadioButton findRadioButton31() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton31", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton31", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTimerTaskCommentContainer(Component root) {
        return (com.codename1.ui.Container)findByName("TimerTaskCommentContainer", root);
    }

    public com.codename1.ui.Container findTimerTaskCommentContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TimerTaskCommentContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TimerTaskCommentContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton32(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton32", root);
    }

    public com.codename1.ui.RadioButton findRadioButton32() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton32", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton32", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findLabel6(Component root) {
        return (com.codename1.ui.Label)findByName("Label6", root);
    }

    public com.codename1.ui.Label findLabel6() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("Label6", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("Label6", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findLabel4(Component root) {
        return (com.codename1.ui.Label)findByName("Label4", root);
    }

    public com.codename1.ui.Label findLabel4() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("Label4", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("Label4", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findNamedListRemainingTime(Component root) {
        return (com.codename1.ui.Label)findByName("NamedListRemainingTime", root);
    }

    public com.codename1.ui.Label findNamedListRemainingTime() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("NamedListRemainingTime", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("NamedListRemainingTime", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findLabel5(Component root) {
        return (com.codename1.ui.Label)findByName("Label5", root);
    }

    public com.codename1.ui.Label findLabel5() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("Label5", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("Label5", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findItemIndent(Component root) {
        return (com.codename1.ui.Label)findByName("ItemIndent", root);
    }

    public com.codename1.ui.Label findItemIndent() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("ItemIndent", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("ItemIndent", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemPriority(Component root) {
        return (com.codename1.ui.Button)findByName("ItemPriority", root);
    }

    public com.codename1.ui.Button findItemPriority() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemPriority", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemPriority", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findBottomline(Component root) {
        return (com.codename1.ui.Container)findByName("bottomline", root);
    }

    public com.codename1.ui.Container findBottomline() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("bottomline", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("bottomline", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findRemainingFinish(Component root) {
        return (com.codename1.ui.Container)findByName("remainingFinish", root);
    }

    public com.codename1.ui.Container findRemainingFinish() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("remainingFinish", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("remainingFinish", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findTimerTaskStatusButton(Component root) {
        return (com.codename1.ui.Button)findByName("TimerTaskStatusButton", root);
    }

    public com.codename1.ui.Button findTimerTaskStatusButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("TimerTaskStatusButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("TimerTaskStatusButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findWorkSlotStartDate(Component root) {
        return (com.codename1.ui.Label)findByName("WorkSlotStartDate", root);
    }

    public com.codename1.ui.Label findWorkSlotStartDate() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("WorkSlotStartDate", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("WorkSlotStartDate", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton1(Component root) {
        return (com.codename1.ui.Button)findByName("Button1", root);
    }

    public com.codename1.ui.Button findButton1() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button1", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button1", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.TextArea findTimerTextComment(Component root) {
        return (com.codename1.ui.TextArea)findByName("TimerTextComment", root);
    }

    public com.codename1.ui.TextArea findTimerTextComment() {
        com.codename1.ui.TextArea cmp = (com.codename1.ui.TextArea)findByName("TimerTextComment", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.TextArea)findByName("TimerTextComment", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemListFlattenNumberTasksButton(Component root) {
        return (com.codename1.ui.Button)findByName("ItemListFlattenNumberTasksButton", root);
    }

    public com.codename1.ui.Button findItemListFlattenNumberTasksButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemListFlattenNumberTasksButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemListFlattenNumberTasksButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findDateTimeField(Component root) {
        return (com.codename1.ui.Container)findByName("DateTimeField", root);
    }

    public com.codename1.ui.Container findDateTimeField() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("DateTimeField", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("DateTimeField", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemDueDate(Component root) {
        return (com.codename1.ui.Button)findByName("ItemDueDate", root);
    }

    public com.codename1.ui.Button findItemDueDate() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemDueDate", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemDueDate", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findCategoryEditButton(Component root) {
        return (com.codename1.ui.Button)findByName("CategoryEditButton", root);
    }

    public com.codename1.ui.Button findCategoryEditButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("CategoryEditButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("CategoryEditButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findCategoryRenderer(Component root) {
        return (com.codename1.ui.Container)findByName("CategoryRenderer", root);
    }

    public com.codename1.ui.Container findCategoryRenderer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("CategoryRenderer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("CategoryRenderer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.TextArea findTextArea(Component root) {
        return (com.codename1.ui.TextArea)findByName("TextArea", root);
    }

    public com.codename1.ui.TextArea findTextArea() {
        com.codename1.ui.TextArea cmp = (com.codename1.ui.TextArea)findByName("TextArea", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.TextArea)findByName("TextArea", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemCategories(Component root) {
        return (com.codename1.ui.Button)findByName("ItemCategories", root);
    }

    public com.codename1.ui.Button findItemCategories() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemCategories", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemCategories", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTimerContainer(Component root) {
        return (com.codename1.ui.Container)findByName("TimerContainer", root);
    }

    public com.codename1.ui.Container findTimerContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TimerContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TimerContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.TextArea findTimerTaskTitle(Component root) {
        return (com.codename1.ui.TextArea)findByName("TimerTaskTitle", root);
    }

    public com.codename1.ui.TextArea findTimerTaskTitle() {
        com.codename1.ui.TextArea cmp = (com.codename1.ui.TextArea)findByName("TimerTaskTitle", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.TextArea)findByName("TimerTaskTitle", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findTimerNextTaskContainer(Component root) {
        return (com.codename1.ui.Container)findByName("TimerNextTaskContainer", root);
    }

    public com.codename1.ui.Container findTimerNextTaskContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("TimerNextTaskContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("TimerNextTaskContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.CheckBox findCheckBox(Component root) {
        return (com.codename1.ui.CheckBox)findByName("CheckBox", root);
    }

    public com.codename1.ui.CheckBox findCheckBox() {
        com.codename1.ui.CheckBox cmp = (com.codename1.ui.CheckBox)findByName("CheckBox", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.CheckBox)findByName("CheckBox", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findTimerTaskEstimate(Component root) {
        return (com.codename1.ui.Label)findByName("TimerTaskEstimate", root);
    }

    public com.codename1.ui.Label findTimerTaskEstimate() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("TimerTaskEstimate", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("TimerTaskEstimate", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findWorkSlotDuration(Component root) {
        return (com.codename1.ui.Label)findByName("WorkSlotDuration", root);
    }

    public com.codename1.ui.Label findWorkSlotDuration() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("WorkSlotDuration", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("WorkSlotDuration", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.List findList(Component root) {
        return (com.codename1.ui.List)findByName("List", root);
    }

    public com.codename1.ui.List findList() {
        com.codename1.ui.List cmp = (com.codename1.ui.List)findByName("List", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.List)findByName("List", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findCategoryRemainingTime(Component root) {
        return (com.codename1.ui.Label)findByName("CategoryRemainingTime", root);
    }

    public com.codename1.ui.Label findCategoryRemainingTime() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("CategoryRemainingTime", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("CategoryRemainingTime", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findContainer(Component root) {
        return (com.codename1.ui.Container)findByName("Container", root);
    }

    public com.codename1.ui.Container findContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Container", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Container", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.list.ContainerList findContainerList(Component root) {
        return (com.codename1.ui.list.ContainerList)findByName("ContainerList", root);
    }

    public com.codename1.ui.list.ContainerList findContainerList() {
        com.codename1.ui.list.ContainerList cmp = (com.codename1.ui.list.ContainerList)findByName("ContainerList", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.list.ContainerList)findByName("ContainerList", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findDescription(Component root) {
        return (com.codename1.ui.Container)findByName("Description", root);
    }

    public com.codename1.ui.Container findDescription() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("Description", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("Description", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findItemListFlattenName(Component root) {
        return (com.codename1.ui.Label)findByName("ItemListFlattenName", root);
    }

    public com.codename1.ui.Label findItemListFlattenName() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("ItemListFlattenName", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("ItemListFlattenName", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton", root);
    }

    public com.codename1.ui.RadioButton findRadioButton() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.components.InfiniteProgress findInfiniteProgress(Component root) {
        return (com.codename1.components.InfiniteProgress)findByName("InfiniteProgress", root);
    }

    public com.codename1.components.InfiniteProgress findInfiniteProgress() {
        com.codename1.components.InfiniteProgress cmp = (com.codename1.components.InfiniteProgress)findByName("InfiniteProgress", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.components.InfiniteProgress)findByName("InfiniteProgress", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton10(Component root) {
        return (com.codename1.ui.Button)findByName("Button10", root);
    }

    public com.codename1.ui.Button findButton10() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button10", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button10", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.TextField findTextField(Component root) {
        return (com.codename1.ui.TextField)findByName("TextField", root);
    }

    public com.codename1.ui.TextField findTextField() {
        com.codename1.ui.TextField cmp = (com.codename1.ui.TextField)findByName("TextField", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.TextField)findByName("TextField", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findFieldDescription(Component root) {
        return (com.codename1.ui.Label)findByName("FieldDescription", root);
    }

    public com.codename1.ui.Label findFieldDescription() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("FieldDescription", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("FieldDescription", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findItemListFlattenRemainingTime(Component root) {
        return (com.codename1.ui.Label)findByName("ItemListFlattenRemainingTime", root);
    }

    public com.codename1.ui.Label findItemListFlattenRemainingTime() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("ItemListFlattenRemainingTime", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("ItemListFlattenRemainingTime", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findButton11(Component root) {
        return (com.codename1.ui.Button)findByName("Button11", root);
    }

    public com.codename1.ui.Button findButton11() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("Button11", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("Button11", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findNamedListName(Component root) {
        return (com.codename1.ui.Label)findByName("NamedListName", root);
    }

    public com.codename1.ui.Label findNamedListName() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("NamedListName", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("NamedListName", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findSelectRendererWrapper(Component root) {
        return (com.codename1.ui.Container)findByName("SelectRendererWrapper", root);
    }

    public com.codename1.ui.Container findSelectRendererWrapper() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("SelectRendererWrapper", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("SelectRendererWrapper", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.ComponentGroup findComponentGroup(Component root) {
        return (com.codename1.ui.ComponentGroup)findByName("ComponentGroup", root);
    }

    public com.codename1.ui.ComponentGroup findComponentGroup() {
        com.codename1.ui.ComponentGroup cmp = (com.codename1.ui.ComponentGroup)findByName("ComponentGroup", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.ComponentGroup)findByName("ComponentGroup", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemListFlattenEditButton(Component root) {
        return (com.codename1.ui.Button)findByName("ItemListFlattenEditButton", root);
    }

    public com.codename1.ui.Button findItemListFlattenEditButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemListFlattenEditButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemListFlattenEditButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Tabs findTabs(Component root) {
        return (com.codename1.ui.Tabs)findByName("Tabs", root);
    }

    public com.codename1.ui.Tabs findTabs() {
        com.codename1.ui.Tabs cmp = (com.codename1.ui.Tabs)findByName("Tabs", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Tabs)findByName("Tabs", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Label findButtonTimer(Component root) {
        return (com.codename1.ui.Label)findByName("ButtonTimer", root);
    }

    public com.codename1.ui.Label findButtonTimer() {
        com.codename1.ui.Label cmp = (com.codename1.ui.Label)findByName("ButtonTimer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Label)findByName("ButtonTimer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findWorkSlotStatusButton(Component root) {
        return (com.codename1.ui.Button)findByName("WorkSlotStatusButton", root);
    }

    public com.codename1.ui.Button findWorkSlotStatusButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("WorkSlotStatusButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("WorkSlotStatusButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findItemFinishDate(Component root) {
        return (com.codename1.ui.Button)findByName("ItemFinishDate", root);
    }

    public com.codename1.ui.Button findItemFinishDate() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("ItemFinishDate", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("ItemFinishDate", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton3(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton3", root);
    }

    public com.codename1.ui.RadioButton findRadioButton3() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton3", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton3", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton4(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton4", root);
    }

    public com.codename1.ui.RadioButton findRadioButton4() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton4", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton4", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton1(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton1", root);
    }

    public com.codename1.ui.RadioButton findRadioButton1() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton1", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton1", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findSettingRenderer(Component root) {
        return (com.codename1.ui.Container)findByName("SettingRenderer", root);
    }

    public com.codename1.ui.Container findSettingRenderer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("SettingRenderer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("SettingRenderer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.RadioButton findRadioButton2(Component root) {
        return (com.codename1.ui.RadioButton)findByName("RadioButton2", root);
    }

    public com.codename1.ui.RadioButton findRadioButton2() {
        com.codename1.ui.RadioButton cmp = (com.codename1.ui.RadioButton)findByName("RadioButton2", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.RadioButton)findByName("RadioButton2", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Container findHelpTextContainer(Component root) {
        return (com.codename1.ui.Container)findByName("HelpTextContainer", root);
    }

    public com.codename1.ui.Container findHelpTextContainer() {
        com.codename1.ui.Container cmp = (com.codename1.ui.Container)findByName("HelpTextContainer", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Container)findByName("HelpTextContainer", aboutToShowThisContainer);
        }
        return cmp;
    }

    public com.codename1.ui.Button findNamedListEditButton(Component root) {
        return (com.codename1.ui.Button)findByName("NamedListEditButton", root);
    }

    public com.codename1.ui.Button findNamedListEditButton() {
        com.codename1.ui.Button cmp = (com.codename1.ui.Button)findByName("NamedListEditButton", Display.getInstance().getCurrent());
        if(cmp == null && aboutToShowThisContainer != null) {
            cmp = (com.codename1.ui.Button)findByName("NamedListEditButton", aboutToShowThisContainer);
        }
        return cmp;
    }

    public static final int COMMAND_GUI1C7 = 7;
    public static final int COMMAND_GUI1C5 = 6;
    public static final int COMMAND_GUI1C4 = 5;
    public static final int COMMAND_GUI1C3 = 4;
    public static final int COMMAND_GUI1Cmn1 = 2;
    public static final int COMMAND_GUI1Cmd2 = 3;
    public static final int COMMAND_GUI1ShowADialog = 1;

    protected boolean onGUI1C7() {
        return false;
    }

    protected boolean onGUI1C5() {
        return false;
    }

    protected boolean onGUI1C4() {
        return false;
    }

    protected boolean onGUI1C3() {
        return false;
    }

    protected boolean onGUI1Cmn1() {
        return false;
    }

    protected boolean onGUI1Cmd2() {
        return false;
    }

    protected boolean onGUI1ShowADialog() {
        return false;
    }

    protected void processCommand(ActionEvent ev, Command cmd) {
        switch(cmd.getId()) {
            case COMMAND_GUI1C7:
                if(onGUI1C7()) {
                    ev.consume();
                    return;
                }
                break;

            case COMMAND_GUI1C5:
                if(onGUI1C5()) {
                    ev.consume();
                    return;
                }
                break;

            case COMMAND_GUI1C4:
                if(onGUI1C4()) {
                    ev.consume();
                    return;
                }
                break;

            case COMMAND_GUI1C3:
                if(onGUI1C3()) {
                    ev.consume();
                    return;
                }
                break;

            case COMMAND_GUI1Cmn1:
                if(onGUI1Cmn1()) {
                    ev.consume();
                    return;
                }
                break;

            case COMMAND_GUI1Cmd2:
                if(onGUI1Cmd2()) {
                    ev.consume();
                    return;
                }
                break;

            case COMMAND_GUI1ShowADialog:
                if(onGUI1ShowADialog()) {
                    ev.consume();
                    return;
                }
                break;

        }
        if(ev.getComponent() != null) {
            handleComponentAction(ev.getComponent(), ev);
        }
    }

    protected void exitForm(Form f) {
        if("CategoryRenderer".equals(f.getName())) {
            exitCategoryRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Timer".equals(f.getName())) {
            exitTimer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("UserViewScreen".equals(f.getName())) {
            exitUserViewScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Test".equals(f.getName())) {
            exitTest(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("reusableContainer".equals(f.getName())) {
            exitReusableContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("SettingRenderer".equals(f.getName())) {
            exitSettingRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateTimeField".equals(f.getName())) {
            exitDateTimeField(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("WorkSlotRendere".equals(f.getName())) {
            exitWorkSlotRendere(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemScreen".equals(f.getName())) {
            exitItemScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("PrioritySorter".equals(f.getName())) {
            exitPrioritySorter(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Filter".equals(f.getName())) {
            exitFilter(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemRenderer".equals(f.getName())) {
            exitItemRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateField".equals(f.getName())) {
            exitDateField(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("LabelAndFieldCont".equals(f.getName())) {
            exitLabelAndFieldCont(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("main".equals(f.getName())) {
            exitMain(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 2".equals(f.getName())) {
            exitGUI2(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 1".equals(f.getName())) {
            exitGUI1(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("HighLowToggle".equals(f.getName())) {
            exitHighLowToggle(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestScreen".equals(f.getName())) {
            exitTestScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("PriorityPicker".equals(f.getName())) {
            exitPriorityPicker(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestLabelAndFieldContainer".equals(f.getName())) {
            exitTestLabelAndFieldContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Category".equals(f.getName())) {
            exitCategory(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("SelectRendererWrapper".equals(f.getName())) {
            exitSelectRendererWrapper(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemListFlattenRenderer".equals(f.getName())) {
            exitItemListFlattenRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerLineInListView".equals(f.getName())) {
            exitTimerLineInListView(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("NamedListRenderer".equals(f.getName())) {
            exitNamedListRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ViewRenderer".equals(f.getName())) {
            exitViewRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerContainer".equals(f.getName())) {
            exitTimerContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

    }


    protected void exitCategoryRenderer(Form f) {
    }


    protected void exitTimer(Form f) {
    }


    protected void exitUserViewScreen(Form f) {
    }


    protected void exitTest(Form f) {
    }


    protected void exitReusableContainer(Form f) {
    }


    protected void exitSettingRenderer(Form f) {
    }


    protected void exitDateTimeField(Form f) {
    }


    protected void exitWorkSlotRendere(Form f) {
    }


    protected void exitItemScreen(Form f) {
    }


    protected void exitPrioritySorter(Form f) {
    }


    protected void exitFilter(Form f) {
    }


    protected void exitItemRenderer(Form f) {
    }


    protected void exitDateField(Form f) {
    }


    protected void exitLabelAndFieldCont(Form f) {
    }


    protected void exitMain(Form f) {
    }


    protected void exitGUI2(Form f) {
    }


    protected void exitGUI1(Form f) {
    }


    protected void exitHighLowToggle(Form f) {
    }


    protected void exitTestScreen(Form f) {
    }


    protected void exitPriorityPicker(Form f) {
    }


    protected void exitTestLabelAndFieldContainer(Form f) {
    }


    protected void exitCategory(Form f) {
    }


    protected void exitSelectRendererWrapper(Form f) {
    }


    protected void exitItemListFlattenRenderer(Form f) {
    }


    protected void exitTimerLineInListView(Form f) {
    }


    protected void exitNamedListRenderer(Form f) {
    }


    protected void exitViewRenderer(Form f) {
    }


    protected void exitTimerContainer(Form f) {
    }

    protected void beforeShow(Form f) {
    aboutToShowThisContainer = f;
        if("CategoryRenderer".equals(f.getName())) {
            beforeCategoryRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Timer".equals(f.getName())) {
            beforeTimer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("UserViewScreen".equals(f.getName())) {
            beforeUserViewScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Test".equals(f.getName())) {
            beforeTest(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("reusableContainer".equals(f.getName())) {
            beforeReusableContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("SettingRenderer".equals(f.getName())) {
            beforeSettingRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateTimeField".equals(f.getName())) {
            beforeDateTimeField(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("WorkSlotRendere".equals(f.getName())) {
            beforeWorkSlotRendere(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemScreen".equals(f.getName())) {
            beforeItemScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("PrioritySorter".equals(f.getName())) {
            beforePrioritySorter(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Filter".equals(f.getName())) {
            beforeFilter(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemRenderer".equals(f.getName())) {
            beforeItemRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateField".equals(f.getName())) {
            beforeDateField(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("LabelAndFieldCont".equals(f.getName())) {
            beforeLabelAndFieldCont(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("main".equals(f.getName())) {
            beforeMain(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 2".equals(f.getName())) {
            beforeGUI2(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 1".equals(f.getName())) {
            beforeGUI1(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("HighLowToggle".equals(f.getName())) {
            beforeHighLowToggle(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestScreen".equals(f.getName())) {
            beforeTestScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("PriorityPicker".equals(f.getName())) {
            beforePriorityPicker(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestLabelAndFieldContainer".equals(f.getName())) {
            beforeTestLabelAndFieldContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Category".equals(f.getName())) {
            beforeCategory(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("SelectRendererWrapper".equals(f.getName())) {
            beforeSelectRendererWrapper(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemListFlattenRenderer".equals(f.getName())) {
            beforeItemListFlattenRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerLineInListView".equals(f.getName())) {
            beforeTimerLineInListView(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("NamedListRenderer".equals(f.getName())) {
            beforeNamedListRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ViewRenderer".equals(f.getName())) {
            beforeViewRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerContainer".equals(f.getName())) {
            beforeTimerContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

    }


    protected void beforeCategoryRenderer(Form f) {
    }


    protected void beforeTimer(Form f) {
    }


    protected void beforeUserViewScreen(Form f) {
    }


    protected void beforeTest(Form f) {
    }


    protected void beforeReusableContainer(Form f) {
    }


    protected void beforeSettingRenderer(Form f) {
    }


    protected void beforeDateTimeField(Form f) {
    }


    protected void beforeWorkSlotRendere(Form f) {
    }


    protected void beforeItemScreen(Form f) {
    }


    protected void beforePrioritySorter(Form f) {
    }


    protected void beforeFilter(Form f) {
    }


    protected void beforeItemRenderer(Form f) {
    }


    protected void beforeDateField(Form f) {
    }


    protected void beforeLabelAndFieldCont(Form f) {
    }


    protected void beforeMain(Form f) {
    }


    protected void beforeGUI2(Form f) {
    }


    protected void beforeGUI1(Form f) {
    }


    protected void beforeHighLowToggle(Form f) {
    }


    protected void beforeTestScreen(Form f) {
    }


    protected void beforePriorityPicker(Form f) {
    }


    protected void beforeTestLabelAndFieldContainer(Form f) {
    }


    protected void beforeCategory(Form f) {
    }


    protected void beforeSelectRendererWrapper(Form f) {
    }


    protected void beforeItemListFlattenRenderer(Form f) {
    }


    protected void beforeTimerLineInListView(Form f) {
    }


    protected void beforeNamedListRenderer(Form f) {
    }


    protected void beforeViewRenderer(Form f) {
    }


    protected void beforeTimerContainer(Form f) {
    }

    protected void beforeShowContainer(Container c) {
    aboutToShowThisContainer = c;
        if("CategoryRenderer".equals(c.getName())) {
            beforeContainerCategoryRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("Timer".equals(c.getName())) {
            beforeContainerTimer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("UserViewScreen".equals(c.getName())) {
            beforeContainerUserViewScreen(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("Test".equals(c.getName())) {
            beforeContainerTest(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("reusableContainer".equals(c.getName())) {
            beforeContainerReusableContainer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("SettingRenderer".equals(c.getName())) {
            beforeContainerSettingRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateTimeField".equals(c.getName())) {
            beforeContainerDateTimeField(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("WorkSlotRendere".equals(c.getName())) {
            beforeContainerWorkSlotRendere(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemScreen".equals(c.getName())) {
            beforeContainerItemScreen(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("PrioritySorter".equals(c.getName())) {
            beforeContainerPrioritySorter(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("Filter".equals(c.getName())) {
            beforeContainerFilter(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemRenderer".equals(c.getName())) {
            beforeContainerItemRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateField".equals(c.getName())) {
            beforeContainerDateField(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("LabelAndFieldCont".equals(c.getName())) {
            beforeContainerLabelAndFieldCont(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("main".equals(c.getName())) {
            beforeContainerMain(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 2".equals(c.getName())) {
            beforeContainerGUI2(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 1".equals(c.getName())) {
            beforeContainerGUI1(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("HighLowToggle".equals(c.getName())) {
            beforeContainerHighLowToggle(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestScreen".equals(c.getName())) {
            beforeContainerTestScreen(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("PriorityPicker".equals(c.getName())) {
            beforeContainerPriorityPicker(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestLabelAndFieldContainer".equals(c.getName())) {
            beforeContainerTestLabelAndFieldContainer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("Category".equals(c.getName())) {
            beforeContainerCategory(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("SelectRendererWrapper".equals(c.getName())) {
            beforeContainerSelectRendererWrapper(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemListFlattenRenderer".equals(c.getName())) {
            beforeContainerItemListFlattenRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerLineInListView".equals(c.getName())) {
            beforeContainerTimerLineInListView(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("NamedListRenderer".equals(c.getName())) {
            beforeContainerNamedListRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("ViewRenderer".equals(c.getName())) {
            beforeContainerViewRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerContainer".equals(c.getName())) {
            beforeContainerTimerContainer(c);
            aboutToShowThisContainer = null;
            return;
        }

    }


    protected void beforeContainerCategoryRenderer(Container c) {
    }


    protected void beforeContainerTimer(Container c) {
    }


    protected void beforeContainerUserViewScreen(Container c) {
    }


    protected void beforeContainerTest(Container c) {
    }


    protected void beforeContainerReusableContainer(Container c) {
    }


    protected void beforeContainerSettingRenderer(Container c) {
    }


    protected void beforeContainerDateTimeField(Container c) {
    }


    protected void beforeContainerWorkSlotRendere(Container c) {
    }


    protected void beforeContainerItemScreen(Container c) {
    }


    protected void beforeContainerPrioritySorter(Container c) {
    }


    protected void beforeContainerFilter(Container c) {
    }


    protected void beforeContainerItemRenderer(Container c) {
    }


    protected void beforeContainerDateField(Container c) {
    }


    protected void beforeContainerLabelAndFieldCont(Container c) {
    }


    protected void beforeContainerMain(Container c) {
    }


    protected void beforeContainerGUI2(Container c) {
    }


    protected void beforeContainerGUI1(Container c) {
    }


    protected void beforeContainerHighLowToggle(Container c) {
    }


    protected void beforeContainerTestScreen(Container c) {
    }


    protected void beforeContainerPriorityPicker(Container c) {
    }


    protected void beforeContainerTestLabelAndFieldContainer(Container c) {
    }


    protected void beforeContainerCategory(Container c) {
    }


    protected void beforeContainerSelectRendererWrapper(Container c) {
    }


    protected void beforeContainerItemListFlattenRenderer(Container c) {
    }


    protected void beforeContainerTimerLineInListView(Container c) {
    }


    protected void beforeContainerNamedListRenderer(Container c) {
    }


    protected void beforeContainerViewRenderer(Container c) {
    }


    protected void beforeContainerTimerContainer(Container c) {
    }

    protected void postShow(Form f) {
        if("CategoryRenderer".equals(f.getName())) {
            postCategoryRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Timer".equals(f.getName())) {
            postTimer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("UserViewScreen".equals(f.getName())) {
            postUserViewScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Test".equals(f.getName())) {
            postTest(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("reusableContainer".equals(f.getName())) {
            postReusableContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("SettingRenderer".equals(f.getName())) {
            postSettingRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateTimeField".equals(f.getName())) {
            postDateTimeField(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("WorkSlotRendere".equals(f.getName())) {
            postWorkSlotRendere(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemScreen".equals(f.getName())) {
            postItemScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("PrioritySorter".equals(f.getName())) {
            postPrioritySorter(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Filter".equals(f.getName())) {
            postFilter(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemRenderer".equals(f.getName())) {
            postItemRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateField".equals(f.getName())) {
            postDateField(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("LabelAndFieldCont".equals(f.getName())) {
            postLabelAndFieldCont(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("main".equals(f.getName())) {
            postMain(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 2".equals(f.getName())) {
            postGUI2(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 1".equals(f.getName())) {
            postGUI1(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("HighLowToggle".equals(f.getName())) {
            postHighLowToggle(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestScreen".equals(f.getName())) {
            postTestScreen(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("PriorityPicker".equals(f.getName())) {
            postPriorityPicker(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestLabelAndFieldContainer".equals(f.getName())) {
            postTestLabelAndFieldContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("Category".equals(f.getName())) {
            postCategory(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("SelectRendererWrapper".equals(f.getName())) {
            postSelectRendererWrapper(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemListFlattenRenderer".equals(f.getName())) {
            postItemListFlattenRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerLineInListView".equals(f.getName())) {
            postTimerLineInListView(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("NamedListRenderer".equals(f.getName())) {
            postNamedListRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("ViewRenderer".equals(f.getName())) {
            postViewRenderer(f);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerContainer".equals(f.getName())) {
            postTimerContainer(f);
            aboutToShowThisContainer = null;
            return;
        }

    }


    protected void postCategoryRenderer(Form f) {
    }


    protected void postTimer(Form f) {
    }


    protected void postUserViewScreen(Form f) {
    }


    protected void postTest(Form f) {
    }


    protected void postReusableContainer(Form f) {
    }


    protected void postSettingRenderer(Form f) {
    }


    protected void postDateTimeField(Form f) {
    }


    protected void postWorkSlotRendere(Form f) {
    }


    protected void postItemScreen(Form f) {
    }


    protected void postPrioritySorter(Form f) {
    }


    protected void postFilter(Form f) {
    }


    protected void postItemRenderer(Form f) {
    }


    protected void postDateField(Form f) {
    }


    protected void postLabelAndFieldCont(Form f) {
    }


    protected void postMain(Form f) {
    }


    protected void postGUI2(Form f) {
    }


    protected void postGUI1(Form f) {
    }


    protected void postHighLowToggle(Form f) {
    }


    protected void postTestScreen(Form f) {
    }


    protected void postPriorityPicker(Form f) {
    }


    protected void postTestLabelAndFieldContainer(Form f) {
    }


    protected void postCategory(Form f) {
    }


    protected void postSelectRendererWrapper(Form f) {
    }


    protected void postItemListFlattenRenderer(Form f) {
    }


    protected void postTimerLineInListView(Form f) {
    }


    protected void postNamedListRenderer(Form f) {
    }


    protected void postViewRenderer(Form f) {
    }


    protected void postTimerContainer(Form f) {
    }

    protected void postShowContainer(Container c) {
        if("CategoryRenderer".equals(c.getName())) {
            postContainerCategoryRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("Timer".equals(c.getName())) {
            postContainerTimer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("UserViewScreen".equals(c.getName())) {
            postContainerUserViewScreen(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("Test".equals(c.getName())) {
            postContainerTest(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("reusableContainer".equals(c.getName())) {
            postContainerReusableContainer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("SettingRenderer".equals(c.getName())) {
            postContainerSettingRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateTimeField".equals(c.getName())) {
            postContainerDateTimeField(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("WorkSlotRendere".equals(c.getName())) {
            postContainerWorkSlotRendere(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemScreen".equals(c.getName())) {
            postContainerItemScreen(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("PrioritySorter".equals(c.getName())) {
            postContainerPrioritySorter(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("Filter".equals(c.getName())) {
            postContainerFilter(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemRenderer".equals(c.getName())) {
            postContainerItemRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("DateField".equals(c.getName())) {
            postContainerDateField(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("LabelAndFieldCont".equals(c.getName())) {
            postContainerLabelAndFieldCont(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("main".equals(c.getName())) {
            postContainerMain(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 2".equals(c.getName())) {
            postContainerGUI2(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 1".equals(c.getName())) {
            postContainerGUI1(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("HighLowToggle".equals(c.getName())) {
            postContainerHighLowToggle(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestScreen".equals(c.getName())) {
            postContainerTestScreen(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("PriorityPicker".equals(c.getName())) {
            postContainerPriorityPicker(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("TestLabelAndFieldContainer".equals(c.getName())) {
            postContainerTestLabelAndFieldContainer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("Category".equals(c.getName())) {
            postContainerCategory(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("SelectRendererWrapper".equals(c.getName())) {
            postContainerSelectRendererWrapper(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemListFlattenRenderer".equals(c.getName())) {
            postContainerItemListFlattenRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerLineInListView".equals(c.getName())) {
            postContainerTimerLineInListView(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("NamedListRenderer".equals(c.getName())) {
            postContainerNamedListRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("ViewRenderer".equals(c.getName())) {
            postContainerViewRenderer(c);
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerContainer".equals(c.getName())) {
            postContainerTimerContainer(c);
            aboutToShowThisContainer = null;
            return;
        }

    }


    protected void postContainerCategoryRenderer(Container c) {
    }


    protected void postContainerTimer(Container c) {
    }


    protected void postContainerUserViewScreen(Container c) {
    }


    protected void postContainerTest(Container c) {
    }


    protected void postContainerReusableContainer(Container c) {
    }


    protected void postContainerSettingRenderer(Container c) {
    }


    protected void postContainerDateTimeField(Container c) {
    }


    protected void postContainerWorkSlotRendere(Container c) {
    }


    protected void postContainerItemScreen(Container c) {
    }


    protected void postContainerPrioritySorter(Container c) {
    }


    protected void postContainerFilter(Container c) {
    }


    protected void postContainerItemRenderer(Container c) {
    }


    protected void postContainerDateField(Container c) {
    }


    protected void postContainerLabelAndFieldCont(Container c) {
    }


    protected void postContainerMain(Container c) {
    }


    protected void postContainerGUI2(Container c) {
    }


    protected void postContainerGUI1(Container c) {
    }


    protected void postContainerHighLowToggle(Container c) {
    }


    protected void postContainerTestScreen(Container c) {
    }


    protected void postContainerPriorityPicker(Container c) {
    }


    protected void postContainerTestLabelAndFieldContainer(Container c) {
    }


    protected void postContainerCategory(Container c) {
    }


    protected void postContainerSelectRendererWrapper(Container c) {
    }


    protected void postContainerItemListFlattenRenderer(Container c) {
    }


    protected void postContainerTimerLineInListView(Container c) {
    }


    protected void postContainerNamedListRenderer(Container c) {
    }


    protected void postContainerViewRenderer(Container c) {
    }


    protected void postContainerTimerContainer(Container c) {
    }

    protected void onCreateRoot(String rootName) {
        if("CategoryRenderer".equals(rootName)) {
            onCreateCategoryRenderer();
            aboutToShowThisContainer = null;
            return;
        }

        if("Timer".equals(rootName)) {
            onCreateTimer();
            aboutToShowThisContainer = null;
            return;
        }

        if("UserViewScreen".equals(rootName)) {
            onCreateUserViewScreen();
            aboutToShowThisContainer = null;
            return;
        }

        if("Test".equals(rootName)) {
            onCreateTest();
            aboutToShowThisContainer = null;
            return;
        }

        if("reusableContainer".equals(rootName)) {
            onCreateReusableContainer();
            aboutToShowThisContainer = null;
            return;
        }

        if("SettingRenderer".equals(rootName)) {
            onCreateSettingRenderer();
            aboutToShowThisContainer = null;
            return;
        }

        if("DateTimeField".equals(rootName)) {
            onCreateDateTimeField();
            aboutToShowThisContainer = null;
            return;
        }

        if("WorkSlotRendere".equals(rootName)) {
            onCreateWorkSlotRendere();
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemScreen".equals(rootName)) {
            onCreateItemScreen();
            aboutToShowThisContainer = null;
            return;
        }

        if("PrioritySorter".equals(rootName)) {
            onCreatePrioritySorter();
            aboutToShowThisContainer = null;
            return;
        }

        if("Filter".equals(rootName)) {
            onCreateFilter();
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemRenderer".equals(rootName)) {
            onCreateItemRenderer();
            aboutToShowThisContainer = null;
            return;
        }

        if("DateField".equals(rootName)) {
            onCreateDateField();
            aboutToShowThisContainer = null;
            return;
        }

        if("LabelAndFieldCont".equals(rootName)) {
            onCreateLabelAndFieldCont();
            aboutToShowThisContainer = null;
            return;
        }

        if("main".equals(rootName)) {
            onCreateMain();
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 2".equals(rootName)) {
            onCreateGUI2();
            aboutToShowThisContainer = null;
            return;
        }

        if("GUI 1".equals(rootName)) {
            onCreateGUI1();
            aboutToShowThisContainer = null;
            return;
        }

        if("HighLowToggle".equals(rootName)) {
            onCreateHighLowToggle();
            aboutToShowThisContainer = null;
            return;
        }

        if("TestScreen".equals(rootName)) {
            onCreateTestScreen();
            aboutToShowThisContainer = null;
            return;
        }

        if("PriorityPicker".equals(rootName)) {
            onCreatePriorityPicker();
            aboutToShowThisContainer = null;
            return;
        }

        if("TestLabelAndFieldContainer".equals(rootName)) {
            onCreateTestLabelAndFieldContainer();
            aboutToShowThisContainer = null;
            return;
        }

        if("Category".equals(rootName)) {
            onCreateCategory();
            aboutToShowThisContainer = null;
            return;
        }

        if("SelectRendererWrapper".equals(rootName)) {
            onCreateSelectRendererWrapper();
            aboutToShowThisContainer = null;
            return;
        }

        if("ItemListFlattenRenderer".equals(rootName)) {
            onCreateItemListFlattenRenderer();
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerLineInListView".equals(rootName)) {
            onCreateTimerLineInListView();
            aboutToShowThisContainer = null;
            return;
        }

        if("NamedListRenderer".equals(rootName)) {
            onCreateNamedListRenderer();
            aboutToShowThisContainer = null;
            return;
        }

        if("ViewRenderer".equals(rootName)) {
            onCreateViewRenderer();
            aboutToShowThisContainer = null;
            return;
        }

        if("TimerContainer".equals(rootName)) {
            onCreateTimerContainer();
            aboutToShowThisContainer = null;
            return;
        }

    }


    protected void onCreateCategoryRenderer() {
    }


    protected void onCreateTimer() {
    }


    protected void onCreateUserViewScreen() {
    }


    protected void onCreateTest() {
    }


    protected void onCreateReusableContainer() {
    }


    protected void onCreateSettingRenderer() {
    }


    protected void onCreateDateTimeField() {
    }


    protected void onCreateWorkSlotRendere() {
    }


    protected void onCreateItemScreen() {
    }


    protected void onCreatePrioritySorter() {
    }


    protected void onCreateFilter() {
    }


    protected void onCreateItemRenderer() {
    }


    protected void onCreateDateField() {
    }


    protected void onCreateLabelAndFieldCont() {
    }


    protected void onCreateMain() {
    }


    protected void onCreateGUI2() {
    }


    protected void onCreateGUI1() {
    }


    protected void onCreateHighLowToggle() {
    }


    protected void onCreateTestScreen() {
    }


    protected void onCreatePriorityPicker() {
    }


    protected void onCreateTestLabelAndFieldContainer() {
    }


    protected void onCreateCategory() {
    }


    protected void onCreateSelectRendererWrapper() {
    }


    protected void onCreateItemListFlattenRenderer() {
    }


    protected void onCreateTimerLineInListView() {
    }


    protected void onCreateNamedListRenderer() {
    }


    protected void onCreateViewRenderer() {
    }


    protected void onCreateTimerContainer() {
    }

    protected boolean setListModel(List cmp) {
        String listName = cmp.getName();
        if("ComboBox".equals(listName)) {
            return initListModelComboBox(cmp);
        }
        if("List".equals(listName)) {
            return initListModelList(cmp);
        }
        return super.setListModel(cmp);
    }

    protected boolean initListModelComboBox(List cmp) {
        return false;
    }

    protected boolean initListModelList(List cmp) {
        return false;
    }

    protected boolean setListModel(com.codename1.ui.list.ContainerList cmp) {
        String listName = cmp.getName();
        if("ContainerList".equals(listName)) {
            return initListModelContainerList(cmp);
        }
        return super.setListModel(cmp);
    }

    protected boolean initListModelContainerList(com.codename1.ui.list.ContainerList cmp) {
        return false;
    }

    protected void handleComponentAction(Component c, ActionEvent event) {
        Container rootContainerAncestor = getRootAncestor(c);
        if(rootContainerAncestor == null) return;
        String rootContainerName = rootContainerAncestor.getName();
        if(c.getParent().getLeadParent() != null) {
            c = c.getParent().getLeadParent();
        }
        if(rootContainerName == null) return;
        if(rootContainerName.equals("CategoryRenderer")) {
            if("CategoryNumberTasksButton".equals(c.getName())) {
                onCategoryRenderer_CategoryNumberTasksButtonAction(c, event);
                return;
            }
            if("CategoryEditButton".equals(c.getName())) {
                onCategoryRenderer_CategoryEditButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("Timer")) {
            if("Button5".equals(c.getName())) {
                onTimer_Button5Action(c, event);
                return;
            }
            if("Button4".equals(c.getName())) {
                onTimer_Button4Action(c, event);
                return;
            }
            if("TextField".equals(c.getName())) {
                onTimer_TextFieldAction(c, event);
                return;
            }
            if("Button6".equals(c.getName())) {
                onTimer_Button6Action(c, event);
                return;
            }
            if("CheckBox".equals(c.getName())) {
                onTimer_CheckBoxAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("UserViewScreen")) {
            if("RadioButton".equals(c.getName())) {
                onUserViewScreen_RadioButtonAction(c, event);
                return;
            }
            if("RadioButton1".equals(c.getName())) {
                onUserViewScreen_RadioButton1Action(c, event);
                return;
            }
            if("RadioButton2".equals(c.getName())) {
                onUserViewScreen_RadioButton2Action(c, event);
                return;
            }
            if("RadioButton3".equals(c.getName())) {
                onUserViewScreen_RadioButton3Action(c, event);
                return;
            }
            if("RadioButton4".equals(c.getName())) {
                onUserViewScreen_RadioButton4Action(c, event);
                return;
            }
            if("RadioButton31".equals(c.getName())) {
                onUserViewScreen_RadioButton31Action(c, event);
                return;
            }
            if("RadioButton32".equals(c.getName())) {
                onUserViewScreen_RadioButton32Action(c, event);
                return;
            }
            if("RadioButton321".equals(c.getName())) {
                onUserViewScreen_RadioButton321Action(c, event);
                return;
            }
            if("RadioButton12".equals(c.getName())) {
                onUserViewScreen_RadioButton12Action(c, event);
                return;
            }
            if("RadioButton11".equals(c.getName())) {
                onUserViewScreen_RadioButton11Action(c, event);
                return;
            }
        }
        if(rootContainerName.equals("Test")) {
            if("FieldValueWrap".equals(c.getName())) {
                onTest_FieldValueWrapAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("reusableContainer")) {
            if("Button6".equals(c.getName())) {
                onReusableContainer_Button6Action(c, event);
                return;
            }
            if("Button5".equals(c.getName())) {
                onReusableContainer_Button5Action(c, event);
                return;
            }
            if("Button4".equals(c.getName())) {
                onReusableContainer_Button4Action(c, event);
                return;
            }
            if("Button11".equals(c.getName())) {
                onReusableContainer_Button11Action(c, event);
                return;
            }
            if("Button3".equals(c.getName())) {
                onReusableContainer_Button3Action(c, event);
                return;
            }
            if("Button2".equals(c.getName())) {
                onReusableContainer_Button2Action(c, event);
                return;
            }
        }
        if(rootContainerName.equals("SettingRenderer")) {
            if("SettingEditButton".equals(c.getName())) {
                onSettingRenderer_SettingEditButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("WorkSlotRendere")) {
            if("WorkSlotEdit".equals(c.getName())) {
                onWorkSlotRendere_WorkSlotEditAction(c, event);
                return;
            }
            if("WorkSlotStatusButton".equals(c.getName())) {
                onWorkSlotRendere_WorkSlotStatusButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("ItemScreen")) {
            if("TextField".equals(c.getName())) {
                onItemScreen_TextFieldAction(c, event);
                return;
            }
            if("ContainerList".equals(c.getName())) {
                onItemScreen_ContainerListAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("PrioritySorter")) {
            if("Button1".equals(c.getName())) {
                onPrioritySorter_Button1Action(c, event);
                return;
            }
            if("Button2".equals(c.getName())) {
                onPrioritySorter_Button2Action(c, event);
                return;
            }
            if("Button3".equals(c.getName())) {
                onPrioritySorter_Button3Action(c, event);
                return;
            }
            if("Button".equals(c.getName())) {
                onPrioritySorter_ButtonAction(c, event);
                return;
            }
            if("List".equals(c.getName())) {
                onPrioritySorter_ListAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("ItemRenderer")) {
            if("ItemEditButton".equals(c.getName())) {
                onItemRenderer_ItemEditButtonAction(c, event);
                return;
            }
            if("ItemStatus".equals(c.getName())) {
                onItemRenderer_ItemStatusAction(c, event);
                return;
            }
            if("ItemSubtasksButton1".equals(c.getName())) {
                onItemRenderer_ItemSubtasksButton1Action(c, event);
                return;
            }
            if("ItemDescription".equals(c.getName())) {
                onItemRenderer_ItemDescriptionAction(c, event);
                return;
            }
            if("ItemDueDate".equals(c.getName())) {
                onItemRenderer_ItemDueDateAction(c, event);
                return;
            }
            if("ItemAlarmIcon".equals(c.getName())) {
                onItemRenderer_ItemAlarmIconAction(c, event);
                return;
            }
            if("ItemCategories".equals(c.getName())) {
                onItemRenderer_ItemCategoriesAction(c, event);
                return;
            }
            if("ItemPriority".equals(c.getName())) {
                onItemRenderer_ItemPriorityAction(c, event);
                return;
            }
            if("ItemRemaining".equals(c.getName())) {
                onItemRenderer_ItemRemainingAction(c, event);
                return;
            }
            if("ItemFinishDate".equals(c.getName())) {
                onItemRenderer_ItemFinishDateAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("LabelAndFieldCont")) {
            if("FieldHelp".equals(c.getName())) {
                onLabelAndFieldCont_FieldHelpAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("GUI 2")) {
            if("TextArea".equals(c.getName())) {
                onGUI2_TextAreaAction(c, event);
                return;
            }
            if("Button6".equals(c.getName())) {
                onGUI2_Button6Action(c, event);
                return;
            }
            if("Button5".equals(c.getName())) {
                onGUI2_Button5Action(c, event);
                return;
            }
            if("Button4".equals(c.getName())) {
                onGUI2_Button4Action(c, event);
                return;
            }
            if("Button11".equals(c.getName())) {
                onGUI2_Button11Action(c, event);
                return;
            }
            if("Button3".equals(c.getName())) {
                onGUI2_Button3Action(c, event);
                return;
            }
            if("Button2".equals(c.getName())) {
                onGUI2_Button2Action(c, event);
                return;
            }
            if("Button1".equals(c.getName())) {
                onGUI2_Button1Action(c, event);
                return;
            }
            if("Button".equals(c.getName())) {
                onGUI2_ButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("GUI 1")) {
            if("RadioButton2".equals(c.getName())) {
                onGUI1_RadioButton2Action(c, event);
                return;
            }
            if("CheckBox".equals(c.getName())) {
                onGUI1_CheckBoxAction(c, event);
                return;
            }
            if("Button".equals(c.getName())) {
                onGUI1_ButtonAction(c, event);
                return;
            }
            if("ComboBox".equals(c.getName())) {
                onGUI1_ComboBoxAction(c, event);
                return;
            }
            if("TextField".equals(c.getName())) {
                onGUI1_TextFieldAction(c, event);
                return;
            }
            if("Button1".equals(c.getName())) {
                onGUI1_Button1Action(c, event);
                return;
            }
        }
        if(rootContainerName.equals("HighLowToggle")) {
            if("RadioButton1".equals(c.getName())) {
                onHighLowToggle_RadioButton1Action(c, event);
                return;
            }
            if("RadioButton".equals(c.getName())) {
                onHighLowToggle_RadioButtonAction(c, event);
                return;
            }
            if("RadioButton2".equals(c.getName())) {
                onHighLowToggle_RadioButton2Action(c, event);
                return;
            }
        }
        if(rootContainerName.equals("TestScreen")) {
            if("TestButton".equals(c.getName())) {
                onTestScreen_TestButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("PriorityPicker")) {
            if("Button5".equals(c.getName())) {
                onPriorityPicker_Button5Action(c, event);
                return;
            }
            if("Button6".equals(c.getName())) {
                onPriorityPicker_Button6Action(c, event);
                return;
            }
            if("Button8".equals(c.getName())) {
                onPriorityPicker_Button8Action(c, event);
                return;
            }
            if("Button7".equals(c.getName())) {
                onPriorityPicker_Button7Action(c, event);
                return;
            }
            if("Button10".equals(c.getName())) {
                onPriorityPicker_Button10Action(c, event);
                return;
            }
            if("Button9".equals(c.getName())) {
                onPriorityPicker_Button9Action(c, event);
                return;
            }
            if("Button4".equals(c.getName())) {
                onPriorityPicker_Button4Action(c, event);
                return;
            }
            if("Button3".equals(c.getName())) {
                onPriorityPicker_Button3Action(c, event);
                return;
            }
            if("Button2".equals(c.getName())) {
                onPriorityPicker_Button2Action(c, event);
                return;
            }
        }
        if(rootContainerName.equals("SelectRendererWrapper")) {
            if("SelectStateButton".equals(c.getName())) {
                onSelectRendererWrapper_SelectStateButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("ItemListFlattenRenderer")) {
            if("ItemListFlattenNumberTasksButton".equals(c.getName())) {
                onItemListFlattenRenderer_ItemListFlattenNumberTasksButtonAction(c, event);
                return;
            }
            if("ItemListFlattenEditButton".equals(c.getName())) {
                onItemListFlattenRenderer_ItemListFlattenEditButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("TimerLineInListView")) {
            if("Button5".equals(c.getName())) {
                onTimerLineInListView_Button5Action(c, event);
                return;
            }
            if("TextField".equals(c.getName())) {
                onTimerLineInListView_TextFieldAction(c, event);
                return;
            }
            if("Button4".equals(c.getName())) {
                onTimerLineInListView_Button4Action(c, event);
                return;
            }
        }
        if(rootContainerName.equals("NamedListRenderer")) {
            if("NamedListNumberTasksButton".equals(c.getName())) {
                onNamedListRenderer_NamedListNumberTasksButtonAction(c, event);
                return;
            }
            if("NamedListEditButton".equals(c.getName())) {
                onNamedListRenderer_NamedListEditButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("ViewRenderer")) {
            if("ViewEditButton".equals(c.getName())) {
                onViewRenderer_ViewEditButtonAction(c, event);
                return;
            }
        }
        if(rootContainerName.equals("TimerContainer")) {
            if("Button2".equals(c.getName())) {
                onTimerContainer_Button2Action(c, event);
                return;
            }
            if("TimerTaskStatusButton".equals(c.getName())) {
                onTimerContainer_TimerTaskStatusButtonAction(c, event);
                return;
            }
            if("TimerTaskEdit".equals(c.getName())) {
                onTimerContainer_TimerTaskEditAction(c, event);
                return;
            }
            if("TimerTaskTitle".equals(c.getName())) {
                onTimerContainer_TimerTaskTitleAction(c, event);
                return;
            }
            if("TimerTextComment".equals(c.getName())) {
                onTimerContainer_TimerTextCommentAction(c, event);
                return;
            }
        }
    }

      protected void onCategoryRenderer_CategoryNumberTasksButtonAction(Component c, ActionEvent event) {
      }

      protected void onCategoryRenderer_CategoryEditButtonAction(Component c, ActionEvent event) {
      }

      protected void onTimer_Button5Action(Component c, ActionEvent event) {
      }

      protected void onTimer_Button4Action(Component c, ActionEvent event) {
      }

      protected void onTimer_TextFieldAction(Component c, ActionEvent event) {
      }

      protected void onTimer_Button6Action(Component c, ActionEvent event) {
      }

      protected void onTimer_CheckBoxAction(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButtonAction(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton1Action(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton2Action(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton3Action(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton4Action(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton31Action(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton32Action(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton321Action(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton12Action(Component c, ActionEvent event) {
      }

      protected void onUserViewScreen_RadioButton11Action(Component c, ActionEvent event) {
      }

      protected void onTest_FieldValueWrapAction(Component c, ActionEvent event) {
      }

      protected void onReusableContainer_Button6Action(Component c, ActionEvent event) {
      }

      protected void onReusableContainer_Button5Action(Component c, ActionEvent event) {
      }

      protected void onReusableContainer_Button4Action(Component c, ActionEvent event) {
      }

      protected void onReusableContainer_Button11Action(Component c, ActionEvent event) {
      }

      protected void onReusableContainer_Button3Action(Component c, ActionEvent event) {
      }

      protected void onReusableContainer_Button2Action(Component c, ActionEvent event) {
      }

      protected void onSettingRenderer_SettingEditButtonAction(Component c, ActionEvent event) {
      }

      protected void onWorkSlotRendere_WorkSlotEditAction(Component c, ActionEvent event) {
      }

      protected void onWorkSlotRendere_WorkSlotStatusButtonAction(Component c, ActionEvent event) {
      }

      protected void onItemScreen_TextFieldAction(Component c, ActionEvent event) {
      }

      protected void onItemScreen_ContainerListAction(Component c, ActionEvent event) {
      }

      protected void onPrioritySorter_Button1Action(Component c, ActionEvent event) {
      }

      protected void onPrioritySorter_Button2Action(Component c, ActionEvent event) {
      }

      protected void onPrioritySorter_Button3Action(Component c, ActionEvent event) {
      }

      protected void onPrioritySorter_ButtonAction(Component c, ActionEvent event) {
      }

      protected void onPrioritySorter_ListAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemEditButtonAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemStatusAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemSubtasksButton1Action(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemDescriptionAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemDueDateAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemAlarmIconAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemCategoriesAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemPriorityAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemRemainingAction(Component c, ActionEvent event) {
      }

      protected void onItemRenderer_ItemFinishDateAction(Component c, ActionEvent event) {
      }

      protected void onLabelAndFieldCont_FieldHelpAction(Component c, ActionEvent event) {
      }

      protected void onGUI2_TextAreaAction(Component c, ActionEvent event) {
      }

      protected void onGUI2_Button6Action(Component c, ActionEvent event) {
      }

      protected void onGUI2_Button5Action(Component c, ActionEvent event) {
      }

      protected void onGUI2_Button4Action(Component c, ActionEvent event) {
      }

      protected void onGUI2_Button11Action(Component c, ActionEvent event) {
      }

      protected void onGUI2_Button3Action(Component c, ActionEvent event) {
      }

      protected void onGUI2_Button2Action(Component c, ActionEvent event) {
      }

      protected void onGUI2_Button1Action(Component c, ActionEvent event) {
      }

      protected void onGUI2_ButtonAction(Component c, ActionEvent event) {
      }

      protected void onGUI1_RadioButton2Action(Component c, ActionEvent event) {
      }

      protected void onGUI1_CheckBoxAction(Component c, ActionEvent event) {
      }

      protected void onGUI1_ButtonAction(Component c, ActionEvent event) {
      }

      protected void onGUI1_ComboBoxAction(Component c, ActionEvent event) {
      }

      protected void onGUI1_TextFieldAction(Component c, ActionEvent event) {
      }

      protected void onGUI1_Button1Action(Component c, ActionEvent event) {
      }

      protected void onHighLowToggle_RadioButton1Action(Component c, ActionEvent event) {
      }

      protected void onHighLowToggle_RadioButtonAction(Component c, ActionEvent event) {
      }

      protected void onHighLowToggle_RadioButton2Action(Component c, ActionEvent event) {
      }

      protected void onTestScreen_TestButtonAction(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button5Action(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button6Action(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button8Action(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button7Action(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button10Action(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button9Action(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button4Action(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button3Action(Component c, ActionEvent event) {
      }

      protected void onPriorityPicker_Button2Action(Component c, ActionEvent event) {
      }

      protected void onSelectRendererWrapper_SelectStateButtonAction(Component c, ActionEvent event) {
      }

      protected void onItemListFlattenRenderer_ItemListFlattenNumberTasksButtonAction(Component c, ActionEvent event) {
      }

      protected void onItemListFlattenRenderer_ItemListFlattenEditButtonAction(Component c, ActionEvent event) {
      }

      protected void onTimerLineInListView_Button5Action(Component c, ActionEvent event) {
      }

      protected void onTimerLineInListView_TextFieldAction(Component c, ActionEvent event) {
      }

      protected void onTimerLineInListView_Button4Action(Component c, ActionEvent event) {
      }

      protected void onNamedListRenderer_NamedListNumberTasksButtonAction(Component c, ActionEvent event) {
      }

      protected void onNamedListRenderer_NamedListEditButtonAction(Component c, ActionEvent event) {
      }

      protected void onViewRenderer_ViewEditButtonAction(Component c, ActionEvent event) {
      }

      protected void onTimerContainer_Button2Action(Component c, ActionEvent event) {
      }

      protected void onTimerContainer_TimerTaskStatusButtonAction(Component c, ActionEvent event) {
      }

      protected void onTimerContainer_TimerTaskEditAction(Component c, ActionEvent event) {
      }

      protected void onTimerContainer_TimerTaskTitleAction(Component c, ActionEvent event) {
      }

      protected void onTimerContainer_TimerTextCommentAction(Component c, ActionEvent event) {
      }

}
