package com.todocatalyst.todocatalyst;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 * https://github.com/codenameone/CodenameOne/issues/1609
 * add -ea VM option in Netbeans: http://stackoverflow.com/questions/11686166/enable-assert-during-runtime-in-netbeans
 * http://stackoverflow.com/questions/33929174/how-can-i-use-asserts-in-codenameone-code
 */
public class ASSERT {

    interface GenString {
        String s();
    }
    
    public static void that(boolean trueAssertion, GenString assertion) {
        assert true;
        if (!trueAssertion) {
            try {
//                Log.l("ASSERTION not true:"+assertion);
                throw new Exception("ASSERTION FAILED " + assertion.s());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void that(boolean trueAssertion, String assertion) {
        assert true;
        if (!trueAssertion) {
            try {
//                Log.l("ASSERTION not true:"+assertion);
                throw new Exception("ASSERTION FAILED " + assertion);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void that(boolean trueAssertion) {
        that(trueAssertion, "<no text defined>");
    }

    /**
     *      */
    public static void that(String assertion) {
        that(false, assertion);
    }

    public static void that() {
        that("Should nevern be called!!");
    }
}
