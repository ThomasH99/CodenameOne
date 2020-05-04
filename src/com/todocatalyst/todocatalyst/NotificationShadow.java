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
            return type+"/"+MyDate.formatDateTimeNew(alarmTime)+"/ ["+DAO.getInstance().fetchFromCacheOnly(AlarmType.getObjectIdStrWithoutTypeStr(notificationId))+"]";
        }
        
        public String toStringXX() {
            return "NotifShadow id="+notificationId+", alarmTime="+MyDate.formatDateNew(alarmTime)+", type="+type;
        }
        
        public NotificationShadow() {
            
        }
        
        NotificationShadow(String notificationId, Date time, AlarmType type) {
            this.notificationId = notificationId;
            this.alarmTime = time;
            this.type = type;
        }

        /**
         * create
         *
         * @param type
         * @param objectId
         * @param time
         */
        NotificationShadow(AlarmType type, String objectId, Date time) {
            this.type = type;
            this.notificationId = type.addTypeStrToStr(objectId);
            this.alarmTime = time;
        }
        

        String getObjectIdStr() {
            return AlarmType.getObjectIdStrWithoutTypeStr(notificationId);
        }
        
        AlarmType getAlarmType() {
            return AlarmType.getTypeContainedInStr(notificationId);
        }
        
        /**
         * returns true if this NotisifcationShadow is for the objectId
         * @param objectId
         * @return 
         */
        boolean isForItem(String objectId) {
            return notificationId.indexOf(objectId) == 0;
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
    }

