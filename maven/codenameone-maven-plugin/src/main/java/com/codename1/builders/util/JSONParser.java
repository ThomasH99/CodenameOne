/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores
 * CA 94065 USA or visit www.oracle.com if you need additional information or
 * have any questions.
 */


package com.codename1.builders.util;


import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;


/**
 * This is a copy of the com.codename1.io.JSONParser for use in the BuildDaemon project
 * so that we don't need to add the CodenameOne.jar as a dependency on the OfflineBuilder.
 *
 * @author Shai Almog
 */
public class JSONParser implements JSONParseCallback {
    
    private static class Log {
        static void e(Throwable t) {
            t.printStackTrace();
        }
        static void p(String str) {
            System.out.println(str);
        }
    }

    /**
     * Indicates that the parser will generate long objects and not just doubles for numeric values
     * @return the useLongsDefault
     */
    public static boolean isUseLongs() {
        return useLongsDefault;
    }

    /**
     * Indicates that the parser will generate long objects and not just doubles for numeric values
     * @param aUseLongsDefault the useLongsDefault to set
     */
    public static void setUseLongs(boolean aUseLongsDefault) {
        useLongsDefault = aUseLongsDefault;
    }

    /**
     * Indicates that the parser will include null values in the parsed output
     * @return the includeNullsDefault
     */
    public static boolean isIncludeNulls() {
        return includeNullsDefault;
    }

    /**
     * Indicates that the parser will include null values in the parsed output
     * @param aIncludeNullsDefault the includeNullsDefault to set
     */
    public static void setIncludeNulls(boolean aIncludeNullsDefault) {
        includeNullsDefault = aIncludeNullsDefault;
    }

    /**
     * Indicates that the parser will generate Boolean objects and not just Strings for boolean values
     * @return the useBooleanDefault
     */
    public static boolean isUseBoolean() {
        return useBooleanDefault;
    }

    /**
     * Indicates that the parser will generate Boolean objects and not just Strings for boolean values
     * @param aUseBooleanDefault the useBooleanDefault to set
     */
    public static void setUseBoolean(boolean aUseBooleanDefault) {
        useBooleanDefault = aUseBooleanDefault;
    }

    static class ReaderClass {
        char[] buffer;
        int buffOffset;
        int buffSize = -1;
        int read(Reader is) throws IOException {
            int c = -1;
            if(buffer == null) {
                buffer = new char[8192];
            }

            if(buffSize < 0 || buffOffset >= buffSize) {
                buffSize = is.read(buffer, 0, buffer.length);
                if(buffSize < 0) {
                    return -1;
                }
                buffOffset = 0;
            }
            c = buffer[buffOffset];
            buffOffset ++;

            return c;
        }

    }

    private static boolean useLongsDefault;
    
    /**
     * Indicates that the parser will generate Boolean objects and not just Strings for boolean values
     */
    private static boolean useBooleanDefault;
    private static boolean includeNullsDefault;
    private boolean modern;
    private Map<String, Object> state;
    private java.util.List<Object> parseStack;
    private String currentKey;
    
    /**
     * If strict is set to false, then the parser will attempt to sanitize the JSON
     * input before parsing.  I.e. it will accept invalid JSON, such as unquoted keys, etc..
     */
    private boolean strict = true;
    static class KeyStack extends Vector {
		protected String peek() {
			return (String)elementAt(0);
		}

		protected void push(String key) {
			insertElementAt(key, 0);
		}
		
		protected String pop() {
			if (isEmpty()) {
				return null;
			}
			String key = peek();
			removeElementAt(0);
			return key;
		}
	};
    
