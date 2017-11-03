package com.todocatalyst.todocat;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas
 */
public class ASSERT {

    public static void that(boolean trueAssertion, String assertion) {
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
