/**
 * ARKService.java
 */
package edu.princeton.diglib.arkifier;

import javax.xml.ws.http.HTTPException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Cheap wrapper for a NOID/ARK service.
 * 
 * @author <a href="mailto:jstroop@princeton.edu">Jon Stroop</a>
 * @since Apr 10, 2011
 */
public class ARKService {

    private static Client client;
    private static WebResource mintResource;
    private static WebResource bindResource;
    private String uri;
    private String naan;

    public static void main(String[] args) {
        System.out.println(new ARKService("http://arks.princeton.edu/nd/noidu_scratch", "88435").mint());
    };
    
    public ARKService(String uri, String naan) {
        this.uri = uri;
        this.naan = naan;
        client = Client.create();
    }

    public String mint() throws HTTPException {
        if (mintResource == null) {
            String url = this.uri + "?mint+1";
            mintResource = client.resource(url);
        }
        String noid;

        ClientResponse mintResponse;
        mintResponse = mintResource.accept("text/plain").get(ClientResponse.class);
        int status = mintResponse.getStatus();
        String body = mintResponse.getEntity(String.class);
        if (status != 200) {
            throw new HTTPException(status);
        }
        
        noid = body.split("/")[1].trim();
        
        return noid;
    }

    public boolean bind(String noid, String target) throws HTTPException {
        String url = this.uri + "?bind+set+" + naan + "/" + noid + "+location+" + target;
        bindResource = client.resource(url);
        ClientResponse bindResponse;
        bindResponse = bindResource.accept("text/plain").get(ClientResponse.class);

        int status = bindResponse.getStatus();

        if (status != 200) {
            throw new HTTPException(status);
        } else
            return true;
    }
}
