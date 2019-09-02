package harness;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import harness.utils.JsonDoc;

/**
 * @author swapnil.pingle
 *
 */
public class HttpRequest {

	private HttpEntity entity;
	private Map<String, String> header;
	private JsonDoc jsonDoc = new JsonDoc();
	private String method;
	private RequestBuilder requestBuilder;
	private URIBuilder uriBuilder;
	private String url;
	private String path;
	private String domain;
	private String query;
	private String mediaType;
	private Charset charset;
	private ContentType contentType;

	public HttpEntity getEntity() {
		return entity;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public RequestBuilder prepareRequestBuilder() throws URISyntaxException {
		uriBuilder = new URIBuilder(url);
		String temp = uriBuilder.getPath();
		uriBuilder.setPath(temp + path);
		URI uri = uriBuilder.build();
		requestBuilder = RequestBuilder.create(method.toUpperCase()).setUri(uri);

		if (query != null)
		{
				HttpEntity entity = prepareEntity(query);
				requestBuilder.setEntity(entity);
		}
		if (header != null) {
    		for (String currentHeader : header.keySet()) {
    			requestBuilder.setHeader(currentHeader, header.get(currentHeader));
    		}
		}

		return requestBuilder;
	}

	private StringEntity prepareEntity(String value) {
		try {
			String jsonString = jsonDoc.toJsonDoc(value).jsonString();
			if (contentType == null) { // "bad" value such as an empty string
				StringEntity entity = new StringEntity(jsonString);
				    entity.setContentType(mediaType);
				return entity;
			} else {
				return new StringEntity(jsonString, contentType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
