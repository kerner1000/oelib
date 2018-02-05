/*
 * Copyright 2018 fr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package common.system.utils;

import common.formatting.StringFormatters;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
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
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

/**
 *
 * @author fr
 */
public class HttpDownload {

	public static final int STATUS_NOT_INITED = 0;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_FAILURE = 2;
	public static final String FILENAME_PREFIX = "filename=";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static HttpClientFactory fac = new HttpClientFactory();

	// general
	private String method = GET; // may be overwritten
	private final String url;
	private final URI uri;
	private Map<String, String> cookies;
	private String referer;
	private Map<String, String> headerLines;
	private Map<String, Object> options;

	// POST
	private Map<String, String> postData;
	private ContentType postContentType;
	private Map<String, ByteArrayBody> files;
	private String postBody;

	// PUT
	private InputStream fileContent;

	private String username;
	private String password;

	// SOAP
	private String soapAction;
	private HttpEntity soapData;

	// result data
	private HttpClient httpclient;
	private HttpRequestBase request;
	private HttpResponse response;
	private HttpEntity entity;
	private Map<String, String> redirCookies;
	private Map<String, String> receivedCookies;
	private String receivedCookieString;
	private InputStream inputStream;
	private String data;
	private List<String> redirectChain;
	private byte[] bindata;
	private int status = STATUS_NOT_INITED;
	private StatusLine statusLine;

	public static HttpDownload withURL(Object urlObject) {
		return new HttpDownload(urlObject);
	}

	public static String encodeURL(String location) {
		System.out.println("Navigating to " + location);
		String newLocation = StringFormatters.fixUrlEncoding(location);
		if (!newLocation.equals(location)) {
			System.out.println("Fix URL to " + newLocation);
		}
		return newLocation;
	}

	private HttpDownload(Object urlObject) {
		if (urlObject instanceof URI) {
			uri = (URI) urlObject;
			url = uri.toString();
		} else {
			if (urlObject instanceof String) {
				url = encodeURL((String) urlObject);
			} else if (urlObject instanceof URL) {
				url = ((URL) urlObject).toString();
			} else {
				throw new UnsupportedOperationException("urlObject must be String or URL.");
			}
			uri = URI.create(url);
		}
	}

//<editor-fold defaultstate="collapsed" desc="getters/chaining setters">
	public String getMethod() {
		return method;
	}

