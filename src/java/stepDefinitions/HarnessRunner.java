package stepDefinitions;

import cucumber.api.CucumberOptions;
import org.junit.runner.RunWith;
import cucumber.api.junit.Cucumber;

/**
 * @author swapnil.pingle
 *
 */
@RunWith(Cucumber.class)
@CucumberOptions(tags = { "@searchFolderCreate" }, plugin = {
		"html:target/cucumber-reports" }, features = "src/java/features", glue = "stepDefinitions")
public class HarnessRunner {
	public static void main(String[] args){
		System.out.println("doing nothing");
	}
}