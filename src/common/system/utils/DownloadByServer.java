/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.system.utils;

import common.formatting.StringFormatters;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

/**
 * Class that lets the server download WWW documents and perform HTTP requests
 * like a client, used for screenscraping.
 *
 * @author fr
 */
public class DownloadByServer {
	
	public static final int STATUS_NOT_INITED = 0;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAILURE = 2;
	public static final String FILENAME_PREFIX = "filename=";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static HttpClientFactory fac = new HttpClientFactory();
	// fields
	private String url;
	private URI uri;
	private HttpResponse response;
	private HttpEntity entity;
	private HashMap<String, String> cookiesToSend;
	private HashMap<String, String> receivedCookies;
	private String receivedCookieString;
	private InputStream inputStream;
	private String data;
	private List<String> redirectChain;
	private byte[] bindata;
	private int status = STATUS_NOT_INITED;
	private String referer;
	private StatusLine statusLine;
	
	protected DownloadByServer(Object urlObject, HashMap<String, String> cookiesToSend, String referer, List<String> redirectChain) {
		// must use factory methods
		processURL(urlObject);
		this.referer = referer;
		this.cookiesToSend = cookiesToSend;
		this.redirectChain = redirectChain;
	}
	
	public static DownloadByServer perform(Object urlObject) {
		return DownloadByServer.perform(urlObject, (HashMap) null, (String) null);
	}
	
	public static DownloadByServer perform(Object urlObject, String referer) {
		return DownloadByServer.perform(urlObject, (HashMap) null, referer);
	}
	
	public static DownloadByServer perform(Object urlObject, HashMap<String, String> cookies) {
		return DownloadByServer.perform(urlObject, cookies, (String) null);
	}
	
	public static DownloadByServer perform(Object urlObject, HashMap<String, String> cookies, String referer) {
		return perform(urlObject, cookies, referer, null, null);
	}
	
	private static DownloadByServer perform(Object urlObject, HashMap<String, String> cookies, String referer, List<String> redirectChain) {
		return perform(urlObject, cookies, referer, null, null, redirectChain);
	}
	
	public static DownloadByServer perform(Object urlObject, HashMap<String, String> cookies, String referer, String username, String password) {
		return performRequest(GET, urlObject, null, cookies, null, referer, username, password, null);
	}
	
	private static DownloadByServer perform(Object urlObject, HashMap<String, String> cookies, String referer, String username, String password, List<String> redirectChain) {
		return performRequest(GET, urlObject, null, cookies, null, referer, username, password, redirectChain);
	}
	
	public static DownloadByServer performDelete(Object urlObject, HashMap<String, String> cookies, String referer, String username, String password) {
		return performRequest(DELETE, urlObject, null, cookies, null, referer, username, password, null);
	}
	
	public static DownloadByServer performPost(Object urlObject, HashMap<String, String> data) {
		return DownloadByServer.performPost(urlObject, data, (String) null);
	}
	
	public static DownloadByServer performPost(Object urlObject, HashMap<String, String> data, String referer) {
		return DownloadByServer.performPost(urlObject, data, (HashMap) null);
	}
	
	public static DownloadByServer performPost(Object urlObject, HashMap<String, String> data, HashMap<String, String> cookies) {
		return DownloadByServer.performPost(urlObject, data, cookies, (HashMap) null);
	}
	
	public static DownloadByServer performPost(Object urlObject, HashMap<String, String> data, HashMap<String, String> cookies, String referer) {
		return DownloadByServer.performPost(urlObject, data, cookies, null, referer);
	}
	
	public static DownloadByServer performPost(Object urlObject, HashMap<String, String> data, HashMap<String, String> cookies, HashMap<String, ByteArrayBody> files) {
		return DownloadByServer.performPost(urlObject, data, cookies, files, (String) null);
	}
	
	public static DownloadByServer performPost(Object urlObject, HashMap<String, String> data, HashMap<String, String> cookies, HashMap<String, ByteArrayBody> files, String referer) {
		return performRequest(POST, urlObject, data, cookies, files, referer);
	}
	
