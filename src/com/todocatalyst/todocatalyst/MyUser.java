/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.parse4cn1.ParseUser;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thomashjelm
 */
public class MyUser extends ParseUser {

    final static String PARSE_USER_DEVICES = "userDevices";
    final static String PARSE_ACTIVE_DEVICES = "activeDevices";

    public String getDeviceId() {
        String uniqueId = Display.getInstance().getUdid();
        return uniqueId;
    }

    public boolean isAnotherDeviceActive() {
        return getActiveDevices().isEmpty() || !(getActiveDevices().size() == 1 && getActiveDevices().contains(getDeviceId()));
    }

    public List<String> getActiveDevices() {
        List<String> activeDevices = (List<String>) get(PARSE_ACTIVE_DEVICES);
        return activeDevices != null ? activeDevices : new ArrayList();
    }

    public void addDeviceActive(String deviceId) {

    }

    public void setDeviceActive(String deviceId) {
        if (MyPrefs.onlyAllowOneDeviceToEdit.getBoolean() && isAnotherDeviceActive()) {
            Dialog.show("","Another device is already editing, please exit TodoCatalyst** and then restart", "OK", null);
        } else if (!getActiveDevices().contains(deviceId)) {
            addDeviceActive(deviceId);
        }
    }

}
