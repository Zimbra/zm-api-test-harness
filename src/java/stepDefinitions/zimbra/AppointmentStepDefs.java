package stepDefinitions.zimbra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import harness.HarnessContext;
import net.minidev.json.JSONObject;
import stepDefinitions.BaseStepDefs;
/**
 * @author swapnil.pingle
 *
 */
public class AppointmentStepDefs extends BaseStepDefs{
    
    private Scenario scenario;
    private HarnessContext context;
    private List<Map> emailAddresses;
    private List<Map> attendees;
    private Map<String,Object> message;
    private Map<String,Object> tEmailAddress;
    private Map<String,Object> sEmailAddress;
    private Map<String,Object> mimePart;
    private Map<String,Object> invite;
    private Map<String,Object> organizer;
    private Map<String,Object> startDate;
    private Map<String,Object> endDate;
    private Map<String,Object> attendee;
    private String apptCreateMutation;
    
    public AppointmentStepDefs(){
        emailAddresses = new ArrayList<Map>();
        attendees = new ArrayList<Map>();
        message = new HashMap<String,Object>();
        tEmailAddress = new HashMap<String,Object>();
        sEmailAddress = new HashMap<String,Object>();
        mimePart = new HashMap<String,Object>();
        invite = new HashMap<String,Object>();
        organizer = new HashMap<String,Object>();
        startDate = new HashMap<String,Object>();
        endDate = new HashMap<String,Object>();
        attendee = new HashMap<String,Object>();
        apptCreateMutation = "'query':'mutation createAppt($vMessage:MessageInput){appointmentCreate(message: $vMessage){calendarInviteId}}'";
    }
    
    public AppointmentStepDefs(HarnessContext context) {
        this();
        this.context = context;
    }
    
    @Before
    public void BeforeSearchFolderMutations(Scenario scenario) {
        this.scenario = scenario;
    }
    @Given("^User '(.+)' schedules an appointment '(.+)' with user '(.+)' from '(.+)' to '(.+)'$")
    public void createAppointment(String vOrganizer, String vSubject, String vAttendee, String startTime, String endTime){
        // EmailAddress
        if(vOrganizer.contains("@host")){
            vOrganizer = vOrganizer.split("@")[0]+"@"+globalProperties.getProperty("server");
        }
        if(vAttendee.contains("@host")){
            vAttendee = vAttendee.split("@")[0]+"@"+globalProperties.getProperty("server");
        }
        tEmailAddress.put("addressType", "t");
        tEmailAddress.put("address", vAttendee);
        sEmailAddress.put("addressType", "s");
        sEmailAddress.put("address", vOrganizer);
        emailAddresses.add(tEmailAddress);
        emailAddresses.add(sEmailAddress);
        //mimePart
        mimePart.put("contentType", "text/plain");
        mimePart.put("content", "HI this is a test");
        //invite.orgnizer
        organizer.put("address", vOrganizer);
        //invite.startDate
        startDate.put("dateTime", startTime);
        //invite.endDate
        endDate.put("dateTime", endTime);
        //invite.attendee
        attendee.put("address", vAttendee);
        attendee.put("participationStatus", "NE");
        attendee.put("rsvp", true);
        attendees.add(attendee);
        //invite
        invite.put("name", vSubject);
        invite.put("description","just a simple test");
        invite.put("organizer",organizer);
        invite.put("isAllDay", false);
        invite.put("startDate", startDate);
        invite.put("endDate", endDate);
        invite.put("attendees", attendees);
        //message
        message.put("subject", vSubject);
        message.put("emailAddresses", emailAddresses);
        message.put("mimePart", mimePart);
        message.put("invite", invite);
        //mutation
        Map<String, Object> variables = new HashMap<String,Object>();
        variables.put("vMessage", message);
        
        JSONObject json = new JSONObject(variables);
        String variable = "'variables':" + json.toJSONString();
        
        String requestBody = "{" + apptCreateMutation + " , " + variable + "}";
        logger.info(requestBody);
        System.out.println(requestBody);
        scenario.write("Query being processed :" + requestBody);
        baseline.processRequest(context, requestBody, "Post");
    }
}