	public HttpDownload withMethod(String method) {
		this.method = method;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public URI getUri() {
		return uri;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public HttpDownload withCookies(Map<String, String> cookies) {
		this.cookies = cookies;
		return this;
	}

	public String getReferer() {
		return referer;
	}

	public HttpDownload withReferer(String referer) {
		this.referer = referer;
		return this;
	}

	public Map<String, String> getHeaderLines() {
		return headerLines;
	}

	public HttpDownload withHeaderLines(Map<String, String> headerLines) {
		this.headerLines = headerLines;
		return this;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public HttpDownload withOptions(Map<String, Object> options) {
		this.options = options;
		return this;
	}

	public Map<String, String> getPostData() {
		return postData;
	}

	public HttpDownload withPostData(Map<String, String> postData) {
		this.postData = postData;
		return withMethod(POST); // auto-switch to POST
	}

	public ContentType getPostContentType() {
		return postContentType;
	}

	public HttpDownload withPostContentType(ContentType postContentType) {
		this.postContentType = postContentType;
		return withMethod(POST); // auto-switch to POST
	}

	public Map<String, ByteArrayBody> getFiles() {
		return files;
	}

	public HttpDownload withFiles(Map<String, ByteArrayBody> files) {
		this.files = files;
		return withMethod(POST); // auto-switch to POST
	}

	public String getPostBody() {
		return postBody;
	}

	public HttpDownload withPostBody(String postBody) {
		this.postBody = postBody;
		return withMethod(POST); // auto-switch to POST
	}

	public InputStream getFileContent() {
		return fileContent;
	}

	public HttpDownload withFileContent(InputStream fileContent) {
		this.fileContent = fileContent;
		return withMethod(PUT); // auto-switch to PUT
	}

	public String getUsername() {
		return username;
	}

	public HttpDownload withUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public HttpDownload withUsernamePassword(String username, String password) {
		this.password = password;
		return withUsername(username);
	}

	public String getSoapAction() {
		return soapAction;
	}

	public HttpDownload withSoapAction(String soapAction) {
		this.soapAction = soapAction;
		return withMethod(POST); // auto-switch to POST
	}

	public HttpEntity getSoapData() {
		return soapData;
	}

	public HttpDownload withSoapData(HttpEntity soapData) {
		this.soapData = soapData;
		return withMethod(POST); // auto-switch to POST
	}
//</editor-fold>

	private HttpDownload withRedirectChain(List<String> redirectChain) {
		this.redirectChain = redirectChain;
		return this;
	}

	private void initRedirChain() {
		if (redirectChain == null) {
			// begin redirect tracking
			redirectChain = new ArrayList<String>();
		}
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

	public HttpDownload perform() {
		try {
			if (GET.equals(method)) {
				request = new HttpGet(uri);
			} else if (DELETE.equals(method)) {
				request = new HttpDelete(uri);
			} else if (PUT.equals(method)) {
				HttpPut httpPut = new HttpPut(uri);
				request = httpPut;
				httpPut.setEntity(new BufferedHttpEntity(new InputStreamEntity(fileContent)));
			} else if (POST.equals(method)) {
				HttpPost httpPost = new HttpPost(uri);
				request = httpPost;

				if (files != null) {
					// POST and
					MultipartEntity mpEntity = new MultipartEntity();
					if (postData != null) {
						for (Map.Entry<String, String> entry : postData.entrySet()) {
							mpEntity.addPart(entry.getKey(), new StringBody(entry.getValue()));
						}
					}
					// also FILE
					for (Map.Entry<String, ByteArrayBody> entry : files.entrySet()) {
						mpEntity.addPart(entry.getKey(), entry.getValue());
					}
					httpPost.setEntity(mpEntity);
				} else if (postBody != null) {
					if (postContentType == null) {
						postContentType = ContentType.APPLICATION_JSON;
					}
					StringEntity formData = new StringEntity(postBody, postContentType);
					httpPost.setEntity(formData);
				} else if (soapData != null) {
					httpPost.setHeader("Content-Type", "application/soap+xml; charset=utf-8");
					httpPost.setHeader("SOAPAction", soapAction);
					if (!soapData.isRepeatable()) {
						soapData = new BufferedHttpEntity(soapData);
					}
					httpPost.setEntity(soapData);
				} else {
					// normal POST
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();
					if (postData != null) {
						for (Map.Entry<String, String> entry : postData.entrySet()) {
							nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
						}
					}

					UrlEncodedFormEntity formData = new UrlEncodedFormEntity(nvps, Charsets.UTF_8);
					httpPost.setEntity(formData);
				}
			} else {
				return null;
			}

			addHeaderLinesToRequest(request);

			if (headerLines != null) {
				for (Map.Entry<String, String> entry : headerLines.entrySet()) {
					request.setHeader(entry.getKey(), entry.getValue());
				}
			}

			this.httpclient = fac.getDefaultHttpClient(request.getURI()); // URI is used to determine which proxy to use

			HttpClient realClient = getRealHttpClient();
			if (username != null && password != null && realClient instanceof DefaultHttpClient) {
				DefaultHttpClient dHttpclient = (DefaultHttpClient) realClient;
				CredentialsProvider credentialsProvider = dHttpclient.getCredentialsProvider();
				credentialsProvider.setCredentials(
						new AuthScope(uri.getHost(), uri.getPort()),
						new UsernamePasswordCredentials(username, password));
			}

			if (options != null) {
				HttpParams params = httpclient.getParams();
				for (Map.Entry<String, Object> entry : options.entrySet()) {
					params.setParameter(entry.getKey(), entry.getValue());
				}
			}

			response = httpclient.execute(request);
			statusLine = response.getStatusLine();
			entity = response.getEntity();

			// get cookiesToSend from redirect
			if (realClient instanceof AbstractHttpClient) {
				RedirectStrategy redirectStrategy = ((AbstractHttpClient) realClient).getRedirectStrategy();
				if (redirectStrategy instanceof TolerantRedirectStrategy) {
					TolerantRedirectStrategy tolerantRedirectStrategy = (TolerantRedirectStrategy) redirectStrategy;
					List<String> redirChain = tolerantRedirectStrategy.getRedirectChain();
					if (redirChain != null) {
						initRedirChain();
						redirectChain.addAll(redirChain);
					}
					redirCookies = tolerantRedirectStrategy.getReceivedCookies();
				}
			}

			Header newLocation = getFirstHeader("location");
			if (newLocation != null) {
				// add any new cookies or overwrite existing ones
				getCookiesBackend();
				if (cookies == null) {
					cookies = receivedCookies;
				} else if (receivedCookies != null) {
					cookies.putAll(receivedCookies);
				}

				// use GET on new location
				String newLocationValue = newLocation.getValue();
				try {
					URL newUrl = new URL(uri.toURL(), newLocationValue);
					return withURL(newUrl).withCookies(cookies).withReferer(url).perform();
				} catch (Exception ex) {
					return withURL(newLocationValue).withCookies(cookies).withReferer(url).withRedirectChain(redirectChain).perform();
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
			status = STATUS_FAILURE;
		}
		return this;
	}

	protected HttpClient getRealHttpClient() {
		if (httpclient instanceof DecompressingHttpClient) {
			return ((DecompressingHttpClient) httpclient).getHttpClient();
		}
		return httpclient;
	}

	private void addHeaderLinesToRequest(HttpRequestBase request) {
		// required by Kinesis
		request.setHeader(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
//		request.setHeader(new BasicHeader("Accept-Encoding", "gzip, deflate"));
		// get German texts
		request.setHeader(new BasicHeader("Accept-Language", "de-de,de;q=0.8,en-us;q=0.5,en;q=0.3"));
		if (referer != null) {
			request.setHeader(new BasicHeader("Referer", referer));
		}
		addCookiesToRequest(request);
	}

	private void addCookiesToRequest(HttpRequestBase request) {
		if (MapUtils.isEmpty(cookies)) {
			return;
		}
		// write cookie header
		List<String> cookieNvps = new ArrayList<String>();
		for (Map.Entry<String, String> entry : cookies.entrySet()) {
			cookieNvps.add(entry.getKey() + "=" + entry.getValue());
		}
		request.setHeader(new BasicHeader("Cookie", StringUtils.join(cookieNvps, ";")));
	}

	public Map<String, String> getCookiesHashMap() {
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

	protected static void addCookiesToMap(HttpResponse response, Map<String, String> receivedCookies) {
		if (response != null) {
			Header[] headers = response.getHeaders("Set-Cookie");
			for (int i = 0; i < headers.length; i++) {
				Header header = headers[i];
				String cookieNvp = StringUtils.substringBefore(header.getValue(), ";");
				receivedCookies.put(
						StringUtils.substringBefore(cookieNvp, "="),
						StringUtils.substringAfter(cookieNvp, "="));
			}
		}
	}

	private void getCookiesBackend() {
		this.receivedCookies = new HashMap<String, String>();

		// pre-set cookiesToSend
		if (cookies != null) {
			receivedCookies.putAll(cookies);
		}

		// collect all cookiesToSend from redirects
		if (redirCookies != null) {
			receivedCookies.putAll(redirCookies);
		}

		// parse cookiesToSend from header
		addCookiesToMap(response, receivedCookies);

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
		return getBindata(Long.MAX_VALUE);
	}

	public byte[] getBindata(long maxSize) {
		if (data != null) {
			return data.getBytes();
		}
		if (bindata == null) {
			long copied = 0;
			InputStream is = null;
			try {
				is = getInputStream();
				if (is != null) {
					// code copied to inline, to realize size limit
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					copied = IOUtils.copyLarge(is, baos, 0, maxSize);
					bindata = baos.toByteArray();

					status = STATUS_SUCCESS;
				} else {
					status = STATUS_FAILURE;
				}
			} catch (Exception ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
				status = STATUS_FAILURE;
			} finally {
				closeConnection(is, copied >= maxSize);
			}
		}
		return bindata;
	}

	public String getData() {
		return getData(null);
	}

	public String getData(long maxSize) {
		return getData(null, maxSize);
	}

	public String getData(String encoding) {
		return getData(encoding, Long.MAX_VALUE);
	}

	public String getData(String encoding, long maxSize) {
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
			long copied = 0;
			InputStream is = null;
			try {
				is = getInputStream();
				if (is != null) {
					Charset charset = Charsets.toCharset(encoding);
					if (maxSize < Long.MAX_VALUE) {
						// code copied to inline, to realize size limit
						StringBuilderWriter sw = new StringBuilderWriter();
						InputStreamReader in = new InputStreamReader(is, charset);
						copied = IOUtils.copyLarge(in, sw, 0, maxSize);
						data = sw.toString();
					} else {
						// more efficient
						data = IOUtils.toString(is, charset);
						copied = maxSize;
					}
					status = STATUS_SUCCESS;
				} else {
					status = STATUS_FAILURE;
				}
			} catch (Exception ex) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
				status = STATUS_FAILURE;
			} finally {
				closeConnection(is, copied >= maxSize);
			}
		}
		return data;
	}

	protected void closeConnection(final InputStream is, boolean doAbort) {
		if (doAbort) {
			// otherwise close will read infinitely
			request.abort();
			HttpClient realHttpClient = getRealHttpClient();
			if (realHttpClient instanceof CloseableHttpClient) {
				try {
					((CloseableHttpClient) realHttpClient).close();
				} catch (IOException ex) {
					Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		IOUtils.closeQuietly(is);
		request.releaseConnection();
	}

	public String getMimeType() {
		return StringUtils.substringBefore(entity.getContentType().getValue(), ";"); // remove any charset info if present
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

	public boolean isFailed() {
		if (status == STATUS_FAILURE) {
			return true;
		}
		int statusCode = statusLine.getStatusCode();
		return (statusCode >= 400 && statusCode < 600);
	}

	public int getStatusCode() {
		if (statusLine != null) {
			return statusLine.getStatusCode();
		}
		return -1;
	}

	public String getStatusText() {
		if (statusLine != null) {
			return statusLine.getReasonPhrase();
		}
		return null;
	}
}
