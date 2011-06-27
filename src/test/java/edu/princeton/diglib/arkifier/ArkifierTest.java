/**
 * ArkifierTest.java
 */
package edu.princeton.diglib.arkifier;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.TestCase;
import edu.princeton.diglib.arkifier.Arkifier.ACTIONS;

/**
 * 
 * @author <a href="jstroop@princeton.edu>Jon Stroop</a>
 * @since Jun 24, 2011
 * 
 */
/* 
 * "/4601808/s41.mets" : noid  
 * "/4601808/4609990/s43.mets" : path
 * "/4601808/4609990/332.mets" : uuid 
 * "/3493083.mets" : path 
 * "/3499523.mets" : uuid
 * "/4609321/331.mets" : noid
 */
public class ArkifierTest extends TestCase {

    private static Arkifier arkifier;
    private static String testDir;
    
    public static void main(String[] args) {
        ArkifierTest at = new ArkifierTest();
        at.run();
        
    }

    public ArkifierTest() {
        arkifier = new Arkifier();
        arkifier.setSimulate(true); // IMPORTANT!!

        ClassLoader cl = Arkifier.class.getClassLoader();
        testDir = cl.getResource("testData").getPath().toString();

    }

    public void testMintBind() {
        arkifier.setMint(true);
        arkifier.setBind(true);
        HashMap<String, ArrayList<ACTIONS>> report = arkifier.run(testDir);
        for (String key : report.keySet()) {
            ArrayList<ACTIONS> actions = report.get(key);
            if (key.endsWith("/4601808/s41.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertTrue(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4601808/4609990/s43.mets")) {
                assertTrue(actions.contains(ACTIONS.MINT));
                assertTrue(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4601808/4609990/332.mets")) {
                assertTrue(actions.contains(ACTIONS.MINT));
                assertTrue(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/3493083.mets")) {
                assertTrue(actions.contains(ACTIONS.MINT));
                assertTrue(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/3499523.mets")) {
                assertTrue(actions.contains(ACTIONS.MINT));
                assertTrue(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4609321/331.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertTrue(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
        }
    }

    public void testMintNotBind() {
        arkifier.setMint(true);
        arkifier.setBind(false);
        HashMap<String, ArrayList<ACTIONS>> report = arkifier.run(testDir);
        for (String key : report.keySet()) {
            ArrayList<ACTIONS> actions = report.get(key);
            if (key.endsWith("/4601808/s41.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertTrue(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4601808/4609990/s43.mets")) {
                assertTrue(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4601808/4609990/332.mets")) {
                assertTrue(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/3493083.mets")) {
                assertTrue(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/3499523.mets")) {
                assertTrue(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4609321/331.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertTrue(actions.contains(ACTIONS.NO_ACTION));
            }
        }
    }

    public void testBindNotMint() {
        arkifier.setMint(false);
        arkifier.setBind(true);
        HashMap<String, ArrayList<ACTIONS>> report = arkifier.run(testDir);
        for (String key : report.keySet()) {
            ArrayList<ACTIONS> actions = report.get(key);
            if (key.endsWith("/4601808/s41.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertTrue(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4601808/4609990/s43.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertTrue(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4601808/4609990/332.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertTrue(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/3493083.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertTrue(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/3499523.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertFalse(actions.contains(ACTIONS.BIND));
                assertTrue(actions.contains(ACTIONS.NO_ACTION));
            }
            if (key.endsWith("/4609321/331.mets")) {
                assertFalse(actions.contains(ACTIONS.MINT));
                assertTrue(actions.contains(ACTIONS.BIND));
                assertFalse(actions.contains(ACTIONS.NO_ACTION));
            }
        }
    }

}
