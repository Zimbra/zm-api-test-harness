package harness;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;

/**
 * @author swapnil.pingle
 *
 */
public class HarnessClient {

	private HttpClientBuilder clientBuilder;
	private BasicCookieStore cookieStore;
	private final static int TIMEOUT = -1;

	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public HttpClientBuilder prepareClientBuilder(HttpUriRequest requestBuilder) {
		clientBuilder = HttpClientBuilder.create();
		clientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
		clientBuilder.setDefaultCookieStore(cookieStore);
		RequestConfig.Builder configBuilder = RequestConfig.custom().setConnectTimeout(TIMEOUT)
				.setSocketTimeout(TIMEOUT);
		clientBuilder.setDefaultRequestConfig(configBuilder.build());
		return clientBuilder;
	}
}
