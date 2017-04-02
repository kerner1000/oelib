/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.system.utils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	private List<String> redirectChain;
	private Map<String, String> receivedCookies;

	@Override
	protected URI createLocationURI(String location) throws ProtocolException {
		if (redirectChain == null) {
			// begin redirect tracking
			redirectChain = new ArrayList<String>();
		}
		String url = DownloadByServer.encodeURL(location);
		redirectChain.add(url);
		return super.createLocationURI(url);
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

	public List<String> getRedirectChain() {
		return redirectChain;
	}

	public Map<String, String> getReceivedCookies() {
		return receivedCookies;
	}
}
