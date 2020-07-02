package com.codename1.ui.spinner;

import com.codename1.components.SpanLabel;
import com.codename1.components.Switch;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.operation.SetFieldOperation;
import java.util.Date;

/**
 *
 */
public class PickerDialog {

    Dialog dlg;
    Command doneCmd;
    int type;
    DateTimeSpinner3D dateTimeSpinner;
    DateSpinner3D dateSpinner;
    DurationSpinner3D durationSpinner3D;

    public static String DONE_BUTTON_TEXT = "Done";
    public static String CANCEL_BUTTON_TEXT = "Cancel";

    public PickerDialog(String title, String text, Object value,  String doneText, String cancelText,int type) {
        this.type = type;

        dlg = new Dialog();
        dlg.setDialogUIID("PickerDialog");
        dlg.setTitle(title);
        dlg.setLayout(BorderLayout.center());

//        spinner3D.getPreferredSize();
        Container cont = new Container(BoxLayout.y());
        SpanLabel textSpanLabel = new SpanLabel(text);
        textSpanLabel.setTextUIID("PickerDialogText");
        cont.add(textSpanLabel);
        switch (this.type) {
            case Display.PICKER_TYPE_DATE_AND_TIME:
                dateTimeSpinner = new DateTimeSpinner3D();
                dateTimeSpinner.setValue(value);
                cont.add(dateTimeSpinner);
                break;
            case Display.PICKER_TYPE_DATE:
                dateSpinner = new DateSpinner3D();
                dateSpinner.setValue(value);
                cont.add(dateSpinner);
                break;
            case Display.PICKER_TYPE_DURATION:
                durationSpinner3D = new DurationSpinner3D(DurationSpinner3D.FIELD_HOUR | DurationSpinner3D.FIELD_MINUTE);
                durationSpinner3D.setValue(value);
                cont.add(durationSpinner3D);
                break;
        }

        doneCmd = Command.create(doneText, null, (e) -> {
            dlg.dispose();
        });
        Button doneButton = new Button(doneCmd);
        Container buttonBar;
        if (cancelText != null && !cancelText.isEmpty()) {
            Button cancelButton = new Button(Command.create(cancelText, null, (e) -> {
                dlg.dispose();
            }));
            buttonBar = BorderLayout.centerEastWest(null, doneButton, cancelButton);
        } else {
            buttonBar = BorderLayout.centerEastWest(doneButton, null, null);
        }

        dlg.getContentPane().add(BorderLayout.SOUTH, buttonBar);
        dlg.getContentPane().add(BorderLayout.CENTER, cont);

    }
    
        /**
     * return the value of the picker of the defined type (Date or
     *
     * @return
     */
    public Object show() {
        Command cmd = dlg.showDialog();
        if (cmd == doneCmd) {
            switch (type) {
                case Display.PICKER_TYPE_DATE_AND_TIME:
                    return dateTimeSpinner.getValue();
                case Display.PICKER_TYPE_DATE:
                    return dateSpinner.getValue();
                case Display.PICKER_TYPE_DURATION:
                    return durationSpinner3D.getValue();
            }
        }
        return null;
    }


    public interface SetDate {

        void set(Date date);
    }

    public interface GetDate {

        Date get(Date date);
    }

