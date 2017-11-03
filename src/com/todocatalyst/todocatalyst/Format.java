/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.plaf.UIManager;

/**
 * http://jcs.mobile-utopia.com/jcs/4521_MessageFormat.java Improved to allow
 * explanatory comments inside curly brackets, e.g. "Text {0,Done} is not
 * {1,Finished}."
 *
 * @author Thomas
 */
/*
 *
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */
//package com.sun.j2me.global;
/**
 * <code>MessageFormat</code> provides means to produce concatenated messages in
 * language-neutral way. Use this to construct messages displayed for end users.
 * <code>MessageFormat</code> formats message with variable number of
 * parameters. Pattern string contains placeholders of the form {0} .. {nn}.
 * Placeholders are replaced by parameters. Escaped "{{" can be used to get '{'
 * in formatted message.
 * <p>
 *
 * <code>MessageFormat</code> takes a set of strings, and inserts the them into
 * the pattern at the appropriate places.
 * <p>
 *
 * <pre>
 * String output = MessageFormat.format("Message {1} for formatting {2}",
 * 				        new String[]{"first_arg",
 *                                      "second_arg"});
 * </pre>
 *
 */
public class Format {

    /**
     * Value for the left parenthesis char
     */
    private final static char LEFT_PARENTHESIS = '{';
    private final static char LEFT_LABEL = '[';
    /**
     * Value for the right parenthesis char
     */
    private final static char RIGHT_PARENTHESIS = '}';
    private final static char RIGHT_LABEL = ']';

    /**
     * Replace placeholders in template with parameters.
     *
     * @param message the template with placeholders. Placeholders start with
     * "{0}"
     * @param params array of parameters
     * @return buffer containing formatted message
     */
//    public static String format(String message, String[] params) {
    public static String f(String message, String... params) {
        if (message == null || params == null) {
            throw new NullPointerException("Template or parameter array is null.");
        }
        boolean inside = false;
        boolean insideLabel = false;
        boolean escaped = false;
        boolean escapedLabel = false;
        StringBuffer result = new StringBuffer();
        StringBuffer placeholder = null;
        StringBuffer placeholderLabel = null;
        char lookingFor = LEFT_PARENTHESIS;
        char lookingForLabel = LEFT_LABEL;
        char c;

        for (int i = 0; i < message.length(); i++) {
            c = message.charAt(i);
            if (c == lookingFor || c == lookingForLabel) {
                if (escaped || escapedLabel) {
                    result.append(c);
                    escaped = false;
                    escapedLabel = false;
                    continue;
                }
//                if (escapedLabel) {
//                    result.append(c);
//                    escapedLabel = false;
//                    continue;
//                }
                if (c == LEFT_PARENTHESIS) {
                    // look ahead for escaped parenthesis
                    if ((i + 1) < message.length() && message.charAt(i + 1) == LEFT_PARENTHESIS) {
                        escaped = true;
                    } else {
                        inside = true;
                        lookingFor = RIGHT_PARENTHESIS;
                        placeholder = new StringBuffer();
                    }
                } else if (c == LEFT_LABEL) {
                    // look ahead for escaped parenthesis
                    if ((i + 1) < message.length() && message.charAt(i + 1) == LEFT_LABEL) {
                        escapedLabel = true;
                    } else {
                        insideLabel = true;
                        lookingForLabel = RIGHT_LABEL;
                        placeholderLabel = new StringBuffer();
                    }
                } else {
                    // placeholder finished get parameter
                    if (inside) {
                        try {
                            if (inside && placeholder.length() > 2) {
                                throw new IllegalArgumentException(
                                        "Illegal placeholder.");
                            }
                            int index = Integer.parseInt(placeholder.toString());
                            result.append(params[index]);
                        } catch (ArrayIndexOutOfBoundsException ie) {
                            throw new IllegalArgumentException(
                                    "Illegal number of parameters.");
                        }
                    } else if (insideLabel) {
                        result.append(UIManager.getInstance().localize(placeholderLabel.toString(), placeholderLabel.toString())); //localize a standard Label, e.g. [DONE]
                    }
                    inside = false;
                    insideLabel = false;
                    lookingFor = LEFT_PARENTHESIS;
                    lookingForLabel = LEFT_LABEL;
                }
            } else {
                if (inside) {
                    if (c >= '0' && c <= '9') { //only append numbers, ignore other chars inside the paraenthesis
                        placeholder.append(c);
                    }
                } else if (insideLabel) { //only append numbers, ignore other chars inside the paraenthesis
                } else {
                    result.append(c);
                }
            }
        }
        // for
        return result.toString();
    }

    public static String f_OLD(String message, String... params) {
        if (message == null || params == null) {
            throw new NullPointerException("Template or parameter array is null.");
        }
        boolean inside = false;
        boolean escaped = false;
        StringBuffer result = new StringBuffer();
        StringBuffer placeholder = null;
        char lookingFor = LEFT_PARENTHESIS;
        char c;

        for (int i = 0; i < message.length(); i++) {
            c = message.charAt(i);
            if (c == lookingFor) {
                if (escaped) {
                    result.append(c);
                    escaped = false;
                    continue;
                }
                if (c == LEFT_PARENTHESIS) {
                    // look ahead for escaped parenthesis
                    if ((i + 1) < message.length()
                            && message.charAt(i + 1) == LEFT_PARENTHESIS) {
                        escaped = true;
                    } else {
                        inside = true;
                        lookingFor = RIGHT_PARENTHESIS;
                        placeholder = new StringBuffer();
                    }
                } else {
                    inside = false;
                    lookingFor = LEFT_PARENTHESIS;
                    // placeholder finished get parameter
                    try {
                        if (placeholder.length() > 2) {
                            throw new IllegalArgumentException(
                                    "Illegal placeholder.");
                        }
                        int index = Integer.parseInt(placeholder.toString());
                        result.append(params[index]);
                    } catch (ArrayIndexOutOfBoundsException ie) {
                        throw new IllegalArgumentException(
                                "Illegal number of parameters.");
                    }
                }
            } else {
                if (inside) {
                    placeholder.append(c);
                } else {
                    result.append(c);
                }
            }
        }
        // for
        return result.toString();
    }
}