    /**
     * Static method! Parses the given input stream and fires the data into the given callback.
     *
     * @param i the reader
     * @param callback a generic callback to receive the parse events
     * @throws IOException if thrown by the stream
     */
    public static void parse(Reader i, JSONParseCallback callback) throws IOException {
        boolean quoteMode = false;
        ReaderClass rc = new ReaderClass();
        rc.buffOffset = 0;
        rc.buffSize = -1;
        int row = 1;
        int column = 1;
        StringBuilder currentToken = new StringBuilder();
        KeyStack blocks = new KeyStack();
        String currentBlock = "";
        String lastKey = null;
        try {
            while (callback.isAlive()) {
                int currentChar = rc.read(i);
                if (currentChar < 0) {
                    return;
                }
                char c = (char) currentChar;
                if(c == '\n') { 
                    row++;
                    column = 0;
                } else {
                    column++;
                }
                
                if (quoteMode) {
                    switch (c) {
                        case '"':
                            String v = currentToken.toString();
                            callback.stringToken(v);
                            if (lastKey != null) {
                                callback.keyValue(lastKey, v);
                                lastKey = null;
                            } else {
                                lastKey = v;
                            }
                            currentToken.setLength(0);
                            quoteMode = false;
                            continue;
                        case '\\':
                            c = (char) rc.read(i);
                            if (c == 'u') {
                                String unicode = "" + ((char) rc.read(i)) + ((char) rc.read(i)) + ((char) rc.read(i)) + ((char) rc.read(i));
                                try {
                                    c = (char) Integer.parseInt(unicode, 16);
                                } catch (NumberFormatException err) {
                                    // problem in parsing the u notation!
                                    Log.e(err);
                                    System.out.println("Error in parsing \\u" + unicode);
                                }
                            } else {
                                switch(c) {
                                    case 'n':
                                        currentToken.append('\n');
                                        continue;
                                    case 't':
                                        currentToken.append('\t');
                                        continue;
                                    case 'r':
                                        currentToken.append('\r');
                                        continue;
                                }
                            }
                            currentToken.append(c);
                            continue;
                    }
                    currentToken.append(c);
                } else {
                    switch (c) {
                        case 'n':
                            // check for null
                            char u = (char) rc.read(i);
                            char l = (char) rc.read(i);
                            char l2 = (char) rc.read(i);
                            if (u == 'u' && l == 'l' && l2 == 'l') {
                                // this is null
                                callback.stringToken(null);
                                if (lastKey != null) {
                                    callback.keyValue(lastKey, null);
                                    lastKey = null;
                                }
                            } else {
                                // parsing error....
                                Log.p("Expected null for key value while parsing JSON token at row: " + row + " column: " + column + " buffer: " + currentToken.toString());
                            }

                            continue;
                        case 't':
                            // check for true
                            char a1 = (char) rc.read(i);
                            char a2 = (char) rc.read(i);
                            char a3 = (char) rc.read(i);
                            if (a1 == 'r' && a2 == 'u' && a3 == 'e') {
                                if(useBooleanDefault) {
                                    callback.booleanToken(true);
                                } else {
                                    callback.stringToken("true");
                                }
                                if (lastKey != null) {
                                    callback.keyValue(lastKey, "true");
                                    lastKey = null;
                                }
                            } else {
                                // parsing error....
                                Log.p("Expected true for key value while parsing JSON token at row: " + row + " column: " + column + " buffer: " + currentToken.toString());
                            }

                            continue;
                        case 'f':
                            // this can either be the start of "false" or the end of a
                            // fraction number...
                            if (currentToken.length() > 0) {
                                currentToken.append('f');
                                continue;
                            }
                            // check for false
                            char b1 = (char) rc.read(i);
                            char b2 = (char) rc.read(i);
                            char b3 = (char) rc.read(i);
                            char b4 = (char) rc.read(i);
                            if (b1 == 'a' && b2 == 'l' && b3 == 's' && b4 == 'e') {
                                if(useBooleanDefault) {
                                    callback.booleanToken(false);
                                } else {
                                    callback.stringToken("false");
                                }
                                if (lastKey != null) {
                                    callback.keyValue(lastKey, "false");
                                    lastKey = null;
                                }
                            } else {
                                // parsing error....
                                Log.p("Expected false for key value while parsing JSON token at row: " + row + " column: " + column + " buffer: " + currentToken.toString());
                            }

                            continue;
                        case '{':
                            if (lastKey == null) {
                            	if (blocks.size() == 0) {
                            		lastKey = "root";
                            	} else {
                            		lastKey = blocks.peek();
                            	}
                            }
                        	blocks.push(lastKey);
                            callback.startBlock(lastKey);
                            lastKey = null;
                            continue;
                        case '}':
                            if (currentToken.length() > 0) {
                                try {
                                    String ct = currentToken.toString();
                                    if(useLongsDefault) {
                                        if(ct.indexOf('.') > -1) {
                                            callback.numericToken(Double.parseDouble(ct));
                                        } else {
                                            callback.longToken(Long.parseLong(ct));
                                        }
                                    } else {
                                        callback.numericToken(Double.parseDouble(ct));
                                    }
                                    if (lastKey != null) {
                                        callback.keyValue(lastKey, currentToken.toString());
                                        lastKey = null;
                                        currentToken.setLength(0);
                                    }
                                    
                                } catch (NumberFormatException err) {
                                    Log.e(err);
                                    // this isn't a number!
                                }
                            }
                            currentBlock = blocks.pop();
                            callback.endBlock(currentBlock);
                            lastKey = null;
                            continue;
                        case '[':
                        	blocks.push(lastKey);

                            callback.startArray(lastKey);
                            lastKey = null;
                            continue;
                        case ']':
                            if (currentToken.length() > 0) {
                                try {
                                    String ct = currentToken.toString();
                                    if(useLongsDefault) {
                                        if(ct.indexOf('.') > -1) {
                                            callback.numericToken(Double.parseDouble(ct));
                                        } else {
                                            callback.longToken(Long.parseLong(ct));
                                        }
                                    } else {
                                        callback.numericToken(Double.parseDouble(ct));
                                    }
                                    if (lastKey != null) {
                                        callback.keyValue(lastKey, currentToken.toString());
                                        lastKey = null;
                                    }
                                } catch (NumberFormatException err) {
                                    // this isn't a number!
                                }
                            }
                            currentToken.setLength(0);

                            currentBlock = blocks.pop();
                            callback.endArray(currentBlock);
                            lastKey = null;
                            continue;
                        case ' ':
                        case '\r':
                        case '\t':
                        case '\n':
                            // whitespace
                            continue;

                        case '"':
                            quoteMode = true;
                            continue;
                        case ':':
                        case ',':
                            if (currentToken.length() > 0) {
                                try {
                                    String ct = currentToken.toString();
                                    if(useLongsDefault) {
                                        if(ct.indexOf('.') > -1) {
                                            callback.numericToken(Double.parseDouble(ct));
                                        } else {
                                            callback.longToken(Long.parseLong(ct));
                                        }
                                    } else {
                                        callback.numericToken(Double.parseDouble(ct));
                                    }
                                    if (lastKey != null) {
                                        callback.keyValue(lastKey, currentToken.toString());
                                        lastKey = null;
                                    }
                                } catch (NumberFormatException err) {
                                    // this isn't a number!
                                }
                            }
                            currentToken.setLength(0);
                            continue;
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                        case '-':
                        case '.':
                        case 'x':
                        case 'd':
                        case 'l':
                        case 'e':
                        case 'E':
                            currentToken.append(c);
                            continue;
                    }
                }
            }
        } catch (Exception err) {
            Log.e(err);
            Log.p("Exception during JSON parsing at row: " + row + " column: " + column + " buffer: " + currentToken.toString());
            /*System.out.println();
            int current = i.read();
            while(current >= 0) {
            System.out.print((char)current);
            current = i.read();
            }*/
            i.close();
        }
    }
    /**
     * <p>
     * Parses the given input stream into this object and returns the parse tree.<br>
     * The {@code JSONParser} returns a {@code Map} which is great if the root object is a {@code Map} but in 
     * some cases its a list of elements (as is the case above). In this case a special case {@code "root"} element is 
     * created to contain the actual list of elements. See the sample below for exact usage of this.
     * </p>
     * <p>
     * The sample below includes JSON from <a href="https://anapioficeandfire.com/">https://anapioficeandfire.com/</a>
     * generated by the query <a href="http://www.anapioficeandfire.com/api/characters?page=5&pageSize=3">http://www.anapioficeandfire.com/api/characters?page=5&pageSize=3</a>:
     * </p>
     * <script src="https://gist.github.com/codenameone/f9fdacaac12583cd2eed.js"></script>
     * <img src="https://www.codenameone.com/img/developer-guide/json-parsing.png" alt="JSON Parsing Result">
     *
     * @param i the reader
     * @return the parse tree as a hashtable
     * @throws IOException if thrown by the stream
     */
    public Map<String, Object> parseJSON(Reader i) throws IOException {
        modern = true;
        state = new LinkedHashMap<String, Object>();
        parseStack = new ArrayList<Object>();
        currentKey = null;
        if (!strict) {
            i = new CharArrayReader(JSONSanitizer.sanitize(Util.readToString(i)).toCharArray());
        }
        parse(i, this);
        return state;
    }

