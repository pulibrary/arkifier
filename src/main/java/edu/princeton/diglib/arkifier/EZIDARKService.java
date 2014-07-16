/**
 * EZIDARKService.java
 */
package edu.princeton.diglib.arkifier;

import javax.xml.ws.http.HTTPException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 * Cheap wrapper for a NOID/ARK service.
 * 
 * @author <a href="mailto:ratliff@princeton.edu">Mark Ratliff</a>
 * @since Jun 26, 2014
 */
public class EZIDARKService {

	private static Client client;
	private static WebTarget rootResource;
	private static WebTarget mintResource;
	private String uri;
	private String naan;
	private String shoulder;

	private static HttpAuthenticationFeature basicAuthFeature;

	public EZIDARKService(String uri, String naan, String username, String password, String shoulder) {
		this.uri = uri;
		this.naan = naan;
		this.shoulder = shoulder;
		client = ClientBuilder.newClient();

		basicAuthFeature = HttpAuthenticationFeature.basic(username, password);

		client.register(basicAuthFeature);

		rootResource = client.target(uri);
	}

	/**
	 * Mint a new ARK
	 * 
	 * @returns The NOID identifier portion of the ARK
	 */

	public String mint() throws HTTPException {
		return mint(null);
	}

	/**
	 * Mint a new ARK and bind it to the given URL in one transaction.
	 * 
	 * @returns The NOID identifier portion of the ARK
	 */

	public String mint(String target_url) throws HTTPException {

		// If the minting resource has not already been instantiated, then make
		// a static resource that
		// can be reused
		if (mintResource == null) {
//			String url = this.uri;
			mintResource = rootResource.path("shoulder").path("ark:/" + naan + "/" + this.shoulder);
		}

		String post_data;
		if (target_url == null) {
			post_data = "";
		} else {
			post_data = "_target: " + target_url;
		}

		String noid;

		Invocation.Builder invocationBuilder = mintResource.request(MediaType.TEXT_PLAIN_TYPE);

		Response mintResponse;
		mintResponse = invocationBuilder.post(Entity.entity(post_data, MediaType.TEXT_PLAIN));

		int status = mintResponse.getStatus();

		if (status != 201) {
			throw new HTTPException(status);
		}

		String body = mintResponse.readEntity(String.class);
		noid = body.split("/")[2].trim();

		mintResponse.close();

		return noid;
	}

	/**
	 * Bind the given NOID identifier to the given URL
	 * 
	 * @returns true if bind was successful
	 * @throws exception
	 *             if bind was unsuccessful
	 */

	public boolean bind(String noid, String target_url) throws HTTPException {

		WebTarget bindResource = rootResource.path("id").path("ark:/" + naan + "/" + noid);

		Invocation.Builder invocationBuilder = bindResource.request(MediaType.TEXT_PLAIN_TYPE);

		String post_data = "_target: " + target_url;
		Response bindResponse;
		bindResponse = invocationBuilder.post(Entity.entity(post_data, MediaType.TEXT_PLAIN));

		int status = bindResponse.getStatus();
		bindResponse.close();

		if (status != 200) {
			throw new HTTPException(status);
		} else
			return true;
	}
}
