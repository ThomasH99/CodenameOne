/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.OnOffSwitch;
import com.codename1.components.Switch;
import java.util.Map;

/**
 *
 * @author Thomas
 */
//class MyOnOffSwitch extends OnOffSwitch {
class MyOnOffSwitch extends Switch {

//            String title;
//            String parseId;
    MyOnOffSwitch(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetBoolean get, MyForm.PutBoolean set) {
        super();
//        getAllStyles().setPaddingRight(6);
//            setUIID("Button");
//            setUIID("MyOnOffSwitch");
//                this.title = title;
//                this.parseId = parseId;
//            this.setValue(parseObject.getBoolean(parseId));
//                Boolean b = parseObject.getBoolean(parseId);
        Boolean b = get.get();
        if (b != null) {
            this.setValue(b);
        }
//                parseIdMap.put(parseId, () -> parseObject.put(parseId, this.isValue()));
        if (parseIdMap != null) {
            parseIdMap.put(this, () -> set.accept(this.isValue()));
        }
    }

    MyOnOffSwitch() {
        super();
    }

//            MyOnOffSwitch(String title, String onString, String offString, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//    MyOnOffSwitch(String offString, String onString, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetBoolean get, MyForm.PutBoolean set) {
//        this(parseIdMap, get, set);
//        this.setOn(onString);
//        this.setOff(offString);
//    }

};
