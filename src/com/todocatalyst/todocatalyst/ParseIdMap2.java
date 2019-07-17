/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import static com.todocatalyst.todocatalyst.MyForm.REPEAT_RULE_KEY;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author thomashjelm
 */
public class ParseIdMap2 {//extends HashMap {

    private HashMap<Object, Runnable> parseIdMap2 = new HashMap<Object, Runnable>();
    List<Runnable> runnables = new ArrayList<Runnable>();

    ParseIdMap2() {

    }
    
    public Object put(Object key, Runnable value) {
        if (Config.TEST) ASSERT.that(value!=null, "SaveEditedValuesLocally put "+key+" with null value - missing objectIdP??");
            Object previousValue = parseIdMap2.put(key, value);
            return previousValue;
    }

    public Runnable get(Object key) {
            return parseIdMap2.get(key);
    }

    public Runnable remove(Object key) {
            return parseIdMap2.remove(key);
    }


    void addPostSave(Runnable r) {
        runnables.add(r);
    }

    void update() {
        Runnable repeatRule = parseIdMap2.remove(REPEAT_RULE_KEY); //set a repeatRule aside for execution last (after restoring all fields)

        for (Object parseId : parseIdMap2.keySet()) {
            parseIdMap2.get(parseId).run();
        }
        if (repeatRule != null) {
            repeatRule.run();
        }
    }
    
    public Set<Object> keySet(){
        return parseIdMap2.keySet();
    }

    void runPostSaveRunnables() {
        for (Runnable r : runnables) {
            r.run();
        }
    }
    
    void parseIdMapReset(){
        parseIdMap2.clear();
        runnables.clear();
    }

}
