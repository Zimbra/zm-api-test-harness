package stepDefinitions.zimbra;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import harness.HarnessContext;
import harness.utils.StringUtils;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import stepDefinitions.BaseStepDefs;

/**
 * @author swapnil.pingle
 *
 */
public class FreeBusyStepDefsQuery extends BaseStepDefs{
    private Scenario scenario;
    private Map<String, Object> fbQueryMap;
    private String fbQuery;
    private StringUtils stringUtils;
    private HarnessContext context;
    
    public FreeBusyStepDefsQuery(){
        fbQueryMap = new HashMap<String,Object>();
        String q = "'query':'query fb($vStartTime:Long!, $vEndTime:Long!, $vFreeBusyUsers:String){freeBusy(startTime:$vStartTime, endTime:$vEndTime, freeBusyUsers: {name:$vFreeBusyUsers}){elements{startTime,endTime,status},identifier}}'";
        fbQuery = q;
        stringUtils = new StringUtils();
    }
    
    public FreeBusyStepDefsQuery(HarnessContext context) {
        this();
        this.context = context;
    }
    
    @Before
    public void BeforeSearchFolderMutations(Scenario scenario) {
        this.scenario = scenario;
    }
    
    @When("^FreeBusy status is queried for user '(.+)' for startTime '(.+)' and endTime '(.+)'$")
    public void verifyFreeBusySlots(String user, String sTime, String eTime){
        fbQueryMap.put("vStartTime", sTime);
        fbQueryMap.put("vEndTime", eTime);
        fbQueryMap.put("vFreeBusyUsers", user);
        
        JSONObject json = new JSONObject(fbQueryMap);
        System.out.println(json.toJSONString());
        
        String variables = "'variables':"+json.toJSONString();
        
        String requestBody = "{" + fbQuery + " , " + variables +"}";
        logger.info(requestBody);
        System.out.println(requestBody);
        scenario.write("Query being processed :" + requestBody);
        baseline.processRequest(context, requestBody, "Post");
        String jsonPath = "data.freeBusy[?(@.identifier=='" + user + "')].identifier";
        Assert.assertEquals("Verify if identifier matches provided user",user, baseline.getValues(context, jsonPath).get(0));
    }
    
    @When("^Timeslot '(.+)' - '(.+)' should be marked as '(.+)'$")
    public void verifyTimeSlotwiseStatus(String slot1, String slot2, String expStatus){
        String jsonPath = "data.freeBusy[0].elements[?(@.startTime==" + slot1 + ")]";
        JSONArray output = baseline.getValues(context, jsonPath);
        if ( output.size() == 0){
            throw new AssertionFailedError("No timeslot details matching start time "+slot1);
        } else if (output.size() > 1) {
            scenario.write("More than one data found for start time "+slot1);
        } else {
            LinkedHashMap contents = (LinkedHashMap)output.get(0);
            Assert.assertEquals("Verify if expected and actual status match",expStatus, contents.get("status"));
            Assert.assertEquals("Verify if expected and actual endTime match",new Long(slot2), contents.get("endTime"));
        }
    }
}
