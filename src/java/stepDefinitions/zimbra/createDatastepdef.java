package stepDefinitions.zimbra;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.When;
import exception.HarnessException;
import stepDefinitions.BaseStepDefs;
import utilities.CommandLineUtilities;

public class createDatastepdef extends BaseStepDefs {

    private Scenario scenario;

    @Before
    public void beforeChangePasswordMutations(Scenario scenario) {
        this.scenario = scenario;
    }

    @When("^Create '(.+)' bulk accounts with mask '(.+)' on domain '(.+)'$")
    public void createBulkAccounts(int count, String mask, String domain) {
        String host = globalProperties.getProperty("server");
        int port = new Integer(globalProperties.getProperty("port"));
        String user = globalProperties.getProperty("serverUser");
        String password = globalProperties.getProperty("serverPassword");
        String command = "su - zimbra -c 'zmprov cabulk " + domain + " " + mask + " " + count + "'";
        scenario.write("Command being executed :" + command);
        String output = CommandLineUtilities.executeCommand(host, port, user, password, command);
        if (output == null) {
            throw new HarnessException("CLI failed");
        }
        scenario.write(output);
    }

    @When("^Create Single account with username '(.+)' and password '(.+)' on domain '(.+)' with attributes '(.+)'$")
    public void createSingleAccount(String userName, String userPassword, String domain, String attributes) {
        String host = globalProperties.getProperty("server");
        if (attributes.equals("default")) {
            attributes = "";
        }
        int port = new Integer(globalProperties.getProperty("port"));
        String serverUser = globalProperties.getProperty("serverUser");
        String serverPassword = globalProperties.getProperty("serverPassword");
        String command = "su - zimbra -c 'zmprov ca " + userName + "@" + domain + " " + userPassword + " " + attributes + "'";
        scenario.write("Command being executed :" + command);
        String output = CommandLineUtilities.executeCommand(host, port, serverUser, serverPassword, command);
        if (output == null) {
            throw new HarnessException("CLI failed");
        }
        scenario.write(output);
    }

}
