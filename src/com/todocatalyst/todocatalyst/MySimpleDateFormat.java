/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.l10n.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author thomashjelm
 */
    class MySimpleDateFormat extends SimpleDateFormat {
        
        String showWhenUndefined;

        /**
         * Construct a SimpleDateFormat with a given pattern.
         *
         * @param pattern
         */
        public MySimpleDateFormat(String pattern, String showWhenUndefined) {
            super(pattern);
            this.showWhenUndefined = showWhenUndefined;
        }
        
        public String format(Date source) {
            if (source.getTime() == 0) {
                return showWhenUndefined;
            } else {
                return super.format(source);
            }
        }
    }

