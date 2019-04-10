package harness;

import harness.client.HarnessClient;

/**
 * @author swapnil.pingle
 *
 */
public class HarnessContext {

	private static HttpRequest request;
	private static HttpResponse response;
	private static HarnessClient harnessClient;
	private static HarnessContext context;
	private static Object runtimeData;

	public HarnessContext() {
		getInstance();
		System.out.println("in harness context");

	}

	public static Object getRuntimeData() {
		return runtimeData;
	}

	public static void setRuntimeData(Object runtimeData) {
		HarnessContext.runtimeData = runtimeData;
	}

	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public HarnessClient getHarnessClient() {
		return harnessClient;
	}

	public void setHarnessClient(HarnessClient harnessClient) {
		this.harnessClient = harnessClient;
	}

	public HarnessContext getInstance() {
		if (this.context == null) {

			this.request = new HttpRequest();
			this.harnessClient = new HarnessClient();
		}
		this.context = this;
		return context;

	}

}
