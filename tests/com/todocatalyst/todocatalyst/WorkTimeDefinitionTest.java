/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author THJ
 */
public class WorkTimeDefinitionTest {
    
    public WorkTimeDefinitionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getFinishTime method, of class WorkTimeDefinition.
     */
    @Test
    public void testGetFinishTime() {
        System.out.println("getFinishTime");
        int index = 0;
        WorkTimeDefinition instance = null;
        long expResult = 0L;
        long result = instance.getFinishTime(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getStartTime method, of class WorkTimeDefinition.
     */
    @Test
    public void testGetStartTime() {
        System.out.println("getStartTime");
        int index = 0;
        WorkTimeDefinition instance = null;
        long expResult = 0L;
        long result = instance.getStartTime(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFirstWorkSlot method, of class WorkTimeDefinition.
     */
    @Test
    public void testGetFirstWorkSlot() {
        System.out.println("getFirstWorkSlot");
        int index = 0;
        WorkTimeDefinition instance = null;
        WorkSlot expResult = null;
        WorkSlot result = instance.getFirstWorkSlot(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLastWorkSlot method, of class WorkTimeDefinition.
     */
    @Test
    public void testGetLastWorkSlot() {
        System.out.println("getLastWorkSlot");
        int index = 0;
        WorkTimeDefinition instance = null;
        WorkSlot expResult = null;
        WorkSlot result = instance.getLastWorkSlot(index);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setWorkSlotList method, of class WorkTimeDefinition.
     */
    @Test
    public void testSetWorkSlotList() {
        System.out.println("setWorkSlotList");
        List<WorkSlot> workSlots = null;
        WorkTimeDefinition instance = null;
        instance.setWorkSlotList(workSlots);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getWorkSlotList method, of class WorkTimeDefinition.
     */
    @Test
    public void testGetWorkSlotList() {
        System.out.println("getWorkSlotList");
        WorkTimeDefinition instance = null;
        List<WorkSlot> expResult = null;
        List<WorkSlot> result = instance.getWorkSlotList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getElementSlotList method, of class WorkTimeDefinition.
     */
    @Test
    public void testGetElementSlotList() {
        System.out.println("getElementSlotList");
        WorkTimeDefinition instance = null;
        List expResult = null;
        List result = instance.getElementSlotList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setElementSlotList method, of class WorkTimeDefinition.
     */
    @Test
    public void testSetElementSlotList() {
        System.out.println("setElementSlotList");
        List listOfItemsOrItemLists = null;
        WorkTimeDefinition instance = null;
        instance.setElementSlotList(listOfItemsOrItemLists);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reset method, of class WorkTimeDefinition.
     */
    @Test
    public void testReset() {
        System.out.println("reset");
        int indexFromWhichToResetTheList = 0;
        WorkTimeDefinition instance = null;
        instance.reset(indexFromWhichToResetTheList);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of refresh method, of class WorkTimeDefinition.
     */
    @Test
    public void testRefresh() {
        System.out.println("refresh");
        WorkTimeDefinition instance = null;
        instance.refresh();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNextWorkSlot method, of class WorkTimeDefinition.
     */
    @Test
    public void testGetNextWorkSlot() {
        System.out.println("getNextWorkSlot");
        Integer workSlotIndex = null;
        WorkTimeDefinition instance = null;
        WorkSlot expResult = null;
        WorkSlot result = instance.getNextWorkSlot(workSlotIndex);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
