package stepDefinitions;

import org.junit.Assert;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import harness.HarnessContext;
import harness.utils.StringUtils;

public class ChangePasswordMutationStepDefs extends BaseStepDefs {
    private String changePwdMutation;
    public String authToken,lifeTime;
    private String authTokenPath="data.passwordChange.value";
    private String lifeTimePath="data.passwordChange.lifetime";
    private Scenario scenario;
    private HarnessContext context;
    private StringUtils stringUtils;
    
    public ChangePasswordMutationStepDefs(){
        changePwdMutation = "'query': 'mutation cp($op:String!, $np:String!, $account:AccountSelectorInput){passwordChange(oldPassword:$op, newPassword:$np, accountSelector:$account){value,lifetime}}'";
        stringUtils = new StringUtils();
    }
    
    public ChangePasswordMutationStepDefs(HarnessContext context){
        this(); this.context = context;
    }
    
    @Before
    public void beforeChangePasswordMutations(Scenario scenario){
        this.scenario = scenario;
    }
    
    @When("^'(.+)' changes old password '(.+)' to new password '(.+)' for '(.+)' using '(.+)'$")
    public void changePassword(String user, String oPwd, String nPwd, String vHostName, String identifier){
        vHostName = vHostName.equalsIgnoreCase("default")? "":vHostName;
        String variable = "'variables':{'op': '" + oPwd + "','np': '" + nPwd + "','account': {'key': '" + user
                + "','accountBy': '" + identifier.toLowerCase() + "'}}";
        String requestBody = "{"+changePwdMutation+ " , "+ variable+"}";
        logger.info(requestBody);
        scenario.write("ChangePasswordMutation being processed :" + requestBody);
        baseline.processRequest(context, requestBody, "post");
    }
    
    @Then("^verify that valid authToken '(.+)' being returned$")
    public void verify_authToken_returned(String valid) throws Throwable {
        authToken = (String) baseline.getValue(context, authTokenPath);
        scenario.write("AuthToken returned is "+authToken);
        if (valid.equalsIgnoreCase("is")){
            Assert.assertTrue("Verify that zimbraAuth token '" + authToken + "' is valid", authToken.split("\\.").length == 1);            
        } else {
            Assert.assertTrue("Verify that zimbraAuth token is not returned", stringUtils.isEmpty(authToken));
        }
    }

    @Then("^verify that valid lifetime '(.+)' being returned$")
    public void verify_lifetime_returned(String valid) throws Throwable {
        
        if (valid.equalsIgnoreCase("is")){
            scenario.write("LifeTime returned is "+baseline.getValue(context, lifeTimePath));
            Assert.assertTrue("Verify that lifetime '" + lifeTime + "' is valid", baseline.getValue(context, lifeTimePath) instanceof Integer);            
        } else {
            Assert.assertTrue("Verify that lifeTime is not returned", stringUtils.isEmpty((String) baseline.getValue(context, lifeTimePath)));
        }
        
    }
}