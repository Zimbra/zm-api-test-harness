package baseLine;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.Assert;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import exception.HarnessException;
import net.minidev.json.JSONArray;
import harness.HarnessContext;
import harness.HttpRequest;
import harness.HttpResponse;
import harness.client.HarnessClient;
import harness.utils.StringUtils;

/**
 * @author swapnil.pingle
 *
 */
public class Baseline {

	private static HttpRequest request;
	private static HttpResponse response;
	public static CloseableHttpResponse executionResponse;
	public static final String authCookieName = "ZM_AUTH_TOKEN";
	private static HarnessClient harnessClient;
	private StringUtils stringUtils;
	private long startTime;
	private long endTime;

	public Baseline() {
		harnessClient = new HarnessClient();
		// request = new HttpRequest();
		stringUtils = new StringUtils();
	}

	public boolean validateJWTAuthToken(String authToken) {
		return (authToken.split(".").length > 3);
	}

	public HttpResponse processRequest(HarnessContext context, String requestBody, String method) {
		request = context.getRequest();
		request.setQuery(requestBody);
		request.setMethod(method);
		request.setMediaType("application/json");
		context.setRequest(request);
		return executeRequest(context);
	}

	public void validateStatus(HarnessContext context, int statusCode) {
		context.setResponse(response);
		Assert.assertTrue("Actual Status code verification with expected statusCode " + statusCode,
				response.getStatus() == statusCode);
	}

	public void setUrl(HarnessContext context, String host, String scheme, String port, String path, String domain) {
		request = context.getRequest();
		String url = scheme + "://" + host + ":" + port;
		request.setUrl(url);
		request.setPath(path);
		request.setDomain(domain);
		context.setRequest(request);
		System.out.println(url);
	}

	public void setCookies(HarnessContext context, String cookieName, String cookieValue) {
		request = context.getRequest();
		BasicCookieStore cookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie(cookieName, cookieValue);
		cookie.setDomain(request.getDomain());
		cookie.setPath(request.getPath());
		cookieStore.addCookie(cookie);
		harnessClient.setCookieStore(cookieStore);
		context.setHarnessClient(harnessClient);
	}

	public HttpResponse executeRequest(HarnessContext context) {
		try {
			request = context.getRequest();
			harnessClient = context.getHarnessClient();
			HttpUriRequest requestBuilder = request.prepareRequestBuilder().build();
			CloseableHttpClient client = harnessClient.prepareClientBuilder(requestBuilder).build();
			startTime = System.currentTimeMillis();
			executionResponse = client.execute(requestBuilder);
			endTime = System.currentTimeMillis();
			return setResponse(context, executionResponse);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		return null;
	}

	// jsonDoc.toJsonDoc(value).jsonString();
	public HttpResponse setResponse(HarnessContext context, CloseableHttpResponse actualResponse) {
		response = new HttpResponse(startTime, endTime);
		response.setStatus(actualResponse.getStatusLine().getStatusCode());
		// response.setBody(body);
		InputStream is;
		try {
			is = actualResponse.getEntity().getContent();
			byte[] bytes = stringUtils.toByteStream(is).toByteArray();
			String res = new String(bytes);
			System.out.println("response is #" + res + "#");
			DocumentContext respBody = JsonPath.parse(res);
			response.setBody(respBody);
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.setResponse(response);
		return response;
	}

	public String getValue(HarnessContext context, String jsonPath) {
		jsonPath = "$." + jsonPath;
		response = context.getResponse();
		Object res = "";
		if(response == null){
			throw new HarnessException("Response is null");
		}
		try {
			res = response.getBody().read(jsonPath);
			return res instanceof Long ? String.valueOf(res) : res.toString();
		} catch (PathNotFoundException e) {
			return "";
		}
	}

	public JSONArray getValues(HarnessContext context, String jsonPath) {
	        jsonPath = "$." + jsonPath;
	        response = context.getResponse();
	        if(response == null){
	            throw new HarnessException("Response is null");
	        }
	        try {
	            return response.getBody().read(jsonPath);
	        } catch (PathNotFoundException e) {
	            return null;
	        }
	    }

	public void setHeader(HarnessContext context, HashMap<String, String> header) {
		request = context.getRequest();
		request.setHeader(header);
		context.setRequest(request);
		context.setRequest(request);
	}

	public boolean isAuthTokenPresent(HarnessContext context) {
		harnessClient = context.getHarnessClient();
		List<Cookie> cookieList = harnessClient.getCookieStore().getCookies();
		if (!cookieList.isEmpty()) {
			for (Cookie currentCookie : cookieList) {
				if (currentCookie.getName().equals(authCookieName)) {
					return (!stringUtils.isEmpty(currentCookie.getValue()));
				}
			}
		}
		return false;
	}

}
