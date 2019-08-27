package stepDefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Scenario scenario;
    private HarnessContext context;
    
    public AccountStepdefs(){
        createMutation = "'query':'mutation ca($password:String, $name:String!, $attributes:[AttrInput]){accountCreate(name:$name, password:$password, attributes:$attributes){ account{ id,  attrList{ key, value}} }}'";
        modifyMutation = "'query':'mutation ca($id:String!, $attributes:[AttrInput]){accountModify(id:$id, attributes:$attributes){ account{ id, name , attrList{ key, value}} }}'";
    }
    
    public AccountStepdefs(HarnessContext context) {
        this();
        this.context = context;
    }
    @Before
    public void beforeAccount(Scenario scenario){
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
        HttpResponse response = baseline.processRequest(context, requestBody, "POST");
        context.setResponse(response);
    }
    
    @Then("^Validate account is created/modified with attributes '(.+)'$")
    public void validateAccountAttributes(String attributes){
        String[] attrs = attributes.split(",");
        for(String current : attrs){
            String[] keyValue = current.split("=");
            String attribute = keyValue[0].trim();
            String expValue = keyValue[1].trim();
            //data.accountCreate.account.attrList
            String accountAttrPath = context.getResponse().getBody().jsonString().contains("accountCreate")? "data.accountCreate.account.attrList":"data.accountModify.account.attrList";
            String jsonPath = accountAttrPath+"[?(@.key=='"+attribute+"')].value";
            String actValue = baseline.getValue(context, jsonPath);
            scenario.write("Actual value in the response: "+actValue);
            scenario.write("Expected value: [\""+expValue+"\"]");
            Assert.assertEquals("[\""+expValue+"\"]", actValue);
        }
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
        HttpResponse response = baseline.processRequest(context, requestBody, "POST");
        context.setResponse(response);
    }
    
    @Then("^Attributes '(.+)' are modified for user '(.+)'$")
    public void modifyAccount(String attrList, String name){
        HashMap<String,Object> varMap = new HashMap<String,Object>();
        List<Object> attributes = setAttributes(attrList);
        varMap.put("attributes", attributes);
        varMap.put("id", readIDfromResponse());
        JSONObject var = new JSONObject(varMap);
        String variable = var.toJSONString();
        String variables = "'variables': " + variable;
        String requestBody = "{" + modifyMutation + " , " + variables + "}";
        HttpResponse response = baseline.processRequest(context, requestBody, "POST");
        context.setResponse(response);
    }

    private String readIDfromResponse() {
        String jsonPath = context.getResponse().getBody().jsonString().contains("accountCreate")?"data.accountCreate.account.id":"data.accountModify.account.id";
        return baseline.getValue(context, jsonPath);
    }
    
    private List<Object> setAttributes(String attrlist) {
        List<Object> attrList = new ArrayList<Object>();
        
        for(String current: attrlist.split(",")){
            Map<String,Object> attrs = new HashMap<String,Object>();
            String[] currentAttr = current.trim().split("=");
            attrs.put("key",currentAttr[0].trim());
            attrs.put("value", currentAttr[1].trim());
            attrList.add(attrs);
        }
        return attrList;
    }

}
