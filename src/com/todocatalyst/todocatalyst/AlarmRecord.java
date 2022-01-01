/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Date;

/**
 *
 * @author Thomas
 */
public class AlarmRecord {
//        String itemGuid;

    Item item;
    Date alarmTime;
    AlarmType type;

    AlarmRecord(Date alarmTime, AlarmType type, Item item) {
        this.item = item;
        this.alarmTime = alarmTime;
        this.type = type;
    }
    
    String getNotificationId() {
        return type.addTypeStrToStr(item.getGuid());
    }

    @Override
    public String toString() {
        return type + "/" + MyDate.formatDateTimeNew(alarmTime);
    }
}
