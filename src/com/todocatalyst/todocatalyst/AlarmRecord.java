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
    Date alarmTime;
    AlarmType type;
    
    AlarmRecord(Date alarmTime,AlarmType type) {
        this.alarmTime=alarmTime;
        this.type=type;
    }
    
     @Override
    public String toString() {
        return type+"/"+MyDate.formatDateTimeNew(alarmTime);
    }
}
