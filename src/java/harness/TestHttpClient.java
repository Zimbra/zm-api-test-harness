package harness;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.cookie.BasicClientCookie;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * @author swapnil.pingle
 *
 */
public class TestHttpClient {

	private static RequestBuilder requestBuilder;
	private static CookieStore cookieStore;
	private static HttpClientBuilder clientBuilder;
	private static CloseableHttpResponse httpResponse;
	static byte[] bytes;
	private static final String[] PRINTABLES = { "json", "xml", "text", "urlencoded", "html" };
	public static final String CHARSET = "charset";
	private static final String EMPTY = "";

	public static void main(String[] args) {
		/*
		 * ====================================================================
		 * String text =
		 * "{'query':'{ folder(getFolder: {folderId: \"16\"}) { name}}'}" ;
		 * DocumentContext json = JsonUtils.toJsonDoc(text); BasicCookieStore
		 * cookieStore = new BasicCookieStore(); BasicClientCookie cookie = new
		 * BasicClientCookie("ZM_AUTH_TOKEN",
		 * "0_840258f75fb85b2caebb60661151bd3f0ed1973c_69643d33363a33363731393331362d636436392d346161332d623363662d6638393133373737376531343b6578703d31333a313533333830353031373838393b747970653d363a7a696d6272613b753d313a613b7469643d31303a313739363437373631343b76657273696f6e3d31343a382e382e31305f47415f323032303b637372663d313a313b"
		 * ); cookieStore.addCookie(cookie);
		 * 
		 * CloseableHttpClient client = HttpClients.createDefault(); HttpPost
		 * method = new HttpPost(
		 * "https://zdev-vm002.eng.zimbra.com/service/extension/graphql" );
		 * StringEntity requestEntity = new StringEntity(text,
		 * ContentType.APPLICATION_JSON); method.setEntity(requestEntity);
		 * HttpUriRequest httpRequest = requestBuilder.build();
		 * 
		 * ====================================================================
		 */
		try {
			requestBuilder = prepareRequestBuilder();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		clientBuilder = prepareClientBuilder();

		CloseableHttpClient client = clientBuilder.build();
		HttpUriRequest httpRequest = requestBuilder.build();
		try {
			httpResponse = client.execute(httpRequest);
			InputStream is = httpResponse.getEntity().getContent();

			bytes = toByteStream(is).toByteArray();
			String res = new String(bytes);
			System.out.println("Response is :" + res);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static HttpClientBuilder prepareClientBuilder() {
		clientBuilder = HttpClientBuilder.create();
		clientBuilder.setRedirectStrategy(new LaxRedirectStrategy());
		cookieStore = new BasicCookieStore();
		String cookieName = "ZM_AUTH_TOKEN";
		String cookieValue = "0_42fea4331014a70c5fdec48375948a80c5f7e741_69643d33363a39623735393765612d643961382d343131632d383733302d3933376638313431326433343b6578703d31333a313533383134353131343533343b747970653d363a7a696d6272613b753d313a613b7469643d393a3832363531343336383b76657273696f6e3d31343a382e382e31305f47415f333033343b";
		BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
		cookie.setDomain(requestBuilder.getUri().getHost());
		cookieStore.addCookie(cookie);
		clientBuilder.setDefaultCookieStore(cookieStore);
		RequestConfig.Builder configBuilder = RequestConfig.custom().setConnectTimeout(-1).setSocketTimeout(-1);
		clientBuilder.setDefaultRequestConfig(configBuilder.build());

		return clientBuilder;
	}

	public static RequestBuilder prepareRequestBuilder() throws URISyntaxException {
		String method = "POST";
		RequestBuilder requestBuilder;
		URIBuilder uriBuilder;
		String url = "https://zdev-vm002.eng.zimbra.com";
		uriBuilder = new URIBuilder(url);
		String temp = uriBuilder.getPath();
		String path = "/service/extension/graphql";
		uriBuilder.setPath(temp + path);
		URI uri = uriBuilder.build();
		requestBuilder = RequestBuilder.create(method).setUri(uri);
		String text = "{'query':'{ folder(getFolder: {folderId: \"16\"}) { name}}'}";
		HttpEntity entity = getEntity(text);
		requestBuilder.setEntity(entity);
		requestBuilder.setHeader(entity.getContentType());
		return requestBuilder;
	}

	private static ContentType getContentType(String mediaType, Charset charset) {
		if (!isPrintable(mediaType)) {
			try {
				return ContentType.create(mediaType);
			} catch (Exception e) {
				return null;
			}
		}
		Map<String, String> map = parseContentTypeParams(mediaType);
		if (map != null) {
			String cs = map.get(CHARSET);
			if (cs != null) {
				charset = Charset.forName(cs);
				map.remove(CHARSET);
			}
		}
		ContentType ct = ContentType.parse(mediaType).withCharset(charset);
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				ct = ct.withParameters(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		return ct;
	}

	public static boolean isPrintable(String mediaType) {
		if (mediaType == null) {
			return false;
		}
		String type = mediaType.toLowerCase();
		for (String temp : PRINTABLES) {
			if (type.contains(temp)) {
				return true;
			}
		}
		return false;
	}

	public static Map<String, String> parseContentTypeParams(String mimeType) {
		List<String> items = split(mimeType, ';');
		int count = items.size();
		if (count <= 1) {
			return null;
		}
		Map<String, String> map = new LinkedHashMap(count - 1);
		for (int i = 1; i < count; i++) {
			String item = items.get(i);
			int pos = item.indexOf('=');
			if (pos == -1) {
				continue;
			}
			String key = item.substring(0, pos).trim();
			String val = item.substring(pos + 1).trim();
			map.put(key, val);
		}
		return map;
	}

	public static List<String> split(String s, char delimiter) {
		int pos = s.indexOf(delimiter);
		if (pos == -1) {
			return Collections.singletonList(s);
		}
		List<String> list = new ArrayList();
		int startPos = 0;
		while (pos != -1) {
			String temp = s.substring(startPos, pos);
			if (!EMPTY.equals(temp)) {
				list.add(temp);
			}
			startPos = pos + 1;
			pos = s.indexOf(delimiter, startPos);
		}
		if (startPos != s.length()) {
			String temp = s.substring(startPos);
			if (!EMPTY.equals(temp)) {
				list.add(temp);
			}
		}
		return list;
	}

	public static StringEntity getEntity(String value) {
		try {
			String mediaType = "application/json";
			Charset charset = Charset.forName("UTF-8");
			ContentType ct = getContentType(mediaType, charset);
			String jsonString = toJsonDoc(value).jsonString();
			if (ct == null) { // "bad" value such as an empty string
				StringEntity entity = new StringEntity(jsonString);
				entity.setContentType(mediaType);
				return entity;
			} else {
				return new StringEntity(jsonString, ct);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/*
	 * * protected void buildCookie(com.intuit.karate.http.Cookie c) {
	 * BasicClientCookie cookie = new BasicClientCookie(c.getName(),
	 * c.getValue()); for (Entry<String, String> entry : c.entrySet()) { switch
	 * (entry.getKey()) { case DOMAIN: cookie.setDomain(entry.getValue());
	 * break; case PATH: cookie.setPath(entry.getValue()); break; } } if
	 * (cookie.getDomain() == null) { cookie.setDomain(uriBuilder.getHost()); }
	 * cookieStore.addCookie(cookie); }
	 */

	private static ByteArrayOutputStream toByteStream(InputStream is) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		try {
			while ((length = is.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			return result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static DocumentContext toJsonDoc(String raw) {
		return JsonPath.parse(raw);
	}
}
