package stepDefinitions;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.junit.Assert;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import exception.HarnessException;
import harness.HarnessContext;
import harness.utils.StringUtils;
import net.minidev.json.JSONObject;

/**
 * @author swapnil.pingle
 *
 */
public class AdminAuthMutationStepDefs extends BaseStepDefs {

    private String authMutation;
    public static String authToken;
    private String authTokenJsonPath = "data.authenticate.authToken";
    private Scenario scenario;
    private StringUtils stringUtils;
    private HarnessContext context;

    public AdminAuthMutationStepDefs() {
        authMutation = "'query':'mutation auth($authInput:AdminAuthRequestInput!){ authenticate(authInput: $authInput) { authToken, csrfToken, lifetime }}'";
        stringUtils = new StringUtils();
    }

    public AdminAuthMutationStepDefs(HarnessContext context) {
        this();
        this.context = context;
    }

    @Before
    public void BeforeBaseLine(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("^Generate admin authToken for user '(.+)' and password '(.+)'$")
    public void authRequestUserNamePassword(String username, String password) {

        Map<String, Object> authInput = new HashMap<String, Object>();
        Map<String, Object> account = new HashMap<String, Object>();
        Map<String, Object> varMap = new HashMap<String, Object>();
        account.put("accountBy", "name");
        account.put("key", username);
        authInput.put("account", account);
        authInput.put("password", password);
        varMap.put("authInput", authInput);
        JSONObject json = new JSONObject(varMap);
        String authenticate = json.toJSONString();
        String variables = "'variables': " + authenticate;
        String requestBody = "{" + authMutation + " , " + variables + "}";
        System.out.println(requestBody);
        logger.info(requestBody);
        baseline.processRequest(context, requestBody, HttpPost.METHOD_NAME);
        authToken = baseline.getValue(context, authTokenJsonPath);
        scenario.write("AuthToken returned is :" + authToken);

    }
    @Given("^set adminAuthToken in the cookie$")
    public void setAuthToken() throws HarnessException {
        if (!stringUtils.isEmpty(authToken)) {
            baseline.setCookies(context, "ZM_ADMIN_AUTH_TOKEN", authToken);
        } else {
            throw new HarnessException("Something went wrong! Empty authToken returned.");
        }
    }
    
    @Then("^Validate token is '(.+)' '(.+)' token$")
    public void validateToken(String validity, String type){

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
                    // Assert.assertFalse("Verify that JWT token is valid", size == 3);
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

    @Given("^adminAuthToken is present in the cookies$")
    public void checkAuthTokenExists() throws Exception {
        Assert.assertTrue("Check if cookie contains authToken", baseline.isAdminAuthTokenPresent(context));
    }
}
