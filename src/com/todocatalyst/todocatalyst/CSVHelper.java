/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 * https://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/
 
//examples of how you would use this to read a file
public static List<MyClass> readData() throws Exception {
    List<MyClass> collection = new Vector<MyClass>();
    File fileTemplate = new File( <<path to your file >>);
    FileInputStream fis = new FileInputStream(fileTemplate);
    Reader fr = new InputStreamReader(fis, "UTF-8");
 
    List<String> values = CSVHelper.parseLine(fr);
    while (values!=null) {
        collection.add( MyClass.constructFromStrings(values) );
        values = CSVHelper.parseLine(fr);
    }
    lnr.close();
    return collection;
// example of how to output a collection of objects to a CSV file
public static void saveData(List<MyClass> myData) throws Exception {
    File csvFile = new File(<<path to write to>>);
    FileOutputStream fos = new FileOutputStream(csvFile);
    Writer fw = new OutputStreamWriter(fos, "UTF-8");
    for (MyClass oneDatum : myData) {
        List<String> rowValues = oneDatum.getValues();
        CSVHelper.writeLine(fw, rowValues);
    }
    fw.flush();
    fw.close();
}
}
 * @author Thomas
 */
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
public class CSVHelper
{
    public static void writeLine(Writer w, List<String> values) 
        throws Exception
    {
        boolean firstVal = true;
        for (String val : values)  {
            if (!firstVal) {
                w.write(",");
            }
            w.write("\"");
            for (int i=0; i<val.length(); i++) {
                char ch = val.charAt(i);
                if (ch=='"') {
                    w.write("\"");  //extra quote
                }
                w.write(ch);
            }
            w.write("\"");
            firstVal = false;
        }
        w.write("n");
    }
 
    /**
    * Returns a null when the input stream is empty
    */
    public static List<String> parseLine(Reader r) throws Exception {
        int ch = r.read();
        while (ch == 'r') {
            ch = r.read();
        }
        if (ch<0) {
            return null;
        }
        ArrayList<String> store = new ArrayList<String>();
        StringBuffer curVal = new StringBuffer();
        boolean inquotes = false;
        boolean started = false;
        while (ch>=0) {
            if (inquotes) {
                started=true;
                if (ch == '"') {
                    inquotes = false;
                }
                else {
                    curVal.append((char)ch);
                }
            }
            else {
                if (ch == '"') {
                    inquotes = true;
                    if (started) {
   // if this is the second quote in a value, add a quote
   // this is for the double quote in the middle of a value
                        curVal.append('"');
                    }
                }
                else if (ch == ',') {
                    store.add(curVal.toString());
                    curVal = new StringBuffer();
                    started = false;
                }
                else if (ch == 'r') {
                    //ignore LF characters
                }
                else if (ch == 'n') {
                    //end of a line, break out
                    break;
                }
                else {
                    curVal.append((char)ch);
                }
            }
            ch = r.read();
        }
        store.add(curVal.toString());
        return store;
    }
}
