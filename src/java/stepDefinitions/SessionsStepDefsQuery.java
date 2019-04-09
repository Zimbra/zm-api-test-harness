package stepDefinitions;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import harness.HarnessContext;
import harness.utils.StringUtils;
import junit.framework.Assert;
import stepDefinitions.BaseStepDefs;

/**
 * @author piyush.nimoria
 *
 */
public class SessionsStepDefsQuery extends BaseStepDefs {
	private Scenario scenario;
	private StringUtils stringUtils;
	private HarnessContext context;
	private String sessionQuery;
	private String expectedUserAgent = "Apache-HttpClient";

	public SessionsStepDefsQuery() {
		sessionQuery = "'query':'query GetSessions { sessions {sessionId, createdDate, lastAccessed, requestIPAddress, userAgent, browserInfo} }'";
		stringUtils = new StringUtils();
	}

	public SessionsStepDefsQuery(HarnessContext context) {
		this();
		this.context = context;
	}

	@Before
	public void BeforeSearchFolderMutations(Scenario scenario) {
		this.scenario = scenario;
	}

	@When("^user queries session information$")
	public void querySession() {
		String variables = "'variables':{}";
		String requestBody = "{" + sessionQuery + " , " + variables + "}";
		logger.info(requestBody);
		scenario.write("Query being processed :" + requestBody);
		baseline.processRequest(context, requestBody, "Post");
	}

	@When("^validate valid response is returned for '(.+)'$")
	public void verifySessionResponse(String param) {
		boolean flag = false;
		String jsonPath = "data.sessions[0]." + param;
		String getVal = "", longVal = "";
		switch (param) {

		case "sessionId":
			try {
				getVal = baseline.getValue(context, jsonPath);
				Integer.parseInt(getVal);
				flag = true;
			} catch (NumberFormatException e) {
				flag = false;
				scenario.write("returned value is non integer" + getVal);
			}
			Assert.assertTrue("Validate " + param + " is valid", flag);
			break;

		case "createdDate":
		case "lastAccessed":
			try {
				longVal = baseline.getValue(context, jsonPath);
				flag = (longVal.length() == 13) ? true : false;
				if (flag) {
					new BigInteger(longVal);
				}
			} catch (NumberFormatException e) {
				flag = false;
				scenario.write("returned value is non epoch time" + longVal);
			} catch (ClassCastException e) {
				flag = false;
				scenario.write("returned value is non epoch time" + longVal);
			}
			Assert.assertTrue("Validate " + param + " is epoch format", flag);
			break;

		case "requestIPAddress":
			try {
				getVal = baseline.getValue(context, jsonPath);
				flag = validateIpAddress(getVal);
			} catch (Exception e) {
				flag = false;
				scenario.write("returned value is invalid IP Address" + getVal);
			}
			Assert.assertTrue("Validate " + param + " is valid IP Address", flag);
			break;

		case "userAgent":
			try {
				getVal = baseline.getValue(context, jsonPath);
				flag = (getVal.contains(expectedUserAgent)) ? true : false;
			} catch (Exception e) {
				flag = false;
				scenario.write("returned value is invalid IP Address" + getVal);
			}
			Assert.assertTrue("Validate " + param + " is valid user agent", flag);
			break;
		}
	}

	public static boolean validateIpAddress(String ip) {
		boolean isValidIp;
		try {
			final InetAddress inet = InetAddress.getByName(ip);
			isValidIp = inet.getHostAddress().equals(ip) && inet instanceof Inet4Address;
		} catch (final UnknownHostException e) {
			isValidIp = false;
		}
		return isValidIp;
	}

}
