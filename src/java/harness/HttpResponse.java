package harness;

import java.util.List;
import com.jayway.jsonpath.DocumentContext;

/**
 * @author swapnil.pingle
 *
 */
public class HttpResponse {

	private String uri;
	private MultiValuedMap headers;
	private DocumentContext body;
	private int status;
	private final long startTime;
	private final long endTime;

	// make sure client implementations don't forget to set response time
	public HttpResponse(long startTime, long endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getResponseTime() {
		return endTime - startTime;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public MultiValuedMap getHeaders() {
		return headers;
	}

	public DocumentContext getBody() {
		return body;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setBody(DocumentContext body) {
		this.body = body;
	}

	public void addHeader(String name, String value) {
		if (headers == null) {
			headers = new MultiValuedMap();
		}
		headers.add(name, value);
	}

	public void putHeader(String name, List values) {
		if (headers == null) {
			headers = new MultiValuedMap();
		}
		headers.put(name, values);
	}

}
