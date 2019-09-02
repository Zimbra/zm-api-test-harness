package stepDefinitions;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;

import org.apache.http.client.methods.HttpGet;

import org.apache.http.client.methods.HttpPut;

import org.apache.http.client.methods.HttpDelete;
import org.junit.Assert;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import exception.HarnessException;
import harness.HarnessContext;
import harness.HttpRequest;
import harness.HttpResponse;
import harness.utils.StringUtils;

public class SmxProxyStefDefs extends BaseStepDefs {
    private static HttpRequest request;
    private String authMutation;
    public static String authToken;
    private Scenario scenario;
    private StringUtils stringUtils;
    private HarnessContext context;
    private String authTokenJsonPath = "data.authenticate.authToken";
    
    public SmxProxyStefDefs() {
        authMutation = "'query':'mutation authMutation_UserName_pwd($authInput:AuthRequestInput!){ authenticate(authInput: $authInput) { authToken }}'";

        stringUtils = new StringUtils();
    }

    public SmxProxyStefDefs(HarnessContext context) {
        this();
        this.context = context;
    }

    @Before
    public void BeforeBaseLine(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("^Get authToken for username \"([^\"]*)\" and password \"([^\"]*)\" using \"([^\"]*)\" method$")
    public void authRequestUserNamePassword(String accountType, String tokenType, String userName, String password,
            String method) {
        userName = generateUserName(userName);
        String variables = "'variables':{'authInput': {'account': {'key': '" + userName
                + "', 'accountBy': 'name'}, 'password': '" + password + "','tokenType':'"
                + tokenType.trim().toUpperCase() + "'} }";

        String requestBody = "{" + authMutation + " , " + variables + "}";
        logger.info(requestBody);
        scenario.write("AuthMutation being processed :" + requestBody);
        baseline.processRequest(context, requestBody, method);
        authToken = baseline.getValue(context, authTokenJsonPath);
        scenario.write("AuthToken returned is :" + authToken);

    }
    @Given("^Given set SmxAuthToken in the cookie$")
    public void setAuthToken() throws HarnessException {
        String authToken = "0_dec1efa988e293ac75371eae6bb277809f924c46_69643d33363a31313962353730382d643136642d346235652d616232362d6132666136306464363664653b6578703d31333a313536373531323432363039383b747970653d363a7a696d6272613b753d313a613b7469643d393a3932393431393839313b76657273696f6e3d31353a382e392e305f626574615f313030303b637372663d313a313b";;;
        if (!stringUtils.isEmpty(authToken)) {
            baseline.setCookies(context, "ZM_AUTH_TOKEN", authToken);
        } else {
            throw new HarnessException("Something went wrong! Empty authToken returned.");
        }
    }

    @Given("^Set SmxProxy Host and Port details")
    public void setSmxProxyUri() {

        String server = globalProperties.getProperty("smxProxyHost");
        String scheme = globalProperties.getProperty("scheme", "https");
        String port = globalProperties.getProperty("smxProxyPort", "8443");
        String path = globalProperties.getProperty("smxProxyPath");

        scenario.write("server :" + server);
        scenario.write("scheme :" + scheme);
        scenario.write("port :" + port);
        scenario.write("path :" + path);
        baseline.setUrl(context, server, scheme, port, path, server);
    }
    @When("^Get Smx Mailbox$")
    public void getMailbox() {
        HttpResponse response = baseline.processRequest(context, null, HttpGet.METHOD_NAME);

        // System.out.println("GET MAILBOX == ");
        context.setResponse(response);
    }

    @Given("^Request headers \"([^\"]*)\"$")
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

    @When("^Post Smx Mailbox Whitelists$")
    public void postMailboxWhitelists() {
        request = context.getRequest();
        request.setPath(request.getPath() + "add-whitelists");
        context.setRequest(request);
        String jsonBody = "{ \"add\" : [  {\"type\": \"EMAIL-ADDRESS\", \"target\": \"three3@example.com\"}]}";
        HttpResponse response = baseline.processRequest(context, jsonBody, HttpPost.METHOD_NAME);

        //System.out.println("POST MAILBOX WHITELISTS == ");
        context.setResponse(response);
    }
    
    @Then("^Validate Smx Mailbox$")
    public void validateMailbox() {
        String jsonPath = "address";
        String respValue = baseline.getValue(context, jsonPath);
        scenario.write("Actual value in the response: " + respValue);
        scenario.write("Expected value: [\"testajsmx@smxdev.zimbrax.com\"]");
        Assert.assertEquals("testajsmx@smxdev.zimbrax.com", respValue.toString());

    }

    @Then("^Validate Smx Mailbox Whitelists$")
    public void validateMailboxWhitelists() {
        int respStatus = context.getResponse().getStatus();
        scenario.write("Actual value in the response status: " + respStatus);
        scenario.write("Expected value: [200]");
        Assert.assertEquals(200,respStatus );

    }
}