    /**
     * Parses the given input stream into this object and returns the parse tree
     *
     * @param i the reader
     * @return the parse tree as a hashtable
     * @throws IOException if thrown by the stream
     * @deprecated use the new parseJSON instead
     */
    public Hashtable<String, Object> parse(Reader i) throws IOException {
        modern = false;
        state = new Hashtable();
        parseStack = new Vector();
        currentKey = null;
        if (!strict) {
            String cleaned = JSONSanitizer.sanitize(Util.readToString(i));
            i = new CharArrayReader(cleaned.toCharArray());
        }
        parse(i, this);
        return (Hashtable<String, Object>)state;
    }

    private boolean isStackHash() {
        return parseStack.get(parseStack.size() - 1) instanceof Map;
    }

    private Map getStackHash() {
        return (Map) parseStack.get(parseStack.size() - 1);
    }

    private java.util.List<Object> getStackVec() {
        return (java.util.List<Object>) parseStack.get(parseStack.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    public void startBlock(String blockName) {
        if (parseStack.size() == 0) {
            parseStack.add(state);
        } else {
            Map newOne;
            if(modern) {
                newOne = new LinkedHashMap();
            } else {
                newOne = new Hashtable();
            }
            if (isStackHash()) {
                getStackHash().put(currentKey, newOne);
                currentKey = null;
            } else {
                getStackVec().add(newOne);
            }
            parseStack.add(newOne);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void endBlock(String blockName) {
        parseStack.remove(parseStack.size() - 1);
    }

    /**
     * Checks if this JSON parser is in strict mode.  When in strict mode, which is the default,
     * only valid JSON will be parsed.  If strict mode is disabled, then it will attempt to 
     * sanitize the JSON input before parsing.  This can be handy if you want to parse structures
     * that are *almost* JSON.  E.g. non-quoted keys, single-quotes on strings, unquoted strings. Etc.
     * 
     * @return True if strict mode is enabled.
     * @since 7.0
     * @see #setStrict(boolean) 
     */
    public boolean isStrict() {
        return strict;
    }
    
    /**
     * Enables or disables strict mode.  Default is true.
     * <p>When strict mode is disabled, the parser will sanitize the JSON input before parsing.  The effect
     * is that it will be able to parse input that is json-ish.</p>
     * <h3>Non-Strict Input</h3>
     * The sanitizer takes JSON like content, and interprets it as JS eval
     * would. Specifically, it deals with these non-standard constructs.
     * <ul>
     * <li>{@code '...'} Single quoted strings are converted to JSON strings.
     * <li>{@code \xAB} Hex escapes are converted to JSON unicode escapes.
     * <li>{@code \012} Octal escapes are converted to JSON unicode escapes.
     * <li>{@code 0xAB} Hex integer literals are converted to JSON decimal
     * numbers.
     * <li>{@code 012} Octal integer literals are converted to JSON decimal
     * numbers.
     * <li>{@code +.5} Decimal numbers are coerced to JSON's stricter format.
     * <li>{@code [0,,2]} Elisions in arrays are filled with {@code null}.
     * <li>{@code [1,2,3,]} Trailing commas are removed.
     * <li><code>{foo:"bar"}</code> Unquoted property names are quoted.
     * <li><code>//comments</code> JS style line and block comments are removed.
     * <li><code>(...)</code> Grouping parentheses are removed.
     * </ul>
     *
     * @param strict True to enable strict mode, false to disable it.
     * @see #isStrict() 
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }
    /**
     * {@inheritDoc}
     */
    public void startArray(String arrayName) {
        java.util.List<Object> currentVector;
        Map newOne;
        if(modern) {
            currentVector = new ArrayList<Object>();
        } else {
            currentVector = new Vector<Object>();
        }

        // the root of the JSON is an array, we need to wrap it in an assignment
        if (parseStack.size() == 0) {
            parseStack.add(state);
            currentKey = "root";
        }
        if (isStackHash()) {
            getStackHash().put(currentKey, currentVector);
            currentKey = null;
        } else {
            getStackVec().add(currentVector);
        }
        parseStack.add(currentVector);
    }

    /**
     * {@inheritDoc}
     */
    public void endArray(String arrayName) {
        parseStack.remove(parseStack.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    public void stringToken(String tok) {
        if (isStackHash()) {
            if (currentKey == null) {
                currentKey = tok;
            } else {
                if (tok != null || isIncludeNulls()) {
                    getStackHash().put(currentKey, tok);
                }
                currentKey = null;
            }
        } else {
            getStackVec().add(tok);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void numericToken(double tok) {
        if (isStackHash()) {
            getStackHash().put(currentKey, new Double(tok));
            currentKey = null;
        } else {
            getStackVec().add(new Double(tok));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void longToken(long tok) {
        if (isStackHash()) {
            getStackHash().put(currentKey, new Long(tok));
            currentKey = null;
        } else {
            getStackVec().add(new Long(tok));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void booleanToken(boolean tok) {
        if (isStackHash()) {
            getStackHash().put(currentKey, tok);
            currentKey = null;
        } else {
            getStackVec().add(tok);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void keyValue(String key, String value) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAlive() {
        return true;
    }


	
}
