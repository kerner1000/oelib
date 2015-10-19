/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.system.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * Redirect strategy used in Apache HTTP client, which is more tolerant.
 *
 * @author fr
 */
public class TolerantRedirectStrategy extends DefaultRedirectStrategy {

	private Map<String, String> receivedCookies;

	@Override
	protected URI createLocationURI(String location) throws ProtocolException {
		return super.createLocationURI(DownloadByServer.encodeURL(location));
	}

	@Override
	public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
		boolean redirected = super.isRedirected(request, response, context);
		if (redirected && response.containsHeader("Set-Cookie")) {
			// info would otherwise be lost
			if (receivedCookies == null) {
				receivedCookies = new HashMap<String, String>();
			}
			DownloadByServer.addCookiesToMap(response, receivedCookies);
		}
		return redirected;
	}

	public Map<String, String> getReceivedCookies() {
		return receivedCookies;
	}
}
