/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import static com.codename1.io.Util.readObject;
import static com.codename1.io.Util.writeObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author Thomas
 */
public class ExpiredAlarm implements Externalizable {

    final static String CLASS_NAME_EXPIRED_ALARM = "expiredAlarm";

    String objectId;
    Date alarmTime;
//        Date repeatTime;
//        Date snoozeTime;
    AlarmType type;

    ExpiredAlarm(String objectId, Date alarmTime, AlarmType type) {
        ASSERT.that(objectId!=null, "trying to set alarm with objectId==null");
        this.objectId = objectId;
        ASSERT.that(type==AlarmType.notification||type==AlarmType.waiting);
        this.alarmTime = alarmTime;
        this.type = type;
    }

    ExpiredAlarm(NotificationShadow notif) {
        ASSERT.that(notif.getObjectIdStr()!=null, "trying to set alarm with objectId==null, notif="+notif);
        this.objectId = notif.getObjectIdStr();
        this.alarmTime = notif.alarmTime;
        this.type = notif.type;
    }
    
    public ExpiredAlarm() {
    }
    
    @Override
    public String toString() {
        return type+"/"+MyDate.formatDateTimeNew(alarmTime)+"/"+((Item)DAO.getInstance().fetchItem(objectId)).getText()+"/"+objectId ;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void externalize(DataOutputStream out) throws IOException {
        writeObject(objectId, out);
        writeObject(alarmTime, out);
        out.writeInt(type.ordinal());
    }

    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        objectId = (String) readObject(in);
        alarmTime = (Date) readObject(in);
        type = AlarmType.values()[in.readInt()];
    }

    @Override
    public String getObjectId() {
//            return CLASS_NAME_ALARM_DATA;
        return CLASS_NAME_EXPIRED_ALARM;
    }

}