    /**
     *
     * @param title
     * @param text
     * @param waitingDateOnOffText
     * @param waitingDate
     * @param defaultWaitingDate used to set waitingDate in spinner when
     * switched on if no date was previously defined
     * @param waitingAlarmOnOffText
     * @param waitingAlarmDate
     * @param setWaitingDate
     * @param setWaitingAlarm
     * @param defaultWaitingAlarmDate used to generate a default alarm date
     * based on the edited waitingDate
     */
    public PickerDialog(String title, String text,
            String waitingDateOnOffText, Date waitingDate, Date defaultWaitingDate,
            String waitingAlarmOnOffText, Date waitingAlarmDate,
            SetDate setWaitingDate, SetDate setWaitingAlarm, GetDate defaultWaitingAlarmDate) {

        dlg = new Dialog();
        dlg.setDialogUIID("PickerDialog");
        dlg.setTitle(title);
        dlg.setLayout(BorderLayout.center());
        dlg.setScrollableY(true); //scrollable in case of small screens

//        spinner3D.getPreferredSize();
        Container cont = new Container(BoxLayout.y());
        SpanLabel textSpanLabel = new SpanLabel(text);
        textSpanLabel.setTextUIID("PickerDialogText");
        cont.add(textSpanLabel);

        DateSpinner3D waitingDateSpinner = new DateSpinner3D();
        waitingDateSpinner.setValue(waitingDate);
        waitingDateSpinner.setHidden(waitingDate.getTime()==0); //hide by default unless a value is defined

        Switch waitingDateSwitch = new Switch();
        waitingDateSwitch.setValue(waitingDate.getTime() != 0); //shown by default if a value is already defined, otherwise hidden by default
        waitingDateSwitch.addActionListener((e) -> {
//            waitingDateSwitch.setValue(!waitingDateSwitch.isValue());
//            if(waitingDateSwitch.isValue()) 
            waitingDateSpinner.setHidden(waitingDateSwitch.isOff());
            long test = ((Date)waitingDateSpinner.getValue()).getTime();
            if (waitingDateSwitch.isOn() && ((Date)waitingDateSpinner.getValue()).getTime() == 0) { //if WatitingDate spinner has a date, use it to calculate appropriate alarmdate
                waitingDateSpinner.setValue(defaultWaitingDate);
            }
////            dlg.getParent().animateHierarchy(300); //getParent==NPE
//            dlg.animateHierarchy(300);
//            dlg.revalidateWithAnimationSafety();
            dlg.growOrShrink();
        });

        cont.add(BorderLayout.centerEastWest(null, waitingDateSwitch, new SpanLabel(waitingDateOnOffText)));
        cont.add(waitingDateSpinner);

        DateTimeSpinner3D waitingAlarmSpinner = new DateTimeSpinner3D();
        waitingAlarmSpinner.setValue(waitingAlarmDate);
        waitingAlarmSpinner.setHidden(waitingAlarmDate.getTime()==0); //hide by default, unless a value is already defined

        Switch waitingAlarmSwitch = new Switch();
//        waitingAlarmSwitch.setValue(false);
        waitingAlarmSwitch.setValue(waitingAlarmDate.getTime() != 0);
        waitingAlarmSwitch.addActionListener((e) -> {
            waitingAlarmSpinner.setHidden(waitingAlarmSwitch.isOff());
            long test = ((Date)waitingAlarmSpinner.getValue()).getTime();
//            if (waitingDateSwitch.isOn() && ((Date) watingDateSpinner.getValue()).getTime() == 0) { //if WatitingDate spinner has a date, use it to calculate appropriate alarmdate
            if (waitingDateSwitch.isOn() && ((Date)waitingAlarmSpinner.getValue()).getTime() == 0) { //if WatitingDate spinner has a date, use it to calculate appropriate alarmdate
                waitingAlarmSpinner.setValue(defaultWaitingAlarmDate.get(((Date) waitingDateSpinner.getValue())));
            }
//            dlg.animateHierarchy(300);
            dlg.growOrShrink();
        });

        cont.add(BorderLayout.centerEastWest(null, waitingAlarmSwitch, new SpanLabel(waitingAlarmOnOffText)));
        cont.add(waitingAlarmSpinner);

        Command doneCmd = Command.create(DONE_BUTTON_TEXT, null, (e) -> {
            if (waitingDateSwitch.isOn()) {
                setWaitingDate.set((Date) waitingDateSpinner.getValue());
            }
            if (waitingAlarmSwitch.isOn()) {
                setWaitingAlarm.set((Date) waitingAlarmSpinner.getValue());
            }
            dlg.dispose();
        });
        Button doneButton = new Button(doneCmd);
        Button cancelButton = new Button(Command.create(CANCEL_BUTTON_TEXT, null, (e) -> dlg.dispose()));

        Container buttonBar;
        buttonBar = BorderLayout.centerEastWest(null, doneButton, cancelButton);
        dlg.getContentPane().add(BorderLayout.SOUTH, buttonBar);
        dlg.getContentPane().add(BorderLayout.CENTER, cont);
    }

    public PickerDialog(String title, String text, Date value) {
//        this(title, text, value, "Done", "Cancel", Display.PICKER_TYPE_DATE_AND_TIME);
        this(title, text, value, DONE_BUTTON_TEXT, CANCEL_BUTTON_TEXT, Display.PICKER_TYPE_DATE_AND_TIME);
    }

    public PickerDialog(String title, String text, long value) {
        this(title, text, value, DONE_BUTTON_TEXT, CANCEL_BUTTON_TEXT, Display.PICKER_TYPE_DURATION);
    }

}
