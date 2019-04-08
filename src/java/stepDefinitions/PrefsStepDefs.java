package stepDefinitions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import harness.HarnessContext;
import harness.utils.StringUtils;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public class PrefsStepDefs extends BaseStepDefs{
    private Scenario scenario;
    private String prefsQuery;
    private HarnessContext context;
    
    public PrefsStepDefs(){
        prefsQuery = "'query':'query prefs($preference:[PrefInput]){prefs(preferences:$preference){name,value,modifiedTimestamp}}'";
        new StringUtils();
    }
    
    public PrefsStepDefs(HarnessContext context) {
        this();
        this.context = context;
    }
    
    @Before
    public void BeforeSearchFolderMutations(Scenario scenario) {
        this.scenario = scenario;
    }
    
    @Given("^User queries value for preference '(.+)'$")
    public void queryPref(String pref){
        Map<String,Object> preference = new HashMap<String,Object>();
        preference.put("name", pref);
        ArrayList<Map> prefArray = new ArrayList<Map>();
        prefArray.add(preference);
        Map<String,Object> variablesMap = new HashMap<String,Object>();
        variablesMap.put("preference", prefArray);
        JSONObject json = new JSONObject(variablesMap);
        System.out.println(json.toJSONString());
        
        String variables = "'variables':"+json.toJSONString();
        String requestBody = "{" + prefsQuery + " , " + variables +"}";
        logger.info(requestBody);
        
        System.out.println(requestBody);
        scenario.write("Query being processed :" + requestBody);
        baseline.processRequest(context, requestBody, "Post");
    }
    
    @Given("^'(.+)' should be returned for preference '(.+)'$")
    public void validatePref(String value, String pref){
        String jsonPath = "data.prefs[?(@.name=='" + pref +"')]";
        JSONArray output = baseline.getValues(context, jsonPath);
        if ( output.size() == 0){
            throw new AssertionFailedError("No prefernces details matching "+pref);
        } else if (output.size() > 1) {
            scenario.write("More than one data found for prefernces "+pref);
        } else {
            LinkedHashMap contents = (LinkedHashMap)output.get(0);
            scenario.write("Preference value for preference '" + pref + "' is '"+ contents.get("value") + "'");
            Assert.assertEquals("Verify if expected preference value is being returned",value, contents.get("value"));
        }
    }

}
