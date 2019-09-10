package harness.client;

import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

/**
 * @author swapnil.pingle
 *
 */
@SuppressWarnings("deprecation")
public class HarnessClient {

	private HttpClientBuilder clientBuilder;
	private BasicCookieStore cookieStore;
	private final static int TIMEOUT = -1;
	private SSLConnectionSocketFactory factory;

	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public HttpClientBuilder prepareClientBuilder(HttpUriRequest requestBuilder) throws Exception {
		factory = new HttpClientFactory().getSocketFactory();
		clientBuilder = HttpClientBuilder.create();
		clientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
		clientBuilder.setDefaultCookieStore(cookieStore);
		clientBuilder.setSSLSocketFactory(factory);
		SSLContext sslcontext = SSLContexts.custom().useSSL().build();
		sslcontext.init(null, new X509TrustManager[]{new HttpsTrustManager()}, new SecureRandom());
		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslcontext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		clientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
		clientBuilder.setDefaultCookieStore(cookieStore);
		clientBuilder.setSSLSocketFactory(factory);

		RequestConfig.Builder configBuilder = RequestConfig.custom().setConnectTimeout(TIMEOUT)
				.setSocketTimeout(TIMEOUT);

		clientBuilder.setDefaultRequestConfig(configBuilder.build());
		return clientBuilder;
	}
}
