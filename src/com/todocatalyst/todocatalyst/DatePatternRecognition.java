/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Date;

/**
 *
 * @author thomashjelm
 */
public class DatePatternRecognition {
    //TODO find regular expressions (already localized) for most patterns?
    //TODO check locale and check that localized patterns exist
    //TODO recognize 'modifiers': nothing=> due date; "wait/w[date]", "reminder/R/r[date]"
    //TODO recngnize relative dates, eg alarms could be R-1d to set an alarm 24h before dueDate
    //TODO (option to) start all patterns with a special character to avoid misinterpretations, e.g. sth easily types on virtual keybaords so NOT '#' but maybe " .r" or " :r"

    Date parseDateN(String fullStr) {
        
//        if (sub)
// "in 12 days", "in 2( )hours/h"
// "12/10", "31-1-2020",...
//"tomorrow", "today", 
//"next [Mon/Tues/...]day", 
//"in [one/1/two/2/...] week(s)",
//in one month, 
        return null;
    }
}
