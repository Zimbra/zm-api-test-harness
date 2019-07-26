package stepDefinitions.zimbra;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

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
        if(domain.equalsIgnoreCase("host")){
            domain = host;
        }
        int port = new Integer(globalProperties.getProperty("port"));
        String user = globalProperties.getProperty("serverUser");
        String password = globalProperties.getProperty("serverPassword");
        Date date = new Date();
        long num = date.getTime();
        String command = "su - zimbra -c 'zmprov cabulk " + domain + " " + mask+ num +" "+ count + "'";
        scenario.write("Command being executed :" + command);
        String output = CommandLineUtilities.executeCommand(host, port, user, password, command);
        if (output == null) {
            throw new HarnessException("CLI failed");
        }
        scenario.write(output);
        File userNameMask = new File("userNameMask.txt");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(userNameMask,false));
            writer.write(mask + num);
            writer.close();
        } catch (IOException e) {
            throw new HarnessException("Error writing the NameMask file");
        }
    }

    @When("^Create Single account with username '(.+)' and password '(.+)' on domain '(.+)' with attributes '(.+)'$")
    public void createSingleAccount(String userName, String userPassword, String domain, String attributes) {
        String host = globalProperties.getProperty("server");
        if(domain.equalsIgnoreCase("host")){
            domain = host;
        }
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
