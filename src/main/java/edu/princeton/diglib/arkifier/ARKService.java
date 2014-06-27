/**
 * ARKService.java
 */
package edu.princeton.diglib.arkifier;

import javax.xml.ws.http.HTTPException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;


/**
 * Cheap wrapper for a NOID/ARK service.
 * 
 * @author <a href="mailto:jstroop@princeton.edu">Jon Stroop</a>
 * @since Apr 10, 2011
 */
public class ARKService {

    private static Client client;
    private static WebTarget mintResource;
    private String uri;
    private String naan;

    public ARKService(String uri, String naan) {
        this.uri = uri;
        this.naan = naan;
        client = ClientBuilder.newClient();
    }

    /**
     * Mint a new ARK.
     * 
     * @returns The NOID identifier portion of the ARK
     */
    
    public String mint() throws HTTPException {
    	
    	// If the minting resource has not already been instantiated, then make a static resource that
    	//  can be reused
        if (mintResource == null) {
        	String url = this.uri + "?mint+1";
            mintResource = client.target(url);
        }
        
        String noid;

        Invocation.Builder invocationBuilder = mintResource.request(MediaType.TEXT_PLAIN_TYPE);
        
        Response mintResponse;
        mintResponse = invocationBuilder.get();
        
        int status = mintResponse.getStatus();
        String body = mintResponse.readEntity(String.class);
        if (status != 200) {
            throw new HTTPException(status);
        }
        
        noid = body.split("/")[1].trim();
        mintResponse.close();
        
        return noid;
    }

    /**
     * Bind the given NOID identifier to the given URL
     * 
     * @returns true if bind was successful
     * @throws exception if bind was unsuccessful
     */
    
    public boolean bind(String noid, String target) throws HTTPException {
        String url = this.uri + "?bind+set+" + naan + "/" + noid + "+location+" + target;
        WebTarget bindResource = client.target(url);
        
        Invocation.Builder invocationBuilder = mintResource.request(MediaType.TEXT_PLAIN_TYPE);
        
        Response bindResponse;
        bindResponse = invocationBuilder.get();

        int status = bindResponse.getStatus();
        bindResponse.close();
        
        if (status != 200) {
            throw new HTTPException(status);
        } else
            return true;
    }
}
