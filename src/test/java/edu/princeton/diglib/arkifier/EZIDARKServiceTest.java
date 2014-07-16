package edu.princeton.diglib.arkifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

public class EZIDARKServiceTest  extends TestCase {
    private static EZIDARKService arkService;
        
    public static void main(String[] args) {
    	EZIDARKServiceTest et = new EZIDARKServiceTest();
        et.run();
        
    }

    public EZIDARKServiceTest() {
    	Properties props = new Properties();
        try {
            InputStream propsStream;
            ClassLoader cl;
            cl = EZIDARKService.class.getClassLoader();
            propsStream = cl.getResourceAsStream("arkifier.test.properties");
            props.load(propsStream);
            String uri = props.getProperty("Arkifier.uri");
            String naan = props.getProperty("Arkifier.naan");
            String user = props.getProperty("Arkifier.user");
            String shoulder = props.getProperty("Arkifier.shoulder", "");
            String password = props.getProperty("Arkifier.password");   
            this.arkService = new EZIDARKService(uri, naan, user, password, shoulder);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    public void testMint() {
    	String noid = arkService.mint();
    	assertTrue(noid.startsWith("fk4"));
    }

    public void testBind() {
    	String noid = "fk4zc81p9m";
    	boolean bindOK = arkService.bind(noid, "http://pudl.princeton.edu");
    	assertTrue(bindOK);
//    	System.out.println();
    }
    
}