	public static DownloadByServer performPut(Object urlObject, HashMap<String, String> cookies, InputStream fileContent, String username, String password) {
		DownloadByServer retval = new DownloadByServer(urlObject, cookies, null, null);
		
		try {
			HttpPut request = new HttpPut(retval.uri);
			
			HttpEntity putFile = new BufferedHttpEntity(new InputStreamEntity(fileContent, -1));
			request.setEntity(putFile);
			
			retval.addHeaderLinesToRequest(request);
			retval.performRequest(request, username, password);
			
			return retval;
		} catch (Exception ex) {
			Logger.getLogger(DownloadByServer.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
	
	private static DownloadByServer performRequest(String method, Object urlObject, HashMap<String, String> data, HashMap<String, String> cookies, HashMap<String, ByteArrayBody> files, String referer) {
		return performRequest(method, urlObject, data, cookies, files, referer, null, null, null);
	}
	
	private static DownloadByServer performRequest(final String method, Object urlObject, HashMap<String, String> data, HashMap<String, String> cookies, HashMap<String, ByteArrayBody> files, String referer, String username, String password, List<String> redirectChain) {
		DownloadByServer retval = new DownloadByServer(urlObject, cookies, referer, redirectChain);
		
		if (data == null) {
			data = new HashMap<String, String>();
		}
		
		try {
			HttpRequestBase request;
			if (GET.equals(method)) {
				request = new HttpGet(retval.uri);
			} else if (DELETE.equals(method)) {
				request = new HttpDelete(retval.uri);
			} else if (POST.equals(method)) {
				HttpPost httpPost = new HttpPost(retval.uri);
				request = httpPost;
				
				if (files != null) {
					// POST and
					MultipartEntity mpEntity = new MultipartEntity();
					for (Map.Entry<String, String> entry : data.entrySet()) {
						mpEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
					}
					// also FILE
					for (Map.Entry<String, ByteArrayBody> entry : files.entrySet()) {
						mpEntity.addPart(entry.getKey(), entry.getValue());
					}
					httpPost.setEntity(mpEntity);
				} else {
					// POST only
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					for (Map.Entry<String, String> entry : data.entrySet()) {
						nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
					}
					
					UrlEncodedFormEntity formData = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
					httpPost.setEntity(formData);
				}
			} else {
				return null;
			}
			
			retval.addHeaderLinesToRequest(request);
			retval.performRequest(request, username, password);
			Header newLocation = retval.getFirstHeader("location");
			if (newLocation != null) {
				// add any new cookies or overwrite existing ones
				HashMap<String, String> receivedCookies = retval.getCookiesHashMap();
				if (cookies == null) {
					cookies = receivedCookies;
				} else if (receivedCookies != null) {
					cookies.putAll(receivedCookies);
				}
				// use GET on new location
				String newLocationValue = newLocation.getValue();
				try {
					URL newUrl = new URL(retval.uri.toURL(), newLocationValue);
					return perform(newUrl, cookies, referer);
				} catch (Exception ex) {
					return perform(newLocationValue, cookies, referer, retval.redirectChain);
				}
			}
			
			return retval;
		} catch (Exception ex) {
			Logger.getLogger(DownloadByServer.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}
	
	public static String encodeURL(String location) {
		System.out.println("Navigating to " + location);
//		try {
//			URI.create(location);
//			// seems ok
//			return location;
//		} catch (Exception ex) {
//			// encode
//			try {
//				location = URLEncoder.encode(location, "UTF-8");
//			} catch (UnsupportedEncodingException ex2) {
//				Logger.getLogger(DownloadByServer.class.getName()).log(Level.SEVERE, null, ex);
//				location = URLEncoder.encode(location);
//			}
//
//			// fix most common problems, see http://www.permadi.com/tutorial/urlEncoding/ for useful info
//			location = location.replaceFirst("(?ims)%3a", ":").
//					replaceAll("(?ims)%2f", "/").
//					replaceFirst("(?ims)%3f", "?").
//					replaceAll("(?ims)%3d", "=").
//					replaceAll("(?ims)%26", "&").
//					replaceAll("(?ims)%2b", "+");
		String newLocation = StringFormatters.fixUrlEncoding(location);
		if (!newLocation.equals(location)) {
			System.out.println("Fix URL to " + newLocation);
		}
		return newLocation;
//		}
//        return location.replace(" ", "%20").
//                replace("^", "%5E").
//                replace("|", "%7C");
	}
	
	private void processURL(Object urlObject) {
		if (urlObject instanceof String) {
			url = encodeURL((String) urlObject);
		} else if (urlObject instanceof URL) {
			url = ((URL) urlObject).toString();
		} else {
			throw new UnsupportedOperationException("urlObject must be String or URL.");
		}
		uri = URI.create(url);
		if (redirectChain == null) {
			// begin redirect tracking
			redirectChain = new ArrayList<String>();
		}
		redirectChain.add(url);
	}
	
	public Header[] getHeaders(String name) {
		if (response != null) {
			return response.getHeaders(name);
		}
		return new Header[0];
	}
	
	public Header getFirstHeader(String name) {
		if (response != null) {
			return response.getFirstHeader(name);
		}
		return null;
	}
	
	public Header getLastHeader(String name) {
		if (response != null) {
			return response.getLastHeader(name);
		}
		return null;
	}
	
	public List<String> getRedirectChain() {
		return redirectChain;
	}
	
	public String getLastUrl() {
		if (redirectChain != null) {
			int size = redirectChain.size();
			if (size > 0) {
				return redirectChain.get(size - 1);
			}
		}
		return url;
	}
	
	private void performRequest(HttpRequestBase request, String username, String password) {
		try {
			HttpClient httpclient = fac.getDefaultHttpClient(request.getURI()); // URI is used to determine which proxy to use
			if (username != null && password != null && httpclient instanceof DefaultHttpClient) {
				DefaultHttpClient dHttpclient = (DefaultHttpClient) httpclient;
				CredentialsProvider credentialsProvider = dHttpclient.getCredentialsProvider();
				credentialsProvider.setCredentials(
						new AuthScope(uri.getHost(), uri.getPort()),
						new UsernamePasswordCredentials(username, password));
			}
			response = httpclient.execute(request);
			statusLine = response.getStatusLine();
			entity = response.getEntity();
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
			status = STATUS_FAILURE;
		}
	}
	
	private void addHeaderLinesToRequest(HttpRequestBase request) {
		// get German texts
		request.setHeader(new BasicHeader("Accept-Language", "de-de,de;q=0.8,en-us;q=0.5,en;q=0.3"));
		if (referer != null) {
			request.setHeader(new BasicHeader("Referer", referer));
		}
		addCookiesToRequest(request);
	}
	
	private void addCookiesToRequest(HttpRequestBase request) {
		if (cookiesToSend == null) {
			return;
		}
		// write cookie header
		List<String> cookieNvps = new ArrayList<String>();
		for (Map.Entry<String, String> entry : cookiesToSend.entrySet()) {
			cookieNvps.add(entry.getKey() + "=" + entry.getValue());
		}
		request.setHeader(new BasicHeader("Cookie", StringUtils.join(cookieNvps, ";")));
	}
	
	public HashMap<String, String> getCookiesHashMap() {
		if (receivedCookies == null) {
			getCookiesBackend();
		}
		return receivedCookies;
	}
	
	public String getCookiesString() {
		if (receivedCookieString == null) {
			getCookiesBackend();
		}
		return receivedCookieString;
	}
	
	private void getCookiesBackend() {
		this.receivedCookies = new HashMap<String, String>();
		if (cookiesToSend != null) {
			// collect all
			receivedCookies.putAll(cookiesToSend);
		}
		// have not yet been parsed

		Header[] headers = response.getHeaders("Set-Cookie");
//        HeaderIterator it = response.headerIterator("Set-Cookie");
//        while (it.hasNext()) {
		for (int i = 0; i < headers.length; i++) {
			Header header = headers[i];
			String cookieNvp = StringUtils.substringBefore(header.getValue(), ";");
//            String cookieNvp = StringUtils.substringBefore(StringUtils.substringAfter(it.next().toString(), ": "), ";");
			receivedCookies.put(
					StringUtils.substringBefore(cookieNvp, "="),
					StringUtils.substringAfter(cookieNvp, "="));
		}
		// avoid doubles
		List<String> receivedCookieList = new ArrayList<String>();
		for (Map.Entry<String, String> entry : receivedCookies.entrySet()) {
			receivedCookieList.add(entry.getKey() + "=" + entry.getValue());
		}
		this.receivedCookieString = StringUtils.join(receivedCookieList, ";");
	}
	
	public InputStream getInputStream() {
		if (status == STATUS_FAILURE // failed
				|| data != null || bindata != null) { // too late
			return null;
		}
		if (inputStream == null) {
			try {
				inputStream = entity.getContent();
			} catch (IOException ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
			} catch (IllegalStateException ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
			}
		}
		return inputStream;
	}
	
	public byte[] getBindata() {
		if (data != null) {
			return data.getBytes();
		}
		if (bindata == null) {
			try {
				InputStream is = getInputStream();
				if (is != null) {
					bindata = IOUtils.toByteArray(is);
					status = STATUS_SUCCESS;
				} else {
					status = STATUS_FAILURE;
				}
				IOUtils.closeQuietly(is);
			} catch (Exception ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
				status = STATUS_FAILURE;
			}
		}
		return bindata;
	}
	
	public String getData() {
		return getData(null);
	}
	
	public String getData(String encoding) {
		if (bindata != null) {
			if (encoding == null) {
				return new String(bindata);
			}
			
			try {
				return new String(bindata, encoding);
			} catch (UnsupportedEncodingException ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
			}
		}
		if (data == null) {
			try {
				InputStream is = getInputStream();
				if (is != null) {
					data = IOUtils.toString(is, encoding);
					status = STATUS_SUCCESS;
				} else {
					status = STATUS_FAILURE;
				}
				IOUtils.closeQuietly(is);
			} catch (Exception ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
				status = STATUS_FAILURE;
			}
		}
		return data;
	}
	
	public String getMimeType() {
		return entity.getContentType().getValue();
	}
	
	public String getFilename() {
		Header lastHeader = this.response.getLastHeader("Content-Disposition");
		if (lastHeader != null) {
			String filenameByServer = lastHeader.getValue();
			if (filenameByServer != null && !filenameByServer.isEmpty() && filenameByServer.contains(FILENAME_PREFIX)) {
				// cut away
				filenameByServer = StringUtils.substringAfter(filenameByServer, FILENAME_PREFIX);

				// unquote if required
				filenameByServer = StringFormatters.removeStart(filenameByServer, "\"", "\'");
				filenameByServer = StringFormatters.removeEnd(filenameByServer, "\"", "\'");
				
				return filenameByServer;
			}
		}
		// cutAway everything before last / and after first ?
		return StringFormatters.cutAwaySearch(StringFormatters.cutAwayPath(this.url));
	}
	
	public int getStatus() {
		return status;
	}
	
	public int getStatusCode() {
		if (statusLine != null) {
			return statusLine.getStatusCode();
		}
		return -1;
	}
}
