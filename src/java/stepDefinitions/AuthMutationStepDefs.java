package stepDefinitions;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import exception.HarnessException;
import harness.HarnessContext;
import harness.utils.StringUtils;

import org.junit.Assert;

/**
 * @author swapnil.pingle
 *
 */
public class AuthMutationStepDefs extends BaseStepDefs {

	private String authMutation;
	public static String authToken;
	private String authTokenJsonPath = "data.authenticate.authToken";
	private Scenario scenario;
	private StringUtils stringUtils;
	private HarnessContext context;

	public AuthMutationStepDefs() {
		authMutation = "'query':'mutation authMutation_UserName_pwd($authInput:AuthRequestInput!){ authenticate(authInput: $authInput) { authToken }}'";
		stringUtils = new StringUtils();
	}

	public AuthMutationStepDefs(HarnessContext context) {
		this();
		this.context = context;
	}

	@Before
	public void BeforeBaseLine(Scenario scenario) {
		this.scenario = scenario;
	}

	@When("^Get '(.+)' '(.+)' authToken for username '(.+)' and password '(.+)' using '(.+)' method$")
	public void authRequestUserNamePassword(String accountType, String tokenType, String userName, String password,
			String method) {
		String variables = "'variables':{'authInput': {'account': {'key': '" + userName
				+ "', 'accountBy': 'name'}, 'password': '" + password + "','tokenType':'"
				+ tokenType.trim().toUpperCase() + "'} }";

		String requestBody = "{" + authMutation + " , " + variables + "}";
		logger.info(requestBody);
		scenario.write("AuthMutation being processed :" + requestBody);
		baseline.processRequest(context, requestBody, method);
		authToken = (String) baseline.getValue(context, authTokenJsonPath);
		scenario.write("AuthToken returned is :" + authToken);
		// Assert.assertTrue("Verify that non empty authToken is returned",
		// stringUtils.isEmpty(authToken));

	}

	@Then("^Validate authToken is '(.+)' '(.+)' token$")
	public void validateAuthTokenType(String validity, String type) throws HarnessException {

		scenario.write("Fetched authToken for type '" + type + "' is '" + authToken + "'");
		int size = authToken.split("\\.").length;
		if (validity.equalsIgnoreCase("valid")) {
			if (stringUtils.isEmpty(authToken)) {
				throw new HarnessException("Something went wrong! Empty authToken returned.");
			}
			switch (type) {
			case "JWT":
				if (validity.equalsIgnoreCase("valid")) {
					Assert.assertTrue("Verify that JWT token '" + authToken + "' is valid", size == 3);
				} else {
					// Assert.assertFalse("Verify that JWT token is valid", size
					// ==
					// 3);
				}
				break;
			case "zimbraAuthToken":
			default:
				Assert.assertTrue("Verify that zimbraAuth token '" + authToken + "' is valid", size == 1);
				break;
			}
		} else {
			Assert.assertEquals("For invalid data empty authToken should be returned", "", authToken);
		}
	}

	@Given("^set AuthToken in the cookie$")
	public void setAuthToken() throws HarnessException {
		if (!stringUtils.isEmpty(authToken)) {
			baseline.setCookies(context, "ZM_AUTH_TOKEN", authToken);
		} else {
			throw new HarnessException("Something went wrong! Empty authToken returned.");
		}
	}

	@Given("^authToken is present in the cookies$")
	public void checkAuthTokenExists() throws Exception {
		Assert.assertTrue("Check if cookie contains authToken", baseline.isAuthTokenPresent(context));
	}
}
