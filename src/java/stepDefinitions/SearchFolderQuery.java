package stepDefinitions;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Then;
import harness.HarnessContext;
import harness.utils.StringUtils;

/**
 * @author swapnil.pingle
 *
 */
public class SearchFolderQuery extends BaseStepDefs {
	private String searchFolderGetQuery;
	public static String searchFolderId;
	private String searchFolderIdJsonPath = "data.searchFolderCreate.id";
	private Scenario scenario;
	private StringUtils stringUtils;
	private HarnessContext context;

	public SearchFolderQuery() {
		searchFolderGetQuery = "'query':'query getSearchFolder{searchFolderGet{name}}'";
		stringUtils = new StringUtils();
	}

	public SearchFolderQuery(HarnessContext context) {
		this();
		this.context = context;
	}

	@Before
	public void BeforeSearchFolderMutations(Scenario scenario) {
		this.scenario = scenario;
	}

	@Then("^Get all search folders using '(.+)' method$")
	public void get_all_search_folders(String method) throws Throwable {
		String variables = "'variables':{}";
		String requestBody = "{" + searchFolderGetQuery + " , " + variables + "}";
		logger.info(requestBody);
		scenario.write("SearchFolderCreateMutation being processed :" + requestBody);
		baseline.processRequest(context, requestBody, method);
		scenario.write(context.getResponse().getBody().jsonString());
	}
}
