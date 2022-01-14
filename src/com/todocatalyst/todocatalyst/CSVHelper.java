/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 * https://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/
 *
 *
 * Here are a couple of things that you might want to notice about this
 * implementation:
 *
 * The class has two static methods, one to read a line of a CSV file, and one
 * to write a line of a CSV file. Nothing to set up, nothing to configure,
 * nothing to go wrong. It handles each character only once. No temporary
 * strings, and no concatenating of strings. It makes a single pass through the
 * stream. There is no pre-fetching, no pushing back, no scanning ahead for
 * length, and then re-parsing. There is no breaking the string initially
 * (prescan) into chunks that are then parsed separately. This single pass
 * approach is fast and it does not use any excessive memory, nor does it copy
 * the string values more than once. Everything is in characters, which means
 * you can use any encoding into bytes that you like. These methods never see
 * the bytes. You can parse from any stream source: file, http request, ftp,
 * nntp, whatever. You only need to create a Reader to that source. No need to
 * reinvent that. You can write to out to any output destination, just supply
 * the Writer class. It focuses just on the parsing of the value, and not
 * interpretation of the values. I have found that once the line is parsed into
 * a List of Strings, it is pretty easy to loop over this and do whatever
 * interpretation is needed of the values. Generally, that loop to interpret the
 * values is the simplest, most concise implementation. When I have used classes
 * that automatically do further conversions, I find I nearly always have to
 * write a loop anyway to do special things on the values beyond data types. If
 * I have to write the loop anyway to patch things up, I might as well convert
 * from strings at the same time. It does not tell you the size and shape of the
 * file. for example it does not tell you the number of rows or columns. You
 * simply read each row and you get a number of values, telling you the number
 * of columns. A properly formed CSV file will have the same number of columns
 * on each row, but this approach simply gives you the values that are there on
 * the row. There is no special handling for the first row. Often the first row
 * contains names of the columns. The code reading the file can easily handle
 * the first row differently. This has no dependencies on other non-standard
 * classes. It uses that standard Java stream classes. It does not invent new
 * protocols or patterns of call, instead leveraging the standard Java streams
 * as they were intended to be used. It does not handle comments. Why would
 * anyone have comments in a CSV file? If you need it, comments could be ignored
 * with about 3 additional lines of code. Since I have not encountered that
 * need, I did not put it in. (YAGNI) I used Vector because I am more
 * comfortable with it, but a purist might substitute ArrayList for performance
 * reasons. That is why the interface uses the more generic “List” interface. On
 * output it puts quotes around all values. This was easier than figuring out
 * whether the value needed it or not, and some might view this as wasteful,
 * adding two extra characters for every values that does not need it. For me,
 * processing speed was more important than space on the disk, so I would rather
 * avoid the overhead and complexity of figuring out whether it is needed, even
 * if there is a small cost on output size. About 50 lines of code, makes it
 * smaller than the interface files of most competition. This makes it so easy
 * to write a CSV file. I write a loop over my data set. For each row, I collect
 * all the values (converted to strings) into a List of Strings. Then one method
 * call writes that line out. Repeat until finished. Generally this is all I
 * need. If I want a header with the column names, I do that just before looping
 * over the data. So easy.
 *
 * Reading is similarly easy. Open the file, and start pulling lines out as
 * Lists of Strings. It is easy then to iterate through that list, and plug the
 * value into whatever internal data structure I am using. If it needs to be
 * converted to Integer or Long, I do it in that loop without difficulty. This
 * is the step of interpreting the data values, and generally I don’t need a CSV
 * class to do that.
 *
 * Most importantly, this one class can be reused anywhere without bringing any
 * extra dependencies. A single class, is a single file. It uses only standard
 * Java classes, so I don’t need to include any extra exotic libraries into my
 * program.
 *
 * //examples of how you would use this to read a file public static
 * List<MyClass> readData() throws Exception {     List<MyClass> collection =
 * new Vector<MyClass>();     File fileTemplate = new File( <<path to your file
 * >>);     FileInputStream fis = new FileInputStream(fileTemplate);     Reader
 * fr = new InputStreamReader(fis, "UTF-8");       List<String> values =
 * CSVHelper.parseLine(fr);     while (values!=null) {         collection.add(
 * MyClass.constructFromStrings(values) );         values =
 * CSVHelper.parseLine(fr);     }     lnr.close();     return collection;
 *
 * // example of how to output a collection of objects to a CSV file public
 * static void saveData(List<MyClass> myData) throws Exception {      File
 * csvFile = new File(<<path to write to>>);      FileOutputStream fos = new
 * FileOutputStream(csvFile);     Writer fw = new OutputStreamWriter(fos,
 * "UTF-8");     for (MyClass oneDatum : myData) {         List<String>
 * rowValues = oneDatum.getValues();         CSVHelper.writeLine(fw, rowValues);
 *     }     fw.flush();     fw.close(); } }
 *
 * @author Thomas
 */
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    private final static String linebreak = MyPrefs.useMacLineBreakForCSVFiles.getBoolean() ? "\n" : "\r\n";
    private final static char sep = ';';

    public static void writeLine(Writer w, List<String> values) throws Exception {
        boolean firstVal = true;
        for (String val : values) {
            if (!firstVal) {
//                w.write(",");
                w.write(sep);
            }
            w.write("\"");
            for (int i = 0; i < val.length(); i++) {
                char ch = val.charAt(i);
                if (ch == '\"') {
                    w.write("\"");  //extra quote
                }
                w.write(ch);
            }
            w.write("\"");
            firstVal = false;
        }
//        w.write("\n");
        w.write(linebreak);
    }

    public static void writeLine(StringBuilder w, List<String> values) throws Exception {
        boolean firstVal = true;
        for (String val : values) {
            if (!firstVal) {
//                w.append(",");
                w.append(sep);
            }
            w.append("\"");
            for (int i = 0; i < val.length(); i++) {
                char ch = val.charAt(i);
                if (ch == '\"') {
                    w.append("\"");  //extra quote
                }
                w.append(ch);
            }
            w.append("\"");
            firstVal = false;
        }
//        w.append("\n");
        w.append(linebreak);
    }

    /**
     * Returns a null when the input stream is empty
     */
    public static List<String> parseLine(Reader r) throws Exception {
        int ch = r.read();
        while (ch == '\r') {
            ch = r.read();
        }
        if (ch < 0) {
            return null;
        }
        ArrayList<String> store = new ArrayList<String>();
        StringBuffer curVal = new StringBuffer();
        boolean inquotes = false;
        boolean started = false;
        while (ch >= 0) {
            if (inquotes) {
                started = true;
                if (ch == '\"') {
                    inquotes = false;
                } else {
                    curVal.append((char) ch);
                }
            } else {
                if (ch == '\"') {
                    inquotes = true;
                    if (started) {
                        // if this is the second quote in a value, add a quote
                        // this is for the double quote in the middle of a value
                        curVal.append('"');
                    }
//                } else if (ch == ',') {
                } else if (ch == sep) {
                    store.add(curVal.toString());
                    curVal = new StringBuffer();
                    started = false;
                } else if (ch == '\r') {
                    //ignore LF characters
                } else if (ch == '\n') {
                    //end of a line, break out
                    break;
                } else {
                    curVal.append((char) ch);
                }
            }
            ch = r.read();
        }
        store.add(curVal.toString());
        return store;
    }

    public static List<String> parseLine(StringBuffer r) throws Exception {
        int i = 0;
        int ch = r.charAt(i++);
        while (ch == '\r') {
            ch = r.charAt(i++);
        }
        if (ch < 0) {
            return null;
        }
        ArrayList<String> store = new ArrayList<String>();
        StringBuffer curVal = new StringBuffer();
        boolean inquotes = false;
        boolean started = false;
        while (ch >= 0) {
            if (inquotes) {
                started = true;
                if (ch == '\"') {
                    inquotes = false;
                } else {
                    curVal.append((char) ch);
                }
            } else {
                if (ch == '\"') {
                    inquotes = true;
                    if (started) {
                        // if this is the second quote in a value, add a quote
                        // this is for the double quote in the middle of a value
                        curVal.append('"');
                    }
//                } else if (ch == ',') {
                } else if (ch == sep) {
                    store.add(curVal.toString());
                    curVal = new StringBuffer();
                    started = false;
                } else if (ch == '\r') {
                    //ignore LF characters
                } else if (ch == '\n') {
                    //end of a line, break out
                    break;
                } else {
                    curVal.append((char) ch);
                }
            }
            ch = r.charAt(i++);
        }
        store.add(curVal.toString());
        return store;
    }
}
