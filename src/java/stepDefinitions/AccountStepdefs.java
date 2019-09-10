package stepDefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import harness.HarnessContext;
import harness.HttpResponse;
import junit.framework.Assert;
import net.minidev.json.JSONObject;

public class AccountStepdefs extends BaseStepDefs {
    private String createMutation;
    private String modifyMutation;
    private String getAccountMutation;
    private String deleteAccountMutation;
    private Scenario scenario;
    private HarnessContext context;
    
    public AccountStepdefs() {
        createMutation = "'query':'mutation ca($password:String, $name:String!, $attributes:[AttrInput]){accountCreate(name:$name, password:$password, attributes:$attributes){ account{ id, name, attrList{ key, value}} }}'";
        modifyMutation = "'query':'mutation ca($id:String!, $attributes:[AttrInput]){accountModify(id:$id, attributes:$attributes){ account{ id, name , attrList{ key, value}} }}'";
        getAccountMutation = "'query':'query getAccount($accountBy:AccountByInput!, $key:String!, $attributes:[String]){accountGet(account:{accountBy:$accountBy,key:$key}, attributes:$attributes) { account{ id, isExternal, name,  attrList{ key, value} } }}'";
        deleteAccountMutation = "'query':'mutation deleteAccount($id:String!){ accountDelete(id:$id)}'";
    }

    public AccountStepdefs(HarnessContext context) {
        this();
        this.context = context;
    }
    @Before
    public void beforeAccount(Scenario scenario) {
        this.scenario = scenario;
    }
    
    @Given("^Create user with name '(.+)' password '(.+)' attribute '(.+)'$")
    public void createAccount(String name, String password, String attrList) {
        HashMap<String,Object> varMap = new HashMap<String,Object>();
        List<Object> attributes = setAttributes(attrList);
        varMap.put("attributes", attributes);
        varMap.put("password", password);
        if(name.contains("host")){
            String domain = globalProperties.getProperty("server").trim();
            String randomName = name.split("@")[0] + generateRandomInt();
            name = randomName+"@"+domain;
        }
        varMap.put("name", name); 
        JSONObject var = new JSONObject(varMap);
        String variable = var.toJSONString();
        String variables = "'variables': " + variable;
        String requestBody = "{" + createMutation + " , " + variables + "}";

        HttpResponse response = baseline.processRequest(context, requestBody, HttpPost.METHOD_NAME);
        context.setResponse(response);
    }
    
    @Then("^Validate account has attributes '(.+)'$")
    public void validateAccountAttributes(String attributes) {
        String[] attrs = attributes.split(",");
        for(String current : attrs){
            String[] keyValue = current.split("=");
            String attribute = keyValue[0].trim();
            String expValue = keyValue[1].trim();
            //data.accountCreate.account.attrList
            String accountAttrPath = "";
            accountAttrPath = context.getResponse().getBody().jsonString().contains("accountCreate") ? "data.accountCreate.account.attrList" :
                context.getResponse().getBody().jsonString().contains("accountModify") ? "data.accountModify.account.attrList" : "data.accountGet.account.attrList";

            String jsonPath = accountAttrPath + "[?(@.key=='"+attribute+"')].value";
            String actValue = baseline.getValue(context, jsonPath);
            scenario.write("Actual value in the response: " + actValue);
            scenario.write("Expected value: [\"" + expValue + "\"]");
            Assert.assertEquals("[\""+expValue+"\"]", actValue);
        }
    }

    @Then("^Validate account is deleted$")
    public void validateAccountDelete() {
            String jsonPath = "data.accountDelete";
            String respValue = baseline.getValue(context, jsonPath);
            scenario.write("Actual value in the response: " + respValue);
            scenario.write("Expected value: [\"true\"]");
            Assert.assertEquals("true", respValue.toString());
    }

