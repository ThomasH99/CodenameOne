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
import java.util.Objects;

/**
 *
 * @author Thomas
 */
public class NotificationShadow implements Externalizable {

    final static String CLASS_NAME_NOTIFICATION_SHADOW = "notificationShadow";

    String notificationId;
    Date alarmTime;
//        Date repeatTime;
//        Date snoozeTime;
    AlarmType type;
//        boolean snooze = false; //set to true when an alarm is snoozed

    public String toString() {
//            return "Type:"+type+" alarm:"+MyDate.formatDateTimeNew(alarmTime)+" NotifId:"+notificationId;
//            return type+"/"+MyDate.formatDateTimeNew(alarmTime)+"/ ["+DAO.getInstance().fetchFromCacheOnly(AlarmType.getObjectIdStrWithoutTypeStr(notificationId))+"]";
        return type + "/" + (alarmTime != null ? MyDate.formatDateNew(alarmTime, false, true, true, false, false, true)                : "<null>") 
                + "/ [" + (AlarmType.getGuidStrWithoutTypeStr(notificationId) != null ? AlarmType.getGuidStrWithoutTypeStr(notificationId) : "<noGuid>") + "]";
    }

    public String toStringXX() {
        return "NotifShadow id=" + notificationId + ", alarmTime=" + MyDate.formatDateNew(alarmTime) + ", type=" + type;
    }

    public NotificationShadow() {

    }

//        NotificationShadow(String notificationId, Date time, AlarmType type) {
//            this.notificationId = notificationId;
//            this.alarmTime = time;
//            this.type = type;
//        }
    /**
     * create
     *
     * @param type
     * @param guid
     * @param time
     */
    NotificationShadow(AlarmType type, String guid, Date time) {
        this.type = type;
        this.notificationId = type.addTypeStrToStr(guid);
        this.alarmTime = time;
    }

    NotificationShadow(AlarmRecord alarmRecord) {
        this(alarmRecord.type, alarmRecord.item.getGuid(), alarmRecord.alarmTime);
    }

    String getGuidStr() {
        return AlarmType.getGuidStrWithoutTypeStr(notificationId);
    }

    AlarmType getAlarmType() {
        return AlarmType.getAlarmTypeFromNotifStr(notificationId);
    }

    /**
     * returns true if this NotisifcationShadow is for the objectId
     *
     * @param guid
     * @return
     */
    boolean isForItem(String guid) {
        return notificationId.indexOf(guid) == 0;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void externalize(DataOutputStream out) throws IOException {
        writeObject(notificationId, out);
        writeObject(alarmTime, out);
        out.writeInt(type.ordinal());
    }

    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        notificationId = (String) readObject(in);
        alarmTime = (Date) readObject(in);
        type = AlarmType.values()[in.readInt()];
    }

    @Override
    public String getObjectId() {
//            return CLASS_NAME_ALARM_DATA;
        return CLASS_NAME_NOTIFICATION_SHADOW;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.notificationId);
        hash = 17 * hash + Objects.hashCode(this.alarmTime);
        hash = 17 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NotificationShadow other = (NotificationShadow) obj;
        if (!Objects.equals(this.notificationId, other.notificationId)) {
            return false;
        }
        if (!Objects.equals(this.alarmTime, other.alarmTime)) {
            return false;
        }
//        if (!Objects.equals(this.type, other.type)) {
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

}
