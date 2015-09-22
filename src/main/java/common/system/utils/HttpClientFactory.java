/*******************************************************************************
 * Copyright 2015 Felix Rudolphi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common.system.utils;

import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

/**
 *
 * @author fr
 */
public class HttpClientFactory {

	/**
	 * overwrite this method to implement a client with configured proxy.
	 *
	 * @param uri
	 * @return
	 */
	protected HttpClient getDefaultHttpClient(URI uri) {

		HttpParams httpParams = new BasicHttpParams();

		int socketTimeout = 20, connectionTimeout = 20;
		httpParams.setParameter("http.socket.timeout", socketTimeout * 1000);
		httpParams.setParameter("http.connection.timeout", connectionTimeout * 1000);
		httpParams.setParameter(CoreProtocolPNames.HTTP_ELEMENT_CHARSET, "UTF-8");

		return getDefaultHttpClient(httpParams);
	}

	/**
	 * standard procedure to avoid errors caused by HttpClient being too strict.
	 * Should not normally not be overwritten.
	 *
	 * @param httpParams
	 * @return
	 */
	protected AbstractHttpClient getDefaultHttpClient(HttpParams httpParams) {
		TolerantRedirectStrategy tolerantRedirectStrategy = new TolerantRedirectStrategy();

		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
		httpclient.setRedirectStrategy(tolerantRedirectStrategy);
		httpclient = wrapClient(httpclient);
		httpclient.setRedirectStrategy(tolerantRedirectStrategy);

		return httpclient;
	}

	public static DefaultHttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			return null;
		}
	}

	/*public RequestConfig.Builder getRequestConfig() {
		int socketTimeout = 20, connectionTimeout = 20;

		return RequestConfig.custom().
				setConnectTimeout(connectionTimeout * 1000).
				setSocketTimeout(socketTimeout * 1000);
	}

	public ConnectionConfig.Builder getConnectionConfig() {
		return ConnectionConfig.custom().setCharset(Charset.forName(Constants.UTF8));
	}*/
}