    @Given("^Create user account with name '(.+)' password '(.+)'$")
    public void createAccountNoAttrs(String name, String password) {
        HashMap<String,Object> varMap = new HashMap<String,Object>();
        varMap.put("password", password);
        if(name.contains("host")){
            String domain = globalProperties.getProperty("server").trim();
            String randomName = name.split("@")[0] + generateRandomInt();
            name = randomName+"@"+domain;
        }
        varMap.put("name", name); 
        JSONObject var = new JSONObject(varMap);
        String variable = var.toJSONString();
        String variables = "'variables': " + variable;
        String requestBody = "{" + createMutation + " , " + variables + "}";
        HttpResponse response = baseline.processRequest(context, requestBody, HttpPost.METHOD_NAME);
        context.setResponse(response);
    }

    @Then("^Attributes '(.+)' are modified for user '(.+)'$")
    public void modifyAccount(String attrList, String name) {
        HashMap<String,Object> varMap = new HashMap<String,Object>();
        List<Object> attributes = setAttributes(attrList);
        varMap.put("attributes", attributes);
        varMap.put("id", readIDfromResponse());
        JSONObject var = new JSONObject(varMap);
        String variable = var.toJSONString();
        String variables = "'variables': " + variable;
        String requestBody = "{" + modifyMutation + " , " + variables + "}";
        HttpResponse response = baseline.processRequest(context, requestBody, HttpPost.METHOD_NAME);
        context.setResponse(response);
    }

    @Given("^Delete user account with name '(.+)'$")
    public void deleteAccount(String name, String password) {
        String accountIdPath = "data.accountCreate.account.id";
        String accountIdValue = baseline.getValue(context, accountIdPath);

        HashMap<String,Object> varMap = new HashMap<String,Object>();
        varMap.put("id", accountIdValue);
        JSONObject var = new JSONObject(varMap);
        String variable = var.toJSONString();
        String variables = "'variables': " + variable;
        String requestBody = "{" + deleteAccountMutation + " , " + variables + "}";
        HttpResponse response = baseline.processRequest(context, requestBody, HttpPost.METHOD_NAME);
        context.setResponse(response);
    }

    @Given("^Create and Get user with name '(.+)' password '(.+)' attribute '(.+)'$")
    public void getAccount(String name, String password, String attrList) {
        this.createAccount(name, password, attrList);
        String accountNamePath = "data.accountCreate.account.name";
        String accountNameValue = baseline.getValue(context, accountNamePath);
        scenario.write("Actual value in the response: " + accountNameValue);
        HashMap<String,Object> varMap = new HashMap<String,Object>();
        String[] attributes = this.setAttributesOnly(attrList);
        varMap.put("attributes", attributes);
        varMap.put("accountBy", "name");
        varMap.put("key", accountNameValue);
        JSONObject var = new JSONObject(varMap);
        String variable = var.toJSONString();
        String variables = "'variables': " + variable;
        String requestBody = "{" + getAccountMutation + " , " + variables + "}";

        HttpResponse response = baseline.processRequest(context, requestBody, HttpPost.METHOD_NAME);
        context.setResponse(response);
    }

    private String readIDfromResponse() {
        String jsonPath = context.getResponse().getBody().jsonString().contains("accountCreate")?"data.accountCreate.account.id":"data.accountModify.account.id";
        return baseline.getValue(context, jsonPath);
    }

    private List<Object> setAttributes(String attrlist) {
        List<Object> attrList = new ArrayList<Object>();
        
        for(String current: attrlist.split(",")) {
            Map<String,Object> attrs = new HashMap<String,Object>();
            String[] currentAttr = current.trim().split("=");
            attrs.put("key",currentAttr[0].trim());
            attrs.put("value", currentAttr[1].trim());
            attrList.add(attrs);
        }
        return attrList;
    }

    private String[] setAttributesOnly(String attributes) {
        String[] attrArr = attributes.split(",");
        String[] attrList = new String[attrArr.length];
        for(int i=0; i < attrArr.length; i++) {
            String[] currentAttr = attrArr[i].trim().split("=");
            attrList[i] = (currentAttr[0].trim());
        }
        return attrList;
    }
}
