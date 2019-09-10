package stepDefinitions;

import java.util.HashMap;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import harness.HarnessContext;

/**
 * @author swapnil.pingle
 *
 */
public class GenericStepDefs extends BaseStepDefs {
	private Scenario scenario;
	private HarnessContext context;

	public GenericStepDefs() {
		System.out.println("Initializing GenericStepdefs");
	}

	public GenericStepDefs(HarnessContext context) {
		this();
		this.context = context;
		System.out.println("Initializing GenericStepdefs");
	}

	@Before
	public void beforeGenericStepDefs(Scenario scenario) {
		this.scenario = scenario;
	}

	@Given("^Application url '(.+)' and path '(.+)'$")
	public void setURI(String host, String paths) {
		String server = globalProperties.getProperty("server");
		String scheme = globalProperties.getProperty("scheme", "https");
		String port = host.contains("admin") ? globalProperties.getProperty("adminPort") : globalProperties.getProperty("port");
		String path = globalProperties.getProperty(paths);
		scenario.write("server :" + server);
		scenario.write("scheme :" + scheme);
		scenario.write("port :" + port);
		scenario.write("path :" + path);
		baseline.setUrl(context, server, scheme, port, path, server);
	}

	@Given("^Request headers '(.+)'$")
	public void setHeaders(String headers) {
		HashMap<String, String> tempHeader = new HashMap<String, String>();
		for (String current : headers.split(",")) {
			String[] headerDetails = current.split(":");
			String headerName = headerDetails[0];
			tempHeader.put(headerName, headerDetails[1]);
			baseline.setHeader(context, tempHeader);
			scenario.write("Added Header:" + current);
		}
	}

	@Then("^Validate status is '(.+)'$")
	public void validateStatus(int status) {
		scenario.write("Validating that status of the response is " + status);
		baseline.validateStatus(context, status);
	}
}
